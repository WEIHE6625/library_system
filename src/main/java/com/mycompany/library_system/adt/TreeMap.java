package com.mycompany.library_system.adt;

/**
 *
 * @author wh
 * 
 * A generic TreeMap class with red-black tree algorithm (K for Key *must be comparable, V for Value)
 */
public class TreeMap<K extends Comparable<K>, V> implements MapInterface<K, V> {

    private Node<K, V> root;
    private int size;

    public TreeMap() {
        this.root = null;
        this.size = 0;
    }

    // Red-Black Tree Node
    private static class Node<K, V> implements MapInterface.Entry<K, V> {
        K key;
        V value;
        Node<K, V> left = null;
        Node<K, V> right = null;
        Node<K, V> parent = null;
        boolean color = true; // true for Red, false for Black

        Node(K key, V value, boolean color, Node<K, V> parent) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.parent = parent;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
             // Basic implementation for Entry equality
             if (!(o instanceof MapInterface.Entry)) return false;
             MapInterface.Entry<?,?> e = (MapInterface.Entry<?,?>)o;
             return (key==null ? e.getKey()==null : key.equals(e.getKey())) &&
                    (value==null ? e.getValue()==null : value.equals(e.getValue()));
        }
    }


    // For Red-Black Tree to prevent null pointer exceptions
    private boolean colorOf(Node<K, V> p) {
        return (p == null ? false : p.color); // Null (Leaves) is always BLACK
    }

    private Node<K, V> parentOf(Node<K, V> p) {
        return (p == null ? null : p.parent);
    }

    private void setColor(Node<K, V> p, boolean c) {
        if (p != null) p.color = c;
    }

    private Node<K, V> leftOf(Node<K, V> p) {
        return (p == null) ? null : p.left;
    }

    private Node<K, V> rightOf(Node<K, V> p) {
        return (p == null) ? null : p.right;
    }


    @Override
    public V put(K key, V value) {
        if (key == null) return null;

        Node<K, V> t = root;
        
        // If entry is root
        if (t == null) {
            root = new Node<>(key, value, false, null); // Root is always black
            size = 1;
            return value;

        }
        else {
        // If entry is not root, find insertion point
            int cmp;
            Node<K, V> parent;

            do {
                parent = t;
                cmp = key.compareTo(t.key);
                if (cmp < 0) {
                    t = t.left;
                } else if (cmp > 0) {
                    t = t.right;
                } else {
                    // Key exists, update value
                    return t.setValue(value);
                }
            } while (t != null);

            // Create new node
            Node<K, V> e = new Node<>(key, value, true, parent);
            if (cmp < 0) {
                parent.left = e;
            } else {
                parent.right = e;
            }

            // Fix the Red-Black properties
            insert_fixup(e);
            size++;
            return value;
        }
    }

    @Override
    public V get(K key) {
        Node<K, V> p = getNode(key);
        return (p == null ? null : p.value);
    }

    public Node<K, V> getNode(K key) {
        if (key == null) return null;
        Node<K, V> p = root;
        while (p != null) {
            int cmp = key.compareTo(p.key);
            if (cmp < 0) p = p.left;
            else if (cmp > 0) p = p.right;
            else return p;
        }
        return null;
    }


    @Override
    public V remove(K key) {
        Node<K,V> z = getNode(key);
        if (z == null) return null;

        V oldValue = z.value;
        Node<K,V> x;
        Node<K,V> y = z; // y is temp node to prevent destroying tree structure
        boolean yOriginalColor = y.color;

        // If z has no left child
        if (z.left == null) {
            x = z.right;

            // Special case: if x is null and y (z) is black
            if (x == null && yOriginalColor == false) {
                 x = new Node<>(null, null, false, z.parent); // Black dummy node
                 transplant(z, x); // Put dummy in the tree
                 delete_fixup(x);
                 transplant(x, null);
                 size--;
                 return oldValue;
            }

            transplant(z, z.right);
        } 
        // If z has no right child
        else if (z.right == null) {
            x = z.left;
            transplant(z, z.left);
        } 
        // If z has two children
        else {

            // Find z's right child's minimum node
            Node<K,V> tmp = z.right;
            while (tmp.left != null) {
                tmp = tmp.left;
            }
            y = tmp;

            yOriginalColor = y.color;
            x = y.right;
            
            if (y.parent == z) {
                if (x != null) x.parent = y; // Special case if x is null
            } else {
                transplant(y, y.right);
                y.right = z.right;
                if (y.right != null) y.right.parent = y;
            }
            
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color; // y takes z's color
        }

        size--;
        
        // if violation occurred because the deleted node is black
        if (yOriginalColor == false) {
            delete_fixup(x);
        }
        
        return oldValue;
    }


    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean containsKey(K key) {
        return getNode(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        if (root == null) return false;
        // Simulate a queue
        Node<K,V>[] queue = new Node[this.size];
        int head = 0;
        int tail = 0;

        // Add root to queue
        queue[tail++] = root;

        while (head < tail) {
            Node<K,V> current = queue[head++];

            // Check if value matches
            if (value == null ? current.value == null : value.equals(current.value)) {
                return true;
            }

            // Add children to queue
            if (current.left != null) queue[tail++] = current.left;
            if (current.right != null) queue[tail++] = current.right;
        }

        return false;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        size = 0; root = null;
    }

    @Override
    public void putAll(MapInterface<? extends K, ? extends V> m) {
        if (m instanceof TreeMap) {
            TreeMap<? extends K, ? extends V> map = (TreeMap<? extends K, ? extends V>) m;
            // Recursive to put all nodes
            recursiveputAll(map.root);
        }
    }

    @Override
    public MapInterface.Entry<K, V>[] entrySet() {
        MapInterface.Entry<K, V>[] result = new MapInterface.Entry[size];
        // Recursive using In-Order
        fillEntryArray(root, result, 0);
        return result;
    }

    private int fillEntryArray(Node<K, V> node, MapInterface.Entry<K, V>[] array, int index) {
        if (node == null) return index;
        index = fillEntryArray(node.left, array, index);
        array[index++] = node;
        index = fillEntryArray(node.right, array, index);
        return index;
    }



    // Loop the new nodes to current tree
    private void recursiveputAll(Node<? extends K, ? extends V> node) {
        if (node == null) return;
        this.put(node.key, node.value); // Add current node
        recursiveputAll(node.left);     // Add left subtree
        recursiveputAll(node.right);    // Add right subtree
    }
    

    // Left Rotations for Red-Black Tree
    private void rotateLeft(Node<K, V> p) {
        if (p != null) {
            Node<K, V> r = p.right;
            p.right = r.left;
            if (r.left != null)
                r.left.parent = p;
            r.parent = p.parent;
            if (p.parent == null)
                root = r;
            else if (p.parent.left == p)
                p.parent.left = r;
            else
                p.parent.right = r;
            r.left = p;
            p.parent = r;
        }
    }

    // Right rotation for Red-Black Tree
    private void rotateRight(Node<K, V> p) {
        if (p != null) {
            Node<K, V> l = p.left;
            p.left = l.right;
            if (l.right != null) l.right.parent = p;
            l.parent = p.parent;
            if (p.parent == null) root = l;
            else if (p.parent.right == p) p.parent.right = l;
            else p.parent.left = l;
            l.right = p;
            p.parent = l;
        }
    }
    
    // Fix the Red-Black Tree properties after insertion.       
    private void insert_fixup(Node<K, V> x) {
        // Check violation
        while (x != null && x != root && x.parent.color == true) {
            
            // If parent is a LEFT child
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                Node<K, V> y = rightOf(parentOf(parentOf(x))); // Uncle
                
                // Uncle is RED
                if (colorOf(y) == true) {
                    setColor(parentOf(x), false);      // Parent -> BLACK
                    setColor(y, false);                // Uncle -> BLACK
                    setColor(parentOf(parentOf(x)), true); // Grandpa -> RED
                    x = parentOf(parentOf(x));         // Move up to Grandpa
                } 
                else {
                // Uncle is BLACK, x is Right Child
                    if (x == rightOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateLeft(x);
                    }
                    else {
                // Uncle is BLACK, x is Left Child
                        setColor(parentOf(x), false);
                        setColor(parentOf(parentOf(x)), true);
                        rotateRight(parentOf(parentOf(x)));
                    }
                }
            } 
            else { 
            // Parent is a RIGHT child
                Node<K, V> y = leftOf(parentOf(parentOf(x))); // Uncle
                
                // Uncle is RED
                if (colorOf(y) == true) {
                    setColor(parentOf(x), false);
                    setColor(y, false);
                    setColor(parentOf(parentOf(x)), true);
                    x = parentOf(parentOf(x));
                } 
                else {
                // Uncle is BLACK, x is Right Child
                    if (x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    else {
                // Uncle is BLACK, x is Left Child
                        setColor(parentOf(x), false);
                        setColor(parentOf(parentOf(x)), true);
                        rotateLeft(parentOf(parentOf(x)));
                    }
                }
            }
        }
        root.color = false; // Root must always be BLACK
    }


    // Replaces the subtree rooted at node u with the subtree rooted at node v.
    private void transplant(Node<K,V> u, Node<K,V> v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        
        if (v != null) {
            v.parent = u.parent;
        }
    }

    // Fix the Red-Black Tree properties after deletion. 
    private void delete_fixup(Node<K,V> x) {
        // While x is not root and x is BLACK
        while (x != root && colorOf(x) == false) {
            
            if (x == leftOf(parentOf(x))) {
                Node<K,V> w = rightOf(parentOf(x)); // Sibling w

                // Case 1: Sibling w is RED
                if (colorOf(w) == true) {
                    setColor(w, false);
                    setColor(parentOf(x), true);
                    rotateLeft(parentOf(x));
                    w = rightOf(parentOf(x));
                }

                // Case 2: Sibling w is BLACK and both children are BLACK
                if (colorOf(leftOf(w)) == false && colorOf(rightOf(w)) == false) {
                    setColor(w, true);
                    x = parentOf(x);
                } else {
                    // Case 3: Sibling w is BLACK, Left child RED, Right child BLACK
                    if (colorOf(rightOf(w)) == false) {
                        setColor(leftOf(w), false);
                        setColor(w, true);
                        rotateRight(w);
                        w = rightOf(parentOf(x));
                    }
                    
                    // Case 4: Sibling w is BLACK, Right child RED
                    setColor(w, colorOf(parentOf(x)));
                    setColor(parentOf(x), false);
                    setColor(rightOf(w), false);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            } else {
                // Symmetric code for when x is a RIGHT child
                Node<K,V> w = leftOf(parentOf(x));

                if (colorOf(w) == true) {
                    setColor(w, false);
                    setColor(parentOf(x), true);
                    rotateRight(parentOf(x));
                    w = leftOf(parentOf(x));
                }

                if (colorOf(rightOf(w)) == false && colorOf(leftOf(w)) == false) {
                    setColor(w, true);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(w)) == false) {
                        setColor(rightOf(w), false);
                        setColor(w, true);
                        rotateLeft(w);
                        w = leftOf(parentOf(x));
                    }
                    setColor(w, colorOf(parentOf(x)));
                    setColor(parentOf(x), false);
                    setColor(leftOf(w), false);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }
        
        setColor(x, false); // Ensure x is Black at the end
    }
}
