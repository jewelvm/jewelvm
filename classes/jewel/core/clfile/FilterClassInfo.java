/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.clfile;

import jewel.core.clfile.ClassInfo.ConstantPool;
import jewel.core.clfile.ClassInfo.FieldInfo;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo;
import jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo.HandlerInfo;
import jewel.core.clfile.ClassInfo.InnerInfo;

/**
 * This class is a concrete implementation of the
 * interface <CODE>ClassInfo</CODE>, and other related
 * interfaces, that provides filtering.
 */
public class FilterClassInfo implements ClassInfo {

  /**
   * The underlying <CODE>ClassInfo</CODE> instance being
   * filtered by this <CODE>FilterClassInfo</CODE> object.
   */
  protected /*final*/ ClassInfo underlying;

  private transient ConstantPool constantPool;
  private transient FieldInfo[] fields;
  private transient MethodInfo[] methods;
  private transient InnerInfo[] inners;

  /**
   * This is an alternate constructor for <CODE>FilterClassInfo</CODE>
   * objects. The <CODE>underlying</CODE> field must be set by subclasses.
   */
  protected FilterClassInfo() { }

  /**
   * This is the default constructor for <CODE>FilterClassInfo</CODE>
   * objects.
   *
   * @param underlying the <CODE>ClassInfo</CODE> object from which
   *        messages will be filtered.
   */
  public FilterClassInfo(ClassInfo underlying) {
    if (underlying == null)
      throw new NullPointerException();
    this.underlying = underlying;
  }

  /**
   * This method should be overriden by subclasses of <CODE>FilterClassInfo</CODE>
   * in order to filter messages sent to the associated <CODE>ConstantPool</CODE>
   * object.
   *
   * @param constantPool the <CODE>ConstantPool</CODE> object to be filtered.
   * @return a new <CODE>ConstantPool</CODE> instance.
   */
  protected ConstantPool filter(ConstantPool constantPool) {
    return new FilterConstantPool(constantPool);
  }

  /**
   * This method should be overriden by subclasses of <CODE>FilterClassInfo</CODE>
   * in order to filter messages sent to the associated <CODE>FieldInfo</CODE>
   * objects.
   *
   * @param field the <CODE>FieldInfo</CODE> object to be filtered.
   * @return a new <CODE>FieldInfo</CODE> instance.
   */
  protected FieldInfo filter(FieldInfo field) {
    return new FilterFieldInfo(field);
  }

  /**
   * This method should be overriden by subclasses of <CODE>FilterClassInfo</CODE>
   * in order to filter messages sent to the associated <CODE>MethodInfo</CODE>
   * objects.
   *
   * @param method the <CODE>MethodInfo</CODE> object to be filtered.
   * @return a new <CODE>MethodInfo</CODE> instance.
   */
  protected MethodInfo filter(MethodInfo method) {
    return new FilterMethodInfo(method);
  }

  /**
   * This method should be overriden by subclasses of <CODE>FilterClassInfo</CODE>
   * in order to filter messages sent to the associated <CODE>InnerInfo</CODE>
   * objects.
   *
   * @param inner the <CODE>InnerInfo</CODE> object to be filtered.
   * @return a new <CODE>InnerInfo</CODE> instance.
   */
  protected InnerInfo filter(InnerInfo inner) {
    return new FilterInnerInfo(inner);
  }

  /**
   * Retrieves the constant pool of the current <CODE>ClassInfo</CODE>.
   *
   * @return a <CODE>ConstantPool</CODE> object.
   */
  public ConstantPool getConstantPool() {
    if (constantPool == null)
      constantPool = filter(underlying.getConstantPool());
    return constantPool;
  }

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is public.
   *
   * @return <CODE>true</CODE> if it is public, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isPublic() {
    return underlying.isPublic();
  }

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is package private.
   *
   * @return <CODE>true</CODE> if it is package private, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isPackagePrivate() {
    return underlying.isPackagePrivate();
  }

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is final.
   *
   * @return <CODE>true</CODE> if it is final, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isFinal() {
    return underlying.isFinal();
  }

  /**
   * Checks if the current <CODE>ClassInfo</CODE> has its super
   * flag set.
   *
   * @return <CODE>true</CODE> if the super flag is set, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isSuper() {
    return underlying.isSuper();
  }

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is actually an
   * interface rather than a class.
   *
   * @return <CODE>true</CODE> if it is an interface, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isInterface() {
    return underlying.isInterface();
  }

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is abstract.
   *
   * @return <CODE>true</CODE> if it is abstract, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isAbstract() {
    return underlying.isAbstract();
  }

  /**
   * Retrieves the access flags present in the current
   * <CODE>ClassInfo</CODE>.
   *
   * @return the access flags packed in an integer.
   */
  public int getAccessFlags() {
    return underlying.getAccessFlags();
  }

  /**
   * Retrieves the class name for the current <CODE>ClassInfo</CODE>.
   *
   * @return a <CODE>String</CODE> object representing the class name.
   */
  public String getName() {
    return underlying.getName();
  }

