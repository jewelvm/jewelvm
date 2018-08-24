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

public final class FileCache extends PersistentCache {

  private final File cacheDir;

  public FileCache(Cache cache, File cacheDir) {
    super(cache);
    if (cacheDir == null)
      throw new NullPointerException();
    this.cacheDir = cacheDir;
  }

  private File cacheFile(CacheKey cacheKey) {
    String entry = cacheKey.getEntry();
    String cacheFile = entry.replace('/', File.separatorChar);
    return new File(cacheDir, cacheFile);
  }

  protected CacheObject load(CacheKey cacheKey) {
    File cacheFile = cacheFile(cacheKey);
    if (cacheFile.exists()) {
      CacheObject cacheObject = cacheKey.newInstance();
      try {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(cacheFile));
        try {
          cacheObject.readFrom(in);
        } finally {
          in.close();
        }
        return cacheObject;
      } catch (IOException e) {
        cacheFile.delete();
      }
    }
    return null;
  }

  protected boolean store(CacheKey cacheKey, CacheObject cacheObject) {
    File cacheFile = cacheFile(cacheKey);
    if (cacheFile.exists())
      return true;
    if (cacheDir.exists())
      try {
        File tmpFile = File.createTempFile("cache", null);
        try {
          BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmpFile));
          try {
            cacheObject.writeTo(out);
          } finally {
            out.close();
          }
          File parentFile = cacheFile.getParentFile();
          parentFile.mkdirs();
          if (tmpFile.renameTo(cacheFile)) {
            cacheFile.setReadOnly();
            return true;
          }
        } finally {
          tmpFile.delete();
        }
      } catch (IOException e) { }
    return false;
  }

}

