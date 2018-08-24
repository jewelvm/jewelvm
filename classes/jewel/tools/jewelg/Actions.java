/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jewelg;

import jewel.core.clfile.LinkageException;

public interface Actions {

  public void init(String name) throws LinkageException, IllegalAccessException;
  public void call(String name, int dindex) throws LinkageException, IllegalAccessException;
  public void vcall(String name, int dindex) throws LinkageException, IllegalAccessException;
  public void icall(String name, String iname, int dindex) throws LinkageException, IllegalAccessException;
  public void meta(String name) throws LinkageException, IllegalAccessException;

}

