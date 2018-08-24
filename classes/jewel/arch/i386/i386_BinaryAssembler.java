/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Addr;
import jewel.core.bend.BinaryAssembler;
import jewel.core.bend.Imm;
import jewel.core.bend.Reg;
import jewel.core.bend.Symbol;

// 1) lacks support for 16-bit symbolic addresses
// 2) relative addresses (for jumps) are always 32-bit
public class i386_BinaryAssembler extends BinaryAssembler implements i386_Architecture {

  private boolean address16;
  private boolean operand16;

  public i386_BinaryAssembler() { }

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

  public void align(int value) {
    while (size()%value != 0)
      nop();
  }

  /* decode segment register */
  protected int rs(Reg rs) {
    switch (rs.index) {
    case _ES: return 0;
    case _CS: return 1;
    case _SS: return 2;
    case _DS: return 3;
    case _FS: return 4;
    case _GS: return 5;
    }
    throw new IllegalArgumentException();
  }

  /* decode 8-bit register */
  protected int r8(Reg r8) {
    switch (r8.index) {
    case _AL: return 0;
    case _CL: return 1;
    case _DL: return 2;
    case _BL: return 3;
    case _AH: return 4;
    case _CH: return 5;
    case _DH: return 6;
    case _BH: return 7;
    }
    throw new IllegalArgumentException();
  }

  /* decode 16-bit register */
  protected int r16(Reg r16) {
    switch (r16.index) {
    case _AX: return 0;
    case _CX: return 1;
    case _DX: return 2;
    case _BX: return 3;
    case _SP: return 4;
    case _BP: return 5;
    case _SI: return 6;
    case _DI: return 7;
    }
    throw new IllegalArgumentException();
  }

  /* decode 32-bit register */
  protected int r32(Reg r32) {
    switch (r32.index) {
    case _EAX: return 0;
    case _ECX: return 1;
    case _EDX: return 2;
    case _EBX: return 3;
    case _ESP: return 4;
    case _EBP: return 5;
    case _ESI: return 6;
    case _EDI: return 7;
    }
    throw new IllegalArgumentException();
  }

  /* decode control register */
  protected int rc(Reg rc) {
    switch (rc.index) {
    case _CR0: return 0;
    case _CR1: return 1;
    case _CR2: return 2;
    case _CR3: return 3;
    case _CR4: return 4;
    case _CR5: return 5;
    case _CR6: return 6;
    case _CR7: return 7;
    }
    throw new IllegalArgumentException();
  }

  /* decode debug register */
  protected int rd(Reg rd) {
    switch (rd.index) {
    case _DR0: return 0;
    case _DR1: return 1;
    case _DR2: return 2;
    case _DR3: return 3;
    case _DR4: return 4;
    case _DR5: return 5;
    case _DR6: return 6;
    case _DR7: return 7;
    }
    throw new IllegalArgumentException();
  }

  /* decode test register */
  protected int rt(Reg rt) {
    switch (rt.index) {
    case _TR0: return 0;
    case _TR1: return 1;
    case _TR2: return 2;
    case _TR3: return 3;
    case _TR4: return 4;
    case _TR5: return 5;
    case _TR6: return 6;
    case _TR7: return 7;
    }
    throw new IllegalArgumentException();
  }

  /* decode floating point register */
  protected int rf(Reg rf) {
    switch (rf.index) {
    case _ST0: return 0;
    case _ST1: return 1;
    case _ST2: return 2;
    case _ST3: return 3;
    case _ST4: return 4;
    case _ST5: return 5;
    case _ST6: return 6;
    case _ST7: return 7;
    }
    throw new IllegalArgumentException();
  }

  /* decode memory addressing mode */
  protected int m(Addr _mem) {
    i386_Addr mem = (i386_Addr)_mem;
    int scale = mem.scale;
    Reg sreg1 = mem.sreg1;
    Reg sreg0 = mem.sreg0;
    Symbol symbol = mem.symbol;
    int disp = mem.disp;

    int dz = 0;
    int mod = 0;
    boolean ism16 = false;
    boolean hs = false;
    int rm = 0;
    int ss = 0;
    int indx = 0;
    int base = 0;

    if (sreg0 == null)
      switch (sreg1.index) {
      case _SI: ism16 = true; rm = 4; break;
      case _DI: ism16 = true; rm = 5; break;
      case _BP: ism16 = true; rm = 6; break;
      case _BX: ism16 = true; rm = 7; break;
      case _EAX: rm = 0; indx = 0; break;
      case _ECX: rm = 1; indx = 1; break;
      case _EDX: rm = 2; indx = 2; break;
      case _EBX: rm = 3; indx = 3; break;
      case _ESP: rm = 4; indx = 4; break;
      case _EBP: rm = 5; indx = 5; break;
      case _ESI: rm = 6; indx = 6; break;
      case _EDI: rm = 7; indx = 7; break;
      default: throw new IllegalArgumentException();
      }
    else
      switch (sreg0.index) {
      case _BX:
        switch (sreg1.index) {
        case _SI: ism16 = true; rm = 0; break;
        case _DI: ism16 = true; rm = 1; break;
        default: throw new IllegalArgumentException();
        }
        break;
      case _BP:
        switch (sreg1.index) {
        case _SI: ism16 = true; rm = 2; break;
        case _DI: ism16 = true; rm = 3; break;
        default: throw new IllegalArgumentException();
        }
        break;
      case _SI:
        switch (sreg1.index) {
        case _BX: ism16 = true; rm = 0; break;
        case _BP: ism16 = true; rm = 2; break;
        default: throw new IllegalArgumentException();
        }
        break;
      case _DI:
        switch (sreg1.index) {
        case _BX: ism16 = true; rm = 1; break;
        case _BP: ism16 = true; rm = 3; break;
        default: throw new IllegalArgumentException();
        }
        break;
      case _EAX: base = 0; break;
      case _ECX: base = 1; break;
      case _EDX: base = 2; break;
      case _EBX: base = 3; break;
      case _ESP: base = 4; break;
      case _EBP: base = 5; break;
      case _ESI: base = 6; break;
      case _EDI: base = 7; break;
      default: throw new IllegalArgumentException();
      }

    if (ism16) {

      if (scale != 1)
        throw new IllegalArgumentException();
      if (disp != 0 || rm == 6)
        if (is8(disp)) {
          mod = 1;
          dz = 1;
        } else {
          mod = 2;
          dz = 2;
        }

    } else {

      if (sreg0 == null) {

        if (scale == 1) {
          if (rm == 4) {
            hs = true;
            ss = 0;
            base = 4;
          }
          if (disp != 0 || rm == 5)
            if (is8(disp)) {
              mod = 1;
              dz = 1;
            } else {
              mod = 2;
              dz = 4;
            }
        } else if (scale == 2) {
          if (indx == 4)
            throw new IllegalArgumentException();
          hs = true;
          rm = 4;
          ss = 0;
          base = indx;
          if (disp != 0 || indx == 5)
            if (is8(disp)) {
              mod = 1;
              dz = 1;
            } else {
              mod = 2;
              dz = 4;
            }
        } else {
          if (indx == 4)
            throw new IllegalArgumentException();
          hs = true;
          rm = 4;
          switch (scale) {
          case 4: ss = 2; break;
          case 8: ss = 3; break;
          }
          base = 5;
          mod = 0;
          dz = 4;
        }
      
      } else {

        hs = true;
        rm  = 4;
        indx = r32(sreg1);
        if (indx == 4) {
          if (base == 4 || scale != 1)
            throw new IllegalArgumentException();
          int temp = base;
          base = indx;
          indx = temp;
        }
        if (base == 5 && scale == 1) {
          int temp = base;
          base = indx;
          indx = temp;
        }
        if (disp != 0 || base == 5)
          if (is8(disp)) {
            mod = 1;
            dz = 1;
          } else {
            mod = 2;
            dz = 4;
          }
        switch (scale) {
        case 1: ss = 0; break;
        case 2: ss = 1; break;
        case 4: ss = 2; break;
        case 8: ss = 3; break;
        }

      }

    }

    int sib = (ss & 3) << 6 | (indx & 7) << 3 | (base & 7);
    if (dz == 4)
      dz = 3;
    int ctrl = 0;
    ctrl |= 1 << 31;
    ctrl |= (dz & 3) << 16;
    ctrl |= (mod & 3) << 14;
    ctrl |= (ism16 ? 1 : 0) << 12;
    ctrl |= (hs ? 1 : 0) << 11;
    ctrl |= (rm & 7) << 8;
    ctrl |= (sib & 255);
    return ctrl;
  }

  /* read memory field */
  protected int rm(Addr _addr) {
    i386_Addr addr = (i386_Addr)_addr;
    if (addr.symbol == null) {
      int ctrl = m(addr);
      return (ctrl >> 8) & 7;
    }
    return 5;//hardcoded 32-bit, 6 for 16-bit
  }

  protected int mod(Addr _addr) {
    i386_Addr addr = (i386_Addr)_addr;
    if (addr.symbol == null) {
      int ctrl = m(addr);
      return (ctrl >> 14) & 3;
    }
    return 0;
  }

  protected int dz(Addr _addr) {
    i386_Addr addr = (i386_Addr)_addr;
    if (addr.symbol == null) {
      int ctrl = m(addr);
      int dz = (ctrl >> 16) & 3;
      if (dz == 3)
        dz = 4;
      return dz;
    }
    return 4;//hardcoded 32-bit, 2 for 16-bit
  }

  protected boolean ism16(Addr _addr) {
    i386_Addr addr = (i386_Addr)_addr;
    if (addr.symbol == null) {
      int ctrl = m(addr);
      return ((ctrl >> 12) & 1) != 0;
    }
    return false;//hardcoded 32-bit, true for 16-bit
  }

  protected boolean hs(Addr _addr) {
    i386_Addr addr = (i386_Addr)_addr;
    if (addr.symbol == null) {
      int ctrl = m(addr);
      return ((ctrl >> 11) & 1) != 0;
    }
    return false;
  }
  
  protected int sib(Addr _addr) {
    i386_Addr addr = (i386_Addr)_addr;
    if (addr.symbol == null) {
      int ctrl = m(addr);
      return ctrl & 255;
    }
    throw new IllegalArgumentException();
  }

  /* outputs address size override prefix */
  protected void ad16() {
    if (!address16)
      prefix(0x67);
  }
  
  protected void ad32() {
    if (address16)
      prefix(0x67);
  }

  /* outputs operand size override prefix */
  protected void op16() {
    if (!operand16)
      prefix(0x66);
  }
  
  protected void op32() {
    if (operand16)
      prefix(0x66);
  }

  /* output a prefix */
  protected void prefix(int op) {
    write(op);
  }

  /* outputs opcode */
  protected void opcode8(int op) {
    write(op);
  }

  protected void opcode16(int op) {
    op16();
    opcode8(op|0x01);
  }
  
  protected void opcode32(int op) {
    op32();
    opcode8(op|0x01);
  }

  protected void opcode88(int op1, int op2) {
    opcode8(op1);
    opcode8(op2);
  }

  protected void opcodex8(int op) {
    opcode88(0x0F, op);
  }
  
  protected void opcodex16(int op) {
    op16();
    opcodex8(op|0x01);
  }
  
  protected void opcodex32(int op) {
    op32();
    opcodex8(op|0x01);
  }
  
  protected void opcodef8(int op) {
    opcode88(0xD9, op);
  }
  
  /* override memory address size */
  protected void overrd(Addr addr) {
    if (ism16(addr))
      ad16();
    else
      ad32();
  }

  /* outputs modrm */
  protected void modrm(int mod, int reg, int rm) {
    if ((mod & ~3) != 0 || (reg & ~7) != 0 || (rm & ~7) != 0)
      throw new IllegalArgumentException();
    int modrm = (mod & 3) << 6 | (reg & 7) << 3 | (rm & 7);
    write(modrm);
  }

  /* outputs sibdisp */
  protected void sibdisp(Addr _addr) {
    i386_Addr addr = (i386_Addr)_addr;
    if (hs(addr))
      write(sib(addr));
    int disp = addr.disp;
    if (addr.symbol == null)
      switch (dz(addr)) {
      case 1: imm8 (disp); break;
      case 2: imm16(disp); break;
      case 4: imm32(disp); break;
      }
    else {
      Symbol ssym = addr.symbol;
      write(false, disp, ssym);
    }
  }

  /* outputs a relative */
  protected void rel32(Imm _imm) {
    i386_Imm imm = (i386_Imm)_imm;
    write(true, imm.disp, imm.symbol);
  }

  /* test if is signed byte */
  protected boolean is8(int imm) {
    return -128 <= imm && imm <= 127;
  }

  /* test if special case (what?) */
  protected boolean ismd(Addr addr) {
    return mod(addr) == 0 && (rm(addr) == 5 || rm(addr) == 6) && dz(addr) != 0;
  }

  /* output 8-bit, 16-bit and 32-bit immediates */
  protected void imm8(int imm) {
    if (imm < -128 || imm > 255)
      throw new IllegalArgumentException();
    write(imm);
  }

  protected void imm16(int imm) {
    if (imm < -32768 || imm > 65535)
      throw new IllegalArgumentException();
    write(imm      & 0xFF);
    write(imm >> 8 & 0xFF);
  }

  protected void imm16(Imm _imm) {
    i386_Imm imm = (i386_Imm)_imm;
    write(false, imm.disp, imm.symbol);
  }

  protected void imm32(int imm) {
    write(imm       & 0xFF);
    write(imm >>  8 & 0xFF);
    write(imm >> 16 & 0xFF);
    write(imm >> 24 & 0xFF);
  }

  protected void imm32(Imm _imm) {
    i386_Imm imm = (i386_Imm)_imm;
    write(false, imm.disp, imm.symbol);
  }

  /* outputs data */
  public final void db(int val) {
    imm8(val);
  }

  public final void dw(int val) {
    imm16(val);
  }

  public final void dw(Imm imm) {
    imm16(imm);
  }

  public final void dl(int val) {
    imm32(val);
  }

  public final void dl(Imm imm) {
    imm32(imm);
  }

  public final void dll(long val) {
    imm32((int)val);
    imm32((int)(val >> 32));
  }

  public final void ds(float val) {
    dl(Float.floatToIntBits(val));
  }

  public final void dl(double val) {
    dll(Double.doubleToLongBits(val));
  }

