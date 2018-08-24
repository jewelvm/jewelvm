/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class is the placeholder of all IR related classes.
 * Each public non-abstract direct inner class that is
 * subclass of TreeNode is considered by the BEG as an
 * IR opcode.
 *
 * @see jewel.core.jiro.beg.TreeNode
 */
public class IR {

  public static final short IRECEIVE = 1;
  public static final short LRECEIVE = 2;
  public static final short FRECEIVE = 3;
  public static final short DRECEIVE = 4;
  public static final short ARECEIVE = 5;
  public static final short IPASS = 6;
  public static final short LPASS = 7;
  public static final short FPASS = 8;
  public static final short DPASS = 9;
  public static final short APASS = 10;
  public static final short CALL = 11;
  public static final short CALLX = 12;
  public static final short NCALL = 13;
  public static final short NCALLX = 14;
  public static final short IRESULT = 15;
  public static final short LRESULT = 16;
  public static final short FRESULT = 17;
  public static final short DRESULT = 18;
  public static final short ARESULT = 19;
  public static final short LABEL = 20;
  public static final short JUMP = 21;
  public static final short AJUMP = 22;
  public static final short IJUMP = 23;
  public static final short ISWITCH = 24;
  public static final short ACATCH = 25;
  public static final short ATHROW = 26;
  public static final short IRETURN = 27;
  public static final short LRETURN = 28;
  public static final short FRETURN = 29;
  public static final short DRETURN = 30;
  public static final short ARETURN = 31;
  public static final short VRETURN = 32;
  public static final short IDEFINE = 33;
  public static final short LDEFINE = 34;
  public static final short FDEFINE = 35;
  public static final short DDEFINE = 36;
  public static final short ADEFINE = 37;
  public static final short IPHI = 38;
  public static final short LPHI = 39;
  public static final short FPHI = 40;
  public static final short DPHI = 41;
  public static final short APHI = 42;
  public static final short BSTORE = 43;
  public static final short SSTORE = 44;
  public static final short ISTORE = 45;
  public static final short LSTORE = 46;
  public static final short FSTORE = 47;
  public static final short DSTORE = 48;
  public static final short ASTORE = 49;
  public static final short BASTORE = 50;
  public static final short SASTORE = 51;
  public static final short IASTORE = 52;
  public static final short LASTORE = 53;
  public static final short FASTORE = 54;
  public static final short DASTORE = 55;
  public static final short AASTORE = 56;
  public static final short INIT = 57;
  public static final short INITX = 58;
  public static final short NEWINSTANCE = 59;
  public static final short NEWINSTANCEX = 60;
  public static final short NEWARRAY = 61;
  public static final short NEWARRAYX = 62;
  public static final short LOCK = 63;
  public static final short LOCKX = 64;
  public static final short UNLOCK = 65;
  public static final short READBARRIER = 66;
  public static final short WRITEBARRIER = 67;
  public static final short GETCLASS = 68;
  public static final short ALOAD = 69;
  public static final short AALOAD = 70;
  public static final short MLOOKUP = 71;
  public static final short IMLOOKUP = 72;
  public static final short AUSE = 73;
  public static final short ANULL = 74;
  public static final short ASTRING = 75;
  public static final short ACLASS = 76;
  public static final short I2B = 77;
  public static final short I2C = 78;
  public static final short I2S = 79;
  public static final short L2I = 80;
  public static final short F2I = 81;
  public static final short D2I = 82;
  public static final short IADD = 83;
  public static final short ISUB = 84;
  public static final short IMUL = 85;
  public static final short IDIV = 86;
  public static final short IREM = 87;
  public static final short INEG = 88;
  public static final short ISHL = 89;
  public static final short ISHR = 90;
  public static final short IUSHR = 91;
  public static final short IAND = 92;
  public static final short IOR = 93;
  public static final short IXOR = 94;
  public static final short LCMP = 95;
  public static final short FCMPG = 96;
  public static final short FCMPL = 97;
  public static final short DCMPG = 98;
  public static final short DCMPL = 99;
  public static final short LENGTH = 100;
  public static final short BLOAD = 101;
  public static final short SLOAD = 102;
  public static final short ILOAD = 103;
  public static final short BALOAD = 104;
  public static final short SALOAD = 105;
  public static final short IALOAD = 106;
  public static final short ISLOCKED = 107;
  public static final short SUBTYPEOF = 108;
  public static final short COMPTYPEOF = 109;
  public static final short IUSE = 110;
  public static final short ICONST = 111;
  public static final short I2L = 112;
  public static final short F2L = 113;
  public static final short D2L = 114;
  public static final short LADD = 115;
  public static final short LSUB = 116;
  public static final short LMUL = 117;
  public static final short LDIV = 118;
  public static final short LREM = 119;
  public static final short LNEG = 120;
  public static final short LSHL = 121;
  public static final short LSHR = 122;
  public static final short LUSHR = 123;
  public static final short LAND = 124;
  public static final short LOR = 125;
  public static final short LXOR = 126;
  public static final short LLOAD = 127;
  public static final short LALOAD = 128;
  public static final short LUSE = 129;
  public static final short LCONST = 130;
  public static final short I2F = 131;
  public static final short L2F = 132;
  public static final short D2F = 133;
  public static final short FSTRICT = 134;
  public static final short FADD = 135;
  public static final short FSUB = 136;
  public static final short FMUL = 137;
  public static final short FDIV = 138;
  public static final short FREM = 139;
  public static final short FNEG = 140;
  public static final short FLOAD = 141;
  public static final short FALOAD = 142;
  public static final short FUSE = 143;
  public static final short FCONST = 144;
  public static final short I2D = 145;
  public static final short L2D = 146;
  public static final short F2D = 147;
  public static final short DSTRICT = 148;
  public static final short DADD = 149;
  public static final short DSUB = 150;
  public static final short DMUL = 151;
  public static final short DDIV = 152;
  public static final short DREM = 153;
  public static final short DNEG = 154;
  public static final short DLOAD = 155;
  public static final short DALOAD = 156;
  public static final short DUSE = 157;
  public static final short DCONST = 158;

  /** R */
  public static abstract class rnode implements Cloneable, TreeNode {

    protected rnode() { }

    public TreeNode left() {
      return null;
    }

    public void setLeft(TreeNode left) {
      throw new UnsupportedOperationException();
    }

    public TreeNode middle() {
      return null;
    }

    public void setMiddle(TreeNode middle) {
      throw new UnsupportedOperationException();
    }

    public TreeNode right() {
      return null;
    }

    public void setRight(TreeNode right) {
      throw new UnsupportedOperationException();
    }

    public int hashCode() {
      int hashCode = op();
      TreeNode left = left();
      if (left != null) {
        int hcode = left.hashCode();
        hashCode ^=  (hcode<<8)|(hcode>>>24);
      }
      TreeNode middle = middle();
      if (middle != null) {
        int hcode = middle.hashCode();
        hashCode ^=  (hcode<<16)|(hcode>>>16);
      }
      TreeNode right = right();
      if ( right != null) {
        int hcode = right.hashCode();
        hashCode ^=  (hcode<<24)|(hcode>>>8);
      }
      return hashCode;
    }

    public boolean equals(TreeNode node) {
      if (node == null)
        return false;
      if (op() != node.op())
        return false;
      TreeNode left = left();
      TreeNode nodeLeft = node.left();
      if (left != nodeLeft && !left.equals(nodeLeft))
        return false;
      TreeNode middle = middle();
      TreeNode nodeMiddle = node.middle();
      if (middle != nodeMiddle && !middle.equals(nodeMiddle))
        return false;
      TreeNode right = right();
      TreeNode nodeRight = node.right();
      if (right != nodeRight && !right.equals(nodeRight))
        return false;
      return true;
    }

    public Object clone() {
      rnode node;
      try { 
        node = (rnode)super.clone();
      } catch (CloneNotSupportedException e) {
        throw new Error();
      }
      vnode left = (vnode)node.left();
      if (left != null)
        node.setLeft((vnode)left.clone());
      vnode middle = (vnode)node.middle();
      if (middle != null)
        node.setMiddle((vnode)middle.clone());
      vnode right = (vnode)node.right();
      if (right != null)
        node.setRight((vnode)right.clone());
      return node;
    }

    protected void readFrom(DataInputStream in) throws IOException { }

    public void writeTo(DataOutputStream out) throws IOException {
      out.writeByte(op());
    }

  }

  /** S */
  public static abstract class snode extends rnode {

    public static snode newFrom(DataInputStream in) throws IOException {
      int op = in.readUnsignedByte();
      snode node;
      switch (op) {
      case IRECEIVE: node = new ireceive(); break;
      case LRECEIVE: node = new lreceive(); break;
      case FRECEIVE: node = new freceive(); break;
      case DRECEIVE: node = new dreceive(); break;
      case ARECEIVE: node = new areceive(); break;
      case IPASS: node = new ipass(); break;
      case LPASS: node = new lpass(); break;
      case FPASS: node = new fpass(); break;
      case DPASS: node = new dpass(); break;
      case APASS: node = new apass(); break;
      case CALL: node = new call(); break;
      case CALLX: node = new callx(); break;
      case NCALL: node = new ncall(); break;
      case NCALLX: node = new ncallx(); break;
      case IRESULT: node = new iresult(); break;
      case LRESULT: node = new lresult(); break;
      case FRESULT: node = new fresult(); break;
      case DRESULT: node = new dresult(); break;
      case ARESULT: node = new aresult(); break;
      case LABEL: node = new label(); break;
      case JUMP: node = new jump(); break;
      case AJUMP: node = new ajump(); break;
      case IJUMP: node = new ijump(); break;
      case ISWITCH: node = new iswitch(); break;
      case ACATCH: node = new acatch(); break;
      case ATHROW: node = new athrow(); break;
      case IRETURN: node = new ireturn(); break;
      case LRETURN: node = new lreturn(); break;
      case FRETURN: node = new freturn(); break;
      case DRETURN: node = new dreturn(); break;
      case ARETURN: node = new areturn(); break;
      case VRETURN: node = new vreturn(); break;
      case IDEFINE: node = new idefine(); break;
      case LDEFINE: node = new ldefine(); break;
      case FDEFINE: node = new fdefine(); break;
      case DDEFINE: node = new ddefine(); break;
      case ADEFINE: node = new adefine(); break;
      case IPHI: node = new iphi(); break;
      case LPHI: node = new lphi(); break;
      case FPHI: node = new fphi(); break;
      case DPHI: node = new dphi(); break;
      case APHI: node = new aphi(); break;
      case BSTORE: node = new bstore(); break;
      case SSTORE: node = new sstore(); break;
      case ISTORE: node = new istore(); break;
      case LSTORE: node = new lstore(); break;
      case FSTORE: node = new fstore(); break;
      case DSTORE: node = new dstore(); break;
      case ASTORE: node = new astore(); break;
      case BASTORE: node = new bastore(); break;
      case SASTORE: node = new sastore(); break;
      case IASTORE: node = new iastore(); break;
      case LASTORE: node = new lastore(); break;
      case FASTORE: node = new fastore(); break;
      case DASTORE: node = new dastore(); break;
      case AASTORE: node = new aastore(); break;
      case INIT: node = new init(); break;
      case INITX: node = new initx(); break;
      case NEWINSTANCE: node = new newinstance(); break;
      case NEWINSTANCEX: node = new newinstancex(); break;
      case NEWARRAY: node = new newarray(); break;
      case NEWARRAYX: node = new newarrayx(); break;
      case LOCK: node = new lock(); break;
      case LOCKX: node = new lockx(); break;
      case UNLOCK: node = new unlock(); break;
      case READBARRIER: node = new readbarrier(); break;
      case WRITEBARRIER: node = new writebarrier(); break;
      default:
        throw new IOException();
      }
      node.readFrom(in);
      return node;
    }

    private IRStatement ownerStmt;

    protected snode() { }

    void attachTo(IRStatement stmt) {
      if (stmt != null && ownerStmt != null)
        throw new IllegalStateException();
      ownerStmt = stmt;
    }

    public IRStatement ownerStmt() {
      return ownerStmt;
    }

    public final boolean hasNext() {
      return true;
    }

    public final TreeNode previous() {
      if (ownerStmt == null)
        return null;
      IRStatement stmt = (IRStatement)ownerStmt.previousStmt();
      if (stmt == null)
        return null;
      return stmt.snode();
    }

    public final TreeNode next() {
      if (ownerStmt == null)
        return null;
      IRStatement stmt = (IRStatement)ownerStmt.nextStmt();
      if (stmt == null)
        return null;
      return stmt.snode();
    }

    public Object clone() {
      snode node = (snode)super.clone();
      node.ownerStmt = null;
      return node;
    }

  }

  /** T */
  public static abstract class tnode extends snode {

    private trace[] strace = { };

    public static final class trace {

      private String symbolicType;
      private char index;
      private char line;

      protected trace() { }

