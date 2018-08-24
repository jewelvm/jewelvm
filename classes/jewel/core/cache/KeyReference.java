/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

public final class KeyReference extends SoftReference {

  private final CacheKey cacheKey;

  public KeyReference(CacheKey cacheKey, CacheObject cacheObject) {
    this(cacheKey, cacheObject, null);
  }

  public KeyReference(CacheKey cacheKey, CacheObject cacheObject, ReferenceQueue queue) {
    super(cacheObject, queue);
    if (cacheKey == null)
      throw new NullPointerException();
    this.cacheKey = cacheKey;
  }

  public CacheKey cacheKey() {
    return cacheKey;
  }

}

