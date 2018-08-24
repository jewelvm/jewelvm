/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Addr;
import jewel.core.bend.Assembler;
import jewel.core.bend.FlowAssembler;
import jewel.core.bend.Imm;
import jewel.core.bend.Opc;
import jewel.core.bend.Reg;
import jewel.core.bend.Symbol;

import java.util.Arrays;
import java.util.BitSet;
import java.util.StringTokenizer;

public class i386_FlowAssembler extends FlowAssembler implements i386_Architecture {

  public i386_FlowAssembler() { }

  /* no-register addressing modes */
  public final Addr addr(Symbol symbol) { return addr(0, symbol, null, null, 0); }
  public final Addr addr(int disp, Symbol symbol) { return addr(disp, symbol, null, null, 0); }

  /* one-register addressing modes */
  public final Addr addr(Reg sreg1) { return addr(0, null, null, sreg1, 1); }
  public final Addr addr(Reg sreg1, int scale) { return addr(0, null, null, sreg1, scale); }
  public final Addr addr(Symbol symbol, Reg sreg1) { return addr(0, symbol, null, sreg1, 1); }
  public final Addr addr(Symbol symbol, Reg sreg1, int scale) { return addr(0, symbol, null, sreg1, scale); }
  public final Addr addr(int disp, Reg sreg1) { return addr(disp, null, null, sreg1, 1); }
  public final Addr addr(int disp, Reg sreg1, int scale) { return addr(disp, null, null, sreg1, scale); }
  public final Addr addr(int disp, Symbol symbol, Reg sreg1) { return addr(disp, symbol, null, sreg1, 1); }
  public final Addr addr(int disp, Symbol symbol, Reg sreg1, int scale) { return addr(disp, symbol, null, sreg1, scale); }

  /* two-register addressing modes */
  public final Addr addr(Reg sreg0, Reg sreg1) { return addr(0, null, sreg0, sreg1, 1); }
  public final Addr addr(Reg sreg0, Reg sreg1, int scale) { return addr(0, null, sreg0, sreg1, scale); }
  public final Addr addr(Symbol symbol, Reg sreg0, Reg sreg1) { return addr(0, symbol, sreg0, sreg1, 1); }
  public final Addr addr(Symbol symbol, Reg sreg0, Reg sreg1, int scale) { return addr(0, symbol, sreg0, sreg1, scale); }
  public final Addr addr(int disp, Reg sreg0, Reg sreg1) { return addr(disp, null, sreg0, sreg1, 1); }
  public final Addr addr(int disp, Reg sreg0, Reg sreg1, int scale) { return addr(disp, null, sreg0, sreg1, scale); }
  public final Addr addr(int disp, Symbol symbol, Reg sreg0, Reg sreg1) { return addr(disp, symbol, sreg0, sreg1, 1); }
  public Addr addr(int disp, Symbol symbol, Reg sreg0, Reg sreg1, int scale) { return new i386_Addr(disp, symbol, sreg0, sreg1, scale); }

  /* immediate addressing modes */
  public final Imm imm(Symbol symbol) { return imm(0, symbol); }
  public final Imm imm(int disp) { return imm(disp, null); }
  public Imm imm(int disp, Symbol symbol) { return new i386_Imm(disp, symbol); }

  /* address from address plus offset */
  public Addr addr(int diff, Addr _addr) {
    i386_Addr addr = (i386_Addr)_addr;
    return new i386_Addr(addr.disp+diff, addr.symbol, addr.sreg0, addr.sreg1, addr.scale);
  }

  public final void cs() { append(insn_(cs)); }
  public final void ds() { append(insn_(ds)); }
  public final void es() { append(insn_(es)); }
  public final void fs() { append(insn_(fs)); }
  public final void gs() { append(insn_(gs)); }
  public final void ss() { append(insn_(ss)); }
  public final void aaa() { append(insn_(aaa)); }
  public final void aad() { append(insn_(aad)); }
  public final void aam() { append(insn_(aam)); }
  public final void aas() { append(insn_(aas)); }
  public final void cbw() { append(insn_(cbw)); }
  public final void cwde() { append(insn_(cwde)); }
  public final void clc() { append(insn_(clc)); }
  public final void cld() { append(insn_(cld)); }
  public final void cli() { append(insn_(cli)); }
  public final void clts() { append(insn_(clts)); }
  public final void cmc() { append(insn_(cmc)); }
  public final void cmpsb() { append(insn_(cmpsb)); }
  public final void cmpsw() { append(insn_(cmpsw)); }
  public final void cmpsd() { append(insn_(cmpsd)); }
  public final void cwd() { append(insn_(cwd)); }
  public final void cdq() { append(insn_(cdq)); }
  public final void daa() { append(insn_(daa)); }
  public final void das() { append(insn_(das)); }
  public final void f2xm1() { append(insn_(f2xm1)); }
  public final void fabs() { append(insn_(fabs)); }
  public final void faddp() { append(insn_(faddp)); }
  public final void fchs() { append(insn_(fchs)); }
  public final void fclex() { append(insn_(fclex)); }
  public final void fnclex() { append(insn_(fnclex)); }
  public final void fcom() { append(insn_(fcom)); }
  public final void fcomp() { append(insn_(fcomp)); }
  public final void fcompp() { append(insn_(fcompp)); }
  public final void fcos() { append(insn_(fcos)); }
  public final void fdecstp() { append(insn_(fdecstp)); }
  public final void fdivp() { append(insn_(fdivp)); }
  public final void fdivrp() { append(insn_(fdivrp)); }
  public final void fincstp() { append(insn_(fincstp)); }
  public final void finit() { append(insn_(finit)); }
  public final void fninit() { append(insn_(fninit)); }
  public final void fld1() { append(insn_(fld1)); }
  public final void fldl2t() { append(insn_(fldl2t)); }
  public final void fldl2e() { append(insn_(fldl2e)); }
  public final void fldpi() { append(insn_(fldpi)); }
  public final void fldlg2() { append(insn_(fldlg2)); }
  public final void fldln2() { append(insn_(fldln2)); }
  public final void fldz() { append(insn_(fldz)); }
  public final void fmulp() { append(insn_(fmulp)); }
  public final void fnop() { append(insn_(fnop)); }
  public final void fpatan() { append(insn_(fpatan)); }
  public final void fprem() { append(insn_(fprem)); }
  public final void fprem1() { append(insn_(fprem1)); }
  public final void fptan() { append(insn_(fptan)); }
  public final void frndint() { append(insn_(frndint)); }
  public final void fscale() { append(insn_(fscale)); }
  public final void fsin() { append(insn_(fsin)); }
  public final void fsincos() { append(insn_(fsincos)); }
  public final void fsqrt() { append(insn_(fsqrt)); }
  public final void fstsw() { append(insn_(fstsw)); }
  public final void fnstsw() { append(insn_(fnstsw)); }
  public final void fsubp() { append(insn_(fsubp)); }
  public final void fsubrp() { append(insn_(fsubrp)); }
  public final void ftst() { append(insn_(ftst)); }
  public final void fucom() { append(insn_(fucom)); }
  public final void fucomp() { append(insn_(fucomp)); }
  public final void fucompp() { append(insn_(fucompp)); }
  public final void fxam() { append(insn_(fxam)); }
  public final void fxch() { append(insn_(fxch)); }
  public final void fxtract() { append(insn_(fxtract)); }
  public final void fyl2x() { append(insn_(fyl2x)); }
  public final void fyl2xp1() { append(insn_(fyl2xp1)); }
  public final void hlt() { append(insn_(hlt)); }
  public final void inb() { append(insn_(inb)); }
  public final void inw() { append(insn_(inw)); }
  public final void inl() { append(insn_(inl)); }
  public final void insb() { append(insn_(insb)); }
  public final void insw() { append(insn_(insw)); }
  public final void insd() { append(insn_(insd)); }
  public final void into() { append(insn_(into)); }
  public final void iret() { append(insn_(iret)); }
  public final void iretd() { append(insn_(iretd)); }
  public final void lahf() { append(insn_(lahf)); }
  public final void leave() { append(insn_(leave)); }
  public final void lock() { append(insn_(lock)); }
  public final void lodsb() { append(insn_(lodsb)); }
  public final void lodsw() { append(insn_(lodsw)); }
  public final void lodsd() { append(insn_(lodsd)); }
  public final void movsb() { append(insn_(movsb)); }
  public final void movsw() { append(insn_(movsw)); }
  public final void movsd() { append(insn_(movsd)); }
  public final void nop() { append(insn_(nop)); }
  public final void outb() { append(insn_(outb)); }
  public final void outw() { append(insn_(outw)); }
  public final void outl() { append(insn_(outl)); }
  public final void outsb() { append(insn_(outsb)); }
  public final void outsw() { append(insn_(outsw)); }
  public final void outsd() { append(insn_(outsd)); }
  public final void popa() { append(insn_(popa)); }
  public final void popad() { append(insn_(popad)); }
  public final void popf() { append(insn_(popf)); }
  public final void popfd() { append(insn_(popfd)); }
  public final void pusha() { append(insn_(pusha)); }
  public final void pushad() { append(insn_(pushad)); }
  public final void pushf() { append(insn_(pushf)); }
  public final void pushfd() { append(insn_(pushfd)); }
  public final void rep() { append(insn_(rep)); }
  public final void repe() { append(insn_(repe)); }
  public final void repz() { append(insn_(repz)); }
  public final void repne() { append(insn_(repne)); }
  public final void repnz() { append(insn_(repnz)); }
  public final void ret() { append(insn_(ret)); }
  public final void retf() { append(insn_(retf)); }
  public final void sahf() { append(insn_(sahf)); }
  public final void scasb() { append(insn_(scasb)); }
  public final void scasw() { append(insn_(scasw)); }
  public final void scasd() { append(insn_(scasd)); }
  public final void stc() { append(insn_(stc)); }
  public final void std() { append(insn_(std)); }
  public final void sti() { append(insn_(sti)); }
  public final void stosb() { append(insn_(stosb)); }
  public final void stosw() { append(insn_(stosw)); }
  public final void stosd() { append(insn_(stosd)); }
  public final void fwait() { append(insn_(fwait)); }
  public final void xlatb() { append(insn_(xlatb)); }

