/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jewelg;

import jewel.core.clfile.LinkageException;
import jewel.core.jiro.IR;
import jewel.core.jiro.IRCFG;
import jewel.core.jiro.IRStatement;
import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.dataflow.DataFlowAnalyser;
import jewel.core.jiro.dataflow.DataFlowAnalysis;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowFunction;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowItem;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

final class Closure {

  private final IRCFG cfg;

  public Closure(IRCFG cfg) {
    this.cfg = cfg;
  }

  public void exec(Actions actions) throws LinkageException, IllegalAccessException {
    DataFlowAnalyser analyser = new IterativeDataFlowAnalyser(new TypeAnalysis());
    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      TypeItem item = (TypeItem)analyser.valueBefore(stmt);
      Matcher matcher = new Matcher(stmt);
      matcher.exec(actions, item);
      IR.snode snode = stmt.snode();
      if (snode instanceof IR.tnode) {
        IR.tnode tnode = (IR.tnode)snode;
        for (Iterator j = tnode.traces(); j.hasNext(); ) {
          IR.tnode.trace trace = (IR.tnode.trace)j.next();
          actions.meta(trace.getSymbolicType());
        }
      }
    }
  }

  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public Type exec(Actions actions, TypeItem item) throws LinkageException, IllegalAccessException
      : IR.INIT(IR.ACLASS) = {
        actions.init(@2.getSymbolicType());
        return null;
      }
      | IR.INITX(IR.ACLASS) = {
        actions.init(@2.getSymbolicType());
        return null;
      }
      | IR.GETCLASS(exec) = { return @2(actions, item); }
      | IR.MLOOKUP(IR.ACLASS) = {
        actions.call(@2.getSymbolicType(), @1.getDispatchIndex());
        return new Type("java/lang/MethodText");
      }
      | IR.MLOOKUP(exec) [!(@1.left() instanceof IR.aclass)] = {
        Type type = @2(actions, item);
        String[] names = type.names();
        for (int i = 0; i < names.length; i++)
          actions.vcall(names[i], @1.getDispatchIndex());
        return new Type("java/lang/MethodText");
      }
      | IR.IMLOOKUP(exec,IR.ACLASS) = {
        Type type = @2(actions, item);
        String[] names = type.names();
        for (int i = 0; i < names.length; i++)
          actions.icall(names[i], @3.getSymbolicType(), @1.getDispatchIndex());
        return new Type("java/lang/MethodText");
      }
      | IR.ALOAD(exec) = {
        @2(actions, item);
        return new Type(@1.getSymbolicType());
      }
      | IR.AALOAD(exec,exec) = {
        Type type = @2(actions, item);
        @3(actions, item);
        return type.subtype();
      }
      | IR.ANULL = { return new Type(); }
      | IR.ASTRING = { return new Type("java/lang/String"); }
      | IR.ACLASS = { return new Type(@1.getSymbolicType()); }
      | IR.AUSE = { return item.get(new Reg(@1.getReg())); }
      | default = {
        if (left$ != null) left$.exec(actions, item);
        if (middle$ != null) middle$.exec(actions, item);
        if (right$ != null) right$.exec(actions, item);
        return null;
      }
      ;

    public Object stmt(TypeFunction function)
      : IR.ARECEIVE = {
        function.set(new Reg(@1.getReg()), new Type(@1.getSymbolicType()));
        return null;
      }
      | IR.ARESULT = {
        function.set(new Reg(@1.getReg()), new Type(@1.getSymbolicType()));
        return null;
      }
      | IR.ACATCH = {
        function.set(new Reg(@1.getReg()), new Type(@1.getSymbolicType()));
        return null;
      }
      | IR.ADEFINE(stmt) = {
        Object value = @2(function);
        if (value instanceof Reg) {
          function.set(new Reg(@1.getReg()), (Reg)value);
          return null;
        }
        if (value instanceof Access) {
          function.set(new Reg(@1.getReg()), (Access)value);
          return null;
        }
        if (value instanceof Type) {
          function.set(new Reg(@1.getReg()), (Type)value);
          return null;
        }
        throw new IllegalStateException();
      }
      | IR.GETCLASS(stmt) = { return @2(function); }
      | IR.ALOAD(stmt) = { return new Type(@1.getSymbolicType()); }
      | IR.AALOAD(stmt,stmt) = {
        Object value = @2(function);
        if (value instanceof Reg)
          return new Access((Reg)value, 1);
        if (value instanceof Access)
          return new Access(((Access)value).reg(), ((Access)value).times()+1);
        if (value instanceof Type)
          return ((Type)value).subtype();
        throw new IllegalStateException();
      }
      | IR.MLOOKUP(stmt) = { return new Type("java/lang/MethodText"); }
      | IR.IMLOOKUP(stmt,stmt) = { return new Type("java/lang/MethodText"); }
      | IR.ANULL = { return new Type(); }
      | IR.ASTRING = { return new Type("java/lang/String"); }
      | IR.ACLASS = { return new Type(@1.getSymbolicType()); }
      | IR.AUSE = { return new Reg(@1.getReg()); }
      | default = { return null; }
      ;

  }

  public static final class TypeAnalysis implements DataFlowAnalysis {

    public TypeAnalysis() { }

    public byte direction() { return FORWARD; }

    public FlowItem newFlowItem() { return new TypeItem(); }

    public FlowFunction newFlowFunction() { return new TypeFunction(); }

    public void modelEffect(FlowFunction function, Statement stmt) {
      Matcher matcher = new Matcher((IRStatement)stmt);
      matcher.stmt((TypeFunction)function);
    }

    public void modelEffect(FlowFunction function, ControlEdge edge) { }

    public FlowItem merge(FlowItem one, FlowItem another) {
      TypeItem result = (TypeItem)one.clone();
      result.merge((TypeItem)another);
      return result;
    }

  }

  public static final class TypeFunction implements Cloneable, FlowFunction {

    private HashMap values = new HashMap();

    public TypeFunction() { }

    public void set(Reg reg, Reg sreg) {
      if (reg == null) throw new NullPointerException();
      if (sreg == null) throw new NullPointerException();
      Object value = values.get(sreg);
      if (value == null)
        value = sreg;
      values.put(reg, value);
    }

    public void set(Reg reg, Access access) {
      if (reg == null) throw new NullPointerException();
      Object value = values.get(access.reg());
      if (value == null)
        value = access;
      else if (value instanceof Reg)
        value = new Access((Reg)value, access.times());
      else if (value instanceof Access)
        value = new Access(((Access)value).reg(), ((Access)value).times()+access.times());
      else if (value instanceof Type)
        value = ((Type)value).subtype(access.times());
      values.put(reg, value);
    }

    public void set(Reg reg, Type type) {
      if (reg == null) throw new NullPointerException();
      if (type == null) throw new NullPointerException();
      values.put(reg, type);
    }

    public FlowItem apply(FlowItem input) {
      TypeItem output = (TypeItem)input.clone();
      Set set = values.entrySet();
      for (Iterator i = set.iterator(); i.hasNext(); ) {
        Entry entry = (Entry)i.next();
        Reg reg = (Reg)entry.getKey();
        Object value = entry.getValue();
        Type type;
        if (value instanceof Reg)
          type = ((TypeItem)input).get((Reg)value);
        else if (value instanceof Access) {
          Access access = (Access)value;
          type = ((TypeItem)input).get(access.reg());
          type = type.subtype(access.times());
        } else if (value instanceof Type)
          type = (Type)value;
        else
          throw new IllegalStateException();
        output.set(reg, type);
      }
      return output;
    }

    public Object clone() {
      try {
        TypeFunction function = (TypeFunction)super.clone();
        function.values = (HashMap)values.clone();
        return function;
      } catch (CloneNotSupportedException e) {
        throw new InternalError(e.getMessage());
      }
    }

    public int hashCode() {
      return values.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof TypeFunction
          && ((TypeFunction)object).values.equals(values);
    }

    public String toString() {
      return values.toString();
    }

  }

  public static final class TypeItem implements Cloneable, FlowItem {

    private HashMap types = new HashMap();

    public TypeItem() { }

    public Type get(Reg reg) {
      if (reg == null) throw new NullPointerException();
      Type type = (Type)types.get(reg);
      if (type == null)
        type = new Type();
      return type;
    }

    public void set(Reg reg, Type type) {
      if (reg == null) throw new NullPointerException();
      if (type == null) throw new NullPointerException();
      types.put(reg, type);
    }

    public void merge(TypeItem item) {
      Set set = item.types.entrySet();
      for (Iterator i = set.iterator(); i.hasNext(); ) {
        Entry entry = (Entry)i.next();
        Reg reg = (Reg)entry.getKey();
        Type type = (Type)entry.getValue();
        set(reg, type.merge(get(reg)));
      }
    }
    
    public Object clone() {
      try {
        TypeItem item = (TypeItem)super.clone();
        item.types = (HashMap)types.clone();
        return item;
      } catch (CloneNotSupportedException e) {
        throw new InternalError(e.getMessage());
      }
    }

    public int hashCode() {
      return types.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof TypeItem
          && ((TypeItem)object).types.equals(types);
    }

    public String toString() {
      return types.toString();
    }

  }

  public static final class Reg {

    private final int index;

    public Reg(int index) {
      if (index < 0) throw new IllegalArgumentException();
      this.index = index;
    }

    public int hashCode() {
      return index;
    }

    public boolean equals(Object object) {
      return object instanceof Reg
          && ((Reg)object).index == index;
    }

    public String toString() {
      return "%"+index;
    }

  }

  public static final class Access {

    private final Reg reg;
    private final int times;

    public Access(Reg reg, int times) {
      if (reg == null) throw new NullPointerException();
      if (times < 1) throw new IllegalArgumentException();
      this.reg = reg;
      this.times = times;
    }

    public Reg reg() {
      return reg;
    }

    public int times() {
      return times;
    }

    public int hashCode() {
      return times*reg.hashCode();
    }

    public boolean equals(Object object) {
      return object instanceof Access
          && ((Access)object).reg.equals(reg)
          && ((Access)object).times == times;
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(reg);
      for (int i = 0; i < times; i++)
        sb.append("[]");
      return sb.toString();
    }

  }

  public static final class Type {

    private final String[] names;

    public Type() {
      names = new String[]{ };
    }

    public Type(String name) {
      if (name == null) throw new NullPointerException();
      names = new String[]{ name };
    }

    public Type(String[] names) {
      HashSet set = new HashSet();
      for (int i = 0; i < names.length; i++) {
        String name = names[i];
        if (name == null) throw new NullPointerException();
        set.add(name);
      }
      names = (String[])set.toArray(new String[set.size()]);
      Arrays.sort(names);
      this.names = names;
    }

    public String[] names() {
      return (String[])names.clone();
    }

    public Type subtype() {
      String[] names = new String[this.names.length];
      for (int i = 0; i < names.length; i++) {
        String name = this.names[i];
        if (!name.startsWith("["))
          name = "java/lang/Object";
        else {
          name = name.substring(1);
          if (name.startsWith("L"))
            name = name.substring(1, name.length()-1);
        }
        names[i] = name;
      }
      return new Type(names);
    }

    public Type subtype(int times) {
      if (times < 1) throw new IllegalArgumentException();
      Type subtype = subtype();
      return times == 1 ? subtype : subtype.subtype(times-1);
    }

    public Type merge(Type type) {
      String[] names = new String[this.names.length+type.names.length];
      System.arraycopy(this.names, 0, names, 0, this.names.length);
      System.arraycopy(type.names, 0, names, this.names.length, type.names.length);
      return new Type(names);
    }

    public int hashCode() {
      int hashCode = 0;
      for (int i = 0; i < names.length; i++)
        hashCode += names[i].hashCode();
      return hashCode;
    }

    public boolean equals(Object object) {
      if (object instanceof Type) {
        Type type = (Type)object;
        if (names.length != type.names.length)
          return false;
        for (int i = 0; i < names.length; i++)
          if (!names[i].equals(type.names[i]))
            return false;
        return true;
      }
      return false;
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append('{');
      for (int i = 0; i < names.length; i++) {
        sb.append(names[i]);
        if (i+1 < names.length)
          sb.append(',');
      }
      sb.append('}');
      return sb.toString();
    }

  }

}

