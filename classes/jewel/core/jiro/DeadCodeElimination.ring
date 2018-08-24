/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.Context;
import jewel.core.ContextClassInfo;
import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.DataFlowAnalyser;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;
import jewel.core.jiro.optimize.Optimization;

import java.util.BitSet;
import java.util.Iterator;

/* improve, use dfs thru ud-chains to mark live */
public final class DeadCodeElimination implements Optimization {

  public static final class SSA implements Optimization {

    public SSA() { }

    public boolean applyTo(ControlFlowGraph _cfg) {
      boolean changed = false;

      IRCFG cfg = (IRCFG)_cfg;

      if (cfg.isSSA()) {

        cfg.invalidate();

        int[] defsites = new int[cfg.regMax()];
        Matcher2[] matchers = new Matcher2[cfg.stmtCount()];

        int index = 0;
        for (Iterator i = cfg.topdownStmts(); i.hasNext(); index++) {
          IRStatement stmt = (IRStatement)i.next();
          Matcher2 matcher = new Matcher2(stmt);
          int define = matcher.define();
          if (define != -1)
            defsites[define] = index;
          matchers[index] = matcher;
        }

        BitSet marked = new BitSet(cfg.stmtCount());
        index = 0;
        for (Iterator i = cfg.topdownStmts(); i.hasNext(); index++) {
          IRStatement stmt = (IRStatement)i.next();
          Matcher2 matcher = matchers[index];
          if (matcher.essencial())
            if (!marked.get(index)) {
              marked.set(index);
              matcher.mark(marked, defsites, matchers);
            }
        }

        index = 0;
        for (Iterator i = cfg.topdownStmts(); i.hasNext(); index++) {
          IRStatement stmt = (IRStatement)i.next();
          if (!marked.get(index)) {

            int op = stmt.snode().op();
            if (op == IR.NEWINSTANCEX || op == IR.NEWARRAYX)
              for (Iterator j = stmt.ownerBB().outEdges(); j.hasNext(); ) {
                IRControlEdge edge = (IRControlEdge)j.next();
                if (edge.type() == IRControlEdge.EXCEPT) {
                  j.remove();
                  break;
                }
              }

            i.remove();

            changed = true;
          }
        }

      }

      return changed;
    }

    private static final class Matcher2 {

      public Matcher2(IRStatement stmt) {
        this(stmt.snode());
      }

      public int define()
        : IR.IRECEIVE = { return @1.getReg(); }
        | IR.LRECEIVE = { return @1.getReg(); }
        | IR.FRECEIVE = { return @1.getReg(); }
        | IR.DRECEIVE = { return @1.getReg(); }
        | IR.ARECEIVE = { return @1.getReg(); }
        | IR.IRESULT = { return @1.getReg(); }
        | IR.LRESULT = { return @1.getReg(); }
        | IR.FRESULT = { return @1.getReg(); }
        | IR.DRESULT = { return @1.getReg(); }
        | IR.ARESULT = { return @1.getReg(); }
        | IR.ACATCH = { return @1.getReg(); }
        | IR.IDEFINE(define) = { return @1.getReg(); }
        | IR.LDEFINE(define) = { return @1.getReg(); }
        | IR.FDEFINE(define) = { return @1.getReg(); }
        | IR.DDEFINE(define) = { return @1.getReg(); }
        | IR.ADEFINE(define) = { return @1.getReg(); }
        | IR.IPHI = { return @1.getReg(); }
        | IR.LPHI = { return @1.getReg(); }
        | IR.FPHI = { return @1.getReg(); }
        | IR.DPHI = { return @1.getReg(); }
        | IR.APHI = { return @1.getReg(); }
        | default = { return -1; }
        ;

