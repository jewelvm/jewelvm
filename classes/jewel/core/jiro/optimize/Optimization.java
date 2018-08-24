/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.optimize;

import jewel.core.jiro.control.ControlFlowGraph;

public interface Optimization {

  public boolean applyTo(ControlFlowGraph cfg);

}

