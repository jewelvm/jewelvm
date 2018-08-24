/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.jiro.IR;

import java.util.Iterator;

public final class CFGBuilder {

  private boolean start = true;
  private IRCFG cfg = new IRCFG();
  private IRBasicBlock[] nos = new IRBasicBlock[10];

  public CFGBuilder() { }

  private void append(IR.snode snode) {

    if (cfg == null)
      throw new IllegalStateException("CFG already builded");

    int op = snode.op();

    if (op == IR.LABEL)
      start = true;

    if (start)
      cfg.addNode(new IRBasicBlock());

    IRBasicBlock currentBB = (IRBasicBlock)cfg.bottomBB();

    currentBB.appendStmt(new IRStatement(snode));

    if (op == IR.LABEL) {
      IR.label label = (IR.label)snode;
      if (label.getValue() >= nos.length) {
        IRBasicBlock[] tmp = nos;
        nos = new IRBasicBlock[label.getValue()+10];
        System.arraycopy(tmp, 0, nos, 0, tmp.length);
      }
      if (nos[label.getValue()] != null)
        throw new IllegalArgumentException("Duplicate label no definition");
      nos[label.getValue()] = currentBB;
    }

    switch (op) {
    case IR.CALLX:
    case IR.NCALLX:
    case IR.JUMP:
    case IR.AJUMP:
    case IR.IJUMP:
    case IR.ISWITCH:
    case IR.ATHROW:
    case IR.IRETURN:
    case IR.LRETURN:
    case IR.FRETURN:
    case IR.DRETURN:
    case IR.ARETURN:
    case IR.VRETURN:
    case IR.INITX:
    case IR.NEWINSTANCEX:
    case IR.NEWARRAYX:
    case IR.LOCKX:
      start = true;
      break;
    default:
      start = false;
    }

  }

  private void appendFast(IR.snode snode) {
    if (start)
      cfg.addNode(new IRBasicBlock());
    IRStatement stmt = new IRStatement(snode);
    IRBasicBlock currentBB = (IRBasicBlock)cfg.bottomBB();
    currentBB.appendStmt(stmt);
    start = false;
  }

  private static int ireg(int reg) {
    return 5*reg+0;
  }

  private static int lreg(int reg) {
    return 5*reg+1;
  }

  private static int freg(int reg) {
    return 5*reg+2;
  }

  private static int dreg(int reg) {
    return 5*reg+3;
  }

  private static int areg(int reg) {
    return 5*reg+4;
  }

  /* generation commands */

  public void ncall(MethodInfo method, int line) {
    IR.ncall ncall = new IR.ncall(method.getOwner().getName(), method.getName(), method.getDescriptor());
    ncall.append(method.getOwner().getName(), method.getIndex(), line);
    appendFast(ncall);
  }

  public void ncallx(int label, MethodInfo method, int line) {
    IR.ncallx ncallx = new IR.ncallx(method.getOwner().getName(), method.getName(), method.getDescriptor(), label);
    ncallx.append(method.getOwner().getName(), method.getIndex(), line);
    append(ncallx);
  }

  public void scall(String clazz, int dindex) {
    appendFast(new IR.call(new IR.mlookup(new IR.aclass(clazz), dindex)));
  }

  public void scall(String clazz, int dindex, MethodInfo method, int line) {
    IR.call call = new IR.call(new IR.mlookup(new IR.aclass(clazz), dindex));
    call.append(method.getOwner().getName(), method.getIndex(), line);
    appendFast(call);
  }

  public void scallx(String clazz, int dindex, int label, MethodInfo method, int line) {
    IR.callx callx = new IR.callx(new IR.mlookup(new IR.aclass(clazz), dindex), label);
    callx.append(method.getOwner().getName(), method.getIndex(), line);
    append(callx);
  }

  public void vcall(int source, int dindex, MethodInfo method, int line) {
    IR.call call = new IR.call(new IR.mlookup(new IR.getclass(new IR.ause(areg(source))), dindex));
    call.append(method.getOwner().getName(), method.getIndex(), line);
    appendFast(call);
  }

  public void vcallx(int source, int dindex, int label, MethodInfo method, int line) {
    IR.callx callx = new IR.callx(new IR.mlookup(new IR.getclass(new IR.ause(areg(source))), dindex), label);
    callx.append(method.getOwner().getName(), method.getIndex(), line);
    append(callx);
  }

