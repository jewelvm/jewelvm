/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;

public final class SoftCache extends DelegateCache {

  private final HashMap map;
  private final ReferenceQueue queue = new ReferenceQueue();

  public SoftCache(Cache cache) {
    super(cache);
    map = new HashMap();
  }

  public SoftCache(Cache cache, int size) {
    super(cache);
    map = new HashMap(size);
  }

  private Entry entryFor(CacheKey cacheKey) {
    if (cacheKey == null)
      throw new NullPointerException();
    Entry entry;
    synchronized (map) {
      entry = (Entry)map.get(cacheKey);
      if (entry == null) {
        entry = new Entry(cacheKey);
        KeyReference ref;
        while ((ref = (KeyReference)queue.poll()) != null)
          map.remove(ref.cacheKey());
        map.put(cacheKey, entry);
      }
    }
    return entry;
  }

  protected CacheObject get(CacheKey cacheKey, BoolRef stored) throws CacheMissException {
    Entry entry = entryFor(cacheKey);
    return entry.get(stored);
  }

  protected CacheObject get(CacheKey cacheKey, Object cacheParams, BoolRef stored) throws ConstructionTargetException {
    Entry entry = entryFor(cacheKey);
    return entry.get(cacheParams, stored);
  }

  CacheObject get(CacheKey cacheKey, ProxyObject proxyObject) throws CacheMissException {
    Entry entry = entryFor(cacheKey);
    CacheObject cacheObject;
    Cache previous = getContextCache();
    setContextCache(this);
    try {
      cacheObject = entry.get(new BoolRef());
    } finally {
      setContextCache(previous);
    }
    proxyObject.ref = entry.ref;
    return cacheObject;
  }

  CacheObject get(CacheKey cacheKey, Object cacheParams, ProxyObject proxyObject) throws ConstructionTargetException {
    Entry entry = entryFor(cacheKey);
    CacheObject cacheObject;
    Cache previous = getContextCache();
    setContextCache(this);
    try {
      cacheObject = entry.get(cacheParams, new BoolRef());
    } finally {
      setContextCache(previous);
    }
    proxyObject.ref = entry.ref;
    return cacheObject;
  }

  //bug in jikes SoftCache.super.get(cacheKey, stored);
  private CacheObject super_get(CacheKey cacheKey, BoolRef stored) throws CacheMissException {
    return super.get(cacheKey, stored);
  }

  //bug in jikes SoftCache.super.get(cacheKey, stored);
  private CacheObject super_get(CacheKey cacheKey, Object cacheParams, BoolRef stored) throws ConstructionTargetException {
    return super.get(cacheKey, cacheParams, stored);
  }

  private final class Entry {

    private KeyReference ref;

    public Entry(CacheKey cacheKey) {
      ref = new KeyReference(cacheKey, null, queue);
    }

    public synchronized CacheObject get(BoolRef stored) throws CacheMissException {
      if (ref == null)
        throw new ConstructionCircularityException();
      CacheObject cacheObject = (CacheObject)ref.get();
      if (cacheObject == null) {
        CacheKey cacheKey = ref.cacheKey();
        cacheObject = super_get(cacheKey, stored);
        ref = new KeyReference(cacheKey, cacheObject, queue);
      }
      return cacheObject;
    }

    public synchronized CacheObject get(Object cacheParams, BoolRef stored) throws ConstructionTargetException {
      if (ref == null)
        throw new ConstructionCircularityException();
      CacheObject cacheObject = (CacheObject)ref.get();
      if (cacheObject == null) {
        CacheKey cacheKey = ref.cacheKey();
        KeyReference backref = ref;
        ref = null;
        try {
          cacheObject = super_get(cacheKey, cacheParams, stored);
        } finally {
          ref = backref;
        }
        ref = new KeyReference(cacheKey, cacheObject, queue);
      }
      return cacheObject;
    }

  }

}

