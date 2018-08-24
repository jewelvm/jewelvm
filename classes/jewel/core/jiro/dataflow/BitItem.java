/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.dataflow;

import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowItem;

import java.util.BitSet;

public class BitItem extends BitSet implements FlowItem {

  public BitItem() { }

  public BitItem(int nbits) {
    super(nbits);
  }

}

