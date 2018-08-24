/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

public abstract class Cache {

  private static Cache context;

  public static void setContextCache(Cache context) {
    Cache.context = context;
  }

  public static Cache getContextCache() {
    return context;
  }

  protected Cache() { }

  public final CacheObject get(CacheKey cacheKey) throws CacheMissException {
    Cache previous = getContextCache();
    setContextCache(this);
    try {
      return get(cacheKey, new BoolRef());
    } finally {
      setContextCache(previous);
    }
  }

  public final CacheObject get(CacheKey cacheKey, Object cacheParams) throws ConstructionTargetException {
    Cache previous = getContextCache();
    setContextCache(this);
    try {
      return get(cacheKey, cacheParams, new BoolRef());
    } finally {
      setContextCache(previous);
    }
  }

  protected abstract CacheObject get(CacheKey cacheKey, BoolRef stored) throws CacheMissException;
  protected abstract CacheObject get(CacheKey cacheKey, Object cacheParams, BoolRef stored) throws ConstructionTargetException;

  protected static final class BoolRef {

    private boolean value;

    public BoolRef() { }

    public void set(boolean value) {
      this.value = value;
    }

    public boolean get() {
      return value;
    }

  }

}

