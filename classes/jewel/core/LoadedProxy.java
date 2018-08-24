/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import jewel.core.LoadedClass.Key;
import jewel.core.cache.CacheKey;

class LoadedProxy extends RegisteredProxy implements LoadedClassInfo {

  protected LoadedProxy(CacheKey cacheKey) {
    super(cacheKey);
  }

  public LoadedProxy(String name, long version) {
    this(new Key(name, version));
  }

  protected final LoadedClassInfo loadedInfo() {
    return (LoadedClassInfo)get();
  }

  public final long getRegisteredVersion() { return loadedInfo().getRegisteredVersion(); }
  
  public final long getLoadedVersion() {
    ClassID key = (ClassID)cacheKey();
    return key.getVersion();
  }

  public final LoadedClassInfo getSuperclassClass() { return loadedInfo().getSuperclassClass(); }
  public final LoadedClassInfo[] getInterfacesClass() { return loadedInfo().getInterfacesClass(); }
  public int getInterfaceBaseIndex(int index) { return loadedInfo().getInterfaceBaseIndex(index); }
  public int[] getInterfaceBaseIndexes() { return loadedInfo().getInterfaceBaseIndexes(); }
  public int lookupInterfaceBaseIndex(LoadedClassInfo clazz) { return loadedInfo().lookupInterfaceBaseIndex(clazz); }
  public final int getDepth() { return loadedInfo().getDepth(); }
  public final int getSuperLoaderDepth() { return loadedInfo().getSuperLoaderDepth(); }
  public final long getStaticSize() { return loadedInfo().getStaticSize(); }
  public final long getStaticRefsOffset() { return loadedInfo().getStaticRefsOffset(); }
  public final int getStaticRefsCount() { return loadedInfo().getStaticRefsCount(); }
  public final long getInstanceSize() { return loadedInfo().getInstanceSize(); }
  public final long getInstanceRefsOffset() { return loadedInfo().getInstanceRefsOffset(); }
  public final int getInstanceRefsCount() { return loadedInfo().getInstanceRefsCount(); }
  public final int getOverridableDispatchEntryCount() { return loadedInfo().getOverridableDispatchEntryCount(); }
  public final int getNonOverridableDispatchEntryCount() { return loadedInfo().getNonOverridableDispatchEntryCount(); }
  public final PlacedFieldInfo lookupField(String name, String descriptor) { return loadedInfo().lookupField(name, descriptor); }
  public final PlacedMethodInfo lookupMethod(String name, String descriptor) { return loadedInfo().lookupMethod(name, descriptor); }
  public final PlacedMethodInfo lookupInterfaceMethod(String name, String descriptor) { return loadedInfo().lookupInterfaceMethod(name, descriptor); }
  public final PlacedMethodInfo lookupDispatchMethod(int index) { return loadedInfo().lookupDispatchMethod(index); }
  public final boolean finalizes() { return loadedInfo().finalizes(); }

}

