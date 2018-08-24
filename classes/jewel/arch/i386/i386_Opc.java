/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Addr;
import jewel.core.bend.Assembler;
import jewel.core.bend.FlowAssembler.InstructionStatement;
import jewel.core.bend.Imm;
import jewel.core.bend.Opc;
import jewel.core.bend.Reg;

public class i386_Opc extends Opc {

  private final String name;

  public i386_Opc(int index, String name) {
    super(index);
    if (name == null)
      throw new NullPointerException();
    this.name = name;
  }

  public String toString() {
    return name;
  }

  public void emit(Assembler as, InstructionStatement insn, String format) {
    i386_Architecture arch = (i386_Architecture)as;
    if (format.equals("\t%op")) emit0(arch, insn);
    if (format.equals("\t%op\t%u0")) emit1(arch, insn);
    if (format.equals("\t%op\t%d0")) emit2(arch, insn);
    if (format.equals("\t%op\t%i0")) emit3(arch, insn);
    if (format.equals("\t%op\t%l0")) emit4(arch, insn);
    if (format.equals("\t%op\t%j0")) emit5(arch, insn);
    if (format.equals("\t%op\t%u0,%u1")) emit6(arch, insn);
    if (format.equals("\t%op\t%u0,%d0")) emit7(arch, insn);
    if (format.equals("\t%op\t%d0,%d1")) emit8(arch, insn);
    if (format.equals("\t%op\t%i0,%u0")) emit9(arch, insn);
    if (format.equals("\t%op\t%i0,%d0")) emit10(arch, insn);
    if (format.equals("\t%op\t%i0,%i1")) emit11(arch, insn);
    if (format.equals("\t%op\t%u0,%l0")) emit12(arch, insn);
    if (format.equals("\t%op\t%d0,%l0")) emit13(arch, insn);
    if (format.equals("\t%op\t%i0,%l0")) emit14(arch, insn);
    if (format.equals("\t%op\t%l0,%u0")) emit15(arch, insn);
    if (format.equals("\t%op\t%l0,%d0")) emit16(arch, insn);
    if (format.equals("\t%op\t%j0,%u0")) emit17(arch, insn);
    if (format.equals("\t%op\t%j0,%d0")) emit18(arch, insn);
    if (format.equals("\t%op\t%j0,%l0")) emit19(arch, insn);
    if (format.equals("\t%op\t%i0,%u0,%d0")) emit20(arch, insn);
    if (format.equals("\t%op\t%i0,%u0,%l0")) emit21(arch, insn);
    if (format.equals("\t%op\t%i0,%l0,%d0")) emit22(arch, insn);
    if (format.equals("\t%op\t%j0,%u0,%d0")) emit23(arch, insn);
    if (format.equals("\t%op\t%j0,%l0,%d0")) emit24(arch, insn);
  }

  private void emit0(i386_Architecture arch, InstructionStatement insn) {
    switch (index) {
    case i386_Opcodes._cs: arch.cs(); break;
    case i386_Opcodes._ds: arch.ds(); break;
    case i386_Opcodes._es: arch.es(); break;
    case i386_Opcodes._fs: arch.fs(); break;
    case i386_Opcodes._gs: arch.gs(); break;
    case i386_Opcodes._ss: arch.ss(); break;
    case i386_Opcodes._aaa: arch.aaa(); break;
    case i386_Opcodes._aad: arch.aad(); break;
    case i386_Opcodes._aam: arch.aam(); break;
    case i386_Opcodes._aas: arch.aas(); break;
    case i386_Opcodes._cbw: arch.cbw(); break;
    case i386_Opcodes._cwde: arch.cwde(); break;
    case i386_Opcodes._clc: arch.clc(); break;
    case i386_Opcodes._cld: arch.cld(); break;
    case i386_Opcodes._cli: arch.cli(); break;
    case i386_Opcodes._clts: arch.clts(); break;
    case i386_Opcodes._cmc: arch.cmc(); break;
    case i386_Opcodes._cmpsb: arch.cmpsb(); break;
    case i386_Opcodes._cmpsw: arch.cmpsw(); break;
    case i386_Opcodes._cmpsd: arch.cmpsd(); break;
    case i386_Opcodes._cwd: arch.cwd(); break;
    case i386_Opcodes._cdq: arch.cdq(); break;
    case i386_Opcodes._daa: arch.daa(); break;
    case i386_Opcodes._das: arch.das(); break;
    case i386_Opcodes._f2xm1: arch.f2xm1(); break;
    case i386_Opcodes._fabs: arch.fabs(); break;
    case i386_Opcodes._faddp: arch.faddp(); break;
    case i386_Opcodes._fchs: arch.fchs(); break;
    case i386_Opcodes._fclex: arch.fclex(); break;
    case i386_Opcodes._fnclex: arch.fnclex(); break;
    case i386_Opcodes._fcom: arch.fcom(); break;
    case i386_Opcodes._fcomp: arch.fcomp(); break;
    case i386_Opcodes._fcompp: arch.fcompp(); break;
    case i386_Opcodes._fcos: arch.fcos(); break;
    case i386_Opcodes._fdecstp: arch.fdecstp(); break;
    case i386_Opcodes._fdivp: arch.fdivp(); break;
    case i386_Opcodes._fdivrp: arch.fdivrp(); break;
    case i386_Opcodes._fincstp: arch.fincstp(); break;
    case i386_Opcodes._finit: arch.finit(); break;
    case i386_Opcodes._fninit: arch.fninit(); break;
    case i386_Opcodes._fld1: arch.fld1(); break;
    case i386_Opcodes._fldl2t: arch.fldl2t(); break;
    case i386_Opcodes._fldl2e: arch.fldl2e(); break;
    case i386_Opcodes._fldpi: arch.fldpi(); break;
    case i386_Opcodes._fldlg2: arch.fldlg2(); break;
    case i386_Opcodes._fldln2: arch.fldln2(); break;
    case i386_Opcodes._fldz: arch.fldz(); break;
    case i386_Opcodes._fmulp: arch.fmulp(); break;
    case i386_Opcodes._fnop: arch.fnop(); break;
    case i386_Opcodes._fpatan: arch.fpatan(); break;
    case i386_Opcodes._fprem: arch.fprem(); break;
    case i386_Opcodes._fprem1: arch.fprem1(); break;
    case i386_Opcodes._fptan: arch.fptan(); break;
    case i386_Opcodes._frndint: arch.frndint(); break;
    case i386_Opcodes._fscale: arch.fscale(); break;
    case i386_Opcodes._fsin: arch.fsin(); break;
    case i386_Opcodes._fsincos: arch.fsincos(); break;
    case i386_Opcodes._fsqrt: arch.fsqrt(); break;
    case i386_Opcodes._fstsw: arch.fstsw(); break;
    case i386_Opcodes._fnstsw: arch.fnstsw(); break;
    case i386_Opcodes._fsubp: arch.fsubp(); break;
    case i386_Opcodes._fsubrp: arch.fsubrp(); break;
    case i386_Opcodes._ftst: arch.ftst(); break;
    case i386_Opcodes._fucom: arch.fucom(); break;
    case i386_Opcodes._fucomp: arch.fucomp(); break;
    case i386_Opcodes._fucompp: arch.fucompp(); break;
    case i386_Opcodes._fxam: arch.fxam(); break;
    case i386_Opcodes._fxch: arch.fxch(); break;
    case i386_Opcodes._fxtract: arch.fxtract(); break;
    case i386_Opcodes._fyl2x: arch.fyl2x(); break;
    case i386_Opcodes._fyl2xp1: arch.fyl2xp1(); break;
    case i386_Opcodes._hlt: arch.hlt(); break;
    case i386_Opcodes._inb: arch.inb(); break;
    case i386_Opcodes._inw: arch.inw(); break;
    case i386_Opcodes._inl: arch.inl(); break;
    case i386_Opcodes._insb: arch.insb(); break;
    case i386_Opcodes._insw: arch.insw(); break;
    case i386_Opcodes._insd: arch.insd(); break;
    case i386_Opcodes._into: arch.into(); break;
    case i386_Opcodes._iret: arch.iret(); break;
    case i386_Opcodes._iretd: arch.iretd(); break;
    case i386_Opcodes._lahf: arch.lahf(); break;
    case i386_Opcodes._leave: arch.leave(); break;
    case i386_Opcodes._lock: arch.lock(); break;
    case i386_Opcodes._lodsb: arch.lodsb(); break;
    case i386_Opcodes._lodsw: arch.lodsw(); break;
    case i386_Opcodes._lodsd: arch.lodsd(); break;
    case i386_Opcodes._movsb: arch.movsb(); break;
    case i386_Opcodes._movsw: arch.movsw(); break;
    case i386_Opcodes._movsd: arch.movsd(); break;
    case i386_Opcodes._nop: arch.nop(); break;
    case i386_Opcodes._outb: arch.outb(); break;
    case i386_Opcodes._outw: arch.outw(); break;
    case i386_Opcodes._outl: arch.outl(); break;
    case i386_Opcodes._outsb: arch.outsb(); break;
    case i386_Opcodes._outsw: arch.outsw(); break;
    case i386_Opcodes._outsd: arch.outsd(); break;
    case i386_Opcodes._popa: arch.popa(); break;
    case i386_Opcodes._popad: arch.popad(); break;
    case i386_Opcodes._popf: arch.popf(); break;
    case i386_Opcodes._popfd: arch.popfd(); break;
    case i386_Opcodes._pusha: arch.pusha(); break;
    case i386_Opcodes._pushad: arch.pushad(); break;
    case i386_Opcodes._pushf: arch.pushf(); break;
    case i386_Opcodes._pushfd: arch.pushfd(); break;
    case i386_Opcodes._rep: arch.rep(); break;
    case i386_Opcodes._repe: arch.repe(); break;
    case i386_Opcodes._repz: arch.repz(); break;
    case i386_Opcodes._repne: arch.repne(); break;
    case i386_Opcodes._repnz: arch.repnz(); break;
    case i386_Opcodes._ret: arch.ret(); break;
    case i386_Opcodes._retf: arch.retf(); break;
    case i386_Opcodes._sahf: arch.sahf(); break;
    case i386_Opcodes._scasb: arch.scasb(); break;
    case i386_Opcodes._scasw: arch.scasw(); break;
    case i386_Opcodes._scasd: arch.scasd(); break;
    case i386_Opcodes._stc: arch.stc(); break;
    case i386_Opcodes._std: arch.std(); break;
    case i386_Opcodes._sti: arch.sti(); break;
    case i386_Opcodes._stosb: arch.stosb(); break;
    case i386_Opcodes._stosw: arch.stosw(); break;
    case i386_Opcodes._stosd: arch.stosd(); break;
    case i386_Opcodes._fwait: arch.fwait(); break;
    case i386_Opcodes._xlatb: arch.xlatb(); break;
    }
  }