  /**
   * Retrieves the class name for the superclass of the current <CODE>ClassInfo</CODE>.
   *
   * @return a <CODE>String</CODE> object representing the superclass name, or
   *         null if the class has no superclass.
   */
  public String getSuperclass() {
    return underlying.getSuperclass();
  }

  /**
   * Retrieves the number of interfaces implemented by the
   * current <CODE>ClassInfo</CODE>.
   *
   * @return an <CODE>integer</CODE> value.
   */
  public int getInterfaceCount() {
    return underlying.getInterfaceCount();
  }

  /**
   * Retrieves the class name for an interface implemented by the
   * current <CODE>ClassInfo</CODE>.
   *
   * @param index the interface index in the interface array.
   * @return an <CODE>String</CODE> object.
   * @exception IndexOutOfBoundsException if the index is out of bounds.
   */
  public String getInterface(int index) {
    return underlying.getInterface(index);
  }

  /**
   * Retrieves the classes names for the interfaces implemented by the
   * current <CODE>ClassInfo</CODE>.
   *
   * @return an array of <CODE>String</CODE> objects.
   */
  public String[] getInterfaces() {
    return underlying.getInterfaces();
  }

  /**
   * Retrieves the number of fields present in the current
   * <CODE>ClassInfo</CODE>.
   *
   * @return an <CODE>integer</CODE> value.
   */
  public int getFieldCount() {
    return underlying.getFieldCount();
  }

  /**
   * Retrieves a field present in the current <CODE>ClassInfo</CODE>.
   *
   * @param name the field name.
   * @param descriptor the field descriptor.
   * @return a <CODE>FieldInfo</CODE> object or null if no such field exits.
   */
  public FieldInfo getField(String name, String descriptor) {
    FieldInfo field = underlying.getField(name, descriptor);
    if (field != null) {
      if (fields == null)
        fields = new FieldInfo[underlying.getFieldCount()];
      int index = field.getIndex();
      if (fields[index] == null)
        fields[index] = filter(field);
      field = fields[index];
    }
    return field;
  }

  /**
   * Retrieves a field present in the current <CODE>ClassInfo</CODE>.
   *
   * @param index the field index in the field array.
   * @return a <CODE>FieldInfo</CODE> object.
   * @exception IndexOutOfBoundsException if the index is out of bounds.
   */
  public FieldInfo getField(int index) {
    FieldInfo field = underlying.getField(index);
    if (fields == null)
      fields = new FieldInfo[underlying.getFieldCount()];
    if (fields[index] == null)
      fields[index] = filter(field);
    field = fields[index];
    return field;
  }

  /**
   * Retrieves all fields present in the current <CODE>ClassInfo</CODE>.
   *
   * @return an array of <CODE>FieldInfo</CODE> objects.
   */
  public FieldInfo[] getFields() {
    if (fields == null)
      fields = new FieldInfo[underlying.getFieldCount()];
    for (int i = 0; i < fields.length; i++)
      if (fields[i] == null)
        fields[i] = filter(underlying.getField(i));
    return (FieldInfo[])fields.clone();
  }

  /**
   * Retrieves the number of methods present in the current
   * <CODE>ClassInfo</CODE>.
   *
   * @return an <CODE>integer</CODE> value.
   */
  public int getMethodCount() {
    return underlying.getMethodCount();
  }

  /**
   * Retrieves a method present in the current <CODE>ClassInfo</CODE>.
   *
   * @param name the method name.
   * @param descriptor the method descriptor.
   * @return a <CODE>MethodInfo</CODE> object or null if no such method exits.
   */
  public MethodInfo getMethod(String name, String descriptor) {
    MethodInfo method = underlying.getMethod(name, descriptor);
    if (method != null) {
      if (methods == null)
        methods = new MethodInfo[underlying.getMethodCount()];
      int index = method.getIndex();
      if (methods[index] == null)
        methods[index] = filter(method);
      method = methods[index];
    }
    return method;
  }

  /**
   * Retrieves a method present in the current <CODE>ClassInfo</CODE>.
   *
   * @param index the method index in the method array.
   * @return a <CODE>MethodInfo</CODE> object.
   * @exception IndexOutOfBoundsException if the index is out of bounds.
   */
  public MethodInfo getMethod(int index) {
    MethodInfo method = underlying.getMethod(index);
    if (methods == null)
      methods = new MethodInfo[underlying.getMethodCount()];
    if (methods[index] == null)
      methods[index] = filter(method);
    method = methods[index];
    return method;
  }

  /**
   * Retrieves all methods present in the current <CODE>ClassInfo</CODE>.
   *
   * @return an array of <CODE>MethodInfo</CODE> objects.
   */
  public MethodInfo[] getMethods() {
    if (methods == null)
      methods = new MethodInfo[underlying.getMethodCount()];
    for (int i = 0; i < methods.length; i++)
      if (methods[i] == null)
        methods[i] = filter(underlying.getMethod(i));
    return (MethodInfo[])methods.clone();
  }

