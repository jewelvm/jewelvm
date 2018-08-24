/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Addr;
import jewel.core.bend.Imm;
import jewel.core.bend.Reg;

public interface i386_InstructionSet {

  /* CS/DS/ES/FS/GS/SS */
  public void cs();
  public void ds();
  public void es();
  public void fs();
  public void gs();
  public void ss();

  /* AAA */
  public void aaa();

  /* AAD */
  public void aad();

  /* AAM */
  public void aam();

  /* AAS */
  public void aas();

  /* ADC */
  public void adcb(Reg sreg, Reg treg);
  public void adcw(Reg sreg, Reg treg);
  public void adcl(Reg sreg, Reg treg);
  public void adcb(Reg sreg, Addr tmem);
  public void adcw(Reg sreg, Addr tmem);
  public void adcl(Reg sreg, Addr tmem);
  public void adcb(Addr smem, Reg treg);
  public void adcw(Addr smem, Reg treg);
  public void adcl(Addr smem, Reg treg);
  public void adcb(int simm, Reg treg);
  public void adcw(int simm, Reg treg);
  public void adcl(int simm, Reg treg);
  public void adcb(int simm, Addr tmem);
  public void adcw(int simm, Addr tmem);
  public void adcl(int simm, Addr tmem);
  public void adcw(Imm simm, Reg treg);
  public void adcl(Imm simm, Reg treg);
  public void adcw(Imm simm, Addr tmem);
  public void adcl(Imm simm, Addr tmem);
  
  /* ADD */
  public void addb(Reg sreg, Reg treg);
  public void addw(Reg sreg, Reg treg);
  public void addl(Reg sreg, Reg treg);
  public void addb(Reg sreg, Addr tmem);
  public void addw(Reg sreg, Addr tmem);
  public void addl(Reg sreg, Addr tmem);
  public void addb(Addr smem, Reg treg);
  public void addw(Addr smem, Reg treg);
  public void addl(Addr smem, Reg treg);
  public void addb(int simm, Reg treg);
  public void addw(int simm, Reg treg);
  public void addl(int simm, Reg treg);
  public void addb(int simm, Addr tmem);
  public void addw(int simm, Addr tmem);
  public void addl(int simm, Addr tmem);
  public void addw(Imm simm, Reg treg);
  public void addl(Imm simm, Reg treg);
  public void addw(Imm simm, Addr tmem);
  public void addl(Imm simm, Addr tmem);

  /* AND */
  public void andb(Reg sreg, Reg treg);
  public void andw(Reg sreg, Reg treg);
  public void andl(Reg sreg, Reg treg);
  public void andb(Reg sreg, Addr tmem);
  public void andw(Reg sreg, Addr tmem);
  public void andl(Reg sreg, Addr tmem);
  public void andb(Addr smem, Reg treg);
  public void andw(Addr smem, Reg treg);
  public void andl(Addr smem, Reg treg);
  public void andb(int simm, Reg treg);
  public void andw(int simm, Reg treg);
  public void andl(int simm, Reg treg);
  public void andb(int simm, Addr tmem);
  public void andw(int simm, Addr tmem);
  public void andl(int simm, Addr tmem);
  public void andw(Imm simm, Reg treg);
  public void andl(Imm simm, Reg treg);
  public void andw(Imm simm, Addr tmem);
  public void andl(Imm simm, Addr tmem);

  /* ARPL */
  public void arpl(Reg sreg, Reg treg);
  public void arpl(Reg sreg, Addr tmem);

  /* BOUND */
  public void boundw(Addr smem, Reg sreg);
  public void boundl(Addr smem, Reg sreg);

  /* BSF */
  public void bsfw(Reg sreg, Reg treg);
  public void bsfw(Addr smem, Reg treg);
  public void bsfl(Reg sreg, Reg treg);
  public void bsfl(Addr smem, Reg treg);

  /* BSR */
  public void bsrw(Reg sreg, Reg treg);
  public void bsrw(Addr smem, Reg treg);
  public void bsrl(Reg sreg, Reg treg);
  public void bsrl(Addr smem, Reg treg);

  /* BT */
  public void btw(Reg sreg, Reg treg);
  public void btw(Reg sreg, Addr tmem);
  public void btl(Reg sreg, Reg treg);
  public void btl(Reg sreg, Addr tmem);
  public void btw(int simm, Reg treg);
  public void btw(int simm, Addr tmem);
  public void btl(int simm, Reg treg);
  public void btl(int simm, Addr tmem);

