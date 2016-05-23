package cz.deznekcz.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.deznekcz.reference.Out;
import cz.deznekcz.reference.Out.IntegerOut;

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
 * @see #start(Iterable, Iteration) from version 2 #start(Iterable, Iteration)
 * @author Zdenek Novotny (DeznekCZ)
 * @version 2
 * @param <T> Type of iterable element
 */
public abstract class ForEach<T> {

	@Deprecated
	private final Iterable<T> iterable;
	@Deprecated
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
	@Deprecated
	public ForEach(Iterable<T> iterable) {
		this.iterable = iterable;
		this.breaking = false;
		start();
	}
	
	/** Hidden method of execution (do a synchronized foreach loop) */
	@Deprecated
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
	@Deprecated
	public abstract void apply(T t);

	/**
	 * Ends execution of ForEach loop
	 */
	@Deprecated
	protected final void breakLoop() {
		breaking = true;
	}

	@FunctionalInterface
	public interface Concurent<T> {
		void loop(T value);
	}
	
	@FunctionalInterface
	public interface Iteration<T> {
		boolean loop(T value);
	}
	
	/**
	 * Method starts a breakable foreach.
	 * 
	 * <br>Example:
	 * <pre>
	 * <code>
	 * Iterable&lt;T> iterable = ... ;
	 * ForEach.&lt;T>(iterable, (T element) -&gt; {
	 * 	do some...
	 * 	if (needs break) {
	 * 		return false;
	 * 	} else if (needs continue) {
	 * 		return true;
	 * 	}
	 * });
	 * </pre>
	 * 
	 * @param iterable instance of {@link Iterable}
	 * @param iteration instance of {@link FunctionalInterface} {@link Iterator}
	 */
	public static <T> void start(Iterable<T> iterable, Iteration<T> iteration) {
		Iterator<T> it = iterable.iterator();
		while(
				it.hasNext() 		// items exists
			&&  iteration.loop(it.next())		// normal use of Out.set(true) 
			)	; // loop action
	}

	public static <T> void paralel(Iterable<T> iterable, int threadCount, Concurent<T> iteration) {
		ExecutorService exec = Executors.newFixedThreadPool(threadCount);
		IntegerOut countOfRunning = IntegerOut.create();
				
		Iterator<T> it = iterable.iterator();
		while(it.hasNext()) {
			countOfRunning.increment();
			exec.execute(new Runnable() {
				private T value = it.next();
				public void run() { 
					iteration.loop(value); 
					countOfRunning.decrement();  
				};
			});	// loop action
		}
		while (countOfRunning.isGreather(1));
	}

	public static Iterable<Integer> integer(final int i, final int max) {
		return new Iterable<Integer>() {
			@Override
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int iteration = i;
					final int limit = max;

					@Override
					public boolean hasNext() {
						return iteration < limit;
					}

					@Override
					public Integer next() {
						return iteration++;
					}
				};
			}
		};
	}

	public static <T> Iterable<T> enumeration(Enumeration<T> keys) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					@Override
					public boolean hasNext() {
						return keys.hasMoreElements();
					}

					@Override
					public T next() {
						return keys.nextElement();
					}
				};
			}
		};
	}
}
