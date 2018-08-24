/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.BitSet;
import java.util.Iterator;

public final class RegisterPacking implements Optimization {

  public RegisterPacking() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    cfg.invalidate();
    int maxReg = cfg.regMax();

    BitSet used = new BitSet(maxReg);
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher matcher = new Matcher(stmt);
      matcher.cover(used);
    }

    int[] map = new int[maxReg];
    for (int i = 0; i < map.length; i++)
      if (used.get(i)) {
        map[i] = i;
        for (int j = i%5; j < i; j += 5)
          if (!used.get(j)) {
            used.set(j);
            used.clear(i);
            map[i] = j;
            changed = true;
            break;
          }
      }

    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher matcher = new Matcher(stmt);
      matcher.update(map);
    }

    cfg.invalidate();

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void cover(BitSet used)
      : IR.IRECEIVE = { used.set(@1.getReg()); }
      | IR.LRECEIVE = { used.set(@1.getReg()); }
      | IR.FRECEIVE = { used.set(@1.getReg()); }
      | IR.DRECEIVE = { used.set(@1.getReg()); }
      | IR.ARECEIVE = { used.set(@1.getReg()); }
      | IR.IRESULT = { used.set(@1.getReg()); }
      | IR.LRESULT = { used.set(@1.getReg()); }
      | IR.FRESULT = { used.set(@1.getReg()); }
      | IR.DRESULT = { used.set(@1.getReg()); }
      | IR.ARESULT = { used.set(@1.getReg()); }
      | IR.ACATCH = { used.set(@1.getReg()); }
      | IR.IDEFINE(cover) = { used.set(@1.getReg()); }
      | IR.LDEFINE(cover) = { used.set(@1.getReg()); }
      | IR.FDEFINE(cover) = { used.set(@1.getReg()); }
      | IR.DDEFINE(cover) = { used.set(@1.getReg()); }
      | IR.ADEFINE(cover) = { used.set(@1.getReg()); }
      | IR.IPHI = { used.set(@1.getReg()); }
      | IR.LPHI = { used.set(@1.getReg()); }
      | IR.FPHI = { used.set(@1.getReg()); }
      | IR.DPHI = { used.set(@1.getReg()); }
      | IR.APHI = { used.set(@1.getReg()); }
      | default = { }
      ;

    public void update(int[] map)
      : IR.IUSE = { @1.setReg(map[@1.getReg()]); }
      | IR.LUSE = { @1.setReg(map[@1.getReg()]); }
      | IR.FUSE = { @1.setReg(map[@1.getReg()]); }
      | IR.DUSE = { @1.setReg(map[@1.getReg()]); }
      | IR.AUSE = { @1.setReg(map[@1.getReg()]); }
      | IR.IRECEIVE = { @1.setReg(map[@1.getReg()]); }
      | IR.LRECEIVE = { @1.setReg(map[@1.getReg()]); }
      | IR.FRECEIVE = { @1.setReg(map[@1.getReg()]); }
      | IR.DRECEIVE = { @1.setReg(map[@1.getReg()]); }
      | IR.ARECEIVE = { @1.setReg(map[@1.getReg()]); }
      | IR.IRESULT = { @1.setReg(map[@1.getReg()]); }
      | IR.LRESULT = { @1.setReg(map[@1.getReg()]); }
      | IR.FRESULT = { @1.setReg(map[@1.getReg()]); }
      | IR.DRESULT = { @1.setReg(map[@1.getReg()]); }
      | IR.ARESULT = { @1.setReg(map[@1.getReg()]); }
      | IR.ACATCH = { @1.setReg(map[@1.getReg()]); }
      | IR.IDEFINE(update) = { @2(map); @1.setReg(map[@1.getReg()]); }
      | IR.LDEFINE(update) = { @2(map); @1.setReg(map[@1.getReg()]); }
      | IR.FDEFINE(update) = { @2(map); @1.setReg(map[@1.getReg()]); }
      | IR.DDEFINE(update) = { @2(map); @1.setReg(map[@1.getReg()]); }
      | IR.ADEFINE(update) = { @2(map); @1.setReg(map[@1.getReg()]); }
      | IR.IPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, map[@1.getReg(edge)]);
        }
        @1.setReg(map[@1.getReg()]);
      }
      | IR.LPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, map[@1.getReg(edge)]);
        }
        @1.setReg(map[@1.getReg()]); 
      }
      | IR.FPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, map[@1.getReg(edge)]);
        }
        @1.setReg(map[@1.getReg()]);
      }
      | IR.DPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, map[@1.getReg(edge)]);
        }
        @1.setReg(map[@1.getReg()]);
      }
      | IR.APHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, map[@1.getReg(edge)]);
        }
        @1.setReg(map[@1.getReg()]);
      }
      | default = {
        if (  left$ != null)   left$.update(map);
        if (middle$ != null) middle$.update(map);
        if ( right$ != null)  right$.update(map);
      }
      ;

  }

}

