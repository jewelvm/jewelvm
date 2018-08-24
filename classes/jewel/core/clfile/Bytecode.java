/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.clfile;

import jewel.core.clfile.ClassInfo.ConstantPool;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo.HandlerInfo;

/**
 * This class provides useful methods for manipulating the bytecode
 * array.
 */
public final class Bytecode implements ClassFileConstants {

  private static final int VALID = 0x04000000;
  private static final int TARGET = 0x08000000;
  private static final int LEADER = 0x10000000;
  private static final int TRAILER = 0x20000000;
  private static final int UNREACHABLE = 0x40000000;
  private static final int OP1TYPE = 0x001F0000;
  private static final int OP2TYPE = 0x03E00000;
  private static final int STACKSIZE = 0x0000FFFF;

  public static final byte X_INT = 1;
  public static final byte X_FLOAT = 2;
  public static final byte X_REF = 3;
  public static final byte X_ADDR = 4;
  public static final byte X_INT_INT = 5;
  public static final byte X_INT_FLOAT = 6;
  public static final byte X_INT_REF = 7;
  public static final byte X_INT_ADDR = 8;
  public static final byte X_FLOAT_INT = 9;
  public static final byte X_FLOAT_FLOAT = 10;
  public static final byte X_FLOAT_REF = 11;
  public static final byte X_FLOAT_ADDR = 12;
  public static final byte X_REF_INT = 13;
  public static final byte X_REF_FLOAT = 14;
  public static final byte X_REF_REF = 15;
  public static final byte X_REF_ADDR = 16;
  public static final byte X_ADDR_INT = 17;
  public static final byte X_ADDR_FLOAT = 18;
  public static final byte X_ADDR_REF = 19;
  public static final byte X_ADDR_ADDR = 20;
  public static final byte X_LONG = 21;
  public static final byte X_DOUBLE = 22;

  /**
   * Retrieves an opcode given the bytecode array and an offset.
   */
  public static short opcodeAt(byte[] text, int address) {
    return (short)(text[address] & 0xFF);
  }

  /**
   * Retrieves a byte given the bytecode array and an offset.
   */
  public static int byteAt(byte[] text, int address) {
    return text[address];
  }

  /**
   * Retrieves an unsigned byte given the bytecode array and an offset.
   */
  public static int ubyteAt(byte[] text, int address) {
    return text[address] & 0xFF;
  }

  /**
   * Retrieves a short given the bytecode array and an offset.
   */
  public static int shortAt(byte[] text, int address) {
    return text[address] << 8 | text[address+1] & 0xFF;
  }

  /**
   * Retrieves an unsigned short given the bytecode array and an offset.
   */
  public static int ushortAt(byte[] text, int address) {
    return (text[address] & 0xFF) << 8 | text[address+1] & 0xFF;
  }

  /**
   * Retrieves an int given the bytecode array and an offset.
   */
  public static int intAt(byte[] text, int address) {
    return  text[address  ]         << 24 |
           (text[address+1] & 0xFF) << 16 |
           (text[address+2] & 0xFF) <<  8 |
            text[address+3] & 0xFF;
  }

  /**
   * Checks if a particular offset in the bytecode array is
   * a valid intruction offset.
   */
  public static boolean isValid(int[] textInfo, int address) {
    return (textInfo[address] & VALID) != 0;
  }

  private static void setValid(int[] textInfo, int address) {
    textInfo[address] |= VALID;
  }

  /**
   * Checks if a particular offset in the bytecode array is
   * a branch target offset.
   */
  public static boolean isTarget(int[] textInfo, int address) {
    return (textInfo[address] & TARGET) != 0;
  }

  private static void setTarget(int[] textInfo, int address) {
    textInfo[address] |= TARGET;
  }

  /**
   * Checks if a particular offset in the bytecode array is
   * a leader instruction offset. A leader instruction is
   * the first instruction of a basic block.
   */
  public static boolean isLeader(int[] textInfo, int address) {
    return (textInfo[address] & LEADER) != 0;
  }

  private static void setLeader(int[] textInfo, int address) {
    textInfo[address] |= LEADER;
  }