  /**
   * Retrieves the number of inner classes present in the current
   * <CODE>ClassInfo</CODE>.
   *
   * @return an <CODE>integer</CODE> value.
   */
  public int getInnerClassCount() {
    return underlying.getInnerClassCount();
  }

  /**
   * Retrieves a inner class present in the current <CODE>ClassInfo</CODE>.
   *
   * @param index the inner class index in the inner class array.
   * @return a <CODE>InnerInfo</CODE> object, or null if not available.
   * @exception IndexOutOfBoundsException if the index is out of bounds.
   */
  public InnerInfo getInnerClass(int index) {
    InnerInfo inner = underlying.getInnerClass(index);
    if (inner != null) {
      if (inners == null)
        inners = new InnerInfo[underlying.getInnerClassCount()];
      if (inners[index] == null)
        inners[index] = filter(inner);
      inner = inners[index];
    }
    return inner;
  }

  /**
   * Retrieves all inner classes present in the current <CODE>ClassInfo</CODE>.
   *
   * @return an array of <CODE>InnerInfo</CODE> objects, or null if not available.
   */
  public InnerInfo[] getInnerClasses() {
    if (underlying.getInnerClassCount() == 0)
      return underlying.getInnerClasses();
    if (inners == null)
      inners = new InnerInfo[underlying.getInnerClassCount()];
    for (int i = 0; i < inners.length; i++)
      if (inners[i] == null)
        inners[i] = filter(underlying.getInnerClass(i));
    return (InnerInfo[])inners.clone();
  }

  /**
   * Retrieves the source file name in which the current
   * <CODE>ClassInfo</CODE> was declared. Actually, the
   * contents of a <I>SourceFile</I> attribute if available.
   *
   * @return a <CODE>String</CODE> object representing the file
   *         name, or <CODE>null</CODE> if this information is
   *         not available.
   */
  public String getSourceFile() {
    return underlying.getSourceFile();
  }

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is synthetic,
   * i.e.it has a <I>Synthetic</I> attribute.
   *
   * @return <CODE>true</CODE> if it is synthetic, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isSynthetic() {
    return underlying.isSynthetic();
  }

  /**
   * Checks if the current <CODE>ClassInfo</CODE> has been
   * deprecated, i.e.it has a <I>Deprecated</I> attribute.
   *
   * @return <CODE>true</CODE> if it is deprecated, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isDeprecated() {
    return underlying.isDeprecated();
  }

  /**
   * The <CODE>ConstantPool</CODE> class represents the constant pool
   * of a <CODE>ClassInfo</CODE> object.
   */
  public class FilterConstantPool implements ConstantPool {

    protected final ConstantPool underlying;

    /**
     * This is the default constructor for <CODE>FilterConstantPool</CODE>
     * objects.
     *
     * @param underlying the <CODE>ConstantPool</CODE> object from which
     *        messages will be filtered.
     */
    public FilterConstantPool(ConstantPool underlying) {
      if (underlying == null)
        throw new NullPointerException();
      this.underlying = underlying;
    }

    /**
     * Retrieves the <CODE>ClassInfo</CODE> object that owns the
     * current <CODE>ConstantPool</CODE>.
     *
     * @return the owner <CODE>ClassInfo</CODE>.
     */
    public final ClassInfo getOwner() {
      return FilterClassInfo.this;
    }

    /**
     * Returns the number of entries in the current <CODE>ConstantPool</CODE>.
     *
     * @return an <CODE>integer</CODE> with the constant pool size including
     *         the 0 entry and the second entry for CONSTANT_LONG and CONSTANT_DOUBLE.
     */
    public int size() {
      return underlying.size();
    }

    /**
     * Returns the tag field for an entry in the current <CODE>ConstantPool</CODE>.
     *
     * @param index the entry index.
     * @return the tag for the entry at index or 0 if the entry is not valid.
     */
    public byte tag(int index) {
      return underlying.tag(index);
    }

    /**
     * Returns the <CODE>String</CODE> value for a CONSTANT_UTF8 entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_UTF8 entry index.
     * @return the CONSTANT_UTF8 entry value.
     */
    public String getUTF8(int index) {
      return underlying.getUTF8(index);
    }

    /**
     * Returns the <CODE>int</CODE> value for a CONSTANT_INTEGER entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_INTEGER entry index.
     * @return the CONSTANT_INTEGER entry value.
     */
    public int getInteger(int index) {
      return underlying.getInteger(index);
    }

    /**
     * Returns the <CODE>float</CODE> value for a CONSTANT_FLOAT entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_FLOAT entry index.
     * @return the CONSTANT_FLOAT entry value.
     */
    public float getFloat(int index) {
      return underlying.getFloat(index);
    }

    /**
     * Returns the <CODE>long</CODE> value for a CONSTANT_LONG entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_LONG entry index.
     * @return the CONSTANT_LONG entry value.
     */
    public long getLong(int index) {
      return underlying.getLong(index);
    }

