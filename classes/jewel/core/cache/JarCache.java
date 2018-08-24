/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

public final class JarCache extends PersistentCache {

  private final File jarFile;

  public JarCache(Cache cache, File jarFile) {
    super(cache);
    if (jarFile == null)
      throw new NullPointerException();
    this.jarFile = jarFile;
  }

  protected CacheObject load(CacheKey cacheKey) {
    if (jarFile.exists())
      try {
        JarFile jar = new JarFile(jarFile);
        try {
          JarEntry entry = jar.getJarEntry(cacheKey.getEntry());
          if (entry != null) {
            CacheObject cacheObject = cacheKey.newInstance();
            BufferedInputStream in = new BufferedInputStream(jar.getInputStream(entry));
            try {
              cacheObject.readFrom(in);
            } finally {
              in.close();
            }
            return cacheObject;
          }
        } finally {
          jar.close();
        }
      } catch (IOException e) { }
    return null;
  }

  protected boolean store(CacheKey cacheKey, CacheObject cacheObject) {
    return false;
  }

}

