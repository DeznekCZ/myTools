package cz.deznekcz.reference;

import java.util.Arrays;

public class OutArray<C> extends Out<C[]> {

	private OutArray(C[] defaultValue) {
		super(defaultValue);
	}

	@SafeVarargs
	public static <C> OutArray<C> from(C... split) {
		return new OutArray<>(split);
	}

	/**
	 * Method casts simple {@link Out} reference to {@link OutArray}
	 * @param reference casted reference
	 * @param <C> Array element type
	 * @return new instance of {@link OutArray} or same if it is same implementation 
	 * if reference is null returns instance with null value 
	 */
	public static <C> OutArray<C> cast(Out<C[]> reference) {
		if (reference instanceof OutArray)
			return (OutArray<C>) reference;
		return new OutArray<C>(reference == null ? null : reference.get());
	}
	
	public int length() {
		return get() == null ? -1 : get().length;
	}
	
	/**
	 * 
	 * @param index
	 * @param value
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public void setAt(int index, C value) {
		synchronized (this) {
			if (index < length())
				get()[index] = value;
			else
				throw new ArrayIndexOutOfBoundsException("OutArray index exception: Length = "+length()+", index = "+index);
		}
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public C getAt(int index) {
		synchronized (this) {
			if (index < length())
				return get()[index];
			else
				throw new ArrayIndexOutOfBoundsException("OutArray index exception: Length = "+length()+", index = "+index);
		}
	}
	
	/**
	 * Method sorts referenced array value.
	 * <br>In cases: 
	 * <br>&nbsp;- null value,  
	 * <br>&nbsp;- length of array is 0,  
	 * <br>&nbsp;- instances of {@link C} not implement {@link Comparable}
	 * <br>throws {@link RuntimeException} with an error message
	 * @throws RuntimeException
	 */
	public void sort() {
		synchronized (this) {
			if (length() > 0) {
				if (get()[0] instanceof Comparable)
					Arrays.sort(get());
				else
					throw new RuntimeException("This reference is not sortable");
			} else {
				throw new RuntimeException("Not egnouth length to compare: "+length());
			}
		}
	}
}
