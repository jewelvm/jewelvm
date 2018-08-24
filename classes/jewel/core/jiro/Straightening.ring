/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.Iterator;

public final class Straightening implements Optimization {

  public Straightening() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    BasicBlock succBB;
    for (BasicBlock currentBB = cfg.topBB(); currentBB != null; currentBB = currentBB.downBB())
      while (currentBB.outDegree() == 1 && (succBB = (BasicBlock)currentBB.succBBs().next()).inDegree() == 1) {

        BasicBlock downBB = currentBB.downBB();
        if (succBB != downBB) {
          boolean contiguous;
          BasicBlock lastBB = currentBB;
          BasicBlock moveBB = succBB;
          do {
            BasicBlock nextBB = moveBB.downBB();
            contiguous = nextBB != null && cfg.contains(new IRControlEdge(moveBB, IRControlEdge.FALL, nextBB));
            cfg.moveBBAfterBB(moveBB, lastBB);
            lastBB = moveBB;
            moveBB = nextBB;
          } while (contiguous);
          // test if it is a loop of fall through and scape
          // this is a simplified solution
          downBB = currentBB.downBB();
          if (succBB != downBB)
            break;
        }

        IRStatement trailer = (IRStatement)currentBB.trailer();
        if (trailer != null) {
          IR.snode snode = trailer.snode();
          int op = snode.op();
          if (op == IR.JUMP || op == IR.ISWITCH)
            currentBB.removeStmt(trailer);
        }

        IRStatement leader = (IRStatement)succBB.leader();
        if (leader != null) {
          IR.snode snode = leader.snode();
          int op = snode.op();
          if (op == IR.LABEL)
            succBB.removeStmt(leader);
        }

        for (Iterator i = succBB.topdownStmts(); i.hasNext(); ) {
          IRStatement stmt = (IRStatement)i.next();
          i.remove();
          currentBB.appendStmt(stmt);
        }

        for (Iterator i = succBB.outEdges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          BasicBlock targetBB = edge.targetBB();
          IRControlEdge newEdge = (IRControlEdge)edge.clone(currentBB, targetBB);
          cfg.addEdge(newEdge);
          if (cfg.isSSA())
            if (targetBB.inDegree() > 1)
              for (Iterator j = targetBB.topdownStmts(); j.hasNext(); ) {
                IRStatement stmt = (IRStatement)j.next();
                Matcher matcher = new Matcher(stmt);
                if (!matcher.phinode(edge, newEdge))
                  break;
              }
        }

        cfg.removeBB(succBB);

        changed = true;

      }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public boolean phinode(IRControlEdge edge, IRControlEdge newEdge)
      : IR.LABEL = { return true; }
      | IR.IPHI = { @1.setReg(newEdge, @1.getReg(edge)); return true; }
      | IR.LPHI = { @1.setReg(newEdge, @1.getReg(edge)); return true; }
      | IR.FPHI = { @1.setReg(newEdge, @1.getReg(edge)); return true; }
      | IR.DPHI = { @1.setReg(newEdge, @1.getReg(edge)); return true; }
      | IR.APHI = { @1.setReg(newEdge, @1.getReg(edge)); return true; }
      | default = { return false; }
      ;

  }

}

