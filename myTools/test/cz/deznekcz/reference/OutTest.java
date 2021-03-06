package cz.deznekcz.reference;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import org.junit.Test;

import cz.deznekcz.reference.Out;
import cz.deznekcz.reference.Out.InvalidValueException;
import cz.deznekcz.reference.OutInteger;

public class OutTest {

	public <C> ArrayList<Out<C>> createList(
			@SuppressWarnings("unchecked") Out<C>... listReferences) {
		return new ArrayList<>(Arrays.asList(listReferences));
	}
	
	public <C> ArrayList<Out<C>> createList(
			@SuppressWarnings("unchecked") C... listReferences) {
		ArrayList<Out<C>> values = new ArrayList<>();
		for (C c : listReferences) {
			values.add(Out.init(c));
		}
		return values;
	}

	@Test
	public void testInit() {
		OutInteger count = OutInteger.create();
		
		boolean threads = true;
		
		if (threads) {
			Thread counter = new Thread(() -> {
				long lastTime = 0;
				long newTime = 0;
				int i = 0;
				
				while (true) {
					if ((newTime = System.currentTimeMillis()) > lastTime) {
						lastTime = newTime + 1000;
						synchronized (count) {
							System.out.println("Thread one: "+count.get());
							count.set(0);
						}
					} Thread.yield();
					if (i < 0) break;
				}
			}); counter.start();
			Executors.newCachedThreadPool().execute(() -> {
				while(true){
					count.increment();
				}
			});
		} else {
			long lastTime = 0;
			long newTime = 0;
			int i = 0;
			while (true) {
				count.increment();
				
				if ((newTime = System.currentTimeMillis()) > lastTime) {
					lastTime = newTime + 1000;
					synchronized (count) {
						System.out.println("Thread one: "+count.get());
						count.set(0);
					}
				}
				if (i < 0) break;
			}
		}
		
		Out<Integer> value = Out.init(5);
		assertEquals("Default value", 5, (int)value.get());
	}
	
	@Test
	public void testSet() {
		Out<String> value = Out.init("stringBefore");
		value.set("stringAfter");
		assertEquals("New value","stringAfter",value.get());
	}
	
	@Test
	public void testSetBooleanCondition() throws InvalidValueException {
		Out<Integer> value = Out.init();
		value.set(5, 5 < 10);
		assertEquals("New value",5,(int)value.get());
	}
	
	@Test
	public void testSetLambdaCondition() throws InvalidValueException {
		Out<Integer> value = Out.init();
		value.set(5, intValue -> {
			// (0 + 1) % 6 = 1; 
			// (1 + 2) % 6 = 3; 
			// (3 + 3) % 6 = 0; 
			// (0 + 4) % 6 = 4;
			int result = 0;
			for (int i = 0; i < intValue; i++) {
				result += i; 
				result %= 6;
			}
			return result != 0; 
		});
		assertEquals("New value",5,(int)value.get());
	}
	
	@Test
	public void testSetImplementCondition() throws InvalidValueException {
		Out<String> value = Out.init();
		value.set("stringAfter", new Predicate<String>() {
			@Override
			public boolean test(String value) {
				return value.contains("string");
			}
		});
		assertEquals("New value","stringAfter",value.get());
	}
	
	@Test(expected=Out.InvalidValueException.class)
	public void testSetBooleanConditionExcept() throws InvalidValueException {
		Out<Integer> value = Out.init();
		value.set(5, intValue -> {
			// (0 + 1) % 6 = 1; 
			// (1 + 2) % 6 = 3; 
			// (3 + 3) % 6 = 0; 
			// (0 + 4) % 6 = 4;
			int result = 0;
			for (int i = 0; i < intValue; i++) {
				result += i; 
				result %= 6;
			}
			return result == 0; 
		});
		assertEquals("New value",5,(int)value.get());
	}

	@Test
	public void testGet() { // TODO
		
	}
	
	@Test
	public void testSort() { // TODO
		List<Integer> numbers = new ArrayList<>();
		numbers.addAll(Arrays.asList(5,8,9,5,4,2,3,5,4,8,5));
		Integer[] sorted = numbers.stream().sorted().toArray(Integer[]::new);
		
		List<Out<Integer>> references = createList(5,8,9,5,4,2,3,5,4,8,5);
		@SuppressWarnings("unchecked")
		Out<Integer>[] sortedRef = references.stream().sorted().toArray(Out[]::new);
		
		for (int i = 0; i < sorted.length; i++) {
			assertEquals("Is not sorted correctly", sorted[i], sortedRef[i].get());
		}
	}
}
