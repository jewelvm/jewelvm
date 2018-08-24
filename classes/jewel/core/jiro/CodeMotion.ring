/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.graph.DirectedGraph;
import jewel.core.jiro.graph.DirectedGraph.DirectedEdge;
import jewel.core.jiro.graph.DirectedGraph.DirectedNode;
import jewel.core.jiro.optimize.Optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public final class CodeMotion implements Optimization {

  public CodeMotion() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (cfg.isSSA()) {

      DGraph graph = new DGraph(cfg.stmtCount());
      for (Iterator i = cfg.entryBBs(); i.hasNext(); ) {
        BasicBlock entryBB = (BasicBlock)i.next();
        build(graph, entryBB);
      }

      for (Iterator i = graph.nodes(); i.hasNext();) {
        DNode node = (DNode)i.next();
        changed |= node.findBestBB();
      }

      if (changed) {
        //System.err.println("*CODE MOTION*");
        ArrayList actions = new ArrayList();
        for (Iterator i = cfg.entryBBs(); i.hasNext(); ) {
          BasicBlock entryBB = (BasicBlock)i.next();
          actions(graph, entryBB, actions);
        }
        for (Iterator i = actions.iterator(); i.hasNext(); ) {
          Runnable action = (Runnable)i.next();
          action.run();
        }
        //try { System.in.read(); } catch (Exception e) { }
      }

    }

    return changed;
  }

  private static void build(DGraph graph, BasicBlock currentBB) {
    ControlFlowGraph cfg = currentBB.ownerCFG();
    for (Iterator i = currentBB.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      DNode node = graph.getNode(cfg.indexOf(stmt));
      Matcher matcher = new Matcher(stmt);
      matcher.build(graph, node);
      node.fixed = matcher.fixed();
      node.sensitive = matcher.sensitive();
      node.defBB = currentBB;
    }
    for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
      IRControlEdge edge = (IRControlEdge)i.next();
      BasicBlock targetBB = edge.targetBB();
      for (Iterator j = targetBB.topdownStmts(); j.hasNext(); ) {
        IRStatement stmt = (IRStatement)j.next();
        DNode node = graph.getNode(cfg.indexOf(stmt));
        Matcher matcher = new Matcher(stmt);
        if (!matcher.phistmt(graph, node, edge))
          break;
      }
    }
    for (Iterator i = currentBB.idominateeBBs(); i.hasNext(); ) {
      BasicBlock dominateeBB = (BasicBlock)i.next();
      build(graph, dominateeBB);
    }
  }

  private static void actions(DGraph graph, final BasicBlock currentBB, ArrayList actions) {
    ControlFlowGraph cfg = currentBB.ownerCFG();
    for (Iterator i = currentBB.topdownStmts(); i.hasNext(); ) {
      final IRStatement stmt = (IRStatement)i.next();
      DNode node = graph.getNode(cfg.indexOf(stmt));
      final BasicBlock defBB = node.defBB;
      if (currentBB != defBB)
        if (defBB.dominates(currentBB))
          actions.add(
            new Runnable() {
              public void run() {
                //System.err.println("Moving up (BB"+currentBB.ownerCFG().indexOf(currentBB)+" => BB"+defBB.ownerCFG().indexOf(defBB)+"): "+stmt);
                currentBB.removeStmt(stmt);
                IRStatement trailer = (IRStatement)defBB.trailer();
                if (trailer != null)
                  switch (trailer.snode().op()) {
                  case IR.CALLX: case IR.NCALLX:
                    IRStatement previous = (IRStatement)trailer.previous();
                    if (previous != null)
                      for (;;) {
                        switch (previous.snode().op()) {
                        case IR.IPASS: case IR.LPASS: case IR.FPASS: case IR.DPASS: case IR.APASS:
                          trailer = previous;
                          previous = (IRStatement)trailer.previous();
                          if (previous != null)
                            continue;
                        }
                        break;
                      }
                    break;
                  case IR.JUMP: case IR.AJUMP: case IR.IJUMP: case IR.ISWITCH:
                  case IR.INITX: case IR.NEWINSTANCEX: case IR.NEWARRAYX: case IR.LOCKX:
                    break;
                  default:
                    trailer = null;
                  }
                if (trailer == null)
                  defBB.appendStmt(stmt);
                else
                  defBB.insertStmtBeforeStmt(stmt, trailer);
              }
            }
          );
    }
    for (Iterator i = currentBB.idominateeBBs(); i.hasNext(); ) {
      BasicBlock dominateeBB = (BasicBlock)i.next();
      actions(graph, dominateeBB, actions);
    }
    for (Iterator i = currentBB.bottomupStmts(); i.hasNext(); ) {
      final IRStatement stmt = (IRStatement)i.next();
      DNode node = graph.getNode(cfg.indexOf(stmt));
      final BasicBlock defBB = node.defBB;
      if (currentBB != defBB)
        if (currentBB.dominates(defBB))
          actions.add(
            new Runnable() {
              public void run() {
                //System.err.println("Moving down (BB"+currentBB.ownerCFG().indexOf(currentBB)+" => BB"+defBB.ownerCFG().indexOf(defBB)+"): "+stmt);
                currentBB.removeStmt(stmt);
                IRStatement leader = (IRStatement)defBB.leader();
                if (leader != null)
                  for (;;) {
                    switch (leader.snode().op()) {
                    case IR.LABEL:
                    case IR.IPHI: case IR.LPHI: case IR.FPHI: case IR.DPHI: case IR.APHI:
                    case IR.IRESULT: case IR.LRESULT: case IR.FRESULT: case IR.DRESULT: case IR.ARESULT:
                    case IR.ACATCH:
                      leader = (IRStatement)leader.next();
                      if (leader != null)
                        continue;
                    }
                    break;
                  }
                if (leader == null)
                  defBB.appendStmt(stmt);
                else
                  defBB.insertStmtBeforeStmt(stmt, leader);
              }
            }
          );
    }
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void build(DGraph graph, DNode node)
      : IR.IRECEIVE = { node.setReg(@1.getReg()); }
      | IR.LRECEIVE = { node.setReg(@1.getReg()); }
      | IR.FRECEIVE = { node.setReg(@1.getReg()); }
      | IR.DRECEIVE = { node.setReg(@1.getReg()); }
      | IR.ARECEIVE = { node.setReg(@1.getReg()); }
      | IR.IRESULT = { node.setReg(@1.getReg()); }
      | IR.LRESULT = { node.setReg(@1.getReg()); }
      | IR.FRESULT = { node.setReg(@1.getReg()); }
      | IR.DRESULT = { node.setReg(@1.getReg()); }
      | IR.ARESULT = { node.setReg(@1.getReg()); }
      | IR.ACATCH = { node.setReg(@1.getReg()); }
      | IR.IDEFINE(build) = { @2(graph, node); node.setReg(@1.getReg()); }
      | IR.LDEFINE(build) = { @2(graph, node); node.setReg(@1.getReg()); }
      | IR.FDEFINE(build) = { @2(graph, node); node.setReg(@1.getReg()); }
      | IR.DDEFINE(build) = { @2(graph, node); node.setReg(@1.getReg()); }
      | IR.ADEFINE(build) = { @2(graph, node); node.setReg(@1.getReg()); }
      | IR.IUSE = { graph.addEdge(new DirectedEdge(graph.getDef(@1.getReg()), node)); }
      | IR.LUSE = { graph.addEdge(new DirectedEdge(graph.getDef(@1.getReg()), node)); }
      | IR.FUSE = { graph.addEdge(new DirectedEdge(graph.getDef(@1.getReg()), node)); }
      | IR.DUSE = { graph.addEdge(new DirectedEdge(graph.getDef(@1.getReg()), node)); }
      | IR.AUSE = { graph.addEdge(new DirectedEdge(graph.getDef(@1.getReg()), node)); }
      | IR.IPHI = { node.setReg(@1.getReg()); }
      | IR.LPHI = { node.setReg(@1.getReg()); }
      | IR.FPHI = { node.setReg(@1.getReg()); }
      | IR.DPHI = { node.setReg(@1.getReg()); }
      | IR.APHI = { node.setReg(@1.getReg()); }
      | default = {
        if (  left$ != null)   left$.build(graph, node);
        if (middle$ != null) middle$.build(graph, node);
        if ( right$ != null)  right$.build(graph, node);
      }
      ;

    public boolean phistmt(DGraph graph, DNode node, IRControlEdge edge)
      : IR.LABEL = { return true; }
      | IR.IPHI = { graph.addEdge(new DirectedEdge(graph.getDef(@1.getReg(edge)), node)); node.phidef(@1.getReg(edge), edge.sourceBB()); return true; }
      | IR.LPHI = { graph.addEdge(new DirectedEdge(graph.getDef(@1.getReg(edge)), node)); node.phidef(@1.getReg(edge), edge.sourceBB()); return true; }
      | IR.FPHI = { graph.addEdge(new DirectedEdge(graph.getDef(@1.getReg(edge)), node)); node.phidef(@1.getReg(edge), edge.sourceBB()); return true; }
      | IR.DPHI = { graph.addEdge(new DirectedEdge(graph.getDef(@1.getReg(edge)), node)); node.phidef(@1.getReg(edge), edge.sourceBB()); return true; }
      | IR.APHI = { graph.addEdge(new DirectedEdge(graph.getDef(@1.getReg(edge)), node)); node.phidef(@1.getReg(edge), edge.sourceBB()); return true; }
      | default = { return false; }
      ;
  
    public boolean fixed()
      : IR.IDEFINE(nomemlock) = { return !@2(); }
      | IR.LDEFINE(nomemlock) = { return !@2(); }
      | IR.FDEFINE(nomemlock) = { return !@2(); }
      | IR.DDEFINE(nomemlock) = { return !@2(); }
      | IR.ADEFINE(nomemlock) = { return !@2(); }
      | default = { return true; }
      ;

    private boolean nomemlock()
      : IR.ISLOCKED(nomemlock) = { return false; }
      | IR.BLOAD(nomemlock) = { return false; }
      | IR.SLOAD(nomemlock) = { return false; }
      | IR.ILOAD(nomemlock) = { return false; }
      | IR.LLOAD(nomemlock) = { return false; }
      | IR.FLOAD(nomemlock) = { return false; }
      | IR.DLOAD(nomemlock) = { return false; }
      | IR.ALOAD(nomemlock) = { return false; }
      | IR.BALOAD(nomemlock,nomemlock) = { return false; }
      | IR.SALOAD(nomemlock,nomemlock) = { return false; }
      | IR.IALOAD(nomemlock,nomemlock) = { return false; }
      | IR.LALOAD(nomemlock,nomemlock) = { return false; }
      | IR.FALOAD(nomemlock,nomemlock) = { return false; }
      | IR.DALOAD(nomemlock,nomemlock) = { return false; }
      | IR.AALOAD(nomemlock,nomemlock) = { return false; }
      | default = {
        boolean nomemlock = true;
        if (  left$ != null) nomemlock &=   left$.nomemlock();
        if (middle$ != null) nomemlock &= middle$.nomemlock();
        if ( right$ != null) nomemlock &=  right$.nomemlock();
        return nomemlock;
      }
      ;

    public boolean sensitive()
      : IR.IDEFINE(clslendiv) = { return @2(); }
      | IR.LDEFINE(clslendiv) = { return @2(); }
      | IR.FDEFINE(clslendiv) = { return @2(); }
      | IR.DDEFINE(clslendiv) = { return @2(); }
      | IR.ADEFINE(clslendiv) = { return @2(); }
      | default = { return false; }
      ;

    private boolean clslendiv()
      : IR.GETCLASS(IR.AUSE) = { return true; }
      | IR.LENGTH(IR.AUSE) = { return true; }
      | IR.IDIV(clslendiv,IR.IUSE) = { return true; }
      | IR.IREM(clslendiv,IR.IUSE) = { return true; }
      | IR.LDIV(clslendiv,IR.LUSE) = { return true; }
      | IR.LREM(clslendiv,IR.LUSE) = { return true; }
      | default = {
        boolean clslendiv = false;
        if (  left$ != null) clslendiv |=   left$.clslendiv();
        if (middle$ != null) clslendiv |= middle$.clslendiv();
        if ( right$ != null) clslendiv |=  right$.clslendiv();
        return clslendiv;
      }
      ;

  }

  private static final class DGraph extends DirectedGraph {

    private final DNode[] nodes;
    private final HashMap map = new HashMap();

    public DGraph(int count) {
      if (count < 0)
        throw new IllegalArgumentException();
      this.nodes = new DNode[count];
      for (int i = 0; i < nodes.length; i++) {
        nodes[i] = new DNode(i);
        super.addNode(nodes[i]);
      }
    }

    public Iterator nodes() {
      return new Iterator() {
        private int index;
        public boolean hasNext() { return index < nodes.length; }
        public Object next() { return nodes[index++]; }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }

    public DNode getNode(int index) {
      return nodes[index];
    }

    public DNode getDef(int index) {
      return (DNode)map.get(new Integer(index));
    }

    public void addNode(DirectedNode _node) {
      throw new UnsupportedOperationException();
    }

    public void removeNode(DirectedNode _node) {
      throw new UnsupportedOperationException();
    }

  }

  private static final class DNode extends DirectedNode {

    public final int index;
    private int reg = -1;
    public boolean fixed;
    public boolean sensitive;
    public BasicBlock defBB;
    private HashMap phidef;

    public DNode(int index) {
      if (index < 0)
        throw new IllegalArgumentException();
      this.index = index;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      DGraph graph = (DGraph)ownerGraph();
      if (this.reg != -1 || graph.map.get(new Integer(reg)) != null)
        throw new IllegalArgumentException();
      this.reg = reg;
      graph.map.put(new Integer(reg), this);
    }

    public void phidef(int reg, BasicBlock phiBB) {
      if (phidef == null)
        phidef = new HashMap();
      Integer key = new Integer(reg);
      HashSet set = (HashSet)phidef.get(key);
      if (set == null) {
        set = new HashSet();
        phidef.put(key, set);
      }
      set.add(phiBB);
    }

    public boolean findBestBB() {
      if (fixed)
        return false;
      BasicBlock minBB = minBB();
      BasicBlock maxBB = maxBB();
      BasicBlock bestBB = defBB;
      for (Iterator i = maxBB.dominatorBBs(); i.hasNext(); ) {
        BasicBlock candBB = (BasicBlock)i.next();
        if (candBB.loopDepth() < bestBB.loopDepth() || (candBB.loopDepth() == bestBB.loopDepth() && !candBB.dominates(bestBB)))
          bestBB = candBB;
        if (candBB == minBB)
          break;
      }
      if (bestBB != defBB) {
        defBB = bestBB;
        for (Iterator i = inEdges(); i.hasNext(); ) {
          DirectedEdge edge = (DirectedEdge)i.next();
          DNode node = (DNode)edge.sourceNode();
          node.findBestBB();
        }
        for (Iterator i = outEdges(); i.hasNext(); ) {
          DirectedEdge edge = (DirectedEdge)i.next();
          DNode node = (DNode)edge.targetNode();
          node.findBestBB();
        }
        return true;
      }
      return false;
    }

    private BasicBlock minBB() {
      if (fixed || sensitive)
        return defBB;
      BasicBlock minBB = defBB;
      for (Iterator i = minBB.dominatorBBs(); i.hasNext(); ) {
        BasicBlock dominatorBB = (BasicBlock)i.next();
        minBB = dominatorBB;
      }
      for (Iterator i = inEdges(); i.hasNext(); ) {
        DirectedEdge edge = (DirectedEdge)i.next();
        DNode node = (DNode)edge.sourceNode();
        if (minBB.dominates(node.defBB))
          minBB = node.defBB;
      }
      return minBB;
    }

    private BasicBlock maxBB() {
      if (fixed)
        return defBB;
      HashSet set = new HashSet();
      for (Iterator i = outEdges(); i.hasNext(); ) {
        DirectedEdge edge = (DirectedEdge)i.next();
        DNode node = (DNode)edge.targetNode();
        if (node.phidef == null)
          set.add(node.defBB);
        else {
          HashSet phidef = (HashSet)node.phidef.get(new Integer(reg));
          if (phidef != null)
            set.addAll(phidef);
        }
      }
      BasicBlock maxBB = null;
      for (Iterator i = set.iterator(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        if (maxBB == null)
          maxBB = currentBB;
        else
          for (Iterator j = maxBB.dominatorBBs(); j.hasNext(); ) {
            BasicBlock dominatorBB = (BasicBlock)j.next();
            if (dominatorBB.dominates(currentBB)) {
              maxBB = dominatorBB;
              break;
            }
          }
      }
      if (maxBB == null)
        maxBB = defBB;
      return maxBB;
    }

  }

}

