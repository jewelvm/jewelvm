/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

// updated for SSA
public final class CriticalEdgeSplitting implements Optimization {

  public CriticalEdgeSplitting() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;
    cfg.invalidate();

    // find critical edges
    ArrayList criticalEdges = new ArrayList();
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      if (currentBB.outDegree() > 1)
        for (Iterator j = currentBB.outEdges(); j.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)j.next();
          BasicBlock targetBB = edge.targetBB();
          if (targetBB.inDegree() > 1)
            criticalEdges.add(edge);
        }
    }

    if (!criticalEdges.isEmpty()) {

      // add new empty BB
      HashSet handlerBBs = new HashSet();
      for (Iterator i = criticalEdges.iterator(); i.hasNext(); ) {
        IRControlEdge edge = (IRControlEdge)i.next();
        BasicBlock sourceBB = edge.sourceBB();
        BasicBlock targetBB = edge.targetBB();
        BasicBlock emptyBB = new IRBasicBlock();
        BasicBlock previousBB;
        IRControlEdge sourceEdge = (IRControlEdge)edge.clone(sourceBB, emptyBB);
        IRControlEdge targetEdge;
        if (edge.type() == IRControlEdge.FALL) {
          previousBB = sourceBB;
          targetEdge = new IRControlEdge(emptyBB, IRControlEdge.FALL, targetBB);
        } else {
          previousBB = cfg.bottomBB();
          targetEdge = new IRControlEdge(emptyBB, IRControlEdge.JUMP, targetBB);
          IRStatement leader = (IRStatement)targetBB.leader();
          IR.label label = (IR.label)leader.snode();
          int seq = cfg.labelSeq();
          int lab = label.getValue();
          emptyBB.appendStmt(new IRStatement(new IR.label(seq)));
          emptyBB.appendStmt(new IRStatement(new IR.jump(lab)));
          IRStatement trailer = (IRStatement)sourceBB.trailer();
          Matcher matcher = new Matcher(trailer);
          matcher.update(edge.value(), seq);
          if (edge.type() == IRControlEdge.EXCEPT)
            handlerBBs.add(targetBB);
        }
        cfg.insertBBAfterBB(emptyBB, previousBB);
        cfg.addEdge(sourceEdge);
        cfg.addEdge(targetEdge);
        if (cfg.isSSA())
          for (Iterator j = targetBB.topdownStmts(); j.hasNext(); ) {
            IRStatement stmt = (IRStatement)j.next();
            Matcher matcher = new Matcher(stmt);
            if (!matcher.phinode(edge, targetEdge))
              break;
          }
        cfg.removeEdge(edge);
      }

      // fixup for exception edges
      for (Iterator i = handlerBBs.iterator(); i.hasNext(); ) {
        BasicBlock handlerBB = (BasicBlock)i.next();
        IRStatement catchStmt = null;
        for (Iterator j = handlerBB.topdownStmts(); j.hasNext(); ) {
          IRStatement stmt = (IRStatement)j.next();
          Matcher matcher = new Matcher(stmt);
          if (matcher.catchnode()) {
            catchStmt = stmt;
            break;
          }
        }
        if (catchStmt == null)
          continue;
        IRStatement phiStmt = null;
        if (cfg.isSSA()) {
          IR.acatch acatch = (IR.acatch)catchStmt.snode();
          phiStmt = new IRStatement(new IR.aphi(acatch.getReg()));
          handlerBB.insertStmtBeforeStmt(phiStmt, catchStmt);
        }
        for (Iterator j = handlerBB.inEdges(); j.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)j.next();
          BasicBlock sourceBB = edge.sourceBB();
          IRStatement newStmt = (IRStatement)catchStmt.clone();
          if (cfg.isSSA()) {
            int reg = cfg.aregSeq();
            IR.acatch acatch = (IR.acatch)newStmt.snode();
            IR.aphi aphi = (IR.aphi)phiStmt.snode();
            acatch.setReg(reg);
            aphi.setReg(edge, reg);
          }
          sourceBB.insertStmtAfterStmt(newStmt, sourceBB.leader());
        }
        handlerBB.removeStmt(catchStmt);
      }

      changed = true;

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void update(int key, int seq)
      : IR.AJUMP(update, update) = { @1.setTarget(seq); }
      | IR.IJUMP(update, update) = { @1.setTarget(seq); }
      | IR.ISWITCH(update) = { @1.setTarget(key, seq); }
      | IR.CALLX(update) = { @1.setHandler(seq); }
      | IR.NCALLX = { @1.setHandler(seq); }
      | IR.INITX(update) = { @1.setHandler(seq); }
      | IR.NEWINSTANCEX(update) = { @1.setHandler(seq); }
      | IR.NEWARRAYX(update, update) = { @1.setHandler(seq); }
      | IR.LOCKX(update) = { @1.setHandler(seq); }
      | default
      ;

    public boolean phinode(IRControlEdge oldEdge, IRControlEdge newEdge)
      : IR.LABEL = { return true; }
      | IR.IPHI = { @1.setReg(newEdge, @1.getReg(oldEdge)); @1.removeEdge(oldEdge); return true; }
      | IR.LPHI = { @1.setReg(newEdge, @1.getReg(oldEdge)); @1.removeEdge(oldEdge); return true; }
      | IR.FPHI = { @1.setReg(newEdge, @1.getReg(oldEdge)); @1.removeEdge(oldEdge); return true; }
      | IR.DPHI = { @1.setReg(newEdge, @1.getReg(oldEdge)); @1.removeEdge(oldEdge); return true; }
      | IR.APHI = { @1.setReg(newEdge, @1.getReg(oldEdge)); @1.removeEdge(oldEdge); return true; }
      | default = { return false; }
      ;

    public boolean catchnode()
      : IR.ACATCH = { return true; }
      | default = { return false; }
      ;

  }

}

