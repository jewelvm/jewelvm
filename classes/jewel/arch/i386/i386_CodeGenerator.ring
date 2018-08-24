/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.Context;
import jewel.core.ContextClassInfo;
import jewel.core.bend.Addr;
import jewel.core.bend.Assembler;
import jewel.core.bend.CodeGenerator;
import jewel.core.bend.Binder;
import jewel.core.bend.Extern;
import jewel.core.bend.Imm;
import jewel.core.bend.Label;
import jewel.core.bend.MethodEntry;
import jewel.core.bend.Reg;
import jewel.core.bend.StringRef;
import jewel.core.bend.Symbol;
import jewel.core.bend.SymbolicType;
import jewel.core.bend.VirtReg;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.Syntax;
import jewel.core.jiro.IR;
import jewel.core.jiro.IRBinder;
import jewel.core.jiro.IRCFG;
import jewel.core.jiro.IRStatement;
import jewel.core.jiro.Measure;
import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

public final class i386_CodeGenerator extends CodeGenerator {

  public static final int TEXT_BASE = 0;

  private static final Extern _INIT_ = new Extern("_init_");
  private static final Extern _NEWINSTANCE_ = new Extern("_newinstance_");
  private static final Extern _NEWARRAY_ = new Extern("_newarray_");
  private static final Extern _ATHROW_ = new Extern("_athrow_");
  private static final Extern _LOCK_ = new Extern("_lock_");
  private static final Extern _UNLOCK_ = new Extern("_unlock_");
  private static final Extern _ISLOCKED_ = new Extern("_islocked_");
  private static final Extern _SUBTYPEOF_ = new Extern("_subtypeof_");
  private static final Extern _COMPTYPEOF_ = new Extern("_comptypeof_");
  private static final Extern _IMLOOKUP_ = new Extern("_imlookup_");
  private static final Extern _LDIV_ = new Extern("_ldiv_");
  private static final Extern _LREM_ = new Extern("_lrem_");
  private static final Extern _NCALLL_ = new Extern("_ncalll_");
  private static final Extern _NCALLZ_ = new Extern("_ncallz_");
  private static final Extern _NCALLB_ = new Extern("_ncallb_");
  private static final Extern _NCALLC_ = new Extern("_ncallc_");
  private static final Extern _NCALLS_ = new Extern("_ncalls_");
  private static final Extern _NCALLI_ = new Extern("_ncalli_");
  private static final Extern _NCALLJ_ = new Extern("_ncallj_");
  private static final Extern _NCALLF_ = new Extern("_ncallf_");
  private static final Extern _NCALLD_ = new Extern("_ncalld_");
  private static final Extern _NCALLV_ = new Extern("_ncallv_");

  private static int lo(long l) {
    return (int)l;
  }

  private static int hi(long l) {
    return (int)(l>>32);
  }

  private static long hilo(int hi, int lo) {
    return ((long)hi << 32)|((long)lo & 0xFFFFFFFFL);
  }

  private static Reg lo(Reg[] l) {
    return l[1];
  }

  private static Reg hi(Reg[] l) {
    return l[0];
  }

  private static Reg[] hilo(Reg hi, Reg lo) {
    return new Reg[]{ hi, lo };
  }

  private static int align(int value) {
    return ((value+3)/4)*4;
  }

  private static int dispatchBase() {
    Context context = Context.get();
    ContextClassInfo loadedClass = context.forName("java/lang/Class");
    return align(Measure.getScaled(loadedClass.getInstanceSize(), 4));
  }

  private static int arrayBase(String name) {
    Context context = Context.get();
    ContextClassInfo loadedClass = context.forName(name);
    return align(Measure.getScaled(loadedClass.getInstanceSize(), 4));
  }

  private static int getNativeIndex(String symbolicType, String name, String descriptor) {
    Context context = Context.get();
    ContextClassInfo loadedClass = context.forName(symbolicType);
    int index = 0;
    for (int i = 0; i < loadedClass.getMethodCount(); i++) {
      MethodInfo method = loadedClass.getMethod(i);
      if (method.isNative()) {
        if (method.getName().equals(name) && method.getDescriptor().equals(descriptor))
          break;
        index++;
      }
    }
    return index;
  }

  private final ArrayList handlers = new ArrayList();
  private final ArrayList inspectors = new ArrayList();
  private final ArrayList natives = new ArrayList();
  private final ArrayList switches = new ArrayList();
  private final HashMap irregmap = new HashMap();

  private transient int seqLabel;
  private transient int seqReg;

  private transient boolean use_eax;
  private transient boolean use_edx;

  public i386_CodeGenerator() { }

  public ArrayList getHandlers() { return handlers; }
  public ArrayList getInspectors() { return inspectors; }
  public ArrayList getNatives() { return natives; }
  public ArrayList getSwitches() { return switches; }

  public synchronized void emit(ControlFlowGraph cfg, Assembler as) {
    super.emit(cfg, as);
  }

  protected void prologue(Assembler _as) {
    i386_FlowAssembler as = (i386_FlowAssembler)_as;
    handlers.clear();
    inspectors.clear();
    natives.clear();
    switches.clear();
    irregmap.clear();

    seqLabel = 0;
    seqReg = as._VR0;

    use_eax = false;
    use_edx = false;
  }

  protected void epilogue(Assembler _as) {
    i386_FlowAssembler as = (i386_FlowAssembler)_as;
    as.label("Exit");
    as.label("USE_ESP");
    as.label("USE_EBP");
    if (use_eax)
      as.label("USE_EAX");
    if (use_edx)
      as.label("USE_EDX");
  }

  protected Binder newBinder() {
    return new IRBinder();
  }

  protected Matcher newMatcher(Statement stmt) {
    return new i386_Matcher((IRStatement)stmt);
  }

  private int staticBase(String symbolicType) {
    Context context = Context.get();
    ContextClassInfo loadedClass = context.forName(symbolicType);
    return dispatchBase()+4*(loadedClass.getOverridableDispatchEntryCount()+loadedClass.getNonOverridableDispatchEntryCount());
  }

  private int mtableOffset(int index) {
    return dispatchBase()+4*index;
  }

  private int addNative(String descriptor) {
    String[] parameters = Syntax.getParameterTypes(descriptor);
    StringBuffer sb = new StringBuffer(parameters.length);
    for (int i = 0; i < parameters.length; i++)
      sb.append(parameters[i].charAt(0));
    String s = sb.toString();
    int index = natives.indexOf(s);
    if (index == -1) {
      index = natives.size();
      natives.add(s);
    }
    return index;
  }

  private int nextLabel() {
    return seqLabel++;
  }

  private Reg nextReg() {
    return new VirtReg(seqReg++);
  }

  private Reg mapReg(int irreg) {
    Integer key = new Integer(irreg);
    Reg reg = (Reg)irregmap.get(key);
    if (reg == null) {
      reg = nextReg();
      ((VirtReg)reg).regBase = binder.regBase(irreg);
      if (irreg % 5 == 4)
        ((VirtReg)reg).fixed = true;
      irregmap.put(key, reg);
    }
    return reg;
  }

  private Reg[] mapReg2(int irreg) {
    Integer key = new Integer(irreg);
    Reg[] rp = (Reg[])irregmap.get(key);
    if (rp == null) {
      Reg lo = nextReg();
      Reg hi = nextReg();
      ((VirtReg)lo).regBase = binder.regBase(irreg);
      ((VirtReg)hi).regBase = binder.regBase(irreg)+4;
      rp = hilo(hi, lo);
      irregmap.put(key, rp);
    }
    return rp;
  }

  private final class i386_Matcher extends Matcher {

    public i386_Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void emit(Assembler as) {
      stmt((i386_FlowAssembler)as);
    }

