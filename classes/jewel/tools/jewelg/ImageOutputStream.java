/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jewelg;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class ImageOutputStream extends FilterOutputStream {

  protected ImageOutputStream(OutputStream out) {
    super(out);
  }

  public abstract int wordSize();
  public abstract boolean littleEndian();

  public abstract void globl(String label) throws IOException;
  public abstract void extern(String label) throws IOException;
  public abstract void label(String label) throws IOException;
  public abstract void align() throws IOException;
  public abstract void space(int size) throws IOException;
  public abstract void writeByte(int bite) throws IOException;
  public abstract void writeShort(int shirt) throws IOException;
  public abstract void writeInt(int ant) throws IOException;
  public abstract void writeLong(long lung) throws IOException;
  public abstract void writeWord(int word) throws IOException;
  public abstract void writeWord(String label, int offset) throws IOException;
  public abstract void writeWord(String label, String negLabel, int offset) throws IOException;

  public final void writeWord(String label) throws IOException {
    writeWord(label, 0);
  }

  public final void writeWord(String label, String negLabel) throws IOException {
    writeWord(label, negLabel, 0);
  }

  public final void writeUTF(String string) throws IOException {
    int length = string.length();
    for (int i = 0; i < length; i++) {
      int shar = string.charAt(i);
      if (shar >= 0x0001 && shar <= 0x007F)
        writeByte(shar);
      else {
        if (shar <= 0x07FF)
          writeByte(((shar >>  6)&0x1F)|0xC0);
        else {
          writeByte(((shar >> 12)&0x0F)|0xE0);
          writeByte(((shar >>  6)&0x3F)|0x80);
        }
        writeByte((shar&0x3F)|0x80);
      }
    }
    writeByte(0);
  }

}

