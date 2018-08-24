/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.dataflow.DataFlowAnalyser;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;
import jewel.core.jiro.optimize.Optimization;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;

// updated for SSA
public class SSAConverter implements Optimization {

  public SSAConverter() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (!cfg.isSSA()) {

      cfg.invalidate();
      int maxReg = cfg.regMax();

      BitSet globals = new BitSet(maxReg);
      BitSet[] defines = new BitSet[cfg.count()];
      BitSet[] variables = new BitSet[cfg.count()];
      HashSet worklist = new HashSet(cfg.count());

      // get definitions and globals
      int index = 0;
      for (Iterator i = cfg.topdownBBs(); i.hasNext(); index++) {
        BasicBlock currentBB = (BasicBlock)i.next();
        BitSet set = new BitSet(maxReg);
        for (Iterator j = currentBB.topdownStmts(); j.hasNext(); ) {
          IRStatement stmt = (IRStatement)j.next();
          Matcher matcher = new Matcher(stmt);
          matcher.defines(globals, set);
        }
        defines[index] = set;
        variables[index] = new BitSet(maxReg);
      }

      // initialize variables
      index = 0;
      for (Iterator i = cfg.topdownBBs(); i.hasNext(); index++) {
        BasicBlock currentBB = (BasicBlock)i.next();
        BitSet set = defines[index];
        set.and(globals);
        if (set.length() > 0)
          for (Iterator j = currentBB.frontierBBs(); j.hasNext(); ) {
            BasicBlock frontierBB = (BasicBlock)j.next();
            BitSet fset = variables[cfg.indexOf(frontierBB)];
            fset.or(set);
            worklist.add(frontierBB);
          }
      }

      // iterate variable thru frontiers
      while (!worklist.isEmpty()) {
        Iterator extractor = worklist.iterator();
        BasicBlock currentBB = (BasicBlock)extractor.next();
        extractor.remove();
        BitSet set = variables[cfg.indexOf(currentBB)];
        for (Iterator i = currentBB.frontierBBs(); i.hasNext(); ) {
          BasicBlock frontierBB = (BasicBlock)i.next();
          BitSet fset = variables[cfg.indexOf(frontierBB)];
          if (!fset.equals(set)) {
            fset.or(set);
            worklist.add(frontierBB);
          }
        }
      }

      // discard non-live variables
      DataFlowAnalyser analyser = new IterativeDataFlowAnalyser(new LiveVariables());
      index = 0;
      for (Iterator i = cfg.topdownBBs(); i.hasNext(); index++) {
        BasicBlock currentBB = (BasicBlock)i.next();
        BitSet set = variables[index];
        set.and((BitSet)analyser.valueBefore(currentBB));
      }

      // add phi statements
      index = 0;
      for (Iterator i = cfg.topdownBBs(); i.hasNext(); index++) {
        BasicBlock currentBB = (BasicBlock)i.next();
        BitSet set = variables[index];
        IRStatement leader = (IRStatement)currentBB.leader();
        for (int length = set.length(); length > 0; length = set.length()) {
          IRStatement stmt = null;
          int reg = length-1;
          switch (reg%5) {
          case 0: stmt = new IRStatement(new IR.iphi(reg)); break;
          case 1: stmt = new IRStatement(new IR.lphi(reg)); break;
          case 2: stmt = new IRStatement(new IR.fphi(reg)); break;
          case 3: stmt = new IRStatement(new IR.dphi(reg)); break;
          case 4: stmt = new IRStatement(new IR.aphi(reg)); break;
          }
          currentBB.insertStmtAfterStmt(stmt, leader);
          set.clear(reg);
        }
      }

      // rename variables
      int[] regs = new int[maxReg];
      int[] map = new int[cfg.stmtCount()];
      for (Iterator i = cfg.entryBBs(); i.hasNext(); ) {
        BasicBlock entryBB = (BasicBlock)i.next();
        rename(entryBB, regs, 0, map);
      }

      cfg.setSSA(true);

      changed = true;

    }

