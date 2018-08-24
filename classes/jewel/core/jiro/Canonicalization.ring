/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.beg.TreeNode.LeafRef;
import jewel.core.jiro.beg.TreeNode.LeftRef;
import jewel.core.jiro.beg.TreeNode.MiddleRef;
import jewel.core.jiro.beg.TreeNode.RightRef;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;

import java.util.Iterator;

public final class Canonicalization implements Optimization {

  public Canonicalization() { }

  public boolean applyTo(ControlFlowGraph cfg) {
    boolean changed = false;

    for (Iterator i = cfg.topdownStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      //String before = stmt.toString();
      for (;;) {
        Matcher matcher = new Matcher(stmt);
        if (!matcher.cover(null))
          break;
        //System.err.println("LOOP: "+stmt);
        changed = true;
      }
      //String after = stmt.toString();
      //if (!before.equals(after))
        //System.err.println("CANON: "+before+" => "+after);
    }

    return changed;
  }
  
  /* E = { (x+y)+z, (x+y)-z, (x-y)+z, (x-y)-z, -(x+y), x+y, x-y, x+c, c-x, x, -x, c } */
  private static final class Matcher {

    public Matcher(IRStatement stmt) {
      this(stmt.snode());
    }

    public boolean cover(LeafRef ref)
      /* x+(y+z) => (x+y)+z */
      : IR.IADD(iany,IR.IADD(iany,iany)) = { ref.set(new IR.iadd(new IR.iadd(@2(),@4()), @5())); return true; }
      | IR.LADD(lany,IR.LADD(lany,lany)) = { ref.set(new IR.ladd(new IR.ladd(@2(),@4()), @5())); return true; }

      /* x+(y-z) => (x+y)-z */
      | IR.IADD(iany,IR.ISUB(iany,iany)) = { ref.set(new IR.isub(new IR.iadd(@2(),@4()), @5())); return true; }
      | IR.LADD(lany,IR.LSUB(lany,lany)) = { ref.set(new IR.lsub(new IR.ladd(@2(),@4()), @5())); return true; }

      /* x-(y+z) => (x-y)-z */
      | IR.ISUB(iany,IR.IADD(iany,iany)) = { ref.set(new IR.isub(new IR.isub(@2(),@4()), @5())); return true; }
      | IR.LSUB(lany,IR.LADD(lany,lany)) = { ref.set(new IR.lsub(new IR.lsub(@2(),@4()), @5())); return true; }

      /* x-(y-z) => (x-y)+z */
      | IR.ISUB(iany,IR.ISUB(iany,iany)) = { ref.set(new IR.iadd(new IR.isub(@2(),@4()), @5())); return true; }
      | IR.LSUB(lany,IR.LSUB(lany,lany)) = { ref.set(new IR.ladd(new IR.lsub(@2(),@4()), @5())); return true; }

      /* x+(-y) => x-y */
      | IR.IADD(iany,IR.INEG(iany)) = { ref.set(new IR.isub(@2(), @4())); return true; }
      | IR.LADD(lany,IR.LNEG(lany)) = { ref.set(new IR.lsub(@2(), @4())); return true; }

      /* x-(-y) => x+y */
      | IR.ISUB(iany,IR.INEG(iany)) = { ref.set(new IR.iadd(@2(), @4())); return true; }
      | IR.LSUB(lany,IR.LNEG(lany)) = { ref.set(new IR.ladd(@2(), @4())); return true; }

      /* (-x)+y => y-x */
      | IR.IADD(IR.INEG(iany),iany) = { ref.set(new IR.isub(@4(), @3())); return true; }
      | IR.LADD(IR.LNEG(lany),lany) = { ref.set(new IR.lsub(@4(), @3())); return true; }

      /* (-x)-y => -(x+y) */
      | IR.ISUB(IR.INEG(iany),iany) = { ref.set(new IR.ineg(new IR.iadd(@3(), @4()))); return true; }
      | IR.LSUB(IR.LNEG(lany),lany) = { ref.set(new IR.lneg(new IR.ladd(@3(), @4()))); return true; }

      /* -(x-y) => y-x */
      | IR.INEG(IR.ISUB(iany,iany)) = { ref.set(new IR.isub(@4(), @3())); return true; }
      | IR.LNEG(IR.LSUB(lany,lany)) = { ref.set(new IR.lsub(@4(), @3())); return true; }