    /**
     * Returns the <CODE>double</CODE> value for a CONSTANT_DOUBLE entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_DOUBLE entry index.
     * @return the CONSTANT_DOUBLE entry value.
     */
    public double getDouble(int index) {
      return underlying.getDouble(index);
    }

    /**
     * Returns the <CODE>String</CODE> value for a CONSTANT_STRING entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_STRING entry index.
     * @return the CONSTANT_STRING entry value.
     */
    public String getString(int index) {
      return underlying.getString(index);
    }

    /**
     * Returns the <CODE>String</CODE> value representing a fully qualified class name for a
     * CONSTANT_CLASS, CONSTANT_FIELDREF, CONSTANT_METHODREF or CONSTANT_INTERFACEMETHODREF
     * entry in the current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the entry index.
     * @return the entry value.
     */
    public String getClass(int index) {
      return underlying.getClass(index);
    }

    /**
     * Returns the <CODE>String</CODE> value representing a simple name for a
     * CONSTANT_NAMEANDTYPE, CONSTANT_FIELDREF, CONSTANT_METHODREF or CONSTANT_INTERFACEMETHODREF
     * entry in the current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the entry index.
     * @return the entry value.
     */
    public String getName(int index) {
      return underlying.getName(index);
    }

    /**
     * Returns the <CODE>String</CODE> value representing a descriptor for a
     * CONSTANT_NAMEANDTYPE, CONSTANT_FIELDREF, CONSTANT_METHODREF or CONSTANT_INTERFACEMETHODREF
     * entry in the current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the entry index.
     * @return the entry value.
     */
    public String getDescriptor(int index) {
      return underlying.getDescriptor(index);
    }

  }

  /**
   * The <CODE>FieldInfo</CODE> class represents fields inside
   * a <CODE>ClassInfo</CODE> object.
   */
  public class FilterFieldInfo implements FieldInfo {

    protected final FieldInfo underlying;

    /**
     * This is the default constructor for <CODE>FilterFieldInfo</CODE>
     * objects.
     *
     * @param underlying the <CODE>FieldInfo</CODE> object from which
     *        messages will be filtered.
     */
    public FilterFieldInfo(FieldInfo underlying) {
      if (underlying == null)
        throw new NullPointerException();
      this.underlying = underlying;
    }

    /**
     * Retrieves the <CODE>ClassInfo</CODE> object that owns the
     * current <CODE>FieldInfo</CODE>.
     *
     * @return the owner <CODE>ClassInfo</CODE>.
     */
    public final ClassInfo getOwner() {
      return FilterClassInfo.this;
    }

    /**
     * Retrieves the current <CODE>FieldInfo</CODE> index in the method array
     * of the <CODE>ClassInfo</CODE> object that owns it.
     *
     * @return an <CODE>integer</CODE> as the index.
     */
    public int getIndex() {
      return underlying.getIndex();
    }

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is public.
     *
     * @return <CODE>true</CODE> if it is public, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPublic() {
      return underlying.isPublic();
    }

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is private.
     *
     * @return <CODE>true</CODE> if it is private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPrivate() {
      return underlying.isPrivate();
    }

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is protected.
     *
     * @return <CODE>true</CODE> if it is protected, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isProtected() {
      return underlying.isProtected();
    }

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is package private.
     *
     * @return <CODE>true</CODE> if it is package private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPackagePrivate() {
      return underlying.isPackagePrivate();
    }

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is static.
     *
     * @return <CODE>true</CODE> if it is static, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isStatic() {
      return underlying.isStatic();
    }

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is final.
     *
     * @return <CODE>true</CODE> if it is final, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isFinal() {
      return underlying.isFinal();
    }

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is volatile.
     *
     * @return <CODE>true</CODE> if it is volatile, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isVolatile() {
      return underlying.isVolatile();
    }

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is transient.
     *
     * @return <CODE>true</CODE> if it is transient, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isTransient() {
      return underlying.isTransient();
    }

    /**
     * Retrieves the access flags present in the current
     * <CODE>FieldInfo</CODE>.
     *
     * @return the access flags packed in an integer.
     */
    public int getAccessFlags() {
      return underlying.getAccessFlags();
    }

    /**
     * Retrieves the field name for the current <CODE>FieldInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the field name.
     */
    public String getName() {
      return underlying.getName();
    }

    /**
     * Retrieves the field descriptor for the current <CODE>FieldInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the field descriptor.
     */
    public String getDescriptor() {
      return underlying.getDescriptor();
    }

