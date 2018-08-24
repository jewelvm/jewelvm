/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ConstructionTargetException extends RuntimeException {

  private final Exception target;

  public ConstructionTargetException() {
    target = null;
  }

  public ConstructionTargetException(Exception target) {
    this.target = target;
  }

  public ConstructionTargetException(Exception target, String message) {
    super(message);
    this.target = target;
  }

  public Exception getTargetException() {
    return target;
  }

  public void printStackTrace() {
    printStackTrace(System.err);
  }

  public void printStackTrace(PrintStream out) {
    if (target == null)
      super.printStackTrace(out);
    else {
      out.print(getClass().getName());
      out.print(": ");
      target.printStackTrace(out);
    }
  }

  public void printStackTrace(PrintWriter out) {
    if (target == null)
      super.printStackTrace(out);
    else {
      out.print(getClass().getName());
      out.print(": ");
      target.printStackTrace(out);
    }
  }

}