      public trace(String symbolicType, int index, int line) {
        if (symbolicType == null)
          throw new NullPointerException();
        this.symbolicType = symbolicType;
        if (index < 0 || index > 65534)
          throw new IllegalArgumentException();
        if (line < -1 || line > 65535)
          throw new IllegalArgumentException();
        if (line == -1) {
          this.index = (char)65535;
          this.line = (char)index;
        } else {
          this.index = (char)index;
          this.line = (char)line;
        }
      }

      public String getSymbolicType() {
        return symbolicType;
      }

      public int getIndex() {
        return index == 65535 ? line : index;
      }

      public int getLine() {
        return index == 65535 ? -1 : line;
      }

      public boolean equals(Object object) {
        return object instanceof trace
            && ((trace)object).index == index
            && ((trace)object).line == line
            && ((trace)object).symbolicType.equals(symbolicType);
      }

      protected void readFrom(DataInputStream in) throws IOException {
        symbolicType = in.readUTF();
        index = in.readChar();
        line = in.readChar();
        if (index == 65535 && line == 65535)
          throw new IOException();
      }

      protected void writeTo(DataOutputStream out) throws IOException {
        out.writeUTF(symbolicType);
        out.writeChar(index);
        out.writeChar(line);
      }

      public String toString() {
        return "[$"+symbolicType+","+getIndex()+","+getLine()+"]";
      }

    }

    protected tnode() { }

    public void append(String symbolicType, int index, int line) {
      trace[] tmp = strace;
      strace = new trace[tmp.length+1];
      System.arraycopy(tmp, 0, strace, 0, tmp.length);
      strace[tmp.length] = new trace(symbolicType, index, line);
    }

    public void append(trace t) {
      append(t.getSymbolicType(), t.getIndex(), t.getLine());
    }

    public Iterator traces() {
      return new Iterator() {
        private int index;
        public boolean hasNext() {
          return index < strace.length;
        }
        public Object next() {
          return strace[index++];
        }
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }

    public boolean equals(TreeNode node) {
      if (node instanceof tnode) {
        tnode tnode = (tnode)node;
        if (strace.length != tnode.strace.length)
          return false;
        for (int i = 0; i < strace.length; i++)
          if (!strace[i].equals(tnode.strace[i]))
            return false;
        return super.equals(node);
      }
      return false;
    }

    public Object clone() {
      tnode node = (tnode)super.clone();
      node.strace = (trace[])strace.clone();
      return node;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      int length = in.readInt();
      if (length < 0)
        throw new IOException();
      strace = new trace[length];
      for (int i = 0; i < strace.length; i++) {
        strace[i] = new trace();
        strace[i].readFrom(in);
      }
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(strace.length);
      for (int i = 0; i < strace.length; i++)
        strace[i].writeTo(out);
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < strace.length; i++)
        sb.append(strace[i]);
      return sb.toString();
    }

  }

  /** V */
  public static abstract class vnode extends rnode {

    protected vnode() { }

    public final boolean hasNext() {
      return false;
    }

    public final TreeNode next() {
      throw new UnsupportedOperationException();
    }

  }
  
  /** A */
  public static abstract class anode extends vnode {

    public static anode newFrom(DataInputStream in) throws IOException {
      int op = in.readUnsignedByte();
      anode node;
      switch (op) {
      case GETCLASS: node = new getclass(); break;
      case ALOAD: node = new aload(); break;
      case AALOAD: node = new aaload(); break;
      case MLOOKUP: node = new mlookup(); break;
      case IMLOOKUP: node = new imlookup(); break;
      case AUSE: node = new ause(); break;
      case ANULL: node = new anull(); break;
      case ASTRING: node = new astring(); break;
      case ACLASS: node = new aclass(); break;
      default:
        throw new IOException();
      }
      node.readFrom(in);
      return node;
    }

    protected anode() { }

  }

  /** I */
  public static abstract class inode extends vnode {

    public static inode newFrom(DataInputStream in) throws IOException {
      int op = in.readUnsignedByte();
      inode node;
      switch (op) {
      case I2B: node = new i2b(); break;
      case I2C: node = new i2c(); break;
      case I2S: node = new i2s(); break;
      case L2I: node = new l2i(); break;
      case F2I: node = new f2i(); break;
      case D2I: node = new d2i(); break;
      case IADD: node = new iadd(); break;
      case ISUB: node = new isub(); break;
      case IMUL: node = new imul(); break;
      case IDIV: node = new idiv(); break;
      case IREM: node = new irem(); break;
      case INEG: node = new ineg(); break;
      case ISHL: node = new ishl(); break;
      case ISHR: node = new ishr(); break;
      case IUSHR: node = new iushr(); break;
      case IAND: node = new iand(); break;
      case IOR: node = new ior(); break;
      case IXOR: node = new ixor(); break;
      case LCMP: node = new lcmp(); break;
      case FCMPG: node = new fcmpg(); break;
      case FCMPL: node = new fcmpl(); break;
      case DCMPG: node = new dcmpg(); break;
      case DCMPL: node = new dcmpl(); break;
      case LENGTH: node = new length(); break;
      case BLOAD: node = new bload(); break;
      case SLOAD: node = new sload(); break;
      case ILOAD: node = new iload(); break;
      case BALOAD: node = new baload(); break;
      case SALOAD: node = new saload(); break;
      case IALOAD: node = new iaload(); break;
      case ISLOCKED: node = new islocked(); break;
      case SUBTYPEOF: node = new subtypeof(); break;
      case COMPTYPEOF: node = new comptypeof(); break;
      case IUSE: node = new iuse(); break;
      case ICONST: node = new iconst(); break;
      default:
        throw new IOException();
      }
      node.readFrom(in);
      return node;
    }

    protected inode() { }

  }
  
  /** L */
  public static abstract class lnode extends vnode {

    public static lnode newFrom(DataInputStream in) throws IOException {
      int op = in.readUnsignedByte();
      lnode node;
      switch (op) {
      case I2L: node = new i2l(); break;
      case F2L: node = new f2l(); break;
      case D2L: node = new d2l(); break;
      case LADD: node = new ladd(); break;
      case LSUB: node = new lsub(); break;
      case LMUL: node = new lmul(); break;
      case LDIV: node = new ldiv(); break;
      case LREM: node = new lrem(); break;
      case LNEG: node = new lneg(); break;
      case LSHL: node = new lshl(); break;
      case LSHR: node = new lshr(); break;
      case LUSHR: node = new lushr(); break;
      case LAND: node = new land(); break;
      case LOR: node = new lor(); break;
      case LXOR: node = new lxor(); break;
      case LLOAD: node = new lload(); break;
      case LALOAD: node = new laload(); break;
      case LUSE: node = new luse(); break;
      case LCONST: node = new lconst(); break;
      default:
        throw new IOException();
      }
      node.readFrom(in);
      return node;
    }

    protected lnode() { }

  }
  
  /** F */
  public static abstract class fnode extends vnode {

    public static fnode newFrom(DataInputStream in) throws IOException {
      int op = in.readUnsignedByte();
      fnode node;
      switch (op) {
      case I2F: node = new i2f(); break;
      case L2F: node = new l2f(); break;
      case D2F: node = new d2f(); break;
      case FSTRICT: node = new fstrict(); break;
      case FADD: node = new fadd(); break;
      case FSUB: node = new fsub(); break;
      case FMUL: node = new fmul(); break;
      case FDIV: node = new fdiv(); break;
      case FREM: node = new frem(); break;
      case FNEG: node = new fneg(); break;
      case FLOAD: node = new fload(); break;
      case FALOAD: node = new faload(); break;
      case FUSE: node = new fuse(); break;
      case FCONST: node = new fconst(); break;
      default:
        throw new IOException();
      }
      node.readFrom(in);
      return node;
    }

    protected fnode() { }

  }
  
  /** D */
  public static abstract class dnode extends vnode {

    public static dnode newFrom(DataInputStream in) throws IOException {
      int op = in.readUnsignedByte();
      dnode node;
      switch (op) {
      case I2D: node = new i2d(); break;
      case L2D: node = new l2d(); break;
      case F2D: node = new f2d(); break;
      case DSTRICT: node = new dstrict(); break;
      case DADD: node = new dadd(); break;
      case DSUB: node = new dsub(); break;
      case DMUL: node = new dmul(); break;
      case DDIV: node = new ddiv(); break;
      case DREM: node = new drem(); break;
      case DNEG: node = new dneg(); break;
      case DLOAD: node = new dload(); break;
      case DALOAD: node = new daload(); break;
      case DUSE: node = new duse(); break;
      case DCONST: node = new dconst(); break;
      default:
        throw new IOException();
      }
      node.readFrom(in);
      return node;
    }

    protected dnode() { }

  }

  /** S: ireceive(%i) */
  public static final class ireceive extends snode {

    private int reg;

    protected ireceive() { }

    public ireceive(int reg) {
      setReg(reg);
    }

    public int op() {
      return IRECEIVE;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 0)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof ireceive
          && ((ireceive)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "ireceive(%"+reg+")";
    }

  }

  /** S: lreceive(%l) */
  public static final class lreceive extends snode {

    private int reg;

    protected lreceive() { }

    public lreceive(int reg) {
      setReg(reg);
    }

