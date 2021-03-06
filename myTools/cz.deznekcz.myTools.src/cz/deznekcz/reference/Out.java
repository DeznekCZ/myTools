package cz.deznekcz.reference;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.jws.Oneway;

import com.sun.javafx.application.LauncherImpl;

import cz.deznekcz.util.EqualAble;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
	protected static final String TO_STRING_FORMAT = "Reference@%x: <%s> Invalidation Listeners: %s, Change Listeners: %s";
	
	protected static final HashMap<Class<?>, Object> NULL = new HashMap<>();
	
	/** Referenced instance of {@link C} */
	private C value;

	private Function<C, String> toStringConfiguration;
	
	/**
	 * Constructor references an external instance
	 * @param defaultValue instance of {@link C}
	 * @since Hidden since version 2
	 * @see Out#init(Object) 
	 */
	protected Out(C defaultValue) {
		this.set(defaultValue);
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
		Out<C> clone = init(get());
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
		return toStringConfiguration != null 
				? toStringConfiguration.apply(get()) 
				: String.format(TO_STRING_FORMAT, hashCode(), 
						value != null ? value.toString() : "null",
								invalList != null ? invalList.toString() : "none",
								changeList != null ? changeList.toString() : "none");
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
	 * @param <R> Final reference implementation !WARNING: Unchecked cast
	 * @return new instance of {@link Out}
	 */
	public static <C> Out<C> init() {
		return init((C) null);
	}

	/**
	 * Initializer for simple reference by type.
	 * @param <C> Class of stored instance
	 * @param <R> Final reference implementation !WARNING: Unchecked cast
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
//		listener.changed(this, value, value);
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
	protected synchronized final void invokeChange(C lastValue, C newValue) {
		invalList.forEach((listener) -> listener.invalidated(this));
		changeList.forEach((listener) -> listener.changed(this, lastValue, newValue));
	}
	
	@Deprecated
	public Out<C> listened(ChangeListener<? super C> listener) {
		Objects.requireNonNull(listener);
		this.addListener(listener);
		return this;
	}
	
	@Deprecated
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
		beanListener.changed(bean, bean.getValue(), bean.getValue());
		this.bean = bean;
		this.beanListener = beanListener;
	}

	@SuppressWarnings("unchecked")
	public <O> void unbind() {
		if (bean != null) {
			((ObservableValue<O>) bean).removeListener((ChangeListener<O>) beanListener);
			bean = null;
			beanListener = null;
		}
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
	
	public <O extends Out<N>, N> O bindTransform(Function<N, O> constructor, Function<C, N> tranformer) {
		O output = constructor.apply(tranformer.apply(get()));
		ChangeListener<C> beanListener = (o, l, n) -> output.set(tranformer.apply(n));
		this.addListener(beanListener);
		output.setBean(this, beanListener);
		return output;
	}

	public synchronized void fireChange() {
		if (isObservable()) {
			invalList.forEach((aList) -> aList.invalidated(this));
			changeList.forEach((aList) -> aList.changed(this,get(),get()));
		}
	}

	public Out<C> fxThread() {
		Out<C> ref = Out.init();
		ChangeListener<C> listener = (o, l, n) -> {
			if (Platform.isFxApplicationThread())
				ref.set(n);
			else
				Platform.runLater(() -> ref.set(n));
		};
		this.addListener(listener);
		ref.setBean(this, listener);
		return ref;
	}
	
	public void setToString(Function<C, String> toStringConfiguration) {
		this.toStringConfiguration = toStringConfiguration;
	}
	
	public Function<C, String> getToString() {
		return toStringConfiguration;
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

	public class Change implements ChangeListener<C> {
		private C lastValue;
		private C newValue;
		private Out<C> reference;
		private Consumer<Out<C>.Change> action;

		private Change(Consumer<Out<C>.Change> action) {
			this.action = action;
		}
		
		public Change(Out<C> observable, C oldValue, C newValue) {
			this.lastValue = oldValue;
			this.newValue = newValue;
			this.reference = observable;
		}

		public C getNewValue() {
			return newValue;
		}
		
		public C getLastValue() {
			return lastValue;
		}
		
		public boolean isFired() {
			return lastValue == newValue;
		}
		
		public Out<C> getReference() {
			return reference;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void changed(ObservableValue<? extends C> observable, C oldValue, C newValue) {
			this.action.accept(new Change((Out<C>) observable, oldValue, newValue));
		}
	}

	public Out<C> onChange(Consumer<C> setter) {
		addListener((o,l,n) -> setter.accept(n));
		return this;
	}
	
	public Out<C> onChangeComplex(Consumer<Change> applyFunction) {
		this.addListener(new Change(applyFunction));
		return this;
	}
}
