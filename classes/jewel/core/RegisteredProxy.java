/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.RegisteredClass.Key;
import jewel.core.cache.CacheKey;
import jewel.core.cache.ProxyObject;

class RegisteredProxy extends ProxyObject implements RegisteredClassInfo {

  protected RegisteredProxy(CacheKey cacheKey) {
    super(cacheKey);
  }

  public RegisteredProxy(String name, long version) {
    this(new Key(name, version));
  }

  protected final RegisteredClassInfo registeredInfo() {
    return (RegisteredClassInfo)get();
  }

  public final ConstantPool getConstantPool() { return registeredInfo().getConstantPool(); }
  public final boolean isPublic() { return registeredInfo().isPublic(); }
  public final boolean isPackagePrivate() { return registeredInfo().isPackagePrivate(); }
  public final boolean isFinal() { return registeredInfo().isFinal(); }
  public final boolean isSuper() { return registeredInfo().isSuper(); }
  public final boolean isInterface() { return registeredInfo().isInterface(); }
  public final boolean isAbstract() { return registeredInfo().isAbstract(); }
  public final int getAccessFlags() { return registeredInfo().getAccessFlags(); }

  public final String getName() {
    ClassID key = (ClassID)cacheKey();
    return key.getName();
  }

  public final String getSuperclass() { return registeredInfo().getSuperclass(); }
  public final int getInterfaceCount() { return registeredInfo().getInterfaceCount(); }
  public final String getInterface(int index) { return registeredInfo().getInterface(index); }
  public final String[] getInterfaces() { return registeredInfo().getInterfaces(); }
  public final int getFieldCount() { return registeredInfo().getFieldCount(); }
  public final FieldInfo getField(String name, String descriptor) { return registeredInfo().getField(name, descriptor); }
  public final FieldInfo getField(int index) { return registeredInfo().getField(index); }
  public final FieldInfo[] getFields() { return registeredInfo().getFields(); }
  public final int getMethodCount() { return registeredInfo().getMethodCount(); }
  public final MethodInfo getMethod(String name, String descriptor) { return registeredInfo().getMethod(name, descriptor); }
  public final MethodInfo getMethod(int index) { return registeredInfo().getMethod(index); }
  public final MethodInfo[] getMethods() { return registeredInfo().getMethods(); }
  public final int getInnerClassCount() { return registeredInfo().getInnerClassCount(); }
  public final InnerInfo getInnerClass(int index) { return registeredInfo().getInnerClass(index); }
  public final InnerInfo[] getInnerClasses() { return registeredInfo().getInnerClasses(); }
  public final String getSourceFile() { return registeredInfo().getSourceFile(); }
  public final boolean isSynthetic() { return registeredInfo().isSynthetic(); }
  public final boolean isDeprecated() { return registeredInfo().isDeprecated(); }

  public long getRegisteredVersion() {
    ClassID key = (ClassID)cacheKey();
    return key.getVersion();
  }
  
}

