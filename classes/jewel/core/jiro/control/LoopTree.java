/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.control;

import jewel.core.jiro.graph.DirectedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public final class LoopTree extends DirectedGraph {

  private final LoopNode[] nodes;
  private final Loop[] loops;

  public LoopTree(ControlFlowGraph cfg) {

    /* epilogue */
    nodes = new LoopNode[cfg.count()];
    int index = 0;
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); index++) {
      BasicBlock currentBB = (BasicBlock)i.next();
      LoopNode node = new LoopBlock(index);
      nodes[index] = node;
      super.addNode(node);
    }

    /* find back edges */
    HashSet backedges = new HashSet();
    byte[] state = new byte[cfg.count()];
    for (Iterator i = cfg.entryBBs(); i.hasNext(); ) {
      BasicBlock entryBB = (BasicBlock)i.next();
      dfs1(cfg, entryBB, state, backedges);
    }

    /* process each back edge */
    ArrayList all_loops = new ArrayList();
    ArrayList all_edges = new ArrayList();
    for (Iterator i = backedges.iterator(); i.hasNext(); ) {

      /* find common dominator */
      ControlEdge edge = (ControlEdge)i.next();
      BasicBlock sourceBB = edge.sourceBB();
      BasicBlock targetBB = edge.targetBB();
      BasicBlock commonBB = null;
      for (Iterator j = targetBB.dominatorBBs(); j.hasNext(); ) {
        BasicBlock dominatorBB = (BasicBlock)j.next();
        if (dominatorBB.dominates(sourceBB)) {
          commonBB = dominatorBB;
          break;
        }
      }

      /* find all loop nodes up to common dominator */
      HashSet loop = new HashSet();
      if (commonBB != null)
        loop.add(commonBB);
      HashSet list = new HashSet();
      if (!loop.contains(sourceBB))
        list.add(sourceBB);
      while (!list.isEmpty()) {
        Iterator extractor = list.iterator();
        BasicBlock loopBB = (BasicBlock)extractor.next();
        extractor.remove();
        loop.add(loopBB);
        for (Iterator j = loopBB.predBBs(); j.hasNext(); ) {
          BasicBlock predBB = (BasicBlock)j.next();
          if (!loop.contains(predBB))
            list.add(predBB);
        }
      }

      /* remove extra entry nodes */
      if (commonBB != targetBB) {
        HashSet visited = new HashSet();
        dfs2(targetBB, visited, loop);
        for (Iterator j = loop.iterator(); j.hasNext(); ) {
          BasicBlock loopBB = (BasicBlock)j.next();
          if (!visited.contains(loopBB))
            j.remove();
        }
      }

      /* add the new loop */
      all_loops.add(loop);
      HashSet edges = new HashSet();
      edges.add(edge);
      all_edges.add(edges);

    }

    /* merge loops */
    boolean changed = true;
    while (changed) {
      changed = false;
      for (Iterator i = all_loops.iterator(), m = all_edges.iterator(); i.hasNext(); ) {
        HashSet loop1 = (HashSet)i.next();
        HashSet edges1 = (HashSet)m.next();
        for (Iterator j = all_loops.iterator(), n = all_edges.iterator(); j.hasNext(); ) {
          HashSet loop2 = (HashSet)j.next();
          HashSet edges2 = (HashSet)n.next();
          if (loop1 != loop2) {
            HashSet common = new HashSet();
            common.addAll(loop1);
            common.retainAll(loop2);
            if (loop1.equals(loop2) || (!common.isEmpty() && !loop1.containsAll(loop2) && !loop2.containsAll(loop1))) {
              loop1.addAll(loop2);
              edges1.addAll(edges2);
              j.remove();
              n.remove();
              changed = true;
              break;
            }
          }
        }
        if (changed)
          break;
      }
    }

    /* create loop nodes */
    loops = new Loop[all_loops.size()];
    index = 0;
    for (Iterator i = all_loops.iterator(), m = all_edges.iterator(); i.hasNext(); index++) {
      HashSet allBBs = (HashSet)i.next();
      HashSet allBEs = (HashSet)m.next();
      Loop loop = new Loop(cfg, nodes, allBBs, allBEs);
      loops[index] = loop;
      super.addNode(loop);
    }

    /* nest loops */
    for (int i = 0; i < loops.length; i++)
      for (int j = 0; j < loops.length; j++)
        if (i != j) {
          Loop loop1 = loops[i];
          Loop loop2 = loops[j];
          if (loop2.nodes.containsAll(loop1.nodes))
            if (loop1.iloop == null)
              loop1.iloop = loop2;
            else if (loop1.iloop.nodes.size() > loop2.nodes.size())
              loop1.iloop = loop2;
        }
    for (int i = 0; i < loops.length; i++) {
      Loop loop = loops[i];
      Loop iloop = loop.iloop;
      if (iloop != null)
        iloop.loops.add(loop);
    }

  }

  private void dfs1(ControlFlowGraph cfg, BasicBlock currentBB, byte[] state, HashSet edges) {
    int index = cfg.indexOf(currentBB);
    if (state[index] == 0) {
      state[index] = 1;
      for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
        ControlEdge edge = (ControlEdge)i.next();
        BasicBlock targetBB = edge.targetBB();
        int tindex = cfg.indexOf(targetBB);
        if (state[tindex] == 1)
          edges.add(edge);
        else
          dfs1(cfg, targetBB, state, edges);
      }
      state[index] = 2;
    }
  }

  private void dfs2(BasicBlock currentBB, HashSet visited, HashSet loop) {
    if (!visited.contains(currentBB)) {
      visited.add(currentBB);
      for (Iterator i = currentBB.succBBs(); i.hasNext(); ) {
        BasicBlock succBB = (BasicBlock)i.next();
        if (loop.contains(succBB))
          dfs2(succBB, visited, loop);
      }
    }
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

  public int indexOf(LoopNode node) {
    if (this != node.ownerGraph())
      return -1;
    if (node instanceof LoopBlock)
      return ((LoopBlock)node).index;
    return -1;
  }

  public LoopNode getNode(int index) {
    return nodes[index];
  }

  public Iterator nodes() {
    HashSet set = new HashSet();
    for (int i = 0; i < nodes.length; i++)
      set.add(nodes[i]);
    for (int i = 0; i < loops.length; i++)
      set.add(loops[i]);
    return set.iterator();
  }

  public int loopCount() {
    return loops.length;
  }

  public Iterator allLoops() {
    HashSet set = new HashSet();
    for (int i = 0; i < loops.length; i++)
      set.add(loops[i]);
    return set.iterator();
  }

  public Iterator loops() {
    HashSet set = new HashSet();
    for (int i = 0; i < loops.length; i++)
      if (loops[i].iloop == null)
        set.add(loops[i]);
    return set.iterator();
  }

/*  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("digraph LoopTree {");
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
  }*/

  public static abstract class LoopNode extends DirectedNode {

    protected Loop iloop;

    protected LoopNode() { }

    public final Loop iloop() {
      return iloop;
    }

    public final int depth() {
      int depth = 0;
      for (Loop loop = iloop(); loop != null; loop = loop.iloop())
        depth++;
      return depth;
    }

  }

  private static final class LoopBlock extends LoopNode {

    private final int index;

    private LoopBlock(int index) {
      this.index = index;
    }

    public String toString() {
      return hashCode()+" [label=\"BB"+index+"\",shape=box];";
    }

  }

  public static final class Loop extends LoopNode {

    private final HashSet loops = new HashSet();
    private final HashSet nodes = new HashSet();
    private final HashSet entry = new HashSet();
    private final HashSet exit = new HashSet();
    private final HashSet backedges = new HashSet();
    private LoopNode preheader;

    private Loop(ControlFlowGraph cfg, LoopNode[] nodes, HashSet allBBs, HashSet allBEs) {
      for (Iterator i = allBBs.iterator(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        LoopNode node = nodes[cfg.indexOf(currentBB)];
        this.nodes.add(node);
        node.iloop = this;
        for (Iterator j = cfg.entryBBs(); j.hasNext(); ) {
          BasicBlock entryBB = (BasicBlock)j.next();
          if (currentBB == entryBB) {
            entry.add(node);
            break;
          }
        }
        for (Iterator j = currentBB.predBBs(); j.hasNext(); ) {
          BasicBlock predBB = (BasicBlock)j.next();
          if (!allBBs.contains(predBB)) {
            entry.add(node);
            break;
          }
        }
        for (Iterator j = currentBB.succBBs(); j.hasNext(); ) {
          BasicBlock succBB = (BasicBlock)j.next();
          if (!allBBs.contains(succBB)) {
            exit.add(node);
            break;
          }
        }
      }
      for (Iterator i = allBEs.iterator(); i.hasNext(); ) {
        ControlEdge edge = (ControlEdge)i.next();
        LoopNode source = nodes[cfg.indexOf(edge.sourceBB())];
        LoopNode target = nodes[cfg.indexOf(edge.targetBB())];
        backedges.add(new DirectedEdge(source, target));
      }
      if (entry.size() == 1) {
        LoopBlock node = (LoopBlock)entry.iterator().next();
        BasicBlock entryBB = cfg.getBB(node.index);
        int preheaders = 0;
        BasicBlock preheaderBB = null;
        for (Iterator i = entryBB.predBBs(); i.hasNext(); ) {
          BasicBlock predBB = (BasicBlock)i.next();
          if (!allBBs.contains(predBB)) {
            preheaderBB = predBB;
            preheaders++;
          }
        }
        if (preheaders == 1)
          if (preheaderBB.outDegree() == 1)
            preheader = nodes[cfg.indexOf(preheaderBB)];
      }
    }

    public boolean contains(LoopNode node) {
      return nodes.contains(node);
    }

    public int loopCount() {
      return loops.size();
    }

    public Iterator loops() {
      return loops.iterator();
    }

    public int nodeCount() {
      return nodes.size();
    }

    public Iterator nodes() {
      return nodes.iterator();
    }

    public Iterator entryNodes() {
      return entry.iterator();
    }

    public Iterator exitNodes() {
      return exit.iterator();
    }

    public Iterator backedges() {
      return backedges.iterator();
    }

    public LoopNode preheader() {
      return preheader;
    }

    public String toString() {
      return hashCode()+" [label=\"Loop\"];";
    }

  }

}