  private void emit1(i386_Architecture arch, InstructionStatement insn) {
    Reg ureg_0 = insn.ureg[0];
    switch (index) {
    case i386_Opcodes._callw: arch.callw(ureg_0); break;
    case i386_Opcodes._calll: arch.calll(ureg_0); break;
    case i386_Opcodes._divb: arch.divb(ureg_0); break;
    case i386_Opcodes._divw: arch.divw(ureg_0); break;
    case i386_Opcodes._divl: arch.divl(ureg_0); break;
    case i386_Opcodes._fcom: arch.fcom(ureg_0); break;
    case i386_Opcodes._fcomp: arch.fcomp(ureg_0); break;
    case i386_Opcodes._fst: arch.fst(ureg_0); break;
    case i386_Opcodes._fstp: arch.fstp(ureg_0); break;
    case i386_Opcodes._fucom: arch.fucom(ureg_0); break;
    case i386_Opcodes._fucomp: arch.fucomp(ureg_0); break;
    case i386_Opcodes._idivb: arch.idivb(ureg_0); break;
    case i386_Opcodes._idivw: arch.idivw(ureg_0); break;
    case i386_Opcodes._idivl: arch.idivl(ureg_0); break;
    case i386_Opcodes._imulb: arch.imulb(ureg_0); break;
    case i386_Opcodes._imulw: arch.imulw(ureg_0); break;
    case i386_Opcodes._imull: arch.imull(ureg_0); break;
    case i386_Opcodes._jmpw: arch.jmpw(ureg_0); break;
    case i386_Opcodes._jmpl: arch.jmpl(ureg_0); break;
    case i386_Opcodes._lldt: arch.lldt(ureg_0); break;
    case i386_Opcodes._lmsw: arch.lmsw(ureg_0); break;
    case i386_Opcodes._ltr: arch.ltr(ureg_0); break;
    case i386_Opcodes._mulb: arch.mulb(ureg_0); break;
    case i386_Opcodes._mulw: arch.mulw(ureg_0); break;
    case i386_Opcodes._mull: arch.mull(ureg_0); break;
    case i386_Opcodes._pushw: arch.pushw(ureg_0); break;
    case i386_Opcodes._pushl: arch.pushl(ureg_0); break;
    case i386_Opcodes._pushs: arch.pushs(ureg_0); break;
    case i386_Opcodes._verr: arch.verr(ureg_0); break;
    case i386_Opcodes._verw: arch.verw(ureg_0); break;
    }
  }

  private void emit2(i386_Architecture arch, InstructionStatement insn) {
    Reg dreg_0 = insn.dreg[0];
    switch (index) {
    case i386_Opcodes._ffree: arch.ffree(dreg_0); break;
    case i386_Opcodes._fld: arch.fld(dreg_0); break;
    case i386_Opcodes._popw: arch.popw(dreg_0); break;
    case i386_Opcodes._popl: arch.popl(dreg_0); break;
    case i386_Opcodes._pops: arch.pops(dreg_0); break;
    case i386_Opcodes._seta: arch.seta(dreg_0); break;
    case i386_Opcodes._setae: arch.setae(dreg_0); break;
    case i386_Opcodes._setb: arch.setb(dreg_0); break;
    case i386_Opcodes._setbe: arch.setbe(dreg_0); break;
    case i386_Opcodes._setc: arch.setc(dreg_0); break;
    case i386_Opcodes._sete: arch.sete(dreg_0); break;
    case i386_Opcodes._setg: arch.setg(dreg_0); break;
    case i386_Opcodes._setge: arch.setge(dreg_0); break;
    case i386_Opcodes._setl: arch.setl(dreg_0); break;
    case i386_Opcodes._setle: arch.setle(dreg_0); break;
    case i386_Opcodes._setna: arch.setna(dreg_0); break;
    case i386_Opcodes._setnae: arch.setnae(dreg_0); break;
    case i386_Opcodes._setnb: arch.setnb(dreg_0); break;
    case i386_Opcodes._setnbe: arch.setnbe(dreg_0); break;
    case i386_Opcodes._setnc: arch.setnc(dreg_0); break;
    case i386_Opcodes._setne: arch.setne(dreg_0); break;
    case i386_Opcodes._setng: arch.setng(dreg_0); break;
    case i386_Opcodes._setnge: arch.setnge(dreg_0); break;
    case i386_Opcodes._setnl: arch.setnl(dreg_0); break;
    case i386_Opcodes._setnle: arch.setnle(dreg_0); break;
    case i386_Opcodes._setno: arch.setno(dreg_0); break;
    case i386_Opcodes._setnp: arch.setnp(dreg_0); break;
    case i386_Opcodes._setns: arch.setns(dreg_0); break;
    case i386_Opcodes._setnz: arch.setnz(dreg_0); break;
    case i386_Opcodes._seto: arch.seto(dreg_0); break;
    case i386_Opcodes._setp: arch.setp(dreg_0); break;
    case i386_Opcodes._setpe: arch.setpe(dreg_0); break;
    case i386_Opcodes._setpo: arch.setpo(dreg_0); break;
    case i386_Opcodes._sets: arch.sets(dreg_0); break;
    case i386_Opcodes._setz: arch.setz(dreg_0); break;
    case i386_Opcodes._sldt: arch.sldt(dreg_0); break;
    case i386_Opcodes._smsw: arch.smsw(dreg_0); break;
    case i386_Opcodes._str: arch.str(dreg_0); break;
    case i386_Opcodes._decb: arch.decb(dreg_0); break;
    case i386_Opcodes._decw: arch.decw(dreg_0); break;
    case i386_Opcodes._decl: arch.decl(dreg_0); break;
    case i386_Opcodes._faddp: arch.faddp(dreg_0); break;
    case i386_Opcodes._fdivp: arch.fdivp(dreg_0); break;
    case i386_Opcodes._fdivrp: arch.fdivrp(dreg_0); break;
    case i386_Opcodes._fmulp: arch.fmulp(dreg_0); break;
    case i386_Opcodes._fsubp: arch.fsubp(dreg_0); break;
    case i386_Opcodes._fsubrp: arch.fsubrp(dreg_0); break;
    case i386_Opcodes._fxch: arch.fxch(dreg_0); break;
    case i386_Opcodes._incb: arch.incb(dreg_0); break;
    case i386_Opcodes._incw: arch.incw(dreg_0); break;
    case i386_Opcodes._incl: arch.incl(dreg_0); break;
    case i386_Opcodes._negb: arch.negb(dreg_0); break;
    case i386_Opcodes._negw: arch.negw(dreg_0); break;
    case i386_Opcodes._negl: arch.negl(dreg_0); break;
    case i386_Opcodes._notb: arch.notb(dreg_0); break;
    case i386_Opcodes._notw: arch.notw(dreg_0); break;
    case i386_Opcodes._notl: arch.notl(dreg_0); break;
    case i386_Opcodes._rclb: arch.rclb(dreg_0); break;
    case i386_Opcodes._rclw: arch.rclw(dreg_0); break;
    case i386_Opcodes._rcll: arch.rcll(dreg_0); break;
    case i386_Opcodes._rcrb: arch.rcrb(dreg_0); break;
    case i386_Opcodes._rcrw: arch.rcrw(dreg_0); break;
    case i386_Opcodes._rcrl: arch.rcrl(dreg_0); break;
    case i386_Opcodes._rolb: arch.rolb(dreg_0); break;
    case i386_Opcodes._rolw: arch.rolw(dreg_0); break;
    case i386_Opcodes._roll: arch.roll(dreg_0); break;
    case i386_Opcodes._rorb: arch.rorb(dreg_0); break;
    case i386_Opcodes._rorw: arch.rorw(dreg_0); break;
    case i386_Opcodes._rorl: arch.rorl(dreg_0); break;
    case i386_Opcodes._salb: arch.salb(dreg_0); break;
    case i386_Opcodes._salw: arch.salw(dreg_0); break;
    case i386_Opcodes._sall: arch.sall(dreg_0); break;
    case i386_Opcodes._sarb: arch.sarb(dreg_0); break;
    case i386_Opcodes._sarw: arch.sarw(dreg_0); break;
    case i386_Opcodes._sarl: arch.sarl(dreg_0); break;
    case i386_Opcodes._shlb: arch.shlb(dreg_0); break;
    case i386_Opcodes._shlw: arch.shlw(dreg_0); break;
    case i386_Opcodes._shll: arch.shll(dreg_0); break;
    case i386_Opcodes._shrb: arch.shrb(dreg_0); break;
    case i386_Opcodes._shrw: arch.shrw(dreg_0); break;
    case i386_Opcodes._shrl: arch.shrl(dreg_0); break;
    }
  }

