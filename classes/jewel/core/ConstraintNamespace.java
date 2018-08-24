/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.clfile.Namespace;
import jewel.core.clfile.Namespace.AbstractType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public final class ConstraintNamespace implements Cloneable, Namespace {

  private final class SymbolicType implements AbstractType {

    private final Object value;

    public SymbolicType(DataInputStream in) throws IOException {
      int count = in.readInt();
      if (count == 1)
        value = in.readUTF();
      else {
        String[] strings = new String[count];
        for (int i = 0; i < strings.length; i++)
          strings[i] = in.readUTF();
        value = strings;
      }
    }

    public SymbolicType(String value) {
      this.value = value;
    }

    public SymbolicType(String value1, String value2) {
      value = new String[]{ value1, value2 };
    }

    public SymbolicType(String[] value1, int index, String value2) {
      String[] value3 = new String[value1.length+1];
      System.arraycopy(value1, 0, value3, 0, index);
      value3[index] = value2;
      System.arraycopy(value1, index, value3, index+1, value1.length-index);
      value = value3;
    }

    public SymbolicType(String[] value1, String[] value2) {
      int count = value1.length;
      for (int i = 0; i < value2.length; i++) {
        int index = Arrays.binarySearch(value1, value2[i]);
        if (index < 0)
          count++;
      }
      String[] value3 = new String[count];
      count = value1.length;
      System.arraycopy(value1, 0, value3, 0, count);
      for (int i = 0; i < value2.length; i++) {
        int index = Arrays.binarySearch(value1, value2[i]);
        if (index < 0)
          value3[count++] = value2[i];
      }
      Arrays.sort(value3);
      value = value3;
    }

    public String[] getNames() {
      if (value instanceof String)
        return new String[] { (String)value };
      return (String[])((String[])value).clone();
    }

    public boolean isArray() {
      return (value instanceof String && ((String)value).charAt(0) == '[') || isReferenceArray();
    }

    public boolean isReferenceArray() {
      String[] names = getNames();
      for (int i = 0; i < names.length; i++)
        if (names[i].charAt(0) != '[' || (names[i].charAt(1) != 'L' && names[i].charAt(1) != '['))
          return false;
      return true;
    }

    public boolean isBooleanOrByteArray() {
      return value instanceof String 
        && ((String)value).charAt(0) == '['
        && (((String)value).charAt(1) == 'Z' || ((String)value).charAt(1) == 'B');
    }

    public AbstractType getComponentType() {
      String[] names = getNames();
      for (int i = 0; i < names.length; i++) {
        String name = names[i];
        name = name.substring(1);
        if (name.charAt(0) == 'L')
          name = name.substring(1, name.length()-1);
        names[i] = name;
      }
      AbstractType ctype = forName(names[0]);
      for (int i = 1; i < names.length; i++)
        ctype = findCommonSuperclass(ctype, forName(names[i]));
      return ctype;
    }

    public int hashCode() {
      if (value instanceof String)
        return value.hashCode();
      int hashCode = 0;
      String[] strings = (String[])value;
      for (int i = 0; i < strings.length; i++)
        hashCode ^= strings[i].hashCode();
      return hashCode;
    }

    public boolean equals(Object object) {
      if (object == this)
        return true;
      if (object instanceof SymbolicType) {
        SymbolicType symbolicType = (SymbolicType)object;
        if (value.equals(symbolicType.value))
          return true;
        if (value instanceof String[] && symbolicType.value instanceof String[])
          return Arrays.equals((String[])value, (String[])symbolicType.value);
      }
      return false;
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      if (value instanceof String) {
        String string = (String)value;
        sb.append(string);
      } else {
        String[] strings = (String[])value;
        sb.append('S');
        sb.append('(');
        for (int i = 0; i < strings.length; i++) {
          String string = strings[i];
          sb.append(string);
          sb.append(',');
        }
        sb.setLength(sb.length()-1);
        sb.append(')');
      }
      return sb.toString();
    }

    public void writeTo(DataOutputStream out) throws IOException {
      if (value instanceof String) {
        String string = (String)value;
        out.writeInt(1);
        out.writeUTF(string);
      } else {
        String[] strings = (String[])value;
        out.writeInt(strings.length);
        for (int i = 0; i < strings.length; i++)
          out.writeUTF(strings[i]);
      }
    }

  }

  private final static class Constraint {

    private final SymbolicType superType;
    private final SymbolicType subType;

    public Constraint(SymbolicType superType, SymbolicType subType) {
      this.superType = superType;
      this.subType = subType;
    }

    public SymbolicType getSuperType() {
      return superType;
    }

    public SymbolicType getSubType() {
      return subType;
    }

    public int hashCode() {
      return superType.hashCode() ^ subType.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof Constraint
          && (((Constraint)object).superType).equals(superType)
          && (((Constraint)object).subType).equals(subType);
    }

    public String toString() {
      return superType+" <= "+subType;
    }

    public void writeTo(DataOutputStream out) throws IOException {
      superType.writeTo(out);
      subType.writeTo(out);
    }

  }

  private HashMap map;
  private HashSet set = new HashSet();

  public ConstraintNamespace() { }

  public void addAll(ConstraintNamespace namespace) {
    set.addAll(namespace.set);
  }

  public AbstractType forName(String name) {
    if (map == null)
      map = new HashMap();
    SymbolicType symbolicType = (SymbolicType)map.get(name);
    if (symbolicType == null) {
      symbolicType = new SymbolicType(name);
      map.put(name, symbolicType);
    }
    return symbolicType;
  }

  public AbstractType findCommonSuperclass(AbstractType one, AbstractType another) {
    SymbolicType a = (SymbolicType)one;
    SymbolicType b = (SymbolicType)another;
    if (a.value instanceof String) {
      String p0 = (String)a.value;
      if (b.value instanceof String) {
        String p1 = (String)b.value;
        int result = p0.compareTo(p1);
        if (result < 0)
          return new SymbolicType(p0, p1);
        else if (result > 0)
          return new SymbolicType(p1, p0);
        return a;
      } else {
        String[] p1 = (String[])b.value;
        int result = Arrays.binarySearch(p1, p0);
        if (result < 0)
          return new SymbolicType(p1, ~result, p0);
        return b;
      }
    } else {
      String[] p0 = (String[])a.value;
      if (b.value instanceof String) {
        String p1 = (String)b.value;
        int result = Arrays.binarySearch(p0, p1);
        if (result < 0)
          return new SymbolicType(p0, ~result, p1);
        return a;
      } else {
        String[] p1 = (String[])b.value;
        if (!Arrays.equals(p0, p1))
          return new SymbolicType(p0, p1);
        return a;
      }
    }
  }

  public void ensureSubtyping(AbstractType a, AbstractType b) {
    if (!a.equals(b))
      set.add(new Constraint((SymbolicType)a, (SymbolicType)b));
  }

  public Object clone() {
    try {
      ConstraintNamespace namespace = (ConstraintNamespace)super.clone();
      namespace.map = null;
      namespace.set = (HashSet)set.clone();
      return namespace;
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e.getMessage());
    }
  }

  public String toString() {
    return set.toString();
  }

  public void readFrom(DataInputStream in) throws IOException {
    int constraintCount = in.readInt();
    for (int i = 0; i < constraintCount; i++) {
      SymbolicType superType = new SymbolicType(in);
      SymbolicType subType = new SymbolicType(in);
      Constraint constraint = new Constraint(superType, subType);
      set.add(constraint);
    }
  }

  public void writeTo(DataOutputStream out) throws IOException {
    out.writeInt(set.size());
    for (Iterator i = set.iterator(); i.hasNext(); ) {
      Constraint constraint = (Constraint)i.next();
      constraint.writeTo(out);
    }
  }

  public String[][] getTargets() {
    int targetCount = set.size();
    String[][] targets = new String[targetCount][];
    int index = 0;
    for (Iterator i = set.iterator(); i.hasNext(); index++) {
      Constraint constraint = (Constraint)i.next();
      SymbolicType target = constraint.getSuperType();
      targets[index] = target.getNames();
    }
    return targets;
  }

  public String[][] getSources() {
    int sourceCount = set.size();
    String[][] sources = new String[sourceCount][];
    int index = 0;
    for (Iterator i = set.iterator(); i.hasNext(); index++) {
      Constraint constraint = (Constraint)i.next();
      SymbolicType source = constraint.getSubType();
      sources[index] = source.getNames();
    }
    return sources;
  }

}

