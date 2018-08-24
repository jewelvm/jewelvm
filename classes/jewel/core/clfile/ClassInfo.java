/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.clfile;

/**
 * The interface <CODE>ClassInfo</CODE> provides full
 * information about the standard contents of a class.
 */
public interface ClassInfo {

  /**
   * Retrieves the constant pool of the current <CODE>ClassInfo</CODE>.
   *
   * @return a <CODE>ConstantPool</CODE> object.
   */
  public ConstantPool getConstantPool();

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is public.
   *
   * @return <CODE>true</CODE> if it is public, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isPublic();

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is package private.
   *
   * @return <CODE>true</CODE> if it is package private, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isPackagePrivate();

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is final.
   *
   * @return <CODE>true</CODE> if it is final, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isFinal();

  /**
   * Checks if the current <CODE>ClassInfo</CODE> has its super
   * flag set.
   *
   * @return <CODE>true</CODE> if the super flag is set, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isSuper();

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is actually an
   * interface rather than a class.
   *
   * @return <CODE>true</CODE> if it is an interface, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isInterface();

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is abstract.
   *
   * @return <CODE>true</CODE> if it is abstract, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isAbstract();

  /**
   * Retrieves the access flags present in the current
   * <CODE>ClassInfo</CODE>.
   *
   * @return the access flags packed in an integer.
   */
  public int getAccessFlags();

  /**
   * Retrieves the class name for the current <CODE>ClassInfo</CODE>.
   *
   * @return a <CODE>String</CODE> object representing the class name.
   */
  public String getName();

  /**
   * Retrieves the class name for the superclass of the current <CODE>ClassInfo</CODE>.
   *
   * @return a <CODE>String</CODE> object representing the superclass name, or
   *         null if the class has no superclass.
   */
  public String getSuperclass();

  /**
   * Retrieves the number of interfaces implemented by the
   * current <CODE>ClassInfo</CODE>.
   *
   * @return an <CODE>integer</CODE> value.
   */
  public int getInterfaceCount();

  /**
   * Retrieves the class name for an interface implemented by the
   * current <CODE>ClassInfo</CODE>.
   *
   * @param index the interface index in the interface array.
   * @return an <CODE>String</CODE> object.
   * @exception IndexOutOfBoundsException if the index is out of bounds.
   */
  public String getInterface(int index);

  /**
   * Retrieves the classes names for the interfaces implemented by the
   * current <CODE>ClassInfo</CODE>.
   *
   * @return an array of <CODE>String</CODE> objects.
   */
  public String[] getInterfaces();

  /**
   * Retrieves the number of fields present in the current
   * <CODE>ClassInfo</CODE>.
   *
   * @return an <CODE>integer</CODE> value.
   */
  public int getFieldCount();

  /**
   * Retrieves a field present in the current <CODE>ClassInfo</CODE>.
   *
   * @param name the field name.
   * @param descriptor the field descriptor.
   * @return a <CODE>FieldInfo</CODE> object or null if no such field exits.
   */
  public FieldInfo getField(String name, String descriptor);

  /**
   * Retrieves a field present in the current <CODE>ClassInfo</CODE>.
   *
   * @param index the field index in the field array.
   * @return a <CODE>FieldInfo</CODE> object.
   * @exception IndexOutOfBoundsException if the index is out of bounds.
   */
  public FieldInfo getField(int index);

  /**
   * Retrieves all fields present in the current <CODE>ClassInfo</CODE>.
   *
   * @return an array of <CODE>FieldInfo</CODE> objects.
   */
  public FieldInfo[] getFields();

  /**
   * Retrieves the number of methods present in the current
   * <CODE>ClassInfo</CODE>.
   *
   * @return an <CODE>integer</CODE> value.
   */
  public int getMethodCount();

