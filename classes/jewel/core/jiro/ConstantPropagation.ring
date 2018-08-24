/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.beg.TreeNode.LeafRef;
import jewel.core.jiro.beg.TreeNode.LeftRef;
import jewel.core.jiro.beg.TreeNode.MiddleRef;
import jewel.core.jiro.beg.TreeNode.RightRef;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.Iterator;

public final class ConstantPropagation implements Optimization {

  public ConstantPropagation() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (cfg.isSSA()) {

      cfg.invalidate();
      int maxReg = cfg.regMax();

      Object[] cons = new Object[maxReg];
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.cover(cons);
      }

      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        changed |= matcher.replace(cons, null);
      }

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void cover(Object[] cons)
      : IR.IDEFINE(IR.ICONST) = { cons[@1.getReg()] = new Integer(@2.getValue()); }
      | IR.LDEFINE(IR.LCONST) = { cons[@1.getReg()] = new Long(@2.getValue()); }
      | IR.FDEFINE(IR.FCONST) = { cons[@1.getReg()] = new Float(@2.getValue()); }
      | IR.DDEFINE(IR.DCONST) = { cons[@1.getReg()] = new Double(@2.getValue()); }
      | IR.ADEFINE(IR.ANULL) = { cons[@1.getReg()] = NULL; }
      | IR.ADEFINE(IR.ASTRING) = { cons[@1.getReg()] = @2.getValue(); }
      | IR.ADEFINE(IR.ACLASS) = { cons[@1.getReg()] = new SymType(@2.getSymbolicType()); }
      | IR.ADEFINE(IR.MLOOKUP(IR.ACLASS)) = { cons[@1.getReg()] = new MethodType(@3.getSymbolicType(), @2.getDispatchIndex()); }
      | default = { }
      ;

    public boolean replace(Object[] cons, LeafRef ref)
      : IR.IUSE = {
        Integer source = (Integer)cons[@1.getReg()];
        if (source != null) {
          ref.set(new IR.iconst(source.intValue()));
          return true;
        }
        return false;
      }
      | IR.LUSE = {
        Long source = (Long)cons[@1.getReg()];
        if (source != null) {
          ref.set(new IR.lconst(source.longValue()));
          return true;
        }
        return false;
      }
      | IR.FUSE = {
        Float source = (Float)cons[@1.getReg()];
        if (source != null) {
          ref.set(new IR.fconst(source.floatValue()));
          return true;
        }
        return false;
      }
      | IR.DUSE = {
        Double source = (Double)cons[@1.getReg()];
        if (source != null) {
          ref.set(new IR.dconst(source.doubleValue()));
          return true;
        }
        return false;
      }
      | IR.AUSE = {
        Object source = cons[@1.getReg()];
        if (source != null) {
          if (source instanceof String)
            ref.set(new IR.astring((String)source));
          else if (source instanceof SymType)
            ref.set(new IR.aclass(((SymType)source).name));
          else if (source instanceof MethodType)
            ref.set(new IR.mlookup(new IR.aclass(((MethodType)source).name), ((MethodType)source).index));
          else
            ref.set(new IR.anull());
          return true;
        }
        return false;
      }
      | default = {
        boolean changed = false;
        if (  left$ != null) changed |=   left$.replace(cons, new   LeftRef(@1)); 
        if (middle$ != null) changed |= middle$.replace(cons, new MiddleRef(@1)); 
        if ( right$ != null) changed |=  right$.replace(cons, new  RightRef(@1));
        return changed;
      }
      ;
  }

  private static final Object NULL = new Object() {

    public int hashCode() {
      return 0;
    }

    public String toString() {
      return "null";
    }

  };

  private static final class SymType {

    public final String name;

    public SymType(String name) {
      if (name == null)
        throw new NullPointerException();
      this.name = name;
    }

    public int hashCode() {
      return ~name.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof SymType
          && ((SymType)object).name.equals(name);
    }

    public String toString() {
      return name;
    }

  }

  private static final class MethodType {

    public final String name;
    public final int index;

    public MethodType(String name, int index) {
      if (name == null)
        throw new NullPointerException();
      this.name = name;
      if (index < 0)
        throw new IllegalArgumentException();
      this.index = index;
    }

    public int hashCode() {
      return ~name.hashCode()^index;
    }

    public boolean equals(Object object) {
      return object instanceof MethodType
          && ((MethodType)object).index == index
          && ((MethodType)object).name.equals(name);
    }

    public String toString() {
      return name+"["+index+"]";
    }

  }

}