  private void emit3(i386_Architecture arch, InstructionStatement insn) {
    int imm_0 = insn.imm[0];
    switch (index) {
    case i386_Opcodes._inb: arch.inb(imm_0); break;
    case i386_Opcodes._inw: arch.inw(imm_0); break;
    case i386_Opcodes._inl: arch.inl(imm_0); break;
    case i386_Opcodes._inti: arch.inti(imm_0); break;
    case i386_Opcodes._outb: arch.outb(imm_0); break;
    case i386_Opcodes._outw: arch.outw(imm_0); break;
    case i386_Opcodes._outl: arch.outl(imm_0); break;
    case i386_Opcodes._pushw: arch.pushw(imm_0); break;
    case i386_Opcodes._pushl: arch.pushl(imm_0); break;
    case i386_Opcodes._ret: arch.ret(imm_0); break;
    case i386_Opcodes._retf: arch.retf(imm_0); break;
    }
  }

  private void emit4(i386_Architecture arch, InstructionStatement insn) {
    Addr lmem_0 = insn.lmem[0];
    switch (index) {
    case i386_Opcodes._callw: arch.callw(lmem_0); break;
    case i386_Opcodes._calll: arch.calll(lmem_0); break;
    case i386_Opcodes._callfw: arch.callfw(lmem_0); break;
    case i386_Opcodes._callfl: arch.callfl(lmem_0); break;
    case i386_Opcodes._decb: arch.decb(lmem_0); break;
    case i386_Opcodes._decw: arch.decw(lmem_0); break;
    case i386_Opcodes._decl: arch.decl(lmem_0); break;
    case i386_Opcodes._divb: arch.divb(lmem_0); break;
    case i386_Opcodes._divw: arch.divw(lmem_0); break;
    case i386_Opcodes._divl: arch.divl(lmem_0); break;
    case i386_Opcodes._fadds: arch.fadds(lmem_0); break;
    case i386_Opcodes._faddl: arch.faddl(lmem_0); break;
    case i386_Opcodes._fiaddw: arch.fiaddw(lmem_0); break;
    case i386_Opcodes._fiaddl: arch.fiaddl(lmem_0); break;
    case i386_Opcodes._fbld: arch.fbld(lmem_0); break;
    case i386_Opcodes._fbstp: arch.fbstp(lmem_0); break;
    case i386_Opcodes._fcoms: arch.fcoms(lmem_0); break;
    case i386_Opcodes._fcoml: arch.fcoml(lmem_0); break;
    case i386_Opcodes._fcomps: arch.fcomps(lmem_0); break;
    case i386_Opcodes._fcompl: arch.fcompl(lmem_0); break;
    case i386_Opcodes._fdivs: arch.fdivs(lmem_0); break;
    case i386_Opcodes._fdivl: arch.fdivl(lmem_0); break;
    case i386_Opcodes._fidivw: arch.fidivw(lmem_0); break;
    case i386_Opcodes._fidivl: arch.fidivl(lmem_0); break;
    case i386_Opcodes._fdivrs: arch.fdivrs(lmem_0); break;
    case i386_Opcodes._fdivrl: arch.fdivrl(lmem_0); break;
    case i386_Opcodes._fidivrw: arch.fidivrw(lmem_0); break;
    case i386_Opcodes._fidivrl: arch.fidivrl(lmem_0); break;
    case i386_Opcodes._ficomw: arch.ficomw(lmem_0); break;
    case i386_Opcodes._ficoml: arch.ficoml(lmem_0); break;
    case i386_Opcodes._ficompw: arch.ficompw(lmem_0); break;
    case i386_Opcodes._ficompl: arch.ficompl(lmem_0); break;
    case i386_Opcodes._fildw: arch.fildw(lmem_0); break;
    case i386_Opcodes._fildl: arch.fildl(lmem_0); break;
    case i386_Opcodes._fildll: arch.fildll(lmem_0); break;
    case i386_Opcodes._fistw: arch.fistw(lmem_0); break;
    case i386_Opcodes._fistl: arch.fistl(lmem_0); break;
    case i386_Opcodes._fistpw: arch.fistpw(lmem_0); break;
    case i386_Opcodes._fistpl: arch.fistpl(lmem_0); break;
    case i386_Opcodes._fistpll: arch.fistpll(lmem_0); break;
    case i386_Opcodes._flds: arch.flds(lmem_0); break;
    case i386_Opcodes._fldl: arch.fldl(lmem_0); break;
    case i386_Opcodes._fldt: arch.fldt(lmem_0); break;
    case i386_Opcodes._fldcw: arch.fldcw(lmem_0); break;
    case i386_Opcodes._fldenv: arch.fldenv(lmem_0); break;
    case i386_Opcodes._fmuls: arch.fmuls(lmem_0); break;
    case i386_Opcodes._fmull: arch.fmull(lmem_0); break;
    case i386_Opcodes._fimulw: arch.fimulw(lmem_0); break;
    case i386_Opcodes._fimull: arch.fimull(lmem_0); break;
    case i386_Opcodes._frstor: arch.frstor(lmem_0); break;
    case i386_Opcodes._fsave: arch.fsave(lmem_0); break;
    case i386_Opcodes._fnsave: arch.fnsave(lmem_0); break;
    case i386_Opcodes._fsts: arch.fsts(lmem_0); break;
    case i386_Opcodes._fstl: arch.fstl(lmem_0); break;
    case i386_Opcodes._fstps: arch.fstps(lmem_0); break;
    case i386_Opcodes._fstpl: arch.fstpl(lmem_0); break;
    case i386_Opcodes._fstpt: arch.fstpt(lmem_0); break;
    case i386_Opcodes._fstcw: arch.fstcw(lmem_0); break;
    case i386_Opcodes._fnstcw: arch.fnstcw(lmem_0); break;
    case i386_Opcodes._fstenv: arch.fstenv(lmem_0); break;
    case i386_Opcodes._fnstenv: arch.fnstenv(lmem_0); break;
    case i386_Opcodes._fstsw: arch.fstsw(lmem_0); break;
    case i386_Opcodes._fnstsw: arch.fnstsw(lmem_0); break;
    case i386_Opcodes._fsubs: arch.fsubs(lmem_0); break;
    case i386_Opcodes._fsubl: arch.fsubl(lmem_0); break;
    case i386_Opcodes._fisubw: arch.fisubw(lmem_0); break;
    case i386_Opcodes._fisubl: arch.fisubl(lmem_0); break;
    case i386_Opcodes._fsubrs: arch.fsubrs(lmem_0); break;
    case i386_Opcodes._fsubrl: arch.fsubrl(lmem_0); break;
    case i386_Opcodes._fisubrw: arch.fisubrw(lmem_0); break;
    case i386_Opcodes._fisubrl: arch.fisubrl(lmem_0); break;
    case i386_Opcodes._idivb: arch.idivb(lmem_0); break;
    case i386_Opcodes._idivw: arch.idivw(lmem_0); break;
    case i386_Opcodes._idivl: arch.idivl(lmem_0); break;
    case i386_Opcodes._imulb: arch.imulb(lmem_0); break;
    case i386_Opcodes._imulw: arch.imulw(lmem_0); break;
    case i386_Opcodes._imull: arch.imull(lmem_0); break;
    case i386_Opcodes._incb: arch.incb(lmem_0); break;
    case i386_Opcodes._incw: arch.incw(lmem_0); break;
    case i386_Opcodes._incl: arch.incl(lmem_0); break;
    case i386_Opcodes._jmpw: arch.jmpw(lmem_0); break;
    case i386_Opcodes._jmpl: arch.jmpl(lmem_0); break;
    case i386_Opcodes._jmpfw: arch.jmpfw(lmem_0); break;
    case i386_Opcodes._jmpfl: arch.jmpfl(lmem_0); break;
    case i386_Opcodes._lgdt: arch.lgdt(lmem_0); break;
    case i386_Opcodes._lidt: arch.lidt(lmem_0); break;
    case i386_Opcodes._lldt: arch.lldt(lmem_0); break;
    case i386_Opcodes._lmsw: arch.lmsw(lmem_0); break;
    case i386_Opcodes._ltr: arch.ltr(lmem_0); break;
    case i386_Opcodes._mulb: arch.mulb(lmem_0); break;
    case i386_Opcodes._mulw: arch.mulw(lmem_0); break;
    case i386_Opcodes._mull: arch.mull(lmem_0); break;
    case i386_Opcodes._negb: arch.negb(lmem_0); break;
    case i386_Opcodes._negw: arch.negw(lmem_0); break;
    case i386_Opcodes._negl: arch.negl(lmem_0); break;
    case i386_Opcodes._notb: arch.notb(lmem_0); break;
    case i386_Opcodes._notw: arch.notw(lmem_0); break;
    case i386_Opcodes._notl: arch.notl(lmem_0); break;
    case i386_Opcodes._popw: arch.popw(lmem_0); break;
    case i386_Opcodes._popl: arch.popl(lmem_0); break;
    case i386_Opcodes._pushw: arch.pushw(lmem_0); break;
    case i386_Opcodes._pushl: arch.pushl(lmem_0); break;
    case i386_Opcodes._rclb: arch.rclb(lmem_0); break;
    case i386_Opcodes._rclw: arch.rclw(lmem_0); break;
    case i386_Opcodes._rcll: arch.rcll(lmem_0); break;
    case i386_Opcodes._rcrb: arch.rcrb(lmem_0); break;
    case i386_Opcodes._rcrw: arch.rcrw(lmem_0); break;
    case i386_Opcodes._rcrl: arch.rcrl(lmem_0); break;
    case i386_Opcodes._rolb: arch.rolb(lmem_0); break;
    case i386_Opcodes._rolw: arch.rolw(lmem_0); break;
    case i386_Opcodes._roll: arch.roll(lmem_0); break;
    case i386_Opcodes._rorb: arch.rorb(lmem_0); break;
    case i386_Opcodes._rorw: arch.rorw(lmem_0); break;
    case i386_Opcodes._rorl: arch.rorl(lmem_0); break;
    case i386_Opcodes._salb: arch.salb(lmem_0); break;
    case i386_Opcodes._salw: arch.salw(lmem_0); break;
    case i386_Opcodes._sall: arch.sall(lmem_0); break;
    case i386_Opcodes._sarb: arch.sarb(lmem_0); break;
    case i386_Opcodes._sarw: arch.sarw(lmem_0); break;
    case i386_Opcodes._sarl: arch.sarl(lmem_0); break;
    case i386_Opcodes._shlb: arch.shlb(lmem_0); break;
    case i386_Opcodes._shlw: arch.shlw(lmem_0); break;
    case i386_Opcodes._shll: arch.shll(lmem_0); break;
    case i386_Opcodes._shrb: arch.shrb(lmem_0); break;
    case i386_Opcodes._shrw: arch.shrw(lmem_0); break;
    case i386_Opcodes._shrl: arch.shrl(lmem_0); break;
    case i386_Opcodes._seta: arch.seta(lmem_0); break;
    case i386_Opcodes._setae: arch.setae(lmem_0); break;
    case i386_Opcodes._setb: arch.setb(lmem_0); break;
    case i386_Opcodes._setbe: arch.setbe(lmem_0); break;
    case i386_Opcodes._setc: arch.setc(lmem_0); break;
    case i386_Opcodes._sete: arch.sete(lmem_0); break;
    case i386_Opcodes._setg: arch.setg(lmem_0); break;
    case i386_Opcodes._setge: arch.setge(lmem_0); break;
    case i386_Opcodes._setl: arch.setl(lmem_0); break;
    case i386_Opcodes._setle: arch.setle(lmem_0); break;
    case i386_Opcodes._setna: arch.setna(lmem_0); break;
    case i386_Opcodes._setnae: arch.setnae(lmem_0); break;
    case i386_Opcodes._setnb: arch.setnb(lmem_0); break;
    case i386_Opcodes._setnbe: arch.setnbe(lmem_0); break;
    case i386_Opcodes._setnc: arch.setnc(lmem_0); break;
    case i386_Opcodes._setne: arch.setne(lmem_0); break;
    case i386_Opcodes._setng: arch.setng(lmem_0); break;
    case i386_Opcodes._setnge: arch.setnge(lmem_0); break;
    case i386_Opcodes._setnl: arch.setnl(lmem_0); break;
    case i386_Opcodes._setnle: arch.setnle(lmem_0); break;
    case i386_Opcodes._setno: arch.setno(lmem_0); break;
    case i386_Opcodes._setnp: arch.setnp(lmem_0); break;
    case i386_Opcodes._setns: arch.setns(lmem_0); break;
    case i386_Opcodes._setnz: arch.setnz(lmem_0); break;
    case i386_Opcodes._seto: arch.seto(lmem_0); break;
    case i386_Opcodes._setp: arch.setp(lmem_0); break;
    case i386_Opcodes._setpe: arch.setpe(lmem_0); break;
    case i386_Opcodes._setpo: arch.setpo(lmem_0); break;
    case i386_Opcodes._sets: arch.sets(lmem_0); break;
    case i386_Opcodes._setz: arch.setz(lmem_0); break;
    case i386_Opcodes._sgdt: arch.sgdt(lmem_0); break;
    case i386_Opcodes._sidt: arch.sidt(lmem_0); break;
    case i386_Opcodes._sldt: arch.sldt(lmem_0); break;
    case i386_Opcodes._smsw: arch.smsw(lmem_0); break;
    case i386_Opcodes._str: arch.str(lmem_0); break;
    case i386_Opcodes._verr: arch.verr(lmem_0); break;
    case i386_Opcodes._verw: arch.verw(lmem_0); break;
    }
  }

