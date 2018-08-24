/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.Context;
import jewel.core.ContextClassInfo;
import jewel.core.LoadedClassInfo;
import jewel.core.LoadedClassInfo.PlacedMethodInfo;
import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.BitFunction;
import jewel.core.jiro.dataflow.DataFlowAnalyser;
import jewel.core.jiro.dataflow.DataFlowAnalysis;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowFunction;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowItem;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;
import jewel.core.jiro.optimize.Optimization;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;

// updated for SSA
public final class InitializationRemoval implements Optimization {

  public InitializationRemoval() { }

  public boolean applyTo(ControlFlowGraph cfg) {
    boolean changed = false;
    
    HashSet set = new HashSet();
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher matcher = new Matcher(stmt);
      matcher.cover1(set);
    }

    if (set.size() > 0) {

      ArrayList list = new ArrayList(set.size());
      list.addAll(set);

      BitSet mask = new BitSet(list.size());
      for (int i = 0; i < list.size(); i++) {
        Context context = Context.get();
        ContextClassInfo loadedInfo = context.forName((String)list.get(i));
        if (loadedInfo != null)
          if (loadedInfo.isInitialized())
            mask.set(i);
      }

      ArrayList mods = new ArrayList();
      DataFlowAnalyser analyser = new IterativeDataFlowAnalyser(new Analysis(list));
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        BitItem item = (BitItem)analyser.valueBefore(stmt);
        item.or(mask);
        matcher.cover4(list, item, mods);
      }

      if (mods.size() > 0) {

        for (Iterator i = mods.iterator(); i.hasNext(); ) {
          Runnable runnable = (Runnable)i.next();
          runnable.run();
        }

        changed = true;

      }

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void cover1(HashSet set)
      : IR.INIT(IR.ACLASS)  = { set.add(@2.getSymbolicType()); }
      | IR.INITX(IR.ACLASS) = { set.add(@2.getSymbolicType()); }
      | default = { }
      ;

    public void cover2(ArrayList list, BitFunction function)
      : IR.INIT(IR.ACLASS)  = { function.set(list.indexOf(@2.getSymbolicType()), BitFunction.GEN); }
      | default = { }
      ;

    public void cover3(ArrayList list, BitFunction function)
      : IR.INITX(IR.ACLASS) = { function.set(list.indexOf(@2.getSymbolicType()), BitFunction.GEN); }
      | default = { }
      ;

    public void cover4(ArrayList list, BitItem item, ArrayList mods)
      : IR.INIT(IR.ACLASS) = {
        if (item.get(list.indexOf(@2.getSymbolicType()))) {
          mods.add(new Runnable() {
            public void run() {
              IRStatement stmt = @1.ownerStmt();
              BasicBlock ownerBB = stmt.ownerBB();
              ownerBB.removeStmt(stmt);
            }
          });
        }
      }
      | IR.INITX(IR.ACLASS) = {
        if (item.get(list.indexOf(@2.getSymbolicType()))) {
          mods.add(new Runnable() {
            public void run() {
              IRStatement stmt = @1.ownerStmt();
              BasicBlock ownerBB = stmt.ownerBB();
              IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
              ownerBB.removeStmt(stmt);
              for (Iterator i = ownerBB.outEdges(); i.hasNext(); ) {
                IRControlEdge edge = (IRControlEdge)i.next();
                if (edge.type() == IRControlEdge.EXCEPT) {
                  i.remove();
                  break;
                }
              }
            }
          });
        }
      }
      | default = { }
      ;

  }

  private static final class Analysis implements DataFlowAnalysis {

    private final ArrayList list;

    Analysis(ArrayList list) {
      this.list = list;
    }

    public byte direction() {
      return FORWARD;
    }

    public FlowItem newFlowItem() {
      return new BitItem(list.size());
    }


    public FlowFunction newFlowFunction() {
      return new BitFunction(list.size());
    }

    public void modelEffect(FlowFunction function, Statement stmt) {
      modelEffect((BitFunction)function, (IRStatement)stmt);
    }

    public void modelEffect(BitFunction function, IRStatement stmt) {
      Matcher matcher = new Matcher(stmt);
      matcher.cover2(list, function);
    }

    public void modelEffect(FlowFunction function, ControlEdge edge) {
      modelEffect((BitFunction)function, (IRControlEdge)edge);
    }

    public void modelEffect(BitFunction function, IRControlEdge edge) {
      if (edge.type() == IRControlEdge.FALL) {
        BasicBlock sourceBB = edge.sourceBB();
        IRStatement stmt = (IRStatement)sourceBB.trailer();
        if (stmt != null) {
          Matcher matcher = new Matcher(stmt);
          matcher.cover3(list, function);
        }
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

  }

}