      /* -(-x) => x */
      | IR.INEG(IR.INEG(iany)) = { ref.set(@3()); return true; }
      | IR.LNEG(IR.LNEG(lany)) = { ref.set(@3()); return true; }

      /* (x+c1)+y => (x+y)+c1 */
      | IR.IADD(IR.IADD(iexp,icon),iexp) = { ref.set(new IR.iadd(new IR.iadd(@3(), @5()), @4())); return true; }
      | IR.LADD(IR.LADD(lexp,lcon),lexp) = { ref.set(new IR.ladd(new IR.ladd(@3(), @5()), @4())); return true; }

      /* (x+c1)-y => (x-y)+c1 */
      | IR.ISUB(IR.IADD(iexp,icon),iexp) = { ref.set(new IR.iadd(new IR.isub(@3(), @5()), @4())); return true; }
      | IR.LSUB(IR.LADD(lexp,lcon),lexp) = { ref.set(new IR.ladd(new IR.lsub(@3(), @5()), @4())); return true; }

      /* (c1-x)-y => c1-(x+y) */
      | IR.ISUB(IR.ISUB(icon,iexp),iexp) = { ref.set(new IR.isub(@3(), new IR.iadd(@4(), @5()))); return true; }
      | IR.LSUB(IR.LSUB(lcon,lexp),lexp) = { ref.set(new IR.lsub(@3(), new IR.ladd(@4(), @5()))); return true; }

      /* (x+c1)+c2 => x+c3 (c3 == c1+c2) */
      | IR.IADD(IR.IADD(iexp,icon),icon) = { ref.set(new IR.iadd(@3(), new IR.iconst(@4.val+@5.val))); return true; }
      | IR.LADD(IR.LADD(lexp,lcon),lcon) = { ref.set(new IR.ladd(@3(), new IR.lconst(@4.val+@5.val))); return true; }

      /* -(x+c1) => c2-x (c2 == -c1) */
      | IR.INEG(IR.IADD(iexp,icon)) = { ref.set(new IR.isub(new IR.iconst(-@4.val), @3())); return true; }
      | IR.LNEG(IR.LADD(lexp,lcon)) = { ref.set(new IR.lsub(new IR.lconst(-@4.val), @3())); return true; }

      /* c1+x => x+c1 */
      | IR.IADD(icon,iexp) = { ref.set(new IR.iadd(@3(), @2())); return true; }
      | IR.LADD(lcon,lexp) = { ref.set(new IR.ladd(@3(), @2())); return true; }

      /* x-c1 => x+c2 (c2 == -c1) */
      | IR.ISUB(iexp,icon) = { ref.set(new IR.iadd(@2(), new IR.iconst(-@3.val))); return true; }
      | IR.LSUB(lexp,lcon) = { ref.set(new IR.ladd(@2(), new IR.lconst(-@3.val))); return true; }

      /* c1+c2 => c3 (c3 == c1+c2)*/
      | IR.IADD(icon,icon) = { ref.set(new IR.iconst(@2.val+@3.val)); return true; }
      | IR.LADD(lcon,lcon) = { ref.set(new IR.lconst(@2.val+@3.val)); return true; }

      /* c1-c2 => c3 (c3 == c1-c2)*/
      | IR.ISUB(icon,icon) = { ref.set(new IR.iconst(@2.val-@3.val)); return true; }
      | IR.LSUB(lcon,lcon) = { ref.set(new IR.lconst(@2.val-@3.val)); return true; }

      /* -c1 => c2 (c2 == -c1) */
      | IR.INEG(icon) = { ref.set(new IR.iconst(-@2.val)); return true; }
      | IR.LNEG(lcon) = { ref.set(new IR.lconst(-@2.val)); return true; }

      /* (x+y)-x => y */
      | IR.ISUB(IR.IADD(iexp,iexp),iexp) [@3.exp.equals(@5.exp)] = { ref.set(@4()); return true; }
      | IR.LSUB(IR.LADD(lexp,lexp),lexp) [@3.exp.equals(@5.exp)] = { ref.set(@4()); return true; }

