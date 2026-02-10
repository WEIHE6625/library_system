/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
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
    public T getEntry(int givenPosition);
    public int size();
    public boolean contains(T anEntry);
    public boolean isEmpty();
    public T get(int index);
    public void sort(java.util.Comparator<T> comparator);
    
}
