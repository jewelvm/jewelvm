/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import java.util.BitSet;

public abstract class Reg {

  public final int index;

  protected Reg(int index) {
    if (index < 0)
      throw new IllegalArgumentException();
    this.index = index;
  }

  public final void addTo(BitSet set) {
    set.set(index);
  }

  public final void removeFrom(BitSet set) {
    set.clear(index);
  }

  public final int hashCode() {
    return index;
  }

  public final boolean equals(Object object) {
    return this == object
        || (object instanceof Reg
        && ((Reg)object).index == index
        && ((Reg)object).getClass() == getClass());
  }

  public abstract String toString();

}

