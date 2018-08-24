/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Addr;
import jewel.core.bend.FlowAssembler.AssemblerStatement;
import jewel.core.bend.FlowAssembler.InstructionStatement;
import jewel.core.bend.FlowAssembler.LabelStatement;
import jewel.core.bend.Reg;
import jewel.core.bend.RegisterAllocator;
import jewel.core.bend.VirtReg;

import java.util.BitSet;

public class i386_RegisterAllocator extends RegisterAllocator implements i386_RegisterSet, i386_Opcodes {

  public i386_RegisterAllocator() { }

  protected Reg[] machineRegisters() {
    return new Reg[]{ ES, CS, SS, DS, FS, GS,
                      AL, CL, DL, BL, AH, CH, DH, BH,
                      AX, CX, DX, BX, SP, BP, SI, DI,
                      EAX, ECX, EDX, EBX, ESP, EBP, ESI, EDI,
                      CR0, CR1, CR2, CR3, CR4, CR5, CR6, CR7,
                      DR0, DR1, DR2, DR3, DR4, DR5, DR6, DR7,
                      TR0, TR1, TR2, TR3, TR4, TR5, TR6, TR7,
                      ST0, ST1, ST2, ST3, ST4, ST5, ST6, ST7, };
  }

  protected void calleeSaveRegisters(BitSet set) {
//    set.set(_BL); set.set(_BH);
//    set.set(_BX); set.set(_SI); set.set(_DI);
//    set.set(_EBX); set.set(_ESI); set.set(_EDI);
  }

