/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.clfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ClassPath {

  private final File[] classPath;

  public ClassPath(String value) {
    if (value == null)
      throw new NullPointerException();
    StringTokenizer st = new StringTokenizer(value, File.pathSeparator);
    classPath = new File[st.countTokens()];
    for (int i = 0; i < classPath.length; i++)
      classPath[i] = new File(st.nextToken());
  }

  public byte[] findDefinition(String name) throws NoClassDefFoundException {
    for (int i = 0; i < classPath.length; i++)
      try {
        if (classPath[i].isDirectory()) {
          File file = new File(classPath[i], name.replace('/', File.separatorChar)+".class");
          FileInputStream in = new FileInputStream(file);
          try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int count;
            byte[] buffer = new byte[4096];
            while ((count = in.read(buffer, 0, buffer.length)) != -1)
              out.write(buffer, 0, count);
            return out.toByteArray();
          } finally {
            in.close();
          }
        } else {
          ZipFile zipfile = new ZipFile(classPath[i]);
          try {
            ZipEntry zipentry = zipfile.getEntry(name+".class");
            if (zipentry != null) {
              InputStream in = zipfile.getInputStream(zipentry);
              try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int count;
                byte[] buffer = new byte[4096];
                while ((count = in.read(buffer, 0, buffer.length)) != -1)
                  out.write(buffer, 0, count);
                return out.toByteArray();
              } finally {
                in.close();
              }
            }
          } finally {
            zipfile.close();
          }
        }
      } catch (IOException e) { }
    throw new NoClassDefFoundException(name);
  }

  public String[] getAllNames() {
    HashSet set = new HashSet();
    for (int i = 0; i < classPath.length; i++)
      try {
        if (classPath[i].isDirectory()) {
  /*        File file = new File(classPath[i], name.replace('/', File.separatorChar)+".class");
          FileInputStream in = new FileInputStream(file);
          try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int count;
            byte[] buffer = new byte[4096];
            while ((count = in.read(buffer, 0, buffer.length)) != -1)
              out.write(buffer, 0, count);
            return out.toByteArray();
          } finally {
            in.close();
          }*/
        } else {
          ZipFile zipfile = new ZipFile(classPath[i]);
          for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
            ZipEntry zipentry = (ZipEntry)e.nextElement();
            String name = zipentry.getName();
            if (name.endsWith(".class"))
              set.add(name.substring(0, name.length()-6));
          }
        }
      } catch (IOException e) { }
    return (String[])set.toArray(new String[set.size()]);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < classPath.length; i++) {
      sb.append(classPath[i]);
      if (i+1 < classPath.length)
        sb.append(File.pathSeparatorChar);
    }
    return sb.toString();
  }

}

