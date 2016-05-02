package cz.deznekcz.reference;

import java.util.function.Predicate;

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
 * <br>
 * <br><b>New usable declarations</b>
 * <br>{@link ExceptionOut}, {@link StringOut}, {@link NumberOut}, 
 *     {@link ByteOut}, {@link ShortOut}, {@link IntOut}, {@link LongOut}, 
 *     {@link FloatOut}, {@link DoubleOut}
 *
 * @author Zdenek Novotny (DeznekCZ)
 * @param <C> Class of stored instances
 * @version 2.5 (method sorting, #getParameterClass() fixed)
 */
public class Out<C> implements Comparable<Out<C>>, EqualAble {

	/* BLOCK*********************************** *
	 * Class declaration
	 * **************************************** */
	
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
	
	/**
	 * Functional interface is usable to export variable
	 * without calling {@link Out#get()}. Can be replaced
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

	/* BLOCK*********************************** *
	 * Instance handling
	 * **************************************** */
	
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
	 * Method returns an instance of {@link C}
	 * @return reference to instance
	 * @see #set()
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #set(Object, Predicate)
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
	 * Returns <b>null</b> if the stored value is <b>null</b>
	 * @return <b>true</b> / <b>false</b>
	 */
	public boolean isNull() {
		return value == null;
	}
	
	/**
	 * Method stores a <b>null</b> value.
	 * Usable for {@link OnSetAction} fast call.
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #set(Object, Condition)
	 * @see #get()
	 */
	public void set() {
		value = null;
	}
	
	/**
	 * Method stores an instance of {@link C}.
	 * @param newValue new stored value
	 * @see #set()
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
	 * @see #set()
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
	 * @see #set()
	 * @see #set(Object)
	 * @see #set(Object, boolean)
	 * @see #get()
	 */
	public void set(C newValue, Condition<C> conditionFunction)
	throws InvalidValueException {
		set(newValue, conditionFunction.check(newValue));
	}
	
	/**
	 * Returns new instance of {@link Out} with same reference.
	 * <br><font color="red">WARNING!</font>: 
	 * {@link OnSetAction} will be lost.
	 */
	@Override
	public Out<C> clone() {
		return new Out<C>(value);
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
	
	/* BLOCK*********************************** *
	 * Usable declarations
	 * **************************************** */
	
	/** 
	 * @see #isExcepted()
	 * @see #throwException()
	 */
	public static class ExceptionOut extends Out<Exception> {
		public ExceptionOut() {
			super(null);
		}
		
		public boolean isExcepted() {
			return get() != null;
		}
		
		public void throwException() throws Exception {
			if (isExcepted())
				throw get();
			else
				throw new NullPointerException("No exception to throw!");
		}
	}
	
	/**
	 * @see CharSequence
	 * @see Appendable
	 */
	public static class StringOut extends Out<String> implements CharSequence, Appendable {
		public StringOut() {
			super("");
		}

		@Override
		public int length() {
			return get().length();
		}

		@Override
		public char charAt(int index) {
			return get().charAt(index);
		}

		@Override
		public String subSequence(int start, int end) {
			return get().substring(start, end);
		}

		@Override
		public StringOut append(CharSequence csq) {
			set(get().concat( csq instanceof StringOut ? ((StringOut) csq).get() : csq.toString()));
			return this;
		}

		@Override
		public StringOut append(CharSequence csq, int start, int end) {
			return append(csq.subSequence(start, end));
		}

		@Override
		public StringOut append(char c) {
			set(get().concat(""+c));
			return this;
		}
		
		@Override
		public String toString() {
			return String.format("String reference: \"%s\"", get());
		}
		
		@Override
		public boolean isNull() {
			return super.isNull() || length() == 0;
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static abstract class NumberOut<I extends Number> extends Out<I> {
		public NumberOut(I n) {
			super(n);
		}
		
		public abstract NumberOut<I> add(Number n);
		
		public abstract NumberOut<I> mul(Number n);
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static class ByteOut extends NumberOut<Byte> {
		public ByteOut(byte n) {
			super(n);
		}
		
		public ByteOut add(Number n) {
			set((byte) (get() + n.byteValue()));
			return this;
		}
		
		public ByteOut mul(Number n) {
			set((byte) (get() * n.byteValue()));
			return this;
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static class ShortOut extends NumberOut<Short> {
		public ShortOut(short n) {
			super(n);
		}
		
		public ShortOut add(Number n) {
			set((short) (get() + n.intValue()));
			return this;
		}
		
		public ShortOut mul(Number n) {
			set((short) (get() * n.intValue()));
			return this;
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 * @see #increment()
	 * @see #decrement()
	 */
	public static class IntOut extends NumberOut<Integer> {
		public IntOut(int n) {
			super(n);
		}
		
		public IntOut add(Number n) {
			set(get() + n.intValue());
			return this;
		}
		
		public IntOut mul(Number n) {
			set(get() * n.intValue());
			return this;
		}

		public IntOut increment() {
			set(get() + 1);
			return this;
		}

		public IntOut decrement() {
			set(get() - 1);
			return this;
		}
		
		public boolean is(int n) {
			return n == get();
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static class LongOut extends NumberOut<Long> {
		public LongOut(long n) {
			super(n);
		}
		
		public LongOut add(Number n) {
			set(get() + n.longValue());
			return this;
		}
		
		public LongOut mul(Number n) {
			set(get() * n.longValue());
			return this;
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static class FloatOut extends NumberOut<Float> {
		public FloatOut(float n) {
			super(n);
		}
		
		public FloatOut add(Number n) {
			set(get() + n.floatValue());
			return this;
		}
		
		public FloatOut mul(Number n) {
			set(get() * n.floatValue());
			return this;
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static class DoubleOut extends NumberOut<Double> {
		public DoubleOut(double n) {
			super(n);
		}
		
		public DoubleOut add(Number n) {
			set(get() + n.doubleValue());
			return this;
		}
		
		public DoubleOut mul(Number n) {
			set(get() * n.doubleValue());
			return this;
		}
	}
	
	/* BLOCK*********************************** *
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
}
