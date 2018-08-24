/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;

import java.util.Iterator;

public abstract class CodeGenerator {

  protected Binder binder;

  protected CodeGenerator() { }

  public final Binder getBinder() {
    return binder;
  }

  public void emit(ControlFlowGraph cfg, Assembler as) {
    binder = newBinder();
    binder.bind(cfg);
    prologue(as);
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      Statement stmt = (Statement)i.next();
      Matcher matcher = newMatcher(stmt);
      matcher.emit(as);
    }
    epilogue(as);
  }

  protected abstract void prologue(Assembler as);
  protected abstract void epilogue(Assembler as);

  protected abstract Binder newBinder();
  protected abstract Matcher newMatcher(Statement stmt);

  protected static abstract class Matcher {

    protected Matcher() { }

    public abstract void emit(Assembler as);

  }

}

