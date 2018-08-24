/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public final class CodeOutput implements Cloneable {

  private static final int MAGIC = 0xBEBACAFE;
  private static final int VERSION = 0x00010000;
  private static final int MODEMASK = 0x0000000F;

  private static final class patch {
    public final int offset;
    public final Symbol symbol;
    public patch(int offset, Symbol symbol) {
      this.offset = offset;
      this.symbol = symbol;
    }
  }
  
  private int size;
  private byte[] buffer;
  private int rcount;
  private int[] relocs;
  private int mode;
  private int pcount;
  private patch[] patches;

  private transient HashMap map1;
  private transient HashMap map2;
  private transient HashMap map4;
  private transient HashMap map8;

  public CodeOutput() { }

  public int size() {
    return size;
  }

  public int get(int index) {
    if (index >= size)
      throw new ArrayIndexOutOfBoundsException(index);
    return buffer[index] & 0xFF;
  }

  public void set(int index, int bite) {
    if (index >= size)
      throw new ArrayIndexOutOfBoundsException(index);
    if (bite < -128 || bite > 255)
      throw new IllegalArgumentException("Out of range");
    buffer[index] = (byte)bite;
  }

  public void append(int bite) {
    if (bite < -128 || bite > 255)
      throw new IllegalArgumentException("Out of range");
    if (buffer == null)
      buffer = new byte[1024];
    if (size == buffer.length) {
      byte[] tmp = buffer;
      buffer = new byte[2*tmp.length+1];
      System.arraycopy(tmp, 0, buffer, 0, tmp.length);
    }
    buffer[size++] = (byte)bite;
  }

  public void append(byte[] array, int start, int length) {
    if (start < 0 || start > array.length)
      throw new ArrayIndexOutOfBoundsException(start);
    int end = start+length;
    if (end < start || end > array.length)
      throw new ArrayIndexOutOfBoundsException(end);
    if (buffer == null)
      buffer = new byte[length];
    if (size+length > buffer.length) {
      byte[] tmp = buffer;
      buffer = new byte[size+length];
      System.arraycopy(tmp, 0, buffer, 0, size);
    }
    System.arraycopy(array, start, buffer, size, length);
    size += length;
  }

  public void posReloc() {
    if (relocs == null)
      relocs = new int[64];
    if (rcount == relocs.length) {
      int[] tmp = relocs;
      relocs = new int[2*tmp.length+1];
      System.arraycopy(tmp, 0, relocs, 0, tmp.length);
    }
    relocs[rcount++] = size;
  }

  public void negReloc() {
    if (relocs == null)
      relocs = new int[64];
    if (rcount == relocs.length) {
      int[] tmp = relocs;
      relocs = new int[2*tmp.length+1];
      System.arraycopy(tmp, 0, relocs, 0, tmp.length);
    }
    relocs[rcount++] = ~size;
  }

  public void posPatch(Symbol symbol) {
    if (symbol instanceof Extern) mode |= 1;
    else if (symbol instanceof SymbolicType) mode |= 2;
    else if (symbol instanceof MethodEntry) mode |= 4;
    else if (symbol instanceof StringRef) mode |= 8;
    else throw new IllegalArgumentException("Unknown symbol type");
    if (patches == null)
      patches = new patch[64];
    if (pcount == patches.length) {
      patch[] tmp = patches;
      patches = new patch[2*tmp.length+1];
      System.arraycopy(tmp, 0, patches, 0, tmp.length);
    }
    patches[pcount++] = new patch(size, symbol);
  }

  public void negPatch(Symbol symbol) {
    if (symbol instanceof Extern) mode |= 1;
    else if (symbol instanceof SymbolicType) mode |= 2;
    else if (symbol instanceof MethodEntry) mode |= 4;
    else if (symbol instanceof StringRef) mode |= 8;
    else throw new IllegalArgumentException("Unknown symbol type");
    if (patches == null)
      patches = new patch[64];
    if (pcount == patches.length) {
      patch[] tmp = patches;
      patches = new patch[2*tmp.length+1];
      System.arraycopy(tmp, 0, patches, 0, tmp.length);
    }
    patches[pcount++] = new patch(~size, symbol);
  }

  public Object clone() {
    try {
      CodeOutput codeOutput = (CodeOutput)super.clone();
      if (buffer != null)
        codeOutput.buffer = (byte[])buffer.clone();
      if (relocs != null)
        codeOutput.relocs = (int[])relocs.clone();
      if (patches != null)
        codeOutput.patches = (patch[])patches.clone();
      return codeOutput;
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e.getMessage());
    }
  }

  private void constructMaps() {
    map1 = new HashMap();
    map2 = new HashMap();
    map4 = new HashMap();
    map8 = new HashMap();
    for (int i = 0; i < pcount; i++) {
      patch patch = patches[i];
      HashMap map;
      if (patch.symbol instanceof Extern) map = map1;
      else if (patch.symbol instanceof SymbolicType) map = map2;
      else if (patch.symbol instanceof MethodEntry) map = map4;
      else if (patch.symbol instanceof StringRef) map = map8;
      else throw new IllegalStateException("Unknown symbol type");
      int[] offsets = (int[])map.get(patch.symbol);
      if (offsets == null) {
        offsets = new int[4];
        map.put(patch.symbol, offsets);
      }
      if (1+offsets[0] == offsets.length) {
        int[] tmp = offsets;
        offsets = new int[2*tmp.length+1];
        System.arraycopy(tmp, 0, offsets, 0, tmp.length);
        map.put(patch.symbol, offsets);
      }
      offsets[++offsets[0]] = patch.offset;
    }
  }

  private void destroyMaps() {
    map1 = null;
    map2 = null;
    map4 = null;
    map8 = null;
  }

  public Relocatable prepareImage(RelocatableFactory factory) {
    Relocatable text = factory.createRelocatable(buffer, 0, size);
    for (int i = 0; i < rcount; i++) {
      int offset = relocs[i];
      if (offset >= 0)
        text.posReloc(offset);
      else
        text.negReloc(~offset);
    }
    for (int i = 0; i < pcount; i++) {
      patch patch = patches[i];
      if (patch.offset >= 0)
        text.posPatch(patch.offset, patch.symbol);
      else
        text.negPatch(~patch.offset, patch.symbol);
    }
    return text;
  }

  public void readFrom(InputStream is) throws IOException {
    DataInputStream in = is instanceof DataInputStream ? (DataInputStream)is : new DataInputStream(is);

    int magic = in.readInt();
    if (magic != MAGIC)
      throw new IOException("Unknown magic");
    int version = in.readInt();
    if (version != VERSION)
      throw new IOException("Unsupported version");
    mode = in.readInt();
    if ((mode & ~MODEMASK) != 0)
      throw new IOException("Unsupported mode");
    size = in.readInt();
    if (size < 0)
      throw new IOException();

    buffer = new byte[size];
    in.readFully(buffer);
    
    rcount = in.readInt();
    if (rcount < 0)
      throw new IOException();
    relocs = new int[rcount];
    for (int i = 0; i < rcount; i++)
      relocs[i] = in.readInt();

    if ((mode & 1) != 0) {
      int scount = in.readInt();
      for (int i = 0; i < scount; i++) {
        Symbol symbol = new Extern(in.readUTF());
        int ocount = in.readInt();
        if (ocount < 0)
          throw new IOException();
        if (patches == null)
          patches = new patch[64];
        if (patches.length-pcount < ocount) {
          patch[] tmp = patches;
          patches = new patch[2*tmp.length+ocount];
          System.arraycopy(tmp, 0, patches, 0, tmp.length);
        }
        for (int j = 0; j < ocount; j++) {
          int offset = in.readInt();
          patches[pcount++] = new patch(offset, symbol);
        }
      }
    }

    if ((mode & 2) != 0) {
      int scount = in.readInt();
      for (int i = 0; i < scount; i++) {
        Symbol symbol = new SymbolicType(in.readUTF());
        int ocount = in.readInt();
        if (ocount < 0)
          throw new IOException();
        if (patches == null)
          patches = new patch[64];
        if (patches.length-pcount < ocount) {
          patch[] tmp = patches;
          patches = new patch[2*tmp.length+ocount];
          System.arraycopy(tmp, 0, patches, 0, tmp.length);
        }
        for (int j = 0; j < ocount; j++) {
          int offset = in.readInt();
          patches[pcount++] = new patch(offset, symbol);
        }
      }
    }

    if ((mode & 4) != 0) {
      int scount = in.readInt();
      for (int i = 0; i < scount; i++) {
        Symbol symbol = new MethodEntry(in.readUTF(), in.readInt());
        int ocount = in.readInt();
        if (ocount < 0)
          throw new IOException();
        if (patches == null)
          patches = new patch[64];
        if (patches.length-pcount < ocount) {
          patch[] tmp = patches;
          patches = new patch[2*tmp.length+ocount];
          System.arraycopy(tmp, 0, patches, 0, tmp.length);
        }
        for (int j = 0; j < ocount; j++) {
          int offset = in.readInt();
          patches[pcount++] = new patch(offset, symbol);
        }
      }
    }

    if ((mode & 8) != 0) {
      int scount = in.readInt();
      for (int i = 0; i < scount; i++) {
        Symbol symbol = new StringRef(in.readUTF());
        int ocount = in.readInt();
        if (ocount < 0)
          throw new IOException();
        if (patches == null)
          patches = new patch[64];
        if (patches.length-pcount < ocount) {
          patch[] tmp = patches;
          patches = new patch[2*tmp.length+ocount];
          System.arraycopy(tmp, 0, patches, 0, tmp.length);
        }
        for (int j = 0; j < ocount; j++) {
          int offset = in.readInt();
          patches[pcount++] = new patch(offset, symbol);
        }
      }
    }

  }

  public void writeTo(OutputStream os) throws IOException {
    DataOutputStream out = os instanceof DataOutputStream ? (DataOutputStream)os : new DataOutputStream(os);

    out.writeInt(MAGIC);
    out.writeInt(VERSION);
    out.writeInt(mode);
    out.writeInt(size);

    if (size > 0)
      out.write(buffer, 0, size);

    out.writeInt(rcount);
    for (int i = 0; i < rcount; i++)
      out.writeInt(relocs[i]);

    constructMaps();

    if ((mode & 1) != 0) {
      HashMap map = map1;
      out.writeInt(map.size());
      for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
        Entry entry = (Entry)i.next();
        Extern extern = (Extern)entry.getKey();
        out.writeUTF(extern.getName());
        int[] offsets = (int[])entry.getValue();
        out.writeInt(offsets[0]);
        for (int j = 0; j < offsets[0]; j++)
          out.writeInt(offsets[j+1]);
      }
    }
    
    if ((mode & 2) != 0) {
      HashMap map = map2;
      out.writeInt(map.size());
      for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
        Entry entry = (Entry)i.next();
        SymbolicType symbolicType = (SymbolicType)entry.getKey();
        out.writeUTF(symbolicType.getName());
        int[] offsets = (int[])entry.getValue();
        out.writeInt(offsets[0]);
        for (int j = 0; j < offsets[0]; j++)
          out.writeInt(offsets[j+1]);
      }
    }
    
    if ((mode & 4) != 0) {
      HashMap map = map4;
      out.writeInt(map.size());
      for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
        Entry entry = (Entry)i.next();
        MethodEntry methodEntry = (MethodEntry)entry.getKey();
        out.writeUTF(methodEntry.getName());
        out.writeInt(methodEntry.getIndex());
        int[] offsets = (int[])entry.getValue();
        out.writeInt(offsets[0]);
        for (int j = 0; j < offsets[0]; j++)
          out.writeInt(offsets[j+1]);
      }
    }
    
    if ((mode & 8) != 0) {
      HashMap map = map8;
      out.writeInt(map.size());
      for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
        Entry entry = (Entry)i.next();
        StringRef stringRef = (StringRef)entry.getKey();
        out.writeUTF(stringRef.getValue());
        int[] offsets = (int[])entry.getValue();
        out.writeInt(offsets[0]);
        for (int j = 0; j < offsets[0]; j++)
          out.writeInt(offsets[j+1]);
      }
    }

    destroyMaps();

    out.flush();
  }

  public void printTo(PrintStream out) {
    out.println("---------------------------------------------");

    out.println("DATA");
    out.println();
    for (int i = 0; i < size; i++) {
      if (i % 16 == 0) {
        String offset = Integer.toHexString(i).toUpperCase();
        for (int j = 0; j+offset.length() < 8; j++)
          out.print('0');
        out.print(offset);
        out.print(':');
      }
      if (i % 4 == 0)
        out.print(' ');
      String bite = Integer.toHexString(buffer[i] & 0xFF).toUpperCase();
      for (int j = 0; j+bite.length() < 2; j++)
        out.print('0');
      out.print(bite);
      if (i % 16 == 15)
        out.println();
    }
    out.println();
    out.println();

    out.println("POSITIVE RELOCATIONS");
    out.println();
    for (int i = 0, k = 0; i < rcount; i++)
      if (relocs[i] >= 0) {
        if (k % 4 == 0)
          for (int j = 0; j < 9; j++)
            out.print(' ');
        out.print(' ');
        String offset = Integer.toHexString(relocs[i]).toUpperCase();
        for (int j = 0; j+offset.length() < 8; j++)
          out.print('0');
        out.print(offset);
        if (k % 4 == 3)
          out.println();
        k++;
      }
    out.println();
    out.println();

    out.println("NEGATIVE RELOCATIONS");
    out.println();
    for (int i = 0, k = 0; i < rcount; i++)
      if (relocs[i] < 0) {
        if (k % 4 == 0)
          for (int j = 0; j < 9; j++)
            out.print(' ');
        out.print(' ');
        String offset = Integer.toHexString(~relocs[i]).toUpperCase();
        for (int j = 0; j+offset.length() < 8; j++)
          out.print('0');
        out.print(offset);
        if (k % 4 == 3)
          out.println();
        k++;
      }
    out.println();
    out.println();

    constructMaps();

    out.println("EXTERN PATCHES (NEGATIVE INCLUDED)");
    out.println();
    for (Iterator i = map1.entrySet().iterator(); i.hasNext(); ) {
      Entry entry = (Entry)i.next();

      Extern extern = (Extern)entry.getKey();
      out.println(extern.getName());
      out.println();

      int[] offsets = (int[])entry.getValue();
      for (int j = 0; j < offsets[0]; j++) {
        if (j % 4 == 0)
          for (int k = 0; k < 9; k++)
            out.print(' ');
        out.print(' ');
        String offset = Integer.toHexString(offsets[j+1]).toUpperCase();
        for (int k = 0; k+offset.length() < 8; k++)
          out.print('0');
        out.print(offset);
        if (j % 4 == 3)
          out.println();
      }

      out.println();
      out.println();
    }
    out.println();

    out.println("CLASS PATCHES (NEGATIVE INCLUDED)");
    out.println();
    for (Iterator i = map2.entrySet().iterator(); i.hasNext(); ) {
      Entry entry = (Entry)i.next();

      SymbolicType symbolicType = (SymbolicType)entry.getKey();
      out.println(symbolicType.getName());
      out.println();

      int[] offsets = (int[])entry.getValue();
      for (int j = 0; j < offsets[0]; j++) {
        if (j % 4 == 0)
          for (int k = 0; k < 9; k++)
            out.print(' ');
        out.print(' ');
        String offset = Integer.toHexString(offsets[j+1]).toUpperCase();
        for (int k = 0; k+offset.length() < 8; k++)
          out.print('0');
        out.print(offset);
        if (j % 4 == 3)
          out.println();
      }

      out.println();
      out.println();
    }
    out.println();

    out.println("METHOD PATCHES (NEGATIVE INCLUDED)");
    out.println();
    for (Iterator i = map4.entrySet().iterator(); i.hasNext(); ) {
      Entry entry = (Entry)i.next();

      MethodEntry methodEntry = (MethodEntry)entry.getKey();
      out.println(methodEntry.getName()+"["+methodEntry.getIndex()+"]");
      out.println();

      int[] offsets = (int[])entry.getValue();
      for (int j = 0; j < offsets[0]; j++) {
        if (j % 4 == 0)
          for (int k = 0; k < 9; k++)
            out.print(' ');
        out.print(' ');
        String offset = Integer.toHexString(offsets[j+1]).toUpperCase();
        for (int k = 0; k+offset.length() < 8; k++)
          out.print('0');
        out.print(offset);
        if (j % 4 == 3)
          out.println();
      }

      out.println();
      out.println();
    }
    out.println();

    out.println("STRING PATCHES (NEGATIVE INCLUDED)");
    out.println();
    for (Iterator i = map8.entrySet().iterator(); i.hasNext(); ) {
      Entry entry = (Entry)i.next();

      StringRef stringRef = (StringRef)entry.getKey();
      out.println(stringRef.getValue());
      out.println();

      int[] offsets = (int[])entry.getValue();
      for (int j = 0; j < offsets[0]; j++) {
        if (j % 4 == 0)
          for (int k = 0; k < 9; k++)
            out.print(' ');
        out.print(' ');
        String offset = Integer.toHexString(offsets[j+1]).toUpperCase();
        for (int k = 0; k+offset.length() < 8; k++)
          out.print('0');
        out.print(offset);
        if (j % 4 == 3)
          out.println();
      }

      out.println();
      out.println();
    }

    destroyMaps();

    out.println("---------------------------------------------");
  }

  private static String assemblyUTF8(String string) {
    StringBuffer sb = new StringBuffer();
    sb.append('"');
    int length = string.length();
    for (int i = 0; i < length; i++) {
      int shar = string.charAt(i);
      if (shar >= 0x0001 && shar <= 0x007F)
        appendOctal(sb, shar);
      else {
        if (shar <= 0x07FF)
          appendOctal(sb, ((shar >>  6)&0x1F)|0xC0);
        else {
          appendOctal(sb, ((shar >> 12)&0x0F)|0xE0);
          appendOctal(sb, ((shar >>  6)&0x3F)|0x80);
        }
        appendOctal(sb, (shar&0x3F)|0x80);
      }
    }
    sb.append('"');
    return sb.toString();
  }
  
  private static void appendOctal(StringBuffer sb, int bite) {
    if (' ' <= bite  && bite <= '~') {
      if (bite == '"' || bite == '\\')
        sb.append('\\');
      sb.append((char)bite);
    } else {
      sb.append('\\');
      sb.append(bite/8/8);
      sb.append(bite/8%8);
      sb.append(bite%8);
    }
  }

}

