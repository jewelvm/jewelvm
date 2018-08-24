/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;

public final class IRControlEdge extends ControlEdge {

  public static final byte FALL = 0;
  public static final byte JUMP = 1;
  public static final byte EXCEPT = 2;
  public static final byte SWITCH = 3;

  private final byte type;
  private final int value;

  IRControlEdge(BasicBlock sourceBB, int type, BasicBlock targetBB) {
    super(sourceBB, targetBB);
    this.type = (byte)type;
    if (type != FALL && type != JUMP && type != EXCEPT)
      throw new IllegalArgumentException("Fall, jump or except type expected");
    this.value = 0;
  }

  IRControlEdge(BasicBlock sourceBB, int type, int value, BasicBlock targetBB) {
    super(sourceBB, targetBB);
    this.type = (byte)type;
    if (type != SWITCH)
      throw new IllegalArgumentException("Only switch types can have a value");
    this.value = value;
  }

  public int type() {
    return type;
  }

  public int value() {
    return value;
  }

  public int hashCode() {
    return super.hashCode() ^ type ^ value;
  }

  public boolean equals(Object object) {
    return object instanceof IRControlEdge
        && ((IRControlEdge)object).type == type
        && ((IRControlEdge)object).value == value
        && super.equals(object);
  }

  public String toString() {
    BasicBlock sourceBB = sourceBB();
    IRStatement trailer = (IRStatement)sourceBB.trailer();
    switch (type) {
    case FALL:
      if (trailer != null)
        switch (trailer.snode().op()) {
        case IR.IJUMP:
        case IR.AJUMP:
          return sourceNode().hashCode()+" -> "+targetNode().hashCode()+" [label=\"F\"];";
        }
      break;
    case JUMP:
      switch (trailer.snode().op()) {
      case IR.IJUMP:
      case IR.AJUMP:
        return sourceNode().hashCode()+" -> "+targetNode().hashCode()+" [label=\"T\"];";
      }
      break;
    case EXCEPT:
      return sourceNode().hashCode()+" -> "+targetNode().hashCode()+" [color=red];";
    case SWITCH:
      return sourceNode().hashCode()+" -> "+targetNode().hashCode()+" [label=\""+value+"\"];";
    }
    return sourceNode().hashCode()+" -> "+targetNode().hashCode()+";";
  }

}