  public final void callw(Reg sreg) { append(insn_R(callw, sreg)); }
  public final void calll(Reg sreg) { append(insn_R(calll, sreg)); }
  public final void divb(Reg treg) { append(insn_R(divb, treg)); }
  public final void divw(Reg treg) { append(insn_R(divw, treg)); }
  public final void divl(Reg treg) { append(insn_R(divl, treg)); }
  public final void fcom(Reg sreg) { append(insn_R(fcom, sreg)); }
  public final void fcomp(Reg sreg) { append(insn_R(fcomp, sreg)); }
  public final void fst(Reg treg) { append(insn_R(fst, treg)); }
  public final void fstp(Reg treg) { append(insn_R(fstp, treg)); }
  public final void fucom(Reg sreg) { append(insn_R(fucom, sreg)); }
  public final void fucomp(Reg sreg) { append(insn_R(fucomp, sreg)); }
  public final void idivb(Reg sreg) { append(insn_R(idivb, sreg)); }
  public final void idivw(Reg sreg) { append(insn_R(idivw, sreg)); }
  public final void idivl(Reg sreg) { append(insn_R(idivl, sreg)); }
  public final void imulb(Reg sreg) { append(insn_R(imulb, sreg)); }
  public final void imulw(Reg sreg) { append(insn_R(imulw, sreg)); }
  public final void imull(Reg sreg) { append(insn_R(imull, sreg)); }
  public final void jmpw(Reg sreg) { append(insn_R(jmpw, sreg)); }
  public final void jmpl(Reg sreg) { append(insn_R(jmpl, sreg)); }
  public final void lldt(Reg sreg) { append(insn_R(lldt, sreg)); }
  public final void lmsw(Reg sreg) { append(insn_R(lmsw, sreg)); }
  public final void ltr(Reg sreg) { append(insn_R(ltr, sreg)); }
  public final void mulb(Reg sreg) { append(insn_R(mulb, sreg)); }
  public final void mulw(Reg sreg) { append(insn_R(mulw, sreg)); }
  public final void mull(Reg sreg) { append(insn_R(mull, sreg)); }
  public final void pushw(Reg sreg) { append(insn_R(pushw, sreg)); }
  public final void pushl(Reg sreg) { append(insn_R(pushl, sreg)); }
  public final void pushs(Reg sreg) { append(insn_R(pushs, sreg)); }
  public final void verr(Reg sreg) { append(insn_R(verr, sreg)); }
  public final void verw(Reg treg) { append(insn_R(verw, treg)); }

  public final void ffree(Reg sreg) { append(insn_W(ffree, sreg)); }
  public final void fld(Reg sreg) { append(insn_W(fld, sreg)); }
  public final void popw(Reg treg) { append(insn_W(popw, treg)); }
  public final void popl(Reg treg) { append(insn_W(popl, treg)); }
  public final void pops(Reg treg) { append(insn_W(pops, treg)); }
  public final void seta(Reg treg) { append(insn_W(seta, treg)); }
  public final void setae(Reg treg) { append(insn_W(setae, treg)); }
  public final void setb(Reg treg) { append(insn_W(setb, treg)); }
  public final void setbe(Reg treg) { append(insn_W(setbe, treg)); }
  public final void setc(Reg treg) { append(insn_W(setc, treg)); }
  public final void sete(Reg treg) { append(insn_W(sete, treg)); }
  public final void setg(Reg treg) { append(insn_W(setg, treg)); }
  public final void setge(Reg treg) { append(insn_W(setge, treg)); }
  public final void setl(Reg treg) { append(insn_W(setl, treg)); }
  public final void setle(Reg treg) { append(insn_W(setle, treg)); }
  public final void setna(Reg treg) { append(insn_W(setna, treg)); }
  public final void setnae(Reg treg) { append(insn_W(setnae, treg)); }
  public final void setnb(Reg treg) { append(insn_W(setnb, treg)); }
  public final void setnbe(Reg treg) { append(insn_W(setnbe, treg)); }
  public final void setnc(Reg treg) { append(insn_W(setnc, treg)); }
  public final void setne(Reg treg) { append(insn_W(setne, treg)); }
  public final void setng(Reg treg) { append(insn_W(setng, treg)); }
  public final void setnge(Reg treg) { append(insn_W(setnge, treg)); }
  public final void setnl(Reg treg) { append(insn_W(setnl, treg)); }
  public final void setnle(Reg treg) { append(insn_W(setnle, treg)); }
  public final void setno(Reg treg) { append(insn_W(setno, treg)); }
  public final void setnp(Reg treg) { append(insn_W(setnp, treg)); }
  public final void setns(Reg treg) { append(insn_W(setns, treg)); }
  public final void setnz(Reg treg) { append(insn_W(setnz, treg)); }
  public final void seto(Reg treg) { append(insn_W(seto, treg)); }
  public final void setp(Reg treg) { append(insn_W(setp, treg)); }
  public final void setpe(Reg treg) { append(insn_W(setpe, treg)); }
  public final void setpo(Reg treg) { append(insn_W(setpo, treg)); }
  public final void sets(Reg treg) { append(insn_W(sets, treg)); }
  public final void setz(Reg treg) { append(insn_W(setz, treg)); }
  public final void sldt(Reg treg) { append(insn_W(sldt, treg)); }
  public final void smsw(Reg treg) { append(insn_W(smsw, treg)); }
  public final void str(Reg treg) { append(insn_W(str, treg)); }

  public final void decb(Reg treg) { append(insn_A(decb, treg)); }
  public final void decw(Reg treg) { append(insn_A(decw, treg)); }
  public final void decl(Reg treg) { append(insn_A(decl, treg)); }
  public final void faddp(Reg treg) { append(insn_A(faddp, treg)); }
  public final void fdivp(Reg treg) { append(insn_A(fdivp, treg)); }
  public final void fdivrp(Reg treg) { append(insn_A(fdivrp, treg)); }
  public final void fmulp(Reg treg) { append(insn_A(fmulp, treg)); }
  public final void fsubp(Reg treg) { append(insn_A(fsubp, treg)); }
  public final void fsubrp(Reg treg) { append(insn_A(fsubrp, treg)); }
  public final void fxch(Reg sreg) { append(insn_A(fxch, sreg)); }
  public final void incb(Reg treg) { append(insn_A(incb, treg)); }
  public final void incw(Reg treg) { append(insn_A(incw, treg)); }
  public final void incl(Reg treg) { append(insn_A(incl, treg)); }
  public final void negb(Reg treg) { append(insn_A(negb, treg)); }
  public final void negw(Reg treg) { append(insn_A(negw, treg)); }
  public final void negl(Reg treg) { append(insn_A(negl, treg)); }
  public final void notb(Reg treg) { append(insn_A(notb, treg)); }
  public final void notw(Reg treg) { append(insn_A(notw, treg)); }
  public final void notl(Reg treg) { append(insn_A(notl, treg)); }
  public final void rclb(Reg treg) { append(insn_A(rclb, treg)); }
  public final void rclw(Reg treg) { append(insn_A(rclw, treg)); }
  public final void rcll(Reg treg) { append(insn_A(rcll, treg)); }
  public final void rcrb(Reg treg) { append(insn_A(rcrb, treg)); }
  public final void rcrw(Reg treg) { append(insn_A(rcrw, treg)); }
  public final void rcrl(Reg treg) { append(insn_A(rcrl, treg)); }
  public final void rolb(Reg treg) { append(insn_A(rolb, treg)); }
  public final void rolw(Reg treg) { append(insn_A(rolw, treg)); }
  public final void roll(Reg treg) { append(insn_A(roll, treg)); }
  public final void rorb(Reg treg) { append(insn_A(rorb, treg)); }
  public final void rorw(Reg treg) { append(insn_A(rorw, treg)); }
  public final void rorl(Reg treg) { append(insn_A(rorl, treg)); }
  public final void salb(Reg treg) { append(insn_A(salb, treg)); }
  public final void salw(Reg treg) { append(insn_A(salw, treg)); }
  public final void sall(Reg treg) { append(insn_A(sall, treg)); }
  public final void sarb(Reg treg) { append(insn_A(sarb, treg)); }
  public final void sarw(Reg treg) { append(insn_A(sarw, treg)); }
  public final void sarl(Reg treg) { append(insn_A(sarl, treg)); }
  public final void shlb(Reg treg) { append(insn_A(shlb, treg)); }
  public final void shlw(Reg treg) { append(insn_A(shlw, treg)); }
  public final void shll(Reg treg) { append(insn_A(shll, treg)); }
  public final void shrb(Reg treg) { append(insn_A(shrb, treg)); }
  public final void shrw(Reg treg) { append(insn_A(shrw, treg)); }
  public final void shrl(Reg treg) { append(insn_A(shrl, treg)); }

  public final void inb(int simm) { append(insn_I(inb, simm)); }
  public final void inw(int simm) { append(insn_I(inw, simm)); }
  public final void inl(int simm) { append(insn_I(inl, simm)); }
  public final void inti(int simm) { append(insn_I(inti, simm)); }
  public final void outb(int simm) { append(insn_I(outb, simm)); }
  public final void outw(int simm) { append(insn_I(outw, simm)); }
  public final void outl(int simm) { append(insn_I(outl, simm)); }
  public final void pushw(int simm) { append(insn_I(pushw, simm)); }
  public final void pushl(int simm) { append(insn_I(pushl, simm)); }
  public final void ret(int simm) { append(insn_I(ret, simm)); }
  public final void retf(int simm) { append(insn_I(retf, simm)); }