  public void icall(int source, MethodInfo method, int line) {
    IR.call call = new IR.call(new IR.ause(areg(source)));
    call.append(method.getOwner().getName(), method.getIndex(), line);
    appendFast(call);
  }

  public void icallx(int source, int label, MethodInfo method, int line) {
    IR.callx callx = new IR.callx(new IR.ause(areg(source)), label);
    callx.append(method.getOwner().getName(), method.getIndex(), line);
    append(callx);
  }

  public void iresult(int target) {
    appendFast(new IR.iresult(ireg(target)));
  }

  public void lresult(int target) {
    appendFast(new IR.lresult(lreg(target)));
  }

  public void fresult(int target) {
    appendFast(new IR.fresult(freg(target)));
  }

  public void dresult(int target) {
    appendFast(new IR.dresult(dreg(target)));
  }

  public void anull(int target) {
    appendFast(new IR.adefine(areg(target), new IR.anull()));
  }

  public void aclass(int target, String name) {
    appendFast(new IR.adefine(areg(target), new IR.aclass(name)));
  }

  public void astring(int target, String value) {
    appendFast(new IR.adefine(areg(target), new IR.astring(value)));
  }

  public void acopy(int target, int source) {
    appendFast(new IR.adefine(areg(target), new IR.ause(areg(source))));
  }

  public void agetclass(int target, int source) {
    appendFast(new IR.adefine(areg(target), new IR.getclass(new IR.ause(areg(source)))));
  }

  public void acatch(int target, String name) {
    appendFast(new IR.acatch(areg(target), name));
  }

  public void athrow(int source) {
    append(new IR.athrow(new IR.ause(areg(source))));
  }

  public void iconst(int target, int value) {
    appendFast(new IR.idefine(ireg(target), new IR.iconst(value)));
  }

