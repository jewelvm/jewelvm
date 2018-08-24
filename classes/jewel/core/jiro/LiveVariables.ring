/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.BitFunction;
import jewel.core.jiro.dataflow.DataFlowAnalysis;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowItem;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowFunction;

import java.util.Iterator;

public final class LiveVariables implements DataFlowAnalysis {

  public LiveVariables() { }

  public byte direction() {
    return BACKWARD;
  }

  public FlowItem newFlowItem() {
    return new BitItem();
  }

  public FlowFunction newFlowFunction() {
    return new BitFunction();
  }

  public void modelEffect(FlowFunction function, Statement stmt) {
    modelEffect((BitFunction)function, (IRStatement)stmt);
  }

  public void modelEffect(BitFunction function, IRStatement stmt) {
    Matcher matcher = new Matcher(stmt);
    matcher.stmt(function);
  }
  
  public void modelEffect(FlowFunction function, ControlEdge edge) {
    modelEffect((BitFunction)function, (IRControlEdge)edge);
  }

  public void modelEffect(BitFunction function, IRControlEdge edge) {
    BasicBlock targetBB = edge.targetBB();
    for (Iterator i = targetBB.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher matcher = new Matcher(stmt);
      if (!matcher.edge(function, edge))
        break;
    }
  }

  public FlowItem merge(FlowItem one, FlowItem another) {
    return merge((BitItem)one, (BitItem)another);
  }

  public BitItem merge(BitItem one, BitItem another) {
    BitItem result = (BitItem)one.clone();
    result.or(another);
    return result;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void stmt(BitFunction function)
      : IR.IRECEIVE = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.LRECEIVE = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.FRECEIVE = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.DRECEIVE = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.ARECEIVE = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.IRESULT = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.LRESULT = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.FRESULT = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.DRESULT = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.ARESULT = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.ACATCH  = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.IDEFINE(stmt) = { function.set(@1.getReg(), BitFunction.KILL); @2(function); }
      | IR.LDEFINE(stmt) = { function.set(@1.getReg(), BitFunction.KILL); @2(function); }
      | IR.FDEFINE(stmt) = { function.set(@1.getReg(), BitFunction.KILL); @2(function); }
      | IR.DDEFINE(stmt) = { function.set(@1.getReg(), BitFunction.KILL); @2(function); }
      | IR.ADEFINE(stmt) = { function.set(@1.getReg(), BitFunction.KILL); @2(function); }
      | IR.IPHI = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.FPHI = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.FPHI = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.DPHI = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.APHI = { function.set(@1.getReg(), BitFunction.KILL); }
      | IR.IUSE = { function.set(@1.getReg(), BitFunction.GEN); }
      | IR.LUSE = { function.set(@1.getReg(), BitFunction.GEN); }
      | IR.FUSE = { function.set(@1.getReg(), BitFunction.GEN); }
      | IR.DUSE = { function.set(@1.getReg(), BitFunction.GEN); }
      | IR.AUSE = { function.set(@1.getReg(), BitFunction.GEN); }
      | default = {
        if (  left$ != null)   left$.stmt(function); 
        if (middle$ != null) middle$.stmt(function); 
        if ( right$ != null)  right$.stmt(function);
      }
      ;

    public boolean edge(BitFunction function, IRControlEdge edge)
      : IR.LABEL = { return true; }
      | IR.IPHI = { function.set(@1.getReg(edge), BitFunction.GEN); return true; }
      | IR.FPHI = { function.set(@1.getReg(edge), BitFunction.GEN); return true; }
      | IR.FPHI = { function.set(@1.getReg(edge), BitFunction.GEN); return true; }
      | IR.DPHI = { function.set(@1.getReg(edge), BitFunction.GEN); return true; }
      | IR.APHI = { function.set(@1.getReg(edge), BitFunction.GEN); return true; }
      | default = { return false; }
      ;

  }

}