  /**
   * Retrieves a method present in the current <CODE>ClassInfo</CODE>.
   *
   * @param name the method name.
   * @param descriptor the method descriptor.
   * @return a <CODE>MethodInfo</CODE> object or null if no such method exits.
   */
  public MethodInfo getMethod(String name, String descriptor);

  /**
   * Retrieves a method present in the current <CODE>ClassInfo</CODE>.
   *
   * @param index the method index in the method array.
   * @return a <CODE>MethodInfo</CODE> object.
   * @exception IndexOutOfBoundsException if the index is out of bounds.
   */
  public MethodInfo getMethod(int index);

  /**
   * Retrieves all methods present in the current <CODE>ClassInfo</CODE>.
   *
   * @return an array of <CODE>MethodInfo</CODE> objects.
   */
  public MethodInfo[] getMethods();

  /**
   * Retrieves the number of inner classes present in the current
   * <CODE>ClassInfo</CODE>.
   *
   * @return an <CODE>integer</CODE> value.
   */
  public int getInnerClassCount();

  /**
   * Retrieves a inner class present in the current <CODE>ClassInfo</CODE>.
   *
   * @param index the inner class index in the inner class array.
   * @return a <CODE>InnerInfo</CODE> object, or null if not available.
   * @exception IndexOutOfBoundsException if the index is out of bounds.
   */
  public InnerInfo getInnerClass(int index);

  /**
   * Retrieves all inner classes present in the current <CODE>ClassInfo</CODE>.
   *
   * @return an array of <CODE>InnerInfo</CODE> objects, or null if not available.
   */
  public InnerInfo[] getInnerClasses();

  /**
   * Retrieves the source file name in which the current
   * <CODE>ClassInfo</CODE> was declared. Actually, the
   * contents of a <I>SourceFile</I> attribute if available.
   *
   * @return a <CODE>String</CODE> object representing the file
   *         name, or <CODE>null</CODE> if this information is
   *         not available.
   */
  public String getSourceFile();

  /**
   * Checks if the current <CODE>ClassInfo</CODE> is synthetic,
   * i.e.it has a <I>Synthetic</I> attribute.
   *
   * @return <CODE>true</CODE> if it is synthetic, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isSynthetic();

  /**
   * Checks if the current <CODE>ClassInfo</CODE> has been
   * deprecated, i.e.it has a <I>Deprecated</I> attribute.
   *
   * @return <CODE>true</CODE> if it is deprecated, <CODE>false</CODE>
   *         otherwise.
   */
  public boolean isDeprecated();

  /**
   * The <CODE>ConstantPool</CODE> class represents the constant pool
   * of a <CODE>ClassInfo</CODE> object.
   */
  public interface ConstantPool {

    /**
     * Retrieves the <CODE>ClassInfo</CODE> object that owns the
     * current <CODE>ConstantPool</CODE>.
     *
     * @return the owner <CODE>ClassInfo</CODE>.
     */
    public ClassInfo getOwner();

    /**
     * Returns the number of entries in the current <CODE>ConstantPool</CODE>.
     *
     * @return an <CODE>integer</CODE> with the constant pool size including
     *         the 0 entry and the second entry for CONSTANT_LONG and CONSTANT_DOUBLE.
     */
    public int size();

    /**
     * Returns the tag field for an entry in the current <CODE>ConstantPool</CODE>.
     *
     * @param index the entry index.
     * @return the tag for the entry at index or 0 if the entry is not valid.
     */
    public byte tag(int index);

    /**
     * Returns the <CODE>String</CODE> value for a CONSTANT_UTF8 entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_UTF8 entry index.
     * @return the CONSTANT_UTF8 entry value.
     */
    public String getUTF8(int index);

    /**
     * Returns the <CODE>int</CODE> value for a CONSTANT_INTEGER entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_INTEGER entry index.
     * @return the CONSTANT_INTEGER entry value.
     */
    public int getInteger(int index);

