package com.mycompany.library_system.adt;

/**
 *
 * @author weihe
 */
  public interface MapInterface<K, V> extends Iterable<MapInterface.Entry<K, V>> {
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
   * Returns the number of entries in this map.
   * @return the number of entries in this map 
   */
  public int size();
  
  /** Checks whether a specific key is in the map.
   *  @param key
   *  @return true or false if the map contains the key 
   */
  public boolean containsKey(K key);

  /** Retrieves a subtree of all values that fall inclusively within this range.
   *  @param min minimum value
   *  @param max maximum value
   *  @return A subtree of all values that fall inclusively within this range.
   */
  public MapInterface<K, V> getRange(K min, K max);

  /** Returns the entry with the maximum key in the tree (the right-most node).
   *  @return An entry of the maximum key.
   */
  public MapInterface.Entry<K, V> max();

  /** Returns the entry with the minimum key in the tree (the left-most node).
   *  @return An entry of the minimum key.
   */
  public MapInterface.Entry<K, V> min();
  
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

  interface Entry<K, V> {
    /** Retrieves the specific key.
     *  @return the specific key;
     *          otherwise returns null 
     */
    public K getKey();

    /** Retrieves the specific value.
     *  @return the specific value;
     *    otherwise returns null 
     */
    public V getValue();

    /** Set the new value associated with a given new value.
     *  @param value
     *  @return old value corresponding to the entry;
     *          otherwise returns null 
     */
    public V setValue(V value); 
  }
} // end MapInterface
