package cz.deznekcz.reference;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import cz.deznekcz.reference.Out.OutString;
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
 * <br>{@link OutException}, {@link OutString}, {@link OutNumber}, 
 *     {@link OutByte}, {@link OutShort}, {@link OutInteger}, {@link OutLong}, 
 *     {@link OutFloat}, {@link OutDouble}
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
	 */
	public void set(C newValue, Predicate<C> conditionFunction)
	throws InvalidValueException {
		set(newValue, conditionFunction.test(newValue));
	}
	
	/**
	 * Method sets an onSet action of an Out reference, getting of action is not allowed.
	 * @param onSetAction instance of {@link Consumer} or lambda expression
	 */
	public void setOnSetAction(Consumer<C> onSetAction) {
		this.onSetAction = onSetAction;
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
	 * Initializer for simple reference by type.
	 * <br>Default value is null.
	 * @param <C> Class of stored instance
	 * @param defaultValue new stored instance
	 * @param action action on set new value
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> init(Consumer<C> onSetAction) {
		return init(null, onSetAction);
	}
	
	/**
	 * Initializer for simple reference by type.
	 * @param <C> Class of stored instance
	 * @param defaultValue new stored instance
	 * @param onSetAction action on set new value
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> init(C defaultValue, Consumer<C> onSetAction) {
		/* Overriding of default set method */
		if (onSetAction == null)
			throw new NullPointerException(NO_ACTION_EXCEPTION);
		Out<C> out = Out.init(defaultValue);
		out.setOnSetAction(onSetAction);
		return out;
	}
	
	/* BLOCK*********************************** *
	 * Usable declarations
	 * **************************************** */
	
	/** 
	 * @see #isExcepted()
	 * @see #throwException()
	 * @see #printStackTrace()
	 * @see #get() get() for other acces to an Exception
	 */
	public static class OutException extends Out<Exception> {
		public OutException() {
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
		
		public void printStackTrace() {
			get().printStackTrace();
		}
	}
	
	/**
	 * @see CharSequence
	 * @see Appendable
	 */
	public static class OutString extends Out<String> implements CharSequence, Appendable {
		private OutString(String string) {
			super(string == null ? "" : string);
		}

		@Override
		public void set() {
			// Empty String only, null reference can break functionality.
			super.set("");
		}
		
		@Override
		public void set(String value) {
			// Empty String only, null reference can break functionality.
			super.set(value == null ? "" : value);
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
		public OutString append(CharSequence csq) {
			if (csq != null)
				synchronized (this) {
					set(get().concat( 
							csq instanceof OutString 
							? OutString.copy((OutString) csq).get()
									// safer because it could be not null
							: csq.toString()));
				}
			return this;
		}

		@Override
		public OutString append(CharSequence csq, int start, int end) {
			return append(csq.subSequence(start, end));
		}

		@Override
		public OutString append(char c) {
			return append(Character.toString(c));
		}
		
		private static final String TO_STRING_FORMAT = "String reference: \"%s\"";
		
		/**
		 * Returns a string in format:
		 * <br><b>String reference: "referenced string"</b>
		 * @return instance of string
		 */
		@Override
		public String toString() {
			return String.format(OutString.TO_STRING_FORMAT, get());
		}
		
		/**
		 * Returns <b>true</b> if the length equals to "0"
		 * @return <b>true</b>/<b>false</b>
		 */
		public boolean isEmpty() {
			return super.isNull() || length() == 0;
		}

		/**
		 * Returns an empty reference.
		 * @return new instance of {@link OutString} with value ""
		 */
		public static OutString empty() {
			return new OutString("");
		}

		/**
		 * Copies value from {@link String} value.
		 * @param string instance of {@link String}
		 * @return new instance of {@link OutString}
		 */
		public static OutString from(String string) {
			return new OutString(string);
		}

		/**
		 * Copies value of another reference.
		 * @param stringOut another instance of {@link OutString}
		 * @return new instance of {@link OutString}
		 */
		public static OutString copy(OutString stringOut) {
			return new OutString(stringOut.get());
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static abstract class OutNumber<I extends Number> extends Out<I> {
		public OutNumber(I n) {
			super(n);
		}
		
		public abstract OutNumber<I> add(Number n);
		
		public abstract OutNumber<I> mul(Number n);
		
		@SuppressWarnings("unchecked")
		@Override
		public void set() {
			set((I) get().getClass().cast(0));
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static class OutByte extends OutNumber<Byte> {
		public OutByte(byte n) {
			super(n);
		}
		
		public synchronized OutByte add(Number n) {
			set((byte) (get() + n.byteValue()));
			return this;
		}
		
		public synchronized OutByte mul(Number n) {
			set((byte) (get() * n.byteValue()));
			return this;
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static class OutShort extends OutNumber<Short> {
		public OutShort(short n) {
			super(n);
		}
		
		public synchronized OutShort add(Number n) {
			set((short) (get() + n.intValue()));
			return this;
		}
		
		public synchronized OutShort mul(Number n) {
			set((short) (get() * n.intValue()));
			return this;
		}
	}
	
	/**
	 * @see Out
	 * @see #create() create(): stored value 0
	 * @see #create(int)
	 * @see #add(Number)
	 * @see #mul(Number)
	 * @see #div()
	 * @see #mod()
	 * @see #div(int,Out) div(int,IntOut modulo)
	 * @see #div(int,Out) mod(int,IntOut division)
	 * @see #increment()
	 * @see #decrement()
	 * @see #isEqual(int)
	 * @see #isLower(int)
	 * @see #isGreather(int)
	 * @see #isLowerAndEqual(int)
	 * @see #isGreatherAndEqual(int)
	 * @see #binary(Function)
	 * @see #binaryAND(int)
	 * @see #binaryNAND(int)
	 * @see #binaryOR(int)
	 * @see #binaryNOR(int)
	 * @see #binaryXOR(int)
	 */
	public static class OutInteger extends OutNumber<Integer> {
		public static OutInteger create() {
			return new OutInteger(0);
		}
		
		public static OutInteger create(int initial) {
			return new OutInteger(initial);
		}
		
		private OutInteger(int n) {
			super(n);
		}
		
		public synchronized OutInteger add(Number n) {
			set(get() + n.intValue());
			return this;
		}
		
		public synchronized OutInteger mul(Number n) {
			set(n instanceof Integer 
					?	get() * n.intValue()
					:	(int) (get() * n.doubleValue()));
			return this;
		}
		
		public synchronized OutInteger div(Number n) {
			set(n instanceof Integer 
					?	get() / n.intValue()
					:	(int) (get() / n.doubleValue()));
			return this;
		}
		
		public synchronized OutInteger mod(Number n) {
			set(get() % n.intValue());
			return this;
		}
		
		public synchronized OutInteger div(int divider, OutInteger modulo) {
			int divident = get();
			if (modulo != null) {
				modulo.set(divident % divider);
			}
			set(divident / divider);
			return this;
		}
		
		public synchronized OutInteger mod(int divider, OutInteger division) {
			int divident = get();
			if (division != null) {
				division.set(divident / divider);
			}
			set(divident % divider);
			return this;
		}

		public synchronized OutInteger increment() {
			set(get() + 1);
			return this;
		}

		public synchronized OutInteger decrement() {
			set(get() - 1);
			return this;
		}

		public synchronized boolean isEqual(int value) {
			return value == get();
		}

		public synchronized boolean isLower(int value) {
			return value > get();
		}

		public synchronized boolean isGreather(int value) {
			return value < get();
		}

		public synchronized boolean isLowerAndEqual(int value) {
			return value >= get();
		}

		public synchronized boolean isGreatherAndEqual(int value) {
			return value <= get();
		}
		
		public synchronized OutInteger binaryAND(int value) {
			set(get() & value);
			return this;
		}
		
		public synchronized OutInteger binaryOR(int value) {
			set(get() | value);
			return this;
		}
		
		public synchronized OutInteger binaryXOR(int value) {
			set(get() ^ value);
			return this;
		}
		
		public synchronized OutInteger binaryNAND(int value) {
			set(~(get() & value));
			return this;
		}
		
		public synchronized OutInteger binaryNOR(int value) {
			set(~(get() ^ value));
			return this;
		}
		
		public synchronized OutInteger binary(Function<Integer, Integer> applyFunction) {
			set(applyFunction.apply(get()));
			return this;
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static class OutLong extends OutNumber<Long> {
		public OutLong(long n) {
			super(n);
		}
		
		public synchronized OutLong add(Number n) {
			set(get() + n.longValue());
			return this;
		}
		
		public synchronized OutLong mul(Number n) {
			set(get() * n.longValue());
			return this;
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static class OutFloat extends OutNumber<Float> {
		public OutFloat(float n) {
			super(n);
		}
		
		public synchronized OutFloat add(Number n) {
			set(get() + n.floatValue());
			return this;
		}
		
		public synchronized OutFloat mul(Number n) {
			set(get() * n.floatValue());
			return this;
		}
	}
	
	/**
	 * @see #add(Number)
	 * @see #mul(Number)
	 */
	public static class OutDouble extends OutNumber<Double> {
		public OutDouble(double n) {
			super(n);
		}
		
		public synchronized OutDouble add(Number n) {
			set(get() + n.doubleValue());
			return this;
		}
		
		public synchronized OutDouble mul(Number n) {
			set(get() * n.doubleValue());
			return this;
		}
	}
}
