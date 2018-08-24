/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.ConstraintNamespace;
import jewel.core.Context;
import jewel.core.ContextClassInfo;
import jewel.core.LoadedClassInfo;
import jewel.core.LoadedClassInfo.PlacedFieldInfo;
import jewel.core.LoadedClassInfo.PlacedMethodInfo;
import jewel.core.clfile.AbstractMethodException;
import jewel.core.clfile.Bytecode;
import jewel.core.clfile.ClassFileConstants;
import jewel.core.clfile.ClassInfo;
import jewel.core.clfile.ClassInfo.ConstantPool;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo.HandlerInfo;
import jewel.core.clfile.IncompatibleClassChangeException;
import jewel.core.clfile.Syntax;
import jewel.core.clfile.VerifyException;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.DataFlowAnalyser;
import jewel.core.jiro.dataflow.DataFlowAnalysis;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;

import java.util.ArrayList;
import java.util.HashMap;

public final class IRConverter implements ClassFileConstants {

  public IRConverter() { }

  public synchronized IRCFG convert(MethodInfo method) throws VerifyException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, IncompatibleClassChangeException, AbstractMethodException {

    reuses.clear();

    IRCFG cfg;
    ConstraintNamespace namespace = new ConstraintNamespace();
    CodeInfo code = method.getCode();
    if (code != null) {
      try {
        code.checkStructuralConstraints(namespace);
      } catch (VerifyException e) {
        throw new VerifyException("(class: "+method.getOwner().getName()+", method: "+method.getName()+" signature: "+method.getDescriptor()+") "+e.getMessage());
      }
      byte[] text = code.getText();
      int[] textInfo = code.getTextInfo();
      HandlerInfo[] handlers = code.getHandlers();
      cfg = translate(code, text, textInfo, handlers);
    } else if (method.isAbstract())
      cfg = genAbstract(method);
    else
      cfg = genNative(method);
    if (method.isStatic())
      cfg.setStatic(true);
    takeCareOfUninitialized(cfg);
    cfg.setNamespace(namespace);
    return cfg;
  }

  private static void takeCareOfUninitialized(IRCFG cfg) {
    DataFlowAnalyser analyser = new IterativeDataFlowAnalyser(new LiveVariables());

    IRStatement stmt = (IRStatement)cfg.firstStmt();
    BitItem value = (BitItem)analyser.valueBefore(stmt);
    int length = value.length();
    for (int j = 0; j < length; j++)
      if (value.get(j)) {
        IRStatement newStmt = null;
        switch (j % 5) {
        case 0:
          newStmt = new IRStatement(new IR.idefine(j, new IR.iconst(0)));
          break;
        case 1:
          newStmt = new IRStatement(new IR.ldefine(j, new IR.lconst(0)));
          break;
        case 2:
          newStmt = new IRStatement(new IR.fdefine(j, new IR.fconst(0)));
          break;
        case 3:
          newStmt = new IRStatement(new IR.ddefine(j, new IR.dconst(0)));
          break;
        case 4:
          newStmt = new IRStatement(new IR.adefine(j, new IR.anull()));
          break;
        }
        boolean isFirst = true;
        int op = stmt.snode().op();
        while (op == IR.IRECEIVE || op == IR.LRECEIVE || op == IR.FRECEIVE || op == IR.DRECEIVE || op == IR.ARECEIVE) {
          stmt = (IRStatement)stmt.nextStmt();
          isFirst = false;
          op = stmt.snode().op();
        }
        if (isFirst)
          stmt.ownerBB().insertStmtBeforeStmt(newStmt, stmt);
        else {
          stmt = (IRStatement)stmt.previousStmt();
          stmt.ownerBB().insertStmtAfterStmt(newStmt, stmt);
        }
      }
  }

  private static void generateException(CFGBuilder builder, Regs r, String name) {
    Context context = Context.get();
    LoadedClassInfo clazz = context.forName(name);
    PlacedMethodInfo callee = (PlacedMethodInfo)clazz.lookupMethod("<init>", "()V");
    builder.init(name);
    builder.newinstance(name);
    builder.aresult(r.ex(), name);
    builder.apass(r.ex());
    builder.scall(name, callee.getDispatchIndex());
    builder.athrow(r.ex());
  }

  private transient HashMap reuses = new HashMap();
  private static final class Reuse {
    private int handler;
    private int line;
    private String name;
    Reuse(int handler, int line, String name) {
      this.handler = handler;
      this.line = line;
      this.name = name;
    }
    public int hashCode() {
      return handler+line+name.hashCode();
    }
    public boolean equals(Object object) {
      return object instanceof Reuse
          && ((Reuse)object).handler == handler
          && ((Reuse)object).line == line
          && ((Reuse)object).name.equals(name);
    }
  }
  private void generateException(CFGBuilder builder, NestedHandler handler, Regs r, MethodInfo method, int line, String name) {
    
    Reuse reuse = new Reuse(handler == null ? -1 : handler.getIndex(), line, name);
    Integer sl = (Integer)reuses.get(reuse);
    if (sl != null) {
      builder.jump(sl.intValue());
      return;
    }
    sl = new Integer(r.sl());
    reuses.put(reuse, sl);
    builder.label(sl.intValue());

    Context context = Context.get();
    LoadedClassInfo clazz = context.forName(name);
    PlacedMethodInfo callee = (PlacedMethodInfo)clazz.lookupMethod("<init>", "()V");
    if (handler == null) {
      builder.init(name, method, line);
      builder.newinstance(name, method, line);
      builder.aresult(r.ex(), name);
      builder.apass(r.ex());
      builder.scall(name, callee.getDispatchIndex(), method, line);
      builder.athrow(r.ex());
    } else {
      builder.initx(name, r.cl(handler.getIndex()), method, line);
      builder.newinstancex(name, r.cl(handler.getIndex()), method, line);
      builder.aresult(r.ex(), name);
      builder.apass(r.ex());
      builder.scallx(name, callee.getDispatchIndex(), r.cl(handler.getIndex()), method, line);
      builder.jump(r.hl(handler.getIndex()));
    }
  }

  private static void genExceptionInInitializerError(CFGBuilder builder, Regs r) {
    String name = "java/lang/ExceptionInInitializerError";
    Context context = Context.get();
    LoadedClassInfo clazz = context.forName(name);
    PlacedMethodInfo callee = (PlacedMethodInfo)clazz.lookupMethod("<init>", "(Ljava/lang/Throwable;)V");
    builder.init(name);
    builder.newinstance(name);
    builder.aresult(r.ex(), name);
    builder.apass(r.st(-1));
    builder.apass(r.ex());
    builder.scall(name, callee.getDispatchIndex());
    builder.athrow(r.ex());
  }

  private static final class ReuseI {
    private int handler;
    private int line;
    private int reg;
    ReuseI(int handler, int line, int reg) {
      this.handler = handler;
      this.line = line;
      this.reg = reg;
    }
    public int hashCode() {
      return handler+line+reg;
    }
    public boolean equals(Object object) {
      return object instanceof ReuseI
          && ((ReuseI)object).handler == handler
          && ((ReuseI)object).line == line
          && ((ReuseI)object).reg == reg;
    }
  }
  private void genArrayIndexOutOfBoundsException(CFGBuilder builder, NestedHandler handler, Regs r, MethodInfo method, int line, int fault) {
    
    ReuseI reuse = new ReuseI(handler == null ? -1 : handler.getIndex(), line, r.st(fault));
    Integer sl = (Integer)reuses.get(reuse);
    if (sl != null) {
      builder.jump(sl.intValue());
      return;
    }
    sl = new Integer(r.sl());
    reuses.put(reuse, sl);
    builder.label(sl.intValue());

    String name = "java/lang/ArrayIndexOutOfBoundsException";
    Context context = Context.get();
    LoadedClassInfo clazz = context.forName(name);
    PlacedMethodInfo callee = (PlacedMethodInfo)clazz.lookupMethod("<init>", "(I)V");
    if (handler == null) {
      builder.init(name, method, line);
      builder.newinstance(name, method, line);
      builder.aresult(r.ex(), name);
      builder.ipass(r.st(fault));
      builder.apass(r.ex());
      builder.scall(name, callee.getDispatchIndex(), method, line);
      builder.athrow(r.ex());
    } else {
      builder.initx(name, r.cl(handler.getIndex()), method, line);
      builder.newinstancex(name, r.cl(handler.getIndex()), method, line);
      builder.aresult(r.ex(), name);
      builder.ipass(r.st(fault));
      builder.apass(r.ex());
      builder.scallx(name, callee.getDispatchIndex(), r.cl(handler.getIndex()), method, line);
      builder.jump(r.hl(handler.getIndex()));
    }
  }

  private static void genClassCastException(CFGBuilder builder, NestedHandler handler, Regs r, MethodInfo method, int line) {
    String name = "java/lang/ClassCastException";
    Context context = Context.get();
    LoadedClassInfo clazz = context.forName(name);
    PlacedMethodInfo callee = (PlacedMethodInfo)clazz.lookupMethod("<init>", "(Ljava/lang/String;)V");
    String name2 = "java/lang/Class";
    LoadedClassInfo clazz2 = context.forName(name2);
    PlacedMethodInfo callee2 = (PlacedMethodInfo)clazz2.lookupMethod("getName", "()Ljava/lang/String;");
    if (handler == null) {
      builder.init(name, method, line);
      builder.newinstance(name, method, line);
      builder.aresult(r.ex(), name);

      builder.agetclass(r.tr(0), r.st(-1));
      builder.apass(r.tr(0));
      builder.scall(name2, callee2.getDispatchIndex(), method, line);
      builder.aresult(r.tr(0), "java/lang/String");
      builder.apass(r.tr(0));

      builder.apass(r.ex());
      builder.scall(name, callee.getDispatchIndex(), method, line);
      builder.athrow(r.ex());
    } else {
      builder.initx(name, r.cl(handler.getIndex()), method, line);
      builder.newinstancex(name, r.cl(handler.getIndex()), method, line);
      builder.aresult(r.ex(), name);

      builder.agetclass(r.tr(0), r.st(-1));
      builder.apass(r.tr(0));
      builder.scallx(name2, callee2.getDispatchIndex(), r.cl(handler.getIndex()), method, line);
      builder.aresult(r.tr(0), "java/lang/String");
      builder.apass(r.tr(0));

      builder.apass(r.ex());
      builder.scallx(name, callee.getDispatchIndex(), r.cl(handler.getIndex()), method, line);
      builder.jump(r.hl(handler.getIndex()));
    }
  }

  private static IRCFG genAbstract(MethodInfo method) {
    CFGBuilder builder = new CFGBuilder();
    Regs r = new Regs(255, 0, 0, 1, 0);
    int local = 0;
    builder.areceive(r.fr(local), method.getOwner().getName());
    local++;
    String[] params = Syntax.getParameterTypes(method.getDescriptor());
    for (int i = 0; i < params.length; i++) {
      String param = params[i];
      switch (param.charAt(0)) {
      case 'Z': case 'B': case 'C': case 'S': case 'I':
        builder.ireceive(r.fr(local));
        local++;
        break;
      case 'J':
        builder.lreceive(r.fr(local));
        local += 2;
        break;
      case 'F':
        builder.freceive(r.fr(local));
        local++;
        break;
      case 'D':
        builder.dreceive(r.fr(local));
        local += 2;
        break;
      case 'L':
        builder.areceive(r.fr(local), param.substring(1, param.length()-1));
        local++;
        break;
      case '[':
        builder.areceive(r.fr(local), param);
        local++;
        break;
      }
    }
    generateException(builder, r, "java/lang/AbstractMethodError");
    return builder.toCFG();
  }