    /**
     * Returns the <CODE>float</CODE> value for a CONSTANT_FLOAT entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_FLOAT entry index.
     * @return the CONSTANT_FLOAT entry value.
     */
    public float getFloat(int index);

    /**
     * Returns the <CODE>long</CODE> value for a CONSTANT_LONG entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_LONG entry index.
     * @return the CONSTANT_LONG entry value.
     */
    public long getLong(int index);

    /**
     * Returns the <CODE>double</CODE> value for a CONSTANT_DOUBLE entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_DOUBLE entry index.
     * @return the CONSTANT_DOUBLE entry value.
     */
    public double getDouble(int index);

    /**
     * Returns the <CODE>String</CODE> value for a CONSTANT_STRING entry in the
     * current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the CONSTANT_STRING entry index.
     * @return the CONSTANT_STRING entry value.
     */
    public String getString(int index);

    /**
     * Returns the <CODE>String</CODE> value representing a fully qualified class name for a
     * CONSTANT_CLASS, CONSTANT_FIELDREF, CONSTANT_METHODREF or CONSTANT_INTERFACEMETHODREF
     * entry in the current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the entry index.
     * @return the entry value.
     */
    public String getClass(int index);

    /**
     * Returns the <CODE>String</CODE> value representing a simple name for a
     * CONSTANT_NAMEANDTYPE, CONSTANT_FIELDREF, CONSTANT_METHODREF or CONSTANT_INTERFACEMETHODREF
     * entry in the current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the entry index.
     * @return the entry value.
     */
    public String getName(int index);

    /**
     * Returns the <CODE>String</CODE> value representing a descriptor for a
     * CONSTANT_NAMEANDTYPE, CONSTANT_FIELDREF, CONSTANT_METHODREF or CONSTANT_INTERFACEMETHODREF
     * entry in the current <CODE>ConstantPool</CODE>. This method has an undefined behaviour
     * if invoked upon an unexpected parameter.
     *
     * @param index the entry index.
     * @return the entry value.
     */
    public String getDescriptor(int index);

  }

  /**
   * The <CODE>FieldInfo</CODE> class represents fields inside
   * a <CODE>ClassInfo</CODE> object.
   */
  public interface FieldInfo {

    /**
     * Retrieves the <CODE>ClassInfo</CODE> object that owns the
     * current <CODE>FieldInfo</CODE>.
     *
     * @return the owner <CODE>ClassInfo</CODE>.
     */
    public ClassInfo getOwner();

    /**
     * Retrieves the current <CODE>FieldInfo</CODE> index in the method array
     * of the <CODE>ClassInfo</CODE> object that owns it.
     *
     * @return an <CODE>integer</CODE> as the index.
     */
    public int getIndex();

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is public.
     *
     * @return <CODE>true</CODE> if it is public, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPublic();

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is private.
     *
     * @return <CODE>true</CODE> if it is private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPrivate();

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is protected.
     *
     * @return <CODE>true</CODE> if it is protected, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isProtected();

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is package private.
     *
     * @return <CODE>true</CODE> if it is package private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPackagePrivate();

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is static.
     *
     * @return <CODE>true</CODE> if it is static, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isStatic();

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is final.
     *
     * @return <CODE>true</CODE> if it is final, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isFinal();

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is volatile.
     *
     * @return <CODE>true</CODE> if it is volatile, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isVolatile();

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is transient.
     *
     * @return <CODE>true</CODE> if it is transient, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isTransient();

    /**
     * Retrieves the access flags present in the current
     * <CODE>FieldInfo</CODE>.
     *
     * @return the access flags packed in an integer.
     */
    public int getAccessFlags();

    /**
     * Retrieves the field name for the current <CODE>FieldInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the field name.
     */
    public String getName();

    /**
     * Retrieves the field descriptor for the current <CODE>FieldInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the field descriptor.
     */
    public String getDescriptor();

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
    public Object getConstantValue();