  /* BTC */
  public void btcw(Reg sreg, Reg treg);
  public void btcw(Reg sreg, Addr tmem);
  public void btcl(Reg sreg, Reg treg);
  public void btcl(Reg sreg, Addr tmem);
  public void btcw(int simm, Reg treg);
  public void btcw(int simm, Addr tmem);
  public void btcl(int simm, Reg treg);
  public void btcl(int simm, Addr tmem);

  /* BTR */
  public void btrw(Reg sreg, Reg treg);
  public void btrw(Reg sreg, Addr tmem);
  public void btrl(Reg sreg, Reg treg);
  public void btrl(Reg sreg, Addr tmem);
  public void btrw(int simm, Reg treg);
  public void btrw(int simm, Addr tmem);
  public void btrl(int simm, Reg treg);
  public void btrl(int simm, Addr tmem);

  /* BTS */
  public void btsw(Reg sreg, Reg treg);
  public void btsw(Reg sreg, Addr tmem);
  public void btsl(Reg sreg, Reg treg);
  public void btsl(Reg sreg, Addr tmem);
  public void btsw(int simm, Reg treg);
  public void btsw(int simm, Addr tmem);
  public void btsl(int simm, Reg treg);
  public void btsl(int simm, Addr tmem);

  /* CALL */
  public void call(Imm simm);
  public void callw(Reg sreg);
  public void calll(Reg sreg);
  public void callw(Addr smem);
  public void calll(Addr smem);
  public void callf(Imm simm);
  public void callfw(Addr smem);
  public void callfl(Addr smem);

  /* CBW/CWDE */
  public void cbw();
  public void cwde();

  /* CLC */
  public void clc();

  /* CLD */
  public void cld();

  /* CLI */
  public void cli();

  /* CLTS */
  public void clts();

  /* CMC */
  public void cmc();

  /* CMP */
  public void cmpb(Reg sreg, Reg treg);
  public void cmpw(Reg sreg, Reg treg);
  public void cmpl(Reg sreg, Reg treg);
  public void cmpb(Reg sreg, Addr tmem);
  public void cmpw(Reg sreg, Addr tmem);
  public void cmpl(Reg sreg, Addr tmem);
  public void cmpb(Addr smem, Reg treg);
  public void cmpw(Addr smem, Reg treg);
  public void cmpl(Addr smem, Reg treg);
  public void cmpb(int simm, Reg treg);
  public void cmpw(int simm, Reg treg);
  public void cmpl(int simm, Reg treg);
  public void cmpb(int simm, Addr tmem);
  public void cmpw(int simm, Addr tmem);
  public void cmpl(int simm, Addr tmem);
  public void cmpw(Imm simm, Reg treg);
  public void cmpl(Imm simm, Reg treg);
  public void cmpw(Imm simm, Addr tmem);
  public void cmpl(Imm simm, Addr tmem);

  /* CMPSB/CMPSW/CMPSD */
  public void cmpsb();
  public void cmpsw();
  public void cmpsd();

  /* CWD/CDQ */
  public void cwd();
  public void cdq();

  /* DAA */
  public void daa();

  /* DAS */
  public void das();

  /* DEC */
  public void decb(Reg treg);
  public void decw(Reg treg);
  public void decl(Reg treg);
  public void decb(Addr tmem);
  public void decw(Addr tmem);
  public void decl(Addr tmem);

  /* DIV */
  public void divb(Reg treg);
  public void divw(Reg treg);
  public void divl(Reg treg);
  public void divb(Addr tmem);
  public void divw(Addr tmem);
  public void divl(Addr tmem);

  /* ENTER */
  public void enter(int simm1, int simm2);

  /* F2XM1 */
  public void f2xm1();

  /* FABS */
  public void fabs();

  /* FADD/FADDP/FIADD */
  public void fadds(Addr smem);
  public void faddl(Addr smem);
  public void fadd(Reg sreg, Reg treg);
  public void faddp(Reg treg);
  public void faddp();
  public void fiaddw(Addr smem);
  public void fiaddl(Addr smem);

  /* FBLD */
  public void fbld(Addr smem);

  /* FBSTP */
  public void fbstp(Addr smem);

  /* FCHS */
  public void fchs();

  /* FCLEX/FNCLEX */
  public void fclex();
  public void fnclex();

  /* FCOM/FCOMP/FCOMPP */
  public void fcoms(Addr smem);
  public void fcoml(Addr smem);
  public void fcom(Reg sreg);
  public void fcom();
  public void fcomps(Addr smem);
  public void fcompl(Addr smem);
  public void fcomp(Reg sreg);
  public void fcomp();
  public void fcompp();

  /* FCOS */
  public void fcos();

  /* FDECSTP */
  public void fdecstp();
  
