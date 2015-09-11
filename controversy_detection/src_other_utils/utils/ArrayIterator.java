package utils;

import java.util.*;

/**
 * Iterator for an array.
 * 
 * @author Jiepu Jiang
 * @date Jun 3, 2013
 * @param <T> Type of the elements in the array.
 */
public class ArrayIterator<T> implements Iterator<T> {
	
	protected T[] array;
	protected int next;
	
	public ArrayIterator( T[] array ) {
		this.array = array;
		this.next = 0;
	}
	
	public boolean hasNext() {
		return next < array.length;
	}
	
	public T next() {
		T nextElem = array[ next ];
		next++;
		return nextElem;
	}
	
	public void remove() {
		// no need to implement
	}
	
}
