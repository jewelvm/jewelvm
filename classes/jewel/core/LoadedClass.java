/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.cache.Cache;
import jewel.core.cache.CacheKey;
import jewel.core.cache.CacheObject;
import jewel.core.cache.ConstructionCircularityException;
import jewel.core.cache.ConstructionTargetException;
import jewel.core.clfile.AbstractMethodException;
import jewel.core.clfile.Bytecode;
import jewel.core.clfile.ClassFileConstants;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo;
import jewel.core.clfile.FilterClassInfo;
import jewel.core.clfile.IncompatibleClassChangeException;
import jewel.core.clfile.Syntax;
import jewel.core.clfile.VerifyException;
import jewel.core.jiro.IRCFG;
import jewel.core.jiro.Measure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;

final class LoadedClass extends FilterClassInfo implements CacheObject, LoadedClassInfo, ClassFileConstants {

  static final class Key extends ClassID {

    public Key(String name, long version) {
      super(name, version, "loaded");
    }

    public CacheObject newInstance() {
      return new LoadedClass(this);
    }

  }

  static final class Params {

    public final RegisteredClassInfo clazz;
    public final LoadedClassInfo sclazz;
    public final int sdepth;
    public final LoadedClassInfo[] iclazz;
    public final boolean[] isame;

    public Params(RegisteredClassInfo clazz, LoadedClassInfo sclazz, int sdepth, LoadedClassInfo[] iclazz, boolean[] isame) {
      this.clazz = clazz;
      this.sclazz = sclazz;
      this.sdepth = sdepth;
      this.iclazz = iclazz;
      this.isame = isame;
    }

  }

  private final Key key;

  private LoadedClassInfo superProxy;
  private int sdepth;
  private LoadedClassInfo[] ifaceProxy;

  private long staticSize;
  private long staticRefsOffset;
  private char staticRefsCount;
  private long instanceSize;
  private long instanceRefsOffset;
  private char instanceRefsCount;
  private Hole[] holes;

  private int[] baseIndexes;
  private int dynamicLength;
  private int staticLength;

  //begin review
  private final ArrayList inheritedPairs = new ArrayList();
  //end review

  protected LoadedClass(Key key) {
    this.key = key;
  }

  protected FieldInfo filter(FieldInfo field) {
    return new PlacedField(field);
  }

  protected MethodInfo filter(MethodInfo method) {
    return new PlacedMethod(method);
  }

  public long getRegisteredVersion() {
    return ((RegisteredClassInfo)underlying).getRegisteredVersion();
  }

  public long getLoadedVersion() {
    return key.getVersion();
  }

  public LoadedClassInfo getSuperclassClass() {
    return superProxy;
  }

  public LoadedClassInfo[] getInterfacesClass() {
    return (LoadedClassInfo[])ifaceProxy.clone();
  }

  public int getInterfaceBaseIndex(int index) {
    return baseIndexes[index];
  }

  public int[] getInterfaceBaseIndexes() {
    return (int[])baseIndexes.clone();
  }

  public int lookupInterfaceBaseIndex(LoadedClassInfo clazz) {
    if (isInterface()) {
      String name = getName();
      long version = getLoadedVersion();
      if (name.equals(clazz.getName()) && version == clazz.getLoadedVersion())
        return 0;
    }
    if (superProxy != null) {
      int index = superProxy.lookupInterfaceBaseIndex(clazz);
      if (index != -1)
        return index;
    }
    for (int i = 0; i < ifaceProxy.length; i++) {
      int index = ifaceProxy[i].lookupInterfaceBaseIndex(clazz);
      if (index != -1)
        return baseIndexes[i]+index;
    }
    return -1;
  }

  public int getDepth() {
    if (superProxy == null)
      return 0;
    return superProxy.getDepth()+1;
  }

  public int getSuperLoaderDepth() {
    return sdepth;
  }

  public long getStaticSize() {
    return staticSize;
  }

  private void setStaticSize(long staticSize) {
    if (!Measure.isValid(staticSize))
      throw new IllegalArgumentException();
    this.staticSize = staticSize;
  }

  public long getStaticRefsOffset() {
    return staticRefsOffset;
  }

  private void setStaticRefsOffset(long staticRefsOffset) {
    if (!Measure.isValid(staticRefsOffset))
      throw new IllegalArgumentException();
    this.staticRefsOffset = staticRefsOffset;
  }

  public int getStaticRefsCount() {
    return staticRefsCount;
  }

  private void setStaticRefsCount(int staticRefsCount) {
    if (staticRefsCount < 0 || staticRefsCount > 65535)
      throw new IllegalArgumentException();
    this.staticRefsCount = (char)staticRefsCount;
  }

  public long getInstanceSize() {
    return instanceSize;
  }

  private void setInstanceSize(long instanceSize) {
    if (!Measure.isValid(instanceSize))
      throw new IllegalArgumentException();
    this.instanceSize = instanceSize;
  }

  public long getInstanceRefsOffset() {
    return instanceRefsOffset;
  }

  private void setInstanceRefsOffset(long instanceRefsOffset) {
    if (!Measure.isValid(instanceRefsOffset))
      throw new IllegalArgumentException();
    this.instanceRefsOffset = instanceRefsOffset;
  }

  public int getInstanceRefsCount() {
    return instanceRefsCount;
  }

  private void setInstanceRefsCount(int instanceRefsCount) {
    if (instanceRefsCount < 0 || instanceRefsCount > 65535)
      throw new IllegalArgumentException();
    this.instanceRefsCount = (char)instanceRefsCount;
  }

