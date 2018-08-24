/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class PersistentCache extends DelegateCache {

  protected PersistentCache(Cache cache) {
    super(cache);
  }

  protected abstract CacheObject load(CacheKey cacheKey);
  protected abstract boolean store(CacheKey cacheKey, CacheObject cacheObject);

  protected final CacheObject get(CacheKey cacheKey, BoolRef stored) throws CacheMissException {
    CacheObject cacheObject = load(cacheKey);
    if (cacheObject != null)
      stored.set(true);
    else {
      cacheObject = super.get(cacheKey, stored);
      if (!stored.get())
        stored.set(store(cacheKey, cacheObject));
    }
    return cacheObject;
  }

  protected final CacheObject get(CacheKey cacheKey, Object cacheParams, BoolRef stored) throws ConstructionTargetException {
    CacheObject cacheObject = load(cacheKey);
    if (cacheObject != null)
      stored.set(true);
    else {
      cacheObject = super.get(cacheKey, cacheParams, stored);
      if (!stored.get())
        stored.set(store(cacheKey, cacheObject));
    }
    return cacheObject;
  }

}