      /* (x+y)-y => x */
      | IR.ISUB(IR.IADD(iexp,iexp),iexp) [@4.exp.equals(@5.exp)] = { ref.set(@3()); return true; }
      | IR.LSUB(IR.LADD(lexp,lexp),lexp) [@4.exp.equals(@5.exp)] = { ref.set(@3()); return true; }

      /* (x-y)+y => x */
      | IR.IADD(IR.ISUB(iexp,iexp),iexp) [@4.exp.equals(@5.exp)] = { ref.set(@3()); return true; }
      | IR.LADD(IR.LSUB(lexp,lexp),lexp) [@4.exp.equals(@5.exp)] = { ref.set(@3()); return true; }

      /* (x-y)-x => -y */
      | IR.ISUB(IR.ISUB(iexp,iexp),iexp) [@3.exp.equals(@5.exp)] = { ref.set(new IR.ineg(@4())); return true; }
      | IR.LSUB(IR.LSUB(lexp,lexp),lexp) [@3.exp.equals(@5.exp)] = { ref.set(new IR.lneg(@4())); return true; }

      /* x-x => 0 */
      | IR.ISUB(iexp,iexp) [@2.exp.equals(@3.exp)] = { ref.set(new IR.iconst(0)); return true; }
      | IR.LSUB(lexp,lexp) [@2.exp.equals(@3.exp)] = { ref.set(new IR.lconst(0)); return true; }

      /* x+0 => x */
      | IR.IADD(iexp,icon) [@3.val == 0] = { ref.set(@2()); return true; }
      | IR.LADD(lexp,lcon) [@3.val == 0] = { ref.set(@2()); return true; }

      /* x-0 => x */
      | IR.ISUB(iexp,icon) [@3.val == 0] = { ref.set(@2()); return true; }
      | IR.LSUB(lexp,lcon) [@3.val == 0] = { ref.set(@2()); return true; }

      /* 0-x => -x */
      | IR.ISUB(icon,iexp) [@2.val == 0] = { ref.set(new IR.ineg(@3())); return true; }
      | IR.LSUB(lcon,lexp) [@2.val == 0] = { ref.set(new IR.lneg(@3())); return true; }



      /* c1*c2 => c3 (c3 == c1*c2)*/
      | IR.IMUL(icon,icon) = { ref.set(new IR.iconst(@2.val*@3.val)); return true; }
      | IR.LMUL(lcon,lcon) = { ref.set(new IR.lconst(@2.val*@3.val)); return true; }

      /* c1*x => x*c1 */
      | IR.IMUL(icon,iexp) = { ref.set(new IR.imul(@3(), @2())); return true; }
      | IR.LMUL(lcon,lexp) = { ref.set(new IR.lmul(@3(), @2())); return true; }

      /* x*1 => x */
      | IR.IMUL(iexp,icon) [@3.val == 1] = { ref.set(@2()); return true; }
      | IR.LMUL(lexp,lcon) [@3.val == 1] = { ref.set(@2()); return true; }

      /* x*0 => 0 */
      | IR.IMUL(iexp,icon) [@3.val == 0] = { ref.set(new IR.iconst(0)); return true; }
      | IR.LMUL(lexp,lcon) [@3.val == 0] = { ref.set(new IR.lconst(0)); return true; }



      /* x&(y&z) => (x&y)&z */
      | IR.IAND(iany,IR.IAND(iany,iany)) = { ref.set(new IR.iand(new IR.iand(@2(),@4()), @5())); return true; }
      | IR.LAND(lany,IR.LAND(lany,lany)) = { ref.set(new IR.land(new IR.land(@2(),@4()), @5())); return true; }

      /* (x&c1)&y => (x&y)&c1 */
      | IR.IAND(IR.IAND(iexp,icon),iexp) = { ref.set(new IR.iand(new IR.iand(@3(), @5()), @4())); return true; }
      | IR.LAND(IR.LAND(lexp,lcon),lexp) = { ref.set(new IR.land(new IR.land(@3(), @5()), @4())); return true; }

      /* (x&c1)&c2 => x&c3 (c3 == c1&c2) */
      | IR.IAND(IR.IAND(iexp,icon),icon) = { ref.set(new IR.iand(@3(), new IR.iconst(@4.val&@5.val))); return true; }
      | IR.LAND(IR.LAND(lexp,lcon),lcon) = { ref.set(new IR.land(@3(), new IR.lconst(@4.val&@5.val))); return true; }

