/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.LoopTree;
import jewel.core.jiro.control.LoopTree.Loop;
import jewel.core.jiro.control.LoopTree.LoopNode;
import jewel.core.jiro.graph.DirectedGraph.DirectedEdge;
import jewel.core.jiro.optimize.Optimization;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Iterator;

// missing INITX and ISWITCH
public final class LoopPeeling implements Optimization {

  /*public static void main(String[] args) {
    CFGBuilder builder = new CFGBuilder();

    builder.append(new IR.idefine(0, new IR.iconst(0)));
    builder.append(new IR.label(0));

    builder.append(new IR.label(1));
    builder.append(new IR.idefine(5, new IR.iconst(0)));
    builder.append(new IR.ijump(IR.ijump.EQ, new IR.iuse(5), new IR.iconst(0), 1));

    builder.append(new IR.ijump(IR.ijump.EQ, new IR.iuse(0), new IR.iconst(0), 0));
    builder.append(new IR.vreturn());

    IRCFG cfg = builder.toCFG();
    IRCFG mcfg = (IRCFG)cfg.clone();
    OPT.applyTo(mcfg);
    mcfg.printTo(System.err);
    cfg.show(new IRCFG[]{ cfg, mcfg });
  }*/

  public LoopPeeling() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (!cfg.isSSA()) {

      cfg.invalidate();

      LoopTree loopTree = cfg.loopTree();

      Loop bestLoop = null;
      for (Iterator i = loopTree.allLoops(); i.hasNext(); ) {
        Loop loop = (Loop)i.next();

        if (loop.nodeCount() > 8)
          continue;
                  
        if (bestLoop != null && bestLoop.depth() >= loop.depth())
          continue;

        /* check if there is a dominating class initialization */
        for (Iterator j = loop.nodes(); j.hasNext(); ) {
          LoopNode node = (LoopNode)j.next();
          BasicBlock currentBB = cfg.getBB(loopTree.indexOf(node));
          boolean dominates = true;
          for (Iterator k = loop.backedges(); k.hasNext(); ) {
            DirectedEdge edge = (DirectedEdge)k.next();
            LoopNode source = (LoopNode)edge.sourceNode();
            BasicBlock sourceBB = cfg.getBB(loopTree.indexOf(source));
            if (!currentBB.dominates(sourceBB)) {
              dominates = false;
              break;
            }
          }
          if (dominates) {
            for (Iterator k = currentBB.topdownStmts(); k.hasNext(); ) {
              IRStatement stmt = (IRStatement)k.next();
              Matcher matcher = new Matcher(stmt);
              if (matcher.inits()) {
                bestLoop = loop;
                break;
              }
            }
            if (bestLoop == loop)
              break;
          }
        }
        if (bestLoop == loop)
          continue;

        /* check if there is an dominating exit invariant test */
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
        for (Iterator j = loop.exitNodes(); j.hasNext(); ) {
          LoopNode exit = (LoopNode)j.next();
          BasicBlock exitBB = cfg.getBB(loopTree.indexOf(exit));
          IRStatement trailer = (IRStatement)exitBB.trailer();
          if (trailer != null) {
            Matcher matcher = new Matcher(trailer);
            if (matcher.eligible(defines)) {
              boolean dominates = true;
              for (Iterator k = loop.backedges(); k.hasNext(); ) {
                DirectedEdge edge = (DirectedEdge)k.next();
                LoopNode source = (LoopNode)edge.sourceNode();
                BasicBlock sourceBB = cfg.getBB(loopTree.indexOf(source));
                if (!exitBB.dominates(sourceBB)) {
                  dominates = false;
                  break;
                }
              }
              if (dominates) {
                bestLoop = loop;
                break;
              }
            }
          }
        }

      }

      if (bestLoop != null) {
        //System.err.println("*LOOP PEELING*");
        //try { System.in.read(); } catch (Exception e) { }
        peelLoop(cfg, loopTree, bestLoop);
        changed = true;
      }

    }

    return changed;
  }

  private static void peelLoop(IRCFG cfg, LoopTree loopTree, Loop loop) {

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

    /* compute loop back edges */
    HashSet backedges = new HashSet();
    for (Iterator i = loop.backedges(); i.hasNext(); ) {
      DirectedEdge edge = (DirectedEdge)i.next();
      BasicBlock sourceBB = cfg.getBB(loopTree.indexOf((LoopNode)edge.sourceNode()));
      BasicBlock targetBB = cfg.getBB(loopTree.indexOf((LoopNode)edge.targetNode()));
      for (Iterator j = sourceBB.outEdges(); j.hasNext(); ) {
        IRControlEdge iredge = (IRControlEdge)j.next();
        if (iredge.targetBB() == targetBB)
          backedges.add(iredge);
      }
    }

    /* forward original back edges */
    for (Iterator i = backedges.iterator(); i.hasNext(); ) {
      IRControlEdge edge = (IRControlEdge)i.next();
      cfg.removeEdge(edge);
      BasicBlock sourceBB = edge.sourceBB();
      BasicBlock targetBB = edge.targetBB();
      BasicBlock cloneBB = (BasicBlock)bbmap.get(targetBB);
      if (edge.type() != IRControlEdge.FALL) {
        IRStatement trailer = (IRStatement)sourceBB.trailer();
        IRStatement leader = (IRStatement)cloneBB.leader();
        IR.label label = (IR.label)leader.snode();
        Matcher matcher = new Matcher(trailer);
        matcher.trailer(edge.value(), label.getValue());
      } else {
        IRStatement leader = (IRStatement)cloneBB.leader();
        if (leader == null || leader.snode().op() != IR.LABEL) {
          leader = new IRStatement(new IR.label(cfg.labelSeq()));
          cloneBB.prependStmt(leader);
        }
        IR.label label = (IR.label)leader.snode();
        BasicBlock newBB = new IRBasicBlock();
        newBB.appendStmt(new IRStatement(new IR.jump(label.getValue())));
        cfg.insertBBAfterBB(newBB, sourceBB);
        cfg.addEdge(new IRControlEdge(newBB, IRControlEdge.JUMP, cloneBB));
        cloneBB = newBB;
      }
      cfg.addEdge(edge.clone(sourceBB, cloneBB));
    }

  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public boolean inits()
      : IR.INIT(IR.ACLASS) = { return true; }
      | default = { return false; }
      ;

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

