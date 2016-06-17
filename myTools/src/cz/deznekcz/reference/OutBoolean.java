package cz.deznekcz.reference;

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
}