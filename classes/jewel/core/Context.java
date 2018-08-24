/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.LoadedClassInfo.PlacedFieldInfo;
import jewel.core.LoadedClassInfo.PlacedMethodInfo;
import jewel.core.clfile.AbstractMethodException;
import jewel.core.clfile.IncompatibleClassChangeException;
import jewel.core.clfile.Syntax;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public final class Context {

  private static final ThreadLocal local = new ThreadLocal();

  public static void set(Context context) {
    local.set(context);
  }

  public static Context get() {
    return (Context)local.get();
  }

  private final LoadedClassInfo clazz;
  private final HashMap map = new HashMap();

  public Context(LoadedClassInfo clazz, ContextClassInfo[] classes) {
    this.clazz = clazz;
    map.put(clazz.getName(), new ContextProxy(clazz.getName(), clazz.getLoadedVersion(), true, clazz.getDepth()));
    for (int i = 0; i < classes.length; i++) {
      ContextClassInfo cclazz = classes[i];
      map.put(cclazz.getName(), cclazz);
    }
  }

  public long computeVersion() {
    byte[] buffer;
    try {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(bout);
      for (Iterator i = map.values().iterator(); i.hasNext(); ) {
        ContextClassInfo clazz = (ContextClassInfo)i.next();
        out.writeUTF(clazz.getName());
        out.writeLong(clazz.getLoadedVersion());
        out.writeBoolean(clazz.isInitialized());
      }
      buffer = bout.toByteArray();
    } catch (IOException e) {
      throw new Error();
    }
    SHA1 sha1 = new SHA1();
    sha1.update(buffer, 0, buffer.length);
    return sha1.getValue();
  }

  public ContextClassInfo forName(String name) {
    ContextClassInfo result = (ContextClassInfo)map.get(name);
    if (result != null)
      return result;
    for (Iterator i = map.values().iterator(); i.hasNext(); ) {
      ContextClassInfo clazz = (ContextClassInfo)i.next();
      result = search(clazz, name);
      if (result != null)
        return result;
    }
    return null;
  }

  private ContextClassInfo search(ContextClassInfo clazz, String name) {
    if (name.equals(clazz.getName()))
      return clazz;
    LoadedClassInfo sclazz = clazz.getSuperclassClass();
    if (sclazz != null) {
      int loaderDepth = clazz.getContextLoaderDepth();
      if (loaderDepth == clazz.getDepth())
        loaderDepth = clazz.getSuperLoaderDepth();
      ContextClassInfo cclazz = new ContextProxy(sclazz.getName(), sclazz.getLoadedVersion(), clazz.isInitialized(), loaderDepth);
      ContextClassInfo result = search(cclazz, name);
      if (result != null)
        return result;
    }
    // review maybe not class loader sensitive
    LoadedClassInfo[] iclazz = clazz.getInterfacesClass();
    for (int i = 0; i < iclazz.length; i++) {
      int loaderDepth = iclazz[i].getDepth();
      ContextClassInfo cclazz = new ContextProxy(iclazz[i].getName(), iclazz[i].getLoadedVersion(), false, loaderDepth);
      ContextClassInfo result = search(cclazz, name);
      if (result != null)
        return result;
    }
    return null;
  }

  public Context getSubcontext(String name, String[] names) {
    ContextClassInfo clazz = forName(name);
    if (clazz == null)
      return null;
    if (names.length > 0)
      if (clazz.getContextLoaderDepth() != clazz.getDepth())
        return null;
    ContextClassInfo[] classes = new ContextClassInfo[names.length];
    for (int i = 0; i < classes.length; i++) {
      classes[i] = forName(names[i]);
      if (classes[i] == null)
        return null;
    }
    return new Context(clazz, classes);
  }

  public ContextClassInfo resolveClass(String name) throws IllegalAccessException {
    ContextClassInfo contextClass = forName(name);
    if (!contextClass.isPublic())
      if (contextClass.getContextLoaderDepth() != contextClass.getDepth() || !Syntax.getPackage(clazz.getName()).equals(Syntax.getPackage(contextClass.getName())))
        throw new IllegalAccessException("Attempt to access class "+contextClass.getName()+" from class "+clazz.getName());
    return contextClass;
  }

  public PlacedFieldInfo resolveField(String cname, String name, String descriptor) throws IllegalAccessException, NoSuchFieldException {
    ContextClassInfo contextClass = resolveClass(cname);
    PlacedFieldInfo field = contextClass.lookupField(name, descriptor);
    if (field == null)
      throw new NoSuchFieldException("Field "+contextClass.getName()+"."+name+" not found");
    LoadedClassInfo ownerClass = (LoadedClassInfo)field.getOwner();
    if (field.isPublic())
      return field;
    if (field.isPrivate()) {
      if (!clazz.getName().equals(contextClass.getName()) || contextClass.getDepth() != ownerClass.getDepth())
        throw new IllegalAccessException("Attempt to access field "+ownerClass.getName()+"."+name+" from class "+clazz.getName());
      return field;
    }
    if (field.isProtected()) {
      // missing subclass test
      return field;
    }
    if (!Syntax.getPackage(clazz.getName()).equals(Syntax.getPackage(ownerClass.getName())))
      throw new IllegalAccessException("Attempt to access field "+ownerClass.getName()+"."+name+" from class "+clazz.getName());
    int depth = ownerClass.getDepth();
    int ldepth = contextClass.getContextLoaderDepth();
    while (ldepth != depth) {
      if (ldepth < depth)
        throw new IllegalAccessException("Attempt to access field "+ownerClass.getName()+"."+name+" from class "+clazz.getName());
      LoadedClassInfo cl = contextClass;
      while (cl.getDepth() > ldepth)
        cl = cl.getSuperclassClass();
      ldepth = cl.getSuperLoaderDepth();
    }
    return field;
  }

  public PlacedMethodInfo resolveMethod(String cname, String name, String descriptor) throws IllegalAccessException, IncompatibleClassChangeException, NoSuchMethodException, AbstractMethodException {
    ContextClassInfo contextClass = resolveClass(cname);
    if (contextClass.isInterface())
      throw new IncompatibleClassChangeException("Interface "+contextClass.getName()+" used to be a class");
    PlacedMethodInfo method = contextClass.lookupMethod(name, descriptor);
    if (method == null)
      throw new NoSuchMethodException("Method "+contextClass.getName()+"."+name+descriptor+" not found");
    LoadedClassInfo ownerClass = (LoadedClassInfo)method.getOwner();
    if (method.isAbstract())
      if (!contextClass.isAbstract())
        throw new AbstractMethodException("Abstract method "+ownerClass.getName()+"."+name+descriptor+" is not implemented in non-abstract class "+contextClass.getName());
    if (method.isPublic())
      return method;
    if (method.isPrivate()) {
      if (!clazz.getName().equals(contextClass.getName()) || contextClass.getDepth() != ownerClass.getDepth())
        throw new IllegalAccessException("Attempt to access method "+ownerClass.getName()+"."+name+descriptor+" from class "+clazz.getName());
      return method;
    }
    if (method.isProtected()) {
      // missing subclass test
      return method;
    }
    if (!Syntax.getPackage(clazz.getName()).equals(Syntax.getPackage(ownerClass.getName())))
      throw new IllegalAccessException("Attempt to access method "+ownerClass.getName()+"."+name+descriptor+" from class "+clazz.getName());
    int depth = ownerClass.getDepth();
    int ldepth = contextClass.getContextLoaderDepth();
    while (ldepth != depth) {
      if (ldepth < depth)
        throw new IllegalAccessException("Attempt to access method "+ownerClass.getName()+"."+name+descriptor+" from class "+clazz.getName());
      LoadedClassInfo cl = contextClass;
      while (cl.getDepth() > ldepth)
        cl = cl.getSuperclassClass();
      ldepth = cl.getSuperLoaderDepth();
    }
    return method;
  }

  public PlacedMethodInfo resolveInterfaceMethod(String cname, String name, String descriptor) throws IllegalAccessException, IncompatibleClassChangeException, NoSuchMethodException {
    ContextClassInfo contextClass = resolveClass(cname);
    if (!contextClass.isInterface())
      throw new IncompatibleClassChangeException("Class "+contextClass.getName()+" used to be a interface");
    PlacedMethodInfo method = contextClass.lookupInterfaceMethod(name, descriptor);
    if (method == null)
      throw new NoSuchMethodException("Method "+contextClass.getName()+"."+name+descriptor+" not found");
    return method;
  }

}

