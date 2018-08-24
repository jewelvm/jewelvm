/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package java.lang;

import jewel.core.Jewel;
import jewel.core.Jewel.Register;
import jewel.core.Jewel.Load;
import jewel.core.Jewel.Context;
import jewel.core.Jewel.LazyTranslate;
import jewel.core.Jewel.Link;
import jewel.core.Jewel.Meta;
import jewel.core.Jewel.RealTranslate;
import jewel.core.bend.CodeOutput;
import jewel.core.bend.Relocatable;
import jewel.core.bend.RelocatableFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;

final class Machinery {

  private static final Jewel jewel = new Jewel();

  private static final RelocatableFactory methodTextFactory = new RelocatableFactory() {
    public Relocatable createRelocatable(byte[] array, int start, int length) {
      return MethodText.newMethodText(array, start, length);
    }
  };

  protected static void resolveClass(Class clazz) {
    ClassLoader loader = clazz.getClassLoader();
    if (loader == null)
      loader = new ClassLoader(){ }; //care, bad workaround
    loader.resolveClass(clazz);
  }

  private static Class getSuperclass(Class clazz) {
    if (clazz.isInterface())
      try {
        return Class.forName("java.lang.Object", false, clazz.getClassLoader());
      } catch (ClassNotFoundException e) {
        throw new NoClassDefFoundError(e.getMessage());
      }
    return clazz.getSuperclass();
  }

  private static int getDepth(Class clazz) {
    if (clazz.isInterface())
      return 1;
    Class superclass = clazz.getSuperclass();
    if (superclass == null)
      return 0;
    return getDepth(superclass)+1;
  }

  private static Class findCommonSuperclass(Class one, Class another) {
    if (one != null && one.isArray() && another != null && another.isArray()) {
      Class onecomp = one.getComponentType();
      Class anothercomp = another.getComponentType();
      if (!onecomp.isPrimitive() && !anothercomp.isPrimitive()) {
        Class comp = findCommonSuperclass(onecomp, anothercomp);
        if (comp == null)
          return null;
        String name = comp.getName();
        if (name.startsWith("["))
          name = "["+name;
        else
          name = "[L"+name+";";
        try {
          return Class.forName(name, false, comp.getClassLoader());
        } catch (ClassNotFoundException e) {
          throw new NoClassDefFoundError(e.getMessage());
        }
      }
    }
    while (one != null) {
      for (Class clazz = another; clazz != null; clazz = getSuperclass(clazz))
        if (clazz == one)
          return one;
      one = getSuperclass(one);
    }
    return null;
  }