  public final void callw(Addr smem) { append(insn_M(callw, smem)); }
  public final void calll(Addr smem) { append(insn_M(calll, smem)); }
  public final void callfw(Addr smem) { append(insn_M(callfw, smem)); }
  public final void callfl(Addr smem) { append(insn_M(callfl, smem)); }
  public final void decb(Addr tmem) { append(insn_M(decb, tmem)); }
  public final void decw(Addr tmem) { append(insn_M(decw, tmem)); }
  public final void decl(Addr tmem) { append(insn_M(decl, tmem)); }
  public final void divb(Addr tmem) { append(insn_M(divb, tmem)); }
  public final void divw(Addr tmem) { append(insn_M(divw, tmem)); }
  public final void divl(Addr tmem) { append(insn_M(divl, tmem)); }
  public final void fadds(Addr smem) { append(insn_M(fadds, smem)); }
  public final void faddl(Addr smem) { append(insn_M(faddl, smem)); }
  public final void fiaddw(Addr smem) { append(insn_M(fiaddw, smem)); }
  public final void fiaddl(Addr smem) { append(insn_M(fiaddl, smem)); }
  public final void fbld(Addr smem) { append(insn_M(fbld, smem)); }
  public final void fbstp(Addr smem) { append(insn_M(fbstp, smem)); }
  public final void fcoms(Addr smem) { append(insn_M(fcoms, smem)); }
  public final void fcoml(Addr smem) { append(insn_M(fcoml, smem)); }
  public final void fcomps(Addr smem) { append(insn_M(fcomps, smem)); }
  public final void fcompl(Addr smem) { append(insn_M(fcompl, smem)); }
  public final void fdivs(Addr smem) { append(insn_M(fdivs, smem)); }
  public final void fdivl(Addr smem) { append(insn_M(fdivl, smem)); }
  public final void fidivw(Addr smem) { append(insn_M(fidivw, smem)); }
  public final void fidivl(Addr smem) { append(insn_M(fidivl, smem)); }
  public final void fdivrs(Addr smem) { append(insn_M(fdivrs, smem)); }
  public final void fdivrl(Addr smem) { append(insn_M(fdivrl, smem)); }
  public final void fidivrw(Addr smem) { append(insn_M(fidivrw, smem)); }
  public final void fidivrl(Addr smem) { append(insn_M(fidivrl, smem)); }
  public final void ficomw(Addr smem) { append(insn_M(ficomw, smem)); }
  public final void ficoml(Addr smem) { append(insn_M(ficoml, smem)); }
  public final void ficompw(Addr smem) { append(insn_M(ficompw, smem)); }
  public final void ficompl(Addr smem) { append(insn_M(ficompl, smem)); }
  public final void fildw(Addr smem) { append(insn_M(fildw, smem)); }
  public final void fildl(Addr smem) { append(insn_M(fildl, smem)); }
  public final void fildll(Addr smem) { append(insn_M(fildll, smem)); }
  public final void fistw(Addr tmem) { append(insn_M(fistw, tmem)); }
  public final void fistl(Addr tmem) { append(insn_M(fistl, tmem)); }
  public final void fistpw(Addr tmem) { append(insn_M(fistpw, tmem)); }
  public final void fistpl(Addr tmem) { append(insn_M(fistpl, tmem)); }
  public final void fistpll(Addr tmem) { append(insn_M(fistpll, tmem)); }
  public final void flds(Addr smem) { append(insn_M(flds, smem)); }
  public final void fldl(Addr smem) { append(insn_M(fldl, smem)); }
  public final void fldt(Addr smem) { append(insn_M(fldt, smem)); }
  public final void fldcw(Addr smem) { append(insn_M(fldcw, smem)); }
  public final void fldenv(Addr smem) { append(insn_M(fldenv, smem)); }
  public final void fmuls(Addr smem) { append(insn_M(fmuls, smem)); }
  public final void fmull(Addr smem) { append(insn_M(fmull, smem)); }
  public final void fimulw(Addr smem) { append(insn_M(fimulw, smem)); }
  public final void fimull(Addr smem) { append(insn_M(fimull, smem)); }
  public final void frstor(Addr smem) { append(insn_M(frstor, smem)); }
  public final void fsave(Addr tmem) { append(insn_M(fsave, tmem)); }
  public final void fnsave(Addr tmem) { append(insn_M(fnsave, tmem)); }
  public final void fsts(Addr smem) { append(insn_M(fsts, smem)); }
  public final void fstl(Addr smem) { append(insn_M(fstl, smem)); }
  public final void fstps(Addr smem) { append(insn_M(fstps, smem)); }
  public final void fstpl(Addr smem) { append(insn_M(fstpl, smem)); }
  public final void fstpt(Addr smem) { append(insn_M(fstpt, smem)); }
  public final void fstcw(Addr tmem) { append(insn_M(fstcw, tmem)); }
  public final void fnstcw(Addr tmem) { append(insn_M(fnstcw, tmem)); }
  public final void fstenv(Addr tmem) { append(insn_M(fstenv, tmem)); }
  public final void fnstenv(Addr tmem) { append(insn_M(fnstenv, tmem)); }
  public final void fstsw(Addr tmem) { append(insn_M(fstsw, tmem)); }
  public final void fnstsw(Addr tmem) { append(insn_M(fnstsw, tmem)); }
  public final void fsubs(Addr smem) { append(insn_M(fsubs, smem)); }
  public final void fsubl(Addr smem) { append(insn_M(fsubl, smem)); }
  public final void fisubw(Addr tmem) { append(insn_M(fisubw, tmem)); }
  public final void fisubl(Addr tmem) { append(insn_M(fisubl, tmem)); }
  public final void fsubrs(Addr smem) { append(insn_M(fsubrs, smem)); }
  public final void fsubrl(Addr smem) { append(insn_M(fsubrl, smem)); }
  public final void fisubrw(Addr tmem) { append(insn_M(fisubrw, tmem)); }
  public final void fisubrl(Addr tmem) { append(insn_M(fisubrl, tmem)); }
  public final void idivb(Addr smem) { append(insn_M(idivb, smem)); }
  public final void idivw(Addr smem) { append(insn_M(idivw, smem)); }
  public final void idivl(Addr smem) { append(insn_M(idivl, smem)); }
  public final void imulb(Addr smem) { append(insn_M(imulb, smem)); }
  public final void imulw(Addr smem) { append(insn_M(imulw, smem)); }
  public final void imull(Addr smem) { append(insn_M(imull, smem)); }
  public final void incb(Addr tmem) { append(insn_M(incb, tmem)); }
  public final void incw(Addr tmem) { append(insn_M(incw, tmem)); }
  public final void incl(Addr tmem) { append(insn_M(incl, tmem)); }
  public final void jmpw(Addr smem) { append(insn_M(jmpw, smem)); }
  public final void jmpl(Addr smem) { append(insn_M(jmpl, smem)); }
  public final void jmpfw(Addr smem) { append(insn_M(jmpfw, smem)); }
  public final void jmpfl(Addr smem) { append(insn_M(jmpfl, smem)); }
  public final void lgdt(Addr smem) { append(insn_M(lgdt, smem)); }
  public final void lidt(Addr smem) { append(insn_M(lidt, smem)); }
  public final void lldt(Addr smem) { append(insn_M(lldt, smem)); }
  public final void lmsw(Addr smem) { append(insn_M(lmsw, smem)); }
  public final void ltr(Addr smem) { append(insn_M(ltr, smem)); }
  public final void mulb(Addr smem) { append(insn_M(mulb, smem)); }
  public final void mulw(Addr smem) { append(insn_M(mulw, smem)); }
  public final void mull(Addr smem) { append(insn_M(mull, smem)); }
  public final void negb(Addr tmem) { append(insn_M(negb, tmem)); }
  public final void negw(Addr tmem) { append(insn_M(negw, tmem)); }
  public final void negl(Addr tmem) { append(insn_M(negl, tmem)); }
  public final void notb(Addr tmem) { append(insn_M(notb, tmem)); }
  public final void notw(Addr tmem) { append(insn_M(notw, tmem)); }
  public final void notl(Addr tmem) { append(insn_M(notl, tmem)); }
  public final void popw(Addr tmem) { append(insn_M(popw, tmem)); }
  public final void popl(Addr tmem) { append(insn_M(popl, tmem)); }
  public final void pushw(Addr smem) { append(insn_M(pushw, smem)); }
  public final void pushl(Addr smem) { append(insn_M(pushl, smem)); }
  public final void rclb(Addr tmem) { append(insn_M(rclb, tmem)); }
  public final void rclw(Addr tmem) { append(insn_M(rclw, tmem)); }
  public final void rcll(Addr tmem) { append(insn_M(rcll, tmem)); }
  public final void rcrb(Addr tmem) { append(insn_M(rcrb, tmem)); }
  public final void rcrw(Addr tmem) { append(insn_M(rcrw, tmem)); }
  public final void rcrl(Addr tmem) { append(insn_M(rcrl, tmem)); }
  public final void rolb(Addr tmem) { append(insn_M(rolb, tmem)); }
  public final void rolw(Addr tmem) { append(insn_M(rolw, tmem)); }
  public final void roll(Addr tmem) { append(insn_M(roll, tmem)); }
  public final void rorb(Addr tmem) { append(insn_M(rorb, tmem)); }
  public final void rorw(Addr tmem) { append(insn_M(rorw, tmem)); }
  public final void rorl(Addr tmem) { append(insn_M(rorl, tmem)); }
  public final void salb(Addr tmem) { append(insn_M(salb, tmem)); }
  public final void salw(Addr tmem) { append(insn_M(salw, tmem)); }
  public final void sall(Addr tmem) { append(insn_M(sall, tmem)); }
  public final void sarb(Addr tmem) { append(insn_M(sarb, tmem)); }
  public final void sarw(Addr tmem) { append(insn_M(sarw, tmem)); }
  public final void sarl(Addr tmem) { append(insn_M(sarl, tmem)); }
  public final void shlb(Addr tmem) { append(insn_M(shlb, tmem)); }
  public final void shlw(Addr tmem) { append(insn_M(shlw, tmem)); }
  public final void shll(Addr tmem) { append(insn_M(shll, tmem)); }
  public final void shrb(Addr tmem) { append(insn_M(shrb, tmem)); }
  public final void shrw(Addr tmem) { append(insn_M(shrw, tmem)); }
  public final void shrl(Addr tmem) { append(insn_M(shrl, tmem)); }
  public final void seta(Addr tmem) { append(insn_M(seta, tmem)); }
  public final void setae(Addr tmem) { append(insn_M(setae, tmem)); }
  public final void setb(Addr tmem) { append(insn_M(setb, tmem)); }
  public final void setbe(Addr tmem) { append(insn_M(setbe, tmem)); }
  public final void setc(Addr tmem) { append(insn_M(setc, tmem)); }
  public final void sete(Addr tmem) { append(insn_M(sete, tmem)); }
  public final void setg(Addr tmem) { append(insn_M(setg, tmem)); }
  public final void setge(Addr tmem) { append(insn_M(setge, tmem)); }
  public final void setl(Addr tmem) { append(insn_M(setl, tmem)); }
  public final void setle(Addr tmem) { append(insn_M(setle, tmem)); }
  public final void setna(Addr tmem) { append(insn_M(setna, tmem)); }
  public final void setnae(Addr tmem) { append(insn_M(setnae, tmem)); }
  public final void setnb(Addr tmem) { append(insn_M(setnb, tmem)); }
  public final void setnbe(Addr tmem) { append(insn_M(setnbe, tmem)); }
  public final void setnc(Addr tmem) { append(insn_M(setnc, tmem)); }
  public final void setne(Addr tmem) { append(insn_M(setne, tmem)); }
  public final void setng(Addr tmem) { append(insn_M(setng, tmem)); }
  public final void setnge(Addr tmem) { append(insn_M(setnge, tmem)); }
  public final void setnl(Addr tmem) { append(insn_M(setnl, tmem)); }
  public final void setnle(Addr tmem) { append(insn_M(setnle, tmem)); }
  public final void setno(Addr tmem) { append(insn_M(setno, tmem)); }
  public final void setnp(Addr tmem) { append(insn_M(setnp, tmem)); }
  public final void setns(Addr tmem) { append(insn_M(setns, tmem)); }
  public final void setnz(Addr tmem) { append(insn_M(setnz, tmem)); }
  public final void seto(Addr tmem) { append(insn_M(seto, tmem)); }
  public final void setp(Addr tmem) { append(insn_M(setp, tmem)); }
  public final void setpe(Addr tmem) { append(insn_M(setpe, tmem)); }
  public final void setpo(Addr tmem) { append(insn_M(setpo, tmem)); }
  public final void sets(Addr tmem) { append(insn_M(sets, tmem)); }
  public final void setz(Addr tmem) { append(insn_M(setz, tmem)); }
  public final void sgdt(Addr tmem) { append(insn_M(sgdt, tmem)); }
  public final void sidt(Addr tmem) { append(insn_M(sidt, tmem)); }
  public final void sldt(Addr tmem) { append(insn_M(sldt, tmem)); }
  public final void smsw(Addr tmem) { append(insn_M(smsw, tmem)); }
  public final void str(Addr tmem) { append(insn_M(str, tmem)); }
  public final void verr(Addr smem) { append(insn_M(verr, smem)); }
  public final void verw(Addr tmem) { append(insn_M(verw, tmem)); }
  
