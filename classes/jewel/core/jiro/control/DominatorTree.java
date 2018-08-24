/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.control;

import jewel.core.jiro.graph.DirectedGraph;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class DominatorTree extends DirectedGraph {

  private final DominatorNode[] nodes;
  private DominatorNode exit_idom;

  public DominatorTree(ControlFlowGraph cfg) {
    
    /* epilogue */
    nodes = new DominatorNode[cfg.count()];
    int index = 0;
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); index++) {
      BasicBlock currentBB = (BasicBlock)i.next();
      DominatorNode node = new DominatorNode(index);
      nodes[index] = node;
      super.addNode(node);
    }

    /* create canonical entry */
    DominatorNode entry = new DominatorNode();

    /* depth first search */
    DominatorNode first = entry;
    DominatorNode last = entry;
    for (Iterator i = cfg.entryBBs(); i.hasNext(); ) {
      BasicBlock entryBB = (BasicBlock)i.next();
      DominatorNode current = nodes[cfg.indexOf(entryBB)];
      last = dfs1(cfg, last, entry, current);
    }

    /* build dominator tree */
    for (DominatorNode current = last; current != first; current = current.previous) {

      if (current.parent == entry)
        current.sdom = entry;
      else
        for (Iterator i = cfg.getBB(current.index).predBBs(); i.hasNext(); ) {
          BasicBlock predBB = (BasicBlock)i.next();
          DominatorNode pred = nodes[cfg.indexOf(predBB)];
          if (pred.dfnum <= current.dfnum) {
            if (pred.dfnum < current.sdom.dfnum)
              current.sdom = pred;
          } else
            do {
              if (pred.sdom.dfnum < current.sdom.dfnum)
                current.sdom = pred.sdom;
              pred = pred.parent;
            } while (pred.dfnum > current.dfnum);
        }

      current.bucket = current.sdom.bucket;
      current.sdom.bucket = current;

      for (DominatorNode vblk = current.parent.bucket; vblk != null; vblk = vblk.bucket) {
        DominatorNode ublk = vblk;
        DominatorNode cblk = vblk.parent;
        while (cblk.dfnum >= current.dfnum) {
          if (cblk.sdom.dfnum < ublk.sdom.dfnum)
            ublk = cblk;
          cblk = cblk.parent;
        }
        vblk.idom = ublk == vblk ? current.parent : ublk;
      }

      current.parent.bucket = null;
    }

    first.sdom = null;
    for (DominatorNode current = first; current != null; current = current.next)
      if (current.idom != current.sdom)
        current.idom = current.idom.idom;

    /* find dominance frontiers */
    for (DominatorNode current = last; current != first; current = current.previous) {

      for (Iterator i = cfg.getBB(current.index).succBBs(); i.hasNext(); ) {
        BasicBlock succBB = (BasicBlock)i.next();
        DominatorNode succ = nodes[cfg.indexOf(succBB)];
        if (succ.idom != current)
          current.frontier.add(succ);
      }

      for (Iterator i = current.frontier.iterator(); i.hasNext(); ) {
        DominatorNode node = (DominatorNode)i.next();
        if (node.idom != current.idom)
          current.idom.frontier.add(node);
      }
    
    }

    /* find dominator for canonical exit */
    for (Iterator i = cfg.exitBBs(); i.hasNext(); ) {
      BasicBlock exitBB = (BasicBlock)i.next();
      DominatorNode current = nodes[cfg.indexOf(exitBB)];
      if (exit_idom == null)
        exit_idom = current;
      else
        loop: for (;;) {
          for (DominatorNode dom = current; dom != null; dom = dom.idom)
            if (dom == exit_idom)
              break loop;
          exit_idom = exit_idom.idom;
        }
    }

    /* prologue */
    for (int i = 0; i < nodes.length; i++) {
      DominatorNode current = nodes[i];
      if (current.idom == entry)
        current.idom = null;
      else
        super.addEdge(new DirectedEdge(current.idom, current));
    }
    if (exit_idom == entry)
      exit_idom = null;

  }

  private DominatorNode dfs1(ControlFlowGraph cfg, DominatorNode last, DominatorNode parent, DominatorNode current) {
    if (current.dfnum == 0) {
      current.dfnum = last.dfnum+1;
      current.parent = parent;
      current.previous = last;
      last.next = current;
      last = current;
      parent = current;
      for (Iterator i = cfg.getBB(parent.index).succBBs(); i.hasNext(); ) {
        BasicBlock succBB = (BasicBlock)i.next();
        current = nodes[cfg.indexOf(succBB)];
        last = dfs1(cfg, last, parent, current);
      }
    }
    return last;
  }

  public void addNode(DirectedNode node) {
    throw new UnsupportedOperationException();
  }

  public void removeNode(DirectedNode node) {
    throw new UnsupportedOperationException();
  }

  public void addEdge(DirectedEdge edge) {
    throw new UnsupportedOperationException();
  }

  public void removeEdge(DirectedEdge edge) {
    throw new UnsupportedOperationException();
  }

  public int indexOf(DominatorNode node) {
    if (this != node.ownerGraph())
      return -1;
    return node.index;
  }

  public DominatorNode getNode(int index) {
    return nodes[index];
  }

  public Iterator nodes() {
    return new Iterator() {
      private int i;
      public boolean hasNext() {
        return i < nodes.length;
      }
      public Object next() {
        if (i >= nodes.length)
          throw new NoSuchElementException();
        return nodes[i++];
      }
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("digraph DominatorTree {");
    sb.append("entry [label=\"Entry\",shape=plaintext];");
    for (Iterator i = nodes(); i.hasNext(); ) {
      DominatorNode node = (DominatorNode)i.next();
      if (node.idom == null)
        sb.append("entry -> "+node.hashCode()+" [style=dashed];");
      sb.append(node);
    }
    for (Iterator i = edges(); i.hasNext(); )
      sb.append(i.next());
    sb.append("exit [label=\"Exit\",shape=plaintext];");
    if (exit_idom == null)
      sb.append("entry -> exit [style=dashed];");
    else
      sb.append(exit_idom.hashCode()+" -> exit [style=dashed];");
    sb.append("}");
    return sb.toString();
  }

  public static final class DominatorNode extends DirectedNode {

    private final int index;

    private int dfnum;
    private DominatorNode parent;
    private DominatorNode previous;
    private DominatorNode next;

    private DominatorNode idom;
    private DominatorNode sdom = this;
    private DominatorNode bucket;

    private final HashSet frontier;

    private BitSet dominatorSet;

    private DominatorNode() {
      index = -1;
      frontier = null;
    }

    private DominatorNode(int index) {
      this.index = index;
      frontier = new HashSet(5);
    }

    private BitSet dominatorSet() {
      if (dominatorSet == null) {
        DominatorTree owner = (DominatorTree)ownerGraph();
        dominatorSet = new BitSet(owner.nodes.length);
        for (Iterator i = dominators(); i.hasNext(); ) {
          DominatorNode node = (DominatorNode)i.next();
          dominatorSet.set(node.index);
        }
      }
      return dominatorSet;
    }

    public boolean dominates(DominatorNode dominatee) {
      DominatorTree owner = (DominatorTree)ownerGraph();
      if (owner != dominatee.ownerGraph())
        return false;
      return dominatee.dominatorSet().get(index);
    }

    public Iterator dominators() {
      return new Iterator() {
        private DominatorNode next = DominatorNode.this;
        public boolean hasNext() {
          return next != null;
        }
        public Object next() {
          if (next == null)
            throw new NoSuchElementException();
          DominatorNode current = next;
          next = next.idom;
          return current;
        }
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }

    public Iterator idominatees() {
      return new Iterator() {
        private final Iterator i = outEdges();
        public boolean hasNext() {
          return i.hasNext();
        }
        public Object next() {
          DirectedEdge edge = (DirectedEdge)i.next();
          return (DominatorNode)edge.targetNode();
        }
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }

    public Iterator frontier() {
      return new Iterator() {
        private Iterator i = frontier.iterator();
        public boolean hasNext() { return i.hasNext(); }
        public Object next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }

    public String toString() {
      return hashCode()+" [label=\"BB"+index+"\",shape=plaintext];";
    }

  }

}

