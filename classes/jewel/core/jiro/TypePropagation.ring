/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.Context;
import jewel.core.LoadedClassInfo;
import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.beg.TreeNode.LeafRef;
import jewel.core.jiro.beg.TreeNode.LeftRef;
import jewel.core.jiro.beg.TreeNode.MiddleRef;
import jewel.core.jiro.beg.TreeNode.RightRef;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.Iterator;

public final class TypePropagation implements Optimization {

  public TypePropagation() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (cfg.isSSA()) {

      cfg.invalidate();

      String[] symbolic = new String[cfg.aregMax()];

      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.assign(symbolic);
      }

      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        changed |= matcher.propagate(symbolic, null);
      }

    }

    return changed;
  }

  private static String select(String name) {
    Context context = Context.get();
    LoadedClassInfo clazz = context.forName(name);
    if (clazz != null) {
      int dims = name.lastIndexOf('[')+1;
      if (dims > 0)
        if (name.charAt(dims) == 'L')
          clazz = context.forName(name.substring(dims+1, name.length()-1));
      if (clazz != null)
        if (clazz.isFinal())
          return name;
    }
    return null;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void assign(String[] symbolic)
      : IR.ARECEIVE = { symbolic[@1.getReg()] = select(@1.getSymbolicType()); }
      | IR.ARESULT = {
        String name = select(@1.getSymbolicType());
        if (name == null) {
          IRStatement ownerStmt = (IRStatement)@1.ownerStmt();
          IRStatement previousStmt = (IRStatement)ownerStmt.previousStmt();
          Matcher matcher = new Matcher(previousStmt);
          name = matcher.newinstance(); 
        }
        symbolic[@1.getReg()] = name;
      }
      | IR.ADEFINE(IR.ALOAD(assign)) = { symbolic[@1.getReg()] = select(@2.getSymbolicType()); }
      | default = { }
      ;

    private String newinstance()
      : IR.LABEL = {
        IRStatement ownerStmt = (IRStatement)@1.ownerStmt();
        IRStatement previousStmt = (IRStatement)ownerStmt.previousStmt();
        Matcher matcher = new Matcher(previousStmt);
        return matcher.newinstance(); 
      }
      | IR.NEWINSTANCE(IR.ACLASS) = { return @2.getSymbolicType(); }
      | IR.NEWINSTANCEX(IR.ACLASS) = { return @2.getSymbolicType(); }
      | IR.NEWARRAY(IR.ACLASS,newinstance) = { return @2.getSymbolicType(); }
      | IR.NEWARRAYX(IR.ACLASS,newinstance) = { return @2.getSymbolicType(); }
      | default = { return null; }
      ;

    public boolean propagate(String[] symbolic, LeafRef ref)
      : IR.GETCLASS(IR.AUSE) = {
        String sym = symbolic[@2.getReg()];
        if (sym != null) {
          ref.set(new IR.aclass(sym));
          return true;
        }
        return false;
      }
      | default = {
        boolean changed = false;
        if (  left$ != null) changed |=   left$.propagate(symbolic, new   LeftRef(node$));
        if (middle$ != null) changed |= middle$.propagate(symbolic, new MiddleRef(node$));
        if ( right$ != null) changed |=  right$.propagate(symbolic, new  RightRef(node$));
        return changed;
      }
      ;

  }

}