    /**
     * Checks if the current <CODE>FieldInfo</CODE> is synthetic,
     * i.e.it has a <I>Synthetic</I> attribute.
     *
     * @return <CODE>true</CODE> if it is synthetic, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isSynthetic();

    /**
     * Checks if the current <CODE>FieldInfo</CODE> has been
     * deprecated, i.e.it has a <I>Deprecated</I> attribute.
     *
     * @return <CODE>true</CODE> if it is deprecated, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isDeprecated();

  }

  /**
   * The <CODE>MethodInfo</CODE> class represents methods inside
   * a <CODE>ClassInfo</CODE> object.
   */
  public interface MethodInfo {

    /**
     * Retrieves the <CODE>ClassInfo</CODE> object that owns the
     * current <CODE>MethodInfo</CODE>.
     *
     * @return the owner <CODE>ClassInfo</CODE>.
     */
    public ClassInfo getOwner();

    /**
     * Retrieves the current <CODE>MethodInfo</CODE> index in the method array
     * of the <CODE>ClassInfo</CODE> object that owns it.
     *
     * @return an <CODE>integer</CODE> as the index.
     */
    public int getIndex();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is public.
     *
     * @return <CODE>true</CODE> if it is public, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPublic();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is private.
     *
     * @return <CODE>true</CODE> if it is private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPrivate();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is protected.
     *
     * @return <CODE>true</CODE> if it is protected, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isProtected();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is package private.
     *
     * @return <CODE>true</CODE> if it is package private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPackagePrivate();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is static.
     *
     * @return <CODE>true</CODE> if it is static, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isStatic();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is final.
     *
     * @return <CODE>true</CODE> if it is final, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isFinal();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is synchronized.
     *
     * @return <CODE>true</CODE> if it is synchronized, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isSynchronized();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is native.
     *
     * @return <CODE>true</CODE> if it is native, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isNative();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is abstract.
     *
     * @return <CODE>true</CODE> if it is abstract, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isAbstract();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is strict.
     *
     * @return <CODE>true</CODE> if it is strict, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isStrict();

    /**
     * Retrieves the access flags present in the current
     * <CODE>MethodInfo</CODE>.
     *
     * @return the access flags packed in an integer.
     */
    public int getAccessFlags();

    /**
     * Retrieves the method name for the current <CODE>MethodInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the method name.
     */
    public String getName();

    /**
     * Retrieves the method descriptor for the current <CODE>MethodInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the method descriptor.
     */
    public String getDescriptor();

    /**
     * Retrieves the <I>Code</I> attribute for the current <CODE>MethodInfo</CODE>,
     * if available.
     *
     * @return a <CODE>CodeInfo</CODE> object, or null if not available.
     */
    public CodeInfo getCode();

    /**
     * Retrieves the number of exceptions thrown by the
     * current <CODE>ClassInfo</CODE>.
     *
     * @return an <CODE>integer</CODE> value.
     */
    public int getExceptionCount();
  
    /**
     * Retrieves the class name for an exceptions thrown by the current
     * <CODE>MethodInfo</CODE>. This information is provided by the
     * <I>Exceptions</I> attribute, which is optional.
     *
     * @param index the exception index in the exceptions array.
     * @return an <CODE>String</CODE> object, or null if not available.
     * @exception IndexOutOfBoundsException if the index is out of bounds.
     */
    public String getException(int index);

    /**
     * Retrieves the classes names for the exceptions thrown by the current
     * <CODE>MethodInfo</CODE>. This information is provided by the
     * <I>Exceptions</I> attribute, which is optional.
     *
     * @return an array of <CODE>String</CODE>, or null if not available.
     */
    public String[] getExceptions();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> is synthetic,
     * i.e.it has a <I>Synthetic</I> attribute.
     *
     * @return <CODE>true</CODE> if it is synthetic, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isSynthetic();

