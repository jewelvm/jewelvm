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

public final class ArrayLengthPropagation implements Optimization {

  public ArrayLengthPropagation() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (cfg.isSSA()) {

      cfg.invalidate();

      IR.inode[] length = new IR.inode[cfg.aregMax()];

      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.assign(length);
      }

      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        changed |= matcher.propagate(length, null);
      }

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void assign(IR.inode[] length)
      : IR.ARESULT = {
        IRStatement ownerStmt = (IRStatement)@1.ownerStmt();
        IRStatement previousStmt = (IRStatement)ownerStmt.previousStmt();
        Matcher matcher = new Matcher(previousStmt);
        length[@1.getReg()] = matcher.newarray(); 
      }
      | default = { }
      ;

    private IR.inode newarray()
      : IR.LABEL = {
        IRStatement ownerStmt = (IRStatement)@1.ownerStmt();
        IRStatement previousStmt = (IRStatement)ownerStmt.previousStmt();
        Matcher matcher = new Matcher(previousStmt);
        return matcher.newarray(); 
      }
      | IR.NEWARRAY(newarray,iexp) = { return @3(); }
      | IR.NEWARRAYX(newarray,iexp) = { return @3(); }
      | default = { return null; }
      ;

    private IR.inode iexp()
      : IR.IUSE = { return (IR.inode)@1; }
      | IR.ICONST = { return (IR.inode)@1; }
      ;

    public boolean propagate(IR.inode[] length, LeafRef ref)
      : IR.LENGTH(IR.AUSE) = {
        IR.inode inode = length[@2.getReg()];
        if (inode != null) {
          ref.set((IR.inode)inode.clone());
          return true;
        }
        return false;
      }
      | default = {
        boolean changed = false;
        if (  left$ != null) changed |=   left$.propagate(length, new   LeftRef(node$));
        if (middle$ != null) changed |= middle$.propagate(length, new MiddleRef(node$));
        if ( right$ != null) changed |=  right$.propagate(length, new  RightRef(node$));
        return changed;
      }
      ;

  }

}

