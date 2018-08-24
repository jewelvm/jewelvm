/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public abstract class FlowAssembler extends Assembler {

  private ControlFlowGraph cfg;

  protected FlowAssembler() { }

  public void reset() {
    super.reset();
    cfg = new ControlFlowGraph();
    cfg.appendBB(new BasicBlock());
  }

  public void label(String label) {
    SymbolTable symtab = getSymbolTable();
    Symbol symbol = new Label(label);
    Object value = symtab.getDeclValue(symbol);
    if (value instanceof LabelStatement)
      throw new IllegalArgumentException("Label redeclared: "+label);
    if (value instanceof ArrayList) {
      ArrayList list = (ArrayList)value;
      BasicBlock bottomBB = cfg.bottomBB();
      if (bottomBB.count() != 0) {
        BasicBlock newBB = new BasicBlock();
        cfg.appendBB(newBB);
        cfg.addEdge(new AssemblerEdge(bottomBB, AssemblerEdge.FALL, newBB));
        bottomBB = newBB;
      }
      for (Iterator i = list.iterator(); i.hasNext(); ) {
        BasicBlock sourceBB = (BasicBlock)i.next();
        cfg.addEdge(new AssemblerEdge(sourceBB, AssemblerEdge.JUMP, bottomBB));
      }
    }
    LabelStatement stmt = new LabelStatement(label);
    cfg.appendStmt(stmt);
    symtab.declare(symbol, stmt);
  }

  protected void append(InstructionStatement stmt) {
    cfg.appendStmt(stmt);
  }

  public void condXfer(String label) {
    uncondXfer(label);
    BasicBlock bottomBB = cfg.bottomBB();
    BasicBlock previousBB = bottomBB.upBB();
    cfg.addEdge(new AssemblerEdge(previousBB, AssemblerEdge.FALL, bottomBB));
  }

  public void uncondXfer(String label) {
    SymbolTable symtab = getSymbolTable();
    Symbol symbol = new Label(label);
    BasicBlock bottomBB = cfg.bottomBB();
    Object value = symtab.getDeclValue(symbol);
    if (value == null) {
      value = new ArrayList();
      symtab.declare(symbol, value);
    }
    if (value instanceof ArrayList) {
      ArrayList list = (ArrayList)value;
      list.add(bottomBB);
    }
    if (value instanceof LabelStatement) {
      LabelStatement lbstmt = (LabelStatement)value;
      BasicBlock ownerBB = lbstmt.ownerBB();
      if (lbstmt != ownerBB.leader()) {
        BasicBlock newBB = new BasicBlock();
        cfg.insertBBBeforeBB(newBB, ownerBB);
        for (Iterator i = ownerBB.topdownStmts(); i.hasNext(); ) {
          AssemblerStatement stmt = (AssemblerStatement)i.next();
          if (stmt == lbstmt)
            break;
          i.remove();
          newBB.appendStmt(stmt);
        }
        for (Iterator i = ownerBB.inEdges(); i.hasNext(); ) {
          AssemblerEdge edge = (AssemblerEdge)i.next();
          i.remove();
          cfg.addEdge(edge.clone(edge.sourceBB(), newBB));
        }
        cfg.addEdge(new AssemblerEdge(newBB, AssemblerEdge.FALL, ownerBB));
      }
      cfg.addEdge(new AssemblerEdge(bottomBB, AssemblerEdge.JUMP, ownerBB));
    }
    cfg.appendBB(new BasicBlock());
  }

  public void terminator() {
    cfg.appendBB(new BasicBlock());
  }

  public ControlFlowGraph toCFG() {
    SymbolTable symtab = getSymbolTable();
    BasicBlock bottomBB = cfg.bottomBB();
    if (bottomBB.count() == 0)
      cfg.removeBB(bottomBB);
    for (Iterator i = symtab.declarations(); i.hasNext(); ) {
      Entry entry = (Entry)i.next();
      Label label = (Label)entry.getKey();
      Object value = entry.getValue();
      if (value instanceof ArrayList)
        throw new IllegalStateException("Undeclared label: "+label);
    }
    return (ControlFlowGraph)cfg.clone();
  }

  public static abstract class AssemblerStatement extends Statement {

    AssemblerStatement() { }

    public abstract void emit(Assembler as);

    public abstract void uses(BitSet set);
    public abstract void defines(BitSet set);
    public abstract void replace(Reg source, Reg target);

  }

  public static final class LabelStatement extends AssemblerStatement {

    public final String label;

    public LabelStatement(String label) {
      if (label == null)
        throw new NullPointerException();
      this.label = label;
    }

    public void emit(Assembler as) {
      as.label(label);
    }

    public void uses(BitSet set) { }
    public void defines(BitSet set) { }
    public void replace(Reg source, Reg target) { }

    public int hashCode() {
      return label.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof LabelStatement
          && ((LabelStatement)object).label.equals(label);
    }

    public String toString() {
      return label+":";
    }

  }

  public static final class InstructionStatement extends AssemblerStatement {

    public final Opc op;
    public final Reg[] ureg;
    public final Reg[] dreg;
    public final Addr[] lmem;
    public final Addr[] smem;
    public final int[] imm;
    public final Imm[] jmm;
    public final String format;

    public InstructionStatement(Opc op, Reg[] ureg, Reg[] dreg, Addr[] lmem, Addr[] smem, int[] imm, Imm[] jmm, String format) {
      this.op = op;
      this.ureg = ureg;
      this.dreg = dreg;
      this.lmem = lmem;
      this.smem = smem;
      this.imm = imm;
      this.jmm = jmm;
      this.format = format;
    }

    public void emit(Assembler as) {
      op.emit(as, this, format);
    }

    public void uses(BitSet set) {
      if (ureg != null)
        for (int i = 0; i < ureg.length; i++)
          ureg[i].addTo(set);
      if (lmem != null)
        for (int i = 0; i < lmem.length; i++)
          lmem[i].uses(set);
      if (smem != null)
        for (int i = 0; i < smem.length; i++)
          smem[i].uses(set);
    }

    public void defines(BitSet set) {
      if (dreg != null)
        for (int i = 0; i < dreg.length; i++)
          dreg[i].addTo(set);
      if (lmem != null)
        for (int i = 0; i < lmem.length; i++)
          lmem[i].defines(set);
      if (smem != null)
        for (int i = 0; i < smem.length; i++)
          smem[i].defines(set);
    }

    public void replace(Reg source, Reg target) {
      if (ureg != null)
        for (int i = 0; i < ureg.length; i++)
          if (ureg[i].equals(source))
            ureg[i] = target;
      if (dreg != null)
        for (int i = 0; i < dreg.length; i++)
          if (dreg[i].equals(source))
            dreg[i] = target;
      if (lmem != null)
        for (int i = 0; i < lmem.length; i++)
          lmem[i] = lmem[i].replace(source, target);
      if (smem != null)
        for (int i = 0; i < smem.length; i++)
          smem[i] = smem[i].replace(source, target);
    }

    public int hashCode() {
      int hashCode = 0;
      hashCode += op.hashCode();
      if (ureg != null)
        for (int i = 0; i < ureg.length; i++)
          hashCode += ureg[i].hashCode();
      if (dreg != null)
        for (int i = 0; i < dreg.length; i++)
          hashCode += dreg[i].hashCode();
      if (lmem != null)
        for (int i = 0; i < lmem.length; i++)
          hashCode += lmem[i].hashCode();
      if (smem != null)
        for (int i = 0; i < smem.length; i++)
          hashCode += smem[i].hashCode();
      if (imm != null)
        for (int i = 0; i < imm.length; i++)
          hashCode += imm[i];
      if (jmm != null)
        for (int i = 0; i < jmm.length; i++)
          hashCode += jmm[i].hashCode();
      return hashCode;
    }

    public boolean equals(Object object) {
      return object instanceof InstructionStatement
          && ((InstructionStatement)object).op.equals(op)
          && Arrays.equals(((InstructionStatement)object).ureg, ureg)
          && Arrays.equals(((InstructionStatement)object).dreg, dreg)
          && Arrays.equals(((InstructionStatement)object).lmem, lmem)
          && Arrays.equals(((InstructionStatement)object).smem, smem)
          && Arrays.equals(((InstructionStatement)object).imm, imm)
          && Arrays.equals(((InstructionStatement)object).jmm, jmm);
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      StringTokenizer st = new StringTokenizer(format, " \t,:*$", true);
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        if (token.equals("%op"))
          token = op.toString();
        else if (token.startsWith("%u"))
          try {
            token = ureg[Integer.parseInt(token.substring(2))].toString();
          } catch (NumberFormatException e) { }
        else if (token.startsWith("%d"))
          try {
            token = dreg[Integer.parseInt(token.substring(2))].toString();
          } catch (NumberFormatException e) { }
        else if (token.startsWith("%l"))
          try {
            token = lmem[Integer.parseInt(token.substring(2))].toString();
          } catch (NumberFormatException e) { }
        else if (token.startsWith("%s"))
          try {
            token = smem[Integer.parseInt(token.substring(2))].toString();
          } catch (NumberFormatException e) { }
        else if (token.startsWith("%i"))
          try {
            token = "$"+imm[Integer.parseInt(token.substring(2))];
          } catch (NumberFormatException e) { }
        else if (token.startsWith("%j"))
          try {
            token = "$"+jmm[Integer.parseInt(token.substring(2))];
          } catch (NumberFormatException e) { }
        sb.append(token);
      }
      return sb.toString();
    }

  }

  public static final class AssemblerEdge extends ControlEdge {
  
    public static final byte FALL = 0;
    public static final byte JUMP = 1;
  
    public final byte type;
  
    private AssemblerEdge(BasicBlock sourceBB, int type, BasicBlock targetBB) {
      super(sourceBB, targetBB);
      if (type != FALL && type != JUMP)
        throw new IllegalArgumentException("Fall, jump or except type expected");
      this.type = (byte)type;
    }
  
    public int hashCode() {
      return super.hashCode() ^ type;
    }
  
    public boolean equals(Object object) {
      return object instanceof AssemblerEdge
          && ((AssemblerEdge)object).type == type
          && super.equals(object);
    }
  
  }

}