  private ArrayList getHoles() {
    ArrayList list = new ArrayList(holes.length);
    for (int i = 0; i < holes.length; i++) {
      Hole hole = holes[i];
      list.add(new Hole(hole.getOffset(), hole.getSize()));
    }
    return list;
  }

  private void setHoles(ArrayList list) {
    holes = new Hole[list.size()];
    int index = 0;
    for (Iterator i = list.iterator(); i.hasNext(); index++) {
      Hole hole = (Hole)i.next();
      holes[index] = new Hole(hole.getOffset(), hole.getSize());
    }
  }

  public int getNonOverridableDispatchEntryCount() {
    return staticLength;
  }

  private void setNonOverridableDispatchEntryCount(int staticLength) {
    if (staticLength < 0)
      throw new IllegalArgumentException();
    this.staticLength = staticLength;
  }

  public int getOverridableDispatchEntryCount() {
    return dynamicLength;
  }

  private void setOverridableDispatchEntryCount(int dynamicLength) {
    if (dynamicLength < 0)
      throw new IllegalArgumentException();
    this.dynamicLength = dynamicLength;
  }

  public PlacedFieldInfo lookupField(String name, String descriptor) {
    PlacedFieldInfo field = (PlacedFieldInfo)getField(name, descriptor);
    if (field != null)
      return field;
    for (int i = 0; i < ifaceProxy.length; i++) {
      field = ifaceProxy[i].lookupField(name, descriptor);
      if (field != null)
        return field;
    }
    if (superProxy != null) {
      field = superProxy.lookupField(name, descriptor);
      if (field != null)
        return field;
    }
    return null;
  }

  public PlacedMethodInfo lookupMethod(String name, String descriptor) {
    if (isInterface())
      throw new UnsupportedOperationException();
    PlacedMethodInfo method = (PlacedMethodInfo)getMethod(name, descriptor);
    if (method != null)
      return method;
    if (superProxy != null) {
      method = superProxy.lookupMethod(name, descriptor);
      if (method != null)
        return method;
    }
    for (int i = 0; i < ifaceProxy.length; i++) {
      method = ifaceProxy[i].lookupInterfaceMethod(name, descriptor);
      if (method != null)
        return method;
    }
    return null;
  }

  public PlacedMethodInfo lookupInterfaceMethod(String name, String descriptor) {
    if (!isInterface())
      throw new UnsupportedOperationException();
    PlacedMethodInfo method = (PlacedMethodInfo)getMethod(name, descriptor);
    if (method != null)
      return method;
    for (int i = 0; i < ifaceProxy.length; i++) {
      method = ifaceProxy[i].lookupInterfaceMethod(name, descriptor);
      if (method != null)
        return method;
    }
    return null;
  }

  public PlacedMethodInfo lookupDispatchMethod(int index) {
    MethodInfo[] methods = getMethods();
    for (int i = methods.length-1; i >= 0; i--) {
      PlacedMethodInfo method = (PlacedMethodInfo)methods[i];
      int[] patches = method.getPatches();
      for (int j = patches.length-1; j >= 0; j--)
        if (patches[j] == index)
          return method;
    }
    for (int i = inheritedPairs.size()-1; i >= 0; i--) {
      InheritedPair pair = (InheritedPair)inheritedPairs.get(i);
      if (pair.implIndex() == index)
        return superProxy.lookupDispatchMethod(pair.superIndex());
    }
    if (superProxy != null)
      if (!isInterface())
        if (index < superProxy.getOverridableDispatchEntryCount())
          return superProxy.lookupDispatchMethod(index);
    for (int i = ifaceProxy.length-1; i >= 0; i--)
      if (baseIndexes[i] <= index && index < baseIndexes[i]+ifaceProxy[i].getOverridableDispatchEntryCount())
        return ifaceProxy[i].lookupDispatchMethod(index-baseIndexes[i]);
    return null;
  }

  public void construct(Object cacheParams) throws IllegalAccessException, IncompatibleClassChangeException {
    RegisteredClassInfo clazz = ((Params)cacheParams).clazz;
    LoadedClassInfo sclazz = ((Params)cacheParams).sclazz;
    int sdepth = ((Params)cacheParams).sdepth;
    LoadedClassInfo[] iclazz = ((Params)cacheParams).iclazz;
    boolean[] isame = ((Params)cacheParams).isame;
  
    String packg = Syntax.getPackage(clazz.getName());
    if (sclazz != null) {
      if (!sclazz.isPublic())
        if (sdepth != sclazz.getDepth() || !packg.equals(Syntax.getPackage(sclazz.getName())))
          throw new IllegalAccessException("Cannot access superclass "+sclazz.getName()+" from class "+clazz.getName());
      if (sclazz.isInterface())
        throw new IncompatibleClassChangeException("Class "+clazz.getName()+" has interface "+sclazz.getName()+" as superclass");
    }
    for (int i = 0; i < iclazz.length; i++) {
      if (!iclazz[i].isPublic())
        if (!isame[i] || !packg.equals(Syntax.getPackage(iclazz[i].getName())))
          throw new IllegalAccessException("Cannot access interface "+iclazz[i].getName()+" from class "+clazz.getName());
      if (!iclazz[i].isInterface())
        throw new IncompatibleClassChangeException("Class "+clazz.getName()+" has class "+iclazz[i].getName()+" as interface");
    }

    underlying = clazz;
    superProxy = sclazz;
    this.sdepth = sdepth;
    ifaceProxy = iclazz;

    placeFields(this);
    placeInterfaces(this);
    placeMethods(this);
  }

