/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.dataflow;

import jewel.core.jiro.control.ControlEdge;
import jewel.core.jiro.control.Statement;

public interface DataFlowAnalysis {

  public static final byte FORWARD = 1;
  public static final byte BACKWARD = 2;

  public byte direction();
  public FlowItem newFlowItem();
  public FlowFunction newFlowFunction();
  public void modelEffect(FlowFunction function, Statement stmt);
  public void modelEffect(FlowFunction function, ControlEdge edge);
  public FlowItem merge(FlowItem one, FlowItem another);

  public interface FlowFunction {
  
    public FlowItem apply(FlowItem input);
    public Object clone();
    public int hashCode();
    public boolean equals(Object object);
    public String toString();
  
  }

  public interface FlowItem {
    
    public Object clone();
    public int hashCode();
    public boolean equals(Object object);
    public String toString();

  }

}

