/* GLASSES, Generic cLASSES
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package java.lang;

import java.security.AccessController;
import java.security.PrivilegedAction;

final class builtin {

  static {
    AccessController.doPrivileged(
      new PrivilegedAction() {
        public Object run() {
          System.loadLibrary("builtin");
          return null;
        }
      }
    );
  }

  static native long getVersion(Class clazz);
  static native void flagFinalized(Class clazz);
  static native boolean isInitialized(Class clazz);

  static native void allocMetadata(Class clazz, int fields, int methods, int inners);
  static native void setMetaFlags(Class clazz, int flags);
  static native void setMetaField(Class clazz, int index, int flags, String name,String descriptor, long offset);
  static native void setMetaMethod(Class clazz, int index, int flags, String name, String descriptor, String[] exceptions, int dindex);
  static native void setMetaDeclaring(Class clazz, String name);
  static native void setMetaInner(Class clazz, int index, int flags, String name);
  static native void setMetaSource(Class clazz, String source);
  
  static native MethodText getMethodText(Class clazz, int index);
  static native void setMethodText(Class clazz, int index, MethodText text);
  static native MethodText newMethodText(byte[] array, int start, int length);
  static native Class getDeclaringClass(MethodText methodText);
  static native int getIndex(MethodText methodText);

  static native void patchSymbol(MethodText methodText, int offset, String name);
  static native void posReloc(MethodText methodText, int offset);
  static native void negReloc(MethodText methodText, int offset);
  static native void posPatch(MethodText methodText, int offset, Object value);
  static native void negPatch(MethodText methodText, int offset, Object value);

  static native Class newClass(ClassLoader loader, String name, long loadedVersion,
                               char accessFlags, Class superClass, Class[] interfaces,
                               long staticSize, long staticRefOfs, char staticRefCount,
                               long instanceSize, long instanceRefOfs, char instanceRefCount,
                               int staticEntries, int dynamicEntries, int[] baseIndexes, char natives,
                               Class elementClass, int dimensions);

  private builtin() { }

}

