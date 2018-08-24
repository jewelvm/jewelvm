/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core.jiro.beg;

/**
 * This interface should be implemented by nodes
 * used in the tree pattern matching with dynamic
 * programing algorithm.
 */
public interface TreeNode {

  public int op();
  public int arity();
  public TreeNode left();
  public void setLeft(TreeNode left);
  public TreeNode middle();
  public void setMiddle(TreeNode middle);
  public TreeNode right();
  public void setRight(TreeNode right);
  public boolean hasNext();
  public TreeNode next();

  public boolean equals(TreeNode node);
  public Object clone();

  public static abstract class LeafRef {

    protected final TreeNode node;

    protected LeafRef(TreeNode node) {
      if (node == null)
        throw new NullPointerException();
      this.node = node;
    }

    public abstract void set(TreeNode node);

  }

  public static final class LeftRef extends LeafRef {

    public LeftRef(TreeNode node) {
      super(node);
    }

    public void set(TreeNode left) {
      node.setLeft(left);
    }

  }

  public static final class MiddleRef extends LeafRef {

    public MiddleRef(TreeNode node) {
      super(node);
    }

    public void set(TreeNode middle) {
      node.setMiddle(middle);
    }

  }

  public static final class RightRef extends LeafRef {

    public RightRef(TreeNode node) {
      super(node);
    }

    public void set(TreeNode right) {
      node.setRight(right);
    }

  }

}

