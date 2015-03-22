package cz.deznekcz.util;

/**
 * An example class for {@link EqualArrayList}
 * 
 * @author Zdeněk Novotný (DeznekCZ)
 * @version 0.0.3
 */
public class EAL_Integer {
	
	private int value;

	/**
	 * Integer that equals to 0 or "0";
	 */
	public EAL_Integer() {
		this.value = 0;
	}
	
	/**
	 * Integer from value
	 * @param i value
	 */
	public EAL_Integer(int i) {
		this.value = i;
	}
	
	/**
	 * Integer from parse able string
	 * @param i parse able string
	 */
	public EAL_Integer(String i) {
		this.value = Integer.parseInt(i);
	}
	
	/**
	 * Return true when obj is {@link Integer}, parse able {@link String},
	 * or equals to another instance of {@link EAL_Integer}
	 * value type of this integer equal to obj value
	 * @param obj instance of {@link Object}
	 */
	public boolean equals(Object obj) {
		//CONSTRUCT return (condition ? valtrue : valfalse);
		return (obj != null && /* NOT NULL and ... */(false
		
		// SAME CLASS
		||    (obj instanceof EAL_Integer ?
					equalsTo((EAL_Integer) obj)
				: false)
		// SAME VALUE OF INTEGER
		||    (obj instanceof Integer ?
					equalsToInteger((Integer) obj)
				: false)
		// SAME VALUE FROM PARSE ABLE STRING
		||    (obj instanceof String ?
					equalsToString((String) obj)
				: false)
		
		// END COMPARE
		) ? true : false);
	}

	/**
	 * Compares this instance to instance of parameter
	 * @param otherInstance {@link EAL_Integer} instance
	 * @return boolean result
	 */
	private boolean equalsTo(EAL_Integer otherInstance) {
		return otherInstance.value == value ;
	}

	/**
	 * Compares this instance to instance of parameter
	 * @param string {@link String} value
	 * @return boolean result
	 */
	private boolean equalsToString(String string) {
		return Integer.parseInt(string) == value;
	}

	/**
	 * Compares this instance to instance of parameter
	 * @param integer {@link Integer} value
	 * @return boolean result
	 */
	private boolean equalsToInteger(Integer integer) {
		return integer.intValue() == value;
	}
	
	
}