  /**
   * Checks if a particular offset in the bytecode array is
   * a trailer instruction offset. A trailer instruction is
   * the last instruction of a basic block.
   */
  public static boolean isTrailer(int[] textInfo, int address) {
    return (textInfo[address] & TRAILER) != 0;
  }

  private static void setTrailer(int[] textInfo, int address) {
    textInfo[address] |= TRAILER;
  }

  /**
   * Checks if a particular offset in the bytecode array is
   * an unreachable instruction offset.
   */
  public static boolean isUnreachable(int[] textInfo, int address) {
    return (textInfo[address] & UNREACHABLE) != 0;
  }

  /*private*/ static void setUnreachable(int[] textInfo, int address) {
    textInfo[address] |= UNREACHABLE;
  }

  /**
   * Retrieves the type of the first operand of an untyped intruction
   * at a particular offset. The untyped operations are:
   *
   * astore, astore_<n>, 
   * pop, pop2, dup, dup_x1, dup_x2, dup2, dup2_x1, dup2_x2, swap, 
   * wide astore
   */
  public static byte getOp1type(int[] textInfo, int address) {
    return (byte)((textInfo[address] & OP1TYPE) >> 16);
  }

  /*private*/ static void setOp1type(int[] textInfo, int address, byte value) {
    textInfo[address] = (textInfo[address] & ~OP1TYPE) | ((value << 16) & OP1TYPE);
  }

  /**
   * Retrieves the type of the second operand of an untyped intruction
   * at a particular offset. The untyped operations are:
   *
   * astore, astore_<n>, 
   * pop, pop2, dup, dup_x1, dup_x2, dup2, dup2_x1, dup2_x2, swap, 
   * wide astore
   */
  public static byte getOp2type(int[] textInfo, int address) {
    return (byte)((textInfo[address] & OP2TYPE) >> 21);
  }

  /*private*/ static void setOp2type(int[] textInfo, int address, byte value) {
    textInfo[address] = (textInfo[address] & ~OP2TYPE) | ((value << 21) & OP2TYPE);
  }

  /**
   * Retrieves the operand stack size prior to the execution of
   * the instruction at a given offset.
   */
  public static int getStackSize(int[] textInfo, int address) {
    return textInfo[address] & STACKSIZE;
  }

  /*private*/ static void setStackSize(int[] textInfo, int address, int value) {
    textInfo[address] = (textInfo[address] & ~STACKSIZE) | (value & STACKSIZE);
  }

  private static final byte[] sizeTable = {
   /*  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F */
/*0*/  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
/*1*/  2,  3,  2,  3,  3,  2,  2,  2,  2,  2,  1,  1,  1,  1,  1,  1,
/*2*/  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
/*3*/  1,  1,  1,  1,  1,  1,  2,  2,  2,  2,  2,  1,  1,  1,  1,  1,
/*4*/  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
/*5*/  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
/*6*/  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
/*7*/  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
/*8*/  1,  1,  1,  1,  3,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,
/*9*/  1,  1,  1,  1,  1,  1,  1,  1,  1,  3,  3,  3,  3,  3,  3,  3,
/*A*/  3,  3,  3,  3,  3,  3,  3,  3,  3,  2,  1,  1,  1,  1,  1,  1,
/*B*/  1,  1,  3,  3,  3,  3,  3,  3,  3,  5, -1,  3,  2,  3,  1,  1,
/*C*/  3,  3,  1,  1,  2,  4,  3,  3,  5,  5,
  };

  /**
   * Retrieves the size in bytes of fixed length instructions or the
   * minimum size of variable length intructions.
   */
  public static int sizeOf(short opcode) {
    return opcode < 0 || opcode > sizeTable.length ? -1 : sizeTable[opcode];
  }

  private static final byte[] wsizeTable = {
   /*  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F */
/*0*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
/*1*/ -1, -1, -1, -1, -1,  4,  4,  4,  4,  4, -1, -1, -1, -1, -1, -1,
/*2*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
/*3*/ -1, -1, -1, -1, -1, -1,  4,  4,  4,  4,  4, -1, -1, -1, -1, -1,
/*4*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
/*5*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
/*6*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
/*7*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
/*8*/ -1, -1, -1, -1,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
/*9*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
/*A*/ -1, -1, -1, -1, -1, -1, -1, -1, -1,  4,
  };

