/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.arch.i386;

import jewel.core.bend.Backend;
import jewel.core.bend.BinaryAssembler;
import jewel.core.bend.Binder;
import jewel.core.bend.CodeGenerator;
import jewel.core.bend.CodeOutput;
import jewel.core.bend.Extern;
import jewel.core.bend.FlowAssembler;
import jewel.core.bend.Label;
import jewel.core.bend.RegisterAllocator;
import jewel.core.bend.SymbolicType;
import jewel.core.clfile.ClassInfo.MethodInfo;
import jewel.core.clfile.Syntax;
import jewel.core.jiro.IR;
import jewel.core.jiro.IRStatement;
import jewel.core.jiro.control.ControlFlowGraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class i386_Backend extends Backend {

  public i386_Backend() { }

  protected CodeGenerator newCodeGenerator() {
    return new i386_CodeGenerator();
  }

  protected BinaryAssembler newBinaryAssembler() {
    return new i386_BinaryAssembler();
  }

  protected FlowAssembler newFlowAssembler() {
    return new i386_FlowAssembler();
  }

  protected RegisterAllocator newRegisterAllocator() {
    return new i386_RegisterAllocator();
  }

  protected void head(MethodInfo method, BinaryAssembler _as, CodeGenerator _codegen, RegisterAllocator _regalloc) {
    i386_BinaryAssembler as = (i386_BinaryAssembler)_as;
    i386_CodeGenerator codegen = (i386_CodeGenerator)_codegen;
    i386_RegisterAllocator regalloc = (i386_RegisterAllocator)_regalloc;

    Binder binder = codegen.getBinder();

    as.label("Entry");
    as.pushl(as.EBP);
    as.movl(as.ESP, as.EBP);
    as.pushl(as.imm(-i386_CodeGenerator.TEXT_BASE, new Label("Entry")));
    if (regalloc.localWords > 0)
      as.subl(4*regalloc.localWords, as.ESP);
    if (regalloc.usedcsregs.get(as._EDI))
      as.pushl(as.EDI);
    if (regalloc.usedcsregs.get(as._ESI))
      as.pushl(as.ESI);
    if (regalloc.usedcsregs.get(as._EBX))
      as.pushl(as.EBX);
  }

  protected void tail(MethodInfo method, BinaryAssembler _as, CodeGenerator _codegen, RegisterAllocator _regalloc) {
    i386_BinaryAssembler as = (i386_BinaryAssembler)_as;
    i386_CodeGenerator codegen = (i386_CodeGenerator)_codegen;
    i386_RegisterAllocator regalloc = (i386_RegisterAllocator)_regalloc;

    Binder binder = codegen.getBinder();

    if (regalloc.usedcsregs.get(as._EBX))
      as.popl(as.EBX);
    if (regalloc.usedcsregs.get(as._ESI))
      as.popl(as.ESI);
    if (regalloc.usedcsregs.get(as._EDI))
      as.popl(as.EDI);
    as.leave();
    if (binder.paramWords() == 0)
      as.ret();
    else
      as.ret(4*binder.paramWords());

    ArrayList handlers = codegen.getHandlers();
    ArrayList inspectors = codegen.getInspectors();
    ArrayList natives = codegen.getNatives();
    ArrayList switches = codegen.getSwitches();

    if (handlers.size() > 0) {
      int csregs = 0;
      if (regalloc.usedcsregs.get(as._EBX))
        csregs++;
      if (regalloc.usedcsregs.get(as._ESI))
        csregs++;
      if (regalloc.usedcsregs.get(as._EDI))
        csregs++;
      as.align(8);
      as.label("Catcher");
      as.movl(as.ESP, as.ECX);
      as.movl(as.addr(4, as.ESP), as.EDX);
      as.movl(as.addr(8, as.ESP), as.EAX);
      as.leal(as.addr(-4*(1+regalloc.localWords+csregs), as.EBP),as.ESP);
      int index = 0;
      for (Iterator i = handlers.iterator(); i.hasNext(); index++) {
        int handler = ((Integer)i.next()).intValue();
        as.cmpl(as.imm(new Label("X"+index)), as.EDX);
        as.je(as.imm(new Label("L"+handler)));
      }
      as.movl(as.ECX, as.ESP);
      as.ret();
    }

    if (switches.size() > 0) {
      as.align(4);
      int index = 0;
      for (Iterator i = switches.iterator(); i.hasNext(); index++) {
        ArrayList table = (ArrayList)i.next();
        as.label("Switch."+index);
        for (Iterator j = table.iterator(); j.hasNext(); ) {
          String label = (String)j.next();
          as.dl(as.imm(new Label(label)));
        }
      }
    }
    
    if (inspectors.size() > 0) {
      
      int index = 0;
      HashMap traceMap = new HashMap();
      ArrayList[] traceReverse = new ArrayList[inspectors.size()];
      for (Iterator i = inspectors.iterator(); i.hasNext(); index++) {
        IR.tnode tnode = (IR.tnode)i.next();
        ArrayList traces = new ArrayList();
        for (Iterator j = tnode.traces(); j.hasNext(); ) {
          IR.tnode.trace trace = (IR.tnode.trace)j.next();
          traces.add(trace.getSymbolicType());
          traces.add(new Integer(trace.getLine() == -1 ? 65535 : trace.getIndex()));
          traces.add(new Integer(trace.getLine() == -1 ? trace.getIndex() : trace.getLine()));
        }
        ArrayList list = (ArrayList)traceMap.get(traces);
        if (list == null) {
          list = new ArrayList();
          traceMap.put(traces, list);
        }
        list.add(new Integer(index));
        traceReverse[index] = traces;
      }
      
      index = 0;
      HashMap livesMap = new HashMap();
      HashSet[] livesReverse = new HashSet[inspectors.size()];
      for (Iterator i = inspectors.iterator(); i.hasNext(); index++) {
        IR.tnode tnode = (IR.tnode)i.next();
        IRStatement stmt = tnode.ownerStmt();
        BitSet value = binder.livesAt(stmt);
        HashSet lives = new HashSet();
        for (int j = 4; j < value.length(); j += 5)
          if (value.get(j))
            lives.add(new Integer(binder.regBase(j)));
        ArrayList list = (ArrayList)livesMap.get(lives);
        if (list == null) {
          list = new ArrayList();
          livesMap.put(lives, list);
        }
        list.add(new Integer(index));
        livesReverse[index] = lives;
      }

      as.align(4);
      as.label("Inspector");
      index = 0;
      for (Iterator i = inspectors.iterator(); i.hasNext(); index++) {
        IR.tnode tnode = (IR.tnode)i.next();
        as.dl(as.imm(new Label("T"+index)));
        ArrayList trace = traceReverse[index];
        if (trace.size() == 0)
          as.dl(0);
        else {
          ArrayList list = (ArrayList)traceMap.get(trace);
          as.dl(as.imm(new Label("Trace."+list.get(0))));
        }
        HashSet lives = livesReverse[index];
        if (lives.size() == 0)
          as.dl(0);
        else {
          ArrayList list = (ArrayList)livesMap.get(lives);
          as.dl(as.imm(new Label("Lives."+list.get(0))));
        }
        //as.dl(tnode.ownerStmt().ownerBB().ownerCFG().indexOf(tnode.ownerStmt()));
      }
      as.dl(0);
      
      Set traceSet = traceMap.entrySet();
      for (Iterator i = traceSet.iterator(); i.hasNext(); ) {
        Entry entry = (Entry)i.next();
        ArrayList traces = (ArrayList)entry.getKey();
        ArrayList list = (ArrayList)entry.getValue();
        if (traces.size() > 0) {
          as.label("Trace."+list.get(0));
          for (Iterator j = traces.iterator(); j.hasNext(); ) {
            Object object = j.next();
            if (object instanceof String)
              as.dl(as.imm(new SymbolicType((String)object)));
            else {
              int value = ((Integer)object).intValue();
              as.dw(value);
            }
          }
          as.dl(0);
        }
      }
            
      Set livesSet = livesMap.entrySet();
      for (Iterator i = livesSet.iterator(); i.hasNext(); ) {
        Entry entry = (Entry)i.next();
        HashSet lives = (HashSet)entry.getKey();
        ArrayList list = (ArrayList)entry.getValue();
        if (lives.size() > 0) {
          as.label("Lives."+list.get(0));
          for (Iterator j = lives.iterator(); j.hasNext(); ) {
            int integer = ((Integer)j.next()).intValue();
            if (integer/4 != (byte)(integer/4))
              throw new Error("Live variable exceeds frame indexing capacity");
            as.db(integer/4);
          }
          as.db(0);
        }
      }
      
    }

    for (int i = 0; i < natives.size(); i++) {
      String s = (String)natives.get(i);
      as.label("Natives."+i);
      for (int j = 0; j < s.length(); j++)
        as.db(s.charAt(j));
      as.db(0);
    }
    
    as.align(4);
    
    if (as.refEncTableSize() > 0) {
      as.label("References");
      as.writeRefEncTable();
      as.dl(-1);
    }

    as.db(0);
    as.db(0);
    as.dw(method.getIndex());

    as.dl(as.imm(new SymbolicType(method.getOwner().getName())));
    if (handlers.size() == 0)
      as.dl(0);
    else
      as.dl(as.imm(new Label("Catcher")));
    if (inspectors.size() == 0)
      as.dl(0);
    else
      as.dl(as.imm(new Label("Inspector")));
    if (as.refEncTableSize() == 0)
      as.dl(0);
    else
      as.dl(as.imm(new Label("References")));
  }

  public CodeOutput lazyTranslate(MethodInfo method) {
    i386_BinaryAssembler as = new i386_BinaryAssembler();

    as.label("Entry");
    as.movl(as.addr(new Label("Target")), as.EAX);
    as.cmpl(0, as.EAX);
    as.jne(as.imm(new Label("L0")));

    as.pushl(as.EBP);
    as.movl(as.ESP, as.EBP);
    as.pushl(as.imm(-i386_CodeGenerator.TEXT_BASE, new Label("Entry")));

    as.pushl(as.addr(new Label("Level")));
    as.pushl(as.imm(-i386_CodeGenerator.TEXT_BASE, new Label("Entry")));
    as.call(as.imm(new Extern("_lazy_"))); 
    as.label("T0");
    as.movl(as.EAX, as.addr(new Label("Target")));
    //as.movb(1, as.addr(new Label("Skip")));

    as.leave();

    as.label("L0");

    as.decl(as.addr(new Label("Counter")));
    as.jnz(as.imm(new Label("L1")));
    as.incl(as.addr(new Label("Level")));
    as.movl(0, as.addr(new Label("Target")));
    //as.movl(1000, as.addr(new Label("Counter")));
    as.label("L1");

    if (i386_CodeGenerator.TEXT_BASE != 0)
      as.addl(i386_CodeGenerator.TEXT_BASE, as.EAX);

    as.jmpl(as.EAX);

    int[] lives = liveParams(method);

    if (lives.length > 0) {
      as.label("Lives.0");
      for (int i = 0; i < lives.length; i++) {
        int integer = lives[i];
        if (integer/4 != (byte)(integer/4))
          throw new Error("Live variable exceeds frame indexing capacity");
        as.db(integer/4);
      }
      as.db(0);
    }

    as.align(4);

    if (lives.length > 0) {
      as.label("Inspector");
      as.dl(as.imm(new Label("T0")));
      as.dl(0);
      as.dl(as.imm(new Label("Lives.0")));
      //as.dl(-1);
      as.dl(0);
    }

    as.label("Counter");
    as.dl(1000);
    as.label("Level");
    as.dl(0);
    as.label("Target");
    as.dl(0);
    as.label("Skip");
    as.db(0);
    as.db(1);
    as.dw(method.getIndex());
    as.dl(as.imm(new SymbolicType(method.getOwner().getName())));
    as.dl(0);
    if (lives.length == 0)
      as.dl(0);
    else
      as.dl(as.imm(new Label("Inspector")));
    as.dl(0);

    return as.toCodeOutput();
  }

  private static int[] liveParams(MethodInfo method) {
    boolean isStatic = method.isStatic() || method.getName().equals("<clinit>");
    String[] types = Syntax.getParameterTypes(method.getDescriptor());
    int count = 0;
    if (!isStatic)
      count++;
    for (int i = 0; i < types.length; i++) {
      String type = types[i];
      char id = type.charAt(0);
      if (id == 'L' || id == '[')
        count++;
    }
    int[] lives = new int[count];
    count = 0;
    int index = 8;
    if (!isStatic) {
      lives[count++] = index;
      index += 4;
    }
    for (int i = 0; i < types.length; i++) {
      String type = types[i];
      char id = type.charAt(0);
      if (id == 'L' || id == '[')
        lives[count++] = index;
      if (id == 'D' || id == 'J')
        index += 8;
      else
        index += 4;
    }
    return lives;
  }

}

