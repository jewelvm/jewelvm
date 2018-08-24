/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.control;

import jewel.core.jiro.graph.DirectedGraph;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ControlFlowGraph extends DirectedGraph implements Cloneable {

  private int count;
  private BasicBlock topBB;
  private BasicBlock bottomBB;
  private BasicBlock[] blocks;
  /*private*/ Statement[] stmts;
  private DominatorTree dominatorTree;
  private LoopTree loopTree;

  public ControlFlowGraph() { }

  public final int count() {
    return count;
  }

  public final BasicBlock topBB() {
    return topBB;
  }

  public final BasicBlock bottomBB() {
    return bottomBB;
  }

  public final Iterator nodes() {
    return topdownBBs();
  }
  
  public final void addNode(DirectedNode node) {
    appendBB((BasicBlock)node);
  }

  public final void removeNode(DirectedNode node) {
    removeBB((BasicBlock)node);
  }

  public final void addEdge(DirectedEdge edge) {
    addEdge((ControlEdge)edge);
  }

  public final void removeEdge(DirectedEdge edge) {
    removeEdge((ControlEdge)edge);
  }

  public void prependBB(BasicBlock newBB) {
    super.addNode(newBB);
    stmts = null;
    blocks = null;
    count++;
    newBB.downBB = topBB;
    if (bottomBB == null)
      bottomBB = newBB;
    else
      topBB.upBB = newBB;
    topBB = newBB;
  }

  public void appendBB(BasicBlock newBB) {
    super.addNode(newBB);
    stmts = null;
    blocks = null;
    count++;
    newBB.upBB = bottomBB;
    if (topBB == null)
      topBB = newBB;
    else
      bottomBB.downBB = newBB;
    bottomBB = newBB;
  }

  public void insertBBBeforeBB(BasicBlock newBB, BasicBlock downBB) {
    if (downBB.ownerCFG() != this)
      throw new IllegalArgumentException("Next basic block not owned by this control flow graph");
    super.addNode(newBB);
    stmts = null;
    blocks = null;
    count++;
    newBB.upBB = downBB.upBB;
    newBB.downBB = downBB;
    if (topBB == downBB)
      topBB = newBB;
    else
      downBB.upBB.downBB = newBB;
    downBB.upBB = newBB;
  }

  public void insertBBAfterBB(BasicBlock newBB, BasicBlock upBB) {
    if (upBB.ownerCFG() != this)
      throw new IllegalArgumentException("Previous basic block not owned by this control flow graph");
    super.addNode(newBB);
    stmts = null;
    blocks = null;
    count++;
    newBB.upBB = upBB;
    newBB.downBB = upBB.downBB;
    if (bottomBB == upBB)
      bottomBB = newBB;
    else
      upBB.downBB.upBB = newBB;
    upBB.downBB = newBB;
  }

  public void moveBBBeforeBB(BasicBlock moveBB, BasicBlock downBB) {
    if (moveBB.ownerCFG() != this)
      throw new IllegalArgumentException("Basic block being moved is not owned by this control flow graph");
    if (downBB.ownerCFG() != this)
      throw new IllegalArgumentException("Next basic block not owned by this control flow graph");
    if (moveBB == downBB)
      throw new IllegalArgumentException("Next basic block and the one being moved are the same");
    if (moveBB.downBB() == downBB)
      return;
    stmts = null;
    blocks = null;
    if (moveBB == bottomBB)
      bottomBB = moveBB.upBB;
    else
      moveBB.downBB.upBB = moveBB.upBB;
    if (moveBB == topBB)
      topBB = moveBB.downBB;
    else
      moveBB.upBB.downBB = moveBB.downBB;
    moveBB.upBB = downBB.upBB;
    moveBB.downBB = downBB;
    if (topBB == downBB)
      topBB = moveBB;
    else
      downBB.upBB.downBB = moveBB;
    downBB.upBB = moveBB;
  }

  public void moveBBAfterBB(BasicBlock moveBB, BasicBlock upBB) {
    if (moveBB.ownerCFG() != this)
      throw new IllegalArgumentException("Basic block being moved is not owned by this control flow graph");
    if (upBB.ownerCFG() != this)
      throw new IllegalArgumentException("Previous basic block not owned by this control flow graph");
    if (moveBB == upBB)
      throw new IllegalArgumentException("Previous basic block and the one being moved are the same");
    if (moveBB.upBB() == upBB)
      return;
    stmts = null;
    blocks = null;
    if (moveBB == bottomBB)
      bottomBB = moveBB.upBB;
    else
      moveBB.downBB.upBB = moveBB.upBB;
    if (moveBB == topBB)
      topBB = moveBB.downBB;
    else
      moveBB.upBB.downBB = moveBB.downBB;
    moveBB.upBB = upBB;
    moveBB.downBB = upBB.downBB;
    if (bottomBB == upBB)
      bottomBB = moveBB;
    else
      upBB.downBB.upBB = moveBB;
    upBB.downBB = moveBB;
  }

  public void removeBB(BasicBlock oldBB) {
    super.removeNode(oldBB);
    stmts = null;
    blocks = null;
    count--;
    if (oldBB == bottomBB)
      bottomBB = oldBB.upBB;
    else
      oldBB.downBB.upBB = oldBB.upBB;
    if (oldBB == topBB)
      topBB = oldBB.downBB;
    else
      oldBB.upBB.downBB = oldBB.downBB;
    oldBB.upBB = null;
    oldBB.downBB = null;
  }

  public void addEdge(ControlEdge edge) {
    super.addEdge(edge);
    dominatorTree = null;
    loopTree = null;
  }

  public void removeEdge(ControlEdge edge) {
    super.removeEdge(edge);
    dominatorTree = null;
    loopTree = null;
  }

  public Iterator entryBBs() {
    return new Iterator() {
      private BasicBlock nextBB = topBB;
      public boolean hasNext() {
        return nextBB != null;
      }
      public Object next() { 
        if (nextBB == null)
          throw new NoSuchElementException();
        BasicBlock currentBB = nextBB;
        nextBB = null;
        return currentBB;
      }
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public final Iterator exitBBs() {
    final HashSet exits = new HashSet();
    for (Iterator i = topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      if (currentBB.outDegree() == 0)
        exits.add(currentBB);
    }
    HashSet leafs = new HashSet();
    HashSet visited = new HashSet();
    for (Iterator i = entryBBs(); i.hasNext(); ) {
      BasicBlock entryBB = (BasicBlock)i.next();
      dfs1(entryBB, visited, leafs);
    }
    for (Iterator i = leafs.iterator(); i.hasNext(); ) {
      BasicBlock leafBB = (BasicBlock)i.next();
      visited.clear();
      if (!dfs2(leafBB, visited, exits))
        exits.add(leafBB);
    }
    return new Iterator() {
      private final Iterator i = exits.iterator();
      public boolean hasNext() { return i.hasNext(); }
      public Object next() { return i.next(); }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }

  private static int dfs1(BasicBlock currentBB, HashSet visited, HashSet leafs) {
    int count = 0;
    if (!visited.contains(currentBB)) {
      visited.add(currentBB);
      count++;
      for (Iterator i = currentBB.succBBs(); i.hasNext(); ) {
        BasicBlock succBB = (BasicBlock)i.next();
        count += dfs1(succBB, visited, leafs);
      }
      if (count == 1)
        leafs.add(currentBB);
    }
    return count;
  }

  private static boolean dfs2(BasicBlock currentBB, HashSet visited, HashSet exits) {
    if (!visited.contains(currentBB)) {
      visited.add(currentBB);
      if (exits.contains(currentBB))
        return true;
      for (Iterator i = currentBB.succBBs(); i.hasNext(); ) {
        BasicBlock succBB = (BasicBlock)i.next();
        if (dfs2(succBB, visited, exits))
          return true;
      }
    }
    return false;
  }

  public final Iterator topdownBBs() {
    return new Iterator() {
      private BasicBlock currentBB = null;
      private BasicBlock nextBB = topBB;
      public boolean hasNext() {
        return nextBB != null;
      }
      public Object next() {
        if (nextBB == null)
          throw new NoSuchElementException();
        currentBB = nextBB;
        nextBB = currentBB.downBB;
        return currentBB;
      }
      public void remove() {
        if (currentBB == null)
          throw new IllegalStateException();
        removeBB(currentBB);
        currentBB = null;
      }
    };
  }

  public final Iterator bottomupBBs() {
    return new Iterator() {
      private BasicBlock currentBB = null;
      private BasicBlock nextBB = bottomBB;
      public boolean hasNext() {
        return nextBB != null;
      }
      public Object next() {
        if (nextBB == null)
          throw new NoSuchElementException();
        currentBB = nextBB;
        nextBB = currentBB.upBB;
        return currentBB;
      }
      public void remove() {
        if (currentBB == null)
          throw new IllegalStateException();
        removeNode(currentBB);
        currentBB = null;
      }
    };
  }

  private void updateBlocks() {
    blocks = new BasicBlock[count];
    int index = 0;
    for (Iterator i = topdownBBs(); i.hasNext(); index++) {
      BasicBlock currentBB = (BasicBlock)i.next();
      blocks[index] = currentBB;
      currentBB.index = index;
    }
  }

  public final int indexOf(BasicBlock paramBB) {
    if (this != paramBB.ownerCFG())
      return -1;
    if (blocks == null)
      updateBlocks();
    return paramBB.index;
  }

  public final BasicBlock getBB(int index) {
    if (index < 0 || index >= count)
      throw new ArrayIndexOutOfBoundsException(index);
    if (blocks == null)
      updateBlocks();
    return blocks[index];
  }

  private void updateStmts() {
    int stmtCount = 0;
    for (Iterator i = topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      currentBB.offset = stmtCount;
      stmtCount += currentBB.count();
    }
    stmts = new Statement[stmtCount];
    int index = 0;
    for (Iterator i = topdownStmts(); i.hasNext(); index++) {
      Statement stmt = (Statement)i.next();
      stmts[index] = stmt;
    }
  }

  public final int stmtCount() {
    if (stmts == null)
      updateStmts();
    return stmts.length;
  }

  public final int indexOf(Statement stmt) {
    BasicBlock ownerBB = stmt.ownerBB();
    if (ownerBB == null || this != ownerBB.ownerCFG())
      return -1;
    if (stmts == null)
      updateStmts();
    return ownerBB.offset+ownerBB.indexOf(stmt);
  }

  public final Statement getStmt(int index) {
    if (stmts == null)
      updateStmts();
    return stmts[index];
  }

  public final Statement firstStmt() {
    for (Iterator i = topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      if (currentBB.count() > 0)
        return currentBB.leader();
    }
    return null;
  }

  public final Statement lastStmt() {
    for (Iterator i = bottomupBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      if (currentBB.count() > 0)
        return currentBB.trailer();
    }
    return null;
  }

  public final boolean contains(Statement stmt) {
    return contains(stmt.ownerBB());
  }

  public final void prependStmt(Statement stmt) {
    BasicBlock topBB = topBB();
    if (topBB == null)
      throw new IllegalStateException("Control flow graph has no basic blocks");
    topBB.appendStmt(stmt);
  }

  public final void appendStmt(Statement stmt) {
    BasicBlock bottomBB = bottomBB();
    if (bottomBB == null)
      throw new IllegalStateException("Control flow graph has no basic blocks");
    bottomBB.appendStmt(stmt);
  }

  public final void insertStmtBeforeStmt(Statement stmt, Statement next) {
    if (!contains(next))
      throw new IllegalArgumentException("Next statement not owned by this control flow graph");
    next.ownerBB().insertStmtBeforeStmt(stmt, next);
  }

  public final void insertStmtAfterStmt(Statement stmt, Statement previous) {
    if (!contains(previous))
      throw new IllegalArgumentException("Previous statement not owned by this control flow graph");
    previous.ownerBB().insertStmtAfterStmt(stmt, previous);
  }

  public final void removeStmt(Statement stmt) {
    if (!contains(stmt))
      throw new IllegalArgumentException("Statement is not owned by this control flow graph");
    stmt.ownerBB().removeStmt(stmt);
  }

  public final Iterator topdownStmts() {
    return new Iterator() {
      private Statement current = null;
      private Statement next = firstStmt();
      public boolean hasNext() {
        return next != null;
      }
      public Object next() {
        if (next == null)
          throw new NoSuchElementException();
        current = next;
        next = current.nextStmt();
        return current;
      }
      public void remove() {
        if (current == null)
          throw new IllegalStateException();
        current.ownerBB().removeStmt(current);
        current = null;
      }
    };
  }

  public final Iterator bottomupStmts() {
    return new Iterator() {
      private Statement current = null;
      private Statement next = lastStmt();
      public boolean hasNext() {
        return next != null;
      }
      public Object next() {
        if (next == null)
          throw new NoSuchElementException();
        current = next;
        next = current.previousStmt();
        return current;
      }
      public void remove() {
        if (current == null)
          throw new IllegalStateException();
        current.ownerBB().removeStmt(current);
        current = null;
      }
    };
  }

  public DominatorTree dominatorTree() {
    if (dominatorTree == null)
      dominatorTree = new DominatorTree(this);
    return dominatorTree;
  }

  public LoopTree loopTree() {
    if (loopTree == null)
      loopTree = new LoopTree(this);
    return loopTree;
  }

  public Object clone() {
    ControlFlowGraph cfg;
    
    try {
      cfg = (ControlFlowGraph)super.clone();
    } catch (CloneNotSupportedException e) {
      throw new Error();
    }

    cfg.count = 0;
    cfg.topBB = null;
    cfg.bottomBB = null;
    cfg.stmts = null;
    cfg.blocks = null;
    cfg.dominatorTree = null;
    cfg.loopTree = null;

    for (Iterator i = topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      cfg.appendBB((BasicBlock)currentBB.clone());
    }

    for (Iterator i = edges(); i.hasNext(); ) {
      ControlEdge edge = (ControlEdge)i.next();
      cfg.addEdge(edge.clone(cfg.getBB(indexOf(edge.sourceBB())), cfg.getBB(indexOf(edge.targetBB()))));
    }

    cfg.dominatorTree = dominatorTree;
    cfg.loopTree = loopTree;

    return cfg;
  }

  public void printTo(PrintStream out) {
    for (Iterator i = topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      currentBB.printTo(out);
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("digraph ControlFlowGraph {");
    sb.append("entry [label=\"Entry\",style=dashed];");
    for (Iterator i = entryBBs(); i.hasNext(); ) {
      BasicBlock entryBB = (BasicBlock)i.next();
      sb.append("entry -> "+entryBB.hashCode()+" [style=dashed];");
    }
    for (Iterator i = nodes(); i.hasNext(); )
      sb.append(i.next());
    for (Iterator i = edges(); i.hasNext(); )
      sb.append(i.next());
    for (Iterator i = exitBBs(); i.hasNext(); ) {
      BasicBlock exitBB = (BasicBlock)i.next();
      sb.append(exitBB.hashCode()+" -> exit [style=dashed];");
    }
    sb.append("exit [label=\"Exit\",style=dashed];");
    sb.append("}");
    return sb.toString();
  }

}

