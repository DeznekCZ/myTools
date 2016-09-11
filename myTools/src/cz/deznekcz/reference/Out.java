package cz.deznekcz.reference;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import cz.deznekcz.reference.OutInteger;
import cz.deznekcz.reference.OutNumber;
import cz.deznekcz.reference.OutLong;
import cz.deznekcz.reference.OutShort;
import cz.deznekcz.reference.OutString;
import cz.deznekcz.reference.OutDouble;
import cz.deznekcz.reference.OutFloat;
import cz.deznekcz.reference.OutException;
import cz.deznekcz.util.EqualAble;

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
 *     </code>
 *     
 * <br>
 * <br><b>New usable declarations</b>
 * <br>{@link OutException}, {@link OutString}, {@link OutNumber}, 
 *     {@link OutByte}, {@link OutShort}, {@link OutInteger}, {@link OutLong}, 
 *     {@link OutFloat}, {@link OutDouble}
 *
 * @author Zdenek Novotny (DeznekCZ)
 * @param <C> Class of stored instances
 * @version 3.2 (method sorting, #getParameterClass() fixed)
 */
public class Out<C> implements Comparable<Out<C>>, EqualAble, Supplier<C>, Predicate<C>, Function<C, Out<C>> {

	/* BLOCK*********************************** *
	 * Class declaration
	 * **************************************** */
	
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
	public static class InvalidValueException extends RuntimeException {
		
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

	/* BLOCK*********************************** *
	 * Instance handling
	 * **************************************** */
	
	/** ToString formating */
	private static final String TO_STRING_FORMAT = "Reference@%x: <%s>";
	/** No action exception */
	private static final String NO_ACTION_EXCEPTION = "No action is used";
	/** Referenced instance of {@link C} */
	private C value;
	/** On set action function */
	private Consumer<C> onSetAction;

	/**
	 * Constructor references an external instance
	 * @param defaultValue instance of {@link C}
	 * @since Hidden since version 2
	 * @see Out#init(Object) 
	 */
	protected Out(C defaultValue) {
		value = defaultValue;
	}
	
	/**
	 * Method returns an instance of {@link C}
	 * @return reference to instance
	 * @see #set()
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #set(Object, Predicate)
	 * @see #apply(Object)
	 */
	public C get() {
		return value;
	}
	
	/**
	 * Method returns class of referencing instance.
	 * If value of <b>this</b> reference is null, returns <b>null</b>
	 * 
	 * @return instance of {@link Class} or <b>null</b>
	 */
	public Class<? extends Object> getParameterClass() {
		return value != null ? value.getClass() : null;
	}

	/**
	 * Returns <b>true</b> if the stored value is <b>null</b>
	 * @return <b>true</b> / <b>false</b>
	 */
	public boolean isNull() {
		return get() == null;
	}

	/**
	 * Returns <b>false</b> if the stored value is <b>null</b>
	 * @return <b>true</b> / <b>false</b>
	 */
	public boolean isNotNull() {
		return !isNull();
	}
	
	/**
	 * Method stores a <b>null</b> value.
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #set(Object, Predicate)
	 * @see #get()
	 * @see #apply(Object)
	 */
	public void set() {
		set(null);
	}
	
	/**
	 * Method stores an instance of {@link C}.
	 * @param newValue new stored value
	 * @see #set()
	 * @see #set(Object, boolean)
	 * @see #set(Object, Predicate)
	 * @see #get()
	 * @see #apply(Object)
	 */
	public void set(C newValue) {
		value = newValue;
		if (onSetAction != null)
			onSetAction.accept(newValue);
	}
	
	/**
	 * Method stores an instance of {@link C}.
	 * Condition parameter enables storing to reference.
	 * @param newValue new stored value
	 * @param condition pre-counted value of condition (usable for ternary operators) 
	 * @throws InvalidValueException is thrown in case of bad value will be set
	 * @see #set()
	 * @see #set(Object)
	 * @see #set(Object, Predicate)
	 * @see #get()
	 * @see #apply(Object)
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
	 * @param conditionFunction instance or lambda of {@link Predicate} 
	 * @throws InvalidValueException is thrown in case of bad value will be set
	 * @see #set()
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #get()
	 * @see #apply(Object)
	 */
	public void set(C newValue, Predicate<C> conditionFunction)
	throws InvalidValueException {
		set(newValue, conditionFunction.test(newValue));
	}
	
	/**
	 * Method tests stored value versus tested value. In case they equals returns true;
	 * @param tested instance of {@link C}
	 * @return boolean value true/false
	 */
	public boolean test(C tested) {
		return (tested == null && tested == null) || (tested != null && tested.equals(get()));
	}
	
	/**
	 * Method sets value to this reference and returns the reference.
	 * @param value stored value
	 * @return this instance of {@link Out}
	 * @see #set()
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #set(Object, Predicate)
	 * @see #get()
	 */
	public Out<C> apply(C value) {
		set(value);
		return this;
	}
	
	/**
	 * Method sets an onSet action of an Out reference, getting of action is not allowed.
	 * @since 3.0 Is able to use as a build
	 * @param onSetAction instance of {@link Consumer} or lambda expression
	 */
	public Out<C> setOnSetAction(Consumer<C> onSetAction) {
		this.onSetAction = onSetAction;
		return this;
	}
	
	/**
	 * Returns new instance of {@link Out} with same reference.
	 * OnSetAction will be copied.
	 */
	@Override
	public Out<C> clone() {
		return new Out<C>(value){{setOnSetAction(onSetAction);}};
	}
	
	/* BLOCK*********************************** *
	 * Comparing and equaling
	 * **************************************** */
	
	/**
	 * Returns string value in format
	 */
	@Override
	public String toString() {
		return (  value == null
			? String.format(TO_STRING_FORMAT, hashCode(), "null")
			: String.format(TO_STRING_FORMAT, hashCode(), value.toString())
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
	 * Returns <b>true</b> if reference of <b>this</b> value equals to another reference
	 * or value of <b>this</b> reference equals to value
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equalsTo(Object obj) {
		try {
			return equalsTo((Out<C>) obj);
		} catch (ClassCastException outException) {
			return value instanceof EqualAble 
					? ((EqualAble) value).equalsTo(obj) 
					: value.equals(obj);
		}
	}
	
	/**
	 * Returns <b>true</b> if value of reference of <b>this</b> equals to value of another reference
	 * @param ref another reference
	 * @return <b>true</b> / <b>false</b>
	 */
	public boolean equalsTo(Out<C> ref) {
		return value != null && (
					value instanceof EqualAble
						?	((EqualAble) value).equalsTo(ref.value)
						:	value.equals(ref.value)
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
	
	/**
	 * Compare values of {@link Out} reference and a value
	 * @param <C> class of stored instances
	 * @param comparedReference instance of {@link Out}
	 * @param comparedValue instance of {@link C}
	 * @return returns value of {@link Out}&lt;{@link C}&gt;.{@link #get()}.{@link Comparable#compareTo(Object) compareTo}({@link C})
	 */
	public static <C> int compare(Out<C> comparedReference, C comparedValue) {
		return -1*Out.init(comparedValue).compareTo(comparedReference); // 
//		return ((Comparable<C>) comparedReference.value).compareTo(comparedValue);
	}

	/* BLOCK*********************************** *
	 * Factory methods
	 * **************************************** */
	
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
	 * Initializer for simple reference by type.
	 * @param <C> Class of stored instance
	 * @param defaultValue new stored instance
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> init(C defaultValue) {
		return new Out<C>(defaultValue);
	}
	
	/**
	 * Initializer for complicated references like filled arrays.
	 * @since 3.0
	 * @param valueGenerator instance or lambda of {@link Supplier}
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> init(Supplier<C> valueGenerator) {
		return init(valueGenerator.get());
	}
	
	/* BLOCK*********************************** *
	 * Usable declarations
	 * **************************************** */
	
	
}
