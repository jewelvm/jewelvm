/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import jewel.core.bend.FlowAssembler.AssemblerStatement;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;

import java.util.HashMap;
import java.util.Iterator;

public abstract class Backend {

  private static final HashMap backends = new HashMap();

  public static Backend forName(String name) {
    if (name == null)
      throw new NullPointerException();
    Backend backend = (Backend)backends.get(name);
    if (backend == null) {
      Class clazz;
      try {
        clazz = Class.forName("jewel.arch."+name+"."+name+"_Backend");
      } catch (ClassNotFoundException e) {
        throw new NoClassDefFoundError(e.getMessage());
      }
      Object object;
      try {
        object = clazz.newInstance();
      } catch (IllegalAccessException e) {
        throw new IllegalAccessError(e.getMessage());
      } catch (InstantiationException e) {
        throw new InstantiationError(e.getMessage());
      }
      try {
        backend = (Backend)object;
      } catch (ClassCastException e) {
        throw new InstantiationError(e.getMessage());
      }
      backends.put(name, backend);
    }
    return backend;
  }

  protected Backend() { }

  public final CodeOutput translate(MethodInfo method, ControlFlowGraph ircfg) {
    CodeGenerator codegen = newCodeGenerator();
    FlowAssembler fas = newFlowAssembler();
    BinaryAssembler as = newBinaryAssembler();
    codegen.emit(ircfg, fas);
    ControlFlowGraph cfg = fas.toCFG();

    //debug
    //ircfg.printTo(System.err);
    //cfg.printTo(System.err);
    //cfg.show(cfg);
    //debug

    new jewel.core.jiro.UnreachableCodeElimination().applyTo(cfg);

    //cfg.printTo(System.out);

    RegisterAllocator regalloc = newRegisterAllocator();
    ((jewel.arch.i386.i386_RegisterAllocator)regalloc).localWords = codegen.getBinder().localWords();
    try {
      regalloc.allocate(cfg);
    } catch (RuntimeException e) {
      e.printStackTrace();
      cfg.printTo(System.err);
      cfg.show();
      throw e;
    }

    //cfg.printTo(System.out);

    head(method, as, codegen, regalloc);
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      AssemblerStatement stmt = (AssemblerStatement)i.next();
      stmt.emit(as);
    }
    tail(method, as, codegen, regalloc);

    return as.toCodeOutput();
  }

  protected abstract CodeGenerator newCodeGenerator();
  protected abstract BinaryAssembler newBinaryAssembler();
  protected abstract FlowAssembler newFlowAssembler();
  protected abstract RegisterAllocator newRegisterAllocator();

  protected abstract void head(MethodInfo method, BinaryAssembler as, CodeGenerator codegen, RegisterAllocator regalloc);
  protected abstract void tail(MethodInfo method, BinaryAssembler as, CodeGenerator codegen, RegisterAllocator regalloc);

  public abstract CodeOutput lazyTranslate(MethodInfo method);

}

