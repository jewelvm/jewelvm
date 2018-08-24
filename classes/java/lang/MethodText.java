/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package java.lang;

import jewel.core.bend.Extern;
import jewel.core.bend.MethodEntry;
import jewel.core.bend.StringRef;
import jewel.core.bend.Symbol;
import jewel.core.bend.SymbolicType;
import jewel.core.bend.Relocatable;

final class MethodText extends Relocatable {

  private static final ThreadLocal local = new ThreadLocal();

  protected static void setLoader(ClassLoader loader) {
    local.set(loader);
  }

  protected static ClassLoader getLoader() {
    return (ClassLoader)local.get();
  }

  private static Object resolveSymbol(Symbol symbol) {
    if (symbol instanceof SymbolicType) {
      String name = ((SymbolicType)symbol).getName().replace('/', '.');
      try {
        return Class.forName(name, false, getLoader());
      } catch (ClassNotFoundException e) {
        throw new NoClassDefFoundError(e.getMessage());
      }
    }
    if (symbol instanceof MethodEntry) {
      String name = ((MethodEntry)symbol).getName().replace('/', '.');
      int index = ((MethodEntry)symbol).getIndex();
      Class clazz;
      try {
        clazz = Class.forName(name, false, getLoader());
      } catch (ClassNotFoundException e) {
        throw new NoClassDefFoundError(e.getMessage());
      }
      Machinery.resolveClass(clazz);
      return builtin.getMethodText(clazz, index);
    }
    if (symbol instanceof StringRef)
      return ((StringRef)symbol).getValue().intern();
    throw new IllegalArgumentException("Unsupported symbol");
  }

  protected static MethodText newMethodText(byte[] array, int start, int length) {
    if (start < 0 || start > array.length)
      throw new ArrayIndexOutOfBoundsException(start);
    int end = start+length;
    if (end < start || end > array.length)
      throw new ArrayIndexOutOfBoundsException(end);
    return builtin.newMethodText(array, start, length);
  }

  private MethodText() { }

  public Class getDeclaringClass() {
    return builtin.getDeclaringClass(this);
  }

  public int getIndex() {
    return builtin.getIndex(this);
  }
  
  protected void posReloc(int offset) {
    builtin.posReloc(this, offset);
  }

  protected void negReloc(int offset) {
    builtin.negReloc(this, offset);
  }

  protected void posPatch(int offset, Symbol symbol) {
    if (symbol instanceof Extern) {
      String name = ((Extern)symbol).getName();
      builtin.patchSymbol(this, offset, name);
      return;
    }
    Object object = resolveSymbol(symbol);
    builtin.posPatch(this, offset, object);
  }

  protected void negPatch(int offset, Symbol symbol) {
    if (symbol instanceof Extern) {
      throw new UnsupportedOperationException();
      //String name = ((Extern)symbol).getName();
      //builtin.patchSymbol(this, offset, name);
      //return;
    }
    Object object = resolveSymbol(symbol);
    builtin.negPatch(this, offset, object);
  }

}

