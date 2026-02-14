package com.mycompany.library_system.adt;

/**
 *
 * @author wh
 */
  public interface MapInterface<K, V> {
  /** Add or update a specific entry from the map.       
   *  @param key    
   *  @param value  
   *  @return the old value associated with the key if the key
   *          already exists; otherwise returns null 
   */
  public V put(K key, V value);

  /** Retrieve a specific entry from the map.       
   *  @param key      
   *  @return the value associated with the key;
   *          otherwise returns null 
   */
  public V get(K key);
  
  /** Delete a specific entry from the map.
   *  @param key
   *  @return the removed value that was associated with the key;
   *          otherwise returns null
   */
  public V remove(K key);

  /**
   * Returns the number of entrys in this map.
   * @return the number of entrys in this map 
   */
  public int size();
  
  /** Checks whether a specific key is in the map.
   *  @param key
   *  @return true or false if the map contains the key 
   */
  public boolean containsKey(K key);

  // /** Checks whether a specific value is in the map.
  //  *  @param value
  //  *  @return true or false if the map contains the value 
  //  */
  // public boolean containsValue(V value);
  
  /** Checks whether the map is empty.
   *  @return true if the map is empty 
   */
  public boolean isEmpty();

  /** Clear all entries from the map. 
  */
  public void clear();

  /**
   * Copies all of the mappings from the specified map to this map
   * @param m mappings to be stored in this map 
   */
  public void putAll(MapInterface<? extends K, ? extends V> m);

  
  /**
   * Returns a list of all values in the map, sorted by their keys.
   * @return a list of all entries in the map
  */
  public ListInterface<V> values();

  /**
   * Returns a list of all sorted keys in the map.
   * @return a list of all keys in the map
  */
  public ListInterface<K> keys();

  /** Returns a set of all entries in the map
   * @return a set of all entries in the map
   */
  public Entry<K, V>[] entrySet();
  

  interface Entry<K, V> {
    /** Retrieves the specifc key.
     *  @return the specifc key;
     *          otherwise returns null 
     */
    public K getKey();

    /** Retrieves the specifc value.
     *  @return the specifc value;
     *    otherwise returns null 
     */
    public V getValue();

    /** Set the new value associated with a given new value.
     *  @param value
     *  @return old value corresponding to the entry;
     *          otherwise returns null 
     */
    public V setValue(V value); 

    /** Compares the specified object with this entry for equality.
     *  @param o object to be compared for equality with this map entry
     *  @return true or false if the specified object is equal to this map 
     */
    public boolean equals(Object o);

    /** Generates a numeric hash code for this map entry.
     *  @return Integer value for a hash code of this entry 
     */
    public int hashCode();

  }
} // end MapInterface
