/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.clfile;

/**
 * This interface provides the declaration for all constants
 * that appear in the <CODE>class</CODE> file format.
 */
public interface ClassFileConstants {

  /**
   * The magic word <I>cafe babe</I>.
   */
  public static final int MAGIC = 0xCAFEBABE;

  /* Version constants.
   */
  public static final int LOWER_MINOR_VERSION = 0;
  public static final int LOWER_MAJOR_VERSION = 45;
  public static final int UPPER_MINOR_VERSION = 0;
  public static final int UPPER_MAJOR_VERSION = 48;
  public static final int DEFAULT_MINOR_VERSION = 3;
  public static final int DEFAULT_MAJOR_VERSION = 45;

  /* Access flags values.
   */
  public static final int ACC_PUBLIC = 0x0001;
  public static final int ACC_PRIVATE = 0x0002;
  public static final int ACC_PROTECTED = 0x0004;
  public static final int ACC_STATIC = 0x0008;
  public static final int ACC_FINAL = 0x0010;
  public static final int ACC_SUPER = 0x0020;
  public static final int ACC_SYNCHRONIZED = 0x0020;
  public static final int ACC_VOLATILE = 0x0040;
  public static final int ACC_TRANSIENT = 0x0080;
  public static final int ACC_NATIVE = 0x0100;
  public static final int ACC_INTERFACE = 0x0200;
  public static final int ACC_ABSTRACT = 0x0400;
  public static final int ACC_STRICT = 0x0800;

  /* Constant pool tags.
   */
  public static final byte CONSTANT_CLASS = 7;
  public static final byte CONSTANT_FIELDREF = 9;
  public static final byte CONSTANT_METHODREF = 10;
  public static final byte CONSTANT_INTERFACEMETHODREF = 11;
  public static final byte CONSTANT_STRING = 8;
  public static final byte CONSTANT_INTEGER = 3;
  public static final byte CONSTANT_FLOAT = 4;
  public static final byte CONSTANT_LONG = 5;
  public static final byte CONSTANT_DOUBLE = 6;
  public static final byte CONSTANT_NAMEANDTYPE = 12;
  public static final byte CONSTANT_UTF8 = 1;

  /* Syntax IDs
   */
  public static final char VOID_ID = 'V';
  public static final char BOOLEAN_ID = 'Z';
  public static final char CHAR_ID = 'C';
  public static final char BYTE_ID = 'B';
  public static final char SHORT_ID = 'S';
  public static final char INT_ID = 'I';
  public static final char LONG_ID = 'J';
  public static final char FLOAT_ID = 'F';
  public static final char DOUBLE_ID = 'D';
  public static final char ARRAY_ID = '[';
  public static final char OBJECT_ID = 'L';
  public static final char OBJECT_SEP_ID = '/';
  public static final char OBJECT_TERM_ID = ';';
  public static final char PARAM_ID = '(';
  public static final char PARAM_TERM_ID = ')';

  /* Primitive types for the newarray opcode.
   */
  public static final byte T_BOOLEAN = 4;
  public static final byte T_CHAR = 5;
  public static final byte T_FLOAT = 6;
  public static final byte T_DOUBLE = 7;
  public static final byte T_BYTE = 8;
  public static final byte T_SHORT = 9;
  public static final byte T_INT = 10;
  public static final byte T_LONG = 11;

