package cz.deznekcz.reference;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.function.Predicate;

/**
 *     Instance of class {@link Out} represent a returnable parameter
 *     known as "<code><b>out</b> variableName</code>" from <b>C#</b>
 *     or as "<code>&variable</code>" from <b>C</b> or <b>C++</b>.
 * <br>
 * <br>Now is declared new initializers for default types of java:
 * <br>Byte, Character, Short, Integer, Long, Double, Float, String and Object
 * <br>Using: <code>Out&lt;Character&gt; out = Out.init('d');</code>
 * <br>
 * <br><b>Using in code:</b><code>
 * <br>Out&lt;String&gt; out = Out.init("optional default string");
 * <br>method(out);
 * <br>String fromMethod = out.get();
 *     </code>
 * <br><b>Declared method:</b><code>
 * <br>void method(Out&lt;String&gt; out) {
 * <br>&nbsp;out.set("myValue");
 * <br>}
 * <br>
 * <br><b>Using in (old deprecated) code:</b><code>
 * <br>Out&lt;String&gt; out = new Out&lt;&gt;("optional default string");
 * <br>method(out);
 * <br>String fromMethod = out.value();
 *     </code>
 * <br><b>Declared method:</b><code>
 * <br>void method(Out&lt;String&gt; out) {
 * <br>&nbsp;out.lock("myValue");
 * <br>}
 *     </code>
 *
 * @author Zdenek Novotny (DeznekCZ)
 * @param <C> Class of stored instances
 * @version 2.1 (Fixed issue #2)
 */
public class Out<C> implements Comparable<Out<C>> {

	/**
	 * Functional interface used in method {@link Out#set(Object, Condition)}.
	 * Is replaceable with the lambda function.
	 * @author Zdenek Novotny (DeznekCZ)
	 *
	 * @param <C> Instance Class of checked instance
	 * @see InvalidValueException
	 * @see Out#set(Object, boolean)
	 * @see Out#set(Object, Condition)
	 */
	@FunctionalInterface
	public static interface Condition<C> {
		/**
		 * Returns ability to be set to reference
		 * @param value instance of new referenced value
		 * @return true/false
		 */
		public boolean check(C value);
	}
	
	/**
	 * While using condition to set a Out value, methods
	 * are throwing {@link InvalidValueException}.
	 * <br>Format of exception: "Invalid value: "<code>value.toString()</code>"" 
	 * @author Zdenek Novotny (DeznekCZ)
	 * @see Condition
	 * @see Out#set(Object, boolean)
	 * @see Out#set(Object, Condition)
	 */
	@SuppressWarnings("serial")
	public static class InvalidValueException extends Exception {
		
		/** Message formation */
		public static final String EXCEPTION_FORMAT = "Invalid value: \"%s\"";
		
		/**
		 * Constructor of {@link InvalidValueException} formats a message string
		 * from value 
		 * @param value excepting value
		 * @param <C> Class of excepting instance
		 */
		public <C> InvalidValueException(C value) {
			super(String.format(EXCEPTION_FORMAT, value.toString()));
		}
	}

	/** ToString formating */
	private static final String FORMAT = "Reference@%x: <%s>";
	/** No action exception */
	private static final String NO_ACTION_EXCEPTION = "No action is used";
	/** Referenced instance of {@link C} */
	private C value;

	/**
	 * Constructor references an external instance
	 * @param defaultValue instance of {@link C}
	 * @since Hidden since version 2
	 * @see Out#init(Object) 
	 */
	private Out(C defaultValue) {
		value = defaultValue;
	}
	
	/**
	 * Method stores an instance of {@link C}.
	 * @param newValue new stored value
	 * @see #set(Object, boolean)
	 * @see #set(Object, Condition)
	 * @see #get()
	 */
	public void set(C newValue) {
		value = newValue;
	}
	
	/**
	 * Method stores an instance of {@link C}.
	 * Condition parameter enables storing to reference.
	 * @param newValue new stored value
	 * @see #set(Object)
	 * @see #set(Object, Condition)
	 * @see #get()
	 */
	public void set(C newValue, boolean condition)
	throws InvalidValueException {
		if (condition) 
			set(newValue);
		else
			throw new InvalidValueException(newValue);
	}
	
	/**
	 * Method stores an instance of {@link C}.
	 * Condition parameter enables storing to reference.
	 * @param newValue new stored value
	 * @throws InvalidValueException 
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #get()
	 */
	public void set(C newValue, Condition<C> conditionFunction)
	throws InvalidValueException {
		set(newValue, conditionFunction.check(newValue));
	}
	
	/**
	 * Method returns an instance of {@link C}
	 * @return reference to instance
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #set(Object, Predicate)
	 */
	public C get() {
		return value;
	}
	
	/**
	 * Returns string value in format
	 */
	@Override
	public String toString() {
		return (  value == null
			? String.format(FORMAT, hashCode(), "null")
			: String.format(FORMAT, hashCode(), value.toString())
		);
	}
	
