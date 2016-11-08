package cz.deznekcz.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
import cz.deznekcz.util.ForEach;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import sun.reflect.CallerSensitive;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import sun.security.jca.GetInstance.Instance;

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
 * @version 4.0 (fx.beans implemented)
 */
public class Out<C> implements Comparable<Out<C>>, EqualAble, Supplier<C>, Predicate<C>, Function<C, Out<C>>, ObservableValue<C> {

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
	/** ToString formating */
	private static final String LISTENERS = " Invalidation Listeners: %s, Change Listeners: %s";
	/** Referenced instance of {@link C} */
	private C value;

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
	 * @see #get()
	 */
	public C getValue() {
		return get();
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
		if (observable && newValue != value) {
			C lastValue = value;
			value = newValue;
			invokeChange(lastValue, newValue);
		} else {
			value = newValue;
		}
//		if (onSetAction != null)
//			onSetAction.accept(newValue);
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
	 * Returns new instance of {@link Out} with same reference.
	 * OnSetAction will be copied.
	 */
	@Override
	public Out<C> clone() {
		Out<C> clone = new Out<C>(value);
		if( this.isObservable() ) {
			this.changeList.forEach((obs) -> clone.addListener(obs));
			this.invalList.forEach((obs) -> clone.addListener(obs));
		}
		return clone;
	}

	/**
	 * Method converts current referenced value to an instance of {@link R}
	 * @param converter conversion function
	 * @param <R> Class of returned value
	 * @return instance of {@link R}
	 */
	public <R> R to(Function<C, R> converter) {
		return converter.apply(get());
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
		) + (observable ? String.format(LISTENERS, Arrays.toString(invalList.toArray()), Arrays.toString(changeList.toArray())) : "");
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
		return obj instanceof Out && value != null && value.getClass().isInstance(obj) ? equalsTo((Out<C>) obj) :
			(( obj instanceof EqualAble && value != null) ? ((EqualAble) value).equalsTo(obj) :
			(( value != null && value.equals(obj) ) || (value == null && obj == null))); 
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
	
	/* BLOCK*********************************** *
	 * Beans implementation
	 * **************************************** */
	
	/**
	 * Initializer for complicated references like filled arrays.
	 * @since 3.0
	 * @param valueGenerator instance or lambda of {@link Supplier}
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> init(Supplier<C> valueGenerator) {
		return init(valueGenerator.get());
	}
	
	private boolean observable = false;
	private List<InvalidationListener> invalList;
	private List<ChangeListener<? super C>> changeList;
	private ObservableValue<?> bean;
	private ChangeListener<?> beanListener;
	
	/**
	 * Allows instance to be observable ({@link ObservableValue})
	 * @since 4.0
	 * @return this instance of {@link Out}
	 */
	public synchronized Out<C> allowObservable() {
		if (observable) return this;
		invalList = new ArrayList<InvalidationListener>(1);
		changeList = new ArrayList<ChangeListener<? super C>>(1);
		observable = true;
		return this;
	}

	public synchronized boolean isObservable() {
		return observable;
	}
	
	@Override
	public synchronized void addListener(InvalidationListener listener) {
		if (!observable) allowObservable();
		invalList.add(listener);
	}

	@Override
	public synchronized void removeListener(InvalidationListener listener) {
		if (observable) {
			invalList.remove(listener);
		} else {
			throw new NotImplementedException();
		}
	}

	@Override
	public synchronized void addListener(ChangeListener<? super C> listener) {
		if (!observable) allowObservable();
		changeList.add(listener);
	}

	@Override
	public synchronized void removeListener(ChangeListener<? super C> listener) {
		if (observable) {
			changeList.remove(listener);
		} else {
			throw new NotImplementedException();
		}
	}
	
	/**
	 * 
	 * @param lastValue
	 * @param newValue
	 */
	protected final void invokeChange(C lastValue, C newValue) {
		invalList.forEach((listener) -> listener.invalidated(this));
		changeList.forEach((listener) -> listener.changed(this, lastValue, newValue));
	}
	
	public Out<C> listened(ChangeListener<? super C> listener) {
		Objects.requireNonNull(listener);
		this.addListener(listener);
		return this;
	}
	
	public Out<C> listened(InvalidationListener listener) {
		Objects.requireNonNull(listener);
		this.addListener(listener);
		return this;
	}
	
	public void previousIfNull(ObservableValue<? extends C> observable, C lastV, C newV) {
		if (newV == null)
			this.set(lastV);
	}
	
	/**
	 * This method allow check value only versus constant.
	 * <b>WARNING</b> only marked functions by annotation {@link PredictionAble} in {@link Out} implementations can be used.<br>
	 * Examples:<br>
	 * OutBoolean conditionOutBoolean = instanceOutInteger.bindCompared(instanceOutInteger::isLoverOrEqual, 3);
	 * OutBoolean conditionOutBoolean = instanceOutDouble.bindCompared(instanceOutDouble::isEqual, 5.);
	 * OutBoolean conditionOutBoolean = instanceOutString.bindCompared(instanceOutString::contains, "yes");
	 * @see #bindChecked(Predicate, Supplier)
	 * @param checkingFunction
	 * @param value versus checking value
	 * @return new instance of {@link OutBoolean}
	 */
	public OutBoolean bindChecked(Predicate<C> checkingFunction, C value) {
		OutBoolean transformed = OutBoolean.FALSE();
		ChangeListener<C> listener = (o, l, n) -> {
			transformed.set(checkingFunction.test(value));
		};
		this.addListener(listener);
		transformed.setBean(this, listener);
		return transformed;
	}

	/**
	 * This method allow to have variable constant to check.
	 * @see #bindChecked(Predicate, Object)
	 * @param checkingFunction
	 * @param value versus checking getter delegate, lambda or instance of {@link Supplier}
	 * @return new instance of {@link OutBoolean}
	 */
	public OutBoolean bindChecked(Predicate<C> checkingFunction, Supplier<C> value) {
		OutBoolean transformed = OutBoolean.FALSE();
		ChangeListener<C> listener = (o, l, n) -> {
			transformed.set(checkingFunction.test(value.get()));
		};
		this.addListener(listener);
		transformed.setBean(this, listener);
		return transformed;
	}

	/**
	 * Simple binding from javafx.beans
	 * @param observable
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> bind(ObservableValue<C> observable) {
		Out<C> out = Out.init(observable.getValue());
		ChangeListener<C> listener = (o,l,n) -> out.set(n);
		observable.addListener(listener);
		out.setBean(observable, listener);
		return out;
	}
	
	protected <O> void setBean(ObservableValue<O> bean, ChangeListener<O> beanListener) {
		this.bean = bean;
		this.beanListener = beanListener;
	}

	@SuppressWarnings("unchecked")
	public <O> void unbind() {
		((ObservableValue<O>) bean).removeListener((ChangeListener<O>) beanListener);
		bean = null;
		beanListener = null;
	}

	public Out<Object> objectReference() {
		Out<Object> ref = Out.init();
		ChangeListener<C> listener = (o, l, n) -> {
			ref.set(n);
		};
		this.addListener(listener);
		ref.setBean(this, listener);
		return ref;
	}
	
	@Override
	protected void finalize() throws Throwable {
		unbind();
		super.finalize();
	}
	
	/* BLOCK*********************************** *
	 * Usable declarations
	 * **************************************** */
	
	/**
	 * Raw reference to an instance of {@link C}
	 * @author Zdenek Novotny (DeznekCZ)
	 *
	 * @param <C> stored instance
	 */
	public static class Raw<C> {
		private C value = null;
		private Raw() {};
		public C get() { return value; }
		public void set(C value) { this.value = value; };
		public static <C> Raw<C> init() { return new Raw<>(); };
		public static <C> Raw<C> init(C initial) { Raw<C> raw = new Raw<>(); raw.value = initial; return raw; };
	}
}
