/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.Context;
import jewel.core.ContextClassInfo;
import jewel.core.LoadedClassInfo.PlacedMethodInfo;
import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.BitFunction;
import jewel.core.jiro.dataflow.DataFlowAnalysis;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowItem;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowFunction;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;

import java.util.Iterator;

public final class NonNullPointers implements DataFlowAnalysis {

  public static final class NNPFunction extends BitFunction {
  
    private int[] source;

    public int getSource(int index) {
      if (index < 0)
        throw new IllegalArgumentException();
      if (source == null || index >= source.length)
        return index;
      return source[index];
    }
  
    public void setSource(int index, int value) {
      if (index < 0 || value < 0)
        throw new IllegalArgumentException();
      if (index == value)
        return;
      if (source == null) {
        source = new int[index+1];
        for (int i = 0; i < source.length; i++)
          source[i] = i;
      }
      if (index >= source.length) {
        int[] tmp = source;
        source = new int[index+1];
        System.arraycopy(tmp, 0, source, 0, tmp.length);
        for (int i = tmp.length; i < source.length; i++)
          source[i] = i;
      }
      source[index] = value;
    }

    public BitItem apply(BitItem input) {
      BitItem result = super.apply(input);
      if (source != null)
        for (int i = 0; i < source.length; i++) 
          if (i != source[i])
            if (get(i) == FLOW) {
              if (input.get(source[i]))
                result.set(i);
              else
                result.clear(i);
            }
      return result;
    }
    
    public Object clone() {
      NNPFunction clone = (NNPFunction)super.clone();
      if (source != null)
        clone.source = (int[])source.clone();
      return clone;
    }

    public int hashCode() {
      int hashCode = super.hashCode();
      if (source != null)
        for (int i = 0; i < source.length; i++)
          if (i != source[i])
            if (get(i) == FLOW) {
              hashCode ^= i;
              hashCode ^= source[i];
            }
      return hashCode;
    }
  
    // missing
    public boolean equals(Object object) {
      return super.equals(object);
    }
  
    // missing
    public String toString() {
      return super.toString();
    }
  
  }

  public NonNullPointers() { }

  public byte direction() {
    return FORWARD;
  }

  public FlowItem newFlowItem() {
    return new BitItem();
  }

  public FlowFunction newFlowFunction() {
    return new NNPFunction();
  }

  public void modelEffect(FlowFunction function, Statement stmt) {
    modelEffect((NNPFunction)function, (IRStatement)stmt);
  }

  public void modelEffect(NNPFunction function, IRStatement stmt) {
    Matcher matcher = new Matcher(stmt);
    matcher.cover(function);
  }
  
  public void modelEffect(FlowFunction function, ControlEdge edge) {
    modelEffect((NNPFunction)function, (IRControlEdge)edge);
  }

  public void modelEffect(NNPFunction function, IRControlEdge edge) {
    IRBasicBlock sourceBB = (IRBasicBlock)edge.sourceBB();
    IRStatement trailer = (IRStatement)sourceBB.trailer();
    if (trailer != null) {
      Matcher matcher = new Matcher(trailer);
      switch (edge.type()) {
      case IRControlEdge.JUMP:
        matcher.jump(function);
        break;
      case IRControlEdge.FALL:
        matcher.fall(function);
        break;
      }
    }
    IRBasicBlock targetBB = (IRBasicBlock)edge.targetBB();
    for (Iterator i = targetBB.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher matcher = new Matcher(stmt);
      if (!matcher.phinode(function, edge))
        break;
    }
  }

  public FlowItem merge(FlowItem one, FlowItem another) {
    return merge((BitItem)one, (BitItem)another);
  }

  public BitItem merge(BitItem one, BitItem another) {
    BitItem result = (BitItem)one.clone();
    result.and(another);
    return result;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }
  
    public void cover(NNPFunction function)
      : IR.ARECEIVE = {
        if (@1.previous() != null)
          function.set(@1.getReg(), BitFunction.KILL);
        else {
          IRCFG cfg = (IRCFG)@1.ownerStmt().ownerBB().ownerCFG();
          if (!cfg.isStatic())
            function.set(@1.getReg(), BitFunction.GEN);
        }
      }
      | IR.ADEFINE(anonnull) = { function.set(@1.getReg(), BitFunction.GEN); }
      | IR.ADEFINE(aunknown) = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.ADEFINE(IR.AUSE) = {
        function.set(@1.getReg(), function.get(@2.getReg())); 
        function.setSource(@1.getReg(), function.getSource(@2.getReg()));
      }
      | IR.ARESULT = {
        IR.snode snode = (IR.snode)@1.previous();
        Matcher matcher = new Matcher(snode);
        matcher.result(@1.getReg(), function);
      }
      | IR.ACATCH = { function.set(@1.getReg(), BitFunction.GEN); }
      | default = { }
      ;

