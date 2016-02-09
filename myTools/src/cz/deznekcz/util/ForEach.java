package cz.deznekcz.util;

/**
 * Abstract class removes a Java version mismatch between new Java 1.8 and OpenJDK.
 * <br>
 * JAVA 1.8
 * <br>
 * <pre>
 * {@code
 * 	Iterable<T> iterable = ... ;
 * 	iterable.forEach(new Consumer<T> {
 * 		public void accept(T t) {
 * 			// some action for element t
 * 		}
 * 	});
 * }
 * </pre>
 * <br>
 * My ForEach iterator (minimal version 1.7)
 * <br>
 * <pre>
 * {@code
 * 	Iterable<T> iterable = ... ;
 * 	new ForEach<T>(iterable) {
 * 		public void doOne(T t) {
 * 			// some action for element t
 * 		}
 * 	};
 * }
 * </pre>
 * @author Zdenek Novotny (DeznekCZ)
 *
 * @param <T> Type of iterable element
 */
public abstract class ForEach<T> {

	private final Iterable<T> iterable;
	private boolean breaking;

	/**
	 * Creates a foreach loop for iterable, using extending of <b>this</b> class.
	 * <br>Example:
	 * <pre>
	 * {@code
	 * 	Iterable<T> iterable = ... ;
	 * 	new ForEach<T>(iterable) {
	 * 		public void doOne(T t) {
	 * 			// some action for element t
	 * 		}
	 * 	};
	 * }
	 * </pre>
	 * @param iterable instance of an iterable collection or set
	 */
	public ForEach(Iterable<T> iterable) {
		this.iterable = iterable;
		this.breaking = false;
		start();
	}
	
	/** Hidden method of execution (do a synchronized foreach loop) */
	private final void start() {
		synchronized (iterable) {
			for (T t : iterable) {
				if (breaking) {
					break;
				} else {
					apply(t);
				}
			}
		}
	}

	/**
	 * Method defines an action applicable for each element 
	 * @param t element of foreach
	 */
	public abstract void apply(T t);

	/**
	 * Ends execution of ForEach loop
	 */
	protected final void breakLoop() {
		breaking = true;
	}
}
