/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.LoadedClass.InheritedPair;
import jewel.core.LoadedClassInfo.PlacedFieldInfo;
import jewel.core.LoadedClassInfo.PlacedMethodInfo;
import jewel.core.bend.Backend;
import jewel.core.bend.CodeOutput;
import jewel.core.cache.Cache;
import jewel.core.cache.CacheKey;
import jewel.core.cache.CacheObject;
import jewel.core.cache.ConstructionTargetException;
import jewel.core.cache.FileCache;
import jewel.core.cache.JarCache;
import jewel.core.cache.RawCache;
import jewel.core.cache.RemoteCache;
import jewel.core.cache.SoftCache;
import jewel.core.cache.Streamable;
import jewel.core.clfile.AbstractMethodException;
import jewel.core.clfile.Bytecode;
import jewel.core.clfile.ClassFileConstants;
import jewel.core.clfile.ClassFormatException;
import jewel.core.clfile.ClassInfo;
import jewel.core.clfile.ClassInfo.ConstantPool;
import jewel.core.clfile.ClassInfo.FieldInfo;
import jewel.core.clfile.ClassInfo.InnerInfo;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo;
import jewel.core.clfile.IncompatibleClassChangeException;
import jewel.core.clfile.NoClassDefFoundException;
import jewel.core.clfile.Syntax;
import jewel.core.clfile.UnsupportedClassVersionException;
import jewel.core.clfile.VerifyException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

public final class Jewel implements ClassFileConstants {

  public static final int DEFAULT_SERVICE_PORT = 31005;

  private static Cache systemCache;

  static {
    String defaultcacheproxy = "";
    String defaultcachepath = System.getProperty("user.home")+File.separator+".jewelcache";
    if ("jewelvm".equals(System.getProperty("java.vm.name")))
      defaultcachepath += File.pathSeparator+System.getProperty("java.home")+File.separator+"lib"+File.separator+"cache";

    Cache cache = null;

    String cacheproxy = System.getProperty("jewel.vm.cache.proxy", defaultcacheproxy);
    if (cacheproxy.length() > 0) {
      String hostname = cacheproxy;
      int port = DEFAULT_SERVICE_PORT;
      int index = hostname.indexOf(':');
      if (index != -1) {
        hostname = cacheproxy.substring(0, index);
        try {
          port = Integer.parseInt(cacheproxy.substring(index+1));
          if (port < 0 || port > 65535)
            throw new NumberFormatException();
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Bad proxy port number");
        }
      }
      InetAddress address;
      try {
        address = hostname.length() == 0 ? InetAddress.getLocalHost() : InetAddress.getByName(hostname);
      } catch (UnknownHostException e) {
        throw new IllegalArgumentException("Could not locate proxy host name");
      }
      cache = new RemoteCache(address, port);
    }

    if (cache == null)
      try {
        cache = (Cache)RawCache.class.newInstance();//to avoid reachability
      } catch (InstantiationException e) {
        throw new InstantiationError(e.getMessage());
      } catch (IllegalAccessException e) {
        throw new IllegalAccessError(e.getMessage());
      }

    String cachepath = System.getProperty("jewel.vm.cache.path", defaultcachepath);
    if (cachepath.length() > 0) {
      StringTokenizer st = new StringTokenizer(cachepath, File.pathSeparator);
      File[] files = new File[st.countTokens()];
      for (int i = 0; st.hasMoreTokens(); i++) {
        String path = st.nextToken();
        files[i] = new File(path);
      }
      for (int i = files.length-1; i >= 0; i--) {
        File file = files[i];
        if (file.isDirectory())
          cache = new FileCache(cache, file);
        else
          cache = new JarCache(cache, file);
      }
    }

    cache = new SoftCache(cache);

    systemCache = cache;
  }

  public static Cache getSystemCache() {
    return systemCache;
  }

  private final Cache cache;

  private int rgtr;
  private int load;
  private int cntx;
  private int meta;
  private int link;
  private int lazy;
  private int real;

  public Jewel() {
    this(systemCache);
  }

  public Jewel(Cache cache) {
    if (cache == null)
      throw new NullPointerException();
    this.cache = cache;

    Runtime.getRuntime().addShutdownHook(
      new Thread() {
        public void run() {
          boolean verbose = System.getProperty("jewel.vm.verbose") != null;
          if (verbose) {
            System.err.println("rgtr = "+rgtr);
            System.err.println("load = "+load);
            System.err.println("link = "+link);
            System.err.println("lazy = "+lazy);
            System.err.println("cntx = "+cntx);
            System.err.println("real = "+real);
            System.err.println("meta = "+meta);
          }
        }
      }
    );

  }

  public Cache cache() {
    return cache;
  }

  /* * * * * * * * * * * * * * R E G I S T E R * * * * * * * * * * * * * */

  public Register register(String name, byte[] array, int start, int length) {
    rgtr++;
    if (start < 0 || start > array.length)
      throw new ArrayIndexOutOfBoundsException(start);
    int end = start+length;
    if (end < start || end > array.length)
      throw new ArrayIndexOutOfBoundsException(end);
    CacheKey cacheKey = new Register.Key(name, array, start, length);
    Object cacheParams = new Register.Params(name, array, start, length);
    try {
      return (Register)cache.get(cacheKey, cacheParams);
    } catch (ConstructionTargetException e) {
      Exception target = e.getTargetException();
      if (target instanceof UnsupportedClassVersionException)
        throw new UnsupportedClassVersionError(name+" ("+target.getMessage()+")");
      if (target instanceof ClassFormatException)
        throw new ClassFormatError(name+" ("+target.getMessage()+")");
      if (target instanceof NoClassDefFoundException)
        throw new NoClassDefFoundError(name+" ("+target.getMessage()+")");
      throw e;
    }
  }

