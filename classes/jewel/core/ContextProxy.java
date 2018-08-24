/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

final class ContextProxy extends LoadedProxy implements ContextClassInfo {

  private boolean initialized;
  private int cdepth;

  public ContextProxy(String name, long version, boolean initialized, int cdepth) {
    super(name, version);
    this.initialized = initialized;
    this.cdepth = cdepth;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public int getContextLoaderDepth() {
    return cdepth;
  }

}

