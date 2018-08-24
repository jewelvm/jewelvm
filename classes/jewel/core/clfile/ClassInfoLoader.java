/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.clfile;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

// add support for loaders, no borrow IllegalAccessException, and primitive classes later
public abstract class ClassInfoLoader implements ClassFileConstants {

  private final HashMap classes = new HashMap();
  private final ArrayList loading = new ArrayList();

  protected ClassInfoLoader() { }

  protected final ClassInfo getClass(String name) {
    return (ClassInfo)classes.get(name);
  }

  protected final void putClass(String name, ClassInfo clazz) {
    classes.put(name, clazz);
  }

  protected byte[] findClassDef(String name) throws NoClassDefFoundException {
    throw new NoClassDefFoundException(name);
  }

  public final ClassInfo loadClass(String name) throws LinkageException, IllegalAccessException {
    ClassInfo clazz = getClass(name);
    if (clazz == null) {
      byte[] buffer;
      int dims = name.lastIndexOf('[')+1;
      if (dims == 0)
        buffer = findClassDef(name);
      else {
        boolean isPublic = true;
        if (name.charAt(dims) == 'L') {
          String ename = name.substring(dims+1, name.length()-1);
          ClassInfo eclazz = loadClass(ename);
          isPublic = eclazz.isPublic();
        }
        buffer = createArrayImage(name, isPublic);
      }
      clazz = defineClass(name, buffer, 0, buffer.length);
    }
    return clazz;
  }

  public final ClassInfo defineClass(String name, byte[] buffer, int start, int length) throws LinkageException, IllegalAccessException {
    ClassInfo clazz = getClass(name);
    if (clazz != null)
      throw new LinkageException(name);
    if (loading.contains(name))
      throw new ClassCircularityException(name);
    loading.add(name);
    try {
      clazz = deriveClass(name, buffer, start, length);
    } finally {
      loading.remove(loading.size()-1);
    }
    putClass(name, clazz);
    return clazz;
  }

  protected ClassInfo deriveClass(String name, byte[] buffer, int start, int length) throws LinkageException, IllegalAccessException {
    ClassFile clfile = ClassFile.parseImage(buffer, start, length);
    String cfname = clfile.getName();
    if (!name.equals(cfname))
      throw new NoClassDefFoundException("Wrong name: "+cfname);

    String superclass = clfile.getSuperclass();
    ClassInfo sclazz = null;
    if (superclass != null) {
      sclazz = loadClass(superclass);
      if (!sclazz.isPublic())
        if (!Syntax.getPackage(superclass).equals(Syntax.getPackage(name)))
          throw new IllegalAccessException("Cannot access superclass "+superclass+" from class "+name);
      if (sclazz.isInterface())
        throw new IncompatibleClassChangeException("Class "+name+" has interface "+superclass+" as superclass");
    }

    String[] interfaces = clfile.getInterfaces();
    ClassInfo[] iclazz = new ClassInfo[interfaces.length];
    for (int i = 0; i < interfaces.length; i++) {
      iclazz[i] = loadClass(interfaces[i]);
      if (!iclazz[i].isPublic())
        if (!Syntax.getPackage(interfaces[i]).equals(Syntax.getPackage(name)))
          throw new IllegalAccessException("Cannot access interface "+interfaces[i]+" from class "+name);
      if (!iclazz[i].isInterface())
        throw new IncompatibleClassChangeException("Class "+name+" has class "+interfaces[i]+" as interface");
    }

    return newClassInfo(clfile, sclazz, iclazz);
  }
  
  protected ClassInfo newClassInfo(ClassInfo underlying, ClassInfo superclass, ClassInfo[] interfaces) {
    throw new UnsupportedOperationException();
  }

  private byte[] createArrayImage(String name, boolean isPublic) {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bout);
    try {
      out.writeInt(MAGIC);
      out.writeChar(DEFAULT_MINOR_VERSION);
      out.writeChar(DEFAULT_MAJOR_VERSION);
      out.writeChar(9);
      out.writeByte(CONSTANT_CLASS);
      out.writeChar(5);
      out.writeByte(CONSTANT_CLASS);
      out.writeChar(6);
      out.writeByte(CONSTANT_CLASS);
      out.writeChar(7);
      out.writeByte(CONSTANT_CLASS);
      out.writeChar(8);
      out.writeByte(CONSTANT_UTF8);
      out.writeUTF(name);
      out.writeByte(CONSTANT_UTF8);
      out.writeUTF("java/lang/Object");
      out.writeByte(CONSTANT_UTF8);
      out.writeUTF("java/lang/Cloneable");
      out.writeByte(CONSTANT_UTF8);
      out.writeUTF("java/io/Serializable");
      out.writeChar(ACC_FINAL|ACC_ABSTRACT|ACC_SUPER|(isPublic ? ACC_PUBLIC : 0));
      out.writeChar(1);
      out.writeChar(2);
      out.writeChar(2);
      out.writeChar(3);
      out.writeChar(4);
      out.writeChar(0);
      out.writeChar(0);
      out.writeChar(0);
      out.flush();
    } catch (IOException e) {
      throw new InternalError(e.getMessage());
    }
    return bout.toByteArray();
  }

}

