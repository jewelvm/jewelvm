/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.control;

import jewel.core.jiro.control.DominatorTree.DominatorNode;
import jewel.core.jiro.control.LoopTree.LoopNode;
import jewel.core.jiro.graph.DirectedGraph.DirectedNode;

import java.io.PrintStream;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BasicBlock extends DirectedNode implements Cloneable {

  BasicBlock upBB;
  BasicBlock downBB;
  int index;
  int offset;

  private int count;
  private Statement leader;
  private Statement trailer;
  private Statement[] stmts;

  public BasicBlock() { }

  public final ControlFlowGraph ownerCFG() {
    return (ControlFlowGraph)ownerGraph();
  }

  public final BasicBlock upBB() {
    return upBB;
  }

  public final BasicBlock downBB() {
    return downBB;
  }

  public final int count() {
    return count;
  }

  public final Statement leader() {
    return leader;
  }

  public final Statement trailer() {
    return trailer;
  }

  private void updateStmts() {
    stmts = new Statement[count];
    int index = 0;
    for (Iterator i = topdownStmts(); i.hasNext(); index++) {
      Statement stmt = (Statement)i.next();
      stmts[index] = stmt;
      stmt.index = index;
    }
  }

  public final int indexOf(Statement stmt) {
    if (this != stmt.ownerBB)
      return -1;
    if (stmts == null)
      updateStmts();
    return stmt.index;
  }

  public final Statement getStmt(int index) {
    if (index < 0 || index >= count)
      throw new ArrayIndexOutOfBoundsException(index);
    if (stmts == null)
      updateStmts();
    return stmts[index];
  }

  public final boolean contains(Statement stmt) {
    return stmt.ownerBB() == this;
  }

  public void prependStmt(Statement stmt) {
    if (stmt.ownerBB != null)
      throw new IllegalArgumentException("Statement owned by another basic block");
    ControlFlowGraph cfg = ownerCFG();
    if (cfg != null)
      cfg.stmts = null;
    stmts = null;
    count++;
    stmt.ownerBB = this;
    stmt.next = leader;
    if (trailer == null)
      trailer = stmt;
    else
      leader.previous = stmt;
    leader = stmt;
  }

  public void appendStmt(Statement stmt) {
    if (stmt.ownerBB != null)
      throw new IllegalArgumentException("Statement owned by another basic block");
    ControlFlowGraph cfg = ownerCFG();
    if (cfg != null)
      cfg.stmts = null;
    stmts = null;
    count++;
    stmt.ownerBB = this;
    stmt.previous = trailer;
    if (leader == null)
      leader = stmt;
    else
      trailer.next = stmt;
    trailer = stmt;
  }

  public void insertStmtBeforeStmt(Statement stmt, Statement next) {
    if (stmt.ownerBB != null)
      throw new IllegalArgumentException("Statement owned by another basic block");
    if (!contains(next))
      throw new IllegalArgumentException("Next statement not owned by this basic block");
    ControlFlowGraph cfg = ownerCFG();
    if (cfg != null)
      cfg.stmts = null;
    stmts = null;
    count++;
    stmt.ownerBB = this;
    stmt.previous = next.previous;
    stmt.next = next;
    if (leader == next)
      leader = stmt;
    else
      next.previous.next = stmt;
    next.previous = stmt;
  }

  public void insertStmtAfterStmt(Statement stmt, Statement previous) {
    if (stmt.ownerBB != null)
      throw new IllegalArgumentException("Statement owned by another basic block");
    if (!contains(previous))
      throw new IllegalArgumentException("Previous statement not owned by this basic block");
    ControlFlowGraph cfg = ownerCFG();
    if (cfg != null)
      cfg.stmts = null;
    stmts = null;
    count++;
    stmt.ownerBB = this;
    stmt.previous = previous;
    stmt.next = previous.next;
    if (trailer == previous)
      trailer = stmt;
    else
      previous.next.previous = stmt;
    previous.next = stmt;
  }

  public void removeStmt(Statement stmt) {
    if (!contains(stmt))
      throw new IllegalArgumentException("Statement is not owned by this basic block");
    ControlFlowGraph cfg = ownerCFG();
    if (cfg != null)
      cfg.stmts = null;
    stmts = null;
    count--;
    if (stmt == trailer)
      trailer = stmt.previous;
    else
      stmt.next.previous = stmt.previous;
    if (stmt == leader)
      leader = stmt.next;
    else
      stmt.previous.next = stmt.next;
    stmt.ownerBB = null;
    stmt.previous = null;
    stmt.next = null;
  }

  public final Iterator topdownStmts() {
    return new Iterator() {
      private Statement current = null;
      private Statement next = leader;
      public boolean hasNext() {
        return next != null;
      }
      public Object next() { 
        if (next == null)
          throw new NoSuchElementException();
        current = next;
        next = current.next;
        return current;
      }
      public void remove() {
        if (current == null)
          throw new IllegalStateException();
        removeStmt(current);
        current = null;
      }
    };
  }

  public final Iterator bottomupStmts() {
    return new Iterator() {
      private Statement current = null;
      private Statement next = trailer;
      public boolean hasNext() {
        return next != null;
      }
      public Object next() {
        if (next == null)
          throw new NoSuchElementException();
        current = next;
        next = current.previous;
        return current;
      }
      public void remove() {
        if (current == null)
          throw new IllegalStateException();
        removeStmt(current);
        current = null;
      }
    };
  }

  public final Iterator predBBs() {
    return new Iterator() {
      private final Iterator i = inEdges();
      public boolean hasNext() { return i.hasNext(); }
      public Object next() { return ((ControlEdge)i.next()).sourceBB(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }

  public final Iterator succBBs() {
    return new Iterator() {
      private final Iterator i = outEdges();
      public boolean hasNext() { return i.hasNext(); }
      public Object next() { return ((ControlEdge)i.next()).targetBB(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }

  public final boolean dominates(BasicBlock dominateeBB) {
    ControlFlowGraph cfg = ownerCFG();
    if (cfg == null || cfg != dominateeBB.ownerCFG())
      return false;
    DominatorTree dominatorTree = cfg.dominatorTree();
    DominatorNode dominator = dominatorTree.getNode(cfg.indexOf(this));
    DominatorNode dominatee = dominatorTree.getNode(cfg.indexOf(dominateeBB));
    return dominator.dominates(dominatee);
  }

  public final Iterator dominatorBBs() {
    final ControlFlowGraph cfg = ownerCFG();
    if (cfg == null)
      return null;
    final DominatorTree dominatorTree = cfg.dominatorTree();
    final DominatorNode node = dominatorTree.getNode(cfg.indexOf(this));
    return new Iterator() {
      private final Iterator i = node.dominators();
      public boolean hasNext() { return i.hasNext(); }
      public Object next() { return cfg.getBB(dominatorTree.indexOf((DominatorNode)i.next())); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }

  public final Iterator idominateeBBs() {
    final ControlFlowGraph cfg = ownerCFG();
    if (cfg == null)
      return null;
    final DominatorTree dominatorTree = cfg.dominatorTree();
    final DominatorNode node = dominatorTree.getNode(cfg.indexOf(this));
    return new Iterator() {
      private final Iterator i = node.idominatees();
      public boolean hasNext() { return i.hasNext(); }
      public Object next() { return cfg.getBB(dominatorTree.indexOf((DominatorNode)i.next())); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }

  public final Iterator frontierBBs() {
    final ControlFlowGraph cfg = ownerCFG();
    if (cfg == null)
      return null;
    final DominatorTree dominatorTree = cfg.dominatorTree();
    final DominatorNode node = dominatorTree.getNode(cfg.indexOf(this));
    return new Iterator() {
      private final Iterator i = node.frontier();
      public boolean hasNext() { return i.hasNext(); }
      public Object next() { return cfg.getBB(dominatorTree.indexOf((DominatorNode)i.next())); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }

  public final int loopDepth() {
    ControlFlowGraph cfg = ownerCFG();
    if (cfg == null)
      return 0;
    LoopTree loopTree = cfg.loopTree();
    LoopNode node = loopTree.getNode(cfg.indexOf(this));
    return node.depth();
  }

  public Object clone() {
    BasicBlock cloneBB;
    try {
      cloneBB = (BasicBlock)super.clone();
    } catch (CloneNotSupportedException e) {
      throw new Error();
    }
    cloneBB.upBB = null;
    cloneBB.downBB = null;
    cloneBB.index = 0;
    cloneBB.offset = 0;

    Statement previousStmt = null;
    for (Statement stmt = leader; stmt != null; stmt = stmt.next) {
      Statement cloneStmt = (Statement)stmt.clone();
      cloneStmt.ownerBB = cloneBB;
      cloneStmt.previous = previousStmt;
      if (previousStmt == null)
        cloneBB.leader = cloneStmt;
      else
        previousStmt.next = cloneStmt;
      previousStmt = cloneStmt;
    }
    cloneBB.trailer = previousStmt;

    cloneBB.stmts = null;
    return cloneBB;
  }

  public void printTo(PrintStream out) {
    ControlFlowGraph cfg = ownerCFG();
    if (cfg != null)
      out.println("/* BB"+cfg.indexOf(this)+" */");
    for (Iterator i = topdownStmts(); i.hasNext(); ) {
      Statement stmt = (Statement)i.next();
      stmt.printTo(out);
    }
  }

  public String toString() {
    return hashCode()+" [label=\"BB"+ownerCFG().indexOf(this)+"\",shape=box];";
  }

}

