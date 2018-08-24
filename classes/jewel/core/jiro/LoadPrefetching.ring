/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.optimize.Optimization;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.BitFunction;
import jewel.core.jiro.dataflow.DataFlowAnalyser;
import jewel.core.jiro.dataflow.DataFlowAnalysis;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowFunction;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowItem;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public final class LoadPrefetching implements Optimization {

  public LoadPrefetching() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;
    if (cfg.isSSA()) {

      HashSet set = new HashSet();
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.addLoad(set);
      }

      if (set.size() > 0) {

        Load[] loads = (Load[])set.toArray(new Load[set.size()]);
        Arrays.sort(loads);

        HashMap kills = new HashMap();
        for (int i = 0; i < loads.length; i++) {
          Load load = loads[i];
          Object key = load.newKillKey();
          BitSet kill = (BitSet)kills.get(key);
          if (kill == null) {
            kill = new BitSet(loads.length);
            kills.put(key, kill);
          }
          kill.set(i);
        }
        BitSet kill = new BitSet(loads.length);
        for (int i = 0; i < loads.length; i++)
          kill.set(i);
        kills.put(null, kill);

        DataFlowAnalyser analyser = new IterativeDataFlowAnalyser(new Analysis(loads, kills));
        ArrayList mods = new ArrayList();
        for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
          IRStatement stmt = (IRStatement)i.next();
          Matcher matcher = new Matcher(stmt);
          BitItem value = (BitItem)analyser.valueBefore(stmt);
          matcher.modify(loads, value, mods);
        }

        changed = mods.size() > 0;
        for (Iterator i = mods.iterator(); i.hasNext(); ) {
          Runnable r = (Runnable)i.next();
          r.run();
        }

      }

    }

    return changed;
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void addLoad(HashSet set)
      : IR.IDEFINE(IR.BLOAD(IR.ACLASS)) = { set.add(new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'B')); }
      | IR.IDEFINE(IR.SLOAD(IR.ACLASS)) = { set.add(new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'S')); }
      | IR.IDEFINE(IR.ILOAD(IR.ACLASS)) = { set.add(new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'I')); }
      | IR.LDEFINE(IR.LLOAD(IR.ACLASS)) = { set.add(new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'L')); }
      | IR.FDEFINE(IR.FLOAD(IR.ACLASS)) = { set.add(new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'F')); }
      | IR.DDEFINE(IR.DLOAD(IR.ACLASS)) = { set.add(new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'D')); }
      | IR.ADEFINE(IR.ALOAD(IR.ACLASS)) = { set.add(new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'A')); }
      | IR.BSTORE(IR.ACLASS,IR.IUSE) = { set.add(new SLoad(@3.getReg(), @2.getSymbolicType(), @1.getOffset(), 'B')); }
      | IR.SSTORE(IR.ACLASS,IR.IUSE) = { set.add(new SLoad(@3.getReg(), @2.getSymbolicType(), @1.getOffset(), 'S')); }
      | IR.ISTORE(IR.ACLASS,IR.IUSE) = { set.add(new SLoad(@3.getReg(), @2.getSymbolicType(), @1.getOffset(), 'I')); }
      | IR.LSTORE(IR.ACLASS,IR.LUSE) = { set.add(new SLoad(@3.getReg(), @2.getSymbolicType(), @1.getOffset(), 'L')); }
      | IR.FSTORE(IR.ACLASS,IR.FUSE) = { set.add(new SLoad(@3.getReg(), @2.getSymbolicType(), @1.getOffset(), 'F')); }
      | IR.DSTORE(IR.ACLASS,IR.DUSE) = { set.add(new SLoad(@3.getReg(), @2.getSymbolicType(), @1.getOffset(), 'D')); }
      | IR.ASTORE(IR.ACLASS,IR.AUSE) = { set.add(new SLoad(@3.getReg(), @2.getSymbolicType(), @1.getOffset(), 'A')); }
      | IR.IDEFINE(IR.BLOAD(IR.AUSE)) = { set.add(new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'B')); }
      | IR.IDEFINE(IR.SLOAD(IR.AUSE)) = { set.add(new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'S')); }
      | IR.IDEFINE(IR.ILOAD(IR.AUSE)) = { set.add(new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'I')); }
      | IR.LDEFINE(IR.LLOAD(IR.AUSE)) = { set.add(new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'L')); }
      | IR.FDEFINE(IR.FLOAD(IR.AUSE)) = { set.add(new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'F')); }
      | IR.DDEFINE(IR.DLOAD(IR.AUSE)) = { set.add(new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'D')); }
      | IR.ADEFINE(IR.ALOAD(IR.AUSE)) = { set.add(new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'A')); }
      | IR.BSTORE(IR.AUSE,IR.IUSE) = { set.add(new ILoad(@3.getReg(), @2.getReg(), @1.getOffset(), 'B')); }
      | IR.SSTORE(IR.AUSE,IR.IUSE) = { set.add(new ILoad(@3.getReg(), @2.getReg(), @1.getOffset(), 'S')); }
      | IR.ISTORE(IR.AUSE,IR.IUSE) = { set.add(new ILoad(@3.getReg(), @2.getReg(), @1.getOffset(), 'I')); }
      | IR.LSTORE(IR.AUSE,IR.LUSE) = { set.add(new ILoad(@3.getReg(), @2.getReg(), @1.getOffset(), 'L')); }
      | IR.FSTORE(IR.AUSE,IR.FUSE) = { set.add(new ILoad(@3.getReg(), @2.getReg(), @1.getOffset(), 'F')); }
      | IR.DSTORE(IR.AUSE,IR.DUSE) = { set.add(new ILoad(@3.getReg(), @2.getReg(), @1.getOffset(), 'D')); }
      | IR.ASTORE(IR.AUSE,IR.AUSE) = { set.add(new ILoad(@3.getReg(), @2.getReg(), @1.getOffset(), 'A')); }
      | IR.IDEFINE(IR.BALOAD(IR.AUSE,IR.IUSE)) = { set.add(new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'B')); }
      | IR.IDEFINE(IR.SALOAD(IR.AUSE,IR.IUSE)) = { set.add(new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'S')); }
      | IR.IDEFINE(IR.IALOAD(IR.AUSE,IR.IUSE)) = { set.add(new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'I')); }
      | IR.LDEFINE(IR.LALOAD(IR.AUSE,IR.IUSE)) = { set.add(new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'L')); }
      | IR.FDEFINE(IR.FALOAD(IR.AUSE,IR.IUSE)) = { set.add(new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'F')); }
      | IR.DDEFINE(IR.DALOAD(IR.AUSE,IR.IUSE)) = { set.add(new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'D')); }
      | IR.ADEFINE(IR.AALOAD(IR.AUSE,IR.IUSE)) = { set.add(new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'A')); }
      | IR.BASTORE(IR.AUSE,IR.IUSE,IR.IUSE) = { set.add(new ALoad(@4.getReg(), @2.getReg(), @3.getReg(), 'B')); }
      | IR.SASTORE(IR.AUSE,IR.IUSE,IR.IUSE) = { set.add(new ALoad(@4.getReg(), @2.getReg(), @3.getReg(), 'S')); }
      | IR.IASTORE(IR.AUSE,IR.IUSE,IR.IUSE) = { set.add(new ALoad(@4.getReg(), @2.getReg(), @3.getReg(), 'I')); }
      | IR.LASTORE(IR.AUSE,IR.IUSE,IR.LUSE) = { set.add(new ALoad(@4.getReg(), @2.getReg(), @3.getReg(), 'L')); }
      | IR.FASTORE(IR.AUSE,IR.IUSE,IR.FUSE) = { set.add(new ALoad(@4.getReg(), @2.getReg(), @3.getReg(), 'F')); }
      | IR.DASTORE(IR.AUSE,IR.IUSE,IR.DUSE) = { set.add(new ALoad(@4.getReg(), @2.getReg(), @3.getReg(), 'D')); }
      | IR.AASTORE(IR.AUSE,IR.IUSE,IR.AUSE) = { set.add(new ALoad(@4.getReg(), @2.getReg(), @3.getReg(), 'A')); }
      | default = { }
      ;

    public void modelEffect(Load[] loads, HashMap kills, BitFunction function)
      : IR.IDEFINE(IR.BLOAD(IR.ACLASS)) = { load(loads, function, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'B')); }
      | IR.IDEFINE(IR.SLOAD(IR.ACLASS)) = { load(loads, function, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'S')); }
      | IR.IDEFINE(IR.ILOAD(IR.ACLASS)) = { load(loads, function, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'I')); }
      | IR.LDEFINE(IR.LLOAD(IR.ACLASS)) = { load(loads, function, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'L')); }
      | IR.FDEFINE(IR.FLOAD(IR.ACLASS)) = { load(loads, function, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'F')); }
      | IR.DDEFINE(IR.DLOAD(IR.ACLASS)) = { load(loads, function, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'D')); }
      | IR.ADEFINE(IR.ALOAD(IR.ACLASS)) = { load(loads, function, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'A')); }
      | IR.BSTORE(IR.ACLASS,any) = { store(loads, kills, function, new SLoad(@3(), @2.getSymbolicType(), @1.getOffset(), 'B')); }
      | IR.SSTORE(IR.ACLASS,any) = { store(loads, kills, function, new SLoad(@3(), @2.getSymbolicType(), @1.getOffset(), 'S')); }
      | IR.ISTORE(IR.ACLASS,any) = { store(loads, kills, function, new SLoad(@3(), @2.getSymbolicType(), @1.getOffset(), 'I')); }
      | IR.LSTORE(IR.ACLASS,any) = { store(loads, kills, function, new SLoad(@3(), @2.getSymbolicType(), @1.getOffset(), 'L')); }
      | IR.FSTORE(IR.ACLASS,any) = { store(loads, kills, function, new SLoad(@3(), @2.getSymbolicType(), @1.getOffset(), 'F')); }
      | IR.DSTORE(IR.ACLASS,any) = { store(loads, kills, function, new SLoad(@3(), @2.getSymbolicType(), @1.getOffset(), 'D')); }
      | IR.ASTORE(IR.ACLASS,any) = { store(loads, kills, function, new SLoad(@3(), @2.getSymbolicType(), @1.getOffset(), 'A')); }
      | IR.IDEFINE(IR.BLOAD(IR.AUSE)) = { load(loads, function, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'B')); }
      | IR.IDEFINE(IR.SLOAD(IR.AUSE)) = { load(loads, function, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'S')); }
      | IR.IDEFINE(IR.ILOAD(IR.AUSE)) = { load(loads, function, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'I')); }
      | IR.LDEFINE(IR.LLOAD(IR.AUSE)) = { load(loads, function, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'L')); }
      | IR.FDEFINE(IR.FLOAD(IR.AUSE)) = { load(loads, function, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'F')); }
      | IR.DDEFINE(IR.DLOAD(IR.AUSE)) = { load(loads, function, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'D')); }
      | IR.ADEFINE(IR.ALOAD(IR.AUSE)) = { load(loads, function, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'A')); }
      | IR.BSTORE(reg,any) = { store(loads, kills, function, new ILoad(@3(), @2(), @1.getOffset(), 'B')); }
      | IR.SSTORE(reg,any) = { store(loads, kills, function, new ILoad(@3(), @2(), @1.getOffset(), 'S')); }
      | IR.ISTORE(reg,any) = { store(loads, kills, function, new ILoad(@3(), @2(), @1.getOffset(), 'I')); }
      | IR.LSTORE(reg,any) = { store(loads, kills, function, new ILoad(@3(), @2(), @1.getOffset(), 'L')); }
      | IR.FSTORE(reg,any) = { store(loads, kills, function, new ILoad(@3(), @2(), @1.getOffset(), 'F')); }
      | IR.DSTORE(reg,any) = { store(loads, kills, function, new ILoad(@3(), @2(), @1.getOffset(), 'D')); }
      | IR.ASTORE(reg,any) = { store(loads, kills, function, new ILoad(@3(), @2(), @1.getOffset(), 'A')); }
      | IR.IDEFINE(IR.BALOAD(IR.AUSE,IR.IUSE)) = { load(loads, function, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'B')); }
      | IR.IDEFINE(IR.SALOAD(IR.AUSE,IR.IUSE)) = { load(loads, function, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'S')); }
      | IR.IDEFINE(IR.IALOAD(IR.AUSE,IR.IUSE)) = { load(loads, function, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'I')); }
      | IR.LDEFINE(IR.LALOAD(IR.AUSE,IR.IUSE)) = { load(loads, function, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'L')); }
      | IR.FDEFINE(IR.FALOAD(IR.AUSE,IR.IUSE)) = { load(loads, function, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'F')); }
      | IR.DDEFINE(IR.DALOAD(IR.AUSE,IR.IUSE)) = { load(loads, function, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'D')); }
      | IR.ADEFINE(IR.AALOAD(IR.AUSE,IR.IUSE)) = { load(loads, function, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'A')); }
      | IR.BASTORE(any,any,any) = { store(loads, kills, function, new ALoad(@4(), @2(), @3(), 'B')); }
      | IR.SASTORE(any,any,any) = { store(loads, kills, function, new ALoad(@4(), @2(), @3(), 'S')); }
      | IR.IASTORE(any,any,any) = { store(loads, kills, function, new ALoad(@4(), @2(), @3(), 'I')); }
      | IR.LASTORE(any,any,any) = { store(loads, kills, function, new ALoad(@4(), @2(), @3(), 'L')); }
      | IR.FASTORE(any,any,any) = { store(loads, kills, function, new ALoad(@4(), @2(), @3(), 'F')); }
      | IR.DASTORE(any,any,any) = { store(loads, kills, function, new ALoad(@4(), @2(), @3(), 'D')); }
      | IR.AASTORE(any,any,any) = { store(loads, kills, function, new ALoad(@4(), @2(), @3(), 'A')); }
      | IR.CALL(any) = { kill(kills, function); }
      | IR.CALLX(any) = { kill(kills, function); }
      | IR.NCALL = { kill(kills, function); }
      | IR.NCALLX = { kill(kills, function); }
      | IR.INIT(any) = { kill(kills, function); }
      | IR.INITX(any) = { kill(kills, function); }
      | IR.READBARRIER = { kill(kills, function); }
      | default = { }
      ;

    private int reg()
      : IR.IUSE = { return @1.getReg(); }
      | IR.LUSE = { return @1.getReg(); }
      | IR.FUSE = { return @1.getReg(); }
      | IR.DUSE = { return @1.getReg(); }
      | IR.AUSE = { return @1.getReg(); }
      ;

    private int any()
      : reg = { return @1(); }
      | default = { return -1; }
      ;

    private static void load(Load[] loads, BitFunction function, Load load) {
      int index = Arrays.binarySearch(loads, load);
      function.set(index, BitFunction.GEN);
    }

    private static void store(Load[] loads, HashMap kills, BitFunction function, Load load) {
      BitSet kill = (BitSet)kills.get(load.newKillKey());
      if (kill != null)
        function.set(kill, BitFunction.KILL);
      if (load.isPrecise())
        load(loads, function, load);
    }

    private static void kill(HashMap kills, BitFunction function) {
      BitSet kill = (BitSet)kills.get(null);
      function.set(kill, BitFunction.KILL);
    }

    public void modify(Load[] loads, BitItem value, ArrayList mods)
      : IR.IDEFINE(IR.BLOAD(IR.ACLASS)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'B'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.iuse(source));
            }
          });
      }
      | IR.IDEFINE(IR.SLOAD(IR.ACLASS)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'S'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.iuse(source));
            }
          });
      }
      | IR.IDEFINE(IR.ILOAD(IR.ACLASS)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'I'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.iuse(source));
            }
          });
      }
      | IR.LDEFINE(IR.LLOAD(IR.ACLASS)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'L'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.luse(source));
            }
          });
      }
      | IR.FDEFINE(IR.FLOAD(IR.ACLASS)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'F'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.fuse(source));
            }
          });
      }
      | IR.DDEFINE(IR.DLOAD(IR.ACLASS)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'D'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.duse(source));
            }
          });
      }
      | IR.ADEFINE(IR.ALOAD(IR.ACLASS)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new SLoad(@1.getReg(), @3.getSymbolicType(), @2.getOffset(), 'A'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.ause(source));
            }
          });
      }
      | IR.IDEFINE(IR.BLOAD(IR.AUSE)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'B'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.iuse(source));
            }
          });
      }
      | IR.IDEFINE(IR.SLOAD(IR.AUSE)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'S'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.iuse(source));
            }
          });
      }
      | IR.IDEFINE(IR.ILOAD(IR.AUSE)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'I'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.iuse(source));
            }
          });
      }
      | IR.LDEFINE(IR.LLOAD(IR.AUSE)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'L'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.luse(source));
            }
          });
      }
      | IR.FDEFINE(IR.FLOAD(IR.AUSE)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'F'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.fuse(source));
            }
          });
      }
      | IR.DDEFINE(IR.DLOAD(IR.AUSE)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'D'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.duse(source));
            }
          });
      }
      | IR.ADEFINE(IR.ALOAD(IR.AUSE)) [!@2.isVolatile()] = {
        final int source = search(loads, value, new ILoad(@1.getReg(), @3.getReg(), @2.getOffset(), 'A'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.ause(source));
            }
          });
      }
      | IR.IDEFINE(IR.BALOAD(IR.AUSE,IR.IUSE)) = {
        final int source = search(loads, value, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'B'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.iuse(source));
            }
          });
      }
      | IR.IDEFINE(IR.SALOAD(IR.AUSE,IR.IUSE)) = {
        final int source = search(loads, value, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'S'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.iuse(source));
            }
          });
      }
      | IR.IDEFINE(IR.IALOAD(IR.AUSE,IR.IUSE)) = {
        final int source = search(loads, value, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'I'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.iuse(source));
            }
          });
      }
      | IR.LDEFINE(IR.LALOAD(IR.AUSE,IR.IUSE)) = {
        final int source = search(loads, value, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'L'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.luse(source));
            }
          });
      }
      | IR.FDEFINE(IR.FALOAD(IR.AUSE,IR.IUSE)) = {
        final int source = search(loads, value, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'F'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.fuse(source));
            }
          });
      }
      | IR.DDEFINE(IR.DALOAD(IR.AUSE,IR.IUSE)) = {
        final int source = search(loads, value, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'D'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.duse(source));
            }
          });
      }
      | IR.ADEFINE(IR.AALOAD(IR.AUSE,IR.IUSE)) = {
        final int source = search(loads, value, new ALoad(@1.getReg(), @3.getReg(), @4.getReg(), 'A'));
        if (source != -1)
          mods.add(new Runnable() {
            public void run() {
              @1.setLeft(new IR.ause(source));
            }
          });
      }
      | default = { }
      ;

    private static int search(Load[] loads, BitItem value, Load load) {
      for (int i = 0; i < loads.length; i++)
        if (value.get(i)) {
          Load reachingLoad = loads[i];
          if (load.equalsNoTarget(reachingLoad))
            return reachingLoad.target;
        }
      return -1;
    }

  }

  private static final class Analysis implements DataFlowAnalysis {

    private final Load[] loads;
    private final HashMap kills;

    public Analysis(Load[] loads, HashMap kills) {
      this.loads = loads;
      this.kills = kills;
    }

    public byte direction() {
      return FORWARD;
    }

    public FlowItem newFlowItem() {
      return new BitItem(loads.length);
    }

    public FlowFunction newFlowFunction() {
      return new BitFunction(loads.length);
    }

    public void modelEffect(FlowFunction function, Statement stmt) {
      Matcher matcher = new Matcher((IRStatement)stmt);
      matcher.modelEffect(loads, kills, (BitFunction)function);
    }

    public void modelEffect(FlowFunction function, ControlEdge edge) { }

    public FlowItem merge(FlowItem one, FlowItem another) {
      BitItem result = (BitItem)one.clone();
      result.and((BitItem)another);
      return result;
    }

  }

  private static abstract class Load implements Comparable {

    public final int target;
    public final char type;

    protected Load(int target, char type) {
      this.target = target;
      this.type = type;
    }

    public Object newKillKey() {
      return new String(new char[]{ type });
    }

    public boolean isPrecise() {
      return target != -1;
    }

    public boolean equalsNoTarget(Load load) {
      return type == load.type;
    }
    
    protected int compareTo(Load load) {
      return target < load.target ? -1 : target > load.target ? 1
           : type   < load.type   ? -1 : type   > load.type   ? 1
           : 0;
    }
  
    public final int compareTo(Object object) {
      return compareTo((Load)object);
    }
  
    public int hashCode() {
      return target+type;
    }

    public boolean equals(Object object) {
      return object instanceof Load
          && ((Load)object).target == target
          && ((Load)object).type   == type;
    }

  }

  private static final class SLoad extends Load implements Comparable {

    public final String name;
    public final long offset;

    public SLoad(int target, String name, long offset, char type) {
      super(target, type);
      this.name = name;
      this.offset = offset;
    }

    public Object newKillKey() {
      return new String(new char[]{ type,
        (char)(offset>>48), (char)(offset>>32),
        (char)(offset>>16), (char)(offset    ),
      })+name;
    }

    public boolean equalsNoTarget(Load load) {
      return load instanceof SLoad
          && ((SLoad)load).offset == offset
          && ((SLoad)load).name.equals(name)
          && super.equalsNoTarget(load);
    }

    protected int compareTo(Load load) {
      if (!(load instanceof SLoad))
        return getClass().hashCode() < load.getClass().hashCode() ? -1 : 1;
      int result = name.compareTo(((SLoad)load).name);
      if (result != 0)
        return result;
      return offset < ((SLoad)load).offset ? -1 : offset > ((SLoad)load).offset ? 1
           : super.compareTo(load);
    }
  
    public int hashCode() {
      return name.hashCode() ^ ~super.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof SLoad
          && ((SLoad)object).offset == offset
          && ((SLoad)object).name.equals(name)
          && super.equals(object);
    }

  }

  private static final class ILoad extends Load implements Comparable {

    public final int source;
    public final long offset;

    public ILoad(int target, int source, long offset, char type) {
      super(target, type);
      this.source = source;
      this.offset = offset;
    }

    public Object newKillKey() {
      return new String(new char[]{ type,
        (char)(offset>>48), (char)(offset>>32),
        (char)(offset>>16), (char)(offset    ),
      });
    }

    public boolean isPrecise() {
      return source != -1
          && super.isPrecise();
    }

    public boolean equalsNoTarget(Load load) {
      return load instanceof ILoad
          && ((ILoad)load).source == source
          && ((ILoad)load).offset == offset
          && super.equalsNoTarget(load);
    }

    protected int compareTo(Load load) {
      if (!(load instanceof ILoad))
        return getClass().hashCode() < load.getClass().hashCode() ? -1 : 1;
      return source < ((ILoad)load).source ? -1 : source > ((ILoad)load).source ? 1
           : offset < ((ILoad)load).offset ? -1 : offset > ((ILoad)load).offset ? 1
           : super.compareTo(load);
    }
  
    public int hashCode() {
      return source ^ ~super.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof ILoad
          && ((ILoad)object).source == source
          && ((ILoad)object).offset == offset
          && super.equals(object);
    }

  }

  private static final class ALoad extends Load implements Comparable {

    public final int base;
    public final int index;

    public ALoad(int target, int base, int index, char type) {
      super(target, type);
      this.base = base;
      this.index = index;
    }

    public boolean isPrecise() {
      return base != -1
          && index != -1
          && super.isPrecise();
    }

    public boolean equalsNoTarget(Load load) {
      return load instanceof ALoad
          && ((ALoad)load).base == base
          && ((ALoad)load).index == index
          && super.equalsNoTarget(load);
    }

    protected int compareTo(Load load) {
      if (!(load instanceof ALoad))
        return getClass().hashCode() < load.getClass().hashCode() ? -1 : 1;
      return base  < ((ALoad)load).base  ? -1 : base  > ((ALoad)load).base  ? 1
           : index < ((ALoad)load).index ? -1 : index > ((ALoad)load).index ? 1
           : super.compareTo(load);
    }
  
    public int hashCode() {
      return base*index ^ ~super.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof ALoad
          && ((ALoad)object).base == base
          && ((ALoad)object).index == index
          && super.equals(object);
    }

  }

}