  private static IRCFG genNative(MethodInfo method) {
    CFGBuilder builder = new CFGBuilder();
    Regs r = new Regs(255, 0, 0, 1, 0);
    int local = 0;
    if (!method.isStatic()) {
      builder.areceive(r.fr(local), method.getOwner().getName());
      local++;
    }
    String[] params = Syntax.getParameterTypes(method.getDescriptor());
    for (int i = 0; i < params.length; i++) {
      String param = params[i];
      switch (param.charAt(0)) {
      case 'Z': case 'B': case 'C': case 'S': case 'I':
        builder.ireceive(r.fr(local));
        local++;
        break;
      case 'J':
        builder.lreceive(r.fr(local));
        local += 2;
        break;
      case 'F':
        builder.freceive(r.fr(local));
        local++;
        break;
      case 'D':
        builder.dreceive(r.fr(local));
        local += 2;
        break;
      case 'L':
        builder.areceive(r.fr(local), param.substring(1, param.length()-1));
        local++;
        break;
      case '[':
        builder.areceive(r.fr(local), param);
        local++;
        break;
      }
    }

    r.changeTo(0, 0);

    if (method.isStatic())
      builder.aclass(r.th(), method.getOwner().getName());
    else
      builder.acopy(r.th(), r.fr(0));
    boolean synch = method.isSynchronized();
    if (synch)
      builder.alock(r.th());
    String descriptor = method.getDescriptor();
    params = Syntax.getParameterTypes(descriptor);
    for (int i = params.length-1; i >= 0; i--) {
      String type = params[i];
      switch (type.charAt(0)) {
      case 'Z': case 'B': case 'C': case 'S': case 'I':
        local--;
        builder.ipass(r.fr(local));
        break;
      case 'J':
        local -= 2;
        builder.lpass(r.fr(local));
        break;
      case 'F':
        local--;
        builder.fpass(r.fr(local));
        break;
      case 'D':
        local -= 2;
        builder.dpass(r.fr(local));
        break;
      case 'L': case '[':
        local--;
        builder.apass(r.fr(local));
        break;
      }
    }
    builder.apass(r.th());
    if (!synch)
      builder.ncall(method, -1);
    else
      builder.ncallx(r.ll(1), method, -1);
    String type = Syntax.getReturnType(descriptor);
    switch (type.charAt(0)) {
    case 'Z': case 'B': case 'C': case 'S': case 'I':
      builder.iresult(r.tr(0));
      break;
    case 'J':
      builder.lresult(r.tr(0));
      break;
    case 'F':
      builder.fresult(r.tr(0));
      if (method.isStrict())
        builder.fstrictcopy(r.tr(0), r.tr(0));
      break;
    case 'D':
      builder.dresult(r.tr(0));
      if (method.isStrict())
        builder.dstrictcopy(r.tr(0), r.tr(0));
      break;
    case 'L':
      builder.aresult(r.tr(0), type.substring(1, type.length()-1));
      break;
    case '[':
      builder.aresult(r.tr(0), type);
      break;
    case 'V':
      break;
    }
    if (synch) {
      builder.ajumplocked(r.th(), r.ll(0));
      generateException(builder, r, "java/lang/IllegalMonitorStateException");
      builder.label(r.ll(0));
      builder.aunlock(r.th());
    }
    switch (type.charAt(0)) {
    case 'Z': case 'B': case 'C': case 'S': case 'I':
      builder.ireturn(r.tr(0));
      break;
    case 'J':
      builder.lreturn(r.tr(0));
      break;
    case 'F':
      builder.freturn(r.tr(0));
      break;
    case 'D':
      builder.dreturn(r.tr(0));
      break;
    case 'L': case '[':
      builder.areturn(r.tr(0));
      break;
    case 'V':
      builder.vreturn();
      break;
    }
    if (synch) {
      builder.label(r.ll(1));
      builder.acatch(r.ex(), "java/lang/Throwable");
      builder.ajumplocked(r.th(), r.ll(2));
      generateException(builder, r, "java/lang/IllegalMonitorStateException");
      builder.label(r.ll(2));
      builder.aunlock(r.th());
      builder.athrow(r.ex());
    }
    return builder.toCFG();
  }

  private IRCFG translate(CodeInfo code, byte[] text, int[] textInfo, HandlerInfo[] hnds) throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, IncompatibleClassChangeException, AbstractMethodException {
    Context context = Context.get();
    int maxStack = code.getMaxStack();
    int maxLocals = code.getMaxLocals();
    MethodInfo method = code.getOwner();
    ConstantPool constantPool = method.getOwner().getConstantPool();

    boolean clinit = method.getName().equals("<clinit>");
    boolean synch = !clinit && method.isSynchronized();

    NestedHandler[] handlers = rewriteHandlers(hnds, text, synch || clinit);

    int maxDims = 0;
    for (int offset = 0; offset < text.length; offset++)
      if (Bytecode.isValid(textInfo, offset))
        if (Bytecode.opcodeAt(text, offset) == MULTIANEWARRAY) {
          int dims = Bytecode.ubyteAt(text, offset+3);
          if (dims > maxDims)
            maxDims = dims;
        }

    if ((synch || clinit) && maxStack == 0)
      maxStack = 1;

    Regs r = new Regs(maxLocals, maxStack, maxDims, text.length, handlers.length);

    CFGBuilder builder = new CFGBuilder();

    int local = 0;
    if (!method.isStatic() && !clinit) {
      builder.areceive(r.fr(local), method.getOwner().getName());
      local++;
    }
    String[] params = Syntax.getParameterTypes(method.getDescriptor());
    for (int i = 0; i < params.length; i++) {
      String param = params[i];
      switch (param.charAt(0)) {
      case 'Z': case 'B': case 'C': case 'S': case 'I':
        builder.ireceive(r.fr(local));
        local++;
        break;
      case 'J':
        builder.lreceive(r.fr(local));
        local += 2;
        break;
      case 'F':
        builder.freceive(r.fr(local));
        local++;
        break;
      case 'D':
        builder.dreceive(r.fr(local));
        local += 2;
        break;
      case 'L':
        builder.areceive(r.fr(local), param.substring(1, param.length()-1));
        local++;
        break;
      case '[':
        builder.areceive(r.fr(local), param);
        local++;
        break;
      }
    }

    if (synch) {
      if (method.isStatic())
        builder.aclass(r.th(), method.getOwner().getName());
      else
        builder.acopy(r.th(), r.fr(0));
      builder.alock(r.th());
    }

    if (clinit) {
      ClassInfo clazz = method.getOwner();
      for (int i = 0; i < clazz.getFieldCount(); i++) {
        PlacedFieldInfo field = (PlacedFieldInfo)clazz.getField(i);
        if (field.isStatic() && field.isFinal()) {
          Object constantValue = field.getConstantValue();
          if (constantValue != null)
            switch (field.getDescriptor().charAt(0)) {
            case 'Z': case 'B':
              builder.iconst(r.tr(0), ((Integer)constantValue).intValue());
              builder.sbstore(clazz.getName(), field.getOffset(), false, r.tr(0));
              break;
            case 'C': case 'S':
              builder.iconst(r.tr(0), ((Integer)constantValue).intValue());
              builder.ssstore(clazz.getName(), field.getOffset(), false, r.tr(0));
              break;
            case 'I':
              builder.iconst(r.tr(0), ((Integer)constantValue).intValue());
              builder.sistore(clazz.getName(), field.getOffset(), false, r.tr(0));
              break;
            case 'J':
              builder.lconst(r.tr(0), ((Long)constantValue).longValue());
              builder.slstore(clazz.getName(), field.getOffset(), false, r.tr(0));
              break;
            case 'F':
              builder.fconst(r.tr(0), ((Float)constantValue).floatValue());
              builder.sfstore(clazz.getName(), field.getOffset(), false, r.tr(0));
              break;
            case 'D':
              builder.dconst(r.tr(0), ((Double)constantValue).doubleValue());
              builder.sdstore(clazz.getName(), field.getOffset(), false, r.tr(0));
              break;
            case 'L':
              builder.astring(r.tr(0), (String)constantValue);
              builder.sastore(clazz.getName(), field.getOffset(), false, r.tr(0));
              break;
            }
        }
      }
    }

    int offset = 0;
    while (offset < text.length) {

      int line = code.lineOf(offset);

      if (Bytecode.isUnreachable(textInfo, offset)) {
        short opcode = Bytecode.opcodeAt(text, offset);
        int opsize;
        switch (opcode) {
        case TABLESWITCH:
          int pad = ~offset & 3;
          int low = Bytecode.intAt(text, offset+1+pad+4);
          int high = Bytecode.intAt(text, offset+1+pad+8);
          int entries = high-low+1;
          opsize = 1+pad+12+4*entries;
          break;
        case LOOKUPSWITCH:
          pad = ~offset & 3;
          entries = Bytecode.intAt(text, offset+1+pad+4);
          opsize = 1+pad+8+8*entries;
          break;
        default:
          opsize = Bytecode.sizeOf(opcode);
        }
        offset += opsize;
        continue;
      }

      r.changeTo(offset, Bytecode.getStackSize(textInfo, offset));

      if (Bytecode.isLeader(textInfo, offset))
        builder.label(r.gl(offset));

      short opcode = Bytecode.opcodeAt(text, offset);
      int opsize = Bytecode.sizeOf(opcode);

      NestedHandler handler = null;
      for (int i = 0; i < handlers.length; i++)
        if (handlers[i].encloses(offset)) {
          handler = handlers[i];
          break;
        }

      switch (opcode) {

      case NOP:
        break;

      case ACONST_NULL:
        builder.anull(r.st(0));
        break;

      case ICONST_M1:
        builder.iconst(r.st(0), -1);
        break;

      case ICONST_0:
        builder.iconst(r.st(0), 0);
        break;

      case ICONST_1:
        builder.iconst(r.st(0), 1);
        break;

      case ICONST_2:
        builder.iconst(r.st(0), 2);
        break;

      case ICONST_3:
        builder.iconst(r.st(0), 3);
        break;

      case ICONST_4:
        builder.iconst(r.st(0), 4);
        break;

      case ICONST_5:
        builder.iconst(r.st(0), 5);
        break;

      case LCONST_0:
        builder.lconst(r.st(0), 0L);
        break;

      case LCONST_1:
        builder.lconst(r.st(0), 1L);
        break;

      case FCONST_0:
        builder.fconst(r.st(0), 0.0F);
        break;

      case FCONST_1:
        builder.fconst(r.st(0), 1.0F);
        break;

      case FCONST_2:
        builder.fconst(r.st(0), 2.0F);
        break;

      case DCONST_0:
        builder.dconst(r.st(0), 0.0);
        break;

      case DCONST_1:
        builder.dconst(r.st(0), 1.0);
        break;

      case BIPUSH:
        int value = Bytecode.byteAt(text, offset+1);
        builder.iconst(r.st(0), value);
        break;

      case SIPUSH:
        value = Bytecode.shortAt(text, offset+1);
        builder.iconst(r.st(0), value);
        break;

      case LDC:
        int index = Bytecode.ubyteAt(text, offset+1);
        switch (constantPool.tag(index)) {
        case CONSTANT_INTEGER:
          builder.iconst(r.st(0), constantPool.getInteger(index));
          break;
        case CONSTANT_FLOAT:
          builder.fconst(r.st(0), constantPool.getFloat(index));
          break;
        case CONSTANT_STRING:
          builder.astring(r.st(0), constantPool.getString(index));
          break;
        }
        break;

      case LDC_W: 
        index = Bytecode.ushortAt(text, offset+1);
        switch (constantPool.tag(index)) {
        case CONSTANT_INTEGER:
          builder.iconst(r.st(0), constantPool.getInteger(index));
          break;
        case CONSTANT_FLOAT:
          builder.fconst(r.st(0), constantPool.getFloat(index));
          break;
        case CONSTANT_STRING:
          builder.astring(r.st(0), constantPool.getString(index));
          break;
        }
        break;

      case LDC2_W:
        index = Bytecode.ushortAt(text, offset+1);
        switch (constantPool.tag(index)) {
        case CONSTANT_LONG:
          builder.lconst(r.st(0), constantPool.getLong(index));
          break;
        case CONSTANT_DOUBLE:
          builder.dconst(r.st(0), constantPool.getDouble(index));
          break;
        }
        break;

      case ILOAD:
        index = Bytecode.ubyteAt(text, offset+1);
        builder.icopy(r.st(0), r.fr(index));
        break;

      case LLOAD:
        index = Bytecode.ubyteAt(text, offset+1);
        builder.lcopy(r.st(0), r.fr(index));
        break;

      case FLOAD:
        index = Bytecode.ubyteAt(text, offset+1);
        builder.fcopy(r.st(0), r.fr(index));
        break;

      case DLOAD:
        index = Bytecode.ubyteAt(text, offset+1);
        builder.dcopy(r.st(0), r.fr(index));
        break;

      case ALOAD:
        index = Bytecode.ubyteAt(text, offset+1);
        builder.acopy(r.st(0), r.fr(index));
        break;

      case ILOAD_0:
        builder.icopy(r.st(0), r.fr(0));
        break;

      case ILOAD_1:
        builder.icopy(r.st(0), r.fr(1));
        break;

      case ILOAD_2:
        builder.icopy(r.st(0), r.fr(2));
        break;

      case ILOAD_3:
        builder.icopy(r.st(0), r.fr(3));
        break;

      case LLOAD_0:
        builder.lcopy(r.st(0), r.fr(0));
        break;

      case LLOAD_1:
        builder.lcopy(r.st(0), r.fr(1));
        break;

      case LLOAD_2:
        builder.lcopy(r.st(0), r.fr(2));
        break;

      case LLOAD_3:
        builder.lcopy(r.st(0), r.fr(3));
        break;

      case FLOAD_0:
        builder.fcopy(r.st(0), r.fr(0));
        break;

      case FLOAD_1:
        builder.fcopy(r.st(0), r.fr(1));
        break;

      case FLOAD_2:
        builder.fcopy(r.st(0), r.fr(2));
        break;

      case FLOAD_3:
        builder.fcopy(r.st(0), r.fr(3));
        break;

      case DLOAD_0:
        builder.dcopy(r.st(0), r.fr(0));
        break;

      case DLOAD_1:
        builder.dcopy(r.st(0), r.fr(1));
        break;

      case DLOAD_2:
        builder.dcopy(r.st(0), r.fr(2));
        break;

      case DLOAD_3:
        builder.dcopy(r.st(0), r.fr(3));
        break;

      case ALOAD_0:
        builder.acopy(r.st(0), r.fr(0));
        break;

      case ALOAD_1:
        builder.acopy(r.st(0), r.fr(1));
        break;

      case ALOAD_2:
        builder.acopy(r.st(0), r.fr(2));
        break;

      case ALOAD_3:
        builder.acopy(r.st(0), r.fr(3));
        break;

      case IALOAD:
        builder.ajumpnonnull(r.st(-2), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-1), r.st(-2), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -1);
        builder.label(r.ll(1));
        builder.iaload(r.st(-2), r.st(-2), r.st(-1));
        break;

      case LALOAD:
        builder.ajumpnonnull(r.st(-2), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-1), r.st(-2), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -1);
        builder.label(r.ll(1));
        builder.laload(r.st(-2), r.st(-2), r.st(-1));
        break;

      case FALOAD:
        builder.ajumpnonnull(r.st(-2), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-1), r.st(-2), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -1);
        builder.label(r.ll(1));
        builder.faload(r.st(-2), r.st(-2), r.st(-1));
        break;

