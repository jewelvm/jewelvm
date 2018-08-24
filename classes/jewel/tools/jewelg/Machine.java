/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jewelg;

import jewel.core.Jewel;
import jewel.core.Jewel.Context;
import jewel.core.Jewel.LazyTranslate;
import jewel.core.Jewel.Link;
import jewel.core.Jewel.Load;
import jewel.core.Jewel.Meta;
import jewel.core.Jewel.RealTranslate;
import jewel.core.Jewel.Register;
import jewel.core.bend.CodeOutput;
import jewel.core.clfile.ClassInfo;
import jewel.core.clfile.ClassInfoLoader;
import jewel.core.clfile.ClassPath;
import jewel.core.clfile.LinkageException;
import jewel.core.clfile.NoClassDefFoundException;
import jewel.core.jiro.IRCFG;
import jewel.core.jiro.Measure;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class Machine extends ClassInfoLoader {

  private final Jewel jewel = new Jewel();

  private final ClassPath classPath;
  private final HashMap interns = new HashMap();

  private int heapSize;
  private HObject heapFirst;
  private HObject heapLast;

  public Machine(String classpath) {
    this.classPath = new ClassPath(classpath);
  }

  protected byte[] findClassDef(String name) throws NoClassDefFoundException {
    return classPath.findDefinition(name);
  }

  public HClass loadPrimitiveClass(String name) {
    HClass clazz = (HClass)getClass(name);
    if (clazz == null) {
      clazz = allocPrimitiveClass(name);
      putClass(name, clazz);
    }
    return clazz;
  }

  protected ClassInfo deriveClass(String name, byte[] buffer, int start, int length) throws LinkageException, IllegalAccessException {
    Register register = jewel.register(name, buffer, start, length);
    String sname = null;
    long sversion = 0;
    HClass superClass = null;
    int sdepth = -1;
    if (register.getSuperclass() != null) {
      superClass = (HClass)loadClass(register.getSuperclass());
      sname = superClass.getName();
      sversion = superClass.getVersion();
      sdepth = superClass.getDepth();
    }
    String[] iname = new String[register.getInterfaces().length];
    long[] iversion = new long[register.getInterfaces().length];
    HClass[] ifaceClass = new HClass[register.getInterfaces().length];
    boolean[] ifaceSame = new boolean[register.getInterfaces().length];
    for (int i = 0; i < ifaceClass.length; i++) {
      ifaceClass[i] = (HClass)loadClass(register.getInterfaces()[i]);
      iname[i] = ifaceClass[i].getName();
      iversion[i] = ifaceClass[i].getVersion();
      ifaceSame[i] = true;
    }
    Load load = jewel.load(name, register.getVersion(), sname, sversion, sdepth, iname, iversion, ifaceSame);
    HClass elementClass = null;
    int dimensions = name.lastIndexOf('[')+1;
    if (dimensions != 0) {
      switch (name.charAt(dimensions)) {
      case 'B': elementClass = loadPrimitiveClass("<byte>"); break;
      case 'S': elementClass = loadPrimitiveClass("<short>"); break;
      case 'I': elementClass = loadPrimitiveClass("<int>"); break;
      case 'J': elementClass = loadPrimitiveClass("<long>"); break;
      case 'Z': elementClass = loadPrimitiveClass("<boolean>"); break;
      case 'C': elementClass = loadPrimitiveClass("<char>"); break;
      case 'F': elementClass = loadPrimitiveClass("<float>"); break;
      case 'D': elementClass = loadPrimitiveClass("<double>"); break;
      case 'V': elementClass = loadPrimitiveClass("<void>"); break;
      case 'L': elementClass = loadPrimitiveClass(name.substring(dimensions+1, name.length()-1)); break;
      default: throw new InternalError();
      }
    }
    HClass clazz = allocClass(name, load.getVersion(),
                       (char)register.getAccessFlags(), superClass, ifaceClass,
                       load.getStaticSize(), load.getStaticRefsOffset(), (char)load.getStaticRefsCount(),
                       load.getInstanceSize(), load.getInstanceRefsOffset(), (char)load.getInstanceRefsCount(),
                       load.getNonOverridableDispatchEntryCount(), load.getOverridableDispatchEntryCount(),
                       load.getInterfaceOffsets(), (char)load.getNativeSlots(),
                       elementClass, dimensions);
    if (dimensions > 0)
      clazz.link();
    return clazz;
  }

  public HObject resolveString(String string) throws LinkageException, IllegalAccessException {
    HObject object = (HObject)interns.get(string);
    if (object == null) {
      HClass clazz = (HClass)loadClass("java/lang/String");
      HCharArray array = allocCharArray(string.length());
      for (int i = 0; i < array.length; i++)
        array.set(i, string.charAt(i));
      object = allocObject(clazz);
      interns.put(string, object);
    }
    return object;
  }

  private HClass findCommonSuperclass(HClass one, HClass another) {
    //missing arrays
    if (one == null)
      throw new NullPointerException();
    if (another == null)
      throw new NullPointerException();
    for (HClass clazz = one; clazz != null; clazz = clazz.getHSuperclass())
      if (clazz == another)
        return another;
    for (HClass clazz = another; clazz != null; clazz = clazz.getHSuperclass())
      if (clazz == one)
        return one;
    return findCommonSuperclass(one.getHSuperclass(), another.getHSuperclass());
  }

  public int heapSize() {
    return heapSize;
  }

  private void heapAdd(HObject object) {
    if (heapFirst == null)
      heapFirst = object;
    if (heapLast != null)
      heapLast.next = object;
    heapLast = object;
    object.next = null;
    object.machine = this;
    object.address = ++heapSize;
  }

  public Iterator heapObjects() {
    return new Iterator() {
      private HObject next = heapFirst;
      public boolean hasNext() {
        return next != null;
      }
      public Object next() {
        if (next == null)
          throw new NoSuchElementException();
        HObject current = next;
        next = next.next;
        return current;
      }
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public int addressOf(HObject object) {
    if (object == null)
      return 0;
    if (object.machine != this)
      throw new IllegalArgumentException();
    return object.address;
  }

  private HObject allocObject(HClass clazz) {
    HObject object = new HOrdinaryObject(clazz);
    heapAdd(object);
    return object;
  }

  private HCharArray allocCharArray(int length) throws LinkageException, IllegalAccessException {
    HClass clazz = (HClass)loadClass("[C");
    HCharArray array = new HCharArray(clazz, length);
    heapAdd(array);
    return array;
  }

  private HMethodText allocMethodText(HClass declaringClass, char index) throws LinkageException, IllegalAccessException {
    HClass clazz = (HClass)loadClass("java/lang/MethodText");
    HMethodText methodText = new HMethodText(clazz, declaringClass, index);
    heapAdd(methodText);
    return methodText;
  }

  private HClass allocPrimitiveClass(String name) {
    char accessFlags = ACC_PUBLIC|ACC_FINAL|ACC_ABSTRACT|ACC_SUPER;
    HClass clazz = new HClass(name, 0,
                              accessFlags, null, new HClass[0],
                              Measure.encode(0, 0), Measure.encode(0, 0), (char)0,
                              Measure.encode(0, 0), Measure.encode(0, 0), (char)0,
                              1, 0,
                              new int[0], (char)0,
                              null, 0);
    clazz.status = 2;
    clazz.metaInfo = new MetaInfo(accessFlags, new FieldRecord[0], new MethodRecord[0], null, new InnerRecord[0], null);
    heapAdd(clazz);
    return clazz;
  }

  private HClass allocClass(String name, long version,
                           char accessFlags, HClass superClass, HClass[] interfaces,
                           long staticSize, long staticRefOfs, char staticRefCount,
                           long instanceSize, long instanceRefOfs, char instanceRefCount,
                           int staticEntries, int dynamicEntries,
                           int[] baseIndexes, char natives,
                           HClass elementClass, int dimensions) {
    HClass clazz = new HClass(name, version,
                              accessFlags, superClass, interfaces,
                              staticSize, staticRefOfs, staticRefCount,
                              instanceSize, instanceRefOfs, instanceRefCount,
                              staticEntries, dynamicEntries,
                              baseIndexes, natives,
                              elementClass, dimensions);
    heapAdd(clazz);
    return clazz;
  }
  
  public static abstract class HObject {

    HObject next;
    Machine machine;
    int address;

    private HClass clazz;

    HObject() { }

    HObject(HClass clazz) {
      this.clazz = clazz;
    }

    public final HClass getHClass() throws LinkageException, IllegalAccessException {
      if (clazz == null)
        clazz = (HClass)machine.loadClass("java/lang/Class");
      return clazz;
    }

  }

  public static final class HOrdinaryObject extends HObject {

    HOrdinaryObject(HClass clazz) {
      super(clazz);
    }

  }

  public static final class HCharArray extends HObject {

    public final int length;
    private final char[] elements;

    HCharArray(HClass clazz, int length) {
      super(clazz);
      this.length = length;
      elements = new char[length];
    }

    public char get(int index) {
      return elements[index];
    }

    public void set(int index, char value) {
      elements[index] = value;
    }

  }

  public static final class HMethodText extends HObject {

    private final HClass declaringClass;
    private final char index;
    private boolean lazy = true;

    HMethodText(HClass clazz, HClass declaringClass, char index) {
      super(clazz);
      this.declaringClass = declaringClass;
      this.index = index;
    }

    public HClass getDeclaringClass() {
      return declaringClass;
    }

    public int getIndex() {
      return index;
    }

    public boolean isLazy() {
      return lazy;
    }

    public void lazyResolve() {
      lazy = false;
    }

    public IRCFG getCFG() throws LinkageException, IllegalAccessException {
      if (lazy)
        throw new IllegalStateException();
      Context context = machine.jewel.context(declaringClass.getName(), declaringClass.getVersion(), index);
      String[] names = context.getClasses();
      long[] versions = new long[names.length];
      boolean[] initializeds = new boolean[names.length];
      int[] loaders = new int[names.length];
      for (int j = 0; j < names.length; j++) {
        HClass cclazz = (HClass)machine.loadClass(names[j]);
        versions[j] = cclazz.getVersion();
        initializeds[j] = false;
        loaders[j] = cclazz.getDepth();
      }
      RealTranslate translate = machine.jewel.realTranslate(declaringClass.getName(), declaringClass.getVersion(), index, names, versions, initializeds, loaders, 0, "raw");
      return (IRCFG)translate.getObject();
    }

    public CodeOutput getCodeOutput(String backend) throws LinkageException, IllegalAccessException {
      if (lazy) {
        LazyTranslate translate = machine.jewel.lazyTranslate(declaringClass.getName(), declaringClass.getVersion(), backend);
        return translate.getCodeOutput()[index];
      } else {
        Context context = machine.jewel.context(declaringClass.getName(), declaringClass.getVersion(), index);
        String[] names = context.getClasses();
        long[] versions = new long[names.length];
        boolean[] initializeds = new boolean[names.length];
        int[] loaders = new int[names.length];
        for (int j = 0; j < names.length; j++) {
          HClass cclazz = (HClass)machine.loadClass(names[j]);
          versions[j] = cclazz.getVersion();
          initializeds[j] = cclazz.isInitialized() || machine.findCommonSuperclass(declaringClass, cclazz) == cclazz;
          loaders[j] = cclazz.getDepth();
        }
        RealTranslate translate = machine.jewel.realTranslate(declaringClass.getName(), declaringClass.getVersion(), index, names, versions, initializeds, loaders, 0, backend);
        for (int j = 0; j < translate.getTargetClass().length; j++) {
          HClass targetClass = (HClass)machine.loadClass(translate.getTargetClass()[j]);
          HClass sourceClass = (HClass)machine.loadClass(translate.getSourceClass()[j][0]);
          for (int k = 1; k < translate.getSourceClass()[j].length; k++)
            sourceClass = machine.findCommonSuperclass(sourceClass, (HClass)machine.loadClass(translate.getSourceClass()[j][k]));
          if (!sourceClass.isSubtypeOf(targetClass))
            throw new VerifyError();
        }
        return (CodeOutput)translate.getObject();
      }
    }

  }

  public static final class HClass extends HObject implements ClassInfo {
  
    private final String name;
    private final long version;
    private final char accessFlags;
    private final HClass superClass;
    private final char directs;
    private final HClass[] interfaces;
    private final int[] baseIndexes;
  
    private final long staticSize;
    private final long staticRefOfs;
    private final char staticRefCount;
    private final long instanceSize;
    private final long instanceRefOfs;
    private final char instanceRefCount;
  
    private final int staticEntries;
    private final int dynamicEntries;
    private final HMethodText[] methodTable;
  
    private final char natives;

    private final HClass elementClass;
    private final byte dimensions;
  
    private byte status;
    private boolean finalizes;
  
    private MetaInfo metaInfo;
  
    HClass(String name, long version,
           char accessFlags, HClass superClass, HClass[] interfaces,
           long staticSize, long staticRefOfs, char staticRefCount,
           long instanceSize, long instanceRefOfs, char instanceRefCount,
           int staticEntries, int dynamicEntries,
           int[] baseIndexes, char natives,
           HClass elementClass, int dimensions) {
      this.name = name;
      this.version = version;
      this.accessFlags = accessFlags;
      this.superClass = superClass;
      this.directs = (char)interfaces.length;
  
      HashSet set = new HashSet();
      if (superClass != null)
        for (int i = 0; i < superClass.interfaces.length; i++)
          set.add(superClass.interfaces[i]);
      for (int i = 0; i < interfaces.length; i++) {
        set.add(interfaces[i]);
        for (int j = 0; j < interfaces[i].interfaces.length; j++)
          set.add(interfaces[i].interfaces[j]);
      }
      int interfaceCount = set.size();
      this.interfaces = new HClass[interfaceCount];
      this.baseIndexes = new int[interfaceCount];
      
      interfaceCount = 0;
      for (int i = 0; i < interfaces.length; i++)
        if (set.contains(interfaces[i])) {
          set.remove(interfaces[i]);
          this.interfaces[interfaceCount] = interfaces[i];
          this.baseIndexes[interfaceCount] = baseIndexes[i];
          interfaceCount++;
        }
      if (superClass != null)
        for (int i = 0; i < superClass.interfaces.length; i++)
          if (set.contains(superClass.interfaces[i])) {
            set.remove(superClass.interfaces[i]);
            this.interfaces[interfaceCount] = superClass.interfaces[i];
            this.baseIndexes[interfaceCount] = superClass.baseIndexes[i];
            interfaceCount++;
          }
      for (int i = 0; i < interfaces.length; i++)
        for (int j = 0; j < interfaces[i].interfaces.length; j++)
          if (set.contains(interfaces[i].interfaces[j])) {
            set.remove(interfaces[i].interfaces[j]);
            this.interfaces[interfaceCount] = interfaces[i].interfaces[j];
            this.baseIndexes[interfaceCount] = baseIndexes[i]+interfaces[i].baseIndexes[j];
            interfaceCount++;
          }

      this.staticSize = staticSize;
      this.staticRefOfs = staticRefOfs;
      this.staticRefCount = staticRefCount;
      this.instanceSize = instanceSize;
      this.instanceRefOfs = instanceRefOfs;
      this.instanceRefCount = instanceRefCount;
  
      this.staticEntries = staticEntries;
      this.dynamicEntries = dynamicEntries;
      this.methodTable = new HMethodText[dynamicEntries+staticEntries];
  
      this.natives = natives;

      this.elementClass = elementClass;
      this.dimensions = (byte)dimensions;
    }

    public ConstantPool getConstantPool() {
      throw new UnsupportedOperationException();
    }
  
    public boolean isPublic() {
      return (accessFlags & ACC_PUBLIC) != 0;
    }

    public boolean isPackagePrivate() {
      return (accessFlags & ACC_PUBLIC) == 0;
    }

    public boolean isFinal() {
      return (accessFlags & ACC_FINAL) != 0;
    }

    public boolean isSuper() {
      return (accessFlags & ACC_SUPER) != 0;
    }

    public boolean isInterface() {
      return (accessFlags & ACC_INTERFACE) != 0;
    }

    public boolean isAbstract() {
      return (accessFlags & ACC_ABSTRACT) != 0;
    }

    public int getAccessFlags() {
      return accessFlags;
    }
  
    public String getName() {
      return name;
    }
  
    public long getVersion() {
      return version;
    }

    public String getSuperclass() {
      return superClass == null ? null : superClass.getName();
    }
  
    public HClass getHSuperclass() {
      return superClass;
    }
  
    public int getDepth() {
      if (superClass == null)
        return 0;
      return superClass.getDepth()+1;
    }
  
    public int getInterfaceCount() {
      return directs;
    }
  
    public int getTotalInterfaceCount() {
      return interfaces.length;
    }

    public String getInterface(int index) {
      return interfaces[index].getName();
    }

    public String[] getInterfaces() {
      String[] interfaces = new String[getInterfaceCount()];
      for (int i = 0; i < interfaces.length; i++)
        interfaces[i] = getInterface(i);
      return interfaces;
    }
  
    public HClass getHInterface(int index) {
      return interfaces[index];
    }
  
    public int getInterfaceBase(int index) {
      return baseIndexes[index];
    }

    public int getInterfaceBase(HClass interFace) {
      for (int i = 0; i < interfaces.length; i++)
        if (interfaces[i] == interFace)
          return baseIndexes[i];
      return -1;
    }

    public int getFieldCount() {
      throw new UnsupportedOperationException();
    }

    public FieldInfo getField(String name, String descriptor) {
      throw new UnsupportedOperationException();
    }

    public FieldInfo getField(int index) {
      throw new UnsupportedOperationException();
    }

    public FieldInfo[] getFields() {
      throw new UnsupportedOperationException();
    }
    public int getMethodCount() {
      throw new UnsupportedOperationException();
    }

    public MethodInfo getMethod(String name, String descriptor) {
      throw new UnsupportedOperationException();
    }

    public MethodInfo getMethod(int index) {
      throw new UnsupportedOperationException();
    }

    public MethodInfo[] getMethods() {
      throw new UnsupportedOperationException();
    }

    public int getInnerClassCount() {
      throw new UnsupportedOperationException();
    }

    public InnerInfo getInnerClass(int index) {
      throw new UnsupportedOperationException();
    }

    public InnerInfo[] getInnerClasses() {
      throw new UnsupportedOperationException();
    }

    public String getSourceFile() {
      throw new UnsupportedOperationException();
    }
  
    public boolean isSynthetic() {
      throw new UnsupportedOperationException();
    }
  
    public boolean isDeprecated() {
      throw new UnsupportedOperationException();
    }
  
    public boolean isSubtypeOf(HClass ancestor) {
      for (HClass clazz = this; clazz != null; clazz = clazz.superClass)
        if (clazz == ancestor)
          return true;
      for (int i = 0; i < interfaces.length; i++)
        if (interfaces[i] == ancestor)
          return true;
      if (getDimensions() > 0 && ancestor.getDimensions() > 0) {
        if (getDimensions() == ancestor.getDimensions())
          return elementClass.isSubtypeOf(ancestor.elementClass);
        if (getDimensions() > ancestor.getDimensions())
          return isSubtypeOf(ancestor.elementClass);
      }
      return false;
    }
  
    public long getStaticSize() {
      return staticSize;
    }
  
    public long getStaticRefOfs() {
      return staticRefOfs;
    }
  
    public int getStaticRefCount() {
      return staticRefCount;
    }
  
    public long getInstanceSize() {
      return instanceSize;
    }
  
    public long getInstanceRefOfs() {
      return instanceRefOfs;
    }
  
    public int getInstanceRefCount() {
      return instanceRefCount;
    }
  
    public int getStaticEntries() {
      return staticEntries;
    }
  
    public int getDynamicEntries() {
      return dynamicEntries;
    }
  
    public HMethodText getMethodText(int index) {
      return methodTable[index];
    }
  
    public int getNativeMethodCount() {
      return natives;
    }
  
    public HClass getElementClass() {
      return elementClass;
    }
  
    public int getDimensions() {
      return dimensions & 0xFF;
    }

    public int getStatus() {
      return status;
    }
  
    public boolean isLinked() {
      return status != 0;
    }
  
    public boolean isInitialized() {
      return status == 2;
    }
  
    public boolean finalizes() {
      return finalizes;
    }
  
    public MetaInfo getMetaData() {
      return metaInfo;
    }
  
    public void link() throws LinkageException, IllegalAccessException {
      if (status == 0) {
        if (superClass != null)
          superClass.link();
        for (int i = 0; i < directs; i++)
          interfaces[i].link();

        for (int i = 0; i < directs; i++)
          System.arraycopy(interfaces[i].methodTable, 0, methodTable, baseIndexes[i], interfaces[i].dynamicEntries);
        if (superClass != null)
          if ((accessFlags & ACC_INTERFACE) == 0)
            System.arraycopy(superClass.methodTable, 0, methodTable, 0, superClass.dynamicEntries);

        Link link = machine.jewel.link(name, version);
        // apply loading constraints
        for (int i = 0; i < link.getMethodIndex().length; i++)
          methodTable[link.getImplementationIndex()[i]] = superClass.methodTable[link.getMethodIndex()[i]];
        finalizes = link.getFinalizes();

        int[][] patches = link.getPatches();
        for (int i = 0; i < patches.length; i++) {
          HMethodText methodText = machine.allocMethodText(this, (char)i);
          for (int j = 0; j < patches[i].length; j++)
            methodTable[patches[i][j]] = methodText;
        }

        if (superClass == null || superClass.status == 2) {
          HMethodText text = methodTable[dynamicEntries];
          if (text == null) {
            status = 2;
            return;
          }
        }
        status = 1;
      }
    }

    public void loadMetadata() {
      if (metaInfo == null) {
        Meta meta = machine.jewel.meta(name, version);
        char sourceFlags = meta.getAccessFlags();
        FieldRecord[] fields = new FieldRecord[meta.getFieldFlags().length];
        for (int i = 0; i < fields.length; i++)
          fields[i] = new FieldRecord(meta.getFieldFlags()[i], meta.getFieldName()[i], meta.getFieldDesc()[i], meta.getFieldOffset()[i]);
        MethodRecord[] methods = new MethodRecord[meta.getMethodFlags().length];
        for (int i = 0; i < methods.length; i++)
          methods[i] = new MethodRecord(meta.getMethodFlags()[i], meta.getMethodName()[i], meta.getMethodDesc()[i], meta.getMethodExceptions()[i], meta.getMethodDispatchIndex()[i]);
        String declaringClass = meta.getDeclaringClass();
        InnerRecord[] inners = new InnerRecord[meta.getInnerFlags().length];
        for (int i = 0; i < inners.length; i++)
          inners[i] = new InnerRecord(meta.getInnerFlags()[i], meta.getInnerName()[i]);
        String sourceFile = meta.getSourceFile();
        metaInfo = new MetaInfo(sourceFlags, fields, methods, declaringClass, inners, sourceFile);
      }
    }

  }

  public static final class MetaInfo {

    private final char sourceFlags;
    private final FieldRecord[] fields;
    private final MethodRecord[] methods;
    private final String declaringClass;
    private final InnerRecord[] inners;
    private final String sourceFile;

    MetaInfo(char sourceFlags, FieldRecord[] fields, MethodRecord[] methods, String declaringClass, InnerRecord[] inners, String sourceFile) {
      this.sourceFlags = sourceFlags;
      this.fields = (FieldRecord[])fields.clone();
      this.methods = (MethodRecord[])methods.clone();
      this.declaringClass = declaringClass;
      this.inners = (InnerRecord[])inners.clone();
      this.sourceFile = sourceFile;
    }

    public int getSourceFlags() {
      return sourceFlags;
    }

    public int getFieldCount() {
      return fields.length;
    }

    public FieldRecord getFieldAt(int index) {
      return fields[index];
    }

    public int getMethodCount() {
      return methods.length;
    }

    public MethodRecord getMethodAt(int index) {
      return methods[index];
    }

    public String getDeclaringClass() {
      return declaringClass;
    }

    public int getInnerCount() {
      return inners.length;
    }

    public InnerRecord getInnerAt(int index) {
      return inners[index];
    }

    public String getSourceFile() {
      return sourceFile;
    }

  }

  public static final class FieldRecord {

    private final char accessFlags;
    private final String name;
    private final String descriptor;
    private final long offset;

    FieldRecord(char accessFlags, String name, String descriptor, long offset) {
      this.accessFlags = accessFlags;
      this.name = name;
      this.descriptor = descriptor;
      this.offset = offset;
    }

    // remove
    public boolean isStatic() {
      return (accessFlags & ACC_STATIC) != 0;
    }

    public int getAccessFlags() {
      return accessFlags;
    }

    public String getName() {
      return name;
    }

    public String getDescriptor() {
      return descriptor;
    }

    public long getOffset() {
      return offset;
    }

  }

  public static final class MethodRecord {

    private final char accessFlags;
    private final String name;
    private final String descriptor;
    private final String[] exceptions;
    private final int dispatchIndex;

    MethodRecord(char accessFlags, String name, String descriptor, String[] exceptions, int dispatchIndex) {
      this.accessFlags = accessFlags;
      this.name = name;
      this.descriptor = descriptor;
      this.exceptions = (String[])exceptions.clone();
      this.dispatchIndex = dispatchIndex;
    }

    public int getAccessFlags() {
      return accessFlags;
    }

    public String getName() {
      return name;
    }

    public String getDescriptor() {
      return descriptor;
    }

    public int getExceptionCount() {
      return exceptions.length;
    }

    public String getExceptionAt(int index) {
      return exceptions[index];
    }

    public int getDispatchIndex() {
      return dispatchIndex;
    }

  }

  public static final class InnerRecord {

    private final char accessFlags;
    private final String name;

    InnerRecord(char accessFlags, String name) {
      this.accessFlags = accessFlags;
      this.name = name;
    }

    public int getAccessFlags() {
      return accessFlags;
    }

    public String getName() {
      return name;
    }

  }

}

