package cz.deznekcz.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import cz.deznekcz.reference.OutInteger;

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
 * @version 3.1 (Added non-break loop)
 */
public class ForEach {

	/**
	 * Method starts foreach.
	 * 
	 * <br>Example:
	 * <pre>
	 * <code>
	 * Iterable&lt;T> iterable = ... ;
	 * ForEach.&lt;T>(iterable, (T element) -&gt; {
	 * 	do some...
	 * });
	 * </pre>
	 * 
	 * @param iterable instance of {@link Iterable}
	 * @param iteration lambda function of instance of {@link Consumer}&lt;{@link T}&gt;
	 * @param <T> Class of element object
	 */
	public static <T> void start(Iterable<T> iterable, Consumer<T> iteration) {
		Iterator<T> it = iterable.iterator();
		while(
				it.hasNext() 		// items exists
			)	iteration.accept(it.next());; // loop action
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
	 * @param iteration lambda function of instance of {@link Predicate}&lt;{@link T}&gt;
	 * @param <T> Class of element object
	 */
	public static <T> void start(Iterable<T> iterable, Predicate<T> iteration) {
		Iterator<T> it = iterable.iterator();
		while(
				it.hasNext() 		// items exists
			&&  iteration.test(it.next())		// normal use of Out.set(true) 
			)	; // loop action
	}

	/**
	 * 
	 * @param iterable instance of {@link Iterable}
	 * @param threadCount
	 * @param iteration instance of {@link FunctionalInterface} 
	 * {@link Function}&lt;{@link T}&gt;&lt;{@link Boolean}&gt;
	 * @param <T> Class of element object
	 * @see #start(Iterable, Function)
	 */
	public static <T> void paralel(Iterable<T> iterable, int threadCount, Consumer<T> iteration) {
		ExecutorService exec = Executors.newFixedThreadPool(threadCount);
		OutInteger countOfRunning = OutInteger.create();
				
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
	 * <br><font color="red">!WARNING - May work not correctly while paralel foreach!
	 * External lock would be needed.</font>
	 * @param i lover value
	 * @param max upper value
	 * @param <T> Class of element object
	 * @return new instance of {@link Iterable}
	 */
	public static Iterable<Integer> intAscend(final int i, final int max) {
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
	 * Creates {@link Iterable} from an integer i to 0 (i-1 count)
	 * <br><font color="red">!WARNING - May work not correctly while paralel foreach!
	 * External lock would be needed.</font>
	 * @param i lover value
	 * @param max upper value
	 * @param <T> Class of element object
	 * @return new instance of {@link Iterable}
	 */
	public static Iterable<Integer> intDescend(final int i) {
		return new Iterable<Integer>() {
			@Override
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int iteration = i;

					@Override
					public boolean hasNext() {
						return iteration > 0;
					}

					@Override
					public Integer next() {
						return iteration--;
					}
				};
			}
		};
	}

	/**
	 * Creates {@link Iterable} from a {@link Random} values (max-1). 
	 * Predicate function tests final count of ramdom values.
	 * Example:
	 * <br>ForEach.random(new Random(), (count)-> count <= 5)
	 * <br>Means: loop stops after 5 cycles
	 * <br><font color="red">!WARNING - May work not correctly while paralel foreach!
	 * External lock would be needed.</font>
	 * @param r instance of {@link Random}
	 * @param predicate instance or lambda of {@link Predicate}
	 * @param <T> Class of element object
	 * @return new instance of {@link Iterable}
	 */
	public static Iterable<Random> random(Random r, Predicate<Integer> predicate) {
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
	 * @param <T> Class of element object
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

	/**
	 * Creates {@link Iterable} from an array
	 * @param tArray array or list of instances of {@link T}
	 * @param <T> Class of element object
	 * @return new instance of {@link Iterable}
	 */
	public static <T> Iterable<T> array(@SuppressWarnings("unchecked") T... tArray) {
		return Arrays.asList(tArray);
	}
	
	/**
	 * Returns count of elements in {@link Iterable}
	 * @param iterable instance of {@link Iterable}
	 * @return count of elements {@link T}
	 * @param <T> Class of element object
	 */
	public static <T> int count(Iterable<T> iterable) {
		OutInteger counter = OutInteger.create();
		ForEach.start(iterable, (v) -> {counter.increment();});
		return counter.get();
	}
}
