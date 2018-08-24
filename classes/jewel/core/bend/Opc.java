/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import jewel.core.bend.FlowAssembler.InstructionStatement;

public abstract class Opc {

  public final int index;

  protected Opc(int index) {
    if (index < 0)
      throw new IllegalArgumentException();
    this.index = index;
  }

  public abstract void emit(Assembler as, InstructionStatement insn, String format);

  public final int hashCode() {
    return index;
  }

  public final boolean equals(Object object) {
    return this == object
        || (object instanceof Opc
        && ((Opc)object).index == index
        && ((Opc)object).getClass() == getClass());
  }

  public abstract String toString();

}

