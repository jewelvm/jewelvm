/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.Iterator;

public final class CopyPropagation implements Optimization {

  public CopyPropagation() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (cfg.isSSA()) {

      cfg.invalidate();

      UnionFind uf = new UnionFind(cfg.regMax());
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        changed |= matcher.addcopy(uf);
      }

      if (changed) {

        boolean propagate;
        do {
          propagate = false;
          for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
            BasicBlock currentBB = (BasicBlock)i.next();
            for (Iterator j = currentBB.topdownStmts(); j.hasNext(); ) {
              IRStatement stmt = (IRStatement)j.next();
              Matcher matcher = new Matcher(stmt);
              if (!matcher.phistmt())
                break;
              propagate |= matcher.phicopy(uf);
            }
          }
        } while (propagate);

        for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
          IRStatement stmt = (IRStatement)i.next();
          Matcher matcher = new Matcher(stmt);
          matcher.replace(uf);
        }

      }

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public boolean addcopy(UnionFind uf)
      : IR.IDEFINE(IR.IUSE) = { uf.union2(@1.getReg(), @2.getReg()); return true; }
      | IR.LDEFINE(IR.LUSE) = { uf.union2(@1.getReg(), @2.getReg()); return true; }
      | IR.FDEFINE(IR.FUSE) = { uf.union2(@1.getReg(), @2.getReg()); return true; }
      | IR.DDEFINE(IR.DUSE) = { uf.union2(@1.getReg(), @2.getReg()); return true; }
      | IR.ADEFINE(IR.AUSE) = { uf.union2(@1.getReg(), @2.getReg()); return true; }
      | default = { return false; }
      ;

    public boolean phistmt()
      : IR.LABEL = { return true; }
      | IR.IPHI = { return true; }
      | IR.LPHI = { return true; }
      | IR.FPHI = { return true; }
      | IR.DPHI = { return true; }
      | IR.APHI = { return true; }
      | default = { return false; }
      ;

    public boolean phicopy(UnionFind uf)
      : IR.IPHI = {
        Iterator i = @1.edges();
        if (i.hasNext()) {
          IRControlEdge edge = (IRControlEdge)i.next();
          int reg = uf.find(@1.getReg(edge));
          while (i.hasNext()) {
            edge = (IRControlEdge)i.next();
            if (uf.find(@1.getReg(edge)) != reg)
              return false;
          }
          if (uf.find(@1.getReg()) != reg) {
            uf.union2(@1.getReg(), reg);
            return true;
          }
        }
        return false;
      }
      | IR.LPHI = {
        Iterator i = @1.edges();
        if (i.hasNext()) {
          IRControlEdge edge = (IRControlEdge)i.next();
          int reg = uf.find(@1.getReg(edge));
          while (i.hasNext()) {
            edge = (IRControlEdge)i.next();
            if (uf.find(@1.getReg(edge)) != reg)
              return false;
          }
          if (uf.find(@1.getReg()) != reg) {
            uf.union2(@1.getReg(), reg);
            return true;
          }
        }
        return false;
      }
      | IR.FPHI = {
        Iterator i = @1.edges();
        if (i.hasNext()) {
          IRControlEdge edge = (IRControlEdge)i.next();
          int reg = uf.find(@1.getReg(edge));
          while (i.hasNext()) {
            edge = (IRControlEdge)i.next();
            if (uf.find(@1.getReg(edge)) != reg)
              return false;
          }
          if (uf.find(@1.getReg()) != reg) {
            uf.union2(@1.getReg(), reg);
            return true;
          }
        }
        return false;
      }
      | IR.DPHI = {
        Iterator i = @1.edges();
        if (i.hasNext()) {
          IRControlEdge edge = (IRControlEdge)i.next();
          int reg = uf.find(@1.getReg(edge));
          while (i.hasNext()) {
            edge = (IRControlEdge)i.next();
            if (uf.find(@1.getReg(edge)) != reg)
              return false;
          }
          if (uf.find(@1.getReg()) != reg) {
            uf.union2(@1.getReg(), reg);
            return true;
          }
        }
        return false;
      }
      | IR.APHI = {
        Iterator i = @1.edges();
        if (i.hasNext()) {
          IRControlEdge edge = (IRControlEdge)i.next();
          int reg = uf.find(@1.getReg(edge));
          while (i.hasNext()) {
            edge = (IRControlEdge)i.next();
            if (uf.find(@1.getReg(edge)) != reg)
              return false;
          }
          if (uf.find(@1.getReg()) != reg) {
            uf.union2(@1.getReg(), reg);
            return true;
          }
        }
        return false;
      }
      | default = { return false; }
      ;

    public void replace(UnionFind uf)
      : IR.IUSE = { @1.setReg(uf.find(@1.getReg())); }
      | IR.LUSE = { @1.setReg(uf.find(@1.getReg())); }
      | IR.FUSE = { @1.setReg(uf.find(@1.getReg())); }
      | IR.DUSE = { @1.setReg(uf.find(@1.getReg())); }
      | IR.AUSE = { @1.setReg(uf.find(@1.getReg())); }
      | IR.IPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, uf.find(@1.getReg(edge))); 
        }
      }
      | IR.LPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, uf.find(@1.getReg(edge))); 
        }
      }
      | IR.FPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, uf.find(@1.getReg(edge))); 
        }
      }
      | IR.DPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, uf.find(@1.getReg(edge))); 
        }
      }
      | IR.APHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, uf.find(@1.getReg(edge))); 
        }
      }
      | default = {
        if (  left$ != null)   left$.replace(uf);
        if (middle$ != null) middle$.replace(uf);
        if ( right$ != null)  right$.replace(uf);
      }
      ;

  }

  private static final class UnionFind {

    private final int[] values;

    public UnionFind(int size) {
      if (size < 0)
        throw new IllegalArgumentException();
      values = new int[size];
      for (int i = 0; i < values.length; i++)
        values[i] = i;
    }

    public int find(int value) {
      if (value >= values.length)
        throw new IllegalArgumentException();
      while (values[value] != value)
        value = values[value];
      return value;
    }

    public int union(int value1, int value2) {
      if (value1 >= values.length || value2 >= values.length)
        throw new IllegalArgumentException();
      int depth1 = 0;
      while (values[value1] != value1) {
        value1 = values[value1];
        depth1++;
      }
      int depth2 = 0;
      while (values[value2] != value2) {
        value2 = values[value2];
        depth2++;
      }
      if (depth2 > depth1) {
        int value = value1;
        value1 = value2;
        value2 = value;
      }
      values[value2] = value1;
      return value1;
    }

    public int union1(int value1, int value2) {
      value1 = find(value1);
      value2 = find(value2);
      values[value2] = value1;
      return value1;
    }

    public int union2(int value1, int value2) {
      value1 = find(value1);
      value2 = find(value2);
      values[value1] = value2;
      return value2;
    }

  }

}

