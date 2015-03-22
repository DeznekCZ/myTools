package cz.deznekcz.util;

import java.util.ArrayList;
import java.util.List;


/**
 * Class extends an {@link ArrayList} class.
 * This class changes only method {@link #indexOf(Object)}.
 * In default {@link ArrayList} is using <code>object.equals(ELEMENT[i])</code>,
 * but new method  is using <code>ELEMENT[i].equals(object)</code>
 * 
 * @author Zdeněk Novotný (DeznekCZ)
 * @version 0.0.1
 *
 * @param <T> Element class
 * 
 * @see ArrayList
 * @see List
 */
public class EqualArrayList<T> extends ArrayList<T> {
	
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Method returns first index of element {@link T} in {@link List}.
	 * <br>This change allow compare {@link T} to another object
	 * and object can be an instance of different class.
	 * <br>Example:<br>
	 * <code>
	 * boolean (new T).equals(object)<br>
	 * {<br>
	 * &nbsp;return object instanceof T<br> 
	 * &nbsp;&nbsp;&nbsp;&nbsp;&& ((T) object).methodOrParam == this.param ...<br>
	 * &nbsp;&nbsp;|| object instanceof DiffClass<br> 
	 * &nbsp;&nbsp;&nbsp;&nbsp;&& ...<br>
	 * }<br>
	 * </code>
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
				if (get(i).equals(object))
					return i;
	    }
		return -1;
	}
	
	/**
	 * Method returns last index of element {@link T} in {@link List}.
	 * <br>This change allow compare {@link T} to another object
	 * and object can be an instance of different class.
	 * <br>Example:<br>
	 * <code>
	 * boolean (new T).equals(object)<br>
	 * {<br>
	 * &nbsp;return object instanceof T<br> 
	 * &nbsp;&nbsp;&nbsp;&nbsp;&& ((T) object).methodOrParam == this.param ...<br>
	 * &nbsp;&nbsp;|| object instanceof DiffClass<br> 
	 * &nbsp;&nbsp;&nbsp;&nbsp;&& ...<br>
	 * }<br>
	 * </code>
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
				if (get(i).equals(object))
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
		return indexOf(object) > -1;
	}
}
