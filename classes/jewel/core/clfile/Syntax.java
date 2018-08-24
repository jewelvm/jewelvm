/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.clfile;

/**
 * This class provides useful routines to manipulate
 * names and descriptors.
 */
public final class Syntax implements ClassFileConstants {

  /**
   * Checks is a <CODE>String</CODE> object represents a
   * valid Java identifier.
   *
   * @param identifier the <CODE>String</CODE> object to
   *        be checked.
   * @return true, if the <CODE>String</CODE> is a valid
   *         Java identifier, false otherwise.
   */
  public static boolean isIdentifier(String identifier) {
    int length = identifier.length();
    if (length == 0)
      return false;
    if (!Character.isJavaIdentifierStart(identifier.charAt(0)))
      return false;
    for (int i = 1; i < length; i++)
      if (!Character.isJavaIdentifierPart(identifier.charAt(i)))
        return false;
    return true;
  }

  /**
   * Checks is a <CODE>String</CODE> object represents a
   * valid fully qualified class name, in its internal
   * form.
   *
   * @param name the <CODE>String</CODE> object to
   *        be checked.
   * @return true, if the <CODE>String</CODE> is a valid
   *         fully qualified class name, false otherwise.
   */
  public static boolean isFullyQualifiedName(String name) {
    int length = name.length();
    int begin = 0;
    while (begin <= length) {
      int end = name.indexOf(OBJECT_SEP_ID, begin);
      if (end == -1)
        end = length;
      if (!isIdentifier(name.substring(begin, end)))
        return false;
      begin = end+1;
    }
    return true;
  }

  /**
   * Checks is a <CODE>String</CODE> object represents a
   * valid class or array name.
   *
   * @param name the <CODE>String</CODE> object to
   *        be checked.
   * @return true, if the <CODE>String</CODE> is a valid
   *         class or array name, false otherwise.
   */
  public static boolean isClassName(String name) {
    int length = name.length();
    if (length == 0)
      return false;
    int dims = 0;
    while (name.charAt(dims) == ARRAY_ID) {
      dims++;
      if (dims == length)
        return false;
    }
    if (dims == 0)
      return isFullyQualifiedName(name);
    if (dims > 255)
      return false;
    int begin = dims;
    int end = length;
    switch (name.charAt(begin)) {
    case BOOLEAN_ID: case CHAR_ID:
    case FLOAT_ID: case DOUBLE_ID:
    case BYTE_ID: case SHORT_ID:
    case INT_ID: case LONG_ID:
      return begin+1 == end;
    case OBJECT_ID:
      if (name.charAt(end-1) != OBJECT_TERM_ID)
        return false;
      return isFullyQualifiedName(name.substring(begin+1, end-1));
    }
    return false;
  }

  /**
   * Retrieves the package name for a given class or array name.
   * This method assumes that the parameter name is a valid
   * class or array name, otherwise this method may have an
   * unpredicted behaviour.
   *
   * @param name the a valid class or array name.
   * @return an <CODE>String</CODE> object representing the package name.
   */
  public static String getPackage(String name) {
    int begin = name.lastIndexOf(ARRAY_ID);
    begin = begin == -1 ? 0 : begin+2;
    int end = name.lastIndexOf(OBJECT_SEP_ID);
    end = end == -1 ? begin : end;
    return name.substring(begin, end);
  }

  /**
   * Checks is a <CODE>String</CODE> object represents a
   * valid field descriptor.
   *
   * @param descriptor the <CODE>String</CODE> object to
   *        be checked.
   * @return true, if the <CODE>String</CODE> is a valid
   *         field descriptor, false otherwise.
   */
  public static boolean isFieldDescriptor(String descriptor) {
    int length = descriptor.length();
    if (length == 0)
      return false;
    switch (descriptor.charAt(0)) {
    case BOOLEAN_ID: case CHAR_ID:
    case FLOAT_ID: case DOUBLE_ID:
    case BYTE_ID: case SHORT_ID:
    case INT_ID: case LONG_ID:
      return length == 1;
    case ARRAY_ID:
      return isClassName(descriptor);
    case OBJECT_ID:
      if (descriptor.charAt(length-1) != OBJECT_TERM_ID)
        return false;
      return isFullyQualifiedName(descriptor.substring(1, length-1));
    }
    return false;
  }

  /**
   * Checks is a <CODE>String</CODE> object represents a
   * valid method descriptor.
   *
   * @param descriptor the <CODE>String</CODE> object to
   *        be checked.
   * @return true, if the <CODE>String</CODE> is a valid
   *         method descriptor, false otherwise.
   */
  public static boolean isMethodDescriptor(String descriptor) {
    int length = descriptor.length();
    if (length == 0)
      return false;
    if (descriptor.charAt(0) != PARAM_ID)
      return false;
    int paramsBegin = 1;
    int paramsEnd = descriptor.indexOf(PARAM_TERM_ID, paramsBegin);
    if (paramsEnd == -1)
      return false;
    int retBegin = paramsEnd+1;
    int retEnd = length;
    if (retBegin == retEnd)
      return false;
    int size = 0;
    for (int i = paramsBegin; i < paramsEnd; i++) {
      int dims = 0;
      while (descriptor.charAt(i) == ARRAY_ID) {
        dims++;
        i++;
        if (i == paramsEnd)
          return false;
      }
      if (dims > 255)
        return false;
      switch (descriptor.charAt(i)) {
      case BOOLEAN_ID: case CHAR_ID:
      case BYTE_ID: case SHORT_ID:
      case FLOAT_ID: case INT_ID:
        size++;
        break;
      case LONG_ID: case DOUBLE_ID:
        size += dims == 0 ? 2 : 1;
        break;
      case OBJECT_ID:
        int begin = i+1;
        int end = descriptor.indexOf(OBJECT_TERM_ID, begin);
        if (end == -1 || end > paramsEnd)
          return false;
        if (!isFullyQualifiedName(descriptor.substring(begin, end)))
          return false;
        i = end;
        size++;
        break;
      default:
        return false;
      }
    }
    if (size > 255)
      return false;
    if (descriptor.charAt(retBegin) == VOID_ID)
      return retBegin+1 == retEnd;
    return isFieldDescriptor(descriptor.substring(retBegin, retEnd));
  }

