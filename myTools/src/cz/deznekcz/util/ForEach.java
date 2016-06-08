package cz.deznekcz.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import cz.deznekcz.reference.Out.IntegerOut;

/**
 * Provider class {@link ForEach} creates a new access to loop iterables.
 * <br>Now is available conversions for {@link Enumeration} -> {@link #enumeration(Enumeration)},
 * for {@link Random} -> {@link #random(Random, Predicate)} and for the sorted integers {@link #integer(int, int)}
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
 * My ForEach iterator (minimal version 1.8)
 * <br>
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
 * @see #start(Iterable, Function)
 * @see #paralel(Iterable, Consumer)
 * @author Zdenek Novotny (DeznekCZ)
 * @version 3 Removed instantiation, Only static methods
 */
public class ForEach {
	
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
	 * @param iteration instance of {@link FunctionalInterface} 
	 * {@link Function}&lt;{@link T}&gt;&lt;{@link Boolean}&gt;
	 * @param <T> Class of array object
	 */
	public static <T> void start(Iterable<T> iterable, Function<T,Boolean> iteration) {
		Iterator<T> it = iterable.iterator();
		while(
				it.hasNext() 		// items exists
			&&  iteration.apply(it.next())		// normal use of Out.set(true) 
			)	; // loop action
	}

	/**
	 * 
	 * @param iterable instance of {@link Iterable}
	 * @param threadCount
	 * @param iteration instance of {@link FunctionalInterface} 
	 * {@link Function}&lt;{@link T}&gt;&lt;{@link Boolean}&gt;
	 * @param <T> Class of array object
	 * @see #start(Iterable, Function)
	 */
	public static <T> void paralel(Iterable<T> iterable, int threadCount, Consumer<T> iteration) {
		ExecutorService exec = Executors.newFixedThreadPool(threadCount);
		IntegerOut countOfRunning = IntegerOut.create();
				
		Iterator<T> it = iterable.iterator();
		while(it.hasNext()) {
			T nextValue = it.next();
			countOfRunning.increment();
			exec.execute(new Runnable() {
				private T value = nextValue;
				public void run() { 
					iteration.accept(value); 
					countOfRunning.decrement();  
				};
			});	// loop action
		}
		while (countOfRunning.isGreather(1)) Thread.yield();
	}

	/**
	 * Creates {@link Iterable} from an integer i to integer (max-1)
	 * @param i lover value
	 * @param max upper value
	 * @return new instance of {@link Iterable}
	 */
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

	/**
	 * Creates {@link Iterable} from a {@link Random} values (max-1). 
	 * Predicate function tests final count of ramdom values.
	 * @param r instance of {@link Random}
	 * @param predicate instance or lambda of {@link Predicate}
	 * @return new instance of {@link Iterable}
	 */
	public static Iterable<Random> random(Random r, Predicate<Object> predicate) {
		return new Iterable<Random>() {
			@Override
			public Iterator<Random> iterator() {
				return new Iterator<Random>() {
					int count = 0;

					@Override
					public boolean hasNext() {
						return predicate.test(count);
					}

					@Override
					public Random next() {
						count++;
						return r;
					}
				};
			}
		};
	}

	/**
	 * Creates {@link Iterable} from an {@link Enumeration} 
	 * @param keys instance of {@link Enumeration}
	 * @return new instance of {@link Iterable}
	 */
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
