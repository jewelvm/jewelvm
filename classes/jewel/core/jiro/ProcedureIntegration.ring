/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.ConstraintNamespace;
import jewel.core.Context;
import jewel.core.ContextClassInfo;
import jewel.core.LoadedClassInfo;
import jewel.core.LoadedClassInfo.PlacedMethodInfo;
import jewel.core.clfile.Syntax;
import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.ArrayList;
import java.util.Iterator;

// missing update symbolic types if crosses class loader boundaries
public final class ProcedureIntegration implements Optimization {

  private final byte level;
  
  public ProcedureIntegration() {
    this(0);
  }

  public ProcedureIntegration(int level) {
    if (level < 0 || level >= 255)
      throw new IllegalArgumentException();
    this.level = (byte)level;
  }
  
  public int getLevel() {
    return level & 0xFF;
  }

  public boolean applyTo(ControlFlowGraph _cfg) {
    boolean changed = false;

    IRCFG cfg = (IRCFG)_cfg;

    if (!cfg.isSSA()) {

      ArrayList list = new ArrayList();
      for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        Matcher1 matcher = new Matcher1(stmt);
        matcher.cover(list, getLevel());
      }

      changed = list.size() > 0;

      for (Iterator i = list.iterator(); i.hasNext(); ) {
        Runnable runnable = (Runnable)i.next();
        runnable.run();
      }

    }