  private void emit5(i386_Architecture arch, InstructionStatement insn) {
    Imm jmm_0 = insn.jmm[0];
    switch (index) {
    case i386_Opcodes._call: arch.call(jmm_0); break;
    case i386_Opcodes._callf: arch.callf(jmm_0); break;
    case i386_Opcodes._ja: arch.ja(jmm_0); break;
    case i386_Opcodes._jae: arch.jae(jmm_0); break;
    case i386_Opcodes._jb: arch.jb(jmm_0); break;
    case i386_Opcodes._jbe: arch.jbe(jmm_0); break;
    case i386_Opcodes._jc: arch.jc(jmm_0); break;
    case i386_Opcodes._jcxe: arch.jcxe(jmm_0); break;
    case i386_Opcodes._jcxz: arch.jcxz(jmm_0); break;
    case i386_Opcodes._jecxe: arch.jecxe(jmm_0); break;
    case i386_Opcodes._jecxz: arch.jecxz(jmm_0); break;
    case i386_Opcodes._je: arch.je(jmm_0); break;
    case i386_Opcodes._jg: arch.jg(jmm_0); break;
    case i386_Opcodes._jge: arch.jge(jmm_0); break;
    case i386_Opcodes._jl: arch.jl(jmm_0); break;
    case i386_Opcodes._jle: arch.jle(jmm_0); break;
    case i386_Opcodes._jna: arch.jna(jmm_0); break;
    case i386_Opcodes._jnae: arch.jnae(jmm_0); break;
    case i386_Opcodes._jnb: arch.jnb(jmm_0); break;
    case i386_Opcodes._jnbe: arch.jnbe(jmm_0); break;
    case i386_Opcodes._jnc: arch.jnc(jmm_0); break;
    case i386_Opcodes._jne: arch.jne(jmm_0); break;
    case i386_Opcodes._jng: arch.jng(jmm_0); break;
    case i386_Opcodes._jnge: arch.jnge(jmm_0); break;
    case i386_Opcodes._jnl: arch.jnl(jmm_0); break;
    case i386_Opcodes._jnle: arch.jnle(jmm_0); break;
    case i386_Opcodes._jno: arch.jno(jmm_0); break;
    case i386_Opcodes._jnp: arch.jnp(jmm_0); break;
    case i386_Opcodes._jns: arch.jns(jmm_0); break;
    case i386_Opcodes._jnz: arch.jnz(jmm_0); break;
    case i386_Opcodes._jo: arch.jo(jmm_0); break;
    case i386_Opcodes._jp: arch.jp(jmm_0); break;
    case i386_Opcodes._jpe: arch.jpe(jmm_0); break;
    case i386_Opcodes._jpo: arch.jpo(jmm_0); break;
    case i386_Opcodes._js: arch.js(jmm_0); break;
    case i386_Opcodes._jz: arch.jz(jmm_0); break;
    case i386_Opcodes._jmp: arch.jmp(jmm_0); break;
    case i386_Opcodes._jmpf: arch.jmpf(jmm_0); break;
    case i386_Opcodes._loop: arch.loop(jmm_0); break;
    case i386_Opcodes._loope: arch.loope(jmm_0); break;
    case i386_Opcodes._loopz: arch.loopz(jmm_0); break;
    case i386_Opcodes._loopne: arch.loopne(jmm_0); break;
    case i386_Opcodes._loopnz: arch.loopnz(jmm_0); break;
    case i386_Opcodes._pushw: arch.pushw(jmm_0); break;
    case i386_Opcodes._pushl: arch.pushl(jmm_0); break;
    }
  }

