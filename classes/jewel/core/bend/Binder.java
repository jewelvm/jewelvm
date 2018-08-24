/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;

import java.util.BitSet;

public abstract class Binder {

  protected Binder() { }
  
  protected abstract void bind(ControlFlowGraph cfg);

  public abstract int paramWords();
  public abstract int localWords();
  public abstract int regBase(int reg);
  public abstract BitSet livesAt(Statement stmt);

}

