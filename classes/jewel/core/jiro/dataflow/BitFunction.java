/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.dataflow;

import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowFunction;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowItem;

import java.util.BitSet;

public class BitFunction implements FlowFunction, Cloneable {

  public static final byte KILL = 0;
  public static final byte GEN = 1;
  public static final byte FLOW = 2;

  private BitSet gen;
  private BitSet kill;

  public BitFunction() {
    gen = new BitSet();
    kill = new BitSet();
  }

  public BitFunction(int nbits) {
    gen = new BitSet(nbits);
    kill = new BitSet(nbits);
  }

  public byte get(int index) {
    return gen.get(index) ? GEN : kill.get(index) ? KILL : FLOW;
  }

  public void set(int index, byte value) {
    switch (value) {
    case GEN:
      gen.set(index);
      kill.clear(index);
      break;
    case KILL:
      gen.clear(index);
      kill.set(index);
      break;
    case FLOW:
      gen.clear(index);
      kill.clear(index);
      break;
    default:
      throw new IllegalArgumentException("Unknown value");
    }
  }

  public void set(BitSet index, byte value) {
    switch (value) {
    case GEN:
      gen.or(index);
      kill.andNot(index);
      break;
    case KILL:
      gen.andNot(index);
      kill.or(index);
      break;
    case FLOW:
      gen.andNot(index);
      kill.andNot(index);
      break;
    default:
      throw new IllegalArgumentException("Unknown value");
    }
  }

  public final FlowItem apply(FlowItem input) {
    return apply((BitItem)input);
  }

  public BitItem apply(BitItem input) {
    BitItem output = (BitItem)input.clone();
    output.andNot(kill);
    output.or(gen);
    return output;
  }

  public Object clone() {
    BitFunction clone;
    try {
      clone = (BitFunction)super.clone();
    } catch (CloneNotSupportedException e) {
      throw new Error();
    }
    clone.gen = (BitSet)gen.clone();
    clone.kill = (BitSet)kill.clone();
    return clone;
  }

  public int hashCode() {
    return gen.hashCode() ^ kill.hashCode();
  }

  public boolean equals(Object object) {
    return object instanceof BitFunction
        && ((BitFunction)object).gen.equals(gen)
        && ((BitFunction)object).kill.equals(kill);
  }

  public String toString() {
    return "GEN = "+gen+", KILL = "+kill;
  }

}