    public int op() {
      return LRECEIVE;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 1)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof lreceive
          && ((lreceive)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 1)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "lreceive(%"+reg+")";
    }

  }

  /** S: freceive(%f) */
  public static final class freceive extends snode {

    private int reg;

    protected freceive() { }

    public freceive(int reg) { 
      setReg(reg);
    }

    public int op() {
      return FRECEIVE;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 2)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof freceive
          && ((freceive)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 2)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "freceive(%"+reg+")";
    }

  }

  /** S: dreceive(%d) */
  public static final class dreceive extends snode {

    private int reg;

    protected dreceive() { }

    public dreceive(int reg) { 
      setReg(reg);
    }

    public int op() {
      return DRECEIVE;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 3)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof dreceive
          && ((dreceive)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 3)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "dreceive(%"+reg+")";
    }

  }

  /** S: areceive(%a,#c) */
  public static final class areceive extends snode {

    private int reg;
    private String symbolicType;

    protected areceive() { }

    public areceive(int reg, String symbolicType) { 
      setReg(reg);
      setSymbolicType(symbolicType);
    }

    public int op() {
      return ARECEIVE;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 4)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public String getSymbolicType() {
      return symbolicType;
    }

    public void setSymbolicType(String symbolicType) {
      if (symbolicType == null)
        throw new NullPointerException();
      this.symbolicType = symbolicType;
    }

    public boolean equals(TreeNode node) {
      return node instanceof areceive
          && ((areceive)node).reg == reg
          && ((areceive)node).symbolicType.equals(symbolicType)
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 4)
        throw new IOException();
      symbolicType = in.readUTF();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
      out.writeUTF(symbolicType);
    }

    public String toString() {
      return "areceive(%"+reg+",#"+symbolicType+")";
    }

  }

  /** S: ipass(I) */
  public static final class ipass extends snode {

    private inode left;

    protected ipass() { }

    public ipass(inode left) { 
      setLeft(left);
    }

    public int op() {
      return IPASS;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "ipass("+left+")";
    }

  }

  /** S: lpass(L) */
  public static final class lpass extends snode {

    private lnode left;

    protected lpass() { }

    public lpass(lnode left) { 
      setLeft(left); 
    }

    public int op() {
      return LPASS;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "lpass("+left+")";
    }

  }

  /** S: fpass(F) */
  public static final class fpass extends snode {

    private fnode left;

    protected fpass() { }

    public fpass(fnode left) { 
      setLeft(left);
    }

    public int op() {
      return FPASS;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "fpass("+left+")";
    }

  }

  /** S: dpass(D) */
  public static final class dpass extends snode {

    private dnode left;

    protected dpass() { }

    public dpass(dnode left) { 
      setLeft(left);
    }

    public int op() {
      return DPASS;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "dpass("+left+")";
    }

  }

  /** S: apass(A) */
  public static final class apass extends snode {

    private anode left;

    protected apass() { }

    public apass(anode left) { 
      setLeft(left);
    }

    public int op() {
      return APASS;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "apass("+left+")";
    }

  }

  /** S: call(A,#t) */
  public static final class call extends tnode {

    private anode left;

    protected call() { }

    public call(anode left) { 
      setLeft(left);
    }

    public int op() {
      return CALL;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "call("+left+","+super.toString()+")";
    }

  }

  /** S: callx(A,#t,@l) */
  public static final class callx extends tnode {

    private anode left;
    private int label;

    protected callx() { }

    public callx(anode left, int label) { 
      setLeft(left);
      setHandler(label);
    }

    public int op() {
      return CALLX;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public int getHandler() {
      return label;
    }

    public void setHandler(int label) {
      if (label < 0)
        throw new IllegalArgumentException();
      this.label = label;
    }

    public boolean equals(TreeNode node) {
      return node instanceof callx
          && ((callx)node).label == label
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      label = in.readInt();
      if (label < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeInt(label);
    }

    public String toString() {
      return "callx("+left+","+super.toString()+",@"+label+")";
    }

  }

  /** S: ncall(#c,#s,#t) */
  public static final class ncall extends tnode {

    private String symbolicType;
    private String name;
    private String descriptor;

    protected ncall() { }

    public ncall(String symbolicType, String name, String descriptor) { 
      setSymbolicType(symbolicType);
      setName(name);
      setDescriptor(descriptor);
    }

    public int op() {
      return NCALL;
    }

    public int arity() {
      return 0;
    }

    public String getSymbolicType() {
      return symbolicType;
    }

    public void setSymbolicType(String symbolicType) {
      if (symbolicType == null)
        throw new NullPointerException();
      this.symbolicType = symbolicType;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      if (name == null)
        throw new NullPointerException();
      this.name = name;
    }

    public String getDescriptor() {
      return descriptor;
    }

    public void setDescriptor(String descriptor) {
      if (descriptor == null)
        throw new NullPointerException();
      this.descriptor = descriptor;
    }

    public boolean equals(TreeNode node) {
      return node instanceof ncall
          && ((ncall)node).symbolicType.equals(symbolicType)
          && ((ncall)node).name.equals(name)
          && ((ncall)node).descriptor.equals(descriptor)
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      symbolicType = in.readUTF();
      name = in.readUTF();
      descriptor = in.readUTF();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeUTF(symbolicType);
      out.writeUTF(name);
      out.writeUTF(descriptor);
    }

    public String toString() {
      return "ncall(\""+symbolicType+"."+name+descriptor+"\","+super.toString()+")";
    }

  }

  /** S: ncallx(#c,#s,#t,@l) */
  public static final class ncallx extends tnode {

    private String symbolicType;
    private String name;
    private String descriptor;
    private int label;

    protected ncallx() { }

    public ncallx(String symbolicType, String name, String descriptor, int label) { 
      setSymbolicType(symbolicType);
      setName(name);
      setDescriptor(descriptor);
      setHandler(label);
    }

    public int op() {
      return NCALLX;
    }

    public int arity() {
      return 0;
    }

    public String getSymbolicType() {
      return symbolicType;
    }

    public void setSymbolicType(String symbolicType) {
      if (symbolicType == null)
        throw new NullPointerException();
      this.symbolicType = symbolicType;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      if (name == null)
        throw new NullPointerException();
      this.name = name;
    }

    public String getDescriptor() {
      return descriptor;
    }

    public void setDescriptor(String descriptor) {
      if (descriptor == null)
        throw new NullPointerException();
      this.descriptor = descriptor;
    }

    public int getHandler() {
      return label;
    }

    public void setHandler(int label) {
      if (label < 0)
        throw new IllegalArgumentException();
      this.label = label;
    }

    public boolean equals(TreeNode node) {
      return node instanceof ncallx
          && ((ncallx)node).label == label
          && ((ncallx)node).symbolicType.equals(symbolicType)
          && ((ncallx)node).name.equals(name)
          && ((ncallx)node).descriptor.equals(descriptor)
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      symbolicType = in.readUTF();
      name = in.readUTF();
      descriptor = in.readUTF();
      label = in.readInt();
      if (label < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeUTF(symbolicType);
      out.writeUTF(name);
      out.writeUTF(descriptor);
      out.writeInt(label);
    }

    public String toString() {
      return "ncallx(\""+symbolicType+"."+name+descriptor+"\","+super.toString()+",@"+label+")";
    }

  }

  /** S: iresult(%i) */
  public static final class iresult extends snode {

    private int reg;

    protected iresult() { }

    public iresult(int reg) { 
      setReg(reg);
    }

    public int op() {
      return IRESULT;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 0)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof iresult
          && ((iresult)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "iresult(%"+reg+")";
    }

  }

  /** S: lresult(%l) */
  public static final class lresult extends snode {

    private int reg;

    protected lresult() { }

    public lresult(int reg) { 
      setReg(reg);
    }

    public int op() {
      return LRESULT;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 1)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof lresult
          && ((lresult)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 1)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "lresult(%"+reg+")";
    }

  }

  /** S: fresult(%f) */
  public static final class fresult extends snode {

    private int reg;

    protected fresult() { }

    public fresult(int reg) {
      setReg(reg);
    }

    public int op() {
      return FRESULT;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 2)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof fresult
          && ((fresult)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 2)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "fresult(%"+reg+")";
    }

  }

  /** S: dresult(%d) */
  public static final class dresult extends snode {

    private int reg;

    protected dresult() { }

    public dresult(int reg) {
      setReg(reg);
    }

    public int op() {
      return DRESULT;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 3)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof dresult
          && ((dresult)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 3)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "dresult(%"+reg+")";
    }

  }

  /** S: aresult(%a,#c) */
  public static final class aresult extends snode {

    private int reg;
    private String symbolicType;

    protected aresult() { }

    public aresult(int reg, String symbolicType) { 
      setReg(reg);
      setSymbolicType(symbolicType);
    }

    public int op() {
      return ARESULT;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 4)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public String getSymbolicType() {
      return symbolicType;
    }

    public void setSymbolicType(String symbolicType) {
      if (symbolicType == null)
        throw new NullPointerException();
      this.symbolicType = symbolicType;
    }

    public boolean equals(TreeNode node) {
      return node instanceof aresult
          && ((aresult)node).reg == reg
          && ((aresult)node).symbolicType.equals(symbolicType)
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 4)
        throw new IOException();
      symbolicType = in.readUTF();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
      out.writeUTF(symbolicType);
    }

    public String toString() {
      return "aresult(%"+reg+",#"+symbolicType+")";
    }

  }

  /** S: label(@l) */
  public static final class label extends snode {

    private int label;

    protected label() { }

    public label(int label) { 
      setValue(label);
    }

    public int op() {
      return LABEL;
    }

    public int arity() {
      return 0;
    }

    public int getValue() {
      return label;
    }

    public void setValue(int label) {
      if (label < 0)
        throw new IllegalArgumentException();
      this.label = label;
    }

    public boolean equals(TreeNode node) {
      return node instanceof label
          && ((label)node).label == label
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      label = in.readInt();
      if (label < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(label);
    }

    public String toString() {
      return "label(@"+label+")";
    }

  }

  /** S: jump(@l) */
  public static final class jump extends snode {

    private int label;

    protected jump() { }

    public jump(int label) { 
      setTarget(label);
    }

    public int op() {
      return JUMP;
    }

    public int arity() {
      return 0;
    }

    public int getTarget() {
      return label;
    }

    public void setTarget(int label) {
      if (label < 0)
        throw new IllegalArgumentException();
      this.label = label;
    }

    public boolean equals(TreeNode node) {
      return node instanceof jump
          && ((jump)node).label == label
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      label = in.readInt();
      if (label < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(label);
    }

    public String toString() {
      return "jump(@"+label+")";
    }

  }

  /** S: ajump(#x,A,A,@l) */
  public static final class ajump extends snode {

    public static final byte EQ = 1;
    public static final byte NE = 2;

    public static String xtoString(int xop) {
      switch (xop) {
      case EQ: return "EQ";
      case NE: return "NE";
      default: return null;
      }
    }

    private byte xop;
    private anode left;
    private anode right;
    private int label;

    protected ajump() { }

    public ajump(byte xop, anode left, anode right, int label) { 
      setXop(xop);
      setLeft(left); 
      setRight(right);
      setTarget(label);
    }

    public int op() {
      return AJUMP;
    }

    public int arity() {
      return 2;
    }

    public int xop() {
      return xop;
    }

    public void setXop(int xop) {
      if (xop < EQ || xop > NE)
        throw new IllegalArgumentException();
      this.xop = (byte)xop;
    }

    public void invertXop() {
      switch (xop) {
      case EQ: xop = NE; break;
      case NE: xop = EQ; break;
      }
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((anode)right);
    }

    public void setRight(anode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public int getTarget() {
      return label;
    }

    public void setTarget(int label) {
      if (label < 0)
        throw new IllegalArgumentException();
      this.label = label;
    }

    public boolean equals(TreeNode node) {
      return node instanceof ajump
          && ((ajump)node).xop == xop
          && ((ajump)node).label == label
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      xop = in.readByte();
      if (xop < EQ || xop > NE)
        throw new IOException();
      left = anode.newFrom(in);
      right = anode.newFrom(in);
      label = in.readInt();
      if (label < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeByte(xop);
      left.writeTo(out);
      right.writeTo(out);
      out.writeInt(label);
    }

    public String toString() {
      return "ajump("+xtoString(xop)+","+left+","+right+",@"+label+")";
    }

  }

  /** S: ijump(#x,I,I,@l) */
  public static final class ijump extends snode {

    public static final byte EQ = 1;
    public static final byte NE = 2;
    public static final byte LT = 3;
    public static final byte GE = 4;
    public static final byte GT = 5;
    public static final byte LE = 6;
    public static final byte B = 7;
    public static final byte AE = 8;
    public static final byte A = 9;
    public static final byte BE = 10;

    public static String xtoString(int xop) {
      switch (xop) {
      case EQ: return "EQ";
      case NE: return "NE";
      case LT: return "LT";
      case GE: return "GE";
      case GT: return "GT";
      case LE: return "LE";
      case B: return "B";
      case AE: return "AE";
      case A: return "A";
      case BE: return "BE";
      default: return null;
      }
    }

    private byte xop;
    private inode left;
    private inode right;
    private int label;

    protected ijump() { }

    public ijump(byte xop, inode left, inode right, int label) {
      setXop(xop);
      setLeft(left);
      setRight(right);
      setTarget(label);
    }

    public int op() {
      return IJUMP;
    }

    public int arity() {
      return 2;
    }

    public int xop() {
      return xop;
    }

    public void setXop(int xop) {
      if (xop < EQ || xop > BE)
        throw new IllegalArgumentException();
      this.xop = (byte)xop;
    }

    public void invertXop() {
      switch (xop) {
      case EQ: xop = NE; break;
      case NE: xop = EQ; break;
      case LT: xop = GE; break;
      case GE: xop = LT; break;
      case GT: xop = LE; break;
      case LE: xop = GT; break;
      case B: xop = AE; break;
      case AE: xop = B; break;
      case A: xop = BE; break;
      case BE: xop = A; break;
      }
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public int getTarget() {
      return label;
    }

    public void setTarget(int label) {
      if (label < 0)
        throw new IllegalArgumentException();
      this.label = label;
    }

    public boolean equals(TreeNode node) {
      return node instanceof ijump
          && ((ijump)node).xop == xop
          && ((ijump)node).label == label
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      xop = in.readByte();
      if (xop < EQ || xop > BE)
        throw new IOException();
      left = inode.newFrom(in);
      right = inode.newFrom(in);
      label = in.readInt();
      if (label < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeByte(xop);
      left.writeTo(out);
      right.writeTo(out);
      out.writeInt(label);
    }

    public String toString() {
      return "ijump("+xtoString(xop)+","+left+","+right+",@"+label+")";
    }

  }

  /** S: iswitch(I,[$i,@l]...) */
  public static final class iswitch extends snode {

    private inode left;
    private int pairs_size;
    private pair[] pairs;

    public static final class pair implements Cloneable {

      private int key;
      private int label;

      protected pair() { }

      pair(int key, int label) {
        this.key = key;
        setTarget(label);
      }

      public int getKey() {
        return key;
      }

      public int getTarget() {
        return label;
      }

      public void setTarget(int label) {
        if (label < 0)
          throw new IllegalArgumentException();
        this.label = label;
      }

      public boolean equals(Object object) {
        return object instanceof pair
            && ((pair)object).key == key
            && ((pair)object).label == label;
      }

      public Object clone() {
        try {
          return super.clone();
        } catch (CloneNotSupportedException e) {
          throw new Error();
        }
      }

      protected void readFrom(DataInputStream in) throws IOException {
        key = in.readInt();
        label = in.readInt();
        if (label < 0)
          throw new IOException();
      }

      protected void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(key);
        out.writeInt(label);
      }

      public String toString() {
        return "[$"+key+",@"+label+"]";
      }

    }

    protected iswitch() { }

    public iswitch(inode left) {
      setLeft(left);
      pairs = new pair[8];
    }

    public iswitch(inode left, int size) {
      setLeft(left);
      if (size < 0)
        throw new IllegalArgumentException();
      pairs = new pair[size];
    }

    public int op() {
      return ISWITCH;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    // improove
    public int getTarget(int key) {
      int left = 0;
      int right = pairs_size;
      while (left < right) {
        int middle = (left+right)/2;
        pair middlePair = pairs[middle];
        int middleKey = middlePair.getKey();
        if (key < middleKey)
          right = middle;
        else if (key > middleKey)
          left = middle+1;
        else
          return middlePair.getTarget();
      }
      return -1;
    }

    // improove
    public void setTarget(int key, int label) {
      if (label < -1)
        throw new IllegalArgumentException();
      int left = 0;
      int right = pairs_size;
      int middle;
      while (left < right) {
        middle = (left+right)/2;
        pair middlePair = pairs[middle];
        int middleKey = middlePair.getKey();
        if (key < middleKey)
          right = middle;
        else if (key > middleKey)
          left = middle+1;
        else {
          if (label != -1)
            middlePair.label = label;
          else {
            pairs_size--;
            System.arraycopy(pairs, middle+1, pairs, middle, pairs_size-middle);
          }
          return;
        }
      }
      middle = left;
      if (label != -1) {
        pair[] tmp = pairs;
        if (pairs_size == pairs.length) {
          pairs = new pair[pairs_size+8];
          System.arraycopy(tmp, 0, pairs, 0, pairs_size);
        }
        System.arraycopy(tmp, middle, pairs, middle+1, pairs_size-middle);
        pairs_size++;
        pairs[middle] = new pair(key, label);
      }
    }

    public int pairCount() {
      return pairs_size;
    }

    public int tableSize() {
      if (pairs_size == 0)
        return 0;
      return pairs[pairs_size-1].getKey()-pairs[0].getKey()+1;
    }

    public float tableWaste() {
      int count = pairCount();
      int size = tableSize();
      if (size == 0)
        return 0.0F;
      return (float)(size-count)/(float)size;
    }

    public Iterator pairs() {
      return new Iterator() {
        private int last = -1;
        private int index;
        public boolean hasNext() {
          return index < pairs_size;
        }
        public Object next() {
          if (index >= pairs_size)
            throw new NoSuchElementException();
          last = index;
          return pairs[index++];
        }
        public void remove() {
          if (last == -1)
            throw new IllegalStateException();
          pairs_size--;
          index--;
          System.arraycopy(pairs, last+1, pairs, last, pairs_size-index);
          last = -1;
        }
      };
    }

    public boolean equals(TreeNode node) {
      if (node instanceof iswitch) {
        iswitch iswitch = (iswitch)node;
        if (pairs_size != iswitch.pairs_size)
          return false;
        for (int i = 0; i < pairs_size; i++)
          if (!pairs[i].equals(iswitch.pairs[i]))
            return false;
        return super.equals(node);
      }
      return false;
    }

    public Object clone() {
      iswitch node = (iswitch)super.clone();
      node.pairs = new pair[pairs_size];
      for (int i = 0; i < pairs_size; i++)
        node.pairs[i] = (pair)pairs[i].clone();
      return node;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      pairs_size = in.readInt();
      if (pairs_size < 0)
        throw new IOException();
      pairs = new pair[pairs_size];
      for (int i = 0; i < pairs_size; i++) {
        pair pair = new pair();
        pair.readFrom(in);
        pairs[i] = pair;
      }
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeInt(pairs_size);
      for (int i = 0; i < pairs_size; i++)
        pairs[i].writeTo(out);
    }

    public String toString() {
      StringBuffer sb = new StringBuffer(14*pairs_size);
      for (int i = 0; i < pairs_size; i++)
        sb.append(pairs[i]);
      return "iswitch("+left+","+sb+")";
    }

  }

  /** S: acatch(%a,#c) */
  public static final class acatch extends snode {

    private int reg;
    private String symbolicType;

    protected acatch() { }

    public acatch(int reg, String symbolicType) { 
      setReg(reg);
      setSymbolicType(symbolicType);
    }

    public int op() {
      return ACATCH;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 4)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public String getSymbolicType() {
      return symbolicType;
    }

    public void setSymbolicType(String symbolicType) {
      if (symbolicType == null)
        throw new NullPointerException();
      this.symbolicType = symbolicType;
    }

    public boolean equals(TreeNode node) {
      return node instanceof acatch
          && ((acatch)node).reg == reg
          && ((acatch)node).symbolicType.equals(symbolicType)
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 4)
        throw new IOException();
      symbolicType = in.readUTF();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
      out.writeUTF(symbolicType);
    }

    public String toString() {
      return "acatch(%"+reg+",#"+symbolicType+")";
    }

  }

  /** S: athrow(A) */
  public static final class athrow extends snode {

    private anode left;

    protected athrow() { }

    public athrow(anode left) { 
      setLeft(left);
    }

    public int op() {
      return ATHROW;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "athrow("+left+")";
    }

  }

  /** S: ireturn(I) */
  public static final class ireturn extends snode {

    private inode left;

    protected ireturn() { }

    public ireturn(inode left) { 
      setLeft(left);
    }

    public int op() {
      return IRETURN;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "ireturn("+left+")";
    }

  }

  /** S: lreturn(L) */
  public static final class lreturn extends snode {

    private lnode left;

    protected lreturn() { }

    public lreturn(lnode left) { 
      setLeft(left);
    }

    public int op() {
      return LRETURN;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "lreturn("+left+")";
    }

  }

  /** S: freturn(F) */
  public static final class freturn extends snode {

    private fnode left;

    protected freturn() { }

    public freturn(fnode left) { 
      setLeft(left);
    }

    public int op() {
      return FRETURN;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "freturn("+left+")";
    }

  }

  /** S: dreturn(D) */
  public static final class dreturn extends snode {

    private dnode left;

    protected dreturn() { }

    public dreturn(dnode left) { 
      setLeft(left);
    }

    public int op() {
      return DRETURN;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "dreturn("+left+")";
    }

  }

  /** S: areturn(A) */
  public static final class areturn extends snode {

    private anode left;

    protected areturn() { }

    public areturn(anode left) { 
      setLeft(left);
    }

    public int op() {
      return ARETURN;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "areturn("+left+")";
    }

  }

  /** S: vreturn() */
  public static final class vreturn extends snode {

    public vreturn() { }

    public int op() {
      return VRETURN;
    }

    public int arity() {
      return 0;
    }

    public String toString() {
      return "vreturn()";
    }

  }

  /** S: idefine(%i,I) */
  public static final class idefine extends snode {

    private int reg;
    private inode left;

    protected idefine() { }

    public idefine(int reg, inode left) {
      setReg(reg);
      setLeft(left);
    }

    public int op() {
      return IDEFINE;
    }

    public int arity() {
      return 1;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 0)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public boolean equals(TreeNode node) {
      return node instanceof idefine
          && ((idefine)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 0)
        throw new IOException();
      left = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
      left.writeTo(out);
    }

    public String toString() {
      return "idefine(%"+reg+","+left+")";
    }

  }

  /** S: ldefine(%l,L) */
  public static final class ldefine extends snode {

    private int reg;
    private lnode left;

    protected ldefine() { }

    public ldefine(int reg, lnode left) {
      setReg(reg);
      setLeft(left);
    }

    public int op() {
      return LDEFINE;
    }

    public int arity() {
      return 1;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 1)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public boolean equals(TreeNode node) {
      return node instanceof ldefine
          && ((ldefine)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 1)
        throw new IOException();
      left = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
      left.writeTo(out);
    }

    public String toString() {
      return "ldefine(%"+reg+","+left+")";
    }

  }

  /** S: fdefine(%f,F) */
  public static final class fdefine extends snode {

    private int reg;
    private fnode left;

    protected fdefine() { }

    public fdefine(int reg, fnode left) {
      setReg(reg);
      setLeft(left);
    }

    public int op() {
      return FDEFINE;
    }

    public int arity() {
      return 1;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 2)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public boolean equals(TreeNode node) {
      return node instanceof fdefine
          && ((fdefine)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 2)
        throw new IOException();
      left = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
      left.writeTo(out);
    }

    public String toString() {
      return "fdefine(%"+reg+","+left+")";
    }

  }

  /** S: ddefine(%d,D) */
  public static final class ddefine extends snode {

    private int reg;
    private dnode left;

    protected ddefine() { }

    public ddefine(int reg, dnode left) {
      setReg(reg);
      setLeft(left);
    }

    public int op() {
      return DDEFINE;
    }

    public int arity() {
      return 1;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 3)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public boolean equals(TreeNode node) {
      return node instanceof ddefine
          && ((ddefine)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 3)
        throw new IOException();
      left = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
      left.writeTo(out);
    }

    public String toString() {
      return "ddefine(%"+reg+","+left+")";
    }

  }

  /** S: adefine(%a,A) */
  public static final class adefine extends snode {

    private int reg;
    private anode left;

    protected adefine() { }

    public adefine(int reg, anode left) {
      setReg(reg);
      setLeft(left);
    }

    public int op() {
      return ADEFINE;
    }

    public int arity() {
      return 1;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 4)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public boolean equals(TreeNode node) {
      return node instanceof adefine
          && ((adefine)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 4)
        throw new IOException();
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
      left.writeTo(out);
    }

    public String toString() {
      return "adefine(%"+reg+","+left+")";
    }

  }

  /** S: iphi(%i,[%i]...) */
  public static final class iphi extends snode {

    private int reg;
    private HashMap map = new HashMap(5);

    protected iphi() { }

    public iphi(int reg) {
      setReg(reg);
    }

    public int op() {
      return IPHI;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 0)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public void clear() {
      map.clear();
    }

    public int getReg(IRControlEdge edge) {
      Integer intg = (Integer)map.get(edge);
      if (intg == null)
        throw new IllegalArgumentException();
      return intg.intValue();
    }

    public void setReg(IRControlEdge edge, int reg) {
      if (reg < 0 || reg % 5 != 0)
        throw new IllegalArgumentException();
      if (!map.containsKey(edge)) {
        IRStatement stmt = ownerStmt();
        if (stmt == null)
          throw new IllegalArgumentException();
        IRBasicBlock ownerBB = (IRBasicBlock)stmt.ownerBB();
        if (ownerBB == null)
          throw new IllegalArgumentException();
        if (ownerBB != edge.targetBB())
          throw new IllegalArgumentException();
        IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
        if (cfg == null)
          throw new IllegalArgumentException();
        if (!cfg.contains(edge))
          throw new IllegalArgumentException();
      }
      map.put(edge, new Integer(reg));
    }

    public void removeEdge(IRControlEdge edge) {
      map.remove(edge);
    }

    public Iterator edges() {
      return map.keySet().iterator();
    }

    public Iterator regs() {
      return map.values().iterator();
    }

    public boolean equals(TreeNode node) {
      return node instanceof iphi
          && ((iphi)node).reg == reg
          && ((iphi)node).map.equals(map)// care with edges
          && super.equals(node);
    }

    public Object clone() {
      iphi node = (iphi)super.clone();
      node.map = (HashMap)map.clone();
      node.clear();
      return node;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      throw new IOException();
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("iphi(%");
      sb.append(reg);
      sb.append(",");
      for (Iterator i = regs(); i.hasNext(); ) {
        Integer intg = (Integer)i.next();
        sb.append("[%");
        sb.append(intg);
        sb.append("]");
      }
      sb.append(")");
      return sb.toString();
    }

  }
  
  /** S: lphi(%l,[%l]...) */
  public static final class lphi extends snode {

    private int reg;
    private HashMap map = new HashMap(5);

    protected lphi() { }

    public lphi(int reg) {
      setReg(reg);
    }

    public int op() {
      return LPHI;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 1)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public void clear() {
      map.clear();
    }

    public int getReg(IRControlEdge edge) {
      Integer intg = (Integer)map.get(edge);
      if (intg == null)
        throw new IllegalArgumentException();
      return intg.intValue();
    }

    public void setReg(IRControlEdge edge, int reg) {
      if (reg < 0 || reg % 5 != 1)
        throw new IllegalArgumentException();
      if (!map.containsKey(edge)) {
        IRStatement stmt = ownerStmt();
        if (stmt == null)
          throw new IllegalArgumentException();
        IRBasicBlock ownerBB = (IRBasicBlock)stmt.ownerBB();
        if (ownerBB == null)
          throw new IllegalArgumentException();
        if (ownerBB != edge.targetBB())
          throw new IllegalArgumentException();
        IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
        if (cfg == null)
          throw new IllegalArgumentException();
        if (!cfg.contains(edge))
          throw new IllegalArgumentException();
      }
      map.put(edge, new Integer(reg));
    }

    public void removeEdge(IRControlEdge edge) {
      map.remove(edge);
    }

    public Iterator edges() {
      return map.keySet().iterator();
    }

    public Iterator regs() {
      return map.values().iterator();
    }

    public boolean equals(TreeNode node) {
      return node instanceof lphi
          && ((lphi)node).reg == reg
          && ((lphi)node).map.equals(map)// care with edges
          && super.equals(node);
    }

    public Object clone() {
      lphi node = (lphi)super.clone();
      node.map = (HashMap)map.clone();
      node.clear();
      return node;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      throw new IOException();
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("lphi(%");
      sb.append(reg);
      sb.append(",");
      for (Iterator i = regs(); i.hasNext(); ) {
        Integer intg = (Integer)i.next();
        sb.append("[%");
        sb.append(intg);
        sb.append("]");
      }
      sb.append(")");
      return sb.toString();
    }

  }
  
  /** S: fphi(%f,[%f]...) */
  public static final class fphi extends snode {

    private int reg;
    private HashMap map = new HashMap(5);

    protected fphi() { }

    public fphi(int reg) {
      setReg(reg);
    }

    public int op() {
      return FPHI;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 2)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public void clear() {
      map.clear();
    }

    public int getReg(IRControlEdge edge) {
      Integer intg = (Integer)map.get(edge);
      if (intg == null)
        throw new IllegalArgumentException();
      return intg.intValue();
    }

    public void setReg(IRControlEdge edge, int reg) {
      if (reg < 0 || reg % 5 != 2)
        throw new IllegalArgumentException();
      if (!map.containsKey(edge)) {
        IRStatement stmt = ownerStmt();
        if (stmt == null)
          throw new IllegalArgumentException();
        IRBasicBlock ownerBB = (IRBasicBlock)stmt.ownerBB();
        if (ownerBB == null)
          throw new IllegalArgumentException();
        if (ownerBB != edge.targetBB())
          throw new IllegalArgumentException();
        IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
        if (cfg == null)
          throw new IllegalArgumentException();
        if (!cfg.contains(edge))
          throw new IllegalArgumentException();
      }
      map.put(edge, new Integer(reg));
    }

    public void removeEdge(IRControlEdge edge) {
      map.remove(edge);
    }

    public Iterator edges() {
      return map.keySet().iterator();
    }

    public Iterator regs() {
      return map.values().iterator();
    }

    public boolean equals(TreeNode node) {
      return node instanceof fphi
          && ((fphi)node).reg == reg
          && ((fphi)node).map.equals(map)// care with edges
          && super.equals(node);
    }

    public Object clone() {
      fphi node = (fphi)super.clone();
      node.map = (HashMap)map.clone();
      node.clear();
      return node;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      throw new IOException();
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("fphi(%");
      sb.append(reg);
      sb.append(",");
      for (Iterator i = regs(); i.hasNext(); ) {
        Integer intg = (Integer)i.next();
        sb.append("[%");
        sb.append(intg);
        sb.append("]");
      }
      sb.append(")");
      return sb.toString();
    }

  }
  
  /** S: dphi(%d,[%d]...) */
  public static final class dphi extends snode {

    private int reg;
    private HashMap map = new HashMap(5);

    protected dphi() { }

    public dphi(int reg) {
      setReg(reg);
    }

    public int op() {
      return DPHI;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 3)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public void clear() {
      map.clear();
    }

    public int getReg(IRControlEdge edge) {
      Integer intg = (Integer)map.get(edge);
      if (intg == null)
        throw new IllegalArgumentException();
      return intg.intValue();
    }

    public void setReg(IRControlEdge edge, int reg) {
      if (reg < 0 || reg % 5 != 3)
        throw new IllegalArgumentException();
      if (!map.containsKey(edge)) {
        IRStatement stmt = ownerStmt();
        if (stmt == null)
          throw new IllegalArgumentException();
        IRBasicBlock ownerBB = (IRBasicBlock)stmt.ownerBB();
        if (ownerBB == null)
          throw new IllegalArgumentException();
        if (ownerBB != edge.targetBB())
          throw new IllegalArgumentException();
        IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
        if (cfg == null)
          throw new IllegalArgumentException();
        if (!cfg.contains(edge))
          throw new IllegalArgumentException();
      }
      map.put(edge, new Integer(reg));
    }

    public void removeEdge(IRControlEdge edge) {
      map.remove(edge);
    }

    public Iterator edges() {
      return map.keySet().iterator();
    }

    public Iterator regs() {
      return map.values().iterator();
    }

    public boolean equals(TreeNode node) {
      return node instanceof dphi
          && ((dphi)node).reg == reg
          && ((dphi)node).map.equals(map)// care with edges
          && super.equals(node);
    }

    public Object clone() {
      dphi node = (dphi)super.clone();
      node.map = (HashMap)map.clone();
      node.clear();
      return node;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      throw new IOException();
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("dphi(%");
      sb.append(reg);
      sb.append(",");
      for (Iterator i = regs(); i.hasNext(); ) {
        Integer intg = (Integer)i.next();
        sb.append("[%");
        sb.append(intg);
        sb.append("]");
      }
      sb.append(")");
      return sb.toString();
    }

  }
  
  /** S: aphi(%a,[%a]...) */
  public static final class aphi extends snode {

    private int reg;
    private HashMap map = new HashMap(5);

    protected aphi() { }

    public aphi(int reg) {
      setReg(reg);
    }

    public int op() {
      return APHI;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 4)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public void clear() {
      map.clear();
    }

    public int getReg(IRControlEdge edge) {
      Integer intg = (Integer)map.get(edge);
      if (intg == null)
        throw new IllegalArgumentException();
      return intg.intValue();
    }

    public void setReg(IRControlEdge edge, int reg) {
      if (reg < 0 || reg % 5 != 4)
        throw new IllegalArgumentException();
      if (!map.containsKey(edge)) {
        IRStatement stmt = ownerStmt();
        if (stmt == null)
          throw new IllegalArgumentException();
        IRBasicBlock ownerBB = (IRBasicBlock)stmt.ownerBB();
        if (ownerBB == null)
          throw new IllegalArgumentException();
        if (ownerBB != edge.targetBB())
          throw new IllegalArgumentException();
        IRCFG cfg = (IRCFG)ownerBB.ownerCFG();
        if (cfg == null)
          throw new IllegalArgumentException();
        if (!cfg.contains(edge))
          throw new IllegalArgumentException();
      }
      map.put(edge, new Integer(reg));
    }

    public void removeEdge(IRControlEdge edge) {
      map.remove(edge);
    }

    public Iterator edges() {
      return map.keySet().iterator();
    }

    public Iterator regs() {
      return map.values().iterator();
    }

    public boolean equals(TreeNode node) {
      return node instanceof aphi
          && ((aphi)node).reg == reg
          && ((aphi)node).map.equals(map)// care with edges
          && super.equals(node);
    }

    public Object clone() {
      aphi node = (aphi)super.clone();
      node.map = (HashMap)map.clone();
      node.clear();
      return node;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      throw new IOException();
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("aphi(%");
      sb.append(reg);
      sb.append(",");
      for (Iterator i = regs(); i.hasNext(); ) {
        Integer intg = (Integer)i.next();
        sb.append("[%");
        sb.append(intg);
        sb.append("]");
      }
      sb.append(")");
      return sb.toString();
    }

  }
  
  /** S: bstore(A,#o,#v,I) */
  public static final class bstore extends snode {

    private anode left;
    private long offset;
    private boolean volat;
    private inode right;

    protected bstore() { }

    public bstore(anode left, long offset, boolean volat, inode right) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
      setRight(right);
    }

    public int op() {
      return BSTORE;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public boolean equals(TreeNode node) {
      return node instanceof bstore
          && ((bstore)node).offset == offset
          && ((bstore)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
      right.writeTo(out);
    }

    public String toString() {
      return "bstore("+left+","+Measure.toString(offset)+","+volat+","+right+")";
    }

  }

  /** S: sstore(A,#o,#v,I) */
  public static final class sstore extends snode {

    private anode left;
    private long offset;
    private boolean volat;
    private inode right;

    protected sstore() { }

    public sstore(anode left, long offset, boolean volat, inode right) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
      setRight(right);
    }

    public int op() {
      return SSTORE;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public boolean equals(TreeNode node) {
      return node instanceof sstore
          && ((sstore)node).offset == offset
          && ((sstore)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
      right.writeTo(out);
    }

    public String toString() {
      return "sstore("+left+","+Measure.toString(offset)+","+volat+","+right+")";
    }

  }

  /** S: istore(A,#o,#v,I) */
  public static final class istore extends snode {

    private anode left;
    private long offset;
    private boolean volat;
    private inode right;

    protected istore() { }

    public istore(anode left, long offset, boolean volat, inode right) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
      setRight(right);
    }

    public int op() {
      return ISTORE;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public boolean equals(TreeNode node) {
      return node instanceof istore
          && ((istore)node).offset == offset
          && ((istore)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
      right.writeTo(out);
    }

    public String toString() {
      return "istore("+left+","+Measure.toString(offset)+","+volat+","+right+")";
    }

  }

  /** S: lstore(A,#o,#v,L) */
  public static final class lstore extends snode {

    private anode left;
    private long offset;
    private boolean volat;
    private lnode right;

    protected lstore() { }

    public lstore(anode left, long offset, boolean volat, lnode right) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
      setRight(right);
    }

    public int op() {
      return LSTORE;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public boolean equals(TreeNode node) {
      return node instanceof lstore
          && ((lstore)node).offset == offset
          && ((lstore)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
      right.writeTo(out);
    }

    public String toString() {
      return "lstore("+left+","+Measure.toString(offset)+","+volat+","+right+")";
    }

  }

  /** S: fstore(A,#o,#v,F) */
  public static final class fstore extends snode {

    private anode left;
    private long offset;
    private boolean volat;
    private fnode right;

    protected fstore() { }

    public fstore(anode left, long offset, boolean volat, fnode right) { 
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
      setRight(right);
    }

    public int op() {
      return FSTORE;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((fnode)right);
    }

    public void setRight(fnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public boolean equals(TreeNode node) {
      return node instanceof fstore
          && ((fstore)node).offset == offset
          && ((fstore)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
      right = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
      right.writeTo(out);
    }

    public String toString() {
      return "fstore("+left+","+Measure.toString(offset)+","+volat+","+right+")";
    }

  }

  /** S: dstore(A,#o,#v,D) */
  public static final class dstore extends snode {

    private anode left;
    private long offset;
    private boolean volat;
    private dnode right;

    protected dstore() { }

    public dstore(anode left, long offset, boolean volat, dnode right) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
      setRight(right);
    }

    public int op() {
      return DSTORE;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((dnode)right);
    }

    public void setRight(dnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public boolean equals(TreeNode node) {
      return node instanceof dstore
          && ((dstore)node).offset == offset
          && ((dstore)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
      right = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
      right.writeTo(out);
    }

    public String toString() {
      return "dstore("+left+","+Measure.toString(offset)+","+volat+","+right+")";
    }

  }

  /** S: astore(A,#o,#v,A) */
  public static final class astore extends snode {

    private anode left;
    private long offset;
    private boolean volat;
    private anode right;

    protected astore() { }

    public astore(anode left, long offset, boolean volat, anode right) { 
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
      setRight(right);
    }

    public int op() {
      return ASTORE;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((anode)right);
    }

    public void setRight(anode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public boolean equals(TreeNode node) {
      return node instanceof astore
          && ((astore)node).offset == offset
          && ((astore)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
      right = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
      right.writeTo(out);
    }

    public String toString() {
      return "astore("+left+","+Measure.toString(offset)+","+volat+","+right+")";
    }

  }

  /** S: bastore(A,I,I) */
  public static final class bastore extends snode {

    private anode left;
    private inode middle;
    private inode right;

    protected bastore() { }

    public bastore(anode left, inode middle, inode right) {
      setLeft(left);
      setMiddle(middle);
      setRight(right);
    }

    public int op() {
      return BASTORE;
    }

    public int arity() {
      return 3;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode middle() {
      return middle;
    }

    public void setMiddle(TreeNode middle) {
      setMiddle((inode)middle);
    }

    public void setMiddle(inode middle) {
      if (middle == null)
        throw new NullPointerException();
      this.middle = middle;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      middle = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      middle.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "bastore("+left+","+middle+","+right+")";
    }

  }

  /** S: sastore(A,I,I) */
  public static final class sastore extends snode {

    private anode left;
    private inode middle;
    private inode right;

    protected sastore() { }

    public sastore(anode left, inode middle, inode right) {
      setLeft(left);
      setMiddle(middle);
      setRight(right);
    }

    public int op() {
      return SASTORE;
    }

    public int arity() {
      return 3;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode middle() {
      return middle;
    }

    public void setMiddle(TreeNode middle) {
      setMiddle((inode)middle);
    }

    public void setMiddle(inode middle) {
      if (middle == null)
        throw new NullPointerException();
      this.middle = middle;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      middle = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      middle.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "aastore("+left+","+middle+","+right+")";
    }

  }

  /** S: iastore(A,I,I) */
  public static final class iastore extends snode {

    private anode left;
    private inode middle;
    private inode right;

    protected iastore() { }

    public iastore(anode left, inode middle, inode right) {
      setLeft(left);
      setMiddle(middle);
      setRight(right);
    }

    public int op() {
      return IASTORE;
    }

    public int arity() {
      return 3;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode middle() {
      return middle;
    }

    public void setMiddle(TreeNode middle) {
      setMiddle((inode)middle);
    }

    public void setMiddle(inode middle) {
      if (middle == null)
        throw new NullPointerException();
      this.middle = middle;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      middle = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      middle.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "iastore("+left+","+middle+","+right+")";
    }

  }

  /** S: lastore(A,I,L) */
  public static final class lastore extends snode {

    private anode left;
    private inode middle;
    private lnode right;

    protected lastore() { }

    public lastore(anode left, inode middle, lnode right) {
      setLeft(left);
      setMiddle(middle);
      setRight(right);
    }

    public int op() {
      return LASTORE;
    }

    public int arity() {
      return 3;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode middle() {
      return middle;
    }

    public void setMiddle(TreeNode middle) {
      setMiddle((inode)middle);
    }

    public void setMiddle(inode middle) {
      if (middle == null)
        throw new NullPointerException();
      this.middle = middle;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      middle = inode.newFrom(in);
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      middle.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "lastore("+left+","+middle+","+right+")";
    }

  }

  /** S: fastore(A,I,F) */
  public static final class fastore extends snode {

    private anode left;
    private inode middle;
    private fnode right;

    protected fastore() { }

    public fastore(anode left, inode middle, fnode right) {
      setLeft(left);
      setMiddle(middle);
      setRight(right);
    }

    public int op() {
      return FASTORE;
    }

    public int arity() {
      return 3;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode middle() {
      return middle;
    }

    public void setMiddle(TreeNode middle) {
      setMiddle((inode)middle);
    }

    public void setMiddle(inode middle) {
      if (middle == null)
        throw new NullPointerException();
      this.middle = middle;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((fnode)right);
    }

    public void setRight(fnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      middle = inode.newFrom(in);
      right = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      middle.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "fastore("+left+","+middle+","+right+")";
    }

  }

  /** S: dastore(A,I,D) */
  public static final class dastore extends snode {

    private anode left;
    private inode middle;
    private dnode right;

    protected dastore() { }

    public dastore(anode left, inode middle, dnode right) {
      setLeft(left);
      setMiddle(middle);
      setRight(right);
    }

    public int op() {
      return DASTORE;
    }

    public int arity() {
      return 3;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode middle() {
      return middle;
    }

    public void setMiddle(TreeNode middle) {
      setMiddle((inode)middle);
    }

    public void setMiddle(inode middle) {
      if (middle == null)
        throw new NullPointerException();
      this.middle = middle;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((dnode)right);
    }

    public void setRight(dnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      middle = inode.newFrom(in);
      right = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      middle.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "dastore("+left+","+middle+","+right+")";
    }

  }

  /** S: aastore(A,I,A) */
  public static final class aastore extends snode {

    private anode left;
    private inode middle;
    private anode right;
    
    protected aastore() { }

    public aastore(anode left, inode middle, anode right) {
      setLeft(left);
      setMiddle(middle);
      setRight(right);
    }

    public int op() {
      return AASTORE;
    }

    public int arity() {
      return 3;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode middle() {
      return middle;
    }

    public void setMiddle(TreeNode middle) {
      setMiddle((inode)middle);
    }

    public void setMiddle(inode middle) {
      if (middle == null)
        throw new NullPointerException();
      this.middle = middle;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((anode)right);
    }

    public void setRight(anode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      middle = inode.newFrom(in);
      right = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      middle.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "aastore("+left+","+middle+","+right+")";
    }

  }

  /** S: init(A,#t) */
  public static final class init extends tnode {

    private anode left;

    protected init() { }

    public init(anode left) {
      setLeft(left);
    }

    public int op() {
      return INIT;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "init("+left+","+super.toString()+")";
    }

  }

  /** S: initx(A,#t,@l) */
  public static final class initx extends tnode {

    private anode left;
    private int label;

    protected initx() { }

    public initx(anode left, int label) {
      setLeft(left);
      setHandler(label);
    }

    public int op() {
      return INITX;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public int getHandler() {
      return label;
    }

    public void setHandler(int label) {
      if (label < 0)
        throw new IllegalArgumentException();
      this.label = label;
    }

    public boolean equals(TreeNode node) {
      return node instanceof initx
          && ((initx)node).label == label
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      label = in.readInt();
      if (label < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeInt(label);
    }

    public String toString() {
      return "initx("+left+","+super.toString()+",@"+label+")";
    }

  }

  /** S: newinstance(A,#t) */
  public static final class newinstance extends tnode {

    private anode left;

    protected newinstance() { }

    public newinstance(anode left) {
      setLeft(left);
    }

    public int op() {
      return NEWINSTANCE;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "newinstance("+left+","+super.toString()+")";
    }

  }

  /** S: newinstancex(A,#t,@l) */
  public static final class newinstancex extends tnode {

    private anode left;
    private int label;

    protected newinstancex() { }

    public newinstancex(anode left, int label) {
      setLeft(left);
      setHandler(label);
    }

    public int op() {
      return NEWINSTANCEX;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public int getHandler() {
      return label;
    }

    public void setHandler(int label) {
      if (label < 0)
        throw new IllegalArgumentException();
      this.label = label;
    }

    public boolean equals(TreeNode node) {
      return node instanceof newinstancex
          && ((newinstancex)node).label == label
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      label = in.readInt();
      if (label < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeInt(label);
    }

    public String toString() {
      return "newinstancex("+left+","+super.toString()+",@"+label+")";
    }

  }

  /** S: newarray(A,I,#t) */
  public static final class newarray extends tnode {

    private anode left;
    private inode right;

    protected newarray() { }

    public newarray(anode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return NEWARRAY;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "newarray("+left+","+right+","+super.toString()+")";
    }

  }

  /** S: newarrayx(A,I,#t,@l) */
  public static final class newarrayx extends tnode {

    private anode left;
    private inode right;
    private int label;

    protected newarrayx() { }

    public newarrayx(anode left, inode right, int label) {
      setLeft(left);
      setRight(right);
      setHandler(label);
    }

    public int op() {
      return NEWARRAYX;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public int getHandler() {
      return label;
    }

    public void setHandler(int label) {
      if (label < 0)
        throw new IllegalArgumentException();
      this.label = label;
    }

    public boolean equals(TreeNode node) {
      return node instanceof newarrayx
          && ((newarrayx)node).label == label
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = inode.newFrom(in);
      label = in.readInt();
      if (label < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
      out.writeInt(label);
    }

    public String toString() {
      return "newarrayx("+left+","+right+","+super.toString()+",@"+label+")";
    }

  }

  /** S: lock(A,#t) */
  public static final class lock extends tnode {

    private anode left;

    protected lock() { }

    public lock(anode left) {
      setLeft(left);
    }

    public int op() {
      return LOCK;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "lock("+left+","+super.toString()+")";
    }

  }

  /** S: lockx(A,#t,@l) */
  public static final class lockx extends tnode {

    private anode left;
    private int label;

    protected lockx() { }

    public lockx(anode left, int label) {
      setLeft(left);
      setHandler(label);
    }

    public int op() {
      return LOCKX;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public int getHandler() {
      return label;
    }

    public void setHandler(int label) {
      if (label < 0)
        throw new IllegalArgumentException();
      this.label = label;
    }

    public boolean equals(TreeNode node) {
      return node instanceof lockx
          && ((lockx)node).label == label
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      label = in.readInt();
      if (label < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeInt(label);
    }

    public String toString() {
      return "lockx("+left+","+super.toString()+",@"+label+")";
    }

  }

  /** S: unlock(A) */
  public static final class unlock extends snode {

    private anode left;

    protected unlock() { }

    public unlock(anode left) {
      setLeft(left);
    }

    public int op() {
      return UNLOCK;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "unlock("+left+")";
    }

  }

  /** S: readbarrier() */
  public static final class readbarrier extends snode {

    public readbarrier() { }

    public int op() {
      return READBARRIER;
    }

    public int arity() {
      return 0;
    }

    public String toString() {
      return "readbarrier()";
    }

  }

  /** S: writebarrier() */
  public static final class writebarrier extends snode {

    public writebarrier() { }

    public int op() {
      return WRITEBARRIER;
    }

    public int arity() {
      return 0;
    }

    public String toString() {
      return "writebarrier()";
    }

  }

  /** A: getclass(A) */
  public static final class getclass extends anode {

    private anode left;

    protected getclass() { }

    public getclass(anode left) {
      setLeft(left);
    }

    public int op() {
      return GETCLASS;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "getclass("+left+")";
    }

  }

  /** A: aload(A,#o,#v,#c) */
  public static final class aload extends anode {

    private anode left;
    private long offset;
    private boolean volat;
    private String symbolicType;

    protected aload() { }

    public aload(anode left, long offset, boolean volat, String symbolicType) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
      setSymbolicType(symbolicType);
    }

    public int op() {
      return ALOAD;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public String getSymbolicType() {
      return symbolicType;
    }

    public void setSymbolicType(String symbolicType) {
      if (symbolicType == null)
        throw new NullPointerException();
      this.symbolicType = symbolicType;
    }

    public boolean equals(TreeNode node) {
      return node instanceof aload
          && ((aload)node).offset == offset
          && ((aload)node).volat == volat
          && ((aload)node).symbolicType.equals(symbolicType)
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
      symbolicType = in.readUTF();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
      out.writeUTF(symbolicType);
    }

    public String toString() {
      return "aload("+left+","+Measure.toString(offset)+","+volat+",#"+symbolicType+")";
    }

  }

  /** A: aaload(A,I) */
  public static final class aaload extends anode {

    private anode left;
    private inode right;

    protected aaload() { }

    public aaload(anode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return AALOAD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "aaload("+left+","+right+")";
    }

  }

  /** A: mlookup(A,#i) */
  public static final class mlookup extends anode {

    private anode left;
    private int index;

    protected mlookup() { }

    public mlookup(anode left, int index) {
      setLeft(left);
      setDispatchIndex(index);
    }

    public int op() {
      return MLOOKUP;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public int getDispatchIndex() {
      return index;
    }

    public void setDispatchIndex(int index) {
      if (index < 0)
        throw new IllegalArgumentException();
      this.index = index;
    }

    public boolean equals(TreeNode node) {
      return node instanceof mlookup
          && ((mlookup)node).index == index
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      index = in.readInt();
      if (index < 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeInt(index);
    }

    public String toString() {
      return "mlookup("+left+",["+index+"])";
    }

  }

  /** A: imlookup(A,A,#i) */
  public static final class imlookup extends anode {

    private anode left;
    private anode right;
    private int index;

    protected imlookup() { }

    public imlookup(anode left, anode right, int index) {
      setLeft(left);
      setRight(right);
      setDispatchIndex(index);
    }

    public int op() {
      return IMLOOKUP;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((anode)right);
    }

    public void setRight(anode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    public int getDispatchIndex() {
      return index;
    }

    public void setDispatchIndex(int index) {
      if (index < 0)
        throw new IllegalArgumentException();
      this.index = index;
    }

    public boolean equals(TreeNode node) {
      return node instanceof imlookup
          && ((imlookup)node).index == index
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = anode.newFrom(in);
      index = in.readInt();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
      out.writeInt(index);
    }

    public String toString() {
      return "imlookup("+left+","+right+",["+index+"])";
    }

  }

  /** A: ause(%a) */
  public static final class ause extends anode {

    private int reg;

    protected ause() { }

    public ause(int reg) {
      setReg(reg);
    }

    public int op() {
      return AUSE;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 4)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof ause
          && ((ause)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 4)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "ause(%"+reg+")";
    }

  }

  /** A: anull() */
  public static final class anull extends anode {
    
    public anull() { }

    public int op() {
      return ANULL;
    }

    public int arity() {
      return 0;
    }

    public String toString() {
      return "anull()";
    }

  }

  /** A: aclass($c) */
  public static final class aclass extends anode {

    private String symbolicType;

    protected aclass() { }

    public aclass(String symbolicType) {
      setSymbolicType(symbolicType);
    }

    public int op() {
      return ACLASS;
    }

    public int arity() {
      return 0;
    }

    public String getSymbolicType() {
      return symbolicType;
    }

    public void setSymbolicType(String symbolicType) {
      if (symbolicType == null)
        throw new NullPointerException();
      this.symbolicType = symbolicType;
    }

    public boolean equals(TreeNode node) {
      return node instanceof aclass
          && ((aclass)node).symbolicType.equals(symbolicType)
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      symbolicType = in.readUTF();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeUTF(symbolicType);
    }

    public String toString() {
      return "aclass($"+symbolicType+")";
    }

  }

  /** A: astring($s) */
  public static final class astring extends anode {

    private String value;

    protected astring() { }

    public astring(String value) {
      setValue(value);
    }

    public int op() {
      return ASTRING;
    }

    public int arity() {
      return 0;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      if (value == null)
        throw new NullPointerException();
      this.value = value;
    }

    public boolean equals(TreeNode node) {
      return node instanceof astring
          && ((astring)node).value.equals(value)
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      value = in.readUTF();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeUTF(value);
    }

    public String toString() {
      return "astring($"+extern(value)+")";
    }

    private static String extern(String string) {
      StringBuffer sb = new StringBuffer();
      int length = string.length();
      for (int i = 0; i < length; i++) {
        char shar = string.charAt(i);
        if (' ' <= shar && shar <= '~')
          sb.append(shar);
        else {
          sb.append('\\');
          sb.append('u');
          sb.append(Integer.toHexString(shar/16/16/16));
          sb.append(Integer.toHexString(shar/16/16%16));
          sb.append(Integer.toHexString(shar/16%16));
          sb.append(Integer.toHexString(shar%16));
        }
      }
      return sb.toString();
    }

  }

  /** I: i2b(I) */
  public static final class i2b extends inode {

    private inode left;

    protected i2b() { }

    public i2b(inode left) {
      setLeft(left);
    }

    public int op() {
      return I2B;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "i2b("+left+")";
    }

  }

  /** I: i2c(I) */
  public static final class i2c extends inode {

    private inode left;

    protected i2c() { }

    public i2c(inode left) {
      setLeft(left);
    }

    public int op() {
      return I2C;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "i2c("+left+")";
    }

  }

  /** I: i2s(I) */
  public static final class i2s extends inode {

    private inode left;

    protected i2s() { }

    public i2s(inode left) {
      setLeft(left);
    }

    public int op() {
      return I2S;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "i2s("+left+")";
    }

  }

  /** I: l2i(L) */
  public static final class l2i extends inode {

    private lnode left;

    protected l2i() { }

    public l2i(lnode left) {
      setLeft(left);
    }

    public int op() {
      return L2I;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "l2i("+left+")";
    }

  }

  /** I: f2i(F) */
  public static final class f2i extends inode {

    private fnode left;

    protected f2i() { }

    public f2i(fnode left) {
      setLeft(left);
    }

    public int op() {
      return F2I;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "f2i("+left+")";
    }

  }

  /** I: d2i(D) */
  public static final class d2i extends inode {

    private dnode left;

    protected d2i() { }

    public d2i(dnode left) {
      setLeft(left);
    }

    public int op() {
      return D2I;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "d2i("+left+")";
    }

  }

  /** I: iadd(I,I) */
  public static final class iadd extends inode {

    private inode left;
    private inode right;

    protected iadd() { }

    public iadd(inode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return IADD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "iadd("+left+","+right+")";
    }

  }

  /** I: isub(I,I) */
  public static final class isub extends inode {

    private inode left;
    private inode right;

    protected isub() { }

    public isub(inode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return ISUB;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "isub("+left+","+right+")";
    }

  }

  /** I: imul(I,I) */
  public static final class imul extends inode {

    private inode left;
    private inode right;

    protected imul() { }

    public imul(inode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return IMUL;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "imul("+left+","+right+")";
    }

  }

  /** I: idiv(I,I) */
  public static final class idiv extends inode {

    private inode left;
    private inode right;

    protected idiv() { }

    public idiv(inode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return IDIV;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "idiv("+left+","+right+")";
    }

  }

  /** I: irem(I,I) */
  public static final class irem extends inode {

    private inode left;
    private inode right;

    protected irem() { }

    public irem(inode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return IREM;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "irem("+left+","+right+")";
    }

  }

  /** I: ineg(I) */
  public static final class ineg extends inode {

    private inode left;

    protected ineg() { }

    public ineg(inode left) {
      setLeft(left);
    }

    public int op() {
      return INEG;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "ineg("+left+")";
    }

  }

  /** I: ishl(I,I) */
  public static final class ishl extends inode {

    private inode left;
    private inode right;

    protected ishl() { }

    public ishl(inode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return ISHL;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "ishl("+left+","+right+")";
    }

  }

  /** I: ishr(I,I) */
  public static final class ishr extends inode {

    private inode left;
    private inode right;

    protected ishr() { }

    public ishr(inode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return ISHR;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "ishr("+left+","+right+")";
    }

  }

  /** I: iushr(I,I) */
  public static final class iushr extends inode {

    private inode left;
    private inode right;

    protected iushr() { }

    public iushr(inode left, inode right) { 
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return IUSHR;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "iushr("+left+","+right+")";
    }

  }

  /** I: iand(I,I) */
  public static final class iand extends inode {

    private inode left;
    private inode right;

    protected iand() { }

    public iand(inode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return IAND;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "iand("+left+","+right+")";
    }

  }

  /** I: ior(I,I) */
  public static final class ior extends inode {

    private inode left;
    private inode right;

    protected ior() { }

    public ior(inode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return IOR;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "ior("+left+","+right+")";
    }

  }

  /** I: ixor(I,I) */
  public static final class ixor extends inode {

    private inode left;
    private inode right;

    protected ixor() { }

    public ixor(inode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return IXOR;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "ixor("+left+","+right+")";
    }

  }

  /** I: lcmp(L,L) */
  public static final class lcmp extends inode {

    private lnode left;
    private lnode right;

    protected lcmp() { }

    public lcmp(lnode left, lnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LCMP;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "lcmp("+left+","+right+")";
    }

  }

  /** I: fcmpg(F,F) */
  public static final class fcmpg extends inode {

    private fnode left;
    private fnode right;

    protected fcmpg() { }

    public fcmpg(fnode left, fnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return FCMPG;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((fnode)right);
    }

    public void setRight(fnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
      right = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "fcmpg("+left+","+right+")";
    }

  }

  /** I: fcmpl(F,F) */
  public static final class fcmpl extends inode {

    private fnode left;
    private fnode right;

    protected fcmpl() { }

    public fcmpl(fnode left, fnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return FCMPL;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((fnode)right);
    }

    public void setRight(fnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
      right = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "fcmpl("+left+","+right+")";
    }

  }

  /** I: dcmpg(D,D) */
  public static final class dcmpg extends inode {

    private dnode left;
    private dnode right;

    protected dcmpg() { }

    public dcmpg(dnode left, dnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return DCMPG;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((dnode)right);
    }

    public void setRight(dnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
      right = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "dcmpg("+left+","+right+")";
    }

  }

  /** I: dcmpl(D,D) */
  public static final class dcmpl extends inode {

    private dnode left;
    private dnode right;

    protected dcmpl() { }

    public dcmpl(dnode left, dnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return DCMPL;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((dnode)right);
    }

    public void setRight(dnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
      right = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "dcmpl("+left+","+right+")";
    }

  }

  /** I: length(A) */
  public static final class length extends inode {

    private anode left;

    protected length() { }

    public length(anode left) {
      setLeft(left);
    }

    public int op() {
      return LENGTH;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "length("+left+")";
    }

  }

  /** I: bload(A,#o,#v) */
  public static final class bload extends inode {

    private anode left;
    private long offset;
    private boolean volat;

    protected bload() { }

    public bload(anode left, long offset, boolean volat) { 
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
    }

    public int op() {
      return BLOAD;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public boolean equals(TreeNode node) {
      return node instanceof bload
          && ((bload)node).offset == offset
          && ((bload)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
    }

    public String toString() {
      return "bload("+left+","+Measure.toString(offset)+","+volat+")";
    }

  }

  /** I: sload(A,#o,#v) */
  public static final class sload extends inode {

    private anode left;
    private long offset;
    private boolean volat;

    protected sload() { }

    public sload(anode left, long offset, boolean volat) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
    }

    public int op() {
      return SLOAD;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public boolean equals(TreeNode node) {
      return node instanceof sload
          && ((sload)node).offset == offset
          && ((sload)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
    }

    public String toString() {
      return "sload("+left+","+Measure.toString(offset)+","+volat+")";
    }

  }

  /** I: iload(A,#o,#v) */
  public static final class iload extends inode {

    private anode left;
    private long offset;
    private boolean volat;

    protected iload() { }

    public iload(anode left, long offset, boolean volat) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
    }

    public int op() {
      return ILOAD;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public boolean equals(TreeNode node) {
      return node instanceof iload
          && ((iload)node).offset == offset
          && ((iload)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
    }

    public String toString() {
      return "iload("+left+","+Measure.toString(offset)+","+volat+")";
    }

  }

  /** I: baload(A,I) */
  public static final class baload extends inode {

    private anode left;
    private inode right;

    protected baload() { }

    public baload(anode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return BALOAD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "baload("+left+","+right+")";
    }

  }

  /** I: saload(A,I) */
  public static final class saload extends inode {

    private anode left;
    private inode right;

    protected saload() { }

    public saload(anode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return SALOAD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "saload("+left+","+right+")";
    }

  }

  /** I: iaload(A,I) */
  public static final class iaload extends inode {

    private anode left;
    private inode right;

    protected iaload() { }

    public iaload(anode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return IALOAD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "iaload("+left+","+right+")";
    }

  }

  /** I: islocked(A) */
  public static final class islocked extends inode {

    private anode left;

    protected islocked() { }

    public islocked(anode left) {
      setLeft(left);
    }

    public int op() {
      return ISLOCKED;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "islocked("+left+")";
    }

  }
  
  /** I: subtypeof(A,A) */
  public static final class subtypeof extends inode {

    private anode left;
    private anode right;

    protected subtypeof() { }

    public subtypeof(anode left, anode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return SUBTYPEOF;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((anode)right);
    }

    public void setRight(anode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "subtypeof("+left+","+right+")";
    }

  }

  /** I: comptypeof(A,A) */
  public static final class comptypeof extends inode {

    private anode left;
    private anode right;

    protected comptypeof() { }
    
    public comptypeof(anode left, anode right) {
      setLeft(left);
      setRight(right);
    }
    
    public int op() {
      return COMPTYPEOF;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((anode)right);
    }

    public void setRight(anode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = anode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "comptypeof("+left+","+right+")";
    }

  }

  /** I: iuse(%i) */
  public static final class iuse extends inode {

    private int reg;

    protected iuse() { }

    public iuse(int reg) {
      setReg(reg);
    }

    public int op() {
      return IUSE;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 0)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof iuse
          && ((iuse)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 0)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "iuse(%"+reg+")";
    }

  }

  /** I: iconst($i) */
  public static final class iconst extends inode {

    private int value;

    protected iconst() { }

    public iconst(int value) {
      setValue(value);
    }

    public int op() {
      return ICONST;
    }

    public int arity() {
      return 0;
    }

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }

    public boolean equals(TreeNode node) {
      return node instanceof iconst
          && ((iconst)node).value == value
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      value = in.readInt();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(value);
    }

    public String toString() {
      return "iconst($"+value+")";
    }

  }

  /** L: i2l(I) */
  public static final class i2l extends lnode {

    private inode left;

    protected i2l() { }

    public i2l(inode left) {
      setLeft(left);
    }

    public int op() {
      return I2L;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "i2l("+left+")";
    }

  }

  /** L: f2l(F) */
  public static final class f2l extends lnode {

    private fnode left;

    protected f2l() { }

    public f2l(fnode left) {
      setLeft(left);
    }

    public int op() {
      return F2L;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "f2l("+left+")";
    }

  }

  /** L: d2l(D) */
  public static final class d2l extends lnode {

    private dnode left;

    protected d2l() { }

    public d2l(dnode left) {
      setLeft(left);
    }

    public int op() {
      return D2L;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "d2l("+left+")";
    }

  }

  /** L: ladd(L,L) */
  public static final class ladd extends lnode {

    private lnode left;
    private lnode right;

    protected ladd() { }

    public ladd(lnode left, lnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LADD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "ladd("+left+","+right+")";
    }

  }

  /** L: lsub(L,L) */
  public static final class lsub extends lnode {

    private lnode left;
    private lnode right;

    protected lsub() { }

    public lsub(lnode left, lnode right) { 
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LSUB;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "lsub("+left+","+right+")";
    }

  }

  /** L: lmul(L,L) */
  public static final class lmul extends lnode {

    private lnode left;
    private lnode right;

    protected lmul() { }

    public lmul(lnode left, lnode right) { 
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LMUL;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "lmul("+left+","+right+")";
    }

  }

  /** L: ldiv(L,L) */
  public static final class ldiv extends lnode {

    private lnode left;
    private lnode right;

    protected ldiv() { }

    public ldiv(lnode left, lnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LDIV;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "ldiv("+left+","+right+")";
    }

  }

  /** L: lrem(L,L) */
  public static final class lrem extends lnode {

    private lnode left;
    private lnode right;

    protected lrem() { }

    public lrem(lnode left, lnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LREM;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "lrem("+left+","+right+")";
    }

  }

  /** L: lneg(L) */
  public static final class lneg extends lnode {

    private lnode left;

    protected lneg() { }

    public lneg(lnode left) {
      setLeft(left);
    }

    public int op() {
      return LNEG;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "lneg("+left+")";
    }

  }

  /** L: lshl(L,I) */
  public static final class lshl extends lnode {

    private lnode left;
    private inode right;

    protected lshl() { }

    public lshl(lnode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LSHL;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "lshl("+left+","+right+")";
    }

  }

  /** L: lshr(L,I) */
  public static final class lshr extends lnode {

    private lnode left;
    private inode right;

    protected lshr() { }

    public lshr(lnode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LSHR;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "lshr("+left+","+right+")";
    }

  }

  /** L: lushr(L,I) */
  public static final class lushr extends lnode {

    private lnode left;
    private inode right;

    protected lushr() { }

    public lushr(lnode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LUSHR;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "lushr("+left+","+right+")";
    }

  }

  /** L: land(L,L) */
  public static final class land extends lnode {

    private lnode left;
    private lnode right;

    protected land() { }

    public land(lnode left, lnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LAND;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "land("+left+","+right+")";
    }

  }

  /** L: lor(L,L) */
  public static final class lor extends lnode {

    private lnode left;
    private lnode right;

    protected lor() { }

    public lor(lnode left, lnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LOR;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "lor("+left+","+right+")";
    }

  }

  /** L: lxor(L,L) */
  public static final class lxor extends lnode {

    private lnode left;
    private lnode right;

    protected lxor() { }

    public lxor(lnode left, lnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LXOR;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((lnode)right);
    }

    public void setRight(lnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
      right = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "lxor("+left+","+right+")";
    }

  }

  /** L: lload(A,#o,#v) */
  public static final class lload extends lnode {

    private anode left;
    private long offset;
    private boolean volat;

    protected lload() { }

    public lload(anode left, long offset, boolean volat) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
    }

    public int op() {
      return LLOAD;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public boolean equals(TreeNode node) {
      return node instanceof lload
          && ((lload)node).offset == offset
          && ((lload)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
    }

    public String toString() {
      return "lload("+left+","+Measure.toString(offset)+","+volat+")";
    }

  }

  /** L: laload(A,I) */
  public static final class laload extends lnode {

    private anode left;
    private inode right;

    protected laload() { }

    public laload(anode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return LALOAD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "laload("+left+","+right+")";
    }

  }

  /** L: luse(%l) */
  public static final class luse extends lnode {

    private int reg;

    protected luse() { }

    public luse(int reg) {
      setReg(reg);
    }

    public int op() {
      return LUSE;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 1)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof luse
          && ((luse)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 1)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "luse(%"+reg+")";
    }

  }

  /** L: lconst($l) */
  public static final class lconst extends lnode {

    private long value;

    protected lconst() { }

    public lconst(long value) {
      setValue(value);
    }

    public int op() {
      return LCONST;
    }

    public int arity() {
      return 0;
    }

    public long getValue() {
      return value;
    }

    public void setValue(long value) {
      this.value = value;
    }

    public boolean equals(TreeNode node) {
      return node instanceof lconst
          && ((lconst)node).value == value
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      value = in.readLong();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeLong(value);
    }

    public String toString() {
      return "lconst($"+value+")";
    }

  }

  /** F: i2f(I) */
  public static final class i2f extends fnode {

    private inode left;

    protected i2f() { }

    public i2f(inode left) {
      setLeft(left);
    }

    public int op() {
      return I2F;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "i2f("+left+")";
    }

  }

  /** F: l2f(L) */
  public static final class l2f extends fnode {

    private lnode left;

    protected l2f() { }

    public l2f(lnode left) {
      setLeft(left);
    }

    public int op() {
      return L2F;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "l2f("+left+")";
    }

  }

  /** F: d2f(D) */
  public static final class d2f extends fnode {

    private dnode left;

    protected d2f() { }

    public d2f(dnode left) {
      setLeft(left);
    }

    public int op() {
      return D2F;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "d2f("+left+")";
    }

  }

  /** F: fstrict(F) */
  public static final class fstrict extends fnode {

    private fnode left;

    protected fstrict() { }

    public fstrict(fnode left) {
      setLeft(left);
    }

    public int op() {
      return FSTRICT;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "fstrict("+left+")";
    }

  }

  /** F: fadd(F,F) */
  public static final class fadd extends fnode {

    private fnode left;
    private fnode right;

    protected fadd() { }

    public fadd(fnode left, fnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return FADD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((fnode)right);
    }

    public void setRight(fnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
      right = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "fadd("+left+","+right+")";
    }

  }

  /** F: fsub(F,F) */
  public static final class fsub extends fnode {

    private fnode left;
    private fnode right;

    protected fsub() { }

    public fsub(fnode left, fnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return FSUB;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((fnode)right);
    }

    public void setRight(fnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
      right = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "fsub("+left+","+right+")";
    }

  }

  /** F: fmul(F,F) */
  public static final class fmul extends fnode {

    private fnode left;
    private fnode right;

    protected fmul() { }

    public fmul(fnode left, fnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return FMUL;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((fnode)right);
    }

    public void setRight(fnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
      right = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "fmul("+left+","+right+")";
    }

  }

  /** F: fdiv(F,F) */
  public static final class fdiv extends fnode {

    private fnode left;
    private fnode right;

    protected fdiv() { }

    public fdiv(fnode left, fnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return FDIV;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((fnode)right);
    }

    public void setRight(fnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
      right = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "fdiv("+left+","+right+")";
    }

  }

  /** F: frem(F,F) */
  public static final class frem extends fnode {

    private fnode left;
    private fnode right;

    protected frem() { }

    public frem(fnode left, fnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return FREM;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((fnode)right);
    }

    public void setRight(fnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
      right = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "frem("+left+","+right+")";
    }

  }

  /** F: fneg(F) */
  public static final class fneg extends fnode {

    private fnode left;

    protected fneg() { }

    public fneg(fnode left) {
      setLeft(left);
    }

    public int op() {
      return FNEG;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "fneg("+left+")";
    }

  }

  /** F: fload(A,#o,#v) */
  public static final class fload extends fnode {

    private anode left;
    private long offset;
    private boolean volat;

    protected fload() { }

    public fload(anode left, long offset, boolean volat) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
    }

    public int op() {
      return FLOAD;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public boolean equals(TreeNode node) {
      return node instanceof fload
          && ((fload)node).offset == offset
          && ((fload)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
    }

    public String toString() {
      return "fload("+left+","+Measure.toString(offset)+","+volat+")";
    }

  }

  /** F: faload(A,I) */
  public static final class faload extends fnode {

    private anode left;
    private inode right;

    protected faload() { }

    public faload(anode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return FALOAD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "faload("+left+","+right+")";
    }

  }

  /** F: fuse(%f) */
  public static final class fuse extends fnode {

    private int reg;

    protected fuse() { }

    public fuse(int reg) {
      setReg(reg);
    }

    public int op() {
      return FUSE;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 2)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof fuse
          && ((fuse)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 2)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "fuse(%"+reg+")";
    }

  }

  /** F: fconst($f) */
  public static final class fconst extends fnode {

    private float value;

    protected fconst() { }

    public fconst(float value) {
      setValue(value);
    }

    public int op() {
      return FCONST;
    }

    public int arity() {
      return 0;
    }

    public float getValue() {
      return value;
    }

    public void setValue(float value) {
      this.value = value;
    }

    public boolean equals(TreeNode node) {
      return node instanceof fconst
          && Float.floatToIntBits(((fconst)node).value) == Float.floatToIntBits(value)
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      value = in.readFloat();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeFloat(value);
    }

    public String toString() {
      return "fconst($"+value+")";
    }

  }

  /** D: i2d(I) */
  public static final class i2d extends dnode {

    private inode left;

    protected i2d() { }

    public i2d(inode left) {
      setLeft(left);
    }

    public int op() {
      return I2D;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((inode)left);
    }

    public void setLeft(inode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "i2d("+left+")";
    }

  }

  /** D: l2d(L) */
  public static final class l2d extends dnode {

    private lnode left;

    protected l2d() { }

    public l2d(lnode left) {
      setLeft(left);
    }

    public int op() {
      return L2D;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((lnode)left);
    }

    public void setLeft(lnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = lnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "l2d("+left+")";
    }

  }

  /** D: f2d(F) */
  public static final class f2d extends dnode {

    private fnode left;

    protected f2d() { }

    public f2d(fnode left) {
      setLeft(left);
    }

    public int op() {
      return F2D;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((fnode)left);
    }

    public void setLeft(fnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = fnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "f2d("+left+")";
    }

  }

  /** D: dstrict(D) */
  public static final class dstrict extends dnode {

    private dnode left;

    protected dstrict() { }

    public dstrict(dnode left) {
      setLeft(left);
    }

    public int op() {
      return DSTRICT;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "dstrict("+left+")";
    }

  }

  /** D: dadd(D,D) */
  public static final class dadd extends dnode {

    private dnode left;
    private dnode right;

    protected dadd() { }

    public dadd(dnode left, dnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return DADD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((dnode)right);
    }

    public void setRight(dnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
      right = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "dadd("+left+","+right+")";
    }

  }

  /** D: dsub(D,D) */
  public static final class dsub extends dnode {

    private dnode left;
    private dnode right;

    protected dsub() { }

    public dsub(dnode left, dnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return DSUB;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((dnode)right);
    }

    public void setRight(dnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
      right = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "dsub("+left+","+right+")";
    }

  }

  /** D: dmul(D,D) */
  public static final class dmul extends dnode {

    private dnode left;
    private dnode right;

    protected dmul() { }

    public dmul(dnode left, dnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return DMUL;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((dnode)right);
    }

    public void setRight(dnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
      right = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "dmul("+left+","+right+")";
    }

  }

  /** D: ddiv(D,D) */
  public static final class ddiv extends dnode {

    private dnode left;
    private dnode right;

    protected ddiv() { }

    public ddiv(dnode left, dnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return DDIV;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((dnode)right);
    }

    public void setRight(dnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
      right = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "ddiv("+left+","+right+")";
    }

  }

  /** D: drem(D,D) */              
  public static final class drem extends dnode {

    private dnode left;
    private dnode right;

    protected drem() { }

    public drem(dnode left, dnode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return DREM;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((dnode)right);
    }

    public void setRight(dnode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
      right = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "drem("+left+","+right+")";
    }

  }

  /** D: dneg(D) */
  public static final class dneg extends dnode {

    private dnode left;

    protected dneg() { }

    public dneg(dnode left) { 
      setLeft(left);
    }

    public int op() {
      return DNEG;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((dnode)left);
    }

    public void setLeft(dnode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = dnode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
    }

    public String toString() {
      return "dneg("+left+")";
    }

  }

  /** D: dload(A,#o,#v) */
  public static final class dload extends dnode {

    private anode left;
    private long offset;
    private boolean volat;

    protected dload() { }

    public dload(anode left, long offset, boolean volat) {
      setLeft(left);
      setOffset(offset);
      setVolatile(volat);
    }

    public int op() {
      return DLOAD;
    }

    public int arity() {
      return 1;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public boolean isVolatile() {
      return volat;
    }

    public void setVolatile(boolean volat) {
      this.volat = volat;
    }

    public boolean equals(TreeNode node) {
      return node instanceof dload
          && ((dload)node).offset == offset
          && ((dload)node).volat == volat
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      volat = in.readBoolean();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      out.writeLong(offset);
      out.writeBoolean(volat);
    }

    public String toString() {
      return "dload("+left+","+Measure.toString(offset)+","+volat+")";
    }

  }

  /** D: daload(A,I) */
  public static final class daload extends dnode {

    private anode left;
    private inode right;

    protected daload() { }

    public daload(anode left, inode right) {
      setLeft(left);
      setRight(right);
    }

    public int op() {
      return DALOAD;
    }

    public int arity() {
      return 2;
    }

    public TreeNode left() {
      return left;
    }

    public void setLeft(TreeNode left) {
      setLeft((anode)left);
    }

    public void setLeft(anode left) {
      if (left == null)
        throw new NullPointerException();
      this.left = left;
    }

    public TreeNode right() {
      return right;
    }

    public void setRight(TreeNode right) {
      setRight((inode)right);
    }

    public void setRight(inode right) {
      if (right == null)
        throw new NullPointerException();
      this.right = right;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      left = anode.newFrom(in);
      right = inode.newFrom(in);
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      left.writeTo(out);
      right.writeTo(out);
    }

    public String toString() {
      return "daload("+left+","+right+")";
    }

  }

  /** D: duse(%d) */
  public static final class duse extends dnode {

    private int reg;

    protected duse() { }

    public duse(int dreg) {
      setReg(dreg);
    }

    public int op() {
      return DUSE;
    }

    public int arity() {
      return 0;
    }

    public int getReg() {
      return reg;
    }

    public void setReg(int reg) {
      if (reg < 0 || reg % 5 != 3)
        throw new IllegalArgumentException();
      this.reg = reg;
    }

    public boolean equals(TreeNode node) {
      return node instanceof duse
          && ((duse)node).reg == reg
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      reg = in.readInt();
      if (reg < 0 || reg % 5 != 3)
        throw new IOException();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeInt(reg);
    }

    public String toString() {
      return "duse(%"+reg+")";
    }

  }

  /** D: dconst($d) */
  public static final class dconst extends dnode {

    private double value;

    protected dconst() { }

    public dconst(double value) {
      setValue(value);
    }

    public int op() {
      return DCONST;
    }

    public int arity() {
      return 0;
    }

    public double getValue() {
      return value;
    }

    public void setValue(double value) {
      this.value = value;
    }

    public boolean equals(TreeNode node) {
      return node instanceof dconst
          && Double.doubleToLongBits(((dconst)node).value) == Double.doubleToLongBits(value)
          && super.equals(node);
    }

    protected void readFrom(DataInputStream in) throws IOException {
      super.readFrom(in);
      value = in.readDouble();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      super.writeTo(out);
      out.writeDouble(value);
    }

    public String toString() {
      return "dconst($"+value+")";
    }

  }

  private IR() { }

}

