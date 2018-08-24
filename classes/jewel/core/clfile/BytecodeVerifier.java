/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.clfile;

import jewel.core.clfile.ClassInfo.ConstantPool;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo.HandlerInfo;
import jewel.core.clfile.Namespace.AbstractType;
import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;

final class BytecodeVerifier implements ClassFileConstants {

  static void checkStructuralConstraints(Namespace nameSpace, CodeInfo code, byte[] text, int[] textInfo, HandlerInfo[] handlers) throws VerifyException {
    int maxStack = code.getMaxStack();
    int maxLocals = code.getMaxLocals();
    MethodInfo method = code.getOwner();
    ConstantPool constantPool = method.getOwner().getConstantPool();

    ControlFlowGraph cfg = new ControlFlowGraph();
    VBlock currentBB = null;
    VBlock[] allBBs = new VBlock[text.length];
    for (int offset = 0; offset < text.length; offset++)
      if (Bytecode.isLeader(textInfo, offset)) {
        currentBB = new VBlock(offset, maxLocals);
        cfg.addNode(currentBB);
        allBBs[offset] = currentBB;
      }

    int[] diffs = new int[text.length];

    boolean fallsthru = false;

    int offset = 0;
    while (offset < text.length) {

      if (Bytecode.isLeader(textInfo, offset)) {
        VBlock nextBB = allBBs[offset];
        if (fallsthru)
          cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, nextBB));
        currentBB = nextBB;
        fallsthru = true;
      }

      short opcode = Bytecode.opcodeAt(text, offset);
      int opsize = Bytecode.sizeOf(opcode);

      diffs[offset] = currentBB.ssdiff;