      case DALOAD:
        builder.ajumpnonnull(r.st(-2), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-1), r.st(-2), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -1);
        builder.label(r.ll(1));
        builder.daload(r.st(-2), r.st(-2), r.st(-1));
        break;

      case AALOAD:
        builder.ajumpnonnull(r.st(-2), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-1), r.st(-2), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -1);
        builder.label(r.ll(1));
        builder.aaload(r.st(-2), r.st(-2), r.st(-1));
        break;

      case BALOAD:
        builder.ajumpnonnull(r.st(-2), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-1), r.st(-2), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -1);
        builder.label(r.ll(1));
        builder.baload(r.st(-2), r.st(-2), r.st(-1));
        break;

      case CALOAD:
        builder.ajumpnonnull(r.st(-2), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-1), r.st(-2), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -1);
        builder.label(r.ll(1));
        builder.caload(r.st(-2), r.st(-2), r.st(-1));
        break;

      case SALOAD:
        builder.ajumpnonnull(r.st(-2), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-1), r.st(-2), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -1);
        builder.label(r.ll(1));
        builder.saload(r.st(-2), r.st(-2), r.st(-1));
        break;

      case ISTORE:
        index = Bytecode.ubyteAt(text, offset+1);
        builder.icopy(r.fr(index), r.st(-1));
        break;

      case LSTORE:
        index = Bytecode.ubyteAt(text, offset+1);
        builder.lcopy(r.fr(index), r.st(-2));
        break;
      
      case FSTORE:
        index = Bytecode.ubyteAt(text, offset+1);
        builder.fstrictcopy(r.fr(index), r.st(-1));
        break;
      
      case DSTORE:
        index = Bytecode.ubyteAt(text, offset+1);
        builder.dstrictcopy(r.fr(index), r.st(-2));
        break;

      case ASTORE:
        index = Bytecode.ubyteAt(text, offset+1);
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_ADDR:
          builder.icopy(r.fr(index), r.st(-1));
          break;
        case Bytecode.X_REF:
          builder.acopy(r.fr(index), r.st(-1));
          break;
        }
        break;

      case ISTORE_0:
        builder.icopy(r.fr(0), r.st(-1));
        break;

      case ISTORE_1:
        builder.icopy(r.fr(1), r.st(-1));
        break;

      case ISTORE_2:
        builder.icopy(r.fr(2), r.st(-1));
        break;

      case ISTORE_3:
        builder.icopy(r.fr(3), r.st(-1));
        break;

      case LSTORE_0:
        builder.lcopy(r.fr(0), r.st(-2));
        break;
      
      case LSTORE_1:
        builder.lcopy(r.fr(1), r.st(-2));
        break;
      
      case LSTORE_2:
        builder.lcopy(r.fr(2), r.st(-2));
        break;
      
      case LSTORE_3:
        builder.lcopy(r.fr(3), r.st(-2));
        break;
      
      case FSTORE_0:
        builder.fstrictcopy(r.fr(0), r.st(-1));
        break;
      
      case FSTORE_1:
        builder.fstrictcopy(r.fr(1), r.st(-1));
        break;
      
      case FSTORE_2:
        builder.fstrictcopy(r.fr(2), r.st(-1));
        break;
      
      case FSTORE_3:
        builder.fstrictcopy(r.fr(3), r.st(-1));
        break;
      
      case DSTORE_0:
        builder.dstrictcopy(r.fr(0), r.st(-2));
        break;

      case DSTORE_1:
        builder.dstrictcopy(r.fr(1), r.st(-2));
        break;

      case DSTORE_2:
        builder.dstrictcopy(r.fr(2), r.st(-2));
        break;

      case DSTORE_3:
        builder.dstrictcopy(r.fr(3), r.st(-2));
        break;

      case ASTORE_0:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_ADDR:
          builder.icopy(r.fr(0), r.st(-1));
          break;
        case Bytecode.X_REF:
          builder.acopy(r.fr(0), r.st(-1));
          break;
        }
        break;

      case ASTORE_1:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_ADDR:
          builder.icopy(r.fr(1), r.st(-1));
          break;
        case Bytecode.X_REF:
          builder.acopy(r.fr(1), r.st(-1));
          break;
        }
        break;

      case ASTORE_2:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_ADDR:
          builder.icopy(r.fr(2), r.st(-1));
          break;
        case Bytecode.X_REF:
          builder.acopy(r.fr(2), r.st(-1));
          break;
        }
        break;

      case ASTORE_3:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_ADDR:
          builder.icopy(r.fr(3), r.st(-1));
          break;
        case Bytecode.X_REF:
          builder.acopy(r.fr(3), r.st(-1));
          break;
        }
        break;

      case IASTORE:
        builder.ajumpnonnull(r.st(-3), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-2), r.st(-3), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -2);
        builder.label(r.ll(1));
        builder.iastore(r.st(-3), r.st(-2), r.st(-1));
        break;

      case LASTORE:
        builder.ajumpnonnull(r.st(-4), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-3), r.st(-4), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -3);
        builder.label(r.ll(1));
        builder.lastore(r.st(-4), r.st(-3), r.st(-2));
        break;

      case FASTORE:
        builder.ajumpnonnull(r.st(-3), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-2), r.st(-3), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -2);
        builder.label(r.ll(1));
        builder.fastore(r.st(-3), r.st(-2), r.st(-1));
        break;

      case DASTORE:
        builder.ajumpnonnull(r.st(-4), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-3), r.st(-4), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -3);
        builder.label(r.ll(1));
        builder.dastore(r.st(-4), r.st(-3), r.st(-2));
        break;

      case AASTORE:
        builder.ajumpnonnull(r.st(-3), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-2), r.st(-3), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -2);
        builder.label(r.ll(1));
        builder.ajumpnull(r.st(-1), r.ll(2));
        builder.ajumpcomptype(r.st(-1), r.st(-3), r.ll(2));
        generateException(builder, handler, r, method, line, "java/lang/ArrayStoreException");
        builder.label(r.ll(2));
        builder.aastore(r.st(-3), r.st(-2), r.st(-1));
        break;

      case BASTORE:
        builder.ajumpnonnull(r.st(-3), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-2), r.st(-3), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -2);
        builder.label(r.ll(1));
        builder.bastore(r.st(-3), r.st(-2), r.st(-1));
        break;

      case CASTORE:
      case SASTORE:
        builder.ajumpnonnull(r.st(-3), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ijumplength(r.st(-2), r.st(-3), r.ll(1));
        genArrayIndexOutOfBoundsException(builder, handler, r, method, line, -2);
        builder.label(r.ll(1));
        builder.sastore(r.st(-3), r.st(-2), r.st(-1));
        break;

      case POP:
        break;

      case POP2:
        break;

      case DUP:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_INT:
        case Bytecode.X_ADDR:
          builder.icopy(r.st(0), r.st(-1));
          break;
        case Bytecode.X_FLOAT:
          builder.fcopy(r.st(0), r.st(-1));
          break;
        case Bytecode.X_REF:
          builder.acopy(r.st(0), r.st(-1));
          break;
        }
        break;

      case DUP_X1:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_INT:
        case Bytecode.X_ADDR:
          builder.icopy(r.st(0), r.st(-1));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-2));
            break;
          }
          builder.icopy(r.st(-2), r.st(0));
          break;
        case Bytecode.X_FLOAT:
          builder.fcopy(r.st(0), r.st(-1));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-2));
            break;
          }
          builder.fcopy(r.st(-2), r.st(0));
          break;
        case Bytecode.X_REF:
          builder.acopy(r.st(0), r.st(-1));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-2));
            break;
          }
          builder.acopy(r.st(-2), r.st(0));
          break;
        }
        break;

      case DUP_X2:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_INT:
        case Bytecode.X_ADDR:
          builder.icopy(r.st(0), r.st(-1));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            builder.icopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            builder.icopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-2));
            builder.icopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            builder.fcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            builder.fcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-2));
            builder.fcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            builder.acopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            builder.acopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-2));
            builder.acopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-3));
            break;
          }
          builder.icopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_FLOAT:
          builder.fcopy(r.st(0), r.st(-1));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            builder.icopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            builder.icopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-2));
            builder.icopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            builder.fcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            builder.fcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-2));
            builder.fcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            builder.acopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            builder.acopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-2));
            builder.acopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-3));
            break;
          }
          builder.fcopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_REF:
          builder.acopy(r.st(0), r.st(-1));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            builder.icopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            builder.icopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-2));
            builder.icopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            builder.fcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            builder.fcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-2));
            builder.fcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            builder.acopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            builder.acopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-2));
            builder.acopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-3));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-3));
            break;
          }
          builder.acopy(r.st(-3), r.st(0));
          break;
        }
        break;

      case DUP2:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_INT_INT:
        case Bytecode.X_INT_ADDR:
        case Bytecode.X_ADDR_INT:
        case Bytecode.X_ADDR_ADDR:
          builder.icopy(r.st(1), r.st(-1));
          builder.icopy(r.st(0), r.st(-2));
          break;
        case Bytecode.X_INT_FLOAT:
        case Bytecode.X_ADDR_FLOAT:
          builder.fcopy(r.st(1), r.st(-1));
          builder.icopy(r.st(0), r.st(-2));
          break;
        case Bytecode.X_INT_REF:
        case Bytecode.X_ADDR_REF:
          builder.acopy(r.st(1), r.st(-1));
          builder.icopy(r.st(0), r.st(-2));
          break;
        case Bytecode.X_FLOAT_INT:
        case Bytecode.X_FLOAT_ADDR:
          builder.icopy(r.st(1), r.st(-1));
          builder.fcopy(r.st(0), r.st(-2));
          break;
        case Bytecode.X_FLOAT_FLOAT:
          builder.fcopy(r.st(1), r.st(-1));
          builder.fcopy(r.st(0), r.st(-2));
          break;
        case Bytecode.X_FLOAT_REF:
          builder.acopy(r.st(1), r.st(-1));
          builder.fcopy(r.st(0), r.st(-2));
          break;
        case Bytecode.X_REF_INT:
        case Bytecode.X_REF_ADDR:
          builder.icopy(r.st(1), r.st(-1));
          builder.acopy(r.st(0), r.st(-2));
          break;
        case Bytecode.X_REF_FLOAT:
          builder.fcopy(r.st(1), r.st(-1));
          builder.acopy(r.st(0), r.st(-2));
          break;
        case Bytecode.X_REF_REF:
          builder.acopy(r.st(1), r.st(-1));
          builder.acopy(r.st(0), r.st(-2));
          break;
        case Bytecode.X_LONG:
          builder.lcopy(r.st(0), r.st(-2));
          break;
        case Bytecode.X_DOUBLE:
          builder.dcopy(r.st(0), r.st(-2));
          break;
        }
        break;

      case DUP2_X1:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_INT_INT:
        case Bytecode.X_INT_ADDR:
        case Bytecode.X_ADDR_INT:
        case Bytecode.X_ADDR_ADDR:
          builder.icopy(r.st(1), r.st(-1));
          builder.icopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.icopy(r.st(-2), r.st(1));
          builder.icopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_INT_FLOAT:
        case Bytecode.X_ADDR_FLOAT:
          builder.fcopy(r.st(1), r.st(-1));
          builder.icopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.fcopy(r.st(-2), r.st(1));
          builder.icopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_INT_REF:
        case Bytecode.X_ADDR_REF:
          builder.acopy(r.st(1), r.st(-1));
          builder.icopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.acopy(r.st(-2), r.st(1));
          builder.icopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_FLOAT_INT:
        case Bytecode.X_FLOAT_ADDR:
          builder.icopy(r.st(1), r.st(-1));
          builder.fcopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.icopy(r.st(-2), r.st(1));
          builder.fcopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_FLOAT_FLOAT:
          builder.fcopy(r.st(1), r.st(-1));
          builder.fcopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.fcopy(r.st(-2), r.st(1));
          builder.fcopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_FLOAT_REF:
          builder.acopy(r.st(1), r.st(-1));
          builder.fcopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.acopy(r.st(-2), r.st(1));
          builder.fcopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_REF_INT:
        case Bytecode.X_REF_ADDR:
          builder.icopy(r.st(1), r.st(-1));
          builder.acopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.icopy(r.st(-2), r.st(1));
          builder.acopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_REF_FLOAT:
          builder.fcopy(r.st(1), r.st(-1));
          builder.acopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.fcopy(r.st(-2), r.st(1));
          builder.acopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_REF_REF:
          builder.acopy(r.st(1), r.st(-1));
          builder.acopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.acopy(r.st(-2), r.st(1));
          builder.acopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_LONG:
          builder.lcopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.lcopy(r.st(-3), r.st(0));
          break;
        case Bytecode.X_DOUBLE:
          builder.dcopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-3));
            break;
          }
          builder.dcopy(r.st(-3), r.st(0));
          break;
        }
        break;
        
      case DUP2_X2:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_INT_INT:
        case Bytecode.X_INT_ADDR:
        case Bytecode.X_ADDR_INT:
        case Bytecode.X_ADDR_ADDR:
          builder.icopy(r.st(1), r.st(-1));
          builder.icopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.icopy(r.st(-3), r.st(1));
          builder.icopy(r.st(-4), r.st(0));
          break;
        case Bytecode.X_INT_FLOAT:
        case Bytecode.X_ADDR_FLOAT:
          builder.fcopy(r.st(1), r.st(-1));
          builder.icopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.fcopy(r.st(-3), r.st(1));
          builder.icopy(r.st(-4), r.st(0));
          break;
        case Bytecode.X_INT_REF:
        case Bytecode.X_ADDR_REF:
          builder.acopy(r.st(1), r.st(-1));
          builder.icopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.acopy(r.st(-3), r.st(1));
          builder.icopy(r.st(-4), r.st(0));
          break;
        case Bytecode.X_FLOAT_INT:
        case Bytecode.X_FLOAT_ADDR:
          builder.icopy(r.st(1), r.st(-1));
          builder.fcopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.icopy(r.st(-3), r.st(1));
          builder.fcopy(r.st(-4), r.st(0));
          break;
        case Bytecode.X_FLOAT_FLOAT:
          builder.fcopy(r.st(1), r.st(-1));
          builder.fcopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.fcopy(r.st(-3), r.st(1));
          builder.fcopy(r.st(-4), r.st(0));
          break;
        case Bytecode.X_FLOAT_REF:
          builder.acopy(r.st(1), r.st(-1));
          builder.fcopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.acopy(r.st(-3), r.st(1));
          builder.fcopy(r.st(-4), r.st(0));
          break;
        case Bytecode.X_REF_INT:
        case Bytecode.X_REF_ADDR:
          builder.icopy(r.st(1), r.st(-1));
          builder.acopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.icopy(r.st(-3), r.st(1));
          builder.acopy(r.st(-4), r.st(0));
          break;
        case Bytecode.X_REF_FLOAT:
          builder.fcopy(r.st(1), r.st(-1));
          builder.acopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.fcopy(r.st(-3), r.st(1));
          builder.acopy(r.st(-4), r.st(0));
          break;
        case Bytecode.X_REF_REF:
          builder.acopy(r.st(1), r.st(-1));
          builder.acopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.acopy(r.st(-3), r.st(1));
          builder.acopy(r.st(-4), r.st(0));
          break;
        case Bytecode.X_LONG:
          builder.lcopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.lcopy(r.st(-4), r.st(0));
          break;
        case Bytecode.X_DOUBLE:
          builder.dcopy(r.st(0), r.st(-2));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT_INT:
          case Bytecode.X_INT_ADDR:
          case Bytecode.X_ADDR_INT:
          case Bytecode.X_ADDR_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_FLOAT:
          case Bytecode.X_ADDR_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_INT_REF:
          case Bytecode.X_ADDR_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.icopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_INT:
          case Bytecode.X_FLOAT_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_FLOAT_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.fcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_INT:
          case Bytecode.X_REF_ADDR:
            builder.icopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_FLOAT:
            builder.fcopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_REF_REF:
            builder.acopy(r.st(-1), r.st(-3));
            builder.acopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_LONG:
            builder.lcopy(r.st(-2), r.st(-4));
            break;
          case Bytecode.X_DOUBLE:
            builder.dcopy(r.st(-2), r.st(-4));
            break;
          }
          builder.dcopy(r.st(-4), r.st(0));
          break;
        }
        break;

      case SWAP:
        switch (Bytecode.getOp1type(textInfo, offset)) {
        case Bytecode.X_INT:
        case Bytecode.X_ADDR:
          builder.icopy(r.tr(0), r.st(-1));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-2));
            break;
          }
          builder.icopy(r.st(-2), r.tr(0));
          break;
        case Bytecode.X_FLOAT:
          builder.fcopy(r.tr(0), r.st(-1));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-2));
            break;
          }
          builder.fcopy(r.st(-2), r.tr(0));
          break;
        case Bytecode.X_REF:
          builder.acopy(r.tr(0), r.st(-1));
          switch (Bytecode.getOp2type(textInfo, offset)) {
          case Bytecode.X_INT:
          case Bytecode.X_ADDR:
            builder.icopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_FLOAT:
            builder.fcopy(r.st(-1), r.st(-2));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.st(-1), r.st(-2));
            break;
          }
          builder.acopy(r.st(-2), r.tr(0));
          break;
        }
        break;

      case IADD:
        builder.iadd(r.st(-2), r.st(-1));
        break;

      case LADD:
        builder.ladd(r.st(-4), r.st(-2));
        break;

      case FADD:
        builder.fadd(r.st(-2), r.st(-1));
        if (method.isStrict())
          builder.fstrictcopy(r.st(-2), r.st(-2));
        break;

      case DADD:
        builder.dadd(r.st(-4), r.st(-2));
        if (method.isStrict())
          builder.dstrictcopy(r.st(-4), r.st(-4));
        break;

      case ISUB:
        builder.isub(r.st(-2), r.st(-1));
        break;

      case LSUB:
        builder.lsub(r.st(-4), r.st(-2));
        break;

      case FSUB:
        builder.fsub(r.st(-2), r.st(-1));
        if (method.isStrict())
          builder.fstrictcopy(r.st(-2), r.st(-2));
        break;

      case DSUB:
        builder.dsub(r.st(-4), r.st(-2));
        if (method.isStrict())
          builder.dstrictcopy(r.st(-4), r.st(-4));
        break;

      case IMUL:
        builder.imul(r.st(-2), r.st(-1));
        break;

      case LMUL:
        builder.lmul(r.st(-4), r.st(-2));
        break;

      case FMUL:
        builder.fmul(r.st(-2), r.st(-1));
        if (method.isStrict())
          builder.fstrictcopy(r.st(-2), r.st(-2));
        break;

      case DMUL:
        builder.dmul(r.st(-4), r.st(-2));
        if (method.isStrict())
          builder.dstrictcopy(r.st(-4), r.st(-4));
        break;

      case IDIV:
        builder.ijumpnonzero(r.st(-1), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/ArithmeticException");
        builder.label(r.ll(0));
        builder.idiv(r.st(-2), r.st(-1));
        break;

      case LDIV:
        builder.ljumpnonzero(r.st(-2), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/ArithmeticException");
        builder.label(r.ll(0));
        builder.ldiv(r.st(-4), r.st(-2));
        break;

      case FDIV:
        builder.fdiv(r.st(-2), r.st(-1));
        if (method.isStrict())
          builder.fstrictcopy(r.st(-2), r.st(-2));
        break;

      case DDIV:
        builder.ddiv(r.st(-4), r.st(-2));
        if (method.isStrict())
          builder.dstrictcopy(r.st(-4), r.st(-4));
        break;

      case IREM:
        builder.ijumpnonzero(r.st(-1), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/ArithmeticException");
        builder.label(r.ll(0));
        builder.irem(r.st(-2), r.st(-1));
        break;

      case LREM:
        builder.ljumpnonzero(r.st(-2), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/ArithmeticException");
        builder.label(r.ll(0));
        builder.lrem(r.st(-4), r.st(-2));
        break;

      case FREM:
        builder.frem(r.st(-2), r.st(-1));
        if (method.isStrict())
          builder.fstrictcopy(r.st(-2), r.st(-2));
        break;

      case DREM:
        builder.drem(r.st(-4), r.st(-2));
        if (method.isStrict())
          builder.dstrictcopy(r.st(-4), r.st(-4));
        break;

      case INEG:
        builder.ineg(r.st(-1));
        break;

      case LNEG:
        builder.lneg(r.st(-2));
        break;

      case FNEG:
        builder.fneg(r.st(-1));
        break;

      case DNEG:
        builder.dneg(r.st(-2));
        break;

      case ISHL:
        builder.ishl(r.st(-2), r.st(-1));
        break;

      case LSHL:
        builder.lshl(r.st(-3), r.st(-1));
        break;

      case ISHR:
        builder.ishr(r.st(-2), r.st(-1));
        break;

      case LSHR:
        builder.lshr(r.st(-3), r.st(-1));
        break;

      case IUSHR:
        builder.iushr(r.st(-2), r.st(-1));
        break;

      case LUSHR:
        builder.lushr(r.st(-3), r.st(-1));
        break;

      case IAND:
        builder.iand(r.st(-2), r.st(-1));
        break;

      case LAND:
        builder.land(r.st(-4), r.st(-2));
        break;

      case IOR:
        builder.ior(r.st(-2), r.st(-1));
        break;

      case LOR:
        builder.lor(r.st(-4), r.st(-2));
        break;

      case IXOR:
        builder.ixor(r.st(-2), r.st(-1));
        break;

      case LXOR:
        builder.lxor(r.st(-4), r.st(-2));
        break;

      case IINC:
        index = Bytecode.ubyteAt(text, offset+1);
        value = Bytecode.byteAt(text, offset+2);
        builder.iinc(r.fr(index), value);
        break;

      case I2L:
        builder.i2l(r.st(-1), r.st(-1));
        break;

      case I2F:
        builder.i2f(r.st(-1), r.st(-1));
        break;

      case I2D:
        builder.i2d(r.st(-1), r.st(-1));
        break;

      case L2I:
        builder.l2i(r.st(-2), r.st(-2));
        break;

      case L2F:
        builder.l2f(r.st(-2), r.st(-2));
        break;

      case L2D:
        builder.l2d(r.st(-2), r.st(-2));
        break;

      case F2I:
        builder.f2i(r.st(-1), r.st(-1));
        break;

      case F2L:
        builder.f2l(r.st(-1), r.st(-1));
        break;

      case F2D:
        builder.f2d(r.st(-1), r.st(-1));
        break;

      case D2I:
        builder.d2i(r.st(-2), r.st(-2));
        break;

      case D2L:
        builder.d2l(r.st(-2), r.st(-2));
        break;

      case D2F:
        builder.d2f(r.st(-2), r.st(-2));
        if (method.isStrict())
          builder.fstrictcopy(r.st(-2), r.st(-2));
        break;

      case I2B:
        builder.i2b(r.st(-1), r.st(-1));
        break;

      case I2C:
        builder.i2c(r.st(-1), r.st(-1));
        break;

      case I2S:
        builder.i2s(r.st(-1), r.st(-1));
        break;

      case LCMP:
        builder.lcmp(r.st(-4), r.st(-4), r.st(-2));
        break;

      case FCMPL:
        builder.fcmpl(r.st(-2), r.st(-2), r.st(-1));
        break;

      case FCMPG:
        builder.fcmpg(r.st(-2), r.st(-2), r.st(-1));
        break;

      case DCMPL:
        builder.dcmpl(r.st(-4), r.st(-4), r.st(-2));
        break;

      case DCMPG:
        builder.dcmpg(r.st(-4), r.st(-4), r.st(-2));
        break;

      case IFEQ:
        int branch = Bytecode.shortAt(text, offset+1);
        int target = offset+branch;
        builder.ijumpzero(r.st(-1), r.gl(target));
        break;

      case IFNE:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumpnonzero(r.st(-1), r.gl(target));
        break;

      case IFLT:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumpnegative(r.st(-1), r.gl(target));
        break;

      case IFGE:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumpnotnegative(r.st(-1), r.gl(target));
        break;

      case IFGT:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumppositive(r.st(-1), r.gl(target));
        break;

      case IFLE:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumpnotpositive(r.st(-1), r.gl(target));
        break;

      case IF_ICMPEQ:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumpeq(r.st(-2), r.st(-1), r.gl(target));
        break;

      case IF_ICMPNE:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumpne(r.st(-2), r.st(-1), r.gl(target));
        break;

      case IF_ICMPLT:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumplt(r.st(-2), r.st(-1), r.gl(target));
        break;

      case IF_ICMPGE:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumpge(r.st(-2), r.st(-1), r.gl(target));
        break;

      case IF_ICMPGT:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumpgt(r.st(-2), r.st(-1), r.gl(target));
        break;

      case IF_ICMPLE:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ijumple(r.st(-2), r.st(-1), r.gl(target));
        break;

      case IF_ACMPEQ:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ajumpeq(r.st(-2), r.st(-1), r.gl(target));
        break;

      case IF_ACMPNE:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ajumpne(r.st(-2), r.st(-1), r.gl(target));
        break;

      case GOTO:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.jump(r.gl(target));
        break;

      case JSR:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        int jsr = 0;
        for (int i = 0; i < offset; i++)
          if (Bytecode.isValid(textInfo, i) && !Bytecode.isUnreachable(textInfo, i))
            switch (Bytecode.opcodeAt(text, i)) {
            case JSR:
              if (!Bytecode.isUnreachable(textInfo, i+3))
                jsr++;
              break;
            case JSR_W:
              if (!Bytecode.isUnreachable(textInfo, i+5))
                jsr++;
              break;
            }
        builder.iconst(r.st(0), jsr);
        builder.jump(r.gl(target));
        break;

      case RET:
        index = Bytecode.ubyteAt(text, offset+1);
        IR.iswitch isw = builder.iswitch(r.fr(index));
        jsr = 0;
        for (int i = 0; i < text.length; i++)
          if (Bytecode.isValid(textInfo, i) && !Bytecode.isUnreachable(textInfo, i))
            switch (Bytecode.opcodeAt(text, i)) {
            case JSR:
              if (!Bytecode.isUnreachable(textInfo, i+3))
                isw.setTarget(jsr++, r.gl(i+3));
              break;
            case JSR_W:
              if (!Bytecode.isUnreachable(textInfo, i+5))
                isw.setTarget(jsr++, r.gl(i+5));
              break;
            }
        builder.label(r.ll(0));
        builder.jump(r.ll(0));
        break;

      case TABLESWITCH:
        int pad = ~offset & 3;
        int defaultBranch = Bytecode.intAt(text, offset+1+pad);
        int defaultTarget = offset+defaultBranch;
        int low = Bytecode.intAt(text, offset+1+pad+4);
        int high = Bytecode.intAt(text, offset+1+pad+8);
        int entries = high-low+1;
        opsize = 1+pad+12+4*entries;
        isw = builder.iswitch(r.st(-1), entries);
        for (int i = 0; i < entries; i++) {
          int key = low+i;
          branch = Bytecode.intAt(text, offset+1+pad+12+4*i);
          target = offset+branch;
          if (target != defaultTarget)
            isw.setTarget(key, r.gl(target));
        }
        builder.jump(r.gl(defaultTarget));
        break;

      case LOOKUPSWITCH:
        pad = ~offset & 3;
        defaultBranch = Bytecode.intAt(text, offset+1+pad);
        defaultTarget = offset+defaultBranch;
        entries = Bytecode.intAt(text, offset+1+pad+4);
        opsize = 1+pad+8+8*entries;
        isw = builder.iswitch(r.st(-1), entries);
        for (int i = 0; i < entries; i++) {
          int key = Bytecode.intAt(text, offset+1+pad+8+8*i);
          branch = Bytecode.intAt(text, offset+1+pad+8+8*i+4);
          target = offset+branch;
          if (target != defaultTarget)
            isw.setTarget(key, r.gl(target));
        }
        builder.jump(r.gl(defaultTarget));
        break;

      case IRETURN:
        if (synch) {
          builder.ajumplocked(r.th(), r.ll(0));
          generateException(builder, r, "java/lang/IllegalMonitorStateException");
          builder.label(r.ll(0));
          builder.aunlock(r.th());
        }
        builder.ireturn(r.st(-1));
        break;

      case LRETURN:
        if (synch) {
          builder.ajumplocked(r.th(), r.ll(0));
          generateException(builder, r, "java/lang/IllegalMonitorStateException");
          builder.label(r.ll(0));
          builder.aunlock(r.th());
        }
        builder.lreturn(r.st(-2));
        break;

      case FRETURN:
        if (synch) {
          builder.ajumplocked(r.th(), r.ll(0));
          generateException(builder, r, "java/lang/IllegalMonitorStateException");
          builder.label(r.ll(0));
          builder.aunlock(r.th());
        }
        builder.freturn(r.st(-1));
        break;

      case DRETURN:
        if (synch) {
          builder.ajumplocked(r.th(), r.ll(0));
          generateException(builder, r, "java/lang/IllegalMonitorStateException");
          builder.label(r.ll(0));
          builder.aunlock(r.th());
        }
        builder.dreturn(r.st(-2));
        break;

      case ARETURN:
        if (synch) {
          builder.ajumplocked(r.th(), r.ll(0));
          generateException(builder, r, "java/lang/IllegalMonitorStateException");
          builder.label(r.ll(0));
          builder.aunlock(r.th());
        }
        builder.areturn(r.st(-1));
        break;

      case RETURN:
        if (synch) {
          builder.ajumplocked(r.th(), r.ll(0));
          generateException(builder, r, "java/lang/IllegalMonitorStateException");
          builder.label(r.ll(0));
          builder.aunlock(r.th());
        }
        builder.vreturn();
        break;

      case GETSTATIC:
        index = Bytecode.ushortAt(text, offset+1);
        String clazz = constantPool.getClass(index);
        String name = constantPool.getName(index);
        String type = constantPool.getDescriptor(index);
        // field resolution
        PlacedFieldInfo field = context.resolveField(clazz, name, type);
        if (!field.isStatic())
          throw new IncompatibleClassChangeException("Field "+field.getOwner().getName()+"."+field.getName()+" used to be static");
        // apply loading constraints
        clazz = field.getOwner().getName();//care! ignores loader boundaries
        // field resolution
        boolean volat = field.isVolatile();
        if (handler == null)
          builder.init(clazz, method, line);
        else
          builder.initx(clazz, r.cl(handler.getIndex()), method, line);
        switch (type.charAt(0)) {
        case 'Z': case 'B': 
          builder.sbload(r.st(0), clazz, field.getOffset(), volat);
          break;
        case 'C': 
          builder.scload(r.st(0), clazz, field.getOffset(), volat);
          break;
        case 'S':
          builder.ssload(r.st(0), clazz, field.getOffset(), volat);
          break;
        case 'I':
          builder.siload(r.st(0), clazz, field.getOffset(), volat);
          break;
        case 'J':
          builder.slload(r.st(0), clazz, field.getOffset(), volat);
          break;
        case 'F':
          builder.sfload(r.st(0), clazz, field.getOffset(), volat);
          break;
        case 'D':
          builder.sdload(r.st(0), clazz, field.getOffset(), volat);
          break;
        case 'L':
          builder.saload(r.st(0), clazz, field.getOffset(), volat, type.substring(1, type.length()-1));
          break;
        case '[':
          builder.saload(r.st(0), clazz, field.getOffset(), volat, type);
          break;
        }
        break;

      case PUTSTATIC:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        name = constantPool.getName(index);
        type = constantPool.getDescriptor(index);
        // field resolution
        field = context.resolveField(clazz, name, type);
        if (!field.isStatic())
          throw new IncompatibleClassChangeException("Field "+field.getOwner().getName()+"."+field.getName()+" used to be static");
        if (field.isFinal() && false/*field.getOwner() != clazz*/)
          throw new IllegalAccessException("Attempt to modify final field "+field.getOwner().getName()+"."+field.getName()+" from class "+method.getOwner().getName());
        // apply loading constraints
        clazz = field.getOwner().getName();//care! ignores loader boundaries
        // field resolution
        volat = field.isVolatile();
        if (handler == null)
          builder.init(clazz, method, line);
        else
          builder.initx(clazz, r.cl(handler.getIndex()), method, line);
        switch (type.charAt(0)) {
        case 'Z': case 'B': 
          builder.sbstore(clazz, field.getOffset(), volat, r.st(-1));
          break;
        case 'C': case 'S':
          builder.ssstore(clazz, field.getOffset(), volat, r.st(-1));
          break;
        case 'I':
          builder.sistore(clazz, field.getOffset(), volat, r.st(-1));
          break;
        case 'J':
          builder.slstore(clazz, field.getOffset(), volat, r.st(-2));
          break;
        case 'F':
          builder.sfstore(clazz, field.getOffset(), volat, r.st(-1));
          break;
        case 'D':
          builder.sdstore(clazz, field.getOffset(), volat, r.st(-2));
          break;
        case 'L': case '[':
          builder.sastore(clazz, field.getOffset(), volat, r.st(-1));
          break;
        }
        break;

      case GETFIELD:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        name = constantPool.getName(index);
        type = constantPool.getDescriptor(index);
        // field resolution
        field = context.resolveField(clazz, name, type);
        if (field.isStatic())
          throw new IncompatibleClassChangeException("Field "+field.getOwner().getName()+"."+field.getName()+" did not used to be static");
        // apply loading constraints
        // field resolution
        volat = field.isVolatile();
        builder.ajumpnonnull(r.st(-1), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        switch (type.charAt(0)) {
        case 'Z': case 'B': 
          builder.ibload(r.st(-1), r.st(-1), field.getOffset(), volat);
          break;
        case 'C': 
          builder.icload(r.st(-1), r.st(-1), field.getOffset(), volat);
          break;
        case 'S':
          builder.isload(r.st(-1), r.st(-1), field.getOffset(), volat);
          break;
        case 'I':
          builder.iiload(r.st(-1), r.st(-1), field.getOffset(), volat);
          break;
        case 'J':
          builder.ilload(r.st(-1), r.st(-1), field.getOffset(), volat);
          break;
        case 'F':
          builder.ifload(r.st(-1), r.st(-1), field.getOffset(), volat);
          break;
        case 'D':
          builder.idload(r.st(-1), r.st(-1), field.getOffset(), volat);
          break;
        case 'L':
          builder.iaload(r.st(-1), r.st(-1), field.getOffset(), volat, type.substring(1, type.length()-1));
          break;
        case '[':
          builder.iaload(r.st(-1), r.st(-1), field.getOffset(), volat, type);
          break;
        }
        break;

      case PUTFIELD:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        name = constantPool.getName(index);
        type = constantPool.getDescriptor(index);
        // field resolution
        field = context.resolveField(clazz, name, type);
        if (field.isStatic())
          throw new IncompatibleClassChangeException("Field "+field.getOwner().getName()+"."+field.getName()+" did not used to be static");
        if (field.isFinal() && false/*field.getOwner() != clazz*/)
          throw new IllegalAccessException("Attempt to modify final field "+field.getOwner().getName()+"."+field.getName()+" from class "+method.getOwner().getName());
        // apply loading constraints
        // field resolution
        volat = field.isVolatile();
        switch (type.charAt(0)) {
        case 'J': case 'D':
          builder.ajumpnonnull(r.st(-3), r.ll(0));
          break;
        default:
          builder.ajumpnonnull(r.st(-2), r.ll(0));
        }
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        switch (type.charAt(0)) {
        case 'Z': case 'B': 
          builder.ibstore(r.st(-2), field.getOffset(), volat, r.st(-1));
          break;
        case 'C': case 'S':
          builder.isstore(r.st(-2), field.getOffset(), volat, r.st(-1));
          break;
        case 'I':
          builder.iistore(r.st(-2), field.getOffset(), volat, r.st(-1));
          break;
        case 'J':
          builder.ilstore(r.st(-3), field.getOffset(), volat, r.st(-2));
          break;
        case 'F':
          builder.ifstore(r.st(-2), field.getOffset(), volat, r.st(-1));
          break;
        case 'D':
          builder.idstore(r.st(-3), field.getOffset(), volat, r.st(-2));
          break;
        case 'L': case '[':
          builder.iastore(r.st(-2), field.getOffset(), volat, r.st(-1));
          break;
        }
        break;

      case INVOKEVIRTUAL:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        name = constantPool.getName(index);
        String descriptor = constantPool.getDescriptor(index);
        // method resolution
        PlacedMethodInfo calledMethod = context.resolveMethod(clazz, name, descriptor);
        if (calledMethod.isStatic())
          throw new IncompatibleClassChangeException("Method "+calledMethod.getOwner().getName()+"."+calledMethod.getName()+calledMethod.getDescriptor()+" did not used to be static");
        // apply loading constraints
        clazz = calledMethod.getOwner().getName();//care! ignores loader boundaries
        // method resolution
        int dindex = calledMethod.getDispatchIndex();
        if (calledMethod.getOwner().isInterface())
          dindex += context.forName(constantPool.getClass(index)).lookupInterfaceBaseIndex(context.forName(clazz));
        boolean dynamic = calledMethod.isVirtual();
        int pcount = Syntax.getParametersSize(descriptor, true);
        builder.ajumpnonnull(r.st(-pcount), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        int pindex = 0;
        params = Syntax.getParameterTypes(descriptor);
        for (int i = params.length-1; i >= 0; i--) {
          type = params[i];
          switch (type.charAt(0)) {
          case 'Z': case 'B': case 'C': case 'S': case 'I':
            pindex--;
            builder.ipass(r.st(pindex));
            break;
          case 'J':
            pindex -= 2;
            builder.lpass(r.st(pindex));
            break;
          case 'F':
            pindex--;
            builder.fpass(r.st(pindex));
            break;
          case 'D':
            pindex -= 2;
            builder.dpass(r.st(pindex));
            break;
          case 'L': case '[':
            pindex--;
            builder.apass(r.st(pindex));
            break;
          }
        }
        pindex--;
        builder.apass(r.st(pindex));
        if (dynamic) {
          if (handler == null)
            builder.vcall(r.st(-pcount), dindex, method, line);
          else
            builder.vcallx(r.st(-pcount), dindex, r.cl(handler.getIndex()), method, line);
        } else {
          if (handler == null)
            builder.scall(clazz, dindex, method, line);
          else
            builder.scallx(clazz, dindex, r.cl(handler.getIndex()), method, line);
        }
        type = Syntax.getReturnType(descriptor);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          builder.iresult(r.st(-pcount));
          break;
        case 'J':
          builder.lresult(r.st(-pcount));
          break;
        case 'F':
          builder.fresult(r.st(-pcount));
          if (method.isStrict())
            builder.fstrictcopy(r.st(-pcount), r.st(-pcount));
          break;
        case 'D':
          builder.dresult(r.st(-pcount));
          if (method.isStrict())
            builder.dstrictcopy(r.st(-pcount), r.st(-pcount));
          break;
        case 'L':
          builder.aresult(r.st(-pcount), type.substring(1, type.length()-1));
          break;
        case '[':
          builder.aresult(r.st(-pcount), type);
          break;
        case 'V':
          break;
        }
        break;

      case INVOKESPECIAL:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        name = constantPool.getName(index);
        descriptor = constantPool.getDescriptor(index);
        // method resolution
        calledMethod = context.resolveMethod(clazz, name, descriptor);
        if (calledMethod.isStatic())
          throw new IncompatibleClassChangeException("Method "+calledMethod.getOwner().getName()+"."+calledMethod.getName()+calledMethod.getDescriptor()+" did not used to be static");
        if (calledMethod.isAbstract())
          throw new AbstractMethodException("Abstract method "+calledMethod.getOwner().getName()+"."+calledMethod.getName()+calledMethod.getDescriptor()+" is abstract in class "+clazz);
        // apply loading constraints
        clazz = calledMethod.getOwner().getName();//care! ignores loader boundaries
        // method resolution
        dindex = calledMethod.getDispatchIndex();
        pcount = Syntax.getParametersSize(descriptor, true);
        builder.ajumpnonnull(r.st(-pcount), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        pindex = 0;
        params = Syntax.getParameterTypes(descriptor);
        for (int i = params.length-1; i >= 0; i--) {
          type = params[i];
          switch (type.charAt(0)) {
          case 'Z': case 'B': case 'C': case 'S': case 'I':
            pindex--;
            builder.ipass(r.st(pindex));
            break;
          case 'J':
            pindex -= 2;
            builder.lpass(r.st(pindex));
            break;
          case 'F':
            pindex--;
            builder.fpass(r.st(pindex));
            break;
          case 'D':
            pindex -= 2;
            builder.dpass(r.st(pindex));
            break;
          case 'L': case '[':
            pindex--;
            builder.apass(r.st(pindex));
            break;
          }
        }
        pindex--;
        builder.apass(r.st(pindex));
        if (handler == null)
          builder.scall(clazz, dindex, method, line);
        else
          builder.scallx(clazz, dindex, r.cl(handler.getIndex()), method, line);
        type = Syntax.getReturnType(descriptor);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          builder.iresult(r.st(-pcount));
          break;
        case 'J':
          builder.lresult(r.st(-pcount));
          break;
        case 'F':
          builder.fresult(r.st(-pcount));
          if (method.isStrict())
            builder.fstrictcopy(r.st(-pcount), r.st(-pcount));
          break;
        case 'D':
          builder.dresult(r.st(-pcount));
          if (method.isStrict())
            builder.dstrictcopy(r.st(-pcount), r.st(-pcount));
          break;
        case 'L':
          builder.aresult(r.st(-pcount), type.substring(1, type.length()-1));
          break;
        case '[':
          builder.aresult(r.st(-pcount), type);
          break;
        case 'V':
          break;
        }
        break;

      case INVOKESTATIC:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        name = constantPool.getName(index);
        descriptor = constantPool.getDescriptor(index);
        // method resolution
        calledMethod = context.resolveMethod(clazz, name, descriptor);
        if (!calledMethod.isStatic())
          throw new IncompatibleClassChangeException("Method "+calledMethod.getOwner().getName()+"."+calledMethod.getName()+calledMethod.getDescriptor()+" used to be static");
        // apply loading constraints
        clazz = calledMethod.getOwner().getName();//care! ignores loader boundaries
        // method resolution
        dindex = calledMethod.getDispatchIndex();
        pcount = Syntax.getParametersSize(descriptor, false);
        if (handler == null)
          builder.init(clazz, method, line);
        else
          builder.initx(clazz, r.cl(handler.getIndex()), method, line);
        params = Syntax.getParameterTypes(descriptor);
        pindex = 0;
        for (int i = params.length-1; i >= 0; i--) {
          type = params[i];
          switch (type.charAt(0)) {
          case 'Z': case 'B': case 'C': case 'S': case 'I':
            pindex--;
            builder.ipass(r.st(pindex));
            break;
          case 'J':
            pindex -= 2;
            builder.lpass(r.st(pindex));
            break;
          case 'F':
            pindex--;
            builder.fpass(r.st(pindex));
            break;
          case 'D':
            pindex -= 2;
            builder.dpass(r.st(pindex));
            break;
          case 'L': case '[':
            pindex--;
            builder.apass(r.st(pindex));
            break;
          }
        }
        if (handler == null)
          builder.scall(clazz, dindex, method, line);
        else
          builder.scallx(clazz, dindex, r.cl(handler.getIndex()), method, line);
        type = Syntax.getReturnType(descriptor);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          builder.iresult(r.st(-pcount));
          break;
        case 'J':
          builder.lresult(r.st(-pcount));
          break;
        case 'F':
          builder.fresult(r.st(-pcount));
          if (method.isStrict())
            builder.fstrictcopy(r.st(-pcount), r.st(-pcount));
          break;
        case 'D':
          builder.dresult(r.st(-pcount));
          if (method.isStrict())
            builder.dstrictcopy(r.st(-pcount), r.st(-pcount));
          break;
        case 'L':
          builder.aresult(r.st(-pcount), type.substring(1, type.length()-1));
          break;
        case '[':
          builder.aresult(r.st(-pcount), type);
          break;
        case 'V':
          break;
        }
        break;

      case INVOKEINTERFACE:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        name = constantPool.getName(index);
        descriptor = constantPool.getDescriptor(index);
        // method resolution
        calledMethod = context.resolveInterfaceMethod(clazz, name, descriptor);
        // apply loading constraints
        clazz = calledMethod.getOwner().getName();//care! ignores loader boundaries
        // method resolution
        dindex = calledMethod.getDispatchIndex();
        pcount = Syntax.getParametersSize(descriptor, true);
        builder.ajumpnonnull(r.st(-pcount), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.imlookup(r.tr(0), r.st(-pcount), clazz, dindex);
        builder.ajumpnonnull(r.tr(0), r.ll(1));
        generateException(builder, handler, r, method, line, "java/lang/IncompatibleClassChangeError");
        builder.label(r.ll(1));
        pindex = 0;
        params = Syntax.getParameterTypes(descriptor);
        for (int i = params.length-1; i >= 0; i--) {
          type = params[i];
          switch (type.charAt(0)) {
          case 'Z': case 'B': case 'C': case 'S': case 'I':
            pindex--;
            builder.ipass(r.st(pindex));
            break;
          case 'J':
            pindex -= 2;
            builder.lpass(r.st(pindex));
            break;
          case 'F':
            pindex--;
            builder.fpass(r.st(pindex));
            break;
          case 'D':
            pindex -= 2;
            builder.dpass(r.st(pindex));
            break;
          case 'L': case '[':
            pindex--;
            builder.apass(r.st(pindex));
            break;
          }
        }
        pindex--;
        builder.apass(r.st(pindex));
        if (handler == null)
          builder.icall(r.tr(0), method, line);
        else
          builder.icallx(r.tr(0), r.cl(handler.getIndex()), method, line);
        type = Syntax.getReturnType(descriptor);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          builder.iresult(r.st(-pcount));
          break;
        case 'J':
          builder.lresult(r.st(-pcount));
          break;
        case 'F':
          builder.fresult(r.st(-pcount));
          if (method.isStrict())
            builder.fstrictcopy(r.st(-pcount), r.st(-pcount));
          break;
        case 'D':
          builder.dresult(r.st(-pcount));
          if (method.isStrict())
            builder.dstrictcopy(r.st(-pcount), r.st(-pcount));
          break;
        case 'L':
          builder.aresult(r.st(-pcount), type.substring(1, type.length()-1));
          break;
        case '[':
          builder.aresult(r.st(-pcount), type);
          break;
        case 'V':
          break;
        }
        break;

      case NEW:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        ContextClassInfo contextClass = context.resolveClass(clazz);
        if (contextClass.isAbstract())
          throw new InstantiationException("Attempt to instantiate abstract class or interface "+contextClass.getName()+" from class "+method.getOwner().getName());
        if (handler == null) {
          builder.init(clazz, method, line);
          builder.newinstance(clazz, method, line);
        } else {
          builder.initx(clazz, r.cl(handler.getIndex()), method, line);
          builder.newinstancex(clazz, r.cl(handler.getIndex()), method, line);
        }
        builder.aresult(r.st(0), clazz);
        break;

      case NEWARRAY:
        int atype = Bytecode.ubyteAt(text, offset+1);
        clazz = null;
        switch (atype) {
        case T_BOOLEAN: clazz = "[Z"; break;
        case T_BYTE: clazz = "[B"; break;
        case T_CHAR: clazz = "[C"; break;
        case T_SHORT: clazz = "[S"; break;
        case T_INT: clazz = "[I"; break;
        case T_FLOAT: clazz = "[F"; break;
        case T_LONG: clazz = "[J"; break;
        case T_DOUBLE: clazz = "[D"; break;
        }
        builder.ijumpnotnegative(r.st(-1), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NegativeArraySizeException");
        builder.label(r.ll(0));
        if (handler == null)
          builder.newarray(clazz, r.st(-1), method, line);
        else
          builder.newarrayx(clazz, r.st(-1), r.cl(handler.getIndex()), method, line);
        builder.aresult(r.st(-1), clazz);
        break;

      case ANEWARRAY:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        contextClass = context.resolveClass(clazz);
        if (clazz.charAt(0) != '[')
          clazz = "L"+clazz+";";
        clazz = "["+clazz;
        builder.ijumpnotnegative(r.st(-1), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NegativeArraySizeException");
        builder.label(r.ll(0));
        if (handler == null)
          builder.newarray(clazz, r.st(-1), method, line);
        else
          builder.newarrayx(clazz, r.st(-1), r.cl(handler.getIndex()), method, line);
        builder.aresult(r.st(-1), clazz);
        break;

      case ARRAYLENGTH:
        builder.ajumpnonnull(r.st(-1), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.alength(r.st(-1), r.st(-1));
        break;

      case ATHROW:
        builder.ajumpnonnull(r.st(-1), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        if (handler == null)
          builder.athrow(r.st(-1));
        else {
          builder.acopy(r.ex(), r.st(-1));
          builder.jump(r.hl(handler.getIndex()));
        }
        break;

      case CHECKCAST:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        contextClass = context.resolveClass(clazz);
        builder.ajumpnull(r.st(-1), r.ll(0));
        builder.ajumpsubtype(r.st(-1), clazz, r.ll(0));
        genClassCastException(builder, handler, r, method, line);
        builder.label(r.ll(0));
        break;

      case INSTANCEOF:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        contextClass = context.resolveClass(clazz);
        builder.ajumpnull(r.st(-1), r.ll(0));
        builder.issubtype(r.st(-1), r.st(-1), clazz);
        builder.jump(r.ll(1));
        builder.label(r.ll(0));
        builder.iconst(r.st(-1), 0);
        builder.label(r.ll(1));
        break;

      case MONITORENTER:
        builder.ajumpnonnull(r.st(-1), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        if (handler == null)
          builder.alock(r.st(-1), method, line);
        else
          builder.alockx(r.st(-1), r.cl(handler.getIndex()), method, line);
        break;

      case MONITOREXIT:
        builder.ajumpnonnull(r.st(-1), r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NullPointerException");
        builder.label(r.ll(0));
        builder.ajumplocked(r.st(-1), r.ll(1));
        generateException(builder, handler, r, method, line, "java/lang/IllegalMonitorStateException");
        builder.label(r.ll(1));
        builder.aunlock(r.st(-1));
        break;

      case WIDE:

        short wopcode = Bytecode.opcodeAt(text, offset+1);
        opsize = Bytecode.wsizeOf(wopcode);

        switch (wopcode) {
        
        case ILOAD:
          index = Bytecode.ushortAt(text, offset+2);
          builder.icopy(r.st(0), r.fr(index));
          break;

        case LLOAD:
          index = Bytecode.ushortAt(text, offset+2);
          builder.lcopy(r.st(0), r.fr(index));
          break;

        case FLOAD:
          index = Bytecode.ushortAt(text, offset+2);
          builder.fcopy(r.st(0), r.fr(index));
          break;

        case DLOAD:
          index = Bytecode.ushortAt(text, offset+2);
          builder.dcopy(r.st(0), r.fr(index));
          break;

        case ALOAD:
          index = Bytecode.ushortAt(text, offset+2);
          builder.acopy(r.st(0), r.fr(index));
          break;

        case ISTORE:
          index = Bytecode.ushortAt(text, offset+2);
          builder.icopy(r.fr(index), r.st(-1));
          break;

        case LSTORE:
          index = Bytecode.ushortAt(text, offset+2);
          builder.lcopy(r.fr(index), r.st(-2));
          break;

        case FSTORE:
          index = Bytecode.ushortAt(text, offset+2);
          builder.fstrictcopy(r.fr(index), r.st(-1));
          break;

        case DSTORE:
          index = Bytecode.ushortAt(text, offset+2);
          builder.dstrictcopy(r.fr(index), r.st(-2));
          break;

        case ASTORE:
          index = Bytecode.ushortAt(text, offset+2);
          switch (Bytecode.getOp1type(textInfo, offset)) {
          case Bytecode.X_ADDR:
            builder.icopy(r.fr(index), r.st(-1));
            break;
          case Bytecode.X_REF:
            builder.acopy(r.fr(index), r.st(-1));
            break;
          }
          break;

        case IINC:
          index = Bytecode.ushortAt(text, offset+2);
          value = Bytecode.shortAt(text, offset+4);
          builder.iinc(r.fr(index), value);
          break;

        case RET:
          index = Bytecode.ushortAt(text, offset+2);
          isw = builder.iswitch(r.fr(index));
          jsr = 0;
          for (int i = 0; i < text.length; i++)
            if (Bytecode.isValid(textInfo, i) && !Bytecode.isUnreachable(textInfo, i))
              switch (Bytecode.opcodeAt(text, i)) {
              case JSR:
                if (!Bytecode.isUnreachable(textInfo, i+3))
                  isw.setTarget(jsr++, r.gl(i+3));
                break;
              case JSR_W:
                if (!Bytecode.isUnreachable(textInfo, i+5))
                  isw.setTarget(jsr++, r.gl(i+5));
                break;
              }
          builder.label(r.ll(0));
          builder.jump(r.ll(0));
          break;

        }
        break;

      case MULTIANEWARRAY:
        index = Bytecode.ushortAt(text, offset+1);
        int dims = Bytecode.ubyteAt(text, offset+3);
        type = constantPool.getClass(index);
        contextClass = context.resolveClass(type);
        for (int i = 0; i < dims; i++)
          builder.ijumpnegative(r.st(-(i+1)), r.ll(0));
        builder.jump(r.ll(1));
        builder.label(r.ll(0));
        generateException(builder, handler, r, method, line, "java/lang/NegativeArraySizeException");
        builder.label(r.ll(1));
        if (handler == null)
          builder.newarray(type, r.st(-dims), method, line);
        else
          builder.newarrayx(type, r.st(-dims), r.cl(handler.getIndex()), method, line);
        builder.aresult(r.st(-dims), type);
        for (int i = 1; i < dims; i++) {
          builder.iconst(r.tr(i-1), 0);
          builder.ijumpge(r.tr(i-1), r.st(-dims+i-1), r.ll(2*i+1));
          builder.label(r.ll(2*i));
          type = type.substring(1);
          if (handler == null)
            builder.newarray(type, r.st(-dims+i), method, line);
          else
            builder.newarrayx(type, r.st(-dims+i), r.cl(handler.getIndex()), method, line);
          builder.aresult(r.st(-dims+i), type);
        }
        for (int i = dims-1; i > 0; i--) {
          builder.aastore(r.st(-dims+i-1), r.tr(i-1), r.st(-dims+i));
          builder.iinc(r.tr(i-1), 1);
          builder.ijumplt(r.tr(i-1), r.st(-dims+i-1), r.ll(2*i));
          builder.label(r.ll(2*i+1));
        }
        break;

      case IFNULL:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ajumpnull(r.st(-1), r.gl(target));
        break;

      case IFNONNULL:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        builder.ajumpnonnull(r.st(-1), r.gl(target));
        break;

      case GOTO_W:
        branch = Bytecode.intAt(text, offset+1);
        target = offset+branch;
        builder.jump(r.gl(target));
        break;

      case JSR_W:
        branch = Bytecode.intAt(text, offset+1);
        target = offset+branch;
        jsr = 0;
        for (int i = 0; i < offset; i++)
          if (Bytecode.isValid(textInfo, i) && !Bytecode.isUnreachable(textInfo, i))
            switch (Bytecode.opcodeAt(text, i)) {
            case JSR:
              if (!Bytecode.isUnreachable(textInfo, i+3))
                jsr++;
              break;
            case JSR_W:
              if (!Bytecode.isUnreachable(textInfo, i+5))
                jsr++;
              break;
            }
        builder.iconst(r.st(0), jsr);
        builder.jump(r.gl(target));
        break;

      }

      offset += opsize;
    }

    if (synch) {
      r.changeTo(text.length, 1);
      builder.label(r.gl(text.length));
      builder.ajumplocked(r.th(), r.ll(0));
      generateException(builder, r, "java/lang/IllegalMonitorStateException");
      builder.label(r.ll(0));
      builder.aunlock(r.th());
      builder.athrow(r.st(-1));
    }

    if (clinit) {
      r.changeTo(text.length, 1);
      builder.label(r.gl(text.length));
      builder.ajumpsubtype(r.st(-1), "java/lang/Error", r.ll(0));
      genExceptionInInitializerError(builder, r);
      builder.label(r.ll(0));
      builder.athrow(r.st(-1));
    }

    r.changeTo(0, 0);
    for (int i = 0; i < handlers.length; i++) {
      NestedHandler handler = handlers[i];
      if (handler.getHandlerPC() == text.length || !Bytecode.isUnreachable(textInfo, handler.getHandlerPC())) {
        builder.label(r.cl(handler.getIndex()));
        builder.acatch(r.ex(), "java/lang/Throwable");
        builder.label(r.hl(handler.getIndex()));
        if (handler.getCatchType() != null)
          if (handler.delegateTo != null)
            builder.ajumpnotsubtype(r.ex(), handler.getCatchType(), r.hl(handler.delegateTo.getIndex()));
          else {
            builder.ajumpsubtype(r.ex(), handler.getCatchType(), r.fl(handler.getIndex()));
            builder.athrow(r.ex());
            builder.label(r.fl(handler.getIndex()));
          }
        builder.acopy(r.st(0), r.ex());
        builder.jump(r.gl(handler.getHandlerPC()));
      }
    }

    return builder.toCFG();

  }

  private static final class Regs {

    private final int maxLocals;
    private final int maxStack;
    private final int maxDims;
    private final int length;
    private final int handlers;
    private int stackSize;
    private int offset;
    private int seq;

    public Regs(int maxLocals, int maxStack, int maxDims, int length, int handlers) { 
      if (maxLocals < 0)
        throw new IllegalArgumentException();
      this.maxLocals = maxLocals;
      if (maxStack < 0)
        throw new IllegalArgumentException();
      this.maxStack = maxStack;
      if (maxDims < 0 || maxDims > 255)
        throw new IllegalArgumentException();
      this.maxDims = maxDims;
      if (length < 0)
        throw new IllegalArgumentException();
      this.length = length;
      if (handlers < 0)
        throw new IllegalArgumentException();
      this.handlers = handlers;
    }

    public void changeTo(int offset, int stackSize) {
      if (offset < 0 || offset > length)
        throw new IllegalArgumentException();
      this.offset = offset;
      if (stackSize < 0 || stackSize > maxStack)
        throw new IllegalArgumentException();
      this.stackSize = stackSize;
    }

    public int maxLabel() {
      return (5+2*maxDims)*(length+1)+3*handlers;
    }

    public int th() {
      return maxLocals+maxStack;
    }

    public int ex() {
      return maxLocals+maxStack+1;
    }

    public int st(int index) {
      int absolute = stackSize+index;
      if (absolute < 0 || absolute >= maxStack)
        throw new IllegalArgumentException();
      return maxLocals+absolute;
    }

    public int fr(int index) {
      if (index < 0 || index >= maxLocals)
        throw new IllegalArgumentException();
      return index;
    }

    public int tr(int index) {
      if (index < 0)
        throw new IllegalArgumentException();
      return maxLocals+maxStack+2+index;
    }

    public int gl(int offset) {
      if (offset < 0 || offset > length)
        throw new IllegalArgumentException();
      return (5+2*maxDims)*offset;
    }

    public int ll(int index) {
      if (index < 0 || index > 3+2*maxDims)
        throw new IllegalArgumentException();
      return ((5+2*maxDims)*offset)+index+1;
    }

    public int cl(int index) {
      if (index < 0 || index >= handlers)
        throw new IllegalArgumentException();
      return (5+2*maxDims)*(length+1)+3*index;
    }

    public int hl(int index) {
      if (index < 0 || index >= handlers)
        throw new IllegalArgumentException();
      return (5+2*maxDims)*(length+1)+3*index+1;
    }

    public int fl(int index) {
      if (index < 0 || index >= handlers)
        throw new IllegalArgumentException();
      return (5+2*maxDims)*(length+1)+3*index+2;
    }

    public int sl() {
      return (5+2*maxDims)*(length+1)+3*handlers+seq++;
    }

  }

  private static NestedHandler[] rewriteHandlers(HandlerInfo[] handlers, byte[] text, boolean overall) {
    ArrayList list = new ArrayList(handlers.length+1);

    if (overall)
      list.add(new NestedHandler(text));
    for (int i = handlers.length-1; i >= 0; i--)
      list.add(new NestedHandler(handlers[i]));

internal_loop:
    for (int i = 1; i < list.size(); i++) {
      NestedHandler internal = (NestedHandler)list.get(i);
external_loop:
      for (int j = i-1; j >= 0; j--) {
        NestedHandler external = (NestedHandler)list.get(j);
        if (internal.getStartPC() < external.getStartPC()) {
          if (internal.getEndPC() <= external.getStartPC())
            continue external_loop;
          else if (internal.getEndPC() <= external.getEndPC()) {
            NestedHandler head = new NestedHandler(internal);
            head.end_pc = (char)external.getStartPC();
            list.add(i+1, head);
            internal.start_pc = (char)external.getStartPC();
            internal.delegateTo = external;
            continue internal_loop;
          } else {
            NestedHandler head = new NestedHandler(internal);
            head.end_pc = (char)external.getStartPC();
            list.add(i+1, head);
            NestedHandler tail = new NestedHandler(internal);
            tail.start_pc = (char)external.getEndPC();
            list.add(i+1, tail);
            internal.start_pc = (char)external.getStartPC();
            internal.end_pc = (char)external.getEndPC();
            internal.delegateTo = external;
            continue internal_loop;
          }
        } else if (internal.getStartPC() >= external.getEndPC())
          continue external_loop;
        else {
          if (internal.getEndPC() <= external.getEndPC()) {
            internal.delegateTo = external;
            continue internal_loop;
          } else {
            NestedHandler tail = new NestedHandler(internal);
            tail.start_pc = (char)external.getEndPC();
            list.add(i+1, tail);
            internal.end_pc = (char)external.getEndPC();
            internal.delegateTo = external;
            continue internal_loop;
          }
        }
      }
    }

    NestedHandler[] nestedHandlers = new NestedHandler[list.size()];
    for (int i = 0; i < nestedHandlers.length; i++) {
      nestedHandlers[i] = (NestedHandler)list.get(nestedHandlers.length-(i+1));
      nestedHandlers[i].index = i;
    }

    return nestedHandlers;
  }

  private static final class NestedHandler implements HandlerInfo {

    private int index = -1;
    private char start_pc;
    private char end_pc;
    private final char handler_pc;
    private final String catch_type;
    private NestedHandler delegateTo;

    public NestedHandler(HandlerInfo handler) {
      this.start_pc = (char)handler.getStartPC();
      this.end_pc = (char)handler.getEndPC();
      this.handler_pc = (char)handler.getHandlerPC();
      this.catch_type = handler.getCatchType();
    }

    public NestedHandler(byte[] text) {
      this.start_pc = 0;
      this.end_pc = (char)text.length;
      this.handler_pc = (char)text.length;
      this.catch_type = null;
    }

    public CodeInfo getOwner() {
      return null;
    }

    public int getIndex() {
      return index;
    }

    public int getStartPC() {
      return start_pc;
    }

    public int getEndPC() {
      return end_pc;
    }

    public int getHandlerPC() {
      return handler_pc;
    }

    public String getCatchType() {
      return catch_type;
    }

    public boolean encloses(int pc) {
      return start_pc <= pc && pc < end_pc;
    }

    public NestedHandler getDelegate() {
      return delegateTo;
    }

  }

}

