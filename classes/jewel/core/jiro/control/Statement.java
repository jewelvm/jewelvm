/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.control;

import java.io.PrintStream;

public abstract class Statement implements Cloneable {

  BasicBlock ownerBB;
  Statement previous;
  Statement next;
  int index;

  protected Statement() { }

  public final BasicBlock ownerBB() {
    return ownerBB;
  }

  public final Statement previous() {
    return previous;
  }

  public final Statement next() {
    return next;
  }

  public final Statement previousStmt() {
    if (previous != null)
      return previous;
    if (ownerBB == null)
      return null;
    for (BasicBlock currentBB = ownerBB.upBB(); currentBB != null; currentBB = currentBB.upBB())
      if (currentBB.count() > 0)
        return currentBB.trailer();
    return null;
  }

  public final Statement nextStmt() {
    if (next != null)
      return next;
    if (ownerBB == null)
      return null;
    for (BasicBlock currentBB = ownerBB.downBB(); currentBB != null; currentBB = currentBB.downBB())
      if (currentBB.count() > 0)
        return currentBB.leader();
    return null;
  }

  public final boolean dominates(Statement dominateeStmt) {
    if (ownerBB == null || dominateeStmt.ownerBB == null)
      return false;
    if (ownerBB == dominateeStmt.ownerBB) {
      for (Statement stmt = dominateeStmt; stmt != null; stmt = stmt.previous)
        if (stmt == this)
          return true;
      return false;
    }
    return ownerBB.dominates(dominateeStmt.ownerBB);
  }

  public Object clone() {
    Statement stmt;
    try {
      stmt = (Statement)super.clone();
    } catch (CloneNotSupportedException e) {
      throw new Error();
    }
    stmt.ownerBB = null;
    stmt.previous = null;
    stmt.next = null;
    stmt.index = 0;
    return stmt;
  }

  public void printTo(PrintStream out) {
    out.println(this);
  }

}

