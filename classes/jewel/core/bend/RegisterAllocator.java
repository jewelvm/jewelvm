/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import jewel.core.bend.FlowAssembler.AssemblerStatement;
import jewel.core.bend.FlowAssembler.InstructionStatement;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.dataflow.BitFunction;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.DataFlowAnalyser;
import jewel.core.jiro.dataflow.DataFlowAnalysis;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;
import jewel.core.jiro.graph.ConflictGraph;
import jewel.core.jiro.graph.ConflictGraph.ConflictEdge;
import jewel.core.jiro.graph.ConflictGraph.ConflictNode;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class RegisterAllocator {

  public BitSet usedcsregs;

  protected RegisterAllocator() { }

  public final void allocate(ControlFlowGraph cfg) {

    /* collect all registers: OK */
    Reg[] registers = new Reg[0];
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      AssemblerStatement stmt = (AssemblerStatement)i.next();
      if (stmt instanceof InstructionStatement) {
        InstructionStatement insn = (InstructionStatement)stmt;
        if (insn.dreg != null)
          for (int j = 0; j < insn.dreg.length; j++) {
            Reg reg = insn.dreg[j];
            int index = reg.index;
            if (index >= registers.length) {
              Reg[] tmp = registers;
              registers = new Reg[index+1];
              System.arraycopy(tmp, 0, registers, 0, tmp.length);
            }
            registers[index] = reg;
          }
      }
    }

    Reg[] Mregs = machineRegisters();
    int maxregs = Mregs.length;
    final BitSet mregs = new BitSet();
    for (int i = 0; i < Mregs.length; i++) {
      Reg Mreg = Mregs[i];
      int index = Mreg.index;
      if (index >= registers.length) {
        Reg[] tmp = registers;
        registers = new Reg[index+1];
        System.arraycopy(tmp, 0, registers, 0, tmp.length);
      }
      registers[index] = Mreg;
      Mreg.addTo(mregs);
    }
    final BitSet csregs = new BitSet();
    calleeSaveRegisters(csregs);

    /* while spills: OK */
    BitSet allspills = new BitSet();
    for (;;) {

      final ConflictGraph conflictGraph = new ConflictGraph();

      /* add all nodes: OK */
      BitSet regs = new BitSet();
      regs.or(mregs);
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        AssemblerStatement stmt = (AssemblerStatement)i.next();
        stmt.uses(regs);
        stmt.defines(regs);
      }
      int regsLength = regs.length();
      for (int i = 0; i < regsLength; i++)
        if (regs.get(i))
          conflictGraph.addNode(new CNode(registers[i]));

      /* add liveness conflicts: OK */
      DataFlowAnalyser analyser = new IterativeDataFlowAnalyser(new LiveRegisters());
      for (Iterator i = cfg.bottomupStmts(); i.hasNext(); ) {
        Statement stmt = (Statement)i.next();
        BitSet value = (BitSet)analyser.valueBefore(stmt);
        int length = value.length();
        for (int j = 0; j < length-1; j++)
          if (value.get(j))
            for (int k = j+1; k < length; k++)
              if (value.get(k))
                if (!(mregs.get(j) && mregs.get(k)))
                  conflictGraph.addEdge(new ConflictEdge(conflictGraph.getNode(j), conflictGraph.getNode(k)));
      }

      /* add conflicts for calls: OK */
      BitSet across = new BitSet();
      BitSet xcross = new BitSet();
      for (Iterator i = cfg.bottomupStmts(); i.hasNext(); ) {
        AssemblerStatement stmt = (AssemblerStatement)i.next();
        if (isCall(stmt)) {
          BitSet value = (BitSet)analyser.valueAfter(stmt);
          across.or(value);
          BasicBlock ownerBB = stmt.ownerBB();
          if (stmt == ownerBB.trailer())
            if (ownerBB.outDegree() == 2) {
              Iterator j = ownerBB.succBBs();
              BasicBlock targetBB = (BasicBlock)j.next();
              if (targetBB == ownerBB.downBB())
                targetBB = (BasicBlock)j.next();
              value = (BitSet)analyser.valueBefore(targetBB);
              xcross.or(value);
            }
        }
      }
      across.andNot(mregs);
      int mlength = mregs.length();
      int alength;
      while ((alength = across.length()) != 0) {
        int vreg = alength-1;
        boolean fixed = registers[vreg] instanceof VirtReg && ((VirtReg)registers[vreg]).fixed;
        for (int i = 0; i < mlength; i++)
          if (mregs.get(i) && (fixed || !csregs.get(i)))
            conflictGraph.addEdge(new ConflictEdge(conflictGraph.getNode(vreg), conflictGraph.getNode(i)));
        across.clear(vreg);
      }
      xcross.andNot(mregs);
      int xlength;
      while ((xlength = xcross.length()) != 0) {
        int vreg = xlength-1;
        for (int i = 0; i < mlength; i++)
          if (mregs.get(i))
            conflictGraph.addEdge(new ConflictEdge(conflictGraph.getNode(vreg), conflictGraph.getNode(i)));
        xcross.clear(vreg);
      }

      /* add machine restrictions: OK */
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        AssemblerStatement stmt = (AssemblerStatement)i.next();
        BitSet udregs = new BitSet();
        stmt.uses(udregs);
        stmt.defines(udregs);
        udregs.andNot(mregs);
        int udlength = udregs.length();
        for (int j = 0; j < udlength; j++)
          if (udregs.get(j)) {
            BitSet notclass = new BitSet();
            incompatibleRegisters(stmt, registers[j], notclass);
            int nclength = notclass.length();
            for (int k = 0; k < nclength; k++)
              if (notclass.get(k)) 
                conflictGraph.addEdge(new ConflictEdge(conflictGraph.getNode(j), conflictGraph.getNode(k)));
          }
      }

      /* coalesce nodes: OK */
      Reg[] replace = new Reg[regsLength];
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        AssemblerStatement copy = (AssemblerStatement)i.next();
        if (isCopy(copy)) {
          Reg source = getCopySource(copy);
          if (replace[source.index] != null)
            source = replace[source.index];
          Reg target = getCopyTarget(copy);
          if (replace[target.index] != null)
            target = replace[target.index];
          if (source.index != target.index)
            if (!(mregs.get(source.index) && mregs.get(target.index)))
              if (!conflictGraph.hasConflict(source.index, target.index)) {
                if (mregs.get(target.index)) {
                  Reg tmp = source;
                  source = target;
                  target = tmp;
                }
                HashSet nodes = new HashSet();
                CNode snode = (CNode)conflictGraph.getNode(source.index);
                for (Iterator j = snode.conflictNodes(); j.hasNext(); ) {
                  CNode node = (CNode)j.next();
                  nodes.add(node);
                }
                CNode tnode = (CNode)conflictGraph.getNode(target.index);
                for (Iterator j = tnode.conflictNodes(); j.hasNext(); ) {
                  CNode node = (CNode)j.next();
                  nodes.add(node);
                }
                if (mregs.get(source.index) || nodes.size() < maxregs) {
                  conflictGraph.removeNode(tnode);
                  for (Iterator j = nodes.iterator(); j.hasNext(); ) {
                    CNode node = (CNode)j.next();
                    if (!(mregs.get(source.index) && mregs.get(node.register())))
                      conflictGraph.addEdge(new ConflictEdge(snode, node));
                  }
                  for (int j = 0; j < replace.length; j++)
                    if (target.equals(replace[j]))
                      replace[j] = source;
                  replace[target.index] = source;
                  target = source;
                }
              }
          if (source.equals(target))
            i.remove();
        }
      }
      BitSet coalesce = new BitSet();
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        AssemblerStatement stmt = (AssemblerStatement)i.next();
        stmt.uses(coalesce);
        stmt.defines(coalesce);
        int length;
        while ((length = coalesce.length()) > 0) {
          int reg = length-1;
          if (replace[reg] != null)
            stmt.replace(registers[reg], replace[reg]);
          coalesce.clear(reg);
        }
      }

      /* compute spill costs: OK */
      for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        int depth = currentBB.loopDepth();
        if (depth > 9)
          depth = 9;
        int weight = 2<<(3*depth);
        for (Iterator j = currentBB.topdownStmts(); j.hasNext(); ) {
          AssemblerStatement stmt = (AssemblerStatement)j.next();
          BitSet allows = new BitSet();
          allowsInMemory(stmt, allows);
          allows.andNot(mregs);
          BitSet uses = new BitSet();
          stmt.uses(uses);
          uses.andNot(mregs);
          int ulength = uses.length();
          for (int u = 0; u < ulength; u++)
            if (uses.get(u)) {
              CNode node = (CNode)conflictGraph.getNode(u);
              node.addCost(weight);
              if (!allows.get(u))
                node.addCost(1);
            }
          BitSet defines = new BitSet();
          stmt.defines(defines);
          defines.andNot(mregs);
          int dlength = defines.length();
          for (int d = 0; d < dlength; d++)
            if (defines.get(d)) {
              CNode node = (CNode)conflictGraph.getNode(d);
              node.addCost(weight);
              if (!allows.get(d))
                node.addCost(1);
            }
        }
      }

      /* remove nodes pushing on stack: OK */
      ArrayList stack = new ArrayList();
      for (;;) {
        Iterator i = new Iterator() {
          private final Iterator i = conflictGraph.nodes();
          private CNode current;
          public boolean hasNext() {
            if (current != null)
              return true;
            while (i.hasNext()) {
              CNode node = (CNode)i.next();
              if (!mregs.get(node.register())) {
                current = node;
                return true;
              }
            }
            return false;
          }
          public Object next() {
            if (!hasNext())
              throw new NoSuchElementException();
            CNode node = current;
            current = null;
            return node;
          }
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
        if (!i.hasNext())
          break;
        CNode best = (CNode)i.next();
        if (best.degree() >= maxregs)
          while (i.hasNext()) {
            CNode node = (CNode)i.next();
            if (node.degree() < maxregs) {
              best = node;
              break;
            }
            if (allspills.get(best.register())) {
              if (!allspills.get(node.register()) || node.cost()/node.degree() < best.cost()/best.degree())
                best = node;
            } else {
              if (!allspills.get(node.register()) && node.cost()/node.degree() < best.cost()/best.degree())
                best = node;
            }
          }
        HashSet edges = new HashSet();
        for (Iterator j = best.edges(); j.hasNext(); ) {
          ConflictEdge edge = (ConflictEdge)j.next();
          edges.add(edge);
        }
        stack.add(new Object[]{ best, edges });
        conflictGraph.removeNode(best);
      }

      /* pop nodes assigning colors: OK */
      for (Iterator i = conflictGraph.nodes(); i.hasNext(); ) {
        CNode node = (CNode)i.next();
        node.setColor(node.reg);
      }
      BitSet spills = new BitSet();
      while (stack.size() > 0) {
        Object[] top = (Object[])stack.remove(stack.size()-1);
        CNode node = (CNode)top[0];
        HashSet edges = (HashSet)top[1];
        conflictGraph.addNode(node);
        for (Iterator i = edges.iterator(); i.hasNext(); ) {
          ConflictEdge edge = (ConflictEdge)i.next();
          conflictGraph.addEdge(edge);
        }
        BitSet avail = (BitSet)mregs.clone();
        for (Iterator i = node.conflictNodes(); i.hasNext(); ) {
          CNode cnode = (CNode)i.next();
          Reg color = cnode.color();
          if (color != null)
            color.removeFrom(avail);
        }
        int length = avail.length();
        if (length > 0) {
          int color = length-1;
          avail.andNot(csregs);
          length = avail.length();
          if (length > 0)
            color = length-1;
          node.setColor(registers[color]);
          continue;
        }
        spills.set(node.register());
      }
      if (spills.length() > 0) {
        spills.andNot(allspills);
        genSpill(cfg, registers, spills);
        allspills.or(spills);
        continue;
      }

      /* rename virtual registers: OK */
      BitSet rename = new BitSet();
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        AssemblerStatement stmt = (AssemblerStatement)i.next();
        stmt.uses(rename);
        stmt.defines(rename);
        int length = rename.length();
        while (length > 0) {
          int reg = length-1;
          CNode node = (CNode)conflictGraph.getNode(reg);
          Reg color = node.color();
          if (!color.equals(registers[reg]))
            stmt.replace(registers[reg], color);
          rename.clear(reg);
          length = rename.length();
        }
        if (isCopy(stmt))
          if (getCopySource(stmt).equals(getCopyTarget(stmt)))
            i.remove();
      }

      /* collect used callee save registers: OK */
      usedcsregs = new BitSet();
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        AssemblerStatement stmt = (AssemblerStatement)i.next();
        stmt.uses(usedcsregs);
        stmt.defines(usedcsregs);
        implicitUsesAndDefines(stmt, usedcsregs, usedcsregs);
      }
      usedcsregs.and(csregs);

      break;

    }

  }

  private void genSpill(ControlFlowGraph cfg, Reg[] registers, BitSet spills) {
    //System.err.println("*spill "+spills+"*");
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      AssemblerStatement stmt = (AssemblerStatement)i.next();
      BitSet allows = new BitSet();
      allowsInMemory(stmt, allows);
      allows.and(spills);
      int length;
      while ((length = allows.length()) != 0) {
        int vreg = length-1;
        AssemblerStatement nstmt = newInMemoryStatement(registers[vreg], stmt);
        stmt.ownerBB().insertStmtBeforeStmt(nstmt, stmt);
        stmt.ownerBB().removeStmt(stmt);
        stmt = nstmt;
        allows.xor(allows);
        allowsInMemory(stmt, allows);
        allows.and(spills);
      }
      BitSet uses = new BitSet();
      stmt.uses(uses);
      uses.and(spills);
      while ((length = uses.length()) != 0) {
        int vreg = length-1;
        AssemblerStatement loadStmt = newLoadStatement(registers[vreg]);
        AssemblerStatement storeStmt = newStoreStatement(registers[vreg]);
        if (stmt.equals(storeStmt)) {
          stmt.ownerBB().removeStmt(stmt);
          stmt = null;
        } else
          stmt.ownerBB().insertStmtBeforeStmt(loadStmt, stmt);
        uses.clear(vreg);
      }
      if (stmt == null)
        continue;
      BitSet defines = new BitSet();
      stmt.defines(defines);
      defines.and(spills);
      while ((length = defines.length()) != 0) {
        int vreg = length-1;
        AssemblerStatement loadStmt = newLoadStatement(registers[vreg]);
        AssemblerStatement storeStmt = newStoreStatement(registers[vreg]);
        if (stmt.equals(loadStmt)) {
          stmt.ownerBB().removeStmt(stmt);
          stmt = null;
        } else
          stmt.ownerBB().insertStmtAfterStmt(storeStmt, stmt);
        defines.clear(vreg);
      }
    }
  }

  protected abstract Reg[] machineRegisters();
  protected abstract void calleeSaveRegisters(BitSet set);
  protected abstract void sharedRegisters(int reg, BitSet set);
  protected abstract boolean isCopy(AssemblerStatement stmt);
  protected abstract Reg getCopySource(AssemblerStatement stmt);
  protected abstract Reg getCopyTarget(AssemblerStatement stmt);
  protected abstract void implicitUsesAndDefines(AssemblerStatement stmt, BitSet uses, BitSet defines);
  protected abstract void allowsInMemory(AssemblerStatement stmt, BitSet set);
  protected abstract void incompatibleRegisters(AssemblerStatement stmt, Reg reg, BitSet set);
  protected abstract AssemblerStatement newInMemoryStatement(Reg reg, AssemblerStatement stmt);
  protected abstract AssemblerStatement newLoadStatement(Reg reg);
  protected abstract AssemblerStatement newStoreStatement(Reg reg);
  protected abstract boolean isCall(AssemblerStatement stmt);

  private final class LiveRegisters implements DataFlowAnalysis {

    public LiveRegisters() { }

    public byte direction() {
      return BACKWARD;
    }

    public FlowItem newFlowItem() {
      return new BitItem();
    }

    public FlowFunction newFlowFunction() {
      return new BitFunction();
    }

    public void modelEffect(FlowFunction _function, Statement _stmt) {
      BitFunction function = (BitFunction)_function;
      AssemblerStatement stmt = (AssemblerStatement)_stmt;
      BitSet uses = new BitSet();
      BitSet defines = new BitSet();
      stmt.uses(uses);
      stmt.defines(defines);
      implicitUsesAndDefines(stmt, uses, defines);
      Reg[] Mregs = machineRegisters();
      BitSet umregs = new BitSet();
      for (int i = 0; i < Mregs.length; i++)
        Mregs[i].addTo(umregs);
      umregs.and(uses);
      int ulength = umregs.length();
      for (int i = 0; i < ulength; i++)
        if (umregs.get(i))
          sharedRegisters(i, uses);
      BitSet dmregs = new BitSet();
      for (int i = 0; i < Mregs.length; i++)
        Mregs[i].addTo(dmregs);
      dmregs.and(defines);
      int dlength = dmregs.length();
      for (int i = 0; i < dlength; i++)
        if (dmregs.get(i))
          sharedRegisters(i, defines);
      function.set(defines, BitFunction.KILL);
      function.set(uses, BitFunction.GEN);
    }

    public FlowItem merge(FlowItem one, FlowItem another) {
      BitItem result = (BitItem)one.clone();
      result.or((BitItem)another);
      return result;
    }

    public void modelEffect(FlowFunction function, ControlEdge edge) { }

  }

  private static final class CNode extends ConflictNode {

    private final Reg reg;
    private float cost;
    private Reg color;

    public CNode(Reg reg) {
      super(reg.index);
      this.reg = reg;
    }

    public float cost() {
      return cost;
    }

    public void addCost(float cost) {
      if (cost < 0)
        throw new IllegalArgumentException();
      this.cost += cost;
    }

    public Reg color() {
      return color;
    }

    public void setColor(Reg color) {
      if (color == null)
        throw new IllegalArgumentException();
      this.color = color;
    }

    public String toString() {
      return hashCode()+" [label=\""+reg+"\",shape=circle];";
    }

  }

}