    /**
     * Retrieves the <I>ConstantField</I> attribute for the current
     * <CODE>FieldInfo</CODE>. If available, the value is wrapped based
     * on the following table:
     * <TABLE>
     * <TR><TH>Type</TH><TH>Wrapping Class</TH></TR>
     * <TR><TD>boolean</TD><TD>java.lang.Integer</TD></TR>
     * <TR><TD>byte</TD><TD>java.lang.Integer</TD></TR>
     * <TR><TD>char</TD><TD>java.lang.Integer</TD></TR>
     * <TR><TD>short</TD><TD>java.lang.Integer</TD></TR>
     * <TR><TD>int</TD><TD>java.lang.Integer</TD></TR>
     * <TR><TD>long</TD><TD>java.lang.Long</TD></TR>
     * <TR><TD>float</TD><TD>java.lang.Float</TD></TR>
     * <TR><TD>double</TD><TD>java.lang.Double</TD></TR>
     * <TR><TD>String</TD><TD>java.lang.String</TD></TR>
     * </TABLE>
     *
     * @return the wrapped constant value, or null if not available.
     */
    public Object getConstantValue() {
      return underlying.getConstantValue();
    }

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is synthetic,
     * i.e.it has a <I>Synthetic</I> attribute.
     *
     * @return <CODE>true</CODE> if it is synthetic, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isSynthetic() {
      return underlying.isSynthetic();
    }

    /**
     * Checks if the current <CODE>FieldInfo</CODE> has been
     * deprecated, i.e.it has a <I>Deprecated</I> attribute.
     *
     * @return <CODE>true</CODE> if it is deprecated, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isDeprecated() {
      return underlying.isDeprecated();
    }

  }

  /**
   * The <CODE>MethodInfo</CODE> class represents methods inside
   * a <CODE>ClassInfo</CODE> object.
   */
  public class FilterMethodInfo implements MethodInfo {

    protected final MethodInfo underlying;

    private transient CodeInfo code;

    /**
     * This is the default constructor for <CODE>FilterMethodInfo</CODE>
     * objects.
     *
     * @param underlying the <CODE>MethodInfo</CODE> object from which
     *        messages will be filtered.
     */
    public FilterMethodInfo(MethodInfo underlying) {
      if (underlying == null)
        throw new NullPointerException();
      this.underlying = underlying;
    }

    /**
     * This method should be overriden by subclasses of <CODE>FilterMethodInfo</CODE>
     * in order to filter messages sent to the associated <CODE>CodeInfo</CODE>
     * object.
     *
     * @param code the <CODE>CodeInfo</CODE> object to be filtered.
     * @return a new <CODE>CodeInfo</CODE> instance.
     */
    protected CodeInfo filter(CodeInfo code) {
      return new FilterCodeInfo(code);
    }

    /**
     * Retrieves the <CODE>ClassInfo</CODE> object that owns the
     * current <CODE>MethodInfo</CODE>.
     *
     * @return the owner <CODE>ClassInfo</CODE>.
     */
    public final ClassInfo getOwner() {
      return FilterClassInfo.this;
    }

