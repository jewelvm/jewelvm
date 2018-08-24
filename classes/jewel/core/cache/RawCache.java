/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

public final class RawCache extends Cache {

  public RawCache() { }

  protected CacheObject get(CacheKey cacheKey, BoolRef stored) throws CacheMissException {
    throw new CacheMissException(cacheKey.getEntry());
  }

  protected CacheObject get(CacheKey cacheKey, Object cacheParams, BoolRef stored) throws ConstructionTargetException {
    CacheObject cacheObject = cacheKey.newInstance();
    try {
      cacheObject.construct(cacheParams);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new ConstructionTargetException(e);
    }
    return cacheObject;
  }

}

