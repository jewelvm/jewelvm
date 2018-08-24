/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.LoopTree;
import jewel.core.jiro.control.LoopTree.Loop;
import jewel.core.jiro.control.LoopTree.LoopNode;
import jewel.core.jiro.optimize.Optimization;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Iterator;

public final class Unswitching implements Optimization {

  public Unswitching() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (!cfg.isSSA()) {

      cfg.invalidate();

      LoopTree loopTree = cfg.loopTree();

      Loop bestLoop = null;
      IRStatement condStmt = null;
      for (Iterator i = loopTree.allLoops(); i.hasNext(); ) {
        Loop loop = (Loop)i.next();

        if (loop.nodeCount() > 8)
          continue;
                  
        if (bestLoop != null && bestLoop.depth() >= loop.depth())
          continue;

        /* check if loop has preheader and there is a invariant test */
        LoopNode preheader = loop.preheader();
        if (preheader == null)
          continue;
        BitSet defines = new BitSet();
        for (Iterator j = loop.nodes(); j.hasNext(); ) {
          LoopNode node = (LoopNode)j.next();
          BasicBlock currentBB = cfg.getBB(loopTree.indexOf(node));
          for (Iterator k = currentBB.topdownStmts(); k.hasNext(); ) {
            IRStatement stmt = (IRStatement)k.next();
            Matcher matcher = new Matcher(stmt);
            matcher.defines(defines);
          }
        }
        for (Iterator j = loop.nodes(); j.hasNext(); ) {
          LoopNode node = (LoopNode)j.next();
          BasicBlock currentBB = cfg.getBB(loopTree.indexOf(node));
          IRStatement trailer = (IRStatement)currentBB.trailer();
          if (trailer != null) {
            Matcher matcher = new Matcher(trailer);
            if (matcher.eligible(defines)) {
              bestLoop = loop;
              condStmt = (IRStatement)trailer.clone();
              break;
            }
          }
        }

      }

      if (bestLoop != null) {
        //System.err.println("*UNSWITCHING*");
        //try { System.in.read(); } catch (Exception e) { }
        unswitch(cfg, loopTree, bestLoop, condStmt);
        changed = true;
      }

    }

