/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

public abstract class Assembler {

  private SymbolTable symtab;

  protected Assembler() {
    reset();
  }

  public void reset() {
    symtab = new SymbolTable();
  }

  public abstract void label(String label);

  public final SymbolTable getSymbolTable() {
    return symtab;
  }

}

