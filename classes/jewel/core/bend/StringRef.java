/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

public final class StringRef extends Symbol {

  private final String value;

  public StringRef(String value) {
    if (value == null)
      throw new NullPointerException();
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public int hashCode() {
    return value.hashCode();
  }

  public boolean equals(Object object) {
    return object instanceof StringRef
        && ((StringRef)object).value.equals(value);
  }

  public String toString() {
    return value;
  }

}