  public final void call(Imm simm) { append(insn_J(call, simm)); }
  public final void callf(Imm simm) { append(insn_J(callf, simm)); }
  public final void ja(Imm simm) { append(insn_J(ja, simm)); }
  public final void jae(Imm simm) { append(insn_J(jae, simm)); }
  public final void jb(Imm simm) { append(insn_J(jb, simm)); }
  public final void jbe(Imm simm) { append(insn_J(jbe, simm)); }
  public final void jc(Imm simm) { append(insn_J(jc, simm)); }
  public final void jcxe(Imm simm) { append(insn_J(jcxe, simm)); }
  public final void jcxz(Imm simm) { append(insn_J(jcxz, simm)); }
  public final void jecxe(Imm simm) { append(insn_J(jecxe, simm)); }
  public final void jecxz(Imm simm) { append(insn_J(jecxz, simm)); }
  public final void je(Imm simm) { append(insn_J(je, simm)); }
  public final void jg(Imm simm) { append(insn_J(jg, simm)); }
  public final void jge(Imm simm) { append(insn_J(jge, simm)); }
  public final void jl(Imm simm) { append(insn_J(jl, simm)); }
  public final void jle(Imm simm) { append(insn_J(jle, simm)); }
  public final void jna(Imm simm) { append(insn_J(jna, simm)); }
  public final void jnae(Imm simm) { append(insn_J(jnae, simm)); }
  public final void jnb(Imm simm) { append(insn_J(jnb, simm)); }
  public final void jnbe(Imm simm) { append(insn_J(jnbe, simm)); }
  public final void jnc(Imm simm) { append(insn_J(jnc, simm)); }
  public final void jne(Imm simm) { append(insn_J(jne, simm)); }
  public final void jng(Imm simm) { append(insn_J(jng, simm)); }
  public final void jnge(Imm simm) { append(insn_J(jnge, simm)); }
  public final void jnl(Imm simm) { append(insn_J(jnl, simm)); }
  public final void jnle(Imm simm) { append(insn_J(jnle, simm)); }
  public final void jno(Imm simm) { append(insn_J(jno, simm)); }
  public final void jnp(Imm simm) { append(insn_J(jnp, simm)); }
  public final void jns(Imm simm) { append(insn_J(jns, simm)); }
  public final void jnz(Imm simm) { append(insn_J(jnz, simm)); }
  public final void jo(Imm simm) { append(insn_J(jo, simm)); }
  public final void jp(Imm simm) { append(insn_J(jp, simm)); }
  public final void jpe(Imm simm) { append(insn_J(jpe, simm)); }
  public final void jpo(Imm simm) { append(insn_J(jpo, simm)); }
  public final void js(Imm simm) { append(insn_J(js, simm)); }
  public final void jz(Imm simm) { append(insn_J(jz, simm)); }
  public final void jmp(Imm simm) { append(insn_J(jmp, simm)); }
  public final void jmpf(Imm simm) { append(insn_J(jmpf, simm)); }
  public final void loop(Imm simm) { append(insn_J(loop, simm)); }
  public final void loope(Imm simm) { append(insn_J(loope, simm)); }
  public final void loopz(Imm simm) { append(insn_J(loopz, simm)); }
  public final void loopne(Imm simm) { append(insn_J(loopne, simm)); }
  public final void loopnz(Imm simm) { append(insn_J(loopnz, simm)); }
  public final void pushw(Imm simm) { append(insn_J(pushw, simm)); }
  public final void pushl(Imm simm) { append(insn_J(pushl, simm)); }

  public final void btw(Reg sreg, Reg treg) { append(insn_RR(btw, sreg, treg)); }
  public final void btl(Reg sreg, Reg treg) { append(insn_RR(btl, sreg, treg)); }
  public final void cmpb(Reg sreg, Reg treg) { append(insn_RR(cmpb, sreg, treg)); }
  public final void cmpw(Reg sreg, Reg treg) { append(insn_RR(cmpw, sreg, treg)); }
  public final void cmpl(Reg sreg, Reg treg) { append(insn_RR(cmpl, sreg, treg)); }
  public final void testb(Reg sreg, Reg treg) { append(insn_RR(testb, sreg, treg)); }
  public final void testw(Reg sreg, Reg treg) { append(insn_RR(testw, sreg, treg)); }
  public final void testl(Reg sreg, Reg treg) { append(insn_RR(testl, sreg, treg)); }
  
  public final void bsfw(Reg sreg, Reg treg) { append(insn_RW(bsfw, sreg, treg)); }
  public final void bsfl(Reg sreg, Reg treg) { append(insn_RW(bsfl, sreg, treg)); }
  public final void bsrw(Reg sreg, Reg treg) { append(insn_RW(bsrw, sreg, treg)); }
  public final void bsrl(Reg sreg, Reg treg) { append(insn_RW(bsrl, sreg, treg)); }
  public final void larw(Reg sreg, Reg treg) { append(insn_RW(larw, sreg, treg)); }
  public final void larl(Reg sreg, Reg treg) { append(insn_RW(larl, sreg, treg)); }
  public final void lslw(Reg sreg, Reg treg) { append(insn_RW(lslw, sreg, treg)); }
  public final void lsll(Reg sreg, Reg treg) { append(insn_RW(lsll, sreg, treg)); }
  public final void movb(Reg sreg, Reg treg) { append(insn_RW(movb, sreg, treg)); }
  public final void movw(Reg sreg, Reg treg) { append(insn_RW(movw, sreg, treg)); }
  public final void movl(Reg sreg, Reg treg) { append(insn_RW(movl, sreg, treg)); }
  public final void movws(Reg sreg, Reg treg) { append(insn_RW(movws, sreg, treg)); }
  public final void movsw(Reg sreg, Reg treg) { append(insn_RW(movsw, sreg, treg)); }
  public final void movlc(Reg sreg, Reg treg) { append(insn_RW(movlc, sreg, treg)); }
  public final void movcl(Reg sreg, Reg treg) { append(insn_RW(movcl, sreg, treg)); }
  public final void movlg(Reg sreg, Reg treg) { append(insn_RW(movlg, sreg, treg)); }
  public final void movgl(Reg sreg, Reg treg) { append(insn_RW(movgl, sreg, treg)); }
  public final void movlt(Reg sreg, Reg treg) { append(insn_RW(movlt, sreg, treg)); }
  public final void movtl(Reg sreg, Reg treg) { append(insn_RW(movtl, sreg, treg)); }
  public final void movsxbw(Reg sreg, Reg treg) { append(insn_RW(movsxbw, sreg, treg)); }
  public final void movsxbl(Reg sreg, Reg treg) { append(insn_RW(movsxbl, sreg, treg)); }
  public final void movsxwl(Reg sreg, Reg treg) { append(insn_RW(movsxwl, sreg, treg)); }
  public final void movzxbw(Reg sreg, Reg treg) { append(insn_RW(movzxbw, sreg, treg)); }
  public final void movzxbl(Reg sreg, Reg treg) { append(insn_RW(movzxbl, sreg, treg)); }
  public final void movzxwl(Reg sreg, Reg treg) { append(insn_RW(movzxwl, sreg, treg)); }

