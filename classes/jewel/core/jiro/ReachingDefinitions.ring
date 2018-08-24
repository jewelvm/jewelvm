/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.BitFunction;
import jewel.core.jiro.dataflow.DataFlowAnalysis;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowItem;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowFunction;

import java.util.BitSet;
import java.util.Iterator;

public final class ReachingDefinitions implements DataFlowAnalysis {

  private final ControlFlowGraph cfg;
  private BitSet[] sets;

  public ReachingDefinitions(ControlFlowGraph cfg) {
    this.cfg = cfg;

    sets = new BitSet[100];

    int index = 0;
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); index++) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher1 matcher = new Matcher1(stmt.snode());
      matcher.cover(index);
    }
  }

  public BitItem filter(int reg, BitItem value) {
    value = (BitItem)value.clone();
    BitSet set = sets[reg];
    if (set == null)
      value.andNot(value);
    else
      value.and(set);
    return value;
  }

  public byte direction() {
    return FORWARD;
  }

  public FlowItem newFlowItem() {
    return new BitItem(cfg.stmtCount());
  }

  public FlowFunction newFlowFunction() {
    return new BitFunction(cfg.stmtCount());
  }

  public void modelEffect(FlowFunction function, Statement stmt) {
    modelEffect((BitFunction)function, (IRStatement)stmt);
  }

  public void modelEffect(BitFunction function, IRStatement stmt) {
    Matcher2 matcher = new Matcher2(stmt.snode());
    matcher.cover(function, cfg.indexOf(stmt));
  }
  
  public void modelEffect(FlowFunction function, ControlEdge edge) {
    modelEffect((BitFunction)function, (IRControlEdge)edge);
  }

  public void modelEffect(BitFunction function, IRControlEdge edge) { }

  public FlowItem merge(FlowItem one, FlowItem another) {
    return merge((BitItem)one, (BitItem)another);
  }

  public BitItem merge(BitItem one, BitItem another) {
    BitItem result = (BitItem)one.clone();
    result.or(another);
    return result;
  }

  private final class Matcher1 {

    private void cover(int index)
      : IR.IRECEIVE = { add(index, @1.getReg()); }
      | IR.LRECEIVE = { add(index, @1.getReg()); }
      | IR.FRECEIVE = { add(index, @1.getReg()); }
      | IR.DRECEIVE = { add(index, @1.getReg()); }
      | IR.ARECEIVE = { add(index, @1.getReg()); }

      | IR.IDEFINE(cover) = { add(index, @1.getReg()); }
      | IR.LDEFINE(cover) = { add(index, @1.getReg()); }
      | IR.FDEFINE(cover) = { add(index, @1.getReg()); }
      | IR.DDEFINE(cover) = { add(index, @1.getReg()); }
      | IR.ADEFINE(cover) = { add(index, @1.getReg()); }

      | IR.IPHI = { add(index, @1.getReg()); }
      | IR.LPHI = { add(index, @1.getReg()); }
      | IR.FPHI = { add(index, @1.getReg()); }
      | IR.DPHI = { add(index, @1.getReg()); }
      | IR.APHI = { add(index, @1.getReg()); }

      | IR.IRESULT = { add(index, @1.getReg()); }
      | IR.LRESULT = { add(index, @1.getReg()); }
      | IR.FRESULT = { add(index, @1.getReg()); }
      | IR.DRESULT = { add(index, @1.getReg()); }
      | IR.ARESULT = { add(index, @1.getReg()); }

      | IR.ACATCH  = { add(index, @1.getReg()); }

      | default = { }
      ;

    private void add(int index, int reg) {
      if (reg >= sets.length) {
        BitSet[] tmp = sets;
        sets = new BitSet[reg+1];
        System.arraycopy(tmp, 0, sets, 0, tmp.length);
      }
      BitSet set = sets[reg];
      if (set == null) {
        set = new BitSet(cfg.stmtCount());
        sets[reg] = set;
      }
      set.set(index);
    }

  }

  private final class Matcher2 {

    private void cover(BitFunction function, int index)
      : IR.IRECEIVE = { mod(function, index, @1.getReg()); }
      | IR.LRECEIVE = { mod(function, index, @1.getReg()); }
      | IR.FRECEIVE = { mod(function, index, @1.getReg()); }
      | IR.DRECEIVE = { mod(function, index, @1.getReg()); }
      | IR.ARECEIVE = { mod(function, index, @1.getReg()); }

      | IR.IDEFINE(cover) = { mod(function, index, @1.getReg()); }
      | IR.LDEFINE(cover) = { mod(function, index, @1.getReg()); }
      | IR.FDEFINE(cover) = { mod(function, index, @1.getReg()); }
      | IR.DDEFINE(cover) = { mod(function, index, @1.getReg()); }
      | IR.ADEFINE(cover) = { mod(function, index, @1.getReg()); }

      | IR.IPHI = { mod(function, index, @1.getReg()); }
      | IR.LPHI = { mod(function, index, @1.getReg()); }
      | IR.FPHI = { mod(function, index, @1.getReg()); }
      | IR.DPHI = { mod(function, index, @1.getReg()); }
      | IR.APHI = { mod(function, index, @1.getReg()); }

      | IR.IRESULT = { mod(function, index, @1.getReg()); }
      | IR.LRESULT = { mod(function, index, @1.getReg()); }
      | IR.FRESULT = { mod(function, index, @1.getReg()); }
      | IR.DRESULT = { mod(function, index, @1.getReg()); }
      | IR.ARESULT = { mod(function, index, @1.getReg()); }

      | IR.ACATCH = { mod(function, index, @1.getReg()); }

      | default = { }
      ;

    private void mod(BitFunction function, int index, int reg) {
      BitSet set = sets[reg];
      function.set(set, BitFunction.KILL);
      function.set(index, BitFunction.GEN);
    }

  }

}

