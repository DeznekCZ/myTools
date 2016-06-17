package cz.deznekcz.reference;

/** 
 * @see #isExcepted()
 * @see #throwException()
 * @see #printStackTrace()
 * @see #get() get() for other acces to an Exception
 */
public class OutException extends Out<Exception> {
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