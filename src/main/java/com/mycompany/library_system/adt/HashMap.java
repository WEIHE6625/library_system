package com.mycompany.library_system.adt;

/**
 *
 * @author wh
 */
// A simple generic HashMap class (K for Key, V for Value)
public class HashMap<K, V> implements MapInterface<K, V> {

    private static final int DEFAULT_CAPACITY = 16; // The default initial capacity (power of two).
    private static final float LOAD_FACTOR = 0.75f; // Used for rehashing.
    private HashNode<K, V>[] table;                 // The array of linked list
    private int size;


    // Act as the node of linked list
    private static class HashNode<K, V> implements MapInterface.Entry<K, V> {
        final K key;
        V value;
        HashNode<K, V> next; // Pointer to the next node

        HashNode(K key, V value, HashNode<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }
    }

    public HashMap() {
        this.table = (HashNode<K, V>[]) new HashNode[DEFAULT_CAPACITY];
        this.size = 0;
    }


    public int size() {
        return size;
    }


    // Hashing function to get hash value
    private int hash(Object key, int length) {
        if (key == null) return 0;
        // Get the raw hash code (normally ID)
        int hash = Math.abs(key.hashCode());
        // Convert to index using Bitwise AND
        return hash & (length - 1);
    }

    // Check if two keys are equal
    private boolean isKeyEqual(K k1, K k2) {
        if (k1 == k2) return true;
        else if (k1 == null || k2 == null) return false;
        else return k1.equals(k2);
    }


    // table size x2 and recalculate all nodes (When load factor exceeded)
    private void rehash() {
        int newCapacity = table.length * 2;
        // Initial new hashmap table
        HashNode<K, V>[] newTable = (HashNode<K, V>[]) new HashNode[newCapacity];

        for (int i = 0; i < table.length; i++) {
            HashNode<K, V> node = table[i];
            
            // Loop the linked list
            while (node != null) {
                HashNode<K, V> next = node.next; //Next pointer

                // Recalculate hash value
                int newIndex = hash(node.key, newCapacity);

                // Place into new table
                node.next = newTable[newIndex];
                newTable[newIndex] = node;

                node = next; // Move to next in old list
            }
        }
        table = newTable;
    }

    // If hash collision occurs, add node to the linked list at the index
    private void addNode(K key, V value, int nodeIndex) {
        // Check if load factor exceeded
        if (size >= table.length * LOAD_FACTOR) {
            rehash();
            // Recalculate index because table size changed
            nodeIndex = hash(key, table.length);
        }

        HashNode<K, V> oldNode = table[nodeIndex];
        // Create new node pointing to old node
        HashNode<K, V> newNode = new HashNode<K, V>(key, value, oldNode);
        table[nodeIndex] = newNode;
        size++;
    }



    @Override
    public V put(K key, V value) {
        // Compute Hash and Index
        int index = hash(key, table.length);

        // Try to find the key in the existing Linked List at this slot
        HashNode<K, V> current = table[index];
        while (current != null) {
            if (isKeyEqual(current.key, key)) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            current = current.next;
        }

        // If Key not found, add new node to the linked list.
        addNode(key, value, index);
        return null;
    }

    @Override
    public V get(K key) {
        int index = hash(key, table.length);

        HashNode<K, V> current = table[index];

        // Loop the Linked List
        while (current != null) {
            if (isKeyEqual(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }

        return null;
    }

    @Override
    public V remove(K key) {
        int index = hash(key, table.length);

        HashNode<K, V> current = table[index];
        HashNode<K, V> previous = null;

        while (current != null) {
            if (isKeyEqual(current.key, key)) {
                V oldValue = current.value;
                // If found, unlink it from list
                // Check if it is the first node
                if (previous == null) {
                    table[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return oldValue;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }


    @Override
    public boolean containsKey(K key) { 
        int index = hash(key, table.length);
        HashNode<K, V> current = table[index];

        // Loop the Linked List
        while (current != null) {
            if (isKeyEqual(current.key, key)) {
                return true;
            }
            current = current.next;
        }
        
        return false;
    }


    @Override
    public boolean containsValue(V value) {
        // Check every single slot in the table
        for (int i = 0; i < table.length; i++) {
            HashNode<K, V> node = table[i];
            
            // Check every node in this linked list
            while (node != null) {
                if (value == null) {
                    if (node.value == null) return true;
                } else {
                    if (value.equals(node.value)) return true;
                }
                node = node.next;
            }
        }
        return false;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public void clear() {
        this.table = (HashNode<K, V>[]) new HashNode[DEFAULT_CAPACITY];
        this.size = 0;
    }


    @Override
    public void putAll(MapInterface<? extends K, ? extends V> m) {
        if (m.isEmpty()) return;

        HashMap<? extends K, ? extends V> otherMap = (HashMap<? extends K, ? extends V>) m;
            
        // Iterate over the OTHER map's internal table
        for (int i = 0; i < otherMap.table.length; i++) {
            HashNode<? extends K, ? extends V> node = otherMap.table[i];
            
            while (node != null) {
                // Insert the key-value pair into THIS map
                this.put(node.key, node.value);
                node = node.next;
            }
        }
    }
}