  /* FDIV/FDIVP/FIDIV */
  public void fdivs(Addr smem);
  public void fdivl(Addr smem);
  public void fdiv(Reg sreg, Reg treg);
  public void fdivp(Reg treg);
  public void fdivp();
  public void fidivw(Addr smem);
  public void fidivl(Addr smem);

  /* FDIVR/FDIVRP/FIDIVR */
  public void fdivrs(Addr smem);
  public void fdivrl(Addr smem);
  public void fdivr(Reg sreg, Reg treg);
  public void fdivrp(Reg treg);
  public void fdivrp();
  public void fidivrw(Addr smem);
  public void fidivrl(Addr smem);

  /* FFREE */
  public void ffree(Reg sreg);

  /* FICOM/FICOMP */
  public void ficomw(Addr smem);
  public void ficoml(Addr smem);
  public void ficompw(Addr smem);
  public void ficompl(Addr smem);
  
  /* FILD */
  public void fildw(Addr smem);
  public void fildl(Addr smem);
  public void fildll(Addr smem);

  /* FINCSTP */
  public void fincstp();

  /* FINIT/FNINIT */
  public void finit();
  public void fninit();

  /* FIST/FISTP */
  public void fistw(Addr tmem);
  public void fistl(Addr tmem);
  public void fistpw(Addr tmem);
  public void fistpl(Addr tmem);
  public void fistpll(Addr tmem);

  /* FLD */
  public void flds(Addr smem);
  public void fldl(Addr smem);
  public void fldt(Addr smem);
  public void fld(Reg sreg);

  /* FLD1/FLDL2T/FLDL2E/FLDPI/FLDLG2/FLDLN2/FLDZ */
  public void fld1();
  public void fldl2t();
  public void fldl2e();
  public void fldpi();
  public void fldlg2();
  public void fldln2();
  public void fldz();

  /* FLDCW */
  public void fldcw(Addr smem);

  /* FLDENV */
  public void fldenv(Addr smem);

  /* FMUL/FMULP/FIMUL */
  public void fmuls(Addr smem);
  public void fmull(Addr smem);
  public void fmul(Reg sreg, Reg treg);
  public void fmulp(Reg treg);
  public void fmulp();
  public void fimulw(Addr smem);
  public void fimull(Addr smem);

  /* FNOP */
  public void fnop();

  /* FPATAN */
  public void fpatan();

  /* FPREM */
  public void fprem();

  /* FPREM1 */
  public void fprem1();

  /* FPTAN */
  public void fptan();

  /* FRNDINT */
  public void frndint();

  /* FRSTOR */
  public void frstor(Addr smem);
  
  /* FSAVE/FNSAVE */
  public void fsave(Addr tmem);
  public void fnsave(Addr tmem);
  
  /* FSCALE */
  public void fscale();

  /* FSIN */
  public void fsin();

  /* FSINCOS */
  public void fsincos();

  /* FSQRT */
  public void fsqrt();

  /* FST/FSTP */
  public void fsts(Addr smem);
  public void fstl(Addr smem);
  public void fst(Reg treg);
  public void fstps(Addr smem);
  public void fstpl(Addr smem);
  public void fstpt(Addr smem);
  public void fstp(Reg treg);

  /* FSTCW/FNSTCW */
  public void fstcw(Addr tmem);
  public void fnstcw(Addr tmem);

  /* FSTENV/FNSTENV */
  public void fstenv(Addr tmem);
  public void fnstenv(Addr tmem);

  /* FSTSW/FNSTSW */
  public void fstsw(Addr tmem);
  public void fstsw();
  public void fnstsw(Addr tmem);
  public void fnstsw();
  
  /* FSUB/FSUBP/FISUB */
  public void fsubs(Addr smem);
  public void fsubl(Addr smem);
  public void fsub(Reg sreg, Reg treg);
  public void fsubp(Reg treg);
  public void fsubp();
  public void fisubw(Addr tmem);
  public void fisubl(Addr tmem);

  /* FSUBR/FSUBRP/FISUBR */
  public void fsubrs(Addr smem);
  public void fsubrl(Addr smem);
  public void fsubr(Reg sreg, Reg treg);
  public void fsubrp(Reg treg);
  public void fsubrp();
  public void fisubrw(Addr tmem);
  public void fisubrl(Addr tmem);

  /* FTST */
  public void ftst();

  /* FUCOM/FUCOMP/FUCOMPP */
  public void fucom(Reg sreg);
  public void fucom();
  public void fucomp(Reg sreg);
  public void fucomp();
  public void fucompp();

  /* FXAM */
  public void fxam();
  
  /* FXCH */
  public void fxch(Reg sreg);
  public void fxch();

  /* FXTRACT */
  public void fxtract();

  /* FYL2X */
  public void fyl2x();

  /* FYL2XP1 */
  public void fyl2xp1();

