package cz.deznekcz.reference;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import cz.deznekcz.util.ForEach;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class OutBoolean extends Out<Boolean> {

	protected OutBoolean(boolean value) {
		super(value);
	}
	
	/**
	 * Returns new reference with default <b>true</b> value
	 * @return new instance of {@link OutBoolean}
	 */
	public static OutBoolean TRUE() {
		return new OutBoolean(true);
	}
	
	/**
	 * Returns new reference with default <b>false</b> value
	 * @return new instance of {@link OutBoolean}
	 */
	public static OutBoolean FALSE() {
		return new OutBoolean(false);
	}
	
	/**
	 * Returns new reference to boolean value stored in casted reference, 
	 * if the reference is null returns new reference with true value
	 * @param reference instance of {@link Out}
	 * @return new instance of {@link OutBoolean} or same instance if it is same implementation
	 */
	public static OutBoolean cast(Out<Boolean> reference) {
		if (reference instanceof OutBoolean)
			return (OutBoolean) reference;
		return new OutBoolean(reference == null || reference.get());
	}
	
	/**
	 * Returns new reference to boolean value
	 * @param value boolean value
	 * @return new instance of {@link OutBoolean}
	 */
	public static OutBoolean from(boolean value) {
		return new OutBoolean(value);
	}

	/**
	 * Returns actual <b>boolean</b> value
	 * @see #getNot()
	 * @return boolean value
	 */
	@Override
	public Boolean get() {
		return OutBoolean.super.get();
	}
	
	/**
	 * Returns inverted <b>boolean</b> value
	 * @see #get()
	 * @return boolean value
	 */
	public Boolean getNot() {
		return !get();
	}

	/**
	 * Sets value of reference to <b>true</b>
	 * @see #set()
	 * @see #setFalse()
	 */
	public void setTrue() {
		set(true);
	}

	/**
	 * Sets value of reference to <b>false</b>
	 * @see #set()
	 * @see #setTrue()
	 */
	public void setFalse() {
		set(false);
	}

	/**
	 * Boolean operation <b>"OR"</b>
	 * @param value second operand
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean or(boolean value) {
		set(get() || value);
		return this;
	}

	/**
	 * Boolean operation <b>"OR"</b>
	 * @param value second operand reference
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public OutBoolean or(OutBoolean value) {
		return or(value.get());
	}

	/**
	 * Boolean operation <b>"AND"</b>
	 * @param value second operand
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean and(boolean value) {
		set(get() && value);
		return this;
	}

	/**
	 * Boolean operation <b>"AND"</b>
	 * @param value second operand reference
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public OutBoolean and(OutBoolean value) {
		return and(value.get());
	}

	/**
	 * Boolean operation <b>"XOR"</b>
	 * @param value second operand
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean xor(boolean value) {
		set(get() ^ value);
		return this;
	}

	/**
	 * Boolean operation <b>"XOR"</b>
	 * @param value second operand reference
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public OutBoolean xor(OutBoolean value) {
		return xor(value.get());
	}

	/**
	 * Boolean operation <b>"NOR"</b>
	 * @param value second operand
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean nor(boolean value) {
		set(!(get() || value));
		return this;
	}

	/**
	 * Boolean operation <b>"NOR"</b>
	 * @param value second operand reference
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public OutBoolean nor(OutBoolean value) {
		return nor(value.get());
	}

	/**
	 * Boolean operation <b>"NAND"</b>
	 * @param value second operand
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean nand(boolean value) {
		set(!(get() && value));
		return this;
	}

	/**
	 * Boolean operation <b>"NAND"</b>
	 * @param value second operand reference
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public OutBoolean nand(OutBoolean value) {
		return nand(value.get());
	}

	/**
	 * Boolean operation <b>"EQUAL"</b>
	 * @param value second operand
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean equal(boolean value) {
		set(get() == value);
		return this;
	}

	/**
	 * Boolean operation <b>"EQUAL"</b>
	 * @param value second operand reference
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public OutBoolean equal(OutBoolean value) {
		return equal(value.get());
	}

	/**
	 * Boolean operation <b>"NON-EQUAL"</b>
	 * @param value second operand
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean nonEqual(boolean value) {
		set(get() != value);
		return this;
	}

	/**
	 * Boolean operation <b>"NON-EQUAL"</b>
	 * @param value second operand reference
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public OutBoolean nonEqual(OutBoolean value) {
		return nonEqual(value.get());
	}

	/**
	 * Boolean operation <b>"A-&gt;B"</b>
	 * @param value second operand
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean implyAB(boolean value) {
		set(!(get() && !value));
		return this;
	}

	/**
	 * Boolean operation <b>"A-&gt;B"</b>
	 * @param value second operand reference
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public OutBoolean implyAB(OutBoolean value) {
		return implyAB(value.get());
	}

	/**
	 * Boolean operation <b>"A&lt;-B"</b>
	 * @param value second operand
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean implyBA(boolean value) {
		set(!(value && !get()));
		return this;
	}

	/**
	 * Boolean operation <b>"A&lt;-B"</b>
	 * @param value second operand reference
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public OutBoolean implyBA(OutBoolean value) {
		return implyBA(value.get());
	}

	/**
	 * Boolean operation <b>"NOT"</b>
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean not() {
		set(!get());
		return this;
	}

	/**
	 * Boolean function
	 * @param f function apply-able to current value of reference
	 * @return <b>this</b> instance of {@link OutBoolean}
	 */
	public synchronized OutBoolean function(Function<Boolean,Boolean> f) {
		set(f.apply(get()));
		return this;
	}

	@SafeVarargs
	public static OutBoolean bindOr(ObservableValue<Boolean>... args) {
		Objects.requireNonNull(args);
		OutBoolean result = OutBoolean.FALSE();
		ChangeListener<Boolean> listener = (o, l, n) -> {
			OutBoolean presult = OutBoolean.FALSE();
			ForEach.start(ForEach.array(args), (arg) -> {
				presult.or(n);
				return presult.getNot();
			});
			result.set(presult.get());
		};
		for (ObservableValue<Boolean> arg : args) {
			arg.addListener(listener);
		}
		return result;
	}

	@SafeVarargs
	public static OutBoolean bindAnd(ObservableValue<Boolean>... args) {
		Objects.requireNonNull(args);
		OutBoolean result = OutBoolean.FALSE();
		ChangeListener<Boolean> listener = (o, l, n) -> {
			OutBoolean presult = OutBoolean.FALSE();
			ForEach.start(ForEach.array(args), (arg) -> {
				presult.and(n);
				return presult.getNot();
			});
			result.set(presult.get());
		};
		for (ObservableValue<Boolean> arg : args) {
			arg.addListener(listener);
		}
		return result;
	}

	public static OutBoolean bindNot(ObservableValue<Boolean> arg) {
		Objects.requireNonNull(arg);
		OutBoolean result = OutBoolean.FALSE();
		ChangeListener<Boolean> listener = (o, l, n) -> {
			result.set(l);
		};
		arg.addListener(listener);
		return result;
	}
	
	public static abstract class ALinkable extends OutBoolean implements ChangeListener<Boolean> {
		
		protected ALinkable() {
			super(false);
		}
		private ArrayList<ObservableValue<Boolean>> from = new ArrayList<>();
		
		public void addListenable(ObservableValue<Boolean> observable) {
			from.add(observable);
			observable.addListener(this);
		}
		public void removeListenable(ObservableValue<Boolean> observable) {
			from.remove(observable);
			observable.removeListener(this);
		}
		@Override
		public void changed(ObservableValue<? extends Boolean> o, Boolean l, Boolean n) {
			OutBoolean newValue = resetValue();
			Consumer<Boolean> action = getAction(newValue);
			from.forEach((obs) -> action.accept(obs.getValue()));
			this.set(newValue.get());
		}
		
		public abstract Consumer<Boolean> getAction(OutBoolean value);
		
		public abstract OutBoolean resetValue();
		
		public void stopListening() {
			from.forEach((obs) -> obs.removeListener(this));
			from.clear();
		}
	}

	public static ALinkable orBinding() {
		return new ALinkable() {

			@Override
			public Consumer<Boolean> getAction(OutBoolean value) {
				return value::or;
			}

			@Override
			public OutBoolean resetValue() {
				return OutBoolean.FALSE();
			}
		};
	}

	public static ALinkable andBinding() {
		return new ALinkable() {

			@Override
			public Consumer<Boolean> getAction(OutBoolean value) {
				return value::and;
			}

			@Override
			public OutBoolean resetValue() {
				return OutBoolean.TRUE();
			}
		};
	}
}