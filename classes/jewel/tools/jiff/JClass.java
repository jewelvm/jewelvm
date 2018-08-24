/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jiff;

import jewel.core.clfile.ClassInfo;
import jewel.core.clfile.ClassInfo.FieldInfo;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.FilterClassInfo;
import jewel.core.clfile.FilterClassInfo.FilterFieldInfo;
import jewel.core.clfile.FilterClassInfo.FilterMethodInfo;

import java.util.HashSet;

final class JClass extends FilterClassInfo implements Comparable {

  private final JClass superclass;
  private final JClass[] interfaces;

  public JClass(ClassInfo underlying, ClassInfo superclass, ClassInfo[] interfaces) {
    super(underlying);
    this.superclass = (JClass)superclass;
    this.interfaces = new JClass[interfaces.length];
    for (int i = 0; i < interfaces.length; i++)
      this.interfaces[i] = (JClass)interfaces[i];
  }

  public int compareTo(Object object) {
    return compareTo((JClass)object);
  }

  public int compareTo(JClass clazz) {
    return getName().compareTo(clazz.getName());
  }

  protected FieldInfo filter(FieldInfo field) {
    return new JField(field);
  }

  protected MethodInfo filter(MethodInfo method) {
    return new JMethod(method);
  }

  public ClassInfo[] getAllTypes() {
    ClassInfo[] supertypes = new ClassInfo[]{ };
    supertypes = (ClassInfo[])concat(supertypes, new ClassInfo[]{ this });
    if (superclass != null)
      supertypes = (ClassInfo[])concat(supertypes, superclass.getAllTypes());
    for (int i = 0; i < interfaces.length; i++)
      supertypes = (ClassInfo[])concat(supertypes, interfaces[i].getAllTypes());
    return supertypes;
  }

  public FieldInfo[] getAllFields() {
    FieldInfo[] fields = new FieldInfo[]{ };
    fields = (FieldInfo[])concat(fields, getFields());
    for (int i = 0; i < interfaces.length; i++)
      fields = (FieldInfo[])concat(fields, interfaces[i].getAllFields());
    if (superclass != null)
      fields = (FieldInfo[])concat(fields, superclass.getAllFields());
    return fields;
  }

  public MethodInfo[] getAllMethods() {
    MethodInfo[] methods = new MethodInfo[]{ };
    methods = (MethodInfo[])concat(methods, getMethods());
    if (superclass != null)
      methods = (MethodInfo[])concat(methods, superclass.getAllMethods());
    for (int i = 0; i < interfaces.length; i++)
      methods = (MethodInfo[])concat(methods, interfaces[i].getAllMethods());
    return methods;
  }

  private static Object concat(Object[] left, Object[] right) {
    HashSet set = new HashSet();
    for (int i = 0; i < left.length; i++)
      set.add(left[i]);
    for (int i = 0; i < right.length; i++)
      set.add(right[i]);
    return set.toArray(left);
  }

  private final class JField extends FilterFieldInfo implements Comparable {

    public JField(FieldInfo underlying) {
      super(underlying);
    }

    public int compareTo(Object object) {
      return compareTo((JField)object);
    }

    public int compareTo(JField field) {
      int result = this.getName().compareTo(field.getName());
      if (result == 0)
        result = this.getDescriptor().compareTo(field.getDescriptor());
      return result;
    }

    public int hashCode() {
      return this.getName().hashCode()
           + getDescriptor().hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof JField
          && ((JField)object).getName().equals(this.getName())
          && ((JField)object).getDescriptor().equals(getDescriptor());
    }

  }

  private final class JMethod extends FilterMethodInfo implements Comparable {

    public JMethod(MethodInfo underlying) {
      super(underlying);
    }

    public int compareTo(Object object) {
      return compareTo((JMethod)object);
    }

    public int compareTo(JMethod method) {
      int result = this.getName().compareTo(method.getName());
      if (result == 0)
        result = this.getDescriptor().compareTo(method.getDescriptor());
      return result;
    }

    public int hashCode() {
      return this.getName().hashCode()
           + getDescriptor().hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof JMethod
          && ((JMethod)object).getName().equals(this.getName())
          && ((JMethod)object).getDescriptor().equals(getDescriptor());
    }

  }

}