  /* HLT */
  public void hlt();

  /* IDIV */
  public void idivb(Reg sreg);
  public void idivw(Reg sreg);
  public void idivl(Reg sreg);
  public void idivb(Addr smem);
  public void idivw(Addr smem);
  public void idivl(Addr smem);

  /* IMUL */
  public void imulb(Reg sreg);
  public void imulw(Reg sreg);
  public void imull(Reg sreg);
  public void imulb(Addr smem);
  public void imulw(Addr smem);
  public void imull(Addr smem);
  public void imulw(Reg sreg, Reg treg);
  public void imull(Reg sreg, Reg treg);
  public void imulw(Addr smem, Reg treg);
  public void imull(Addr smem, Reg treg);
  public void imulw(int simm, Reg sreg, Reg treg);
  public void imull(int simm, Reg sreg, Reg treg);
  public void imulw(int simm, Addr smem, Reg treg);
  public void imull(int simm, Addr smem, Reg treg);
  public void imulw(int simm, Reg treg);
  public void imull(int simm, Reg treg);
  public void imulw(Imm simm, Reg sreg, Reg treg);
  public void imull(Imm simm, Reg sreg, Reg treg);
  public void imulw(Imm simm, Addr smem, Reg treg);
  public void imull(Imm simm, Addr smem, Reg treg);
  public void imulw(Imm simm, Reg treg);
  public void imull(Imm simm, Reg treg);

  /* IN */
  public void inb();
  public void inw();
  public void inl();
  public void inb(int simm);
  public void inw(int simm);
  public void inl(int simm);

  /* INC */
  public void incb(Reg treg);
  public void incw(Reg treg);
  public void incl(Reg treg);
  public void incb(Addr tmem);
  public void incw(Addr tmem);
  public void incl(Addr tmem);

  /* INSB/INSW/INSD */
  public void insb();
  public void insw();
  public void insd();

  /* INT/INTO */
  public void inti(int simm);
  public void into();

  /* IRET/IRETD */
  public void iret();
  public void iretd();

  /* Jcc */
  public void ja(Imm simm);
  public void jae(Imm simm);
  public void jb(Imm simm);
  public void jbe(Imm simm);
  public void jc(Imm simm);
  public void jcxe(Imm simm);
  public void jcxz(Imm simm);
  public void jecxe(Imm simm);
  public void jecxz(Imm simm);
  public void je(Imm simm);
  public void jg(Imm simm);
  public void jge(Imm simm);
  public void jl(Imm simm);
  public void jle(Imm simm);
  public void jna(Imm simm);
  public void jnae(Imm simm);
  public void jnb(Imm simm);
  public void jnbe(Imm simm);
  public void jnc(Imm simm);
  public void jne(Imm simm);
  public void jng(Imm simm);
  public void jnge(Imm simm);
  public void jnl(Imm simm);
  public void jnle(Imm simm);
  public void jno(Imm simm);
  public void jnp(Imm simm);
  public void jns(Imm simm);
  public void jnz(Imm simm);
  public void jo(Imm simm);
  public void jp(Imm simm);
  public void jpe(Imm simm);
  public void jpo(Imm simm);
  public void js(Imm simm);
  public void jz(Imm simm);

  /* JMP */
  public void jmp(Imm simm);
  public void jmpw(Reg sreg);
  public void jmpl(Reg sreg);
  public void jmpw(Addr smem);
  public void jmpl(Addr smem);
  public void jmpf(Imm simm);
  public void jmpfw(Addr smem);
  public void jmpfl(Addr smem);

  /* LAHF */
  public void lahf();

  /* LAR */
  public void larw(Reg sreg, Reg treg);
  public void larl(Reg sreg, Reg treg);
  public void larw(Addr smem, Reg treg);
  public void larl(Addr smem, Reg treg);

  /* LDS/LES/LFS/LGS/LSS */
  public void ldsw(Addr smem, Reg treg);
  public void ldsl(Addr smem, Reg treg);
  public void lesw(Addr smem, Reg treg);
  public void lesl(Addr smem, Reg treg);
  public void lfsw(Addr smem, Reg treg);
  public void lfsl(Addr smem, Reg treg);
  public void lgsw(Addr smem, Reg treg);
  public void lgsl(Addr smem, Reg treg);
  public void lssw(Addr smem, Reg treg);
  public void lssl(Addr smem, Reg treg);

  /* LEA */
  public void leaw(Addr smem, Reg treg);
  public void leal(Addr smem, Reg treg);

  /* LEAVE */
  public void leave();
  
  /* LGDT/LIDT */
  public void lgdt(Addr smem);
  public void lidt(Addr smem);