	/**
	 * Method returns true if instance of {@link C}
	 * stored in this instance of {@link Out} equals to
	 * instance stored in parameter instance of {@link Out} or
	 * instance of parameter equals to stored instance
	 * 
	 * @param obj instance of {@link Out} or {@link C}
	 * 
	 * @return <code>true</code> if instances equals else <code>false</code>
	 */
	public boolean equals(Object obj) {
		return  obj != null
			&&  value != null
			&&  (	(	obj instanceof Out<?>
					&&  ((Out<?>) obj).value != null
					&&  ((Out<?>) obj).value.equals(value)
				) || (	obj.equals(value)
				)
			);
	}

	/**
	 * Uses method of {@link C#compareTo(I)} to compare stored parameters.
	 * To compare values only use: 
	 * <br><code>firstOut.compareTo(Out.init("String"))</code>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Out<C> o) {
		return (value != null && o.value != null 
				? ((Comparable<C>)value).compareTo(o.value) 
				: (value == null && o.value == null ? 0 
						: value == null ? 1 : -1));
	}
	
	public static <C> int compare(Out<C> comparedReference, C comparedValue) {
		return -1*Out.init(comparedValue).compareTo(comparedReference);
	}

	/* **************************************** *
	 * Factory methods
	 * **************************************** */

	/**
	 * Initializer for simple reference by type.
	 * @param <C> Class of stored instance
	 * @param defaultValue new stored instance
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> init(C defaultValue) {
		return new Out<C>(defaultValue);
	}
	
	/**
	 * Initializer for simple reference by type.
	 * <br>Default value is null.
	 * @param <C> Class of stored instance
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> init() {
		return init((C) null);
	}
	
	/**
	 * Functional interface is usable to export variable
	 * without keeping of Out instance. It can bring problem
	 * with locality of exported value. Can be replaced
	 * with lambda function.
	 * @author Zdenek Novotny (DeznekCZ)
	 * @param <C> Class of stored instance
	 */
	@FunctionalInterface
	public static interface OnSetAction<C> {
		/**
		 * Brings new value into body of method {@link #onSet(Object)}
		 * @param newValue instance of {@link C}
		 */
		public void onSet(C newValue);
	}
	
	/**
	 * Initializer for simple reference by type.
	 * @param <C> Class of stored instance
	 * @param defaultValue new stored instance
	 * @param action action on set new value
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> init(C defaultValue, OnSetAction<C> action) {
		/* Overriding of default set method */
		if (action == null)
			throw new NullPointerException(NO_ACTION_EXCEPTION);
		return new Out<C>(defaultValue) {
			@Override
			public void set(C newValue) {
				super.set(newValue);
				action.onSet(newValue);
			}
		};
	}
	
	/**
	 * Initializer for simple reference by type.
	 * <br>Default value is null.
	 * @param <C> Class of stored instance
	 * @param defaultValue new stored instance
	 * @param action action on set new value
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> init(OnSetAction<C> action) {
		return init(null, action);
	}
	
	/* **************************************** *
	 * Deprecated methods
	 * **************************************** */

	/**
	 * Method sets an instance of {@link C}
	 * @param instance reference to instance
	 * @see #lock(Object, boolean)
	 * @see #lock(Object, Predicate)
	 * @see #set()
	 * @see #get()
	 */
	@Deprecated
	public void lock(C instance) {
		this.value = instance;
	}
	
	/**
	 * Method sets an instance of {@link C} 
	 * @param instance reference to instance
	 * @param predicate check for able to store
	 * @see #lock(Object)
	 * @see #lock(Object, Predicate)
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #set(Object, Predicate)
	 * @see #get()
	 */
	@Deprecated
	public void lock(C instance, boolean predicate) {
		if (predicate)
			this.value = instance;
		/* DUPLICITE CODE FOR FASTER EXECUTION */
	}
	
	/**
	 * Method sets an instance of {@link C} 
	 * @param instance reference to instance
	 * @param predicate check function for able to store
	 * @see #lock(Object)
	 * @see #lock(Object, boolean)
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #set(Object, Predicate)
	 * @see #get()
	 */
	@Deprecated
	public void lock(C instance, Predicate<C> predicate) {
		if (predicate.test(instance))
			this.value = instance;
		/* DUPLICITE CODE FOR FASTER EXECUTION */
	}

	/**
	 * Method returns an instance of {@link C}
	 * @return reference to instance
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #set(Object, Predicate)
	 * @see #get()
	 */
	@Deprecated
	public C value() {
		return value;
	}
	
	/**
	 * Method returns class of parameter {@link C}
	 * Method is not correctly implemented, 
	 * returns class of referencing instance.
	 * 
	 * @return instance of {@link Class}
	 */
	@Deprecated
	public Class<? extends Object> getParameterClass() {
		System.out.println(
				Arrays.toString(
						getClass()
						.getEnclosingClass()
						.getTypeParameters()));
		
		return getClass();
	}
}