  private static Class deriveClass(ClassLoader loader, String name, byte[] array, int start, int length) {
    String internal = name.replace('.', '/');

    Register registerData = jewel.register(internal, array, start, length);

    String sname = registerData.getSuperclass();
    Class superClass = null;
    long sversion = 0;
    if (sname != null) {
      try {
        superClass = Class.forName(sname.replace('/', '.'), false, loader);
      } catch (ClassNotFoundException e) {
        throw new NoClassDefFoundError(e.getMessage());
      }
      sversion = builtin.getVersion(superClass);
    }

    String[] iname = registerData.getInterfaces();
    Class[] ifaceClass = new Class[iname.length];
    long[] iversion = new long[iname.length];
    for (int i = 0; i < iname.length; i++) {
      try {
        ifaceClass[i] = Class.forName(iname[i].replace('/', '.'), false, loader);
      } catch (ClassNotFoundException e) {
        throw new NoClassDefFoundError(e.getMessage());
      }
      iversion[i] = builtin.getVersion(ifaceClass[i]);
    }
    
    int sdepth = -1;
    for (Class cl = superClass; cl != null; cl = getSuperclass(cl))
      if (loader == cl.getClassLoader()) {
        sdepth = getDepth(cl);
        break;
      }
    
    boolean[] ifaceSame = new boolean[iname.length];
    for (int i = 0; i < ifaceSame.length; i++)
      ifaceSame[i] = loader == ifaceClass[i].getClassLoader();

    Load loadData = jewel.load(internal, registerData.getVersion(), sname, sversion, sdepth, iname, iversion, ifaceSame);
    
    Class elem = null;
    int dims = internal.lastIndexOf('[')+1;
    if (dims > 0)
      switch (internal.charAt(dims)) {
      case 'Z': elem = Boolean.TYPE; break;
      case 'B': elem = Byte.TYPE; break;
      case 'C': elem = Character.TYPE; break;
      case 'S': elem = Short.TYPE; break;
      case 'I': elem = Integer.TYPE; break;
      case 'J': elem = Long.TYPE; break;
      case 'F': elem = Float.TYPE; break;
      case 'D': elem = Double.TYPE; break;
      default: 
        try {
          elem = Class.forName(name.substring(dims+1, name.length()-1), false, loader);
        } catch (ClassNotFoundException e) {
          throw new NoClassDefFoundError(e.getMessage());
        }
      }
    
    Class clazz = builtin.newClass(loader, internal, loadData.getVersion(),
                                   (char)registerData.getAccessFlags(), superClass, ifaceClass,
                                   loadData.getStaticSize(), loadData.getStaticRefsOffset(), (char)loadData.getStaticRefsCount(),
                                   loadData.getInstanceSize(), loadData.getInstanceRefsOffset(), (char)loadData.getInstanceRefsCount(),
                                   loadData.getNonOverridableDispatchEntryCount(), loadData.getOverridableDispatchEntryCount(), loadData.getInterfaceOffsets(), (char)loadData.getNativeSlots(), elem, dims);
    
    if (dims > 0)
      resolveClass(clazz);
    
    return clazz;
  }

  private static void linkClass(Class clazz) {
    String name = clazz.getName();
    String internal = name.replace('.', '/');
    long version = builtin.getVersion(clazz);
    ClassLoader loader = clazz.getClassLoader();

    Link linkData = jewel.link(internal, version);

    /* missing apply loading constraints */

    int[] mindex = linkData.getMethodIndex();
    int[] iindex = linkData.getImplementationIndex();
    Class superclass = getSuperclass(clazz);
    for (int i = 0; i < mindex.length; i++)
      builtin.setMethodText(clazz, iindex[i], builtin.getMethodText(superclass, mindex[i]));

    if (!linkData.getFinalizes())
      builtin.flagFinalized(clazz);

    int[][] patches = linkData.getPatches();

    LazyTranslate translateData = jewel.lazyTranslate(internal, version, "i386");
    CodeOutput[] codes = translateData.getCodeOutput();

    for (int i = 0; i < patches.length; i++) {
      CodeOutput code = codes[i];

      MethodText text;

      ClassLoader previous = MethodText.getLoader();
      MethodText.setLoader(loader);
      try {
        text = (MethodText)code.prepareImage(methodTextFactory);
      } finally {
        MethodText.setLoader(previous);
      }

      for (int j = 0; j < patches[i].length; j++)
        builtin.setMethodText(clazz, patches[i][j], text);
    }
  }

