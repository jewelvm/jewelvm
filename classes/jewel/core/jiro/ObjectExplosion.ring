/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.Context;
import jewel.core.ContextClassInfo;
import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.beg.TreeNode.LeafRef;
import jewel.core.jiro.beg.TreeNode.LeftRef;
import jewel.core.jiro.beg.TreeNode.MiddleRef;
import jewel.core.jiro.beg.TreeNode.RightRef;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public final class ObjectExplosion implements Optimization {

  public ObjectExplosion() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (!cfg.isSSA()) {

      cfg.invalidate();

      BitSet valid = new BitSet();
      BitSet invalid = new BitSet();
      IRStatement pstmt = null;
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        if (pstmt != null) {
          IR.snode snode = pstmt.snode();
          if (snode.op() == IR.NEWINSTANCE || snode.op() == IR.NEWINSTANCEX) {
            IR.aclass aclass = (IR.aclass)snode.left();
            Context context = Context.get();
            ContextClassInfo clazz = context.forName(aclass.getSymbolicType());
            if (clazz != null)
              if (!clazz.finalizes())
                matcher.valid(valid);
          }
        }
        matcher.invalid(invalid);
        pstmt = stmt;
      }

      valid.andNot(invalid);

      for (int length = valid.length(); length > 0; length = valid.length()) {
        int ref = length-1;
        valid.clear(ref);
        HashMap fields = new HashMap();
        for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
          IRStatement stmt = (IRStatement)i.next();
          Matcher matcher = new Matcher(stmt);
          matcher.modify(cfg, ref, fields, null);
        }
        for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
          IRStatement stmt = (IRStatement)i.next();
          IR.snode snode = stmt.snode();
          if (snode.op() == IR.ARESULT) {
            IR.aresult aresult = (IR.aresult)snode;
            if (aresult.getReg() == ref)
              for (Iterator j = fields.values().iterator(); j.hasNext(); ) {
                int reg = ((Integer)j.next()).intValue();
                IR.snode dnode;
                switch (reg % 5) {
                case 0: dnode = new IR.idefine(reg, new IR.iconst(0)); break;
                case 1: dnode = new IR.ldefine(reg, new IR.lconst(0L)); break;
                case 2: dnode = new IR.fdefine(reg, new IR.fconst(0.0F)); break;
                case 3: dnode = new IR.ddefine(reg, new IR.dconst(0.0)); break;
                case 4: dnode = new IR.adefine(reg, new IR.anull()); break;
                default: throw new InternalError();
                }
                stmt.ownerBB().insertStmtAfterStmt(new IRStatement(dnode), stmt);
              }
          }
        }
        changed = true;
        //System.err.println("*OBJECT EXPLOSION*");
        //try { System.in.read(); } catch (Exception e) { }
      }

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void valid(BitSet set)
      : IR.ARESULT = { set.set(@1.getReg()); }
      | default = { }
      ;

    public void invalid(BitSet set) {
      invalid(set, true);
    }

    private void invalid(BitSet set, boolean invalid)
      : IR.AUSE = { if (invalid) set.set(@1.getReg()); }
      | IR.BLOAD(invalid) = { @2(set, false); }
      | IR.SLOAD(invalid) = { @2(set, false); }
      | IR.ILOAD(invalid) = { @2(set, false); }
      | IR.LLOAD(invalid) = { @2(set, false); }
      | IR.FLOAD(invalid) = { @2(set, false); }
      | IR.DLOAD(invalid) = { @2(set, false); }
      | IR.ALOAD(invalid) = { @2(set, false); }
      | IR.BSTORE(invalid,invalid) = { @2(set, false); @3(set, true); }
      | IR.SSTORE(invalid,invalid) = { @2(set, false); @3(set, true); }
      | IR.ISTORE(invalid,invalid) = { @2(set, false); @3(set, true); }
      | IR.LSTORE(invalid,invalid) = { @2(set, false); @3(set, true); }
      | IR.FSTORE(invalid,invalid) = { @2(set, false); @3(set, true); }
      | IR.DSTORE(invalid,invalid) = { @2(set, false); @3(set, true); }
      | IR.ASTORE(invalid,invalid) = { @2(set, false); @3(set, true); }
      | IR.APHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          Integer intg = (Integer)i.next();
          set.set(intg.intValue());
        }
      }
      | default = {
        if (left$ != null) left$.invalid(set, true);
        if (middle$ != null) middle$.invalid(set, true);
        if (right$ != null) right$.invalid(set, true);
      }
      ;

    public void modify(IRCFG cfg, int ref, HashMap fields, LeafRef leaf)
      : IR.BLOAD(IR.AUSE) = {
        if (@2.getReg() == ref)
          leaf.set(new IR.iuse(ireg(cfg, @1.getOffset(), fields)));
      }
      | IR.SLOAD(IR.AUSE) = {
        if (@2.getReg() == ref)
          leaf.set(new IR.iuse(ireg(cfg, @1.getOffset(), fields)));
      }
      | IR.ILOAD(IR.AUSE) = {
        if (@2.getReg() == ref)
          leaf.set(new IR.iuse(ireg(cfg, @1.getOffset(), fields)));
      }
      | IR.LLOAD(IR.AUSE) = {
        if (@2.getReg() == ref)
          leaf.set(new IR.luse(lreg(cfg, @1.getOffset(), fields)));
      }
      | IR.FLOAD(IR.AUSE) = {
        if (@2.getReg() == ref)
          leaf.set(new IR.fuse(freg(cfg, @1.getOffset(), fields)));
      }
      | IR.DLOAD(IR.AUSE) = {
        if (@2.getReg() == ref)
          leaf.set(new IR.duse(dreg(cfg, @1.getOffset(), fields)));
      }
      | IR.ALOAD(IR.AUSE) = {
        if (@2.getReg() == ref)
          leaf.set(new IR.ause(areg(cfg, @1.getOffset(), fields)));
      }
      | IR.BSTORE(IR.AUSE,modify) = {
        @3(cfg, ref, fields, new RightRef(@1));
        if (@2.getReg() == ref)
          @1.ownerStmt().setNode(new IR.idefine(ireg(cfg, @1.getOffset(), fields), (IR.inode)@1.right()));
      }
      | IR.SSTORE(IR.AUSE,modify) = {
        @3(cfg, ref, fields, new RightRef(@1));
        if (@2.getReg() == ref)
          @1.ownerStmt().setNode(new IR.idefine(ireg(cfg, @1.getOffset(), fields), (IR.inode)@1.right()));
      }
      | IR.ISTORE(IR.AUSE,modify) = {
        @3(cfg, ref, fields, new RightRef(@1));
        if (@2.getReg() == ref)
          @1.ownerStmt().setNode(new IR.idefine(ireg(cfg, @1.getOffset(), fields), (IR.inode)@1.right()));
      }
      | IR.LSTORE(IR.AUSE,modify) = {
        @3(cfg, ref, fields, new RightRef(@1));
        if (@2.getReg() == ref)
          @1.ownerStmt().setNode(new IR.ldefine(lreg(cfg, @1.getOffset(), fields), (IR.lnode)@1.right()));
      }
      | IR.FSTORE(IR.AUSE,modify) = {
        @3(cfg, ref, fields, new RightRef(@1));
        if (@2.getReg() == ref)
          @1.ownerStmt().setNode(new IR.fdefine(freg(cfg, @1.getOffset(), fields), (IR.fnode)@1.right()));
      }
      | IR.DSTORE(IR.AUSE,modify) = {
        @3(cfg, ref, fields, new RightRef(@1));
        if (@2.getReg() == ref)
          @1.ownerStmt().setNode(new IR.ddefine(dreg(cfg, @1.getOffset(), fields), (IR.dnode)@1.right()));
      }
      | IR.ASTORE(IR.AUSE,modify) = {
        @3(cfg, ref, fields, new RightRef(@1));
        if (@2.getReg() == ref)
          @1.ownerStmt().setNode(new IR.adefine(areg(cfg, @1.getOffset(), fields), (IR.anode)@1.right()));
      }
      | default = {
        if (left$ != null) left$.modify(cfg, ref, fields, new LeftRef(@1));
        if (middle$ != null) middle$.modify(cfg, ref, fields, new MiddleRef(@1));
        if (right$ != null) right$.modify(cfg, ref, fields, new RightRef(@1));
      }
      ;

    private static int ireg(IRCFG cfg, long offset, HashMap fields) {
      Long field = new Long(offset);
      Integer reg = (Integer)fields.get(field);
      if (reg == null) {
        reg = new Integer(cfg.iregSeq());
        fields.put(field, reg);
      }
      return reg.intValue();
    }

    private static int lreg(IRCFG cfg, long offset, HashMap fields) {
      Long field = new Long(offset);
      Integer reg = (Integer)fields.get(field);
      if (reg == null) {
        reg = new Integer(cfg.lregSeq());
        fields.put(field, reg);
      }
      return reg.intValue();
    }

    private static int freg(IRCFG cfg, long offset, HashMap fields) {
      Long field = new Long(offset);
      Integer reg = (Integer)fields.get(field);
      if (reg == null) {
        reg = new Integer(cfg.fregSeq());
        fields.put(field, reg);
      }
      return reg.intValue();
    }

    private static int dreg(IRCFG cfg, long offset, HashMap fields) {
      Long field = new Long(offset);
      Integer reg = (Integer)fields.get(field);
      if (reg == null) {
        reg = new Integer(cfg.dregSeq());
        fields.put(field, reg);
      }
      return reg.intValue();
    }

    private static int areg(IRCFG cfg, long offset, HashMap fields) {
      Long field = new Long(offset);
      Integer reg = (Integer)fields.get(field);
      if (reg == null) {
        reg = new Integer(cfg.aregSeq());
        fields.put(field, reg);
      }
      return reg.intValue();
    }

  }

}

