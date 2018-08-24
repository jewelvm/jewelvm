/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

public final class Extern extends Symbol {

  private final String name;

  public Extern(String name) {
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
    return object instanceof Extern
        && ((Extern)object).name.equals(name);
  }

  public String toString() {
    return name;
  }

}

