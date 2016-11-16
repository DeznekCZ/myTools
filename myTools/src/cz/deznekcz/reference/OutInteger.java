package cz.deznekcz.reference;

import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.value.ObservableValue;

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
 * @see #isLess(int)
 * @see #isGreater(int)
 * @see #isLessOrEqual(int)
 * @see #isGreatherOrEqual(int)
 * @see #binary(Function)
 * @see #binaryAND(int)
 * @see #binaryNAND(int)
 * @see #binaryOR(int)
 * @see #binaryNOR(int)
 * @see #binaryXOR(int)
 */
public class OutInteger extends OutNumber<Integer> {
	/**
	 * Creates new reference to {@link Integer} with initial value <b>0</b>
	 * @return new instance of {@link OutInteger}
	 */
	public static OutInteger create() {
		return new OutInteger(0);
	}
	
	/**
	 * Creates new reference to {@link Integer} with <b>initial</b> value
	 * @param initial integer value
	 * @return new instance of {@link OutInteger}
	 */
	public static OutInteger create(int initial) {
		return new OutInteger(initial);
	}
	
	/**
	 * Hidden constructor of {@link OutInteger}
	 * @param initial integer value
	 */
	private OutInteger(int initial) {
		super(initial);
	}
	
	public synchronized OutInteger add(Number n) {
		set(get() + n.intValue());
		return this;
	}
	
	public synchronized OutInteger sub(Number n) {
		set(get() - n.intValue());
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

	public synchronized boolean isEqual(Integer value) {
		return value == get();
	}

	public synchronized boolean isLess(Integer value) {
		return value > get();
	}

	public synchronized boolean isGreater(Integer value) {
		return value < get();
	}

	public synchronized boolean isLessOrEqual(Integer value) {
		return value >= get();
	}

	public synchronized boolean isGreatherOrEqual(Integer value) {
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
	
	public synchronized OutInteger binaryNOT() {
		set(~get());
		return this;
	}
	
	public synchronized OutInteger binarySHIFT_LEFT(int value) {
		set(get() << value);
		return this;
	}
	
	public synchronized OutInteger binarySHIFT_RIGHT(int value) {
		set(get() >> value);
		return this;
	}
	
	public synchronized OutInteger binaryROTATE_LEFT(int value) {
		set(Integer.rotateLeft(get(), value));
		return this;
	}
	
	public synchronized OutInteger binaryROTATE_RIGHT(int value) {
		set(Integer.rotateRight(get(), value));
		return this;
	}
	
	/**
	 * Is usable for long binary actions in same moment
	 * <br><b>Using:</b>
	 * <br>outIntegerInstance.binary((Integer value)-&gt;return ~(value & 0x55)|(value & 0xaa)&lt;&lt;6);
	 * @param applyFunction delegate, lambda or instance of {@link Function}
	 * @return <b>bthis</b> instance
	 */
	public synchronized OutInteger binary(Function<Integer, Integer> applyFunction) {
		set(applyFunction.apply(get()));
		return this;
	}
}

