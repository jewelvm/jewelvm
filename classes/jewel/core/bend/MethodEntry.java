/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

public final class MethodEntry extends Symbol {

  private final String name;
  private final int index;

  public MethodEntry(String name, int index) {
    if (name == null)
      throw new NullPointerException();
    this.name = name;
    this.index = index;
  }

  public String getName() {
    return name;
  }
  
  public int getIndex() {
    return index;
  }

  public int hashCode() {
    return name.hashCode()+index;
  }

  public boolean equals(Object object) {
    return object instanceof MethodEntry
        && ((MethodEntry)object).name.equals(name)
	&& ((MethodEntry)object).index == index;
  }

  public String toString() {
    return name+"["+index+"]";
  }

}