    return changed;
  }

  private static int rename(BasicBlock currentBB, int[] regs, int reg, int[] map) {
    for (Iterator i = currentBB.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher matcher = new Matcher(stmt);
      matcher.rename(regs);
      reg = matcher.redefine(regs, reg, map, currentBB.ownerCFG().indexOf(stmt));
    }
    for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
      IRControlEdge edge = (IRControlEdge)i.next();
      BasicBlock targetBB = edge.targetBB();
      for (Iterator j = targetBB.topdownStmts(); j.hasNext(); ) {
        IRStatement stmt = (IRStatement)j.next();
        Matcher matcher = new Matcher(stmt);
        if (!matcher.pathname(regs, edge, map, currentBB.ownerCFG().indexOf(stmt)))
          break;
      }
    }
    for (Iterator i = currentBB.idominateeBBs(); i.hasNext(); ) {
      BasicBlock dominateeBB = (BasicBlock)i.next();
      reg = rename(dominateeBB, (int[])regs.clone(), reg, map);
    }
    return reg;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void defines(BitSet globals, BitSet defines)
      : IR.IUSE = { int reg = @1.getReg(); if (!defines.get(reg)) globals.set(reg); }
      | IR.LUSE = { int reg = @1.getReg(); if (!defines.get(reg)) globals.set(reg); }
      | IR.FUSE = { int reg = @1.getReg(); if (!defines.get(reg)) globals.set(reg); }
      | IR.DUSE = { int reg = @1.getReg(); if (!defines.get(reg)) globals.set(reg); }
      | IR.AUSE = { int reg = @1.getReg(); if (!defines.get(reg)) globals.set(reg); }
      | IR.IRECEIVE = { defines.set(@1.getReg()); }
      | IR.LRECEIVE = { defines.set(@1.getReg()); }
      | IR.FRECEIVE = { defines.set(@1.getReg()); }
      | IR.DRECEIVE = { defines.set(@1.getReg()); }
      | IR.ARECEIVE = { defines.set(@1.getReg()); }
      | IR.IRESULT = { defines.set(@1.getReg()); }
      | IR.LRESULT = { defines.set(@1.getReg()); }
      | IR.FRESULT = { defines.set(@1.getReg()); }
      | IR.DRESULT = { defines.set(@1.getReg()); }
      | IR.ARESULT = { defines.set(@1.getReg()); }
      | IR.ACATCH = { defines.set(@1.getReg()); }
      | IR.IDEFINE(defines) = { @2(globals, defines); defines.set(@1.getReg()); }
      | IR.LDEFINE(defines) = { @2(globals, defines); defines.set(@1.getReg()); }
      | IR.FDEFINE(defines) = { @2(globals, defines); defines.set(@1.getReg()); }
      | IR.DDEFINE(defines) = { @2(globals, defines); defines.set(@1.getReg()); }
      | IR.ADEFINE(defines) = { @2(globals, defines); defines.set(@1.getReg()); }
      | IR.IPHI = { throw new Error(); }
      | IR.LPHI = { throw new Error(); }
      | IR.FPHI = { throw new Error(); }
      | IR.DPHI = { throw new Error(); }
      | IR.APHI = { throw new Error(); }
      | default = {
        if (  left$ != null)   left$.defines(globals, defines);
        if (middle$ != null) middle$.defines(globals, defines);
        if ( right$ != null)  right$.defines(globals, defines);
      }
      ;

    public int redefine(int[] regs, int reg, int[] map, int index)
      : IR.IRECEIVE = { reg = ((reg+4)/5)*5+0; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.LRECEIVE = { reg = ((reg+4)/5)*5+1; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.FRECEIVE = { reg = ((reg+4)/5)*5+2; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.DRECEIVE = { reg = ((reg+4)/5)*5+3; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.ARECEIVE = { reg = ((reg+4)/5)*5+4; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.IRESULT = { reg = ((reg+4)/5)*5+0; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.LRESULT = { reg = ((reg+4)/5)*5+1; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.FRESULT = { reg = ((reg+4)/5)*5+2; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.DRESULT = { reg = ((reg+4)/5)*5+3; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.ARESULT = { reg = ((reg+4)/5)*5+4; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.ACATCH = { reg = ((reg+4)/5)*5+4; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.IDEFINE(redefine) = { reg = ((reg+4)/5)*5+0; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.LDEFINE(redefine) = { reg = ((reg+4)/5)*5+1; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.FDEFINE(redefine) = { reg = ((reg+4)/5)*5+2; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.DDEFINE(redefine) = { reg = ((reg+4)/5)*5+3; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.ADEFINE(redefine) = { reg = ((reg+4)/5)*5+4; regs[@1.getReg()] = ~reg; @1.setReg(reg); return reg+1; }
      | IR.IPHI = { reg = ((reg+4)/5)*5+0; regs[@1.getReg()] = ~reg; map[index] = ~@1.getReg(); @1.setReg(reg); return reg+1; }
      | IR.LPHI = { reg = ((reg+4)/5)*5+1; regs[@1.getReg()] = ~reg; map[index] = ~@1.getReg(); @1.setReg(reg); return reg+1; }
      | IR.FPHI = { reg = ((reg+4)/5)*5+2; regs[@1.getReg()] = ~reg; map[index] = ~@1.getReg(); @1.setReg(reg); return reg+1; }
      | IR.DPHI = { reg = ((reg+4)/5)*5+3; regs[@1.getReg()] = ~reg; map[index] = ~@1.getReg(); @1.setReg(reg); return reg+1; }
      | IR.APHI = { reg = ((reg+4)/5)*5+4; regs[@1.getReg()] = ~reg; map[index] = ~@1.getReg(); @1.setReg(reg); return reg+1; }
      | default = { return reg; }
      ;

    public void rename(int[] regs)
      : IR.IUSE = { @1.setReg(~regs[@1.getReg()]); }
      | IR.LUSE = { @1.setReg(~regs[@1.getReg()]); }
      | IR.FUSE = { @1.setReg(~regs[@1.getReg()]); }
      | IR.DUSE = { @1.setReg(~regs[@1.getReg()]); }
      | IR.AUSE = { @1.setReg(~regs[@1.getReg()]); }
      | default = {
        if (  left$ != null)   left$.rename(regs);
        if (middle$ != null) middle$.rename(regs);
        if ( right$ != null)  right$.rename(regs);
      }
      ;

    public boolean pathname(int[] regs, IRControlEdge edge, int[] map, int index)
      : IR.LABEL = { return true; }
      | IR.IPHI = { int reg = map[index] == 0 ? @1.getReg() : ~map[index]; @1.setReg(edge, ~regs[reg]); return true; }
      | IR.LPHI = { int reg = map[index] == 0 ? @1.getReg() : ~map[index]; @1.setReg(edge, ~regs[reg]); return true; }
      | IR.FPHI = { int reg = map[index] == 0 ? @1.getReg() : ~map[index]; @1.setReg(edge, ~regs[reg]); return true; }
      | IR.DPHI = { int reg = map[index] == 0 ? @1.getReg() : ~map[index]; @1.setReg(edge, ~regs[reg]); return true; }
      | IR.APHI = { int reg = map[index] == 0 ? @1.getReg() : ~map[index]; @1.setReg(edge, ~regs[reg]); return true; }
      | default = { return false; }
      ;

  }

}