  public final void adcb(Reg sreg, Reg treg) { append(insn_RA(adcb, sreg, treg)); }
  public final void adcw(Reg sreg, Reg treg) { append(insn_RA(adcw, sreg, treg)); }
  public final void adcl(Reg sreg, Reg treg) { append(insn_RA(adcl, sreg, treg)); }
  public final void addb(Reg sreg, Reg treg) { append(insn_RA(addb, sreg, treg)); }
  public final void addw(Reg sreg, Reg treg) { append(insn_RA(addw, sreg, treg)); }
  public final void addl(Reg sreg, Reg treg) { append(insn_RA(addl, sreg, treg)); }
  public final void andb(Reg sreg, Reg treg) { append(insn_RA(andb, sreg, treg)); }
  public final void andw(Reg sreg, Reg treg) { append(insn_RA(andw, sreg, treg)); }
  public final void andl(Reg sreg, Reg treg) { append(insn_RA(andl, sreg, treg)); }
  public final void arpl(Reg sreg, Reg treg) { append(insn_RA(arpl, sreg, treg)); }
  public final void btcw(Reg sreg, Reg treg) { append(insn_RA(btcw, sreg, treg)); }
  public final void btcl(Reg sreg, Reg treg) { append(insn_RA(btcl, sreg, treg)); }
  public final void btrw(Reg sreg, Reg treg) { append(insn_RA(btrw, sreg, treg)); }
  public final void btrl(Reg sreg, Reg treg) { append(insn_RA(btrl, sreg, treg)); }
  public final void btsw(Reg sreg, Reg treg) { append(insn_RA(btsw, sreg, treg)); }
  public final void btsl(Reg sreg, Reg treg) { append(insn_RA(btsl, sreg, treg)); }
  public final void fadd(Reg sreg, Reg treg) { append(insn_RA(fadd, sreg, treg)); }
  public final void fdiv(Reg sreg, Reg treg) { append(insn_RA(fdiv, sreg, treg)); }
  public final void fdivr(Reg sreg, Reg treg) { append(insn_RA(fdivr, sreg, treg)); }
  public final void fmul(Reg sreg, Reg treg) { append(insn_RA(fmul, sreg, treg)); }
  public final void fsub(Reg sreg, Reg treg) { append(insn_RA(fsub, sreg, treg)); }
  public final void fsubr(Reg sreg, Reg treg) { append(insn_RA(fsubr, sreg, treg)); }
  public final void imulw(Reg sreg, Reg treg) { append(insn_RA(imulw, sreg, treg)); }
  public final void imull(Reg sreg, Reg treg) { append(insn_RA(imull, sreg, treg)); }
  public final void orb(Reg sreg, Reg treg) { append(insn_RA(orb, sreg, treg)); }
  public final void orw(Reg sreg, Reg treg) { append(insn_RA(orw, sreg, treg)); }
  public final void orl(Reg sreg, Reg treg) { append(insn_RA(orl, sreg, treg)); }
  public final void sbbb(Reg sreg, Reg treg) { append(insn_RA(sbbb, sreg, treg)); }
  public final void sbbw(Reg sreg, Reg treg) { append(insn_RA(sbbw, sreg, treg)); }
  public final void sbbl(Reg sreg, Reg treg) { append(insn_RA(sbbl, sreg, treg)); }
  public final void shldw(Reg sreg, Reg treg) { append(insn_RA(shldw, sreg, treg)); }
  public final void shldl(Reg sreg, Reg treg) { append(insn_RA(shldl, sreg, treg)); }
  public final void shrdw(Reg sreg, Reg treg) { append(insn_RA(shrdw, sreg, treg)); }
  public final void shrdl(Reg sreg, Reg treg) { append(insn_RA(shrdl, sreg, treg)); }
  public final void subb(Reg sreg, Reg treg) { append(insn_RA(subb, sreg, treg)); }
  public final void subw(Reg sreg, Reg treg) { append(insn_RA(subw, sreg, treg)); }
  public final void subl(Reg sreg, Reg treg) { append(insn_RA(subl, sreg, treg)); }
  public final void xorb(Reg sreg, Reg treg) { append(insn_RA(xorb, sreg, treg)); }
  public final void xorw(Reg sreg, Reg treg) { append(insn_RA(xorw, sreg, treg)); }
  public final void xorl(Reg sreg, Reg treg) { append(insn_RA(xorl, sreg, treg)); }
  
  public final void xchgb(Reg sreg, Reg treg) { append(insn_AA(xchgb, sreg, treg)); }
  public final void xchgw(Reg sreg, Reg treg) { append(insn_AA(xchgw, sreg, treg)); }
  public final void xchgl(Reg sreg, Reg treg) { append(insn_AA(xchgl, sreg, treg)); }

  public final void btw(int simm, Reg treg) { append(insn_IR(btw, simm, treg)); }
  public final void btl(int simm, Reg treg) { append(insn_IR(btl, simm, treg)); }
  public final void cmpb(int simm, Reg treg) { append(insn_IR(cmpb, simm, treg)); }
  public final void cmpw(int simm, Reg treg) { append(insn_IR(cmpw, simm, treg)); }
  public final void cmpl(int simm, Reg treg) { append(insn_IR(cmpl, simm, treg)); }
  public final void testb(int simm, Reg treg) { append(insn_IR(testb, simm, treg)); }
  public final void testw(int simm, Reg treg) { append(insn_IR(testw, simm, treg)); }
  public final void testl(int simm, Reg treg) { append(insn_IR(testl, simm, treg)); }
  
  public final void movb(int simm, Reg treg) { append(insn_IW(movb, simm, treg)); }
  public final void movw(int simm, Reg treg) { append(insn_IW(movw, simm, treg)); }
  public final void movl(int simm, Reg treg) { append(insn_IW(movl, simm, treg)); }
  
  public final void adcb(int simm, Reg treg) { append(insn_IA(adcb, simm, treg)); }
  public final void adcw(int simm, Reg treg) { append(insn_IA(adcw, simm, treg)); }
  public final void adcl(int simm, Reg treg) { append(insn_IA(adcl, simm, treg)); }
  public final void addb(int simm, Reg treg) { append(insn_IA(addb, simm, treg)); }
  public final void addw(int simm, Reg treg) { append(insn_IA(addw, simm, treg)); }
  public final void addl(int simm, Reg treg) { append(insn_IA(addl, simm, treg)); }
  public final void andb(int simm, Reg treg) { append(insn_IA(andb, simm, treg)); }
  public final void andw(int simm, Reg treg) { append(insn_IA(andw, simm, treg)); }
  public final void andl(int simm, Reg treg) { append(insn_IA(andl, simm, treg)); }
  public final void btcw(int simm, Reg treg) { append(insn_IA(btcw, simm, treg)); }
  public final void btcl(int simm, Reg treg) { append(insn_IA(btcl, simm, treg)); }
  public final void btrw(int simm, Reg treg) { append(insn_IA(btrw, simm, treg)); }
  public final void btrl(int simm, Reg treg) { append(insn_IA(btrl, simm, treg)); }
  public final void btsw(int simm, Reg treg) { append(insn_IA(btsw, simm, treg)); }
  public final void btsl(int simm, Reg treg) { append(insn_IA(btsl, simm, treg)); }
  public final void imulw(int simm, Reg treg) { append(insn_IA(imulw, simm, treg)); }
  public final void imull(int simm, Reg treg) { append(insn_IA(imull, simm, treg)); }
  public final void orb(int simm, Reg treg) { append(insn_IA(orb, simm, treg)); }
  public final void orw(int simm, Reg treg) { append(insn_IA(orw, simm, treg)); }
  public final void orl(int simm, Reg treg) { append(insn_IA(orl, simm, treg)); }
  public final void rclb(int simm, Reg treg) { append(insn_IA(rclb, simm, treg)); }
  public final void rclw(int simm, Reg treg) { append(insn_IA(rclw, simm, treg)); }
  public final void rcll(int simm, Reg treg) { append(insn_IA(rcll, simm, treg)); }
  public final void rcrb(int simm, Reg treg) { append(insn_IA(rcrb, simm, treg)); }
  public final void rcrw(int simm, Reg treg) { append(insn_IA(rcrw, simm, treg)); }
  public final void rcrl(int simm, Reg treg) { append(insn_IA(rcrl, simm, treg)); }
  public final void rolb(int simm, Reg treg) { append(insn_IA(rolb, simm, treg)); }
  public final void rolw(int simm, Reg treg) { append(insn_IA(rolw, simm, treg)); }
  public final void roll(int simm, Reg treg) { append(insn_IA(roll, simm, treg)); }
  public final void rorb(int simm, Reg treg) { append(insn_IA(rorb, simm, treg)); }
  public final void rorw(int simm, Reg treg) { append(insn_IA(rorw, simm, treg)); }
  public final void rorl(int simm, Reg treg) { append(insn_IA(rorl, simm, treg)); }
  public final void salb(int simm, Reg treg) { append(insn_IA(salb, simm, treg)); }
  public final void salw(int simm, Reg treg) { append(insn_IA(salw, simm, treg)); }
  public final void sall(int simm, Reg treg) { append(insn_IA(sall, simm, treg)); }
  public final void sarb(int simm, Reg treg) { append(insn_IA(sarb, simm, treg)); }
  public final void sarw(int simm, Reg treg) { append(insn_IA(sarw, simm, treg)); }
  public final void sarl(int simm, Reg treg) { append(insn_IA(sarl, simm, treg)); }
  public final void shlb(int simm, Reg treg) { append(insn_IA(shlb, simm, treg)); }
  public final void shlw(int simm, Reg treg) { append(insn_IA(shlw, simm, treg)); }
  public final void shll(int simm, Reg treg) { append(insn_IA(shll, simm, treg)); }
  public final void shrb(int simm, Reg treg) { append(insn_IA(shrb, simm, treg)); }
  public final void shrw(int simm, Reg treg) { append(insn_IA(shrw, simm, treg)); }
  public final void shrl(int simm, Reg treg) { append(insn_IA(shrl, simm, treg)); }
  public final void sbbb(int simm, Reg treg) { append(insn_IA(sbbb, simm, treg)); }
  public final void sbbw(int simm, Reg treg) { append(insn_IA(sbbw, simm, treg)); }
  public final void sbbl(int simm, Reg treg) { append(insn_IA(sbbl, simm, treg)); }
  public final void subb(int simm, Reg treg) { append(insn_IA(subb, simm, treg)); }
  public final void subw(int simm, Reg treg) { append(insn_IA(subw, simm, treg)); }
  public final void subl(int simm, Reg treg) { append(insn_IA(subl, simm, treg)); }
  public final void xorb(int simm, Reg treg) { append(insn_IA(xorb, simm, treg)); }
  public final void xorw(int simm, Reg treg) { append(insn_IA(xorw, simm, treg)); }
  public final void xorl(int simm, Reg treg) { append(insn_IA(xorl, simm, treg)); }

  public final void enter(int simm1, int simm2) { append(insn_II(enter, simm1, simm2)); }

