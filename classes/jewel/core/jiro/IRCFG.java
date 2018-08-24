/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.ConstraintNamespace;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public final class IRCFG extends ControlFlowGraph {

  private boolean isStatic;
  private boolean retNonNull;

  private transient boolean isSSA;
  
  private ConstraintNamespace namespace;

  private int labelmax = -1;
  private int iregmax = -1;
  private int lregmax = -1;
  private int fregmax = -1;
  private int dregmax = -1;
  private int aregmax = -1;

  public IRCFG() { }

  public boolean isStatic() {
    return isStatic;
  }

  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }

  public boolean retNonNull() {
    return retNonNull;
  }

  public void setRetNonNull(boolean retNonNull) {
    this.retNonNull = retNonNull;
  }

  public boolean isSSA() {
    return isSSA;
  }

  void setSSA(boolean isSSA) {
    this.isSSA = isSSA;
  }

  public ConstraintNamespace getNamespace() {
    return namespace;
  }

  public void setNamespace(ConstraintNamespace namespace) {
    this.namespace = namespace;
  }

  public void invalidate() {
    labelmax = -1;
    iregmax = -1;
    lregmax = -1;
    fregmax = -1;
    dregmax = -1;
    aregmax = -1;
  }

  public int labelMax() {
    if (labelmax == -1) {
      labelmax = 0;
      for (Iterator i = topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        IR.snode snode = stmt.snode();
        if (snode.op() == IR.LABEL) {
          IR.label label = (IR.label)snode;
          int value = label.getValue();
          if (labelmax <= value)
            labelmax = value+1;
        }
      }
    }
    return labelmax;
  }

  public int labelSeq() {
    int seq = labelMax();
    labelmax++;
    return seq;
  }

  public int regMax() {
    return Math.max(iregMax(), Math.max(lregMax(), Math.max(fregMax(), Math.max(dregMax(), aregMax()))));
  }

  public int iregMax() {
    if (iregmax == -1) {
      iregmax = 0;
      for (Iterator i = topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        IR.snode snode = stmt.snode();
        int op = snode.op();
        if (op == IR.IDEFINE) {
          IR.idefine idefine = (IR.idefine)snode;
          int reg = idefine.getReg();
          if (iregmax <= reg)
            iregmax = reg+5;
          continue;
        }
        if (op == IR.IPHI) {
          IR.iphi iphi = (IR.iphi)snode;
          int reg = iphi.getReg();
          if (iregmax <= reg)
            iregmax = reg+5;
          continue;
        }
        if (op == IR.IRECEIVE) {
          IR.ireceive ireceive = (IR.ireceive)snode;
          int reg = ireceive.getReg();
          if (iregmax <= reg)
            iregmax = reg+5;
          continue;
        }
        if (op == IR.IRESULT) {
          IR.iresult iresult = (IR.iresult)snode;
          int reg = iresult.getReg();
          if (iregmax <= reg)
            iregmax = reg+5;
          continue;
        }
      }
    }
    return iregmax;
  }

  public int iregSeq() {
    int seq = iregMax();
    iregmax += 5;
    return seq;
  }

  public int lregMax() {
    if (lregmax == -1) {
      lregmax = 1;
      for (Iterator i = topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        IR.snode snode = stmt.snode();
        int op = snode.op();
        if (op == IR.LDEFINE) {
          IR.ldefine ldefine = (IR.ldefine)snode;
          int reg = ldefine.getReg();
          if (lregmax <= reg)
            lregmax = reg+5;
          continue;
        }
        if (op == IR.LPHI) {
          IR.lphi lphi = (IR.lphi)snode;
          int reg = lphi.getReg();
          if (lregmax <= reg)
            lregmax = reg+5;
          continue;
        }
        if (op == IR.LRECEIVE) {
          IR.lreceive lreceive = (IR.lreceive)snode;
          int reg = lreceive.getReg();
          if (lregmax <= reg)
            lregmax = reg+5;
          continue;
        }
        if (op == IR.LRESULT) {
          IR.lresult lresult = (IR.lresult)snode;
          int reg = lresult.getReg();
          if (lregmax <= reg)
            lregmax = reg+5;
          continue;
        }
      }
    }
    return lregmax;
  }

  public int lregSeq() {
    int seq = lregMax();
    lregmax += 5;
    return seq;
  }

  public int fregMax() {
    if (fregmax == -1) {
      fregmax = 2;
      for (Iterator i = topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        IR.snode snode = stmt.snode();
        int op = snode.op();
        if (op == IR.FDEFINE) {
          IR.fdefine fdefine = (IR.fdefine)snode;
          int reg = fdefine.getReg();
          if (fregmax <= reg)
            fregmax = reg+5;
          continue;
        }
        if (op == IR.FPHI) {
          IR.fphi fphi = (IR.fphi)snode;
          int reg = fphi.getReg();
          if (fregmax <= reg)
            fregmax = reg+5;
          continue;
        }
        if (op == IR.FRECEIVE) {
          IR.freceive freceive = (IR.freceive)snode;
          int reg = freceive.getReg();
          if (fregmax <= reg)
            fregmax = reg+5;
          continue;
        }
        if (op == IR.FRESULT) {
          IR.fresult fresult = (IR.fresult)snode;
          int reg = fresult.getReg();
          if (fregmax <= reg)
            fregmax = reg+5;
          continue;
        }
      }
    }
    return fregmax;
  }

  public int fregSeq() {
    int seq = fregMax();
    fregmax += 5;
    return seq;
  }

  public int dregMax() {
    if (dregmax == -1) {
      dregmax = 3;
      for (Iterator i = topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        IR.snode snode = stmt.snode();
        int op = snode.op();
        if (op == IR.DDEFINE) {
          IR.ddefine ddefine = (IR.ddefine)snode;
          int reg = ddefine.getReg();
          if (dregmax <= reg)
            dregmax = reg+5;
          continue;
        }
        if (op == IR.DPHI) {
          IR.dphi dphi = (IR.dphi)snode;
          int reg = dphi.getReg();
          if (dregmax <= reg)
            dregmax = reg+5;
          continue;
        }
        if (op == IR.DRECEIVE) {
          IR.dreceive dreceive = (IR.dreceive)snode;
          int reg = dreceive.getReg();
          if (dregmax <= reg)
            dregmax = reg+5;
          continue;
        }
        if (op == IR.DRESULT) {
          IR.dresult dresult = (IR.dresult)snode;
          int reg = dresult.getReg();
          if (dregmax <= reg)
            dregmax = reg+5;
          continue;
        }
      }
    }
    return dregmax;
  }

  public int dregSeq() {
    int seq = dregMax();
    dregmax += 5;
    return seq;
  }

  public int aregMax() {
    if (aregmax == -1) {
      aregmax = 4;
      for (Iterator i = topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        IR.snode snode = stmt.snode();
        int op = snode.op();
        if (op == IR.ADEFINE) {
          IR.adefine adefine = (IR.adefine)snode;
          int reg = adefine.getReg();
          if (aregmax <= reg)
            aregmax = reg+5;
          continue;
        }
        if (op == IR.APHI) {
          IR.aphi aphi = (IR.aphi)snode;
          int reg = aphi.getReg();
          if (aregmax <= reg)
            aregmax = reg+5;
          continue;
        }
        if (op == IR.ARECEIVE) {
          IR.areceive areceive = (IR.areceive)snode;
          int reg = areceive.getReg();
          if (aregmax <= reg)
            aregmax = reg+5;
          continue;
        }
        if (op == IR.ARESULT) {
          IR.aresult aresult = (IR.aresult)snode;
          int reg = aresult.getReg();
          if (aregmax <= reg)
            aregmax = reg+5;
          continue;
        }
        if (op == IR.ACATCH) {
          IR.acatch acatch = (IR.acatch)snode;
          int reg = acatch.getReg();
          if (aregmax <= reg)
            aregmax = reg+5;
          continue;
        }
      }
    }
    return aregmax;
  }

  public int aregSeq() {
    int seq = aregMax();
    aregmax += 5;
    return seq;
  }

  public void removeEdge(ControlEdge edge) {
    super.removeEdge(edge);
    if (edge instanceof IRControlEdge) {
      IRControlEdge iredge = (IRControlEdge)edge;
      BasicBlock targetBB = edge.targetBB();
      if (targetBB.inDegree() == 1) {
        IRControlEdge otherEdge = (IRControlEdge)targetBB.inEdges().next();
        for (Iterator i = targetBB.topdownStmts(); i.hasNext(); ) {
          IRStatement stmt = (IRStatement)i.next();
          IR.snode snode = stmt.snode();
          switch (snode.op()) {
          case IR.LABEL:
            continue;
          case IR.IPHI:
            IR.iphi iphi = (IR.iphi)snode;
            stmt.setNode(new IR.idefine(iphi.getReg(), new IR.iuse(iphi.getReg(otherEdge))));
            continue;
          case IR.LPHI:
            IR.lphi lphi = (IR.lphi)snode;
            stmt.setNode(new IR.ldefine(lphi.getReg(), new IR.luse(lphi.getReg(otherEdge))));
            continue;
          case IR.FPHI:
            IR.fphi fphi = (IR.fphi)snode;
            stmt.setNode(new IR.fdefine(fphi.getReg(), new IR.fuse(fphi.getReg(otherEdge))));
            continue;
          case IR.DPHI:
            IR.dphi dphi = (IR.dphi)snode;
            stmt.setNode(new IR.ddefine(dphi.getReg(), new IR.duse(dphi.getReg(otherEdge))));
            continue;
          case IR.APHI:
            IR.aphi aphi = (IR.aphi)snode;
            stmt.setNode(new IR.adefine(aphi.getReg(), new IR.ause(aphi.getReg(otherEdge))));
            continue;
          }
          break;
        }
      } else
        for (Iterator i = targetBB.topdownStmts(); i.hasNext(); ) {
          IRStatement stmt = (IRStatement)i.next();
          IR.snode snode = stmt.snode();
          switch (snode.op()) {
          case IR.LABEL:
            continue;
          case IR.IPHI:
            IR.iphi iphi = (IR.iphi)snode;
            iphi.removeEdge(iredge);
            continue;
          case IR.LPHI:
            IR.lphi lphi = (IR.lphi)snode;
            lphi.removeEdge(iredge);
            continue;
          case IR.FPHI:
            IR.fphi fphi = (IR.fphi)snode;
            fphi.removeEdge(iredge);
            continue;
          case IR.DPHI:
            IR.dphi dphi = (IR.dphi)snode;
            dphi.removeEdge(iredge);
            continue;
          case IR.APHI:
            IR.aphi aphi = (IR.aphi)snode;
            aphi.removeEdge(iredge);
            continue;
          }
          break;
        }
    }
  }

  public Object clone() {
    IRCFG cfg = (IRCFG)super.clone();

    cfg.namespace = (ConstraintNamespace)namespace.clone();
    
    int bindex = 0;
    for (Iterator i = topdownBBs(); i.hasNext(); bindex++) {
      BasicBlock currentBB = (BasicBlock)i.next();
      BasicBlock updateBB = cfg.getBB(bindex);

      int sindex = 0;
      for (Iterator j = currentBB.topdownStmts(); j.hasNext(); sindex++) {
        IRStatement stmt = (IRStatement)j.next();
        IRStatement ustmt = (IRStatement)updateBB.getStmt(sindex);
        IR.snode snode = stmt.snode();
        IR.snode usnode = ustmt.snode();
        switch (snode.op()) {
        case IR.LABEL:
          continue;
        case IR.IPHI:
          IR.iphi iphi = (IR.iphi)snode;
          IR.iphi uiphi = (IR.iphi)usnode;
          for (Iterator k = iphi.edges(); k.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)k.next();
            BasicBlock sourceBB = cfg.getBB(indexOf(edge.sourceBB()));
            IRControlEdge uedge = (IRControlEdge)edge.clone(sourceBB, updateBB);
            uiphi.setReg(uedge, iphi.getReg(edge));
          }
          continue;
        case IR.LPHI:
          IR.lphi lphi = (IR.lphi)snode;
          IR.lphi ulphi = (IR.lphi)usnode;
          for (Iterator k = lphi.edges(); k.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)k.next();
            BasicBlock sourceBB = cfg.getBB(indexOf(edge.sourceBB()));
            IRControlEdge uedge = (IRControlEdge)edge.clone(sourceBB, updateBB);
            ulphi.setReg(uedge, lphi.getReg(edge));
          }
          continue;
        case IR.FPHI:
          IR.fphi fphi = (IR.fphi)snode;
          IR.fphi ufphi = (IR.fphi)usnode;
          for (Iterator k = fphi.edges(); k.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)k.next();
            BasicBlock sourceBB = cfg.getBB(indexOf(edge.sourceBB()));
            IRControlEdge uedge = (IRControlEdge)edge.clone(sourceBB, updateBB);
            ufphi.setReg(uedge, fphi.getReg(edge));
          }
          continue;
        case IR.DPHI:
          IR.dphi dphi = (IR.dphi)snode;
          IR.dphi udphi = (IR.dphi)usnode;
          for (Iterator k = dphi.edges(); k.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)k.next();
            BasicBlock sourceBB = cfg.getBB(indexOf(edge.sourceBB()));
            IRControlEdge uedge = (IRControlEdge)edge.clone(sourceBB, updateBB);
            udphi.setReg(uedge, dphi.getReg(edge));
          }
          continue;
        case IR.APHI:
          IR.aphi aphi = (IR.aphi)snode;
          IR.aphi uaphi = (IR.aphi)usnode;
          for (Iterator k = aphi.edges(); k.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)k.next();
            BasicBlock sourceBB = cfg.getBB(indexOf(edge.sourceBB()));
            IRControlEdge uedge = (IRControlEdge)edge.clone(sourceBB, updateBB);
            uaphi.setReg(uedge, aphi.getReg(edge));
          }
          continue;
        }
        break;
      }

    }

    return cfg;
  }

  public void readFrom(InputStream in) throws IOException {
    readFrom(in instanceof DataInputStream ? (DataInputStream)in : new DataInputStream(in));
  }

  public void readFrom(DataInputStream in) throws IOException {
    isStatic = in.readBoolean();
    retNonNull = in.readBoolean();
    namespace = new ConstraintNamespace();
    namespace.readFrom(in);
    int blockCount = in.readInt();
    if (blockCount < 0)
      throw new IOException();
    ArrayList list = new ArrayList(blockCount);
    for (int i = 0; i < blockCount; i++) {
      IRBasicBlock currentBB = new IRBasicBlock();
      currentBB.readFrom(in);
      addNode(currentBB);
      list.add(currentBB);
    }
    int edgeCount = in.readInt();
    for (int i = 0; i < edgeCount; i++) {
      byte type = in.readByte();
      if (type < IRControlEdge.FALL || type > IRControlEdge.SWITCH)
        throw new IOException();
      int value = 0;
      if (type == IRControlEdge.SWITCH)
        value = in.readInt();
      int sourceIndex = in.readInt();
      if (sourceIndex < 0 || sourceIndex >= list.size())
        throw new IOException();
      int targetIndex = in.readInt();
      if (targetIndex < 0 || targetIndex >= list.size())
        throw new IOException();
      IRBasicBlock sourceBB = (IRBasicBlock)list.get(sourceIndex);
      IRBasicBlock targetBB = (IRBasicBlock)list.get(targetIndex);
      if (type != IRControlEdge.SWITCH)
        addEdge(new IRControlEdge(sourceBB, type, targetBB));
      else
        addEdge(new IRControlEdge(sourceBB, type, value, targetBB));
    }
  }

  public void writeTo(OutputStream out) throws IOException {
    writeTo(out instanceof DataOutputStream ? (DataOutputStream)out : new DataOutputStream(out));
  }

  public void writeTo(DataOutputStream out) throws IOException {
    out.writeBoolean(isStatic);
    out.writeBoolean(retNonNull);
    namespace.writeTo(out);
    HashSet edges = new HashSet();
    ArrayList list = new ArrayList(count());
    out.writeInt(count());
    for (Iterator i = topdownBBs(); i.hasNext(); ) {
      IRBasicBlock currentBB = (IRBasicBlock)i.next();
      currentBB.writeTo(out);
      for (Iterator j = currentBB.outEdges(); j.hasNext(); ) {
        IRControlEdge edge = (IRControlEdge)j.next();
        edges.add(edge);
      }
      list.add(currentBB);
    }
    out.writeInt(edges.size());
    for (Iterator i = edges.iterator(); i.hasNext(); ) {
      IRControlEdge edge = (IRControlEdge)i.next();
      out.writeByte(edge.type());
      if (edge.type() == IRControlEdge.SWITCH)
        out.writeInt(edge.value());
      out.writeInt(list.indexOf(edge.sourceBB()));
      out.writeInt(list.indexOf(edge.targetBB()));
    }
    out.flush();
  }

}

