/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.clfile.AbstractMethodException;
import jewel.core.clfile.ClassInfo.FieldInfo;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.IncompatibleClassChangeException;
import jewel.core.clfile.VerifyException;
import jewel.core.jiro.IRCFG;

public interface LoadedClassInfo extends RegisteredClassInfo {

  public long getLoadedVersion();

  public LoadedClassInfo getSuperclassClass();
  public LoadedClassInfo[] getInterfacesClass();

  public int getInterfaceBaseIndex(int index);
  public int[] getInterfaceBaseIndexes();

  public int lookupInterfaceBaseIndex(LoadedClassInfo clazz);

  public int getDepth();
  public int getSuperLoaderDepth();

  public long getStaticSize();
  public long getStaticRefsOffset();
  public int getStaticRefsCount();
  
  public long getInstanceSize();
  public long getInstanceRefsOffset();
  public int getInstanceRefsCount();

  public int getOverridableDispatchEntryCount();
  public int getNonOverridableDispatchEntryCount();

  public PlacedFieldInfo lookupField(String name, String descriptor);
  public PlacedMethodInfo lookupMethod(String name, String descriptor);
  public PlacedMethodInfo lookupInterfaceMethod(String name, String descriptor);

  public PlacedMethodInfo lookupDispatchMethod(int index);

  public boolean finalizes();

  public interface PlacedFieldInfo extends FieldInfo {
  
    public long getOffset();

  }
  
  public interface PlacedMethodInfo extends MethodInfo {

    public boolean isVirtual();
    public int getDispatchIndex();
    public int[] getPatches();
    public String[] getContext();
    public IRCFG getCFG(Context context) throws VerifyException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, IncompatibleClassChangeException, AbstractMethodException;
    public IRCFG getCFG(Context context, int level) throws VerifyException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, IncompatibleClassChangeException, AbstractMethodException;
  
  }

}

