/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.optimize.Optimization;

import java.util.Iterator;

public final class ExpressionBreaking implements Optimization {

  public ExpressionBreaking() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;
    cfg.invalidate();

    boolean ispointLast = true;
    Statement pointStmt = null;
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher matcher = new Matcher(stmt);
      if (ispointLast)
        pointStmt = stmt;
      ispointLast = matcher.ispoint();
      if (ispointLast)
        pointStmt = stmt;
      changed |= matcher.breakit(pointStmt);
    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public boolean ispoint()
      : IR.IPASS(ispoint) = { return false; }
      | IR.LPASS(ispoint) = { return false; }
      | IR.FPASS(ispoint) = { return false; }
      | IR.DPASS(ispoint) = { return false; }
      | IR.APASS(ispoint) = { return false; }
      | IR.CALL(ispoint) = { return false; }
      | IR.CALLX(ispoint) = { return false; }
      | default = { return true; }
      ;

    public boolean breakit(Statement pointStmt)
      : IR.IPASS(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.LPASS(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.FPASS(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.DPASS(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.APASS(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.CALL(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.CALLX(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.AJUMP(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.AJUMP(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.IJUMP(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.IJUMP(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.ISWITCH(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.IRETURN(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.LRETURN(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.FRETURN(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.DRETURN(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.ARETURN(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.IDEFINE(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.LDEFINE(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.FDEFINE(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.DDEFINE(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.ADEFINE(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.BSTORE(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.BSTORE(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.SSTORE(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.SSTORE(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.ISTORE(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.ISTORE(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.LSTORE(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.LSTORE(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.FSTORE(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.FSTORE(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.DSTORE(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.DSTORE(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.ASTORE(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.ASTORE(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.BASTORE(exp,any,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.BASTORE(any,exp,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.BASTORE(any,any,exp) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.SASTORE(exp,any,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.SASTORE(any,exp,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.SASTORE(any,any,exp) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.IASTORE(exp,any,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.IASTORE(any,exp,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.IASTORE(any,any,exp) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.LASTORE(exp,any,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.LASTORE(any,exp,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.LASTORE(any,any,exp) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.FASTORE(exp,any,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.FASTORE(any,exp,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.FASTORE(any,any,exp) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.DASTORE(exp,any,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.DASTORE(any,exp,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.DASTORE(any,any,exp) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.AASTORE(exp,any,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.AASTORE(any,exp,any) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.AASTORE(any,any,exp) = { @1.setLeft(@2(pointStmt)); @1.setMiddle(@3(pointStmt)); @1.setRight(@4(pointStmt)); return true; }
      | IR.INIT(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.INITX(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.NEWINSTANCE(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.NEWINSTANCEX(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.NEWARRAY(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.NEWARRAY(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.NEWARRAYX(exp,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.NEWARRAYX(any,exp) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return true; }
      | IR.LOCK(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.LOCKX(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | IR.UNLOCK(exp) = { @1.setLeft(@2(pointStmt)); return true; }
      | default = { return false; }
      ;

    private TreeNode any(Statement pointStmt)
      : exp = { return @1(pointStmt); }
      | nonexp = { return @1(); }
      ;

    private TreeNode exp(Statement pointStmt)
      : IR.GETCLASS(any) = { @1.setLeft(@2(pointStmt)); return abreak(pointStmt, @1); }
      | IR.ALOAD(any) = { @1.setLeft(@2(pointStmt)); return abreak(pointStmt, @1); }
      | IR.AALOAD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return abreak(pointStmt, @1); }
      | IR.MLOOKUP(IR.AUSE) = { return abreak(pointStmt, @1); }
      | IR.MLOOKUP(exp) = { @1.setLeft(@2(pointStmt)); return abreak(pointStmt, @1); }
      | IR.IMLOOKUP(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return abreak(pointStmt, @1); }
      | IR.I2B(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.I2C(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.I2S(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.L2I(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.F2I(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.D2I(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.IADD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.ISUB(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.IMUL(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.IDIV(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.IREM(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.INEG(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.ISHL(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.ISHR(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.IUSHR(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.IAND(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.IOR(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.IXOR(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.LCMP(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.FCMPG(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.FCMPL(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.DCMPG(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.DCMPL(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.LENGTH(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.BLOAD(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.SLOAD(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.ILOAD(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.BALOAD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.SALOAD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.IALOAD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.ISLOCKED(any) = { @1.setLeft(@2(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.SUBTYPEOF(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.COMPTYPEOF(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return ibreak(pointStmt, @1); }
      | IR.I2L(any) = { @1.setLeft(@2(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.F2L(any) = { @1.setLeft(@2(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.D2L(any) = { @1.setLeft(@2(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LADD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LSUB(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LMUL(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LDIV(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LREM(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LNEG(any) = { @1.setLeft(@2(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LSHL(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LSHR(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LUSHR(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LAND(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LOR(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LXOR(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LLOAD(any) = { @1.setLeft(@2(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.LALOAD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return lbreak(pointStmt, @1); }
      | IR.I2F(any) = { @1.setLeft(@2(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.L2F(any) = { @1.setLeft(@2(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.D2F(any) = { @1.setLeft(@2(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.FSTRICT(any) = { @1.setLeft(@2(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.FADD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.FSUB(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.FMUL(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.FDIV(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.FREM(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.FNEG(any) = { @1.setLeft(@2(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.FLOAD(any) = { @1.setLeft(@2(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.FALOAD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return fbreak(pointStmt, @1); }
      | IR.I2D(any) = { @1.setLeft(@2(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.L2D(any) = { @1.setLeft(@2(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.F2D(any) = { @1.setLeft(@2(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.DSTRICT(any) = { @1.setLeft(@2(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.DADD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.DSUB(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.DMUL(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.DDIV(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.DREM(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.DNEG(any) = { @1.setLeft(@2(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.DLOAD(any) = { @1.setLeft(@2(pointStmt)); return dbreak(pointStmt, @1); }
      | IR.DALOAD(any,any) = { @1.setLeft(@2(pointStmt)); @1.setRight(@3(pointStmt)); return dbreak(pointStmt, @1); }
      ;

    private TreeNode nonexp()
      : IR.MLOOKUP(IR.ACLASS) = { return @1; }
      | IR.AUSE = { return @1; }
      | IR.ANULL = { return @1; }
      | IR.ACLASS = { return @1; }
      | IR.ASTRING = { return @1; }
      | IR.IUSE = { return @1; }
      | IR.ICONST = { return @1; }
      | IR.LUSE = { return @1; }
      | IR.LCONST = { return @1; }
      | IR.FUSE = { return @1; }
      | IR.FCONST = { return @1; }
      | IR.DUSE = { return @1; }
      | IR.DCONST = { return @1; }
      ;

    private static final IR.inode ibreak(Statement pointStmt, IR.inode iexp) {
      BasicBlock ownerBB = pointStmt.ownerBB();
      IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
      int ireg = cfg.iregSeq();
      Statement stmt = new IRStatement(new IR.idefine(ireg, iexp));
      ownerBB.insertStmtBeforeStmt(stmt, pointStmt);
      return new IR.iuse(ireg);
    }

    private static final IR.lnode lbreak(Statement pointStmt, IR.lnode lexp) {
      BasicBlock ownerBB = pointStmt.ownerBB();
      IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
      int lreg = cfg.lregSeq();
      Statement stmt = new IRStatement(new IR.ldefine(lreg, lexp));
      ownerBB.insertStmtBeforeStmt(stmt, pointStmt);
      return new IR.luse(lreg);
    }

    private static final IR.fnode fbreak(Statement pointStmt, IR.fnode fexp) {
      BasicBlock ownerBB = pointStmt.ownerBB();
      IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
      int freg = cfg.fregSeq();
      Statement stmt = new IRStatement(new IR.fdefine(freg, fexp));
      ownerBB.insertStmtBeforeStmt(stmt, pointStmt);
      return new IR.fuse(freg);
    }

    private static final IR.dnode dbreak(Statement pointStmt, IR.dnode dexp) {
      BasicBlock ownerBB = pointStmt.ownerBB();
      IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
      int dreg = cfg.dregSeq();
      Statement stmt = new IRStatement(new IR.ddefine(dreg, dexp));
      ownerBB.insertStmtBeforeStmt(stmt, pointStmt);
      return new IR.duse(dreg);
    }

    private static final IR.anode abreak(Statement pointStmt, IR.anode aexp) {
      BasicBlock ownerBB = pointStmt.ownerBB();
      IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
      int areg = cfg.aregSeq();
      Statement stmt = new IRStatement(new IR.adefine(areg, aexp));
      ownerBB.insertStmtBeforeStmt(stmt, pointStmt);
      return new IR.ause(areg);
    }

  }

}

