/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

import java.lang.ref.SoftReference;

public abstract class ProxyObject {

  private final Cache cache;
  KeyReference ref;

  protected ProxyObject(CacheKey cacheKey) {
    this(Cache.getContextCache(), cacheKey);
  }

  protected ProxyObject(Cache cache, CacheKey cacheKey) {
    if (cache == null)
      throw new NullPointerException();
    this.cache = cache;
    this.ref = new KeyReference(cacheKey, null);
  }

  public final Cache cache() {
    return cache;
  }

  public final CacheKey cacheKey() {
    return ref.cacheKey();
  }

  protected final CacheObject get() throws CacheMissException {
    CacheObject cacheObject = (CacheObject)ref.get();
    if (cacheObject == null) {
      CacheKey cacheKey = ref.cacheKey();
      if (cache instanceof SoftCache)
        cacheObject = ((SoftCache)cache).get(cacheKey, this);
      else {
        cacheObject = cache.get(cacheKey);
        ref = new KeyReference(cacheKey, cacheObject);
      }
    }
    return cacheObject;
  }

  protected final CacheObject get(Object cacheParams) throws ConstructionTargetException {
    CacheObject cacheObject = (CacheObject)ref.get();
    if (cacheObject == null) {
      CacheKey cacheKey = ref.cacheKey();
      if (cache instanceof SoftCache)
        cacheObject = ((SoftCache)cache).get(cacheKey, cacheParams, this);
      else {
        cacheObject = cache.get(cacheKey, cacheParams);
        ref = new KeyReference(cacheKey, cacheObject);
      }
    }
    return cacheObject;
  }

}