      switch (opcode) {
      
      case ACONST_NULL:
        currentBB.apush(null);
        break;

      case ICONST_M1: case ICONST_0: case ICONST_1: case ICONST_2:
      case ICONST_3: case ICONST_4: case ICONST_5:
      case BIPUSH: case SIPUSH:
        currentBB.ipush();
        break;
      
      case LCONST_0: case LCONST_1:
        currentBB.lpush();
        break;
      
      case FCONST_0: case FCONST_1: case FCONST_2:
        currentBB.fpush();
        break;
      
      case DCONST_0: case DCONST_1:
        currentBB.dpush();
        break;
      
      case LDC:
        int index = Bytecode.ubyteAt(text, offset+1);
        switch (constantPool.tag(index)) {
        case CONSTANT_INTEGER:
          currentBB.ipush();
          break;
        case CONSTANT_FLOAT:
          currentBB.fpush();
          break;
        case CONSTANT_STRING:
          currentBB.apush("java/lang/String");
          break;
        }
        break;

      case LDC_W:
        index = Bytecode.ushortAt(text, offset+1);
        switch (constantPool.tag(index)) {
        case CONSTANT_INTEGER:
          currentBB.ipush();
          break;
        case CONSTANT_FLOAT:
          currentBB.fpush();
          break;
        case CONSTANT_STRING:
          currentBB.apush("java/lang/String");
          break;
        }
        break;

      case LDC2_W:
        index = Bytecode.ushortAt(text, offset+1);
        switch (constantPool.tag(index)) {
        case CONSTANT_LONG:
          currentBB.lpush();
          break;
        case CONSTANT_DOUBLE:
          currentBB.dpush();
          break;
        }
        break;

      case ILOAD:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.iensure(index);
        currentBB.ipush();
        break;

      case LLOAD:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.lensure(index);
        currentBB.lensure2(index+1);
        currentBB.lpush();
        break;

      case FLOAD:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.fensure(index);
        currentBB.fpush();
        break;

      case DLOAD:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.densure(index);
        currentBB.densure2(index+1);
        currentBB.dpush();
        break;

      case ALOAD:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.auload(index);
        break;

      case ILOAD_0:
        currentBB.iensure(0);
        currentBB.ipush();
        break;

      case ILOAD_1:
        currentBB.iensure(1);
        currentBB.ipush();
        break;

      case ILOAD_2:
        currentBB.iensure(2);
        currentBB.ipush();
        break;

      case ILOAD_3:
        currentBB.iensure(3);
        currentBB.ipush();
        break;

      case LLOAD_0:
        currentBB.lensure(0);
        currentBB.lensure2(1);
        currentBB.lpush();
        break;

      case LLOAD_1:
        currentBB.lensure(1);
        currentBB.lensure2(2);
        currentBB.lpush();
        break;

      case LLOAD_2:
        currentBB.lensure(2);
        currentBB.lensure2(3);
        currentBB.lpush();
        break;

      case LLOAD_3:
        currentBB.lensure(3);
        currentBB.lensure2(4);
        currentBB.lpush();
        break;

      case FLOAD_0:
        currentBB.fensure(0);
        currentBB.fpush();
        break;

      case FLOAD_1:
        currentBB.fensure(1);
        currentBB.fpush();
        break;

      case FLOAD_2:
        currentBB.fensure(2);
        currentBB.fpush();
        break;

      case FLOAD_3:
        currentBB.fensure(3);
        currentBB.fpush();
        break;

      case DLOAD_0:
        currentBB.densure(0);
        currentBB.densure2(1);
        currentBB.dpush();
        break;

      case DLOAD_1:
        currentBB.densure(1);
        currentBB.densure2(2);
        currentBB.dpush();
        break;

      case DLOAD_2:
        currentBB.densure(2);
        currentBB.densure2(3);
        currentBB.dpush();
        break;

      case DLOAD_3:
        currentBB.densure(3);
        currentBB.densure2(4);
        currentBB.dpush();
        break;

      case ALOAD_0:
        currentBB.auload(0);
        break;

      case ALOAD_1:
        currentBB.auload(1);
        break;

      case ALOAD_2:
        currentBB.auload(2);
        break;

      case ALOAD_3:
        currentBB.auload(3);
        break;

      case IALOAD:
        currentBB.ipop();
        currentBB.apopsubtype("[I");
        currentBB.ipush();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;
      
      case LALOAD:
        currentBB.ipop();
        currentBB.apopsubtype("[J");
        currentBB.lpush();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;
      
      case FALOAD:
        currentBB.ipop();
        currentBB.apopsubtype("[F");
        currentBB.fpush();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;
      
      case DALOAD:
        currentBB.ipop();
        currentBB.apopsubtype("[D");
        currentBB.dpush();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;
      
      case AALOAD:
        currentBB.ipop();
        currentBB.agetcomp();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;
      
      case BALOAD:
        currentBB.ipop();
        currentBB.apopbarray();
        currentBB.ipush();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;
      
      case CALOAD:
        currentBB.ipop();
        currentBB.apopsubtype("[C");
        currentBB.ipush();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case SALOAD:
        currentBB.ipop();
        currentBB.apopsubtype("[S");
        currentBB.ipush();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case ISTORE:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.ipop();
        currentBB.iset(index);
        break;
      
      case LSTORE:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.lpop();
        currentBB.lset(index);
        currentBB.lset2(index+1);
        break;
      
      case FSTORE:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.fpop();
        currentBB.fset(index);
        break;
      
      case DSTORE:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.dpop();
        currentBB.dset(index);
        currentBB.dset2(index+1);
        break;
      
      case ASTORE:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.aurstore(offset, index);
        break;

      case ISTORE_0:
        currentBB.ipop();
        currentBB.iset(0);
        break;
      
      case ISTORE_1:
        currentBB.ipop();
        currentBB.iset(1);
        break;
      
      case ISTORE_2:
        currentBB.ipop();
        currentBB.iset(2);
        break;
      
      case ISTORE_3:
        currentBB.ipop();
        currentBB.iset(3);
        break;
      
      case LSTORE_0:
        currentBB.lpop();
        currentBB.lset(0);
        currentBB.lset2(1);
        break;
      
      case LSTORE_1:
        currentBB.lpop();
        currentBB.lset(1);
        currentBB.lset2(2);
        break;
      
      case LSTORE_2:
        currentBB.lpop();
        currentBB.lset(2);
        currentBB.lset2(3);
        break;
      
      case LSTORE_3:
        currentBB.lpop();
        currentBB.lset(3);
        currentBB.lset2(4);
        break;
      
      case FSTORE_0:
        currentBB.fpop();
        currentBB.fset(0);
        break;
      
      case FSTORE_1:
        currentBB.fpop();
        currentBB.fset(1);
        break;
      
      case FSTORE_2:
        currentBB.fpop();
        currentBB.fset(2);
        break;
      
      case FSTORE_3:
        currentBB.fpop();
        currentBB.fset(3);
        break;
      
      case DSTORE_0:
        currentBB.dpop();
        currentBB.dset(0);
        currentBB.dset2(1);
        break;
      
      case DSTORE_1:
        currentBB.dpop();
        currentBB.dset(1);
        currentBB.dset2(2);
        break;
      
      case DSTORE_2:
        currentBB.dpop();
        currentBB.dset(2);
        currentBB.dset2(3);
        break;
      
      case DSTORE_3:
        currentBB.dpop();
        currentBB.dset(3);
        currentBB.dset2(4);
        break;
      
      case ASTORE_0:
        currentBB.aurstore(offset, 0);
        break;
      
      case ASTORE_1:
        currentBB.aurstore(offset, 1);
        break;
      
      case ASTORE_2:
        currentBB.aurstore(offset, 2);
        break;
      
      case ASTORE_3:
        currentBB.aurstore(offset, 3);
        break;
      
      case IASTORE:
        currentBB.ipop();
        currentBB.ipop();
        currentBB.apopsubtype("[I");
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case LASTORE: 
        currentBB.lpop();
        currentBB.ipop();
        currentBB.apopsubtype("[J");
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case FASTORE:
        currentBB.fpop();
        currentBB.ipop();
        currentBB.apopsubtype("[F");
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case DASTORE:
        currentBB.dpop();
        currentBB.ipop();
        currentBB.apopsubtype("[D");
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case AASTORE:
        currentBB.swap1x1(-1);
        currentBB.ipop();
        currentBB.asetcomp();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case BASTORE:
        currentBB.ipop();
        currentBB.ipop();
        currentBB.apopbarray();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case CASTORE:
        currentBB.ipop();
        currentBB.ipop();
        currentBB.apopsubtype("[C");
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case SASTORE:
        currentBB.ipop();
        currentBB.ipop();
        currentBB.apopsubtype("[S");
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case POP:
        currentBB.pop1(offset);
        break;

      case POP2:
        currentBB.pop2(offset);
        break;

      case DUP:
        currentBB.dup1(offset);
        break;

      case DUP_X1:
        currentBB.dup1x1(offset);
        break;

      case DUP_X2:
        currentBB.dup1x2(offset);
        break;

      case DUP2:
        currentBB.dup2(offset);
        break;

      case DUP2_X1:
        currentBB.dup2x1(offset);
        break;

      case DUP2_X2:
        currentBB.dup2x2(offset);
        break;

      case SWAP:
        currentBB.swap1x1(offset);
        break;

      case IADD: case ISUB: case IMUL:
      case ISHL: case ISHR: case IUSHR:
      case IAND: case IOR: case IXOR:
        currentBB.ipop();
        currentBB.ipop();
        currentBB.ipush();
        break;

      case LADD: case LSUB: case LMUL:
      case LAND: case LOR: case LXOR:
        currentBB.lpop();
        currentBB.lpop();
        currentBB.lpush();
        break;

      case FADD: case FSUB: case FMUL: case FDIV: case FREM:
        currentBB.fpop();
        currentBB.fpop();
        currentBB.fpush();
        break;

      case DADD: case DSUB: case DMUL: case DDIV: case DREM:
        currentBB.dpop();
        currentBB.dpop();
        currentBB.dpush();
        break;

      case IDIV: case IREM:
        currentBB.ipop();
        currentBB.ipop();
        currentBB.ipush();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case LDIV: case LREM:
        currentBB.lpop();
        currentBB.lpop();
        currentBB.lpush();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case INEG:
      case I2B: case I2C: case I2S:
        currentBB.ipop();
        currentBB.ipush();
        break;

      case LNEG:
        currentBB.lpop();
        currentBB.lpush();
        break;

      case FNEG:
        currentBB.fpop();
        currentBB.fpush();
        break;

      case DNEG:
        currentBB.dpop();
        currentBB.dpush();
        break;

      case LSHL: case LSHR: case LUSHR:
        currentBB.ipop();
        currentBB.lpop();
        currentBB.lpush();
        break;

      case IINC:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.iensure(index);
        break;

      case I2L:
        currentBB.ipop();
        currentBB.lpush();
        break;

      case I2F:
        currentBB.ipop();
        currentBB.fpush();
        break;

      case I2D:
        currentBB.ipop();
        currentBB.dpush();
        break;

      case L2I:
        currentBB.lpop();
        currentBB.ipush();
        break;

      case L2F:
        currentBB.lpop();
        currentBB.fpush();
        break;

      case L2D:
        currentBB.lpop();
        currentBB.dpush();
        break;

      case F2I:
        currentBB.fpop();
        currentBB.ipush();
        break;

      case F2L:
        currentBB.fpop();
        currentBB.lpush();
        break;

      case F2D:
        currentBB.fpop();
        currentBB.dpush();
        break;

      case D2I:
        currentBB.dpop();
        currentBB.ipush();
        break;

      case D2L:
        currentBB.dpop();
        currentBB.lpush();
        break;

      case D2F:
        currentBB.dpop();
        currentBB.fpush();
        break;

      case LCMP:
        currentBB.lpop();
        currentBB.lpop();
        currentBB.ipush();
        break;

      case FCMPL: case FCMPG:
        currentBB.fpop();
        currentBB.fpop();
        currentBB.ipush();
        break;

      case DCMPL: case DCMPG:
        currentBB.dpop();
        currentBB.dpop();
        currentBB.ipush();
        break;

      case IFEQ: case IFNE: case IFLT: case IFGE: case IFGT: case IFLE:
        int branch = Bytecode.shortAt(text, offset+1);
        int target = offset+branch;
        currentBB.ipop();
        cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        break;

      case IF_ICMPEQ: case IF_ICMPNE: case IF_ICMPLT: case IF_ICMPGE: case IF_ICMPGT: case IF_ICMPLE:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        currentBB.ipop();
        currentBB.ipop();
        cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        break;

      case IF_ACMPEQ: case IF_ACMPNE:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        currentBB.apop();
        currentBB.apop();
        cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        break;

      case GOTO:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        fallsthru = false;
        break;

      case JSR:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        currentBB.rpush(offset+3);
        cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        fallsthru = false;
        break;

      case RET:
        index = Bytecode.ubyteAt(text, offset+1);
        currentBB.rensure(index);
        fallsthru = false;
        break;

      case TABLESWITCH:
        int pad = ~offset & 3;
        branch = Bytecode.intAt(text, offset+1+pad);
        target = offset+branch;
        cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        int low = Bytecode.intAt(text, offset+1+pad+4);
        int high = Bytecode.intAt(text, offset+1+pad+8);
        int entries = high-low+1;
        opsize = 1+pad+12+4*entries;
        for (int i = 0; i < entries; i++) {
          branch = Bytecode.intAt(text, offset+1+pad+12+4*i);
          target = offset+branch;
          cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        }
        currentBB.ipop();
        fallsthru = false;
        break;

      case LOOKUPSWITCH:
        pad = ~offset & 3;
        branch = Bytecode.intAt(text, offset+1+pad);
        target = offset+branch;
        cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        entries = Bytecode.intAt(text, offset+1+pad+4);
        opsize = 1+pad+8+8*entries;
        for (int i = 0; i < entries; i++) {
          branch = Bytecode.intAt(text, offset+1+pad+8+8*i+4);
          target = offset+branch;
          cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        }
        currentBB.ipop();
        fallsthru = false;
        break;

      case IRETURN:
        currentBB.ipop();
        fallsthru = false;
        break;

      case LRETURN:
        currentBB.lpop();
        fallsthru = false;
        break;

      case FRETURN:
        currentBB.fpop();
        fallsthru = false;
        break;

      case DRETURN:
        currentBB.dpop();
        fallsthru = false;
        break;

      case ARETURN:
        String desc = method.getDescriptor();
        String type = desc.substring(desc.lastIndexOf(')')+1);
        if (type.charAt(0) == 'L')
          type = type.substring(1, type.length()-1);
        currentBB.apopsubtype(type);
        fallsthru = false;
        break;

      case RETURN:
        if (method.getName().equals("<init>"))
          currentBB.checkinit();
        fallsthru = false;
        break;

      case GETSTATIC:
        index = Bytecode.ushortAt(text, offset+1);
        type = constantPool.getDescriptor(index);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          currentBB.ipush();
          break;
        case 'J':
          currentBB.lpush();
          break;
        case 'F':
          currentBB.fpush();
          break;
        case 'D':
          currentBB.dpush();
          break;
        case 'L':
          currentBB.apush(type.substring(1, type.length()-1));
          break;
        case '[':
          currentBB.apush(type);
          break;
        }
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case PUTSTATIC:
        index = Bytecode.ushortAt(text, offset+1);
        type = constantPool.getDescriptor(index);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          currentBB.ipop();
          break;
        case 'J':
          currentBB.lpop();
          break;
        case 'F':
          currentBB.fpop();
          break;
        case 'D':
          currentBB.dpop();
          break;
        case 'L':
          currentBB.apopsubtype(type.substring(1, type.length()-1));
          break;
        case '[':
          currentBB.apopsubtype(type);
          break;
        }
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case GETFIELD:
        index = Bytecode.ushortAt(text, offset+1);
        type = constantPool.getClass(index);
        currentBB.apopsubtype(type);
        type = constantPool.getDescriptor(index);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          currentBB.ipush();
          break;
        case 'J':
          currentBB.lpush();
          break;
        case 'F':
          currentBB.fpush();
          break;
        case 'D':
          currentBB.dpush();
          break;
        case 'L':
          currentBB.apush(type.substring(1, type.length()-1));
          break;
        case '[':
          currentBB.apush(type);
          break;
        }
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case PUTFIELD:
        index = Bytecode.ushortAt(text, offset+1);
        type = constantPool.getDescriptor(index);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          currentBB.ipop();
          break;
        case 'J':
          currentBB.lpop();
          break;
        case 'F':
          currentBB.fpop();
          break;
        case 'D':
          currentBB.dpop();
          break;
        case 'L':
          currentBB.apopsubtype(type.substring(1, type.length()-1));
          break;
        case '[':
          currentBB.apopsubtype(type);
          break;
        }
        type = constantPool.getClass(index);
        currentBB.aupopsubtype(type);
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case INVOKEVIRTUAL:
        index = Bytecode.ushortAt(text, offset+1);
        String descriptor = constantPool.getDescriptor(index);
        String[] params = Syntax.getParameterTypes(descriptor);
        for (int i = params.length-1; i >= 0; i--) {
          type = params[i];
          switch (type.charAt(0)) {
          case 'Z': case 'B': case 'C': case 'S': case 'I':
            currentBB.ipop();
            break;
          case 'J':
            currentBB.lpop();
            break;
          case 'F':
            currentBB.fpop();
            break;
          case 'D':
            currentBB.dpop();
            break;
          case 'L':
            currentBB.apopsubtype(type.substring(1, type.length()-1));
            break;
          case '[':
            currentBB.apopsubtype(type);
            break;
          }
        }
        type = constantPool.getClass(index);
        currentBB.apopsubtype(type);
        type = descriptor.substring(descriptor.lastIndexOf(')')+1);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          currentBB.ipush();
          break;
        case 'J':
          currentBB.lpush();
          break;
        case 'F':
          currentBB.fpush();
          break;
        case 'D':
          currentBB.dpush();
          break;
        case 'L':
          currentBB.apush(type.substring(1, type.length()-1));
          break;
        case '[':
          currentBB.apush(type);
          break;
        case 'V':
          break;
        }
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case INVOKESPECIAL:
        index = Bytecode.ushortAt(text, offset+1);
        String name = constantPool.getName(index);
        descriptor = constantPool.getDescriptor(index);
        params = Syntax.getParameterTypes(descriptor);
        for (int i = params.length-1; i >= 0; i--) {
          type = params[i];
          switch (type.charAt(0)) {
          case 'Z': case 'B': case 'C': case 'S': case 'I':
            currentBB.ipop();
            break;
          case 'J':
            currentBB.lpop();
            break;
          case 'F':
            currentBB.fpop();
            break;
          case 'D':
            currentBB.dpop();
            break;
          case 'L':
            currentBB.apopsubtype(type.substring(1, type.length()-1));
            break;
          case '[':
            currentBB.apopsubtype(type);
            break;
          }
        }
        type = constantPool.getClass(index);
        if (name.equals("<init>"))
          currentBB.upopinit(type);
        else
          currentBB.apopsubtype(type);
        type = descriptor.substring(descriptor.lastIndexOf(')')+1);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          currentBB.ipush();
          break;
        case 'J':
          currentBB.lpush();
          break;
        case 'F':
          currentBB.fpush();
          break;
        case 'D':
          currentBB.dpush();
          break;
        case 'L':
          currentBB.apush(type.substring(1, type.length()-1));
          break;
        case '[':
          currentBB.apush(type);
          break;
        case 'V':
          break;
        }
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case INVOKESTATIC:
        index = Bytecode.ushortAt(text, offset+1);
        descriptor = constantPool.getDescriptor(index);
        params = Syntax.getParameterTypes(descriptor);
        for (int i = params.length-1; i >= 0; i--) {
          type = params[i];
          switch (type.charAt(0)) {
          case 'Z': case 'B': case 'C': case 'S': case 'I':
            currentBB.ipop();
            break;
          case 'J':
            currentBB.lpop();
            break;
          case 'F':
            currentBB.fpop();
            break;
          case 'D':
            currentBB.dpop();
            break;
          case 'L':
            currentBB.apopsubtype(type.substring(1, type.length()-1));
            break;
          case '[':
            currentBB.apopsubtype(type);
            break;
          }
        }
        type = descriptor.substring(descriptor.lastIndexOf(')')+1);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          currentBB.ipush();
          break;
        case 'J':
          currentBB.lpush();
          break;
        case 'F':
          currentBB.fpush();
          break;
        case 'D':
          currentBB.dpush();
          break;
        case 'L':
          currentBB.apush(type.substring(1, type.length()-1));
          break;
        case '[':
          currentBB.apush(type);
          break;
        case 'V':
          break;
        }
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case INVOKEINTERFACE:
        index = Bytecode.ushortAt(text, offset+1);
        descriptor = constantPool.getDescriptor(index);
        params = Syntax.getParameterTypes(descriptor);
        for (int i = params.length-1; i >= 0; i--) {
          type = params[i];
          switch (type.charAt(0)) {
          case 'Z': case 'B': case 'C': case 'S': case 'I':
            currentBB.ipop();
            break;
          case 'J':
            currentBB.lpop();
            break;
          case 'F':
            currentBB.fpop();
            break;
          case 'D':
            currentBB.dpop();
            break;
          case 'L':
            currentBB.apopsubtype(type.substring(1, type.length()-1));
            break;
          case '[':
            currentBB.apopsubtype(type);
            break;
          }
        }
        currentBB.apop();
        type = descriptor.substring(descriptor.lastIndexOf(')')+1);
        switch (type.charAt(0)) {
        case 'Z': case 'B': case 'C': case 'S': case 'I':
          currentBB.ipush();
          break;
        case 'J':
          currentBB.lpush();
          break;
        case 'F':
          currentBB.fpush();
          break;
        case 'D':
          currentBB.dpush();
          break;
        case 'L':
          currentBB.apush(type.substring(1, type.length()-1));
          break;
        case '[':
          currentBB.apush(type);
          break;
        case 'V':
          break;
        }
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case NEW:
        index = Bytecode.ushortAt(text, offset+1);
        type = constantPool.getClass(index);
        currentBB.upush(offset);
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case NEWARRAY:
        int atype = Bytecode.ubyteAt(text, offset+1);
        currentBB.ipop();
        switch (atype) {
        case T_BOOLEAN:
          currentBB.apush("[Z");
          break;
        case T_BYTE:
          currentBB.apush("[B");
          break;
        case T_CHAR:
          currentBB.apush("[C");
          break;
        case T_SHORT:
          currentBB.apush("[S");
          break;
        case T_INT:
          currentBB.apush("[I");
          break;
        case T_LONG:
          currentBB.apush("[J");
          break;
        case T_FLOAT:
          currentBB.apush("[F");
          break;
        case T_DOUBLE:
          currentBB.apush("[D");
          break;
        }
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case ANEWARRAY:
        index = Bytecode.ushortAt(text, offset+1);
        type = constantPool.getClass(index);
        currentBB.ipop();
        currentBB.apush(type.charAt(0) == '[' ? "["+type : "[L"+type+";");
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case ARRAYLENGTH:
        currentBB.apoparray();
        currentBB.ipush();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case ATHROW:
        currentBB.apopsubtype("java/lang/Throwable");
        addHandlers(handlers, offset, currentBB, allBBs);
        fallsthru = false;
        break;

      case CHECKCAST:
        index = Bytecode.ushortAt(text, offset+1);
        type = constantPool.getClass(index);
        currentBB.apop();
        currentBB.apush(type);
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case INSTANCEOF:
        currentBB.apop();
        currentBB.ipush();
        break;

      case MONITORENTER: case MONITOREXIT:
        currentBB.apop();
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case WIDE:

        short wopcode = Bytecode.opcodeAt(text, offset+1);
        opsize = Bytecode.wsizeOf(wopcode);

        switch (wopcode) {
        
        case ILOAD:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.iensure(index);
          currentBB.ipush();
          break;

        case LLOAD:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.lensure(index);
          currentBB.lensure2(index+1);
          currentBB.lpush();
          break;

        case FLOAD:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.fensure(index);
          currentBB.fpush();
          break;

        case DLOAD:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.densure(index);
          currentBB.densure2(index+1);
          currentBB.dpush();
          break;

        case ALOAD:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.auload(index);
          break;

        case ISTORE:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.ipop();
          currentBB.iset(index);
          break;

        case LSTORE:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.lpop();
          currentBB.lset(index);
          currentBB.lset2(index+1);
          break;

        case FSTORE:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.fpop();
          currentBB.fset(index);
          break;

        case DSTORE:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.dpop();
          currentBB.dset(index);
          currentBB.dset2(index+1);
          break;

        case ASTORE:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.aurstore(offset, index);
          break;

        case IINC:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.iensure(index);
          break;

        case RET:
          index = Bytecode.ushortAt(text, offset+2);
          currentBB.rensure(index);
          fallsthru = false;
          break;

        }
        break;

      case MULTIANEWARRAY:
        index = Bytecode.ushortAt(text, offset+1);
        type = constantPool.getClass(index);
        int dims = Bytecode.ubyteAt(text, offset+3);
        for (int i = 0; i < dims; i++)
          currentBB.ipop();
        currentBB.apush(type);
        addHandlers(handlers, offset, currentBB, allBBs);
        break;

      case IFNULL: case IFNONNULL:
        branch = Bytecode.shortAt(text, offset+1);
        target = offset+branch;
        currentBB.apop();
        cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        break;

      case GOTO_W:
        branch = Bytecode.intAt(text, offset+1);
        target = offset+branch;
        cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        fallsthru = false;
        break;

      case JSR_W:
        branch = Bytecode.intAt(text, offset+1);
        target = offset+branch;
        currentBB.rpush(offset+5);
        cfg.addEdge(new VEdge(currentBB, VEdge.NORMAL, allBBs[target]));
        fallsthru = false;
        break;

      }

      offset += opsize;
    }

    if (fallsthru)
      currentBB.checkfall();