  /* ADC/ADD/AND/CMP/OR/SBB/SUB/XOR */
  protected void template_adcb(int op1, int op2, Reg sreg, Reg treg) {
    int sr = r8(sreg);
    int tr = r8(treg);
    opcode8(op1);
    modrm(3, sr, tr);
  }
  protected void template_adcw(int op1, int op2, Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    opcode16(op1);
    modrm(3, sr, tr);
  }
  protected void template_adcl(int op1, int op2, Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    opcode32(op1);
    modrm(3, sr, tr);
  }
  protected void template_adcb(int op1, int op2, Reg sreg, Addr tmem) {
    int sr = r8(sreg);
    overrd(tmem);
    opcode8(op1);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_adcw(int op1, int op2, Reg sreg, Addr tmem) {
    int sr = r16(sreg);
    overrd(tmem);
    opcode16(op1);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_adcl(int op1, int op2, Reg sreg, Addr tmem) {
    int sr = r32(sreg);
    overrd(tmem);
    opcode32(op1);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_adcb(int op1, int op2, Addr smem, Reg treg) {
    int tr = r8(treg);
    overrd(smem);
    opcode8(op1|0x02);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  protected void template_adcw(int op1, int op2, Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    opcode16(op1|0x02);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  protected void template_adcl(int op1, int op2, Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    opcode32(op1|0x02);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  protected void template_adcb(int op1, int op2, int simm, Reg treg) {
    int tr = r8(treg);
    if (tr == 0)
      opcode8(op1|0x04);
    else {
      opcode8(0x80);
      modrm(3, op2, tr);
    }
    imm8(simm);
  }
  protected void template_adcw(int op1, int op2, int simm, Reg treg) {
    int tr = r16(treg);
    if (tr == 0) {
      opcode16(op1|0x04);
      imm16(simm);
    } else {
      boolean iss = is8(simm);
      opcode16(iss?0x82:0x80);
      modrm(3, op2, tr);
      if (iss) imm8(simm); else imm16(simm);
    }
  }
  protected void template_adcl(int op1, int op2, int simm, Reg treg) {
    int tr = r32(treg);
    boolean iss = is8(simm);
    if (tr == 0 && !iss) {
      opcode32(op1|0x04);
      imm32(simm);
    } else {
      opcode32(iss?0x82:0x80);
      modrm(3, op2, tr);
      if (iss) imm8(simm); else imm32(simm);
    }
  }
  protected void template_adcb(int op1, int op2, int simm, Addr tmem) {
    overrd(tmem);
    opcode8(0x80);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
    imm8(simm);
  }
  protected void template_adcw(int op1, int op2, int simm, Addr tmem) {
    boolean iss = is8(simm);
    overrd(tmem);
    opcode16(iss?0x82:0x80);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
    if (iss) imm8(simm); else imm16(simm);
  }
  protected void template_adcl(int op1, int op2, int simm, Addr tmem) {
    boolean iss = is8(simm);
    overrd(tmem);
    opcode32(iss?0x82:0x80);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
    if (iss) imm8(simm); else imm32(simm);
  }
  protected void template_adcw(int op1, int op2, Imm simm, Reg treg) {
    int tr = r16(treg);
    if (tr == 0) {
      opcode16(op1|0x04);
      imm16(simm);
    } else {
      opcode16(0x80);
      modrm(3, op2, tr);
      imm16(simm);
    }
  }
  protected void template_adcl(int op1, int op2, Imm simm, Reg treg) {
    int tr = r32(treg);
    if (tr == 0) {
      opcode32(op1|0x04);
      imm32(simm);
    } else {
      opcode32(0x80);
      modrm(3, op2, tr);
      imm32(simm);
    }
  }
  protected void template_adcw(int op1, int op2, Imm simm, Addr tmem) {
    overrd(tmem);
    opcode16(0x80);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
    imm16(simm);
  }
  protected void template_adcl(int op1, int op2, Imm simm, Addr tmem) {
    overrd(tmem);
    opcode32(0x80);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
    imm32(simm);
  }

  /* BSF/BSR */
  protected void template_bsfw(int op, Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    op16();
    opcodex8(op);
    modrm(3, tr, sr);
  }
  protected void template_bsfw(int op, Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    op16();
    opcodex8(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  protected void template_bsfl(int op, Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    op32();
    opcodex8(op);
    modrm(3, tr, sr);
  }
  protected void template_bsfl(int op, Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    op32();
    opcodex8(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }

  /* BT/BTC/BTR/BTS */
  protected void template_btw(int op1, int op2, Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    op16();
    opcodex8(op1);
    modrm(3, sr, tr);
  }
  protected void template_btw(int op1, int op2, Reg sreg, Addr tmem) {
    int sr = r16(sreg);
    overrd(tmem);
    op16();
    opcodex8(op1);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_btl(int op1, int op2, Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    op32();
    opcodex8(op1);
    modrm(3, sr, tr);
  }
  protected void template_btl(int op1, int op2, Reg sreg, Addr tmem) {
    int sr = r32(sreg);
    overrd(tmem);
    op32();
    opcodex8(op1);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_btw(int op1, int op2, int simm, Reg treg) {
    int tr = r16(treg);
    op16();
    opcodex8(0xBA);
    modrm(3, op2, tr);
    imm8(simm);
  }
  protected void template_btw(int op1, int op2, int simm, Addr tmem) {
    overrd(tmem);
    op16();
    opcodex8(0xBA);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
    imm8(simm);
  }
  protected void template_btl(int op1, int op2, int simm, Reg treg) {
    int tr = r32(treg);
    op32();
    opcodex8(0xBA);
    modrm(3, op2, tr);
    imm8(simm);
  }
  protected void template_btl(int op1, int op2, int simm, Addr tmem) {
    overrd(tmem);
    op32();
    opcodex8(0xBA);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
    imm8(simm);
  }

  /* CBW/CWD/IRET/POPA/POPF/PUSHA/PUSHF */
  protected void template_cbw(int op) {
    op16();
    opcode8(op);
  }

  /* CWDE/CDQ/IRETD/POPAD/POPFD/PUSHAD/PUSHFD */
  protected void template_cwde(int op) {
    op32();
    opcode8(op);
  }

  /* DEC/INC */
  protected void template_decb(int op1, int op2, Reg treg) {
    int tr = r8(treg);
    opcode8(0xFE);
    modrm(3, op2, tr);
  }
  protected void template_decw(int op1, int op2, Reg treg) {
    int tr = r16(treg);
    op16();
    opcode8(op1|tr);
  }
  protected void template_decl(int op1, int op2, Reg treg) {
    int tr = r32(treg);
    op32();
    opcode8(op1|tr);
  }
  protected void template_decb(int op1, int op2, Addr tmem) {
    overrd(tmem);
    opcode8(0xFE);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_decw(int op1, int op2, Addr tmem) {
    overrd(tmem);
    opcode16(0xFE);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_decl(int op1, int op2, Addr tmem) {
    overrd(tmem);
    opcode32(0xFE);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
  }

  /* DIV/IDIV/MUL/NEG/NOT */
  protected void template_divb(int op, Reg treg) {
    int tr = r8(treg);
    opcode8(0xF6);
    modrm(3, op, tr);
  }
  protected void template_divw(int op, Reg treg) {
    int tr = r16(treg);
    opcode16(0xF6);
    modrm(3, op, tr);
  }
  protected void template_divl(int op, Reg treg) {
    int tr = r32(treg);
    opcode32(0xF6);
    modrm(3, op, tr);
  }
  protected void template_divb(int op, Addr tmem) {
    overrd(tmem);
    opcode8(0xF6);
    modrm(mod(tmem), op, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_divw(int op, Addr tmem) {
    overrd(tmem);
    opcode16(0xF6);
    modrm(mod(tmem), op, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_divl(int op, Addr tmem) {
    overrd(tmem);
    opcode32(0xF6);
    modrm(mod(tmem), op, rm(tmem));
    sibdisp(tmem);
  }

  /* FADD/FDIV/FDIVR/FMUL/FSUB/FSUBR */
  protected void template_fadds(int op, Addr smem) {
    overrd(smem);
    opcode8(0xD8);
    modrm(mod(smem), op, rm(smem));
    sibdisp(smem);
  }
  protected void template_faddl(int op, Addr smem) {
    overrd(smem);
    opcode8(0xDC);
    modrm(mod(smem), op, rm(smem));
    sibdisp(smem);
  }
  protected void template_fadd(int op, Reg sreg, Reg treg) {
    int sr = rf(sreg);
    int tr = rf(treg);
    boolean st2 = tr != 0;
    if (st2 && sr != 0)
       throw new IllegalArgumentException();
    opcode8(st2?0xDC:0xD8);
    modrm(3, op, st2?tr:sr);
  }

  /* FADDP/FDIVP/FDIVRP/FMULP/FSUBP/FSUBRP */
  protected void template_faddp(int op) {
    opcode8(0xDE);
    modrm(3, op, 1);
  }
  protected void template_faddp(int op, Reg treg) {
    int tr = rf(treg);
    opcode8(0xDE);
    modrm(3, op, tr);
  }

  /* FBLD/FBSTP */
  protected void template_fbld(int op, Addr smem) {
    overrd(smem);
    opcode8(0xDF);
    modrm(mod(smem), op, rm(smem));
    sibdisp(smem);
  }

  /* FCLEX/FINIT */
  protected void template_fclex(int op) {
    opcode8(0x9B);
    opcode88(0xDB, op);
  }

  /* FCOM/FCOMP */
  protected void template_fcoms(int op, Addr smem) {
    overrd(smem);
    opcode8(0xD8);
    modrm(mod(smem), op, rm(smem));
    sibdisp(smem);
  }
  protected void template_fcoml(int op, Addr smem) {
    overrd(smem);
    opcode8(0xDC);
    modrm(mod(smem), op, rm(smem));
    sibdisp(smem);
  }
  protected void template_fcom(int op, Reg sreg) {
    int sr = rf(sreg);
    opcode8(0xD8);
    modrm(3, op, sr);
  }
  protected void template_fcom(int op) {
    opcode8(0xD8);
    modrm(3, op, 1);
  }

  /* FIADD/FICOM/FICOMP/FIDIV/FIDIVR/FIMUL/FISUB/FISUBR */
  protected void template_fiaddw(int op, Addr smem) {
    overrd(smem);
    opcode8(0xDE);
    modrm(mod(smem), op, rm(smem));
    sibdisp(smem);
  }
  protected void template_fiaddl(int op, Addr smem) {
    overrd(smem);
    opcode8(0xDA);
    modrm(mod(smem), op, rm(smem));
    sibdisp(smem);
  }

  /* FILD/FISTP */
  protected void template_fild(int op1, int op2, Addr smem) {
    overrd(smem);
    opcode8(0xDF);
    modrm(mod(smem), op1, rm(smem));
    sibdisp(smem);
  }
  protected void template_fildl(int op1, int op2, Addr smem) {
    overrd(smem);
    opcode8(0xDB);
    modrm(mod(smem), op1, rm(smem));
    sibdisp(smem);
  }
  protected void template_fildll(int op1, int op2, Addr smem) {
    overrd(smem);
    opcode8(0xDF);
    modrm(mod(smem), op2, rm(smem));
    sibdisp(smem);
  }

  /* FLD/FSTP */
  protected void template_flds(int op1, int op2, Addr smem) {
    overrd(smem);
    opcode8(0xD9);
    modrm(mod(smem), op1, rm(smem));
    sibdisp(smem);
  }
  protected void template_fldl(int op1, int op2, Addr smem) {
    overrd(smem);
    opcode8(0xDD);
    modrm(mod(smem), op1, rm(smem));
    sibdisp(smem);
  }
  protected void template_fldll(int op1, int op2, Addr smem) {
    overrd(smem);
    opcode8(0xDB);
    modrm(mod(smem), op2, rm(smem));
    sibdisp(smem);
  }
  protected void template_fld(int op1, int op2, Reg sreg) {
    int sr = rf(sreg);
    opcode8(0xD9);
    modrm(3, op1, sr);
  }

  /* FLDCW/FNSTCW */
  protected void template_fldcw(int op, Addr smem) {
    overrd(smem);
    opcode8(0xD9);
    modrm(mod(smem), op, rm(smem));
    sibdisp(smem);
  }

  /* FLDENV/FRSTOR/FNSAVE/FNSTENV */
  protected void template_fldenv(int op1, int op2, Addr smem) {
    overrd(smem);
    opcode8(op1);
    modrm(mod(smem), op2, rm(smem));
    sibdisp(smem);
  }

  /* FNCLEX/FNINIT */
  protected void template_fnclex(int op) {
    opcode88(0xDB, op);
  }

  /* FSAVE/FSTENV */
  protected void template_fsave(int op, Addr smem) {
    overrd(smem);
    opcode8(op);
    modrm(mod(smem), 6, rm(smem));
    sibdisp(smem);
  }

  /* FUCOM/FUCOMP/FXCH */
  protected void template_fucom(int op1, int op2, Reg sreg) {
    int sr = rf(sreg);
    opcode8(op1);
    modrm(3, op2, sr);
  }
  protected void template_fucom(int op1, int op2) {
    opcode8(op1);
    modrm(3, op2, 1);
  }

  /* JA/JAE/JB/JBE/JC/JE/JG/JL/JLE/JNA/JNAE/JNB/JNBE */
  /* JNC/JNE/JNG/JNGE/JNL/JNLE/JNO/JNP/JNS/JNZ/JO/JP */
  /* JPE/JPO/JS/JZ */
  protected void template_ja(int op, Imm simm) {
    op32();
    opcodex8(op|0x80);
    rel32(simm);
  }

  /* LAR/LSL */
  protected void template_larw(int op, Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    op16();
    opcodex8(op);
    modrm(3, tr, sr);
  }
  protected void template_larl(int op, Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    op32();
    opcodex8(op);
    modrm(3, tr, sr);
  }
  protected void template_larw(int op, Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    op16();
    opcodex8(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  protected void template_larl(int op, Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    op32();
    opcodex8(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }

  /* LDS/LES */
  protected void template_ldsw(int op, Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    op16();
    opcode8(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  protected void template_ldsl(int op, Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    op32();
    opcode8(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }

  /* LFS/LGS/LSS */
  protected void template_lfsw(int op, Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    op16();
    opcodex8(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  protected void template_lfsl(int op, Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    op32();
    opcodex8(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }

  /* LGDT/LIDT/SGDT/SIDT */
  protected void template_lgdt(int op, Addr smem) {
    overrd(smem);
    opcodex8(0x01);
    modrm(mod(smem), op, rm(smem));
    sibdisp(smem);
  }

  /* LLDT/LMSW/LTR/SLDT/SMSW/STR/VERR/VERW */
  protected void template_lldt(int op1, int op2, Reg treg) {
    int tr = r16(treg);
    opcodex8(op1);
    modrm(3, op2, tr);
  }
  protected void template_lldt(int op1, int op2, Addr tmem) {
    overrd(tmem);
    opcodex8(op1);
    modrm(mod(tmem), op2, rm(tmem));
    sibdisp(tmem);
  }

  /* MOVSX/MOVZX */
  protected void template_movsbw(int op, Reg sreg, Reg treg) {
    int sr = r8(sreg);
    int tr = r16(treg);
    op16();
    opcodex8(op);
    modrm(3, tr, sr);
  }
  protected void template_movsbl(int op, Reg sreg, Reg treg) {
    int sr = r8(sreg);
    int tr = r32(treg);
    op32();
    opcodex8(op);
    modrm(3, tr, sr);
  }
  protected void template_movswl(int op, Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r32(treg);
    opcodex32(op);
    modrm(3, tr, sr);
  }
  protected void template_movsbw(int op, Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    op16();
    opcodex8(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  protected void template_movsbl(int op, Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    op32();
    opcodex8(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  protected void template_movswl(int op, Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    opcodex32(op);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  
  /* RET/RETN/RETF */
  protected void template_ret(int op) {
    opcode8(op|0x01);
  }
  protected void template_ret(int op, int simm) {
    opcode8(op);
    imm16(simm);
  }

  /* RCL/RCR/ROL/ROR */
  /* SAL/SAR/SHL/SHR */
  protected void template_rclb(int op, int simm, Reg treg) {
    int tr = r8(treg);
    if (simm == 1) {
      opcode8(0xD0);
      modrm(3, op, tr);
    } else {
      opcode8(0xC0);
      modrm(3, op, tr);
      imm8(simm);
    }
  }
  protected void template_rclw(int op, int simm, Reg treg) {
    int tr = r16(treg);
    if (simm == 1) {
      opcode16(0xD0);
      modrm(3, op, tr);
    } else {
      opcode16(0xC0);
      modrm(3, op, tr);
      imm8(simm);
    }
  }
  protected void template_rcll(int op, int simm, Reg treg) {
    int tr = r32(treg);
    if (simm == 1) {
      opcode32(0xD0);
      modrm(3, op, tr);
    } else {
      opcode32(0xC0);
      modrm(3, op, tr);
      imm8(simm);
    }
  }
  protected void template_rclb(int op, int simm, Addr tmem) {
    overrd(tmem);
    if (simm == 1) {
      opcode8(0xD0);
      modrm(mod(tmem), op, rm(tmem));
      sibdisp(tmem);
    } else {
      opcode8(0xC0);
      modrm(mod(tmem), op, rm(tmem));
      sibdisp(tmem);
      imm8(simm);
    }
  }
  protected void template_rclw(int op, int simm, Addr tmem) {
    overrd(tmem);
    if (simm == 1) {
      opcode16(0xD0);
      modrm(mod(tmem), op, rm(tmem));
      sibdisp(tmem);
    } else {
      opcode16(0xC0);
      modrm(mod(tmem), op, rm(tmem));
      sibdisp(tmem);
      imm8(simm);
    }
  }
  protected void template_rcll(int op, int simm, Addr tmem) {
    overrd(tmem);
    if (simm == 1) {
      opcode32(0xD0);
      modrm(mod(tmem), op, rm(tmem));
      sibdisp(tmem);
    } else {
      opcode32(0xC0);
      modrm(mod(tmem), op, rm(tmem));
      sibdisp(tmem);
      imm8(simm);
    }
  }
  protected void template_rclb(int op, Reg treg) {
    int tr = r8(treg);
    opcode8(0xD2);
    modrm(3, op, tr);
  }
  protected void template_rclw(int op, Reg treg) {
    int tr = r16(treg);
    opcode16(0xD2);
    modrm(3, op, tr);
  }
  protected void template_rcll(int op, Reg treg) {
    int tr = r32(treg);
    opcode32(0xD2);
    modrm(3, op, tr);
  }
  protected void template_rclb(int op, Addr tmem) {
    overrd(tmem);
    opcode8(0xD2);
    modrm(mod(tmem), op, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_rclw(int op, Addr tmem) {
    overrd(tmem);
    opcode16(0xD2);
    modrm(mod(tmem), op, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_rcll(int op, Addr tmem) {
    overrd(tmem);
    opcode32(0xD2);
    modrm(mod(tmem), op, rm(tmem));
    sibdisp(tmem);
  }

  /* SETO/SETNO/SETB/SETNB/SETE/SETNE/SETA/SETNA */
  /* SETS/SETNS/SETP/SETNP/SETL/SETNL/SETG/SETNG */
  protected void template_seta(int op, Reg treg) {
    int tr = r8(treg);
    opcodex8(op);
    modrm(3, 0, tr);
  }
  protected void template_seta(int op, Addr tmem) {
    overrd(tmem);
    opcodex8(op);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
  }

  /* SHLD/SHRD */
  protected void template_shldw(int op, int simm, Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    op16();
    opcodex8(op);
    modrm(3, sr, tr);
    imm8(simm);
  }
  protected void template_shldl(int op, int simm, Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    op32();
    opcodex8(op);
    modrm(3, sr, tr);
    imm8(simm);
  }
  protected void template_shldw(int op, int simm, Reg sreg, Addr tmem) {
    int sr = r16(sreg);
    overrd(tmem);
    op16();
    opcodex8(op);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
    imm8(simm);
  }
  protected void template_shldl(int op, int simm, Reg sreg, Addr tmem) {
    int sr = r32(sreg);
    overrd(tmem);
    op32();
    opcodex8(op);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
    imm8(simm);
  }
  protected void template_shldw(int op, Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    op16();
    opcodex8(op|0x01);
    modrm(3, sr, tr);
  }
  protected void template_shldl(int op, Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    op32();
    opcodex8(op|0x01);
    modrm(3, sr, tr);
  }
  protected void template_shldw(int op, Reg sreg, Addr tmem) {
    int sr = r16(sreg);
    overrd(tmem);
    op16();
    opcodex8(op|0x01);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  protected void template_shldl(int op, Reg sreg, Addr tmem) {
    int sr = r32(sreg);
    overrd(tmem);
    op32();
    opcodex8(op|0x01);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }

  /* CS/DS/ES/FS/GS/SS */
  public final void cs() {
    prefix(0x2E);
  }

  public final void ds() {
    prefix(0x3E);
  }

  public final void es() {
    prefix(0x26);
  }

  public final void fs() {
    prefix(0x64);
  }

  public final void gs() {
    prefix(0x65);
  }

  public final void ss() {
    prefix(0x36);
  }

  /* AAA */
  public final void aaa() {
    opcode8(0x37);
  }

  /* AAD */
  public final void aad() {
    opcode88(0xD5, 0x0A);
  }

  /* AAM */
  public final void aam() {
    opcode88(0xD4, 0x0A);
  }

  /* AAS */
  public final void aas() {
    opcode8(0x3F);
  }

  /* ADC */
  public final void adcb(Reg sreg, Reg treg) { template_adcb(0x10, 0x02, sreg, treg); }
  public final void adcw(Reg sreg, Reg treg) { template_adcw(0x10, 0x02, sreg, treg); }
  public final void adcl(Reg sreg, Reg treg) { template_adcl(0x10, 0x02, sreg, treg); }
  public final void adcb(Reg sreg, Addr tmem) { template_adcb(0x10, 0x02, sreg, tmem); }
  public final void adcw(Reg sreg, Addr tmem) { template_adcw(0x10, 0x02, sreg, tmem); }
  public final void adcl(Reg sreg, Addr tmem) { template_adcl(0x10, 0x02, sreg, tmem); }
  public final void adcb(Addr smem, Reg treg) { template_adcb(0x10, 0x02, smem, treg); }
  public final void adcw(Addr smem, Reg treg) { template_adcw(0x10, 0x02, smem, treg); }
  public final void adcl(Addr smem, Reg treg) { template_adcl(0x10, 0x02, smem, treg); }
  public final void adcb(int simm, Reg treg) { template_adcb(0x10, 0x02, simm, treg); }
  public final void adcw(int simm, Reg treg) { template_adcw(0x10, 0x02, simm, treg); }
  public final void adcl(int simm, Reg treg) { template_adcl(0x10, 0x02, simm, treg); }
  public final void adcb(int simm, Addr tmem) { template_adcb(0x10, 0x02, simm, tmem); }
  public final void adcw(int simm, Addr tmem) { template_adcw(0x10, 0x02, simm, tmem); }
  public final void adcl(int simm, Addr tmem) { template_adcl(0x10, 0x02, simm, tmem); }
  public final void adcw(Imm simm, Reg treg) { template_adcw(0x10, 0x02, simm, treg); }
  public final void adcl(Imm simm, Reg treg) { template_adcl(0x10, 0x02, simm, treg); }
  public final void adcw(Imm simm, Addr tmem) { template_adcw(0x10, 0x02, simm, tmem); }
  public final void adcl(Imm simm, Addr tmem) { template_adcl(0x10, 0x02, simm, tmem); }
  
  /* ADD */
  public final void addb(Reg sreg, Reg treg) { template_adcb(0x00, 0x00, sreg, treg); }
  public final void addw(Reg sreg, Reg treg) { template_adcw(0x00, 0x00, sreg, treg); }
  public final void addl(Reg sreg, Reg treg) { template_adcl(0x00, 0x00, sreg, treg); }
  public final void addb(Reg sreg, Addr tmem) { template_adcb(0x00, 0x00, sreg, tmem); }
  public final void addw(Reg sreg, Addr tmem) { template_adcw(0x00, 0x00, sreg, tmem); }
  public final void addl(Reg sreg, Addr tmem) { template_adcl(0x00, 0x00, sreg, tmem); }
  public final void addb(Addr smem, Reg treg) { template_adcb(0x00, 0x00, smem, treg); }
  public final void addw(Addr smem, Reg treg) { template_adcw(0x00, 0x00, smem, treg); }
  public final void addl(Addr smem, Reg treg) { template_adcl(0x00, 0x00, smem, treg); }
  public final void addb(int simm, Reg treg) { template_adcb(0x00, 0x00, simm, treg); }
  public final void addw(int simm, Reg treg) { template_adcw(0x00, 0x00, simm, treg); }
  public final void addl(int simm, Reg treg) { template_adcl(0x00, 0x00, simm, treg); }
  public final void addb(int simm, Addr tmem) { template_adcb(0x00, 0x00, simm, tmem); }
  public final void addw(int simm, Addr tmem) { template_adcw(0x00, 0x00, simm, tmem); }
  public final void addl(int simm, Addr tmem) { template_adcl(0x00, 0x00, simm, tmem); }
  public final void addw(Imm simm, Reg treg) { template_adcw(0x00, 0x00, simm, treg); }
  public final void addl(Imm simm, Reg treg) { template_adcl(0x00, 0x00, simm, treg); }
  public final void addw(Imm simm, Addr tmem) { template_adcw(0x00, 0x00, simm, tmem); }
  public final void addl(Imm simm, Addr tmem) { template_adcl(0x00, 0x00, simm, tmem); }

  /* AND */
  public final void andb(Reg sreg, Reg treg) { template_adcb(0x20, 0x04, sreg, treg); }
  public final void andw(Reg sreg, Reg treg) { template_adcw(0x20, 0x04, sreg, treg); }
  public final void andl(Reg sreg, Reg treg) { template_adcl(0x20, 0x04, sreg, treg); }
  public final void andb(Reg sreg, Addr tmem) { template_adcb(0x20, 0x04, sreg, tmem); }
  public final void andw(Reg sreg, Addr tmem) { template_adcw(0x20, 0x04, sreg, tmem); }
  public final void andl(Reg sreg, Addr tmem) { template_adcl(0x20, 0x04, sreg, tmem); }
  public final void andb(Addr smem, Reg treg) { template_adcb(0x20, 0x04, smem, treg); }
  public final void andw(Addr smem, Reg treg) { template_adcw(0x20, 0x04, smem, treg); }
  public final void andl(Addr smem, Reg treg) { template_adcl(0x20, 0x04, smem, treg); }
  public final void andb(int simm, Reg treg) { template_adcb(0x20, 0x04, simm, treg); }
  public final void andw(int simm, Reg treg) { template_adcw(0x20, 0x04, simm, treg); }
  public final void andl(int simm, Reg treg) { template_adcl(0x20, 0x04, simm, treg); }
  public final void andb(int simm, Addr tmem) { template_adcb(0x20, 0x04, simm, tmem); }
  public final void andw(int simm, Addr tmem) { template_adcw(0x20, 0x04, simm, tmem); }
  public final void andl(int simm, Addr tmem) { template_adcl(0x20, 0x04, simm, tmem); }
  public final void andw(Imm simm, Reg treg) { template_adcw(0x20, 0x04, simm, treg); }
  public final void andl(Imm simm, Reg treg) { template_adcl(0x20, 0x04, simm, treg); }
  public final void andw(Imm simm, Addr tmem) { template_adcw(0x20, 0x04, simm, tmem); }
  public final void andl(Imm simm, Addr tmem) { template_adcl(0x20, 0x04, simm, tmem); }

  /* ARPL */
  public final void arpl(Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    opcode8(0x63);
    modrm(3, sr, tr);
  }
  public final void arpl(Reg sreg, Addr tmem) {
    int sr = r16(sreg);
    overrd(tmem);
    opcode8(0x63);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }

  /* BOUND */
  public final void boundw(Addr smem, Reg sreg) {
    int sr = r16(sreg);
    overrd(smem);
    op16();
    opcode8(0x62);
    modrm(mod(smem), sr, rm(smem));
    sibdisp(smem);
  }
  public final void boundl(Addr smem, Reg sreg) {
    int sr = r32(sreg);
    overrd(smem);
    op32();
    opcode8(0x62);
    modrm(mod(smem), sr, rm(smem));
    sibdisp(smem);
  }

  /* BSF */
  public final void bsfw(Reg sreg, Reg treg) { template_bsfw(0xBC, sreg, treg); }
  public final void bsfw(Addr smem, Reg treg) { template_bsfw(0xBC, smem, treg); }
  public final void bsfl(Reg sreg, Reg treg) { template_bsfl(0xBC, sreg, treg); }
  public final void bsfl(Addr smem, Reg treg) { template_bsfl(0xBC, smem, treg); }

  /* BSR */
  public final void bsrw(Reg sreg, Reg treg) { template_bsfw(0xBD, sreg, treg); }
  public final void bsrw(Addr smem, Reg treg) { template_bsfw(0xBD, smem, treg); }
  public final void bsrl(Reg sreg, Reg treg) { template_bsfl(0xBD, sreg, treg); }
  public final void bsrl(Addr smem, Reg treg) { template_bsfl(0xBD, smem, treg); }

  /* BT */
  public final void btw(Reg sreg, Reg treg) { template_btw(0xA3, 0x04, sreg, treg); }
  public final void btw(Reg sreg, Addr tmem) { template_btw(0xA3, 0x04, sreg, tmem); }
  public final void btl(Reg sreg, Reg treg) { template_btl(0xA3, 0x04, sreg, treg); }
  public final void btl(Reg sreg, Addr tmem) { template_btl(0xA3, 0x04, sreg, tmem); }
  public final void btw(int simm, Reg treg) { template_btw(0xA3, 0x04, simm, treg); }
  public final void btw(int simm, Addr tmem) { template_btw(0xA3, 0x04, simm, tmem); }
  public final void btl(int simm, Reg treg) { template_btl(0xA3, 0x04, simm, treg); }
  public final void btl(int simm, Addr tmem) { template_btl(0xA3, 0x04, simm, tmem); }

  /* BTC */
  public final void btcw(Reg sreg, Reg treg) { template_btw(0xBB, 0x07, sreg, treg); }
  public final void btcw(Reg sreg, Addr tmem) { template_btw(0xBB, 0x07, sreg, tmem); }
  public final void btcl(Reg sreg, Reg treg) { template_btl(0xBB, 0x07, sreg, treg); }
  public final void btcl(Reg sreg, Addr tmem) { template_btl(0xBB, 0x07, sreg, tmem); }
  public final void btcw(int simm, Reg treg) { template_btw(0xBB, 0x07, simm, treg); }
  public final void btcw(int simm, Addr tmem) { template_btw(0xBB, 0x07, simm, tmem); }
  public final void btcl(int simm, Reg treg) { template_btl(0xBB, 0x07, simm, treg); }
  public final void btcl(int simm, Addr tmem) { template_btl(0xBB, 0x07, simm, tmem); }
           
  /* BTR */
  public final void btrw(Reg sreg, Reg treg) { template_btw(0xB3, 0x06, sreg, treg); }
  public final void btrw(Reg sreg, Addr tmem) { template_btw(0xB3, 0x06, sreg, tmem); }
  public final void btrl(Reg sreg, Reg treg) { template_btl(0xB3, 0x06, sreg, treg); }
  public final void btrl(Reg sreg, Addr tmem) { template_btl(0xB3, 0x06, sreg, tmem); }
  public final void btrw(int simm, Reg treg) { template_btw(0xB3, 0x06, simm, treg); }
  public final void btrw(int simm, Addr tmem) { template_btw(0xB3, 0x06, simm, tmem); }
  public final void btrl(int simm, Reg treg) { template_btl(0xB3, 0x06, simm, treg); }
  public final void btrl(int simm, Addr tmem) { template_btl(0xB3, 0x06, simm, tmem); }

  /* BTS */
  public final void btsw(Reg sreg, Reg treg) { template_btw(0xAB, 0x05, sreg, treg); }
  public final void btsw(Reg sreg, Addr tmem) { template_btw(0xAB, 0x05, sreg, tmem); }
  public final void btsl(Reg sreg, Reg treg) { template_btl(0xAB, 0x05, sreg, treg); }
  public final void btsl(Reg sreg, Addr tmem) { template_btl(0xAB, 0x05, sreg, tmem); }
  public final void btsw(int simm, Reg treg) { template_btw(0xAB, 0x05, simm, treg); }
  public final void btsw(int simm, Addr tmem) { template_btw(0xAB, 0x05, simm, tmem); }
  public final void btsl(int simm, Reg treg) { template_btl(0xAB, 0x05, simm, treg); }
  public final void btsl(int simm, Addr tmem) { template_btl(0xAB, 0x05, simm, tmem); }

  /* CALL */
  public final void call(Imm simm) { 
    opcode8(0xE8);
    rel32(simm);
  }
  public final void callw(Reg sreg) {
    int sr = r16(sreg);
    op16();
    opcode8(0xFF);
    modrm(3, 2, sr);
  }
  public final void calll(Reg sreg) {
    int sr = r32(sreg);
    op32();
    opcode8(0xFF);
    modrm(3, 2, sr);
  }
  public final void callw(Addr smem) {
    overrd(smem);
    op16();
    opcode8(0xFF);
    modrm(mod(smem), 2, rm(smem));
    sibdisp(smem);
  }
  public final void calll(Addr smem) {
    overrd(smem);
    op32();
    opcode8(0xFF);
    modrm(mod(smem), 2, rm(smem));
    sibdisp(smem);
  }
  public final void callf(Imm simm) { throw new Error("Unimplemented"); }
  public final void callfw(Addr smem) {
    overrd(smem);
    op16();
    opcode8(0xFF);
    modrm(mod(smem), 3, rm(smem));
    sibdisp(smem);
  }
  public final void callfl(Addr smem) {
    overrd(smem);
    op32();
    opcode8(0xFF);
    modrm(mod(smem), 3, rm(smem));
    sibdisp(smem);
  }

  /* CBW/CWDE */
  public final void cbw() { template_cbw(0x98); }
  public final void cwde() { template_cwde(0x98); }

  /* CLC */
  public final void clc() {
    opcode8(0xF8);
  }

  /* CLD */
  public final void cld() {
    opcode8(0xFC);
  }

  /* CLI */
  public final void cli() {
    opcode8(0xFA);
  }

  /* CLTS */
  public final void clts() { 
    opcodex8(0x06);
  }

  /* CMC */
  public final void cmc() { 
    opcode8(0xF5);
  }

  /* CMP */
  public final void cmpb(Reg sreg, Reg treg) { template_adcb(0x38, 0x07, sreg, treg); }
  public final void cmpw(Reg sreg, Reg treg) { template_adcw(0x38, 0x07, sreg, treg); }
  public final void cmpl(Reg sreg, Reg treg) { template_adcl(0x38, 0x07, sreg, treg); }
  public final void cmpb(Reg sreg, Addr tmem) { template_adcb(0x38, 0x07, sreg, tmem); }
  public final void cmpw(Reg sreg, Addr tmem) { template_adcw(0x38, 0x07, sreg, tmem); }
  public final void cmpl(Reg sreg, Addr tmem) { template_adcl(0x38, 0x07, sreg, tmem); }
  public final void cmpb(Addr smem, Reg treg) { template_adcb(0x38, 0x07, smem, treg); }
  public final void cmpw(Addr smem, Reg treg) { template_adcw(0x38, 0x07, smem, treg); }
  public final void cmpl(Addr smem, Reg treg) { template_adcl(0x38, 0x07, smem, treg); }
  public final void cmpb(int simm, Reg treg) { template_adcb(0x38, 0x07, simm, treg); }
  public final void cmpw(int simm, Reg treg) { template_adcw(0x38, 0x07, simm, treg); }
  public final void cmpl(int simm, Reg treg) { template_adcl(0x38, 0x07, simm, treg); }
  public final void cmpb(int simm, Addr tmem) { template_adcb(0x38, 0x07, simm, tmem); }
  public final void cmpw(int simm, Addr tmem) { template_adcw(0x38, 0x07, simm, tmem); }
  public final void cmpl(int simm, Addr tmem) { template_adcl(0x38, 0x07, simm, tmem); }
  public final void cmpw(Imm simm, Reg treg) { template_adcw(0x38, 0x07, simm, treg); }
  public final void cmpl(Imm simm, Reg treg) { template_adcl(0x38, 0x07, simm, treg); }
  public final void cmpw(Imm simm, Addr tmem) { template_adcw(0x38, 0x07, simm, tmem); }
  public final void cmpl(Imm simm, Addr tmem) { template_adcl(0x38, 0x07, simm, tmem); }

  /* CMPSB/CMPSW/CMPSD */
  public final void cmpsb() {
    opcode8(0xA6);
  }
  public final void cmpsw() {
    opcode16(0xA6);
  }
  public final void cmpsd() {
    opcode32(0xA6);
  }

  /* CWD/CDQ */
  public final void cwd() { template_cbw(0x99); }
  public final void cdq() { template_cwde(0x99); }

  /* DAA */
  public final void daa() {
    opcode8(0x27);
  }

  /* DAS */
  public final void das() {
    opcode8(0x2F);
  }

  /* DEC */
  public final void decb(Reg treg) { template_decb(0x48, 0x01, treg); }
  public final void decw(Reg treg) { template_decw(0x48, 0x01, treg); }
  public final void decl(Reg treg) { template_decl(0x48, 0x01, treg); }
  public final void decb(Addr tmem) { template_decb(0x48, 0x01, tmem); }
  public final void decw(Addr tmem) { template_decw(0x48, 0x01, tmem); }
  public final void decl(Addr tmem) { template_decl(0x48, 0x01, tmem); }

  /* DIV */
  public final void divb(Reg treg) { template_divb(0x06, treg); }
  public final void divw(Reg treg) { template_divw(0x06, treg); }
  public final void divl(Reg treg) { template_divl(0x06, treg); }
  public final void divb(Addr tmem) { template_divb(0x06, tmem); }
  public final void divw(Addr tmem) { template_divw(0x06, tmem); }
  public final void divl(Addr tmem) { template_divl(0x06, tmem); }

  /* ENTER */
  public final void enter(int simm1, int simm2) {
    opcode8(0xC8);
    imm16(simm1);
    imm8(simm2);
  }

  /* F2XM1 */
  public final void f2xm1() {
    opcodef8(0xF0);
  }

  /* FABS */
  public final void fabs() {
    opcodef8(0xE1);
  }
  
  /* FADD/FADDP/FIADD */
  public final void fadds(Addr smem) { template_fadds(0x00, smem); }
  public final void faddl(Addr smem) { template_faddl(0x00, smem); }
  public final void fadd(Reg sreg, Reg treg) { template_fadd(0x00, sreg, treg); }
  public final void faddp(Reg treg) { template_faddp(0x00, treg); }
  public final void faddp() { template_faddp(0x00); }
  public final void fiaddw(Addr smem) { template_fiaddw(0x00, smem); }
  public final void fiaddl(Addr smem) { template_fiaddl(0x00, smem); }

  /* FBLD */
  public final void fbld(Addr smem) { template_fbld(0x04, smem); }

  /* FBSTP */
  public final void fbstp(Addr smem) { template_fbld(0x06, smem); }

  /* FCHS */
  public final void fchs() {
    opcodef8(0xE0);
  }

  /* FCLEX/FNCLEX */
  public final void fclex() { template_fclex(0xE2); }
  public final void fnclex() { template_fnclex(0xE2); }

  /* FCOM/FCOMP/FCOMPP */
  public final void fcoms(Addr smem) { template_fcoms(0x02, smem); }
  public final void fcoml(Addr smem) { template_fcoml(0x02, smem); }
  public final void fcom(Reg sreg) { template_fcom(0x02, sreg); }
  public final void fcom() { template_fcom(0x02); }
  public final void fcomps(Addr smem) { template_fcoms(0x03, smem); }
  public final void fcompl(Addr smem) { template_fcoml(0x03, smem); }
  public final void fcomp(Reg sreg) { template_fcom(0x03, sreg); }
  public final void fcomp() { template_fcom(0x03); }
  public final void fcompp() {
    opcode88(0xDE, 0xD9);
  }

  /* FCOS */
  public final void fcos() {
    opcodef8(0xFF);
  }

  /* FDECSTP */
  public final void fdecstp() {
    opcodef8(0xF6);
  }

  /* FDIV/FDIVP/FIDIV */
  public final void fdivs(Addr smem) { template_fadds(0x06, smem); }
  public final void fdivl(Addr smem) { template_faddl(0x06, smem); }
  public final void fdiv(Reg sreg, Reg treg) { template_fadd(0x06, sreg, treg); }
  public final void fdivp(Reg treg) { template_faddp(0x07, treg); }
  public final void fdivp() { template_faddp(0x07); }
  public final void fidivw(Addr smem) { template_fiaddw(0x06, smem); }
  public final void fidivl(Addr smem) { template_fiaddl(0x06, smem); }
  
  /* FDIVR/FDIVRP/FIDIVR */
  public final void fdivrs(Addr smem) { template_fadds(0x07, smem); }
  public final void fdivrl(Addr smem) { template_faddl(0x07, smem); }
  public final void fdivr(Reg sreg, Reg treg) { template_fadd(0x07, sreg, treg); }
  public final void fdivrp(Reg treg) { template_faddp(0x06, treg); }
  public final void fdivrp() { template_faddp(0x06); }
  public final void fidivrw(Addr smem) { template_fiaddw(0x07, smem); }
  public final void fidivrl(Addr smem) { template_fiaddl(0x07, smem); }

  /* FFREE */
  public void ffree(Reg sreg) {
    int sr = rf(sreg);
    opcode8(0xDD);
    modrm(3, 0, sr);
  }

  /* FICOM/FICOMP */
  public final void ficomw(Addr smem) { template_fiaddw(0x02, smem); }
  public final void ficoml(Addr smem) { template_fiaddl(0x02, smem); }
  public final void ficompw(Addr smem) { template_fiaddw(0x03, smem); }
  public final void ficompl(Addr smem) { template_fiaddl(0x03, smem); }

  /* FILD */
  public final void fildw(Addr smem) { template_fild(0x00, 0x05, smem); }
  public final void fildl(Addr smem) { template_fildl(0x00, 0x05, smem); }
  public final void fildll(Addr smem) { template_fildll(0x00, 0x05, smem); }

  /* FINCSTP */
  public final void fincstp() {
    opcodef8(0xF7);
  }

  /* FINIT/FNINIT */
  public final void finit() { template_fclex(0xE3); }
  public final void fninit() { template_fnclex(0xE3); }

  /* FIST/FISTP */
  public final void fistw(Addr tmem) {
    overrd(tmem);
    opcode8(0xDF);
    modrm(mod(tmem), 2, rm(tmem));
    sibdisp(tmem);
  }
  public final void fistl(Addr tmem) {
    overrd(tmem);
    opcode8(0xDB);
    modrm(mod(tmem), 2, rm(tmem));
    sibdisp(tmem);
  }
  public final void fistpw(Addr tmem) { template_fild(0x03, 0x07, tmem); }
  public final void fistpl(Addr tmem) { template_fildl(0x03, 0x07, tmem); }
  public final void fistpll(Addr tmem) { template_fildll(0x03, 0x07, tmem); }

  /* FLD */
  public final void flds(Addr smem) { template_flds(0x00, 0x05, smem); }
  public final void fldl(Addr smem) { template_fldl(0x00, 0x05, smem); }
  public final void fldt(Addr smem) { template_fldll(0x00, 0x05, smem); }
  public final void fld(Reg sreg) { template_fld(0x00, 0x05, sreg); }

  /* FLD1/FLDL2T/FLDL2E/FLDPI/FLDLG2/FLDLN2/FLDZ */
  public final void fld1() {
    opcodef8(0xE8);
  }
  public final void fldl2t() {
    opcodef8(0xE9);
  }
  public final void fldl2e() {
    opcodef8(0xEA);
  }
  public final void fldpi() {
    opcodef8(0xEB);
  }
  public final void fldlg2() {
    opcodef8(0xEC);
  }
  public final void fldln2() {
    opcodef8(0xED);
  }
  public final void fldz() {
    opcodef8(0xEE);
  }

  /* FLDCW */
  public final void fldcw(Addr smem) { template_fldcw(0x05, smem); }

  /* FLDENV */
  public final void fldenv(Addr smem) { template_fldenv(0xD9, 0x04, smem); }
  
  /* FMUL/FMULP/FIMUL */
  public final void fmuls(Addr smem) { template_fadds(0x01, smem); }
  public final void fmull(Addr smem) { template_faddl(0x01, smem); }
  public final void fmul(Reg sreg, Reg treg) { template_fadd(0x01, sreg, treg); }
  public final void fmulp(Reg treg) { template_faddp(0x01, treg); }
  public final void fmulp() { template_faddp(0x01); }
  public final void fimulw(Addr smem) { template_fiaddw(0x01, smem); }
  public final void fimull(Addr smem) { template_fiaddl(0x01, smem); }

  /* FNOP */
  public final void fnop() {
    opcodef8(0xD0);
  }

  /* FPATAN */
  public final void fpatan() {
    opcodef8(0xF3);
  }

  /* FPREM */
  public final void fprem() {
    opcodef8(0xF8);
  }

  /* FPREM1 */
  public final void fprem1() {
    opcodef8(0xF5);
  }

  /* FPTAN */
  public final void fptan() {
    opcodef8(0xF2);
  }

  /* FRNDINT */
  public final void frndint() {
    opcodef8(0xFC);
  }

  /* FRSTOR */
  public final void frstor(Addr smem) { template_fldenv(0xDD, 0x04, smem); }

  /* FSAVE/FNSAVE */
  public final void fsave(Addr smem) { template_fsave(0xDD, smem); }
  public final void fnsave(Addr smem) { template_fldenv(0xDD, 0x06, smem); }
  
  /* FSCALE */
  public final void fscale() {
    opcodef8(0xFD);
  }
  
  /* FSIN */
  public final void fsin() {
    opcodef8(0xFE);
  }

  /* FSINCOS */
  public final void fsincos() {
    opcodef8(0xFB);
  }

  /* FSQRT */
  public final void fsqrt() {
    opcodef8(0xFA);
  }

  /* FST/FSTP */
  public final void fsts(Addr smem) {
    overrd(smem);
    opcode8(0xD9);
    modrm(mod(smem), 2, rm(smem));
    sibdisp(smem);
  }
  public final void fstl(Addr smem) {
    overrd(smem);
    opcode8(0xDD);
    modrm(mod(smem), 2, rm(smem));
    sibdisp(smem);
  }
  public final void fst(Reg sreg) {
    int sr = rf(sreg);
    opcode8(0xDD);
    modrm(3, 2, sr);
  }
  public final void fstps(Addr smem) { template_flds(0x03, 0x07, smem); }
  public final void fstpl(Addr smem) { template_fldl(0x03, 0x07, smem); }
  public final void fstpt(Addr smem) { template_fldll(0x03, 0x07, smem); }
  public final void fstp(Reg sreg) {
    int sr = rf(sreg);
    opcode8(0xDD);
    modrm(3, 3, sr);
  }

  /* FSTCW/FNSTCW */
  public final void fstcw(Addr smem) {
    overrd(smem);
    opcode8(0x9B);
    opcode8(0xD9);
    modrm(mod(smem), 7, rm(smem));
    sibdisp(smem);
  }
  public final void fnstcw(Addr smem) { template_fldcw(0x07, smem); }

  /* FSTENV/FNSTENV */
  public final void fstenv(Addr smem) { template_fsave(0xD9, smem); }
  public final void fnstenv(Addr smem) { template_fldenv(0xD9, 0x06, smem); }

  /* FSTSW/FNSTSW */
  public final void fstsw(Addr tmem) {
    overrd(tmem);
    opcode8(0x9B);
    opcode8(0xDD);
    modrm(mod(tmem), 7, rm(tmem));
    sibdisp(tmem);
  }
  public final void fstsw() {
    opcode8(0x9B);
    opcode88(0xDF, 0xE0);
  }
  public final void fnstsw(Addr tmem) {
    overrd(tmem);
    opcode8(0xDD);
    modrm(mod(tmem), 7, rm(tmem));
    sibdisp(tmem);
  }
  public final void fnstsw() {
    opcode88(0xDF, 0xE0);
  }

  /* FSUB/FSUBP/FISUB */
  public final void fsubs(Addr smem) { template_fadds(0x04, smem); }
  public final void fsubl(Addr smem) { template_faddl(0x04, smem); }
  public final void fsub(Reg sreg, Reg treg) { template_fadd(0x04, sreg, treg); }
  public final void fsubp(Reg treg) { template_faddp(0x05, treg); }
  public final void fsubp() { template_faddp(0x05); }
  public final void fisubw(Addr smem) { template_fiaddw(0x04, smem); }
  public final void fisubl(Addr smem) { template_fiaddl(0x04, smem); }

  /* FSUBR/FSUBRP/FISUBR */
  public final void fsubrs(Addr smem) { template_fadds(0x05, smem); }
  public final void fsubrl(Addr smem) { template_faddl(0x05, smem); }
  public final void fsubr(Reg sreg, Reg treg) { template_fadd(0x05, sreg, treg); }
  public final void fsubrp(Reg treg) { template_faddp(0x04, treg); }
  public final void fsubrp() { template_faddp(0x04); }
  public final void fisubrw(Addr smem) { template_fiaddw(0x05, smem); }
  public final void fisubrl(Addr smem) { template_fiaddl(0x05, smem); }

  /* FTST */
  public final void ftst() {
    opcodef8(0xE4);
  }

  /* FUCOM/FUCOMP/FUCOMPP */
  public final void fucom(Reg sreg) { template_fucom(0xDD, 0x04, sreg); }
  public final void fucom() { template_fucom(0xDD, 0x04); }
  public final void fucomp(Reg sreg) { template_fucom(0xDD, 0x05, sreg); }
  public final void fucomp() { template_fucom(0xDD, 0x05); }
  public final void fucompp() {
    opcode88(0xDA, 0xE9);
  }

  /* FXAM */
  public final void fxam() {
    opcodef8(0xE5);
  }

  /* FXCH */
  public final void fxch(Reg sreg) { template_fucom(0xD9, 0x01, sreg); }
  public final void fxch() { template_fucom(0xD9, 0x01); }

  /* FXTRACT */
  public final void fxtract() {
    opcodef8(0xF4);
  }

  /* FYL2X */
  public final void fyl2x() {
    opcodef8(0xF1);
  }

  /* FYL2XP1 */
  public final void fyl2xp1() {
    opcodef8(0xF9);
  }

  /* HLT */
  public final void hlt() {
    opcode8(0xF4);
  }

  /* IDIV */
  public final void idivb(Reg treg) { template_divb(0x07, treg); }
  public final void idivw(Reg treg) { template_divw(0x07, treg); }
  public final void idivl(Reg treg) { template_divl(0x07, treg); }
  public final void idivb(Addr tmem) { template_divb(0x07, tmem); }
  public final void idivw(Addr tmem) { template_divw(0x07, tmem); }
  public final void idivl(Addr tmem) { template_divl(0x07, tmem); }

  /* IMUL */
  public final void imulb(Reg sreg) {
    int sr = r8(sreg);
    opcode8(0xF6);
    modrm(3, 5, sr);
  }
  public final void imulw(Reg sreg) {
    int sr = r16(sreg);
    opcode16(0xF6);
    modrm(3, 5, sr);
  }
  public final void imull(Reg sreg) {
    int sr = r32(sreg);
    opcode32(0xF6);
    modrm(3, 5, sr);
  }
  public final void imulb(Addr smem) {
    overrd(smem);
    opcode8(0xF6);
    modrm(mod(smem), 5, rm(smem));
    sibdisp(smem);
  }
  public final void imulw(Addr smem) {
    overrd(smem);
    opcode16(0xF6);
    modrm(mod(smem), 5, rm(smem));
    sibdisp(smem);
  }
  public final void imull(Addr smem) {
    overrd(smem);
    opcode32(0xF6);
    modrm(mod(smem), 5, rm(smem));
    sibdisp(smem);
  }
  public final void imulw(Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    op16();
    opcodex8(0xAF);
    modrm(3, tr, sr);
  }
  public final void imull(Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    op32();
    opcodex8(0xAF);
    modrm(3, tr, sr);
  }
  public final void imulw(Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    op16();
    opcodex8(0xAF);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  public final void imull(Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    op32();
    opcodex8(0xAF);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  public final void imulw(int simm, Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    boolean iss = is8(simm);
    op16();
    opcode8(iss?0x6B:0x69);
    modrm(3, tr, sr);
    if (iss) imm8(simm); else imm16(simm);
  }
  public final void imull(int simm, Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    boolean iss = is8(simm);
    op32();
    opcode8(iss?0x6B:0x69);
    modrm(3, tr, sr);
    if (iss) imm8(simm); else imm32(simm);
  }
  public final void imulw(int simm, Addr smem, Reg treg) {
    int tr = r16(treg);
    boolean iss = is8(simm);
    overrd(smem);
    op16();
    opcode8(iss?0x6B:0x69);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
    if (iss) imm8(simm); else imm16(simm);
  }
  public final void imull(int simm, Addr smem, Reg treg) {
    int tr = r32(treg);
    boolean iss = is8(simm);
    overrd(smem);
    op32();
    opcode8(iss?0x6B:0x69);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
    if (iss) imm8(simm); else imm32(simm);
  }
  public final void imulw(int simm, Reg treg) {
    int tr = r16(treg);
    boolean iss = is8(simm);
    op16();
    opcode8(iss?0x6B:0x69);
    modrm(3, tr, tr);
    if (iss) imm8(simm); else imm16(simm);
  }
  public final void imull(int simm, Reg treg) {
    int tr = r32(treg);
    boolean iss = is8(simm);
    op32();
    opcode8(iss?0x6B:0x69);
    modrm(3, tr, tr);
    if (iss) imm8(simm); else imm32(simm);
  }
  public final void imulw(Imm simm, Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    op16();
    opcode8(0x69);
    modrm(3, tr, sr);
    imm16(simm);
  }
  public final void imull(Imm simm, Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    op32();
    opcode8(0x69);
    modrm(3, tr, sr);
    imm32(simm);
  }
  public final void imulw(Imm simm, Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    op16();
    opcode8(0x69);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
    imm16(simm);
  }
  public final void imull(Imm simm, Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    op32();
    opcode8(0x69);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
    imm32(simm);
  }
  public final void imulw(Imm simm, Reg treg) {
    int tr = r16(treg);
    op16();
    opcode8(0x69);
    modrm(3, tr, tr);
    imm16(simm);
  }
  public final void imull(Imm simm, Reg treg) {
    int tr = r32(treg);
    op32();
    opcode8(0x69);
    modrm(3, tr, tr);
    imm32(simm);
  }
  
  /* IN */
  public final void inb() {
    opcode8(0xEC);
  }
  public final void inw() {
    opcode16(0xEC);
  }
  public final void inl() {
    opcode32(0xEC);
  }
  public final void inb(int simm) {
    opcode8(0xE4);
    imm8(simm);
  }
  public final void inw(int simm) {
    opcode16(0xE4);
    imm8(simm);
  }
  public final void inl(int simm) {
    opcode32(0xE4);
    imm8(simm);
  }

  /* INC */
  public final void incb(Reg treg) { template_decb(0x40, 0x00, treg); }
  public final void incw(Reg treg) { template_decw(0x40, 0x00, treg); }
  public final void incl(Reg treg) { template_decl(0x40, 0x00, treg); }
  public final void incb(Addr tmem) { template_decb(0x40, 0x00, tmem); }
  public final void incw(Addr tmem) { template_decw(0x40, 0x00, tmem); }
  public final void incl(Addr tmem) { template_decl(0x40, 0x00, tmem); }

  /* INS/INSB/INSW/INSD */
  public final void insb() {
    opcode8(0x6C);
  }
  public final void insw() {
    opcode16(0x6C);
  }
  public final void insd() {
    opcode32(0x6C);
  }

  /* INT/INTO */
  public final void inti(int simm) {
    if (simm == 3)
      opcode8(0xCC);
    else {
      opcode8(0xCD);
      imm8(simm);
    }
  }
  public final void into() {
    opcode8(0xCE);
  }

  /* IRET/IRETD */
  public final void iret() { template_cbw(0xCF); }
  public final void iretd() { template_cwde(0xCF); }

  /* Jcc */
  public final void ja(Imm simm) { template_ja(0x07, simm); }
  public final void jae(Imm simm) { template_ja(0x03, simm); }
  public final void jb(Imm simm) { template_ja(0x02, simm); }
  public final void jbe(Imm simm) { template_ja(0x06, simm); }
  public final void jc(Imm simm) { template_ja(0x02, simm); }
  public final void jcxe(Imm simm) { throw new Error("Unimplemented"); }
  public final void jcxz(Imm simm) { throw new Error("Unimplemented"); }
  public final void jecxe(Imm simm) { throw new Error("Unimplemented"); }
  public final void jecxz(Imm simm) { throw new Error("Unimplemented"); }
  public final void je(Imm simm) { template_ja(0x04, simm); }
  public final void jg(Imm simm) { template_ja(0x0F, simm); }
  public final void jge(Imm simm) { template_ja(0x0D, simm); }
  public final void jl(Imm simm) { template_ja(0x0C, simm); }
  public final void jle(Imm simm) { template_ja(0x0E, simm); }
  public final void jna(Imm simm) { template_ja(0x06, simm); }
  public final void jnae(Imm simm) { template_ja(0x02, simm); }
  public final void jnb(Imm simm) { template_ja(0x03, simm); }
  public final void jnbe(Imm simm) { template_ja(0x07, simm); }
  public final void jnc(Imm simm) { template_ja(0x03, simm); }
  public final void jne(Imm simm) { template_ja(0x05, simm); }
  public final void jng(Imm simm) { template_ja(0x0E, simm); }
  public final void jnge(Imm simm) { template_ja(0x0C, simm); }
  public final void jnl(Imm simm) { template_ja(0x0D, simm); }
  public final void jnle(Imm simm) { template_ja(0x0F, simm); }
  public final void jno(Imm simm) { template_ja(0x01, simm); }
  public final void jnp(Imm simm) { template_ja(0x0B, simm); }
  public final void jns(Imm simm) { template_ja(0x09, simm); }
  public final void jnz(Imm simm) { template_ja(0x05, simm); }
  public final void jo(Imm simm) { template_ja(0x00, simm); }
  public final void jp(Imm simm) { template_ja(0x0A, simm); }
  public final void jpe(Imm simm) { template_ja(0x0A, simm); }
  public final void jpo(Imm simm) { template_ja(0x0B, simm); }
  public final void js(Imm simm) { template_ja(0x08, simm); }
  public final void jz(Imm simm) { template_ja(0x04, simm); }

  /* JMP */
  public final void jmp(Imm simm) {
    opcode8(0xE9);
    rel32(simm);
  }
  public final void jmpw(Reg sreg) {
    int sr = r16(sreg);
    op16();
    opcode8(0xFF);
    modrm(3, 4, sr);
  }
  public final void jmpl(Reg sreg) {
    int sr = r32(sreg);
    op32();
    opcode8(0xFF);
    modrm(3, 4, sr);
  }
  public final void jmpw(Addr smem) {
    overrd(smem);
    op16();
    opcode8(0xFF);
    modrm(mod(smem), 4, rm(smem));
    sibdisp(smem);
  }
  public final void jmpl(Addr smem) {
    overrd(smem);
    op32();
    opcode8(0xFF);
    modrm(mod(smem), 4, rm(smem));
    sibdisp(smem);
  }
  public final void jmpf(Imm simm) { throw new Error("Unimplemented"); }
  public final void jmpfw(Addr smem) {
    overrd(smem);
    op16();
    opcode8(0xFF);
    modrm(mod(smem), 5, rm(smem));
    sibdisp(smem);
  }
  public final void jmpfl(Addr smem) {
    overrd(smem);
    op32();
    opcode8(0xFF);
    modrm(mod(smem), 5, rm(smem));
    sibdisp(smem);
  }

  /* LAHF */
  public final void lahf() {
    opcode8(0x9F);
  }

  /* LAR */
  public final void larw(Reg sreg, Reg treg) { template_larw(0x02, sreg, treg); }
  public final void larl(Reg sreg, Reg treg) { template_larl(0x02, sreg, treg); }
  public final void larw(Addr smem, Reg treg) { template_larw(0x02, smem, treg); }
  public final void larl(Addr smem, Reg treg) { template_larl(0x02, smem, treg); }

  /* LDS/LES/LFS/LGS/LSS */
  public final void ldsw(Addr smem, Reg treg) { template_ldsw(0xC5, smem, treg); }
  public final void ldsl(Addr smem, Reg treg) { template_ldsl(0xC5, smem, treg); }
  public final void lssw(Addr smem, Reg treg) { template_lfsw(0xB2, smem, treg); }
  public final void lssl(Addr smem, Reg treg) { template_lfsl(0xB2, smem, treg); }
  public final void lesw(Addr smem, Reg treg) { template_ldsw(0xCE, smem, treg); }
  public final void lesl(Addr smem, Reg treg) { template_ldsl(0xCE, smem, treg); }
  public final void lfsw(Addr smem, Reg treg) { template_lfsw(0xB4, smem, treg); }
  public final void lfsl(Addr smem, Reg treg) { template_lfsl(0xB4, smem, treg); }
  public final void lgsw(Addr smem, Reg treg) { template_lfsw(0xB5, smem, treg); }
  public final void lgsl(Addr smem, Reg treg) { template_lfsl(0xB5, smem, treg); }

  /* LEA */
  public final void leaw(Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    op16();
    opcode8(0x8D);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  public final void leal(Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    op32();
    opcode8(0x8D);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }

  /* LEAVE */
  public final void leave() {
    opcode8(0xC9);
  }

  /* LGDT/LIDT */
  public final void lgdt(Addr smem) { template_lgdt(0x02, smem); }
  public final void lidt(Addr smem) { template_lgdt(0x03, smem); }

  /* LLDT */
  public final void lldt(Reg sreg) { template_lldt(0x00, 0x02, sreg); }
  public final void lldt(Addr smem) { template_lldt(0x00, 0x02, smem); }

  /* LMSW */
  public final void lmsw(Reg sreg) { template_lldt(0x01, 0x06, sreg); }
  public final void lmsw(Addr smem) { template_lldt(0x01, 0x06, smem); }

  /* LOCK */
  public final void lock() {
    prefix(0xF0);
  }

  /* LODSB/LODSW/LODSD */
  public final void lodsb() {
    opcode8(0xAC);
  }
  public final void lodsw() {
    opcode16(0xAC);
  }
  public final void lodsd() {
    opcode32(0xAC);
  }

  /* LOOP/LOOPcc */
  public final void loop(Imm simm) { throw new Error("Unimplemented"); }
  public final void loope(Imm simm) { throw new Error("Unimplemented"); }
  public final void loopz(Imm simm) { throw new Error("Unimplemented"); }
  public final void loopne(Imm simm) { throw new Error("Unimplemented"); }
  public final void loopnz(Imm simm) { throw new Error("Unimplemented"); }

  /* LSL */
  public final void lslw(Reg sreg, Reg treg) { template_larw(0x03, sreg, treg); }
  public final void lsll(Reg sreg, Reg treg) { template_larl(0x03, sreg, treg); }
  public final void lslw(Addr smem, Reg treg) { template_larw(0x03, smem, treg); }
  public final void lsll(Addr smem, Reg treg) { template_larl(0x03, smem, treg); }

  /* LTR */
  public final void ltr(Reg treg) { template_lldt(0x00, 0x03, treg); }
  public final void ltr(Addr tmem) { template_lldt(0x00, 0x03, tmem); }

  /* MOV */
  public final void movb(Reg sreg, Reg treg) {
    int sr = r8(sreg);
    int tr = r8(treg);
    opcode8(0x88);
    modrm(3, sr, tr);
  }
  public final void movw(Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    opcode16(0x88);
    modrm(3, sr, tr);
  }
  public final void movl(Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    opcode32(0x88);
    modrm(3, sr, tr);
  }
  public final void movb(Reg sreg, Addr tmem) {
    int sr = r8(sreg);
    overrd(tmem);
    if (sr == 0 && ismd(tmem))
      opcode8(0xA2);
    else {
      opcode8(0x88);
      modrm(mod(tmem), sr, rm(tmem));
    }
    sibdisp(tmem);
  }
  public final void movw(Reg sreg, Addr tmem) {
    int sr = r16(sreg);
    overrd(tmem);
    if (sr == 0 && ismd(tmem))
      opcode16(0xA2);
    else {
      opcode16(0x88);
      modrm(mod(tmem), sr, rm(tmem));
    }
    sibdisp(tmem);
  }
  public final void movl(Reg sreg, Addr tmem) {
    int sr = r32(sreg);
    overrd(tmem);
    if (sr == 0 && ismd(tmem))
      opcode32(0xA2);
    else {
      opcode32(0x88);
      modrm(mod(tmem), sr, rm(tmem));
    }
    sibdisp(tmem);
  }
  public final void movb(Addr smem, Reg treg) {
    int tr = r8(treg);
    overrd(smem);
    if (tr == 0 && ismd(smem))
      opcode8(0xA0);
    else {
      opcode8(0x8A);
      modrm(mod(smem), tr, rm(smem));
    }
    sibdisp(smem);
  }
  public final void movw(Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    if (tr == 0 && ismd(smem))
      opcode16(0xA0);
    else {
      opcode16(0x8A);
      modrm(mod(smem), tr, rm(smem));
    }
    sibdisp(smem);
  }
  public final void movl(Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    if (tr == 0 && ismd(smem))
      opcode32(0xA0);
    else {
      opcode32(0x8A);
      modrm(mod(smem), tr, rm(smem));
    }
    sibdisp(smem);
  }
  public final void movws(Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = rs(treg);
    opcode8(0x8E);
    modrm(3, tr, sr);
  }
  public final void movsw(Reg sreg, Reg treg) {
    int sr = rs(sreg);
    int tr = r16(treg);
    opcode8(0x8C);
    modrm(3, sr, tr);
  }
  public final void movws(Addr smem, Reg treg) {
    int tr = rs(treg);
    overrd(smem);
    opcode8(0x8E);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  public final void movsw(Reg sreg, Addr tmem) {
    int sr = rs(sreg);
    overrd(tmem);
    opcode8(0x8C);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  public final void movb(int simm, Reg treg) {
    int tr = r8(treg);
    opcode8(0xB0|tr);
    imm8(simm);
  }
  public final void movw(int simm, Reg treg) {
    int tr = r16(treg);
    op16();
    opcode8(0xB8|tr);
    imm16(simm);
  }
  public final void movl(int simm, Reg treg) {
    int tr = r32(treg);
    op32();
    opcode8(0xB8|tr);
    imm32(simm);
  }
  public final void movb(int simm, Addr tmem) {
    overrd(tmem);
    opcode8(0xC6);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
    imm8(simm);
  }
  public final void movw(int simm, Addr tmem) {
    overrd(tmem);
    opcode16(0xC6);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
    imm16(simm);
  }
  public final void movl(int simm, Addr tmem) {
    overrd(tmem);
    opcode32(0xC6);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
    imm32(simm);
  }
  public final void movw(Imm simm, Reg treg) {
    int tr = r16(treg);
    op16();
    opcode8(0xB8|tr);
    imm16(simm);
  }
  public final void movl(Imm simm, Reg treg) {
    int tr = r32(treg);
    op32();
    opcode8(0xB8|tr);
    imm32(simm);
  }
  public final void movw(Imm simm, Addr tmem) {
    overrd(tmem);
    opcode16(0xC6);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
    imm16(simm);
  }
  public final void movl(Imm simm, Addr tmem) {
    overrd(tmem);
    opcode32(0xC6);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
    imm32(simm);
  }

  /* MOVc */
  public final void movlc(Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = rc(treg);
    opcodex8(0x22);
    modrm(3, tr, sr);
  }
  public final void movcl(Reg sreg, Reg treg) {
    int sr = rc(sreg);
    int tr = r32(treg);
    opcodex8(0x20);
    modrm(3, sr, tr);
  }

  /* MOVg */
  public final void movlg(Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = rd(treg);
    opcodex8(0x23);
    modrm(3, tr, sr);
  }
  public final void movgl(Reg sreg, Reg treg) {
    int sr = rd(sreg);
    int tr = r32(treg);
    opcodex8(0x21);
    modrm(3, sr, tr);
  }

  /* MOVt */
  public final void movlt(Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = rt(treg);
    opcodex8(0x26);
    modrm(3, tr, sr);
  }
  public final void movtl(Reg sreg, Reg treg) {
    int sr = rt(sreg);
    int tr = r32(treg);
    opcodex8(0x24);
    modrm(3, sr, tr);
  }

  /* MOVSB/MOVSW/MOVSD */
  public final void movsb() {
    opcode8(0xA4);
  }
  public final void movsw() {
    opcode16(0xA4);
  }
  public final void movsd() {
    opcode32(0xA4);
  }

  /* MOVSX */
  public final void movsxbw(Reg sreg, Reg treg) { template_movsbw(0xBE, sreg, treg); }
  public final void movsxbl(Reg sreg, Reg treg) { template_movsbl(0xBE, sreg, treg); }
  public final void movsxwl(Reg sreg, Reg treg) { template_movswl(0xBE, sreg, treg); }
  public final void movsxbw(Addr smem, Reg treg) { template_movsbw(0xBE, smem, treg); }
  public final void movsxbl(Addr smem, Reg treg) { template_movsbl(0xBE, smem, treg); }
  public final void movsxwl(Addr smem, Reg treg) { template_movswl(0xBE, smem, treg); }

  /* MOVZX */
  public final void movzxbw(Reg sreg, Reg treg) { template_movsbw(0xB6, sreg, treg); }
  public final void movzxbl(Reg sreg, Reg treg) { template_movsbl(0xB6, sreg, treg); }
  public final void movzxwl(Reg sreg, Reg treg) { template_movswl(0xB6, sreg, treg); }
  public final void movzxbw(Addr smem, Reg treg) { template_movsbw(0xB6, smem, treg); }
  public final void movzxbl(Addr smem, Reg treg) { template_movsbl(0xB6, smem, treg); }
  public final void movzxwl(Addr smem, Reg treg) { template_movswl(0xB6, smem, treg); }

  /* MUL */
  public final void mulb(Reg treg) { template_divb(0x04, treg); }
  public final void mulw(Reg treg) { template_divw(0x04, treg); }
  public final void mull(Reg treg) { template_divl(0x04, treg); }
  public final void mulb(Addr tmem) { template_divb(0x04, tmem); }
  public final void mulw(Addr tmem) { template_divw(0x04, tmem); }
  public final void mull(Addr tmem) { template_divl(0x04, tmem); }

  /* NEG */
  public final void negb(Reg treg) { template_divb(0x03, treg); }
  public final void negw(Reg treg) { template_divw(0x03, treg); }
  public final void negl(Reg treg) { template_divl(0x03, treg); }
  public final void negb(Addr tmem) { template_divb(0x03, tmem); }
  public final void negw(Addr tmem) { template_divw(0x03, tmem); }
  public final void negl(Addr tmem) { template_divl(0x03, tmem); }

  /* NOP */
  public final void nop() {
    opcode8(0x90);
  }

  /* NOT */
  public final void notb(Reg treg) { template_divb(0x02, treg); }
  public final void notw(Reg treg) { template_divw(0x02, treg); }
  public final void notl(Reg treg) { template_divl(0x02, treg); }
  public final void notb(Addr tmem) { template_divb(0x02, tmem); }
  public final void notw(Addr tmem) { template_divw(0x02, tmem); }
  public final void notl(Addr tmem) { template_divl(0x02, tmem); }

  /* OR */
  public final void orb(Reg sreg, Reg treg) { template_adcb(0x08, 0x01, sreg, treg); }
  public final void orw(Reg sreg, Reg treg) { template_adcw(0x08, 0x01, sreg, treg); }
  public final void orl(Reg sreg, Reg treg) { template_adcl(0x08, 0x01, sreg, treg); }
  public final void orb(Reg sreg, Addr tmem) { template_adcb(0x08, 0x01, sreg, tmem); }
  public final void orw(Reg sreg, Addr tmem) { template_adcw(0x08, 0x01, sreg, tmem); }
  public final void orl(Reg sreg, Addr tmem) { template_adcl(0x08, 0x01, sreg, tmem); }
  public final void orb(Addr smem, Reg treg) { template_adcb(0x08, 0x01, smem, treg); }
  public final void orw(Addr smem, Reg treg) { template_adcw(0x08, 0x01, smem, treg); }
  public final void orl(Addr smem, Reg treg) { template_adcl(0x08, 0x01, smem, treg); }
  public final void orb(int simm, Reg treg) { template_adcb(0x08, 0x01, simm, treg); }
  public final void orw(int simm, Reg treg) { template_adcw(0x08, 0x01, simm, treg); }
  public final void orl(int simm, Reg treg) { template_adcl(0x08, 0x01, simm, treg); }
  public final void orb(int simm, Addr tmem) { template_adcb(0x08, 0x01, simm, tmem); }
  public final void orw(int simm, Addr tmem) { template_adcw(0x08, 0x01, simm, tmem); }
  public final void orl(int simm, Addr tmem) { template_adcl(0x08, 0x01, simm, tmem); }
  public final void orw(Imm simm, Reg treg) { template_adcw(0x08, 0x01, simm, treg); }
  public final void orl(Imm simm, Reg treg) { template_adcl(0x08, 0x01, simm, treg); }
  public final void orw(Imm simm, Addr tmem) { template_adcw(0x08, 0x01, simm, tmem); }
  public final void orl(Imm simm, Addr tmem) { template_adcl(0x08, 0x01, simm, tmem); }

  /* OUT */
  public final void outb() {
    opcode8(0xEE);
  }
  public final void outw() {
    opcode16(0xEE);
  }
  public final void outl() {
    opcode32(0xEE);
  }
  public final void outb(int simm) {
    opcode8(0xE6);
    imm8(simm);
  }
  public final void outw(int simm) {
    opcode16(0xE6);
    imm8(simm);
  }
  public final void outl(int simm) {
    opcode32(0xE6);
    imm8(simm);
  }

  /* OUTSB/OUTSW/OUTSD */
  public final void outsb() {
    opcode8(0x6E);
  }
  public final void outsw() {
    opcode16(0x6E);
  }
  public final void outsd() {
    opcode32(0x6E);
  }

  /* POP */
  public final void popw(Reg treg) {
    int tr = r16(treg);
    op16();
    opcode8(0x58|tr);
  }
  public final void popl(Reg treg) {
    int tr = r32(treg);
    op32();
    opcode8(0x58|tr);
  }
  public final void popw(Addr tmem) {
    overrd(tmem);
    op16();
    opcode8(0x8F);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
  }
  public final void popl(Addr tmem) {
    overrd(tmem);
    op32();
    opcode8(0x8F);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
  }
  public final void pops(Reg treg) {
    int tr = rs(treg);
    switch (tr) {
    case 0: opcode8(0x07); break;
    case 2: opcode8(0x17); break;
    case 3: opcode8(0x1F); break;
    case 4: opcodex8(0xA1); break;
    case 5: opcodex8(0xA9); break;
    default: throw new IllegalArgumentException();
    }
  }

  /* POPA/POPAD */
  public final void popa() { template_cbw(0x61); }
  public final void popad() { template_cwde(0x61); }

  /* POPF/POPFD */
  public final void popf() { template_cbw(0x9D); }
  public final void popfd() { template_cwde(0x9D); }

  /* PUSH */
  public final void pushw(Reg sreg) {
    int sr = r16(sreg);
    op16();
    opcode8(0x50|sr);
  }
  public final void pushl(Reg sreg) {
    int sr = r32(sreg);
    op32();
    opcode8(0x50|sr);
  }
  public final void pushw(Addr smem) {
    overrd(smem);
    op16();
    opcode8(0xFF);
    modrm(mod(smem), 6, rm(smem));
    sibdisp(smem);
  }
  public final void pushl(Addr smem) {
    overrd(smem);
    op32();
    opcode8(0xFF);
    modrm(mod(smem), 6, rm(smem));
    sibdisp(smem);
  }
  public final void pushs(Reg sreg) {
    int sr = rs(sreg);
    switch (sr) {
    case 0: opcode8(0x06); break;
    case 1: opcode8(0x0E); break;
    case 2: opcode8(0x16); break;
    case 3: opcode8(0x1E); break;
    case 4: opcodex8(0xA0); break;
    case 5: opcodex8(0xA8); break;
    default: throw new IllegalArgumentException();
    }
  }
  public final void pushw(int simm) {
    op16();
    if (is8(simm)) {
      opcode8(0x6A);
      imm8(simm);
    } else {
      opcode8(0x68);
      imm16(simm);
    }
  }
  public final void pushl(int simm) {
    op32();
    if (is8(simm)) {
      opcode8(0x6A);
      imm8(simm);
    } else {
      opcode8(0x68);
      imm32(simm);
    }
  }
  public final void pushw(Imm simm) {
    op16();
    opcode8(0x68);
    imm16(simm);
  }
  public final void pushl(Imm simm) {
    op32();
    opcode8(0x68);
    imm32(simm);
  }

  /* PUSHA/PUSHAD */
  public final void pusha() { template_cbw(0x60); }
  public final void pushad() { template_cwde(0x60); }

  /* PUSHF/PUSHFD */
  public final void pushf() { template_cbw(0x9C); }
  public final void pushfd() { template_cwde(0x9C); }

  /* RCL/RCR/ROL/ROR */
  public final void rclb(Reg treg) { template_rclb(0x02, treg); }
  public final void rclw(Reg treg) { template_rclw(0x02, treg); }
  public final void rcll(Reg treg) { template_rcll(0x02, treg); }
  public final void rclb(Addr tmem) { template_rclb(0x02, tmem); }
  public final void rclw(Addr tmem) { template_rclw(0x02, tmem); }
  public final void rcll(Addr tmem) { template_rcll(0x02, tmem); }
  public final void rclb(int simm, Reg treg) { template_rclb(0x02, simm, treg); }
  public final void rclw(int simm, Reg treg) { template_rclw(0x02, simm, treg); }
  public final void rcll(int simm, Reg treg) { template_rcll(0x02, simm, treg); }
  public final void rclb(int simm, Addr tmem) { template_rclb(0x02, simm, tmem); }
  public final void rclw(int simm, Addr tmem) { template_rclw(0x02, simm, tmem); }
  public final void rcll(int simm, Addr tmem) { template_rcll(0x02, simm, tmem); }
  public final void rcrb(Reg treg) { template_rclb(0x03, treg); }
  public final void rcrw(Reg treg) { template_rclw(0x03, treg); }
  public final void rcrl(Reg treg) { template_rcll(0x03, treg); }
  public final void rcrb(Addr tmem) { template_rclb(0x03, tmem); }
  public final void rcrw(Addr tmem) { template_rclw(0x03, tmem); }
  public final void rcrl(Addr tmem) { template_rcll(0x03, tmem); }
  public final void rcrb(int simm, Reg treg) { template_rclb(0x03, simm, treg); }
  public final void rcrw(int simm, Reg treg) { template_rclw(0x03, simm, treg); }
  public final void rcrl(int simm, Reg treg) { template_rcll(0x03, simm, treg); }
  public final void rcrb(int simm, Addr tmem) { template_rclb(0x03, simm, tmem); }
  public final void rcrw(int simm, Addr tmem) { template_rclw(0x03, simm, tmem); }
  public final void rcrl(int simm, Addr tmem) { template_rcll(0x03, simm, tmem); }
  public final void rolb(Reg treg) { template_rclb(0x00, treg); }
  public final void rolw(Reg treg) { template_rclw(0x00, treg); }
  public final void roll(Reg treg) { template_rcll(0x00, treg); }
  public final void rolb(Addr tmem) { template_rclb(0x00, tmem); }
  public final void rolw(Addr tmem) { template_rclw(0x00, tmem); }
  public final void roll(Addr tmem) { template_rcll(0x00, tmem); }
  public final void rolb(int simm, Reg treg) { template_rclb(0x00, simm, treg); }
  public final void rolw(int simm, Reg treg) { template_rclw(0x00, simm, treg); }
  public final void roll(int simm, Reg treg) { template_rcll(0x00, simm, treg); }
  public final void rolb(int simm, Addr tmem) { template_rclb(0x00, simm, tmem); }
  public final void rolw(int simm, Addr tmem) { template_rclw(0x00, simm, tmem); }
  public final void roll(int simm, Addr tmem) { template_rcll(0x00, simm, tmem); }
  public final void rorb(Reg treg) { template_rclb(0x01, treg); }
  public final void rorw(Reg treg) { template_rclw(0x01, treg); }
  public final void rorl(Reg treg) { template_rcll(0x01, treg); }
  public final void rorb(Addr tmem) { template_rclb(0x01, tmem); }
  public final void rorw(Addr tmem) { template_rclw(0x01, tmem); }
  public final void rorl(Addr tmem) { template_rcll(0x01, tmem); }
  public final void rorb(int simm, Reg treg) { template_rclb(0x01, simm, treg); }
  public final void rorw(int simm, Reg treg) { template_rclw(0x01, simm, treg); }
  public final void rorl(int simm, Reg treg) { template_rcll(0x01, simm, treg); }
  public final void rorb(int simm, Addr tmem) { template_rclb(0x01, simm, tmem); }
  public final void rorw(int simm, Addr tmem) { template_rclw(0x01, simm, tmem); }
  public final void rorl(int simm, Addr tmem) { template_rcll(0x01, simm, tmem); }

  /* REP/REPE/REPZ/REPNE/REPNZ */
  public final void rep() {
    prefix(0xF3);
  }
  public final void repe() {
    prefix(0xF3);
  }
  public final void repne() {
    prefix(0xF2);
  }
  public final void repz() {
    prefix(0xF3);
  }
  public final void repnz() {
    prefix(0xF2);
  }

  /* RET */
  public final void ret() { template_ret(0xC2); }
  public final void ret(int simm) { template_ret(0xC2, simm); }
  public final void retf() { template_ret(0xCA); }
  public final void retf(int simm) { template_ret(0xCA, simm); }

  /* SAHF */
  public final void sahf() {
    opcode8(0x9E);
  }

  /* SAL/SAR/SHL/SHR */
  public final void salb(Reg treg) { template_rclb(0x04, treg); }
  public final void salw(Reg treg) { template_rclw(0x04, treg); }
  public final void sall(Reg treg) { template_rcll(0x04, treg); }
  public final void salb(Addr tmem) { template_rclb(0x04, tmem); }
  public final void salw(Addr tmem) { template_rclw(0x04, tmem); }
  public final void sall(Addr tmem) { template_rcll(0x04, tmem); }
  public final void salb(int simm, Reg treg) { template_rclb(0x04, simm, treg); }
  public final void salw(int simm, Reg treg) { template_rclw(0x04, simm, treg); }
  public final void sall(int simm, Reg treg) { template_rcll(0x04, simm, treg); }
  public final void salb(int simm, Addr tmem) { template_rclb(0x04, simm, tmem); }
  public final void salw(int simm, Addr tmem) { template_rclw(0x04, simm, tmem); }
  public final void sall(int simm, Addr tmem) { template_rcll(0x04, simm, tmem); }
  public final void sarb(Reg treg) { template_rclb(0x07, treg); }
  public final void sarw(Reg treg) { template_rclw(0x07, treg); }
  public final void sarl(Reg treg) { template_rcll(0x07, treg); }
  public final void sarb(Addr tmem) { template_rclb(0x07, tmem); }
  public final void sarw(Addr tmem) { template_rclw(0x07, tmem); }
  public final void sarl(Addr tmem) { template_rcll(0x07, tmem); }
  public final void sarb(int simm, Reg treg) { template_rclb(0x07, simm, treg); }
  public final void sarw(int simm, Reg treg) { template_rclw(0x07, simm, treg); }
  public final void sarl(int simm, Reg treg) { template_rcll(0x07, simm, treg); }
  public final void sarb(int simm, Addr tmem) { template_rclb(0x07, simm, tmem); }
  public final void sarw(int simm, Addr tmem) { template_rclw(0x07, simm, tmem); }
  public final void sarl(int simm, Addr tmem) { template_rcll(0x07, simm, tmem); }
  public final void shlb(Reg treg) { template_rclb(0x04, treg); }
  public final void shlw(Reg treg) { template_rclw(0x04, treg); }
  public final void shll(Reg treg) { template_rcll(0x04, treg); }
  public final void shlb(Addr tmem) { template_rclb(0x04, tmem); }
  public final void shlw(Addr tmem) { template_rclw(0x04, tmem); }
  public final void shll(Addr tmem) { template_rcll(0x04, tmem); }
  public final void shlb(int simm, Reg treg) { template_rclb(0x04, simm, treg); }
  public final void shlw(int simm, Reg treg) { template_rclw(0x04, simm, treg); }
  public final void shll(int simm, Reg treg) { template_rcll(0x04, simm, treg); }
  public final void shlb(int simm, Addr tmem) { template_rclb(0x04, simm, tmem); }
  public final void shlw(int simm, Addr tmem) { template_rclw(0x04, simm, tmem); }
  public final void shll(int simm, Addr tmem) { template_rcll(0x04, simm, tmem); }
  public final void shrb(Reg treg) { template_rclb(0x05, treg); }
  public final void shrw(Reg treg) { template_rclw(0x05, treg); }
  public final void shrl(Reg treg) { template_rcll(0x05, treg); }
  public final void shrb(Addr tmem) { template_rclb(0x05, tmem); }
  public final void shrw(Addr tmem) { template_rclw(0x05, tmem); }
  public final void shrl(Addr tmem) { template_rcll(0x05, tmem); }
  public final void shrb(int simm, Reg treg) { template_rclb(0x05, simm, treg); }
  public final void shrw(int simm, Reg treg) { template_rclw(0x05, simm, treg); }
  public final void shrl(int simm, Reg treg) { template_rcll(0x05, simm, treg); }
  public final void shrb(int simm, Addr tmem) { template_rclb(0x05, simm, tmem); }
  public final void shrw(int simm, Addr tmem) { template_rclw(0x05, simm, tmem); }
  public final void shrl(int simm, Addr tmem) { template_rcll(0x05, simm, tmem); }

  /* SBB */
  public final void sbbb(Reg sreg, Reg treg) { template_adcb(0x18, 0x03, sreg, treg); }
  public final void sbbw(Reg sreg, Reg treg) { template_adcw(0x18, 0x03, sreg, treg); }
  public final void sbbl(Reg sreg, Reg treg) { template_adcl(0x18, 0x03, sreg, treg); }
  public final void sbbb(Reg sreg, Addr tmem) { template_adcb(0x18, 0x03, sreg, tmem); }
  public final void sbbw(Reg sreg, Addr tmem) { template_adcw(0x18, 0x03, sreg, tmem); }
  public final void sbbl(Reg sreg, Addr tmem) { template_adcl(0x18, 0x03, sreg, tmem); }
  public final void sbbb(Addr smem, Reg treg) { template_adcb(0x18, 0x03, smem, treg); }
  public final void sbbw(Addr smem, Reg treg) { template_adcw(0x18, 0x03, smem, treg); }
  public final void sbbl(Addr smem, Reg treg) { template_adcl(0x18, 0x03, smem, treg); }
  public final void sbbb(int simm, Reg treg) { template_adcb(0x18, 0x03, simm, treg); }
  public final void sbbw(int simm, Reg treg) { template_adcw(0x18, 0x03, simm, treg); }
  public final void sbbl(int simm, Reg treg) { template_adcl(0x18, 0x03, simm, treg); }
  public final void sbbb(int simm, Addr tmem) { template_adcb(0x18, 0x03, simm, tmem); }
  public final void sbbw(int simm, Addr tmem) { template_adcw(0x18, 0x03, simm, tmem); }
  public final void sbbl(int simm, Addr tmem) { template_adcl(0x18, 0x03, simm, tmem); }
  public final void sbbw(Imm simm, Reg treg) { template_adcw(0x18, 0x03, simm, treg); }
  public final void sbbl(Imm simm, Reg treg) { template_adcl(0x18, 0x03, simm, treg); }
  public final void sbbw(Imm simm, Addr tmem) { template_adcw(0x18, 0x03, simm, tmem); }
  public final void sbbl(Imm simm, Addr tmem) { template_adcl(0x18, 0x03, simm, tmem); }

  /* SCASB/SCASW/SCASD */
  public final void scasb() {
    opcode8(0xAE);
  }
  public final void scasw() {
    opcode16(0xAE);
  }
  public final void scasd() {
    opcode32(0xAE);
  }

  /* SETcc */
  public final void seta(Reg treg) { template_seta(0x97, treg); }
  public final void seta(Addr tmem) { template_seta(0x97, tmem); }
  public final void setae(Reg treg) { template_seta(0x93, treg); }
  public final void setae(Addr tmem) { template_seta(0x93, tmem); }
  public final void setb(Reg treg) { template_seta(0x92, treg); }
  public final void setb(Addr tmem) { template_seta(0x92, tmem); }
  public final void setbe(Reg treg) { template_seta(0x96, treg); }
  public final void setbe(Addr tmem) { template_seta(0x96, tmem); }
  public final void setc(Reg treg) { template_seta(0x92, treg); }
  public final void setc(Addr tmem) { template_seta(0x92, tmem); }
  public final void sete(Reg treg) { template_seta(0x94, treg); }
  public final void sete(Addr tmem) { template_seta(0x94, tmem); }
  public final void setg(Reg treg) { template_seta(0x9F, treg); }
  public final void setg(Addr tmem) { template_seta(0x9F, tmem); }
  public final void setge(Reg treg) { template_seta(0x9D, treg); }
  public final void setge(Addr tmem) { template_seta(0x9D, tmem); }
  public final void setl(Reg treg) { template_seta(0x9C, treg); }
  public final void setl(Addr tmem) { template_seta(0x9C, tmem); }
  public final void setle(Reg treg) { template_seta(0x9E, treg); }
  public final void setle(Addr tmem) { template_seta(0x9E, tmem); }
  public final void setna(Reg treg) { template_seta(0x96, treg); }
  public final void setna(Addr tmem) { template_seta(0x96, tmem); }
  public final void setnae(Reg treg) { template_seta(0x92, treg); }
  public final void setnae(Addr tmem) { template_seta(0x92, tmem); }
  public final void setnb(Reg treg) { template_seta(0x93, treg); }
  public final void setnb(Addr tmem) { template_seta(0x93, tmem); }
  public final void setnbe(Reg treg) { template_seta(0x97, treg); }
  public final void setnbe(Addr tmem) { template_seta(0x97, tmem); }
  public final void setnc(Reg treg) { template_seta(0x93, treg); }
  public final void setnc(Addr tmem) { template_seta(0x93, tmem); }
  public final void setne(Reg treg) { template_seta(0x95, treg); }
  public final void setne(Addr tmem) { template_seta(0x95, tmem); }
  public final void setng(Reg treg) { template_seta(0x9E, treg); }
  public final void setng(Addr tmem) { template_seta(0x9E, tmem); }
  public final void setnge(Reg treg) { template_seta(0x9C, treg); }
  public final void setnge(Addr tmem) { template_seta(0x9C, tmem); }
  public final void setnl(Reg treg) { template_seta(0x9D, treg); }
  public final void setnl(Addr tmem) { template_seta(0x9D, tmem); }
  public final void setnle(Reg treg) { template_seta(0x9F, treg); }
  public final void setnle(Addr tmem) { template_seta(0x9F, tmem); }
  public final void setno(Reg treg) { template_seta(0x91, treg); }
  public final void setno(Addr tmem) { template_seta(0x91, tmem); }
  public final void setnp(Reg treg) { template_seta(0x9B, treg); }
  public final void setnp(Addr tmem) { template_seta(0x9B, tmem); }
  public final void setns(Reg treg) { template_seta(0x99, treg); }
  public final void setns(Addr tmem) { template_seta(0x99, tmem); }
  public final void setnz(Reg treg) { template_seta(0x95, treg); }
  public final void setnz(Addr tmem) { template_seta(0x95, tmem); }
  public final void seto(Reg treg) { template_seta(0x90, treg); }
  public final void seto(Addr tmem) { template_seta(0x90, tmem); }
  public final void setp(Reg treg) { template_seta(0x9A, treg); }
  public final void setp(Addr tmem) { template_seta(0x9A, tmem); }
  public final void setpe(Reg treg) { template_seta(0x9A, treg); }
  public final void setpe(Addr tmem) { template_seta(0x9A, tmem); }
  public final void setpo(Reg treg) { template_seta(0x9B, treg); }
  public final void setpo(Addr tmem) { template_seta(0x9B, tmem); }
  public final void sets(Reg treg) { template_seta(0x98, treg); }
  public final void sets(Addr tmem) { template_seta(0x98, tmem); }
  public final void setz(Reg treg) { template_seta(0x94, treg); }
  public final void setz(Addr tmem) { template_seta(0x94, tmem); }

  /* SGDT/SIDT */
  public final void sgdt(Addr tmem) { template_lgdt(0x00, tmem); }
  public final void sidt(Addr tmem) { template_lgdt(0x01, tmem); }

  /* SHLD */
  public final void shldw(Reg sreg, Reg treg) { template_shldw(0xA4, sreg, treg); }
  public final void shldl(Reg sreg, Reg treg) { template_shldl(0xA4, sreg, treg); }
  public final void shldw(Reg sreg, Addr tmem) { template_shldw(0xA4, sreg, tmem); }
  public final void shldl(Reg sreg, Addr tmem) { template_shldl(0xA4, sreg, tmem); }
  public final void shldw(int simm, Reg sreg, Reg treg) { template_shldw(0xA4, simm, sreg, treg); }
  public final void shldl(int simm, Reg sreg, Reg treg) { template_shldl(0xA4, simm, sreg, treg); }
  public final void shldw(int simm, Reg sreg, Addr tmem) { template_shldw(0xA4, simm, sreg, tmem); }
  public final void shldl(int simm, Reg sreg, Addr tmem) { template_shldl(0xA4, simm, sreg, tmem); }

  /* SHRD */
  public final void shrdw(Reg sreg, Reg treg) { template_shldw(0xAC, sreg, treg); }
  public final void shrdl(Reg sreg, Reg treg) { template_shldl(0xAC, sreg, treg); }
  public final void shrdw(Reg sreg, Addr tmem) { template_shldw(0xAC, sreg, tmem); }
  public final void shrdl(Reg sreg, Addr tmem) { template_shldl(0xAC, sreg, tmem); }
  public final void shrdw(int simm, Reg sreg, Reg treg) { template_shldw(0xAC, simm, sreg, treg); }
  public final void shrdl(int simm, Reg sreg, Reg treg) { template_shldl(0xAC, simm, sreg, treg); }
  public final void shrdw(int simm, Reg sreg, Addr tmem) { template_shldw(0xAC, simm, sreg, tmem); }
  public final void shrdl(int simm, Reg sreg, Addr tmem) { template_shldl(0xAC, simm, sreg, tmem); }

  /* SLDT */
  public final void sldt(Reg treg) { template_lldt(0x00, 0x00, treg); }
  public final void sldt(Addr tmem) { template_lldt(0x00, 0x00, tmem); }

  /* SMSW */
  public final void smsw(Reg treg) { template_lldt(0x01, 0x04, treg); }
  public final void smsw(Addr tmem) { template_lldt(0x01, 0x04, tmem); }

  /* STC */
  public final void stc() {
    opcode8(0xF9);
  }

  /* STD */
  public final void std() {
    opcode8(0xFD);
  }

  /* STI */
  public final void sti() {
    opcode8(0xFB);
  }

  /* STOSB/STOSW/STOSD */
  public final void stosb() {
    opcode8(0xAA);
  }
  public final void stosw() {
    opcode16(0xAA);
  }
  public final void stosd() {
    opcode32(0xAA);
  }

  /* STR */
  public final void str(Reg treg) { template_lldt(0x00, 0x01, treg); }
  public final void str(Addr tmem) { template_lldt(0x00, 0x01, tmem); }

  /* SUB */
  public final void subb(Reg sreg, Reg treg) { template_adcb(0x28, 0x05, sreg, treg); }
  public final void subw(Reg sreg, Reg treg) { template_adcw(0x28, 0x05, sreg, treg); }
  public final void subl(Reg sreg, Reg treg) { template_adcl(0x28, 0x05, sreg, treg); }
  public final void subb(Reg sreg, Addr tmem) { template_adcb(0x28, 0x05, sreg, tmem); }
  public final void subw(Reg sreg, Addr tmem) { template_adcw(0x28, 0x05, sreg, tmem); }
  public final void subl(Reg sreg, Addr tmem) { template_adcl(0x28, 0x05, sreg, tmem); }
  public final void subb(Addr smem, Reg treg) { template_adcb(0x28, 0x05, smem, treg); }
  public final void subw(Addr smem, Reg treg) { template_adcw(0x28, 0x05, smem, treg); }
  public final void subl(Addr smem, Reg treg) { template_adcl(0x28, 0x05, smem, treg); }
  public final void subb(int simm, Reg treg) { template_adcb(0x28, 0x05, simm, treg); }
  public final void subw(int simm, Reg treg) { template_adcw(0x28, 0x05, simm, treg); }
  public final void subl(int simm, Reg treg) { template_adcl(0x28, 0x05, simm, treg); }
  public final void subb(int simm, Addr tmem) { template_adcb(0x28, 0x05, simm, tmem); }
  public final void subw(int simm, Addr tmem) { template_adcw(0x28, 0x05, simm, tmem); }
  public final void subl(int simm, Addr tmem) { template_adcl(0x28, 0x05, simm, tmem); }
  public final void subw(Imm simm, Reg treg) { template_adcw(0x28, 0x05, simm, treg); }
  public final void subl(Imm simm, Reg treg) { template_adcl(0x28, 0x05, simm, treg); }
  public final void subw(Imm simm, Addr tmem) { template_adcw(0x28, 0x05, simm, tmem); }
  public final void subl(Imm simm, Addr tmem) { template_adcl(0x28, 0x05, simm, tmem); }

  /* TEST */
  public final void testb(Reg sreg, Reg treg) {
    int sr = r8(sreg);
    int tr = r8(treg);
    opcode8(0x84);
    modrm(3, sr, tr);
  }
  public final void testw(Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    opcode16(0x84);
    modrm(3, sr, tr);
  }
  public final void testl(Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    opcode32(0x84);
    modrm(3, sr, tr);
  }
  public final void testb(Reg sreg, Addr tmem) {
    int sr = r8(sreg);
    overrd(tmem);
    opcode8(0x84);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  public final void testw(Reg sreg, Addr tmem) {
    int sr = r16(sreg);
    overrd(tmem);
    opcode16(0x84);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  public final void testl(Reg sreg, Addr tmem) {
    int sr = r32(sreg);
    overrd(tmem);
    opcode32(0x84);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  public final void testb(int simm, Reg treg) {
    int tr = r8(treg);
    if (tr == 0) {
      opcode8(0xA8);
      imm8(simm);
    } else {
      opcode8(0xF6);
      modrm(3, 0, tr);
      imm8(simm);
    }
  }
  public final void testw(int simm, Reg treg) {
    int tr = r16(treg);
    if (tr == 0) {
      opcode16(0xA8);
      imm16(simm);
    } else {
      opcode16(0xF6);
      modrm(3, 0, tr);
      imm16(simm);
    }
  }
  public final void testl(int simm, Reg treg) {
    int tr = r32(treg);
    if (tr == 0) {
      opcode32(0xA8);
      imm32(simm);
    } else {
      opcode32(0xF6);
      modrm(3, 0, tr);
      imm32(simm);
    }
  }
  public final void testb(int simm, Addr tmem) {
    overrd(tmem);
    opcode8(0xF6);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
    imm8(simm);
  }
  public final void testw(int simm, Addr tmem) {
    overrd(tmem);
    opcode16(0xF6);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
    imm16(simm);
  }
  public final void testl(int simm, Addr tmem) {
    overrd(tmem);
    opcode32(0xF6);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
    imm32(simm);
  }
  public final void testw(Imm simm, Reg treg) {
    int tr = r16(treg);
    if (tr == 0) {
      opcode16(0xA8);
      imm16(simm);
    } else {
      opcode16(0xF6);
      modrm(3, 0, tr);
      imm16(simm);
    }
  }
  public final void testl(Imm simm, Reg treg) {
    int tr = r32(treg);
    if (tr == 0) {
      opcode32(0xA8);
      imm32(simm);
    } else {
      opcode32(0xF6);
      modrm(3, 0, tr);
      imm32(simm);
    }
  }
  public final void testw(Imm simm, Addr tmem) {
    overrd(tmem);
    opcode16(0xF6);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
    imm16(simm);
  }
  public final void testl(Imm simm, Addr tmem) {
    overrd(tmem);
    opcode32(0xF6);
    modrm(mod(tmem), 0, rm(tmem));
    sibdisp(tmem);
    imm32(simm);
  }
  
  /* VERR/VERW */
  public final void verr(Reg treg) { template_lldt(0x00, 0x04, treg); }
  public final void verr(Addr tmem) { template_lldt(0x00, 0x04, tmem); }
  public final void verw(Reg treg) { template_lldt(0x00, 0x05, treg); }
  public final void verw(Addr tmem) { template_lldt(0x00, 0x05, tmem); }

  /* WAIT/FWAIT */
  public final void fwait() {
    opcode8(0x9B);
  }

  /* XCHG */
  public final void xchgb(Reg sreg, Reg treg) {
    int sr = r8(sreg);
    int tr = r8(treg);
    opcode8(0x86);
    modrm(3, tr, sr);
  }
  public final void xchgb(Reg sreg, Addr tmem) {
    int sr = r8(sreg);
    overrd(tmem);
    opcode8(0x86);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  public final void xchgb(Addr smem, Reg treg) {
    int tr = r8(treg);
    overrd(smem);
    opcode8(0x86);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  public final void xchgw(Reg sreg, Reg treg) {
    int sr = r16(sreg);
    int tr = r16(treg);
    if (tr == 0) {
      op16();
      opcode8(0x90|sr);
    } else if (sr == 0) {
      op16();
      opcode8(0x90|tr);
    } else {
      opcode16(0x86);
      modrm(3, tr, sr);
    }
  }
  public final void xchgw(Reg sreg, Addr tmem) {
    int sr = r16(sreg);
    overrd(tmem);
    opcode16(0x86);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  public final void xchgw(Addr smem, Reg treg) {
    int tr = r16(treg);
    overrd(smem);
    opcode16(0x86);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }
  public final void xchgl(Reg sreg, Reg treg) {
    int sr = r32(sreg);
    int tr = r32(treg);
    if (tr == 0) {
      op32();
      opcode8(0x90|sr);
    } else if (sr == 0) {
      op32();
      opcode8(0x90|tr);
    } else {
      opcode32(0x86);
      modrm(3, tr, sr);
    }
  }
  public final void xchgl(Reg sreg, Addr tmem) {
    int sr = r32(sreg);
    overrd(tmem);
    opcode32(0x86);
    modrm(mod(tmem), sr, rm(tmem));
    sibdisp(tmem);
  }
  public final void xchgl(Addr smem, Reg treg) {
    int tr = r32(treg);
    overrd(smem);
    opcode32(0x86);
    modrm(mod(smem), tr, rm(smem));
    sibdisp(smem);
  }

  /* XLATB */
  public final void xlatb() {
    opcode8(0xD7);
  }

  /* XOR */
  public final void xorb(Reg sreg, Reg treg) { template_adcb(0x30, 0x06, sreg, treg); }
  public final void xorw(Reg sreg, Reg treg) { template_adcw(0x30, 0x06, sreg, treg); }
  public final void xorl(Reg sreg, Reg treg) { template_adcl(0x30, 0x06, sreg, treg); }
  public final void xorb(Reg sreg, Addr tmem) { template_adcb(0x30, 0x06, sreg, tmem); }
  public final void xorw(Reg sreg, Addr tmem) { template_adcw(0x30, 0x06, sreg, tmem); }
  public final void xorl(Reg sreg, Addr tmem) { template_adcl(0x30, 0x06, sreg, tmem); }
  public final void xorb(Addr smem, Reg treg) { template_adcb(0x30, 0x06, smem, treg); }
  public final void xorw(Addr smem, Reg treg) { template_adcw(0x30, 0x06, smem, treg); }
  public final void xorl(Addr smem, Reg treg) { template_adcl(0x30, 0x06, smem, treg); }
  public final void xorb(int simm, Reg treg) { template_adcb(0x30, 0x06, simm, treg); }
  public final void xorw(int simm, Reg treg) { template_adcw(0x30, 0x06, simm, treg); }
  public final void xorl(int simm, Reg treg) { template_adcl(0x30, 0x06, simm, treg); }
  public final void xorb(int simm, Addr tmem) { template_adcb(0x30, 0x06, simm, tmem); }
  public final void xorw(int simm, Addr tmem) { template_adcw(0x30, 0x06, simm, tmem); }
  public final void xorl(int simm, Addr tmem) { template_adcl(0x30, 0x06, simm, tmem); }
  public final void xorw(Imm simm, Reg treg) { template_adcw(0x30, 0x06, simm, treg); }
  public final void xorl(Imm simm, Reg treg) { template_adcl(0x30, 0x06, simm, treg); }
  public final void xorw(Imm simm, Addr tmem) { template_adcw(0x30, 0x06, simm, tmem); }
  public final void xorl(Imm simm, Addr tmem) { template_adcl(0x30, 0x06, simm, tmem); }

}