  public final void adcb(Reg sreg, Addr tmem) { append(insn_RM(adcb, sreg, tmem)); }
  public final void adcw(Reg sreg, Addr tmem) { append(insn_RM(adcw, sreg, tmem)); }
  public final void adcl(Reg sreg, Addr tmem) { append(insn_RM(adcl, sreg, tmem)); }
  public final void addb(Reg sreg, Addr tmem) { append(insn_RM(addb, sreg, tmem)); }
  public final void addw(Reg sreg, Addr tmem) { append(insn_RM(addw, sreg, tmem)); }
  public final void addl(Reg sreg, Addr tmem) { append(insn_RM(addl, sreg, tmem)); }
  public final void andb(Reg sreg, Addr tmem) { append(insn_RM(andb, sreg, tmem)); }
  public final void andw(Reg sreg, Addr tmem) { append(insn_RM(andw, sreg, tmem)); }
  public final void andl(Reg sreg, Addr tmem) { append(insn_RM(andl, sreg, tmem)); }
  public final void arpl(Reg sreg, Addr tmem) { append(insn_RM(arpl, sreg, tmem)); }
  public final void btw(Reg sreg, Addr tmem) { append(insn_RM(btw, sreg, tmem)); }
  public final void btl(Reg sreg, Addr tmem) { append(insn_RM(btl, sreg, tmem)); }
  public final void btcw(Reg sreg, Addr tmem) { append(insn_RM(btcw, sreg, tmem)); }
  public final void btcl(Reg sreg, Addr tmem) { append(insn_RM(btcl, sreg, tmem)); }
  public final void btrw(Reg sreg, Addr tmem) { append(insn_RM(btrw, sreg, tmem)); }
  public final void btrl(Reg sreg, Addr tmem) { append(insn_RM(btrl, sreg, tmem)); }
  public final void btsw(Reg sreg, Addr tmem) { append(insn_RM(btsw, sreg, tmem)); }
  public final void btsl(Reg sreg, Addr tmem) { append(insn_RM(btsl, sreg, tmem)); }
  public final void cmpb(Reg sreg, Addr tmem) { append(insn_RM(cmpb, sreg, tmem)); }
  public final void cmpw(Reg sreg, Addr tmem) { append(insn_RM(cmpw, sreg, tmem)); }
  public final void cmpl(Reg sreg, Addr tmem) { append(insn_RM(cmpl, sreg, tmem)); }
  public final void movb(Reg sreg, Addr tmem) { append(insn_RM(movb, sreg, tmem)); }
  public final void movw(Reg sreg, Addr tmem) { append(insn_RM(movw, sreg, tmem)); }
  public final void movl(Reg sreg, Addr tmem) { append(insn_RM(movl, sreg, tmem)); }
  public final void movsw(Reg sreg, Addr tmem) { append(insn_RM(movsw, sreg, tmem)); }
  public final void orb(Reg sreg, Addr tmem) { append(insn_RM(orb, sreg, tmem)); }
  public final void orw(Reg sreg, Addr tmem) { append(insn_RM(orw, sreg, tmem)); }
  public final void orl(Reg sreg, Addr tmem) { append(insn_RM(orl, sreg, tmem)); }
  public final void sbbb(Reg sreg, Addr tmem) { append(insn_RM(sbbb, sreg, tmem)); }
  public final void sbbw(Reg sreg, Addr tmem) { append(insn_RM(sbbw, sreg, tmem)); }
  public final void sbbl(Reg sreg, Addr tmem) { append(insn_RM(sbbl, sreg, tmem)); }
  public final void shldw(Reg sreg, Addr tmem) { append(insn_RM(shldw, sreg, tmem)); }
  public final void shldl(Reg sreg, Addr tmem) { append(insn_RM(shldl, sreg, tmem)); }
  public final void shrdw(Reg sreg, Addr tmem) { append(insn_RM(shrdw, sreg, tmem)); }
  public final void shrdl(Reg sreg, Addr tmem) { append(insn_RM(shrdl, sreg, tmem)); }
  public final void subb(Reg sreg, Addr tmem) { append(insn_RM(subb, sreg, tmem)); }
  public final void subw(Reg sreg, Addr tmem) { append(insn_RM(subw, sreg, tmem)); }
  public final void subl(Reg sreg, Addr tmem) { append(insn_RM(subl, sreg, tmem)); }
  public final void testb(Reg sreg, Addr tmem) { append(insn_RM(testb, sreg, tmem)); }
  public final void testw(Reg sreg, Addr tmem) { append(insn_RM(testw, sreg, tmem)); }
  public final void testl(Reg sreg, Addr tmem) { append(insn_RM(testl, sreg, tmem)); }
  public final void xorb(Reg sreg, Addr tmem) { append(insn_RM(xorb, sreg, tmem)); }
  public final void xorw(Reg sreg, Addr tmem) { append(insn_RM(xorw, sreg, tmem)); }
  public final void xorl(Reg sreg, Addr tmem) { append(insn_RM(xorl, sreg, tmem)); }

  public final void xchgb(Reg sreg, Addr tmem) { append(insn_AM(xchgb, sreg, tmem)); }
  public final void xchgw(Reg sreg, Addr tmem) { append(insn_AM(xchgw, sreg, tmem)); }
  public final void xchgl(Reg sreg, Addr tmem) { append(insn_AM(xchgl, sreg, tmem)); }
  
  public final void adcb(int simm, Addr tmem) { append(insn_IM(adcb, simm, tmem)); }
  public final void adcw(int simm, Addr tmem) { append(insn_IM(adcw, simm, tmem)); }
  public final void adcl(int simm, Addr tmem) { append(insn_IM(adcl, simm, tmem)); }
  public final void addb(int simm, Addr tmem) { append(insn_IM(addb, simm, tmem)); }
  public final void addw(int simm, Addr tmem) { append(insn_IM(addw, simm, tmem)); }
  public final void addl(int simm, Addr tmem) { append(insn_IM(addl, simm, tmem)); }
  public final void andb(int simm, Addr tmem) { append(insn_IM(andb, simm, tmem)); }
  public final void andw(int simm, Addr tmem) { append(insn_IM(andw, simm, tmem)); }
  public final void andl(int simm, Addr tmem) { append(insn_IM(andl, simm, tmem)); }
  public final void btw(int simm, Addr tmem) { append(insn_IM(btw, simm, tmem)); }
  public final void btl(int simm, Addr tmem) { append(insn_IM(btl, simm, tmem)); }
  public final void btcw(int simm, Addr tmem) { append(insn_IM(btcw, simm, tmem)); }
  public final void btcl(int simm, Addr tmem) { append(insn_IM(btcl, simm, tmem)); }
  public final void btrw(int simm, Addr tmem) { append(insn_IM(btrw, simm, tmem)); }
  public final void btrl(int simm, Addr tmem) { append(insn_IM(btrl, simm, tmem)); }
  public final void btsw(int simm, Addr tmem) { append(insn_IM(btsw, simm, tmem)); }
  public final void btsl(int simm, Addr tmem) { append(insn_IM(btsl, simm, tmem)); }
  public final void cmpb(int simm, Addr tmem) { append(insn_IM(cmpb, simm, tmem)); }
  public final void cmpw(int simm, Addr tmem) { append(insn_IM(cmpw, simm, tmem)); }
  public final void cmpl(int simm, Addr tmem) { append(insn_IM(cmpl, simm, tmem)); }
  public final void movb(int simm, Addr tmem) { append(insn_IM(movb, simm, tmem)); }
  public final void movw(int simm, Addr tmem) { append(insn_IM(movw, simm, tmem)); }
  public final void movl(int simm, Addr tmem) { append(insn_IM(movl, simm, tmem)); }
  public final void orb(int simm, Addr tmem) { append(insn_IM(orb, simm, tmem)); }
  public final void orw(int simm, Addr tmem) { append(insn_IM(orw, simm, tmem)); }
  public final void orl(int simm, Addr tmem) { append(insn_IM(orl, simm, tmem)); }
  public final void rclb(int simm, Addr tmem) { append(insn_IM(rclb, simm, tmem)); }
  public final void rclw(int simm, Addr tmem) { append(insn_IM(rclw, simm, tmem)); }
  public final void rcll(int simm, Addr tmem) { append(insn_IM(rcll, simm, tmem)); }
  public final void rcrb(int simm, Addr tmem) { append(insn_IM(rcrb, simm, tmem)); }
  public final void rcrw(int simm, Addr tmem) { append(insn_IM(rcrw, simm, tmem)); }
  public final void rcrl(int simm, Addr tmem) { append(insn_IM(rcrl, simm, tmem)); }
  public final void rolb(int simm, Addr tmem) { append(insn_IM(rolb, simm, tmem)); }
  public final void rolw(int simm, Addr tmem) { append(insn_IM(rolw, simm, tmem)); }
  public final void roll(int simm, Addr tmem) { append(insn_IM(roll, simm, tmem)); }
  public final void rorb(int simm, Addr tmem) { append(insn_IM(rorb, simm, tmem)); }
  public final void rorw(int simm, Addr tmem) { append(insn_IM(rorw, simm, tmem)); }
  public final void rorl(int simm, Addr tmem) { append(insn_IM(rorl, simm, tmem)); }
  public final void salb(int simm, Addr tmem) { append(insn_IM(salb, simm, tmem)); }
  public final void salw(int simm, Addr tmem) { append(insn_IM(salw, simm, tmem)); }
  public final void sall(int simm, Addr tmem) { append(insn_IM(sall, simm, tmem)); }
  public final void sarb(int simm, Addr tmem) { append(insn_IM(sarb, simm, tmem)); }
  public final void sarw(int simm, Addr tmem) { append(insn_IM(sarw, simm, tmem)); }
  public final void sarl(int simm, Addr tmem) { append(insn_IM(sarl, simm, tmem)); }
  public final void shlb(int simm, Addr tmem) { append(insn_IM(shlb, simm, tmem)); }
  public final void shlw(int simm, Addr tmem) { append(insn_IM(shlw, simm, tmem)); }
  public final void shll(int simm, Addr tmem) { append(insn_IM(shll, simm, tmem)); }
  public final void shrb(int simm, Addr tmem) { append(insn_IM(shrb, simm, tmem)); }
  public final void shrw(int simm, Addr tmem) { append(insn_IM(shrw, simm, tmem)); }
  public final void shrl(int simm, Addr tmem) { append(insn_IM(shrl, simm, tmem)); }
  public final void sbbb(int simm, Addr tmem) { append(insn_IM(sbbb, simm, tmem)); }
  public final void sbbw(int simm, Addr tmem) { append(insn_IM(sbbw, simm, tmem)); }
  public final void sbbl(int simm, Addr tmem) { append(insn_IM(sbbl, simm, tmem)); }
  public final void subb(int simm, Addr tmem) { append(insn_IM(subb, simm, tmem)); }
  public final void subw(int simm, Addr tmem) { append(insn_IM(subw, simm, tmem)); }
  public final void subl(int simm, Addr tmem) { append(insn_IM(subl, simm, tmem)); }
  public final void testb(int simm, Addr tmem) { append(insn_IM(testb, simm, tmem)); }
  public final void testw(int simm, Addr tmem) { append(insn_IM(testw, simm, tmem)); }
  public final void testl(int simm, Addr tmem) { append(insn_IM(testl, simm, tmem)); }
  public final void xorb(int simm, Addr tmem) { append(insn_IM(xorb, simm, tmem)); }
  public final void xorw(int simm, Addr tmem) { append(insn_IM(xorw, simm, tmem)); }
  public final void xorl(int simm, Addr tmem) { append(insn_IM(xorl, simm, tmem)); }