    /**
     * Checks if the current <CODE>MethodInfo</CODE> has been
     * deprecated, i.e.it has a <I>Deprecated</I> attribute.
     *
     * @return <CODE>true</CODE> if it is deprecated, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isDeprecated();

    /**
     * The <CODE>CodeInfo</CODE> class represents <I>Code</I>
     * attributes inside a <CODE>MethodInfo</CODE> object.
     */
    public interface CodeInfo {

      /**
       * Retrieves the <CODE>MethodInfo</CODE> object that owns the
       * current <CODE>CodeInfo</CODE>.
       *
       * @return the owner <CODE>MethodInfo</CODE>.
       */
      public MethodInfo getOwner();

      /**
       * Retrieves the maximum operand stack size for the current
       * <CODE>CodeInfo</CODE>.
       *
       * @return an integer containing the maximum operand stack size.
       */
      public int getMaxStack();

      /**
       * Retrieves the maximum local frame size for the current
       * <CODE>CodeInfo</CODE>.
       *
       * @return an integer containing the maximum local frame size.
       */
      public int getMaxLocals();

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
      public byte[] getText();

      /**
       * This method returns useful information regarding the method text for the
       * current <CODE>CodeInfo</CODE>.
       *
       * @return an array of <CODE>int</CODE> or null if not available.
       * @see jewel.core.clfile.Bytecode
       * @see jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo#checkStaticConstraints()
       * @see jewel.core.clfile.ClassInfo.MethodInfo.CodeInfo#checkStructuralConstraints()
       */
      public int[] getTextInfo();

      /**
       * Retrieves the number of exception handlers present in the current
       * <CODE>CodeInfo</CODE>.
       *
       * @return an <CODE>integer</CODE> value.
       */
      public int getHandlerCount();
    
      /**
       * Retrieves a exception handler present in the current <CODE>CodeInfo</CODE>.
       *
       * @param index the handler index in the exception handler table.
       * @return a <CODE>HandlerInfo</CODE> object.
       * @exception IndexOutOfBoundsException if the index is out of bounds.
       */
      public HandlerInfo getHandler(int index);
    
      /**
       * Retrieves the exception handler table for the current <CODE>CodeInfo</CODE>.
       *
       * @return an array of <CODE>HandlerInfo</CODE>.
       */
      public HandlerInfo[] getHandlers();

      /**
       * Retrieves the source file line number for the code in a particular
       * offset of the current <CODE>CodeInfo</CODE> method text.
       *
       * @return the line number, or <CODE>-1</CODE> if the information is
       *         not available.
       */
      public int lineOf(int pc);

      /**
       * Retrieves the local variable name associated with an index in
       * the local frame for a particular offset in the current
       * <CODE>CodeInfo</CODE> method text.
       *
       * @return a <CODE>String</CODE> containing the local variable name,
       *         or <CODE>null</CODE> if the information is not available.
       */
      public String getLocalName(int pc, int index);

      /**
       * Retrieves the local variable descriptor associated with an index
       * in the local frame for a particular offset in the current
       * <CODE>CodeInfo</CODE> method text.
       *
       * @return a <CODE>String</CODE> containing the local variable
       *         descriptor, or <CODE>null</CODE> if the information is
       *         not available.
       */
      public String getLocalDescriptor(int pc, int index);

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
      public void checkStaticConstraints() throws VerifyException;

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
      public void checkStructuralConstraints(Namespace nameSpace) throws VerifyException;

      /**
       * The <CODE>HandlerInfo</CODE> class represents exception
       * handlers inside a <I>Code</I> attribute.
       */
      public interface HandlerInfo {

        /**
         * Retrieves the <CODE>CodeInfo</CODE> object that owns the
         * current <CODE>HandlerInfo</CODE>.
         *
         * @return the owner <CODE>CodeInfo</CODE>.
         */
        public CodeInfo getOwner();

