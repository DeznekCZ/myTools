package cz.deznekcz.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Class extends an {@link ArrayList} class.
 * This class changes only method {@link #indexOf(Object)}.
 * In default {@link ArrayList} is index searched by 
 * <code>object.equals(ELEMENT[i])</code>,
 * but this type of {@link List} uses 
 * <code>ELEMENT[i].equalsTo(object)</code>. That method
 * must be implemented in each class stored in {@link EqualArrayList}
 * 
 * @author Zdeněk Novotný (DeznekCZ)
 * @version 1.0.1
 *
 * @param <T> Element class implements {@link EqualAble}
 * 
 * @see ArrayList
 * @see List
 * @see #indexOf(Object)
 * @see #lastIndexOf(Object)
 */
public class EqualArrayList<T extends EqualAble> 
extends ArrayList<T>
implements EqualAble {

	/**
	 * <b>Overrode from {@link ArrayList}</b>
	 */
    private static final long serialVersionUID = 8683452581122892189L;

	/**
	 * <b>Overrode from {@link ArrayList}</b><br>
     * Constructs an empty list with an initial capacity of ten.
     */
	public EqualArrayList() {
		super();
	}

	/**
	 * <b>Overrode from {@link ArrayList}</b><br>
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
	public EqualArrayList(Collection<? extends T> c) {
		super(c);
	}

	/**
	 * <b>Overrode from {@link ArrayList}</b><br>
     * Constructs an empty list with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    public EqualArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Method returns first index of element {@link T} in {@link EqualArrayList}.
	 * <br>{@link EqualAble} inteface implemeted to {@link T} allow
	 * send more type of objects.
	 * 
	 * @param object instance of any {@link Object} to compare
	 * @see #lastIndexOf(Object)
	 * @see #contains(Object)
	 */
	public int indexOf(Object object) {
		if (object == null) {
	        for (int i = 0; i < size(); i++)
	            if (get(i)==null)
	                return i;
	    } else {
			for (int i = 0; i < size(); i++)
				if (get(i).equalsTo(object))
					return i;
	    }
		return -1;
	}
	
	/**
	 * Method returns last index of element {@link T} in {@link EqualArrayList}.
	 * <br>{@link EqualAble} inteface implemeted to {@link T} allow
	 * send more type of objects.
	 * 
	 * @param object instance of any {@link Object} to compare
	 * @see #indexOf(Object)
	 * @see #contains(Object)
	 */
	public int lastIndexOf(Object object) {
		if (object == null) {
			for (int i = size()-1; i >= 0; i--)
                if (get(i)==null)
                    return i;
        } else {
        	for (int i = size() - 1; i > -1; i++)
				if (get(i).equalsTo(object))
					return i;
        }
		return -1;
	}
	
	/**
	 * Method returns true, if {@link #indexOf(Object)} returns {@link Integer}
	 * greater than <code>-1</code>.
	 * 
	 * @param object instance of any {@link Object} to compare
	 * @see #indexOf(Object)
	 * @see #lastIndexOf(Object)
	 */
	public boolean contains(Object object) {
		return indexOf(object) >= 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equalsTo(Object obj) {
		return obj == this || (obj != null && (
				ITryDo.checkValue(()->(Object[]) obj) == null
			?		equalsTo((Object[]) obj)
			:	ITryDo.checkValue(()->(T[]) obj) == null
			?		equalsTo((T[]) obj) 
			:	ITryDo.checkValue(()->(Collection<T>) obj) == null
			?		equalsTo((Collection<T>) obj)
			:	ITryDo.checkValue(()->(List<T>) obj) == null
			?		equalsTo((List<T>) obj)
			:	ITryDo.checkValue(()->(ArrayList<T>) obj) == null
			?		equalsTo((List<T>) obj)
			:	ITryDo.checkValue(()->(EqualArrayList<T>) obj) == null
			?		equalsTo((List<T>) obj)
			:	false
				));
	}
	
	public boolean equalsTo(Object[] o) {
		return super.equals(Arrays.asList(o));
	}
	
	public boolean equalsTo(T[] o) {
		return super.equals(Arrays.asList(o));
	}
	
	public boolean equalsTo(Collection<T> o) {
		return equalsTo(o.toArray());
	}
	
	public boolean equalsTo(List<T> o) {
		return super.equals(o);
	}
	
	public boolean equalsTo(ArrayList<T> o) {
		return super.equals(o);
	}
	
	public boolean equalsTo(EqualArrayList<T> o) {
		return super.equals(o);
	}

	public T getEquals(Object searchObject) {
		return get(indexOf(searchObject));
	}
}
