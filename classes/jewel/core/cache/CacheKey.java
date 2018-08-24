/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

public interface CacheKey {

  public CacheObject newInstance();
  public String getEntry();

}

