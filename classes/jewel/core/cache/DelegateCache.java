/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

public abstract class DelegateCache extends Cache {

  private final Cache cache;

  protected DelegateCache(Cache cache) {
    if (cache == null)
      throw new NullPointerException();
    this.cache = cache;
  }

  protected CacheObject get(CacheKey cacheKey, BoolRef stored) throws CacheMissException {
    return cache.get(cacheKey, stored);
  }

  protected CacheObject get(CacheKey cacheKey, Object cacheParams, BoolRef stored) throws ConstructionTargetException {
    return cache.get(cacheKey, cacheParams, stored);
  }

}