  /* LLDT */
  public void lldt(Reg sreg);
  public void lldt(Addr smem);

  /* LMSW */
  public void lmsw(Reg sreg);
  public void lmsw(Addr smem);

  /* LOCK */
  public void lock();

  /* LODSB/LODSW/LODSD */
  public void lodsb();
  public void lodsw();
  public void lodsd();

  /* LOOP/LOOPcc */
  public void loop(Imm simm);
  public void loope(Imm simm);
  public void loopz(Imm simm);
  public void loopne(Imm simm);
  public void loopnz(Imm simm);
  
  /* LSL */
  public void lslw(Reg sreg, Reg treg);
  public void lsll(Reg sreg, Reg treg);
  public void lslw(Addr smem, Reg treg);
  public void lsll(Addr smem, Reg treg);

  /* LTR */
  public void ltr(Reg sreg);
  public void ltr(Addr smem);

  /* MOV */
  public void movb(Reg sreg, Reg treg);
  public void movw(Reg sreg, Reg treg);
  public void movl(Reg sreg, Reg treg);
  public void movb(Reg sreg, Addr tmem);
  public void movw(Reg sreg, Addr tmem);
  public void movl(Reg sreg, Addr tmem);
  public void movb(Addr smem, Reg treg);
  public void movw(Addr smem, Reg treg);
  public void movl(Addr smem, Reg treg);
  public void movws(Reg sreg, Reg treg);
  public void movsw(Reg sreg, Reg treg);
  public void movws(Addr smem, Reg treg);
  public void movsw(Reg sreg, Addr tmem);
  public void movb(int simm, Reg treg);
  public void movw(int simm, Reg treg);
  public void movl(int simm, Reg treg);
  public void movb(int simm, Addr tmem);
  public void movw(int simm, Addr tmem);
  public void movl(int simm, Addr tmem);
  public void movw(Imm simm, Reg treg);
  public void movl(Imm simm, Reg treg);
  public void movw(Imm simm, Addr tmem);
  public void movl(Imm simm, Addr tmem);

  /* MOVc */
  public void movlc(Reg sreg, Reg treg);
  public void movcl(Reg sreg, Reg treg);

  /* MOVg */
  public void movlg(Reg sreg, Reg treg);
  public void movgl(Reg sreg, Reg treg);

  /* MOVt */
  public void movlt(Reg sreg, Reg treg);
  public void movtl(Reg sreg, Reg treg);

  /* MOVSB/MOVSW/MOVSD */
  public void movsb();
  public void movsw();
  public void movsd();

  /* MOVSX */
  public void movsxbw(Reg sreg, Reg treg);
  public void movsxbl(Reg sreg, Reg treg);
  public void movsxwl(Reg sreg, Reg treg);
  public void movsxbw(Addr smem, Reg treg);
  public void movsxbl(Addr smem, Reg treg);
  public void movsxwl(Addr smem, Reg treg);

  /* MOVZX */
  public void movzxbw(Reg sreg, Reg treg);
  public void movzxbl(Reg sreg, Reg treg);
  public void movzxwl(Reg sreg, Reg treg);
  public void movzxbw(Addr smem, Reg treg);
  public void movzxbl(Addr smem, Reg treg);
  public void movzxwl(Addr smem, Reg treg);

  /* MUL */
  public void mulb(Reg sreg);
  public void mulw(Reg sreg);
  public void mull(Reg sreg);
  public void mulb(Addr smem);
  public void mulw(Addr smem);
  public void mull(Addr smem);

  /* NEG */
  public void negb(Reg treg);
  public void negw(Reg treg);
  public void negl(Reg treg);
  public void negb(Addr tmem);
  public void negw(Addr tmem);
  public void negl(Addr tmem);

  /* NOP */
  public void nop();

  /* NOT */
  public void notb(Reg treg);
  public void notw(Reg treg);
  public void notl(Reg treg);
  public void notb(Addr tmem);
  public void notw(Addr tmem);
  public void notl(Addr tmem);

  /* OR */
  public void orb(Reg sreg, Reg treg);
  public void orw(Reg sreg, Reg treg);
  public void orl(Reg sreg, Reg treg);
  public void orb(Reg sreg, Addr tmem);
  public void orw(Reg sreg, Addr tmem);
  public void orl(Reg sreg, Addr tmem);
  public void orb(Addr smem, Reg treg);
  public void orw(Addr smem, Reg treg);
  public void orl(Addr smem, Reg treg);
  public void orb(int simm, Reg treg);
  public void orw(int simm, Reg treg);
  public void orl(int simm, Reg treg);
  public void orb(int simm, Addr tmem);
  public void orw(int simm, Addr tmem);
  public void orl(int simm, Addr tmem);
  public void orw(Imm simm, Reg treg);
  public void orl(Imm simm, Reg treg);
  public void orw(Imm simm, Addr tmem);
  public void orl(Imm simm, Addr tmem);

