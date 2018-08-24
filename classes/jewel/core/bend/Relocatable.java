/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

public abstract class Relocatable {

  protected Relocatable() { }

  protected abstract void posReloc(int offset);
  protected abstract void negReloc(int offset);
  protected abstract void posPatch(int offset, Symbol symbol);
  protected abstract void negPatch(int offset, Symbol symbol);

}

