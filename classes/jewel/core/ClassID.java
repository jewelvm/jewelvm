/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.cache.CacheKey;
import jewel.core.cache.Streamable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

abstract class ClassID implements CacheKey, Streamable {

  private String name;
  private long version;
  private String extension;
  private String backend;

  protected ClassID() { }

  protected ClassID(String name, long version, String extension) {
    this(name, version, extension, null);
  }

  protected ClassID(String name, long version, String extension, String backend) {
    if (name == null || extension == null)
      throw new NullPointerException();
    this.name = name;
    this.version = version;
    this.extension = extension;
    this.backend = backend;
  }

  public final String getName() {
    return name;
  }

  public final long getVersion() {
    return version;
  }

  public final String getExtension() {
    return extension;
  }

  public final String getBackend() {
    return backend;
  }

  public String getEntry() {
    String classDir = name;
    int sindex = classDir.lastIndexOf('[');
    int lindex = classDir.lastIndexOf('/');
    if (sindex != -1 && lindex != -1)
      classDir = classDir.substring(sindex+2, lindex+1)+classDir.substring(0, sindex+2)+classDir.substring(lindex+1);
    if (backend != null) {
      lindex = classDir.lastIndexOf('/');
      classDir = classDir.substring(0, lindex+1)+backend+".arch/"+classDir.substring(lindex+1);
    }
    String versionFile = Long.toHexString(version);
    for (int i = versionFile.length(); i < 16; i++)
      versionFile = "0"+versionFile;
    return classDir+"#"+versionFile+"."+extension;
  }

  public int hashCode() {
    return (int)version ^ (int)(version >> 32) ^ extension.hashCode();
  }

  public boolean equals(Object object) {
    return object instanceof ClassID
        && version == ((ClassID)object).version
        && name.equals(((ClassID)object).name)
        && extension.equals(((ClassID)object).extension)
        && (backend == ((ClassID)object).backend
        || (backend != null && backend.equals(((ClassID)object).backend)));
  }

  public void readFrom(InputStream is) throws IOException {
    DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
    name = in.readUTF();
    version = in.readLong();
    extension = in.readUTF();
    boolean nonnull = in.readBoolean();
    if (nonnull)
      backend = in.readUTF();
  }

  public void writeTo(OutputStream os) throws IOException {
    DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
    out.writeUTF(name);
    out.writeLong(version);
    out.writeUTF(extension);
    boolean nonnull = backend != null;
    out.writeBoolean(nonnull);
    if (nonnull)
      out.writeUTF(backend);
    out.flush();
  }

}

