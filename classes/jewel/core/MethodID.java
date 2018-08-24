/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

abstract class MethodID extends ClassID {

  private char index;
  private byte level;

  protected MethodID() { }

  protected MethodID(String name, long version, int index, String extension) {
    this(name, version, index, -1, extension);
  }

  protected MethodID(String name, long version, int index, int level, String extension) {
    this(name, version, index, level, extension, null);
  }

  protected MethodID(String name, long version, int index, int level, String extension, String backend) {
    super(name, version, extension, backend);
    if (index < 0 || index >= 65535)
      throw new IllegalArgumentException();
    if (level < -1 || level >= 255)
      throw new IllegalArgumentException();
    this.index = (char)index;
    this.level = (byte)level;
  }

  public final int getIndex() {
    return index;
  }

  public final int getLevel() {
    int level = this.level & 0xFF;
    return level == 255 ? -1 : level;
  }

  public String getEntry() {
    String entry = super.getEntry();
    int index = entry.lastIndexOf('.');
    int level = getLevel();
    return entry.substring(0, index)+"["+getIndex()+"]."+(level == -1 ? "" : level+".")+entry.substring(index+1);
  }

  public int hashCode() {
    return level + index * super.hashCode();
  }

  public boolean equals(Object object) {
    return object instanceof MethodID
        && level == ((MethodID)object).level
        && index == ((MethodID)object).index
        && super.equals(object);
  }

  public void readFrom(InputStream is) throws IOException {
    DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
    super.readFrom(in);
    index = in.readChar();
    level = in.readByte();
  }

  public void writeTo(OutputStream os) throws IOException {
    DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
    super.writeTo(out);
    out.writeChar(index);
    out.writeByte(level);
    out.flush();
  }

}

