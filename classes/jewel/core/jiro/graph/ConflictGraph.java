/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.graph;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ConflictGraph extends UndirectedGraph {

  private ConflictNode[] nodes;

  public ConflictGraph() {
    this(16);
  }

  public ConflictGraph(int size) {
    if (size < 0)
      throw new IllegalArgumentException();
    nodes = new ConflictNode[size];
  }

  public Iterator nodes() {
    return new Iterator() {
      private int index = skip(0);
      private ConflictNode last;
      public boolean hasNext() {
        return index < nodes.length;
      }
      public Object next() {
        if (index >= nodes.length)
          throw new NoSuchElementException();
        last = nodes[index++];
        index = skip(index);
        return last;
      }
      public void remove() {
        if (last == null)
          throw new IllegalStateException();
        removeNode(last);
        last = null;
      }
      private int skip(int index) {
        while (index < nodes.length) {
          if (nodes[index] != null)
            break;
          index++;
        }
        return index;
      }
    };
  }

  public void addNode(UndirectedNode node) {
    addNode((ConflictNode)node);
  }

  public void removeNode(UndirectedNode node) {
    removeNode((ConflictNode)node);
  }

  public void addEdge(UndirectedEdge edge) {
    if (edge instanceof CoalesceEdge)
      addEdge((CoalesceEdge)edge);
    else
      addEdge((ConflictEdge)edge);
  }

  public void removeEdge(UndirectedEdge edge) {
    if (edge instanceof CoalesceEdge)
      removeEdge((CoalesceEdge)edge);
    else
      removeEdge((ConflictEdge)edge);
  }

  public boolean hasConflict(int reg0, int reg1) {
    ConflictNode node0 = getNode(reg0);
    ConflictNode node1 = getNode(reg1);
    if (node0 == null || node1 == null)
      throw new IllegalArgumentException();
    return node0.conflictsWith(node1);
  }

  public ConflictNode getNode(int register) {
    if (register < 0)
      throw new IllegalArgumentException();
    if (register < nodes.length)
      return nodes[register];
    return null;
  }

  public void addNode(ConflictNode node) {
    if (node.register < nodes.length && nodes[node.register] != null)
      throw new IllegalArgumentException();
    super.addNode(node);
    if (node.register >= nodes.length) {
      ConflictNode[] tmp = nodes;
      nodes = new ConflictNode[node.register+1];
      System.arraycopy(tmp, 0, nodes, 0, tmp.length);
    }
    nodes[node.register] = node;
  }

  public void removeNode(ConflictNode node) {
    super.removeNode(node);
    nodes[node.register] = null;
  }

  public void addEdge(ConflictEdge edge) {
    super.addEdge(edge);
    ConflictNode source = (ConflictNode)edge.sourceNode();
    ConflictNode target = (ConflictNode)edge.targetNode();
    source.bitset.set(target.register);
    target.bitset.set(source.register);
  }

  public void removeEdge(ConflictEdge edge) {
    super.removeEdge(edge);
    ConflictNode source = (ConflictNode)edge.sourceNode();
    ConflictNode target = (ConflictNode)edge.targetNode();
    source.bitset.clear(target.register);
    target.bitset.clear(source.register);
  }

  public void addEdge(CoalesceEdge edge) {
    super.addEdge(edge);
  }

  public void removeEdge(CoalesceEdge edge) {
    super.removeEdge(edge);
  }

  public static class ConflictNode extends UndirectedNode {

    private final int register;
    private final BitSet bitset = new BitSet();

    public ConflictNode(int register) {
      if (register < 0)
        throw new IllegalArgumentException();
      this.register = register;
    }

    public int register() {
      return register;
    }

    public boolean conflictsWith(ConflictNode node) {
      return bitset.get(node.register);
    }

    public Iterator conflictNodes() {
      final HashSet set = new HashSet(degree());
      for (Iterator i = edges(); i.hasNext(); ) {
        UndirectedEdge edge = (UndirectedEdge)i.next();
        if (edge instanceof ConflictEdge)
          set.add(edge.otherEnd(this));
      }
      return new Iterator() {
        private final Iterator i = set.iterator();
        public boolean hasNext() { return i.hasNext(); }
        public Object next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }

    public Iterator coalesceNodes() {
      final HashSet set = new HashSet(degree());
      for (Iterator i = edges(); i.hasNext(); ) {
        UndirectedEdge edge = (UndirectedEdge)i.next();
        if (edge instanceof CoalesceEdge)
          set.add(edge.otherEnd(this));
      }
      return new Iterator() {
        private final Iterator i = set.iterator();
        public boolean hasNext() { return i.hasNext(); }
        public Object next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }

    public String toString() {
      String s = Integer.toString(register);
      while (s.length() < 3) s = "0"+s;
      return hashCode()+" [label=\"r"+s+"\",shape=circle];";
    }

  }

  public static class ConflictEdge extends UndirectedEdge {

    public ConflictEdge(ConflictNode sourceNode, ConflictNode targetNode) {
      super(sourceNode, targetNode);
    }

    public boolean equals(Object object) {
      return object instanceof ConflictEdge
          && super.equals(object);
    }

  }

  public static class CoalesceEdge extends UndirectedEdge {

    public CoalesceEdge(ConflictNode sourceNode, ConflictNode targetNode) {
      super(sourceNode, targetNode);
    }

    public boolean equals(Object object) {
      return object instanceof CoalesceEdge
          && super.equals(object);
    }

    public String toString() {
      return sourceNode().hashCode()+" -- "+targetNode().hashCode()+" [style=dotted];";
    }

  }

}