  /**
   * Retrieves the size of parameters for a particular
   * method descriptor. The size is the number of
   * machine words required by a method with this descriptor.
   * Longs and doubles contributes with two units to the size.
   * If the method has a this implicit parameter the size is
   * increased of a unit. This method assumes that the parameter
   * descriptor is a valid method descriptor, otherwise this
   * method may have an unpredicted behaviour.
   *
   * @param descriptor the a valid method descriptor.
   * @param hasThis a flag indicating if the method that has
   *        that descriptor has an implicit this parameter.
   * @return the number of parameters in the descriptor.
   */
  public static int getParametersSize(String descriptor, boolean hasThis) {
    int size = hasThis ? 1 : 0;
    int paramsBegin = 1;
    int paramsEnd = descriptor.lastIndexOf(PARAM_TERM_ID);
    for (int i = paramsBegin; i < paramsEnd; i++)
      switch (descriptor.charAt(i)) {
      case LONG_ID: case DOUBLE_ID:
        size += 2;
        break;
      case OBJECT_ID:
        i = descriptor.indexOf(OBJECT_TERM_ID, i+1);
        size++;
        break;
      case ARRAY_ID:
        while (descriptor.charAt(i) == ARRAY_ID)
          i++;
        if (descriptor.charAt(i) == OBJECT_ID)
          i = descriptor.indexOf(OBJECT_TERM_ID, i+1);
        size++;
        break;
      default:
        size++;
      }
    return size;
  }

  /**
   * Retrieves the number of parameters for a particular
   * method descriptor. This method assumes that the parameter
   * descriptor is a valid method descriptor, otherwise this
   * method may have an unpredicted behaviour.
   *
   * @param descriptor the a valid method descriptor.
   * @return the number of parameters in the descriptor.
   */
  public static int getParameterCount(String descriptor) {
    int count = 0;
    int paramsBegin = 1;
    int paramsEnd = descriptor.lastIndexOf(PARAM_TERM_ID);
    for (int i = paramsBegin; i < paramsEnd; i++) {
      switch (descriptor.charAt(i)) {
      case OBJECT_ID:
        i = descriptor.indexOf(OBJECT_TERM_ID, i+1);
        break;
      case ARRAY_ID:
        while (descriptor.charAt(i) == ARRAY_ID)
          i++;
        if (descriptor.charAt(i) == OBJECT_ID)
          i = descriptor.indexOf(OBJECT_TERM_ID, i+1);
      }
      count++;
    }
    return count;
  }

  /**
   * Retrieves the parameter types of a method descriptor as
   * an array of <CODE>String</CODE> object. This method assumes
   * that the parameter descriptor is a valid method descriptor,
   * otherwise this method may have an unpredicted behaviour.
   *
   * @param descriptor the descriptor from which extract the types.
   * @return an array of <CODE>String</CODE> objects containing each
   *         parameter type in the order they appear in the descriptor.
   */
  public static String[] getParameterTypes(String descriptor) {
    int count = getParameterCount(descriptor);
    String[] paramTypes = new String[count];
    int paramsBegin = 1;
    int paramsEnd = descriptor.lastIndexOf(PARAM_TERM_ID);
    int index = 0;
    for (int i = paramsBegin; i < paramsEnd; i++) {
      int begin = i;
      switch (descriptor.charAt(i)) {
      case OBJECT_ID:
        i = descriptor.indexOf(OBJECT_TERM_ID, i+1);
        break;
      case ARRAY_ID:
        while (descriptor.charAt(i) == ARRAY_ID)
          i++;
        if (descriptor.charAt(i) == OBJECT_ID)
          i = descriptor.indexOf(OBJECT_TERM_ID, i+1);
      }
      int end = i+1;
      paramTypes[index++] = descriptor.substring(begin, end);
    }
    return paramTypes;
  }

  /**
   * Retrieves the return type of a method descriptor as
   * a <CODE>String</CODE> object. This method assumes
   * that the parameter descriptor is a valid method descriptor,
   * otherwise this method may have an unpredicted behaviour.
   *
   * @param descriptor the descriptor from which extract the type.
   * @return an <CODE>String</CODE> object containing the return type.
   */
  public static String getReturnType(String descriptor) {
    return descriptor.substring(descriptor.lastIndexOf(PARAM_TERM_ID)+1);
  }

}