//    cfg.show(cfg);

    VBlock entryBB = allBBs[0];
    entryBB.inDFA = new Frame(nameSpace, method, maxStack, maxLocals);

    HashSet set = new HashSet();
    set.add(entryBB);
    while (!set.isEmpty()) {
      currentBB = (VBlock)set.iterator().next();
      set.remove(currentBB);
      currentBB.flow(nameSpace, constantPool, method, text, textInfo, allBBs, set);
    }

    for (offset = 0; offset < text.length; offset++) {
      if (Bytecode.isLeader(textInfo, offset))
        currentBB = allBBs[offset];
      if (Bytecode.isValid(textInfo, offset)) {
        if (currentBB.inDFA == null)
          Bytecode.setUnreachable(textInfo, offset);
        else
          Bytecode.setStackSize(textInfo, offset, currentBB.inDFA.ss+diffs[offset]);
      }
    }

  }

  private static void addHandlers(HandlerInfo[] handlers, int offset, VBlock currentBB, VBlock[] allBBs) {
    ControlFlowGraph cfg = currentBB.ownerCFG();
    for (int i = 0; i < handlers.length; i++) {
      HandlerInfo handler = handlers[i];
      if (handler.encloses(offset)) {
        String catchType = handler.getCatchType();
        if (catchType == null)
          catchType = "java/lang/Throwable";
        cfg.addEdge(new VEdge(currentBB, VEdge.EXCEPTION, catchType, allBBs[handler.getHandlerPC()]));
      }
    }
  }

  private static final class VBlock extends BasicBlock {

    private final int startPC;
    private final BitSet written;
    private int ssdiff;
    private int minssdiff;
    private int maxssdiff;

    private Frame inDFA;
    private Frame jsrDFA;

    public VBlock(int startPC, int maxLocals) {
      this.startPC = startPC;
      written = new BitSet(maxLocals);
    }

    private void incsp(int min, int max) {
      if (ssdiff-min < minssdiff)
        minssdiff = ssdiff-min;
      ssdiff += max;
      if (ssdiff > maxssdiff)
        maxssdiff = ssdiff;
    }

    private void decsp(int min, int max) {
      if (ssdiff+max > maxssdiff)
        maxssdiff = ssdiff+max;
      ssdiff -= min;
      if (ssdiff < minssdiff)
        minssdiff = ssdiff;
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(hashCode());
      sb.append(" [shape=record,label=\"{BB["+startPC+"]|");
      for (Iterator i = topdownStmts(); i.hasNext(); ) {
        sb.append(i.next());
        sb.append("\\n");
      }
      sb.append("}\"];");
      return sb.toString();
    }

    private byte typeOf(Object value) {
           if (value == "I")                     return Bytecode.X_INT;
      else if (value == "F")                     return Bytecode.X_FLOAT;
      else if (value == "L")                     return Bytecode.X_LONG;
      else if (value == "D")                     return Bytecode.X_DOUBLE;
      else if (value instanceof ReturnAddress[]) return Bytecode.X_ADDR;
      else if (value == "N" || 
               value instanceof AbstractType || 
               value instanceof Integer)         return Bytecode.X_REF;
      return 0;
    }

    private byte typeOf(Object value1, Object value2) {
      return (byte)(4*typeOf(value1)+typeOf(value2));
    }

    public void flow(Namespace nameSpace, ConstantPool constantPool, MethodInfo method, byte[] text, int[] textInfo, VBlock[] allBBs, HashSet set) throws VerifyException {

      if (inDFA.ss+maxssdiff > inDFA.stack.length)
        throw new VerifyException("Stack size too large");
      if (inDFA.ss+minssdiff < 0)
        throw new VerifyException("Unable to pop operand off an empty stack");

      Frame outDFA = new Frame(inDFA);

      outDFA.ss += ssdiff;
      for (int i = 0; i < outDFA.sp; i++)
        if (outDFA.stack[i] instanceof ReturnAddress[]) {
          ReturnAddress[] array = (ReturnAddress[])outDFA.stack[i];
          for (int j = 0; j < array.length; j++)
            array[j].written.or(written);
        }
      for (int i = 0; i < outDFA.locals.length; i++)
        if (outDFA.locals[i] instanceof ReturnAddress[]) {
          ReturnAddress[] array = (ReturnAddress[])outDFA.locals[i];
          for (int j = 0; j < array.length; j++)
            array[j].written.or(written);
        }

      boolean initLast = false;
      ReturnAddress[] addresses = null;

      for (Iterator i = topdownStmts(); i.hasNext(); ) {
        VOp vop = (VOp)i.next();

        initLast = false;

        switch (vop.code) {
        case VOp.IPUSH:
          outDFA.stack[outDFA.sp++] = "I";
          break;
        case VOp.IPOP:
          if (outDFA.stack[--outDFA.sp] != "I")
            throw new VerifyException("Expecting to find integer on stack");
          break;
        case VOp.IENSURE:
          if (outDFA.locals[vop.index] != "I")
            throw new VerifyException("Register "+vop.index+" contains wrong type");
          break;
        case VOp.ISET:
          outDFA.locals[vop.index] = "I";
          break;

        case VOp.LPUSH:
          outDFA.stack[outDFA.sp++] = "L";
          break;
        case VOp.LPOP:
          if (outDFA.stack[--outDFA.sp] != "L")
            throw new VerifyException("Expecting to find long on stack");
          break;
        case VOp.LENSURE:
          if (outDFA.locals[vop.index] != "L")
            throw new VerifyException("Register pair "+vop.index+"/"+(vop.index+1)+" contains wrong type");
          break;
        case VOp.LSET:
          outDFA.locals[vop.index] = "L";
          break;
        case VOp.LENSURE2:
          if (outDFA.locals[vop.index] != "L2")
            throw new VerifyException("Register pair "+(vop.index-1)+"/"+vop.index+" contains wrong type");
          break;
        case VOp.LSET2:
          outDFA.locals[vop.index] = "L2";
          break;

        case VOp.FPUSH:
          outDFA.stack[outDFA.sp++] = "F";
          break;
        case VOp.FPOP:
          if (outDFA.stack[--outDFA.sp] != "F")
            throw new VerifyException("Expecting to find float on stack");
          break;
        case VOp.FENSURE:
          if (outDFA.locals[vop.index] != "F")
            throw new VerifyException("Register "+vop.index+" contains wrong type");
          break;
        case VOp.FSET:
          outDFA.locals[vop.index] = "F";
          break;

        case VOp.DPUSH:
          outDFA.stack[outDFA.sp++] = "D";
          break;
        case VOp.DPOP:
          if (outDFA.stack[--outDFA.sp] != "D")
            throw new VerifyException("Expecting to find double on stack");
          break;
        case VOp.DENSURE:
          if (outDFA.locals[vop.index] != "D")
            throw new VerifyException("Register pair "+vop.index+"/"+(vop.index+1)+" contains wrong type");
          break;
        case VOp.DSET:
          outDFA.locals[vop.index] = "D";
          break;
        case VOp.DENSURE2:
          if (outDFA.locals[vop.index] != "D2")
            throw new VerifyException("Register pair "+(vop.index-1)+"/"+vop.index+" contains wrong type");
          break;
        case VOp.DSET2:
          outDFA.locals[vop.index] = "D2";
          break;

        case VOp.APUSH:
          outDFA.stack[outDFA.sp++] = vop.type == null ? "N" : (Object)nameSpace.forName(vop.type);
          break;
        case VOp.APOP:
          if (outDFA.stack[outDFA.sp-1] instanceof Integer) {
            String superName = method.getOwner().getSuperclass();
            int offset = ((Integer)outDFA.stack[outDFA.sp-1]).intValue();
            if (superName == null && offset == -1) {
              outDFA.init = true;
              AbstractType newType = nameSpace.forName(method.getOwner().getName());
              for (int j = 0; j < outDFA.sp; j++)
                if (outDFA.stack[j] instanceof Integer && ((Integer)outDFA.stack[j]).intValue() == offset)
                  outDFA.stack[j] = newType;
              for (int j = 0; j < outDFA.locals.length; j++)
                if (outDFA.locals[j] instanceof Integer && ((Integer)outDFA.locals[j]).intValue() == offset)
                  outDFA.locals[j] = newType;
            }
          }
          --outDFA.sp;
          if (outDFA.stack[outDFA.sp] != "N" && !(outDFA.stack[outDFA.sp] instanceof AbstractType))
            throw new VerifyException("Expecting to find object/array on stack");
          break;
        case VOp.APOPSUBTYPE:
          if (outDFA.stack[outDFA.sp-1] instanceof Integer) {
            String superName = method.getOwner().getSuperclass();
            int offset = ((Integer)outDFA.stack[outDFA.sp-1]).intValue();
            if (superName == null && offset == -1) {
              outDFA.init = true;
              AbstractType newType = nameSpace.forName(method.getOwner().getName());
              for (int j = 0; j < outDFA.sp; j++)
                if (outDFA.stack[j] instanceof Integer && ((Integer)outDFA.stack[j]).intValue() == offset)
                  outDFA.stack[j] = newType;
              for (int j = 0; j < outDFA.locals.length; j++)
                if (outDFA.locals[j] instanceof Integer && ((Integer)outDFA.locals[j]).intValue() == offset)
                  outDFA.locals[j] = newType;
            }
          }
          --outDFA.sp;
          if (outDFA.stack[outDFA.sp] != "N") {
            if (!(outDFA.stack[outDFA.sp] instanceof AbstractType))
              throw new VerifyException("Expecting to find object/array on stack");
            nameSpace.ensureSubtyping(nameSpace.forName(vop.type), (AbstractType)outDFA.stack[outDFA.sp]);
          }
          break;
        case VOp.AUPOPSUBTYPE:
          if (outDFA.stack[outDFA.sp-1] instanceof Integer) {
            String superName = method.getOwner().getSuperclass();
            int offset = ((Integer)outDFA.stack[outDFA.sp-1]).intValue();
            if (superName == null && offset == -1) {
              outDFA.init = true;
              AbstractType newType = nameSpace.forName(method.getOwner().getName());
              for (int j = 0; j < outDFA.sp; j++)
                if (outDFA.stack[j] instanceof Integer && ((Integer)outDFA.stack[j]).intValue() == offset)
                  outDFA.stack[j] = newType;
              for (int j = 0; j < outDFA.locals.length; j++)
                if (outDFA.locals[j] instanceof Integer && ((Integer)outDFA.locals[j]).intValue() == offset)
                  outDFA.locals[j] = newType;
            }
          }
          --outDFA.sp;
          if (outDFA.stack[outDFA.sp] != "N") {
            if (!(outDFA.stack[outDFA.sp] instanceof AbstractType)) {
              if (outDFA.stack[outDFA.sp] instanceof Integer) {
                String name = method.getOwner().getName();
                int offset = ((Integer)outDFA.stack[outDFA.sp]).intValue();
                if (name.equals(vop.type) && offset == -1)
                  break;
              }
              throw new VerifyException("Expecting to find object/array on stack");
            }
            nameSpace.ensureSubtyping(nameSpace.forName(vop.type), (AbstractType)outDFA.stack[outDFA.sp]);
          }
          break;
        case VOp.APOPARRAY:
          if (outDFA.stack[outDFA.sp-1] instanceof Integer) {
            String superName = method.getOwner().getSuperclass();
            int offset = ((Integer)outDFA.stack[outDFA.sp-1]).intValue();
            if (superName == null && offset == -1) {
              outDFA.init = true;
              AbstractType newType = nameSpace.forName(method.getOwner().getName());
              for (int j = 0; j < outDFA.sp; j++)
                if (outDFA.stack[j] instanceof Integer && ((Integer)outDFA.stack[j]).intValue() == offset)
                  outDFA.stack[j] = newType;
              for (int j = 0; j < outDFA.locals.length; j++)
                if (outDFA.locals[j] instanceof Integer && ((Integer)outDFA.locals[j]).intValue() == offset)
                  outDFA.locals[j] = newType;
            }
          }
          --outDFA.sp;
          if (outDFA.stack[outDFA.sp] != "N") {
            if (!(outDFA.stack[outDFA.sp] instanceof AbstractType) || !((AbstractType)outDFA.stack[outDFA.sp]).isArray())
              throw new VerifyException("Expecting to find array on stack");
          }
          break;
        case VOp.APOPBARRAY:
          if (outDFA.stack[outDFA.sp-1] instanceof Integer) {
            String superName = method.getOwner().getSuperclass();
            int offset = ((Integer)outDFA.stack[outDFA.sp-1]).intValue();
            if (superName == null && offset == -1) {
              outDFA.init = true;
              AbstractType newType = nameSpace.forName(method.getOwner().getName());
              for (int j = 0; j < outDFA.sp; j++)
                if (outDFA.stack[j] instanceof Integer && ((Integer)outDFA.stack[j]).intValue() == offset)
                  outDFA.stack[j] = newType;
              for (int j = 0; j < outDFA.locals.length; j++)
                if (outDFA.locals[j] instanceof Integer && ((Integer)outDFA.locals[j]).intValue() == offset)
                  outDFA.locals[j] = newType;
            }
          }
          --outDFA.sp;
          if (outDFA.stack[outDFA.sp] != "N") {
            if (!(outDFA.stack[outDFA.sp] instanceof AbstractType) || !((AbstractType)outDFA.stack[outDFA.sp]).isBooleanOrByteArray())
              throw new VerifyException("Expecting to find array of booleans/bytes on stack");
          }
          break;

        case VOp.AGETCOMP:
          if (outDFA.stack[outDFA.sp-1] instanceof Integer) {
            String superName = method.getOwner().getSuperclass();
            int offset = ((Integer)outDFA.stack[outDFA.sp-1]).intValue();
            if (superName == null && offset == -1) {
              outDFA.init = true;
              AbstractType newType = nameSpace.forName(method.getOwner().getName());
              for (int j = 0; j < outDFA.sp; j++)
                if (outDFA.stack[j] instanceof Integer && ((Integer)outDFA.stack[j]).intValue() == offset)
                  outDFA.stack[j] = newType;
              for (int j = 0; j < outDFA.locals.length; j++)
                if (outDFA.locals[j] instanceof Integer && ((Integer)outDFA.locals[j]).intValue() == offset)
                  outDFA.locals[j] = newType;
            }
          }
          if (outDFA.stack[outDFA.sp-1] != "N") {
            if (!(outDFA.stack[outDFA.sp-1] instanceof AbstractType) || !((AbstractType)outDFA.stack[outDFA.sp-1]).isReferenceArray())
              throw new VerifyException("Expecting to find array of objects/arrays on stack");
            outDFA.stack[outDFA.sp-1] = ((AbstractType)outDFA.stack[outDFA.sp-1]).getComponentType();
          }
          break;
        case VOp.ASETCOMP:
          if (outDFA.stack[outDFA.sp-1] instanceof Integer) {
            String superName = method.getOwner().getSuperclass();
            int offset = ((Integer)outDFA.stack[outDFA.sp-1]).intValue();
            if (superName == null && offset == -1) {
              outDFA.init = true;
              AbstractType newType = nameSpace.forName(method.getOwner().getName());
              for (int j = 0; j < outDFA.sp; j++)
                if (outDFA.stack[j] instanceof Integer && ((Integer)outDFA.stack[j]).intValue() == offset)
                  outDFA.stack[j] = newType;
              for (int j = 0; j < outDFA.locals.length; j++)
                if (outDFA.locals[j] instanceof Integer && ((Integer)outDFA.locals[j]).intValue() == offset)
                  outDFA.locals[j] = newType;
            }
          }
          if (outDFA.stack[outDFA.sp-2] instanceof Integer) {
            String superName = method.getOwner().getSuperclass();
            int offset = ((Integer)outDFA.stack[outDFA.sp-2]).intValue();
            if (superName == null && offset == -1) {
              outDFA.init = true;
              AbstractType newType = nameSpace.forName(method.getOwner().getName());
              for (int j = 0; j < outDFA.sp; j++)
                if (outDFA.stack[j] instanceof Integer && ((Integer)outDFA.stack[j]).intValue() == offset)
                  outDFA.stack[j] = newType;
              for (int j = 0; j < outDFA.locals.length; j++)
                if (outDFA.locals[j] instanceof Integer && ((Integer)outDFA.locals[j]).intValue() == offset)
                  outDFA.locals[j] = newType;
            }
          }
          outDFA.sp -= 2;
          if (outDFA.stack[outDFA.sp+1] == "N") {
            if (outDFA.stack[outDFA.sp] != "N")
              if (!(outDFA.stack[outDFA.sp] instanceof AbstractType) || !((AbstractType)outDFA.stack[outDFA.sp]).isReferenceArray())
                throw new VerifyException("Expecting to find array of objects/arrays on stack");
          } else {
            if (!(outDFA.stack[outDFA.sp+1] instanceof AbstractType))
              throw new VerifyException("Expecting to find object/array on stack");
            if (outDFA.stack[outDFA.sp] != "N") {
              if (!(outDFA.stack[outDFA.sp] instanceof AbstractType) || !((AbstractType)outDFA.stack[outDFA.sp]).isReferenceArray())
                throw new VerifyException("Expecting to find array of objects/arrays on stack");
              nameSpace.ensureSubtyping(((AbstractType)outDFA.stack[outDFA.sp]).getComponentType(), (AbstractType)outDFA.stack[outDFA.sp+1]);
            }
          }
          break;

        case VOp.AULOAD:
          if (outDFA.locals[vop.index] != "N" && !(outDFA.locals[vop.index] instanceof AbstractType) && !(outDFA.locals[vop.index] instanceof Integer))
            throw new VerifyException("Register "+vop.index+" contains wrong type");
          outDFA.stack[outDFA.sp++] = outDFA.locals[vop.index];
          break;
        case VOp.AURSTORE:
          Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-1]));
          outDFA.locals[vop.index] = outDFA.stack[--outDFA.sp];
          if (outDFA.stack[outDFA.sp] != "N" && !(outDFA.stack[outDFA.sp] instanceof AbstractType) && !(outDFA.stack[outDFA.sp] instanceof Integer) && !(outDFA.stack[outDFA.sp] instanceof ReturnAddress[]))
            throw new VerifyException("Expecting to find array of objects/arrays on stack");
          break;

        case VOp.UPUSH:
          outDFA.stack[outDFA.sp++] = new Integer(vop.index);
          break;
        case VOp.UPOPINIT:
          if (!(outDFA.stack[--outDFA.sp] instanceof Integer))
            throw new VerifyException("Expecting to find unitialized object on stack");
          int offset = ((Integer)outDFA.stack[outDFA.sp]).intValue();
          String className;
          if (offset == -1) {
            className = method.getOwner().getName();
            if (!className.equals(vop.type)) {
              String superName = method.getOwner().getSuperclass();
              if (superName == null || !superName.equals(vop.type))
                throw new VerifyException("Call to wrong initialization method");
            }
            outDFA.init = true;
            initLast = true;
          } else {
            int index = Bytecode.ushortAt(text, offset+1);
            className = constantPool.getClass(index);
            if (!className.equals(vop.type))
              throw new VerifyException("Call to wrong initialization method");
          }
          // undo if may follow an exception path!!!
          AbstractType newType = nameSpace.forName(className);
          for (int j = 0; j < outDFA.sp; j++)
            if (outDFA.stack[j] instanceof Integer && ((Integer)outDFA.stack[j]).intValue() == offset)
              outDFA.stack[j] = newType;
          for (int j = 0; j < outDFA.locals.length; j++)
            if (outDFA.locals[j] instanceof Integer && ((Integer)outDFA.locals[j]).intValue() == offset)
              outDFA.locals[j] = newType;
          break;

        case VOp.RPUSH:
          outDFA.stack[outDFA.sp++] = new ReturnAddress[]{ new ReturnAddress(vop.index, method.getCode().getMaxLocals()) };
          jsrDFA = outDFA;
          break;
        case VOp.RENSURE:
          if (!(outDFA.locals[vop.index] instanceof ReturnAddress[]))
            throw new VerifyException("Register "+vop.index+" contains wrong type");
          addresses = (ReturnAddress[])outDFA.locals[vop.index];
// why invalidate? there is no need...
//          outDFA.locals[vop.index] = null;
          break;

        case VOp.POP1:
          Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-1]));
          if (outDFA.stack[outDFA.sp-1] == "L" || outDFA.stack[outDFA.sp-1] == "D")
            throw new VerifyException("Attempt to split long or double on the stack");
          outDFA.sp--;
          break;
        case VOp.POP2:
          if (outDFA.stack[outDFA.sp-1] == "L" || outDFA.stack[outDFA.sp-1] == "D") {
            Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-1]));
            outDFA.sp--;
          } else {
            if (outDFA.stack[outDFA.sp-2] == "L" || outDFA.stack[outDFA.sp-2] == "D")
              throw new VerifyException("Attempt to split long or double on the stack");
            Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-2], outDFA.stack[outDFA.sp-1]));
            outDFA.sp -= 2;
          }
          break;
        case VOp.DUP1:
          if (outDFA.stack[outDFA.sp-1] == "L" || outDFA.stack[outDFA.sp-1] == "D")
            throw new VerifyException("Attempt to split long or double on the stack");
          Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-1]));
          outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-1];
          outDFA.sp++;
          break;
        case VOp.DUP1X1:
          if (outDFA.stack[outDFA.sp-1] == "L" || outDFA.stack[outDFA.sp-1] == "D")
            throw new VerifyException("Attempt to split long or double on the stack");
          if (outDFA.stack[outDFA.sp-2] == "L" || outDFA.stack[outDFA.sp-2] == "D")
            throw new VerifyException("Attempt to split long or double on the stack");
          Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-1]));
          Bytecode.setOp2type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-2]));
          outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-1];
          outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp-2];
          outDFA.stack[outDFA.sp-2] = outDFA.stack[outDFA.sp];
          outDFA.sp++;
          break;
        case VOp.DUP1X2:
          if (outDFA.stack[outDFA.sp-1] == "L" || outDFA.stack[outDFA.sp-1] == "D")
            throw new VerifyException("Attempt to split long or double on the stack");
          Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-1]));
          if (outDFA.stack[outDFA.sp-2] == "L" || outDFA.stack[outDFA.sp-2] == "D") {
            Bytecode.setOp2type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-2]));
            outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-1];
            outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp-2];
            outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp];
            outDFA.sp++;
          } else {
            if (outDFA.stack[outDFA.sp-3] == "L" || outDFA.stack[outDFA.sp-3] == "D")
              throw new VerifyException("Attempt to split long or double on the stack");
            Bytecode.setOp2type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-3], outDFA.stack[outDFA.sp-2]));
            outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-1];
            outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp-2];
            outDFA.stack[outDFA.sp-2] = outDFA.stack[outDFA.sp-3];
            outDFA.stack[outDFA.sp-3] = outDFA.stack[outDFA.sp];
            outDFA.sp++;
          }
          break;
        case VOp.DUP2:
          if (outDFA.stack[outDFA.sp-1] == "L" || outDFA.stack[outDFA.sp-1] == "D") {
            Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-1]));
            outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-1];
            outDFA.sp++;
          } else {
            if (outDFA.stack[outDFA.sp-2] == "L" || outDFA.stack[outDFA.sp-2] == "D")
              throw new VerifyException("Attempt to split long or double on the stack");
            Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-2], outDFA.stack[outDFA.sp-1]));
            outDFA.stack[outDFA.sp+1] = outDFA.stack[outDFA.sp-1];
            outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-2];
            outDFA.sp += 2;
          }
          break;
        case VOp.DUP2X1:
          if (outDFA.stack[outDFA.sp-1] == "L" || outDFA.stack[outDFA.sp-1] == "D") {
            Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-1]));
            if (outDFA.stack[outDFA.sp-2] == "L" || outDFA.stack[outDFA.sp-2] == "D")
              throw new VerifyException("Attempt to split long or double on the stack");
            Bytecode.setOp2type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-2]));
            outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-1];
            outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp-2];
            outDFA.stack[outDFA.sp-2] = outDFA.stack[outDFA.sp];
            outDFA.sp++;
          } else {
            if (outDFA.stack[outDFA.sp-2] == "L" || outDFA.stack[outDFA.sp-2] == "D")
              throw new VerifyException("Attempt to split long or double on the stack");
            Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-2], outDFA.stack[outDFA.sp-1]));
            if (outDFA.stack[outDFA.sp-3] == "L" || outDFA.stack[outDFA.sp-3] == "D")
              throw new VerifyException("Attempt to split long or double on the stack");
            Bytecode.setOp2type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-3]));
            outDFA.stack[outDFA.sp+1] = outDFA.stack[outDFA.sp-1];
            outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-2];
            outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp-3];
            outDFA.stack[outDFA.sp-2] = outDFA.stack[outDFA.sp+1];
            outDFA.stack[outDFA.sp-3] = outDFA.stack[outDFA.sp];
            outDFA.sp += 2;
          }
          break;
        case VOp.DUP2X2:
          if (outDFA.stack[outDFA.sp-1] == "L" || outDFA.stack[outDFA.sp-1] == "D") {
            Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-1]));
            if (outDFA.stack[outDFA.sp-2] == "L" || outDFA.stack[outDFA.sp-2] == "D") {
              Bytecode.setOp2type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-2]));
              outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-1];
              outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp-2];
              outDFA.stack[outDFA.sp-2] = outDFA.stack[outDFA.sp];
              outDFA.sp++;
            } else {
              if (outDFA.stack[outDFA.sp-3] == "L" || outDFA.stack[outDFA.sp-3] == "D")
                throw new VerifyException("Attempt to split long or double on the stack");
              Bytecode.setOp2type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-3], outDFA.stack[outDFA.sp-2]));
              outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-1];
              outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp-2];
              outDFA.stack[outDFA.sp-2] = outDFA.stack[outDFA.sp-3];
              outDFA.stack[outDFA.sp-3] = outDFA.stack[outDFA.sp];
              outDFA.sp++;
            }
          } else {
            if (outDFA.stack[outDFA.sp-2] == "L" || outDFA.stack[outDFA.sp-2] == "D")
              throw new VerifyException("Attempt to split long or double on the stack");
            Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-2], outDFA.stack[outDFA.sp-1]));
            if (outDFA.stack[outDFA.sp-3] == "L" || outDFA.stack[outDFA.sp-3] == "D") {
              Bytecode.setOp2type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-3]));
              outDFA.stack[outDFA.sp+1] = outDFA.stack[outDFA.sp-1];
              outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-2];
              outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp-3];
              outDFA.stack[outDFA.sp-2] = outDFA.stack[outDFA.sp+1];
              outDFA.stack[outDFA.sp-3] = outDFA.stack[outDFA.sp];
              outDFA.sp += 2;
            } else {
              if (outDFA.stack[outDFA.sp-4] == "L" || outDFA.stack[outDFA.sp-4] == "D")
                throw new VerifyException("Attempt to split long or double on the stack");
              Bytecode.setOp2type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-4], outDFA.stack[outDFA.sp-3]));
              outDFA.stack[outDFA.sp+1] = outDFA.stack[outDFA.sp-1];
              outDFA.stack[outDFA.sp] = outDFA.stack[outDFA.sp-2];
              outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp-3];
              outDFA.stack[outDFA.sp-2] = outDFA.stack[outDFA.sp-4];
              outDFA.stack[outDFA.sp-3] = outDFA.stack[outDFA.sp+1];
              outDFA.stack[outDFA.sp-4] = outDFA.stack[outDFA.sp];
              outDFA.sp += 2;
            }
          }
          break;
        case VOp.SWAP1X1:
          if (outDFA.stack[outDFA.sp-1] == "L" || outDFA.stack[outDFA.sp-1] == "D")
            throw new VerifyException("Attempt to split long or double on the stack");
          if (outDFA.stack[outDFA.sp-2] == "L" || outDFA.stack[outDFA.sp-2] == "D")
            throw new VerifyException("Attempt to split long or double on the stack");
          if (vop.offset != -1) {
            Bytecode.setOp1type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-1]));
            Bytecode.setOp2type(textInfo, vop.offset, typeOf(outDFA.stack[outDFA.sp-2]));
          }
          Object tmp = outDFA.stack[outDFA.sp-1];
          outDFA.stack[outDFA.sp-1] = outDFA.stack[outDFA.sp-2];
          outDFA.stack[outDFA.sp-2] = tmp;
          break;

        case VOp.CHECKINIT:
          if (!outDFA.init && method.getOwner().getSuperclass() != null)
            throw new VerifyException("Constructor must call super() or this()");
          break;
        case VOp.CHECKFALL:
          throw new VerifyException("Falling off the end of the code");

        }

      }

      if (addresses != null) {

        for (int i = 0; i < addresses.length; i++) {
          ReturnAddress address = addresses[i];
          VBlock targetBB = allBBs[address.address];

          Frame callDFA = ((VBlock)targetBB.upBB()).jsrDFA;

          for (int j = 0; j < outDFA.locals.length; j++) {
            if (!address.written.get(j)) {
              outDFA.locals[j] = callDFA.locals[j];
              if (outDFA.locals[j] instanceof ReturnAddress[]) {
                ReturnAddress[] array = (ReturnAddress[])outDFA.locals[j];
                array = (ReturnAddress[])array.clone();
                for (int k = 0; k < array.length; k++) {
                  array[k] = new ReturnAddress(array[k]);
                  array[k].written.or(address.written);
                }
                outDFA.locals[j] = array;
              }
            }
          }

          if (targetBB.inDFA == null) {
            targetBB.inDFA = new Frame(outDFA);
            set.add(targetBB);
          } else
            if (targetBB.inDFA.mergeWith(nameSpace, outDFA))
              set.add(targetBB);
        }

      } else {

        for (Iterator i = outEdges(); i.hasNext(); ) {
          VEdge edge = (VEdge)i.next();
          if (edge.type == VEdge.NORMAL) {
            VBlock targetBB = (VBlock)edge.targetBB();
            if (targetBB.inDFA == null) {
              targetBB.inDFA = new Frame(outDFA);
              set.add(targetBB);
            } else
              if (targetBB.inDFA.mergeWith(nameSpace, outDFA))
                set.add(targetBB);
          }
        }

        if (initLast)
          outDFA.init = false;
        outDFA.ss = 1;
        outDFA.sp = 1;

        for (Iterator i = outEdges(); i.hasNext(); ) {
          VEdge edge = (VEdge)i.next();
          if (edge.type == VEdge.EXCEPTION) {
            outDFA.stack[0] = nameSpace.forName(edge.catchType);
            VBlock targetBB = (VBlock)edge.targetBB();
            if (targetBB.inDFA == null) {
              targetBB.inDFA = new Frame(outDFA);
              set.add(targetBB);
            } else
              if (targetBB.inDFA.mergeWith(nameSpace, outDFA))
                set.add(targetBB);
          }
        }

      }
    }

    public void ipush() {
      appendStmt(new VOp(VOp.IPUSH));
      incsp(0, 1);
    }

    public void ipop() {
      appendStmt(new VOp(VOp.IPOP));
      decsp(1, 0);
    }

    public void iensure(int index) {
      appendStmt(new VOp(VOp.IENSURE, index));
    }

    public void iset(int index) {
      appendStmt(new VOp(VOp.ISET, index));
      written.set(index);
    }

    public void lpush() {
      appendStmt(new VOp(VOp.LPUSH));
      incsp(0, 2);
    }

    public void lpop() {
      appendStmt(new VOp(VOp.LPOP));
      decsp(2, 0);
    }

    public void lensure(int index) {
      appendStmt(new VOp(VOp.LENSURE, index));
    }

    public void lensure2(int index) {
      appendStmt(new VOp(VOp.LENSURE2, index));
    }

    public void lset(int index) {
      appendStmt(new VOp(VOp.LSET, index));
      written.set(index);
    }

    public void lset2(int index) {
      appendStmt(new VOp(VOp.LSET2, index));
      written.set(index);
    }

    public void fpush() {
      appendStmt(new VOp(VOp.FPUSH));
      incsp(0, 1);
    }

    public void fpop() {
      appendStmt(new VOp(VOp.FPOP));
      decsp(1, 0);
    }

    public void fensure(int index) {
      appendStmt(new VOp(VOp.FENSURE, index));
    }

    public void fset(int index) {
      appendStmt(new VOp(VOp.FSET, index));
      written.set(index);
    }

    public void dpush() {
      appendStmt(new VOp(VOp.DPUSH));
      incsp(0, 2);
    }

    public void dpop() {
      appendStmt(new VOp(VOp.DPOP));
      decsp(2, 0);
    }

    public void densure(int index) {
      appendStmt(new VOp(VOp.DENSURE, index));
    }

    public void densure2(int index) {
      appendStmt(new VOp(VOp.DENSURE2, index));
    }

    public void dset(int index) {
      appendStmt(new VOp(VOp.DSET, index));
      written.set(index);
    }

    public void dset2(int index) {
      appendStmt(new VOp(VOp.DSET2, index));
      written.set(index);
    }

    public void apush(String type) {
      appendStmt(new VOp(VOp.APUSH, type));
      incsp(0, 1);
    }

    public void apop() {
      appendStmt(new VOp(VOp.APOP));
      decsp(1, 0);
    }

    public void apopsubtype(String type) {
      appendStmt(new VOp(VOp.APOPSUBTYPE, type));
      decsp(1, 0);
    }

    public void aupopsubtype(String type) {
      appendStmt(new VOp(VOp.AUPOPSUBTYPE, type));
      decsp(1, 0);
    }

    public void apoparray() {
      appendStmt(new VOp(VOp.APOPARRAY));
      decsp(1, 0);
    }

    public void apopbarray() {
      appendStmt(new VOp(VOp.APOPBARRAY));
      decsp(1, 0);
    }

    public void agetcomp() {
      appendStmt(new VOp(VOp.AGETCOMP));
      incsp(1, 0);
    }

    public void asetcomp() {
      appendStmt(new VOp(VOp.ASETCOMP));
      decsp(2, 0);
    }

    public void auload(int index) {
      appendStmt(new VOp(VOp.AULOAD, index));
      incsp(0, 1);
    }

    public void aurstore(int offset, int index) {
      appendStmt(new VOp(offset, VOp.AURSTORE, index));
      decsp(1, 0);
      written.set(index);
    }

    public void upush(int index) {
      appendStmt(new VOp(VOp.UPUSH, index));
      incsp(0, 1);
    }

    public void upopinit(String type) {
      appendStmt(new VOp(VOp.UPOPINIT, type));
      decsp(1, 0);
    }

    public void rpush(int index) {
      appendStmt(new VOp(VOp.RPUSH, index));
      incsp(0, 1);
    }

    public void rensure(int index) {
      appendStmt(new VOp(VOp.RENSURE, index));
      written.set(index);
    }

    public void pop1(int offset) {
      appendStmt(new VOp(offset, VOp.POP1));
      decsp(1, 0);
    }

    public void pop2(int offset) {
      appendStmt(new VOp(offset, VOp.POP2));
      decsp(2, 0);
    }

    public void dup1(int offset) {
      appendStmt(new VOp(offset, VOp.DUP1));
      incsp(1, 1);
    }

    public void dup1x1(int offset) {
      appendStmt(new VOp(offset, VOp.DUP1X1));
      incsp(2, 1);
    }

    public void dup1x2(int offset) {
      appendStmt(new VOp(offset, VOp.DUP1X2));
      incsp(3, 1);
    }

    public void dup2(int offset) {
      appendStmt(new VOp(offset, VOp.DUP2));
      incsp(2, 2);
    }

    public void dup2x1(int offset) {
      appendStmt(new VOp(offset, VOp.DUP2X1));
      incsp(3, 2);
    }

    public void dup2x2(int offset) {
      appendStmt(new VOp(offset, VOp.DUP2X2));
      incsp(4, 2);
    }

    public void swap1x1(int offset) {
      appendStmt(new VOp(offset, VOp.SWAP1X1));
      incsp(2, 0);
    }

    public void checkinit() {
      appendStmt(new VOp(VOp.CHECKINIT));
    }

    public void checkfall() {
      appendStmt(new VOp(VOp.CHECKFALL));
    }

  }

  private static final class VEdge extends ControlEdge {

    public static final byte NORMAL = 0;
    public static final byte EXCEPTION = 1;

    private final byte type;
    private final String catchType;

    public VEdge(VBlock source, byte type, VBlock target) {
      super(source, target);
      this.type = type;
      this.catchType = "";
    }

    public VEdge(VBlock source, byte type, String catchType, VBlock target) {
      super(source, target);
      this.type = type;
      this.catchType = catchType;
    }

    public int hashCode() {
      return type ^ super.hashCode() ^ catchType.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof VEdge
          && ((VEdge)object).type == type
          && ((VEdge)object).catchType.equals(catchType)
          && super.equals(object);
    }

    public String toString() {
      switch (type) {
      case VEdge.NORMAL:
        return sourceNode().hashCode()+" -> "+targetNode().hashCode()+";";
      case VEdge.EXCEPTION:
        return sourceNode().hashCode()+" -> "+targetNode().hashCode()+" [label=\""+catchType+"\"];";
      default:
        throw new Error();
      }
    }

  }

  private static final class VOp extends Statement {
    
    public static final byte IPUSH = 1;
    public static final byte IPOP = 2;
    public static final byte IENSURE = 3;
    public static final byte ISET = 4;

    public static final byte LPUSH = 5;
    public static final byte LPOP = 6;
    public static final byte LENSURE = 7;
    public static final byte LENSURE2 = 8;
    public static final byte LSET = 9;
    public static final byte LSET2 = 10;

    public static final byte FPUSH = 11;
    public static final byte FPOP = 12;
    public static final byte FENSURE = 13;
    public static final byte FSET = 14;

    public static final byte DPUSH = 15;
    public static final byte DPOP = 16;
    public static final byte DENSURE = 17;
    public static final byte DENSURE2 = 18;
    public static final byte DSET = 19;
    public static final byte DSET2 = 20;

    public static final byte APUSH = 21;
    public static final byte APOP = 22;
    public static final byte APOPSUBTYPE = 23;
    public static final byte AUPOPSUBTYPE = 24;
    public static final byte APOPARRAY = 25;
    public static final byte APOPBARRAY = 26;
    public static final byte AGETCOMP = 27;
    public static final byte ASETCOMP = 28;

    public static final byte AULOAD = 29;
    public static final byte AURSTORE = 30;

    public static final byte UPUSH = 31;
    public static final byte UPOPINIT = 32;

    public static final byte RPUSH = 33;
    public static final byte RENSURE = 34;

    public static final byte POP1 = 35;
    public static final byte POP2 = 36;
    public static final byte DUP1 = 37;
    public static final byte DUP1X1 = 38;
    public static final byte DUP1X2 = 39;
    public static final byte DUP2 = 40;
    public static final byte DUP2X1 = 41;
    public static final byte DUP2X2 = 42;
    public static final byte SWAP1X1 = 43;

    public static final byte CHECKINIT = 44;
    public static final byte CHECKFALL = 45;

    private final byte code;
    private int index;
    private int offset;
    private String type;

    public VOp(byte code) {
      this.code = code;
    }

    public VOp(int offset, byte code) {
      this.offset = offset;
      this.code = code;
    }

    public VOp(int offset, byte code, int index) {
      this.offset = offset;
      this.code = code;
      this.index = index;
    }

    public VOp(byte code, int index) {
      this.code = code;
      this.index = index;
    }

    public VOp(byte code, String type) {
      this.code = code;
      this.type = type;
    }

    public String toString() {
      switch (code) {
      
      case VOp.IPUSH: return "IPUSH";
      case VOp.IPOP: return "IPOP";
      case VOp.IENSURE: return "IENSURE "+index;
      case VOp.ISET: return "ISET "+index;

      case VOp.LPUSH: return "LPUSH";
      case VOp.LPOP: return "LPOP";
      case VOp.LENSURE: return "LENSURE "+index;
      case VOp.LENSURE2: return "LENSURE2 "+index;
      case VOp.LSET: return "LSET "+index;
      case VOp.LSET2: return "LSET2 "+index;

      case VOp.FPUSH: return "FPUSH";
      case VOp.FPOP: return "FPOP";
      case VOp.FENSURE: return "FENSURE "+index;
      case VOp.FSET: return "FSET "+index;

      case VOp.DPUSH: return "DPUSH";
      case VOp.DPOP: return "DPOP";
      case VOp.DENSURE: return "DENSURE "+index;
      case VOp.DENSURE2: return "DENSURE2 "+index;
      case VOp.DSET: return "DSET "+index;
      case VOp.DSET2: return "DSET2 "+index;

      case VOp.APUSH: return "APUSH "+type;
      case VOp.APOP: return "APOP";
      case VOp.APOPSUBTYPE: return "APOPSUBTYPE "+type;
      case VOp.AUPOPSUBTYPE: return "AUPOPSUBTYPE "+type;
      case VOp.APOPARRAY: return "APOPARRAY";
      case VOp.APOPBARRAY: return "APOPBARRAY";
      case VOp.AGETCOMP: return "AGETCOMP";
      case VOp.ASETCOMP: return "ASETCOMP";

      case VOp.AULOAD: return "AULOAD "+index;
      case VOp.AURSTORE: return "AURSTORE "+index;

      case VOp.UPUSH: return "UPUSH #"+index;
      case VOp.UPOPINIT: return "UPOPINIT "+type;

      case VOp.RPUSH: return "RPUSH @"+index;
      case VOp.RENSURE: return "RENSURE "+index;

      case VOp.POP1: return "POP1";
      case VOp.POP2: return "POP2";
      case VOp.DUP1: return "DUP1";
      case VOp.DUP1X1: return "DUP1X1";
      case VOp.DUP1X2: return "DUP1X2";
      case VOp.DUP2: return "DUP2";
      case VOp.DUP2X1: return "DUP2X1";
      case VOp.DUP2X2: return "DUP2X2";
      case VOp.SWAP1X1: return "SWAP1X1";

      case VOp.CHECKINIT: return "CHECKINIT";
      case VOp.CHECKFALL: return "CHECKFALL";

      default: throw new Error();
      }
    }

  }

  private static final class Frame {

    private static Object mergeWith(Namespace nameSpace, Object a, Object b) {
      if (a == "I" && b == "I")
        return "I";
      else if (a == "F" && b == "F")
        return "F";
      else if (a == "L" && b == "L")
        return "L";
      else if (a == "L2" && b == "L2")
        return "L2";
      else if (a == "D" && b == "D")
        return "D";
      else if (a == "D2" && b == "D2")
        return "D2";
      else if (a == "N") {
        if (b == "N")
          return "N";
        else if (b instanceof AbstractType)
          return b;
      } else if (a instanceof AbstractType) {
        if (b == "N")
          return a;
        else if (b instanceof AbstractType) {
          AbstractType type_a = (AbstractType)a;
          AbstractType type_b = (AbstractType)b;
          AbstractType type_c = nameSpace.findCommonSuperclass(type_a, type_b);
          if (type_a.equals(type_c))
            return type_a;
          return type_c;
        }
      } else if (a instanceof Integer && b instanceof Integer) {
        Integer type_a = (Integer)a;
        Integer type_b = (Integer)b;
        if (type_a.intValue() == type_b.intValue())
          return type_a;
      } else if (a instanceof ReturnAddress[] && b instanceof ReturnAddress[]) {
        ReturnAddress[] type_a = (ReturnAddress[])a;
        ReturnAddress[] type_b = (ReturnAddress[])b;
        int count = type_a.length;
        boolean change = false;
        for (int i = 0; i < type_b.length; i++) {
          int index = Arrays.binarySearch(type_a, type_b[i]);
          if (index < 0)
            count++;
          else if (!change) {
            BitSet set = (BitSet)type_b[i].written.clone();
            set.or(type_a[index].written);
            change = !type_a[index].written.equals(set);
          }
        }
        if (count == type_a.length && !change)
          return type_a;
        ReturnAddress[] type_c = new ReturnAddress[count];
        count = type_a.length;
        System.arraycopy(type_a, 0, type_c, 0, count);
        for (int i = 0; i < type_b.length; i++) {
          int index = Arrays.binarySearch(type_a, type_b[i]);
          if (index < 0)
            type_c[count++] = type_b[i];
          else if (!type_a[index].written.equals(type_b[i].written)) {
            type_c[index] = new ReturnAddress(type_a[index]);
            type_c[index].written.or(type_b[i].written);
          }
        }
        Arrays.sort(type_c);
        return type_c;
      }
      return null;
    }

    private static String toString(Object object) {
      if (object == null)
        return "X";
      else if (object instanceof Integer)
        return "U#"+object;
      else if (object instanceof ReturnAddress[]) {
        ReturnAddress[] array = (ReturnAddress[])object;
        StringBuffer sb = new StringBuffer();
        sb.append('R');
        sb.append('{');
        for (int i = 0; i < array.length; i++) {
          sb.append('@');
          sb.append(array[i].address);
          sb.append(array[i].written);
          sb.append(',');
        }
        sb.setLength(sb.length()-1);
        sb.append('}');
        return sb.toString();
      }
      return object.toString();
    }

    private int sp;
    private int ss;
    private boolean init;
    private final Object[] stack;
    private final Object[] locals;

    public Frame(Namespace nameSpace, MethodInfo methodInfo, int maxStack, int maxLocals) {
      sp = 0;
      ss = 0;
      init = true;
      stack = new Object[maxStack];
      locals = new Object[maxLocals];
      int index = 0;
      if (!methodInfo.isStatic() && !methodInfo.getName().equals("<clinit>")) {
        if (methodInfo.getName().equals("<init>")) {
          init = false;
          locals[index++] = new Integer(-1);
        } else
          locals[index++] = nameSpace.forName(methodInfo.getOwner().getName());
      }
      String[] params = Syntax.getParameterTypes(methodInfo.getDescriptor());
      for (int i = 0; i < params.length; i++)
        switch (params[i].charAt(0)) {
        case 'Z': case 'C': case 'B': case 'S': case 'I':
          locals[index++] = "I";
          break;
        case 'J':
          locals[index++] = "L";
          locals[index++] = "L2";
          break;
        case 'F':
          locals[index++] = "F";
          break;
        case 'D':
          locals[index++] = "D";
          locals[index++] = "D2";
          break;
        case 'L':
          locals[index++] = nameSpace.forName(params[i].substring(1, params[i].length()-1));
          break;
        case '[':
          locals[index++] = nameSpace.forName(params[i]);
          break;
        }
    }

    public Frame(Frame frame) {
      init = frame.init;
      sp = frame.sp;
      ss = frame.ss;
      stack = (Object[])frame.stack.clone();
      for (int i = 0; i < sp; i++)
        if (stack[i] instanceof ReturnAddress[]) {
          ReturnAddress[] array = (ReturnAddress[])stack[i];
          array = (ReturnAddress[])array.clone();
          for (int j = 0; j < array.length; j++)
            array[j] = new ReturnAddress(array[j]);
          stack[i] = array;
        }
      locals = (Object[])frame.locals.clone();
      for (int i = 0; i < locals.length; i++)
        if (locals[i] instanceof ReturnAddress[]) {
          ReturnAddress[] array = (ReturnAddress[])locals[i];
          array = (ReturnAddress[])array.clone();
          for (int j = 0; j < array.length; j++)
            array[j] = new ReturnAddress(array[j]);
          locals[i] = array;
        }
    }

    public boolean mergeWith(Namespace nameSpace, Frame frame) throws VerifyException {
      if (ss != frame.ss || sp != frame.sp)
        throw new VerifyException("Inconsistent stack height "+ss+" != "+frame.ss);
      boolean changed = false;
      if (init && !frame.init) {
        init = false;
        changed = true;
      }
      for (int i = 0; i < sp; i++) {
        Object merged = mergeWith(nameSpace, stack[i], frame.stack[i]);
        if (merged == null)
          throw new VerifyException("Inconsistent stack types");
        if (merged != stack[i]) {
          stack[i] = merged;
          changed = true;
        }
      }
      for (int i = 0; i < locals.length; i++) {
        Object merged = mergeWith(nameSpace, locals[i], frame.locals[i]);
        if (merged != locals[i]) {
          locals[i] = merged;
          changed = true;
        }
      }
      return changed;
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(init);
      sb.append(' ');
      sb.append('[');
      for (int i = 0; i < locals.length; i++)
        sb.append(toString(locals[i]));
      sb.append(']');
      sb.append(' ');
      sb.append('(');
      sb.append(ss);
      sb.append(')');
      sb.append(' ');
      sb.append('>');
      for (int i = 0; i < sp; i++)
        sb.append(toString(stack[i]));
      sb.append('>');
      return sb.toString();
    }

  }

  private static final class ReturnAddress implements Comparable {

    private final int address;
    private final BitSet written;

    public ReturnAddress(int address, int maxLocals) {
      this.address = address;
      written = new BitSet(maxLocals);
    }

    public ReturnAddress(ReturnAddress other) {
      this.address = other.address;
      written = (BitSet)other.written.clone();
    }

    public int compareTo(Object object) {
      return compareTo((ReturnAddress)object);
    }

    public int compareTo(ReturnAddress other) {
      return address < other.address ? -1 : address > other.address ? 1 : 0;
    }

  }

  private BytecodeVerifier() { }

}