    private void stmt(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      /* IRECEIVE */
      : IR.IRECEIVE { @@.cost = 1; } = {
        BitSet lives = binder.livesAt(@1.ownerStmt());
        if (lives.get(@1.getReg())) {
          Reg r0 = mapReg(@1.getReg());
          as.movl(as.addr(binder.regBase(@1.getReg()), as.EBP), r0);
        }
      }
      /* LRECEIVE */
      | IR.LRECEIVE { @@.cost = 2; } = {
        BitSet lives = binder.livesAt(@1.ownerStmt());
        if (lives.get(@1.getReg())) {
          Reg[] rp = mapReg2(@1.getReg());
          as.movl(as.addr(binder.regBase(@1.getReg()), as.EBP), lo(rp));
          as.movl(as.addr(binder.regBase(@1.getReg())+4, as.EBP), hi(rp));
        }
      }
      /* FRECEIVE */
      | IR.FRECEIVE { @@.cost = 0; } = {
      }
      /* DRECEIVE */
      | IR.DRECEIVE { @@.cost = 0; } = {
      }
      /* ARECEIVE */
      | IR.ARECEIVE { @@.cost = 1; } = {
        BitSet lives = binder.livesAt(@1.ownerStmt());
        if (lives.get(@1.getReg())) {
          Reg r0 = mapReg(@1.getReg());
          as.movl(as.addr(binder.regBase(@1.getReg()), as.EBP), r0);
        }
      }
      /* IPASS */
      | IR.IPASS(r32) { @@.cost = @2.cost+1; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
      }
      | IR.IPASS(m32) { @@.cost = @2.cost+1; } = {
        Addr mem = @2(as);
        as.pushl(mem);
      }
      | IR.IPASS(i32) { @@.cost = 1; } = {
        int imm = @2(as);
        as.pushl(imm);
      }
      /* LPASS */
      | IR.LPASS(rp64) { @@.cost = @2.cost+2; } = {
        Reg[] rp = @2(as);
        as.pushl(hi(rp));
        as.pushl(lo(rp));
      }
      | IR.LPASS(m64) { @@.cost = @2.cost+2; } = {
        Addr mem = @2(as);
        as.pushl(as.addr(4, mem));
        as.pushl(mem);
      }
      | IR.LPASS(i64) { @@.cost = 2; } = {
        long imm = @2(as);
        as.pushl(hi(imm));
        as.pushl(lo(imm));
      }
      /* FPASS */
      | IR.FPASS(fps) { @@.cost = @2.cost+2; } = {
        @2(as);
        as.subl(4, as.ESP);
        as.fstps(as.addr(as.ESP));
      }
      | IR.FPASS(m32) { @@.cost = @2.cost+1; } = {
        Addr mem = @2(as);
        as.pushl(mem);
      }
      | IR.FPASS(i32) { @@.cost = 1; } = {
        int imm = @2(as);
        as.pushl(imm);
      }
      /* DPASS */
      | IR.DPASS(fps) { @@.cost = @2.cost+2; } = {
        @2(as);
        as.subl(8, as.ESP);
        as.fstpl(as.addr(as.ESP));
      }
      | IR.DPASS(m64) { @@.cost = @2.cost+2; } = {
        Addr mem = @2(as);
        as.pushl(as.addr(4, mem));
        as.pushl(mem);
      }
      | IR.DPASS(i64) { @@.cost = 2; } = {
        long imm = @2(as);
        as.pushl(hi(imm));
        as.pushl(lo(imm));
      }
      /* APASS */
      | IR.APASS(r32) { @@.cost = @2.cost+1; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
      }
      | IR.APASS(m32) { @@.cost = @2.cost+1; } = {
        Addr mem = @2(as);
        as.pushl(mem);
      }
      | IR.APASS(i32) { @@.cost = 1; } = {
        int imm = @2(as);
        as.pushl(imm);
      }
      | IR.APASS(s32) { @@.cost = 1; } = {
        Imm imm = @2(as);
        as.pushl(imm);
      }
      /* IRESULT */
      | IR.IRESULT  { @@.cost = 1; } = {
        Reg r0 = mapReg(@1.getReg());
        as.movl(as.EAX, r0);
      }
      /* LRESULT */
      | IR.LRESULT  { @@.cost = 2; } = {
        Reg[] rp = mapReg2(@1.getReg());
        as.movl(as.EAX, lo(rp));
        as.movl(as.EDX, hi(rp));
      }
      /* FRESULT */
      | IR.FRESULT { @@.cost = 1; } = {
        as.fstps(as.addr(binder.regBase(@1.getReg()), as.EBP));
      }
      /* DRESULT */
      | IR.DRESULT { @@.cost = 1; } = {
        as.fstpl(as.addr(binder.regBase(@1.getReg()), as.EBP));
      }
      /* ARESULT */
      | IR.ARESULT { @@.cost = 1; } = {
        Reg r0 = mapReg(@1.getReg());
        as.movl(as.EAX, r0);
      }
      /* IDEFINE */
      | IR.IDEFINE(r32) { @@.cost = @2.cost+1; } = {
        Reg r0 = @2(as);
        Reg r1 = mapReg(@1.getReg());
        as.movl(r0, r1);
      }
      | IR.IDEFINE(m32) { @@.cost = @2.cost+1; } = {
        Addr mem = @2(as);
        Reg r0 = mapReg(@1.getReg());
        as.movl(mem, r0);
      }
      | IR.IDEFINE(i32) { @@.cost = 1; } = {
        int imm = @2(as);
        Reg r0 = mapReg(@1.getReg());
        as.movl(imm, r0);
      }
      | IR.IDEFINE(IR.IADD(IR.IUSE, r32)) [@1.getReg() == @3.getReg()] { @@.cost = @4.cost+1; } = {
        Reg r0 = @4(as);
        Reg r1 = mapReg(@1.getReg());
        as.addl(r0, r1);
      }
      | IR.IDEFINE(IR.IADD(r32, IR.IUSE)) [@1.getReg() == @4.getReg()] { @@.cost = @3.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = mapReg(@1.getReg());
        as.addl(r0, r1);
      }
      | IR.IDEFINE(IR.IADD(IR.IUSE, m32)) [@1.getReg() == @3.getReg()] { @@.cost = @4.cost+1; } = {
        Addr mem = @4(as);
        Reg r0 = mapReg(@1.getReg());
        as.addl(mem, r0);
      }
      | IR.IDEFINE(IR.IADD(m32, IR.IUSE)) [@1.getReg() == @4.getReg()] { @@.cost = @3.cost+1; } = {
        Addr mem = @3(as);
        Reg r0 = mapReg(@1.getReg());
        as.addl(mem, r0);
      }
      | IR.IDEFINE(IR.IADD(IR.IUSE, i32)) [@1.getReg() == @3.getReg()] { @@.cost = 1; } = {
        int imm = @4(as);
        Reg r0 = mapReg(@1.getReg());
        if (imm == 1)
          as.incl(r0);
        else
          as.addl(imm, r0);
      }
      /* LDEFINE */
      | IR.LDEFINE(rp64) { @@.cost = @2.cost+2; } = {
        Reg[] rp0 = @2(as);
        Reg[] rp1 = mapReg2(@1.getReg());
        as.movl(lo(rp0), lo(rp1));
        as.movl(hi(rp0), hi(rp1));
      }
      | IR.LDEFINE(m64) { @@.cost = @2.cost+3; } = {
        Addr mem = @2(as);
        Reg r0 = nextReg();
        Reg[] rp = mapReg2(@1.getReg());
        as.leal(mem, r0);
        as.movl(as.addr(r0), lo(rp));
        as.movl(as.addr(4, r0), hi(rp));
      }
      | IR.LDEFINE(i64) { @@.cost = 2; } = {
        long imm = @2(as);
        Reg[] rp = mapReg2(@1.getReg());
        as.movl(lo(imm), lo(rp));
        as.movl(hi(imm), hi(rp));
      }
      /* FDEFINE */
      | IR.FDEFINE(fps) { @@.cost = @2.cost+1; } = {
        @2(as);
        as.fstps(as.addr(binder.regBase(@1.getReg()), as.EBP));
      }
      | IR.FDEFINE(i32) { @@.cost = 1; } = {
        int imm = @2(as);
        as.movl(imm, as.addr(binder.regBase(@1.getReg()), as.EBP));
      }
      /* DDEFINE */
      | IR.DDEFINE(fps) { @@.cost = @2.cost+1; } = {
        @2(as);
        as.fstpl(as.addr(binder.regBase(@1.getReg()), as.EBP));
      }
      | IR.DDEFINE(i64) { @@.cost = 2; } = {
        long imm = @2(as);
        as.movl(lo(imm), as.addr(binder.regBase(@1.getReg()), as.EBP));
        as.movl(hi(imm), as.addr(binder.regBase(@1.getReg())+4, as.EBP));
      }
      /* ADEFINE */
      | IR.ADEFINE(r32) { @@.cost = @2.cost+1; } = {
        Reg r0 = @2(as);
        Reg r1 = mapReg(@1.getReg());
        as.movl(r0, r1);
      }
      | IR.ADEFINE(m32) { @@.cost = @2.cost+1; } = {
        Addr mem = @2(as);
        Reg r0 = mapReg(@1.getReg());
        as.movl(mem, r0);
      }
      | IR.ADEFINE(i32) { @@.cost = 1; } = {
        int imm = @2(as);
        Reg r0 = mapReg(@1.getReg());
        as.movl(imm, r0);
      }
      | IR.ADEFINE(s32) { @@.cost = 1; } = {
        Imm imm = @2(as);
        Reg r0 = mapReg(@1.getReg());
        as.movl(imm, r0);
      }
      /* BSTORE */
      | IR.BSTORE(cls,eax) { @@.cost = @3.cost+1; } = {
        @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movb(as.AL, as.addr(offset, mem));
      }
      | IR.BSTORE(cls,i32) { @@.cost = 1; } = {
        int imm = (byte)@3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movb(imm, as.addr(offset, mem));
      }
      | IR.BSTORE(r32,eax) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @2(as);
        @3(as);
        as.movb(as.AL, as.addr(Measure.getScaled(@1.getOffset(), 4), r0));
      }
      | IR.BSTORE(r32,i32) { @@.cost = @2.cost+1; } = {
        int imm = (byte)@3(as);
        Reg r0 = @2(as);
        as.movb(imm, as.addr(Measure.getScaled(@1.getOffset(), 4), r0));
      }
      /* SSTORE */
      | IR.SSTORE(cls,eax) { @@.cost = @3.cost+1; } = {
        @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movw(as.AX, as.addr(offset, mem));
      }
      | IR.SSTORE(cls,i32) { @@.cost = 1; } = {
        int imm = (short)@3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movw(imm, as.addr(offset, mem));
      }
      | IR.SSTORE(r32,eax) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @2(as);
        @3(as);
        as.movw(as.AX, as.addr(Measure.getScaled(@1.getOffset(), 4), r0));
      }
      | IR.SSTORE(r32,i32) { @@.cost = @2.cost+1; } = {
        int imm = (short)@3(as);
        Reg r0 = @2(as);
        as.movw(imm, as.addr(Measure.getScaled(@1.getOffset(), 4), r0));
      }
      /* ISTORE */
      | IR.ISTORE(cls,r32) { @@.cost = @3.cost+1; } = {
        Reg r0 = @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(r0, as.addr(offset, mem));
      }
      | IR.ISTORE(cls,i32) { @@.cost = 1; } = {
        int imm = @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(imm, as.addr(offset, mem));
      }
      | IR.ISTORE(r32,r32) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movl(r0, as.addr(Measure.getScaled(@1.getOffset(), 4), r1));
      }
      | IR.ISTORE(r32,i32) { @@.cost = @2.cost+1; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        as.movl(imm, as.addr(Measure.getScaled(@1.getOffset(), 4), r0));
      }
      /* LSTORE */
      | IR.LSTORE(cls,rp64) { @@.cost = @3.cost+2; } = {
        Reg[] rp = @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(lo(rp), as.addr(offset, mem));
        as.movl(hi(rp), as.addr(offset+4, mem));
      }
      | IR.LSTORE(cls,i64) { @@.cost = 2; } = {
        long imm = @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(lo(imm), as.addr(offset, mem));
        as.movl(hi(imm), as.addr(offset+4, mem));
      }
      | IR.LSTORE(r32,rp64) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg[] rp = @3(as);
        Reg r0 = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(lo(rp), as.addr(offset, r0));
        as.movl(hi(rp), as.addr(offset+4, r0));
      }
      | IR.LSTORE(r32,i64) { @@.cost = @2.cost+2; } = {
        long imm = @3(as);
        Reg r0 = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(lo(imm), as.addr(offset, r0));
        as.movl(hi(imm), as.addr(offset+4, r0));
      }
      /* FSTORE */
      | IR.FSTORE(cls,fps) { @@.cost = @3.cost+1; } = {
        @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.fstps(as.addr(offset, mem));
      }
      | IR.FSTORE(cls,i32) { @@.cost = 1; } = {
        int imm = @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(imm, as.addr(offset, mem));
      }
      | IR.FSTORE(r32,fps) { @@.cost = @3.cost+@2.cost+1; } = {
        @3(as);
        Reg r0 = @2(as);
        as.fstps(as.addr(Measure.getScaled(@1.getOffset(), 4), r0));
      }
      | IR.FSTORE(r32,i32) { @@.cost = @2.cost+1; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        as.movl(imm, as.addr(Measure.getScaled(@1.getOffset(), 4), r0));
      }
      /* DSTORE */
      | IR.DSTORE(cls,fps) { @@.cost = @3.cost+1; } = {
        @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.fstpl(as.addr(offset, mem));
      }
      | IR.DSTORE(cls,i64) { @@.cost = 2; } = {
        long imm = @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(lo(imm), as.addr(offset, mem));
        as.movl(hi(imm), as.addr(offset+4, mem));
      }
      | IR.DSTORE(r32,fps) { @@.cost = @3.cost+@2.cost+1; } = {
        @3(as);
        Reg r0 = @2(as);
        as.fstpl(as.addr(Measure.getScaled(@1.getOffset(), 4), r0));
      }
      | IR.DSTORE(r32,i64) { @@.cost = @2.cost+2; } = {
        long imm = @3(as);
        Reg r0 = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(lo(imm), as.addr(offset, r0));
        as.movl(hi(imm), as.addr(offset+4, r0));
      }
      /* ASTORE */
      | IR.ASTORE(cls,r32) { @@.cost = @3.cost+1; } = {
        Reg r0 = @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(r0, as.addr(offset, mem));
      }
      | IR.ASTORE(cls,i32) { @@.cost = 1; } = {
        int imm = @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(imm, as.addr(offset, mem));
      }
      | IR.ASTORE(cls,s32) { @@.cost = 1; } = {
        Imm imm = @3(as);
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        as.movl(imm, as.addr(offset, mem));
      }
      | IR.ASTORE(r32,r32) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movl(r0, as.addr(Measure.getScaled(@1.getOffset(), 4), r1));
      }
      | IR.ASTORE(r32,i32) { @@.cost = @2.cost+1; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        as.movl(imm, as.addr(Measure.getScaled(@1.getOffset(), 4), r0));
      }
      | IR.ASTORE(r32,s32) { @@.cost = @2.cost+1; } = {
        Imm imm = @3(as);
        Reg r0 = @2(as);
        as.movl(imm, as.addr(Measure.getScaled(@1.getOffset(), 4), r0));
      }
      /* BASTORE */
      | IR.BASTORE(r32,r32,eax) { @@.cost = @4.cost+@3.cost+@2.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        @4(as);
        as.movb(as.AL, as.addr(arrayBase("[B"), r1, r0));
      }
      | IR.BASTORE(r32,i32,eax) { @@.cost = @4.cost+@2.cost+1; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        @4(as);
        as.movb(as.AL, as.addr(arrayBase("[B")+imm, r0));
      }
      | IR.BASTORE(r32,r32,i32) { @@.cost = @3.cost+@2.cost+1; } = {
        int imm = (byte)@4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movb(imm, as.addr(arrayBase("[B"), r1, r0));
      }
      | IR.BASTORE(r32,i32,i32) { @@.cost = @2.cost+1; } = {
        int imm0 = (byte)@4(as);
        int imm1 = @3(as);
        Reg r0 = @2(as);
        as.movb(imm0, as.addr(arrayBase("[B")+imm1, r0));
      }
      /* SASTORE */
      | IR.SASTORE(r32,r32,eax) { @@.cost = @4.cost+@3.cost+@2.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        @4(as);
        as.movw(as.AX, as.addr(arrayBase("[S"), r1, r0, 2));
      }
      | IR.SASTORE(r32,i32,eax) { @@.cost = @4.cost+@2.cost+1; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        @4(as);
        as.movw(as.AX, as.addr(arrayBase("[S")+2*imm, r0));
      }
      | IR.SASTORE(r32,r32,i32) { @@.cost = @3.cost+@2.cost+1; } = {
        int imm = (short)@4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movw(imm, as.addr(arrayBase("[S"), r1, r0, 2));
      }
      | IR.SASTORE(r32,i32,i32) { @@.cost = @2.cost+1; } = {
        int imm0 = @4(as);
        int imm1 = @3(as);
        Reg r0 = @2(as);
        as.movw(imm0, as.addr(arrayBase("[S")+2*imm1, r0));
      }
      /* IASTORE */
      | IR.IASTORE(r32,r32,r32) { @@.cost = @4.cost+@3.cost+@2.cost+1; } = {
        Reg r0 = @4(as);
        Reg r1 = @3(as);
        Reg r2 = @2(as);
        as.movl(r0, as.addr(arrayBase("[I"), r2, r1, 4));
      }
      | IR.IASTORE(r32,i32,r32) { @@.cost = @4.cost+@2.cost+1; } = {
        Reg r0 = @4(as);
        int imm = @3(as);
        Reg r1 = @2(as);
        as.movl(r0, as.addr(arrayBase("[I")+4*imm, r1));
      }
      | IR.IASTORE(r32,r32,i32) { @@.cost = @3.cost+@2.cost+1; } = {
        int imm = @4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movl(imm, as.addr(arrayBase("[I"), r1, r0, 4));
      }
      | IR.IASTORE(r32,i32,i32) { @@.cost = @2.cost+1; } = {
        int imm0 = @4(as);
        int imm1 = @3(as);
        Reg r0 = @2(as);
        as.movl(imm0, as.addr(arrayBase("[I")+4*imm1, r0));
      }
      /* LASTORE */
      | IR.LASTORE(r32,r32,rp64) { @@.cost = @4.cost+@3.cost+@2.cost+2; } = {
        Reg[] rp = @4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movl(lo(rp), as.addr(arrayBase("[J"), r1, r0, 8));
        as.movl(hi(rp), as.addr(arrayBase("[J")+4, r1, r0, 8));
      }
      | IR.LASTORE(r32,i32,rp64) { @@.cost = @4.cost+@2.cost+2; } = {
        Reg[] rp = @4(as);
        int imm = @3(as);
        Reg r0 = @2(as);
        as.movl(lo(rp), as.addr(arrayBase("[J")+8*imm, r0));
        as.movl(hi(rp), as.addr(arrayBase("[J")+4+8*imm, r0));
      }
      | IR.LASTORE(r32,r32,i64) { @@.cost = @3.cost+@2.cost+2; } = {
        long imm = @4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movl(lo(imm), as.addr(arrayBase("[J"), r1, r0, 8));
        as.movl(hi(imm), as.addr(arrayBase("[J")+4, r1, r0, 8));
      }
      | IR.LASTORE(r32,i32,i64) { @@.cost = @2.cost+2; } = {
        long imm0 = @4(as);
        int imm1 = @3(as);
        Reg r0 = @2(as);
        as.movl(lo(imm0), as.addr(arrayBase("[J")+8*imm1, r0));
        as.movl(hi(imm0), as.addr(arrayBase("[J")+4+8*imm1, r0));
      }
      /* FASTORE */
      | IR.FASTORE(r32,r32,fps) { @@.cost = @4.cost+@3.cost+@2.cost+1; } = {
        @4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.fstps(as.addr(arrayBase("[F"), r1, r0, 4));
      }
      | IR.FASTORE(r32,i32,fps) { @@.cost = @4.cost+@2.cost+1; } = {
        @4(as);
        int imm = @3(as);
        Reg r0 = @2(as);
        as.fstps(as.addr(arrayBase("[F")+4*imm, r0));
      }
      | IR.FASTORE(r32,r32,i32) { @@.cost = @3.cost+@2.cost+1; } = {
        int imm = @4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movl(imm, as.addr(arrayBase("[F"), r1, r0, 4));
      }
      | IR.FASTORE(r32,i32,i32) { @@.cost = @2.cost+1; } = {
        int imm0 = @4(as);
        int imm1 = @3(as);
        Reg r0 = @2(as);
        as.movl(imm0, as.addr(arrayBase("[F")+4*imm1, r0));
      }
      /* DASTORE */
      | IR.DASTORE(r32,r32,fps) { @@.cost = @4.cost+@3.cost+@2.cost+1; } = {
        @4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.fstpl(as.addr(arrayBase("[D"), r1, r0, 8));
      }
      | IR.DASTORE(r32,i32,fps) { @@.cost = @4.cost+@2.cost+1; } = {
        @4(as);
        int imm = @3(as);
        Reg r0 = @2(as);
        as.fstpl(as.addr(arrayBase("[D")+8*imm, r0));
      }
      | IR.DASTORE(r32,r32,i64) { @@.cost = @3.cost+@2.cost+2; } = {
        long imm = @4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movl(lo(imm), as.addr(arrayBase("[D"), r1, r0, 8));
        as.movl(hi(imm), as.addr(arrayBase("[D")+4, r1, r0, 8));
      }
      | IR.DASTORE(r32,i32,i64) { @@.cost = @2.cost+2; } = {
        long imm0 = @4(as);
        int imm1 = @3(as);
        Reg r0 = @2(as);
        as.movl(lo(imm0), as.addr(arrayBase("[D")+8*imm1, r0));
        as.movl(hi(imm0), as.addr(arrayBase("[D")+4+8*imm1, r0));
      }
      /* AASTORE */
      | IR.AASTORE(r32,r32,r32) { @@.cost = @4.cost+@3.cost+@2.cost+1; } = {
        Reg r0 = @4(as);
        Reg r1 = @3(as);
        Reg r2 = @2(as);
        as.movl(r0, as.addr(arrayBase("[Ljava/lang/Object;"), r2, r1, 4));
      }
      | IR.AASTORE(r32,i32,r32) { @@.cost = @4.cost+@2.cost+1; } = {
        Reg r0 = @4(as);
        int imm = @3(as);
        Reg r1 = @2(as);
        as.movl(r0, as.addr(arrayBase("[Ljava/lang/Object;")+4*imm, r1));
      }
      | IR.AASTORE(r32,r32,i32) { @@.cost = @3.cost+@2.cost+1; } = {
        int imm = @4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movl(imm, as.addr(arrayBase("[Ljava/lang/Object;"), r1, r0, 4));
      }
      | IR.AASTORE(r32,r32,s32) { @@.cost = @3.cost+@2.cost+1; } = {
        Imm imm = @4(as);
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.movl(imm, as.addr(arrayBase("[Ljava/lang/Object;"), r1, r0, 4));
      }
      | IR.AASTORE(r32,i32,i32) { @@.cost = @2.cost+1; } = {
        int imm0 = @4(as);
        int imm1 = @3(as);
        Reg r1 = @2(as);
        as.movl(imm0, as.addr(arrayBase("[Ljava/lang/Object;")+4*imm1, r1));
      }
      | IR.AASTORE(r32,i32,s32) { @@.cost = @2.cost+1; } = {
        Imm imm0 = @4(as);
        int imm1 = @3(as);
        Reg r1 = @2(as);
        as.movl(imm0, as.addr(arrayBase("[Ljava/lang/Object;")+4*imm1, r1));
      }
      /* LABEL */
      | IR.LABEL { @@.cost = 0; } = {
        as.label("L"+@1.getValue());
      }
      /* JUMP */
      | IR.JUMP { @@.cost = 1; } = {
        as.jmp(as.imm(new Label("L"+@1.getTarget())));
        as.uncondXfer("L"+@1.getTarget());
      }
      /* AJUMP */
      | IR.AJUMP(r32,r32) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.cmpl(r0, r1);
        switch (@1.xop()) {
        case IR.ajump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ajump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      | IR.AJUMP(r32,m32) { @@.cost = @3.cost+@2.cost+2; } = {
        Addr mem = @3(as);
        Reg r0 = @2(as);
        as.cmpl(mem, r0);
        switch (@1.xop()) {
        case IR.ajump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ajump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      | IR.AJUMP(r32,i32) { @@.cost = @2.cost+2; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        as.cmpl(imm, r0);
        switch (@1.xop()) {
        case IR.ajump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ajump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      | IR.AJUMP(r32,s32) { @@.cost = @2.cost+2; } = {
        Imm imm = @3(as);
        Reg r0 = @2(as);
        as.cmpl(imm, r0);
        switch (@1.xop()) {
        case IR.ajump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ajump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      | IR.AJUMP(m32,r32) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg r0 = @3(as);
        Addr mem = @2(as);
        as.cmpl(r0, mem);
        switch (@1.xop()) {
        case IR.ajump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ajump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      | IR.AJUMP(m32,i32) { @@.cost = @2.cost+2; } = {
        int imm = @3(as);
        Addr mem = @2(as);
        as.cmpl(imm, mem);
        switch (@1.xop()) {
        case IR.ajump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ajump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      | IR.AJUMP(m32,s32) { @@.cost = @2.cost+2; } = {
        Imm imm = @3(as);
        Addr mem = @2(as);
        as.cmpl(imm, mem);
        switch (@1.xop()) {
        case IR.ajump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ajump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      /* IJUMP */
      | IR.IJUMP(r32,r32) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.cmpl(r0, r1);
        switch (@1.xop()) {
        case IR.ijump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.LT: as.jl(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.GE: as.jge(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.GT: as.jg(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.LE: as.jle(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.B: as.jb(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.AE: as.jae(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.A: as.ja(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.BE: as.jbe(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      | IR.IJUMP(r32,m32) { @@.cost = @3.cost+@2.cost+2; } = {
        Addr mem = @3(as);
        Reg r0 = @2(as);
        as.cmpl(mem, r0);
        switch (@1.xop()) {
        case IR.ijump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.LT: as.jl(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.GE: as.jge(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.GT: as.jg(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.LE: as.jle(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.B: as.jb(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.AE: as.jae(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.A: as.ja(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.BE: as.jbe(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      | IR.IJUMP(r32,i32) { @@.cost = @2.cost+2; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        as.cmpl(imm, r0);
        switch (@1.xop()) {
        case IR.ijump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.LT: as.jl(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.GE: as.jge(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.GT: as.jg(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.LE: as.jle(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.B: as.jb(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.AE: as.jae(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.A: as.ja(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.BE: as.jbe(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      | IR.IJUMP(m32,r32) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg r0 = @3(as);
        Addr mem = @2(as);
        as.cmpl(r0, mem);
        switch (@1.xop()) {
        case IR.ijump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.LT: as.jl(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.GE: as.jge(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.GT: as.jg(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.LE: as.jle(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.B: as.jb(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.AE: as.jae(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.A: as.ja(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.BE: as.jbe(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      | IR.IJUMP(m32,i32) { @@.cost = @2.cost+2; } = {
        int imm = @3(as);
        Addr mem = @2(as);
        as.cmpl(imm, mem);
        switch (@1.xop()) {
        case IR.ijump.EQ: as.je(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.NE: as.jne(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.LT: as.jl(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.GE: as.jge(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.GT: as.jg(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.LE: as.jle(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.B: as.jb(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.AE: as.jae(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.A: as.ja(as.imm(new Label("L"+@1.getTarget()))); break;
        case IR.ijump.BE: as.jbe(as.imm(new Label("L"+@1.getTarget()))); break;
        default: throw new Error("Unknown condition");
        }
        as.condXfer("L"+@1.getTarget());
      }
      /* ISWITCH */
      | IR.ISWITCH(rw32) { @@.cost = @2.cost+2*@1.pairCount(); } = {
        Reg r0 = @2(as);
        if (@1.tableSize() < 10 || @1.tableWaste() < 0.25F) {
          Iterator i = @1.pairs();
          if (i.hasNext()) {
            IR.iswitch.pair lpair = (IR.iswitch.pair)i.next();
            as.subl(lpair.getKey(), r0);
            as.cmpl(@1.tableSize(), r0);
            String label = "Local."+nextLabel();
            as.jae(as.imm(new Label(label)));
            as.condXfer(label);
            as.sall(2, r0);
            as.addl(as.imm(new Label("Switch."+switches.size())), r0);
            as.jmpl(as.addr(r0));
            ArrayList table = new ArrayList();
            table.add("L"+lpair.getTarget());
            as.condXfer("L"+lpair.getTarget());
            while (i.hasNext()) {
              IR.iswitch.pair pair = (IR.iswitch.pair)i.next();
              for (int j = lpair.getKey()+1; j < pair.getKey(); j++) {
                table.add(label);
                as.condXfer(label);
              }
              table.add("L"+pair.getTarget());
              if (i.hasNext())
                as.condXfer("L"+pair.getTarget());
              else
                as.uncondXfer("L"+pair.getTarget());
              lpair = pair;
            }
            switches.add(table);
            as.label(label);
          }
        } else
          for (Iterator i = @1.pairs(); i.hasNext(); ) {
            IR.iswitch.pair pair = (IR.iswitch.pair)i.next();
            as.cmpl(pair.getKey(), r0);
            as.je(as.imm(new Label("L"+pair.getTarget())));
            as.condXfer("L"+pair.getTarget());
          }
      }
      /* ACATCH */
      | IR.ACATCH { @@.cost = 1; } = {
        Reg r0 = mapReg(@1.getReg());
        as.movl(as.EAX, r0);
      }
      /* ATHROW */
      | IR.ATHROW(r32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.call(as.imm(_ATHROW_));
        as.terminator();
      }
      | IR.ATHROW(m32) { @@.cost = @2.cost+2; } = {
        Addr mem = @2(as);
        as.pushl(mem);
        as.call(as.imm(_ATHROW_));
        as.terminator();
      }
      /* IRETURN */
      | IR.IRETURN(eax) { @@.cost = @2.cost; } = {
        @2(as);
        use_eax = true;
        if (@1.next() != null) {
          as.jmp(as.imm(new Label("Exit")));
          as.uncondXfer("Exit");
        }
      }
      /* LRETURN */
      | IR.LRETURN(eaxedx) { @@.cost = @2.cost; } = {
        @2(as);
        use_eax = true;
        use_edx = true;
        if (@1.next() != null) {
          as.jmp(as.imm(new Label("Exit")));
          as.uncondXfer("Exit");
        }
      }
      /* FRETURN */
      | IR.FRETURN(fps) { @@.cost = @2.cost; } = {
        @2(as);
        if (@1.next() != null) {
          as.jmp(as.imm(new Label("Exit")));
          as.uncondXfer("Exit");
        }
      }
      /* DRETURN */
      | IR.DRETURN(fps) { @@.cost = @2.cost; } = {
        @2(as);
        if (@1.next() != null) {
          as.jmp(as.imm(new Label("Exit")));
          as.uncondXfer("Exit");
        }
      }
      /* ARETURN */
      | IR.ARETURN(eax) { @@.cost = @2.cost; } = {
        @2(as);
        use_eax = true;
        if (@1.next() != null) {
          as.jmp(as.imm(new Label("Exit")));
          as.uncondXfer("Exit");
        }
      }
      /* VRETURN */
      | IR.VRETURN { @@.cost = 0; } = {
        if (@1.next() != null) {
          as.jmp(as.imm(new Label("Exit")));
          as.uncondXfer("Exit");
        }
      }
      /* INIT */
      | IR.INIT(r32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.call(as.imm(_INIT_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      | IR.INIT(s32) { @@.cost = 2; } = {
        Imm imm = @2(as);
        as.pushl(imm); 
        as.call(as.imm(_INIT_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      /* INITX */
      | IR.INITX(r32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.call(as.imm(_INIT_));
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1);
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      | IR.INITX(s32) { @@.cost = 2; } = {
        Imm imm = @2(as);
        as.pushl(imm); 
        as.call(as.imm(_INIT_));
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1);
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      /* NEWINSTANCE */
      | IR.NEWINSTANCE(r32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.call(as.imm(_NEWINSTANCE_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      | IR.NEWINSTANCE(s32) { @@.cost = 2; } = {
        Imm imm = @2(as);
        as.pushl(imm); 
        as.call(as.imm(_NEWINSTANCE_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      /* NEWINSTANCEX */
      | IR.NEWINSTANCEX(r32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.call(as.imm(_NEWINSTANCE_));
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      | IR.NEWINSTANCEX(s32) { @@.cost = 2; } = {
        Imm imm = @2(as);
        as.pushl(imm);
        as.call(as.imm(_NEWINSTANCE_));
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      /* NEWARRAY */
      | IR.NEWARRAY(r32,r32) { @@.cost = @3.cost+@2.cost+3; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.pushl(r0);
        as.pushl(r1);
        as.call(as.imm(_NEWARRAY_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      | IR.NEWARRAY(s32,r32) { @@.cost = @3.cost+3; } = {
        Reg r0 = @3(as);
        Imm imm = @2(as);
        as.pushl(r0);
        as.pushl(imm); 
        as.call(as.imm(_NEWARRAY_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      | IR.NEWARRAY(s32,m32) { @@.cost = @3.cost+3; } = {
        Addr mem = @3(as);
        Imm imm = @2(as);
        as.pushl(mem);
        as.pushl(imm); 
        as.call(as.imm(_NEWARRAY_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      | IR.NEWARRAY(s32,i32) { @@.cost = 3; } = {
        int imm0 = @3(as);
        Imm imm1 = @2(as);
        as.pushl(imm0);
        as.pushl(imm1); 
        as.call(as.imm(_NEWARRAY_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      /* NEWARRAYX */
      | IR.NEWARRAYX(r32,r32) { @@.cost = @3.cost+@2.cost+3; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.pushl(r0);
        as.pushl(r1);
        as.call(as.imm(_NEWARRAY_));
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      | IR.NEWARRAYX(s32,r32) { @@.cost = @3.cost+3; } = {
        Reg r0 = @3(as);
        Imm imm = @2(as);
        as.pushl(r0);
        as.pushl(imm); 
        as.call(as.imm(_NEWARRAY_));
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      | IR.NEWARRAYX(s32,m32) { @@.cost = @3.cost+3; } = {
        Addr mem = @3(as);
        Imm imm = @2(as);
        as.pushl(mem);
        as.pushl(imm); 
        as.call(as.imm(_NEWARRAY_));
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      | IR.NEWARRAYX(s32,i32) { @@.cost = 3; } = {
        int imm0 = @3(as);
        Imm imm1 = @2(as);
        as.pushl(imm0);
        as.pushl(imm1); 
        as.call(as.imm(_NEWARRAY_));
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      /* LOCK */
      | IR.LOCK(r32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.call(as.imm(_LOCK_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      | IR.LOCK(m32) { @@.cost = @2.cost+2; } = {
        Addr mem = @2(as);
        as.pushl(mem); 
        as.call(as.imm(_LOCK_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      | IR.LOCK(s32) { @@.cost = 2; } = {
        Imm imm = @2(as);
        as.pushl(imm); 
        as.call(as.imm(_LOCK_));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      /* LOCKX */
      | IR.LOCKX(r32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.call(as.imm(_LOCK_)); 
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      | IR.LOCKX(m32) { @@.cost = @2.cost+2; } = {
        Addr mem = @2(as);
        as.pushl(mem); 
        as.call(as.imm(_LOCK_)); 
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      | IR.LOCKX(s32) { @@.cost = 2; } = {
        Imm imm = @2(as);
        as.pushl(imm);
        as.call(as.imm(_LOCK_)); 
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      /* UNLOCK */
      | IR.UNLOCK(r32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.call(as.imm(_UNLOCK_)); 
      }
      | IR.UNLOCK(m32) { @@.cost = @2.cost+2; } = {
        Addr mem = @2(as);
        as.pushl(mem); 
        as.call(as.imm(_UNLOCK_)); 
      }
      | IR.UNLOCK(s32) { @@.cost = 2; } = {
        Imm imm = @2(as);
        as.pushl(imm); 
        as.call(as.imm(_UNLOCK_)); 
      }
      /* READBARRIER */
      | IR.READBARRIER { @@.cost = 0; } = {
      }
      /* WRITEBARRIER */
      | IR.WRITEBARRIER { @@.cost = 0; } = {
      }
      /* CALL */
      | IR.CALL(r32) { @@.cost = @2.cost+1; } = {
        Reg r0 = @2(as);
        if (TEXT_BASE == 0)
          as.calll(r0);
        else {
          Reg r1 = nextReg();
          as.leal(as.addr(TEXT_BASE, r0), r1);
          as.calll(r1);
        }
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      | IR.CALL(IR.MLOOKUP(r32)) { @@.cost = @3.cost+1; } = {
        Reg r0 = @3(as);
        int offset = mtableOffset(@2.getDispatchIndex());
        if (TEXT_BASE == 0)
          as.calll(as.addr(offset, r0));
        else {
          Reg r1 = nextReg();
          as.movl(as.addr(offset, r0), r1);
          as.addl(TEXT_BASE, r1);
          as.calll(r1);
        }
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      | IR.CALL(IR.MLOOKUP(IR.ACLASS)) { @@.cost = 1; } = {
        as.call(as.imm(TEXT_BASE, new MethodEntry(@3.getSymbolicType(), @2.getDispatchIndex())));
        as.label("T"+inspectors.size());
        inspectors.add(@1);
      }
      /* CALLX */
      | IR.CALLX(r32) { @@.cost = @2.cost+1; } = {
        Reg r0 = @2(as);
        if (TEXT_BASE == 0)
          as.calll(r0); 
        else {
          Reg r1 = nextReg();
          as.leal(as.addr(TEXT_BASE, r0), r1);
          as.calll(r1); 
        }
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      | IR.CALLX(IR.MLOOKUP(r32)) { @@.cost = @3.cost+1; } = {
        Reg r0 = @3(as);
        int offset = mtableOffset(@2.getDispatchIndex());
        if (TEXT_BASE == 0)
          as.calll(as.addr(offset, r0));
        else {
          Reg r1 = nextReg();
          as.movl(as.addr(offset, r0), r1);
          as.addl(TEXT_BASE, r1);
          as.calll(r1);
        }
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1);
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      | IR.CALLX(IR.MLOOKUP(IR.ACLASS)) { @@.cost = 1; } = {
        as.call(as.imm(TEXT_BASE, new MethodEntry(@3.getSymbolicType(), @2.getDispatchIndex())));
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1);
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
      }
      /* NCALL */
      | IR.NCALL { @@.cost = 5; } = {
        as.pushl(as.imm(new Label("Natives."+addNative(@1.getDescriptor()))));
        as.pushl(getNativeIndex(@1.getSymbolicType(), @1.getName(), @1.getDescriptor()));
        as.pushl(as.imm(new SymbolicType(@1.getSymbolicType())));
        switch (Syntax.getReturnType(@1.getDescriptor()).charAt(0)) {
        case 'L':
        case '[': as.call(as.imm(_NCALLL_)); break;
        case 'Z': as.call(as.imm(_NCALLZ_)); break;
        case 'B': as.call(as.imm(_NCALLB_)); break;
        case 'C': as.call(as.imm(_NCALLC_)); break;
        case 'S': as.call(as.imm(_NCALLS_)); break;
        case 'I': as.call(as.imm(_NCALLI_)); break;
        case 'J': as.call(as.imm(_NCALLJ_)); break;
        case 'F': as.call(as.imm(_NCALLF_)); break;
        case 'D': as.call(as.imm(_NCALLD_)); break;
        case 'V': as.call(as.imm(_NCALLV_)); break;
        }
        as.label("T"+inspectors.size());
        inspectors.add(@1);
        as.addl(4*(Syntax.getParametersSize(@1.getDescriptor(), true)+3), as.ESP);
      }
      /* NCALLX */
      | IR.NCALLX { @@.cost = 5; } = {
        as.pushl(as.imm(new Label("Natives."+addNative(@1.getDescriptor()))));
        as.pushl(getNativeIndex(@1.getSymbolicType(), @1.getName(), @1.getDescriptor()));
        as.pushl(as.imm(new SymbolicType(@1.getSymbolicType())));
        switch (Syntax.getReturnType(@1.getDescriptor()).charAt(0)) {
        case 'L':
        case '[': as.call(as.imm(_NCALLL_)); break;
        case 'Z': as.call(as.imm(_NCALLZ_)); break;
        case 'B': as.call(as.imm(_NCALLB_)); break;
        case 'C': as.call(as.imm(_NCALLC_)); break;
        case 'S': as.call(as.imm(_NCALLS_)); break;
        case 'I': as.call(as.imm(_NCALLI_)); break;
        case 'J': as.call(as.imm(_NCALLJ_)); break;
        case 'F': as.call(as.imm(_NCALLF_)); break;
        case 'D': as.call(as.imm(_NCALLD_)); break;
        case 'V': as.call(as.imm(_NCALLV_)); break;
        }
        as.condXfer("L"+@1.getHandler());
        as.label("T"+inspectors.size());
        inspectors.add(@1); 
        as.label("X"+handlers.size());
        handlers.add(new Integer(@1.getHandler()));
        as.addl(4*(Syntax.getParametersSize(@1.getDescriptor(), true)+3), as.ESP);
      }
      ;
  
    private void eax(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      : r32 { @@.cost = @1.cost+1; } = {
        Reg r0 = @1(as);
        as.movl(r0, as.EAX);
      }
      /* IDIV */
      | IR.IDIV(eax,r32) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg r0 = @3(as);
        @2(as);
        as.cdq();
        as.idivl(r0); 
      }
      /* ISLOCKED */
      | IR.ISLOCKED(r32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.call(as.imm(_ISLOCKED_)); 
      }
      | IR.ISLOCKED(m32) { @@.cost = @2.cost+2; } = {
        Addr mem = @2(as);
        as.pushl(mem);
        as.call(as.imm(_ISLOCKED_)); 
      }
      | IR.ISLOCKED(s32) { @@.cost = 2; } = {
        Imm imm = @2(as);
        as.pushl(imm);
        as.call(as.imm(_ISLOCKED_)); 
      }
      /* SUBTYPEOF */
      | IR.SUBTYPEOF(r32,r32) { @@.cost = @3.cost+@2.cost+3; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.pushl(r0);
        as.pushl(r1);
        as.call(as.imm(_SUBTYPEOF_));
      }
      | IR.SUBTYPEOF(r32,s32) { @@.cost = @2.cost+3; } = {
        Imm imm = @3(as);
        Reg r0 = @2(as);
        as.pushl(imm);
        as.pushl(r0);
        as.call(as.imm(_SUBTYPEOF_));
      }
      | IR.SUBTYPEOF(m32,s32) { @@.cost = @2.cost+3; } = {
        Imm imm = @3(as);
        Addr mem = @2(as);
        as.pushl(imm);
        as.pushl(mem);
        as.call(as.imm(_SUBTYPEOF_));
      }
      /* COMPTYPEOF */
      | IR.COMPTYPEOF(r32,r32) { @@.cost = @3.cost+@2.cost+3; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.pushl(r0);
        as.pushl(r1);
        as.call(as.imm(_COMPTYPEOF_));
      }
      | IR.COMPTYPEOF(r32,s32) { @@.cost = @2.cost+3; } = {
        Imm imm = @3(as);
        Reg r0 = @2(as);
        as.pushl(imm);
        as.pushl(r0);
        as.call(as.imm(_COMPTYPEOF_));
      }
      | IR.COMPTYPEOF(m32,s32) { @@.cost = @2.cost+3; } = {
        Imm imm = @3(as);
        Addr mem = @2(as);
        as.pushl(imm);
        as.pushl(mem);
        as.call(as.imm(_COMPTYPEOF_));
      }
      /* IMLOOKUP */
      | IR.IMLOOKUP(r32,r32) { @@.cost = @3.cost+@2.cost+4; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.pushl(@1.getDispatchIndex());
        as.pushl(r0);
        as.pushl(r1);
        as.call(as.imm(_IMLOOKUP_));
      }
      | IR.IMLOOKUP(r32,s32) { @@.cost = @2.cost+4; } = {
        Imm imm = @3(as);
        Reg r0 = @2(as);
        as.pushl(@1.getDispatchIndex());
        as.pushl(imm);
        as.pushl(r0);
        as.call(as.imm(_IMLOOKUP_));
      }
      | IR.IMLOOKUP(m32,s32) { @@.cost = @2.cost+4; } = {
        Imm imm = @3(as);
        Addr mem = @2(as);
        as.pushl(@1.getDispatchIndex());
        as.pushl(imm);
        as.pushl(mem);
        as.call(as.imm(_IMLOOKUP_));
      }
      ;

    private void ecx(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      : r32 { @@.cost = @1.cost+1; } = {
        Reg r0 = @1(as);
        as.movl(r0, as.ECX);
      }
      ;

    private void edx(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      /* IREM */
      : IR.IREM(eax,r32) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg r0 = @3(as);
        @2(as);
        as.cdq();
        as.idivl(r0); 
      }
      ;

    private Reg r32(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      : rw32 { @@.cost = @1.cost; } = {
        Reg r0 = @1(as);
        return r0;
      }
      /* L2I */
      | IR.L2I(rp64) { @@.cost = @2.cost+1; } = {
        Reg[] rp = @2(as);
        Reg r0 = lo(rp);
        Reg r1 = hi(rp);
        as.testl(0, r1);//nop to let r1 live
        return r0;
      }
      /* IUSE */
      | IR.IUSE { @@.cost = 1; } = {
        Reg r0 = mapReg(@1.getReg());
        return r0;
      }
      /* AUSE */
      | IR.AUSE { @@.cost = 1; } = {
        Reg r0 = mapReg(@1.getReg());
        return r0;
      }
      ;

    private Reg rw32(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      : eax { @@.cost = @1.cost+1; } = {
        @1(as);
        Reg r0 = nextReg();
        as.movl(as.EAX, r0);
        return r0;
      }
      | edx { @@.cost = @1.cost+1; } = {
        @1(as);
        Reg r0 = nextReg();
        as.movl(as.EDX, r0);
        return r0;
      }
      | r32 { @@.cost = @1.cost+1; } = {
        Reg r0 = @1(as);
        Reg r1 = nextReg();
        as.movl(r0, r1);
        return r1;
      }
      | m8 { @@.cost = @1.cost+1; } = {
        Addr mem = @1(as);
        Reg r0 = nextReg();
        as.movsxbl(mem, r0);
        return r0;
      }
      | m16 { @@.cost = @1.cost+1; } = {
        Addr mem = @1(as);
        Reg r0 = nextReg();
        as.movsxwl(mem, r0);
        return r0;
      }
      | m32 { @@.cost = @1.cost+1; } = {
        Addr mem = @1(as);
        Reg r0 = nextReg();
        as.movl(mem, r0);
        return r0;
      }
      | i32 { @@.cost = 1; } = {
        int imm = @1(as);
        Reg r0 = nextReg();
        as.movl(imm, r0);
        return r0;
      }
      | s32 { @@.cost = 1; } = {
        Imm imm = @1(as);
        Reg r0 = nextReg();
        as.movl(imm, r0);
        return r0;
      }
      /* I2B */
      | IR.I2B(eax) { @@.cost = @2.cost+1; } = {
        @2(as); 
        Reg r0 = nextReg();
        as.movsxbl(as.AL, r0);
        return r0;
      }
      /* I2S */
      | IR.I2S(eax) { @@.cost = @2.cost+1; } = {
        @2(as);
        Reg r0 = nextReg();
        as.movsxwl(as.AX, r0);
        return r0;
      }
      /* I2C */
      | IR.I2C(eax) { @@.cost = @2.cost+1; } = {
        @2(as);
        Reg r0 = nextReg();
        as.movzxwl(as.AX, r0);
        return r0;
      }
      /* F2I */
      | IR.F2I(fps) { @@.cost = @2.cost+8; } = {
        @2(as);
        as.fnstcw(as.addr(-6, as.ESP));
        as.orb(12, as.addr(-5, as.ESP));
        as.fldcw(as.addr(-6, as.ESP));
        as.fistpl(as.addr(-4, as.ESP));
        as.andb(243, as.addr(-5, as.ESP));
        as.fldcw(as.addr(-6, as.ESP));
        as.addl(-4, as.ESP);
        Reg r0 = nextReg();
        as.popl(r0);
        return r0;
      }
      /* D2I */
      | IR.D2I(fps) { @@.cost = @2.cost+8; } = {
        @2(as);
        as.fnstcw(as.addr(-6, as.ESP));
        as.orb(12, as.addr(-5, as.ESP));
        as.fldcw(as.addr(-6, as.ESP));
        as.fistpl(as.addr(-4, as.ESP));
        as.andb(243, as.addr(-5, as.ESP));
        as.fldcw(as.addr(-6, as.ESP));
        as.addl(-4, as.ESP);
        Reg r0 = nextReg();
        as.popl(r0);
        return r0;
      }
      /* IADD */
      | IR.IADD(rw32,r32) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.addl(r0, r1);
        return r1;
      }
      | IR.IADD(rw32,i32) { @@.cost = @2.cost+1; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        if (imm == 1)
          as.incl(r0);
        else
          as.addl(imm, r0);
        return r0;
      }
      /* ISUB */
      | IR.ISUB(rw32,r32) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.subl(r0, r1);
        return r1;
      }
      | IR.ISUB(rw32,i32) { @@.cost = @2.cost+1; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        as.subl(imm, r0);
        return r0;
      }
      /* IMUL */
      | IR.IMUL(rw32,r32) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.imull(r0, r1); 
        return r1;
      }
      /* INEG */
      | IR.INEG(rw32) { @@.cost = @2.cost+1; } = {
        Reg r0 = @2(as);
        as.negl(r0); 
        return r0;
      }
      /* ISHL */
      | IR.ISHL(rw32,ecx) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @2(as);
        @3(as);
        as.sall(r0); 
        return r0;
      }
      /* ISHR */
      | IR.ISHR(rw32,ecx) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @2(as);
        @3(as);
        as.sarl(r0);
        return r0;
      }
      /* IUSHR */
      | IR.IUSHR(rw32,ecx) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @2(as);
        @3(as);
        as.shrl(r0);
        return r0;
      }
      /* IAND */
      | IR.IAND(rw32,r32) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.andl(r0, r1);
        return r1;
      }
      | IR.IAND(rw32,i32) { @@.cost = @2.cost+1; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        as.andl(imm, r0);
        return r0;
      }
      /* IOR */
      | IR.IOR(rw32,r32) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.orl(r0, r1);
        return r1;
      }
      | IR.IOR(rw32,i32) { @@.cost = @2.cost+1; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        as.orl(imm, r0);
        return r0;
      }
      /* IXOR */
      | IR.IXOR(rw32,r32) { @@.cost = @3.cost+@2.cost+1; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        as.xorl(r0, r1);
        return r1;
      }
      | IR.IXOR(rw32,i32) { @@.cost = @2.cost+1; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        as.xorl(imm, r0);
        return r0;
      }
      /* LCMP */
      | IR.LCMP(rp64,rp64) { @@.cost = @3.cost+@2.cost+15; } = {
        Reg[] rp0 = @3(as);
        Reg[] rp1 = @2(as);
        as.cmpl(hi(rp0), hi(rp1));
        as.setg(as.AH);
        as.setl(as.AL);
        as.negb(as.AL);
        as.orb(as.AH, as.AL);
        Reg r0 = nextReg();
        as.movsxbl(as.AL, r0);
        as.cmpl(lo(rp0), lo(rp1));
        as.seta(as.AH);
        as.setb(as.AL);
        as.negb(as.AL);
        as.orb(as.AH, as.AL);
        Reg r1 = nextReg();
        as.movsxbl(as.AL, r1);
        as.cmpl(0, r0);
        String label = "Local."+nextLabel();
        as.je(as.imm(new Label(label)));
        as.condXfer(label);
        as.movl(r0, r1);
        as.label(label);
        return r1;
      }
      /* FCMPG */
      | IR.FCMPG(fps,fps) { @@.cost = @2.cost+@3.cost+9; } = {
        @2(as);
        @3(as);
        as.fcompp();
        as.fnstsw();
        as.testb(1, as.AH);
        as.setne(as.AL);
        as.testb(69, as.AH);
        as.sete(as.AH);
        as.negb(as.AH);
        as.orb(as.AH, as.AL);
        Reg r0 = nextReg();
        as.movsxbl(as.AL, r0); 
        return r0;
      }
      /* FCMPL */
      | IR.FCMPL(fps,fps) { @@.cost = @3.cost+@2.cost+9; } = {
        @3(as);
        @2(as);
        as.fcompp();
        as.fnstsw();
        as.testb(1, as.AH);
        as.setne(as.AL);
        as.negb(as.AL);
        as.testb(69, as.AH);
        as.sete(as.AH);
        as.orb(as.AH, as.AL);
        Reg r0 = nextReg();
        as.movsxbl(as.AL, r0); 
        return r0;
      }
      /* DCMPG */
      | IR.DCMPG(fps,fps) { @@.cost = @2.cost+@3.cost+9; } = {
        @2(as);
        @3(as);
        as.fcompp();
        as.fnstsw();
        as.testb(1, as.AH);
        as.setne(as.AL);
        as.testb(69, as.AH);
        as.sete(as.AH);
        as.negb(as.AH);
        as.orb(as.AH, as.AL);
        Reg r0 = nextReg();
        as.movsxbl(as.AL, r0); 
        return r0;
      }
      /* DCMPL */
      | IR.DCMPL(fps,fps) { @@.cost = @3.cost+@2.cost+9; } = {
        @3(as);
        @2(as);
        as.fcompp();
        as.fnstsw();
        as.testb(1, as.AH);
        as.setne(as.AL);
        as.negb(as.AL);
        as.testb(69, as.AH);
        as.sete(as.AH);
        as.orb(as.AH, as.AL);
        Reg r0 = nextReg();
        as.movsxbl(as.AL, r0); 
        return r0;
      }
      ;

    private void eaxedx(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      : rp64 { @@.cost = @1.cost+2; } = {
        Reg[] rp = @1(as);
        as.movl(hi(rp), as.EDX);
        as.movl(lo(rp), as.EAX);
      }
      /* LMUL */
      | IR.LMUL(rp64,rpw64) { @@.cost = @3.cost+@2.cost+6; } = {
        Reg[] rp0 = @3(as);
        Reg[] rp1 = @2(as);
        as.movl(lo(rp1), as.EAX);
        as.mull(lo(rp0));
        as.imull(lo(rp1), hi(rp0));
        as.addl(hi(rp0), as.EDX);
        as.imull(hi(rp1), lo(rp0));
        as.addl(lo(rp0), as.EDX); 
      }
      /* LDIV */
      | IR.LDIV(rp64,rp64) { @@.cost = @3.cost+@2.cost+5; } = {
        Reg[] rp0 = @3(as);
        Reg[] rp1 = @2(as);
        as.pushl(hi(rp0));
        as.pushl(lo(rp0));
        as.pushl(hi(rp1));
        as.pushl(lo(rp1));
        as.call(as.imm(_LDIV_));
      }
      /* LREM */
      | IR.LREM(rp64,rp64) { @@.cost = @3.cost+@2.cost+5; } = {
        Reg[] rp0 = @3(as);
        Reg[] rp1 = @2(as);
        as.pushl(hi(rp0));
        as.pushl(lo(rp0));
        as.pushl(hi(rp1));
        as.pushl(lo(rp1));
        as.call(as.imm(_LREM_)); 
      }
      ;

    private Reg[] rp64(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      : rpw64 { @@.cost = @1.cost; } = {
        Reg[] rp = @1(as);
        return rp;
      }
      /* I2L */
      | IR.I2L(r32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        Reg r1 = nextReg();
        as.movl(r0, r1); 
        as.sarl(31, r1);
        return hilo(r1, r0);
      }
      /* LUSE */
      | IR.LUSE { @@.cost = 0; } = {
        Reg[] rp = mapReg2(@1.getReg());
        return rp;
      }
      ;

    private Reg[] rpw64(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      : eaxedx { @@.cost = @1.cost+2; } = {
        @1(as);
        Reg r0 = nextReg();
        Reg r1 = nextReg();
        as.movl(as.EAX, r0); 
        as.movl(as.EDX, r1); 
        return hilo(r1, r0);
      }
      | rp64 { @@.cost = @1.cost+2; } = {
        Reg[] rp = @1(as);
        Reg lo = nextReg();
        Reg hi = nextReg();
        as.movl(lo(rp), lo);
        as.movl(hi(rp), hi);
        return hilo(hi, lo);
      }
      | m64 { @@.cost = @1.cost+2; } = {
        Addr mem = @1(as);
        Reg r0 = nextReg();
        Reg r1 = nextReg();
        as.movl(mem, r0);
        as.movl(as.addr(4, mem), r1);
        return hilo(r1, r0);
      }
      | i64 { @@.cost = 2; } = {
        long imm = @1(as);
        Reg r0 = nextReg();
        Reg r1 = nextReg();
        as.movl(lo(imm), r0);
        as.movl(hi(imm), r1);
        return hilo(r1, r0);
      }
      /* I2L */
      | IR.I2L(rw32) { @@.cost = @2.cost+2; } = {
        Reg r0 = @2(as);
        Reg r1 = nextReg();
        as.movl(r0, r1); 
        as.sarl(31, r1);
        return hilo(r1, r0);
      }
      /* F2L */
      | IR.F2L(fps) { @@.cost = @2.cost+9; } = {
        @2(as);
        as.fnstcw(as.addr(-10, as.ESP));
        as.orb(12, as.addr(-9, as.ESP));
        as.fldcw(as.addr(-10, as.ESP));
        as.fistpll(as.addr(-8, as.ESP));
        as.andb(243, as.addr(-9, as.ESP));
        as.fldcw(as.addr(-10, as.ESP));
        as.addl(-8, as.ESP);
        Reg r0 = nextReg();
        Reg r1 = nextReg();
        as.popl(r0);
        as.popl(r1);
        return hilo(r1, r0);
      }
      /* D2L */
      | IR.D2L(fps) { @@.cost = @2.cost+9; } = {
        @2(as);
        as.fnstcw(as.addr(-10, as.ESP));
        as.orb(12, as.addr(-9, as.ESP));
        as.fldcw(as.addr(-10, as.ESP));
        as.fistpll(as.addr(-8, as.ESP));
        as.andb(243, as.addr(-9, as.ESP));
        as.fldcw(as.addr(-10, as.ESP));
        as.addl(-8, as.ESP);
        Reg r0 = nextReg();
        Reg r1 = nextReg();
        as.popl(r0);
        as.popl(r1);
        return hilo(r1, r0);
      }
      /* LADD */
      | IR.LADD(rpw64,rp64) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg[] rp0 = @3(as);
        Reg[] rp1 = @2(as);
        as.addl(lo(rp0), lo(rp1));
        as.adcl(hi(rp0), hi(rp1));
        return rp1;
      }
      /* LSUB */
      | IR.LSUB(rpw64,rp64) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg[] rp0 = @3(as);
        Reg[] rp1 = @2(as);
        as.subl(lo(rp0), lo(rp1));
        as.sbbl(hi(rp0), hi(rp1));
        return rp1;
      }
      /* LNEG */
      | IR.LNEG(rpw64) { @@.cost = @2.cost+3; } = {
        Reg[] rp = @2(as);
        as.negl(lo(rp)); 
        as.adcl(0, hi(rp)); 
        as.negl(hi(rp));
        return rp;
      }
      /* LSHL */
      | IR.LSHL(rpw64,ecx) { @@.cost = @3.cost+@2.cost+6; } = {
        Reg[] rp = @2(as);
        @3(as);
        as.shldl(lo(rp), hi(rp));
        as.sall(lo(rp));
        as.testb(32, as.CL);
        String label = "Local."+nextLabel();
        as.je(as.imm(new Label(label)));
        as.condXfer(label);
        as.movl(lo(rp), hi(rp));
        as.movl(0, lo(rp));
        as.label(label);
        return rp;
      }
      /* LSHR */
      | IR.LSHR(rpw64,ecx) { @@.cost = @3.cost+@2.cost+6; } = {
        Reg[] rp = @2(as);
        @3(as);
        as.shrdl(hi(rp), lo(rp));
        as.sarl(hi(rp));
        as.testb(32, as.CL);
        String label = "Local."+nextLabel();
        as.je(as.imm(new Label(label)));
        as.condXfer(label);
        as.movl(hi(rp), lo(rp));
        as.sarl(31, hi(rp));
        as.label(label);
        return rp;
      }
      /* LUSHR */
      | IR.LUSHR(rpw64,ecx) { @@.cost = @3.cost+@2.cost+6; } = {
        Reg[] rp = @2(as);
        @3(as);
        as.shrdl(hi(rp), lo(rp));
        as.shrl(hi(rp));
        as.testb(32, as.CL);
        String label = "Local."+nextLabel();
        as.je(as.imm(new Label(label)));
        as.condXfer(label);
        as.movl(hi(rp), lo(rp));
        as.movl(0, hi(rp));
        as.label(label);
        return rp;
      }
      /* LAND */
      | IR.LAND(rpw64,rp64) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg[] rp0 = @3(as);
        Reg[] rp1 = @2(as);
        as.andl(lo(rp0), lo(rp1));
        as.andl(hi(rp0), hi(rp1));
        return rp1;
      }
      /* LOR */
      | IR.LOR(rpw64,rp64) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg[] rp0 = @3(as);
        Reg[] rp1 = @2(as);
        as.orl(lo(rp0), lo(rp1));
        as.orl(hi(rp0), hi(rp1));
        return rp1;
      }
      /* LXOR */
      | IR.LXOR(rpw64,rp64) { @@.cost = @3.cost+@2.cost+2; } = {
        Reg[] rp0 = @3(as);
        Reg[] rp1 = @2(as);
        as.xorl(lo(rp0), lo(rp1));
        as.xorl(hi(rp0), hi(rp1));
        return rp1;
      }
      ;

    private void fps(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      : m32 { @@.cost = @1.cost+1; } = {
        Addr mem = @1(as);
        as.flds(mem);
      }
      | m64 { @@.cost = @1.cost+1; } = {
        Addr mem = @1(as);
        as.fldl(mem);
      }
      | i32 { @@.cost = 2; } = {
        int imm = @1(as);
        as.movl(imm, as.addr(-4, as.ESP)); 
        as.flds(as.addr(-4, as.ESP));
      }
      | i64 { @@.cost = 3; } = {
        long imm = @1(as);
        as.movl(lo(imm), as.addr(-8, as.ESP));
        as.movl(hi(imm), as.addr(-4, as.ESP));
        as.fldl(as.addr(-8, as.ESP));
      }
      /* I2F */
      | IR.I2F(r32) { @@.cost = @2.cost+5; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.fildl(as.addr(as.ESP)); 
        as.fstps(as.addr(as.ESP)); 
        as.flds(as.addr(as.ESP));
        as.addl(4, as.ESP);
      }
      /* I2D */
      | IR.I2D(r32) { @@.cost = @2.cost+3; } = {
        Reg r0 = @2(as);
        as.pushl(r0);
        as.fildl(as.addr(as.ESP));
        as.addl(4, as.ESP);
      }
      /* L2F */
      | IR.L2F(rp64) { @@.cost = @2.cost+6; } = {
        Reg[] rp = @2(as);
        as.pushl(hi(rp));
        as.pushl(lo(rp));
        as.fildll(as.addr(as.ESP));
        as.fstps(as.addr(as.ESP)); 
        as.flds(as.addr(as.ESP));
        as.addl(8, as.ESP);
      }
      /* L2D */
      | IR.L2D(rp64) { @@.cost = @2.cost+6; } = {
        Reg[] rp = @2(as);
        as.pushl(hi(rp));
        as.pushl(lo(rp));
        as.fildll(as.addr(as.ESP));
        as.fstpl(as.addr(as.ESP)); 
        as.fldl(as.addr(as.ESP));
        as.addl(8, as.ESP);
      }
      /* F2D */
      | IR.F2D(fps) { @@.cost = @2.cost; } = {
        @2(as);
      }
      /* D2F */
      | IR.D2F(fps) { @@.cost = @2.cost+2; } = {
        @2(as); 
        as.fstps(as.addr(-4, as.ESP)); 
        as.flds(as.addr(-4, as.ESP));
      }
      /* FSTRICT */
      | IR.FSTRICT(fps) { @@.cost = @2.cost+2; } = {
        @2(as);
        as.fstps(as.addr(-4, as.ESP)); 
        as.flds(as.addr(-4, as.ESP));
      }
      /* DSTRICT */
      | IR.DSTRICT(fps) { @@.cost = @2.cost+2; } = {
        @2(as);
        as.fstpl(as.addr(-8, as.ESP)); 
        as.fldl(as.addr(-8, as.ESP));
      }
      /* FADD */
      | IR.FADD(fps,fps) { @@.cost = @3.cost+@2.cost+4; } = {
        @3(as);
        @2(as);
        as.fnstcw(as.addr(-2, as.ESP));
        as.andw(65023, as.addr(-2, as.ESP));
        as.fldcw(as.addr(-2, as.ESP));
        as.faddp();
      }
      /* DADD */
      | IR.DADD(fps,fps) { @@.cost = @3.cost+@2.cost+4; } = {
        @3(as);
        @2(as);
        as.fnstcw(as.addr(-2, as.ESP));
        as.orw(512, as.addr(-2, as.ESP));
        as.fldcw(as.addr(-2, as.ESP));
        as.faddp();
      }
      /* FSUB */
      | IR.FSUB(fps,fps) { @@.cost = @3.cost+@2.cost+4; } = {
        @3(as);
        @2(as);
        as.fnstcw(as.addr(-2, as.ESP));
        as.andw(65023, as.addr(-2, as.ESP));
        as.fldcw(as.addr(-2, as.ESP));
        as.fsubrp();
      }
      /* DSUB */
      | IR.DSUB(fps,fps) { @@.cost = @3.cost+@2.cost+4; } = {
        @3(as);
        @2(as);
        as.fnstcw(as.addr(-2, as.ESP));
        as.orw(512, as.addr(-2, as.ESP));
        as.fldcw(as.addr(-2, as.ESP));
        as.fsubrp();
      }
      /* FMUL */
      | IR.FMUL(fps,fps) { @@.cost = @3.cost+@2.cost+4; } = {
        @3(as);
        @2(as);
        as.fnstcw(as.addr(-2, as.ESP));
        as.andw(65023, as.addr(-2, as.ESP));
        as.fldcw(as.addr(-2, as.ESP));
        as.fmulp();
      }
      /* DMUL */
      | IR.DMUL(fps,fps) { @@.cost = @3.cost+@2.cost+4; } = { 
        @3(as);
        @2(as);
        as.fnstcw(as.addr(-2, as.ESP));
        as.orw(512, as.addr(-2, as.ESP));
        as.fldcw(as.addr(-2, as.ESP));
        as.fmulp();
      }
      /* FDIV */
      | IR.FDIV(fps,fps) { @@.cost = @3.cost+@2.cost+4; } = {
        @3(as);
        @2(as);
        as.fnstcw(as.addr(-2, as.ESP));
        as.andw(65023, as.addr(-2, as.ESP));
        as.fldcw(as.addr(-2, as.ESP));
        as.fdivrp();
      }
      /* DDIV */
      | IR.DDIV(fps,fps) { @@.cost = @3.cost+@2.cost+4; } = {
        @3(as);
        @2(as);
        as.fnstcw(as.addr(-2, as.ESP));
        as.orw(512, as.addr(-2, as.ESP));
        as.fldcw(as.addr(-2, as.ESP));
        as.fdivrp();
      }
      /* FREM */
      | IR.FREM(fps,fps) { @@.cost = @3.cost+@2.cost+5; } = {
        @3(as);
        @2(as);
        as.fnstcw(as.addr(-2, as.ESP));
        as.andw(65023, as.addr(-2, as.ESP));
        as.fldcw(as.addr(-2, as.ESP));
        as.fprem();
        as.fstp(as.ST1);
      }
      /* DREM */
      | IR.DREM(fps,fps) { @@.cost = @3.cost+@2.cost+5; } = {
        @3(as);
        @2(as);
        as.fnstcw(as.addr(-2, as.ESP));
        as.orw(512, as.addr(-2, as.ESP));
        as.fldcw(as.addr(-2, as.ESP));
        as.fprem();
        as.fstp(as.ST1);
      }
      /* FNEG */
      | IR.FNEG(fps) { @@.cost = @2.cost+1; } = {
        @2(as);
        as.fchs();
      }
      /* DNEG */
      | IR.DNEG(fps) { @@.cost = @2.cost+1; } = {
        @2(as);
        as.fchs();
      }
      ;

    private Addr m8(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      /* BLOAD */
      : IR.BLOAD(cls) { @@.cost = 0; } = {
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, mem);
      }
      | IR.BLOAD(r32) { @@.cost = @2.cost; } = {
        Reg r0 = @2(as); 
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, r0);
      }
      /* BALOAD */
      | IR.BALOAD(r32,r32) { @@.cost = @3.cost+@2.cost; } = {
        Reg r0 = @3(as); 
        Reg r1 = @2(as);
        return as.addr(arrayBase("[B"), r1, r0);
      }
      | IR.BALOAD(r32,i32) { @@.cost = @2.cost; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        return as.addr(arrayBase("[B")+imm, r0);
      }
      ;

    private Addr m16(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      /* SLOAD */
      : IR.SLOAD(cls) { @@.cost = 0; } = {
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, mem);
      }
      | IR.SLOAD(r32) { @@.cost = @2.cost; } = {
        Reg r0 = @2(as); 
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, r0);
      }
      /* SALOAD */
      | IR.SALOAD(r32,r32) { @@.cost = @3.cost+@2.cost; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        return as.addr(arrayBase("[S"), r1, r0, 2);
      }
      | IR.SALOAD(r32,i32) { @@.cost = @2.cost; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        return as.addr(arrayBase("[S")+2*imm, r0);
      }
      ;

    private Addr m32(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      /* FUSE */
      : IR.FUSE { @@.cost = 0; } = {
        return as.addr(binder.regBase(@1.getReg()), as.EBP);
      }
      /* GETCLASS */
      | IR.GETCLASS(r32) { @@.cost = @2.cost; } = {
        Reg r0 = @2(as); 
        return as.addr(-4, r0);
      }
      /* LENGTH */
      | IR.LENGTH(r32) { @@.cost = @2.cost; } = {
        Reg r0 = @2(as); 
        return as.addr(-8, r0);
      }
      /* MLOOKUP */
      | IR.MLOOKUP(IR.ACLASS) { @@.cost = 0; } = {
        int offset = mtableOffset(@1.getDispatchIndex());
        return as.addr(offset, new SymbolicType(@2.getSymbolicType()));
      }
      | IR.MLOOKUP(r32) { @@.cost = @2.cost; } = {
        Reg r0 = @2(as); 
        int offset = mtableOffset(@1.getDispatchIndex());
        return as.addr(offset, r0);
      }
      /* ILOAD */
      | IR.ILOAD(cls) { @@.cost = 0; } = {
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, mem);
      }
      | IR.ILOAD(r32) { @@.cost = @2.cost; } = {
        Reg r0 = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, r0);
      }
      /* FLOAD */
      | IR.FLOAD(cls) { @@.cost = 0; } = {
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, mem);
      }
      | IR.FLOAD(r32) { @@.cost = @2.cost; } = {
        Reg r0 = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, r0);
      }
      /* ALOAD */
      | IR.ALOAD(cls) { @@.cost = 0; } = {
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, mem);
      }
      | IR.ALOAD(r32) { @@.cost = @2.cost; } = {
        Reg r0 = @2(as); 
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, r0);
      }
      /* IALOAD */
      | IR.IALOAD(r32,r32) { @@.cost = @3.cost+@2.cost; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        return as.addr(arrayBase("[I"), r1, r0, 4);
      }
      | IR.IALOAD(r32,i32) { @@.cost = @2.cost; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        return as.addr(arrayBase("[I")+4*imm, r0);
      }
      /* FALOAD */
      | IR.FALOAD(r32,r32) { @@.cost = @3.cost+@2.cost; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as); 
        return as.addr(arrayBase("[F"), r1, r0, 4);
      }
      | IR.FALOAD(r32,i32) { @@.cost = @2.cost; } = {
        int imm = @3(as);
        Reg r0 = @2(as); 
        return as.addr(arrayBase("[F")+4*imm, r0);
      }
      /* AALOAD */
      | IR.AALOAD(r32,r32) { @@.cost = @3.cost+@2.cost; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as); 
        return as.addr(arrayBase("[Ljava/lang/Object;"), r1, r0, 4);
      }
      | IR.AALOAD(r32,i32) { @@.cost = @2.cost; } = {
        int imm = @3(as);
        Reg r0 = @2(as); 
        return as.addr(arrayBase("[Ljava/lang/Object;")+4*imm, r0);
      }
      ;

    private Addr m64(i386_FlowAssembler as)
    <int cost> [@@.cost < cost]
      /* DUSE */
      : IR.DUSE { @@.cost = 0; } = {
        return as.addr(binder.regBase(@1.getReg()), as.EBP);
      }
      /* LLOAD */
      | IR.LLOAD(cls) { @@.cost = 0; } = {
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, mem);
      }
      | IR.LLOAD(r32) { @@.cost = @2.cost; } = {
        Reg r0 = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, r0);
      }
      /* DLOAD */
      | IR.DLOAD(cls) { @@.cost = 0; } = {
        Addr mem = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, mem);
      }
      | IR.DLOAD(r32) { @@.cost = @2.cost; } = {
        Reg r0 = @2(as);
        int offset = Measure.getScaled(@1.getOffset(), 4);
        return as.addr(offset, r0);
      }
      /* LALOAD */
      | IR.LALOAD(r32,r32) { @@.cost = @3.cost+@2.cost; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as);
        return as.addr(arrayBase("[J"), r1, r0, 8);
      }
      | IR.LALOAD(r32,i32) { @@.cost = @2.cost; } = {
        int imm = @3(as);
        Reg r0 = @2(as);
        return as.addr(arrayBase("[J")+8*imm, r0);
      }
      /* DALOAD */
      | IR.DALOAD(r32,r32) { @@.cost = @3.cost+@2.cost; } = {
        Reg r0 = @3(as);
        Reg r1 = @2(as); 
        return as.addr(arrayBase("[D"), r1, r0, 8);
      }
      | IR.DALOAD(r32,i32) { @@.cost = @2.cost; } = {
        int imm = @3(as);
        Reg r0 = @2(as); 
        return as.addr(arrayBase("[D")+8*imm, r0);
      }
      ;

    private int i32(i386_FlowAssembler as)
      /* ANULL */
      : IR.ANULL = {
        return 0;
      }
      /* ICONST */
      | IR.ICONST = {
        return @1.getValue();
      }
      /* FCONST */
      | IR.FCONST = {
        return Float.floatToIntBits(@1.getValue());
      }
      ;

    private Imm s32(i386_FlowAssembler as)
      /* ACLASS */
      : IR.ACLASS = {
        Symbol symbol = new SymbolicType(@1.getSymbolicType());
        return as.imm(symbol);
      }
      /* ASTRING */
      | IR.ASTRING = {
        Symbol symbol = new StringRef(@1.getValue());
        return as.imm(symbol);
      }
      ;

    private long i64(i386_FlowAssembler as)
      /* LCONST */
      : IR.LCONST = {
        return @1.getValue();
      }
      /* DCONST */
      | IR.DCONST = {
        return Double.doubleToLongBits(@1.getValue());
      }
      ;

    private Addr cls(i386_FlowAssembler as)
      : IR.ACLASS = {
        Symbol symbol = new SymbolicType(@1.getSymbolicType());
        int offset = staticBase(@1.getSymbolicType());
        return as.addr(offset, symbol);
      }
      ;

  }

}

