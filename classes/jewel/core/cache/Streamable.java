/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.cache;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public interface Streamable {

  public void readFrom(InputStream in) throws IOException;
  public void writeTo(OutputStream out) throws IOException;

}