    return changed;
  }

  private static void unswitch(IRCFG cfg, LoopTree loopTree, Loop loop, IRStatement condStmt) {

    /* get pre-header */
    LoopNode preheader = loop.preheader();
    BasicBlock preheaderBB = cfg.getBB(loopTree.indexOf(preheader));
    BasicBlock headerBB = (BasicBlock)preheaderBB.succBBs().next();

    /* clone loop */
    HashMap bbmap = new HashMap();
    ArrayList bblist = new ArrayList();
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock loopBB = (BasicBlock)i.next();
      LoopNode node = (LoopNode)loopTree.getNode(cfg.indexOf(loopBB));
      if (loop.contains(node)) {
        BasicBlock cloneBB = (BasicBlock)loopBB.clone();
        bblist.add(cloneBB);
        bbmap.put(loopBB, cloneBB);
      }
    }
    for (Iterator i = bblist.iterator(); i.hasNext(); ) {
      BasicBlock cloneBB = (BasicBlock)i.next();
      IRStatement leader = (IRStatement)cloneBB.leader();
      if (leader != null) {
        IR.snode snode = leader.snode();
        if (snode.op() == IR.LABEL) {
          IR.label label = (IR.label)snode;
          label.setValue(cfg.labelSeq());
        }
      }
      cfg.appendBB(cloneBB);
    }

    /* add clone source edges */
    for (Iterator i = bbmap.entrySet().iterator(); i.hasNext(); ) {
      Entry entry = (Entry)i.next();
      BasicBlock loopBB = (BasicBlock)entry.getKey();
      BasicBlock cloneBB = (BasicBlock)entry.getValue();
      for (Iterator j = loopBB.outEdges(); j.hasNext(); ) {
        IRControlEdge edge = (IRControlEdge)j.next();
        BasicBlock targetBB = edge.targetBB();
        BasicBlock newtargetBB = (BasicBlock)bbmap.get(targetBB);
        if (newtargetBB != null) {
          targetBB = newtargetBB;
          if (edge.type() != IRControlEdge.FALL) {
            IRStatement trailer = (IRStatement)cloneBB.trailer();
            IRStatement leader = (IRStatement)targetBB.leader();
            IR.label label = (IR.label)leader.snode();
            Matcher matcher = new Matcher(trailer);
            matcher.trailer(edge.value(), label.getValue());
          }
        } else {
          if (edge.type() == IRControlEdge.FALL) {
            IRStatement leader = (IRStatement)targetBB.leader();
            if (leader == null || leader.snode().op() != IR.LABEL) {
              leader = new IRStatement(new IR.label(cfg.labelSeq()));
              targetBB.prependStmt(leader);
            }
            IR.label label = (IR.label)leader.snode();
            BasicBlock newBB = new IRBasicBlock();
            newBB.appendStmt(new IRStatement(new IR.jump(label.getValue())));
            cfg.insertBBAfterBB(newBB, cloneBB);
            cfg.addEdge(new IRControlEdge(newBB, IRControlEdge.JUMP, targetBB));
            targetBB = newBB;
          }
        }
        cfg.addEdge(edge.clone(cloneBB, targetBB));
      }
    }

    /* add conditional to the pre-header */
    IRStatement trailer = (IRStatement)preheaderBB.trailer();
    if (trailer != null)
      if (trailer.snode().op() == IR.JUMP) {
        BasicBlock newBB = new IRBasicBlock();
        preheaderBB.removeStmt(trailer);
        cfg.removeEdge(new IRControlEdge(preheaderBB, IRControlEdge.JUMP, headerBB));
        cfg.insertBBAfterBB(newBB, preheaderBB);
        cfg.addEdge(new IRControlEdge(preheaderBB, IRControlEdge.FALL, newBB));
        newBB.appendStmt(trailer);
        cfg.addEdge(new IRControlEdge(newBB, IRControlEdge.JUMP, headerBB));
      }
    headerBB = (BasicBlock)bbmap.get(headerBB);
    IRStatement leader = (IRStatement)headerBB.leader();
    IR.label label = (IR.label)leader.snode();
    Matcher matcher = new Matcher(condStmt);
    matcher.trailer(-1, label.getValue());
    preheaderBB.appendStmt(condStmt);
    cfg.addEdge(new IRControlEdge(preheaderBB, IRControlEdge.JUMP, headerBB));

  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void defines(BitSet defines)
      : IR.IRECEIVE = { defines.set(@1.getReg()); }
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
      | IR.IDEFINE(defines) = { defines.set(@1.getReg()); }
      | IR.LDEFINE(defines) = { defines.set(@1.getReg()); }
      | IR.FDEFINE(defines) = { defines.set(@1.getReg()); }
      | IR.DDEFINE(defines) = { defines.set(@1.getReg()); }
      | IR.ADEFINE(defines) = { defines.set(@1.getReg()); }
      | default = { }
      ;

    public boolean eligible(BitSet defines)
      : IR.AJUMP(nomemlock, nomemlock) [@2.valid && @3.valid] = { return @2(defines) && @3(defines); }
      | IR.IJUMP(nomemlock, nomemlock) [@2.valid && @3.valid] = { return @2(defines) && @3(defines); }
      | default = { return false; }
      ;

    private boolean nomemlock(BitSet defines)
    <boolean valid>
      : IR.IUSE { @@.valid = true; } = { return !defines.get(@1.getReg()); }
      | IR.LUSE { @@.valid = true; } = { return !defines.get(@1.getReg()); }
      | IR.FUSE { @@.valid = true; } = { return !defines.get(@1.getReg()); }
      | IR.DUSE { @@.valid = true; } = { return !defines.get(@1.getReg()); }
      | IR.AUSE { @@.valid = true; } = { return !defines.get(@1.getReg()); }
      | IR.ALOAD(nomemlock)
      | IR.AALOAD(nomemlock,nomemlock)
      | IR.BLOAD(nomemlock)
      | IR.SLOAD(nomemlock)
      | IR.ILOAD(nomemlock)
      | IR.BALOAD(nomemlock,nomemlock)
      | IR.SALOAD(nomemlock,nomemlock)
      | IR.IALOAD(nomemlock,nomemlock)
      | IR.ISLOCKED(nomemlock)
      | IR.LLOAD(nomemlock)
      | IR.LALOAD(nomemlock,nomemlock)
      | IR.FLOAD(nomemlock)
      | IR.FALOAD(nomemlock,nomemlock)
      | IR.DLOAD(nomemlock)
      | IR.DALOAD(nomemlock,nomemlock)
      | default {
        @@.valid = true;
        if (  left$ != null) @@.valid &=   left$.nomemlock.valid;
        if (middle$ != null) @@.valid &= middle$.nomemlock.valid;
        if ( right$ != null) @@.valid &=  right$.nomemlock.valid;
      } = {
        boolean result = true;
        if (  left$ != null) result &=   left$.nomemlock(defines);
        if (middle$ != null) result &= middle$.nomemlock(defines);
        if ( right$ != null) result &=  right$.nomemlock(defines);
        return result;
      }
      ;

    public void trailer(int value, int label)
      : IR.CALLX(trailer) = { @1.setHandler(label); }
      | IR.NCALLX = { @1.setHandler(label); }
      | IR.JUMP = { @1.setTarget(label); }
      | IR.AJUMP(trailer,trailer) = { @1.setTarget(label); }
      | IR.IJUMP(trailer,trailer) = { @1.setTarget(label); }
      | IR.ISWITCH(trailer) = { @1.setTarget(value, label); }
      | IR.INITX(trailer) = { @1.setHandler(label); }
      | IR.NEWINSTANCEX(trailer) = { @1.setHandler(label); }
      | IR.NEWARRAYX(trailer,trailer) = { @1.setHandler(label); }
      | IR.LOCKX(trailer) = { @1.setHandler(label); }
      | default = { }
      ;

  }

}

