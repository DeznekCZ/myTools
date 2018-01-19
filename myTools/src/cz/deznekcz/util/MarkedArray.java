package cz.deznekcz.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

public class MarkedArray<T> implements Iterable<Elem<T>> {

	public static class SimpleArray<T> {
		private T[] instances;
		private boolean[] indices;
		
		private SimpleArray(T[] instances) {
			this.instances = instances;
			this.indices = new boolean[instances.length];
		}
		
		public MarkedArray<T> mark(@SuppressWarnings("unchecked") T... instances) {
			Objects.nonNull(instances);
			List<T> list = Arrays.asList(this.instances);
			for (T t : instances) {
				int index = list.indexOf(t);
				if (index > -1)
					indices[index] = true;
			}
			return new MarkedArray<>(instances, indices);
		}
	}

	private T[] instances;
	private boolean[] indices;
	
	private MarkedArray(T[] instances, boolean[] indices) {
		this.instances = instances;
		this.indices = indices;
	}
	
	@SafeVarargs
	public static <T> SimpleArray<T> from(T... instances) {
		return new SimpleArray<>(instances);
	}
	
	public static <T> SimpleArray<T> from(IntFunction<T[]> generator, Iterable<T> instances) {
		List<T> list = new ArrayList<T>();
		for (T t : instances) {
			list.add(t);
		}
		return from(list.toArray(generator.apply(list.size())));
	}
	
	public Elem<T> get(int index) {
		return new Elem<>(indices[index], instances[index]);
	}
	
	public int getIndex(T element) {
		return Arrays.asList(instances).indexOf(element);
	}
	
	public boolean isMarked(int index) {
		return indices[index];
	}
	
	public boolean isMarked(T element) {
		return isMarked(Arrays.asList(instances).indexOf(element));
	}

	@Override
	public Iterator<Elem<T>> iterator() {
		return new Iterator<Elem<T>>() {
			private int index;

			@Override
			public boolean hasNext() {
				return index < instances.length;
			}
			
			@Override
			public Elem<T> next() {
				return new Elem<T>(indices[index], instances[index++]);
			}
		};
	}

	public void replace(int index, T newValue) {
		instances[index] = newValue;
	}

	public void replace(T lastValue, T newValue) {
		replace(Arrays.asList(instances).indexOf(lastValue), newValue);
	}
}