      /* c1&c2 => c3 (c3 == c1&c2)*/
      | IR.IAND(icon,icon) = { ref.set(new IR.iconst(@2.val&@3.val)); return true; }
      | IR.LAND(lcon,lcon) = { ref.set(new IR.lconst(@2.val&@3.val)); return true; }

      /* c1&x => x&c1 */
      | IR.IAND(icon,iexp) = { ref.set(new IR.iand(@3(), @2())); return true; }
      | IR.LAND(lcon,lexp) = { ref.set(new IR.land(@3(), @2())); return true; }

      /* x&x => x */
      | IR.IAND(iexp,iexp) [@2.exp.equals(@3.exp)] = { ref.set(@2()); return true; }
      | IR.LAND(lexp,lexp) [@2.exp.equals(@3.exp)] = { ref.set(@2()); return true; }

      /* x&-1 => x */
      | IR.IAND(iexp,icon) [@3.val == -1] = { ref.set(@2()); return true; }
      | IR.LAND(lexp,lcon) [@3.val == -1] = { ref.set(@2()); return true; }
      
      /* x&0 => 0 */
      | IR.IAND(iexp,icon) [@3.val == 0] = { ref.set(new IR.iconst(0)); return true; }
      | IR.LAND(lexp,lcon) [@3.val == 0] = { ref.set(new IR.lconst(0)); return true; }
      


      /* x|(y|z) => (x|y)|z */
      | IR.IOR(iany,IR.IOR(iany,iany)) = { ref.set(new IR.ior(new IR.ior(@2(),@4()), @5())); return true; }
      | IR.LOR(lany,IR.LOR(lany,lany)) = { ref.set(new IR.lor(new IR.lor(@2(),@4()), @5())); return true; }

      /* (x|c1)|y => (x|y)|c1 */
      | IR.IOR(IR.IOR(iexp,icon),iexp) = { ref.set(new IR.ior(new IR.ior(@3(), @5()), @4())); return true; }
      | IR.LOR(IR.LOR(lexp,lcon),lexp) = { ref.set(new IR.lor(new IR.lor(@3(), @5()), @4())); return true; }

      /* (x|c1)|c2 => x|c3 (c3 == c1|c2) */
      | IR.IOR(IR.IOR(iexp,icon),icon) = { ref.set(new IR.ior(@3(), new IR.iconst(@4.val|@5.val))); return true; }
      | IR.LOR(IR.LOR(lexp,lcon),lcon) = { ref.set(new IR.lor(@3(), new IR.lconst(@4.val|@5.val))); return true; }

      /* c1|c2 => c3 (c3 == c1|c2)*/
      | IR.IOR(icon,icon) = { ref.set(new IR.iconst(@2.val|@3.val)); return true; }
      | IR.LOR(lcon,lcon) = { ref.set(new IR.lconst(@2.val|@3.val)); return true; }

      /* c1|x => x|c1 */
      | IR.IOR(icon,iexp) = { ref.set(new IR.ior(@3(), @2())); return true; }
      | IR.LOR(lcon,lexp) = { ref.set(new IR.lor(@3(), @2())); return true; }

      /* x|x => x */
      | IR.IOR(iexp,iexp) [@2.exp.equals(@3.exp)] = { ref.set(@2()); return true; }
      | IR.LOR(lexp,lexp) [@2.exp.equals(@3.exp)] = { ref.set(@2()); return true; }

      /* x|0 => x */
      | IR.IOR(iexp,icon) [@3.val == 0] = { ref.set(@2()); return true; }
      | IR.LOR(lexp,lcon) [@3.val == 0] = { ref.set(@2()); return true; }
      
      /* x|-1 => -1 */
      | IR.IOR(iexp,icon) [@3.val == -1] = { ref.set(new IR.iconst(-1)); return true; }
      | IR.LOR(lexp,lcon) [@3.val == -1] = { ref.set(new IR.lconst(-1)); return true; }
      


      /* x^(y^z) => (x^y)^z */
      | IR.IXOR(iany,IR.IXOR(iany,iany)) = { ref.set(new IR.ixor(new IR.ixor(@2(),@4()), @5())); return true; }
      | IR.LXOR(lany,IR.LXOR(lany,lany)) = { ref.set(new IR.lxor(new IR.lxor(@2(),@4()), @5())); return true; }