  /**
   * Retrieves the wide size in bytes of fixed length instructions or the
   * minimum size of variable length intructions.
   */
  public static int wsizeOf(short opcode) {
    return opcode < 0 || opcode > wsizeTable.length ? -1 : wsizeTable[opcode];
  }

  private static final String[] mnemonicTable = {
      /* 0/4/8/C         1/5/9/D            2/6/A/E          3/7/B/F */
/*00*/  "nop",          "aconst_null",     "iconst_m1",     "iconst_0",
/*04*/  "iconst_1",     "iconst_2",        "iconst_3",      "iconst_4",
/*08*/  "iconst_5",     "lconst_0",        "lconst_1",      "fconst_0",
/*0C*/  "fconst_1",     "fconst_2",        "dconst_0",      "dconst_1",
/*10*/  "bipush",       "sipush",          "ldc",           "ldc_w",
/*14*/  "ldc2_w",       "iload",           "lload",         "fload",
/*18*/  "dload",        "aload",           "iload_0",       "iload_1",
/*1C*/  "iload_2",      "iload_3",         "lload_0",       "lload_1",
/*20*/  "lload_2",      "lload_3",         "fload_0",       "fload_1",
/*24*/  "fload_2",      "fload_3",         "dload_0",       "dload_1",
/*28*/  "dload_2",      "dload_3",         "aload_0",       "aload_1",
/*2C*/  "aload_2",      "aload_3",         "iaload",        "laload",
/*30*/  "faload",       "daload",          "aaload",        "baload",
/*34*/  "caload",       "saload",          "istore",        "lstore",
/*38*/  "fstore",       "dstore",          "astore",        "istore_0",
/*3C*/  "istore_1",     "istore_2",        "istore_3",      "lstore_0",
/*40*/  "lstore_1",     "lstore_2",        "lstore_3",      "fstore_0",
/*44*/  "fstore_1",     "fstore_2",        "fstore_3",      "dstore_0",
/*48*/  "dstore_1",     "dstore_2",        "dstore_3",      "astore_0",
/*4C*/  "astore_1",     "astore_2",        "astore_3",      "iastore",
/*50*/  "lastore",      "fastore",         "dastore",       "aastore",
/*54*/  "bastore",      "castore",         "sastore",       "pop",
/*58*/  "pop2",         "dup",             "dup_x1",        "dup_x2",
/*5C*/  "dup2",         "dup2_x1",         "dup2_x2",       "swap",
/*60*/  "iadd",         "ladd",            "fadd",          "dadd",
/*64*/  "isub",         "lsub",            "fsub",          "dsub",
/*68*/  "imul",         "lmul",            "fmul",          "dmul",
/*6C*/  "idiv",         "ldiv",            "fdiv",          "ddiv",
/*70*/  "irem",         "lrem",            "frem",          "drem",
/*74*/  "ineg",         "lneg",            "fneg",          "dneg",
/*78*/  "ishl",         "lshl",            "ishr",          "lshr",
/*7C*/  "iushr",        "lushr",           "iand",          "land",
/*80*/  "ior",          "lor",             "ixor",          "lxor",
/*84*/  "iinc",         "i2l",             "i2f",           "i2d",
/*88*/  "l2i",          "l2f",             "l2d",           "f2i",
/*8C*/  "f2l",          "f2d",             "d2i",           "d2l",
/*90*/  "d2f",          "i2b",             "i2c",           "i2s",
/*94*/  "lcmp",         "fcmpl",           "fcmpg",         "dcmpl",
/*98*/  "dcmpg",        "ifeq",            "ifne",          "iflt",
/*9C*/  "ifge",         "ifgt",            "ifle",          "if_icmpeq",
/*A0*/  "if_icmpne",    "if_icmplt",       "if_icmpge",     "if_icmpgt",
/*A4*/  "if_icmple",    "if_acmpeq",       "if_acmpne",     "goto",
/*A8*/  "jsr",          "ret",             "tableswitch",   "lookupswitch",
/*AC*/  "ireturn",      "lreturn",         "freturn",       "dreturn",
/*B0*/  "areturn",      "return",          "getstatic",     "putstatic",
/*B4*/  "getfield",     "putfield",        "invokevirtual", "invokespecial",
/*B8*/  "invokestatic", "invokeinterface", null,            "new",
/*BC*/  "newarray",     "anewarray",       "arraylength",   "athrow",
/*C0*/  "checkcast",    "instanceof",      "monitorenter",  "monitorexit",
/*C4*/  "wide",         "multianewarray",  "ifnull",        "ifnonnull",
/*C8*/  "goto_w",       "jsr_w",
  };