  private void emit6(i386_Architecture arch, InstructionStatement insn) {
    Reg ureg_0 = insn.ureg[0];
    Reg ureg_1 = insn.ureg[1];
    switch (index) {
    case i386_Opcodes._btw: arch.btw(ureg_0, ureg_1); break;
    case i386_Opcodes._btl: arch.btl(ureg_0, ureg_1); break;
    case i386_Opcodes._cmpb: arch.cmpb(ureg_0, ureg_1); break;
    case i386_Opcodes._cmpw: arch.cmpw(ureg_0, ureg_1); break;
    case i386_Opcodes._cmpl: arch.cmpl(ureg_0, ureg_1); break;
    case i386_Opcodes._testb: arch.testb(ureg_0, ureg_1); break;
    case i386_Opcodes._testw: arch.testw(ureg_0, ureg_1); break;
    case i386_Opcodes._testl: arch.testl(ureg_0, ureg_1); break;
    }
  }

  private void emit7(i386_Architecture arch, InstructionStatement insn) {
    Reg ureg_0 = insn.ureg[0];
    Reg dreg_0 = insn.dreg[0];
    switch (index) {
    case i386_Opcodes._bsfw: arch.bsfw(ureg_0, dreg_0); break;
    case i386_Opcodes._bsfl: arch.bsfl(ureg_0, dreg_0); break;
    case i386_Opcodes._bsrw: arch.bsrw(ureg_0, dreg_0); break;
    case i386_Opcodes._bsrl: arch.bsrl(ureg_0, dreg_0); break;
    case i386_Opcodes._larw: arch.larw(ureg_0, dreg_0); break;
    case i386_Opcodes._larl: arch.larl(ureg_0, dreg_0); break;
    case i386_Opcodes._lslw: arch.lslw(ureg_0, dreg_0); break;
    case i386_Opcodes._lsll: arch.lsll(ureg_0, dreg_0); break;
    case i386_Opcodes._movb: arch.movb(ureg_0, dreg_0); break;
    case i386_Opcodes._movw: arch.movw(ureg_0, dreg_0); break;
    case i386_Opcodes._movl: arch.movl(ureg_0, dreg_0); break;
    case i386_Opcodes._movws: arch.movws(ureg_0, dreg_0); break;
    case i386_Opcodes._movsw: arch.movsw(ureg_0, dreg_0); break;
    case i386_Opcodes._movlc: arch.movlc(ureg_0, dreg_0); break;
    case i386_Opcodes._movcl: arch.movcl(ureg_0, dreg_0); break;
    case i386_Opcodes._movlg: arch.movlg(ureg_0, dreg_0); break;
    case i386_Opcodes._movgl: arch.movgl(ureg_0, dreg_0); break;
    case i386_Opcodes._movlt: arch.movlt(ureg_0, dreg_0); break;
    case i386_Opcodes._movtl: arch.movtl(ureg_0, dreg_0); break;
    case i386_Opcodes._movsxbw: arch.movsxbw(ureg_0, dreg_0); break;
    case i386_Opcodes._movsxbl: arch.movsxbl(ureg_0, dreg_0); break;
    case i386_Opcodes._movsxwl: arch.movsxwl(ureg_0, dreg_0); break;
    case i386_Opcodes._movzxbw: arch.movzxbw(ureg_0, dreg_0); break;
    case i386_Opcodes._movzxbl: arch.movzxbl(ureg_0, dreg_0); break;
    case i386_Opcodes._movzxwl: arch.movzxwl(ureg_0, dreg_0); break;
    case i386_Opcodes._adcb: arch.adcb(ureg_0, dreg_0); break;
    case i386_Opcodes._adcw: arch.adcw(ureg_0, dreg_0); break;
    case i386_Opcodes._adcl: arch.adcl(ureg_0, dreg_0); break;
    case i386_Opcodes._addb: arch.addb(ureg_0, dreg_0); break;
    case i386_Opcodes._addw: arch.addw(ureg_0, dreg_0); break;
    case i386_Opcodes._addl: arch.addl(ureg_0, dreg_0); break;
    case i386_Opcodes._andb: arch.andb(ureg_0, dreg_0); break;
    case i386_Opcodes._andw: arch.andw(ureg_0, dreg_0); break;
    case i386_Opcodes._andl: arch.andl(ureg_0, dreg_0); break;
    case i386_Opcodes._arpl: arch.arpl(ureg_0, dreg_0); break;
    case i386_Opcodes._btcw: arch.btcw(ureg_0, dreg_0); break;
    case i386_Opcodes._btcl: arch.btcl(ureg_0, dreg_0); break;
    case i386_Opcodes._btrw: arch.btrw(ureg_0, dreg_0); break;
    case i386_Opcodes._btrl: arch.btrl(ureg_0, dreg_0); break;
    case i386_Opcodes._btsw: arch.btsw(ureg_0, dreg_0); break;
    case i386_Opcodes._btsl: arch.btsl(ureg_0, dreg_0); break;
    case i386_Opcodes._fadd: arch.fadd(ureg_0, dreg_0); break;
    case i386_Opcodes._fdiv: arch.fdiv(ureg_0, dreg_0); break;
    case i386_Opcodes._fdivr: arch.fdivr(ureg_0, dreg_0); break;
    case i386_Opcodes._fmul: arch.fmul(ureg_0, dreg_0); break;
    case i386_Opcodes._fsub: arch.fsub(ureg_0, dreg_0); break;
    case i386_Opcodes._fsubr: arch.fsubr(ureg_0, dreg_0); break;
    case i386_Opcodes._imulw: arch.imulw(ureg_0, dreg_0); break;
    case i386_Opcodes._imull: arch.imull(ureg_0, dreg_0); break;
    case i386_Opcodes._orb: arch.orb(ureg_0, dreg_0); break;
    case i386_Opcodes._orw: arch.orw(ureg_0, dreg_0); break;
    case i386_Opcodes._orl: arch.orl(ureg_0, dreg_0); break;
    case i386_Opcodes._sbbb: arch.sbbb(ureg_0, dreg_0); break;
    case i386_Opcodes._sbbw: arch.sbbw(ureg_0, dreg_0); break;
    case i386_Opcodes._sbbl: arch.sbbl(ureg_0, dreg_0); break;
    case i386_Opcodes._shldw: arch.shldw(ureg_0, dreg_0); break;
    case i386_Opcodes._shldl: arch.shldl(ureg_0, dreg_0); break;
    case i386_Opcodes._shrdw: arch.shrdw(ureg_0, dreg_0); break;
    case i386_Opcodes._shrdl: arch.shrdl(ureg_0, dreg_0); break;
    case i386_Opcodes._subb: arch.subb(ureg_0, dreg_0); break;
    case i386_Opcodes._subw: arch.subw(ureg_0, dreg_0); break;
    case i386_Opcodes._subl: arch.subl(ureg_0, dreg_0); break;
    case i386_Opcodes._xorb: arch.xorb(ureg_0, dreg_0); break;
    case i386_Opcodes._xorw: arch.xorw(ureg_0, dreg_0); break;
    case i386_Opcodes._xorl: arch.xorl(ureg_0, dreg_0); break;
    }
  }

  private void emit8(i386_Architecture arch, InstructionStatement insn) {
    Reg dreg_0 = insn.dreg[0];
    Reg dreg_1 = insn.dreg[1];
    switch (index) {
    case i386_Opcodes._xchgb: arch.xchgb(dreg_0, dreg_1); break;
    case i386_Opcodes._xchgw: arch.xchgw(dreg_0, dreg_1); break;
    case i386_Opcodes._xchgl: arch.xchgl(dreg_0, dreg_1); break;
    }
  }

  private void emit9(i386_Architecture arch, InstructionStatement insn) {
    int imm_0 = insn.imm[0];
    Reg ureg_0 = insn.ureg[0];
    switch (index) {
    case i386_Opcodes._btw: arch.btw(imm_0, ureg_0); break;
    case i386_Opcodes._btl: arch.btl(imm_0, ureg_0); break;
    case i386_Opcodes._cmpb: arch.cmpb(imm_0, ureg_0); break;
    case i386_Opcodes._cmpw: arch.cmpw(imm_0, ureg_0); break;
    case i386_Opcodes._cmpl: arch.cmpl(imm_0, ureg_0); break;
    case i386_Opcodes._testb: arch.testb(imm_0, ureg_0); break;
    case i386_Opcodes._testw: arch.testw(imm_0, ureg_0); break;
    case i386_Opcodes._testl: arch.testl(imm_0, ureg_0); break;
    }
  }