  /* OUT */
  public void outb();
  public void outw();
  public void outl();
  public void outb(int simm);
  public void outw(int simm);
  public void outl(int simm);

  /* OUTSB/OUTSW/OUTSD */
  public void outsb();
  public void outsw();
  public void outsd();

  /* POP */
  public void popw(Reg treg);
  public void popl(Reg treg);
  public void popw(Addr tmem);
  public void popl(Addr tmem);
  public void pops(Reg treg);

  /* POPA/POPAD */
  public void popa();
  public void popad();

  /* POPF/POPFD */
  public void popf();
  public void popfd();

  /* PUSH */
  public void pushw(Reg sreg);
  public void pushl(Reg sreg);
  public void pushw(Addr smem);
  public void pushl(Addr smem);
  public void pushs(Reg sreg);
  public void pushw(int simm);
  public void pushl(int simm);
  public void pushw(Imm simm);
  public void pushl(Imm simm);

  /* PUSHA/PUSHAD */
  public void pusha();
  public void pushad();

  /* PUSHF/PUSHFD */
  public void pushf();
  public void pushfd();

  /* RCL/RCR/ROL/ROR */
  public void rclb(Reg treg);
  public void rclw(Reg treg);
  public void rcll(Reg treg);
  public void rclb(Addr tmem);
  public void rclw(Addr tmem);
  public void rcll(Addr tmem);
  public void rclb(int simm, Reg treg);
  public void rclw(int simm, Reg treg);
  public void rcll(int simm, Reg treg);
  public void rclb(int simm, Addr tmem);
  public void rclw(int simm, Addr tmem);
  public void rcll(int simm, Addr tmem);
  public void rcrb(Reg treg);
  public void rcrw(Reg treg);
  public void rcrl(Reg treg);
  public void rcrb(Addr tmem);
  public void rcrw(Addr tmem);
  public void rcrl(Addr tmem);
  public void rcrb(int simm, Reg treg);
  public void rcrw(int simm, Reg treg);
  public void rcrl(int simm, Reg treg);
  public void rcrb(int simm, Addr tmem);
  public void rcrw(int simm, Addr tmem);
  public void rcrl(int simm, Addr tmem);
  public void rolb(Reg treg);
  public void rolw(Reg treg);
  public void roll(Reg treg);
  public void rolb(Addr tmem);
  public void rolw(Addr tmem);
  public void roll(Addr tmem);
  public void rolb(int simm, Reg treg);
  public void rolw(int simm, Reg treg);
  public void roll(int simm, Reg treg);
  public void rolb(int simm, Addr tmem);
  public void rolw(int simm, Addr tmem);
  public void roll(int simm, Addr tmem);
  public void rorb(Reg treg);
  public void rorw(Reg treg);
  public void rorl(Reg treg);
  public void rorb(Addr tmem);
  public void rorw(Addr tmem);
  public void rorl(Addr tmem);
  public void rorb(int simm, Reg treg);
  public void rorw(int simm, Reg treg);
  public void rorl(int simm, Reg treg);
  public void rorb(int simm, Addr tmem);
  public void rorw(int simm, Addr tmem);
  public void rorl(int simm, Addr tmem);

  /* REP/REPE/REPZ/REPNE/REPNZ */
  public void rep();
  public void repe();
  public void repz();
  public void repne();
  public void repnz();

  /* RET */
  public void ret();
  public void ret(int simm);
  public void retf();
  public void retf(int simm);

  /* SAHF */
  public void sahf();

  /* SAL/SAR/SHL/SHR */
  public void salb(Reg treg);
  public void salw(Reg treg);
  public void sall(Reg treg);
  public void salb(Addr tmem);
  public void salw(Addr tmem);
  public void sall(Addr tmem);
  public void salb(int simm, Reg treg);
  public void salw(int simm, Reg treg);
  public void sall(int simm, Reg treg);
  public void salb(int simm, Addr tmem);
  public void salw(int simm, Addr tmem);
  public void sall(int simm, Addr tmem);
  public void sarb(Reg treg);
  public void sarw(Reg treg);
  public void sarl(Reg treg);
  public void sarb(Addr tmem);
  public void sarw(Addr tmem);
  public void sarl(Addr tmem);
  public void sarb(int simm, Reg treg);
  public void sarw(int simm, Reg treg);
  public void sarl(int simm, Reg treg);
  public void sarb(int simm, Addr tmem);
  public void sarw(int simm, Addr tmem);
  public void sarl(int simm, Addr tmem);
  public void shlb(Reg treg);
  public void shlw(Reg treg);
  public void shll(Reg treg);
  public void shlb(Addr tmem);
  public void shlw(Addr tmem);
  public void shll(Addr tmem);
  public void shlb(int simm, Reg treg);
  public void shlw(int simm, Reg treg);
  public void shll(int simm, Reg treg);
  public void shlb(int simm, Addr tmem);
  public void shlw(int simm, Addr tmem);
  public void shll(int simm, Addr tmem);
  public void shrb(Reg treg);
  public void shrw(Reg treg);
  public void shrl(Reg treg);
  public void shrb(Addr tmem);
  public void shrw(Addr tmem);
  public void shrl(Addr tmem);
  public void shrb(int simm, Reg treg);
  public void shrw(int simm, Reg treg);
  public void shrl(int simm, Reg treg);
  public void shrb(int simm, Addr tmem);
  public void shrw(int simm, Addr tmem);
  public void shrl(int simm, Addr tmem);

