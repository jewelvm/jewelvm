/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.graph;

import java.util.HashSet;
import java.util.Iterator;

public abstract class UndirectedGraph extends Graph {

  protected UndirectedGraph() { }

  public final boolean contains(Edge edge) {
    return edge instanceof UndirectedEdge
        && ((UndirectedNode)edge.sourceNode()).edges.contains(edge);
  }
  
  public final void addNode(Node node) {
    addNode((UndirectedNode)node);
  }

  public final void removeNode(Node node) {
    removeNode((UndirectedNode)node);
  }

  public final void addEdge(Edge edge) {
    addEdge((UndirectedEdge)edge);
  }

  public final void removeEdge(Edge edge) {
    removeEdge((UndirectedEdge)edge);
  }

  public void addNode(UndirectedNode node) {
    super.addNode(node);
  }

  public void removeNode(UndirectedNode node) {
    super.removeNode(node);
  }

  public void addEdge(UndirectedEdge edge) {
    super.addEdge(edge);
    ((UndirectedNode)edge.sourceNode()).edges.add(edge);
    ((UndirectedNode)edge.targetNode()).edges.add(edge);
  }

  public void removeEdge(UndirectedEdge edge) {
    super.removeEdge(edge);
    ((UndirectedNode)edge.sourceNode()).edges.remove(edge);
    ((UndirectedNode)edge.targetNode()).edges.remove(edge);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("graph UndirectedGraph {");
    for (Iterator i = nodes(); i.hasNext(); )
      sb.append(i.next());
    for (Iterator i = edges(); i.hasNext(); )
      sb.append(i.next());
    sb.append("}");
    return sb.toString();
  }

  public static class UndirectedNode extends Node {

    private HashSet edges;

    public UndirectedNode() {
      this(5);
    }

    public UndirectedNode(int degree) {
      edges = new HashSet(degree);
    }

    public final int degree() {
      return edges.size();
    }

    public final Iterator edges() {
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
          i.remove();
          ownerGraph().removeEdge(last);
        }
      };
    }

    public final int inDegree() {
      return degree();
    }

    public final Iterator inEdges() {
      return edges();
    }

    public final int outDegree() {
      return degree();
    }

    public final Iterator outEdges() {
      return edges();
    }

    protected Object clone() throws CloneNotSupportedException {
      UndirectedNode node = (UndirectedNode)super.clone();
      node.edges = new HashSet(degree());
      return node;
    }

  }

  public static class UndirectedEdge extends Edge {

    public UndirectedEdge(UndirectedNode source, UndirectedNode target) {
      super(source, target);
    }

    public int hashCode() {
      return sourceNode().hashCode() ^ targetNode().hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof UndirectedEdge
          && ((((UndirectedEdge)object).sourceNode() == sourceNode() && ((UndirectedEdge)object).targetNode() == targetNode())
          ||  (((UndirectedEdge)object).sourceNode() == targetNode() && ((UndirectedEdge)object).targetNode() == sourceNode()));
    }

    public String toString() {
      return sourceNode().hashCode()+" -- "+targetNode().hashCode()+";";
    }

  }

}