  private void emit10(i386_Architecture arch, InstructionStatement insn) {
    int imm_0 = insn.imm[0];
    Reg dreg_0 = insn.dreg[0];
    switch (index) {
    case i386_Opcodes._movb: arch.movb(imm_0, dreg_0); break;
    case i386_Opcodes._movw: arch.movw(imm_0, dreg_0); break;
    case i386_Opcodes._movl: arch.movl(imm_0, dreg_0); break;
    case i386_Opcodes._adcb: arch.adcb(imm_0, dreg_0); break;
    case i386_Opcodes._adcw: arch.adcw(imm_0, dreg_0); break;
    case i386_Opcodes._adcl: arch.adcl(imm_0, dreg_0); break;
    case i386_Opcodes._addb: arch.addb(imm_0, dreg_0); break;
    case i386_Opcodes._addw: arch.addw(imm_0, dreg_0); break;
    case i386_Opcodes._addl: arch.addl(imm_0, dreg_0); break;
    case i386_Opcodes._andb: arch.andb(imm_0, dreg_0); break;
    case i386_Opcodes._andw: arch.andw(imm_0, dreg_0); break;
    case i386_Opcodes._andl: arch.andl(imm_0, dreg_0); break;
    case i386_Opcodes._btcw: arch.btcw(imm_0, dreg_0); break;
    case i386_Opcodes._btcl: arch.btcl(imm_0, dreg_0); break;
    case i386_Opcodes._btrw: arch.btrw(imm_0, dreg_0); break;
    case i386_Opcodes._btrl: arch.btrl(imm_0, dreg_0); break;
    case i386_Opcodes._btsw: arch.btsw(imm_0, dreg_0); break;
    case i386_Opcodes._btsl: arch.btsl(imm_0, dreg_0); break;
    case i386_Opcodes._imulw: arch.imulw(imm_0, dreg_0); break;
    case i386_Opcodes._imull: arch.imull(imm_0, dreg_0); break;
    case i386_Opcodes._orb: arch.orb(imm_0, dreg_0); break;
    case i386_Opcodes._orw: arch.orw(imm_0, dreg_0); break;
    case i386_Opcodes._orl: arch.orl(imm_0, dreg_0); break;
    case i386_Opcodes._rclb: arch.rclb(imm_0, dreg_0); break;
    case i386_Opcodes._rclw: arch.rclw(imm_0, dreg_0); break;
    case i386_Opcodes._rcll: arch.rcll(imm_0, dreg_0); break;
    case i386_Opcodes._rcrb: arch.rcrb(imm_0, dreg_0); break;
    case i386_Opcodes._rcrw: arch.rcrw(imm_0, dreg_0); break;
    case i386_Opcodes._rcrl: arch.rcrl(imm_0, dreg_0); break;
    case i386_Opcodes._rolb: arch.rolb(imm_0, dreg_0); break;
    case i386_Opcodes._rolw: arch.rolw(imm_0, dreg_0); break;
    case i386_Opcodes._roll: arch.roll(imm_0, dreg_0); break;
    case i386_Opcodes._rorb: arch.rorb(imm_0, dreg_0); break;
    case i386_Opcodes._rorw: arch.rorw(imm_0, dreg_0); break;
    case i386_Opcodes._rorl: arch.rorl(imm_0, dreg_0); break;
    case i386_Opcodes._salb: arch.salb(imm_0, dreg_0); break;
    case i386_Opcodes._salw: arch.salw(imm_0, dreg_0); break;
    case i386_Opcodes._sall: arch.sall(imm_0, dreg_0); break;
    case i386_Opcodes._sarb: arch.sarb(imm_0, dreg_0); break;
    case i386_Opcodes._sarw: arch.sarw(imm_0, dreg_0); break;
    case i386_Opcodes._sarl: arch.sarl(imm_0, dreg_0); break;
    case i386_Opcodes._shlb: arch.shlb(imm_0, dreg_0); break;
    case i386_Opcodes._shlw: arch.shlw(imm_0, dreg_0); break;
    case i386_Opcodes._shll: arch.shll(imm_0, dreg_0); break;
    case i386_Opcodes._shrb: arch.shrb(imm_0, dreg_0); break;
    case i386_Opcodes._shrw: arch.shrw(imm_0, dreg_0); break;
    case i386_Opcodes._shrl: arch.shrl(imm_0, dreg_0); break;
    case i386_Opcodes._sbbb: arch.sbbb(imm_0, dreg_0); break;
    case i386_Opcodes._sbbw: arch.sbbw(imm_0, dreg_0); break;
    case i386_Opcodes._sbbl: arch.sbbl(imm_0, dreg_0); break;
    case i386_Opcodes._subb: arch.subb(imm_0, dreg_0); break;
    case i386_Opcodes._subw: arch.subw(imm_0, dreg_0); break;
    case i386_Opcodes._subl: arch.subl(imm_0, dreg_0); break;
    case i386_Opcodes._xorb: arch.xorb(imm_0, dreg_0); break;
    case i386_Opcodes._xorw: arch.xorw(imm_0, dreg_0); break;
    case i386_Opcodes._xorl: arch.xorl(imm_0, dreg_0); break;
    }
  }

  private void emit11(i386_Architecture arch, InstructionStatement insn) {
    int imm_0 = insn.imm[0];
    int imm_1 = insn.imm[1];
    switch (index) {
    case i386_Opcodes._enter: arch.enter(imm_0, imm_1); break;
    }
  }

  private void emit12(i386_Architecture arch, InstructionStatement insn) {
    Reg ureg_0 = insn.ureg[0];
    Addr lmem_0 = insn.lmem[0];
    switch (index) {
    case i386_Opcodes._adcb: arch.adcb(ureg_0, lmem_0); break;
    case i386_Opcodes._adcw: arch.adcw(ureg_0, lmem_0); break;
    case i386_Opcodes._adcl: arch.adcl(ureg_0, lmem_0); break;
    case i386_Opcodes._addb: arch.addb(ureg_0, lmem_0); break;
    case i386_Opcodes._addw: arch.addw(ureg_0, lmem_0); break;
    case i386_Opcodes._addl: arch.addl(ureg_0, lmem_0); break;
    case i386_Opcodes._andb: arch.andb(ureg_0, lmem_0); break;
    case i386_Opcodes._andw: arch.andw(ureg_0, lmem_0); break;
    case i386_Opcodes._andl: arch.andl(ureg_0, lmem_0); break;
    case i386_Opcodes._arpl: arch.arpl(ureg_0, lmem_0); break;
    case i386_Opcodes._btw: arch.btw(ureg_0, lmem_0); break;
    case i386_Opcodes._btl: arch.btl(ureg_0, lmem_0); break;
    case i386_Opcodes._btcw: arch.btcw(ureg_0, lmem_0); break;
    case i386_Opcodes._btcl: arch.btcl(ureg_0, lmem_0); break;
    case i386_Opcodes._btrw: arch.btrw(ureg_0, lmem_0); break;
    case i386_Opcodes._btrl: arch.btrl(ureg_0, lmem_0); break;
    case i386_Opcodes._btsw: arch.btsw(ureg_0, lmem_0); break;
    case i386_Opcodes._btsl: arch.btsl(ureg_0, lmem_0); break;
    case i386_Opcodes._cmpb: arch.cmpb(ureg_0, lmem_0); break;
    case i386_Opcodes._cmpw: arch.cmpw(ureg_0, lmem_0); break;
    case i386_Opcodes._cmpl: arch.cmpl(ureg_0, lmem_0); break;
    case i386_Opcodes._movb: arch.movb(ureg_0, lmem_0); break;
    case i386_Opcodes._movw: arch.movw(ureg_0, lmem_0); break;
    case i386_Opcodes._movl: arch.movl(ureg_0, lmem_0); break;
    case i386_Opcodes._movsw: arch.movsw(ureg_0, lmem_0); break;
    case i386_Opcodes._orb: arch.orb(ureg_0, lmem_0); break;
    case i386_Opcodes._orw: arch.orw(ureg_0, lmem_0); break;
    case i386_Opcodes._orl: arch.orl(ureg_0, lmem_0); break;
    case i386_Opcodes._sbbb: arch.sbbb(ureg_0, lmem_0); break;
    case i386_Opcodes._sbbw: arch.sbbw(ureg_0, lmem_0); break;
    case i386_Opcodes._sbbl: arch.sbbl(ureg_0, lmem_0); break;
    case i386_Opcodes._shldw: arch.shldw(ureg_0, lmem_0); break;
    case i386_Opcodes._shldl: arch.shldl(ureg_0, lmem_0); break;
    case i386_Opcodes._shrdw: arch.shrdw(ureg_0, lmem_0); break;
    case i386_Opcodes._shrdl: arch.shrdl(ureg_0, lmem_0); break;
    case i386_Opcodes._subb: arch.subb(ureg_0, lmem_0); break;
    case i386_Opcodes._subw: arch.subw(ureg_0, lmem_0); break;
    case i386_Opcodes._subl: arch.subl(ureg_0, lmem_0); break;
    case i386_Opcodes._testb: arch.testb(ureg_0, lmem_0); break;
    case i386_Opcodes._testw: arch.testw(ureg_0, lmem_0); break;
    case i386_Opcodes._testl: arch.testl(ureg_0, lmem_0); break;
    case i386_Opcodes._xorb: arch.xorb(ureg_0, lmem_0); break;
    case i386_Opcodes._xorw: arch.xorw(ureg_0, lmem_0); break;
    case i386_Opcodes._xorl: arch.xorl(ureg_0, lmem_0); break;
    }
  }

  private void emit13(i386_Architecture arch, InstructionStatement insn) {
    Reg dreg_0 = insn.dreg[0];
    Addr lmem_0 = insn.lmem[0];
    switch (index) {
    case i386_Opcodes._xchgb: arch.xchgb(dreg_0, lmem_0); break;
    case i386_Opcodes._xchgw: arch.xchgw(dreg_0, lmem_0); break;
    case i386_Opcodes._xchgl: arch.xchgl(dreg_0, lmem_0); break;
    }
  }

