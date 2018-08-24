/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.dataflow;

import jewel.core.jiro.control.BasicBlock;
import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.ControlFlowGraph;
import jewel.core.jiro.control.Statement;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowItem;
import jewel.core.jiro.dataflow.DataFlowAnalysis.FlowFunction;

import java.util.Iterator;

public abstract class DataFlowAnalyser {

  protected final DataFlowAnalysis analysis;

  protected DataFlowAnalyser(DataFlowAnalysis analysis) {
    this.analysis = analysis;
  }

  public final FlowFunction buildFlowFunction(BasicBlock block) {
    FlowFunction function = analysis.newFlowFunction();
    switch (analysis.direction()) {
    case DataFlowAnalysis.FORWARD:
      for (Iterator i = block.topdownStmts(); i.hasNext(); ) {
        Statement current = (Statement)i.next();
        analysis.modelEffect(function, current);
      }
      break;
    case DataFlowAnalysis.BACKWARD:
      for (Iterator i = block.bottomupStmts(); i.hasNext(); ) {
        Statement current = (Statement)i.next();
        analysis.modelEffect(function, current);
      }
      break;
    default:
      throw new IllegalArgumentException("Illegal direction");
    }
    return function;
  }

  private transient Statement bstmt;
  private transient FlowFunction bfunction;
  public final FlowItem valueBefore(Statement stmt) {
    BasicBlock block = stmt.ownerBB();
    if (block == null)
      throw new IllegalArgumentException("Statement not associated to a basic block");

    if (stmt == block.leader())
      return valueBefore(block);

    FlowItem input;
    FlowFunction function;
    switch (analysis.direction()) {
    case DataFlowAnalysis.FORWARD:
      input = valueBefore(block);

      if (bstmt != null && bstmt == stmt.previous()) {
        function = bfunction;
        analysis.modelEffect(function, stmt.previous());
      } else {
        function = analysis.newFlowFunction();
        for (Iterator i = block.topdownStmts(); i.hasNext(); ) {
          Statement current = (Statement)i.next();
          if (current == stmt)
            break;
          analysis.modelEffect(function, current);
        }
      }

      bstmt = stmt;
      bfunction = function;

      break;
    case DataFlowAnalysis.BACKWARD:
      input = valueAfter(block);
      
      if (bstmt != null && bstmt == stmt.next()) {
        function = bfunction;
        analysis.modelEffect(function, stmt);
      } else {
        function = analysis.newFlowFunction();
        for (Iterator i = block.bottomupStmts(); i.hasNext(); ) {
          Statement current = (Statement)i.next();
          analysis.modelEffect(function, current);
          if (current == stmt)
            break;
        }
      }

      bstmt = stmt;
      bfunction = function;

      break;
    default:
      throw new IllegalArgumentException("Illegal direction");
    }
    if (input == null)
      return null;
    return function.apply(input);
  }

  private transient Statement astmt;
  private transient FlowFunction afunction;
  public final FlowItem valueAfter(Statement stmt) {
    BasicBlock block = stmt.ownerBB();
    if (block == null)
      throw new IllegalArgumentException("Statement not associated to a basic block");

    if (stmt == block.trailer())
      return valueAfter(block);

    FlowItem input;
    FlowFunction function;
    switch (analysis.direction()) {
    case DataFlowAnalysis.FORWARD:
      input = valueBefore(block);

      if (astmt != null && astmt == stmt.previous()) {
        function = afunction;
        analysis.modelEffect(function, stmt);
      } else {
        function = analysis.newFlowFunction();
        for (Iterator i = block.topdownStmts(); i.hasNext(); ) {
          Statement current = (Statement)i.next();
          analysis.modelEffect(function, current);
          if (current == stmt)
            break;
        }
      }

      astmt = stmt;
      afunction = function;

      break;
    case DataFlowAnalysis.BACKWARD:
      input = valueAfter(block);

      if (astmt != null && astmt == stmt.next()) {
        function = afunction;
        analysis.modelEffect(function, stmt.next());
      } else {
        function = analysis.newFlowFunction();
        for (Iterator i = block.bottomupStmts(); i.hasNext(); ) {
          Statement current = (Statement)i.next();
          if (current == stmt)
            break;
          analysis.modelEffect(function, current);
        }
      }

      astmt = stmt;
      afunction = function;

      break;
    default:
      throw new IllegalArgumentException("Illegal direction");
    }
    if (input == null)
      return null;
    return function.apply(input);
  }

  public final FlowItem valueBefore(ControlEdge edge) {
    FlowItem input;
    switch (analysis.direction()) {
    case DataFlowAnalysis.FORWARD:
      input = valueAfter(edge.sourceBB());
      break;
    case DataFlowAnalysis.BACKWARD:
      FlowItem output = valueBefore(edge.targetBB());
      FlowFunction function = analysis.newFlowFunction();
      analysis.modelEffect(function, edge);
      input = function.apply(output);
      break;
    default:
      throw new IllegalArgumentException("Illegal direction");
    }
    return input;
  }

  public final FlowItem valueAfter(ControlEdge edge) {
    FlowItem input;
    switch (analysis.direction()) {
    case DataFlowAnalysis.FORWARD:
      FlowItem output = valueAfter(edge.sourceBB());
      FlowFunction function = analysis.newFlowFunction();
      analysis.modelEffect(function, edge);
      input = function.apply(output);
      break;
    case DataFlowAnalysis.BACKWARD:
      input = valueBefore(edge.targetBB());
      break;
    default:
      throw new IllegalArgumentException("Illegal direction");
    }
    return input;
  }

  public abstract FlowItem valueBefore(BasicBlock block);
  public abstract FlowItem valueAfter(BasicBlock block);

}

