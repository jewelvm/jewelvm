/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

public class CacheMissException extends RuntimeException {

  public CacheMissException() { }
  
  public CacheMissException(String message) {
    super(message);
  }

}

