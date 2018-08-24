/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

public final class Label extends Symbol {

  private final String label;

  public Label(String label) {
    if (label == null)
      throw new NullPointerException();
    this.label = label;
  }

  public String getName() {
    return label;
  }

  public int hashCode() {
    return label.hashCode();
  }

  public boolean equals(Object object) {
    return object instanceof Label
        && ((Label)object).label.equals(label);
  }

  public String toString() {
    return label;
  }

}

