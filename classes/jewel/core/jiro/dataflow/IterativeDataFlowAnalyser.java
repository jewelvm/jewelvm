/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.dataflow;

import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowFunction;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowItem;

import java.util.Iterator;

public final class IterativeDataFlowAnalyser extends DataFlowAnalyser {

  private transient ControlFlowGraph cfg;
  private transient BBData[] data;

  public IterativeDataFlowAnalyser(DataFlowAnalysis analysis) {
    super(analysis);
  }

  private BBData get(BasicBlock currentBB) {
    return data[cfg.indexOf(currentBB)];
  }

  private void iterate(ControlFlowGraph cfg) {
    boolean change = true;
    switch (analysis.direction()) {
    case DataFlowAnalysis.FORWARD:
      for (Iterator i = cfg.entryBBs(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        get(currentBB).input = analysis.newFlowItem();
      }
      while (change) {
        change = false;
        for (Iterator i = cfg.topdownBBs(); i.hasNext(); ) {
          BasicBlock currentBB = (BasicBlock)i.next();
          BBData data = get(currentBB);
          FlowItem input = data.input;
          for (Iterator j = currentBB.inEdges(); j.hasNext(); ) {
            ControlEdge edge = (ControlEdge)j.next();
            BasicBlock predBB = edge.sourceBB();
            FlowItem output = get(predBB).output;
            if (output != null) {
              FlowFunction function = analysis.newFlowFunction();
              analysis.modelEffect(function, edge);
              FlowItem value = function.apply(output);
              if (input == null)
                input = value;
              else
                input = analysis.merge(input, value);
              data.input = input;
            }
          }
          if (input != null) {
            FlowFunction function = data.function;
            if (function == null) {
              function = buildFlowFunction(currentBB);
              data.function = function;
            }
            FlowItem result = function.apply(input);
            FlowItem output = data.output;
            if (!result.equals(output)) {
              output = result;
              data.output = output;
              change = true;
            }
          }
        }
      }
      break;
    case DataFlowAnalysis.BACKWARD:
      for (Iterator i = cfg.exitBBs(); i.hasNext(); ) {
        BasicBlock currentBB = (BasicBlock)i.next();
        get(currentBB).output = analysis.newFlowItem();
      }
      while (change) {
        change = false;
        for (Iterator i = cfg.bottomupBBs(); i.hasNext(); ) {
          BasicBlock currentBB = (BasicBlock)i.next();
          BBData data = get(currentBB);
          FlowItem output = data.output;
          for (Iterator j = currentBB.outEdges(); j.hasNext(); ) {
            ControlEdge edge = (ControlEdge)j.next();
            BasicBlock succBB = edge.targetBB();
            FlowItem input = get(succBB).input;
            if (input != null) {
              FlowFunction function = analysis.newFlowFunction();
              analysis.modelEffect(function, edge);
              FlowItem value = function.apply(input);
              if (output == null)
                output = value;
              else
                output = analysis.merge(output, value);
              data.output = output;
            }
          }
          if (output != null) {
            FlowFunction function = data.function;
            if (function == null) {
              function = buildFlowFunction(currentBB);
              data.function = function;
            }
            FlowItem result = function.apply(output);
            FlowItem input = data.input;
            if (!result.equals(input)) {
              input = result;
              data.input = input;
              change = true;
            }
          }
        }
      }
      break;
    default:
      throw new IllegalArgumentException("Illegal direction");
    }
  }

  public FlowItem valueBefore(BasicBlock block) {
    ControlFlowGraph cfg = block.ownerCFG();
    if (cfg == null)
      throw new IllegalArgumentException("Basic block not associated to a control flow graph");

    if (this.cfg == null) {
      this.cfg = cfg;
      data = new BBData[cfg.count()];
      for (int i = 0; i < cfg.count(); i++)
        data[i] = new BBData();
      iterate(cfg);
    }

    if (this.cfg != cfg)
      throw new IllegalArgumentException("Basic block not associated to control flow graph");

    return get(block).input;
  }

  public FlowItem valueAfter(BasicBlock block) {
    ControlFlowGraph cfg = block.ownerCFG();
    if (cfg == null)
      throw new IllegalArgumentException("Basic block not associated to a control flow graph");

    if (this.cfg == null) {
      this.cfg = cfg;
      data = new BBData[cfg.count()];
      for (int i = 0; i < cfg.count(); i++)
        data[i] = new BBData();
      iterate(cfg);
    }

    if (this.cfg != cfg)
      throw new IllegalArgumentException("Basic block not associated to control flow graph");

    return get(block).output;
  }

  private static final class BBData {
    public FlowItem input;
    public FlowFunction function;
    public FlowItem output;
  }

}