  /* SBB */
  public void sbbb(Reg sreg, Reg treg);
  public void sbbw(Reg sreg, Reg treg);
  public void sbbl(Reg sreg, Reg treg);
  public void sbbb(Reg sreg, Addr tmem);
  public void sbbw(Reg sreg, Addr tmem);
  public void sbbl(Reg sreg, Addr tmem);
  public void sbbb(Addr smem, Reg treg);
  public void sbbw(Addr smem, Reg treg);
  public void sbbl(Addr smem, Reg treg);
  public void sbbb(int simm, Reg treg);
  public void sbbw(int simm, Reg treg);
  public void sbbl(int simm, Reg treg);
  public void sbbb(int simm, Addr tmem);
  public void sbbw(int simm, Addr tmem);
  public void sbbl(int simm, Addr tmem);
  public void sbbw(Imm simm, Reg treg);
  public void sbbl(Imm simm, Reg treg);
  public void sbbw(Imm simm, Addr tmem);
  public void sbbl(Imm simm, Addr tmem);

  /* SCASB/SCASW/SCASD */
  public void scasb();
  public void scasw();
  public void scasd();

  /* SETcc */
  public void seta(Reg treg);
  public void seta(Addr tmem);
  public void setae(Reg treg);
  public void setae(Addr tmem);
  public void setb(Reg treg);
  public void setb(Addr tmem);
  public void setbe(Reg treg);
  public void setbe(Addr tmem);
  public void setc(Reg treg);
  public void setc(Addr tmem);
  public void sete(Reg treg);
  public void sete(Addr tmem);
  public void setg(Reg treg);
  public void setg(Addr tmem);
  public void setge(Reg treg);
  public void setge(Addr tmem);
  public void setl(Reg treg);
  public void setl(Addr tmem);
  public void setle(Reg treg);
  public void setle(Addr tmem);
  public void setna(Reg treg);
  public void setna(Addr tmem);
  public void setnae(Reg treg);
  public void setnae(Addr tmem);
  public void setnb(Reg treg);
  public void setnb(Addr tmem);
  public void setnbe(Reg treg);
  public void setnbe(Addr tmem);
  public void setnc(Reg treg);
  public void setnc(Addr tmem);
  public void setne(Reg treg);
  public void setne(Addr tmem);
  public void setng(Reg treg);
  public void setng(Addr tmem);
  public void setnge(Reg treg);
  public void setnge(Addr tmem);
  public void setnl(Reg treg);
  public void setnl(Addr tmem);
  public void setnle(Reg treg);
  public void setnle(Addr tmem);
  public void setno(Reg treg);
  public void setno(Addr tmem);
  public void setnp(Reg treg);
  public void setnp(Addr tmem);
  public void setns(Reg treg);
  public void setns(Addr tmem);
  public void setnz(Reg treg);
  public void setnz(Addr tmem);
  public void seto(Reg treg);
  public void seto(Addr tmem);
  public void setp(Reg treg);
  public void setp(Addr tmem);
  public void setpe(Reg treg);
  public void setpe(Addr tmem);
  public void setpo(Reg treg);
  public void setpo(Addr tmem);
  public void sets(Reg treg);
  public void sets(Addr tmem);
  public void setz(Reg treg);
  public void setz(Addr tmem);

  /* SGDT/SIDT */
  public void sgdt(Addr tmem);
  public void sidt(Addr tmem);

  /* SHLD */
  public void shldw(Reg sreg, Reg treg);
  public void shldl(Reg sreg, Reg treg);
  public void shldw(Reg sreg, Addr tmem);
  public void shldl(Reg sreg, Addr tmem);
  public void shldw(int simm, Reg sreg, Reg treg);
  public void shldl(int simm, Reg sreg, Reg treg);
  public void shldw(int simm, Reg sreg, Addr tmem);
  public void shldl(int simm, Reg sreg, Addr tmem);

