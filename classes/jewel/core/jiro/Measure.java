/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

public final class Measure {

  public static boolean isValid(long measure) {
    return (measure & 0x8000000080000000L) == 0L;
  }

  public static long encode(int references, int bytes) {
    return (long)references << 32 | (long)bytes;
  }

  public static int getReferences(long measure) {
    return (int)(measure >> 32);
  }

  public static int getBytes(long measure) {
    return (int)measure;
  }

  public static long incReferences(long measure, int references) {
    return encode(getReferences(measure)+references, getBytes(measure));
  }

  public static long incBytes(long measure, int bytes) {
    return encode(getReferences(measure), getBytes(measure)+bytes);
  }

  public static int getScaled(long measure, int scale) {
    return getReferences(measure)*scale+getBytes(measure);
  }

  public static String toString(long measure) {
    return "("+getReferences(measure)+","+getBytes(measure)+")";
  }

  private Measure() { }

}

