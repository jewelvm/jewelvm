/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.optimize;

import jewel.core.jiro.control.ControlFlowGraph;

public abstract class Optimizer {

  private final byte level;
  protected transient ControlFlowGraph cfg;

  protected Optimizer() {
    this(0);
  }

  protected Optimizer(int level) {
    if (level < 0 || level >= 255)
      throw new IllegalArgumentException();
    this.level = (byte)level;
  }

  public int getLevel() {
    return level & 0xFF;
  }

  public final void optimize(ControlFlowGraph cfg) {
    this.cfg = cfg;
    engine(getLevel());
    this.cfg = null;
  }

  protected abstract void engine(int level);

  protected boolean apply(Optimization opt) {
    return opt.applyTo(cfg);
  }

}

