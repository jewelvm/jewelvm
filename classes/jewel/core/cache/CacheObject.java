/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

public interface CacheObject extends Streamable {

  public void construct(Object cacheParams) throws Exception;

}

