package cz.deznekcz.tool;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * RandomAccessList is simply implementation of real memory. 
 * This implementation includes some warnings:
 * <br>- the size of memory from OS must be respected
 * <br>- list is implemented by ARRAY, which is growing slow
 * <br>- list is growing only
 * @author Zdenek Novotny (DeznekCZ)
 *
 * @param <E>
 */
public class RandomAccessList<E> extends AbstractList<E> {

	@SuppressWarnings("serial")
	public static class MemoryException extends RuntimeException {
		public MemoryException(String message) {
			super(message);
		}

		public static MemoryException writeOrDeleteOnly(int index) {
			return new MemoryException("Memory is not rewritable, aborted at address \""+index+"\"");
		}

		public static MemoryException maxInteger(int index) {
			return new MemoryException(
					  "Illegal memory index, expected: 0 - \""
					+ (Integer.MAX_VALUE - 1)
					+ "\", entered: \""+index+"\"");
		}
	}

	private static final int DEFAULT_SIZE = 16;
	private Object[] elements;
	private int memorySize;
	private int count;
	private boolean onlyWritable;

	public RandomAccessList() {
		this(DEFAULT_SIZE);
	}
	
	public RandomAccessList(int memorySize) {
		this.onlyWritable = false;
		this.memorySize = memorySize;
		this.count = 0;
		this.elements = new Object[memorySize];
	}

	/**
	 * {@inheritDoc}
	 * @throws MemoryException exception is thrown while rewriting an address.
	 */
	@Override
	public synchronized void add(int index, E element) {
		if (index >= memorySize && index < Integer.MAX_VALUE)
			grow(index);
		else if (index >= Integer.MAX_VALUE)
			throw MemoryException.maxInteger(index);
		
		boolean wasSet = elements[index] != null;
		
		if (onlyWritable && wasSet && element != null)
			throw MemoryException.writeOrDeleteOnly(index);
			
		elements[index] = element;
		
		if (element == null && wasSet)
			count --;
		else if (element != null)
			count ++;
	}
	
	private void grow(int minimalSize) {
		while (minimalSize >= memorySize)
			memorySize += DEFAULT_SIZE;
		elements = Arrays.copyOf(elements, memorySize);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized E get(int index) {
		if (index >= memorySize)
			return null;
		else
			return (E) elements[index];
	}

	/**
	 * @return a length of allocated array
	 */
	@Override
	public int size() {
		return memorySize;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (c instanceof RandomAccessList) {
			@SuppressWarnings("unchecked")
			RandomAccessList<E> list = (RandomAccessList<E>) c;
			for (int i = 0; i < list.memorySize; i++) {
				if (list.get(i) != null) 
					add(i, list.get(i));
			}
			return true;
		} else {
	        return super.addAll(c);
		}
	}
	
	@Override
	public boolean add(E e) {
		int index = indexOf(null);
        
		if (index == -1)
			index = memorySize;
		
		add(index, e);
        return true;
	}
	
	@Override
	public E set(int index, E element) {
		E last = get(index);
		add(index, element);
		return last;
	}
	
	@Override
	public boolean remove(Object o) {
		return remove(indexOf(o)) != null;
	}
	
	@Override
	public E remove(int index) {
		E last = get(index);
		add(index, null);
		return last;
	}
	
	@Override
	public boolean isEmpty() {
		return count == 0;
	}

	public int count() {
		return count;
	}
	
	@Override
	public Object[] toArray() {
		Object[] storedElements = new Object[count];
		
		for (int i = 0, j = 0; i < elements.length; i++) {
			Object o = elements[i];
			if (o == null)
				continue;
			storedElements[j++] = o;
		}
		
		return storedElements;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		ArrayList<T> stored = new ArrayList<>(count);
		
		for (int i = 0; i < elements.length; i++) {
			Object o = elements[i];
			if (o == null)
				continue;
			stored.add((T) o);
		}
		
		return stored.toArray(a);
	}

	public void setOnlyWritable(boolean b) {
		this.onlyWritable = b;
	}
}
