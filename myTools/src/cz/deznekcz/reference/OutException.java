package cz.deznekcz.reference;

/** 
 * @see #isExcepted()
 * @see #throwException()
 * @see #printStackTrace()
 * @see #get() get() for other acces to an Exception
 */
public class OutException extends Out<Exception> {
	
	public static OutException create() {
		return new OutException();
	}
	
	private OutException() {
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

	public void callListenersIfNotExcepded() {
		if (!isExcepted()) invokeChange(get(), get());
	}
	
	@SuppressWarnings("unchecked")
	public static OutException init() {
		return new OutException();
	}
	
	public static OutException init(Exception e) {
		OutException oe = new OutException();
		oe.set(e);
		return oe;
	}
}