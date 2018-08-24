/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro;

import jewel.core.bend.Binder;
import jewel.core.jiro.beg.TreeNode;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.dataflow.BitItem;
import jewel.core.jiro.dataflow.DataFlowAnalyser;
import jewel.core.jiro.dataflow.DataFlowAnalysis;
import jewel.core.jiro.dataflow.IterativeDataFlowAnalyser;
import jewel.core.jiro.graph.ConflictGraph;
import jewel.core.jiro.graph.ConflictGraph.ConflictEdge;
import jewel.core.jiro.graph.ConflictGraph.ConflictNode;

import java.util.BitSet;
import java.util.Iterator;

public final class IRBinder extends Binder {

  private DataFlowAnalyser analyser;
  private int paramWords;
  private int localWords;
  private int[] regBase;
  
  public IRBinder() { }
  
  protected void bind(ControlFlowGraph cfg) {
    analyser = new IterativeDataFlowAnalyser(new LiveVariables());
    ConflictGraph conflictGraph = new ConflictGraph();
    for (Iterator i = cfg.bottomupStmts(); i.hasNext(); ) {
      IRStatement stmt = (IRStatement)i.next();
      BitItem value = (BitItem)analyser.valueBefore(stmt);
      int length = value.length();
      for (int j = 0; j < length; j++)
        if (value.get(j)) {
          ConflictNode source = conflictGraph.getNode(j);
          if (source == null) {
            source = new ConflictNode(j);
            conflictGraph.addNode(source);
          }
          for (int k = j+1; k < length; k++)
            if (value.get(k)) {
              ConflictNode target = conflictGraph.getNode(k);
              if (target == null) {
                target = new ConflictNode(k);
                conflictGraph.addNode(target);
              }
              conflictGraph.addEdge(new ConflictEdge(source, target));
            }
        }
    }

//    conflictGraph.show();

    int regLength = 0;
    for (Iterator i = conflictGraph.nodes(); i.hasNext(); ) {
      ConflictNode node = (ConflictNode)i.next();
      if (regLength < node.register()+1)
        regLength = node.register()+1;
    }
    
    paramWords = 0;
    IRStatement firstStmt = (IRStatement)cfg.firstStmt();
    ParamMatcher paramMatcher = new ParamMatcher(firstStmt.snode());
    if (regLength < paramMatcher.cover.length)
      regLength = paramMatcher.cover.length;
    regBase = new int[regLength];
    paramMatcher.cover();
    
    localWords = 0;
outer:
    for (Iterator i = conflictGraph.nodes(); i.hasNext(); ) {
      ConflictNode node = (ConflictNode)i.next();
      if (regBase[node.register()] != 0)
        continue outer;
      for (int j = 0; j < paramWords; j++) {
        if ((node.register()%5)%2 == 1 && j%2 == 1)
          continue;
        int base = 4*j+8;
        boolean free = true;
        for (Iterator k = node.conflictNodes(); k.hasNext(); ) {
          ConflictNode neighbor = (ConflictNode)k.next();
          if (regBase[neighbor.register()] != 0) {
            if (regBase[neighbor.register()] == base)
              free = false;
            else if ((node.register()%5)%2 == 1 && regBase[neighbor.register()] == base+4)
              free = false;
            else if ((neighbor.register()%5)%2 == 1 && regBase[neighbor.register()]+4 == base)
              free = false;
          }
        }
        if (free) {
          if ((node.register()%5)%2 == 1 && j == paramWords-1)
            continue;
          regBase[node.register()] = base;
          continue outer;
        }
      }
      for (int j = 0; j < localWords; j++) {
        if ((node.register()%5)%2 == 1 && j%2 == 1)
          continue;
        int base = -4*j-8;
        boolean free = true;
        for (Iterator k = node.conflictNodes(); k.hasNext(); ) {
          ConflictNode neighbor = (ConflictNode)k.next();
          if (regBase[neighbor.register()] != 0) {
            if (regBase[neighbor.register()] == base)
              free = false;
            else if ((node.register()%5)%2 == 1 && regBase[neighbor.register()] == base+4)
              free = false;
            else if ((neighbor.register()%5)%2 == 1 && regBase[neighbor.register()]+4 == base)
              free = false;
          }
        }
        if (free) {
          if ((node.register()%5)%2 == 1 && j == 0)
            continue;
          regBase[node.register()] = base;
          continue outer;
        }
      }
      if ((node.register()%5)%2 == 1) {
        localWords++;
        if (localWords%2 == 1)
          localWords++;
      }
      regBase[node.register()] = -4*localWords-8;
      localWords++;
    }
  }
  
  public int paramWords() {
    return paramWords;
  }
  
  public int localWords() {
    return localWords;
  }

  public int regBase(int reg) {
    int base = 0;
    if (reg < regBase.length)
      base = regBase[reg];
    if (base == 0)
      throw new IllegalStateException();
    return base;
  }
  
  public BitSet livesAt(Statement stmt) {
    return (BitSet)analyser.valueAfter(stmt);
  }
  
  private final class ParamMatcher {
  
    private void cover()
    <int length, ParamMatcher matcher>
      : IR.IRECEIVE
      { @@.length = @1.getReg()+1;
        @@.matcher = new ParamMatcher(@1.next());
        if (@@.matcher.cover.length > @@.length)
          @@.length = @@.matcher.cover.length; }
      = { regBase[@1.getReg()] = 4*paramWords+8;
          paramWords++;
          @@.matcher.cover(); }
      | IR.LRECEIVE
      { @@.length = @1.getReg()+1;
        @@.matcher = new ParamMatcher(@1.next());
        if (@@.matcher.cover.length > @@.length)
          @@.length = @@.matcher.cover.length; }
      = { regBase[@1.getReg()] = 4*paramWords+8;
          paramWords += 2;
          @@.matcher.cover(); }
      | IR.FRECEIVE
      { @@.length = @1.getReg()+1;
        @@.matcher = new ParamMatcher(@1.next());
        if (@@.matcher.cover.length > @@.length)
          @@.length = @@.matcher.cover.length; }
      = { regBase[@1.getReg()] = 4*paramWords+8;
          paramWords++;
          @@.matcher.cover(); }
      | IR.DRECEIVE
      { @@.length = @1.getReg()+1;
        @@.matcher = new ParamMatcher(@1.next());
        if (@@.matcher.cover.length > @@.length)
          @@.length = @@.matcher.cover.length; }
      = { regBase[@1.getReg()] = 4*paramWords+8;
          paramWords += 2;
          @@.matcher.cover(); }
      | IR.ARECEIVE
      { @@.length = @1.getReg()+1;
        @@.matcher = new ParamMatcher(@1.next());
        if (@@.matcher.cover.length > @@.length)
          @@.length = @@.matcher.cover.length; }
      = { regBase[@1.getReg()] = 4*paramWords+8;
          paramWords++;
          @@.matcher.cover(); }
      | default
      { @@.length = 0; }
      = { }
      ;
  
  }

}