      public boolean essencial()
        : IR.IRESULT = { return false; }
        | IR.LRESULT = { return false; }
        | IR.FRESULT = { return false; }
        | IR.DRESULT = { return false; }
        | IR.ARESULT = { return false; }
        | IR.ACATCH = { return false; }
        | IR.IDEFINE(essencial) = { return false; }
        | IR.LDEFINE(essencial) = { return false; }
        | IR.FDEFINE(essencial) = { return false; }
        | IR.DDEFINE(essencial) = { return false; }
        | IR.ADEFINE(essencial) = { return false; }
        | IR.IPHI = { return false; }
        | IR.LPHI = { return false; }
        | IR.FPHI = { return false; }
        | IR.DPHI = { return false; }
        | IR.APHI = { return false; }
        | IR.NEWINSTANCE(essencial) = {
          IR.aclass aclass = (IR.aclass)@1.left();
          Context context = Context.get();
          ContextClassInfo clazz = context.forName(aclass.getSymbolicType());
          if (clazz != null)
            if (!clazz.finalizes())
              return false;
          return true;
        }
        | IR.NEWINSTANCEX(essencial) = {
          IR.aclass aclass = (IR.aclass)@1.left();
          Context context = Context.get();
          ContextClassInfo clazz = context.forName(aclass.getSymbolicType());
          if (clazz != null)
            if (!clazz.finalizes())
              return false;
          return true;
        }
        | IR.NEWARRAY(essencial, essencial) = {
          IR.aclass aclass = (IR.aclass)@1.left();
          Context context = Context.get();
          ContextClassInfo clazz = context.forName(aclass.getSymbolicType());
          if (clazz != null)
            if (!clazz.finalizes())
              return false;
          return true;
        }
        | IR.NEWARRAYX(essencial, essencial) = {
          IR.aclass aclass = (IR.aclass)@1.left();
          Context context = Context.get();
          ContextClassInfo clazz = context.forName(aclass.getSymbolicType());
          if (clazz != null)
            if (!clazz.finalizes())
              return false;
          return true;
        }
        | default = { return true; }
        ;

      public void mark(BitSet marked, int[] defsites, Matcher2[] matchers)
        : IR.IUSE = {
          int index = defsites[@1.getReg()];
          if (!marked.get(index)) {
            marked.set(index);
            Matcher2 matcher = matchers[index];
            matcher.mark(marked, defsites, matchers);
          }
        }
        | IR.LUSE = {
          int index = defsites[@1.getReg()];
          if (!marked.get(index)) {
            marked.set(index);
            Matcher2 matcher = matchers[index];
            matcher.mark(marked, defsites, matchers);
          }
        }
        | IR.FUSE = {
          int index = defsites[@1.getReg()];
          if (!marked.get(index)) {
            marked.set(index);
            Matcher2 matcher = matchers[index];
            matcher.mark(marked, defsites, matchers);
          }
        }
        | IR.DUSE = {
          int index = defsites[@1.getReg()];
          if (!marked.get(index)) {
            marked.set(index);
            Matcher2 matcher = matchers[index];
            matcher.mark(marked, defsites, matchers);
          }
        }
        | IR.AUSE = {
          int index = defsites[@1.getReg()];
          if (!marked.get(index)) {
            marked.set(index);
            Matcher2 matcher = matchers[index];
            matcher.mark(marked, defsites, matchers);
          }
        }
        | IR.IPHI = {
          for (Iterator i = @1.regs(); i.hasNext(); ) {
            Integer intg = (Integer)i.next();
            int index = defsites[intg.intValue()];
            if (!marked.get(index)) {
              marked.set(index);
              Matcher2 matcher = matchers[index];
              matcher.mark(marked, defsites, matchers);
            }
          }
        }
        | IR.LPHI = {
          for (Iterator i = @1.regs(); i.hasNext(); ) {
            Integer intg = (Integer)i.next();
            int index = defsites[intg.intValue()];
            if (!marked.get(index)) {
              marked.set(index);
              Matcher2 matcher = matchers[index];
              matcher.mark(marked, defsites, matchers);
            }
          }
        }
        | IR.FPHI = {
          for (Iterator i = @1.regs(); i.hasNext(); ) {
            Integer intg = (Integer)i.next();
            int index = defsites[intg.intValue()];
            if (!marked.get(index)) {
              marked.set(index);
              Matcher2 matcher = matchers[index];
              matcher.mark(marked, defsites, matchers);
            }
          }
        }
        | IR.DPHI = {
          for (Iterator i = @1.regs(); i.hasNext(); ) {
            Integer intg = (Integer)i.next();
            int index = defsites[intg.intValue()];
            if (!marked.get(index)) {
              marked.set(index);
              Matcher2 matcher = matchers[index];
              matcher.mark(marked, defsites, matchers);
            }
          }
        }
        | IR.APHI = {
          for (Iterator i = @1.regs(); i.hasNext(); ) {
            Integer intg = (Integer)i.next();
            int index = defsites[intg.intValue()];
            if (!marked.get(index)) {
              marked.set(index);
              Matcher2 matcher = matchers[index];
              matcher.mark(marked, defsites, matchers);
            }
          }
        }
        | IR.ARESULT = {
          int index = defsites[@1.getReg()]-1;//bug with CrossJumping
          if (!marked.get(index)) {
            marked.set(index);
            Matcher2 matcher = matchers[index];
            matcher.mark(marked, defsites, matchers);
          }
        }
        | default = {
          if (  left$ != null)   left$.mark(marked, defsites, matchers);
          if (middle$ != null) middle$.mark(marked, defsites, matchers);
          if ( right$ != null)  right$.mark(marked, defsites, matchers);
        }
        ;

    }

  }

