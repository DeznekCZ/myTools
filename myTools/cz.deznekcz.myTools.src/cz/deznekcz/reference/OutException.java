package cz.deznekcz.reference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @see #isExcepted()
 * @see #throwException()
 * @see #printStackTrace()
 * @see #get() get() for other acces to an Exception
 */
public class OutException extends Out<Exception> {

	@Deprecated
	public static OutException create() {
		return new OutException();
	}

	@SuppressWarnings("unchecked")
	public static OutException init() {
		return new OutException();
	}

	public static OutException init(Exception e) {
		return new OutException(e);
	}

	private OutException() {
		super(null);
	}

	private OutException(Exception e) {
		super(e);
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

	public void callListenersIfNotExcepded() {
		if (!isExcepted()) invokeChange(get(), get());
	}

	public void printStackTrace(PrintStream stream) {
		get().printStackTrace(stream);
	}

	public void printStackTrace(PrintWriter stream) {
		get().printStackTrace(stream);
	}

	@SuppressWarnings("unchecked")
	public <T extends Exception> Out<T> typed() {
		return new Out<T>(this == null ? null : (T) this.get()) {
			{
				onChange(OutException.this::set);
			}
		};
	}
}