  public void readFrom(InputStream is) throws IOException {
    DataInputStream in  = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);

    long rversion = in.readLong();
    underlying = new RegisteredProxy(key.getName(), rversion);

    boolean hasSuper = in.readBoolean();
    if (hasSuper) {
      superProxy = new LoadedProxy(in.readUTF(), in.readLong());
      sdepth = in.readInt();
    }

    int interfaceCount = in.readChar();
    ifaceProxy = new LoadedClassInfo[interfaceCount];
    for (int i = 0; i < interfaceCount; i++)
      ifaceProxy[i] = new LoadedProxy(in.readUTF(), in.readLong());

    staticSize = in.readLong();
    if (!Measure.isValid(staticSize))
      throw new IOException();
    staticRefsOffset = in.readLong();
    if (!Measure.isValid(staticRefsOffset))
      throw new IOException();
    staticRefsCount = in.readChar();

    instanceSize = in.readLong();
    if (!Measure.isValid(instanceSize))
      throw new IOException();
    instanceRefsOffset = in.readLong();
    if (!Measure.isValid(instanceRefsOffset))
      throw new IOException();
    instanceRefsCount = in.readChar();

    int fieldsCount = in.readChar();
    if (fieldsCount != getFieldCount())
      throw new IOException();
    for (int i = 0; i < fieldsCount; i++) {
      PlacedField field = (PlacedField)getField(i);
      field.readFrom(in);
    }

    int holesCount = in.readInt();
    if (holesCount < 0)
      throw new IOException();
    holes = new Hole[holesCount];
    for (int i = 0; i < holesCount; i++) {
      Hole hole = new Hole();
      hole.readFrom(in);
      holes[i] = hole;
    }

    baseIndexes = new int[ifaceProxy.length];
    for (int i = 0; i < baseIndexes.length; i++)
      baseIndexes[i] = in.readInt();

    staticLength = in.readInt();
    dynamicLength = in.readInt();
    if (dynamicLength < 0)
      throw new IOException();

    int methodsCount = in.readChar();
    if (methodsCount != getMethodCount())
      throw new IOException();
    for (int i = 0; i < methodsCount; i++) {
      PlacedMethod method = (PlacedMethod)getMethod(i);
      method.readFrom(in);
    }

