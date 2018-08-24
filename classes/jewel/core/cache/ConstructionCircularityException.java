/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

public class ConstructionCircularityException extends RuntimeException {

  public ConstructionCircularityException() { }
  
  public ConstructionCircularityException(String message) {
    super(message);
  }

}