  public final void boundw(Addr smem, Reg sreg) { append(insn_MR(boundw, smem, sreg)); }
  public final void boundl(Addr smem, Reg sreg) { append(insn_MR(boundl, smem, sreg)); }
  public final void cmpb(Addr smem, Reg treg) { append(insn_MR(cmpb, smem, treg)); }
  public final void cmpw(Addr smem, Reg treg) { append(insn_MR(cmpw, smem, treg)); }
  public final void cmpl(Addr smem, Reg treg) { append(insn_MR(cmpl, smem, treg)); }

  public final void bsfw(Addr smem, Reg treg) { append(insn_MW(bsfw, smem, treg)); }
  public final void bsfl(Addr smem, Reg treg) { append(insn_MW(bsfl, smem, treg)); }
  public final void bsrw(Addr smem, Reg treg) { append(insn_MW(bsrw, smem, treg)); }
  public final void bsrl(Addr smem, Reg treg) { append(insn_MW(bsrl, smem, treg)); }
  public final void larw(Addr smem, Reg treg) { append(insn_MW(larw, smem, treg)); }
  public final void larl(Addr smem, Reg treg) { append(insn_MW(larl, smem, treg)); }
  public final void ldsw(Addr smem, Reg treg) { append(insn_MW(ldsw, smem, treg)); }
  public final void ldsl(Addr smem, Reg treg) { append(insn_MW(ldsl, smem, treg)); }
  public final void lssw(Addr smem, Reg treg) { append(insn_MW(lssw, smem, treg)); }
  public final void lssl(Addr smem, Reg treg) { append(insn_MW(lssl, smem, treg)); }
  public final void lesw(Addr smem, Reg treg) { append(insn_MW(lesw, smem, treg)); }
  public final void lesl(Addr smem, Reg treg) { append(insn_MW(lesl, smem, treg)); }
  public final void lfsw(Addr smem, Reg treg) { append(insn_MW(lfsw, smem, treg)); }
  public final void lfsl(Addr smem, Reg treg) { append(insn_MW(lfsl, smem, treg)); }
  public final void lgsw(Addr smem, Reg treg) { append(insn_MW(lgsw, smem, treg)); }
  public final void lgsl(Addr smem, Reg treg) { append(insn_MW(lgsl, smem, treg)); }
  public final void leaw(Addr smem, Reg treg) { append(insn_MW(leaw, smem, treg)); }
  public final void leal(Addr smem, Reg treg) { append(insn_MW(leal, smem, treg)); }
  public final void lslw(Addr smem, Reg treg) { append(insn_MW(lslw, smem, treg)); }
  public final void lsll(Addr smem, Reg treg) { append(insn_MW(lsll, smem, treg)); }
  public final void movb(Addr smem, Reg treg) { append(insn_MW(movb, smem, treg)); }
  public final void movw(Addr smem, Reg treg) { append(insn_MW(movw, smem, treg)); }
  public final void movl(Addr smem, Reg treg) { append(insn_MW(movl, smem, treg)); }
  public final void movws(Addr smem, Reg treg) { append(insn_MW(movws, smem, treg)); }
  public final void movsxbw(Addr smem, Reg treg) { append(insn_MW(movsxbw, smem, treg)); }
  public final void movsxbl(Addr smem, Reg treg) { append(insn_MW(movsxbl, smem, treg)); }
  public final void movsxwl(Addr smem, Reg treg) { append(insn_MW(movsxwl, smem, treg)); }
  public final void movzxbw(Addr smem, Reg treg) { append(insn_MW(movzxbw, smem, treg)); }
  public final void movzxbl(Addr smem, Reg treg) { append(insn_MW(movzxbl, smem, treg)); }
  public final void movzxwl(Addr smem, Reg treg) { append(insn_MW(movzxwl, smem, treg)); }

  public final void adcb(Addr smem, Reg treg) { append(insn_MA(adcb, smem, treg)); }
  public final void adcw(Addr smem, Reg treg) { append(insn_MA(adcw, smem, treg)); }
  public final void adcl(Addr smem, Reg treg) { append(insn_MA(adcl, smem, treg)); }
  public final void addb(Addr smem, Reg treg) { append(insn_MA(addb, smem, treg)); }
  public final void addw(Addr smem, Reg treg) { append(insn_MA(addw, smem, treg)); }
  public final void addl(Addr smem, Reg treg) { append(insn_MA(addl, smem, treg)); }
  public final void andb(Addr smem, Reg treg) { append(insn_MA(andb, smem, treg)); }
  public final void andw(Addr smem, Reg treg) { append(insn_MA(andw, smem, treg)); }
  public final void andl(Addr smem, Reg treg) { append(insn_MA(andl, smem, treg)); }
  public final void imulw(Addr smem, Reg treg) { append(insn_MA(imulw, smem, treg)); }
  public final void imull(Addr smem, Reg treg) { append(insn_MA(imull, smem, treg)); }
  public final void orb(Addr smem, Reg treg) { append(insn_MA(orb, smem, treg)); }
  public final void orw(Addr smem, Reg treg) { append(insn_MA(orw, smem, treg)); }
  public final void orl(Addr smem, Reg treg) { append(insn_MA(orl, smem, treg)); }
  public final void sbbb(Addr smem, Reg treg) { append(insn_MA(sbbb, smem, treg)); }
  public final void sbbw(Addr smem, Reg treg) { append(insn_MA(sbbw, smem, treg)); }
  public final void sbbl(Addr smem, Reg treg) { append(insn_MA(sbbl, smem, treg)); }
  public final void subb(Addr smem, Reg treg) { append(insn_MA(subb, smem, treg)); }
  public final void subw(Addr smem, Reg treg) { append(insn_MA(subw, smem, treg)); }
  public final void subl(Addr smem, Reg treg) { append(insn_MA(subl, smem, treg)); }
  public final void xchgb(Addr smem, Reg treg) { append(insn_MA(xchgb, smem, treg)); }
  public final void xchgw(Addr smem, Reg treg) { append(insn_MA(xchgw, smem, treg)); }
  public final void xchgl(Addr smem, Reg treg) { append(insn_MA(xchgl, smem, treg)); }
  public final void xorb(Addr smem, Reg treg) { append(insn_MA(xorb, smem, treg)); }
  public final void xorw(Addr smem, Reg treg) { append(insn_MA(xorw, smem, treg)); }
  public final void xorl(Addr smem, Reg treg) { append(insn_MA(xorl, smem, treg)); }

  public final void cmpw(Imm simm, Reg treg) { append(insn_JR(cmpw, simm, treg)); }
  public final void cmpl(Imm simm, Reg treg) { append(insn_JR(cmpl, simm, treg)); }
  public final void testw(Imm simm, Reg treg) { append(insn_JR(testw, simm, treg)); }
  public final void testl(Imm simm, Reg treg) { append(insn_JR(testl, simm, treg)); }

  public final void movw(Imm simm, Reg treg) { append(insn_JW(movw, simm, treg)); }
  public final void movl(Imm simm, Reg treg) { append(insn_JW(movl, simm, treg)); }

  public final void adcw(Imm simm, Reg treg) { append(insn_JA(adcw, simm, treg)); }
  public final void adcl(Imm simm, Reg treg) { append(insn_JA(adcl, simm, treg)); }
  public final void addw(Imm simm, Reg treg) { append(insn_JA(addw, simm, treg)); }
  public final void addl(Imm simm, Reg treg) { append(insn_JA(addl, simm, treg)); }
  public final void andw(Imm simm, Reg treg) { append(insn_JA(andw, simm, treg)); }
  public final void andl(Imm simm, Reg treg) { append(insn_JA(andl, simm, treg)); }
  public final void imulw(Imm simm, Reg treg) { append(insn_JA(imulw, simm, treg)); }
  public final void imull(Imm simm, Reg treg) { append(insn_JA(imull, simm, treg)); }
  public final void orw(Imm simm, Reg treg) { append(insn_JA(orw, simm, treg)); }
  public final void orl(Imm simm, Reg treg) { append(insn_JA(orl, simm, treg)); }
  public final void sbbw(Imm simm, Reg treg) { append(insn_JA(sbbw, simm, treg)); }
  public final void sbbl(Imm simm, Reg treg) { append(insn_JA(sbbl, simm, treg)); }
  public final void subw(Imm simm, Reg treg) { append(insn_JA(subw, simm, treg)); }
  public final void subl(Imm simm, Reg treg) { append(insn_JA(subl, simm, treg)); }
  public final void xorw(Imm simm, Reg treg) { append(insn_JA(xorw, simm, treg)); }
  public final void xorl(Imm simm, Reg treg) { append(insn_JA(xorl, simm, treg)); }

  public final void adcw(Imm simm, Addr tmem) { append(insn_JM(adcw, simm, tmem)); }
  public final void adcl(Imm simm, Addr tmem) { append(insn_JM(adcl, simm, tmem)); }
  public final void addw(Imm simm, Addr tmem) { append(insn_JM(addw, simm, tmem)); }
  public final void addl(Imm simm, Addr tmem) { append(insn_JM(addl, simm, tmem)); }
  public final void andw(Imm simm, Addr tmem) { append(insn_JM(andw, simm, tmem)); }
  public final void andl(Imm simm, Addr tmem) { append(insn_JM(andl, simm, tmem)); }
  public final void cmpw(Imm simm, Addr tmem) { append(insn_JM(cmpw, simm, tmem)); }
  public final void cmpl(Imm simm, Addr tmem) { append(insn_JM(cmpl, simm, tmem)); }
  public final void movw(Imm simm, Addr tmem) { append(insn_JM(movw, simm, tmem)); }
  public final void movl(Imm simm, Addr tmem) { append(insn_JM(movl, simm, tmem)); }
  public final void orw(Imm simm, Addr tmem) { append(insn_JM(orw, simm, tmem)); }
  public final void orl(Imm simm, Addr tmem) { append(insn_JM(orl, simm, tmem)); }
  public final void sbbw(Imm simm, Addr tmem) { append(insn_JM(sbbw, simm, tmem)); }
  public final void sbbl(Imm simm, Addr tmem) { append(insn_JM(sbbl, simm, tmem)); }
  public final void subw(Imm simm, Addr tmem) { append(insn_JM(subw, simm, tmem)); }
  public final void subl(Imm simm, Addr tmem) { append(insn_JM(subl, simm, tmem)); }
  public final void testw(Imm simm, Addr tmem) { append(insn_JM(testw, simm, tmem)); }
  public final void testl(Imm simm, Addr tmem) { append(insn_JM(testl, simm, tmem)); }
  public final void xorw(Imm simm, Addr tmem) { append(insn_JM(xorw, simm, tmem)); }
  public final void xorl(Imm simm, Addr tmem) { append(insn_JM(xorl, simm, tmem)); }