  public static final class Register implements CacheObject {

    private long version;
    private char accessFlags;
    private String superclass;
    private String[] interfaces;

    public Register() { }

    public Register(long version) {
      this.version = version;
    }

    public long getVersion() { return version; }
    public int getAccessFlags() { return accessFlags; }
    public String getSuperclass() { return superclass; }
    public String[] getInterfaces() { return (String[])interfaces.clone(); }

    public void construct(Object cacheParams) {
      String name = ((Params)cacheParams).name;
      byte[] array = ((Params)cacheParams).array;
      int start = ((Params)cacheParams).start;
      int length = ((Params)cacheParams).length;

      Cache cache = Cache.getContextCache();
      Object params = new RegisteredClass.Params(name, array, start, length);
      CacheKey key = new RegisteredClass.Key(name, version);

      RegisteredClass clazz = (RegisteredClass)cache.get(key, params);
      accessFlags = (char)clazz.getAccessFlags();
      superclass = clazz.getSuperclass();
      interfaces = clazz.getInterfaces();
    }

    public void readFrom(InputStream is) throws IOException {
      DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
      version = in.readLong();
      accessFlags = in.readChar();
      boolean hasSuper = in.readBoolean();
      if (hasSuper)
        superclass = in.readUTF();
      int interfaceCount = in.readChar();
      interfaces = new String[interfaceCount];
      for (int i = 0; i < interfaces.length; i++)
        interfaces[i] = in.readUTF();
    }

    public void writeTo(OutputStream os) throws IOException {
      DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
      out.writeLong(version);
      out.writeChar(accessFlags);
      boolean hasSuper = superclass != null;
      out.writeBoolean(hasSuper);
      if (hasSuper)
        out.writeUTF(superclass);
      out.writeChar(interfaces.length);
      for (int i = 0; i < interfaces.length; i++)
        out.writeUTF(interfaces[i]);
      out.flush();
    }

    public static final class Key extends ClassID {

      private static long computeVersion(String name, byte[] array, int start, int length) {
        byte[] buffer;
        try {
          ByteArrayOutputStream bout = new ByteArrayOutputStream();
          DataOutputStream out = new DataOutputStream(bout);
          out.writeUTF(name);
          out.write(array, start, length);
          buffer = bout.toByteArray();
        } catch (IOException e) {
          throw new Error();
        }
        SHA1 sha1 = new SHA1();
        sha1.update(buffer, 0, buffer.length);
        return sha1.getValue();
      }

      public Key() { }

      public Key(String name, byte[] array, int start, int length) {
        super(name, computeVersion(name, array, start, length), "rgtr");
      }

      public CacheObject newInstance() {
        return new Register(this.getVersion());
      }

    }

    public static final class Params implements Streamable {

      public String name;
      public byte[] array;
      public int start;
      public int length;

      public Params() { }

      public Params(String name, byte[] array, int start, int length) {
        this.name = name;
        this.array = array;
        this.start = start;
        this.length = length;
      }

      public void readFrom(InputStream is) throws IOException {
        DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
        name = in.readUTF();
        length = in.readInt();
        if (length < 0)
          throw new IOException();
        array = new byte[length];
        in.readFully(array);
      }

