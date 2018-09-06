package cz.deznekcz.javafx.binding;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.beans.Observable;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableIntegerArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

public class SizeBinding extends IntegerBinding {

	private static final String EXCEPTION_MESSAGE = "CountBinding require non null parameter";
	private Observable observable;
	private Supplier<Integer> sizeGetter;

	private SizeBinding(Observable observable, Supplier<Integer> sizeGetter) {
		this.observable = observable;
		this.sizeGetter = sizeGetter;
		bind(this.observable);
	}

	@Override
	protected int computeValue() {
		return sizeGetter.get();
	}

	public static SizeBinding from(ObservableArray<?> items) {
		Objects.requireNonNull(items, EXCEPTION_MESSAGE);
		return new SizeBinding(items, items::size);
	}

	public static SizeBinding from(ObservableSet<?> set) {
		Objects.requireNonNull(set, EXCEPTION_MESSAGE);
		return new SizeBinding(set, set::size);
	}

	public static SizeBinding from(ObservableList<?> list) {
		Objects.requireNonNull(list, EXCEPTION_MESSAGE);
		return new SizeBinding(list, list::size);
	}

	public static SizeBinding from(ObservableMap<?,?> map) {
		Objects.requireNonNull(map, EXCEPTION_MESSAGE);
		return new SizeBinding(map, map::size);
	}

	public static SizeBinding from(ObservableStringValue string) {
		Objects.requireNonNull(string, EXCEPTION_MESSAGE);
		return new SizeBinding(string, () -> string.getValue() != null ? string.getValue().length() : 0);
	}

}
