/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

//make more general
public final class ConditionTightening implements Optimization {

  public ConditionTightening() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (cfg.isSSA()) {

      BitSet zer = new BitSet();
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.zer(zer);
      }

      BitSet len = new BitSet();
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.len(len);
      }

      HashMap inc = new HashMap();
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.inc(inc);
      }

      BitSet phi = new BitSet();
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.phi(zer, inc, phi);
      }

      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        changed |= matcher.upd(phi, len);
      }

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void zer(BitSet zer)
      : IR.IDEFINE(IR.ICONST) [@2.getValue() == 0] = {
        zer.set(@1.getReg());
      }
      | default = { }
      ;

    public void len(BitSet len)
      : IR.IDEFINE(IR.LENGTH(IR.AUSE)) = {
        len.set(@1.getReg());
      }
      | default = { }
      ;

    public void inc(HashMap inc)
      : IR.IDEFINE(IR.IADD(IR.IUSE,IR.ICONST)) [@4.getValue() == 1] = {
        inc.put(new Integer(@1.getReg()), new Integer(@3.getReg()));
      }
      | default = { }
      ;

    public void phi(BitSet zer, HashMap inc, BitSet phi)
      : IR.IPHI = {
        BitSet regs = new BitSet();
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          Integer intg = (Integer)i.next();
          regs.set(intg.intValue());
        }
        if (regs.length() == 0) return;
        int r0 = regs.length()-1;
        regs.clear(r0);
        if (regs.length() == 0) return;
        int r1 = regs.length()-1;
        regs.clear(r1);
        if (regs.length() != 0) return;
        if (zer.get(r1)) {
          int rt = r1;
          r1 = r0;
          r0 = rt;
        }
        if (zer.get(r0)) {
          Integer intg = (Integer)inc.get(new Integer(r1));
          if (intg != null)
            if (intg.intValue() == @1.getReg())
              phi.set(@1.getReg());
        }
      }
      | default = { }
      ;

    public boolean chk(int phi, int len, boolean falling)
      : IR.IJUMP(IR.IUSE,IR.IUSE) [@1.xop() == IR.ijump.LT || @1.xop() == IR.ijump.GE] = {
        if (@2.getReg() == phi && @3.getReg() == len)
          if (falling)
            return @1.xop() == IR.ijump.GE;
          else
            return @1.xop() == IR.ijump.LT;
        return false;
      }
      | default = {
        return false;
      }
      ;

    public boolean upd(BitSet phi, BitSet len)
      : IR.IJUMP(IR.IUSE,IR.IUSE) [@1.xop() == IR.ijump.B || @1.xop() == IR.ijump.AE] = {
        if (phi.get(@2.getReg()) && len.get(@3.getReg())) {
          for (Iterator i = @1.ownerStmt().ownerBB().dominatorBBs(); i.hasNext(); ) {
            BasicBlock currentBB = (BasicBlock)i.next();
            if (currentBB.inDegree() == 1) {
              BasicBlock predBB = (BasicBlock)currentBB.predBBs().next();
              IRStatement stmt = (IRStatement)predBB.trailer();
              if (stmt != null) {
                Matcher matcher = new Matcher(stmt);
                if (matcher.chk(@2.getReg(), @3.getReg(), predBB.downBB() == currentBB)) {
                  switch (@1.xop()) {
                  case IR.ijump.B:
                    @1.setXop(IR.ijump.LT);
                    break;
                  case IR.ijump.AE:
                    @1.setXop(IR.ijump.GE);
                    break;
                  }
                  return true;
                }
              }
            }
          }
        }
        return false;
      }
      | default = {
        return false;
      }
      ;

  }

}

