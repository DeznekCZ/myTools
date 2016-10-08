package cz.deznekcz.reference;

import java.util.Comparator;
import java.util.function.Predicate;

import cz.deznekcz.util.Builder;

/**
 * Enclosing implementation of {@link Out} for manipulation with strings
 * referenced from methods. Instances of <b>this</b> class implements
 * interfaces {@link Appendable} and {@link CharSequence}.
 * @see CharSequence
 * @see Appendable
 * @see Out
 */
public class OutString extends Out<String> implements CharSequence, Appendable {
	private OutString(String string) {
		super(string == null ? "" : string);
	}

	/**
	 * Value is set to empty string
	 * @see Out#set()
	 */
	@Override
	public void set() {
		// Empty String only, null reference can break functionality.
		super.set("");
	}
	
	/**
	 * Value is set to empty string if the argument contains <b>null</b>
	 * @see Out#set()
	 */
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
	public synchronized OutString subSequence(int start, int end) {
		set(get().substring(start, end));
		return this;
	}

	@Override
	public synchronized OutString append(CharSequence csq) {
		if (csq != null)
			set(get().concat( 
					csq instanceof OutString 
					? ((OutString) csq).get()
							// safer because it could be not null
					: csq.toString()));
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

	/**
	 * Copies value of another reference.
	 * @param stringOut another instance of {@link OutString}
	 * @return new instance of {@link OutString}
	 */
	public static OutString cast(Out<String> stringOut) {
		return Builder.create(new OutString(""))
				.setIf(	v -> stringOut != null, // Predicate
						v -> v.set(stringOut.get()))
				.build();
	}

	public synchronized OutString trim() {
		set(get().trim());
		return this;
	}
	
	/**
	 * Method tests stored string value 
	 * if the new sequence will be stored.
	 * @param test Testing lambda function, delegate or instance of {@link Predicate}
	 * @param sqc Instance of {@link CharSequence} (may be {@link OutString})
	 * @return Returns <b>true</b> if the value was append.
	 */
	public synchronized boolean appendIf(
			Predicate<String> test, 
			CharSequence sqc) 
	{
		if (test.test(get())) {
			append(sqc);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean equalsToString(String s) {
		return get().equals(s);
	}
	
	public boolean equalsToStringIgnoreCase(String s) {
		return get().equalsIgnoreCase(s);
	}
	
	public int compareToString(String s) {
		return get().compareTo(s);
	}
	
	public int compareToStringIgnoreCase(String s) {
		return get().compareToIgnoreCase(s);
	}
	
	public int compareToString(String s, Comparator<String> comparator) {
		return comparator.compare(get(), s);
	}

	public synchronized void format(Object... arguments) {
		set(String.format(get(), arguments));
	}
}