      public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
        out.writeUTF(name);
        out.writeInt(length);
        out.write(array, start, length);
        out.flush();
      }

    }

  }

  /* * * * * * * * * * * * * * * * L O A D * * * * * * * * * * * * * * * */

  public Load load(String name, long rversion, String superclass, long sversion, int sdepth, String[] interfaces, long[] iversion, boolean[] isame) {
    load++;
    CacheKey cacheKey = new Load.Key(name, rversion, superclass, sversion, sdepth, interfaces, iversion, isame);
    Object cacheParams = new Load.Params(name, rversion, superclass, sversion, sdepth, interfaces, iversion, isame);
    try {
      return (Load)cache.get(cacheKey, cacheParams);
    } catch (ConstructionTargetException e) {
      Exception target = e.getTargetException();
      if (target instanceof IllegalAccessException)
        throw new IllegalAccessError(target.getMessage());
      if (target instanceof IncompatibleClassChangeException)
        throw new IncompatibleClassChangeError(target.getMessage());
      throw e;
    }
  }

  public static final class Load implements CacheObject {

    private long version;
    private long staticSize;
    private long staticRefsOffset;
    private char staticRefsCount;
    private long instanceSize;
    private long instanceRefsOffset;
    private char instanceRefsCount;
    private int dynamicLength;
    private int staticLength;
    private int[] interfaceOffsets;
    private char nativeSlots;

    public Load() { }

    public Load(long version) {
      this.version = version;
    }

    public long getVersion() { return version; }
    public long getStaticSize() { return staticSize; }
    public long getStaticRefsOffset() { return staticRefsOffset; }
    public int getStaticRefsCount() { return staticRefsCount; }
    public long getInstanceSize() { return instanceSize; }
    public long getInstanceRefsOffset() { return instanceRefsOffset; }
    public int getInstanceRefsCount() { return instanceRefsCount; }
    public int getOverridableDispatchEntryCount() { return dynamicLength; }
    public int getNonOverridableDispatchEntryCount() { return staticLength; }
    public int[] getInterfaceOffsets() { return (int[])interfaceOffsets.clone(); }
    public int getNativeSlots() { return nativeSlots; }
    
    public void construct(Object cacheParams) {
      String name = ((Params)cacheParams).name;
      long rversion = ((Params)cacheParams).rversion;
      String superclass = ((Params)cacheParams).superclass;
      long sversion = ((Params)cacheParams).sversion;
      int sdepth = ((Params)cacheParams).sdepth;
      String[] interfaces = ((Params)cacheParams).interfaces;
      long[] iversion = ((Params)cacheParams).iversion;
      boolean[] isame = ((Params)cacheParams).isame;

      RegisteredClassInfo rclass = new RegisteredProxy(name, rversion);
      LoadedClassInfo sclass = superclass == null ? null : new LoadedProxy(superclass, sversion);
      LoadedClassInfo[] iclass = new LoadedClassInfo[interfaces.length];
      for (int i = 0; i < iclass.length; i++)
        iclass[i] = new LoadedProxy(interfaces[i], iversion[i]);

      Cache cache = Cache.getContextCache();
      CacheKey key = new LoadedClass.Key(name, version);
      Object params = new LoadedClass.Params(rclass, sclass, sdepth, iclass, isame);

      LoadedClassInfo clazz = (LoadedClassInfo)cache.get(key, params);
      staticSize = clazz.getStaticSize();
      staticRefsOffset = clazz.getStaticRefsOffset();
      staticRefsCount = (char)clazz.getStaticRefsCount();
      instanceSize = clazz.getInstanceSize();
      instanceRefsOffset = clazz.getInstanceRefsOffset();
      instanceRefsCount = (char)clazz.getInstanceRefsCount();
      dynamicLength = clazz.getOverridableDispatchEntryCount();
      staticLength = clazz.getNonOverridableDispatchEntryCount();
      interfaceOffsets = clazz.getInterfaceBaseIndexes();
      nativeSlots = 0;
      for (int i = 0; i < clazz.getMethodCount(); i++) {
        MethodInfo method = clazz.getMethod(i);
        if (method.isNative())
          nativeSlots++;
      }
    }

    public void readFrom(InputStream is) throws IOException {
      DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
      version = in.readLong();
      staticSize = in.readLong();
      staticRefsOffset = in.readLong();
      staticRefsCount = in.readChar();
      instanceSize = in.readLong();
      instanceRefsOffset = in.readLong();
      instanceRefsCount = in.readChar();
      dynamicLength = in.readInt();
      staticLength = in.readInt();
      int interfaceCount = in.readChar();
      interfaceOffsets = new int[interfaceCount];
      for (int i = 0; i < interfaceOffsets.length; i++)
        interfaceOffsets[i] = in.readInt();
      nativeSlots = in.readChar();
    }

    public void writeTo(OutputStream os) throws IOException {
      DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
      out.writeLong(version);
      out.writeLong(staticSize);
      out.writeLong(staticRefsOffset);
      out.writeChar(staticRefsCount);
      out.writeLong(instanceSize);
      out.writeLong(instanceRefsOffset);
      out.writeChar(instanceRefsCount);
      out.writeInt(dynamicLength);
      out.writeInt(staticLength);
      out.writeChar(interfaceOffsets.length);
      for (int i = 0; i < interfaceOffsets.length; i++)
        out.writeInt(interfaceOffsets[i]);
      out.writeChar(nativeSlots);
      out.flush();
    }

    public static final class Key extends ClassID {

      private static long computeVersion(String name, long rversion, String superclass, long sversion, int sdepth, String[] interfaces, long[] iversion, boolean[] isame) {
        byte[] buffer;
        try {
          ByteArrayOutputStream bout = new ByteArrayOutputStream();
          DataOutputStream out = new DataOutputStream(bout);
          out.writeUTF(name);
          out.writeLong(rversion);
          boolean hasSuper = superclass != null;
          out.writeBoolean(hasSuper);
          if (hasSuper) {
            out.writeUTF(superclass);
            out.writeLong(sversion);
            out.writeInt(sdepth);
          }
          out.writeChar(interfaces.length);
          for (int i = 0; i < interfaces.length; i++) {
            out.writeUTF(interfaces[i]);
            out.writeLong(iversion[i]);
            out.writeBoolean(isame[i]);
          }
          buffer = bout.toByteArray();
        } catch (IOException e) {
          throw new Error();
        }
        SHA1 sha1 = new SHA1();
        sha1.update(buffer, 0, buffer.length);
        return sha1.getValue();
      }

      public Key() { }

      public Key(String name, long rversion, String superclass, long sversion, int sdepth, String[] interfaces, long[] iversion, boolean[] isame) {
        super(name, computeVersion(name, rversion, superclass, sversion, sdepth, interfaces, iversion, isame), "load");
      }

      public CacheObject newInstance() {
        return new Load(this.getVersion());
      }

    }

    public static final class Params implements Streamable {

      public String name;
      public long rversion;
      public String superclass;
      public long sversion;
      public int sdepth;
      public String[] interfaces;
      public long[] iversion;
      public boolean[] isame;

      public Params() { }

      public Params(String name, long rversion, String superclass, long sversion, int sdepth, String[] interfaces, long[] iversion, boolean[] isame) {
        this.name = name;
        this.rversion = rversion;
        this.superclass = superclass;
        this.sversion = sversion;
        this.sdepth = sdepth;
        this.interfaces = interfaces;
        this.iversion = iversion;
        this.isame = isame;
      }

      public void readFrom(InputStream is) throws IOException {
        DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
        name = in.readUTF();
        rversion = in.readLong();
        boolean hasSuper = in.readBoolean();
        if (hasSuper) {
          superclass = in.readUTF();
          sversion = in.readLong();
          sdepth = in.readInt();
        }
        int interfaceCount = in.readChar();
        interfaces = new String[interfaceCount];
        iversion = new long[interfaceCount];
        isame = new boolean[interfaceCount];
        for (int i = 0; i < interfaces.length; i++) {
          interfaces[i] = in.readUTF();
          iversion[i] = in.readLong();
          isame[i] = in.readBoolean();
        }
      }

      public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
        out.writeUTF(name);
        out.writeLong(rversion);
        boolean hasSuper = superclass != null;
        out.writeBoolean(hasSuper);
        if (hasSuper) {
          out.writeUTF(superclass);
          out.writeLong(sversion);
          out.writeInt(sdepth);
        }
        out.writeChar(interfaces.length);
        for (int i = 0; i < interfaces.length; i++) {
          out.writeUTF(interfaces[i]);
          out.writeLong(iversion[i]);
          out.writeBoolean(isame[i]);
        }
        out.flush();
      }

    }

  }

  /* * * * * * * * * * * * * * C O N T E X T * * * * * * * * * * * * * * */

  public Context context(String name, long version, int index) {
    cntx++;
    CacheKey cacheKey = new Context.Key(name, version, index);
    Object cacheParams = new Context.Params(name, version, index);
    return (Context)cache.get(cacheKey, cacheParams);
  }

  public static final class Context implements CacheObject {

    private String[] classes;

    public Context() { }

    public String[] getClasses() { return (String[])classes.clone(); }

    public void construct(Object cacheParams) {
      String name = ((Params)cacheParams).name;
      long version = ((Params)cacheParams).version;
      int index = ((Params)cacheParams).index;

      LoadedClassInfo clazz = new LoadedProxy(name, version);
      PlacedMethodInfo method = (PlacedMethodInfo)clazz.getMethod(index);

      classes = method.getContext();
    }

    public void readFrom(InputStream is) throws IOException {
      DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
      int classCount = in.readInt();
      if (classCount < 0)
        throw new IOException();
      classes = new String[classCount];
      for (int i = 0; i < classes.length; i++)
        classes[i] = in.readUTF();
    }

    public void writeTo(OutputStream os) throws IOException {
      DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
      out.writeInt(classes.length);
      for (int i = 0; i < classes.length; i++)
        out.writeUTF(classes[i]);
      out.flush();
    }

    public static final class Key extends MethodID {
      public Key() { }
      public Key(String name, long version, int index) { super(name, version, index, "cntx"); }
      public CacheObject newInstance() { return new Context(); }
    }

    public static final class Params implements Streamable {
      public String name;
      public long version;
      public char index;
      public Params() { }
      public Params(String name, long version, int index) {
        this.name = name;
        this.version = version;
        this.index = (char)index;
      }
      public void readFrom(InputStream is) throws IOException {
        DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
        name = in.readUTF();
        version = in.readLong();
        index = in.readChar();
      }
      public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
        out.writeUTF(name);
        out.writeLong(version);
        out.writeChar(index);
        out.flush();
      }
    }

  }

  /* * * * * * * * * * * * * * * * M E T A * * * * * * * * * * * * * * * */

  public Meta meta(String name, long version) {
    meta++;
    CacheKey cacheKey = new Meta.Key(name, version);
    Object cacheParams = new Meta.Params(name, version);
    return (Meta)cache.get(cacheKey, cacheParams);
  }

  public static final class Meta implements CacheObject {

    private char accessFlags;
    private char[] fieldFlags;
    private String[] fieldName;
    private String[] fieldDesc;
    private long[] fieldOffset;
    private char[] methodFlags;
    private String[] methodName;
    private String[] methodDesc;
    private String[][] methodExceptions;
    private int[] methodDispatchIndex;
    private String declaringClass;
    private char[] innerFlags;
    private String[] innerName;
    private String sourceFile;

    public Meta() { }

    public char getAccessFlags() { return accessFlags; }
    public char[] getFieldFlags() { return (char[])fieldFlags.clone(); }
    public String[] getFieldName() { return (String[])fieldName.clone(); }
    public String[] getFieldDesc() { return (String[])fieldDesc.clone(); }
    public long[] getFieldOffset() { return (long[])fieldOffset.clone(); }
    public char[] getMethodFlags() { return (char[])methodFlags.clone(); }
    public String[] getMethodName() { return (String[])methodName.clone(); }
    public String[] getMethodDesc() { return (String[])methodDesc.clone(); }
    public String[][] getMethodExceptions() {
      String[][] clone =  (String[][])methodExceptions.clone();
      for (int i = 0; i < clone.length; i++)
        clone[i] = (String[])clone[i].clone();
      return clone;
    }
    public int[] getMethodDispatchIndex() { return (int[])methodDispatchIndex.clone(); }
    public String getDeclaringClass() { return declaringClass; }
    public char[] getInnerFlags() { return (char[])innerFlags.clone(); }
    public String[] getInnerName() { return (String[])innerName.clone(); }
    public String getSourceFile() { return sourceFile; }

    public void construct(Object cacheParams) {
      String name = ((Params)cacheParams).name;
      long version = ((Params)cacheParams).version;

      LoadedClassInfo clazz = new LoadedProxy(name, version);

      accessFlags = (char)clazz.getAccessFlags();
      int fieldCount = clazz.getFieldCount();
      fieldFlags = new char[fieldCount];
      fieldName = new String[fieldCount];
      fieldDesc = new String[fieldCount];
      fieldOffset = new long[fieldCount];
      for (int i = 0; i < fieldCount; i++) {
        PlacedFieldInfo field = (PlacedFieldInfo)clazz.getField(i);
        fieldFlags[i] = (char)field.getAccessFlags();
        fieldName[i] = field.getName();
        fieldDesc[i] = field.getDescriptor();
        fieldOffset[i] = field.getOffset();
      }
      int methodCount = clazz.getMethodCount();
      methodFlags = new char[methodCount];
      methodName = new String[methodCount];
      methodDesc = new String[methodCount];
      methodExceptions = new String[methodCount][];
      methodDispatchIndex = new int[methodCount];
      for (int i = 0; i < methodCount; i++) {
        PlacedMethodInfo method = (PlacedMethodInfo)clazz.getMethod(i);
        methodFlags[i] = (char)method.getAccessFlags();
        methodName[i] = method.getName();
        methodDesc[i] = method.getDescriptor();
        String[] exceptions = method.getExceptions();
        if (exceptions == null)
          exceptions = new String[0];
        methodExceptions[i] = exceptions;
        methodDispatchIndex[i] = method.getDispatchIndex();
      }
      int innerCount = clazz.getInnerClassCount();
      for (int i = 0; i < innerCount; i++) {
        InnerInfo inner = clazz.getInnerClass(i);
        if (name.equals(inner.getInnerClass())) {
          accessFlags = (char)inner.getAccessFlags();
          declaringClass = inner.getOuterClass();
          break;
        }
      }
      int innerTotal = 0;
      for (int i = 0; i < innerCount; i++) {
        InnerInfo inner = clazz.getInnerClass(i);
        if (name.equals(inner.getOuterClass()))
          innerTotal++;
      }
      innerFlags = new char[innerTotal];
      innerName = new String[innerTotal];
      innerTotal = 0;
      for (int i = 0; i < innerCount; i++) {
        InnerInfo inner = clazz.getInnerClass(i);
        if (name.equals(inner.getOuterClass())) {
          innerFlags[innerTotal] = (char)inner.getAccessFlags();
          innerName[innerTotal] = inner.getInnerClass();
          innerTotal++;
        }
      }
      sourceFile = clazz.getSourceFile();
    }

    public void readFrom(InputStream is) throws IOException {
      DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
      accessFlags = in.readChar();
      int fieldCount = in.readChar();
      fieldFlags = new char[fieldCount];
      fieldName = new String[fieldCount];
      fieldDesc = new String[fieldCount];
      fieldOffset = new long[fieldCount];
      for (int i = 0; i < fieldCount; i++) {
        fieldFlags[i] = in.readChar();
        fieldName[i] = in.readUTF();
        fieldDesc[i] = in.readUTF();
        fieldOffset[i] = in.readLong();
      }
      int methodCount = in.readChar();
      methodFlags = new char[methodCount];
      methodName = new String[methodCount];
      methodDesc = new String[methodCount];
      methodExceptions = new String[methodCount][];
      methodDispatchIndex = new int[methodCount];
      for (int i = 0; i < methodCount; i++) {
        methodFlags[i] = in.readChar();
        methodName[i] = in.readUTF();
        methodDesc[i] = in.readUTF();
        int exceptionCount = in.readChar();
        String[] exceptions = new String[exceptionCount];
        for (int j = 0; j < exceptionCount; j++)
          exceptions[j] = in.readUTF();
        methodExceptions[i] = exceptions;
        methodDispatchIndex[i]= in.readInt();
      }
      boolean hasDeclaring = in.readBoolean();
      if (hasDeclaring)
        declaringClass = in.readUTF();
      int innerCount = in.readChar();
      innerFlags = new char[innerCount];
      innerName = new String[innerCount];
      for (int i = 0; i < innerCount; i++) {
        innerFlags[i] = in.readChar();
        innerName[i] = in.readUTF();
      }
      boolean hasSource = in.readBoolean();
      if (hasSource)
        sourceFile = in.readUTF();
    }

    public void writeTo(OutputStream os) throws IOException {
      DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
      out.writeChar(accessFlags);
      out.writeChar(fieldFlags.length);
      for (int i = 0; i < fieldFlags.length; i++) {
        out.writeChar(fieldFlags[i]);
        out.writeUTF(fieldName[i]);
        out.writeUTF(fieldDesc[i]);
        out.writeLong(fieldOffset[i]);
      }
      out.writeChar(methodFlags.length);
      for (int i = 0; i < methodFlags.length; i++) {
        out.writeChar(methodFlags[i]);
        out.writeUTF(methodName[i]);
        out.writeUTF(methodDesc[i]);
        String[] exceptions = methodExceptions[i];
        out.writeChar(exceptions.length);
        for (int j = 0; j < exceptions.length; j++)
          out.writeUTF(exceptions[j]);
        out.writeInt(methodDispatchIndex[i]);
      }
      boolean hasDeclaring = declaringClass != null;
      out.writeBoolean(hasDeclaring);
      if (hasDeclaring)
        out.writeUTF(declaringClass);
      out.writeChar(innerFlags.length);
      for (int i = 0; i < innerFlags.length; i++) {
        out.writeChar(innerFlags[i]);
        out.writeUTF(innerName[i]);
      }
      boolean hasSource = sourceFile != null;
      out.writeBoolean(hasSource);
      if (hasSource)
        out.writeUTF(sourceFile);
      out.flush();
    }

    public static final class Key extends ClassID {
      public Key() { }
      public Key(String name, long version) { super(name, version, "meta"); }
      public CacheObject newInstance() { return new Meta(); }
    }

    public static final class Params implements Streamable {
      public String name;
      public long version;
      public Params() { }
      public Params(String name, long version) {
        this.name = name;
        this.version = version;
      }
      public void readFrom(InputStream is) throws IOException {
        DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
        name = in.readUTF();
        version = in.readLong();
      }
      public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
        out.writeUTF(name);
        out.writeLong(version);
        out.flush();
      }
    }

  }

  /* * * * * * * * * * * * * * * * L I N K * * * * * * * * * * * * * * * */

  public Link link(String name, long version) {
    link++;
    CacheKey cacheKey = new Link.Key(name, version);
    Object cacheParams = new Link.Params(name, version);
    try {
      return (Link)cache.get(cacheKey, cacheParams);
    } catch (ConstructionTargetException e) {
      Exception target = e.getTargetException();
      if (target instanceof VerifyException)
        throw new VerifyError(target.getMessage());
      throw e;
    }
  }

  public static final class Link implements CacheObject {

    private static void checkLink(LoadedClassInfo clazz) throws VerifyException {
      LoadedClassInfo sclazz = clazz.getSuperclassClass();
      if (clazz.isInterface()) {
        if (sclazz == null || sclazz.getSuperclassClass() != null)
          throw new VerifyException("Interface "+clazz.getName()+" has bad superclass");
      } else {
        if (sclazz != null)
          if (sclazz.isFinal())
            throw new VerifyException("Class "+clazz.getName()+" is subclass of final class "+clazz.getSuperclass());
      }
      // begin review
  /*    int methodsCount = clazz.getMethodCount();
      for (int i = 0; i < methodsCount; i++) {
        DisplacedMethod method = (DisplacedMethod)loadedClass.getMethod(i);

        MethodInfo vmethod = method.getFinalViolated(null);

        if (vmethod != null) {
          signalError(Jewel.EVERIFY, "Class "+loadedClass.getName()+" overrides final method "+vmethod.getName()+"."+vmethod.getDescriptor());
          return;
        }

      }*/
      // end review
    }

    private String[] symbolicLoader;
    private String[] commonClass;
    private int[] methodIndex;
    private int[] implementationIndex;
    private boolean finalizes;
    private int[][] patches;

    public Link() { }

    public String[] getSymbolicLoader() { return (String[])symbolicLoader.clone(); }
    public String[] getCommonClass() { return (String[])commonClass.clone(); }
    public int[] getMethodIndex() { return (int[])methodIndex.clone(); }
    public int[] getImplementationIndex() { return (int[])implementationIndex.clone(); }
    public boolean getFinalizes() { return finalizes; }
    public int[][] getPatches() {
      int[][] clone = (int[][])patches.clone();
      for (int i = 0; i < clone.length; i++)
        clone[i] = (int[])clone[i].clone();
      return clone;
    }

    public void construct(Object cacheParams) throws VerifyException {
      String name = ((Params)cacheParams).name;
      long version = ((Params)cacheParams).version;

      LoadedClassInfo proxy = new LoadedProxy(name, version);
      LoadedClass clazz = (LoadedClass)((LoadedProxy)proxy).loadedInfo();

      checkLink(proxy);
      // review loading contraints
      symbolicLoader = new String[0];
      commonClass = new String[0];
      int inheritedCount = clazz.getInheritedPairCount();
      methodIndex = new int[inheritedCount];
      implementationIndex = new int[inheritedCount];
      for (int i = 0; i < inheritedCount; i++) {
        InheritedPair inherited = clazz.getInheritedPair(i);
        methodIndex[i] = inherited.superIndex();
        implementationIndex[i] = inherited.implIndex();
      }
      finalizes = clazz.finalizes();
      int methodCount = clazz.getMethodCount();
      patches = new int[methodCount][];
      for (int i = 0; i < patches.length; i++) {
        PlacedMethodInfo method = (PlacedMethodInfo)clazz.getMethod(i);
        patches[i] = method.getPatches();
      }
    }

    public void readFrom(InputStream is) throws IOException {
      DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
      int loadingCount = in.readInt();
      if (loadingCount < 0)
        throw new IOException();
      symbolicLoader = new String[loadingCount];
      commonClass = new String[loadingCount];
      for (int i = 0; i < loadingCount; i++) {
        symbolicLoader[i] = in.readUTF();
        commonClass[i] = in.readUTF();
      }
      int implementationCount = in.readInt();
      if (implementationCount < 0)
        throw new IOException();
      methodIndex = new int[implementationCount];
      implementationIndex = new int[implementationCount];
      for (int i = 0; i < implementationCount; i++) {
        methodIndex[i] = in.readInt();
        implementationIndex[i] = in.readInt();
      }
      finalizes = in.readBoolean();
      int methodCount = in.readChar();
      patches = new int[methodCount][];
      for (int i = 0; i < patches.length; i++) {
        int patchCount = in.readInt();
        if (patchCount < 0)
          throw new IOException();
        patches[i] = new int[patchCount];
        for (int j = 0; j < patchCount; j++)
          patches[i][j] = in.readInt();
      }
    }

    public void writeTo(OutputStream os) throws IOException {
      DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
      int loadingCount = symbolicLoader.length;
      out.writeInt(loadingCount);
      for (int i = 0; i < loadingCount; i++) {
        out.writeUTF(symbolicLoader[i]);
        out.writeUTF(commonClass[i]);
      }
      int implementationCount = methodIndex.length;
      out.writeInt(implementationCount);
      for (int i = 0; i < implementationCount; i++) {
        out.writeInt(methodIndex[i]);
        out.writeInt(implementationIndex[i]);
      }
      out.writeBoolean(finalizes);
      out.writeChar(patches.length);
      for (int i = 0; i < patches.length; i++) {
        out.writeInt(patches[i].length);
        for (int j = 0; j < patches[i].length; j++)
          out.writeInt(patches[i][j]);
      }
      out.flush();
    }

    public static final class Key extends ClassID implements Streamable {

      public Key() { }

      public Key(String className, long version) {
        super(className, version, "link");
      }

      public CacheObject newInstance() {
        return new Link();
      }

    }

    public static final class Params implements Streamable {

      public String name;
      public long version;

      public Params() { }

      public Params(String name, long version) {
        this.name = name;
        this.version = version;
      }

      public void readFrom(InputStream is) throws IOException {
        DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
        name = in.readUTF();
        version = in.readLong();
      }

      public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
        out.writeUTF(name);
        out.writeLong(version);
        out.flush();
      }

    }

  }

  /* * * * * * * * * * * * * * * * L A Z Y * * * * * * * * * * * * * * * */

  public LazyTranslate lazyTranslate(String name, long version, String backend) {
    lazy++;
    CacheKey cacheKey = new LazyTranslate.Key(name, version, backend);
    Object cacheParams = new LazyTranslate.Params(name, version, backend);
    return (LazyTranslate)cache.get(cacheKey, cacheParams);
  }

  public static final class LazyTranslate implements CacheObject {

    private CodeOutput[] codeOutput;

    public LazyTranslate() { }

    public CodeOutput[] getCodeOutput() { return (CodeOutput[])codeOutput.clone(); }

    public void construct(Object cacheParams) {
      String name = ((Params)cacheParams).name;
      long version = ((Params)cacheParams).version;
      String backend = ((Params)cacheParams).backend;

      LoadedClassInfo clazz = new LoadedProxy(name, version);
      Backend be = Backend.forName(backend);

      int methodCount = clazz.getMethodCount();
      codeOutput = new CodeOutput[methodCount];
      for (int i = 0; i < codeOutput.length; i++) {
        PlacedMethodInfo method = (PlacedMethodInfo)clazz.getMethod(i);
        codeOutput[i] = be.lazyTranslate(method);
      }
    }

    public void readFrom(InputStream is) throws IOException {
      DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
      int methodCount = in.readChar();
      codeOutput = new CodeOutput[methodCount];
      for (int i = 0; i < codeOutput.length; i++) {
        codeOutput[i] = new CodeOutput();
        codeOutput[i].readFrom(in);
      }
    }

    public void writeTo(OutputStream os) throws IOException {
      DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
      out.writeChar(codeOutput.length);
      for (int i = 0; i < codeOutput.length; i++)
        codeOutput[i].writeTo(out);
      out.flush();
    }

    public static final class Key extends ClassID {
      public Key() { }
      public Key(String name, long version, String backend) { super(name, version, "lazy", backend); }
      public CacheObject newInstance() { return new LazyTranslate(); }
    }

    public static final class Params implements Streamable {
      public String name;
      public long version;
      public String backend;
      public Params() { }
      public Params(String name, long version, String backend) {
        this.name = name;
        this.version = version;
        this.backend = backend;
      }
      public void readFrom(InputStream is) throws IOException {
        DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
        name = in.readUTF();
        version = in.readLong();
        backend = in.readUTF();
      }
      public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
        out.writeUTF(name);
        out.writeLong(version);
        out.writeUTF(backend);
        out.flush();
      }
    }

  }

  /* * * * * * * * * * * * * * * * R E A L * * * * * * * * * * * * * * * */

  public RealTranslate realTranslate(String name, long version, int index, String[] cname, long[] cversion, boolean[] cinitialized, int[] cdepth, int level, String backend) {
    real++;
    CacheKey cacheKey = new RealTranslate.Key(name, version, index, cname, cversion, cinitialized, cdepth, level, backend);
    Object cacheParams = new RealTranslate.Params(name, version, index, cname, cversion, cinitialized, cdepth, level, backend);
    try {
      return (RealTranslate)cache.get(cacheKey, cacheParams);
    } catch (ConstructionTargetException e) {
      Exception target = e.getTargetException();
      if (target instanceof VerifyException)
        throw new VerifyError(target.getMessage());
      if (target instanceof NoSuchFieldException)
        throw new NoSuchFieldError(target.getMessage());
      if (target instanceof NoSuchMethodException)
        throw new NoSuchMethodError(target.getMessage());
      if (target instanceof InstantiationException)
        throw new InstantiationError(target.getMessage());
      if (target instanceof AbstractMethodException)
        throw new AbstractMethodError(target.getMessage());
      if (target instanceof IllegalAccessException)
        throw new IllegalAccessError(target.getMessage());
      if (target instanceof IncompatibleClassChangeException)
        throw new IncompatibleClassChangeError(target.getMessage());
      throw e;
    }
  }

  public static final class RealTranslate implements CacheObject {

    private String[] targetClass;
    private String[][] sourceClass;
    private Object relocatableObject;

    public RealTranslate() { }

    public String[] getTargetClass() { return (String[])targetClass.clone(); }
    public String[][] getSourceClass() {
      String[][] clone = (String[][])sourceClass.clone();
      for (int i = 0; i < clone.length; i++)
        clone[i] = (String[])clone[i].clone();
      return clone;
    }
    public Object getObject() { return relocatableObject; }

    public void construct(Object cacheParams) throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException, IncompatibleClassChangeException, AbstractMethodException, InstantiationException, VerifyException {
      String name = ((Params)cacheParams).name;
      long version = ((Params)cacheParams).version;
      int index = ((Params)cacheParams).index;
      String[] cname = ((Params)cacheParams).cname;
      long[] cversion = ((Params)cacheParams).cversion;
      boolean[] cinitialized = ((Params)cacheParams).cinitialized;
      int[] cdepth = ((Params)cacheParams).cdepth;
      int level = ((Params)cacheParams).level;
      String backend = ((Params)cacheParams).backend;

      boolean verbose = System.getProperty("jewel.vm.verbose") != null;
      long start = System.currentTimeMillis();

      LoadedClassInfo clazz = new LoadedProxy(name, version);
      PlacedMethodInfo method = (PlacedMethodInfo)clazz.getMethod(index);

      int contextCount = cname.length;
      ContextClassInfo[] cclazz = new ContextClassInfo[contextCount];
      for (int i = 0; i < contextCount; i++)
        cclazz[i] = new ContextProxy(cname[i], cversion[i], cinitialized[i], cdepth[i]);

      jewel.core.Context context = new jewel.core.Context(clazz, cclazz);
      jewel.core.jiro.IRCFG cfg = method.getCFG(context, level);

      ConstraintNamespace namespace = cfg.getNamespace();
      String[][] targets = namespace.getTargets();
      targetClass = new String[targets.length];
      for (int i = 0; i < targets.length; i++) {
        String[] names = targets[i];
        if (names.length != 1)
          throw new Error();
        targetClass[i] = names[0];
      }
      sourceClass = namespace.getSources();

      relocatableObject = cfg;
      if (!backend.equals("raw")) {
        Backend be = Backend.forName(backend);
        jewel.core.Context previous = jewel.core.Context.get();
        jewel.core.Context.set(context);
        try {
          relocatableObject = be.translate(method, cfg);
        } finally {
          jewel.core.Context.set(previous);
        }
      }

      long end = System.currentTimeMillis();
      if (verbose) {
        System.err.println(" Translation: "+((float)(end-start)/1000)+"s");
        System.err.println();
      }

    }

    public void readFrom(InputStream is) throws IOException {
      DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
      int verifyCount = in.readInt();
      if (verifyCount < 0)
        throw new IOException();
      targetClass = new String[verifyCount];
      sourceClass = new String[verifyCount][];
      for (int i = 0; i < verifyCount; i++) {
        targetClass[i] = in.readUTF();
        int sourceCount = in.readInt();
        if (sourceCount < 1)
          throw new IOException();
        sourceClass[i] = new String[sourceCount];
        for (int j = 0; j < sourceCount; j++)
          sourceClass[i][j] = in.readUTF();
      }
      boolean israw = in.readBoolean();
      if (israw) {
        relocatableObject = new jewel.core.jiro.IRCFG();
        ((jewel.core.jiro.IRCFG)relocatableObject).readFrom(in);
      } else {
        relocatableObject = new CodeOutput();
        ((CodeOutput)relocatableObject).readFrom(in);
      }
    }

    public void writeTo(OutputStream os) throws IOException {
      DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
      int targetCount = targetClass.length;
      out.writeInt(targetCount);
      for (int i = 0; i < targetCount; i++) {
        out.writeUTF(targetClass[i]);
        String[] sources = sourceClass[i];
        int sourceCount = sources.length;
        out.writeInt(sourceCount);
        for (int j = 0; j < sourceCount; j++)
          out.writeUTF(sources[j]);
      }
      boolean israw = relocatableObject instanceof jewel.core.jiro.IRCFG;
      out.writeBoolean(israw);
      if (israw)
        ((jewel.core.jiro.IRCFG)relocatableObject).writeTo(out);
      else
        ((CodeOutput)relocatableObject).writeTo(out);
      out.flush();
    }

    public static final class Key extends MethodID {

      private static long computeVersion(String name, long version, String[] cname, long[] cversion, boolean[] cinitialized, int[] cdepth) {
        byte[] buffer;
        try {
          ByteArrayOutputStream bout = new ByteArrayOutputStream();
          DataOutputStream out = new DataOutputStream(bout);
          out.writeUTF(name);
          out.writeLong(version);
          int contextCount = cname.length;
          for (int i = 0; i < contextCount; i++) {
            out.writeUTF(cname[i]);
            out.writeLong(cversion[i]);
            out.writeBoolean(cinitialized[i]);
            out.writeInt(cdepth[i]);
          }
          buffer = bout.toByteArray();
        } catch (IOException e) {
          throw new Error();
        }
        SHA1 sha1 = new SHA1();
        sha1.update(buffer, 0, buffer.length);
        return sha1.getValue();
      }

      public Key() { }

      public Key(String name, long version, int index, String[] cname, long[] cversion, boolean[] cinitialized, int[] cdepth, int level, String backend) {
        super(name, computeVersion(name, version, cname, cversion, cinitialized, cdepth), index, level, "trlt", backend);
      }

      public CacheObject newInstance() {
        return new RealTranslate();
      }

    }

    public static final class Params implements Streamable {

      public String name;
      public long version;
      public char index;
      public String[] cname;
      public long[] cversion;
      public boolean[] cinitialized;
      public int[] cdepth;
      public int level;
      public String backend;

      public Params() { }

      public Params(String name, long version, int index, String[] cname, long[] cversion, boolean[] cinitialized, int[] cdepth, int level, String backend) {
        this.name = name;
        this.version = version;
        this.index = (char)index;
        this.cname = cname;
        this.cversion = cversion;
        this.cinitialized = cinitialized;
        this.cdepth = cdepth;
        this.level = level;
        this.backend = backend;
      }

      public void readFrom(InputStream is) throws IOException {
        DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);
        name = in.readUTF();
        version = in.readLong();
        index = in.readChar();
        int contextCount = in.readInt();
        cname = new String[contextCount];
        cversion = new long[contextCount];
        cinitialized = new boolean[contextCount];
        cdepth = new int[contextCount];
        for (int i = 0; i < contextCount; i++) {
          cname[i] = in.readUTF();
          cversion[i] = in.readLong();
          cinitialized[i] = in.readBoolean();
          cdepth[i] = in.readInt();
        }
        level = in.readInt();
        backend = in.readUTF();
      }

      public void writeTo(OutputStream os) throws IOException {
        DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);
        out.writeUTF(name);
        out.writeLong(version);
        out.writeChar(index);
        int contextCount = cname.length;
        out.writeInt(contextCount);
        for (int i = 0; i < contextCount; i++) {
          out.writeUTF(cname[i]);
          out.writeLong(cversion[i]);
          out.writeBoolean(cinitialized[i]);
          out.writeInt(cdepth[i]);
        }
        out.writeInt(level);
        out.writeUTF(backend);
        out.flush();
      }

    }

  }

}

