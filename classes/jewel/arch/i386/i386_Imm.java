/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Imm;
import jewel.core.bend.Symbol;

public class i386_Imm extends Imm {

  public final int disp;
  public final Symbol symbol;

  public i386_Imm(int disp, Symbol symbol) {
    this.disp = disp;
    this.symbol = symbol;
  }

  public int hashCode() {
    int hashCode = 0;
    hashCode += disp;
    if (symbol != null)
      hashCode += symbol.hashCode();
    return hashCode;
  }

  public boolean equals(Object object) {
    return object instanceof i386_Addr
        && ((i386_Addr)object).disp == disp
        && (((i386_Addr)object).symbol == symbol || (symbol != null && symbol.equals(((i386_Addr)object).symbol)));
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
    return sb.toString();
  }

}