  public void icopy(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.iuse(ireg(source))));
  }

  public void iinc(int target, int value) {
    appendFast(new IR.idefine(ireg(target), new IR.iadd(new IR.iuse(ireg(target)), new IR.iconst(value))));
  }

  public void issubtype(int target, int source, String name) {
    appendFast(new IR.idefine(ireg(target), new IR.subtypeof(new IR.getclass(new IR.ause(areg(source))), new IR.aclass(name))));
  }

  public void alength(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.length(new IR.ause(areg(source)))));
  }

  public void lconst(int target, long value) {
    appendFast(new IR.ldefine(lreg(target), new IR.lconst(value)));
  }

  public void lcopy(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.luse(lreg(source))));
  }

  public void fconst(int target, float value) {
    appendFast(new IR.fdefine(freg(target), new IR.fconst(value)));
  }

  public void fcopy(int target, int source) {
    appendFast(new IR.fdefine(freg(target), new IR.fuse(freg(source))));
  }

  public void fstrictcopy(int target, int source) {
    appendFast(new IR.fdefine(freg(target), new IR.fstrict(new IR.fuse(freg(source)))));
  }

  public void dconst(int target, double value) {
    appendFast(new IR.ddefine(dreg(target), new IR.dconst(value)));
  }

  public void dcopy(int target, int source) {
    appendFast(new IR.ddefine(dreg(target), new IR.duse(dreg(source))));
  }

  public void dstrictcopy(int target, int source) {
    appendFast(new IR.ddefine(dreg(target), new IR.dstrict(new IR.duse(dreg(source)))));
  }

  public void label(int label) {
    append(new IR.label(label));
  }

  public void jump(int label) {
    append(new IR.jump(label));
  }

  public void ajumpnull(int source, int label) {
    append(new IR.ajump(IR.ajump.EQ, new IR.ause(areg(source)), new IR.anull(), label));
  }

  public void ajumpnonnull(int source, int label) {
    append(new IR.ajump(IR.ajump.NE, new IR.ause(areg(source)), new IR.anull(), label));
  }

  public void ajumplocked(int source, int label) {
    append(new IR.ijump(IR.ijump.NE, new IR.islocked(new IR.ause(areg(source))), new IR.iconst(0), label));
  }

  public void ajumpnotsubtype(int source, String name, int label) {
    append(new IR.ijump(IR.ijump.EQ, new IR.subtypeof(new IR.getclass(new IR.ause(areg(source))), new IR.aclass(name)), new IR.iconst(0), label));
  }

  public void ajumpsubtype(int source, String name, int label) {
    append(new IR.ijump(IR.ijump.NE, new IR.subtypeof(new IR.getclass(new IR.ause(areg(source))), new IR.aclass(name)), new IR.iconst(0), label));
  }

  public void ajumpcomptype(int source1, int source2, int label) {
    append(new IR.ijump(IR.ijump.NE, new IR.comptypeof(new IR.getclass(new IR.ause(areg(source1))), new IR.getclass(new IR.ause(areg(source2)))), new IR.iconst(0), label));
  }

  public void ajumpeq(int source1, int source2, int label) {
    append(new IR.ajump(IR.ajump.EQ, new IR.ause(areg(source1)), new IR.ause(areg(source2)), label));
  }

  public void ajumpne(int source1, int source2, int label) {
    append(new IR.ajump(IR.ajump.NE, new IR.ause(areg(source1)), new IR.ause(areg(source2)), label));
  }

  public void ijumpzero(int source, int label) {
    append(new IR.ijump(IR.ajump.EQ, new IR.iuse(ireg(source)), new IR.iconst(0), label));
  }

  public void ijumpnonzero(int source, int label) {
    append(new IR.ijump(IR.ajump.NE, new IR.iuse(ireg(source)), new IR.iconst(0), label));
  }

  public void ijumplength(int source1, int source2, int label) {
    append(new IR.ijump(IR.ijump.B, new IR.iuse(ireg(source1)), new IR.length(new IR.ause(areg(source2))), label));
  }

  public void ijumpnegative(int source, int label) {
    append(new IR.ijump(IR.ijump.LT, new IR.iuse(ireg(source)), new IR.iconst(0), label));
  }

  public void ijumpnotnegative(int source, int label) {
    append(new IR.ijump(IR.ijump.GE, new IR.iuse(ireg(source)), new IR.iconst(0), label));
  }

  public void ijumppositive(int source, int label) {
    append(new IR.ijump(IR.ijump.GT, new IR.iuse(ireg(source)), new IR.iconst(0), label));
  }

  public void ijumpnotpositive(int source, int label) {
    append(new IR.ijump(IR.ijump.LE, new IR.iuse(ireg(source)), new IR.iconst(0), label));
  }

  public void ijumpeq(int source1, int source2, int label) {
    append(new IR.ijump(IR.ijump.EQ, new IR.iuse(ireg(source1)), new IR.iuse(ireg(source2)), label));
  }

  public void ijumpne(int source1, int source2, int label) {
    append(new IR.ijump(IR.ijump.NE, new IR.iuse(ireg(source1)), new IR.iuse(ireg(source2)), label));
  }

  public void ijumplt(int source1, int source2, int label) {
    append(new IR.ijump(IR.ijump.LT, new IR.iuse(ireg(source1)), new IR.iuse(ireg(source2)), label));
  }

  public void ijumpge(int source1, int source2, int label) {
    append(new IR.ijump(IR.ijump.GE, new IR.iuse(ireg(source1)), new IR.iuse(ireg(source2)), label));
  }

  public void ijumpgt(int source1, int source2, int label) {
    append(new IR.ijump(IR.ijump.GT, new IR.iuse(ireg(source1)), new IR.iuse(ireg(source2)), label));
  }

  public void ijumple(int source1, int source2, int label) {
    append(new IR.ijump(IR.ijump.LE, new IR.iuse(ireg(source1)), new IR.iuse(ireg(source2)), label));
  }

  public void lcmp(int target, int source1, int source2) {
    appendFast(new IR.idefine(ireg(target), new IR.lcmp(new IR.luse(lreg(source1)), new IR.luse(lreg(source2)))));
  }

  public void fcmpl(int target, int source1, int source2) {
    appendFast(new IR.idefine(ireg(target), new IR.fcmpl(new IR.fuse(freg(source1)), new IR.fuse(freg(source2)))));
  }

  public void fcmpg(int target, int source1, int source2) {
    appendFast(new IR.idefine(ireg(target), new IR.fcmpg(new IR.fuse(freg(source1)), new IR.fuse(freg(source2)))));
  }

  public void dcmpl(int target, int source1, int source2) {
    appendFast(new IR.idefine(ireg(target), new IR.dcmpl(new IR.duse(dreg(source1)), new IR.duse(dreg(source2)))));
  }

  public void dcmpg(int target, int source1, int source2) {
    appendFast(new IR.idefine(ireg(target), new IR.dcmpg(new IR.duse(dreg(source1)), new IR.duse(dreg(source2)))));
  }

  public void alock(int source) {
    appendFast(new IR.lock(new IR.ause(areg(source))));
    appendFast(new IR.readbarrier());
  }

  public void alockx(int source, int label) {
    append(new IR.lockx(new IR.ause(areg(source)), label));
    appendFast(new IR.readbarrier());
  }

  public void alock(int source, MethodInfo method, int line) {
    IR.lock lock = new IR.lock(new IR.ause(areg(source)));
    lock.append(method.getOwner().getName(), method.getIndex(), line);
    appendFast(lock);
    appendFast(new IR.readbarrier());
  }

  public void alockx(int source, int label, MethodInfo method, int line) {
    IR.lockx lockx = new IR.lockx(new IR.ause(areg(source)), label);
    lockx.append(method.getOwner().getName(), method.getIndex(), line);
    append(lockx);
    appendFast(new IR.readbarrier());
  }

  public void aunlock(int source) {
    appendFast(new IR.writebarrier());
    appendFast(new IR.unlock(new IR.ause(areg(source))));
  }

  public void iaload(int target, int source1, int source2) {
    appendFast(new IR.idefine(ireg(target), new IR.iaload(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)))));
  }

  public void laload(int target, int source1, int source2) {
    appendFast(new IR.ldefine(lreg(target), new IR.laload(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)))));
  }

  public void faload(int target, int source1, int source2) {
    appendFast(new IR.fdefine(freg(target), new IR.faload(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)))));
  }

  public void daload(int target, int source1, int source2) {
    appendFast(new IR.ddefine(dreg(target), new IR.daload(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)))));
  }

  public void aaload(int target, int source1, int source2) {
    appendFast(new IR.adefine(areg(target), new IR.aaload(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)))));
  }

  public void baload(int target, int source1, int source2) {
    appendFast(new IR.idefine(ireg(target), new IR.i2b(new IR.baload(new IR.ause(areg(source1)), new IR.iuse(ireg(source2))))));
  }

  public void caload(int target, int source1, int source2) {
    appendFast(new IR.idefine(ireg(target), new IR.i2c(new IR.saload(new IR.ause(areg(source1)), new IR.iuse(ireg(source2))))));
  }

  public void saload(int target, int source1, int source2) {
    appendFast(new IR.idefine(ireg(target), new IR.i2s(new IR.saload(new IR.ause(areg(source1)), new IR.iuse(ireg(source2))))));
  }

  public void iastore(int source1, int source2, int source3) {
    appendFast(new IR.iastore(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)), new IR.iuse(ireg(source3))));
  }

  public void lastore(int source1, int source2, int source3) {
    appendFast(new IR.lastore(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)), new IR.luse(lreg(source3))));
  }

  public void fastore(int source1, int source2, int source3) {
    appendFast(new IR.fastore(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)), new IR.fuse(freg(source3))));
  }

  public void dastore(int source1, int source2, int source3) {
    appendFast(new IR.dastore(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)), new IR.duse(dreg(source3))));
  }

  public void aastore(int source1, int source2, int source3) {
    appendFast(new IR.aastore(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)), new IR.ause(areg(source3))));
  }

  public void bastore(int source1, int source2, int source3) {
    appendFast(new IR.bastore(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)), new IR.iuse(ireg(source3))));
  }

  public void sastore(int source1, int source2, int source3) {
    appendFast(new IR.sastore(new IR.ause(areg(source1)), new IR.iuse(ireg(source2)), new IR.iuse(ireg(source3))));
  }

  public void ireceive(int target) {
    appendFast(new IR.ireceive(ireg(target)));
  }

  public void lreceive(int target) {
    appendFast(new IR.lreceive(lreg(target)));
  }

  public void freceive(int target) {
    appendFast(new IR.freceive(freg(target)));
  }

  public void dreceive(int target) {
    appendFast(new IR.dreceive(dreg(target)));
  }

  public void areceive(int target, String name) {
    appendFast(new IR.areceive(areg(target), name));
  }

  public void init(String name) {
    appendFast(new IR.init(new IR.aclass(name)));
  }

  public void init(String name, MethodInfo method, int line) {
    IR.init init = new IR.init(new IR.aclass(name));
    init.append(method.getOwner().getName(), method.getIndex(), line);
    appendFast(init);
  }

  public void initx(String name, int label, MethodInfo method, int line) {
    IR.initx initx = new IR.initx(new IR.aclass(name), label);
    initx.append(method.getOwner().getName(), method.getIndex(), line);
    append(initx);
  }

  public void newinstance(String name) {
    appendFast(new IR.newinstance(new IR.aclass(name)));
  }

  public void newinstance(String name, MethodInfo method, int line) {
    IR.newinstance newinstance = new IR.newinstance(new IR.aclass(name));
    newinstance.append(method.getOwner().getName(), method.getIndex(), line);
    appendFast(newinstance);
  }

  public void newinstancex(String name, int label, MethodInfo method, int line) {
    IR.newinstancex newinstancex = new IR.newinstancex(new IR.aclass(name), label);
    newinstancex.append(method.getOwner().getName(), method.getIndex(), line);
    append(newinstancex);
  }

  public void newarray(String name, int source, MethodInfo method, int line) {
    IR.newarray newarray = new IR.newarray(new IR.aclass(name), new IR.iuse(ireg(source)));
    newarray.append(method.getOwner().getName(), method.getIndex(), line);
    appendFast(newarray);
  }

  public void newarrayx(String name, int source, int label, MethodInfo method, int line) {
    IR.newarrayx newarrayx = new IR.newarrayx(new IR.aclass(name), new IR.iuse(ireg(source)), label);
    newarrayx.append(method.getOwner().getName(), method.getIndex(), line);
    append(newarrayx);
  }

  public void ipass(int source) {
    appendFast(new IR.ipass(new IR.iuse(ireg(source))));
  }

  public void lpass(int source) {
    appendFast(new IR.lpass(new IR.luse(lreg(source))));
  }

  public void fpass(int source) {
    appendFast(new IR.fpass(new IR.fstrict(new IR.fuse(freg(source)))));
  }

  public void dpass(int source) {
    appendFast(new IR.dpass(new IR.dstrict(new IR.duse(dreg(source)))));
  }

  public void apass(int source) {
    appendFast(new IR.apass(new IR.ause(areg(source))));
  }

  public void aresult(int target, String name) {
    appendFast(new IR.aresult(areg(target), name));
  }

  public void ireturn(int source) {
    append(new IR.ireturn(new IR.iuse(ireg(source))));
  }

  public void lreturn(int source) {
    append(new IR.lreturn(new IR.luse(lreg(source))));
  }

  public void freturn(int source) {
    append(new IR.freturn(new IR.fuse(freg(source))));
  }

  public void dreturn(int source) {
    append(new IR.dreturn(new IR.duse(dreg(source))));
  }

  public void areturn(int source) {
    append(new IR.areturn(new IR.ause(areg(source))));
  }

  public void vreturn() {
    append(new IR.vreturn());
  }

  public void iadd(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.iadd(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void ladd(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.ladd(new IR.luse(lreg(target)), new IR.luse(lreg(source)))));
  }

  public void fadd(int target, int source) {
    appendFast(new IR.fdefine(freg(target), new IR.fadd(new IR.fuse(freg(target)), new IR.fuse(freg(source)))));
  }

  public void dadd(int target, int source) {
    appendFast(new IR.ddefine(dreg(target), new IR.dadd(new IR.duse(dreg(target)), new IR.duse(dreg(source)))));
  }

  public void isub(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.isub(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void lsub(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.lsub(new IR.luse(lreg(target)), new IR.luse(lreg(source)))));
  }

  public void fsub(int target, int source) {
    appendFast(new IR.fdefine(freg(target), new IR.fsub(new IR.fuse(freg(target)), new IR.fuse(freg(source)))));
  }

  public void dsub(int target, int source) {
    appendFast(new IR.ddefine(dreg(target), new IR.dsub(new IR.duse(dreg(target)), new IR.duse(dreg(source)))));
  }

  public void imul(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.imul(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void lmul(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.lmul(new IR.luse(lreg(target)), new IR.luse(lreg(source)))));
  }

  public void fmul(int target, int source) {
    appendFast(new IR.fdefine(freg(target), new IR.fmul(new IR.fuse(freg(target)), new IR.fuse(freg(source)))));
  }

  public void dmul(int target, int source) {
    appendFast(new IR.ddefine(dreg(target), new IR.dmul(new IR.duse(dreg(target)), new IR.duse(dreg(source)))));
  }

  public void idiv(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.idiv(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void ldiv(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.ldiv(new IR.luse(lreg(target)), new IR.luse(lreg(source)))));
  }

  public void fdiv(int target, int source) {
    appendFast(new IR.fdefine(freg(target), new IR.fdiv(new IR.fuse(freg(target)), new IR.fuse(freg(source)))));
  }

  public void ddiv(int target, int source) {
    appendFast(new IR.ddefine(dreg(target), new IR.ddiv(new IR.duse(dreg(target)), new IR.duse(dreg(source)))));
  }

  public void irem(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.irem(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void lrem(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.lrem(new IR.luse(lreg(target)), new IR.luse(lreg(source)))));
  }

  public void frem(int target, int source) {
    appendFast(new IR.fdefine(freg(target), new IR.frem(new IR.fuse(freg(target)), new IR.fuse(freg(source)))));
  }

  public void drem(int target, int source) {
    appendFast(new IR.ddefine(dreg(target), new IR.drem(new IR.duse(dreg(target)), new IR.duse(dreg(source)))));
  }

  public void ineg(int target) {
    appendFast(new IR.idefine(ireg(target), new IR.ineg(new IR.iuse(ireg(target)))));
  }

  public void lneg(int target) {
    appendFast(new IR.ldefine(lreg(target), new IR.lneg(new IR.luse(lreg(target)))));
  }

  public void fneg(int target) {
    appendFast(new IR.fdefine(freg(target), new IR.fneg(new IR.fuse(freg(target)))));
  }

  public void dneg(int target) {
    appendFast(new IR.ddefine(dreg(target), new IR.dneg(new IR.duse(dreg(target)))));
  }

  public void ishl(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.ishl(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void lshl(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.lshl(new IR.luse(lreg(target)), new IR.iuse(ireg(source)))));
  }

  public void ishr(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.ishr(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void lshr(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.lshr(new IR.luse(lreg(target)), new IR.iuse(ireg(source)))));
  }

  public void iushr(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.iushr(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void lushr(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.lushr(new IR.luse(lreg(target)), new IR.iuse(ireg(source)))));
  }

  public void iand(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.iand(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void land(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.land(new IR.luse(lreg(target)), new IR.luse(lreg(source)))));
  }

  public void ior(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.ior(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void lor(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.lor(new IR.luse(lreg(target)), new IR.luse(lreg(source)))));
  }

  public void ixor(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.ixor(new IR.iuse(ireg(target)), new IR.iuse(ireg(source)))));
  }

  public void lxor(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.lxor(new IR.luse(lreg(target)), new IR.luse(lreg(source)))));
  }

  public void i2l(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.i2l(new IR.iuse(ireg(source)))));
  }

  public void i2f(int target, int source) {
    appendFast(new IR.fdefine(freg(target), new IR.i2f(new IR.iuse(ireg(source)))));
  }

  public void i2d(int target, int source) {
    appendFast(new IR.ddefine(dreg(target), new IR.i2d(new IR.iuse(ireg(source)))));
  }

  public void l2i(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.l2i(new IR.luse(lreg(source)))));
  }

  public void l2f(int target, int source) {
    appendFast(new IR.fdefine(freg(target), new IR.l2f(new IR.luse(lreg(source)))));
  }

  public void l2d(int target, int source) {
    appendFast(new IR.ddefine(dreg(target), new IR.l2d(new IR.luse(lreg(source)))));
  }

  public void f2i(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.f2i(new IR.fuse(freg(source)))));
  }

  public void f2l(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.f2l(new IR.fuse(freg(source)))));
  }

  public void f2d(int target, int source) {
    appendFast(new IR.ddefine(dreg(target), new IR.f2d(new IR.fuse(freg(source)))));
  }

  public void d2i(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.d2i(new IR.duse(dreg(source)))));
  }

  public void d2l(int target, int source) {
    appendFast(new IR.ldefine(lreg(target), new IR.d2l(new IR.duse(dreg(source)))));
  }

  public void d2f(int target, int source) {
    appendFast(new IR.fdefine(freg(target), new IR.d2f(new IR.duse(dreg(source)))));
  }

  public void i2b(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.i2b(new IR.iuse(ireg(source)))));
  }

  public void i2c(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.i2c(new IR.iuse(ireg(source)))));
  }

  public void i2s(int target, int source) {
    appendFast(new IR.idefine(ireg(target), new IR.i2s(new IR.iuse(ireg(source)))));
  }

  public void ljumpnonzero(int source, int label) {
    append(new IR.ijump(IR.ijump.NE, new IR.lcmp(new IR.luse(lreg(source)), new IR.lconst(0L)), new IR.iconst(0), label));
  }

  public void imlookup(int target, int source, String name, int dindex) {
    appendFast(new IR.adefine(areg(target), new IR.imlookup(new IR.getclass(new IR.ause(areg(source))), new IR.aclass(name), dindex)));
  }

  public void sbload(int target, String name, long offset, boolean volat) {
    appendFast(new IR.idefine(ireg(target), new IR.i2b(new IR.bload(new IR.aclass(name), offset, volat))));
  }

  public void scload(int target, String name, long offset, boolean volat) {
    appendFast(new IR.idefine(ireg(target), new IR.i2c(new IR.sload(new IR.aclass(name), offset, volat))));
  }

  public void ssload(int target, String name, long offset, boolean volat) {
    appendFast(new IR.idefine(ireg(target), new IR.i2s(new IR.sload(new IR.aclass(name), offset, volat))));
  }

  public void siload(int target, String name, long offset, boolean volat) {
    appendFast(new IR.idefine(ireg(target), new IR.iload(new IR.aclass(name), offset, volat)));
  }

  public void slload(int target, String name, long offset, boolean volat) {
    appendFast(new IR.ldefine(lreg(target), new IR.lload(new IR.aclass(name), offset, volat)));
  }

  public void sfload(int target, String name, long offset, boolean volat) {
    appendFast(new IR.fdefine(freg(target), new IR.fload(new IR.aclass(name), offset, volat)));
  }

  public void sdload(int target, String name, long offset, boolean volat) {
    appendFast(new IR.ddefine(dreg(target), new IR.dload(new IR.aclass(name), offset, volat)));
  }

  public void saload(int target, String name, long offset, boolean volat, String type) {
    appendFast(new IR.adefine(areg(target), new IR.aload(new IR.aclass(name), offset, volat, type)));
  }

  public void ibload(int target, int source, long offset, boolean volat) {
    appendFast(new IR.idefine(ireg(target), new IR.i2b(new IR.bload(new IR.ause(areg(source)), offset, volat))));
  }

  public void icload(int target, int source, long offset, boolean volat) {
    appendFast(new IR.idefine(ireg(target), new IR.i2c(new IR.sload(new IR.ause(areg(source)), offset, volat))));
  }

  public void isload(int target, int source, long offset, boolean volat) {
    appendFast(new IR.idefine(ireg(target), new IR.i2s(new IR.sload(new IR.ause(areg(source)), offset, volat))));
  }

  public void iiload(int target, int source, long offset, boolean volat) {
    appendFast(new IR.idefine(ireg(target), new IR.iload(new IR.ause(areg(source)), offset, volat)));
  }

  public void ilload(int target, int source, long offset, boolean volat) {
    appendFast(new IR.ldefine(lreg(target), new IR.lload(new IR.ause(areg(source)), offset, volat)));
  }

  public void ifload(int target, int source, long offset, boolean volat) {
    appendFast(new IR.fdefine(freg(target), new IR.fload(new IR.ause(areg(source)), offset, volat)));
  }

  public void idload(int target, int source, long offset, boolean volat) {
    appendFast(new IR.ddefine(dreg(target), new IR.dload(new IR.ause(areg(source)), offset, volat)));
  }

  public void iaload(int target, int source, long offset, boolean volat, String type) {
    appendFast(new IR.adefine(areg(target), new IR.aload(new IR.ause(areg(source)), offset, volat, type)));
  }

  public void sbstore(String name, long offset, boolean volat, int source) {
    appendFast(new IR.bstore(new IR.aclass(name), offset, volat, new IR.iuse(ireg(source))));
  }

  public void ssstore(String name, long offset, boolean volat, int source) {
    appendFast(new IR.sstore(new IR.aclass(name), offset, volat, new IR.iuse(ireg(source))));
  }

  public void sistore(String name, long offset, boolean volat, int source) {
    appendFast(new IR.istore(new IR.aclass(name), offset, volat, new IR.iuse(ireg(source))));
  }

  public void slstore(String name, long offset, boolean volat, int source) {
    appendFast(new IR.lstore(new IR.aclass(name), offset, volat, new IR.luse(lreg(source))));
  }

  public void sfstore(String name, long offset, boolean volat, int source) {
    appendFast(new IR.fstore(new IR.aclass(name), offset, volat, new IR.fuse(freg(source))));
  }

  public void sdstore(String name, long offset, boolean volat, int source) {
    appendFast(new IR.dstore(new IR.aclass(name), offset, volat, new IR.duse(dreg(source))));
  }

  public void sastore(String name, long offset, boolean volat, int source) {
    appendFast(new IR.astore(new IR.aclass(name), offset, volat, new IR.ause(areg(source))));
  }

  public void ibstore(int target, long offset, boolean volat, int source) {
    appendFast(new IR.bstore(new IR.ause(areg(target)), offset, volat, new IR.iuse(ireg(source))));
  }

  public void isstore(int target, long offset, boolean volat, int source) {
    appendFast(new IR.sstore(new IR.ause(areg(target)), offset, volat, new IR.iuse(ireg(source))));
  }

  public void iistore(int target, long offset, boolean volat, int source) {
    appendFast(new IR.istore(new IR.ause(areg(target)), offset, volat, new IR.iuse(ireg(source))));
  }

  public void ilstore(int target, long offset, boolean volat, int source) {
    appendFast(new IR.lstore(new IR.ause(areg(target)), offset, volat, new IR.luse(lreg(source))));
  }

  public void ifstore(int target, long offset, boolean volat, int source) {
    appendFast(new IR.fstore(new IR.ause(areg(target)), offset, volat, new IR.fuse(freg(source))));
  }

  public void idstore(int target, long offset, boolean volat, int source) {
    appendFast(new IR.dstore(new IR.ause(areg(target)), offset, volat, new IR.duse(dreg(source))));
  }

  public void iastore(int target, long offset, boolean volat, int source) {
    appendFast(new IR.astore(new IR.ause(areg(target)), offset, volat, new IR.ause(areg(source))));
  }

  public IR.iswitch iswitch(int source) {
    IR.iswitch iswitch = new IR.iswitch(new IR.iuse(ireg(source)));
    append(iswitch);
    return iswitch;
  }

  public IR.iswitch iswitch(int source, int capacity) {
    IR.iswitch iswitch = new IR.iswitch(new IR.iuse(ireg(source)), capacity);
    append(iswitch);
    return iswitch;
  }

  /* end generation commands */

  public IRCFG toCFG() {

    if (cfg == null)
      throw new IllegalStateException("CFG already builded");

    if (cfg.count() == 0)
      throw new IllegalStateException("No statements in CFG");

    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      IRBasicBlock currentBB = (IRBasicBlock)i.next();

      IRStatement stmt = (IRStatement)currentBB.trailer();
      IR.snode snode = stmt.snode();

      int op = snode.op();

      switch (op) {
      case IR.CALLX:
        IR.callx callx = (IR.callx)snode;
        cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.EXCEPT, nos[callx.getHandler()]));
        break;
      case IR.NCALLX:
        IR.ncallx ncallx = (IR.ncallx)snode;
        cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.EXCEPT, nos[ncallx.getHandler()]));
        break;
      case IR.JUMP:
        IR.jump jump = (IR.jump)snode;
        cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, nos[jump.getTarget()]));
        break;
      case IR.AJUMP:
        IR.ajump ajump = (IR.ajump)snode;
        cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, nos[ajump.getTarget()]));
        break;
      case IR.IJUMP:
        IR.ijump ijump = (IR.ijump)snode;
        cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, nos[ijump.getTarget()]));
        break;
      case IR.ISWITCH:
        IR.iswitch iswitch = (IR.iswitch)snode;
        for (Iterator j = iswitch.pairs(); j.hasNext(); ) {
          IR.iswitch.pair pair = (IR.iswitch.pair)j.next();
          cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.SWITCH, pair.getKey(), nos[pair.getTarget()]));
        }
        break;
      case IR.INITX:
        IR.initx initx = (IR.initx)snode;
        cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.EXCEPT, nos[initx.getHandler()]));
        break;
      case IR.NEWINSTANCEX:
        IR.newinstancex newinstancex = (IR.newinstancex)snode;
        cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.EXCEPT, nos[newinstancex.getHandler()]));
        break;
      case IR.NEWARRAYX:
        IR.newarrayx newarrayx = (IR.newarrayx)snode;
        cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.EXCEPT, nos[newarrayx.getHandler()]));
        break;
      case IR.LOCKX:
        IR.lockx lockx = (IR.lockx)snode;
        cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.EXCEPT, nos[lockx.getHandler()]));
        break;
      }

      switch (op) {
      case IR.JUMP:
      case IR.ATHROW:
      case IR.IRETURN:
      case IR.LRETURN:
      case IR.FRETURN:
      case IR.DRETURN:
      case IR.ARETURN:
      case IR.VRETURN:
        break;

      default:
        if (currentBB == cfg.bottomBB())
          throw new IllegalStateException("CFG last statement falls thru");
        cfg.addEdge(new IRControlEdge(currentBB, IRControlEdge.FALL, currentBB.downBB()));
      }

    }

    IRCFG buildedCFG = cfg;
    cfg = null;
    return buildedCFG;

  }

}

