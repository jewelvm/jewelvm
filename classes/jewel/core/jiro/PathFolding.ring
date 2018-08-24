/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.Iterator;

// updated for SSA
public final class PathFolding implements Optimization {

  public PathFolding() { }

  public boolean applyTo(ControlFlowGraph cfg) {
    boolean changed = false;

    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      IRStatement stmt = (IRStatement)currentBB.trailer();
      if (stmt != null) {
        Matcher matcher = new Matcher(stmt);
        changed |= matcher.cover();
      }
    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }
  
    public boolean cover()
      : IR.AJUMP(IR.ANULL,IR.ANULL) = {
        boolean branches;
        switch (@1.xop()) {
        case IR.ijump.EQ: branches =  true; break;
        case IR.ijump.NE: branches = false; break;
        default: throw new Error();
        }
        IRStatement stmt = @1.ownerStmt();
        BasicBlock currentBB = (BasicBlock)stmt.ownerBB();
        byte edgeType;
        if (branches) {
          stmt.setNode(new IR.jump(@1.getTarget()));
          edgeType = IRControlEdge.JUMP;
        } else {
          currentBB.removeStmt(stmt);
          edgeType = IRControlEdge.FALL;
        }
        for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          if (edge.type() != edgeType)
            i.remove();
        }
        return true;
      }
      | IR.IJUMP(IR.ICONST,IR.ICONST) = {
        int a = @2.getValue();
        int b = @3.getValue();
        boolean branches;
        switch (@1.xop()) {
        case IR.ijump.EQ: branches = a == b; break;
        case IR.ijump.NE: branches = a != b; break;
        case IR.ijump.LT: branches = a <  b; break;
        case IR.ijump.GE: branches = a >= b; break;
        case IR.ijump.GT: branches = a >  b; break;
        case IR.ijump.LE: branches = a <= b; break;
        case IR.ijump.B : branches = ((long)a & 0xFFFFFFFFL) <  ((long)b & 0xFFFFFFFFL); break;
        case IR.ijump.AE: branches = ((long)a & 0xFFFFFFFFL) >= ((long)b & 0xFFFFFFFFL); break;
        case IR.ijump.A : branches = ((long)a & 0xFFFFFFFFL) >  ((long)b & 0xFFFFFFFFL); break;
        case IR.ijump.BE: branches = ((long)a & 0xFFFFFFFFL) <= ((long)b & 0xFFFFFFFFL); break;
        default: throw new Error();
        }
        IRStatement stmt = @1.ownerStmt();
        BasicBlock currentBB = (BasicBlock)stmt.ownerBB();
        byte edgeType;
        if (branches) {
          stmt.setNode(new IR.jump(@1.getTarget()));
          edgeType = IRControlEdge.JUMP;
        } else {
          currentBB.removeStmt(stmt);
          edgeType = IRControlEdge.FALL;
        }
        for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          if (edge.type() != edgeType)
            i.remove();
        }
        return true;
      }
      | IR.ISWITCH(IR.ICONST) = {
        int key = @2.getValue();
        int target = @1.getTarget(key);
        boolean branches = target != -1;
        IRStatement stmt = @1.ownerStmt();
        BasicBlock currentBB = (BasicBlock)stmt.ownerBB();
        byte edgeType;
        if (branches) {
          stmt.setNode(new IR.jump(target));
          edgeType = IRControlEdge.SWITCH;
        } else {
          currentBB.removeStmt(stmt);
          edgeType = IRControlEdge.FALL;
          key = 0;
        }
        for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          if (edge.type() != edgeType || edge.value() != key)
            i.remove();
        }
        return true;
      }
      | default = { return false; }
      ;
      
  }

}

