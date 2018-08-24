/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.cfdump;

import jewel.core.clfile.Bytecode;
import jewel.core.clfile.ClassFileConstants;
import jewel.core.clfile.ClassInfo;
import jewel.core.clfile.ClassInfo.ConstantPool;
import jewel.core.clfile.ClassInfo.FieldInfo;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo.HandlerInfo;
import jewel.core.clfile.VerifyException;

import java.io.PrintStream;
import java.util.BitSet;

public class Dumper implements ClassFileConstants {

  private final PrintStream out;

  protected Dumper(PrintStream out) {
    this.out = out;
  }

  public void dumpClass(ClassInfo clazz) throws VerifyException {
    out.println("; JEWEL, Java Environment With Enhanced Linkage.");
    out.println("; Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira.");
    out.println();
    out.print(clazz.isInterface() ? ".interface " : ".class ");
    if (clazz.isPublic())
      out.print("public ");
    if (clazz.isFinal())
      out.print("final ");
    if (clazz.isAbstract())
      out.print("abstract ");
    out.println(clazz.getName());
    String superName = clazz.getSuperclass();
    if (superName != null) {
      out.print(".super ");
      out.println(superName);
    }
    String[] interfaceName = clazz.getInterfaces();
    for (int i = 0; i < interfaceName.length; i++) {
      out.print(".implements ");
      out.println(interfaceName[i]);
    }
    out.println();
    FieldInfo[] fields = clazz.getFields();
    if (fields.length > 0) {
      for (int i = 0; i < fields.length; i++)
        dumpField(fields[i]);
      out.println();
    }
    MethodInfo[] methods = clazz.getMethods();
    if (methods.length > 0)
      for (int i = 0; i < methods.length; i++) {
        dumpMethod(methods[i]);
        out.println();
      }
  }

  private void dumpField(FieldInfo field) {
    out.print(".field ");
    if (field.isPublic())
      out.print("public ");
    if (field.isPrivate())
      out.print("private ");
    if (field.isProtected())
      out.print("protected ");
    if (field.isStatic())
      out.print("static ");
    if (field.isFinal())
      out.print("final ");
    if (field.isVolatile())
      out.print("volatile ");
    if (field.isTransient())
      out.print("transient ");
    out.print(field.getName());
    out.print(" ");
    out.print(field.getDescriptor());
    Object constantValue = field.getConstantValue();
    if (constantValue != null) {
      out.print(" = ");
      if (constantValue instanceof String)
        constantValue = extern((String)constantValue);
      out.print(constantValue);
    }
    out.println();
  }

