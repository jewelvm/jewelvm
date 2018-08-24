/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.optimize.Optimization;
import jewel.core.jiro.optimize.Optimizer;

public final class IROptimizer extends Optimizer {

  public IROptimizer() { }

  public IROptimizer(int level) {
    super(level);
  }

  protected void engine(int level) {

    switch (level) {
    case 0: level0(); break;
    default: level1();
    }

//      ((IRCFG)cfg).printTo(System.err);
//      try { System.in.read(); } catch (java.io.IOException e) { }

  }

  private void level0() {
  
    for (;;) {

      apply(new UnreachableCodeElimination());
      apply(new Straightening());

      if (apply(new IfRemoval()))
        if (apply(new UnreachableCodeElimination()))
          apply(new Straightening());

      apply(new CriticalEdgeSplitting());
      apply(new SSAConverter());

      if (apply(new InitializationRemoval()))
        if (apply(new UnreachableCodeElimination()))
          apply(new Straightening());

      apply(new ExpressionBreaking());

      apply(new CopyPropagation());
      apply(new RegisterPacking());
      if (apply(new DeadCodeElimination.SSA()))
        if (apply(new UnreachableCodeElimination()))
          apply(new Straightening());

      apply(new TypePropagation());

      boolean changed;
      do {
        changed = false;
        changed |= apply(new ArrayLengthPropagation());
        changed |= apply(new ConstantPropagation());
        changed |= apply(new ConstantFolding());
      } while (changed);

      if (apply(new PathFolding()))
        if (apply(new UnreachableCodeElimination()))
          apply(new Straightening());

      if (apply(new LoadPrefetching()))
        apply(new CopyPropagation());

      apply(new GlobalValueNumbering());

      apply(new ConditionTightening());

      if (apply(new DeadArmRemoval()))
        if (apply(new UnreachableCodeElimination()))
          apply(new Straightening());

      if (apply(new DeadCodeElimination.SSA()))
        if (apply(new UnreachableCodeElimination()))
          apply(new Straightening());

      apply(new CodeMotion());

      apply(new ExpressionBuilding());

      apply(new Canonicalization());

      apply(new UselessStoreRemoval());

      apply(new SSAReverter());
      //apply(new RegisterAllocating());
      apply(new RegisterPacking());

      // old policy
      while (apply(new BranchOptimizations())) {                  /* <-- */
        apply(new UnreachableCodeElimination());
        apply(new Straightening());
      }
      if (apply(new DeadCodeElimination()))                       /* <-- */
        if (apply(new UnreachableCodeElimination()))
          apply(new Straightening());
      while (apply(new CrossJumping())) {                         /* <-- */
        apply(new UnreachableCodeElimination());
        apply(new Straightening());
      }

      if (apply(new PathForwarding()))
        continue;
      if (apply(new ProcedureIntegration()))
        continue;
      if (apply(new ObjectExplosion()))
        continue;
      if (apply(new Unswitching()))
        continue;
      if (apply(new LoopPeeling()))
        continue;

      break;

    }

  }

  private void level1() {

    if (!apply(new ProcedureIntegration(1)))
      return;

    level0();

  }

}

