/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.optimize.Optimization;

import java.util.Iterator;

public final class BranchOptimizations implements Optimization {

  public BranchOptimizations() { }

  public boolean applyTo(ControlFlowGraph cfg) {
    boolean changed = false;

    /* case 1: block ends with conditional jump to down.down and down is an unconditional jump */
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      IRStatement condStmt = (IRStatement)currentBB.trailer();
      if (condStmt == null) continue;
      int op = condStmt.snode().op();
      if (op != IR.AJUMP && op != IR.IJUMP) continue;
      BasicBlock downBB = currentBB.downBB();
      if (downBB.count() != 1) continue;
      IRStatement jumpStmt = (IRStatement)downBB.trailer();
      if (jumpStmt.snode().op() != IR.JUMP) continue;
      BasicBlock downdownBB = downBB.downBB();
      if (downdownBB == null) continue;
      ControlEdge edge = new IRControlEdge(currentBB, IRControlEdge.JUMP, downdownBB);
      if (!cfg.contains(edge)) continue;
      IR.jump jump = (IR.jump)jumpStmt.snode();
      BasicBlock targetBB = (BasicBlock)downBB.succBBs().next();
      if (op == IR.AJUMP) {
        IR.ajump ajump = (IR.ajump)condStmt.snode();
        ajump.invertXop();
        ajump.setTarget(jump.getTarget());
      } else {
        IR.ijump ijump = (IR.ijump)condStmt.snode();
        ijump.invertXop();
        ijump.setTarget(jump.getTarget());
      }
      downBB.removeStmt(jumpStmt);
      cfg.removeEdge(edge);
      cfg.removeEdge(new IRControlEdge(downBB, IRControlEdge.JUMP, targetBB));
      cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, targetBB));
      cfg.addEdge(new IRControlEdge(downBB, IRControlEdge.FALL, downdownBB));
      changed = true;
    }

    /* case 2: block ends with conditional jump to down.down and down does not fall and down.down does fall */
    for (BasicBlock currentBB = cfg.topBB(); currentBB != null; currentBB = currentBB.downBB()) {
      // if move BBs do not use iterator
      IRStatement trailer = (IRStatement)currentBB.trailer();
      if (trailer == null) continue;
      int op = trailer.snode().op();
      if (op != IR.AJUMP && op != IR.IJUMP) continue;
      BasicBlock downBB = currentBB.downBB();
      BasicBlock downdownBB = downBB.downBB();
      if (downdownBB == null) continue;
      BasicBlock downdowndownBB = downdownBB.downBB();
      if (downdowndownBB == null) continue;
      ControlEdge edge1 = new IRControlEdge(currentBB, IRControlEdge.JUMP, downdownBB);
      if (!cfg.contains(edge1)) continue;
      ControlEdge edge2 = new IRControlEdge(downdownBB, IRControlEdge.FALL, downdowndownBB);
      if (!cfg.contains(edge2)) continue;
      ControlEdge edge3 = new IRControlEdge(downBB, IRControlEdge.FALL, downdownBB);
      if (cfg.contains(edge3)) continue;
      BasicBlock bottomBB = cfg.bottomBB();
      IRStatement leader = (IRStatement)downBB.leader();
      if (leader == null || leader.snode().op() != IR.LABEL) {
        leader = new IRStatement(new IR.label(((IRCFG)cfg).labelSeq()));
        downBB.prependStmt(leader);
      }
      IR.label label = (IR.label)leader.snode();
      if (op == IR.AJUMP) {
        IR.ajump ajump = (IR.ajump)trailer.snode();
        ajump.invertXop();
        ajump.setTarget(label.getValue());
      } else {
        IR.ijump ijump = (IR.ijump)trailer.snode();
        ijump.invertXop();
        ijump.setTarget(label.getValue());
      }
      cfg.moveBBAfterBB(downBB, bottomBB);
      cfg.removeEdge(new IRControlEdge(currentBB, IRControlEdge.FALL, downBB));
      cfg.removeEdge(edge1);
      cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.FALL, downdownBB));
      cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, downBB));
      changed = true;
    }

    /* case 3: block jumps and falls to the same block */
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      IRStatement stmt = (IRStatement)currentBB.trailer();
      if (stmt == null) continue;
      int op = stmt.snode().op();
      if (op != IR.AJUMP && op != IR.IJUMP) continue;
      BasicBlock downBB = currentBB.downBB();
      ControlEdge edge = new IRControlEdge(currentBB, IRControlEdge.JUMP, downBB);
      if (!cfg.contains(edge)) continue;
      currentBB.removeStmt(stmt);
      cfg.removeEdge(edge);
      changed = true;
    }

    /* case 4: block switches and falls to the same block, or is too simple */
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      IRStatement stmt = (IRStatement)currentBB.trailer();
      if (stmt == null) continue;
      int op = stmt.snode().op();
      if (op != IR.ISWITCH) continue;
      IR.iswitch iswitch = (IR.iswitch)stmt.snode();
      BasicBlock downBB = currentBB.downBB();
      for (Iterator j = iswitch.pairs(); j.hasNext(); ) {
        IR.iswitch.pair pair = (IR.iswitch.pair)j.next();
        ControlEdge edge = new IRControlEdge(currentBB, IRControlEdge.SWITCH, pair.getKey(), downBB);
        if (cfg.contains(edge)) {
          j.remove();
          cfg.removeEdge(edge);
          changed = true;
        }
      }
      if (currentBB.outDegree() <= 2) {
        currentBB.removeStmt(stmt);
        if (currentBB.outDegree() == 2) {
          IR.iswitch.pair pair = (IR.iswitch.pair)iswitch.pairs().next();
          IR.ijump ijump = new IR.ijump(IR.ijump.EQ, (IR.inode)iswitch.left(), new IR.iconst(pair.getKey()), pair.getTarget());
          currentBB.appendStmt(new IRStatement(ijump));
        }
        changed = true;
      }
    }

    /* case 5: block ends with jump to another who is not fallen thru */
    for (BasicBlock currentBB = cfg.topBB(); currentBB != null; currentBB = currentBB.downBB()) {
      IRStatement trailer = (IRStatement)currentBB.trailer();
      if (trailer == null) continue;
      if (trailer.snode().op() != IR.JUMP) continue;
      BasicBlock targetBB = (BasicBlock)currentBB.succBBs().next();
      BasicBlock upBB = targetBB.upBB();
      if (upBB == null) continue;
      ControlEdge edge = new IRControlEdge(upBB, IRControlEdge.FALL, targetBB);
      if (cfg.contains(edge)) continue;
      if (firstFall(cfg, currentBB) == targetBB) continue;
      currentBB.removeStmt(trailer);
      cfg.removeEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, targetBB));
      cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.FALL, targetBB));
      BasicBlock moveBB = currentBB;
      BasicBlock downBB = targetBB;
      while (downBB != null && cfg.contains(new IRControlEdge(moveBB, IRControlEdge.FALL, downBB))) {
        BasicBlock tmpBB = downBB.downBB();
        cfg.moveBBAfterBB(downBB, moveBB);
        moveBB = downBB;
        downBB = tmpBB;
      }
      changed = true;
    }

    /* case 6: block is a label followed by a jump to another label */
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      if (currentBB.count() != 2) continue;
      IRStatement leader = (IRStatement)currentBB.leader();
      if (leader.snode().op() != IR.LABEL) continue;
      IRStatement trailer = (IRStatement)currentBB.trailer();
      if (trailer.snode().op() != IR.JUMP) continue;
      IR.label label = (IR.label)leader.snode();
      IR.jump jump = (IR.jump)trailer.snode();
      int oldlabel = label.getValue();
      int newlabel = jump.getTarget();
      if (oldlabel == newlabel) continue;
      BasicBlock targetBB = (BasicBlock)currentBB.succBBs().next();
      currentBB.removeStmt(leader);
      for (Iterator j = currentBB.inEdges(); j.hasNext(); ) {
        IRControlEdge edge = (IRControlEdge)j.next();
        if (edge.type() != IRControlEdge.FALL) {
          BasicBlock sourceBB = edge.sourceBB();
          IRStatement stmt = (IRStatement)sourceBB.trailer();
          changeLabel(oldlabel, newlabel, stmt.snode());
          j.remove();
          cfg.addEdge(edge.clone(sourceBB, targetBB));
        }
      }
      changed = true;
    }

    /* case 7: block has only label */
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      if (currentBB.count() != 1) continue;
      IRStatement stmt = (IRStatement)currentBB.trailer();
      if (stmt.snode().op() != IR.LABEL) continue;
      currentBB.removeStmt(stmt);
      IR.label label1 = (IR.label)stmt.snode();
      int oldlabel = label1.getValue();
      int newlabel = oldlabel;
      BasicBlock downBB = currentBB.downBB();
      IRStatement leader = (IRStatement)downBB.leader();
      if (leader == null || leader.snode().op() != IR.LABEL)
        downBB.prependStmt(stmt);
      else {
        IR.label label2 = (IR.label)leader.snode();
        newlabel = label2.getValue();
      }
      for (Iterator j = currentBB.inEdges(); j.hasNext(); ) {
        IRControlEdge edge = (IRControlEdge)j.next();
        if (edge.type() != IRControlEdge.FALL) {
          BasicBlock sourceBB = edge.sourceBB();
          if (newlabel != oldlabel) {
            IRStatement trailer = (IRStatement)sourceBB.trailer();
            changeLabel(oldlabel, newlabel, trailer.snode());
          }
          j.remove();
          cfg.addEdge(edge.clone(sourceBB, downBB));
        }
      }
      changed = true;
    }

    /* case 8: empty block */
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      if (currentBB.count() != 0) continue;
      BasicBlock upBB = currentBB.upBB();
      if (upBB != null) {
        ControlEdge edge = new IRControlEdge(upBB, IRControlEdge.FALL, currentBB);
        if (cfg.contains(edge)) {
          BasicBlock downBB = currentBB.downBB();
          cfg.addEdge(new IRControlEdge(upBB, IRControlEdge.FALL, downBB));
        }
      }
      i.remove();
      changed = true;
    }

    return changed;
  }

  private static void changeLabel(int oldlabel, int newlabel, IR.snode snode) {
    switch (snode.op()) {
    case IR.CALLX:
      IR.callx callx = (IR.callx)snode;
      if (callx.getHandler() == oldlabel)
        callx.setHandler(newlabel);
      break;
    case IR.NCALLX:
      IR.ncallx ncallx = (IR.ncallx)snode;
      if (ncallx.getHandler() == oldlabel)
        ncallx.setHandler(newlabel);
      break;
    case IR.JUMP:
      IR.jump jump = (IR.jump)snode;
      if (jump.getTarget() == oldlabel)
        jump.setTarget(newlabel);
      break;
    case IR.IJUMP:
      IR.ijump ijump = (IR.ijump)snode;
      if (ijump.getTarget() == oldlabel)
        ijump.setTarget(newlabel);
      break;
    case IR.AJUMP:
      IR.ajump ajump = (IR.ajump)snode;
      if (ajump.getTarget() == oldlabel)
        ajump.setTarget(newlabel);
      break;
    case IR.ISWITCH:
      IR.iswitch iswitch = (IR.iswitch)snode;
      for (Iterator j = iswitch.pairs(); j.hasNext(); ) {
        IR.iswitch.pair pair = (IR.iswitch.pair)j.next();
        if (pair.getTarget() == oldlabel)
          pair.setTarget(newlabel);
      }
      break;
    case IR.INITX:
      IR.initx initx = (IR.initx)snode;
      if (initx.getHandler() == oldlabel)
        initx.setHandler(newlabel);
      break;
    case IR.NEWINSTANCEX:
      IR.newinstancex newinstancex = (IR.newinstancex)snode;
      if (newinstancex.getHandler() == oldlabel)
        newinstancex.setHandler(newlabel);
      break;
    case IR.NEWARRAYX:
      IR.newarrayx newarrayx = (IR.newarrayx)snode;
      if (newarrayx.getHandler() == oldlabel)
        newarrayx.setHandler(newlabel);
      break;
    case IR.LOCKX:
      IR.lockx lockx = (IR.lockx)snode;
      if (lockx.getHandler() == oldlabel)
        lockx.setHandler(newlabel);
      break;
    }
  }

  private static BasicBlock firstFall(ControlFlowGraph cfg, BasicBlock currentBB) {
    BasicBlock upBB = currentBB.upBB();
    while (upBB != null && cfg.contains(new IRControlEdge(upBB, IRControlEdge.FALL, currentBB))) {
      currentBB = upBB;
      upBB = upBB.upBB();
    }
    return currentBB;
  }

}

