package cz.deznekcz.reference;

import java.util.function.Function;

public class OutBoolean extends Out<Boolean> {
	private OutBoolean(boolean value) {
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
	 * @return new instance of {@link OutBoolean}
	 */
	public static OutBoolean cast(Out<Boolean> reference) {
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
		return super.get();
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
}