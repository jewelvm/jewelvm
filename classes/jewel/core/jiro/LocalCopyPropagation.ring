/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;

//has some bugs! (check uses and defs special cases)
public final class LocalCopyPropagation implements Optimization {

  public LocalCopyPropagation() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;
    cfg.invalidate();
    int regMax = cfg.regMax();

    cfg.printTo(System.err);
    cfg.show();

    BitSet global = new BitSet(regMax);
    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      BitSet defined = new BitSet();
      for (Iterator j = currentBB.topdownStmts(); j.hasNext(); ) {
        IRStatement stmt = (IRStatement)j.next();
        Matcher matcher = new Matcher(stmt);
        matcher.global(global, defined);
      }
    }

    for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      UnionFind uf = new UnionFind(regMax);
      for (Iterator j = currentBB.topdownStmts(); j.hasNext(); ) {
        IRStatement stmt = (IRStatement)j.next();
        Matcher matcher = new Matcher(stmt);
        if (matcher.forward(global, uf)) {
          j.remove();
          changed = true;
        }
      }
    }

    for (Iterator i = cfg.bottomupBBs(); i.hasNext(); ) {
      BasicBlock currentBB = (BasicBlock)i.next();
      UnionFind uf = new UnionFind(regMax);
      for (Iterator j = currentBB.bottomupStmts(); j.hasNext(); ) {
        IRStatement stmt = (IRStatement)j.next();
        Matcher matcher = new Matcher(stmt);
        if (matcher.backward(global, uf)) {
          j.remove();
          changed = true;
        }
      }
    }

    cfg.printTo(System.err);
    cfg.show();

    return changed;
  }

  public static final class UnionFind {

    private final int[] values;

    public UnionFind(int size) {
      if (size < 0)
        throw new IllegalArgumentException();
      values = new int[size];
      for (int i = 0; i < values.length; i++)
        values[i] = i;
    }

    public int find(int value) {
      if (value < 0 || value >= values.length)
        throw new IllegalArgumentException();
      if (values[value] == value)
        return value;
      return values[value] = find(values[value]);
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

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void global(BitSet global, BitSet defined)
      : IR.IUSE = {
        int reg = @1.getReg();
        if (!defined.get(reg))
          global.set(reg);
      }
      | IR.LUSE = {
        int reg = @1.getReg();
        if (!defined.get(reg))
          global.set(reg);
      }
      | IR.FUSE = {
        int reg = @1.getReg();
        if (!defined.get(reg))
          global.set(reg);
      }
      | IR.DUSE = {
        int reg = @1.getReg();
        if (!defined.get(reg))
          global.set(reg);
      }
      | IR.AUSE = {
        int reg = @1.getReg();
        if (!defined.get(reg))
          global.set(reg);
      }
      | IR.IRECEIVE = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.LRECEIVE = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.FRECEIVE = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.DRECEIVE = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.ARECEIVE = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.IRESULT = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.LRESULT = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.FRESULT = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.DRESULT = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.ARESULT = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.ACATCH = {
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.IDEFINE(global) = {
        @2(global, defined);
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.LDEFINE(global) = {
        @2(global, defined);
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.FDEFINE(global) = {
        @2(global, defined);
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.DDEFINE(global) = {
        @2(global, defined);
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.ADEFINE(global) = {
        @2(global, defined);
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.IPHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          int reg = ((Integer)i.next()).intValue();
          global.set(reg);
        }
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.LPHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          int reg = ((Integer)i.next()).intValue();
          global.set(reg);
        }
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.FPHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          int reg = ((Integer)i.next()).intValue();
          global.set(reg);
        }
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.DPHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          int reg = ((Integer)i.next()).intValue();
          global.set(reg);
        }
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | IR.APHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          int reg = ((Integer)i.next()).intValue();
          global.set(reg);
        }
        int reg = @1.getReg();
        if (defined.get(reg))
          global.set(reg);
        else
          defined.set(reg);
      }
      | default = {
        if (  left$ != null)   left$.global(global, defined);
        if (middle$ != null) middle$.global(global, defined);
        if ( right$ != null)  right$.global(global, defined);
      }
      ;

    public boolean forward(BitSet global, UnionFind uf)
      : IR.IDEFINE(use) = {
        int src = @2(uf);
        int tgt = @1.getReg();
        if (src != -1) {
          if (!global.get(tgt)) {
            uf.union2(tgt, src);
            return true;
          }
          if (!global.get(src))
            uf.union1(tgt, src);
        }
        return false;
      }
      | IR.LDEFINE(use) = {
        int src = @2(uf);
        int tgt = @1.getReg();
        if (src != -1) {
          if (!global.get(tgt)) {
            uf.union2(tgt, src);
            return true;
          }
          if (!global.get(src))
            uf.union1(tgt, src);
        }
        return false;
      }
      | IR.FDEFINE(use) = {
        int src = @2(uf);
        int tgt = @1.getReg();
        if (src != -1) {
          if (!global.get(tgt)) {
            uf.union2(tgt, src);
            return true;
          }
          if (!global.get(src))
            uf.union1(tgt, src);
        }
        return false;
      }
      | IR.DDEFINE(use) = {
        int src = @2(uf);
        int tgt = @1.getReg();
        if (src != -1) {
          if (!global.get(tgt)) {
            uf.union2(tgt, src);
            return true;
          }
          if (!global.get(src))
            uf.union1(tgt, src);
        }
        return false;
      }
      | IR.ADEFINE(use) = {
        int src = @2(uf);
        int tgt = @1.getReg();
        if (src != -1) {
          if (!global.get(tgt)) {
            uf.union2(tgt, src);
            return true;
          }
          if (!global.get(src))
            uf.union1(tgt, src);
        }
        return false;
      }
      | default = {
        if (  left$ != null)   left$.use(uf);
        if (middle$ != null) middle$.use(uf);
        if ( right$ != null)  right$.use(uf);
        return false;
      }
      ;

    public boolean backward(BitSet global, UnionFind uf)
      : IR.IRECEIVE = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.LRECEIVE = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.FRECEIVE = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.DRECEIVE = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.ARECEIVE = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.IRESULT = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.LRESULT = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.FRESULT = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.DRESULT = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.ARESULT = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.ACATCH = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.IPHI = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.LPHI = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.FPHI = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.DPHI = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.APHI = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return false;
      }
      | IR.IDEFINE(use) = {
        int src = @2(uf);
        int tgt = uf.find(@1.getReg());
        if (src != -1)
          if (!global.get(src)) {
            uf.union1(tgt, src);
            return true;
          }
        @1.setReg(tgt);
        return false;
      }
      | IR.LDEFINE(use) = {
        int src = @2(uf);
        int tgt = uf.find(@1.getReg());
        if (src != -1)
          if (!global.get(src)) {
            uf.union1(tgt, src);
            return true;
          }
        @1.setReg(tgt);
        return false;
      }
      | IR.FDEFINE(use) = {
        int src = @2(uf);
        int tgt = uf.find(@1.getReg());
        if (src != -1)
          if (!global.get(src)) {
            uf.union1(tgt, src);
            return true;
          }
        @1.setReg(tgt);
        return false;
      }
      | IR.DDEFINE(use) = {
        int src = @2(uf);
        int tgt = uf.find(@1.getReg());
        if (src != -1)
          if (!global.get(src)) {
            uf.union1(tgt, src);
            return true;
          }
        @1.setReg(tgt);
        return false;
      }
      | IR.ADEFINE(use) = {
        int src = @2(uf);
        int tgt = uf.find(@1.getReg());
        if (src != -1)
          if (!global.get(src)) {
            uf.union1(tgt, src);
            return true;
          }
        @1.setReg(tgt);
        return false;
      }
      | default = {
        if (  left$ != null)   left$.use(uf);
        if (middle$ != null) middle$.use(uf);
        if ( right$ != null)  right$.use(uf);
        return false;
      }
      ;

    private int use(UnionFind uf)
      : IR.IUSE = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return reg;
      }
      | IR.LUSE = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return reg;
      }
      | IR.FUSE = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return reg;
      }
      | IR.DUSE = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return reg;
      }
      | IR.AUSE = {
        int reg = uf.find(@1.getReg());
        @1.setReg(reg);
        return reg;
      }
      | default = {
        if (  left$ != null)   left$.use(uf);
        if (middle$ != null) middle$.use(uf);
        if ( right$ != null)  right$.use(uf);
        return -1; 
      }
      ;

  }

}

