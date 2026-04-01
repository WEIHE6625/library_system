package com.mycompany.library_system.adt;

/**
 *
 * @author wh
 */
public interface ListInterface<T> extends Iterable<T> {
    
    public boolean add(T newEntry);
    public boolean add(int newPosition,T newEntry);
    public boolean remove(T entry, int numberOfCopies);
    public T remove(int removePosition);
    public void clear();
    public boolean replace(int replacePosition,T newEntry);
    public int size();
    public boolean contains(T anEntry);
    public boolean isEmpty();
    public T get(int index);
    public ListInterface<T> unique();
    public void sort(java.util.Comparator<T> comparator);

}