  private void emit14(i386_Architecture arch, InstructionStatement insn) {
    int imm_0 = insn.imm[0];
    Addr lmem_0 = insn.lmem[0];
    switch (index) {
    case i386_Opcodes._adcb: arch.adcb(imm_0, lmem_0); break;
    case i386_Opcodes._adcw: arch.adcw(imm_0, lmem_0); break;
    case i386_Opcodes._adcl: arch.adcl(imm_0, lmem_0); break;
    case i386_Opcodes._addb: arch.addb(imm_0, lmem_0); break;
    case i386_Opcodes._addw: arch.addw(imm_0, lmem_0); break;
    case i386_Opcodes._addl: arch.addl(imm_0, lmem_0); break;
    case i386_Opcodes._andb: arch.andb(imm_0, lmem_0); break;
    case i386_Opcodes._andw: arch.andw(imm_0, lmem_0); break;
    case i386_Opcodes._andl: arch.andl(imm_0, lmem_0); break;
    case i386_Opcodes._btw: arch.btw(imm_0, lmem_0); break;
    case i386_Opcodes._btl: arch.btl(imm_0, lmem_0); break;
    case i386_Opcodes._btcw: arch.btcw(imm_0, lmem_0); break;
    case i386_Opcodes._btcl: arch.btcl(imm_0, lmem_0); break;
    case i386_Opcodes._btrw: arch.btrw(imm_0, lmem_0); break;
    case i386_Opcodes._btrl: arch.btrl(imm_0, lmem_0); break;
    case i386_Opcodes._btsw: arch.btsw(imm_0, lmem_0); break;
    case i386_Opcodes._btsl: arch.btsl(imm_0, lmem_0); break;
    case i386_Opcodes._cmpb: arch.cmpb(imm_0, lmem_0); break;
    case i386_Opcodes._cmpw: arch.cmpw(imm_0, lmem_0); break;
    case i386_Opcodes._cmpl: arch.cmpl(imm_0, lmem_0); break;
    case i386_Opcodes._movb: arch.movb(imm_0, lmem_0); break;
    case i386_Opcodes._movw: arch.movw(imm_0, lmem_0); break;
    case i386_Opcodes._movl: arch.movl(imm_0, lmem_0); break;
    case i386_Opcodes._orb: arch.orb(imm_0, lmem_0); break;
    case i386_Opcodes._orw: arch.orw(imm_0, lmem_0); break;
    case i386_Opcodes._orl: arch.orl(imm_0, lmem_0); break;
    case i386_Opcodes._rclb: arch.rclb(imm_0, lmem_0); break;
    case i386_Opcodes._rclw: arch.rclw(imm_0, lmem_0); break;
    case i386_Opcodes._rcll: arch.rcll(imm_0, lmem_0); break;
    case i386_Opcodes._rcrb: arch.rcrb(imm_0, lmem_0); break;
    case i386_Opcodes._rcrw: arch.rcrw(imm_0, lmem_0); break;
    case i386_Opcodes._rcrl: arch.rcrl(imm_0, lmem_0); break;
    case i386_Opcodes._rolb: arch.rolb(imm_0, lmem_0); break;
    case i386_Opcodes._rolw: arch.rolw(imm_0, lmem_0); break;
    case i386_Opcodes._roll: arch.roll(imm_0, lmem_0); break;
    case i386_Opcodes._rorb: arch.rorb(imm_0, lmem_0); break;
    case i386_Opcodes._rorw: arch.rorw(imm_0, lmem_0); break;
    case i386_Opcodes._rorl: arch.rorl(imm_0, lmem_0); break;
    case i386_Opcodes._salb: arch.salb(imm_0, lmem_0); break;
    case i386_Opcodes._salw: arch.salw(imm_0, lmem_0); break;
    case i386_Opcodes._sall: arch.sall(imm_0, lmem_0); break;
    case i386_Opcodes._sarb: arch.sarb(imm_0, lmem_0); break;
    case i386_Opcodes._sarw: arch.sarw(imm_0, lmem_0); break;
    case i386_Opcodes._sarl: arch.sarl(imm_0, lmem_0); break;
    case i386_Opcodes._shlb: arch.shlb(imm_0, lmem_0); break;
    case i386_Opcodes._shlw: arch.shlw(imm_0, lmem_0); break;
    case i386_Opcodes._shll: arch.shll(imm_0, lmem_0); break;
    case i386_Opcodes._shrb: arch.shrb(imm_0, lmem_0); break;
    case i386_Opcodes._shrw: arch.shrw(imm_0, lmem_0); break;
    case i386_Opcodes._shrl: arch.shrl(imm_0, lmem_0); break;
    case i386_Opcodes._sbbb: arch.sbbb(imm_0, lmem_0); break;
    case i386_Opcodes._sbbw: arch.sbbw(imm_0, lmem_0); break;
    case i386_Opcodes._sbbl: arch.sbbl(imm_0, lmem_0); break;
    case i386_Opcodes._subb: arch.subb(imm_0, lmem_0); break;
    case i386_Opcodes._subw: arch.subw(imm_0, lmem_0); break;
    case i386_Opcodes._subl: arch.subl(imm_0, lmem_0); break;
    case i386_Opcodes._testb: arch.testb(imm_0, lmem_0); break;
    case i386_Opcodes._testw: arch.testw(imm_0, lmem_0); break;
    case i386_Opcodes._testl: arch.testl(imm_0, lmem_0); break;
    case i386_Opcodes._xorb: arch.xorb(imm_0, lmem_0); break;
    case i386_Opcodes._xorw: arch.xorw(imm_0, lmem_0); break;
    case i386_Opcodes._xorl: arch.xorl(imm_0, lmem_0); break;
    }
  }

  private void emit15(i386_Architecture arch, InstructionStatement insn) {
    Reg ureg_0 = insn.ureg[0];
    Addr lmem_0 = insn.lmem[0];
    switch (index) {
    case i386_Opcodes._boundw: arch.boundw(lmem_0, ureg_0); break;
    case i386_Opcodes._boundl: arch.boundl(lmem_0, ureg_0); break;
    case i386_Opcodes._cmpb: arch.cmpb(lmem_0, ureg_0); break;
    case i386_Opcodes._cmpw: arch.cmpw(lmem_0, ureg_0); break;
    case i386_Opcodes._cmpl: arch.cmpl(lmem_0, ureg_0); break;
    }
  }

  private void emit16(i386_Architecture arch, InstructionStatement insn) {
    Reg dreg_0 = insn.dreg[0];
    Addr lmem_0 = insn.lmem[0];
    switch (index) {
    case i386_Opcodes._bsfw: arch.bsfw(lmem_0, dreg_0); break;
    case i386_Opcodes._bsfl: arch.bsfl(lmem_0, dreg_0); break;
    case i386_Opcodes._bsrw: arch.bsrw(lmem_0, dreg_0); break;
    case i386_Opcodes._bsrl: arch.bsrl(lmem_0, dreg_0); break;
    case i386_Opcodes._larw: arch.larw(lmem_0, dreg_0); break;
    case i386_Opcodes._larl: arch.larl(lmem_0, dreg_0); break;
    case i386_Opcodes._ldsw: arch.ldsw(lmem_0, dreg_0); break;
    case i386_Opcodes._ldsl: arch.ldsl(lmem_0, dreg_0); break;
    case i386_Opcodes._lssw: arch.lssw(lmem_0, dreg_0); break;
    case i386_Opcodes._lssl: arch.lssl(lmem_0, dreg_0); break;
    case i386_Opcodes._lesw: arch.lesw(lmem_0, dreg_0); break;
    case i386_Opcodes._lesl: arch.lesl(lmem_0, dreg_0); break;
    case i386_Opcodes._lfsw: arch.lfsw(lmem_0, dreg_0); break;
    case i386_Opcodes._lfsl: arch.lfsl(lmem_0, dreg_0); break;
    case i386_Opcodes._lgsw: arch.lgsw(lmem_0, dreg_0); break;
    case i386_Opcodes._lgsl: arch.lgsl(lmem_0, dreg_0); break;
    case i386_Opcodes._leaw: arch.leaw(lmem_0, dreg_0); break;
    case i386_Opcodes._leal: arch.leal(lmem_0, dreg_0); break;
    case i386_Opcodes._lslw: arch.lslw(lmem_0, dreg_0); break;
    case i386_Opcodes._lsll: arch.lsll(lmem_0, dreg_0); break;
    case i386_Opcodes._movb: arch.movb(lmem_0, dreg_0); break;
    case i386_Opcodes._movw: arch.movw(lmem_0, dreg_0); break;
    case i386_Opcodes._movl: arch.movl(lmem_0, dreg_0); break;
    case i386_Opcodes._movws: arch.movws(lmem_0, dreg_0); break;
    case i386_Opcodes._movsxbw: arch.movsxbw(lmem_0, dreg_0); break;
    case i386_Opcodes._movsxbl: arch.movsxbl(lmem_0, dreg_0); break;
    case i386_Opcodes._movsxwl: arch.movsxwl(lmem_0, dreg_0); break;
    case i386_Opcodes._movzxbw: arch.movzxbw(lmem_0, dreg_0); break;
    case i386_Opcodes._movzxbl: arch.movzxbl(lmem_0, dreg_0); break;
    case i386_Opcodes._movzxwl: arch.movzxwl(lmem_0, dreg_0); break;
    case i386_Opcodes._adcb: arch.adcb(lmem_0, dreg_0); break;
    case i386_Opcodes._adcw: arch.adcw(lmem_0, dreg_0); break;
    case i386_Opcodes._adcl: arch.adcl(lmem_0, dreg_0); break;
    case i386_Opcodes._addb: arch.addb(lmem_0, dreg_0); break;
    case i386_Opcodes._addw: arch.addw(lmem_0, dreg_0); break;
    case i386_Opcodes._addl: arch.addl(lmem_0, dreg_0); break;
    case i386_Opcodes._andb: arch.andb(lmem_0, dreg_0); break;
    case i386_Opcodes._andw: arch.andw(lmem_0, dreg_0); break;
    case i386_Opcodes._andl: arch.andl(lmem_0, dreg_0); break;
    case i386_Opcodes._imulw: arch.imulw(lmem_0, dreg_0); break;
    case i386_Opcodes._imull: arch.imull(lmem_0, dreg_0); break;
    case i386_Opcodes._orb: arch.orb(lmem_0, dreg_0); break;
    case i386_Opcodes._orw: arch.orw(lmem_0, dreg_0); break;
    case i386_Opcodes._orl: arch.orl(lmem_0, dreg_0); break;
    case i386_Opcodes._sbbb: arch.sbbb(lmem_0, dreg_0); break;
    case i386_Opcodes._sbbw: arch.sbbw(lmem_0, dreg_0); break;
    case i386_Opcodes._sbbl: arch.sbbl(lmem_0, dreg_0); break;
    case i386_Opcodes._subb: arch.subb(lmem_0, dreg_0); break;
    case i386_Opcodes._subw: arch.subw(lmem_0, dreg_0); break;
    case i386_Opcodes._subl: arch.subl(lmem_0, dreg_0); break;
    case i386_Opcodes._xchgb: arch.xchgb(lmem_0, dreg_0); break;
    case i386_Opcodes._xchgw: arch.xchgw(lmem_0, dreg_0); break;
    case i386_Opcodes._xchgl: arch.xchgl(lmem_0, dreg_0); break;
    case i386_Opcodes._xorb: arch.xorb(lmem_0, dreg_0); break;
    case i386_Opcodes._xorw: arch.xorw(lmem_0, dreg_0); break;
    case i386_Opcodes._xorl: arch.xorl(lmem_0, dreg_0); break;
    }
  }

