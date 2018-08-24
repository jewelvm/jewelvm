/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

public interface ContextClassInfo extends LoadedClassInfo {

  public boolean isInitialized();
  public int getContextLoaderDepth();

}