      /* (x^c1)^y => (x^y)^c1 */
      | IR.IXOR(IR.IXOR(iexp,icon),iexp) = { ref.set(new IR.ixor(new IR.ixor(@3(), @5()), @4())); return true; }
      | IR.LXOR(IR.LXOR(lexp,lcon),lexp) = { ref.set(new IR.lxor(new IR.lxor(@3(), @5()), @4())); return true; }

      /* (x^c1)^c2 => x^c3 (c3 == c1^c2) */
      | IR.IXOR(IR.IXOR(iexp,icon),icon) = { ref.set(new IR.ixor(@3(), new IR.iconst(@4.val^@5.val))); return true; }
      | IR.LXOR(IR.LXOR(lexp,lcon),lcon) = { ref.set(new IR.lxor(@3(), new IR.lconst(@4.val^@5.val))); return true; }

      /* c1^c2 => c3 (c3 == c1^c2)*/
      | IR.IXOR(icon,icon) = { ref.set(new IR.iconst(@2.val^@3.val)); return true; }
      | IR.LXOR(lcon,lcon) = { ref.set(new IR.lconst(@2.val^@3.val)); return true; }

      /* c1^x => x^c1 */
      | IR.IXOR(icon,iexp) = { ref.set(new IR.ixor(@3(), @2())); return true; }
      | IR.LXOR(lcon,lexp) = { ref.set(new IR.lxor(@3(), @2())); return true; }

      /* x^x => 0 */
      | IR.IXOR(iexp,iexp) [@2.exp.equals(@3.exp)] = { ref.set(new IR.iconst(0)); return true; }
      | IR.LXOR(lexp,lexp) [@2.exp.equals(@3.exp)] = { ref.set(new IR.lconst(0)); return true; }

      /* x^0 => x */
      | IR.IXOR(iexp,icon) [@3.val == 0] = { ref.set(@2()); return true; }
      | IR.LXOR(lexp,lcon) [@3.val == 0] = { ref.set(@2()); return true; }
      


      | default = {
        boolean changed = false;
        if (  left$ != null) changed |=   left$.cover(new   LeftRef(@1));
        if (middle$ != null) changed |= middle$.cover(new MiddleRef(@1));
        if ( right$ != null) changed |=  right$.cover(new  RightRef(@1));
        return changed;
      }
      ;

    private IR.inode iany()
      : default = { return (IR.inode)@1; }
      ;

    private IR.inode iexp()
    <IR.inode exp>
      : IR.I2B(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.I2C(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.I2S(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.L2I(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.F2I(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.D2I(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.IADD(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.ISUB(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.IMUL(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.IDIV(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.IREM(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.INEG(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.ISHL(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.ISHR(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.IUSHR(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.IAND(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.IOR(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.IXOR(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.LCMP(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.FCMPG(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.FCMPL(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.DCMPG(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.DCMPL(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.LENGTH(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.BLOAD(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.SLOAD(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.ILOAD(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.BALOAD(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.SALOAD(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.IALOAD(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.ISLOCKED(iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.SUBTYPEOF(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.COMPTYPEOF(iany,iany) { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      | IR.IUSE { @@.exp = (IR.inode)@1; } = { return @@.exp; }
      ;

    private IR.inode icon()
    <int val>
      : IR.ICONST { @@.val = @1.getValue(); } = { return (IR.inode)@1; }
      ;

    private IR.lnode lany()
      : default = { return (IR.lnode)@1; }
      ;

    private IR.lnode lexp()
    <IR.lnode exp>
      : IR.I2L(lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.F2L(lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.D2L(lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LADD(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LSUB(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LMUL(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LDIV(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LREM(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LNEG(lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LSHL(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LSHR(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LUSHR(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LAND(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LOR(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LXOR(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LLOAD(lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LALOAD(lany,lany) { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      | IR.LUSE { @@.exp = (IR.lnode)@1; } = { return @@.exp; }
      ;

    private IR.lnode lcon()
    <long val>
      : IR.LCONST { @@.val = @1.getValue(); } = { return (IR.lnode)@1; }
      ;

  }

}