        /**
         * Retrieves the current <CODE>HandlerInfo</CODE> index in the exception handler
         * table of the <CODE>CodeInfo</CODE> object that owns it.
         *
         * @return an <CODE>integer</CODE> as the index.
         */
        public int getIndex();

        /**
         * Retrieves the starting point in the text array where this handler
         * becomes effective.
         *
         * @return the inclusive start PC of the current <CODE>HandlerInfo</CODE>.
         */
        public int getStartPC();

        /**
         * Retrieves the ending point in the text array where this handler
         * becomes uneffective.
         *
         * @return the exclusive end PC of the current <CODE>HandlerInfo</CODE>.
         */
        public int getEndPC();

        /**
         * Retrieves the offset in the text array where this handler
         * code lays.
         *
         * @return the PC of the current <CODE>HandlerInfo</CODE> handler code.
         */
        public int getHandlerPC();

        /**
         * Retrieves the class name of the exception being catched by the
         * current <CODE>HandlerInfo</CODE>.
         *
         * @return a <CODE>String</CODE> object containing the class name,
         *         or null if the current handler was derived from a
         *         <CODE>finally</CODE> construct.
         */
        public String getCatchType();

        /**
         * Checks if the current <CODE>HandlerInfo</CODE> encloses a particular
         * text array offset, i.e.checks if the code in the offset is protected
         * by this handler.
         *
         * @param pc an offset in text array.
         * @return <CODE>true</CODE> if the PC is inside this handler protected
         *         area, <CODE>false</CODE> otherwise.
         */
        public boolean encloses(int pc);

      }

    }

  }

  /**
   * The <CODE>InnerInfo</CODE> class represents <I>InnerClasses</I>
   * attributes inside a <CODE>ClassInfo</CODE> object.
   */
  public interface InnerInfo {

    /**
     * Retrieves the <CODE>ClassInfo</CODE> object that owns the
     * current <CODE>InnerInfo</CODE>.
     *
     * @return the owner <CODE>ClassInfo</CODE>.
     */
    public ClassInfo getOwner();

    /**
     * Retrieves the current <CODE>InnerInfo</CODE> index in the inner class array
     * of the <CODE>ClassInfo</CODE> object that owns it.
     *
     * @return an <CODE>integer</CODE> as the index.
     */
    public int getIndex();

    /**
     * Retrieves the class name for the current <CODE>InnerInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the class name.
     */
    public String getInnerClass();

    /**
     * Retrieves the class name for the class of which the current
     * <CODE>InnerInfo</CODE> is a member.
     *
     * @return a <CODE>String</CODE> object representing the class name,
     *         or null if the class is not a member.
     */
    public String getOuterClass();

    /**
     * Retrieves the simple name for the current <CODE>InnerInfo</CODE>.
     *
     * @return a <CODE>String</CODE> object representing the simple name,
     *         or null if the class is anonymous.
     */
    public String getName();

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is public.
     *
     * @return <CODE>true</CODE> if it is public, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPublic();

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is private.
     *
     * @return <CODE>true</CODE> if it is private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPrivate();

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is protected.
     *
     * @return <CODE>true</CODE> if it is protected, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isProtected();

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is package private.
     *
     * @return <CODE>true</CODE> if it is package private, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isPackagePrivate();

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is static.
     *
     * @return <CODE>true</CODE> if it is static, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isStatic();

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is final.
     *
     * @return <CODE>true</CODE> if it is final, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isFinal();

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is actually an
     * interface rather than a class.
     *
     * @return <CODE>true</CODE> if it is an interface, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isInterface();

    /**
     * Checks if the current <CODE>InnerInfo</CODE> is abstract.
     *
     * @return <CODE>true</CODE> if it is abstract, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isAbstract();

    /**
     * Retrieves the access flags present in the current
     * <CODE>InnerInfo</CODE>.
     *
     * @return the access flags packed in an integer.
     */
    public int getAccessFlags();

  }

}

