/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.DataFlowAnalyser;
import jewel.core.jiro.dataflow.DataFlowAnalysis;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;
import jewel.core.jiro.optimize.Optimization;

import java.util.Iterator;

// updated for SSA
// compute retNonNull
public final class IfRemoval implements Optimization {

  public IfRemoval() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    boolean retNonNull = cfg.retNonNull();
    cfg.setRetNonNull(true);

    DataFlowAnalyser analyser = new IterativeDataFlowAnalyser(new NonNullPointers());
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      IRStatement stmt = (IRStatement)currentBB.trailer();
      if (stmt != null) {
        BitItem value = (BitItem)analyser.valueBefore(stmt);
        Matcher matcher = new Matcher(stmt);
        changed |= matcher.cover(value);
        matcher.cover2(cfg, value);
      }
    }

    if (cfg.retNonNull() != retNonNull)
      changed = true;

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }
  
    public boolean cover(BitItem value)
      : IR.AJUMP(IR.AUSE,IR.ANULL) [@1.xop() == IR.ajump.EQ] = { 
        if (value.get(@2.getReg())) {
          IRStatement stmt = @1.ownerStmt();
          BasicBlock currentBB = (BasicBlock)stmt.ownerBB();
          currentBB.removeStmt(stmt);
          for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)i.next();
            if (edge.type() == IRControlEdge.JUMP) {
              i.remove();
              break;
            }
          }
          return true;
        } 
        return false;
      }
      | IR.AJUMP(IR.ANULL,IR.AUSE) [@1.xop() == IR.ajump.EQ] = {
        if (value.get(@3.getReg())) {
          IRStatement stmt = @1.ownerStmt();
          BasicBlock currentBB = (BasicBlock)stmt.ownerBB();
          currentBB.removeStmt(stmt);
          for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)i.next();
            if (edge.type() == IRControlEdge.JUMP) {
              i.remove();
              break;
            }
          }
          return true;
        } 
        return false;
      }
      | IR.AJUMP(IR.AUSE,IR.ANULL) [@1.xop() == IR.ajump.NE] = {
        if (value.get(@2.getReg())) {
          IRStatement stmt = @1.ownerStmt();
          BasicBlock currentBB = (BasicBlock)stmt.ownerBB();
          stmt.setNode(new IR.jump(@1.getTarget()));
          for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)i.next();
            if (edge.type() == IRControlEdge.FALL) {
              i.remove();
              break;
            }
          }
          return true;
        } 
        return false;
      }
      | IR.AJUMP(IR.ANULL,IR.AUSE) [@1.xop() == IR.ajump.NE] = {
        if (value.get(@3.getReg())) {
          IRStatement stmt = @1.ownerStmt();
          BasicBlock currentBB = (BasicBlock)stmt.ownerBB();
          stmt.setNode(new IR.jump(@1.getTarget()));
          for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)i.next();
            if (edge.type() == IRControlEdge.FALL) {
              i.remove();
              break;
            }
          }
          return true;
        }
        return false;
      }
      | default = { return false; }
      ;
      
    public void cover2(IRCFG cfg, BitItem value)
      : IR.ARETURN(aunknown) = { cfg.setRetNonNull(false); }
      | IR.ARETURN(IR.AUSE) = { if (!value.get(@2.getReg())) cfg.setRetNonNull(false); }
      | default = { }
      ;

    private void aunknown()
      : IR.ALOAD(cover)
      | IR.AALOAD(cover,cover)
      | IR.IMLOOKUP(cover,cover)
      | IR.ANULL
      ;

  }

}

