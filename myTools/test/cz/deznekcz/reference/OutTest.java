package cz.deznekcz.reference;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

import org.junit.Test;

import cz.deznekcz.reference.Out;
import cz.deznekcz.reference.Out.InvalidValueException;

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
		assertEquals("New value",4,(int)value.get());
	}
	
	@Test
	public void testSetImplementCondition() throws InvalidValueException {
		Out<String> value = Out.init();
		value.set("stringAfter", new Out.Condition<String>() {
			@Override
			public boolean check(String value) {
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
			return result != 0; 
		});
		assertEquals("New value",4,(int)value.get());
	}
	
	@Test(expected=Out.InvalidValueException.class)
	public void testSetLambdaConditionExcept() { // TODO
		Out<String> value = Out.init("stringBefore");
		value.set("stringAfter");
		assertEquals("New value","stringAfter",value.get());
	}
	
	@Test(expected=Out.InvalidValueException.class)
	public void testSetImplementConditionExcept() { // TODO
		Out<String> value = Out.init("stringBefore");
		value.set("stringAfter");
		assertEquals("New value","stringAfter",value.get());
	}

	@Test
	public void testGet() { // TODO
		
	}
	
	@Test
	public void testSort() { // TODO
		TreeSet<Out<Integer>> tree = new TreeSet<>(createList(5,8,9,5,4,2,3,5,4,8,5));
		Iterator<Out<Integer>> itr = tree.descendingIterator();
		while(itr.hasNext()) {
			
		}
	}
}
