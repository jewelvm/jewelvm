/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.clfile;

/**
 * This interface is used during class file verification.
 * It provides a customized information about classes and
 * hierarchy.
 */
public interface Namespace {

  /**
   * The <CODE>AbstractType</CODE> interface represents
   * a class in the <CODE>Namespace</CODE> object during
   * class file verification. Implementations of this
   * interface should adere to the contract of object
   * equality.
   */
  public interface AbstractType {

    /**
     * Checks if the current <CODE>AbstractType</CODE> is an
     * array.
     *
     * @return <CODE>true</CODE> if it is an array, <CODE>false</CODE>
     *         otherwise.
     */
    public boolean isArray();

    /**
     * Checks if the current <CODE>AbstractType</CODE> is a
     * reference array.
     *
     * @return <CODE>true</CODE> if it is a reference array,
     *         <CODE>false</CODE> otherwise.
     */
    public boolean isReferenceArray();

    /**
     * Checks if the current <CODE>AbstractType</CODE> is a
     * <CODE>boolean</CODE> or <CODE>byte</CODE> array.
     *
     * @return <CODE>true</CODE> if it is a <CODE>boolean</CODE>
     *         or <CODE>byte</CODE> array, <CODE>false</CODE> otherwise.
     */
    public boolean isBooleanOrByteArray();

    /**
     * Retrieves the <CODE>AbstractType</CODE> representing the
     * component type of this <CODE>AbstractType</CODE>. This method
     * will only be called when this abstract type represents a
     * reference array, may have an undefined behaviour otherwise.
     *
     * @return an <CODE>AbstractType</CODE> object representing the
     *         component type.
     */
    public AbstractType getComponentType();

  }
  
  /**
   * Retrieves the corresponding <CODE>AbstractType</CODE> in the
   * current <CODE>Namespace</CODE> for a given class name in its
   * fully qualified internal form.
   *
   * @param name a <CODE>String</CODE> object.
   * @return an <CODE>AbstractType</CODE> object.
   */
  public AbstractType forName(String name);

  /**
   * This method provides an <CODE>AbstractType</CODE> representing
   * the first common superclass of two other abstract types.
   *
   * @param one an <CODE>AbstractType</CODE>.
   * @param another an <CODE>AbstractType</CODE>.
   * @return an <CODE>AbstractType</CODE> representing the first common
   *         superclass.
   */
  public AbstractType findCommonSuperclass(AbstractType one, AbstractType another);

  /**
   * This method is called when the verifier needs to guarantee type
   * compatibility for two abstract types. This method should throw
   * an instance of <CODE>VerifyException</CODE> if there is no type
   * compatibility, or return successfully otherwise. Also this method
   * may return successfully, internally keeping this subtyping constraint.
   * Afterwards, when the information is available, this <CODE>Namespace</CODE>
   * should check for the type compatibility.
   *
   * @param type an <CODE>AbstractType</CODE>.
   * @param subType an <CODE>AbstractType</CODE> that must be a subtype of type.
   * @exception VerifyException if the types are not compatible.
   */
  public void ensureSubtyping(AbstractType type, AbstractType subType) throws VerifyException;

}

