/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.control;

import jewel.core.jiro.graph.Graph;
import jewel.core.jiro.graph.DirectedGraph;

import java.util.HashSet;
import java.util.Iterator;

public final class ControlTree extends DirectedGraph {

  private final HashSet nodes = new HashSet();

  public ControlTree(ControlFlowGraph cfg) {

    BlockRegion[] blocks = new BlockRegion[cfg.count()];
    int index = 0;
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); index++) {
      BasicBlock currentBB = (BasicBlock)i.next();
      BlockRegion region = new BlockRegion(currentBB);
      blocks[index] = region;
      addNode(region);
    }
    for (Iterator i = cfg.edges(); i.hasNext(); ) {
      ControlEdge edge = (ControlEdge)i.next();
      addEdge(new RegionEdge(blocks[cfg.indexOf(edge.sourceBB())], blocks[cfg.indexOf(edge.targetBB())]));
    }

    while (nodes.size() > 1) {

      boolean changed = false;

outer:
      for (Iterator i = nodes.iterator(); i.hasNext(); ) {
        Region pred = (Region)i.next();

        for (Iterator j = pred.outEdges(); j.hasNext(); ) {
          RegionEdge edge = (RegionEdge)j.next();
          Region succ = edge.targetRegion();
          // t1
          if (pred == succ) {
            Region region = new T1Region(pred, edge);
            addNode(region);
            removeEdge(edge);
            for (Iterator k = pred.inEdges(); k.hasNext(); ) {
              RegionEdge e = (RegionEdge)k.next();
              addEdge(e.clone(e.sourceRegion(), region));
            }
            for (Iterator k = pred.outEdges(); k.hasNext(); ) {
              RegionEdge e = (RegionEdge)k.next();
              addEdge(e.clone(region, e.targetRegion()));
            }
            removeNode(pred);
            changed = true;
            break outer;
          }
          // t2
          if (succ.inDegree() == 1) {
            Region region = new T2Region(pred, edge, succ);
            addNode(region);
            removeEdge(edge);
            for (Iterator k = pred.inEdges(); k.hasNext(); ) {
              RegionEdge e = (RegionEdge)k.next();
              addEdge(e.clone(e.sourceRegion(), region));
            }
            for (Iterator k = pred.outEdges(); k.hasNext(); ) {
              RegionEdge e = (RegionEdge)k.next();
              addEdge(e.clone(region, e.targetRegion()));
            }
            for (Iterator k = succ.inEdges(); k.hasNext(); ) {
              RegionEdge e = (RegionEdge)k.next();
              addEdge(e.clone(e.sourceRegion(), region));
            }
            for (Iterator k = succ.outEdges(); k.hasNext(); ) {
              RegionEdge e = (RegionEdge)k.next();
              addEdge(e.clone(region, e.targetRegion()));
            }
            removeNode(pred);
            removeNode(succ);
            changed = true;
            break outer;
          }
        }

      }

      if (!changed) {
        Graph.show(this);
        break;
      }

    }

  }

  private void addNode(Region region) {
    super.addNode(region);
    nodes.add(region);
  }

  private void removeNode(Region region) {
    super.removeNode(region);
    nodes.remove(region);
  }

  private void addEdge(RegionEdge edge) {
    super.addEdge(edge);
  }

  private void removeEdge(RegionEdge edge) {
    super.removeEdge(edge);
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

  public Iterator nodes() {
    return nodes.iterator();
  }

  public static abstract class Region extends DirectedNode { }

  public static final class T1Region extends Region {

    private final Region node;
    private final RegionEdge edge;

    public T1Region(Region node, RegionEdge edge) {
      this.node = node;
      this.edge = edge;
    }

    public String toString() {
      return hashCode()+" [label=\"T1\"];";
    }

  }

  public static final class T2Region extends Region {

    private final Region pred;
    private final RegionEdge edge;
    private final Region succ;

    public T2Region(Region pred, RegionEdge edge, Region succ) {
      this.pred = pred;
      this.edge = edge;
      this.succ = succ;
    }

    public String toString() {
      return hashCode()+" [label=\"T2\"];";
    }

  }

  public static final class BlockRegion extends Region {

    private final BasicBlock block;

    public BlockRegion(BasicBlock block) {
      this.block = block;
    }

    public String toString() {
      return hashCode()+" [label=\"BB\"];";
    }

  }

  public static final class RegionEdge extends DirectedEdge implements Cloneable {

    public RegionEdge(Region source, Region target) {
      super(source, target);
    }

    public Region sourceRegion() {
      return (Region)sourceNode();
    }

    public Region targetRegion() {
      return (Region)targetNode();
    }

    public RegionEdge clone(Region source, Region target) {
      try {
        return (RegionEdge)super.clone(source, target);
      } catch (CloneNotSupportedException e) {
        throw new Error();
      }
    }

    public boolean equals(Object object) {
      return object instanceof RegionEdge
          && super.equals(object);
    }

  }

}

