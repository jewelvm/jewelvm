/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public final class PathForwarding implements Optimization {

  public PathForwarding() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (!cfg.isSSA()) {

      cfg.invalidate();

      HashMap map = new HashMap();
      for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        if (currentBB.inDegree() < 2)
          continue;
        switch (currentBB.count()) {
        default: continue;
        case 2:
          IRStatement leader = (IRStatement)currentBB.leader();
          if (leader.snode().op() != IR.LABEL) continue;
        case 1: break;
        }
        IRStatement trailer = (IRStatement)currentBB.trailer();
        Matcher matcher = new Matcher(trailer);
        int reg = matcher.condreg();
        if (reg == -1)
          continue;
        for (Iterator j = currentBB.inEdges(); j.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)j.next();
          BasicBlock sourceBB = edge.sourceBB();
          int trg = reg;
          for (Iterator k = sourceBB.bottomupStmts(); k.hasNext(); ) {
            IRStatement stmt = (IRStatement)k.next();
            Matcher matcher2 = new Matcher(stmt);
            if (matcher2.def(trg)) {
              int cpy = matcher2.cpy();
              if (cpy != -1) {
                trg = cpy;
                continue;
              }
              int con = matcher2.con();
              if (con != 0xBEBEBABA)
                map.put(edge, matcher.condedg(con));
              break;
            }
          }
        }
      }

      for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
        Entry entry = (Entry)i.next();
        IRControlEdge edge1 = (IRControlEdge)entry.getKey();
        IRControlEdge edge2 = (IRControlEdge)entry.getValue();

        BasicBlock sourceBB = edge1.sourceBB();
        BasicBlock targetBB = edge2.targetBB();

        IRStatement leader = (IRStatement)targetBB.leader();
        IR.label label;
        if (leader != null && leader.snode().op() == IR.LABEL)
          label = (IR.label)leader.snode();
        else {
          label = new IR.label(cfg.labelSeq());
          leader = new IRStatement(label);
          targetBB.prependStmt(leader);
        }

        cfg.removeEdge(edge1);

        switch (edge1.type()) {
        case IRControlEdge.FALL:
          BasicBlock newBB = new IRBasicBlock();
          newBB.appendStmt(new IRStatement(new IR.jump(label.getValue())));
          cfg.insertBBAfterBB(newBB, sourceBB);
          cfg.addEdge(new IRControlEdge(sourceBB, IRControlEdge.FALL, newBB));
          cfg.addEdge(new IRControlEdge(newBB, IRControlEdge.JUMP, targetBB));
          break;
        case IRControlEdge.JUMP:
          IRStatement trailer = (IRStatement)sourceBB.trailer();
          IR.snode snode = trailer.snode();
          switch (snode.op()) {
          case IR.JUMP:
            IR.jump jump = (IR.jump)snode;
            jump.setTarget(label.getValue());
            break;
          case IR.IJUMP:
            IR.ijump ijump = (IR.ijump)snode;
            ijump.setTarget(label.getValue());
            break;
          case IR.AJUMP:
            IR.ajump ajump = (IR.ajump)snode;
            ajump.setTarget(label.getValue());
            break;
          default:
            throw new IllegalStateException();
          }
          cfg.addEdge(new IRControlEdge(sourceBB, IRControlEdge.JUMP, targetBB));
          break;
        case IRControlEdge.EXCEPT:
          trailer = (IRStatement)sourceBB.trailer();
          snode = trailer.snode();
          switch (snode.op()) {
          case IR.CALLX:
            IR.callx callx = (IR.callx)snode;
            callx.setHandler(label.getValue());
            break;
          case IR.NCALLX:
            IR.ncallx ncallx = (IR.ncallx)snode;
            ncallx.setHandler(label.getValue());
            break;
          case IR.INITX:
            IR.initx initx = (IR.initx)snode;
            initx.setHandler(label.getValue());
            break;
          case IR.NEWINSTANCEX:
            IR.newinstancex newinstancex = (IR.newinstancex)snode;
            newinstancex.setHandler(label.getValue());
            break;
          case IR.NEWARRAYX:
            IR.newarrayx newarrayx = (IR.newarrayx)snode;
            newarrayx.setHandler(label.getValue());
            break;
          case IR.LOCKX:
            IR.lockx lockx = (IR.lockx)snode;
            lockx.setHandler(label.getValue());
            break;
          default:
            throw new IllegalStateException();
          }
          cfg.addEdge(new IRControlEdge(sourceBB, IRControlEdge.EXCEPT, targetBB));
          break;
        case IRControlEdge.SWITCH:
          trailer = (IRStatement)sourceBB.trailer();
          IR.iswitch iswitch = (IR.iswitch)trailer.snode();
          iswitch.setTarget(edge1.value(), label.getValue());
          cfg.addEdge(new IRControlEdge(sourceBB, IRControlEdge.SWITCH, edge1.value(), targetBB));
          break;
        }

        changed = true;
      }

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public int condreg()
      : IR.AJUMP(IR.AUSE,IR.ANULL) = { return @2.getReg(); }
      | IR.IJUMP(IR.IUSE,IR.ICONST) = { return @2.getReg(); }
      | IR.ISWITCH(IR.IUSE) = { return @2.getReg(); }
      | default = { return -1; }
      ;

    public IRControlEdge condedg(int con)
      : IR.AJUMP(IR.AUSE,IR.ANULL) = {
        Iterator i = @1.ownerStmt().ownerBB().outEdges();
        IRControlEdge jump;
        IRControlEdge fall = (IRControlEdge)i.next();
        if (fall.type() == IRControlEdge.FALL)
          jump = (IRControlEdge)i.next();
        else {
          jump = fall;
          fall = (IRControlEdge)i.next();
        }
        switch (@1.xop()) {
        case IR.ijump.EQ: return con == 0 ? jump : fall;
        case IR.ijump.NE: return con != 0 ? jump : fall;
        }
      }
      | IR.IJUMP(IR.IUSE,IR.ICONST) = {
        Iterator i = @1.ownerStmt().ownerBB().outEdges();
        IRControlEdge jump;
        IRControlEdge fall = (IRControlEdge)i.next();
        if (fall.type() == IRControlEdge.FALL)
          jump = (IRControlEdge)i.next();
        else {
          jump = fall;
          fall = (IRControlEdge)i.next();
        }
        switch (@1.xop()) {
        case IR.ijump.EQ: return con == @3.getValue() ? jump : fall;
        case IR.ijump.NE: return con != @3.getValue() ? jump : fall;
        case IR.ijump.LT: return con <  @3.getValue() ? jump : fall;
        case IR.ijump.GE: return con >= @3.getValue() ? jump : fall;
        case IR.ijump.GT: return con >  @3.getValue() ? jump : fall;
        case IR.ijump.LE: return con <= @3.getValue() ? jump : fall;
        case IR.ijump.B : return ((long)con & 0xFFFFFFFFL) <  ((long)@3.getValue() & 0xFFFFFFFFL) ? jump : fall;
        case IR.ijump.AE: return ((long)con & 0xFFFFFFFFL) >= ((long)@3.getValue() & 0xFFFFFFFFL) ? jump : fall;
        case IR.ijump.A : return ((long)con & 0xFFFFFFFFL) >  ((long)@3.getValue() & 0xFFFFFFFFL) ? jump : fall;
        case IR.ijump.BE: return ((long)con & 0xFFFFFFFFL) <= ((long)@3.getValue() & 0xFFFFFFFFL) ? jump : fall;
        }
      }
      | IR.ISWITCH(IR.IUSE) = {
        int label = @1.getTarget(con);
        for (Iterator i = @1.ownerStmt().ownerBB().outEdges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          if (label == -1) {
            if (edge.type() == IRControlEdge.FALL)
              return edge;
          } else {
            if (edge.type() == IRControlEdge.SWITCH && edge.value() == con)
              return edge;
          }
        }
        return null;
      }
      | default = { return null; }
      ;

    public boolean def(int reg)
      : IR.IDEFINE(def) = { return @1.getReg() == reg; }
      | IR.ADEFINE(def) = { return @1.getReg() == reg; }
      | default = { return false; }
      ;

    public int cpy()
      : IR.IDEFINE(IR.IUSE) = { return @2.getReg(); }
      | IR.ADEFINE(IR.AUSE) = { return @2.getReg(); }
      | default = { return -1; }
      ;

    public int con()
      : IR.ADEFINE(IR.GETCLASS(con)) = { return 1; }
      | IR.ADEFINE(IR.MLOOKUP(con)) = { return 1; }
      | IR.ADEFINE(IR.ANULL) = { return 0; }
      | IR.ADEFINE(IR.ACLASS) = { return 1; }
      | IR.ADEFINE(IR.ASTRING) = { return 1; }
      | IR.IDEFINE(IR.ICONST) = { return @2.getValue(); }
      | default = { return 0xBEBEBABA; }
      ;

  }

}

