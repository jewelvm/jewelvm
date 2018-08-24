/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import java.util.BitSet;

public abstract class Addr {

  protected Addr() { }
  
  public abstract void uses(BitSet set);
  public abstract void defines(BitSet set);
  public abstract Addr replace(Reg source, Reg target);

  public abstract int hashCode();
  public abstract boolean equals(Object object);
  public abstract String toString();

}

