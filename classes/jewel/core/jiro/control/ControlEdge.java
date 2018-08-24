/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.control;

import jewel.core.jiro.graph.DirectedGraph.DirectedEdge;

public class ControlEdge extends DirectedEdge implements Cloneable {

  protected ControlEdge(BasicBlock sourceBB, BasicBlock targetBB) {
    super(sourceBB, targetBB);
  }

  public final BasicBlock sourceBB() {
    return (BasicBlock)sourceNode();
  }

  public final BasicBlock targetBB() {
    return (BasicBlock)targetNode();
  }

  public ControlEdge clone(BasicBlock sourceBB, BasicBlock targetBB) {
    try {
      return (ControlEdge)super.clone(sourceBB, targetBB);
    } catch (CloneNotSupportedException e) {
      throw new Error();
    }
  }

  public boolean equals(Object object) {
    return object instanceof ControlEdge
        && super.equals(object);
  }

}

