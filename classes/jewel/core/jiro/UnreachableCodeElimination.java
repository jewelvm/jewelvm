/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.BitSet;
import java.util.Iterator;

public final class UnreachableCodeElimination implements Optimization {

  public UnreachableCodeElimination() { }

  public boolean applyTo(ControlFlowGraph cfg) {
    boolean changed = false;

    BitSet reachSet = new BitSet(cfg.count());
    for (Iterator i = cfg.entryBBs(); i.hasNext(); ) {
      BasicBlock entryBB = (BasicBlock)i.next();
      dfs1(cfg, entryBB, reachSet);
    }
    int index = 0;
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); index++) {
      BasicBlock currentBB = (BasicBlock)i.next();
      if (!reachSet.get(index)) {
        i.remove();
        changed = true;
      }
    }

    return changed;
  }

  private static void dfs1(ControlFlowGraph cfg, BasicBlock currentBB, BitSet reachSet) {
    int index = cfg.indexOf(currentBB);
    if (!reachSet.get(index)) {
      reachSet.set(index);
      for (Iterator i = currentBB.succBBs(); i.hasNext(); ) {
        BasicBlock succBB = (BasicBlock)i.next();
        dfs1(cfg, succBB, reachSet);
      }
    }
  }

}

