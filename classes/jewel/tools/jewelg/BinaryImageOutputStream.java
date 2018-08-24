/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.tools.jewelg;

import jewel.core.bend.CodeOutput;
import jewel.core.bend.Extern;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class BinaryImageOutputStream extends ImageOutputStream {

  private final byte wordSize;
  private final boolean littleEndian;
  private final HashMap labels = new HashMap();
  private final CodeOutput output = new CodeOutput();

  public BinaryImageOutputStream(OutputStream out) throws IOException {
    this(out, 4, false);
  }

  public BinaryImageOutputStream(OutputStream out, int wordSize) throws IOException {
    this(out, wordSize, false);
  }

  public BinaryImageOutputStream(OutputStream out, boolean littleEndian) throws IOException {
    this(out, 4, littleEndian);
  }

  public BinaryImageOutputStream(OutputStream out, int wordSize, boolean littleEndian) throws IOException {
    super(out);
    if (wordSize != 4 && wordSize != 8)
      throw new IllegalArgumentException("Invalid word size");
    this.wordSize = (byte)wordSize;
    this.littleEndian = littleEndian;
  }

  public int wordSize() {
    return wordSize;
  }

  public boolean littleEndian() {
    return littleEndian;
  }

  protected void writeImage() throws IOException {
    DataOutputStream out = this.out instanceof DataOutputStream ? (DataOutputStream)this.out : new DataOutputStream(this.out);

    ArrayList globlList = new ArrayList();
    for (Iterator i = labels.keySet().iterator(); i.hasNext(); ) {
      String label = (String)i.next();
      LabelData data = getLabelData(label);
      if (!data.isDeclared())
        throw new IllegalStateException("Undeclared label");
      if (data.isGlobl()) {
        int offset = data.getOffset();
        if (offset == -1)
          throw new IllegalStateException("Unbounded global");
        globlList.add(label);
      }
    }

    output.writeTo(out);

    String[] globls = (String[])globlList.toArray(new String[globlList.size()]);
    Arrays.sort(globls);
    out.writeInt(globls.length);
    for (int i = 0; i < globls.length; i++) {
      String label = globls[i];
      LabelData data = getLabelData(label);
      out.writeUTF(label);
      out.writeInt(data.getOffset());
    }

    out.flush();
  }
  
  public void globl(String label) {
    LabelData data = getLabelData(label);
    data.setGlobl(true);
  }

  public void extern(String label) {
    LabelData data = getLabelData(label);
    data.setDeclared(true);
    data.setExtern(true);
  }

  public void label(String label) {
    LabelData data = getLabelData(label);
    data.setDeclared(true);
    data.setOffset(output.size());
  }
  
  public void align() {
    while (output.size() % wordSize != 0)
      write(0);
  }
  
  public void space(int size) {
    for (int i = 0; i < size; i++)
      write(0);
  }

  public void write(int bite) {
    output.append(bite & 0xFF);
  }

  public void write(byte[] array, int start, int length) {
    output.append(array, start, length);
  }

  public void writeByte(int bite) {
    write(bite);
  }
  
  public void writeShort(int shirt) {
    if (littleEndian) {
      writeByte(shirt);
      writeByte(shirt >> 8);
    } else {
      writeByte(shirt >> 8);
      writeByte(shirt);
    }
  }

  public void writeInt(int ant) {
    if (littleEndian) {
      writeShort(ant);
      writeShort(ant >> 16);
    } else {
      writeShort(ant >> 16);
      writeShort(ant);
    }
  }
  
  public void writeLong(long lung) {
    if (littleEndian) {
      writeInt((int)lung);
      writeInt((int)(lung >> 32));
    } else {
      writeInt((int)(lung >> 32));
      writeInt((int)lung);
    }
  }

  public void writeWord(int word) {
    if (wordSize == 4)
      writeInt(word);
    else
      writeLong(word);
  }

  public void writeWord(String label, int offset) {
    LabelData data = getLabelData(label);
    int labelOffset = data.getOffset();
    output.posReloc();
    if (labelOffset != -1)
      writeWord(labelOffset+offset);
    else {
      data.posPatch();
      writeWord(offset);
    }
  }

  public void writeWord(String label, String negLabel, int offset) {
    LabelData data = getLabelData(label);
    LabelData negData = getLabelData(negLabel);
    int labelOffset = data.getOffset();
    int negLabelOffset = negData.getOffset();
    if (labelOffset != -1)
      if (negLabelOffset != -1)
        writeWord(labelOffset-negLabelOffset+offset);
      else {
        negData.negPatch();
        writeWord(labelOffset+offset);
      }
    else
      if (negLabelOffset != -1) {
        data.posPatch();
        writeWord(-negLabelOffset+offset);
      } else {
        data.posPatch();
        negData.negPatch();
        writeWord(offset);
      }
  }

  private LabelData getLabelData(String label) {
    if (label == null)
      throw new NullPointerException();
    LabelData data = (LabelData)labels.get(label);
    if (data == null) {
      data = new LabelData(label);
      labels.put(label, data);
    }
    return data;
  }

  private int readWord(int offset) {
    int i0, i1, i2, i3, i4, i5, i6, i7;
    if (littleEndian) {
      i0 = 0; i1 = 1; i2 = 2; i3 = 3;
      i4 = 4; i5 = 5; i6 = 6; i7 = 7;
    } else {
      i0 = 7; i1 = 6; i2 = 5; i3 = 4;
      i4 = 3; i5 = 2; i6 = 1; i7 = 0;
    }
    return output.get(offset+i0)
         | output.get(offset+i1) <<  8
         | output.get(offset+i2) << 16
         | output.get(offset+i3) << 24;
  }

  private void writeWord(int offset, int word) {
    int i0, i1, i2, i3, i4, i5, i6, i7;
    if (littleEndian) {
      i0 = 0; i1 = 1; i2 = 2; i3 = 3;
      i4 = 4; i5 = 5; i6 = 6; i7 = 7;
    } else {
      i0 = 7; i1 = 6; i2 = 5; i3 = 4;
      i4 = 3; i5 = 2; i6 = 1; i7 = 0;
    }
    output.set(offset+i0,  word        & 0xFF);
    output.set(offset+i1, (word >>  8) & 0xFF);
    output.set(offset+i2, (word >> 16) & 0xFF);
    output.set(offset+i3, (word >> 24) & 0xFF);
    if (wordSize == 8) {
      output.set(offset+i4, (word >> 31) & 0xFF);
      output.set(offset+i5, (word >> 31) & 0xFF);
      output.set(offset+i6, (word >> 31) & 0xFF);
      output.set(offset+i7, (word >> 31) & 0xFF);
    }
  }

  public void close() throws IOException {
    writeImage();
    super.close();
  }

  private final class LabelData {

    private final String label;
    private boolean declared;
    private boolean globl;
    private boolean extern;
    private int offset = -1;
    private int pcount;
    private int[] patches;

    public LabelData(String label) {
      this.label = label;
    }

    public boolean isDeclared() {
      return declared;
    }

    public void setDeclared(boolean declared) {
      if (this.declared)
        throw new IllegalStateException("Label already declared");
      this.declared = declared;
    }

    public boolean isGlobl() {
      return globl;
    }

    public void setGlobl(boolean globl) {
      this.globl = globl;
    }

    public boolean isExtern() {
      return extern;
    }

    public void setExtern(boolean extern) {
      if (this.offset != -1)
        throw new IllegalStateException("Label cannot be extern");
      if (pcount > 0)
        throw new IllegalStateException("Extern label must be declared prior to use");
      this.extern = extern;
    }

    public int getOffset() {
      return offset;
    }

    public void setOffset(int offset) {
      if (offset < 0)
        throw new IllegalArgumentException("Negative offset");
      if (extern)
        throw new IllegalStateException("Label is extern");
      if (this.offset != -1)
        throw new IllegalStateException("Label already binded");
      this.offset = offset;
      for (int i = 0; i < pcount; i++) {
        int patchOffset = patches[i];
        if (patchOffset >= 0) {
          int word = readWord(patchOffset);
          word += offset;
          writeWord(patchOffset, word);
        } else {
          patchOffset = ~patchOffset;
          int word = readWord(patchOffset);
          word -= offset;
          writeWord(patchOffset, word);
        }
      }
      pcount = 0;
      patches = null;
    }

    public void posPatch() {
      if (this.offset != -1)
        throw new IllegalStateException("Label already binded");
      if (extern)
        output.posPatch(new Extern(label));
      else {
        if (patches == null)
          patches = new int[8];
        if (pcount == patches.length) {
          int[] tmp = patches;
          patches = new int[2*tmp.length+1];
          System.arraycopy(tmp, 0, patches, 0, tmp.length);
        }
        patches[pcount++] = output.size();
      }
    }

    public void negPatch() {
      if (this.offset != -1)
        throw new IllegalStateException("Label already binded");
      if (extern)
        output.negPatch(new Extern(label));
      else {
        if (patches == null)
          patches = new int[8];
        if (pcount == patches.length) {
          int[] tmp = patches;
          patches = new int[2*tmp.length+1];
          System.arraycopy(tmp, 0, patches, 0, tmp.length);
        }
        patches[pcount++] = ~output.size();
      }
    }

  }

}