  private void emit17(i386_Architecture arch, InstructionStatement insn) {
    Imm jmm_0 = insn.jmm[0];
    Reg ureg_0 = insn.ureg[0];
    switch (index) {
    case i386_Opcodes._cmpw: arch.cmpw(jmm_0, ureg_0); break;
    case i386_Opcodes._cmpl: arch.cmpl(jmm_0, ureg_0); break;
    case i386_Opcodes._testw: arch.testw(jmm_0, ureg_0); break;
    case i386_Opcodes._testl: arch.testl(jmm_0, ureg_0); break;
    }
  }

  private void emit18(i386_Architecture arch, InstructionStatement insn) {
    Imm jmm_0 = insn.jmm[0];
    Reg dreg_0 = insn.dreg[0];
    switch (index) {
    case i386_Opcodes._movw: arch.movw(jmm_0, dreg_0); break;
    case i386_Opcodes._movl: arch.movl(jmm_0, dreg_0); break;
    case i386_Opcodes._adcw: arch.adcw(jmm_0, dreg_0); break;
    case i386_Opcodes._adcl: arch.adcl(jmm_0, dreg_0); break;
    case i386_Opcodes._addw: arch.addw(jmm_0, dreg_0); break;
    case i386_Opcodes._addl: arch.addl(jmm_0, dreg_0); break;
    case i386_Opcodes._andw: arch.andw(jmm_0, dreg_0); break;
    case i386_Opcodes._andl: arch.andl(jmm_0, dreg_0); break;
    case i386_Opcodes._imulw: arch.imulw(jmm_0, dreg_0); break;
    case i386_Opcodes._imull: arch.imull(jmm_0, dreg_0); break;
    case i386_Opcodes._orw: arch.orw(jmm_0, dreg_0); break;
    case i386_Opcodes._orl: arch.orl(jmm_0, dreg_0); break;
    case i386_Opcodes._sbbw: arch.sbbw(jmm_0, dreg_0); break;
    case i386_Opcodes._sbbl: arch.sbbl(jmm_0, dreg_0); break;
    case i386_Opcodes._subw: arch.subw(jmm_0, dreg_0); break;
    case i386_Opcodes._subl: arch.subl(jmm_0, dreg_0); break;
    case i386_Opcodes._xorw: arch.xorw(jmm_0, dreg_0); break;
    case i386_Opcodes._xorl: arch.xorl(jmm_0, dreg_0); break;
    }
  }

  private void emit19(i386_Architecture arch, InstructionStatement insn) {
    Imm jmm_0 = insn.jmm[0];
    Addr lmem_0 = insn.lmem[0];
    switch (index) {
    case i386_Opcodes._adcw: arch.adcw(jmm_0, lmem_0); break;
    case i386_Opcodes._adcl: arch.adcl(jmm_0, lmem_0); break;
    case i386_Opcodes._addw: arch.addw(jmm_0, lmem_0); break;
    case i386_Opcodes._addl: arch.addl(jmm_0, lmem_0); break;
    case i386_Opcodes._andw: arch.andw(jmm_0, lmem_0); break;
    case i386_Opcodes._andl: arch.andl(jmm_0, lmem_0); break;
    case i386_Opcodes._cmpw: arch.cmpw(jmm_0, lmem_0); break;
    case i386_Opcodes._cmpl: arch.cmpl(jmm_0, lmem_0); break;
    case i386_Opcodes._movw: arch.movw(jmm_0, lmem_0); break;
    case i386_Opcodes._movl: arch.movl(jmm_0, lmem_0); break;
    case i386_Opcodes._orw: arch.orw(jmm_0, lmem_0); break;
    case i386_Opcodes._orl: arch.orl(jmm_0, lmem_0); break;
    case i386_Opcodes._sbbw: arch.sbbw(jmm_0, lmem_0); break;
    case i386_Opcodes._sbbl: arch.sbbl(jmm_0, lmem_0); break;
    case i386_Opcodes._subw: arch.subw(jmm_0, lmem_0); break;
    case i386_Opcodes._subl: arch.subl(jmm_0, lmem_0); break;
    case i386_Opcodes._testw: arch.testw(jmm_0, lmem_0); break;
    case i386_Opcodes._testl: arch.testl(jmm_0, lmem_0); break;
    case i386_Opcodes._xorw: arch.xorw(jmm_0, lmem_0); break;
    case i386_Opcodes._xorl: arch.xorl(jmm_0, lmem_0); break;
    }
  }

  private void emit20(i386_Architecture arch, InstructionStatement insn) {
    int imm_0 = insn.imm[0];
    Reg ureg_0 = insn.ureg[0];
    Reg dreg_0 = insn.dreg[0];
    switch (index) {
    case i386_Opcodes._imulw: arch.imulw(imm_0, ureg_0, dreg_0); break;
    case i386_Opcodes._imull: arch.imull(imm_0, ureg_0, dreg_0); break;
    case i386_Opcodes._shldw: arch.shldw(imm_0, ureg_0, dreg_0); break;
    case i386_Opcodes._shldl: arch.shldl(imm_0, ureg_0, dreg_0); break;
    case i386_Opcodes._shrdw: arch.shrdw(imm_0, ureg_0, dreg_0); break;
    case i386_Opcodes._shrdl: arch.shrdl(imm_0, ureg_0, dreg_0); break;
    }
  }

  private void emit21(i386_Architecture arch, InstructionStatement insn) {
    int imm_0 = insn.imm[0];
    Reg ureg_0 = insn.ureg[0];
    Addr lmem_0 = insn.lmem[0];
    switch (index) {
    case i386_Opcodes._shldw: arch.shldw(imm_0, ureg_0, lmem_0); break;
    case i386_Opcodes._shldl: arch.shldl(imm_0, ureg_0, lmem_0); break;
    case i386_Opcodes._shrdw: arch.shrdw(imm_0, ureg_0, lmem_0); break;
    case i386_Opcodes._shrdl: arch.shrdl(imm_0, ureg_0, lmem_0); break;
    }
  }

  private void emit22(i386_Architecture arch, InstructionStatement insn) {
    int imm_0 = insn.imm[0];
    Addr lmem_0 = insn.lmem[0];
    Reg dreg_0 = insn.dreg[0];
    switch (index) {
    case i386_Opcodes._imulw: arch.imulw(imm_0, lmem_0, dreg_0); break;
    case i386_Opcodes._imull: arch.imull(imm_0, lmem_0, dreg_0); break;
    }
  }

  private void emit23(i386_Architecture arch, InstructionStatement insn) {
    Imm jmm_0 = insn.jmm[0];
    Reg ureg_0 = insn.ureg[0];
    Reg dreg_0 = insn.dreg[0];
    switch (index) {
    case i386_Opcodes._imulw: arch.imulw(jmm_0, ureg_0, dreg_0); break;
    case i386_Opcodes._imull: arch.imull(jmm_0, ureg_0, dreg_0); break;
    }
  }

  private void emit24(i386_Architecture arch, InstructionStatement insn) {
    Imm jmm_0 = insn.jmm[0];
    Addr lmem_0 = insn.lmem[0];
    Reg dreg_0 = insn.dreg[0];
    switch (index) {
    case i386_Opcodes._imulw: arch.imulw(jmm_0, lmem_0, dreg_0); break;
    case i386_Opcodes._imull: arch.imull(jmm_0, lmem_0, dreg_0); break;
    }
  }

}

