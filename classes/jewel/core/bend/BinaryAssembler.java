/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.bend;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Iterator;

public abstract class BinaryAssembler extends Assembler {

  private CodeOutput output;
  private int rcount;
  private refenc[] refencs;

  private static final class refenc {
    public final boolean relative;
    public final int offset;
    public final int disp;
    public refenc(boolean relative, int offset, int disp) {
      this.relative = relative;
      this.offset = offset;
      this.disp = disp;
    }
  }

  protected BinaryAssembler() { }

  public void reset() {
    super.reset();
    output = new CodeOutput();
    rcount = 0;
    refencs = new refenc[32];
  }

  protected int size() {
    return output.size();
  }

  protected void write(int bite) {
    output.append(bite);
  }

  protected void write(boolean relative, int disp, Symbol ssym) {
    if (ssym instanceof Label) {
      SymbolTable symtab = getSymbolTable();
      Object value = symtab.getDeclValue(ssym);
      if (value == null) {
        value = new ArrayList();
        symtab.declare(ssym, value);
      }
      if (value instanceof Integer)
        disp += ((Integer)value).intValue();
      if (value instanceof ArrayList)
        ((ArrayList)value).add(new Integer(size()));
      if (relative)
        disp -= size()+4;
      else
        output.posReloc();
    } else {
      output.posPatch(ssym);
      if (relative) {
        output.negReloc();
        disp -= size()+4;
      }
      if (ssym instanceof MethodEntry || ssym instanceof StringRef) {
        if (rcount == refencs.length) {
          refenc[] tmp = refencs;
          refencs = new refenc[2*tmp.length+1];
          System.arraycopy(tmp, 0, refencs, 0, tmp.length);
        }
        refencs[rcount++] = new refenc(relative, size(), disp);
      }
    }
    write32LE(disp);
  }

  public CodeOutput toCodeOutput() {
    SymbolTable symtab = getSymbolTable();
    for (Iterator i = symtab.declarations(); i.hasNext(); ) {
      Entry entry = (Entry)i.next();
      Object value = entry.getValue();
      if (value instanceof ArrayList)
        throw new IllegalStateException();
    }
    return (CodeOutput)output.clone();
  }

  public void label(String label) {
    SymbolTable symtab = getSymbolTable();
    Label lab = new Label(label);
    Object value = symtab.getDeclValue(lab);
    if (value instanceof Integer)
      throw new IllegalArgumentException();
    if (value instanceof ArrayList)
      for (Iterator i = ((ArrayList)value).iterator(); i.hasNext(); ) {
        int offset = ((Integer)i.next()).intValue();
        int word = output.get(offset)
                 | output.get(offset+1) << 8
                 | output.get(offset+2) << 16
                 | output.get(offset+3) << 24;
        word += size();
        output.set(offset, word & 0xFF);
        output.set(offset+1, word >> 8 & 0xFF);
        output.set(offset+2, word >> 16 & 0xFF);
        output.set(offset+3, word >> 24 & 0xFF);
      }
    symtab.declare(lab, new Integer(size()));
  }

  public int refEncTableSize() {
    return rcount;
  }

  public void writeRefEncTable() {
    for (int i = 0; i < rcount; i++) {
      write32LE(refencs[i].offset);
      if (refencs[i].relative)
        output.posReloc();
      write32LE(-refencs[i].disp);
    }
  }

  private void write32LE(int disp) {
    write(disp       & 0xFF);
    write(disp >>  8 & 0xFF);
    write(disp >> 16 & 0xFF);
    write(disp >> 24 & 0xFF);
  }

}