    return changed;
  }

  private static final class Matcher1 {

    public Matcher1(IRStatement stmt) {
      this(stmt.snode());
    }

    public void cover(ArrayList list, int level)
      : IR.CALL(IR.MLOOKUP(IR.ACLASS)) = {
        Context context = Context.get();
        ContextClassInfo clazz = context.forName(@3.getSymbolicType());
        if (clazz != null) {
          PlacedMethodInfo method = clazz.lookupDispatchMethod(@2.getDispatchIndex());
          if (method == null)
            return;
          if (!checkCall(@1.ownerStmt(), method.getDescriptor(), !method.isStatic() && !method.getName().equals("<clinit>")))
            return;
          Context subcontext = context.getSubcontext(method.getOwner().getName(), method.getContext());
          if (subcontext == null)
            return;
          final IRCFG cfg;
          try {
            cfg = method.getCFG(subcontext);
          } catch (Exception e) {
            e.printStackTrace();
            return;
          }
          if (cfg == null)
            return;
          if (isEligible(level, @1.ownerStmt(), @2, cfg))
            list.add(new Runnable() {
              public void run() {
                inlineCall(@1.ownerStmt(), cfg);
              }
            });
        }
      }
      | IR.CALLX(IR.MLOOKUP(IR.ACLASS)) = {
        Context context = Context.get();
        ContextClassInfo clazz = context.forName(@3.getSymbolicType());
        if (clazz != null) {
          PlacedMethodInfo method = clazz.lookupDispatchMethod(@2.getDispatchIndex());
          if (method == null)
            return;
          if (!checkCall(@1.ownerStmt(), method.getDescriptor(), !method.isStatic() && !method.getName().equals("<clinit>")))
            return;
          Context subcontext = context.getSubcontext(method.getOwner().getName(), method.getContext());
          if (subcontext == null)
            return;
          final IRCFG cfg;
          try {
            cfg = method.getCFG(subcontext);
          } catch (Exception e) {
            e.printStackTrace();
            return;
          }
          if (cfg == null)
            return;
          if (isEligible(level, @1.ownerStmt(), @2, cfg))
            list.add(new Runnable() {
              public void run() {
                inlineCall(@1.ownerStmt(), cfg);
              }
            });
        }
      }
      | default = { }
      ;

  }

  private static boolean checkCall(IRStatement callStmt, String descriptor, boolean hasThis) {
    String[] paramTypes = Syntax.getParameterTypes(descriptor);
    String retType = Syntax.getReturnType(descriptor);
    IRStatement stmt = callStmt;
    if (hasThis) {
      stmt = (IRStatement)stmt.previousStmt();
      if (stmt == null) return false;
      if (stmt.snode().op() != IR.APASS) return false;
    }
    for (int i = 0; i < paramTypes.length; i++) {
      String paramType = paramTypes[i];
      stmt = (IRStatement)stmt.previousStmt();
      if (stmt == null) return false;
      switch (paramType.charAt(0)) {
      case 'Z':
      case 'C':
      case 'B':
      case 'S':
      case 'I': if (stmt.snode().op() != IR.IPASS) return false; break;
      case 'J': if (stmt.snode().op() != IR.LPASS) return false; break;
      case 'F': if (stmt.snode().op() != IR.FPASS) return false; break;
      case 'D': if (stmt.snode().op() != IR.DPASS) return false; break;
      case 'L':
      case '[': if (stmt.snode().op() != IR.APASS) return false; break;
      }
    }
    stmt = (IRStatement)stmt.previousStmt();
    if (stmt != null)
      switch (stmt.snode().op()) {
      case IR.IPASS:
      case IR.LPASS: 
      case IR.FPASS: 
      case IR.DPASS: 
      case IR.APASS: return false;
      }
    stmt = (IRStatement)callStmt.nextStmt();
    if (stmt != null)
      switch (stmt.snode().op()) {
      case IR.IRESULT: if (retType.charAt(0) != 'Z'
                        && retType.charAt(0) != 'C'
                        && retType.charAt(0) != 'B'
                        && retType.charAt(0) != 'S'
                        && retType.charAt(0) != 'I') return false; break;
      case IR.LRESULT: if (retType.charAt(0) != 'J') return false; break;
      case IR.FRESULT: if (retType.charAt(0) != 'F') return false; break;
      case IR.DRESULT: if (retType.charAt(0) != 'D') return false; break;
      case IR.ARESULT: if (retType.charAt(0) != 'L'
                        && retType.charAt(0) != '[') return false; break;
      }
    return true;
  }

  private static boolean isEligible(int level, IRStatement stmt, IR.mlookup mlookup, IRCFG cfg) {
    switch (level) {
    case 0: return level0(stmt, mlookup, cfg);
    default: return level1(stmt, mlookup, cfg);
    }
  }
  
  private static boolean level0(IRStatement callStmt, IR.mlookup mlookup, IRCFG cfg) {
    int inout = 0;
    int jumps = 0;
    int calls = 0;
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      IR.snode snode = stmt.snode();
      switch (snode.op()) {
      case IR.IRECEIVE: case IR.LRECEIVE: case IR.FRECEIVE: case IR.DRECEIVE: case IR.ARECEIVE:
      case IR.IRETURN: case IR.LRETURN: case IR.FRETURN: case IR.DRETURN: case IR.ARETURN: case IR.VRETURN:
      case IR.ATHROW:
        inout++;
        break;
      case IR.IPASS: case IR.LPASS: case IR.FPASS: case IR.DPASS: case IR.APASS:
      case IR.IRESULT: case IR.LRESULT: case IR.FRESULT: case IR.DRESULT: case IR.ARESULT:
      case IR.ACATCH:
      case IR.IDEFINE: case IR.LDEFINE: case IR.FDEFINE: case IR.DDEFINE: case IR.ADEFINE:
      case IR.BSTORE: case IR.SSTORE: case IR.ISTORE: case IR.LSTORE: case IR.FSTORE: case IR.DSTORE: case IR.ASTORE:
      case IR.BASTORE: case IR.SASTORE: case IR.IASTORE: case IR.LASTORE: case IR.FASTORE: case IR.DASTORE: case IR.AASTORE:
      case IR.INIT: case IR.INITX:
      case IR.NEWINSTANCE: case IR.NEWINSTANCEX:
      case IR.NEWARRAY: case IR.NEWARRAYX:
      case IR.LOCK: case IR.LOCKX: case IR.UNLOCK:
      case IR.READBARRIER: case IR.WRITEBARRIER:
      case IR.LABEL:
      case IR.JUMP:
        break;
      case IR.IJUMP:
      case IR.AJUMP:
        jumps++;
        break;
      case IR.NCALL: case IR.NCALLX:
        calls++;
        break;
      case IR.CALL: case IR.CALLX:
        calls++;
        if (mlookup.equals(snode.left())) return false; //avoid loop on recursive
        break;
      default: return false;
      }
    }
    if (inout == cfg.stmtCount())
      return true;
    int depth = callStmt.ownerBB().loopDepth();
    if (depth > 0) {
      if (jumps > 1) return false;
      if (calls > 1) return false;
      return true;
    }
    return false;
  }

  private static boolean level1(IRStatement callStmt, IR.mlookup mlookup, IRCFG cfg) {
    int inout = 0;
    int jumps = 0;
    int calls = 0;
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      IR.snode snode = stmt.snode();
      switch (snode.op()) {
      case IR.IRECEIVE: case IR.LRECEIVE: case IR.FRECEIVE: case IR.DRECEIVE: case IR.ARECEIVE:
      case IR.IRETURN: case IR.LRETURN: case IR.FRETURN: case IR.DRETURN: case IR.ARETURN: case IR.VRETURN:
      case IR.ATHROW:
        inout++;
        break;
      case IR.IPASS: case IR.LPASS: case IR.FPASS: case IR.DPASS: case IR.APASS:
      case IR.IRESULT: case IR.LRESULT: case IR.FRESULT: case IR.DRESULT: case IR.ARESULT:
      case IR.ACATCH:
      case IR.IDEFINE: case IR.LDEFINE: case IR.FDEFINE: case IR.DDEFINE: case IR.ADEFINE:
      case IR.BSTORE: case IR.SSTORE: case IR.ISTORE: case IR.LSTORE: case IR.FSTORE: case IR.DSTORE: case IR.ASTORE:
      case IR.BASTORE: case IR.SASTORE: case IR.IASTORE: case IR.LASTORE: case IR.FASTORE: case IR.DASTORE: case IR.AASTORE:
      case IR.INIT: case IR.INITX:
      case IR.NEWINSTANCE: case IR.NEWINSTANCEX:
      case IR.NEWARRAY: case IR.NEWARRAYX:
      case IR.LOCK: case IR.LOCKX: case IR.UNLOCK:
      case IR.READBARRIER: case IR.WRITEBARRIER:
      case IR.LABEL:
      case IR.JUMP:
        break;
      case IR.IJUMP:
      case IR.AJUMP:
        jumps++;
        break;
      case IR.NCALL: case IR.NCALLX:
        calls++;
        break;
      case IR.CALL: case IR.CALLX:
        calls++;
        if (mlookup.equals(snode.left())) return false; //avoid loop on recursive
        break;
      default: return false;
      }
    }
    if (inout == cfg.stmtCount())
      return true;
    int depth = callStmt.ownerBB().loopDepth();
    boolean dominates = ((BasicBlock)callStmt.ownerBB().ownerCFG().entryBBs().next()).dominates(callStmt.ownerBB());
    if (depth > 0 || dominates) {
      if (jumps > 10) return false;
      if (calls > 10) return false;
      return true;
    }
    return false;
  }

  private static void inlineCall(IRStatement callStmt, IRCFG tee) {

    //System.err.println("*PROCEDURE INTEGRATION*");

    IR.tnode callSnode = (IR.tnode)callStmt.snode();
    BasicBlock callBB = callStmt.ownerBB();
    IRCFG tor = (IRCFG)callBB.ownerCFG();
    
    BasicBlock xBB = null;
    int xlabel = -1;
    int creg = -1;
    BasicBlock cBB = null;
    int clabel = -1;

    BasicBlock resultBB;
    if (callStmt == callBB.trailer()) {

      resultBB = callBB.downBB();

      IRControlEdge xedge = null;
      for (Iterator i = callBB.outEdges(); i.hasNext(); ) {
        IRControlEdge edge = (IRControlEdge)i.next();
        i.remove();
        if (edge.type() == IRControlEdge.EXCEPT)
          xedge = edge;
      }

      if (xedge != null) {
        xBB = xedge.targetBB();
        IRStatement leader = (IRStatement)xBB.leader();
        IR.label label = (IR.label)leader.snode();
        xlabel = label.getValue();
        IRStatement trailer = leader;
        IRStatement catcher = (IRStatement)leader.nextStmt();
        if (catcher.snode().op() == IR.ACATCH) {
          IR.acatch acatch = (IR.acatch)catcher.snode();
          creg = acatch.getReg();
          trailer = catcher;
        }
        if (trailer == xBB.trailer())
          cBB = xBB.downBB();
        else {
          cBB = new IRBasicBlock();
          tor.insertBBAfterBB(cBB, xBB);
          for (Iterator i = xBB.bottomupStmts(); i.hasNext(); ) {
            IRStatement stmt = (IRStatement)i.next();
            if (stmt == trailer)
              break;
            i.remove();
            cBB.prependStmt(stmt);
          }
          for (Iterator i = xBB.outEdges(); i.hasNext(); ) {
            IRControlEdge edge = (IRControlEdge)i.next();
            i.remove();
            tor.addEdge(edge.clone(cBB, edge.targetBB()));
          }
          tor.addEdge(new IRControlEdge(xBB, IRControlEdge.FALL, cBB));
        }
        leader = (IRStatement)cBB.leader();
        if (leader == null || leader.snode().op() != IR.LABEL) {
          clabel = tor.labelSeq();
          cBB.prependStmt(new IRStatement(new IR.label(clabel)));
        } else {
          label = (IR.label)leader.snode();
          clabel = label.getValue();
        }
      }

    } else {

      resultBB = new IRBasicBlock();
      tor.insertBBAfterBB(resultBB, callBB);

      for (Iterator i = callBB.bottomupStmts(); i.hasNext(); ) {
        IRStatement stmt = (IRStatement)i.next();
        if (stmt == callStmt)
          break;
        i.remove();
        resultBB.prependStmt(stmt);
      }

      for (Iterator i = callBB.outEdges(); i.hasNext(); ) {
        IRControlEdge edge = (IRControlEdge)i.next();
        i.remove();
        tor.addEdge(edge.clone(resultBB, edge.targetBB()));
      }

    }

    int labelBase = tor.labelMax();
    int regBase = tor.iregMax();
    if (regBase < tor.lregMax()) regBase = tor.lregMax()-1;
    if (regBase < tor.fregMax()) regBase = tor.fregMax()-2;
    if (regBase < tor.dregMax()) regBase = tor.dregMax()-3;
    if (regBase < tor.aregMax()) regBase = tor.aregMax()-4;

    int resultReg = -1;
    IRStatement resultStmt = (IRStatement)callStmt.nextStmt();
    IR.snode resultSnode = resultStmt.snode();
    switch (resultSnode.op()) {
    case IR.IRESULT:
      IR.iresult iresult = (IR.iresult)resultSnode;
      resultReg = iresult.getReg();
      resultBB.removeStmt(resultStmt);
      break;
    case IR.LRESULT:
      IR.lresult lresult = (IR.lresult)resultSnode;
      resultReg = lresult.getReg();
      resultBB.removeStmt(resultStmt);
      break;
    case IR.FRESULT:
      IR.fresult fresult = (IR.fresult)resultSnode;
      resultReg = fresult.getReg();
      resultBB.removeStmt(resultStmt);
      break;
    case IR.DRESULT:
      IR.dresult dresult = (IR.dresult)resultSnode;
      resultReg = dresult.getReg();
      resultBB.removeStmt(resultStmt);
      break;
    case IR.ARESULT:
      IR.aresult aresult = (IR.aresult)resultSnode;
      resultReg = aresult.getReg();
      resultBB.removeStmt(resultStmt);
      break;
    }

    callBB.removeStmt(callStmt);

    BasicBlock[] mapped = new BasicBlock[tee.count()];

    int index = 0;
    for (Iterator i = tee.topdownBBs(); i.hasNext(); index++) {
      BasicBlock currentBB = (BasicBlock)i.next();
      BasicBlock clonedBB = (BasicBlock)currentBB.clone();
      mapped[index] = clonedBB;
      tor.appendBB(clonedBB);

      for (Iterator j = clonedBB.topdownStmts(); j.hasNext(); ) {
        IRStatement stmt = (IRStatement)j.next();
        Matcher2 matcher = new Matcher2(stmt);
        matcher.cover(callSnode, labelBase, regBase);
      }

    }

    for (Iterator i = tee.edges(); i.hasNext(); ) {
      ControlEdge edge = (ControlEdge)i.next();
      ControlEdge clonedEdge = edge.clone(mapped[tee.indexOf(edge.sourceBB())], mapped[tee.indexOf(edge.targetBB())]);
      tor.addEdge(clonedEdge);
    }

    tor.invalidate();

    int elabel = tor.labelSeq();
    int rlabel;

    IRStatement leader = (IRStatement)resultBB.leader();
    if (leader == null || leader.snode().op() != IR.LABEL) {
      rlabel = tor.labelSeq();
      resultBB.prependStmt(new IRStatement(new IR.label(rlabel)));
    } else {
      IR.label label = (IR.label)leader.snode();
      rlabel = label.getValue();
    }

    BasicBlock entryBB = mapped[0];

    int paramCount = 0;
    for (Iterator i = entryBB.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      IR.snode snode = stmt.snode();
      switch (snode.op()) {
      case IR.IRECEIVE:
      case IR.LRECEIVE:
      case IR.FRECEIVE:
      case IR.DRECEIVE:
      case IR.ARECEIVE:
        paramCount++;
        continue;
      }
      break;
    }

    int[] paramReg = new int[paramCount];
    paramCount = 0;
    for (Iterator i = entryBB.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      IR.snode snode = stmt.snode();
      switch (snode.op()) {
      case IR.IRECEIVE:
        IR.ireceive ireceive = (IR.ireceive)snode;
        paramReg[paramCount++] = ireceive.getReg();
        i.remove();
        continue;
      case IR.LRECEIVE:
        IR.lreceive lreceive = (IR.lreceive)snode;
        paramReg[paramCount++] = lreceive.getReg();
        i.remove();
        continue;
      case IR.FRECEIVE:
        IR.freceive freceive = (IR.freceive)snode;
        paramReg[paramCount++] = freceive.getReg();
        i.remove();
        continue;
      case IR.DRECEIVE:
        IR.dreceive dreceive = (IR.dreceive)snode;
        paramReg[paramCount++] = dreceive.getReg();
        i.remove();
        continue;
      case IR.ARECEIVE:
        IR.areceive areceive = (IR.areceive)snode;
        paramReg[paramCount++] = areceive.getReg();
        i.remove();
        continue;
      }
      break;
    }

    BasicBlock rootBB = new IRBasicBlock();
    rootBB.appendStmt(new IRStatement(new IR.label(elabel)));
    tor.insertBBBeforeBB(rootBB, entryBB);
    tor.addEdge(new IRControlEdge(rootBB, IRControlEdge.FALL, entryBB));
    tor.addEdge(new IRControlEdge(callBB, IRControlEdge.JUMP, rootBB));

    for (int i = 0; i < mapped.length; i++) {
      BasicBlock currentBB = mapped[i];
      if (currentBB.outDegree() == 0) {
        IRStatement trailer = (IRStatement)currentBB.trailer();
        IR.snode snode = trailer.snode();
        switch (snode.op()) {
        case IR.IRETURN:
          currentBB.removeStmt(trailer);
          IR.ireturn ireturn = (IR.ireturn)snode;
          if (resultReg != -1)
            currentBB.appendStmt(new IRStatement(new IR.idefine(resultReg, (IR.inode)ireturn.left())));
          currentBB.appendStmt(new IRStatement(new IR.jump(rlabel)));
          tor.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, resultBB));
          break;
        case IR.LRETURN:
          currentBB.removeStmt(trailer);
          IR.lreturn lreturn = (IR.lreturn)snode;
          if (resultReg != -1)
            currentBB.appendStmt(new IRStatement(new IR.ldefine(resultReg, (IR.lnode)lreturn.left())));
          currentBB.appendStmt(new IRStatement(new IR.jump(rlabel)));
          tor.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, resultBB));
          break;
        case IR.FRETURN:
          currentBB.removeStmt(trailer);
          IR.freturn freturn = (IR.freturn)snode;
          if (resultReg != -1)
            currentBB.appendStmt(new IRStatement(new IR.fdefine(resultReg, (IR.fnode)freturn.left())));
          currentBB.appendStmt(new IRStatement(new IR.jump(rlabel)));
          tor.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, resultBB));
          break;
        case IR.DRETURN:
          currentBB.removeStmt(trailer);
          IR.dreturn dreturn = (IR.dreturn)snode;
          if (resultReg != -1)
            currentBB.appendStmt(new IRStatement(new IR.ddefine(resultReg, (IR.dnode)dreturn.left())));
          currentBB.appendStmt(new IRStatement(new IR.jump(rlabel)));
          tor.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, resultBB));
          break;
        case IR.ARETURN:
          currentBB.removeStmt(trailer);
          IR.areturn areturn = (IR.areturn)snode;
          if (resultReg != -1)
            currentBB.appendStmt(new IRStatement(new IR.adefine(resultReg, (IR.anode)areturn.left())));
          currentBB.appendStmt(new IRStatement(new IR.jump(rlabel)));
          tor.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, resultBB));
          break;
        case IR.VRETURN:
          currentBB.removeStmt(trailer);
          IR.vreturn vreturn = (IR.vreturn)snode;
          currentBB.appendStmt(new IRStatement(new IR.jump(rlabel)));
          tor.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, resultBB));
          break;
        }
      }
    }

    int paramIndex = 0;
    for (Iterator i = callBB.bottomupStmts(); paramIndex < paramCount; paramIndex++) {
      IRStatement stmt = (IRStatement)i.next();
      IR.snode snode = (IR.snode)stmt.snode();
      switch (snode.op()) {
      case IR.IPASS:
        IR.ipass ipass = (IR.ipass)snode;
        stmt.setNode(new IR.idefine(paramReg[paramIndex], (IR.inode)ipass.left()));
        break;
      case IR.LPASS:
        IR.lpass lpass = (IR.lpass)snode;
        stmt.setNode(new IR.ldefine(paramReg[paramIndex], (IR.lnode)lpass.left()));
        break;
      case IR.FPASS:
        IR.fpass fpass = (IR.fpass)snode;
        stmt.setNode(new IR.fdefine(paramReg[paramIndex], (IR.fnode)fpass.left()));
        break;
      case IR.DPASS:
        IR.dpass dpass = (IR.dpass)snode;
        stmt.setNode(new IR.ddefine(paramReg[paramIndex], (IR.dnode)dpass.left()));
        break;
      case IR.APASS:
        IR.apass apass = (IR.apass)snode;
        stmt.setNode(new IR.adefine(paramReg[paramIndex], (IR.anode)apass.left()));
        break;
      }
    }

    callBB.appendStmt(new IRStatement(new IR.jump(elabel)));

    if (xBB != null)
      for (BasicBlock currentBB = rootBB; currentBB != null; currentBB = currentBB.downBB())
        for (IRStatement stmt = (IRStatement)currentBB.leader(); stmt != null; stmt = (IRStatement)stmt.next()) {
          IR.snode snode = stmt.snode();
          IR.tnode tnode;
          switch (snode.op()) {
          case IR.CALL:
            IR.call call = (IR.call)snode;
            tnode = new IR.callx((IR.anode)call.left(), xlabel);
            break;
          case IR.NCALL:
            IR.ncall ncall = (IR.ncall)snode;
            tnode = new IR.ncallx(ncall.getSymbolicType(), ncall.getName(), ncall.getDescriptor(), xlabel);
            break;
          case IR.INIT:
            IR.init init = (IR.init)snode;
            tnode = new IR.initx((IR.anode)init.left(), xlabel);
            break;
          case IR.NEWINSTANCE:
            IR.newinstance newinstance = (IR.newinstance)snode;
            tnode = new IR.newinstancex((IR.anode)newinstance.left(), xlabel);
            break;
          case IR.NEWARRAY:
            IR.newarray newarray = (IR.newarray)snode;
            tnode = new IR.newarrayx((IR.anode)newarray.left(), (IR.inode)newarray.right(), xlabel);
            break;
          case IR.LOCK:
            IR.lock lock = (IR.lock)snode;
            tnode = new IR.lockx((IR.anode)lock.left(), xlabel);
            break;
          default:
            continue;
          }
          appendTrace((IR.tnode)snode, tnode);
          stmt.setNode(tnode);
          if (stmt != currentBB.trailer()) {
            BasicBlock tailBB = new IRBasicBlock();
            tor.insertBBAfterBB(tailBB, currentBB);
            for (Iterator i = currentBB.bottomupStmts(); i.hasNext(); ) {
              IRStatement istmt = (IRStatement)i.next();
              if (istmt == stmt)
                break;
              i.remove();
              tailBB.prependStmt(istmt);
            }
            for (Iterator i = currentBB.outEdges(); i.hasNext(); ) {
              IRControlEdge edge = (IRControlEdge)i.next();
              i.remove();
              tor.addEdge(edge.clone(tailBB, edge.targetBB()));
            }
            tor.addEdge(new IRControlEdge(currentBB, IRControlEdge.FALL, tailBB));
          }
          tor.addEdge(new IRControlEdge(currentBB, IRControlEdge.EXCEPT, xBB));
        }

    if (cBB != null)
      for (BasicBlock currentBB = rootBB; currentBB != null; currentBB = currentBB.downBB())
        if (currentBB.outDegree() == 0) {
          IRStatement trailer = (IRStatement)currentBB.trailer();
          if (trailer.snode().op() == IR.ATHROW) {
            IR.athrow athrow = (IR.athrow)trailer.snode();
            if (creg == -1)
              currentBB.removeStmt(trailer);
            else
              trailer.setNode(new IR.adefine(creg, (IR.anode)athrow.left()));
            currentBB.appendStmt(new IRStatement(new IR.jump(clabel)));
            tor.addEdge(new IRControlEdge(currentBB, IRControlEdge.JUMP, cBB));
          }
        }

    ConstraintNamespace namespace = tor.getNamespace();
    namespace.addAll(tee.getNamespace());
  }

  private static final class Matcher2 {

    public Matcher2(IRStatement stmt) {
      this(stmt.snode());
    }

    public void cover(IR.tnode call, int labelBase, int regBase)
      : IR.LABEL = { @1.setValue(labelBase+@1.getValue()); }

      | IR.JUMP = { @1.setTarget(labelBase+@1.getTarget()); }

      | IR.AJUMP(uses,uses) = {
        @2(regBase);
        @3(regBase);
        @1.setTarget(labelBase+@1.getTarget());
      }

      | IR.IJUMP(uses,uses) = { 
        @2(regBase);
        @3(regBase);
        @1.setTarget(labelBase+@1.getTarget());
      }

      | IR.ISWITCH(uses) = {
        @2(regBase);
        for (Iterator i = @1.pairs(); i.hasNext(); ) {
          IR.iswitch.pair pair = (IR.iswitch.pair)i.next();
          pair.setTarget(labelBase+pair.getTarget());
        }
      }

      | IR.CALL(uses) = { 
        @2(regBase);
        appendTrace(call, @1); 
      }

      | IR.CALLX(uses) = { 
        @2(regBase);
        @1.setHandler(labelBase+@1.getHandler()); 
        appendTrace(call, @1);
      }

      | IR.NCALL = { appendTrace(call, @1); }

      | IR.NCALLX = { 
        @1.setHandler(labelBase+@1.getHandler()); 
        appendTrace(call, @1);
      }

      | IR.INIT(uses) = {
        @2(regBase);
        appendTrace(call, @1);
      }

      | IR.INITX(uses) = { 
        @2(regBase);
        @1.setHandler(labelBase+@1.getHandler()); 
        appendTrace(call, @1);
      }

      | IR.NEWINSTANCE(uses) = { 
        @2(regBase);
        appendTrace(call, @1);
      }

      | IR.NEWINSTANCEX(uses) = { 
        @2(regBase);
        @1.setHandler(labelBase+@1.getHandler()); 
        appendTrace(call, @1);
      }

      | IR.NEWARRAY(uses,uses) = {
        @2(regBase);
        @3(regBase);
        appendTrace(call, @1);
      }

      | IR.NEWARRAYX(uses,uses) = {
        @2(regBase);
        @3(regBase);
        @1.setHandler(labelBase+@1.getHandler());
        appendTrace(call, @1);
      }

      | IR.LOCK(uses) = {
        @2(regBase);
        appendTrace(call, @1);
      }

      | IR.LOCKX(uses) = {
        @2(regBase);
        @1.setHandler(labelBase+@1.getHandler());
        appendTrace(call, @1);
      }

      | IR.IDEFINE(uses) = {
        @2(regBase);
        @1.setReg(regBase+@1.getReg());
      }

      | IR.LDEFINE(uses) = {
        @2(regBase);
        @1.setReg(regBase+@1.getReg());
      }

      | IR.FDEFINE(uses) = {
        @2(regBase);
        @1.setReg(regBase+@1.getReg());
      }

      | IR.DDEFINE(uses) = {
        @2(regBase);
        @1.setReg(regBase+@1.getReg());
      }

      | IR.ADEFINE(uses) = {
        @2(regBase);
        @1.setReg(regBase+@1.getReg());
      }

      | IR.IPHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, regBase+@1.getReg(edge));
        }
        @1.setReg(regBase+@1.getReg());
      }
      | IR.LPHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, regBase+@1.getReg(edge));
        }
        @1.setReg(regBase+@1.getReg());
      }
      | IR.FPHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, regBase+@1.getReg(edge));
        }
        @1.setReg(regBase+@1.getReg());
      }
      | IR.DPHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, regBase+@1.getReg(edge));
        }
        @1.setReg(regBase+@1.getReg());
      }
      | IR.APHI = {
        for (Iterator i = @1.regs(); i.hasNext(); ) {
          IRControlEdge edge = (IRControlEdge)i.next();
          @1.setReg(edge, regBase+@1.getReg(edge));
        }
        @1.setReg(regBase+@1.getReg());
      }

      | IR.ACATCH = { @1.setReg(regBase+@1.getReg()); }

      | IR.IRECEIVE = { @1.setReg(regBase+@1.getReg()); }
      | IR.LRECEIVE = { @1.setReg(regBase+@1.getReg()); }
      | IR.FRECEIVE = { @1.setReg(regBase+@1.getReg()); }
      | IR.DRECEIVE = { @1.setReg(regBase+@1.getReg()); }
      | IR.ARECEIVE = { @1.setReg(regBase+@1.getReg()); }

      | IR.IRESULT = { @1.setReg(regBase+@1.getReg()); }
      | IR.LRESULT = { @1.setReg(regBase+@1.getReg()); }
      | IR.FRESULT = { @1.setReg(regBase+@1.getReg()); }
      | IR.DRESULT = { @1.setReg(regBase+@1.getReg()); }
      | IR.ARESULT = { @1.setReg(regBase+@1.getReg()); }

      | default = {
        if (  left$ != null)   left$.uses(regBase);
        if (middle$ != null) middle$.uses(regBase);
        if ( right$ != null)  right$.uses(regBase);
      }
      ;

    private void uses(int regBase)
      : IR.IUSE = { @1.setReg(regBase+@1.getReg()); }
      | IR.LUSE = { @1.setReg(regBase+@1.getReg()); }
      | IR.FUSE = { @1.setReg(regBase+@1.getReg()); }
      | IR.DUSE = { @1.setReg(regBase+@1.getReg()); }
      | IR.AUSE = { @1.setReg(regBase+@1.getReg()); }

      | default = {
        if (  left$ != null)   left$.uses(regBase);
        if (middle$ != null) middle$.uses(regBase);
        if ( right$ != null)  right$.uses(regBase);
      }
      ;

  }

  private static void appendTrace(IR.tnode source, IR.tnode target) {
    for (Iterator i = source.traces(); i.hasNext(); ) {
      IR.tnode.trace trace = (IR.tnode.trace)i.next();
      target.append(trace);
    }
  }

}

