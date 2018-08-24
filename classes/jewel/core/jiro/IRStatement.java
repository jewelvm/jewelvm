/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.IR;
import jewel.core.jiro.control.Statement;

public final class IRStatement extends Statement {

  private IR.snode snode;

  public IRStatement(IR.snode snode) {
    snode.attachTo(this);
    this.snode = snode;
  }

  public IR.snode snode() {
    return snode;
  }

  public void setNode(IR.snode snode) {
    snode.attachTo(this);
    this.snode.attachTo(null);
    switch (this.snode.op()) {
    case IR.IPHI:
      IR.iphi iphi = (IR.iphi)this.snode;
      iphi.clear();
      break;
    case IR.LPHI:
      IR.lphi lphi = (IR.lphi)this.snode;
      lphi.clear();
      break;
    case IR.FPHI:
      IR.fphi fphi = (IR.fphi)this.snode;
      fphi.clear();
      break;
    case IR.DPHI:
      IR.dphi dphi = (IR.dphi)this.snode;
      dphi.clear();
      break;
    case IR.APHI:
      IR.aphi aphi = (IR.aphi)this.snode;
      aphi.clear();
      break;
    }
    this.snode = snode;
  }

  public boolean equals(IRStatement stmt) {
    return snode.equals(stmt.snode);
  }

  public Object clone() {
    IRStatement stmt = (IRStatement)super.clone();
    stmt.snode = (IR.snode)snode.clone();
    stmt.snode.attachTo(stmt);
    return stmt;
  }

  public String toString() {
    return snode.toString();
  }

}

