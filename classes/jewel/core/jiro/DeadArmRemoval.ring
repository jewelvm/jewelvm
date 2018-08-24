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

public final class DeadArmRemoval implements Optimization {

  public DeadArmRemoval() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (cfg.isSSA()) {

      ArrayList taken = new ArrayList();
      ArrayList nottaken = new ArrayList();
      for (Iterator i = cfg.entryBBs(); i.hasNext(); ) {
        BasicBlock entryBB = (BasicBlock)i.next();
        visit(entryBB, taken, new HashSet(), nottaken, new HashSet());
      }

      for (Iterator i = taken.iterator(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        IRStatement trailer = (IRStatement)currentBB.trailer();
        int label;
        switch (trailer.snode().op()) {
        case IR.IJUMP:
          IR.ijump ijump = (IR.ijump)trailer.snode();
          label = ijump.getTarget();
          break;
        case IR.AJUMP:
          IR.ajump ajump = (IR.ajump)trailer.snode();
          label = ajump.getTarget();
          break;
        default: throw new Error();
        }
        trailer.setNode(new IR.jump(label));
        for (Iterator j = currentBB.outEdges(); j.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)j.next();
          if (edge.type() == IRControlEdge.FALL) {
            j.remove();
            break;
          }
        }
        changed = true;
      }

      for (Iterator i = nottaken.iterator(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        IRStatement trailer = (IRStatement)currentBB.trailer();
        currentBB.removeStmt(trailer);
        for (Iterator j = currentBB.outEdges(); j.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)j.next();
          if (edge.type() == IRControlEdge.JUMP) {
            j.remove();
            break;
          }
        }
        changed = true;
      }

    }

    return changed;
  }

  private static void visit(BasicBlock currentBB, ArrayList taken, HashSet staken, ArrayList nottaken, HashSet snottaken) {
    IRStatement trailer = (IRStatement)currentBB.trailer();
    Jump jump = null;
    BasicBlock downBB = null;
    BasicBlock targetBB = null;
    if (trailer != null) {
      Matcher matcher = new Matcher(trailer);
      jump = matcher.collect();
      if (jump != null) {
        Jump inverted = jump.inverted();
        if (staken.contains(jump) || snottaken.contains(inverted))
          taken.add(currentBB);
        else if (snottaken.contains(jump) || staken.contains(inverted))
          nottaken.add(currentBB);
        else {
          downBB = currentBB.downBB();
          for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)i.next();
            if (edge.type() == IRControlEdge.JUMP) {
              targetBB = edge.targetBB();
              break;
            }
          }
        }
      }
    }
    for (Iterator i = currentBB.idominateeBBs(); i.hasNext(); ) {
      BasicBlock idomBB = (BasicBlock)i.next();
      if (idomBB == targetBB) staken.add(jump);
      if (idomBB == downBB) snottaken.add(jump);
      visit(idomBB, taken, staken, nottaken, snottaken);
      if (idomBB == targetBB) staken.remove(jump);
      if (idomBB == downBB) snottaken.remove(jump);
    }
  }

  private static final class Jump {

    private final int xop;
    private final TreeNode left;
    private final TreeNode right;

    private Jump(int xop, TreeNode left, TreeNode right) {
      this.xop = xop;
      this.left = left;
      this.right = right;
    }

    public Jump(IR.ijump ijump) {
      this(ijump.xop(), ijump.left(), ijump.right());
    }

    public Jump(IR.ajump ajump) {
      this(ajump.xop(), ajump.left(), ajump.right());
    }

    public int hashCode() {
      return xop+left.hashCode()+right.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof Jump
          && ((Jump)object).xop == xop
          && ((Jump)object).left.equals(left)
          && ((Jump)object).right.equals(right);
    }

    public Jump inverted() {
      int xop = this.xop;
      switch (xop) {
      case IR.ijump.EQ: xop = IR.ijump.NE; break;
      case IR.ijump.NE: xop = IR.ijump.EQ; break;
      case IR.ijump.LT: xop = IR.ijump.GE; break;
      case IR.ijump.GE: xop = IR.ijump.LT; break;
      case IR.ijump.GT: xop = IR.ijump.LE; break;
      case IR.ijump.LE: xop = IR.ijump.GT; break;
      case IR.ijump.B : xop = IR.ijump.AE; break;
      case IR.ijump.AE: xop = IR.ijump. B; break;
      case IR.ijump.A : xop = IR.ijump.BE; break;
      case IR.ijump.BE: xop = IR.ijump. A; break;
      default: throw new Error();
      }
      return new Jump(xop, left, right);
    }

  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public Jump collect()
      : IR.AJUMP(nomemlock, nomemlock) [@2.valid && @3.valid] = { return new Jump(@1); }
      | IR.IJUMP(nomemlock, nomemlock) [@2.valid && @3.valid] = { return new Jump(@1); }
      | default = { return null; }
      ;

    private void nomemlock()
    <boolean valid>
      : IR.ALOAD(nomemlock) { @@.valid = false; }
      | IR.AALOAD(nomemlock,nomemlock) { @@.valid = false; }
      | IR.BLOAD(nomemlock) { @@.valid = false; }
      | IR.SLOAD(nomemlock) { @@.valid = false; }
      | IR.ILOAD(nomemlock) { @@.valid = false; }
      | IR.BALOAD(nomemlock,nomemlock) { @@.valid = false; }
      | IR.SALOAD(nomemlock,nomemlock) { @@.valid = false; }
      | IR.IALOAD(nomemlock,nomemlock) { @@.valid = false; }
      | IR.ISLOCKED(nomemlock) { @@.valid = false; }
      | IR.LLOAD(nomemlock) { @@.valid = false; }
      | IR.LALOAD(nomemlock,nomemlock) { @@.valid = false; }
      | IR.FLOAD(nomemlock) { @@.valid = false; }
      | IR.FALOAD(nomemlock,nomemlock) { @@.valid = false; }
      | IR.DLOAD(nomemlock) { @@.valid = false; }
      | IR.DALOAD(nomemlock,nomemlock) { @@.valid = false; }
      | default {
        @@.valid = true;
        if (  left$ != null) @@.valid &=   left$.nomemlock.valid;
        if (middle$ != null) @@.valid &= middle$.nomemlock.valid;
        if ( right$ != null) @@.valid &=  right$.nomemlock.valid;
      }
      ;

  }

}

