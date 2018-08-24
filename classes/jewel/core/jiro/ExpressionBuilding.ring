/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.beg.TreeNode.LeafRef;
import jewel.core.jiro.beg.TreeNode.LeftRef;
import jewel.core.jiro.beg.TreeNode.MiddleRef;
import jewel.core.jiro.beg.TreeNode.RightRef;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.optimize.Optimization;

import java.util.BitSet;
import java.util.Iterator;

public final class ExpressionBuilding implements Optimization {

  public ExpressionBuilding() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (cfg.isSSA()) {

      cfg.invalidate();

      int maxReg = cfg.regMax();

      int[] uses = new int[maxReg];
      IRStatement[] defines = new IRStatement[maxReg];
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.collect(uses, defines);
      }

      for (;;) {

        boolean build = false;
        for (Statement stmt = cfg.firstStmt(); stmt != null; stmt = stmt.nextStmt()) {
          Matcher matcher = new Matcher((IRStatement)stmt);
          build |= matcher.build(uses, defines);
        }
        if (!build)
          break;

        changed = true;

      }

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void collect(int[] uses, IRStatement[] defines)
      : IR.IUSE = { uses[@1.getReg()]++; }
      | IR.LUSE = { uses[@1.getReg()]++; }
      | IR.FUSE = { uses[@1.getReg()]++; }
      | IR.DUSE = { uses[@1.getReg()]++; }
      | IR.AUSE = { uses[@1.getReg()]++; }
      | IR.IDEFINE(collect) = { @2(uses, defines); defines[@1.getReg()] = @1.ownerStmt(); }
      | IR.LDEFINE(collect) = { @2(uses, defines); defines[@1.getReg()] = @1.ownerStmt(); }
      | IR.FDEFINE(collect) = { @2(uses, defines); defines[@1.getReg()] = @1.ownerStmt(); }
      | IR.DDEFINE(collect) = { @2(uses, defines); defines[@1.getReg()] = @1.ownerStmt(); }
      | IR.ADEFINE(collect) = { @2(uses, defines); defines[@1.getReg()] = @1.ownerStmt(); }
      | IR.IPHI = { for (Iterator i = @1.regs(); i.hasNext(); ) uses[((Integer)i.next()).intValue()]++; }
      | IR.LPHI = { for (Iterator i = @1.regs(); i.hasNext(); ) uses[((Integer)i.next()).intValue()]++; }
      | IR.FPHI = { for (Iterator i = @1.regs(); i.hasNext(); ) uses[((Integer)i.next()).intValue()]++; }
      | IR.DPHI = { for (Iterator i = @1.regs(); i.hasNext(); ) uses[((Integer)i.next()).intValue()]++; }
      | IR.APHI = { for (Iterator i = @1.regs(); i.hasNext(); ) uses[((Integer)i.next()).intValue()]++; }
      | default = {
        if (  left$ != null)   left$.collect(uses, defines);
        if (middle$ != null) middle$.collect(uses, defines);
        if ( right$ != null)  right$.collect(uses, defines);
      }
      ;

    public boolean build(int[] uses, IRStatement[] defines) {
      return build(((IR.snode)node$).ownerStmt(), null, uses, defines);
    }

    private boolean build(IRStatement ustmt, LeafRef ref, int[] uses, IRStatement[] defines)
      : IR.IUSE = { return build(ustmt, ref, uses, defines, @1.getReg()); }
      | IR.LUSE = { return build(ustmt, ref, uses, defines, @1.getReg()); }
      | IR.FUSE = { return build(ustmt, ref, uses, defines, @1.getReg()); }
      | IR.DUSE = { return build(ustmt, ref, uses, defines, @1.getReg()); }
      | IR.AUSE = { return build(ustmt, ref, uses, defines, @1.getReg()); }
      | default = {
        boolean build = false;
        if (  left$ != null) build |=   left$.build(ustmt, new LeftRef(@1), uses, defines);
        if (middle$ != null) build |= middle$.build(ustmt, new MiddleRef(@1), uses, defines);
        if ( right$ != null) build |=  right$.build(ustmt, new RightRef(@1), uses, defines);
        return build;
      }
      ;

