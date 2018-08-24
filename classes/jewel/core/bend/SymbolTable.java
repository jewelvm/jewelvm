/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import java.util.HashMap;
import java.util.Iterator;

public final class SymbolTable {

  private final HashMap declarations = new HashMap();

  public SymbolTable() { }

  public Object getDeclValue(Symbol symbol) {
    return declarations.get(symbol);
  }

  public void declare(Symbol symbol, Object value) {
    declarations.put(symbol, value);
  }

  public Iterator declarations() {
    return declarations.entrySet().iterator();
  }

}

