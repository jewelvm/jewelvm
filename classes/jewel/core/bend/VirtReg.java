/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

public final class VirtReg extends Reg {

  public int regBase;
  public boolean fixed;

  public VirtReg(int index) {
    super(index);
  }

  public String toString() {
    return "%vr"+index;
  }

}