  /* All JVM opcodes.
   */
  public static final short NOP = 0x00;
  public static final short ACONST_NULL = 0x01;
  public static final short ICONST_M1 = 0x02;
  public static final short ICONST_0 = 0x03;
  public static final short ICONST_1 = 0x04;
  public static final short ICONST_2 = 0x05;
  public static final short ICONST_3 = 0x06;
  public static final short ICONST_4 = 0x07;
  public static final short ICONST_5 = 0x08;
  public static final short LCONST_0 = 0x09;
  public static final short LCONST_1 = 0x0A;
  public static final short FCONST_0 = 0x0B;
  public static final short FCONST_1 = 0x0C;
  public static final short FCONST_2 = 0x0D;
  public static final short DCONST_0 = 0x0E;
  public static final short DCONST_1 = 0x0F;
  public static final short BIPUSH = 0x10;
  public static final short SIPUSH = 0x11;
  public static final short LDC = 0x12;
  public static final short LDC_W = 0x13;
  public static final short LDC2_W = 0x14;
  public static final short ILOAD = 0x15;
  public static final short LLOAD = 0x16;
  public static final short FLOAD = 0x17;
  public static final short DLOAD = 0x18;
  public static final short ALOAD = 0x19;
  public static final short ILOAD_0 = 0x1A;
  public static final short ILOAD_1 = 0x1B;
  public static final short ILOAD_2 = 0x1C;
  public static final short ILOAD_3 = 0x1D;
  public static final short LLOAD_0 = 0x1E;
  public static final short LLOAD_1 = 0x1F;
  public static final short LLOAD_2 = 0x20;
  public static final short LLOAD_3 = 0x21;
  public static final short FLOAD_0 = 0x22;
  public static final short FLOAD_1 = 0x23;
  public static final short FLOAD_2 = 0x24;
  public static final short FLOAD_3 = 0x25;
  public static final short DLOAD_0 = 0x26;
  public static final short DLOAD_1 = 0x27;
  public static final short DLOAD_2 = 0x28;
  public static final short DLOAD_3 = 0x29;
  public static final short ALOAD_0 = 0x2A;
  public static final short ALOAD_1 = 0x2B;
  public static final short ALOAD_2 = 0x2C;
  public static final short ALOAD_3 = 0x2D;
  public static final short IALOAD = 0x2E;
  public static final short LALOAD = 0x2F;
  public static final short FALOAD = 0x30;
  public static final short DALOAD = 0x31;
  public static final short AALOAD = 0x32;
  public static final short BALOAD = 0x33;
  public static final short CALOAD = 0x34;
  public static final short SALOAD = 0x35;
  public static final short ISTORE = 0x36;
  public static final short LSTORE = 0x37;
  public static final short FSTORE = 0x38;
  public static final short DSTORE = 0x39;
  public static final short ASTORE = 0x3A;
  public static final short ISTORE_0 = 0x3B;
  public static final short ISTORE_1 = 0x3C;
  public static final short ISTORE_2 = 0x3D;
  public static final short ISTORE_3 = 0x3E;
  public static final short LSTORE_0 = 0x3F;
  public static final short LSTORE_1 = 0x40;
  public static final short LSTORE_2 = 0x41;
  public static final short LSTORE_3 = 0x42;
  public static final short FSTORE_0 = 0x43;
  public static final short FSTORE_1 = 0x44;
  public static final short FSTORE_2 = 0x45;
  public static final short FSTORE_3 = 0x46;
  public static final short DSTORE_0 = 0x47;
  public static final short DSTORE_1 = 0x48;
  public static final short DSTORE_2 = 0x49;
  public static final short DSTORE_3 = 0x4A;
  public static final short ASTORE_0 = 0x4B;
  public static final short ASTORE_1 = 0x4C;
  public static final short ASTORE_2 = 0x4D;
  public static final short ASTORE_3 = 0x4E;
  public static final short IASTORE = 0x4F;
  public static final short LASTORE = 0x50;
  public static final short FASTORE = 0x51;
  public static final short DASTORE = 0x52;
  public static final short AASTORE = 0x53;
  public static final short BASTORE = 0x54;
  public static final short CASTORE = 0x55;
  public static final short SASTORE = 0x56;
  public static final short POP = 0x57;
  public static final short POP2 = 0x58;
  public static final short DUP = 0x59;
  public static final short DUP_X1 = 0x5A;
  public static final short DUP_X2 = 0x5B;
  public static final short DUP2 = 0x5C;
  public static final short DUP2_X1 = 0x5D;
  public static final short DUP2_X2 = 0x5E;
  public static final short SWAP = 0x5F;
  public static final short IADD = 0x60;
  public static final short LADD = 0x61;
  public static final short FADD = 0x62;
  public static final short DADD = 0x63;
  public static final short ISUB = 0x64;
  public static final short LSUB = 0x65;
  public static final short FSUB = 0x66;
  public static final short DSUB = 0x67;
  public static final short IMUL = 0x68;
  public static final short LMUL = 0x69;
  public static final short FMUL = 0x6A;
  public static final short DMUL = 0x6B;
  public static final short IDIV = 0x6C;
  public static final short LDIV = 0x6D;
  public static final short FDIV = 0x6E;
  public static final short DDIV = 0x6F;
  public static final short IREM = 0x70;
  public static final short LREM = 0x71;
  public static final short FREM = 0x72;
  public static final short DREM = 0x73;
  public static final short INEG = 0x74;
  public static final short LNEG = 0x75;
  public static final short FNEG = 0x76;
  public static final short DNEG = 0x77;
  public static final short ISHL = 0x78;
  public static final short LSHL = 0x79;
  public static final short ISHR = 0x7A;
  public static final short LSHR = 0x7B;
  public static final short IUSHR = 0x7C;
  public static final short LUSHR = 0x7D;
  public static final short IAND = 0x7E;
  public static final short LAND = 0x7F;
  public static final short IOR = 0x80;
  public static final short LOR = 0x81;
  public static final short IXOR = 0x82;
  public static final short LXOR = 0x83;
  public static final short IINC = 0x84;
  public static final short I2L = 0x85;
  public static final short I2F = 0x86;
  public static final short I2D = 0x87;
  public static final short L2I = 0x88;
  public static final short L2F = 0x89;
  public static final short L2D = 0x8A;
  public static final short F2I = 0x8B;
  public static final short F2L = 0x8C;
  public static final short F2D = 0x8D;
  public static final short D2I = 0x8E;
  public static final short D2L = 0x8F;
  public static final short D2F = 0x90;
  public static final short I2B = 0x91;
  public static final short I2C = 0x92;
  public static final short I2S = 0x93;
  public static final short LCMP = 0x94;
  public static final short FCMPL = 0x95;
  public static final short FCMPG = 0x96;
  public static final short DCMPL = 0x97;
  public static final short DCMPG = 0x98;
  public static final short IFEQ = 0x99;
  public static final short IFNE = 0x9A;
  public static final short IFLT = 0x9B;
  public static final short IFGE = 0x9C;
  public static final short IFGT = 0x9D;
  public static final short IFLE = 0x9E;
  public static final short IF_ICMPEQ = 0x9F;
  public static final short IF_ICMPNE = 0xA0;
  public static final short IF_ICMPLT = 0xA1;
  public static final short IF_ICMPGE = 0xA2;
  public static final short IF_ICMPGT = 0xA3;
  public static final short IF_ICMPLE = 0xA4;
  public static final short IF_ACMPEQ = 0xA5;
  public static final short IF_ACMPNE = 0xA6;
  public static final short GOTO = 0xA7;
  public static final short JSR = 0xA8;
  public static final short RET = 0xA9;
  public static final short TABLESWITCH = 0xAA;
  public static final short LOOKUPSWITCH = 0xAB;
  public static final short IRETURN = 0xAC;
  public static final short LRETURN = 0xAD;
  public static final short FRETURN = 0xAE;
  public static final short DRETURN = 0xAF;
  public static final short ARETURN = 0xB0;
  public static final short RETURN = 0xB1;
  public static final short GETSTATIC = 0xB2;
  public static final short PUTSTATIC = 0xB3;
  public static final short GETFIELD = 0xB4;
  public static final short PUTFIELD = 0xB5;
  public static final short INVOKEVIRTUAL = 0xB6;
  public static final short INVOKESPECIAL = 0xB7;
  public static final short INVOKESTATIC = 0xB8;
  public static final short INVOKEINTERFACE = 0xB9;
  public static final short XXXUNUSEDXXX = 0xBA;
  public static final short NEW = 0xBB;
  public static final short NEWARRAY = 0xBC;
  public static final short ANEWARRAY = 0xBD;
  public static final short ARRAYLENGTH = 0xBE;
  public static final short ATHROW = 0xBF;
  public static final short CHECKCAST = 0xC0;
  public static final short INSTANCEOF = 0xC1;
  public static final short MONITORENTER = 0xC2;
  public static final short MONITOREXIT = 0xC3;
  public static final short WIDE = 0xC4;
  public static final short MULTIANEWARRAY = 0xC5;
  public static final short IFNULL = 0xC6;
  public static final short IFNONNULL = 0xC7;
  public static final short GOTO_W = 0xC8;
  public static final short JSR_W = 0xC9;

}

