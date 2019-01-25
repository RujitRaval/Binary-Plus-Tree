package bptree;

/**
 * The {@code BPlusTree} class implements B+-trees. Each {@code BPlusTree} stores its elements in the main memory (not
 * on disks) for simplicity.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 * @param <K>
 *            the type of keys
 * @param <P>
 *            the type of pointers
 */
public class BPlusTree<K extends Comparable<K>, P> {

	/**
	 * The maximum number of pointers that each {@code Node} of this {@code BPlusTree} can have.
	 */
	protected int degree;

	/**
	 * The root node of this {@code BPlusTree}.
	 */
	protected Node<K, P> root;

	/**
	 * Constructs a {@code BPlusTree}.
	 * 
	 * @param degree
	 *            the maximum number of pointers that each {@code Node} of this {@code BPlusTree} can have.
	 */
	public BPlusTree(int degree) {
		this.degree = degree;
	}

	/**
	 * Copy-constructs a {@code BPlusTree}.
	 * 
	 * @param tree
	 *            another {@code BPlusTree} to copy from
	 */
	public BPlusTree(BPlusTree<K, P> tree) {
		this.degree = tree.degree;
		if (tree.root instanceof LeafNode)
			this.root = new LeafNode<K, P>(null, (LeafNode<K, P>) tree.root);
		else
			this.root = new NonLeafNode<K, P>(null, (NonLeafNode<K, P>) tree.root);
	}

	/**
	 * Returns the degree of this {@code BPlusTree}.
	 * 
	 * @return the degree of this {@code BPlusTree}
	 */
	public int degree() {
		return degree;
	}

	/**
	 * Returns the root {@code Node} of this {@code BPlusTree}.
	 * 
	 * @return the root {@code Node} of this {@code BPlusTree}
	 */
	public Node<K, P> root() {
		return root;
	}

	/**
	 * Finds the {@code LeafNode} in this {@code BPlusTree} that must be responsible for the specified key.
	 * 
	 * @param k
	 *            the search key
	 * @return the {@code LeafNode} in this {@code BPlusTree} that must be responsible for the specified key
	 */
	public LeafNode<K, P> find(K k) {
		return root.find(k);
	}

	/**
	 * Inserts the specified key and the pointer into this {@code BPlusTree}.
	 * 
	 * @param k
	 *            the key to insert
	 * @param p
	 *            the pointer to insert
	 */
	public void insert(K k, P p) {
		LeafNode<K, P> l; // will eventually be set to the leaf node that should contain the specified key
		if (root == null) { // if the tree is empty
			l = new LeafNode<K, P>(degree); // create an empty leaf node
			root = l; // the new leaf node is also the root
		} else // if the tree is not empty
			l = find(k); // find the leaf node l that should contain the specified key
		if (l.contains(k, p)) // no duplicate key-pointer entries are allowed in the tree
			return;
		if (!l.isFull()) { // if leaf node l has room for the specified key
			l.insert(k, p); // insert the specified key and pointer into leaf node l
		} else { // if leaf node l is full and thus needs to be split
			LeafNode<K, P> t = new LeafNode<K, P>(degree + 1); // create a temporary leaf node t
			t.append(l, 0, degree - 2);// copy everything to temporary node t
			t.insert(k, p); // insert the key and pointer into temporary node t
			LeafNode<K, P> lp = new LeafNode<K, P>(degree); // create a new leaf node lp
			lp.setSuccessor(l.successor()); // chaining from lp to the next leaf node
			l.clear(); // clear leaf node l
			l.setSuccessor(lp); // chaining from leaf node l to leaf node lp
			int m = (int) Math.ceil(degree / 2.0); // compute the split point
			l.append(t, 0, m - 1); // copy the first half to leaf node l
			lp.append(t, m, degree - 1); // copy the second half to leaf node lp
			insertInParent(l, lp.key(0), lp); // use the first key of lp as the separating key
		}
	}

	/**
	 * Inserts pointers to the specified {@code Node}s into an appropriate parent {@code Node}.
	 * 
	 * @param n
	 *            a {@code Node}
	 * @param k
	 *            the key between the {@code Node}s
	 * @param np
	 *            a new {@code Node}
	 */
	void insertInParent(Node<K, P> n, K k, Node<K, P> np) {
		if (n == root) { // if n is the root of the tree
			root = new NonLeafNode<K, P>(degree, n, k, np); // create a new root node containing n, k, np
			return;
		}
		NonLeafNode<K, P> p = n.parent(); // find the parent p of n
		if (!p.isFull()) { // if parent node p has room for a new entry
			p.insertAfter(k, np, n); // insert k and np right after n
		} else { // if p is full and thus needs to be split
			NonLeafNode<K, P> t = new NonLeafNode<K, P>(degree + 1); // crate a temporary node
			t.copy(p, 0, p.keyCount()); // copy everything of p to the temporary node
			t.insertAfter(k, np, n); // insert k and np after n
			p.clear(); // clear p
			NonLeafNode<K, P> pp = new NonLeafNode<K, P>(degree); // create a new node pp
			int m = (int) Math.ceil(degree / 2.0); // compute the split point
			p.copy(t, 0, m - 1); // copy the first half to parent node p
			pp.copy(t, m, degree); // copy the second half to new node pp
			insertInParent(p, t.keys[m - 1], pp); // use the middle key as the separating key
		}
	}

	/**
	 * Removes the specified key and the pointer from this {@code BPlusTree}.
	 * 
	 * @param k
	 *            the key to delete
	 * @param p
	 *            the pointer to delete
	 */
	public void delete(K k, P p) {
		// please implement the body of this method
	}

}
