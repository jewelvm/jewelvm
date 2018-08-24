/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.clfile;

public class ClassCircularityException extends LinkageException {

  public ClassCircularityException() { }

  public ClassCircularityException(String message) {
    super(message);
  }

}

