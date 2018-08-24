/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.LoopTree;
import jewel.core.jiro.control.LoopTree.Loop;
import jewel.core.jiro.control.LoopTree.LoopNode;
import jewel.core.jiro.graph.DirectedGraph.DirectedEdge;
import jewel.core.jiro.optimize.Optimization;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Iterator;

// very very simple
public final class UselessStoreRemoval implements Optimization {

  public UselessStoreRemoval() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher matcher = new Matcher(stmt);
      if (matcher.eligible()) {
        i.remove();
        changed = true;
      }
    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    private boolean newinstance()
      : IR.NEWINSTANCE(IR.ACLASS) = { return true; }
      | IR.NEWINSTANCEX(IR.ACLASS) = { return true; }
      | default = { return false; }
      ;

    private boolean aresult(int reg)
      : IR.ARESULT = {
        if (@1.getReg() != reg)
          return false;
        IRStatement owner = @1.ownerStmt();
        IRStatement previous = (IRStatement)owner.previousStmt();
        if (previous == null)
          return false;
        Matcher matcher = new Matcher(previous);
        return matcher.newinstance();
      }
      | default = { return false; }
      ;

    public boolean eligible()
      : IR.ISTORE(IR.AUSE, IR.ICONST) [@3.getValue() == 0] = {
        IRStatement owner = @1.ownerStmt();
        IRStatement previous = (IRStatement)owner.previousStmt();
        if (previous == null)
          return false;
        Matcher matcher = new Matcher(previous);
        return matcher.aresult(@2.getReg());
      }
      | IR.LSTORE(IR.AUSE, IR.LCONST) [@3.getValue() == 0] = {
        IRStatement owner = @1.ownerStmt();
        IRStatement previous = (IRStatement)owner.previousStmt();
        if (previous == null)
          return false;
        Matcher matcher = new Matcher(previous);
        return matcher.aresult(@2.getReg());
      }
      | IR.FSTORE(IR.AUSE, IR.FCONST) [Float.floatToIntBits(@3.getValue()) == 0] = {
        IRStatement owner = @1.ownerStmt();
        IRStatement previous = (IRStatement)owner.previousStmt();
        if (previous == null)
          return false;
        Matcher matcher = new Matcher(previous);
        return matcher.aresult(@2.getReg());
      }
      | IR.DSTORE(IR.AUSE, IR.DCONST) [Double.doubleToLongBits(@3.getValue()) == 0] = {
        IRStatement owner = @1.ownerStmt();
        IRStatement previous = (IRStatement)owner.previousStmt();
        if (previous == null)
          return false;
        Matcher matcher = new Matcher(previous);
        return matcher.aresult(@2.getReg());
      }
      | IR.ASTORE(IR.AUSE, IR.ANULL) = {
        IRStatement owner = @1.ownerStmt();
        IRStatement previous = (IRStatement)owner.previousStmt();
        if (previous == null)
          return false;
        Matcher matcher = new Matcher(previous);
        return matcher.aresult(@2.getReg());
      }
      | default = { return false; }
      ;

  }

}

