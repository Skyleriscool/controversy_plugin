package utils.type;

import java.util.ArrayList;

/**
 * A tree type structure. Each node can have unlimited number of children and 
 * can obtain a reference to its parent.
 * 
 * @author Jiepu Jiang
 * @date Apr 21, 2013
 * @param <T> The data type stored in the node.
 */
public class TreeNode<T> {
	
	protected T data;
	protected TreeNode<T> parent;
	protected ArrayList<TreeNode<T>> children;
	
	/**
	 * Constructor.
	 */
	public TreeNode() {}
	
	/**
	 * Constructor with the stored data.
	 * 
	 * @param data
	 */
	public TreeNode( T data ) {
		setData( data );
	}
	
	/**
	 * @return
	 */
	public T getData() {
		return this.data;
	}
	
	/**
	 * Set the stored data and return a reference to itself.
	 * 
	 * @param data
	 * @return
	 */
	public TreeNode<T> setData( T data ) {
		this.data = data;
		return this;
	}
	
	/**
	 * @return
	 */
	public TreeNode<T> getParent() {
		return this.parent;
	}
	
	/**
	 * Set the parent node. Note that the parent node may have not 
	 * been linked with the current node as a child node.
	 * 
	 * @param parent
	 * @return
	 */
	public TreeNode<T> setParent( TreeNode<T> parent ) {
		this.parent = parent;
		return this;
	}
	
	/**
	 * @return
	 */
	public boolean isRoot() {
		if( this.parent==null ) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return
	 */
	public boolean isLeaf() {
		if( this.children==null || this.children.size()==0 ) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return
	 */
	public ArrayList<TreeNode<T>> getChildren() {
		return this.children;
	}
	
	/**
	 * Set the children. Note that the child nodes may have not been
	 * linked with the current node as a parent node.
	 * 
	 * @param children
	 * @return
	 */
	public TreeNode<T> setChildren( ArrayList<TreeNode<T>> children ) {
		this.children = children;
		return this;
	}
	
	/**
	 * @return
	 */
	public int numChild() {
		int num = 0;
		if( children!=null ) {
			num = children.size();
		}
		return num;
	}
	
	/**
	 * Get the child node at the specified position of the child node list.
	 * 
	 * @param ix
	 * @return
	 */
	public TreeNode<T> getChild( int ix ) {
		return this.children.get( ix );
	}
	
	/**
	 * Add a child node at the end of the current child node list.
	 * 
	 * @param child
	 * @return
	 */
	public TreeNode<T> addChild( TreeNode<T> child ) {
		if( this.children==null ) {
			this.children = new ArrayList<TreeNode<T>>();
		}
		this.children.add( child );
		return this;
	}
	
	/**
	 * Add a child node at the specified position of the child node list.
	 * 
	 * @param ix
	 * @param child
	 * @return
	 */
	public TreeNode<T> addChild( int ix, TreeNode<T> child ) {
		if( this.children==null ) {
			this.children = new ArrayList<TreeNode<T>>();
		}
		this.children.add( ix, child );
		return this;
	}
	
	/**
	 * Remove the child node at the specified position of the child node list.
	 * The removed child node will be returned.
	 * 
	 * @param ix
	 * @return
	 */
	public TreeNode<T> removeChild( int ix ) {
		return this.children.remove( ix );
	}
	
	/**
	 * NodeProcessor defines a method for processing a tree node.
	 * It can be used to define processing methods when traversing tree nodes.
	 */
	public interface NodeProcessor<T> {
		
		/**
		 * Defines how to process the node.
		 * 
		 * @param node
		 */
		public abstract void process( TreeNode<T> node );
		
	}
	
	/**
	 * Depth-first pre-order traversal for the tree starting at the current node 
	 * (i.e. traverse the current tree node and all its leaves).
	 * 
	 * @param processor							Define the method of processing each node at the traverse time.
	 */
	public void traverseDepthFirstPreOrder( NodeProcessor<T> processor ) {
		processor.process( this );
		if( children!=null ) {
			for( TreeNode<T> child:children ) {
				child.traverseDepthFirstPreOrder( processor );
			}
		}
	}
	
	/**
	 * Depth-first post-order traversal for the tree starting at the current node 
	 * (i.e. traverse the current tree node and all its leaves).
	 * 
	 * @param processor							Define the method of processing each node at the traverse time.
	 */
	public void traverseDepthFirstPostOrder( NodeProcessor<T> processor ) {
		if( children!=null ) {
			for( TreeNode<T> child:children ) {
				child.traverseDepthFirstPostOrder( processor );
			}
		}
		processor.process( this );
	}
	
	/**
	 * Breadth-first traversal for the tree starting at the current node 
	 * (i.e. traverse the current tree node and all its leaves).
	 * 
	 * @param processor							Define the method of processing each node at the traverse time.
	 */
	public void traverseBreadthFirst( NodeProcessor<T> processor ) {
		traverseBreadthFirst( true, processor );
	}
	
	private void traverseBreadthFirst( boolean proc_self, NodeProcessor<T> proc ) {
		if( proc_self ) {
			proc.process( this );
		}
		if( children!=null ) {
			for( TreeNode<T> child:children ) {
				proc.process( child );
			}
			for( TreeNode<T> child:children ) {
				child.traverseBreadthFirst( false, proc );
			}
		}
	}
	
	
	/**
	 * Get the depth of the current node.
	 * It is implemented by recursively trying to visit the parent.
	 * 
	 * @return
	 */
	public int depth() {
		int depth = recurseDepth(parent, 0);
		return depth;
	}
	
	private int recurseDepth( TreeNode<T> node, int depth ) {
		if (node == null) {
			return depth;
		} else {
			return recurseDepth( node.parent, depth + 1 );
		}
	}
	
}
