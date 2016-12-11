package cz.deznekcz.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class OutCollection<C> extends Out<Collection<C>> implements Collection<C> {

	public static <C> OutCollection<C> init(Collection<C> collection) {
		return new OutCollection<>(collection);
	}
	
	protected OutCollection(Collection<C> defaultValue) {
		super(defaultValue != null ? defaultValue : new ArrayList<>(0));
	}

	@Override
	public int size() {
		return get().size();
	}

	@Override
	public boolean isEmpty() {
		return get().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return get().contains(o);
	}

	@Override
	public Iterator<C> iterator() {
		return get().iterator();
	}

	@Override
	public Object[] toArray() {
		return get().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return get().toArray(a);
	}

	@Override
	public boolean add(C e) {
		return get().add(e);
	}

	@Override
	public boolean remove(Object o) {
		return get().remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return get().containsAll(c);
	}

	public boolean containsAll(@SuppressWarnings("unchecked") C... c) {
		return get().containsAll(Arrays.asList(c));
	}

	@Override
	public boolean addAll(Collection<? extends C> c) {
		return get().addAll(c);
	}

	public boolean addAll(@SuppressWarnings("unchecked") C... c) {
		return get().addAll(Arrays.asList(c));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return get().removeAll(c);
	}

	public boolean removeAll(@SuppressWarnings("unchecked") C... c) {
		return get().removeAll(Arrays.asList(c));
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return get().retainAll(c);
	}

	public boolean retainAll(@SuppressWarnings("unchecked") C... c) {
		return get().retainAll(Arrays.asList(c));
	}

	@Override
	public void clear() {
		get().clear();
	}

}