  protected void sharedRegisters(int reg, BitSet set) {
    switch (reg) {
    case _ES: case _CS: case _SS: case _DS: case _FS: case _GS: break;
    case _AL: case _AH: set.set(_AX); set.set(_EAX); break;
    case _CL: case _CH: set.set(_CX); set.set(_ECX); break;
    case _DL: case _DH: set.set(_DX); set.set(_EDX); break;
    case _BL: case _BH: set.set(_BX); set.set(_EBX); break;
    case _AX: set.set(_AL); set.set(_AH); set.set(_EAX); break;
    case _CX: set.set(_CL); set.set(_CH); set.set(_ECX); break;
    case _DX: set.set(_DL); set.set(_DH); set.set(_EDX); break;
    case _BX: set.set(_BL); set.set(_BH); set.set(_EBX); break;
    case _SP: set.set(_ESP); break;
    case _BP: set.set(_EBP); break;
    case _SI: set.set(_ESI); break;
    case _DI: set.set(_EDI); break;
    case _EAX: set.set(_AL); set.set(_AH); set.set(_AX); break;
    case _ECX: set.set(_CL); set.set(_CH); set.set(_CX); break;
    case _EDX: set.set(_DL); set.set(_DH); set.set(_DX); break;
    case _EBX: set.set(_BL); set.set(_BH); set.set(_BX); break;
    case _ESP: set.set(_SP); break;
    case _EBP: set.set(_BP); break;
    case _ESI: set.set(_SI); break;
    case _EDI: set.set(_DI); break;
    case _CR0: case _CR1: case _CR2: case _CR3: case _CR4: case _CR5: case _CR6: case _CR7: break;
    case _DR0: case _DR1: case _DR2: case _DR3: case _DR4: case _DR5: case _DR6: case _DR7: break;
    case _TR0: case _TR1: case _TR2: case _TR3: case _TR4: case _TR5: case _TR6: case _TR7: break;
    case _ST0: set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7); break;
    case _ST1: set.set(_ST0); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7); break;
    case _ST2: set.set(_ST0); set.set(_ST1); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7); break;
    case _ST3: set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7); break;
    case _ST4: set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST5); set.set(_ST6); set.set(_ST7); break;
    case _ST5: set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST6); set.set(_ST7); break;
    case _ST6: set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST7); break;
    case _ST7: set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); break;
    default: throw new IllegalArgumentException();
    }
  }

  protected boolean isCopy(AssemblerStatement _stmt) {
    if (!(_stmt instanceof InstructionStatement))
      return false;
    InstructionStatement stmt = (InstructionStatement)_stmt;
    if (stmt.format.equals("\t%op\t%u0,%d0"))
      switch (stmt.op.index) {
      case _movb:  case _movw: case _movl:
      case _movws: case _movsw:
      case _movlc: case _movcl:
      case _movlg: case _movgl:
      case _movlt: case _movtl:
        return true;
      }
    return false;
  }

  protected Reg getCopySource(AssemblerStatement stmt) {
    return ((InstructionStatement)stmt).ureg[0];
  }

  protected Reg getCopyTarget(AssemblerStatement stmt) {
    return ((InstructionStatement)stmt).dreg[0];
  }

  protected boolean isCall(AssemblerStatement _stmt) {
    if (!(_stmt instanceof InstructionStatement))
      return false;
    InstructionStatement stmt = (InstructionStatement)_stmt;
    if (stmt.format.equals("\t%op\t%u0"))
      switch (stmt.op.index) {
      case _callw: case _calll:
        return true;
      }
    if (stmt.format.equals("\t%op\t%l0"))
      switch (stmt.op.index) {
      case _callw: case _calll:
      case _callfw: case _callfl:
        return true;
      }
    if (stmt.format.equals("\t%op\t%j0"))
      switch (stmt.op.index) {
      case _call:
      case _callf:
        return true;
      }
    return false;
  }

  protected void implicitUsesAndDefines(AssemblerStatement _stmt, BitSet uses, BitSet defines) {
    if (_stmt instanceof LabelStatement) {
      LabelStatement stmt = (LabelStatement)_stmt;
      if (stmt.label.equals("USE_EAX"))
        uses.set(_EAX);
      if (stmt.label.equals("USE_EDX"))
        uses.set(_EDX);
      if (stmt.label.equals("USE_EBP"))
        uses.set(_EBP);
      if (stmt.label.equals("USE_ESP"))
        uses.set(_ESP);
    }
    if (!(_stmt instanceof InstructionStatement))
      return;
    InstructionStatement stmt = (InstructionStatement)_stmt;
    if (stmt.format.equals("\t%op"))
      switch (stmt.op.index) {
      case _cs:
        uses.set(_CS);
        break;
      case _ds:
        uses.set(_DS);
        break;
      case _es:
        uses.set(_ES);
        break;
      case _fs:
        uses.set(_FS);
        break;
      case _gs:
        uses.set(_GS);
        break;
      case _ss:
        uses.set(_SS);
        break;
      case _aaa:
      case _aad:
      case _aam:
      case _aas:
        uses.set(_AL);
        uses.set(_AH);
        defines.set(_AL);
        defines.set(_AH);
        break;
      case _cbw:
        uses.set(_AL);
        defines.set(_AX);
        break;
      case _cwde:
        uses.set(_AX);
        defines.set(_EAX);
        break;
      case _clts:
        uses.set(_CR0);
        defines.set(_CR0);
        break;
      case _cmpsb: case _cmpsw: case _cmpsd:
        uses.set(_DS);
        uses.set(_ESI);
        uses.set(_ES);
        uses.set(_EDI);
        break;
      case _cwd:
        uses.set(_AX);
        defines.set(_AX);
        defines.set(_DX);
        break;
      case _cdq:
        uses.set(_EAX);
        defines.set(_EAX);
        defines.set(_EDX);
        break;
      case _daa:
      case _das:
        uses.set(_AL);
        defines.set(_AL);
        break;
      case _fld1:
      case _fldl2t:
      case _fldl2e:
      case _fldpi:
      case _fldlg2:
      case _fldln2:
      case _fldz:
        defines.set(_ST0);
        break;
      case _f2xm1:
      case _fabs:
      case _fchs:
      case _fcos:
      case _frndint:
      case _fsin:
      case _fsqrt:
        uses.set(_ST0);
        defines.set(_ST0);
        break;
      case _fptan:
      case _fsincos:
      case _fxtract:
        uses.set(_ST0);
        defines.set(_ST0);
        defines.set(_ST1);
        break;
      case _faddp:
      case _fdivp:
      case _fdivrp:
      case _fmulp:
      case _fpatan:
      case _fprem:
      case _fprem1:
      case _fscale:
      case _fsubp:
      case _fsubrp:
      case _fyl2x:
      case _fyl2xp1:
        uses.set(_ST0);
        uses.set(_ST1);
        defines.set(_ST0);
        break;
      case _fcom: case _fcomp: case _fcompp:
      case _fucom: case _fucomp: case _fucompp:
        uses.set(_ST0);
        uses.set(_ST1);
        break;
      case _fdecstp:
      case _fincstp:
        break;
      case _finit: case _fninit:
        defines.set(_ST0);
        defines.set(_ST1);
        defines.set(_ST2);
        defines.set(_ST3);
        defines.set(_ST4);
        defines.set(_ST5);
        defines.set(_ST6);
        defines.set(_ST7);
        break;
      case _fstsw: case _fnstsw:
        defines.set(_AX);
        break;
      case _ftst:
        uses.set(_ST0);
        break;
      case _fxch:
        uses.set(_ST0);
        uses.set(_ST1);
        defines.set(_ST0);
        defines.set(_ST1);
        break;
      case _inb:
        uses.set(_DX);
        defines.set(_AL);
        break;
      case _inw:
        uses.set(_DX);
        defines.set(_AX);
        break;
      case _inl:
        uses.set(_DX);
        defines.set(_EAX);
        break;
      case _insb: case _insw: case _insd:
        uses.set(_DX);
        uses.set(_ES);
        uses.set(_EDI);
        break;
      case _iret: case _iretd:
        uses.set(_ESP);
        defines.set(_ESP);
        defines.set(_CS);
        break;
      case _lahf:
        defines.set(_AH);
        break;
      case _leave:
        uses.set(_ESP);
        uses.set(_EBP);
        defines.set(_ESP);
        defines.set(_EBP);
        break;
      case _lodsb:
        uses.set(_DS);
        uses.set(_ESI);
        defines.set(_AL);
        break;
      case _lodsw:
        uses.set(_DS);
        uses.set(_ESI);
        defines.set(_AX);
        break;
      case _lodsd:
        uses.set(_DS);
        uses.set(_ESI);
        defines.set(_EAX);
        break;
      case _movsb: case _movsw: case _movsd:
        uses.set(_DS);
        uses.set(_ESI);
        uses.set(_ES);
        uses.set(_EDI);
        break;
      case _outb:
        uses.set(_DX);
        uses.set(_AL);
        break;
      case _outw:
        uses.set(_DX);
        uses.set(_AX);
        break;
      case _outl:
        uses.set(_DX);
        uses.set(_EAX);
        break;
      case _outsb: case _outsw: case _outsd:
        uses.set(_DX);
        uses.set(_DS);
        uses.set(_ESI);
        break;
      case _popa:
        uses.set(_SP);
        uses.set(_BP);
        defines.set(_AX);
        defines.set(_CX);
        defines.set(_DX);
        defines.set(_BX);
        defines.set(_SP);
        defines.set(_BP);
        defines.set(_SI);
        defines.set(_DI);
        break;
      case _popad:
        uses.set(_ESP);
        uses.set(_EBP);
        defines.set(_EAX);
        defines.set(_ECX);
        defines.set(_EDX);
        defines.set(_EBX);
        defines.set(_ESP);
        defines.set(_EBP);
        defines.set(_ESI);
        defines.set(_EDI);
        break;
      case _popf:
      case _pushf:
      case _popfd:
      case _pushfd:
        uses.set(_ESP);
        defines.set(_ESP);
        break;
      case _pusha:
        uses.set(_AX);
        uses.set(_CX);
        uses.set(_DX);
        uses.set(_BX);
        uses.set(_SP);
        uses.set(_BP);
        uses.set(_SI);
        uses.set(_DI);
        defines.set(_SP);
        break;
      case _pushad:
        uses.set(_EAX);
        uses.set(_ECX);
        uses.set(_EDX);
        uses.set(_EBX);
        uses.set(_ESP);
        uses.set(_EBP);
        uses.set(_ESI);
        uses.set(_EDI);
        defines.set(_ESP);
        break;
      case _rep:
      case _repe:
      case _repz:
      case _repne:
      case _repnz:
        uses.set(_ECX);
        defines.set(_ECX);
        break;
      case _ret:
        uses.set(_ESP);
        defines.set(_ESP);
        break;
      case _retf:
        uses.set(_ESP);
        defines.set(_ESP);
        defines.set(_CS);
        break;
      case _sahf:
        uses.set(_AH);
        break;
      case _scasb:
      case _stosb:
        uses.set(_AL);
        uses.set(_ES);
        uses.set(_EDI);
        break;
      case _scasw:
      case _stosw:
        uses.set(_AX);
        uses.set(_ES);
        uses.set(_EDI);
        break;
      case _scasd:
      case _stosd:
        uses.set(_EAX);
        uses.set(_ES);
        uses.set(_EDI);
        break;
      case _xlatb:
        uses.set(_DS);
        uses.set(_AL);
        uses.set(_EBX);
        defines.set(_AL);
      }
    if (stmt.format.equals("\t%op\t%u0"))
      switch (stmt.op.index) {
      case _callw: case _calll:
        uses.set(_ESP);
        uses.set(_EBP);
        defines.set(_ESP);
        defines.set(_EBP);
        break;
      case _divb:
      case _idivb:
        uses.set(_AL);
        uses.set(_AH);
        defines.set(_AL);
        defines.set(_AH);
        break;
      case _imulb:
      case _mulb:
        uses.set(_AL);
        defines.set(_AL);
        defines.set(_AH);
        break;
      case _divw:
      case _idivw:
        uses.set(_AX);
        uses.set(_DX);
        defines.set(_AX);
        defines.set(_DX);
        break;
      case _imulw:
      case _mulw:
        uses.set(_AX);
        defines.set(_AX);
        defines.set(_DX);
        break;
      case _divl:
      case _idivl:
        uses.set(_EAX);
        uses.set(_EDX);
        defines.set(_EAX);
        defines.set(_EDX);
        break;
      case _imull:
      case _mull:
        uses.set(_EAX);
        defines.set(_EAX);
        defines.set(_EDX);
        break;
      case _fcom: case _fcomp:
      case _fst: case _fstp:
      case _fucom: case _fucomp:
        uses.set(_ST0);
        break;
      case _pushw: case _pushl:
      case _pushs:
        uses.set(_ESP);
        defines.set(_ESP);
      }
    if (stmt.format.equals("\t%op\t%d0"))
      switch (stmt.op.index) {
      case _fld:
        defines.set(_ST0);
        break;
      case _popw: case _popl:
      case _pops:
        uses.set(_ESP);
        defines.set(_ESP);
      case _faddp:
      case _fdivp:
      case _fdivrp:
      case _fmulp:
      case _fsubp:
      case _fsubrp:
      case _fxch:
        uses.set(_ST0);
        break;
      case _rclb: case _rclw: case _rcll:
      case _rcrb: case _rcrw: case _rcrl:
      case _rolb: case _rolw: case _roll:
      case _rorb: case _rorw: case _rorl:
      case _salb: case _salw: case _sall:
      case _sarb: case _sarw: case _sarl:
      case _shlb: case _shlw: case _shll:
      case _shrb: case _shrw: case _shrl:
        uses.set(_CL);
      }
    if (stmt.format.equals("\t%op\t%i0"))
      switch (stmt.op.index) {
      case _inb:
        defines.set(_AL);
        break;
      case _inw:
        defines.set(_AX);
        break;
      case _inl:
        defines.set(_EAX);
        break;
      case _outb:
        uses.set(_AL);
        break;
      case _outw:
        uses.set(_AX);
        break;
      case _outl:
        uses.set(_EAX);
        break;
      case _pushw:
      case _pushl:
        uses.set(_ESP);
        defines.set(_ESP);
        break;
      case _ret:
        uses.set(_ESP);
        defines.set(_ESP);
        break;
      case _retf:
        uses.set(_ESP);
        defines.set(_ESP);
        defines.set(_CS);
      }
    if (stmt.format.equals("\t%op\t%l0"))
      switch (stmt.op.index) {
      case _callw: case _calll:
        uses.set(_ESP);
        uses.set(_EBP);
        defines.set(_ESP);
        defines.set(_EBP);
        break;
      case _callfw: case _callfl:
        uses.set(_ESP);
        uses.set(_EBP);
        uses.set(_CS);
        defines.set(_ESP);
        defines.set(_EBP);
        defines.set(_CS);
        break;
      case _fadds: case _faddl:
      case _fiaddw: case _fiaddl:
      case _fdivs: case _fdivl:
      case _fidivw: case _fidivl:
      case _fdivrs: case _fdivrl:
      case _fidivrw: case _fidivrl:
      case _fmuls: case _fmull:
      case _fimulw: case _fimull:
      case _fsubs: case _fsubl:
      case _fisubw: case _fisubl:
      case _fsubrs: case _fsubrl:
      case _fisubrw: case _fisubrl:
        uses.set(_ST0);
        defines.set(_ST0);
        break;
      case _fbld:
      case _fildw: case _fildl: case _fildll:
      case _flds: case _fldl: case _fldt:
        defines.set(_ST0);
        break;
      case _fbstp:
      case _fistw: case _fistl:
      case _fistpw: case _fistpl: case _fistpll:
        uses.set(_ST0);
        break;
      case _fcoms: case _fcoml:
      case _fcomps: case _fcompl:
      case _ficomw: case _ficoml:
      case _ficompw: case _ficompl:
        uses.set(_ST0);
        break;
      case _fldenv:
      case _frstor:
        defines.set(_ST0);
        defines.set(_ST1);
        defines.set(_ST2);
        defines.set(_ST3);
        defines.set(_ST4);
        defines.set(_ST5);
        defines.set(_ST6);
        defines.set(_ST7);
        break;
      case _fsave: case _fnsave:
        uses.set(_ST0);
        uses.set(_ST1);
        uses.set(_ST2);
        uses.set(_ST3);
        uses.set(_ST4);
        uses.set(_ST5);
        uses.set(_ST6);
        uses.set(_ST7);
        break;
      case _fsts: case _fstl:
      case _fstps: case _fstpl: case _fstpt:
        uses.set(_ST0);
        break;
      case _fstenv: case _fnstenv:
        uses.set(_ST0);
        uses.set(_ST1);
        uses.set(_ST2);
        uses.set(_ST3);
        uses.set(_ST4);
        uses.set(_ST5);
        uses.set(_ST6);
        uses.set(_ST7);
        break;
      case _jmpfw:
      case _jmpfl:
        defines.set(_CS);
        break;
      case _divb:
      case _idivb:
        uses.set(_AL);
        uses.set(_AH);
        defines.set(_AL);
        defines.set(_AH);
        break;
      case _imulb:
      case _mulb:
        uses.set(_AL);
        defines.set(_AL);
        defines.set(_AH);
        break;
      case _divw:
      case _idivw:
        uses.set(_AX);
        uses.set(_DX);
        defines.set(_AX);
        defines.set(_DX);
        break;
      case _imulw:
      case _mulw:
        uses.set(_AX);
        defines.set(_AX);
        defines.set(_DX);
        break;
      case _divl:
      case _idivl:
        uses.set(_EAX);
        uses.set(_EDX);
        defines.set(_EAX);
        defines.set(_EDX);
        break;
      case _imull:
      case _mull:
        uses.set(_EAX);
        defines.set(_EAX);
        defines.set(_EDX);
        break;
      case _popw: case _popl:
      case _pushw: case _pushl:
        uses.set(_ESP);
        defines.set(_ESP);
        break;
      case _rclb: case _rclw: case _rcll:
      case _rcrb: case _rcrw: case _rcrl:
      case _rolb: case _rolw: case _roll:
      case _rorb: case _rorw: case _rorl:
      case _salb: case _salw: case _sall:
      case _sarb: case _sarw: case _sarl:
      case _shlb: case _shlw: case _shll:
      case _shrb: case _shrw: case _shrl:
        uses.set(_CL);
      }
    if (stmt.format.equals("\t%op\t%j0"))
      switch (stmt.op.index) {
      case _call:
        uses.set(_ESP);
        uses.set(_EBP);
        defines.set(_ESP);
        defines.set(_EBP);
        break;
      case _callf:
        uses.set(_ESP);
        uses.set(_EBP);
        uses.set(_CS);
        defines.set(_ESP);
        defines.set(_EBP);
        defines.set(_CS);
        break;
      case _jcxe: case _jcxz:
        uses.set(_CX);
        break;
      case _jecxe: case _jecxz:
        uses.set(_ECX);
        break;
      case _jmpf:
        defines.set(_CS);
        break;
      case _loop:
      case _loope: case _loopz:
      case _loopne: case _loopnz:
        uses.set(_ECX);
        defines.set(_ECX);
        break;
      case _pushw: case _pushl:
        uses.set(_ESP);
        defines.set(_ESP);
      }
    if (stmt.format.equals("\t%op\t%u0,%d0"))
      switch (stmt.op.index) {
      case _shldw: case _shldl:
      case _shrdw: case _shrdl:
        uses.set(_CL);
      }
    if (stmt.format.equals("\t%op\t%i0,%i1"))
      switch (stmt.op.index) {
      case _enter:
        uses.set(_ESP);
        uses.set(_EBP);
        defines.set(_ESP);
        defines.set(_EBP);
      }
    if (stmt.format.equals("\t%op\t%u0,%l0"))
      switch (stmt.op.index) {
      case _shldw: case _shldl:
      case _shrdw: case _shrdl:
        uses.set(_CL);
      }
    if (stmt.format.equals("\t%op\t%l0,%d0"))
      switch (stmt.op.index) {
      case _ldsw: case _ldsl:
        defines.set(_DS);
        break;
      case _lssw: case _lssl:
        defines.set(_SS);
        break;
      case _lesw: case _lesl:
        defines.set(_ES);
        break;
      case _lfsw: case _lfsl:
        defines.set(_FS);
        break;
      case _lgsw: case _lgsl:
        defines.set(_ES);
      }
  }

  public int localWords;
  private int baseFor(Reg _reg) {
    VirtReg reg = (VirtReg)_reg;
    if (reg.regBase == 0)
      reg.regBase = -8-4*(localWords++);
    return reg.regBase;
  }

  protected AssemblerStatement newLoadStatement(Reg reg) {
    Addr mem = new i386_Addr(baseFor(reg), null, null, EBP, 1);
    return i386_FlowAssembler.insn_MW(movl, mem, reg);
  }

  protected AssemblerStatement newStoreStatement(Reg reg) {
    Addr mem = new i386_Addr(baseFor(reg), null, null, EBP, 1);
    return i386_FlowAssembler.insn_RM(movl, reg, mem);
  }

  protected AssemblerStatement newInMemoryStatement(Reg reg, AssemblerStatement _stmt) {
    Addr mem = new i386_Addr(baseFor(reg), null, null, EBP, 1);
    InstructionStatement stmt = (InstructionStatement)_stmt;
    if (stmt.format.equals("\t%op\t%u0"))
      return i386_FlowAssembler.insn_M(stmt.op, mem);
    if (stmt.format.equals("\t%op\t%d0"))
      return i386_FlowAssembler.insn_M(stmt.op, mem);
    if (stmt.format.equals("\t%op\t%u0,%u1")) {
      if (reg.equals(stmt.ureg[0])) return i386_FlowAssembler.insn_MR(stmt.op, mem, stmt.ureg[1]);
      if (reg.equals(stmt.ureg[1])) return i386_FlowAssembler.insn_RM(stmt.op, stmt.ureg[0], mem);
    }
    if (stmt.format.equals("\t%op\t%u0,%d0") && stmt.ureg.length == 1) {
      if (reg.equals(stmt.ureg[0])) return i386_FlowAssembler.insn_MW(stmt.op, mem, stmt.dreg[0]);
      if (reg.equals(stmt.dreg[0])) return i386_FlowAssembler.insn_RM(stmt.op, stmt.ureg[0], mem);
    }
    if (stmt.format.equals("\t%op\t%u0,%d0") && stmt.ureg.length == 2) {
      if (reg.equals(stmt.ureg[0])) return i386_FlowAssembler.insn_MA(stmt.op, mem, stmt.dreg[0]);
      if (reg.equals(stmt.dreg[0])) return i386_FlowAssembler.insn_RM(stmt.op, stmt.ureg[0], mem);
    }
    if (stmt.format.equals("\t%op\t%d0,%d1")) {
      if (reg.equals(stmt.dreg[0])) return i386_FlowAssembler.insn_MA(stmt.op, mem, stmt.dreg[1]);
      if (reg.equals(stmt.dreg[1])) return i386_FlowAssembler.insn_AM(stmt.op, stmt.dreg[0], mem);
    }
    if (stmt.format.equals("\t%op\t%i0,%u0"))
      return i386_FlowAssembler.insn_IM(stmt.op, stmt.imm[0], mem);
    if (stmt.format.equals("\t%op\t%i0,%d0"))
      return i386_FlowAssembler.insn_IM(stmt.op, stmt.imm[0], mem);
    if (stmt.format.equals("\t%op\t%j0,%u0"))
      return i386_FlowAssembler.insn_JM(stmt.op, stmt.jmm[0], mem);
    if (stmt.format.equals("\t%op\t%j0,%d0"))
      return i386_FlowAssembler.insn_JM(stmt.op, stmt.jmm[0], mem);
    if (stmt.format.equals("\t%op\t%i0,%u0,%d0")) {
      if (reg.equals(stmt.ureg[0])) return i386_FlowAssembler.insn_IMW(stmt.op, stmt.imm[0], mem, stmt.dreg[0]);
      if (reg.equals(stmt.dreg[0])) return i386_FlowAssembler.insn_IRM(stmt.op, stmt.imm[0], stmt.ureg[0], mem);
    }
    if (stmt.format.equals("\t%op\t%j0,%u0,%d0"))
      return i386_FlowAssembler.insn_JMW(stmt.op, stmt.jmm[0], mem, stmt.dreg[0]);
    throw new IllegalArgumentException();
  }

  protected void allowsInMemory(AssemblerStatement _stmt, BitSet set) {
    if (!(_stmt instanceof InstructionStatement))
      return;
    InstructionStatement stmt = (InstructionStatement)_stmt;
    if (stmt.format.equals("\t%op\t%u0"))
      switch (stmt.op.index) {
      case _callw: case _calll:
      case _divb: case _divw: case _divl:
      case _idivb: case _idivw: case _idivl:
      case _imulb: case _imulw: case _imull:
      case _jmpw: case _jmpl:
      case _lldt:
      case _lmsw:
      case _ltr:
      case _mulb: case _mulw: case _mull:
      case _pushw: case _pushl:
      case _verr: case _verw:
        stmt.ureg[0].addTo(set);
      }
    if (stmt.format.equals("\t%op\t%d0"))
      switch (stmt.op.index) {
      case _popw: case _popl:
      case _seta: case _setae: case _setb: case _setbe: case _setc: case _sete:
      case _setg: case _setge: case _setl: case _setle: case _setna: case _setnae:
      case _setnb: case _setnbe: case _setnc: case _setne: case _setng: case _setnge:
      case _setnl: case _setnle: case _setno: case _setnp: case _setns: case _setnz:
      case _seto: case _setp: case _setpe: case _setpo: case _sets: case _setz:
      case _sldt:
      case _smsw:
      case _str:
      case _decb: case _decw: case _decl:
      case _incb: case _incw: case _incl:
      case _negb: case _negw: case _negl:
      case _notb: case _notw: case _notl:
      case _rclb: case _rclw: case _rcll:
      case _rcrb: case _rcrw: case _rcrl:
      case _rolb: case _rolw: case _roll:
      case _rorb: case _rorw: case _rorl:
      case _salb: case _salw: case _sall:
      case _sarb: case _sarw: case _sarl:
      case _shlb: case _shlw: case _shll:
      case _shrb: case _shrw: case _shrl:
        stmt.dreg[0].addTo(set);
      }
    if (stmt.format.equals("\t%op\t%u0,%u1"))
      if (stmt.ureg[0] != stmt.ureg[1])
        switch (stmt.op.index) {
        case _btw: case _btl:
        case _testb: case _testw: case _testl:
          stmt.ureg[0].addTo(set);
          break;
        case _cmpb: case _cmpw: case _cmpl:
          stmt.ureg[0].addTo(set);
          stmt.ureg[1].addTo(set);
        }
    if (stmt.format.equals("\t%op\t%u0,%d0"))
      if (stmt.ureg[0] != stmt.dreg[0])
        switch (stmt.op.index) {
        case _bsfw: case _bsfl:
        case _bsrw: case _bsrl:
        case _larw: case _larl:
        case _lslw: case _lsll:
        case _movws:
        case _movsxbw: case _movsxbl: case _movsxwl:
        case _movzxbw: case _movzxbl: case _movzxwl:
        case _imulw: case _imull:
          stmt.ureg[0].addTo(set);
          break;
        case _movb: case _movw: case _movl:
        case _adcb: case _adcw: case _adcl:
        case _addb: case _addw: case _addl:
        case _andb: case _andw: case _andl:
        case _orb: case _orw: case _orl:
        case _sbbb: case _sbbw: case _sbbl:
        case _subb: case _subw: case _subl:
        case _xorb: case _xorw: case _xorl:
          stmt.ureg[0].addTo(set);
          stmt.dreg[0].addTo(set);
          break;
        case _movsw:
        case _arpl:
        case _btcw: case _btcl:
        case _btrw: case _btrl:
        case _btsw: case _btsl:
        case _shldw: case _shldl:
        case _shrdw: case _shrdl:
          stmt.dreg[0].addTo(set);
        }
    if (stmt.format.equals("\t%op\t%d0,%d1"))
      if (stmt.dreg[0] != stmt.dreg[1])
        switch (stmt.op.index) {
        case _xchgb: case _xchgw: case _xchgl:
          stmt.dreg[0].addTo(set);
          stmt.dreg[1].addTo(set);
        }
    if (stmt.format.equals("\t%op\t%i0,%u0"))
      switch (stmt.op.index) {
      case _btw: case _btl:
      case _cmpb: case _cmpw: case _cmpl:
      case _testb: case _testw: case _testl:
        stmt.ureg[0].addTo(set);
      }

    if (stmt.format.equals("\t%op\t%i0,%d0"))
      switch (stmt.op.index) {
      case _movb: case _movw: case _movl:
      case _adcb: case _adcw: case _adcl:
      case _addb: case _addw: case _addl:
      case _andb: case _andw: case _andl:
      case _btcw: case _btcl:
      case _btrw: case _btrl:
      case _btsw: case _btsl:
      case _orb: case _orw: case _orl:
      case _rclb: case _rclw: case _rcll:
      case _rcrb: case _rcrw: case _rcrl:
      case _rolb: case _rolw: case _roll:
      case _rorb: case _rorw: case _rorl:
      case _salb: case _salw: case _sall:
      case _sarb: case _sarw: case _sarl:
      case _shlb: case _shlw: case _shll:
      case _shrb: case _shrw: case _shrl:
      case _sbbb: case _sbbw: case _sbbl:
      case _subb: case _subw: case _subl:
      case _xorb: case _xorw: case _xorl:
        stmt.dreg[0].addTo(set);
      }
    if (stmt.format.equals("\t%op\t%j0,%u0"))
      switch (stmt.op.index) {
      case _cmpw: case _cmpl:
      case _testw: case _testl:
        stmt.ureg[0].addTo(set);
      }
    if (stmt.format.equals("\t%op\t%j0,%d0"))
      switch (stmt.op.index) {
      case _movw: case _movl:
      case _adcw: case _adcl:
      case _addw: case _addl:
      case _andw: case _andl:
      case _orw: case _orl:
      case _sbbw: case _sbbl:
      case _subw: case _subl:
      case _xorw: case _xorl:
        stmt.dreg[0].addTo(set);
      }
    if (stmt.format.equals("\t%op\t%i0,%u0,%d0"))
      if (stmt.ureg[0] != stmt.dreg[0])
        switch (stmt.op.index) {
        case _imulw: case _imull:
          stmt.ureg[0].addTo(set);
          break;
        case _shldw: case _shldl:
        case _shrdw: case _shrdl:
          stmt.dreg[0].addTo(set);
        }
    if (stmt.format.equals("\t%op\t%j0,%u0,%d0"))
      if (stmt.ureg[0] != stmt.dreg[0])
        switch (stmt.op.index) {
        case _imulw: case _imull:
          stmt.ureg[0].addTo(set);
        }
  }

  protected void incompatibleRegisters(AssemblerStatement _stmt, Reg reg, BitSet set) {
    InstructionStatement stmt = (InstructionStatement)_stmt;
    switch (stmt.op.index) {
    case _divb: case _idivb: case _imulb: case _mulb:
    case _seta: case _setae: case _setb: case _setbe:
    case _setc: case _sete: case _setg: case _setge:
    case _setl: case _setle: case _setna: case _setnae:
    case _setnb: case _setnbe: case _setnc: case _setne:
    case _setng: case _setnge: case _setnl: case _setnle:
    case _setno: case _setnp: case _setns: case _setnz:
    case _seto: case _setp: case _setpe: case _setpo:
    case _sets: case _setz:
    case _decb: case _incb: case _negb: case _notb:
    case _rclb: case _rcrb: case _rolb: case _rorb:
    case _salb: case _sarb: case _shlb: case _shrb:
    case _cmpb: case _testb:
    case _movb:
    case _adcb: case _addb: case _andb: case _orb:
    case _sbbb: case _subb: case _xorb:
    case _xchgb:
      if (stmt.ureg != null)
        for (int i = 0; i < stmt.ureg.length; i++)
          if (stmt.ureg[i].equals(reg))
            r8(set);
      if (stmt.dreg != null)
        for (int i = 0; i < stmt.dreg.length; i++)
          if (stmt.dreg[i].equals(reg))
            r8(set);
      break;
    case _callw: case _divw: case _idivw: case _imulw:
    case _jmpw: case _lldt: case _lmsw: case _ltr:
    case _mulw: case _pushw: case _verr: case _verw:
    case _popw: case _sldt: case _smsw: case _str:
    case _decw: case _incw: case _negw: case _notw:
    case _rclw: case _rcrw: case _rolw: case _rorw:
    case _salw: case _sarw: case _shlw: case _shrw:
    case _btw: case _cmpw: case _testw:
    case _bsfw: case _bsrw: case _larw: case _lslw:
    case _movw:
    case _adcw: case _addw: case _andw: case _arpl:
    case _btcw: case _btrw: case _btsw:
    case _orw: case _sbbw: case _shldw: case _shrdw:
    case _subw: case _xorw:
    case _xchgw:
    case _boundw:
    case _ldsw:
    case _lssw: case _lesw: case _lfsw: case _lgsw:
    case _leaw:
      if (stmt.ureg != null)
        for (int i = 0; i < stmt.ureg.length; i++)
          if (stmt.ureg[i].equals(reg))
            r16(set);
      if (stmt.dreg != null)
        for (int i = 0; i < stmt.dreg.length; i++)
          if (stmt.dreg[i].equals(reg))
            r16(set);
      break;
    case _calll: case _divl: case _idivl: case _imull:
    case _jmpl: case _mull: case _pushl:
    case _popl:
    case _decl: case _incl: case _negl: case _notl:
    case _rcll: case _rcrl: case _roll: case _rorl:
    case _sall: case _sarl: case _shll: case _shrl:
    case _btl: case _cmpl: case _testl:
    case _bsfl: case _bsrl: case _larl: case _lsll:
    case _movl:
    case _adcl: case _addl: case _andl: case _btcl:
    case _btrl: case _btsl: case _orl:
    case _sbbl: case _shldl: case _shrdl: case _subl:
    case _xorl:
    case _xchgl:
    case _boundl:
    case _ldsl:
    case _lssl: case _lesl: case _lfsl: case _lgsl:
    case _leal:
      if (stmt.ureg != null)
        for (int i = 0; i < stmt.ureg.length; i++)
          if (stmt.ureg[i].equals(reg))
            r32(set);
      if (stmt.dreg != null)
        for (int i = 0; i < stmt.dreg.length; i++)
          if (stmt.dreg[i].equals(reg))
            r32(set);
      break;
    case _pushs:
    case _pops:
      if (stmt.ureg != null)
        for (int i = 0; i < stmt.ureg.length; i++)
          if (stmt.ureg[i].equals(reg))
            rs(set);
      if (stmt.dreg != null)
        for (int i = 0; i < stmt.dreg.length; i++)
          if (stmt.dreg[i].equals(reg))
            rs(set);
      break;
    case _fcom: case _fcomp: case _fst: case _fstp:
    case _fucom: case _fucomp:
    case _ffree: case _fld:
    case _faddp: case _fdivp: case _fdivrp: case _fmulp:
    case _fsubp: case _fsubrp: case _fxch:
    case _fadd: case _fdiv: case _fdivr: case _fmul:
    case _fsub: case _fsubr:
      if (stmt.ureg != null)
        for (int i = 0; i < stmt.ureg.length; i++)
          if (stmt.ureg[i].equals(reg))
            rf(set);
      if (stmt.dreg != null)
        for (int i = 0; i < stmt.dreg.length; i++)
          if (stmt.dreg[i].equals(reg))
            rf(set);
    }
    if (stmt.lmem != null)
      for (int i = 0; i < stmt.lmem.length; i++)
        m(stmt.lmem[i], reg, set);
    if (stmt.smem != null)
      for (int i = 0; i < stmt.smem.length; i++)
        m(stmt.smem[i], reg, set);
    if (stmt.format.equals("\t%op\t%u0,%d0"))
      switch (stmt.op.index) {
      case _movws:
        if (stmt.ureg[0].equals(reg)) r16(set);
        if (stmt.dreg[0].equals(reg)) rs(set);
        break;
      case _movsw:
        if (stmt.ureg[0].equals(reg)) rs(set);
        if (stmt.dreg[0].equals(reg)) r16(set);
        break;
      case _movlc:
        if (stmt.ureg[0].equals(reg)) r32(set);
        if (stmt.dreg[0].equals(reg)) rc(set);
        break;
      case _movcl:
        if (stmt.ureg[0].equals(reg)) rc(set);
        if (stmt.dreg[0].equals(reg)) r32(set);
        break;
      case _movlg:
        if (stmt.ureg[0].equals(reg)) r32(set);
        if (stmt.dreg[0].equals(reg)) rd(set);
        break;
      case _movgl:
        if (stmt.ureg[0].equals(reg)) rd(set);
        if (stmt.dreg[0].equals(reg)) r32(set);
        break;
      case _movlt:
        if (stmt.ureg[0].equals(reg)) r32(set);
        if (stmt.dreg[0].equals(reg)) rt(set);
        break;
      case _movtl:
        if (stmt.ureg[0].equals(reg)) rt(set);
        if (stmt.dreg[0].equals(reg)) r32(set);
        break;
      case _movsxbw: case _movzxbw:
        if (stmt.ureg[0].equals(reg)) r8(set);
        if (stmt.dreg[0].equals(reg)) r16(set);
        break;
      case _movsxbl: case _movzxbl:
        if (stmt.ureg[0].equals(reg)) r8(set);
        if (stmt.dreg[0].equals(reg)) r32(set);
        break;
      case _movsxwl: case _movzxwl:
        if (stmt.ureg[0].equals(reg)) r16(set);
        if (stmt.dreg[0].equals(reg)) r32(set);
      }
    if (stmt.format.equals("\t%op\t%u0,%l0"))
      switch (stmt.op.index) {
      case _movsw:
        if (stmt.ureg[0].equals(reg)) rs(set);
      }
    if (stmt.format.equals("\t%op\t%l0,%d0"))
      switch (stmt.op.index) {
      case _movsxbw:
      case _movzxbw:
        if (stmt.dreg[0].equals(reg)) r16(set);
        break;
      case _movsxbl:
      case _movsxwl: case _movzxbl: case _movzxwl:
        if (stmt.dreg[0].equals(reg)) r32(set);
        break;
      case _movws:
        if (stmt.dreg[0].equals(reg)) rs(set);
      }
  }

  private void m(Addr mem, Reg reg, BitSet set) {
    if (reg.equals(((i386_Addr)mem).sreg0)) r32(set);
    if (reg.equals(((i386_Addr)mem).sreg1)) r32(set);
  }

  private void rs(BitSet set) {
    set.set(_AL); set.set(_CL); set.set(_DL); set.set(_BL); set.set(_AH); set.set(_CH); set.set(_DH); set.set(_BH);
    set.set(_AX); set.set(_CX); set.set(_DX); set.set(_BX); set.set(_SP); set.set(_BP); set.set(_SI); set.set(_DI);
    set.set(_EAX); set.set(_ECX); set.set(_EDX); set.set(_EBX); set.set(_ESP); set.set(_EBP); set.set(_ESI); set.set(_EDI);
    set.set(_CR0); set.set(_CR1); set.set(_CR2); set.set(_CR3); set.set(_CR4); set.set(_CR5); set.set(_CR6); set.set(_CR7);
    set.set(_DR0); set.set(_DR1); set.set(_DR2); set.set(_DR3); set.set(_DR4); set.set(_DR5); set.set(_DR6); set.set(_DR7);
    set.set(_TR0); set.set(_TR1); set.set(_TR2); set.set(_TR3); set.set(_TR4); set.set(_TR5); set.set(_TR6); set.set(_TR7);
    set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7);
  }

  private void r8(BitSet set) {
    set.set(_ES); set.set(_CS); set.set(_SS); set.set(_DS); set.set(_FS); set.set(_GS);
    set.set(_AX); set.set(_CX); set.set(_DX); set.set(_BX); set.set(_SP); set.set(_BP); set.set(_SI); set.set(_DI);
    set.set(_EAX); set.set(_ECX); set.set(_EDX); set.set(_EBX); set.set(_ESP); set.set(_EBP); set.set(_ESI); set.set(_EDI);
    set.set(_CR0); set.set(_CR1); set.set(_CR2); set.set(_CR3); set.set(_CR4); set.set(_CR5); set.set(_CR6); set.set(_CR7);
    set.set(_DR0); set.set(_DR1); set.set(_DR2); set.set(_DR3); set.set(_DR4); set.set(_DR5); set.set(_DR6); set.set(_DR7);
    set.set(_TR0); set.set(_TR1); set.set(_TR2); set.set(_TR3); set.set(_TR4); set.set(_TR5); set.set(_TR6); set.set(_TR7);
    set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7);
  }

  private void r16(BitSet set) {
    set.set(_ES); set.set(_CS); set.set(_SS); set.set(_DS); set.set(_FS); set.set(_GS);
    set.set(_AL); set.set(_CL); set.set(_DL); set.set(_BL); set.set(_AH); set.set(_CH); set.set(_DH); set.set(_BH);
    set.set(_EAX); set.set(_ECX); set.set(_EDX); set.set(_EBX); set.set(_ESP); set.set(_EBP); set.set(_ESI); set.set(_EDI);
    set.set(_CR0); set.set(_CR1); set.set(_CR2); set.set(_CR3); set.set(_CR4); set.set(_CR5); set.set(_CR6); set.set(_CR7);
    set.set(_DR0); set.set(_DR1); set.set(_DR2); set.set(_DR3); set.set(_DR4); set.set(_DR5); set.set(_DR6); set.set(_DR7);
    set.set(_TR0); set.set(_TR1); set.set(_TR2); set.set(_TR3); set.set(_TR4); set.set(_TR5); set.set(_TR6); set.set(_TR7);
    set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7);
  }

  private void r32(BitSet set) {
    set.set(_ES); set.set(_CS); set.set(_SS); set.set(_DS); set.set(_FS); set.set(_GS);
    set.set(_AL); set.set(_CL); set.set(_DL); set.set(_BL); set.set(_AH); set.set(_CH); set.set(_DH); set.set(_BH);
    set.set(_AX); set.set(_CX); set.set(_DX); set.set(_BX); set.set(_SP); set.set(_BP); set.set(_SI); set.set(_DI);
    set.set(_CR0); set.set(_CR1); set.set(_CR2); set.set(_CR3); set.set(_CR4); set.set(_CR5); set.set(_CR6); set.set(_CR7);
    set.set(_DR0); set.set(_DR1); set.set(_DR2); set.set(_DR3); set.set(_DR4); set.set(_DR5); set.set(_DR6); set.set(_DR7);
    set.set(_TR0); set.set(_TR1); set.set(_TR2); set.set(_TR3); set.set(_TR4); set.set(_TR5); set.set(_TR6); set.set(_TR7);
    set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7);
  }

  private void rc(BitSet set) {
    set.set(_ES); set.set(_CS); set.set(_SS); set.set(_DS); set.set(_FS); set.set(_GS);
    set.set(_AL); set.set(_CL); set.set(_DL); set.set(_BL); set.set(_AH); set.set(_CH); set.set(_DH); set.set(_BH);
    set.set(_AX); set.set(_CX); set.set(_DX); set.set(_BX); set.set(_SP); set.set(_BP); set.set(_SI); set.set(_DI);
    set.set(_EAX); set.set(_ECX); set.set(_EDX); set.set(_EBX); set.set(_ESP); set.set(_EBP); set.set(_ESI); set.set(_EDI);
    set.set(_DR0); set.set(_DR1); set.set(_DR2); set.set(_DR3); set.set(_DR4); set.set(_DR5); set.set(_DR6); set.set(_DR7);
    set.set(_TR0); set.set(_TR1); set.set(_TR2); set.set(_TR3); set.set(_TR4); set.set(_TR5); set.set(_TR6); set.set(_TR7);
    set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7);
  }

  private void rd(BitSet set) {
    set.set(_ES); set.set(_CS); set.set(_SS); set.set(_DS); set.set(_FS); set.set(_GS);
    set.set(_AL); set.set(_CL); set.set(_DL); set.set(_BL); set.set(_AH); set.set(_CH); set.set(_DH); set.set(_BH);
    set.set(_AX); set.set(_CX); set.set(_DX); set.set(_BX); set.set(_SP); set.set(_BP); set.set(_SI); set.set(_DI);
    set.set(_EAX); set.set(_ECX); set.set(_EDX); set.set(_EBX); set.set(_ESP); set.set(_EBP); set.set(_ESI); set.set(_EDI);
    set.set(_CR0); set.set(_CR1); set.set(_CR2); set.set(_CR3); set.set(_CR4); set.set(_CR5); set.set(_CR6); set.set(_CR7);
    set.set(_TR0); set.set(_TR1); set.set(_TR2); set.set(_TR3); set.set(_TR4); set.set(_TR5); set.set(_TR6); set.set(_TR7);
    set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7);
  }

  private void rt(BitSet set) {
    set.set(_ES); set.set(_CS); set.set(_SS); set.set(_DS); set.set(_FS); set.set(_GS);
    set.set(_AL); set.set(_CL); set.set(_DL); set.set(_BL); set.set(_AH); set.set(_CH); set.set(_DH); set.set(_BH);
    set.set(_AX); set.set(_CX); set.set(_DX); set.set(_BX); set.set(_SP); set.set(_BP); set.set(_SI); set.set(_DI);
    set.set(_EAX); set.set(_ECX); set.set(_EDX); set.set(_EBX); set.set(_ESP); set.set(_EBP); set.set(_ESI); set.set(_EDI);
    set.set(_CR0); set.set(_CR1); set.set(_CR2); set.set(_CR3); set.set(_CR4); set.set(_CR5); set.set(_CR6); set.set(_CR7);
    set.set(_DR0); set.set(_DR1); set.set(_DR2); set.set(_DR3); set.set(_DR4); set.set(_DR5); set.set(_DR6); set.set(_DR7);
    set.set(_ST0); set.set(_ST1); set.set(_ST2); set.set(_ST3); set.set(_ST4); set.set(_ST5); set.set(_ST6); set.set(_ST7);
  }

  private void rf(BitSet set) {
    set.set(_ES); set.set(_CS); set.set(_SS); set.set(_DS); set.set(_FS); set.set(_GS);
    set.set(_AL); set.set(_CL); set.set(_DL); set.set(_BL); set.set(_AH); set.set(_CH); set.set(_DH); set.set(_BH);
    set.set(_AX); set.set(_CX); set.set(_DX); set.set(_BX); set.set(_SP); set.set(_BP); set.set(_SI); set.set(_DI);
    set.set(_EAX); set.set(_ECX); set.set(_EDX); set.set(_EBX); set.set(_ESP); set.set(_EBP); set.set(_ESI); set.set(_EDI);
    set.set(_CR0); set.set(_CR1); set.set(_CR2); set.set(_CR3); set.set(_CR4); set.set(_CR5); set.set(_CR6); set.set(_CR7);
    set.set(_DR0); set.set(_DR1); set.set(_DR2); set.set(_DR3); set.set(_DR4); set.set(_DR5); set.set(_DR6); set.set(_DR7);
    set.set(_TR0); set.set(_TR1); set.set(_TR2); set.set(_TR3); set.set(_TR4); set.set(_TR5); set.set(_TR6); set.set(_TR7);
  }

}