  /**
   * Retrieves the mnemonic for a given opcode.
   */
  public static String mnemonicOf(short opcode) {
    return opcode < 0 || opcode > mnemonicTable.length ? null : mnemonicTable[opcode];
  }

  private static final String[] wmnemonicTable = {
      /* 0/4/8/C         1/5/9/D            2/6/A/E          3/7/B/F */
/*00*/  null,           null,              null,            null,
/*04*/  null,           null,              null,            null,
/*08*/  null,           null,              null,            null,
/*0C*/  null,           null,              null,            null,
/*10*/  null,           null,              null,            null,
/*14*/  null,           "iload",           "lload",         "fload",
/*18*/  "dload",        "aload",           null,            null,
/*1C*/  null,           null,              null,            null,
/*20*/  null,           null,              null,            null,
/*24*/  null,           null,              null,            null,
/*28*/  null,           null,              null,            null,
/*2C*/  null,           null,              null,            null,
/*30*/  null,           null,              null,            null,
/*34*/  null,           null,              "istore",        "lstore",
/*38*/  "fstore",       "dstore",          "astore",        null,
/*3C*/  null,           null,              null,            null,
/*40*/  null,           null,              null,            null,
/*44*/  null,           null,              null,            null,
/*48*/  null,           null,              null,            null,
/*4C*/  null,           null,              null,            null,
/*50*/  null,           null,              null,            null,
/*54*/  null,           null,              null,            null,
/*58*/  null,           null,              null,            null,
/*5C*/  null,           null,              null,            null,
/*60*/  null,           null,              null,            null,
/*64*/  null,           null,              null,            null,
/*68*/  null,           null,              null,            null,
/*6C*/  null,           null,              null,            null,
/*70*/  null,           null,              null,            null,
/*74*/  null,           null,              null,            null,
/*78*/  null,           null,              null,            null,
/*7C*/  null,           null,              null,            null,
/*80*/  null,           null,              null,            null,
/*84*/  "iinc",         null,              null,            null,
/*88*/  null,           null,              null,            null,
/*8C*/  null,           null,              null,            null,
/*90*/  null,           null,              null,            null,
/*94*/  null,           null,              null,            null,
/*98*/  null,           null,              null,            null,
/*9C*/  null,           null,              null,            null,
/*A0*/  null,           null,              null,            null,
/*A4*/  null,           null,              null,            null,
/*A8*/  null,           "ret",
  };

  /**
   * Retrieves the mnemonic for a given wide opcode.
   */
  public static String wmnemonicOf(short opcode) {
    return opcode < 0 || opcode > wmnemonicTable.length ? null : wmnemonicTable[opcode];
  }

  /* Check Static Constraints */

  private static void checkSize(int offset, int size, byte[] text) throws VerifyException {
    if (offset+size > text.length)
      throw new VerifyException("Code stops in the middle of instruction starting at offset "+offset);
  }

  private static void checkLegal(int offset, boolean assertion) throws VerifyException {
    if (!assertion)
      throw new VerifyException("Illegal instruction found at offset "+offset);
  }

  private static boolean enclosed(int offset, HandlerInfo[] handlers) {
    for (int i = 0; i < handlers.length; i++) {
      HandlerInfo handler = handlers[i];
      if (handler.encloses(offset))
        return true;
    }
    return false;
  }

