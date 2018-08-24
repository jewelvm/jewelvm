/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Reg;

public class i386_Reg extends Reg {

  private final String name;

  public i386_Reg(int index, String name) {
    super(index);
    if (name == null)
      throw new NullPointerException();
    this.name = name;
  }

  public String toString() {
    return "%"+name;
  }

}