  /* SHRD */
  public void shrdw(Reg sreg, Reg treg);
  public void shrdl(Reg sreg, Reg treg);
  public void shrdw(Reg sreg, Addr tmem);
  public void shrdl(Reg sreg, Addr tmem);
  public void shrdw(int simm, Reg sreg, Reg treg);
  public void shrdl(int simm, Reg sreg, Reg treg);
  public void shrdw(int simm, Reg sreg, Addr tmem);
  public void shrdl(int simm, Reg sreg, Addr tmem);

  /* SLDT */
  public void sldt(Reg treg);
  public void sldt(Addr tmem);

  /* SMSW */
  public void smsw(Reg treg);
  public void smsw(Addr tmem);

  /* STC */
  public void stc();

  /* STD */
  public void std();

  /* STI */
  public void sti();

  /* STOSB/STOSW/STOSD */
  public void stosb();
  public void stosw();
  public void stosd();

  /* STR */
  public void str(Reg treg);
  public void str(Addr tmem);

  /* SUB */
  public void subb(Reg sreg, Reg treg);
  public void subw(Reg sreg, Reg treg);
  public void subl(Reg sreg, Reg treg);
  public void subb(Reg sreg, Addr tmem);
  public void subw(Reg sreg, Addr tmem);
  public void subl(Reg sreg, Addr tmem);
  public void subb(Addr smem, Reg treg);
  public void subw(Addr smem, Reg treg);
  public void subl(Addr smem, Reg treg);
  public void subb(int simm, Reg treg);
  public void subw(int simm, Reg treg);
  public void subl(int simm, Reg treg);
  public void subb(int simm, Addr tmem);
  public void subw(int simm, Addr tmem);
  public void subl(int simm, Addr tmem);
  public void subw(Imm simm, Reg treg);
  public void subl(Imm simm, Reg treg);
  public void subw(Imm simm, Addr tmem);
  public void subl(Imm simm, Addr tmem);

  /* TEST */
  public void testb(Reg sreg, Reg treg);
  public void testw(Reg sreg, Reg treg);
  public void testl(Reg sreg, Reg treg);
  public void testb(Reg sreg, Addr tmem);
  public void testw(Reg sreg, Addr tmem);
  public void testl(Reg sreg, Addr tmem);
  public void testb(int simm, Reg treg);
  public void testw(int simm, Reg treg);
  public void testl(int simm, Reg treg);
  public void testb(int simm, Addr tmem);
  public void testw(int simm, Addr tmem);
  public void testl(int simm, Addr tmem);
  public void testw(Imm simm, Reg treg);
  public void testl(Imm simm, Reg treg);
  public void testw(Imm simm, Addr tmem);
  public void testl(Imm simm, Addr tmem);

  /* VERR/VERW */
  public void verr(Reg sreg);
  public void verr(Addr smem);
  public void verw(Reg treg);
  public void verw(Addr tmem);

  /* WAIT/FWAIT */
  public void fwait();

  /* XCHG */
  public void xchgb(Reg sreg, Reg treg);
  public void xchgb(Reg sreg, Addr tmem);
  public void xchgb(Addr smem, Reg treg);
  public void xchgw(Reg sreg, Reg treg);
  public void xchgw(Reg sreg, Addr tmem);
  public void xchgw(Addr smem, Reg treg);
  public void xchgl(Reg sreg, Reg treg);
  public void xchgl(Reg sreg, Addr tmem);
  public void xchgl(Addr smem, Reg treg);

  /* XLATB */
  public void xlatb();

  /* XOR */
  public void xorb(Reg sreg, Reg treg);
  public void xorw(Reg sreg, Reg treg);
  public void xorl(Reg sreg, Reg treg);
  public void xorb(Reg sreg, Addr tmem);
  public void xorw(Reg sreg, Addr tmem);
  public void xorl(Reg sreg, Addr tmem);
  public void xorb(Addr smem, Reg treg);
  public void xorw(Addr smem, Reg treg);
  public void xorl(Addr smem, Reg treg);
  public void xorb(int simm, Reg treg);
  public void xorw(int simm, Reg treg);
  public void xorl(int simm, Reg treg);
  public void xorb(int simm, Addr tmem);
  public void xorw(int simm, Addr tmem);
  public void xorl(int simm, Addr tmem);
  public void xorw(Imm simm, Reg treg);
  public void xorl(Imm simm, Reg treg);
  public void xorw(Imm simm, Addr tmem);
  public void xorl(Imm simm, Addr tmem);

}

