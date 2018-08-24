/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.optimize.Optimization;

import java.util.HashSet;
import java.util.Iterator;

// improve, provide more exact implementation
public final class CrossJumping implements Optimization {

  public CrossJumping() { }

  public boolean applyTo(ControlFlowGraph cfg) {
    boolean changed = false;

    HashSet exits = new HashSet();
    for (Iterator i = cfg.exitBBs(); i.hasNext(); ) {
      BasicBlock exitBB = (BasicBlock)i.next();
      if (exitBB.outDegree() == 0)
        exits.add(exitBB);
    }


    for (BasicBlock currentBB = cfg.topBB(); currentBB != null; currentBB = currentBB.downBB())
      if (bestBB(cfg, currentBB, exits, ((IRCFG)cfg).labelSeq()))
        changed = true;

    return changed;
  }

  private static boolean bestBB(ControlFlowGraph cfg, BasicBlock currentBB, HashSet exits, int label) {

    if (currentBB.outDegree() > 1)
      return false;

    HashSet set = candidates(cfg, currentBB, exits);

    int best = 0;
    BasicBlock bestBB = null;
    for (Iterator i = set.iterator(); i.hasNext(); ) {
      BasicBlock candBB = (BasicBlock)i.next();

      int size = 0;
      for (Iterator j = currentBB.bottomupStmts(), k = candBB.bottomupStmts(); j.hasNext() && k.hasNext(); size++) {
        IRStatement stmt = (IRStatement)j.next();
        if (stmt.snode().op() == IR.JUMP || stmt.snode().op() == IR.ISWITCH) {
          if (!j.hasNext())
            break;
          stmt = (IRStatement)j.next();
        }
        IRStatement cand = (IRStatement)k.next();
        if (cand.snode().op() == IR.JUMP || cand.snode().op() == IR.ISWITCH) {
          if (!k.hasNext())
            break;
          cand = (IRStatement)k.next();
        }
        if (!stmt.equals(cand))
          break;
      }

      if (size > best) {
        best = size;
        bestBB = candBB;
      }

    }

    if (bestBB != null) {

      BasicBlock tailBB = new IRBasicBlock();

      cfg.insertBBAfterBB(tailBB, currentBB);

      IRStatement trailer = (IRStatement)currentBB.trailer();
      if (trailer.snode().op() == IR.JUMP || trailer.snode().op() == IR.ISWITCH) {
        currentBB.removeStmt(trailer);
        tailBB.prependStmt(trailer);
      }
      Iterator j = currentBB.bottomupStmts();
      for (int i = 0; i < best; i++) {
        Statement stmt = (Statement)j.next();
        j.remove();
        tailBB.prependStmt(stmt);
      }

      for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
        ControlEdge edge = (ControlEdge)i.next();
        i.remove();
        cfg.addEdge(edge.clone(tailBB, edge.targetBB()));
      }

      cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.FALL, tailBB));

      tailBB.prependStmt(new IRStatement(new IR.label(label)));

      trailer = (IRStatement)bestBB.trailer();
      if (trailer.snode().op() == IR.JUMP || trailer.snode().op() == IR.ISWITCH)
        bestBB.removeStmt(trailer);
      Iterator k = bestBB.bottomupStmts();
      for (int i = 0; i < best; i++) {
        k.next();
        k.remove();
      }

      for (Iterator i = bestBB.outEdges(); i.hasNext(); ) {
        i.next();
        i.remove();
      }

      bestBB.appendStmt(new IRStatement(new IR.jump(label)));

      cfg.addEdge(new IRControlEdge(bestBB, IRControlEdge.JUMP, tailBB));

      if (exits.contains(currentBB)) {
        exits.remove(currentBB);
        exits.remove(bestBB);
        exits.add(tailBB);
      }

      return true;

    }

    return false;
  }

  private static HashSet candidates(ControlFlowGraph cfg, BasicBlock currentBB, HashSet exits) {
    HashSet set = new HashSet();
    if (currentBB.outDegree() == 0) {
      set = (HashSet)exits.clone();
      set.remove(currentBB);
    } else {
      BasicBlock succBB = (BasicBlock)currentBB.succBBs().next();
      for (Iterator j = succBB.predBBs(); j.hasNext(); ) {
        BasicBlock predBB = (BasicBlock)j.next();
        if (predBB != currentBB)
          if (predBB.outDegree() == 1)
            set.add(predBB);
      }
    }
    return set;
  }
  
}

