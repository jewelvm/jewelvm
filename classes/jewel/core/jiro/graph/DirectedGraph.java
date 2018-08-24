/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.graph;

import java.util.HashSet;
import java.util.Iterator;

public abstract class DirectedGraph extends Graph {

  public DirectedGraph() { }
  
  public final boolean contains(Edge edge) {
    return edge instanceof DirectedEdge
        && ((DirectedNode)edge.sourceNode()).outEdges.contains(edge);
  }
  
  public final void addNode(Node node) {
    addNode((DirectedNode)node);
  }

  public final void removeNode(Node node) {
    removeNode((DirectedNode)node);
  }

  public final void addEdge(Edge edge) {
    addEdge((DirectedEdge)edge);
  }

  public final void removeEdge(Edge edge) {
    removeEdge((DirectedEdge)edge);
  }
  
  public void addNode(DirectedNode node) {
    super.addNode(node);
  }

  public void removeNode(DirectedNode node) {
    super.removeNode(node);
  }

  public void addEdge(DirectedEdge edge) {
    super.addEdge(edge);
    ((DirectedNode)edge.sourceNode()).outEdges.add(edge);
    ((DirectedNode)edge.targetNode()).inEdges.add(edge);
  }

  public void removeEdge(DirectedEdge edge) {
    super.removeEdge(edge);
    ((DirectedNode)edge.sourceNode()).outEdges.remove(edge);
    ((DirectedNode)edge.targetNode()).inEdges.remove(edge);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("digraph DirectedGraph {");
    for (Iterator i = nodes(); i.hasNext(); )
      sb.append(i.next());
    for (Iterator i = edges(); i.hasNext(); )
      sb.append(i.next());
    sb.append("}");
    return sb.toString();
  }

  public static class DirectedNode extends Node {

    private HashSet inEdges;
    private HashSet outEdges;

    public DirectedNode() {
      this(5, 5);
    }

    public DirectedNode(int inDegree, int outDegree) {
      inEdges = new HashSet(inDegree);
      outEdges = new HashSet(outDegree);
    }

    public final int degree() {
      return inDegree()+outDegree();
    }

    public final Iterator edges() {
      final HashSet edges = new HashSet();
      for (Iterator j = inEdges(); j.hasNext(); )
        edges.add(j.next());
      for (Iterator j = outEdges(); j.hasNext(); )
        edges.add(j.next());
      return new Iterator() {
        private final Iterator i = edges.iterator();
        private Edge last;
        public boolean hasNext() {
          return i.hasNext();
        }
        public Object next() {
          return last = (Edge)i.next();
        }
        public void remove() {
          if (last == null)
            throw new IllegalStateException();
          ownerGraph().removeEdge(last);
          last = null;
        }
      };
    }

    public final int inDegree() {
      return inEdges.size();
    }

    public final Iterator inEdges() {
      return new Iterator() {
        private final Iterator i = inEdges.iterator();
        private Edge last;
        public boolean hasNext() {
          return i.hasNext();
        }
        public Object next() {
          return last = (Edge)i.next();
        }
        public void remove() {
          i.remove();
          ownerGraph().removeEdge(last);
        }
      };
    }

    public final int outDegree() {
      return outEdges.size();
    }

    public final Iterator outEdges() {
      return new Iterator() {
        private final Iterator i = outEdges.iterator();
        private Edge last;
        public boolean hasNext() {
          return i.hasNext();
        }
        public Object next() {
          return last = (Edge)i.next();
        }
        public void remove() {
          i.remove();
          ownerGraph().removeEdge(last);
        }
      };
    }

    protected Object clone() throws CloneNotSupportedException {
      DirectedNode node = (DirectedNode)super.clone();
      node.inEdges = new HashSet(inDegree());
      node.outEdges = new HashSet(outDegree());
      return node;
    }

  }

  public static class DirectedEdge extends Edge {

    public DirectedEdge(DirectedNode source, DirectedNode target) {
      super(source, target);
    }

    public int hashCode() {
      return (sourceNode().hashCode() ^ 0x55555555) ^ (targetNode().hashCode() ^ 0xAAAAAAAA);
    }

    public boolean equals(Object object) {
      return object instanceof DirectedEdge
          && ((DirectedEdge)object).sourceNode() == sourceNode()
          && ((DirectedEdge)object).targetNode() == targetNode();
    }

    public String toString() {
      return sourceNode().hashCode()+" -> "+targetNode().hashCode()+";";
    }

  }

}