  private static String extern(String s) {
    StringBuffer buffer = new StringBuffer();
    buffer.append('"');
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
      case '\n': buffer.append("\\n"); break;
      case '\t': buffer.append("\\t"); break;
      case '\b': buffer.append("\\b"); break;
      case '\r': buffer.append("\\r"); break;
      case '\f': buffer.append("\\f"); break;
      case '\\': buffer.append("\\\\"); break;
      case '\"': buffer.append("\\\""); break;
      default:
        if (c >= ' ' && c <= '~')
          buffer.append(c);
        else {
          buffer.append("\\u");
          for (int j = 3; j >= 0; j--) {
            int v = (c >> (4*j)) & 0xF;
            v += v <= 9 ? '0' : 'A'-10;
            buffer.append((char)v);
          }
        }
      }
    }
    buffer.append('"');
    return buffer.toString();
  }

  private void dumpMethod(MethodInfo method) throws VerifyException {
    out.print(".method ");
    if (method.isPublic())
      out.print("public ");
    if (method.isPrivate())
      out.print("private ");
    if (method.isProtected())
      out.print("protected ");
    if (method.isStatic())
      out.print("static ");
    if (method.isFinal())
      out.print("final ");
    if (method.isSynchronized())
      out.print("synchronized ");
    if (method.isNative())
      out.print("native ");
    if (method.isAbstract())
      out.print("abstract ");
    if (method.isStrict())
      out.print("strict ");
    out.print(method.getName());
    out.println(method.getDescriptor());
    CodeInfo code = method.getCode();
    if (code != null) {
      out.println();
      try {
        code.checkStaticConstraints();
      } catch (VerifyException e) {
        throw new VerifyException("(method: "+method.getName()+" signature: "+method.getDescriptor()+") "+e.getMessage());
      }
      dumpCode(code);
      out.println();
    }
    out.println(".end method");
  }

  private void dumpCode(CodeInfo code) throws VerifyException {
    int maxStack = code.getMaxStack();
    int maxLocals = code.getMaxLocals();
    byte[] text = code.getText();
    int[] textInfo = code.getTextInfo();
    HandlerInfo[] handlers = code.getHandlers();
    ConstantPool constantPool = code.getOwner().getOwner().getConstantPool();

    if (maxStack > 0)
      out.println("\t.limit stack "+maxStack);
    if (maxLocals > 0)
      out.println("\t.limit locals "+maxLocals);
    if (maxStack > 0 || maxLocals > 0)
      out.println();
    
    BitSet labels = new BitSet(text.length+1);

    for (int i = 0; i < handlers.length; i++) {
      HandlerInfo handler = handlers[i];
      labels.set(handler.getStartPC());
      labels.set(handler.getEndPC());
      labels.set(handler.getHandlerPC());
    }
    
    int offset = 0;
    while (offset < text.length) {

      if (Bytecode.isTarget(textInfo, offset))
        labels.set(offset);

      if (labels.get(offset))
        out.println("@"+offset+":");

      short opcode = Bytecode.opcodeAt(text, offset);
      int opsize = Bytecode.sizeOf(opcode);
      String opmnem = Bytecode.mnemonicOf(opcode);

      switch (opcode) {
      
      case BIPUSH:
        int value = Bytecode.byteAt(text, offset+1);
        out.println("\t"+opmnem+" "+value);
        break;

      case SIPUSH:
        value = Bytecode.shortAt(text, offset+1);
        out.println("\t"+opmnem+" "+value);
        break;

      case LDC:
        int index = Bytecode.ubyteAt(text, offset+1);
        byte tag = constantPool.tag(index);
        switch (tag) {
        case CONSTANT_INTEGER:
          out.println("\t"+opmnem+" "+constantPool.getInteger(index));
          break;
        case CONSTANT_FLOAT:
          out.println("\t"+opmnem+" "+constantPool.getFloat(index));
          break;
        case CONSTANT_STRING:
          out.println("\t"+opmnem+" "+extern(constantPool.getString(index)));
          break;
        }
        break;

      case LDC_W:
        index = Bytecode.ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        switch (tag) {
        case CONSTANT_INTEGER:
          out.println("\t"+opmnem+" "+constantPool.getInteger(index));
          break;
        case CONSTANT_FLOAT:
          out.println("\t"+opmnem+" "+constantPool.getFloat(index));
          break;
        case CONSTANT_STRING:
          out.println("\t"+opmnem+" "+extern(constantPool.getString(index)));
          break;
        }
        break;

      case LDC2_W:
        index = Bytecode.ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        switch (tag) {
        case CONSTANT_LONG:
          out.println("\t"+opmnem+" "+constantPool.getLong(index));
          break;
        case CONSTANT_DOUBLE:
          out.println("\t"+opmnem+" "+constantPool.getDouble(index));
          break;
        }
        break;

      case ILOAD: case LLOAD: case FLOAD: case DLOAD: case ALOAD:
      case ISTORE: case LSTORE: case FSTORE: case DSTORE: case ASTORE:
      case RET:
        index = Bytecode.ubyteAt(text, offset+1);
        out.println("\t"+opmnem+" "+index);
        break;

      case IINC:
        index = Bytecode.ubyteAt(text, offset+1);
        value = Bytecode.byteAt(text, offset+2);
        out.println("\t"+opmnem+" "+index+" "+value);
        break;

      case IFEQ: case IFNE: case IFLT: case IFGE: case IFGT: case IFLE:
      case IF_ICMPEQ: case IF_ICMPNE: case IF_ICMPLT: case IF_ICMPGE: case IF_ICMPGT: case IF_ICMPLE:
      case IF_ACMPEQ: case IF_ACMPNE:
      case GOTO: case JSR:
      case IFNULL: case IFNONNULL:
        int branch = Bytecode.shortAt(text, offset+1);
        int target = offset+branch;
        out.println("\t"+opmnem+" @"+target);
        break;

      case TABLESWITCH:
        int pad = ~offset & 3;
        int low = Bytecode.intAt(text, offset+1+pad+4);
        int high = Bytecode.intAt(text, offset+1+pad+8);
        int entries = high-low+1;
        opsize = 1+pad+12+4*entries;
        out.println("\t"+opmnem+" "+low);
        for (int i = 0; i < entries; i++) {
          branch = Bytecode.intAt(text, offset+1+pad+12+4*i);
          target = offset+branch;
          out.println("\t\t@"+target);
        }
        branch = Bytecode.intAt(text, offset+1+pad);
        target = offset+branch;
        out.println("\t\tdefault: @"+target);
        break;

      case LOOKUPSWITCH:
        pad = ~offset & 3;
        int pairs = Bytecode.intAt(text, offset+1+pad+4);
        opsize = 1+pad+8+8*pairs;
        out.println("\t"+opmnem);
        for (int i = 0; i < pairs; i++) {
          int key = Bytecode.intAt(text, offset+1+pad+8+8*i);
          branch = Bytecode.intAt(text, offset+1+pad+8+8*i+4);
          target = offset+branch;
          out.println("\t\t"+key+": @"+target);
        }
        branch = Bytecode.intAt(text, offset+1+pad);
        target = offset+branch;
        out.println("\t\tdefault: @"+target);
        break;

      case GETSTATIC: case PUTSTATIC: case GETFIELD: case PUTFIELD:
        index = Bytecode.ushortAt(text, offset+1);
        String clazz = constantPool.getClass(index);
        String name = constantPool.getName(index);
        String descriptor = constantPool.getDescriptor(index);
        out.println("\t"+opmnem+" "+clazz+"/"+name+" "+descriptor);
        break;

      case INVOKESTATIC: case INVOKEVIRTUAL: case INVOKESPECIAL:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        name = constantPool.getName(index);
        descriptor = constantPool.getDescriptor(index);
        out.println("\t"+opmnem+" "+clazz+"/"+name+descriptor);
        break;

      case INVOKEINTERFACE:
        index = Bytecode.ushortAt(text, offset+1);
        int args = Bytecode.ubyteAt(text, offset+3);
        clazz = constantPool.getClass(index);
        name = constantPool.getName(index);
        descriptor = constantPool.getDescriptor(index);
        out.println("\t"+opmnem+" "+clazz+"/"+name+descriptor+" "+args);
        break;

      case NEW: case ANEWARRAY:
      case CHECKCAST: case INSTANCEOF:
        index = Bytecode.ushortAt(text, offset+1);
        clazz = constantPool.getClass(index);
        out.println("\t"+opmnem+" "+clazz);
        break;

      case NEWARRAY:
        int atype = Bytecode.ubyteAt(text, offset+1);
        switch (atype) {
        case T_BOOLEAN:
          out.println("\t"+opmnem+" boolean");
          break;
        case T_CHAR:
          out.println("\t"+opmnem+" char");
          break;
        case T_FLOAT:
          out.println("\t"+opmnem+" float");
          break;
        case T_DOUBLE:
          out.println("\t"+opmnem+" double");
          break;
        case T_BYTE:
          out.println("\t"+opmnem+" byte");
          break;
        case T_SHORT:
          out.println("\t"+opmnem+" short");
          break;
        case T_INT:
          out.println("\t"+opmnem+" int");
          break;
        case T_LONG:
          out.println("\t"+opmnem+" long");
          break;
        }
        break;

      case WIDE:

        short wopcode = Bytecode.opcodeAt(text, offset+1);
        opsize = Bytecode.wsizeOf(wopcode);
        String wopmnem = Bytecode.wmnemonicOf(wopcode);

        switch (wopcode) {
        
        case ILOAD: case LLOAD: case FLOAD: case DLOAD: case ALOAD:
        case ISTORE: case LSTORE: case FSTORE: case DSTORE: case ASTORE:
        case RET:
          index = Bytecode.ushortAt(text, offset+2);
          out.println("\t"+opmnem+" "+wopmnem+" "+index);
          break;

        case IINC:
          index = Bytecode.ushortAt(text, offset+2);
          value = Bytecode.shortAt(text, offset+4);
          out.println("\t"+opmnem+" "+wopmnem+" "+index+" "+value);
          break;

        }
        break;

      case MULTIANEWARRAY:
        index = Bytecode.ushortAt(text, offset+1);
        int dims = Bytecode.ubyteAt(text, offset+3);
        clazz = constantPool.getClass(index);
        out.println("\t"+opmnem+" "+clazz+" "+dims);
        break;

      case GOTO_W: case JSR_W:
        branch = Bytecode.intAt(text, offset+1);
        target = offset+branch;
        out.println("\t"+opmnem+" @"+target);
        break;

      default:
        out.println("\t"+opmnem);

      }

      offset += opsize;
    }

    if (labels.get(text.length))
      out.println("@"+text.length+":");
    
    if (handlers.length > 0) {
      out.println();
      for (int i = 0; i < handlers.length; i++) {
        HandlerInfo handler = handlers[i];
        int startPC = handler.getStartPC();
        int endPC = handler.getEndPC();
        int handlerPC = handler.getHandlerPC();
        String catchType = handler.getCatchType();
        if (catchType != null)
          out.print("\t.catch "+catchType);
        else
          out.print("\t.finally");
        out.println(" from @"+startPC+" to @"+endPC+" using @"+handlerPC);
      }
    }
  }

}