    public void result(int reg, NNPFunction function)
      : IR.NEWINSTANCE(cover) = { function.set(reg, BitFunction.GEN); }
      | IR.NEWINSTANCEX(cover) = { function.set(reg, BitFunction.GEN); }
      | IR.NEWARRAY(cover,cover) = { function.set(reg, BitFunction.GEN); }
      | IR.NEWARRAYX(cover,cover) = { function.set(reg, BitFunction.GEN); }
      | IR.CALL(IR.MLOOKUP(IR.ACLASS)) = {
        Context context = Context.get();
        ContextClassInfo clazz = context.forName(@3.getSymbolicType());
        if (clazz != null) {
          PlacedMethodInfo method = clazz.lookupDispatchMethod(@2.getDispatchIndex());
          Context subcontext = context.getSubcontext(method.getOwner().getName(), method.getContext());
          if (subcontext != null) {
            IRCFG cfg = null;
            try {
              cfg = method.getCFG(subcontext);
            } catch (Exception e) {
              e.printStackTrace();
            }
            if (cfg != null)
              if (cfg.retNonNull()) {
                function.set(reg, BitFunction.GEN);
                return;
              }
          }
        }
        function.set(reg, BitFunction.KILL);
      }
      | IR.CALLX(IR.MLOOKUP(IR.ACLASS)) = {
        Context context = Context.get();
        ContextClassInfo clazz = context.forName(@3.getSymbolicType());
        if (clazz != null) {
          PlacedMethodInfo method = clazz.lookupDispatchMethod(@2.getDispatchIndex());
          Context subcontext = context.getSubcontext(method.getOwner().getName(), method.getContext());
          if (subcontext != null) {
            IRCFG cfg = null;
            try {
              cfg = method.getCFG(subcontext);
            } catch (Exception e) {
              e.printStackTrace();
            }
            if (cfg != null)
              if (cfg.retNonNull()) {
                function.set(reg, BitFunction.GEN);
                return;
              }
          }
        }
        function.set(reg, BitFunction.KILL);
      }
      | default = { function.set(reg, BitFunction.KILL); }
      ;

    public void jump(NNPFunction function)
      : IR.AJUMP(IR.AUSE,anonnull) [@1.xop() == IR.ajump.EQ] = { function.set(@2.getReg(), BitFunction.GEN); }
      | IR.AJUMP(anonnull,IR.AUSE) [@1.xop() == IR.ajump.EQ] = { function.set(@3.getReg(), BitFunction.GEN); }
      | IR.AJUMP(IR.AUSE,IR.ANULL) [@1.xop() == IR.ajump.NE] = { function.set(@2.getReg(), BitFunction.GEN); }
      | IR.AJUMP(IR.ANULL,IR.AUSE) [@1.xop() == IR.ajump.NE] = { function.set(@3.getReg(), BitFunction.GEN); }
      | default = { }
      ;
      
    public void fall(NNPFunction function)
      : IR.AJUMP(IR.AUSE,anonnull) [@1.xop() == IR.ajump.NE] = { function.set(@2.getReg(), BitFunction.GEN); }
      | IR.AJUMP(anonnull,IR.AUSE) [@1.xop() == IR.ajump.NE] = { function.set(@3.getReg(), BitFunction.GEN); }
      | IR.AJUMP(IR.AUSE,IR.ANULL) [@1.xop() == IR.ajump.EQ] = { function.set(@2.getReg(), BitFunction.GEN); }
      | IR.AJUMP(IR.ANULL,IR.AUSE) [@1.xop() == IR.ajump.EQ] = { function.set(@3.getReg(), BitFunction.GEN); }
      | default = { }
      ;
      
    private void aunknown()
      : IR.ALOAD(cover)
      | IR.AALOAD(cover,cover)
      | IR.IMLOOKUP(cover,cover)
      | IR.ANULL
      ;

    private void anonnull()
      : IR.GETCLASS(cover)
      | IR.MLOOKUP(cover)
      | IR.ACLASS
      | IR.ASTRING
      ;

    public boolean phinode(NNPFunction function, IRControlEdge edge)
      : IR.LABEL = { return true; }
      | IR.APHI = {
        function.set(@1.getReg(), function.get(@1.getReg(edge))); 
        function.setSource(@1.getReg(), function.getSource(@1.getReg(edge)));
        return true;
      }
      | default = { return false; }
      ;

  }

}