  static void checkStaticConstraints(CodeInfo code, byte[] text, int[] textInfo, HandlerInfo[] handlers) throws VerifyException {
    int maxLocals = code.getMaxLocals();
    MethodInfo method = code.getOwner();
    ConstantPool constantPool = method.getOwner().getConstantPool();

    if (text.length == 0)
      throw new VerifyException("Empty code");

    boolean leader = true;

    int offset = 0;
    while (offset < text.length) {

      setValid(textInfo, offset);

      if (leader) {
        setLeader(textInfo, offset);
        leader = false;
      }

      short opcode = opcodeAt(text, offset);
      int opsize = sizeOf(opcode);
      if (opsize > 1)
        checkSize(offset, opsize, text);

      switch (opcode) {

      case LDC:
        int index = ubyteAt(text, offset+1);
        byte tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_INTEGER || tag == CONSTANT_FLOAT || tag == CONSTANT_STRING);
        break;

      case LDC_W:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_INTEGER || tag == CONSTANT_FLOAT || tag == CONSTANT_STRING);
        break;

      case LDC2_W:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_LONG || tag == CONSTANT_DOUBLE);
        break;

      case ILOAD: case FLOAD: case ALOAD:
      case ISTORE: case FSTORE: case ASTORE:
      case IINC:
        index = ubyteAt(text, offset+1);
        checkLegal(offset, index < maxLocals);
        break;

      case LLOAD: case DLOAD:
      case LSTORE: case DSTORE: 
        index = ubyteAt(text, offset+1);
        checkLegal(offset, index+1 < maxLocals);
        break;

      case ILOAD_0: case FLOAD_0: case ALOAD_0:
      case ISTORE_0: case FSTORE_0: case ASTORE_0:
        checkLegal(offset, 0 < maxLocals);
        break;

      case LLOAD_0: case DLOAD_0:
      case LSTORE_0: case DSTORE_0:
      case ILOAD_1: case FLOAD_1: case ALOAD_1:
      case ISTORE_1: case FSTORE_1: case ASTORE_1:
        checkLegal(offset, 1 < maxLocals);
        break;

      case LLOAD_1: case DLOAD_1:
      case LSTORE_1: case DSTORE_1:
      case ILOAD_2: case FLOAD_2: case ALOAD_2:
      case ISTORE_2: case FSTORE_2: case ASTORE_2:
        checkLegal(offset, 2 < maxLocals);
        break;

      case LLOAD_2: case DLOAD_2:
      case LSTORE_2: case DSTORE_2:
      case ILOAD_3: case FLOAD_3: case ALOAD_3:
      case ISTORE_3: case FSTORE_3: case ASTORE_3:
        checkLegal(offset, 3 < maxLocals);
        break;

      case LLOAD_3: case DLOAD_3:
      case LSTORE_3: case DSTORE_3:
        checkLegal(offset, 4 < maxLocals);
        break;

      case IALOAD: case LALOAD: case FALOAD: case DALOAD:
      case AALOAD: case BALOAD: case CALOAD: case SALOAD:
      case IASTORE: case LASTORE: case FASTORE: case DASTORE:
      case AASTORE: case BASTORE: case CASTORE: case SASTORE:
      case IDIV: case LDIV: case IREM: case LREM:
      case ARRAYLENGTH:
      case MONITORENTER: case MONITOREXIT:
        leader = enclosed(offset, handlers);
        break;

      case RET:
        index = ubyteAt(text, offset+1);
        checkLegal(offset, index < maxLocals);
        leader = true;
        break;

      case ATHROW:
        leader = true;
        break;

      case IRETURN:
        String returnType = Syntax.getReturnType(method.getDescriptor());
        char returnID = returnType.charAt(0);
        checkLegal(offset, returnID == BOOLEAN_ID || returnID == BYTE_ID || returnID == CHAR_ID || returnID == SHORT_ID || returnID == INT_ID);
        leader = true;
        break;

      case LRETURN:
        returnType = Syntax.getReturnType(method.getDescriptor());
        returnID = returnType.charAt(0);
        checkLegal(offset, returnID == LONG_ID);
        leader = true;
        break;

      case FRETURN:
        returnType = Syntax.getReturnType(method.getDescriptor());
        returnID = returnType.charAt(0);
        checkLegal(offset, returnID == FLOAT_ID);
        leader = true;
        break;

      case DRETURN:
        returnType = Syntax.getReturnType(method.getDescriptor());
        returnID = returnType.charAt(0);
        checkLegal(offset, returnID == DOUBLE_ID);
        leader = true;
        break;

