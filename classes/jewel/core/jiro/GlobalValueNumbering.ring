/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.graph.DirectedGraph;
import jewel.core.jiro.optimize.Optimization;
import jewel.core.jiro.GlobalValueNumbering.ValueGraph.ValueNode;
import jewel.core.jiro.GlobalValueNumbering.ValueGraph.ValueEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public final class GlobalValueNumbering implements Optimization {

  public GlobalValueNumbering() { }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (cfg.isSSA()) {

      cfg.invalidate();
      int maxReg = cfg.regMax();

      // build value graph
      ValueGraph graph = new ValueGraph(maxReg);
      for (Iterator i = cfg.entryBBs(); i.hasNext(); ) {
        BasicBlock entryBB = (BasicBlock)i.next();
        build(graph, entryBB);
      }
      for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        for (Iterator j = currentBB.topdownStmts(); j.hasNext(); ) {
          IRStatement stmt = (IRStatement)j.next();
          Matcher matcher = new Matcher(stmt);
          if (!matcher.phibuild(graph))
            break;
        }
      }
      ValueGraph.equivalence(graph);

      // get defsites
      Statement[] defsites = new Statement[maxReg];
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher matcher = new Matcher(stmt);
        matcher.define(defsites);
      }

      // find equivalences
      int[] value = new int[maxReg];
      for (int i = 0; i < value.length; i++)
        value[i] = i;
      for (Iterator i = graph.nodes(); i.hasNext(); ) {
        ValueNode node = (ValueNode)i.next();
        for (Iterator j = node.regs(); j.hasNext(); ) {
          Integer intg1 = (Integer)j.next();
          int i1 = intg1.intValue();
          for (Iterator k = node.regs(); k.hasNext(); ) {
            Integer intg2 = (Integer)k.next();
            int i2 = intg2.intValue();
            if (i1 != i2)
              if (defsites[i1].dominates(defsites[value[i2]])) {
                value[i2] = i1;
                changed = true;
              }
          }
        }
      }

      // rename
      if (changed)
        for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
          IRStatement stmt = (IRStatement)i.next();
          Matcher matcher = new Matcher(stmt);
          matcher.replace(value);
        }

    }

    return changed;
  }

  private static void build(ValueGraph graph, BasicBlock currentBB) {
    for (Iterator i = currentBB.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher matcher = new Matcher(stmt);
      matcher.build(graph);
    }
    for (Iterator i = currentBB.idominateeBBs(); i.hasNext(); ) {
      BasicBlock idominatee = (BasicBlock)i.next();
      build(graph, idominatee);
    }
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public void define(Statement[] defsites)
      : IR.IRECEIVE = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.LRECEIVE = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.FRECEIVE = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.DRECEIVE = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.ARECEIVE = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.IRESULT = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.LRESULT = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.FRESULT = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.DRESULT = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.ARESULT = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.ACATCH = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.IDEFINE(define) = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.LDEFINE(define) = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.FDEFINE(define) = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.DDEFINE(define) = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.ADEFINE(define) = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.IPHI = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.LPHI = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.FPHI = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.DPHI = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | IR.APHI = { defsites[@1.getReg()] = @1.ownerStmt(); }
      | default = { }
      ;

    public void replace(int[] value)
      : IR.IUSE = { @1.setReg(value[@1.getReg()]); }
      | IR.LUSE = { @1.setReg(value[@1.getReg()]); }
      | IR.FUSE = { @1.setReg(value[@1.getReg()]); }
      | IR.DUSE = { @1.setReg(value[@1.getReg()]); }
      | IR.AUSE = { @1.setReg(value[@1.getReg()]); }
      | IR.IPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, value[@1.getReg(edge)]); 
        }
      }
      | IR.LPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, value[@1.getReg(edge)]); 
        }
      }
      | IR.FPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, value[@1.getReg(edge)]); 
        }
      }
      | IR.DPHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, value[@1.getReg(edge)]); 
        }
      }
      | IR.APHI = {
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, value[@1.getReg(edge)]); 
        }
      }
      | default = {
        if (  left$ != null)   left$.replace(value);
        if (middle$ != null) middle$.replace(value);
        if ( right$ != null)  right$.replace(value);
      }
      ;

    public boolean phibuild(ValueGraph graph)
      : IR.LABEL = { return true; }
      | IR.IPHI = { 
        ValueNode node = graph.forReg(@1.getReg());
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          ValueNode left = graph.forReg(@1.getReg(edge));
          BasicBlock sourceBB = edge.sourceBB();
          ControlFlowGraph cfg = sourceBB.ownerCFG();
          int index = cfg.indexOf(sourceBB);
          graph.addEdge(new ValueEdge(node, Integer.toString(index), left));
        }
        return true; 
      }
      | IR.LPHI = {
        ValueNode node = graph.forReg(@1.getReg());
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          ValueNode left = graph.forReg(@1.getReg(edge));
          BasicBlock sourceBB = edge.sourceBB();
          ControlFlowGraph cfg = sourceBB.ownerCFG();
          int index = cfg.indexOf(sourceBB);
          graph.addEdge(new ValueEdge(node, Integer.toString(index), left));
        }
        return true; 
      }
      | IR.FPHI = {
        ValueNode node = graph.forReg(@1.getReg());
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          ValueNode left = graph.forReg(@1.getReg(edge));
          BasicBlock sourceBB = edge.sourceBB();
          ControlFlowGraph cfg = sourceBB.ownerCFG();
          int index = cfg.indexOf(sourceBB);
          graph.addEdge(new ValueEdge(node, Integer.toString(index), left));
        }
        return true; 
      }
      | IR.DPHI = {
        ValueNode node = graph.forReg(@1.getReg());
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          ValueNode left = graph.forReg(@1.getReg(edge));
          BasicBlock sourceBB = edge.sourceBB();
          ControlFlowGraph cfg = sourceBB.ownerCFG();
          int index = cfg.indexOf(sourceBB);
          graph.addEdge(new ValueEdge(node, Integer.toString(index), left));
        }
        return true; 
      }
      | IR.APHI = {
        ValueNode node = graph.forReg(@1.getReg());
        for (Iterator i = @1.edges(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          ValueNode left = graph.forReg(@1.getReg(edge));
          BasicBlock sourceBB = edge.sourceBB();
          ControlFlowGraph cfg = sourceBB.ownerCFG();
          int index = cfg.indexOf(sourceBB);
          graph.addEdge(new ValueEdge(node, Integer.toString(index), left));
        }
        return true; 
      }
      | default = { return false; }
      ;

    public ValueNode build(ValueGraph graph)
      : IR.IRECEIVE = { root(graph, @1.getReg(), new ValueNode(new Diff("receive"))); return null; }
      | IR.LRECEIVE = { root(graph, @1.getReg(), new ValueNode(new Diff("receive"))); return null; }
      | IR.FRECEIVE = { root(graph, @1.getReg(), new ValueNode(new Diff("receive"))); return null; }
      | IR.DRECEIVE = { root(graph, @1.getReg(), new ValueNode(new Diff("receive"))); return null; }
      | IR.ARECEIVE = { root(graph, @1.getReg(), new ValueNode(new Diff("receive"))); return null; }
      | IR.IRESULT = { root(graph, @1.getReg(), new ValueNode(new Diff("result"))); return null; }
      | IR.LRESULT = { root(graph, @1.getReg(), new ValueNode(new Diff("result"))); return null; }
      | IR.FRESULT = { root(graph, @1.getReg(), new ValueNode(new Diff("result"))); return null; }
      | IR.DRESULT = { root(graph, @1.getReg(), new ValueNode(new Diff("result"))); return null; }
      | IR.ARESULT = { root(graph, @1.getReg(), new ValueNode(new Diff("result"))); return null; }
      | IR.ACATCH = { root(graph, @1.getReg(), new ValueNode(new Diff("catch"))); return null; }
      | IR.IDEFINE(build) = { root(graph, @1.getReg(), @2(graph)); return null; }
      | IR.LDEFINE(build) = { root(graph, @1.getReg(), @2(graph)); return null; }
      | IR.FDEFINE(build) = { root(graph, @1.getReg(), @2(graph)); return null; }
      | IR.DDEFINE(build) = { root(graph, @1.getReg(), @2(graph)); return null; }
      | IR.ADEFINE(build) = { root(graph, @1.getReg(), @2(graph)); return null; }
      | IR.IPHI = { root(graph, @1.getReg(), new ValueNode(new Phi(@1.ownerStmt()))); return null; }
      | IR.LPHI = { root(graph, @1.getReg(), new ValueNode(new Phi(@1.ownerStmt()))); return null; }
      | IR.FPHI = { root(graph, @1.getReg(), new ValueNode(new Phi(@1.ownerStmt()))); return null; }
      | IR.DPHI = { root(graph, @1.getReg(), new ValueNode(new Phi(@1.ownerStmt()))); return null; }
      | IR.APHI = { root(graph, @1.getReg(), new ValueNode(new Phi(@1.ownerStmt()))); return null; }
      /* A */
      | IR.GETCLASS(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.ALOAD(build) = { return unary(graph, new Diff("[ ]"), @2(graph)); }
      | IR.AALOAD(build,build) = { return binary(graph, new Diff("[ ]"), @2(graph), @3(graph)); }
      | IR.MLOOKUP(build) = { return unary(graph, new Lookup(@1.op(), @1.getDispatchIndex()), @2(graph)); }
      | IR.IMLOOKUP(build,build) = { return binary(graph, new Lookup(@1.op(), @1.getDispatchIndex()), @2(graph), @3(graph)); }
      | IR.AUSE = { return graph.forReg(@1.getReg()); }
      | IR.ANULL = {
        ValueNode node = new ValueNode(new Null());
        graph.addNode(node);
        return node;
      }
      | IR.ACLASS = {                                       
        ValueNode node = new ValueNode(new Cls(@1.getSymbolicType()));
        graph.addNode(node);
        return node;
      }
      | IR.ASTRING = {
        ValueNode node = new ValueNode(new Str(@1.getValue()));
        graph.addNode(node);
        return node;
      }
      /* I */
      | IR.I2B(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.I2C(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.I2S(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.L2I(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.F2I(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.D2I(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.IADD(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.ISUB(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.IMUL(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.IDIV(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.IREM(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.INEG(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.ISHL(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.ISHR(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.IUSHR(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.IAND(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.IOR(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.IXOR(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LCMP(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.FCMPG(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.FCMPL(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.DCMPG(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.DCMPL(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LENGTH(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.BLOAD(build) = { return unary(graph, new Diff("[ ]"), @2(graph)); }
      | IR.SLOAD(build) = { return unary(graph, new Diff("[ ]"), @2(graph)); }
      | IR.ILOAD(build) = { return unary(graph, new Diff("[ ]"), @2(graph)); }
      | IR.BALOAD(build,build) = { return binary(graph, new Diff("[ ]"), @2(graph), @3(graph)); }
      | IR.SALOAD(build,build) = { return binary(graph, new Diff("[ ]"), @2(graph), @3(graph)); }
      | IR.IALOAD(build,build) = { return binary(graph, new Diff("[ ]"), @2(graph), @3(graph)); }
      | IR.ISLOCKED(build) = { return unary(graph, new Diff("islocked()"), @2(graph)); }
      | IR.SUBTYPEOF(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.COMPTYPEOF(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.IUSE = { return graph.forReg(@1.getReg()); }
      | IR.ICONST = {
        ValueNode node = new ValueNode(new Integer(@1.getValue()));
        graph.addNode(node);
        return node;
      }
      /* L */
      | IR.I2L(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.F2L(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.D2L(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.LADD(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LSUB(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LMUL(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LDIV(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LREM(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LNEG(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.LSHL(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LSHR(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LUSHR(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LAND(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LOR(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LXOR(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.LLOAD(build) = { return unary(graph, new Diff("[ ]"), @2(graph)); }
      | IR.LALOAD(build,build) = { return binary(graph, new Diff("[ ]"), @2(graph), @3(graph)); }
      | IR.LUSE = { return graph.forReg(@1.getReg()); }
      | IR.LCONST = {
        ValueNode node = new ValueNode(new Long(@1.getValue()));
        graph.addNode(node);
        return node;
      }
      /* F */
      | IR.I2F(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.L2F(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.D2F(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.FSTRICT(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.FADD(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.FSUB(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.FMUL(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.FDIV(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.FREM(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.FNEG(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.FLOAD(build) = { return unary(graph, new Diff("[ ]"), @2(graph)); }
      | IR.FALOAD(build,build) = { return binary(graph, new Diff("[ ]"), @2(graph), @3(graph)); }
      | IR.FUSE = { return graph.forReg(@1.getReg()); }
      | IR.FCONST = {
        ValueNode node = new ValueNode(new Float(@1.getValue()));
        graph.addNode(node);
        return node;
      }
      /* D */
      | IR.I2D(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.L2D(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.F2D(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.DSTRICT(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.DADD(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.DSUB(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.DMUL(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.DDIV(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.DREM(build,build) = { return binary(graph, @1.op(), @2(graph), @3(graph)); }
      | IR.DNEG(build) = { return unary(graph, @1.op(), @2(graph)); }
      | IR.DLOAD(build) = { return unary(graph, new Diff("[ ]"), @2(graph)); }
      | IR.DALOAD(build,build) = { return binary(graph, new Diff("[ ]"), @2(graph), @3(graph)); }
      | IR.DUSE = { return graph.forReg(@1.getReg()); }
      | IR.DCONST = {
        ValueNode node = new ValueNode(new Double(@1.getValue()));
        graph.addNode(node);
        return node;
      }
      | default = { return null; }
      ;

    private static void root(ValueGraph graph, int reg, ValueNode node) {
      graph.addNode(reg, node);
    }

    private static ValueNode unary(ValueGraph graph, Object id, ValueNode left) {
      ValueNode node = new ValueNode(id);
      graph.addNode(node);
      graph.addEdge(new ValueEdge(node, "", left));
      return node;
    }

    private static ValueNode unary(ValueGraph graph, int op, ValueNode left) {
      return unary(graph, new Op(op), left);
    }

    private static ValueNode binary(ValueGraph graph, Object id, ValueNode left, ValueNode right) {
      ValueNode node = new ValueNode(id);
      graph.addNode(node);
      graph.addEdge(new ValueEdge(node, "L", left));
      graph.addEdge(new ValueEdge(node, "R", right));
      return node;
    }

    private static ValueNode binary(ValueGraph graph, int op, ValueNode left, ValueNode right) {
      return binary(graph, new Op(op), left, right);
    }

  }

  public static class ValueGraph extends DirectedGraph {

    public static void equivalence(ValueGraph graph) {
      ArrayList sets = new ArrayList();
      HashMap map = new HashMap();

      // startup
      for (Iterator i = graph.nodes(); i.hasNext(); ) {
        ValueNode node = (ValueNode)i.next();
        boolean found = false;
        for (Iterator j = sets.iterator(); j.hasNext(); ) {
          HashSet set = (HashSet)j.next();
          Iterator extractor = set.iterator();
          ValueNode any = (ValueNode)extractor.next();
          if (node.id.equals(any.id)) {
            set.add(node);
            map.put(node, set);
            found = true;
            break;
          }
        }
        if (!found) {
          HashSet set = new HashSet();
          set.add(node);
          map.put(node, set);
          sets.add(set);
        }
      }

      // iterate
      boolean changed = true;
      while (changed) {
        changed = false;
        for (Iterator i = sets.iterator(); i.hasNext(); ) {
          HashSet set = (HashSet)i.next();
          Iterator extractor = set.iterator();
          ValueNode any = (ValueNode)extractor.next();
          HashSet newSet = new HashSet();
          while (extractor.hasNext()) {
            ValueNode node = (ValueNode)extractor.next();
            for (Iterator j = any.outEdges(); j.hasNext(); ) {
              ValueEdge edge1 = (ValueEdge)j.next();
              for (Iterator k = node.outEdges(); k.hasNext(); ) {
                ValueEdge edge2 = (ValueEdge)k.next();
                if (edge1.label.equals(edge2.label)) {
                  ValueNode target1 = (ValueNode)edge1.targetNode();
                  ValueNode target2 = (ValueNode)edge2.targetNode();
                  if (map.get(target1) != map.get(target2))
                    newSet.add(node);
                }
              }
            }
          }
          if (!newSet.isEmpty()) {
            for (Iterator j = newSet.iterator(); j.hasNext(); ) {
              ValueNode node = (ValueNode)j.next();
              set.remove(node);
              map.put(node, newSet);
            }
            sets.add(newSet);
            changed = true;
            break;
          }
        }
      }
      
      // finalizes
      for (Iterator i = sets.iterator(); i.hasNext(); ) {
        HashSet set = (HashSet)i.next();
        Iterator extractor = set.iterator();
        ValueNode any = (ValueNode)extractor.next();
        while (extractor.hasNext()) {
          ValueNode node = (ValueNode)extractor.next();
          any.regs.addAll(node.regs);
          for (Iterator j = node.inEdges(); j.hasNext(); ) {
            ValueEdge edge = (ValueEdge)j.next();
            graph.addEdge(edge.clone((ValueNode)edge.sourceNode(), any));
          }
          graph.removeNode(node);
          for (Iterator j = any.regs.iterator(); j.hasNext(); ) {
            Integer intg = (Integer)j.next();
            graph.nodes[intg.intValue()] = any;
          }
        }
      }
    }

    private final ValueNode[] nodes;
    private final HashSet set = new HashSet();

    public ValueGraph(int size) {
      if (size < 0)
        throw new IllegalArgumentException();
      nodes = new ValueNode[size];
    }

    public Iterator nodes() {
      return new Iterator() {
        private final Iterator i = set.iterator();
        public boolean hasNext() { return i.hasNext(); }
        public Object next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }

    public void addNode(DirectedNode node) {
      addNode((ValueNode)node);
    }

    public void removeNode(DirectedNode node) {
      removeNode((ValueNode)node);
    }

    public void addEdge(DirectedEdge edge) {
      addEdge((ValueEdge)edge);
    }

    public void removeEdge(DirectedEdge edge) {
      removeEdge((ValueEdge)edge);
    }

    private ValueNode forReg(int reg) {
      if (reg < 0 || reg >= nodes.length)
        throw new IllegalArgumentException();
      ValueNode node = nodes[reg];
      if (node == null)
        throw new IllegalArgumentException();
      return node;
    }

    private void addNode(int reg, ValueNode node) {
      if (reg < 0 || reg >= nodes.length || nodes[reg] != null)
        throw new IllegalArgumentException();
      if (!contains(node))
        addNode(node);
      nodes[reg] = node;
      node.regs.add(new Integer(reg));
    }

    private void addNode(ValueNode node) {
      super.addNode(node);
      set.add(node);
    }

    private void removeNode(ValueNode node) {
      super.removeNode(node);
      set.remove(node);
      for (Iterator i = node.regs.iterator(); i.hasNext(); ) {
        Integer intg = (Integer)i.next();
        nodes[intg.intValue()] = null;
        i.remove();
      }
    }

    private void addEdge(ValueEdge edge) {
      super.addEdge(edge);
    }

    private void removeEdge(ValueEdge edge) {
      super.removeEdge(edge);
    }

    protected static final class ValueNode extends DirectedNode {

      private final Object id;
      private HashSet regs = new HashSet();

      public ValueNode(Object id) {
        if (id == null)
          throw new NullPointerException();
        this.id = id;
      }

      public Iterator regs() {
        return regs.iterator();
      }

      public String toString() {
        String label = id.toString();
        int length = label.length();
        if (length > 12) {
          label = label.substring(0, 5)+".."+label.substring(length-5);
          length = 12;
        }
        return hashCode()+" [label=\""+label+"\""+(length<4?",shape=circle":"")+"];";
      }
      
    }
                                              
    protected static final class ValueEdge extends DirectedEdge implements Cloneable {

      private final String label;

      public ValueEdge(ValueNode source, String label, ValueNode target) {
        super(source, target);
        if (label == null)
          throw new NullPointerException();
        this.label = label;
      }

      public int hashCode() {
        return super.hashCode()+label.hashCode();
      }

      public boolean equals(Object object) {
        return object instanceof ValueEdge
            && ((ValueEdge)object).label.equals(label)
            && super.equals(object);
      }

      public Edge clone(Node source, Node target) {
        try {
          return super.clone((ValueNode)source, (ValueNode)target);
        } catch (CloneNotSupportedException e) {
          throw new Error();
        }
      }

      public String toString() {
        return sourceNode().hashCode()+" -> "+targetNode().hashCode()+"[label=\""+label+"\"];";
      }

    }

  }

  private static final class Diff {

    private final String label;

    public Diff(String label) {
      if (label == null)
        throw new NullPointerException();
      this.label = label;
    }

    public String toString() {
      return label;
    }

  }

  private static final class Null {

    public Null() { }

    public int hashCode() {
      return 0;
    }

    public boolean equals(Object object) {
      return object instanceof Null;
    }

    public String toString() {
      return "null";
    }

  }

  private static final class Cls {

    private final String symbolic;

    public Cls(String symbolic) {
      if (symbolic == null)
        throw new NullPointerException();
      this.symbolic = symbolic;
    }

    public int hashCode() {
      return symbolic.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof Cls
          && ((Cls)object).symbolic.equals(symbolic);
    }

    public String toString() {
      return "class "+symbolic;
    }

  }

  private static final class Str {

    private final String string;

    public Str(String string) {
      if (string == null)
        throw new NullPointerException();
      this.string = string;
    }

    public int hashCode() {
      return string.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof Str
          && ((Str)object).string.equals(string);
    }

    public String toString() {
      return "string "+string;
    }

  }

  private static final class Phi {

    private final int index;

    public Phi(Statement stmt) {
      BasicBlock ownerBB = stmt.ownerBB();
      ControlFlowGraph cfg = ownerBB.ownerCFG();
      index = cfg.indexOf(ownerBB);
    }

    public int hashCode() {
      return index;
    }

    public boolean equals(Object object) {
      return object instanceof Phi
          && ((Phi)object).index == index;
    }

    public String toString() {
      return "phi "+index;
    }

  }

  private static final class Lookup {

    private final int op;
    private final int index;

    public Lookup(int op, int index) {
      this.op = op;
      this.index = index;
    }

    public int hashCode() {
      return op*index;
    }

    public boolean equals(Object object) {
      return object instanceof Lookup
          && ((Lookup)object).op == op
          && ((Lookup)object).index == index;
    }

    public String toString() {
      switch (op) {
      case  IR.MLOOKUP: return "mlookup("+index+")";
      case  IR.IMLOOKUP: return "imlookup("+index+")";
      default: throw new Error();
      }
    }

  }

  private static final class Op {

    private final int op;

    public Op(int op) {
      this.op = op;
    }

    public int hashCode() {
      return op;
    }

    public boolean equals(Object object) {
      return object instanceof Op
          && ((Op)object).op == op;
    }

    public String toString() {
      switch (op) {
      case  IR.GETCLASS: return ".class";
      case  IR.I2B: return "(byte)";
      case  IR.I2C: return "(char)";
      case  IR.I2S: return "(short)";
      case  IR.L2I: return "(int)";
      case  IR.F2I: return "(int)";
      case  IR.D2I: return "(int)";
      case  IR.IADD: return "+";
      case  IR.ISUB: return "-";
      case  IR.IMUL: return "*";
      case  IR.IDIV: return "/";
      case  IR.IREM: return "%";
      case  IR.INEG: return "-";
      case  IR.ISHL: return "<<";
      case  IR.ISHR: return ">>";
      case  IR.IUSHR: return ">>>";
      case  IR.IAND: return "&";
      case  IR.IOR: return "|";
      case  IR.IXOR: return "^";
      case  IR.LCMP: return "cmp";
      case  IR.FCMPG: return "cmpg";
      case  IR.FCMPL: return "cmpl";
      case  IR.DCMPG: return "cmpg";
      case  IR.DCMPL: return "cmpl";
      case  IR.LENGTH: return ".length";
      case  IR.SUBTYPEOF: return "subtypeof()";
      case  IR.COMPTYPEOF: return "comptypeof()";
      case  IR.I2L: return "(long)";
      case  IR.F2L: return "(long)";
      case  IR.D2L: return "(long)";
      case  IR.LADD: return "+";
      case  IR.LSUB: return "-";
      case  IR.LMUL: return "*";
      case  IR.LDIV: return "/";
      case  IR.LREM: return "%";
      case  IR.LNEG: return "-";
      case  IR.LSHL: return "<<";
      case  IR.LSHR: return ">>";
      case  IR.LUSHR: return ">>>";
      case  IR.LAND: return "&";
      case  IR.LOR: return "|";
      case  IR.LXOR: return "^";
      case  IR.I2F: return "(float)";
      case  IR.L2F: return "(float)";
      case  IR.D2F: return "(float)";
      case  IR.FSTRICT: return "strict()";
      case  IR.FADD: return "+";
      case  IR.FSUB: return "-";
      case  IR.FMUL: return "*";
      case  IR.FDIV: return "/";
      case  IR.FREM: return "%";
      case  IR.FNEG: return "-";
      case  IR.I2D: return "(double)";
      case  IR.L2D: return "(double)";
      case  IR.F2D: return "(double)";
      case  IR.DSTRICT: return "strict()";
      case  IR.DADD: return "+";
      case  IR.DSUB: return "-";
      case  IR.DMUL: return "*";
      case  IR.DDIV: return "/";
      case  IR.DREM: return "%";
      case  IR.DNEG: return "-";
      default: throw new Error();
      }
    }

  }

}

