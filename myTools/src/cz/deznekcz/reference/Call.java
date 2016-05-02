package cz.deznekcz.reference;

/**
 * 
 * @author Zdenek Novotny (DeznekCZ)
 * @see #returnCall(Delegate, Out)
 * @see #voidCall(Delegate, Out)
 * @see Delegate
 */
public class Call {

	/**
	 * Simply functional interface used to return a value (like delegate).
	 * <br><b>Using as Lambda expression:</b><br><code>() -> {return function();}</code> <i>or</i> <code>() -> function()</code>
	 * <br><b>Using in instance:</b>
	 * <pre>new Return&lt;T&gt;() {
	 * 	&#64;Override
	 * 	public T get() {
	 * 		return aValue;
	 * 	}
	 * }</pre>
	 * @author Zdenek Novotny (DeznekCZ)
	 * @param <T> Returned type
	 */
	public interface Delegate<T> {
		/**
		 * 
		 * @return returned value from delegated function
		 * @throws Exception exception thrown from a method
		 */
		public T get() throws Exception;
	}

	/**
	 * Static method applies function like a delegate using as call by {@link Delegate}
	 * @param <T> returned Type
	 * @param returnFunction instance of {@link Delegate} or lambda
	 * @param exceptions aReferencing instance for thrown exception 
	 * @return returned value of function
	 * @see #voidCall(Delegate, Out)
	 */
	public static <T> T returnCall(Delegate<T> returnFunction, Out<Exception> exceptions) {
		try {
			return returnFunction.get();
		} catch (Exception e) {
			exceptions.set(e);
			return null;
		}
	}

	/**
	 * Static method applies function like a delegate using as call by {@link Delegate}
	 * @param returnFunction instance of {@link Delegate} or lambda
	 * @param exceptions aReferencing instance for thrown exception
	 * @see #returnCall(Delegate, Out)
	 */
	public static void voidCall(Delegate<Void> returnFunction, Out<Exception> exceptions) {
		try {
			returnFunction.get();
		} catch (Exception e) {
			exceptions.set(e);
		}
	}
}
