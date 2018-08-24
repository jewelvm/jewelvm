/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jewelg;

import java.io.OutputStream;
import java.io.PrintStream;

// GNU As i386 tailored
public class AssemblyImageOutputStream extends ImageOutputStream {

  private final boolean underscore;

  public AssemblyImageOutputStream(OutputStream out) {
    this(out, false);
  }

  public AssemblyImageOutputStream(OutputStream out, boolean underscore) {
    super(out instanceof PrintStream ? (PrintStream)out : new PrintStream(out));
    this.underscore = underscore;
    ((PrintStream)this.out).println("\t.data");
  }

  public int wordSize() {
    return 4;
  }

  public boolean littleEndian() {
    return true;
  }

  public void globl(String label) {
    PrintStream out = (PrintStream)this.out;
    if (underscore)
      label = " _"+label;
    out.println("\t.globl "+label);
  }

  public void extern(String label) {
    PrintStream out = (PrintStream)this.out;
    if (underscore)
      label = "_"+label;
    out.println("\t.extern "+label);
  }

  public void label(String label) {
    PrintStream out = (PrintStream)this.out;
    if (underscore)
      label = "_"+label;
    out.println(label+":");
  }

  public void align() {
    PrintStream out = (PrintStream)this.out;
    out.println("\t.align "+wordSize());
  }
  
  public void space(int size) {
    PrintStream out = (PrintStream)this.out;
    out.println("\t.space "+size);
  }

  public void writeByte(int bite) {
    PrintStream out = (PrintStream)this.out;
    out.println("\t.byte "+bite);
  }

  public void writeShort(int shirt) {
    PrintStream out = (PrintStream)this.out;
    out.println("\t.short "+shirt);
  }

  public void writeInt(int ant) {
    PrintStream out = (PrintStream)this.out;
    out.println("\t.long "+ant);
  }

  public void writeLong(long lung) {
    PrintStream out = (PrintStream)this.out;
    out.println("\t.long "+(int)lung);
    out.println("\t.long "+(int)(lung >> 32));
  }

  public void writeWord(int word) {
    PrintStream out = (PrintStream)this.out;
    out.println("\t.long "+word);
  }

  public void writeWord(String label, int offset) {
    PrintStream out = (PrintStream)this.out;
    if (underscore)
      label = "_"+label;
    out.print("\t.long "+label);
    if (offset >= 0)
      out.print("+");
    out.println(offset);
  }

  public void writeWord(String label, String negLabel, int offset) {
    PrintStream out = (PrintStream)this.out;
    if (underscore) {
      label = "_"+label;
      negLabel = "_"+negLabel;
    }
    out.print("\t.long "+label+"-"+negLabel);
    if (offset >= 0)
      out.print("+");
    out.println(offset);
  }

}

