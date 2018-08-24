/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.cache.CacheObject;
import jewel.core.clfile.ClassFile;
import jewel.core.clfile.FilterClassInfo;
import jewel.core.clfile.ClassFormatException;
import jewel.core.clfile.NoClassDefFoundException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

final class RegisteredClass extends FilterClassInfo implements CacheObject, RegisteredClassInfo {

  static final class Key extends ClassID {

    public Key(String name, long version) {
      super(name, version, "class");
    }

    public CacheObject newInstance() {
      return new RegisteredClass(this);
    }

  }

  static final class Params {

    public final String name;
    public final byte[] buffer;
    public final int start;
    public final int length;

    public Params(String name, byte[] buffer, int start, int length) {
      this.name = name;
      this.buffer = buffer;
      this.start = start;
      this.length = length;
    }

  }

  private final Key key;

  protected RegisteredClass(Key key) {
    this.key = key;
  }

  public long getRegisteredVersion() {
    return key.getVersion();
  }

  public void construct(Object cacheParams) throws ClassFormatException, NoClassDefFoundException {
    String name = ((Params)cacheParams).name;
    byte[] buffer = ((Params)cacheParams).buffer;
    int start = ((Params)cacheParams).start;
    int length = ((Params)cacheParams).length;

    ClassFile classFile = ClassFile.parseImage(buffer, start, length);
    String cfname = classFile.getName();
    if (!cfname.equals(name))
      throw new NoClassDefFoundException("Wrong name: "+cfname);

    underlying = classFile;
  }

  public void readFrom(InputStream is) throws IOException {
    DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
    try {
      underlying = new ClassFile(in);
    } catch (ClassFormatException e) {
      throw new IOException(e.getMessage());
    }
  }

  public void writeTo(OutputStream os) throws IOException {
    DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
    ClassFile classFile = (ClassFile)underlying;
    classFile.writeTo(out);
    out.flush();
  }

}

