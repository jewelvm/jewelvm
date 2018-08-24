/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.Context;
import jewel.core.ContextClassInfo;
import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.beg.TreeNode.LeafRef;
import jewel.core.jiro.beg.TreeNode.LeftRef;
import jewel.core.jiro.beg.TreeNode.MiddleRef;
import jewel.core.jiro.beg.TreeNode.RightRef;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.Iterator;

// updated for SSA
public final strictfp class ConstantFolding implements Optimization {

  public ConstantFolding() { }

  public boolean applyTo(ControlFlowGraph cfg) {
    boolean changed = false;

    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      Matcher matcher = new Matcher(stmt);
      changed |= matcher.cover(null);
    }

    return changed;
  }

  private static boolean subtypeof(String subclass, String superclass) {
    return superclass.equals(subclass);
  }
  
  private static boolean comptypeof(String compclass, String arrayclass) {
    return arrayclass.equals(compclass.startsWith("[") ? "["+compclass : "[L"+compclass+";");
  }
  
  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public boolean cover(LeafRef ref)
      : iconst = { ref.set(new IR.iconst(@1.value)); return true; }
      | lconst = { ref.set(new IR.lconst(@1.value)); return true; }
      | fconst = { ref.set(new IR.fconst(@1.value)); return true; }
      | dconst = { ref.set(new IR.dconst(@1.value)); return true; }
      | IR.GETCLASS(IR.ACLASS) = { ref.set(new IR.aclass("java/lang/Class")); return true; } // review
      | IR.GETCLASS(IR.ASTRING) = { ref.set(new IR.aclass("java/lang/String")); return true; } // review
      | IR.IMLOOKUP(IR.ACLASS,IR.ACLASS) = { // review
        Context context = Context.get();
        ContextClassInfo clazz = context.forName(@2.getSymbolicType());
        ContextClassInfo iface = context.forName(@3.getSymbolicType());
        if (clazz != null && iface != null) {
          int index = clazz.lookupInterfaceBaseIndex(iface);
          if (index == -1)
            ref.set(new IR.anull());
          else
            ref.set(new IR.mlookup(@2, index+@1.getDispatchIndex()));
          return true;
        }
        return false;
      }
      | default = {
        boolean changed = false;
        if (  left$ != null) changed |=   left$.cover(new   LeftRef(@1));
        if (middle$ != null) changed |= middle$.cover(new MiddleRef(@1));
        if ( right$ != null) changed |=  right$.cover(new  RightRef(@1));
        return changed;
      }
      ;

    private void iconst()
    <int value>
      : IR.I2B(iconst2)           { @@.value = (byte)@2.value; }
      | IR.I2C(iconst2)           { @@.value = (char)@2.value; }
      | IR.I2S(iconst2)           { @@.value = (short)@2.value; }
      | IR.L2I(lconst2)           { @@.value = (int)@2.value; }
      | IR.F2I(fconst2)           { @@.value = (int)@2.value; }
      | IR.D2I(dconst2)           { @@.value = (int)@2.value; }
      | IR.IADD(iconst2,iconst2)  { @@.value = @2.value + @3.value; }
      | IR.ISUB(iconst2,iconst2)  { @@.value = @2.value - @3.value; }
      | IR.IMUL(iconst2,iconst2)  { @@.value = @2.value * @3.value; }
      | IR.IDIV(iconst2,iconst2)  [@3.value != 0] { @@.value = @2.value / @3.value; }
      | IR.IREM(iconst2,iconst2)  [@3.value != 0] { @@.value = @2.value % @3.value; }
      | IR.INEG(iconst2)          { @@.value = -@2.value; }
      | IR.ISHL(iconst2,iconst2)  { @@.value = @2.value << @3.value; }
      | IR.ISHR(iconst2,iconst2)  { @@.value = @2.value >> @3.value; }
      | IR.IUSHR(iconst2,iconst2) { @@.value = @2.value >>> @3.value; }
      | IR.IAND(iconst2,iconst2)  { @@.value = @2.value & @3.value; }
      | IR.IOR(iconst2,iconst2)   { @@.value = @2.value | @3.value; }
      | IR.IXOR(iconst2,iconst2)  { @@.value = @2.value ^ @3.value; }
      | IR.LCMP(lconst2,lconst2)  { @@.value = @2.value < @3.value ? -1 : @2.value > @3.value ? 1 : 0; }
      | IR.FCMPG(fconst2,fconst2) { @@.value = @2.value < @3.value ? -1 : @2.value == @3.value ? 0 : 1; }
      | IR.FCMPL(fconst2,fconst2) { @@.value = @2.value > @3.value ? 1 : @2.value == @3.value ? 0 : -1; }
      | IR.DCMPG(dconst2,dconst2) { @@.value = @2.value < @3.value ? -1 : @2.value == @3.value ? 0 : 1; }
      | IR.DCMPL(dconst2,dconst2) { @@.value = @2.value > @3.value ? 1 : @2.value == @3.value ? 0 : -1; }
      | IR.SUBTYPEOF(IR.ACLASS,IR.ACLASS) [subtypeof(@2.getSymbolicType(), @3.getSymbolicType())] { @@.value = 1; }
      | IR.COMPTYPEOF(IR.ACLASS,IR.ACLASS) [comptypeof(@2.getSymbolicType(), @3.getSymbolicType())] { @@.value = 1; }
      ;

    private void iconst2()
    <int value>
      : iconst    { @@.value = @1.value; }
      | IR.ICONST { @@.value = @1.getValue(); }
      ;

    private void lconst()
    <long value>
      : IR.I2L(iconst2)           { @@.value = (long)@2.value; }
      | IR.F2L(fconst2)           { @@.value = (long)@2.value; }
      | IR.D2L(dconst2)           { @@.value = (long)@2.value; }
      | IR.LADD(lconst2,lconst2)  { @@.value = @2.value + @3.value; }
      | IR.LSUB(lconst2,lconst2)  { @@.value = @2.value - @3.value; }
      | IR.LMUL(lconst2,lconst2)  { @@.value = @2.value * @3.value; }
      | IR.LDIV(lconst2,lconst2)  [@3.value != 0] { @@.value = @2.value / @3.value; }
      | IR.LREM(lconst2,lconst2)  [@3.value != 0] { @@.value = @2.value % @3.value; }
      | IR.LNEG(lconst2)          { @@.value = -@2.value; }
      | IR.LSHL(lconst2,iconst2)  { @@.value = @2.value << @3.value; }
      | IR.LSHR(lconst2,iconst2)  { @@.value = @2.value >> @3.value; }
      | IR.LUSHR(lconst2,iconst2) { @@.value = @2.value >>> @3.value; }
      | IR.LAND(lconst2,lconst2)  { @@.value = @2.value & @3.value; }
      | IR.LOR(lconst2,lconst2)   { @@.value = @2.value | @3.value; }
      | IR.LXOR(lconst2,lconst2)  { @@.value = @2.value ^ @3.value; }
      ;

    private void lconst2()
    <long value>
      : lconst    { @@.value = @1.value; }
      | IR.LCONST { @@.value = @1.getValue(); }
      ;

    private void fconst()
    <float value>
      : IR.I2F(iconst2)          { @@.value = (float)@2.value; }
      | IR.L2F(lconst2)          { @@.value = (float)@2.value; }
      | IR.D2F(dconst2)          { @@.value = (float)@2.value; }
      | IR.FSTRICT(fconst2)      { @@.value = @2.value; }
      | IR.FADD(fconst2,fconst2) { @@.value = @2.value + @3.value; }
      | IR.FSUB(fconst2,fconst2) { @@.value = @2.value - @3.value; }
      | IR.FMUL(fconst2,fconst2) { @@.value = @2.value * @3.value; }
      | IR.FDIV(fconst2,fconst2) { @@.value = @2.value / @3.value; }
      | IR.FREM(fconst2,fconst2) { @@.value = @2.value % @3.value; }
      | IR.FNEG(fconst2)         { @@.value = -@2.value; }
      ;

      private void fconst2()
      <float value>
        : fconst    { @@.value = @1.value; }
        | IR.FCONST { @@.value = @1.getValue(); }
        ;

    private void dconst()
    <double value>
      : IR.I2D(iconst2)          { @@.value = (double)@2.value; }
      | IR.L2D(lconst2)          { @@.value = (double)@2.value; }
      | IR.F2D(fconst2)          { @@.value = (double)@2.value; }
      | IR.DSTRICT(dconst2)      { @@.value = @2.value; }
      | IR.DADD(dconst2,dconst2) { @@.value = @2.value + @3.value; }
      | IR.DSUB(dconst2,dconst2) { @@.value = @2.value - @3.value; }
      | IR.DMUL(dconst2,dconst2) { @@.value = @2.value * @3.value; }
      | IR.DDIV(dconst2,dconst2) { @@.value = @2.value / @3.value; }
      | IR.DREM(dconst2,dconst2) { @@.value = @2.value % @3.value; }
      | IR.DNEG(dconst2)         { @@.value = -@2.value; }
      ;

    private void dconst2()
    <double value>
      : dconst    { @@.value = @1.value; }
      | IR.DCONST { @@.value = @1.getValue(); }
      ;

  }

}

