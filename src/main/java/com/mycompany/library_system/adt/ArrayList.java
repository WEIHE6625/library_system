package com.mycompany.library_system.adt;

/**
 *
 * @author wh
 */
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

public class ArrayList<T> implements ListInterface<T>, Serializable {

    private T[] array;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public ArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayList(int initialCapacity) {
        this.size = 0;
        array = (T[]) new Object[initialCapacity];
    }

    @Override
    public boolean add(T newEntry) {
        if (size == array.length) {
            increaseCapacityByOne();
        }
        array[size] = newEntry;
        size++;
        return true;
    }

    @Override
    public boolean add(int newPosition, T newEntry) {
        boolean isSuccessful = true;

        if ((newPosition >= 1) && (newPosition <= size + 1)) {
            if (size == array.length) {
                increaseCapacityByOne();
            }
            makeRoom(newPosition);
            array[newPosition - 1] = newEntry;
            size++;
        } else {
            isSuccessful = false;
        }

        return isSuccessful;
    }

    private void increaseCapacityByOne() {
        T[] oldArray = array;
        array = (T[]) new Object[oldArray.length*2];

        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
    }

    @Override
    public boolean remove(T entry, int numberOfCopies) {
        boolean isSuccessful = false;
        int countRemoved = 0;

        for (int i = 0; i < size && countRemoved < numberOfCopies; i++) {
            if (array[i].equals(entry)) {
                remove(i + 1); 
                countRemoved++;
                isSuccessful = true;
            }
        }

        return isSuccessful;
    }

    @Override
    public T remove(int removePosition) {
        T result = null;

        if ((removePosition >= 1) && (removePosition <= size)) {
            result = array[removePosition - 1];

            if (removePosition < size) {
                removeGap(removePosition);
            }

            size--;
        }

        return result;
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public boolean replace(int replacePosition, T newEntry) {
        boolean isSuccessful = true;

        if ((replacePosition >= 1) && (replacePosition <= size)) {
            array[replacePosition - 1] = newEntry;
        } else {
            isSuccessful = false;
        }

        return isSuccessful;
    }

    @Override
    public T getEntry(int givenPosition) {
        T result = null;
        if (givenPosition >= 1 && givenPosition <= size) {
            result = array[givenPosition - 1];

        }
        return result;

    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(T anEntry) {
        boolean found = false;
        for (int i = 0; !found && (i < size); i++) {
            if (anEntry.equals(array[i])) {
                found = true;
            }
        }
        return found;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        String outputStr = "";
        for (int i = 0; i < size; ++i) {
            outputStr += array[i] + "\n";
        }

        return outputStr;
    }

    private void makeRoom(int newPosition) {
        int newIndex = newPosition - 1;
        int lastIndex = size - 1;

        for (int i = lastIndex; i >= newIndex; i--) {
            array[i + 1] = array[i];
        }
    }

    private void removeGap(int givenPosition) {
        int removedIndex = givenPosition - 1;
        int lastIndex = size - 1;

        for (int i = removedIndex; i < lastIndex; i++) {
            array[i] = array[i + 1];
        }
    }

    public void sort(Comparator<T> comparator) {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (comparator.compare(array[j], array[j + 1]) > 0) {
                    T temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                return array[currentIndex++];
            }
        };
    }
    
}