  public DeadCodeElimination() { }

  private transient ControlFlowGraph cfg;
  private transient Matcher1[] matchers;
  private transient BitSet live;
  private transient DataFlowAnalyser analyser;
  private transient ReachingDefinitions analysis;

  public boolean applyTo(ControlFlowGraph cfg) {
    boolean changed = false;

    this.cfg = cfg;
    int count = cfg.stmtCount();

    matchers = new Matcher1[count];
    live = new BitSet(count);
    analysis = new ReachingDefinitions(cfg);
    analyser = new IterativeDataFlowAnalyser(analysis);

    int index = 0;
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); index++) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher1 matcher = new Matcher1(stmt.snode());
      matchers[index] = matcher;
    }

    index = 0;
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); index++) {
      IRStatement stmt = (IRStatement)i.next();
      if (!live.get(index)) {
        Matcher1 matcher = matchers[index];
        matcher.cover(index, (BitItem)analyser.valueBefore(stmt));
      }
    }

    this.cfg = null;
    matchers = null;
    analyser = null;
    analysis = null;

    index = 0;
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); index++) {
      IRStatement stmt = (IRStatement)i.next();
      if (!live.get(index)) {

        int op = stmt.snode().op();
        if (op == IR.NEWINSTANCEX || op == IR.NEWARRAYX)
          for (Iterator j = stmt.ownerBB().outEdges(); j.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)j.next();
            if (edge.type() == IRControlEdge.EXCEPT) {
              j.remove();
              break;
            }
          }

        i.remove();

        changed = true;
      }
    }

    live = null;

    return changed;
  }

  private final class Matcher1 {

    private void cover(int index, BitItem value)
      : IR.IRESULT = { }
      | IR.LRESULT = { }
      | IR.FRESULT = { }
      | IR.DRESULT = { }
      | IR.ARESULT = { }

      | IR.ACATCH = { }

      | IR.IDEFINE(uses) = { }
      | IR.LDEFINE(uses) = { }
      | IR.FDEFINE(uses) = { }
      | IR.DDEFINE(uses) = { }
      | IR.ADEFINE(uses) = { }

      | IR.IPHI = { }
      | IR.LPHI = { }
      | IR.FPHI = { }
      | IR.DPHI = { }
      | IR.APHI = { }

      | IR.NEWINSTANCE(uses) = {
        IR.aclass aclass = (IR.aclass)@1.left();
        Context context = Context.get();
        ContextClassInfo clazz = context.forName(aclass.getSymbolicType());
        if (clazz != null)
          if (!clazz.finalizes())
            return;
        live.set(index);
        @2(index, value);
      }
      | IR.NEWINSTANCEX(uses) = {
        IR.aclass aclass = (IR.aclass)@1.left();
        Context context = Context.get();
        ContextClassInfo clazz = context.forName(aclass.getSymbolicType());
        if (clazz != null)
          if (!clazz.finalizes())
            return;
        live.set(index);
        @2(index, value);
      }
      | IR.NEWARRAY(uses,uses) = {
        IR.aclass aclass = (IR.aclass)@1.left();
        Context context = Context.get();
        ContextClassInfo clazz = context.forName(aclass.getSymbolicType());
        if (clazz != null)
          if (!clazz.finalizes())
            return;
        live.set(index);
        @2(index, value);
        @3(index, value);
      }
      | IR.NEWARRAYX(uses,uses) = {
        IR.aclass aclass = (IR.aclass)@1.left();
        Context context = Context.get();
        ContextClassInfo clazz = context.forName(aclass.getSymbolicType());
        if (clazz != null)
          if (!clazz.finalizes())
            return;
        live.set(index);
        @2(index, value);
        @3(index, value);
      }

      | default
      = { live.set(index); 
          if (left$ != null)     left$.uses(index, value);
          if (middle$ != null) middle$.uses(index, value);
          if (right$ != null)   right$.uses(index, value); }
      ;

    private void uses(int index, BitItem value)
      : IR.ARESULT = { mark(index-1); }//bug with CrossJumping

      | IR.IUSE = { mark(analysis.filter(@1.getReg(), value)); }
      | IR.LUSE = { mark(analysis.filter(@1.getReg(), value)); }
      | IR.FUSE = { mark(analysis.filter(@1.getReg(), value)); }
      | IR.DUSE = { mark(analysis.filter(@1.getReg(), value)); }
      | IR.AUSE = { mark(analysis.filter(@1.getReg(), value)); }

      | IR.IPHI = { for (Iterator i = @1.regs(); i.hasNext(); )
                      mark(analysis.filter(((Integer)i.next()).intValue(), value)); }
      | IR.LPHI = { for (Iterator i = @1.regs(); i.hasNext(); )
                      mark(analysis.filter(((Integer)i.next()).intValue(), value)); }
      | IR.FPHI = { for (Iterator i = @1.regs(); i.hasNext(); )
                      mark(analysis.filter(((Integer)i.next()).intValue(), value)); }
      | IR.DPHI = { for (Iterator i = @1.regs(); i.hasNext(); )
                      mark(analysis.filter(((Integer)i.next()).intValue(), value)); }
      | IR.APHI = { for (Iterator i = @1.regs(); i.hasNext(); )
                      mark(analysis.filter(((Integer)i.next()).intValue(), value)); }

      | default
      = { if (  left$ != null)   left$.uses(index, value);
          if (middle$ != null) middle$.uses(index, value);
          if ( right$ != null)  right$.uses(index, value); }
      ;

    private void mark(int index) {
      IRStatement stmt = (IRStatement)cfg.getStmt(index);
      int op = stmt.snode().op();
      if (op == IR.NEWINSTANCE || op == IR.NEWARRAY || op == IR.NEWINSTANCEX || op == IR.NEWARRAYX) {
        live.set(index);
        Matcher1 matcher = matchers[index];
        matcher.uses(index, (BitItem)analyser.valueBefore(stmt));
      }
    }

    private void mark(BitItem value) {
      value.andNot(live);
      int length = value.length();
      while (length != 0) {
        int index = length-1;
        IRStatement stmt = (IRStatement)cfg.getStmt(index);
        live.set(index);
        Matcher1 matcher = matchers[index];
        matcher.uses(index, (BitItem)analyser.valueBefore(stmt));
        value.andNot(live);
        length = value.length();
      }
    }

  }

}

