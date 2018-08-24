/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Addr;
import jewel.core.bend.Imm;
import jewel.core.bend.Reg;
import jewel.core.bend.Symbol;

public interface i386_AddressingModes {

  /* no-register addressing modes */
  public Addr addr(Symbol symbol);
  public Addr addr(int disp, Symbol symbol);

  /* one-register addressing modes */
  public Addr addr(Reg sreg1);
  public Addr addr(Reg sreg1, int scale);
  public Addr addr(Symbol symbol, Reg sreg1);
  public Addr addr(Symbol symbol, Reg sreg1, int scale);
  public Addr addr(int disp, Reg sreg1);
  public Addr addr(int disp, Reg sreg1, int scale);
  public Addr addr(int disp, Symbol symbol, Reg sreg1);
  public Addr addr(int disp, Symbol symbol, Reg sreg1, int scale);

  /* two-register addressing modes */
  public Addr addr(Reg sreg0, Reg sreg1);
  public Addr addr(Reg sreg0, Reg sreg1, int scale);
  public Addr addr(Symbol symbol, Reg sreg0, Reg sreg1);
  public Addr addr(Symbol symbol, Reg sreg0, Reg sreg1, int scale);
  public Addr addr(int disp, Reg sreg0, Reg sreg1);
  public Addr addr(int disp, Reg sreg0, Reg sreg1, int scale);
  public Addr addr(int disp, Symbol symbol, Reg sreg0, Reg sreg1);
  public Addr addr(int disp, Symbol symbol, Reg sreg0, Reg sreg1, int scale);

  /* immediate addressing modes */
  public Imm imm(Symbol symbol);
  public Imm imm(int disp);
  public Imm imm(int disp, Symbol symbol);

  /* address from address plus offset */
  public Addr addr(int diff, Addr addr);

}