    private static boolean build(IRStatement ustmt, LeafRef ref, int[] uses, IRStatement[] defines, int reg) {
      IRStatement dstmt = defines[reg];
      if (dstmt != null && uses[reg] == 1)
        if (safety(ustmt, dstmt)) {
          dstmt.ownerBB().removeStmt(dstmt);
          TreeNode define = dstmt.snode();
          ref.set(define.left());
          uses[reg] = 0;
          defines[reg] = null;
          return true;
        }
      return false;
    }

    private static boolean safety(IRStatement ustmt, IRStatement dstmt) {
      Matcher matcher = new Matcher(dstmt);
      if (matcher.sensitive()) {
        if (ustmt.ownerBB() != dstmt.ownerBB())
          return false;
        for (Statement stmt = dstmt.next(); stmt != ustmt; stmt = stmt.next()) {
          matcher = new Matcher((IRStatement)stmt);
          if (matcher.prohibits())
            return false;
        }
      }
      return true;
    }

    private boolean sensitive()
      : IR.BLOAD(sensitive) = { return true; }
      | IR.SLOAD(sensitive) = { return true; }
      | IR.ILOAD(sensitive) = { return true; }
      | IR.LLOAD(sensitive) = { return true; }
      | IR.FLOAD(sensitive) = { return true; }
      | IR.DLOAD(sensitive) = { return true; }
      | IR.ALOAD(sensitive) = { return true; }
      | IR.BALOAD(sensitive,sensitive) = { return true; }
      | IR.SALOAD(sensitive,sensitive) = { return true; }
      | IR.IALOAD(sensitive,sensitive) = { return true; }
      | IR.LALOAD(sensitive,sensitive) = { return true; }
      | IR.FALOAD(sensitive,sensitive) = { return true; }
      | IR.DALOAD(sensitive,sensitive) = { return true; }
      | IR.AALOAD(sensitive,sensitive) = { return true; }
      | IR.ISLOCKED(sensitive) = { return true; }
      | default = {
        boolean sensitive = false;
        if (  left$ != null) sensitive |=   left$.sensitive();
        if (middle$ != null) sensitive |= middle$.sensitive();
        if ( right$ != null) sensitive |=  right$.sensitive();
        return sensitive;
      }
      ;

    private boolean prohibits()
      : IR.IPASS(prohibits) = { return true; }
      | IR.LPASS(prohibits) = { return true; }
      | IR.FPASS(prohibits) = { return true; }
      | IR.DPASS(prohibits) = { return true; }
      | IR.APASS(prohibits) = { return true; }
      | IR.CALL(prohibits) = { return true; }
      | IR.CALLX(prohibits) = { return true; }
      | IR.NCALL = { return true; }
      | IR.NCALLX = { return true; }
      | IR.BSTORE(prohibits,prohibits) = { return true; }
      | IR.SSTORE(prohibits,prohibits) = { return true; }
      | IR.ISTORE(prohibits,prohibits) = { return true; }
      | IR.LSTORE(prohibits,prohibits) = { return true; }
      | IR.FSTORE(prohibits,prohibits) = { return true; }
      | IR.DSTORE(prohibits,prohibits) = { return true; }
      | IR.ASTORE(prohibits,prohibits) = { return true; }
      | IR.BASTORE(prohibits,prohibits,prohibits) = { return true; }
      | IR.SASTORE(prohibits,prohibits,prohibits) = { return true; }
      | IR.IASTORE(prohibits,prohibits,prohibits) = { return true; }
      | IR.LASTORE(prohibits,prohibits,prohibits) = { return true; }
      | IR.FASTORE(prohibits,prohibits,prohibits) = { return true; }
      | IR.DASTORE(prohibits,prohibits,prohibits) = { return true; }
      | IR.AASTORE(prohibits,prohibits,prohibits) = { return true; }
      | IR.INIT(prohibits) = { return true; }
      | IR.INITX(prohibits) = { return true; }
      | IR.LOCK(prohibits) = { return true; }
      | IR.LOCKX(prohibits) = { return true; }
      | IR.UNLOCK(prohibits) = { return true; }
      | default = { return false; }
      ;

  }

}