      case ARETURN:
        returnType = Syntax.getReturnType(method.getDescriptor());
        returnID = returnType.charAt(0);
        checkLegal(offset, returnID == OBJECT_ID || returnID == ARRAY_ID);
        leader = true;
        break;

      case RETURN:
        returnType = Syntax.getReturnType(method.getDescriptor());
        returnID = returnType.charAt(0);
        checkLegal(offset, returnID == VOID_ID);
        leader = true;
        break;

      case GETSTATIC: case PUTSTATIC: case GETFIELD: case PUTFIELD:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_FIELDREF);
        leader = enclosed(offset, handlers);
        break;

      case INVOKESTATIC: 
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_METHODREF);
        String name = constantPool.getName(index);
        checkLegal(offset, !name.equals("<init>"));
        leader = enclosed(offset, handlers);
        break;

      case INVOKEVIRTUAL:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_METHODREF);
        name = constantPool.getName(index);
        checkLegal(offset, !name.equals("<init>"));
        String descriptor = constantPool.getDescriptor(index);
        checkLegal(offset, Syntax.getParametersSize(descriptor, true) <= 255);
        leader = enclosed(offset, handlers);
        break;

      case INVOKESPECIAL:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_METHODREF);
        descriptor = constantPool.getDescriptor(index);
        checkLegal(offset, Syntax.getParametersSize(descriptor, true) <= 255);
        leader = enclosed(offset, handlers);
        break;

      case INVOKEINTERFACE:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_INTERFACEMETHODREF);
        descriptor = constantPool.getDescriptor(index);
        int count = ubyteAt(text, offset+3);
        checkLegal(offset, Syntax.getParametersSize(descriptor, true) == count);
        checkLegal(offset, byteAt(text, offset+4) == 0);
        leader = enclosed(offset, handlers);
        break;

      case NEW:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_CLASS);
        checkLegal(offset, constantPool.getClass(index).charAt(0) != ARRAY_ID);
        leader = enclosed(offset, handlers);
        break;

      case NEWARRAY:
        int atype = ubyteAt(text, offset+1);
        checkLegal(offset, T_BOOLEAN <= atype && atype <= T_LONG);
        leader = enclosed(offset, handlers);
        break;

      case ANEWARRAY:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_CLASS);
        checkLegal(offset, constantPool.getClass(index).lastIndexOf('[')+1 != 255);
        leader = enclosed(offset, handlers);
        break;

      case MULTIANEWARRAY:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_CLASS);
        int dims = ubyteAt(text, offset+3);
        checkLegal(offset, dims != 0 && dims <= constantPool.getClass(index).lastIndexOf('[')+1);
        leader = enclosed(offset, handlers);
        break;

      case CHECKCAST:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_CLASS);
        leader = enclosed(offset, handlers);
        break;

      case INSTANCEOF:
        index = ushortAt(text, offset+1);
        tag = constantPool.tag(index);
        checkLegal(offset, tag == CONSTANT_CLASS);
        break;

      case IFEQ: case IFNE: case IFLT: case IFGE: case IFGT: case IFLE:
      case IF_ICMPEQ: case IF_ICMPNE: case IF_ICMPLT: case IF_ICMPGE: case IF_ICMPGT: case IF_ICMPLE:
      case IF_ACMPEQ: case IF_ACMPNE:
      case GOTO: case JSR:
      case IFNULL: case IFNONNULL:
        int branch = shortAt(text, offset+1);
        int target = offset+branch;
        checkLegal(offset, 0 <= target && target < text.length);
        setTarget(textInfo, target);
        setLeader(textInfo, target);
        leader = true;
        break;

      case GOTO_W: case JSR_W:
        branch = intAt(text, offset+1);
        target = offset+branch;
        checkLegal(offset, 0 <= target && target < text.length);
        setTarget(textInfo, target);
        setLeader(textInfo, target);
        leader = true;
        break;

      case TABLESWITCH:
        int pad = ~offset & 3;
        checkLegal(offset, offset+1+pad+12 <= text.length);
        for (int i = 0; i < pad; i++)
          checkLegal(offset, byteAt(text, offset+1+i) == 0);
        branch = intAt(text, offset+1+pad);
        target = offset+branch;
        checkLegal(offset, 0 <= target && target < text.length);
        setTarget(textInfo, target);
        setLeader(textInfo, target);
        int low = intAt(text, offset+1+pad+4);
        int high = intAt(text, offset+1+pad+8);
        long entries = (long)high-(long)low+1L;
        checkLegal(offset, high >= low && entries <= 16380L);
        opsize = 1+pad+12+4*(int)entries;
        checkSize(offset, opsize, text);
        for (int i = 0; i < (int)entries; i++) {
          branch = intAt(text, offset+1+pad+12+4*i);
          target = offset+branch;
          checkLegal(offset, 0 <= target && target < text.length);
          setTarget(textInfo, target);
          setLeader(textInfo, target);
        }
        leader = true;
        break;

      case LOOKUPSWITCH:
        pad = ~offset & 3;
        checkLegal(offset, offset+1+pad+8 <= text.length);
        for (int i = 0; i < pad; i++)
          checkLegal(offset, byteAt(text, offset+1+i) == 0);
        branch = intAt(text, offset+1+pad);
        target = offset+branch;
        checkLegal(offset, 0 <= target && target < text.length);
        setTarget(textInfo, target);
        setLeader(textInfo, target);
        int pairs = intAt(text, offset+1+pad+4);
        checkLegal(offset, pairs >= 0 && pairs <= 8190L);
        opsize = 1+pad+8+8*pairs;
        checkSize(offset, opsize, text);
        if (pairs > 0) {
          int last_key = intAt(text, offset+1+pad+8);
          branch = intAt(text, offset+1+pad+8+4);
          target = offset+branch;
          checkLegal(offset, 0 <= target && target < text.length);
          setTarget(textInfo, target);
          setLeader(textInfo, target);
          for (int i = 1; i < pairs; i++) {
            int key = intAt(text, offset+1+pad+8+8*i);
            checkLegal(offset, key > last_key);
            last_key = key;
            branch = intAt(text, offset+1+pad+8+8*i+4);
            target = offset+branch;
            checkLegal(offset, 0 <= target && target < text.length);
            setTarget(textInfo, target);
            setLeader(textInfo, target);
          }
        }
        leader = true;
        break;

      case WIDE:

        short wopcode = opcodeAt(text, offset+1);
        opsize = wsizeOf(wopcode);
        if (opsize > 1)
          checkSize(offset, opsize, text);

        switch (wopcode) {
        
        case ILOAD: case FLOAD: case ALOAD:
        case ISTORE: case FSTORE: case ASTORE:
        case IINC:
          index = ushortAt(text, offset+2);
          checkLegal(offset, index < maxLocals);
          break;

        case LLOAD: case DLOAD: 
        case LSTORE: case DSTORE: 
          index = ushortAt(text, offset+2);
          checkLegal(offset, index+1 < maxLocals);
          break;

        case RET:
          index = ushortAt(text, offset+2);
          checkLegal(offset, index < maxLocals);
          leader = true;
          break;

        default:
          checkLegal(offset, false);
        
        }
        break;

      default:
        if (opcode <= JSR_W)
          break;

      case XXXUNUSEDXXX:
        checkLegal(offset, false);

      }

      offset += opsize;
    }

    for (int i = 0; i < handlers.length; i++) {
      HandlerInfo handler = handlers[i];
      offset = handler.getStartPC();
      if (!isValid(textInfo, offset))
        throw new VerifyException("Illegal exception handler start offset "+offset);
      offset = handler.getEndPC();
      if (offset != text.length)
        if (!isValid(textInfo, offset))
          throw new VerifyException("Illegal exception handler end offset "+offset);
      offset = handler.getHandlerPC();
      setTarget(textInfo, offset);
      setLeader(textInfo, offset);
    }

    int lastofs = 0;
    for (offset = 1; offset < text.length; offset++)
      if (isValid(textInfo, offset)) {
        if (isLeader(textInfo, offset))
          setTrailer(textInfo, lastofs);
        lastofs = offset;
      } else
        if (isTarget(textInfo, offset))
          throw new VerifyException("Illegal branch target offset "+offset);
    setTrailer(textInfo, lastofs);
  }

  private Bytecode() { }

}

