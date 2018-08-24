/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.optimize.Optimization;

import java.util.BitSet;
import java.util.Iterator;

// assume there are no critical edges
// updated for SSA
public class SSAReverter implements Optimization {

  public SSAReverter() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (cfg.isSSA()) {

      cfg.invalidate();
      int maxReg = cfg.regMax();

      // add copy statements
      for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        if (currentBB.outDegree() == 1) {
          IRControlEdge edge = (IRControlEdge)currentBB.outEdges().next();
          BasicBlock targetBB = edge.targetBB();
          if (targetBB.inDegree() > 1) {
            Statement previous = currentBB.trailer();
            Statement next = null;
            if (edge.type() != IRControlEdge.FALL) {
              next = previous;
              previous = next.previous();
            }
            BitSet written = new BitSet(maxReg);
            int[] temps = new int[maxReg];
            Iterator j = targetBB.topdownStmts();
            j.next();
            while (j.hasNext()) {
              IRStatement stmt = (IRStatement)j.next();
              Matcher matcher = new Matcher(stmt);
              if (!matcher.phi1(cfg, currentBB, edge, previous, next, written, temps))
                break;
            }
          }
        }
      }

      // remove phi statements
      for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        if (currentBB.inDegree() > 1) {
          Iterator j = currentBB.topdownStmts();
          j.next();
          while (j.hasNext()) {
            IRStatement stmt = (IRStatement)j.next();
            Matcher matcher = new Matcher(stmt);
            if (!matcher.phi2())
              break;        
            j.remove();
          }
        }
      }

      cfg.setSSA(false);

      changed = true;

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public boolean phi1(IRCFG cfg, BasicBlock currentBB, IRControlEdge edge, Statement previous, Statement next, BitSet written, int[] temps)
      : IR.IPHI = {
        int src = @1.getReg(edge);
        int trg = @1.getReg();
        if (written.get(src)) {
          int tmp = temps[src];
          if (tmp == 0) {
            tmp = cfg.iregSeq();
            IRStatement stmt = new IRStatement(new IR.idefine(tmp, new IR.iuse(src)));
            if (previous == null) currentBB.prependStmt(stmt);
            else                  currentBB.insertStmtAfterStmt(stmt, previous);
            tmp = ~tmp;
            temps[src] = tmp;
          }
          src = ~tmp;
        }
        IRStatement stmt = new IRStatement(new IR.idefine(trg, new IR.iuse(src)));
        if (next == null) currentBB.appendStmt(stmt);
        else              currentBB.insertStmtBeforeStmt(stmt, next);
        written.set(trg);
        return true;
      }
      | IR.LPHI = {
        int src = @1.getReg(edge);
        int trg = @1.getReg();
        if (written.get(src)) {
          int tmp = temps[src];
          if (tmp == 0) {
            tmp = cfg.lregSeq();
            IRStatement stmt = new IRStatement(new IR.ldefine(tmp, new IR.luse(src)));
            if (previous == null) currentBB.prependStmt(stmt);
            else                  currentBB.insertStmtAfterStmt(stmt, previous);
            tmp = ~tmp;
            temps[src] = tmp;
          }
          src = ~tmp;
        }
        IRStatement stmt = new IRStatement(new IR.ldefine(trg, new IR.luse(src)));
        if (next == null) currentBB.appendStmt(stmt);
        else              currentBB.insertStmtBeforeStmt(stmt, next);
        written.set(trg);
        return true;
      }
      | IR.FPHI = {
        int src = @1.getReg(edge);
        int trg = @1.getReg();
        if (written.get(src)) {
          int tmp = temps[src];
          if (tmp == 0) {
            tmp = cfg.fregSeq();
            IRStatement stmt = new IRStatement(new IR.fdefine(tmp, new IR.fuse(src)));
            if (previous == null) currentBB.prependStmt(stmt);
            else                  currentBB.insertStmtAfterStmt(stmt, previous);
            tmp = ~tmp;
            temps[src] = tmp;
          }
          src = ~tmp;
        }
        IRStatement stmt = new IRStatement(new IR.fdefine(trg, new IR.fuse(src)));
        if (next == null) currentBB.appendStmt(stmt);
        else              currentBB.insertStmtBeforeStmt(stmt, next);
        written.set(trg);
        return true;
      }
      | IR.DPHI = {
        int src = @1.getReg(edge);
        int trg = @1.getReg();
        if (written.get(src)) {
          int tmp = temps[src];
          if (tmp == 0) {
            tmp = cfg.dregSeq();
            IRStatement stmt = new IRStatement(new IR.ddefine(tmp, new IR.duse(src)));
            if (previous == null) currentBB.prependStmt(stmt);
            else                  currentBB.insertStmtAfterStmt(stmt, previous);
            tmp = ~tmp;
            temps[src] = tmp;
          }
          src = ~tmp;
        }
        IRStatement stmt = new IRStatement(new IR.ddefine(trg, new IR.duse(src)));
        if (next == null) currentBB.appendStmt(stmt);
        else              currentBB.insertStmtBeforeStmt(stmt, next);
        written.set(trg);
        return true;
      }
      | IR.APHI = {
        int src = @1.getReg(edge);
        int trg = @1.getReg();
        if (written.get(src)) {
          int tmp = temps[src];
          if (tmp == 0) {
            tmp = cfg.aregSeq();
            IRStatement stmt = new IRStatement(new IR.adefine(tmp, new IR.ause(src)));
            if (previous == null) currentBB.prependStmt(stmt);
            else                  currentBB.insertStmtAfterStmt(stmt, previous);
            tmp = ~tmp;
            temps[src] = tmp;
          }
          src = ~tmp;
        }
        IRStatement stmt = new IRStatement(new IR.adefine(trg, new IR.ause(src)));
        if (next == null) currentBB.appendStmt(stmt);
        else              currentBB.insertStmtBeforeStmt(stmt, next);
        written.set(trg);
        return true;
      }
      | default = { return false; }
      ;

    public boolean phi2()
      : IR.IPHI = { return true; }
      | IR.LPHI = { return true; }
      | IR.FPHI = { return true; }
      | IR.DPHI = { return true; }
      | IR.APHI = { return true; }
      | default = { return false; }
      ;

  }

}