  public final void imulw(int simm, Reg sreg, Reg treg) { append(insn_IRW(imulw, simm, sreg, treg)); }
  public final void imull(int simm, Reg sreg, Reg treg) { append(insn_IRW(imull, simm, sreg, treg)); }

  public final void shldw(int simm, Reg sreg, Reg treg) { append(insn_IRA(shldw, simm, sreg, treg)); }
  public final void shldl(int simm, Reg sreg, Reg treg) { append(insn_IRA(shldl, simm, sreg, treg)); }
  public final void shrdw(int simm, Reg sreg, Reg treg) { append(insn_IRA(shrdw, simm, sreg, treg)); }
  public final void shrdl(int simm, Reg sreg, Reg treg) { append(insn_IRA(shrdl, simm, sreg, treg)); }
  
  public final void shldw(int simm, Reg sreg, Addr tmem) { append(insn_IRM(shldw, simm, sreg, tmem)); }
  public final void shldl(int simm, Reg sreg, Addr tmem) { append(insn_IRM(shldl, simm, sreg, tmem)); }
  public final void shrdw(int simm, Reg sreg, Addr tmem) { append(insn_IRM(shrdw, simm, sreg, tmem)); }
  public final void shrdl(int simm, Reg sreg, Addr tmem) { append(insn_IRM(shrdl, simm, sreg, tmem)); }

  public final void imulw(int simm, Addr smem, Reg treg) { append(insn_IMW(imulw, simm, smem, treg)); }
  public final void imull(int simm, Addr smem, Reg treg) { append(insn_IMW(imull, simm, smem, treg)); }
  
  public final void imulw(Imm simm, Reg sreg, Reg treg) { append(insn_JRW(imulw, simm, sreg, treg)); }
  public final void imull(Imm simm, Reg sreg, Reg treg) { append(insn_JRW(imull, simm, sreg, treg)); }

  public final void imulw(Imm simm, Addr smem, Reg treg) { append(insn_JMW(imulw, simm, smem, treg)); }
  public final void imull(Imm simm, Addr smem, Reg treg) { append(insn_JMW(imull, simm, smem, treg)); }

  protected static InstructionStatement insn_(Opc op) {
    return new InstructionStatement(op, null, null, null, null, null, null, "\t%op");
  }

  protected static InstructionStatement insn_R(Opc op, Reg reg) {
    return new InstructionStatement(op, new Reg[]{ reg }, null, null, null, null, null, "\t%op\t%u0");
  }

  protected static InstructionStatement insn_W(Opc op, Reg reg) {
    return new InstructionStatement(op, null, new Reg[]{ reg }, null, null, null, null, "\t%op\t%d0");
  }

  protected static InstructionStatement insn_A(Opc op, Reg reg) {
    return new InstructionStatement(op, new Reg[]{ reg }, new Reg[]{ reg }, null, null, null, null, "\t%op\t%d0");
  }

  protected static InstructionStatement insn_I(Opc op, int imm) {
    return new InstructionStatement(op, null, null, null, null, new int[]{ imm }, null, "\t%op\t%i0");
  }

  protected static InstructionStatement insn_M(Opc op, Addr mem) {
    return new InstructionStatement(op, null, null, new Addr[]{ mem }, null, null, null, "\t%op\t%l0");
  }

  protected static InstructionStatement insn_J(Opc op, Imm imm) {
    return new InstructionStatement(op, null, null, null, null, null, new Imm[]{ imm }, "\t%op\t%j0");
  }

  protected static InstructionStatement insn_RR(Opc op, Reg reg1, Reg reg2) {
    return new InstructionStatement(op, new Reg[]{ reg1, reg2 }, null, null, null, null, null, "\t%op\t%u0,%u1");
  }

  protected static InstructionStatement insn_RW(Opc op, Reg reg1, Reg reg2) {
    return new InstructionStatement(op, new Reg[]{ reg1 }, new Reg[]{ reg2 }, null, null, null, null, "\t%op\t%u0,%d0");
  }

  protected static InstructionStatement insn_RA(Opc op, Reg reg1, Reg reg2) {
    return new InstructionStatement(op, new Reg[]{ reg1, reg2 }, new Reg[]{ reg2 }, null, null, null, null, "\t%op\t%u0,%d0");
  }

  protected static InstructionStatement insn_AA(Opc op, Reg reg1, Reg reg2) {
    return new InstructionStatement(op, new Reg[]{ reg1, reg2 }, new Reg[]{ reg1, reg2 }, null, null, null, null, "\t%op\t%d0,%d1");
  }

  protected static InstructionStatement insn_IR(Opc op, int imm, Reg reg) {
    return new InstructionStatement(op, new Reg[]{ reg }, null, null, null, new int[]{ imm }, null, "\t%op\t%i0,%u0");
  }

  protected static InstructionStatement insn_IW(Opc op, int imm, Reg reg) {
    return new InstructionStatement(op, null, new Reg[]{ reg }, null, null, new int[]{ imm }, null, "\t%op\t%i0,%d0");
  }

  protected static InstructionStatement insn_IA(Opc op, int imm, Reg reg) {
    return new InstructionStatement(op, new Reg[]{ reg }, new Reg[]{ reg }, null, null, new int[]{ imm }, null, "\t%op\t%i0,%d0");
  }

  protected static InstructionStatement insn_II(Opc op, int imm1, int imm2) {
    return new InstructionStatement(op, null, null, null, null, new int[]{ imm1, imm2 }, null, "\t%op\t%i0,%i1");
  }

  protected static InstructionStatement insn_RM(Opc op, Reg reg, Addr mem) {
    return new InstructionStatement(op, new Reg[]{ reg }, null, new Addr[]{ mem }, null, null, null, "\t%op\t%u0,%l0");
  }

  protected static InstructionStatement insn_AM(Opc op, Reg reg, Addr mem) {
    return new InstructionStatement(op, new Reg[]{ reg }, new Reg[]{ reg }, new Addr[]{ mem }, null, null, null, "\t%op\t%d0,%l0");
  }

  protected static InstructionStatement insn_IM(Opc op, int imm, Addr mem) {
    return new InstructionStatement(op, null, null, new Addr[]{ mem }, null, new int[]{ imm }, null, "\t%op\t%i0,%l0");
  }

  protected static InstructionStatement insn_MR(Opc op, Addr mem, Reg reg) {
    return new InstructionStatement(op, new Reg[]{ reg }, null, new Addr[]{ mem }, null, null, null, "\t%op\t%l0,%u0");
  }

  protected static InstructionStatement insn_MW(Opc op, Addr mem, Reg reg) {
    return new InstructionStatement(op, null, new Reg[]{ reg }, new Addr[]{ mem }, null, null, null, "\t%op\t%l0,%d0");
  }

  protected static InstructionStatement insn_MA(Opc op, Addr mem, Reg reg) {
    return new InstructionStatement(op, new Reg[]{ reg }, new Reg[]{ reg }, new Addr[]{ mem }, null, null, null, "\t%op\t%l0,%d0");
  }

  protected static InstructionStatement insn_JR(Opc op, Imm imm, Reg reg) {
    return new InstructionStatement(op, new Reg[]{ reg }, null, null, null, null, new Imm[]{ imm }, "\t%op\t%j0,%u0");
  }

  protected static InstructionStatement insn_JW(Opc op, Imm imm, Reg reg) {
    return new InstructionStatement(op, null, new Reg[]{ reg }, null, null, null, new Imm[]{ imm }, "\t%op\t%j0,%d0");
  }

  protected static InstructionStatement insn_JA(Opc op, Imm imm, Reg reg) {
    return new InstructionStatement(op, new Reg[]{ reg }, new Reg[]{ reg }, null, null, null, new Imm[]{ imm }, "\t%op\t%j0,%d0");
  }

  protected static InstructionStatement insn_JM(Opc op, Imm imm, Addr mem) {
    return new InstructionStatement(op, null, null, new Addr[]{ mem }, null, null, new Imm[]{ imm }, "\t%op\t%j0,%l0");
  }

  protected static InstructionStatement insn_IRW(Opc op, int imm, Reg reg1, Reg reg2) {
    return new InstructionStatement(op, new Reg[]{ reg1 }, new Reg[]{ reg2 }, null, null, new int[]{ imm }, null, "\t%op\t%i0,%u0,%d0");
  }

  protected static InstructionStatement insn_IRA(Opc op, int imm, Reg reg1, Reg reg2) {
    return new InstructionStatement(op, new Reg[]{ reg1, reg2 }, new Reg[]{ reg2 }, null, null, new int[]{ imm }, null, "\t%op\t%i0,%u0,%d0");
  }

  protected static InstructionStatement insn_IRM(Opc op, int imm, Reg reg, Addr mem) {
    return new InstructionStatement(op, new Reg[]{ reg }, null, new Addr[]{ mem }, null, new int[]{ imm }, null, "\t%op\t%i0,%u0,%l0");
  }

  protected static InstructionStatement insn_IMW(Opc op, int imm, Addr mem, Reg reg) {
    return new InstructionStatement(op, null, new Reg[]{ reg }, new Addr[]{ mem }, null, new int[]{ imm }, null, "\t%op\t%i0,%l0,%d0");
  }

  protected static InstructionStatement insn_JRW(Opc op, Imm imm, Reg reg1, Reg reg2) {
    return new InstructionStatement(op, new Reg[]{ reg1 }, new Reg[]{ reg2 }, null, null, null, new Imm[]{ imm }, "\t%op\t%j0,%u0,%d0");
  }

  protected static InstructionStatement insn_JMW(Opc op, Imm imm, Addr mem, Reg reg) {
    return new InstructionStatement(op, null, new Reg[]{ reg }, new Addr[]{ mem }, null, null, new Imm[]{ imm }, "\t%op\t%j0,%l0,%d0");
  }

}