    /**
     * Retrieves the current <CODE>MethodInfo</CODE> index in the method array
     * of the <CODE>ClassInfo</CODE> object that owns it.
     *
     * @return an <CODE>integer</CODE> as the index.
     */
    public int getIndex() {
      return underlying.getIndex();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is public.
     *
     * @return <CODE>true</CODE> if it is public, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPublic() {
      return underlying.isPublic();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is private.
     *
     * @return <CODE>true</CODE> if it is private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPrivate() {
      return underlying.isPrivate();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is protected.
     *
     * @return <CODE>true</CODE> if it is protected, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isProtected() {
      return underlying.isProtected();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is package private.
     *
     * @return <CODE>true</CODE> if it is package private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPackagePrivate() {
      return underlying.isPackagePrivate();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is static.
     *
     * @return <CODE>true</CODE> if it is static, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isStatic() {
      return underlying.isStatic();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is final.
     *
     * @return <CODE>true</CODE> if it is final, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isFinal() {
      return underlying.isFinal();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is synchronized.
     *
     * @return <CODE>true</CODE> if it is synchronized, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isSynchronized() {
      return underlying.isSynchronized();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is native.
     *
     * @return <CODE>true</CODE> if it is native, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isNative() {
      return underlying.isNative();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is abstract.
     *
     * @return <CODE>true</CODE> if it is abstract, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isAbstract() {
      return underlying.isAbstract();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is strict.
     *
     * @return <CODE>true</CODE> if it is strict, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isStrict() {
      return underlying.isStrict();
    }

    /**
     * Retrieves the access flags present in the current
     * <CODE>MethodInfo</CODE>.
     *
     * @return the access flags packed in an integer.
     */
    public int getAccessFlags() {
      return underlying.getAccessFlags();
    }

    /**
     * Retrieves the method name for the current <CODE>MethodInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the method name.
     */
    public String getName() {
      return underlying.getName();
    }

    /**
     * Retrieves the method descriptor for the current <CODE>MethodInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the method descriptor.
     */
    public String getDescriptor() {
      return underlying.getDescriptor();
    }

    /**
     * Retrieves the <I>Code</I> attribute for the current <CODE>MethodInfo</CODE>,
     * if available.
     *
     * @return a <CODE>CodeInfo</CODE> object, or null if not available.
     */
    public CodeInfo getCode() {
      if (code == null) {
        CodeInfo underlyingCode = underlying.getCode();
        if (underlyingCode != null)
          code = filter(underlyingCode);
      }
      return code;
    }

    /**
     * Retrieves the number of exceptions thrown by the
     * current <CODE>ClassInfo</CODE>.
     *
     * @return an <CODE>integer</CODE> value.
     */
    public int getExceptionCount() {
      return underlying.getExceptionCount();
    }
  
    /**
     * Retrieves the class name for an exceptions thrown by the current
     * <CODE>MethodInfo</CODE>. This information is provided by the
     * <I>Exceptions</I> attribute, which is optional.
     *
     * @param index the exception index in the exceptions array.
     * @return an <CODE>String</CODE> object, or null if not available.
     * @exception IndexOutOfBoundsException if the index is out of bounds.
     */
    public String getException(int index) {
      return underlying.getException(index);
    }

    /**
     * Retrieves the classes names for the exceptions thrown by the current
     * <CODE>MethodInfo</CODE>. This information is provided by the
     * <I>Exceptions</I> attribute, which is optional.
     *
     * @return an array of <CODE>String</CODE>, or null if not available.
     */
    public String[] getExceptions() {
      return underlying.getExceptions();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is synthetic,
     * i.e.it has a <I>Synthetic</I> attribute.
     *
     * @return <CODE>true</CODE> if it is synthetic, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isSynthetic() {
      return underlying.isSynthetic();
    }

    /**
     * Checks if the current <CODE>MethodInfo</CODE> has been
     * deprecated, i.e.it has a <I>Deprecated</I> attribute.
     *
     * @return <CODE>true</CODE> if it is deprecated, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isDeprecated() {
      return underlying.isDeprecated();
    }

    /**
     * The <CODE>CodeInfo</CODE> class represents <I>Code</I>
     * attributes inside a <CODE>MethodInfo</CODE> object.
     */
    public class FilterCodeInfo implements CodeInfo {

      protected final CodeInfo underlying;

      private transient HandlerInfo[] handlers;

      /**
       * This is the default constructor for <CODE>FilterCodeInfo</CODE>
       * objects.
       *
       * @param underlying the <CODE>CodeInfo</CODE> object from which
       *        messages will be filtered.
       */
      public FilterCodeInfo(CodeInfo underlying) {
        if (underlying == null)
          throw new NullPointerException();
        this.underlying = underlying;
      }

      /**
       * This method should be overriden by subclasses of <CODE>FilterCodeInfo</CODE>
       * in order to filter messages sent to the associated <CODE>HandlerInfo</CODE>
       * objects.
       *
       * @param handler the <CODE>HandlerInfo</CODE> object to be filtered.
       * @return a new <CODE>HandlerInfo</CODE> instance.
       */
      protected HandlerInfo filter(HandlerInfo handler) {
        return new FilterHandlerInfo(handler);
      }

      /**
       * Retrieves the <CODE>MethodInfo</CODE> object that owns the
       * current <CODE>CodeInfo</CODE>.
       *
       * @return the owner <CODE>MethodInfo</CODE>.
       */
      public final MethodInfo getOwner() {
        return FilterMethodInfo.this;
      }

      /**
       * Retrieves the maximum operand stack size for the current
       * <CODE>CodeInfo</CODE>.
       *
       * @return an integer containing the maximum operand stack size.
       */
      public int getMaxStack() {
        return underlying.getMaxStack();
      }

      /**
       * Retrieves the maximum local frame size for the current
       * <CODE>CodeInfo</CODE>.
       *
       * @return an integer containing the maximum local frame size.
       */
      public int getMaxLocals() {
        return underlying.getMaxLocals();
      }

      /**
       * Returns a copy of the method text for the current <CODE>CodeInfo</CODE>.
       * The text array returned is not guaranteed to obey the static and
       * structural constraints. However to check if the text array obeys the
       * static constraints ensure that the method <CODE>checkStaticConstraints</CODE>
       * has been invoked before using the array. Similarly, to check if the
       * text array obeys the structural constraints ensure that the method
       * <CODE>checkStructuralConstraints</CODE> has benn invoked before using
       * the array.
       *
       * @return an array of <CODE>byte</CODE> with the bytecodes.
       * @see jewel.core.clfile.Bytecode
       * @see jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo#checkStaticConstraints()
       * @see jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo#checkStructuralConstraints()
       */
      public byte[] getText() {
        return underlying.getText();
      }

      /**
       * This method returns useful information regarding the method text for the
       * current <CODE>CodeInfo</CODE>.
       *
       * @return an array of <CODE>int</CODE> or null if not available.
       * @see jewel.core.clfile.Bytecode
       * @see jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo#checkStaticConstraints()
       * @see jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo#checkStructuralConstraints()
       */
      public int[] getTextInfo() {
        return underlying.getTextInfo();
      }

      /**
       * Retrieves the number of exception handlers present in the current
       * <CODE>CodeInfo</CODE>.
       *
       * @return an <CODE>integer</CODE> value.
       */
      public int getHandlerCount() {
        return underlying.getHandlerCount();
      }
    
      /**
       * Retrieves a exception handler present in the current <CODE>CodeInfo</CODE>.
       *
       * @param index the handler index in the exception handler table.
       * @return a <CODE>HandlerInfo</CODE> object.
       * @exception IndexOutOfBoundsException if the index is out of bounds.
       */
      public HandlerInfo getHandler(int index) {
        HandlerInfo handler = underlying.getHandler(index);
        if (handlers == null)
          handlers = new HandlerInfo[underlying.getHandlerCount()];
        if (handlers[index] == null)
          handlers[index] = filter(handler);
        handler = handlers[index];
        return handler;
      }
    
      /**
       * Retrieves the exception handler table for the current <CODE>CodeInfo</CODE>.
       *
       * @return an array of <CODE>HandlerInfo</CODE>.
       */
      public HandlerInfo[] getHandlers() {
        if (handlers == null)
          handlers = new HandlerInfo[underlying.getHandlerCount()];
        for (int i = 0; i < handlers.length; i++)
          if (handlers[i] == null)
            handlers[i] = filter(underlying.getHandler(i));
        return (HandlerInfo[])handlers.clone();
      }

      /**
       * Retrieves the source file line number for the code in a particular
       * offset of the current <CODE>CodeInfo</CODE> method text.
       *
       * @return the line number, or <CODE>-1</CODE> if the information is
       *         not available.
       */
      public int lineOf(int pc) {
        return underlying.lineOf(pc);
      }

      /**
       * Retrieves the local variable name associated with an index in
       * the local frame for a particular offset in the current
       * <CODE>CodeInfo</CODE> method text.
       *
       * @return a <CODE>String</CODE> containing the local variable name,
       *         or <CODE>null</CODE> if the information is not available.
       */
      public String getLocalName(int pc, int index) {
        return underlying.getLocalName(pc, index);
      }

      /**
       * Retrieves the local variable descriptor associated with an index
       * in the local frame for a particular offset in the current
       * <CODE>CodeInfo</CODE> method text.
       *
       * @return a <CODE>String</CODE> containing the local variable
       *         descriptor, or <CODE>null</CODE> if the information is
       *         not available.
       */
      public String getLocalDescriptor(int pc, int index) {
        return underlying.getLocalDescriptor(pc, index);
      }

      /**
       * This method should be called to check if the text array
       * associated with the current <CODE>CodeInfo</CODE> object
       * obeys the static constraints requirements. This means
       * that, if this method returns successfully, all the opcodes
       * in the text array are legal, the branch targets are valid
       * and all the exception handler windows are well constructed.
       * However this method does not check the structural constraints,
       * which requires data flow analysis.
       *
       * @exception VerifyException if the text array associated to the
       *            current <CODE>CodeInfo</CODE> violates some static
       *            constraint.
       */
      public void checkStaticConstraints() throws VerifyException {
        underlying.checkStaticConstraints();
      }

      /**
       * This method should be called to check if the text array
       * associated with the current <CODE>CodeInfo</CODE> object
       * obeys the structural constraints requirements. A structural
       * contraint violation is discovered by applying a data flow
       * analysis to the text array. The dataflow analysis can be
       * customized to fit in a wide range of contexts by providing
       * a proper implementation of interface <CODE>Namespace</CODE>.
       *
       * @param nameSpace a <CODE>Namespace</CODE> implementation.
       * @exception VerifyException if the text array associated to the
       *            current <CODE>CodeInfo</CODE> violates some structural
       *            constraint on the passed <CODE>Namespace</CODE>.
       * @see jewel.core.clfile.Namespace
       */
      public void checkStructuralConstraints(Namespace nameSpace) throws VerifyException {
        underlying.checkStructuralConstraints(nameSpace);
      }

      /**
       * The <CODE>HandlerInfo</CODE> class represents exception
       * handlers inside a <I>Code</I> attribute.
       */
      public class FilterHandlerInfo implements HandlerInfo {

        protected final HandlerInfo underlying;

        /**
         * This is the default constructor for <CODE>FilterHandlerInfo</CODE>
         * objects.
         *
         * @param underlying the <CODE>HandlerInfo</CODE> object from which
         *        messages will be filtered.
         */
        public FilterHandlerInfo(HandlerInfo underlying) {
          if (underlying == null)
            throw new NullPointerException();
          this.underlying = underlying;
        }

        /**
         * Retrieves the <CODE>CodeInfo</CODE> object that owns the
         * current <CODE>HandlerInfo</CODE>.
         *
         * @return the owner <CODE>CodeInfo</CODE>.
         */
        public final CodeInfo getOwner() {
          return FilterCodeInfo.this;
        }

        /**
         * Retrieves the current <CODE>HandlerInfo</CODE> index in the exception handler
         * table of the <CODE>CodeInfo</CODE> object that owns it.
         *
         * @return an <CODE>integer</CODE> as the index.
         */
        public int getIndex() {
          return underlying.getIndex();
        }

        /**
         * Retrieves the starting point in the text array where this handler
         * becomes effective.
         *
         * @return the inclusive start PC of the current <CODE>HandlerInfo</CODE>.
         */
        public int getStartPC() {
          return underlying.getStartPC();
        }

        /**
         * Retrieves the ending point in the text array where this handler
         * becomes uneffective.
         *
         * @return the exclusive end PC of the current <CODE>HandlerInfo</CODE>.
         */
        public int getEndPC() {
          return underlying.getEndPC();
        }

        /**
         * Retrieves the offset in the text array where this handler
         * code lays.
         *
         * @return the PC of the current <CODE>HandlerInfo</CODE> handler code.
         */
        public int getHandlerPC() {
          return underlying.getHandlerPC();
        }

        /**
         * Retrieves the class name of the exception being catched by the
         * current <CODE>HandlerInfo</CODE>.
         *
         * @return a <CODE>String</CODE> object containing the class name,
         *         or null if the current handler was derived from a
         *         <CODE>finally</CODE> construct.
         */
        public String getCatchType() {
          return underlying.getCatchType();
        }

        /**
         * Checks if the current <CODE>HandlerInfo</CODE> encloses a particular
         * text array offset, i.e.checks if the code in the offset is protected
         * by this handler.
         *
         * @param pc an offset in text array.
         * @return <CODE>true</CODE> if the PC is inside this handler protected
         *         area, <CODE>false</CODE> otherwise.
         */
        public boolean encloses(int pc) {
          return underlying.encloses(pc);
        }

      }

    }

  }

  /**
   * The <CODE>InnerInfo</CODE> class represents <I>InnerClasses</I>
   * attributes inside a <CODE>ClassInfo</CODE> object.
   */
  public class FilterInnerInfo implements InnerInfo {

    protected final InnerInfo underlying;

    /**
     * This is the default constructor for <CODE>FilterInnerInfo</CODE>
     * objects.
     *
     * @param underlying the <CODE>InnerInfo</CODE> object from which
     *        messages will be filtered.
     */
    public FilterInnerInfo(InnerInfo underlying) {
      if (underlying == null)
        throw new NullPointerException();
      this.underlying = underlying;
    }

    /**
     * Retrieves the <CODE>ClassInfo</CODE> object that owns the
     * current <CODE>InnerInfo</CODE>.
     *
     * @return the owner <CODE>ClassInfo</CODE>.
     */
    public final ClassInfo getOwner() {
      return FilterClassInfo.this;
    }

    /**
     * Retrieves the current <CODE>InnerInfo</CODE> index in the inner class array
     * of the <CODE>ClassInfo</CODE> object that owns it.
     *
     * @return an <CODE>integer</CODE> as the index.
     */
    public int getIndex() {
      return underlying.getIndex();
    }

    /**
     * Retrieves the class name for the current <CODE>InnerInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the class name.
     */
    public String getInnerClass() {
      return underlying.getInnerClass();
    }

    /**
     * Retrieves the class name for the class of which the current
     * <CODE>InnerInfo</CODE> is a member.
     *
     * @return a <CODE>String</CODE> object representing the class name,
     *         or null if the class is not a member.
     */
    public String getOuterClass() {
      return underlying.getOuterClass();
    }

    /**
     * Retrieves the simple name for the current <CODE>InnerInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the simple name,
     *         or null if the class is anonymous.
     */
    public String getName() {
      return underlying.getName();
    }

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is public.
     *
     * @return <CODE>true</CODE> if it is public, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPublic() {
      return underlying.isPublic();
    }

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is private.
     *
     * @return <CODE>true</CODE> if it is private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPrivate() {
      return underlying.isPrivate();
    }

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is protected.
     *
     * @return <CODE>true</CODE> if it is protected, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isProtected() {
      return underlying.isProtected();
    }

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is package private.
     *
     * @return <CODE>true</CODE> if it is package private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPackagePrivate() {
      return underlying.isPackagePrivate();
    }

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is static.
     *
     * @return <CODE>true</CODE> if it is static, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isStatic() {
      return underlying.isStatic();
    }

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is final.
     *
     * @return <CODE>true</CODE> if it is final, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isFinal() {
      return underlying.isFinal();
    }

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is actually an
     * interface rather than a class.
     *
     * @return <CODE>true</CODE> if it is an interface, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isInterface() {
      return underlying.isInterface();
    }

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is abstract.
     *
     * @return <CODE>true</CODE> if it is abstract, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isAbstract() {
      return underlying.isAbstract();
    }

    /**
     * Retrieves the access flags present in the current
     * <CODE>InnerInfo</CODE>.
     *
     * @return the access flags packed in an integer.
     */
    public int getAccessFlags() {
      return underlying.getAccessFlags();
    }

  }

}

