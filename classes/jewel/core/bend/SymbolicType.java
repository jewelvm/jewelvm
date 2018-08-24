/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

public final class SymbolicType extends Symbol {

  private final String name;

  public SymbolicType(String name) {
    if (name == null)
      throw new NullPointerException();
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int hashCode() {
    return name.hashCode();
  }

  public boolean equals(Object object) {
    return object instanceof SymbolicType
        && ((SymbolicType)object).name.equals(name);
  }

  public String toString() {
    return name;
  }

}