    int pairs = in.readInt();
    if (pairs < 0)
      throw new IOException();
    for (int i = 0; i < pairs; i++)
      inheritedPairs.add(new InheritedPair(in));
  }

  public void writeTo(OutputStream os) throws IOException {
    DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);

    out.writeLong(getRegisteredVersion());

    boolean hasSuper = superProxy != null;
    out.writeBoolean(hasSuper);
    if (hasSuper) {
      out.writeUTF(superProxy.getName());
      out.writeLong(superProxy.getLoadedVersion());
      out.writeInt(sdepth);
    }

    int interfaceCount = getInterfaceCount();
    out.writeChar(interfaceCount);
    for (int i = 0; i < interfaceCount; i++) {
      out.writeUTF(ifaceProxy[i].getName());
      out.writeLong(ifaceProxy[i].getLoadedVersion());
    }

    out.writeLong(staticSize);
    out.writeLong(staticRefsOffset);
    out.writeChar(staticRefsCount);

    out.writeLong(instanceSize);
    out.writeLong(instanceRefsOffset);
    out.writeChar(instanceRefsCount);

    int fieldsCount = getFieldCount();
    out.writeChar(fieldsCount);
    for (int i = 0; i < fieldsCount; i++) {
      PlacedField field = (PlacedField)getField(i);
      field.writeTo(out);
    }

    out.writeInt(holes.length);
    for (int i = 0; i < holes.length; i++) {
      Hole hole = holes[i];
      hole.writeTo(out);
    }

    for (int i = 0; i < baseIndexes.length; i++)
      out.writeInt(baseIndexes[i]);

    out.writeInt(staticLength);
    out.writeInt(dynamicLength);

    int methodsCount = getMethodCount();
    out.writeChar(methodsCount);
    for (int i = 0; i < methodsCount; i++) {
      PlacedMethod method = (PlacedMethod)getMethod(i);
      method.writeTo(out);
    }

    out.writeInt(inheritedPairs.size());
    for (Iterator i = inheritedPairs.iterator(); i.hasNext(); ) {
      InheritedPair pair = (InheritedPair)i.next();
      pair.writeTo(out);
    }

    out.flush();
  }

  private final class PlacedField extends FilterFieldInfo implements PlacedFieldInfo {

    private long offset;

    protected PlacedField(FieldInfo underlying) {
      super(underlying);
    }

    public long getOffset() {
      return offset;
    }

    protected void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
    }

    protected void writeTo(DataOutputStream out) throws IOException {
      out.writeLong(offset);
    }

  }

  private static final class Hole {

    private long offset;
    private byte size;

    public Hole() { }

    public Hole(long offset, int size) {
      setOffset(offset);
      setSize(size);
    }

    public long getOffset() {
      return offset;
    }

    protected void setOffset(long offset) {
      if (!Measure.isValid(offset))
        throw new IllegalArgumentException();
      this.offset = offset;
    }

    public int getSize() {
      return size;
    }

    protected void setSize(int size) {
      if (size < 1 || size > 4)
        throw new IllegalArgumentException();
      this.size = (byte)size;
    }

    protected void readFrom(DataInputStream in) throws IOException {
      offset = in.readLong();
      if (!Measure.isValid(offset))
        throw new IOException();
      size = in.readByte();
      if (size < 1 || size > 4)
        throw new IOException();
    }

    protected void writeTo(DataOutputStream out) throws IOException {
      out.writeLong(offset);
      out.writeByte(size);
    }

  }

  private static void placeFields(LoadedClass clazz) {
    int fieldCount = clazz.getFieldCount();
    BitSet placed = new BitSet(fieldCount);

    LoadedClass sclazz = clazz.superClass();

    ArrayList holes = new ArrayList(0);

    long instanceSize = Measure.encode(0, 0);

    if (sclazz != null) {
      holes = sclazz.getHoles();
      instanceSize = sclazz.getInstanceSize();
      int hsize = (4-Measure.getBytes(instanceSize)%4)%4;
      if (hsize > 0) {
        holes.add(new Hole(instanceSize, hsize));
        instanceSize = Measure.incBytes(instanceSize, hsize);
      }
    }

    // fill holes bigger than 1
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic()) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'S' || id == 'C')
          for (Iterator j = holes.iterator(); j.hasNext(); ) {
            Hole hole = (Hole)j.next();
            if (hole.getSize() == 2) {
              field.setOffset(hole.getOffset());
              placed.set(i);
              j.remove();
            }
            if (hole.getSize() == 3) {
              field.setOffset(Measure.incBytes(hole.getOffset(), 1));
              placed.set(i);
              hole.setSize(1);
            }
          }
      }
    }

    // fill holes of size 1 and 3
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic()) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'B' || id == 'Z')
          for (Iterator j = holes.iterator(); j.hasNext(); ) {
            Hole hole = (Hole)j.next();
            if (hole.getSize() == 1) {
              field.setOffset(hole.getOffset());
              placed.set(i);
              j.remove();
            }
            if (hole.getSize() == 3) {
              field.setOffset(hole.getOffset());
              placed.set(i);
              hole.setOffset(Measure.incBytes(hole.getOffset(), 1));
              hole.setSize(2);
            }
          }
      }
    }

    // fill holes of size 2
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic() && !placed.get(i)) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'B' || id == 'Z')
          for (Iterator j = holes.iterator(); j.hasNext(); ) {
            Hole hole = (Hole)j.next();
            if (hole.getSize() == 2) {
              field.setOffset(hole.getOffset());
              placed.set(i);
              hole.setOffset(Measure.incBytes(hole.getOffset(), 1));
              hole.setSize(1);
            }
          }
      }
    }

    // check if 64-bit data may be kept aligned without wasting storage in 32-bit systems
    if (Measure.getBytes(instanceSize) % 8 != 0) {
      boolean unaligned = false;
      for (int i = 0; i < fieldCount; i++) {
        PlacedField field = (PlacedField)clazz.getField(i);
        if (!field.isStatic()) {
          String descriptor = field.getDescriptor();
          char id = descriptor.charAt(0);
          if (id == 'J' || id == 'D' || id == 'L' || id == '[') {
            unaligned = true;
            break;
          }
        }
      }
      boolean fills = false;
      for (int i = 0; i < fieldCount; i++) {
        PlacedField field = (PlacedField)clazz.getField(i);
        if (!field.isStatic() && !placed.get(i)) {
          String descriptor = field.getDescriptor();
          char id = descriptor.charAt(0);
          if (id == 'I' || id == 'F' || id == 'S' || id == 'C' || id == 'B' || id == 'Z') {
            fills = true;
            break;
          }
        }
      }
      if (unaligned && fills) {
        holes.add(new Hole(instanceSize, 4));
        instanceSize = Measure.incBytes(instanceSize, 4);
      }
    }

    // fill holes of size 4
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic()) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'I' || id == 'F')
          for (Iterator j = holes.iterator(); j.hasNext(); ) {
            Hole hole = (Hole)j.next();
            if (hole.getSize() == 4) {
              field.setOffset(hole.getOffset());
              placed.set(i);
              j.remove();
            }
          }
      }
    }

    // fill holes of size 4 or 2
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic() && !placed.get(i)) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'S' || id == 'C')
          for (Iterator j = holes.iterator(); j.hasNext(); ) {
            Hole hole = (Hole)j.next();
            if (hole.getSize() == 2) {
              field.setOffset(hole.getOffset());
              placed.set(i);
              j.remove();
            }
            if (hole.getSize() == 4) {
              field.setOffset(hole.getOffset());
              placed.set(i);
              hole.setOffset(Measure.incBytes(hole.getOffset(), 2));
              hole.setSize(2);
            }
          }
      }
    }

    // fill holes of size 4 or 2 or 1
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic() && !placed.get(i)) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'B' || id == 'Z')
          for (Iterator j = holes.iterator(); j.hasNext(); ) {
            Hole hole = (Hole)j.next();
            if (hole.getSize() == 1) {
              field.setOffset(hole.getOffset());
              placed.set(i);
              j.remove();
            }
            if (hole.getSize() == 2) {
              field.setOffset(hole.getOffset());
              placed.set(i);
              hole.setOffset(Measure.incBytes(hole.getOffset(), 1));
              hole.setSize(1);
            }
            if (hole.getSize() == 4) {
              field.setOffset(hole.getOffset());
              placed.set(i);
              hole.setOffset(Measure.incBytes(hole.getOffset(), 1));
              hole.setSize(3);
            }
          }
      }
    }

    // place instance longs and doubles
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic()) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'D' || id == 'J') {
          field.setOffset(instanceSize);
          instanceSize = Measure.incBytes(instanceSize, 8);
          placed.set(i);
        }
      }
    }

    clazz.setInstanceRefsOffset(instanceSize);

    int instanceRefsCount = 0;

    // place instance references
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic()) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'L' || id == '[') {
          field.setOffset(instanceSize);
          instanceSize = Measure.incReferences(instanceSize, 1);
          instanceRefsCount++;
          placed.set(i);
        }
      }
    }

    clazz.setInstanceRefsCount(instanceRefsCount);

    // place instance ints and floats
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic() && !placed.get(i)) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'I' || id == 'F') {
          field.setOffset(instanceSize);
          instanceSize = Measure.incBytes(instanceSize, 4);
          placed.set(i);
        }
      }
    }

    // place instance shorts and chars
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic() && !placed.get(i)) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'S' || id == 'C') {
          field.setOffset(instanceSize);
          instanceSize = Measure.incBytes(instanceSize, 2);
          placed.set(i);
        }
      }
    }

    // place instance bytes and booleans
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (!field.isStatic() && !placed.get(i)) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'B' || id == 'Z') {
          field.setOffset(instanceSize);
          instanceSize = Measure.incBytes(instanceSize, 1);
          placed.set(i);
        }
      }
    }

    clazz.setInstanceSize(instanceSize);

    clazz.setHoles(holes);

    long staticSize = Measure.encode(0, 0);

    // place static longs and doubles
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (field.isStatic()) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'J' || id == 'D') {
          field.setOffset(staticSize);
          staticSize = Measure.incBytes(staticSize, 8);
          placed.set(i);
        }
      }
    }

    clazz.setStaticRefsOffset(staticSize);

    int staticRefsCount = 0;

    // place static references
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (field.isStatic()) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'L' || id == '[') {
          field.setOffset(staticSize);
          staticSize = Measure.incReferences(staticSize, 1);
          staticRefsCount++;
          placed.set(i);
        }
      }
    }

    clazz.setStaticRefsCount(staticRefsCount);

    // place static ints and floats
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (field.isStatic()) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'I' || id == 'F') {
          field.setOffset(staticSize);
          staticSize = Measure.incBytes(staticSize, 4);
          placed.set(i);
        }
      }
    }

    // place static shorts and chars
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (field.isStatic()) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'S' || id == 'C') {
          field.setOffset(staticSize);
          staticSize = Measure.incBytes(staticSize, 2);
          placed.set(i);
        }
      }
    }

    // place static bytes and booleans
    for (int i = 0; i < fieldCount; i++) {
      PlacedField field = (PlacedField)clazz.getField(i);
      if (field.isStatic()) {
        String descriptor = field.getDescriptor();
        char id = descriptor.charAt(0);
        if (id == 'B' || id == 'Z') {
          field.setOffset(staticSize);
          staticSize = Measure.incBytes(staticSize, 1);
          placed.set(i);
        }
      }
    }

    clazz.setStaticSize(staticSize);
  }

  private LoadedClass superClass() {
    return superProxy == null ? null : (LoadedClass)((LoadedProxy)superProxy).loadedInfo();
  }

  private LoadedClass[] ifaceClass() {
    LoadedClass[] ifaceClass = new LoadedClass[ifaceProxy.length];
    for (int i = 0; i < ifaceClass.length; i++)
      ifaceClass[i] = (LoadedClass)((LoadedProxy)ifaceProxy[i]).loadedInfo();
    return ifaceClass;
  }

  private int searchInterface(LoadedClass clazz) {
    LoadedClass superClass = superClass();
    if (superClass != null)
      if (!isInterface()) {
        int index = superClass.searchInterface(clazz);
        if (index != -1)
          return index;
      }
    LoadedClass[] ifaceClass = ifaceClass();
    for (int i = 0; i < ifaceClass.length; i++) {
      int index = ifaceClass[i].searchInterface(clazz);
      if (index != -1)
        return baseIndexes[i]+index;
    }
    if (clazz == this)
      return 0;
    return -1;
  }

  private static void placeInterfaces(LoadedClass clazz) {
    LoadedClass sclazz = clazz.superClass();
    LoadedClass[] iclazz = clazz.ifaceClass();

    int interfaceCount = iclazz.length;

    BitSet placed = new BitSet(interfaceCount);
    int[] indexes = new int[interfaceCount];

    for (int i = 0; i < interfaceCount; i++) {
      int index = sclazz.searchInterface(iclazz[i]);
      if (index != -1) {
        indexes[i] = index;
        placed.set(i);
      }
    }

    for (int i = 0; i < interfaceCount; i++)
      if (!placed.get(i))
        for (int j = 0; j < interfaceCount; j++) 
          if (i != j) {
            int index = iclazz[j].searchInterface(iclazz[i]);
            if (index != -1) {
              indexes[i] = ~j;
              placed.set(i);
            }
          }

    int base = 0;
    if (sclazz != null && !clazz.isInterface())
      base = sclazz.getOverridableDispatchEntryCount();

    for (int i = 0; i < interfaceCount; i++)
      if (!placed.get(i)) {
        indexes[i] = base;
        base += iclazz[i].getOverridableDispatchEntryCount();
        placed.set(i);
      }

    for (int i = 0; i < interfaceCount; i++)
      if (indexes[i] < 0) {
        int j = ~indexes[i];
        while (indexes[j] < 0)
          j = ~indexes[j];
        int index = indexes[j]+iclazz[j].searchInterface(iclazz[i]);
        indexes[i] = index;
      }

    clazz.setOverridableDispatchEntryCount(base);
    clazz.baseIndexes = indexes;
  }

  private final class PlacedMethod extends FilterMethodInfo implements PlacedMethodInfo {

    private boolean virtual;
    private int index = -1;
    private boolean violates;
    private int pcount;
    private int[] patches;

    protected PlacedMethod(MethodInfo underlying) {
      super(underlying);
    }

    public boolean isVirtual() {
      return virtual;
    }

    private void setVirtual(boolean virtual) {
      this.virtual = virtual;
    }

    public boolean isPlaced() {
      return index != -1;
    }

    public int getDispatchIndex() {
      return index;
    }

    private void setDispatchIndex(int index) {
      this.index = index;
    }

    public boolean violates() {
      return violates;
    }

    private void setViolates() {
      violates = true;
    }

    public int[] getPatches() {
      boolean found = false;
      for (int i = 0; i < pcount; i++)
        if (index == patches[i]) {
          found = true;
          break;
        }
      int[] indices = new int[pcount+(found ? 0 : 1)];
      for (int i = 0; i < pcount; i++)
        indices[i] = patches[i];
      if (!found)
        indices[pcount] = index;
      return indices;
    }

    private void addPatch(int index) {
      if (patches == null)
        patches = new int[5];
      for (int i = 0; i < pcount; i++)
        if (index == patches[i])
          return;
      if (pcount == patches.length) {
        int[] tmp = patches;
        patches = new int[2*tmp.length+1];
        System.arraycopy(tmp, 0, patches, 0, tmp.length);
      }
      patches[pcount++] = index;
    }

    public String[] getContext() {
      HashSet set = new HashSet();
      ConstantPool constantPool = getConstantPool();
      CodeInfo code = getCode();
      if (this.getName().equals("<clinit>")) {
        set.add("java/lang/Error");
        set.add("java/lang/ExceptionInInitializerError");
        set.add("java/lang/Class");
      }
      if (isSynchronized()) {
        set.add("java/lang/IllegalMonitorStateException");
        set.add("java/lang/Class");
      }
      if (code == null) {
        if (this.isAbstract()) {
          set.add("java/lang/AbstractMethodError");
          set.add("java/lang/Class");
        }
      } else {
        try {
          code.checkStaticConstraints();
        } catch (VerifyException e) {
          return new String[0];
        }
        byte[] text = code.getText();
        int[] textInfo = code.getTextInfo();
        for (int offset = 0; offset < text.length; offset++)
          if (Bytecode.isValid(textInfo, offset))
            switch (Bytecode.opcodeAt(text, offset)) {
            case GETSTATIC: case PUTSTATIC:
            case INVOKESTATIC:
              int index = Bytecode.ushortAt(text, offset+1);
              set.add(constantPool.getClass(index));
              set.add("java/lang/Class");
              break;
            case NEW:
            case INSTANCEOF:
              index = Bytecode.ushortAt(text, offset+1);
              set.add(constantPool.getClass(index));
              break;
            case GETFIELD: case PUTFIELD:
            case INVOKEVIRTUAL: case INVOKESPECIAL:
              index = Bytecode.ushortAt(text, offset+1);
              set.add(constantPool.getClass(index));
              set.add("java/lang/NullPointerException");
              set.add("java/lang/Class");
              break;
            case INVOKEINTERFACE:
              index = Bytecode.ushortAt(text, offset+1);
              set.add(constantPool.getClass(index));
              set.add("java/lang/NullPointerException");
              set.add("java/lang/IncompatibleClassChangeError");
              set.add("java/lang/Class");
              break;
            case CHECKCAST:
              index = Bytecode.ushortAt(text, offset+1);
              set.add(constantPool.getClass(index));
              set.add("java/lang/ClassCastException");
              set.add("java/lang/Class");
              break;
            case BALOAD: case BASTORE:
              set.add("java/lang/NullPointerException");
              set.add("java/lang/ArrayIndexOutOfBoundsException");
              set.add("[B");
              set.add("java/lang/Class");
              break;
            case CALOAD: case SALOAD: case CASTORE: case SASTORE:
              set.add("java/lang/NullPointerException");
              set.add("java/lang/ArrayIndexOutOfBoundsException");
              set.add("[S");
              set.add("java/lang/Class");
              break;
            case IALOAD: case IASTORE:
              set.add("java/lang/NullPointerException");
              set.add("java/lang/ArrayIndexOutOfBoundsException");
              set.add("[I");
              set.add("java/lang/Class");
              break;
            case LALOAD: case LASTORE:
              set.add("java/lang/NullPointerException");
              set.add("java/lang/ArrayIndexOutOfBoundsException");
              set.add("[J");
              set.add("java/lang/Class");
              break;
            case FALOAD: case FASTORE:
              set.add("java/lang/NullPointerException");
              set.add("java/lang/ArrayIndexOutOfBoundsException");
              set.add("[F");
              set.add("java/lang/Class");
              break;
            case DALOAD: case DASTORE:
              set.add("java/lang/NullPointerException");
              set.add("java/lang/ArrayIndexOutOfBoundsException");
              set.add("[D");
              set.add("java/lang/Class");
              break;
            case AALOAD:
              set.add("java/lang/NullPointerException");
              set.add("java/lang/ArrayIndexOutOfBoundsException");
              set.add("[Ljava/lang/Object;");
              set.add("java/lang/Class");
              break;
            case AASTORE:
              set.add("java/lang/NullPointerException");
              set.add("java/lang/ArrayIndexOutOfBoundsException");
              set.add("java/lang/ArrayStoreException");
              set.add("[Ljava/lang/Object;");
              set.add("java/lang/Class");
              break;
            case NEWARRAY:
              set.add("java/lang/NegativeArraySizeException");
              set.add("java/lang/Class");
              break;
            case ANEWARRAY:
              index = Bytecode.ushortAt(text, offset+1);
              set.add(constantPool.getClass(index));
              set.add("java/lang/NegativeArraySizeException");
              set.add("java/lang/Class");
              break;
            case MULTIANEWARRAY:
              index = Bytecode.ushortAt(text, offset+1);
              set.add(constantPool.getClass(index));
              set.add("java/lang/NegativeArraySizeException");
              set.add("[Ljava/lang/Object;");
              set.add("java/lang/Class");
              break;
            case IDIV: case LDIV: case IREM: case LREM:
              set.add("java/lang/ArithmeticException");
              set.add("java/lang/Class");
              break;
            case ARRAYLENGTH:
            case ATHROW:
            case MONITORENTER:
              set.add("java/lang/NullPointerException");
              set.add("java/lang/Class");
              break;
            case MONITOREXIT:
              set.add("java/lang/NullPointerException");
              set.add("java/lang/IllegalMonitorStateException");
              set.add("java/lang/Class");
              break;
            }
      }
      return (String[])set.toArray(new String[set.size()]);
    }

    public final IRCFG getCFG(Context context) throws VerifyException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, IncompatibleClassChangeException, AbstractMethodException {
      return getCFG(context, 0);
    }

    public IRCFG getCFG(Context context, int level) throws VerifyException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, IncompatibleClassChangeException, AbstractMethodException {
      Cache cache = Cache.getContextCache();
      CacheKey cacheKey = new ConvertedMethod.Key(LoadedClass.this.getName(), context.computeVersion(), getIndex(), level);
      Object cacheParams = new ConvertedMethod.Params(this, context, level);
      ConvertedMethod object;
      try {
        object = (ConvertedMethod)cache.get(cacheKey, cacheParams);
      } catch (ConstructionCircularityException e) {
        return null;
      } catch (ConstructionTargetException e) {
        Exception target = e.getTargetException();
        if (target instanceof VerifyException)
          throw (VerifyException)target;
        if (target instanceof IllegalAccessException)
          throw (IllegalAccessException)target;
        if (target instanceof InstantiationException)
          throw (InstantiationException)target;
        if (target instanceof NoSuchFieldException)
          throw (NoSuchFieldException)target;
        if (target instanceof NoSuchMethodException)
          throw (NoSuchMethodException)target;
        if (target instanceof IncompatibleClassChangeException)
          throw (IncompatibleClassChangeException)target;
        if (target instanceof AbstractMethodException)
          throw (AbstractMethodException)target;
        throw e;
      }
      return object.getCFG();
    }

    protected void readFrom(DataInputStream in) throws IOException {
      virtual = in.readBoolean();
      index = in.readInt();
      violates = in.readBoolean();
      pcount = in.readInt();
      patches = new int[pcount];
      for (int i = 0; i < pcount; i++)
        patches[i] = in.readInt();
    }

    protected void writeTo(DataOutputStream out) throws IOException {
      out.writeBoolean(virtual);
      out.writeInt(index);
      out.writeBoolean(violates);
      out.writeInt(pcount);
      for (int i = 0; i < pcount; i++)
        out.writeInt(patches[i]);
    }

  }

  private static void placeMethods(LoadedClass clazz) {
    LoadedClass superClass = clazz.superClass();
    LoadedClass[] ifaceClass = clazz.ifaceClass();

    int staticLength = 1;
    int dynamicLength = clazz.getOverridableDispatchEntryCount();

    MethodInfo[] methodInfo = clazz.getMethods();
    BitSet statics = new BitSet(methodInfo.length);
    for (int i = 0; i < methodInfo.length; i++) {
      PlacedMethod method = (PlacedMethod)methodInfo[i];
      String mname = method.getName();
      String mdesc = method.getDescriptor();

      if (mname.equals("<clinit>")) {
        method.setDispatchIndex(0);
        statics.set(i);
        continue;
      }
        
      if (method.isStatic() || method.isPrivate() || mname.equals("<init>")) {
        method.setDispatchIndex(staticLength++);
        statics.set(i);
        continue;
      }

      if (!method.isFinal() && !clazz.isFinal())
        method.setVirtual(true);

      if (clazz.isInterface()) {

        for (int j = 0; j < ifaceClass.length; j++) {
          PlacedMethodInfo imethod = ifaceClass[j].lookupInterfaceMethod(mname, mdesc);
          if (imethod != null) {
            method.setDispatchIndex(clazz.baseIndexes[j]+imethod.getDispatchIndex());
            break;
          }
        }

      } else {

        for (LoadedClass cl = superClass; cl != null; cl = cl.superClass()) {

          boolean sameLoader = true;
          int depth = cl.getDepth();
          int ldepth = clazz.getSuperLoaderDepth();
          while (ldepth != depth) {
            if (ldepth < depth) {
              sameLoader = false;
              break;
            }
            LoadedClassInfo cl2 = clazz;
            while (cl2.getDepth() > ldepth)
              cl2 = cl2.getSuperclassClass();
            ldepth = cl2.getSuperLoaderDepth();
          }

          PlacedMethod smethod = (PlacedMethod)cl.getMethod(mname, mdesc);
          if (smethod == null)
            continue;
          if (smethod.isStatic())
            continue;
          if (smethod.isPrivate())
            continue;
          if (smethod.isVirtual()) {
            if (smethod.isPublic() || smethod.isProtected()) {
              method.addPatch(smethod.getDispatchIndex());
              if (clazz.isFinal() || (method.isPublic() || method.isProtected()))
                if (!method.isPlaced() || method.getDispatchIndex() > smethod.getDispatchIndex())
                  method.setDispatchIndex(smethod.getDispatchIndex());
              continue;
            }
            if (!Syntax.getPackage(cl.getName()).equals(Syntax.getPackage(clazz.getName())))
              continue;
            if (sameLoader) {
              method.addPatch(smethod.getDispatchIndex());
              if (clazz.isFinal() || (!method.isPublic() && !method.isProtected()))
                if (!method.isPlaced() || method.getDispatchIndex() > smethod.getDispatchIndex())
                  method.setDispatchIndex(smethod.getDispatchIndex());
            }
            continue;
          }
          if (smethod.isPublic() || smethod.isProtected()) {
            method.setViolates();
            continue;
          }
          if (!Syntax.getPackage(cl.getName()).equals(Syntax.getPackage(clazz.getName())))
            continue;
          if (sameLoader)
            method.setViolates();
        }

        clazz.addInterfacePatches(0, method);
      }

      if (!method.isPlaced())
        if (method.isVirtual())
          method.setDispatchIndex(dynamicLength++);
        else {
          method.setDispatchIndex(staticLength++);
          statics.set(i);
        }
    }

    if (!clazz.isInterface())
      clazz.addInheritedInterfacePatches(0, clazz);

    clazz.setNonOverridableDispatchEntryCount(staticLength);
    clazz.setOverridableDispatchEntryCount(dynamicLength);
    for (int i = 0; i < methodInfo.length; i++) {
      PlacedMethod method = (PlacedMethod)methodInfo[i];
      if (statics.get(i))
        method.setDispatchIndex(dynamicLength+method.getDispatchIndex());
    }
  }

  private void addInterfacePatches(int baseIndex, PlacedMethod method) {
    if (isInterface()) {
      PlacedMethodInfo imethod = (PlacedMethodInfo)getMethod(method.getName(), method.getDescriptor());
      if (imethod != null)
        if (method.isPublic()) {
          int dispatchIndex = baseIndex+imethod.getDispatchIndex();
          method.addPatch(dispatchIndex);
          if (method.getOwner().isFinal() || method.isFinal())
            if (!method.isPlaced() || method.getDispatchIndex() > dispatchIndex)
              method.setDispatchIndex(dispatchIndex);
        } else {
          //illegal access error
        }
    } else {
      LoadedClass superClass = superClass();
      if (superClass != null)
        superClass.addInterfacePatches(baseIndex, method);
    }
    LoadedClass[] ifaceClass = ifaceClass();
    for (int i = 0; i < ifaceClass.length; i++)
      ifaceClass[i].addInterfacePatches(baseIndex+baseIndexes[i], method);
  }

  private void addInheritedInterfacePatches(int baseIndex, LoadedClass clazz) {
    if (isInterface()) {
      MethodInfo[] imethods = getMethods();
      for (int i = 0; i < imethods.length; i++) {
        PlacedMethod imethod = (PlacedMethod)imethods[i];
        PlacedMethod method = (PlacedMethod)clazz.getMethod(imethod.getName(), imethod.getDescriptor());
        if (method != null && !method.isStatic() && !method.isPrivate())
          continue;
        for (LoadedClass cl = clazz.superClass(); cl != null; cl = cl.superClass()) {
          PlacedMethod smethod = (PlacedMethod)cl.getMethod(imethod.getName(), imethod.getDescriptor());
          if (smethod == null)
            continue;
          if (smethod.isStatic())
            continue;
          if (smethod.isPrivate())
            continue;
          if (smethod.isPublic()) {
            clazz.addInheritedPair(smethod.getDispatchIndex(), baseIndex+imethod.getDispatchIndex());
          } else {
            //illegal access error
          }
          break;
        }
      }
    }
    LoadedClass[] ifaceClass = ifaceClass();
    for (int i = 0; i < ifaceClass.length; i++)
      ifaceClass[i].addInheritedInterfacePatches(baseIndex+baseIndexes[i], clazz);
  }

  int getInheritedPairCount() {
    return inheritedPairs.size();
  }

  InheritedPair getInheritedPair(int index) {
    return (InheritedPair)inheritedPairs.get(index);
  }

  private void addInheritedPair(int superIndex, int implIndex) {
    inheritedPairs.add(new InheritedPair(superIndex, implIndex));
  }

  static final class InheritedPair {

    private int superIndex;
    private int implIndex;

    InheritedPair(int superIndex, int implIndex) {
      this.superIndex = superIndex;
      this.implIndex = implIndex;
    }

    InheritedPair(DataInputStream in) throws IOException {
      superIndex = in.readInt();
      if (superIndex < 0)
        throw new IOException();
      implIndex = in.readInt();
      if (implIndex < 0)
        throw new IOException();
    }

    public int superIndex() {
      return superIndex;
    }

    public int implIndex() {
      return implIndex;
    }

    void writeTo(DataOutputStream out) throws IOException {
      out.writeInt(superIndex);
      out.writeInt(implIndex);
    }

  }

  // improove with analysis
  public boolean finalizes() {
    if (!isAbstract()) {
      MethodInfo method = lookupMethod("finalize", "()V");
      if (method == null)
        return true;
      if (method.isStatic())
        return true;
      CodeInfo code = method.getCode();
      if (code == null)
        return true;
      byte[] text = code.getText();
      if (text.length != 1)
        return true;
      if (Bytecode.opcodeAt(text, 0) != RETURN)
        return true;
    }
    return false;
  }

}