  private static void loadMetadata(Class clazz) {
    String name = clazz.getName();
    String internal = name.replace('.', '/');
    long version = builtin.getVersion(clazz);

    Meta metaData = jewel.meta(internal, version);

    char accessFlags = metaData.getAccessFlags();
    char[] fieldFlags = metaData.getFieldFlags();
    String[] fieldName = metaData.getFieldName();
    String[] fieldDesc = metaData.getFieldDesc();
    long[] fieldOffset = metaData.getFieldOffset();
    char[] methodFlags = metaData.getMethodFlags();
    String[] methodName = metaData.getMethodName();
    String[] methodDesc = metaData.getMethodDesc();
    String[][] methodExceptions = metaData.getMethodExceptions();
    int[] methodDispatchIndex = metaData.getMethodDispatchIndex();
    String declaringClass = metaData.getDeclaringClass();
    char[] innerFlags = metaData.getInnerFlags();
    String[] innerName = metaData.getInnerName();
    String sourceFile = metaData.getSourceFile();

    builtin.allocMetadata(clazz, fieldFlags.length, methodFlags.length, innerFlags.length);
    builtin.setMetaFlags(clazz, accessFlags);
    for (int i = 0; i < fieldFlags.length; i++)
      builtin.setMetaField(clazz, i, fieldFlags[i], fieldName[i], fieldDesc[i], fieldOffset[i]);
    for (int i = 0; i < methodFlags.length; i++)
      builtin.setMetaMethod(clazz, i, methodFlags[i], methodName[i], methodDesc[i], methodExceptions[i], methodDispatchIndex[i]);
    builtin.setMetaDeclaring(clazz, declaringClass);
    for (int i = 0; i < innerFlags.length; i++)
      builtin.setMetaInner(clazz, i, innerFlags[i], innerName[i]);
    builtin.setMetaSource(clazz, sourceFile);
  }

  private static MethodText lazyResolve(MethodText lazyText, int level) {
    Class clazz = lazyText.getDeclaringClass();
    int index = lazyText.getIndex();
    String name = clazz.getName();
    String internal = name.replace('.', '/');
    long version = builtin.getVersion(clazz);
    ClassLoader loader = clazz.getClassLoader();

    Context contextData = jewel.context(internal, version, index);

    String[] cname = contextData.getClasses();
    long[] cversion = new long[cname.length];
    boolean[] cinitialized = new boolean[cname.length];
    int[] cdepth = new int[cname.length];
    for (int i = 0; i < cname.length; i++) {
      Class contextClass;
      try {
        contextClass = Class.forName(cname[i].replace('/', '.'), false, loader);
      } catch (ClassNotFoundException e) {
        throw new NoClassDefFoundError(e.getMessage());
      }
      cversion[i] = builtin.getVersion(contextClass);
      cinitialized[i] = builtin.isInitialized(contextClass) || findCommonSuperclass(clazz, contextClass) == contextClass;
      cdepth[i] = -1;
      for (Class cl = contextClass; cl != null; cl = getSuperclass(cl))
        if (loader == cl.getClassLoader()) {
          cdepth[i] = getDepth(cl);
          break;
        }
    }

    RealTranslate translateData = jewel.realTranslate(internal, version, index, cname, cversion, cinitialized, cdepth, level, "i386");
    
    String[] tclass = translateData.getTargetClass();
    String[][] sclasses = translateData.getSourceClass();
    for (int i = 0; i < tclass.length; i++) {
      Class targetClass;
      try {
        targetClass = Class.forName(tclass[i].replace('/', '.'), false, loader);
      } catch (ClassNotFoundException e) {
        throw new NoClassDefFoundError(e.getMessage());
      }
      if (!targetClass.isInterface()) {
        String[] sclass = sclasses[i];
        Class sourceClass;
        try {
          sourceClass = Class.forName(sclass[0].replace('/', '.'), false, loader);
        } catch (ClassNotFoundException e) {
          throw new NoClassDefFoundError(e.getMessage());
        }
        for (int j = 1; j < sclass.length; j++) {
          Class mergeClass;
          try {
            mergeClass = Class.forName(sclass[j].replace('/', '.'), false, loader);
          } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
          }
          sourceClass = findCommonSuperclass(sourceClass, mergeClass);
        }
        if (sourceClass == null || !targetClass.isAssignableFrom(sourceClass))
          throw new VerifyError(name);
      }
    }

    CodeOutput code = (CodeOutput)translateData.getObject();

    ClassLoader previous = MethodText.getLoader();
    MethodText.setLoader(loader);
    try {
      return (MethodText)code.prepareImage(methodTextFactory);
    } finally {
      MethodText.setLoader(previous);
    }
  }

  private Machinery() { }

}

