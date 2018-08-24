/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.DataFlowAnalyser;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;
import jewel.core.jiro.graph.ConflictGraph;
import jewel.core.jiro.graph.ConflictGraph.CoalesceEdge;
import jewel.core.jiro.graph.ConflictGraph.ConflictEdge;
import jewel.core.jiro.graph.ConflictGraph.ConflictNode;
import jewel.core.jiro.optimize.Optimization;

import java.util.BitSet;
import java.util.Iterator;

public final class RegisterAllocating implements Optimization {

  public RegisterAllocating() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (!cfg.isSSA()) {

      cfg.invalidate();
      int maxReg = cfg.regMax();

      DataFlowAnalyser analyser = new IterativeDataFlowAnalyser(new LiveVariables());

      ConflictGraph conflictGraph = new ConflictGraph();
      for (Iterator i = cfg.bottomupStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.copy(conflictGraph);
        BitItem item = (BitItem)analyser.valueBefore(stmt);
        for (int length = item.length(); length > 0; length = item.length()) {
          int reg0 = length-1;
          item.clear(reg0);
          ConflictNode node0 = conflictGraph.getNode(reg0);
          if (node0 == null) {
            node0 = new ConflictNode(reg0);
            conflictGraph.addNode(node0);
          }
          for (int reg1 = reg0-1; reg1 >= 0; reg1--)
            if (item.get(reg1)) {
              ConflictNode node1 = conflictGraph.getNode(reg1);
              if (node1 == null) {
                node1 = new ConflictNode(reg1);
                conflictGraph.addNode(node1);
              }
              conflictGraph.removeEdge(new CoalesceEdge(node0, node1));
              conflictGraph.addEdge(new ConflictEdge(node0, node1));
            }
        }
      }

//      cfg.printTo(System.err);
//      conflictGraph.show();

      int[] regs = new int[maxReg];
      for (Iterator i = conflictGraph.nodes(); i.hasNext(); ) {
        ConflictNode node0 = (ConflictNode)i.next();

        BitSet set = new BitSet(maxReg);
        for (Iterator j = node0.conflictNodes(); j.hasNext(); ) {
          ConflictNode node1 = (ConflictNode)j.next();
          if (regs[node1.register()] != 0)
            set.set(~regs[node1.register()]);
        }
        for (int j = node0.register()%5; ; j += 5)
          if (!set.get(j)) {
            regs[node0.register()] = ~j;
            break;
          }


/*        for (Iterator j = node0.coalesceNodes(); j.hasNext(); ) {
          ConflictNode node1 = (ConflictNode)j.next();
          regs[node1.register()] = regs[node0.register()];
        }*/

      }

      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.rename(regs);
        if (matcher.useless())
          i.remove();
      }

      changed = true;

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void rename(int[] regs)
      : IR.IUSE = { @1.setReg(~regs[@1.getReg()]); }
      | IR.LUSE = { @1.setReg(~regs[@1.getReg()]); }
      | IR.FUSE = { @1.setReg(~regs[@1.getReg()]); }
      | IR.DUSE = { @1.setReg(~regs[@1.getReg()]); }
      | IR.AUSE = { @1.setReg(~regs[@1.getReg()]); }
      | IR.IRECEIVE = { if (regs[@1.getReg()] != 0) @1.setReg(~regs[@1.getReg()]); }
      | IR.LRECEIVE = { if (regs[@1.getReg()] != 0) @1.setReg(~regs[@1.getReg()]); }
      | IR.FRECEIVE = { if (regs[@1.getReg()] != 0) @1.setReg(~regs[@1.getReg()]); }
      | IR.DRECEIVE = { if (regs[@1.getReg()] != 0) @1.setReg(~regs[@1.getReg()]); }
      | IR.ARECEIVE = { if (regs[@1.getReg()] != 0) @1.setReg(~regs[@1.getReg()]); }
      | IR.IRESULT = { @1.setReg(~regs[@1.getReg()]); }
      | IR.LRESULT = { @1.setReg(~regs[@1.getReg()]); }
      | IR.FRESULT = { @1.setReg(~regs[@1.getReg()]); }
      | IR.DRESULT = { @1.setReg(~regs[@1.getReg()]); }
      | IR.ARESULT = { @1.setReg(~regs[@1.getReg()]); }
      | IR.ACATCH = { @1.setReg(~regs[@1.getReg()]); }
      | IR.IDEFINE(rename) = { @2(regs); @1.setReg(~regs[@1.getReg()]); }
      | IR.LDEFINE(rename) = { @2(regs); @1.setReg(~regs[@1.getReg()]); }
      | IR.FDEFINE(rename) = { @2(regs); @1.setReg(~regs[@1.getReg()]); }
      | IR.DDEFINE(rename) = { @2(regs); @1.setReg(~regs[@1.getReg()]); }
      | IR.ADEFINE(rename) = { @2(regs); @1.setReg(~regs[@1.getReg()]); }
      | default = {
        if (  left$ != null)   left$.rename(regs);
        if (middle$ != null) middle$.rename(regs);
        if ( right$ != null)  right$.rename(regs);
      }
      ;

    public boolean useless()
      : IR.IDEFINE(IR.IUSE) = { return @1.getReg() == @2.getReg(); }
      | IR.LDEFINE(IR.LUSE) = { return @1.getReg() == @2.getReg(); }
      | IR.FDEFINE(IR.FUSE) = { return @1.getReg() == @2.getReg(); }
      | IR.DDEFINE(IR.DUSE) = { return @1.getReg() == @2.getReg(); }
      | IR.ADEFINE(IR.AUSE) = { return @1.getReg() == @2.getReg(); }
      | default = { return false; }
      ;

    public void copy(ConflictGraph conflictGraph)
      : IR.IDEFINE(IR.IUSE) = { add(conflictGraph, @1.getReg(), @2.getReg()); }
      | IR.LDEFINE(IR.LUSE) = { add(conflictGraph, @1.getReg(), @2.getReg()); }
      | IR.FDEFINE(IR.FUSE) = { add(conflictGraph, @1.getReg(), @2.getReg()); }
      | IR.DDEFINE(IR.DUSE) = { add(conflictGraph, @1.getReg(), @2.getReg()); }
      | IR.ADEFINE(IR.AUSE) = { add(conflictGraph, @1.getReg(), @2.getReg()); }
      | default = { }
      ;

    private static void add(ConflictGraph conflictGraph, int reg0, int reg1) {
      ConflictNode node0 = conflictGraph.getNode(reg0);
      if (node0 == null) {
        node0 = new ConflictNode(reg0);
        conflictGraph.addNode(node0);
      }
      ConflictNode node1 = conflictGraph.getNode(reg1);
      if (node1 == null) {
        node1 = new ConflictNode(reg1);
        conflictGraph.addNode(node1);
      }
      if (!conflictGraph.contains(new ConflictEdge(node0, node1)))
        conflictGraph.addEdge(new CoalesceEdge(node0, node1));
    }

  }

}

