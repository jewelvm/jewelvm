/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.graph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;

public abstract class Graph {

  public static boolean show(Graph graph) {
    File dotFile = null;
    File psFile = null;
    try {
      dotFile = File.createTempFile("jewel", ".dot");
      psFile = File.createTempFile("jewel", ".ps");
      PrintStream out = new PrintStream(new FileOutputStream(dotFile));
      out.println(graph);
      out.close();
      Process dot = Runtime.getRuntime().exec("dot -Tps -o "+psFile+" "+dotFile);
      dot.waitFor();
      Process gv = Runtime.getRuntime().exec("gv "+psFile);
      gv.waitFor();
      return true;
    } catch (Exception e) {
      return false;
    } finally {
      if (dotFile != null)
        dotFile.delete();
      if (psFile != null)
        psFile.delete();
    }
  }

  public static boolean show(Graph[] graph) {
    File[] dotFile = new File[graph.length];
    File[] psFile = new File[graph.length];
    try {
      for (int i = 0; i < dotFile.length; i++)
        dotFile[i] = File.createTempFile("jewel", ".dot");
      for (int i = 0; i < psFile.length; i++)
        psFile[i] = File.createTempFile("jewel", ".ps");
      Process[] gv = new Process[graph.length];
      for (int i = 0; i < graph.length; i++) {
        PrintStream out = new PrintStream(new FileOutputStream(dotFile[i]));
        out.println(graph[i]);
        out.close();
        Process dot = Runtime.getRuntime().exec("dot -Tps -o "+psFile[i]+" "+dotFile[i]);
        dot.waitFor();
        gv[i] = Runtime.getRuntime().exec("gv "+psFile[i]);
      }
      for (int i = 0; i < gv.length; i++)
        gv[i].waitFor();
      return true;
    } catch (Exception e) {
      return false;
    } finally {
      for (int i = 0; i < dotFile.length; i++)
        if (dotFile[i] != null)
          dotFile[i].delete();
      for (int i = 0; i < psFile.length; i++)
        if (psFile[i] != null)
          psFile[i].delete();
    }
  }

  Graph() { }

  public final void show() {
    show(this);
  }

  public abstract Iterator nodes();
  
  public Iterator edges() {
    final HashSet edges = new HashSet();
    for (Iterator i = nodes(); i.hasNext(); ) {
      Node node = (Node)i.next();
      for (Iterator j = node.edges(); j.hasNext(); )
        edges.add(j.next());
    }
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
        removeEdge(last);
        last = null;
      }
    };
  }

  public final boolean contains(Node node) {
    return node.ownerGraph == this;
  }
  
  public abstract boolean contains(Edge node);
  
  public void addNode(Node node) {
    if (node.ownerGraph != null)
      throw new IllegalArgumentException("Node associated to another graph");
    node.ownerGraph = this;
  }

  public void removeNode(Node node) {
    if (node.ownerGraph != this)
      throw new IllegalArgumentException("Node is not associated to this graph");
    for (Iterator i = node.edges(); i.hasNext(); ) {
      i.next();
      i.remove();
    }
    node.ownerGraph = null;
  }

  public void addEdge(Edge edge) {
    if (edge.source.ownerGraph != this || edge.target.ownerGraph != this)
      throw new IllegalArgumentException("Edge source and target nodes should be associated to this graph");
  }

  public void removeEdge(Edge edge) {
    if (edge.source.ownerGraph != this || edge.target.ownerGraph != this)
      throw new IllegalArgumentException("Edge source and target nodes should be associated to this graph");
  }

  public static abstract class Node {

    private Graph ownerGraph;

    Node() { }

    public final Graph ownerGraph() {
      return ownerGraph;
    }

    public abstract int degree();
    public abstract Iterator edges();
    public abstract int inDegree();
    public abstract Iterator inEdges();
    public abstract int outDegree();
    public abstract Iterator outEdges();

    protected Object clone() throws CloneNotSupportedException {
      Node node = (Node)super.clone();
      node.ownerGraph = null;
      return node;
    }

    public final int hashCode() {
      return super.hashCode();
    }

    public final boolean equals(Object object) {
      return super.equals(object);
    }

    public String toString() {
      return hashCode()+" [label=\"\"];";
    }

  }

  public static abstract class Edge {

    private Node source;
    private Node target;

    Edge(Node source, Node target) {
      if (source == null || target == null)
        throw new NullPointerException();
      this.source = source;
      this.target = target;
    }

    public final Node sourceNode() {
      return source;
    }

    public final Node targetNode() {
      return target;
    }

    public final Node otherEnd(Node node) {
      if (node == source)
        return target;
      else if (node == target)
        return source;
      else
        throw new IllegalArgumentException("Parameter node must be an edge end");
    }

    protected Edge clone(Node source, Node target) throws CloneNotSupportedException {
      if (source == null || target == null)
        throw new NullPointerException();
      Edge edge = (Edge)clone();
      edge.source = source;
      edge.target = target;
      return edge;
    }

  }

}

