/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

public final class IRBasicBlock extends BasicBlock {

  public IRBasicBlock() { }

  public void removeStmt(Statement stmt) {
    super.removeStmt(stmt);
    if (stmt instanceof IRStatement) {
      IR.snode snode = ((IRStatement)stmt).snode();
      switch (snode.op()) {
      case IR.IPHI:
        IR.iphi iphi = (IR.iphi)snode;
        iphi.clear();
        break;
      case IR.LPHI:
        IR.lphi lphi = (IR.lphi)snode;
        lphi.clear();
        break;
      case IR.FPHI:
        IR.fphi fphi = (IR.fphi)snode;
        fphi.clear();
        break;
      case IR.DPHI:
        IR.dphi dphi = (IR.dphi)snode;
        dphi.clear();
        break;
      case IR.APHI:
        IR.aphi aphi = (IR.aphi)snode;
        aphi.clear();
        break;
      }
    }
  }

  public void readFrom(DataInputStream in) throws IOException {
    int stmtCount = in.readInt();
    if (stmtCount < 0)
      throw new IOException();
    for (int i = 0; i < stmtCount; i++) {
      IR.snode snode = IR.snode.newFrom(in);
      IRStatement stmt = new IRStatement(snode);
      appendStmt(stmt);
    }
  }

  public void writeTo(DataOutputStream out) throws IOException {
    out.writeInt(count());
    for (Iterator i = topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      IR.snode snode = stmt.snode();
      snode.writeTo(out);
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(hashCode()+" [label=\"BB"+ownerCFG().indexOf(this)+"\",shape=box,color=");
    int code = -1;
    IRStatement trailer = (IRStatement)trailer();
    if (trailer != null)
      code = trailer.snode().op();
    switch (code) {
    case IR.ATHROW:
      sb.append("red");
      break;
    case IR.IRETURN:
    case IR.LRETURN:
    case IR.FRETURN:
    case IR.DRETURN:
    case IR.ARETURN:
    case IR.VRETURN:
      sb.append("black");
      break;
    default:
      sb.append("gray");
    }
    sb.append("];");
    return sb.toString();
  }

}

