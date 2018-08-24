/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Addr;
import jewel.core.bend.Reg;
import jewel.core.bend.Symbol;

import java.util.BitSet;

public class i386_Addr extends Addr {

  public final int disp;
  public final Symbol symbol;
  public final Reg sreg0;
  public final Reg sreg1;
  public final byte scale;

  public i386_Addr(int disp, Symbol symbol, Reg sreg0, Reg sreg1, int scale) {
    this.disp = disp;
    this.symbol = symbol;
    this.sreg0 = sreg0;
    this.sreg1 = sreg1;
    this.scale = (byte)scale;
  }

  public void uses(BitSet set) {
    if (sreg0 != null)
      sreg0.addTo(set);
    if (sreg1 != null)
      sreg1.addTo(set);
    if (i386_RegisterSet.ESP.equals(sreg0) || i386_RegisterSet.EBP.equals(sreg0) || i386_RegisterSet.ESP.equals(sreg1) || i386_RegisterSet.EBP.equals(sreg1))
      i386_RegisterSet.SS.addTo(set);
    else
      i386_RegisterSet.DS.addTo(set);
  }

  public void defines(BitSet set) { }

  public Addr replace(Reg source, Reg target) {
    int disp = this.disp;
    Symbol symbol = this.symbol;
    Reg sreg0 = this.sreg0;
    Reg sreg1 = this.sreg1;
    int scale = this.scale;
    if (source.equals(sreg0))
      sreg0 = target;
    if (source.equals(sreg1))
      sreg1 = target;
    return new i386_Addr(disp, symbol, sreg0, sreg1, scale);
  }

  public int hashCode() {
    int hashCode = 0;
    hashCode += disp;
    if (symbol != null)
      hashCode += symbol.hashCode();
    if (sreg0 != null)
      hashCode += sreg0.hashCode();
    if (sreg1 != null)
      hashCode += sreg1.hashCode();
    hashCode += scale;
    return hashCode;
  }

  public boolean equals(Object object) {
    return object instanceof i386_Addr
        && ((i386_Addr)object).disp == disp
        && (((i386_Addr)object).symbol == symbol || (symbol != null && symbol.equals(((i386_Addr)object).symbol)))
        && (((i386_Addr)object).sreg0 == sreg0 || (sreg0 != null && sreg0.equals(((i386_Addr)object).sreg0)))
        && (((i386_Addr)object).sreg1 == sreg1 || (sreg1 != null && sreg1.equals(((i386_Addr)object).sreg1)))
        && ((i386_Addr)object).scale == scale;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (symbol != null) {
      sb.append(symbol);
      if (disp > 0)
        sb.append('+');
    }
    if (disp != 0)
      sb.append(disp);
    if (sreg1 != null) {
      sb.append('(');
      if (sreg0 == null && scale == 1)
        sb.append(sreg1);
      else {
        if (sreg0 != null)
          sb.append(sreg0);
        sb.append(',');
        sb.append(sreg1);
        sb.append(',');
        if (scale != 1)
          sb.append(scale);
      }
      sb.append(')');
    }
    return sb.toString();
  }

}

