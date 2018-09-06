package cz.deznekcz.javafx.binding;

import java.util.stream.Stream;

import javafx.beans.value.ObservableValue;

public class AndBooleanBinding extends ListBooleanBinding<Boolean> {

	/**
	 * Constructs AND binding
	 */
	public AndBooleanBinding() {
		super("AndBooleanBinding");
	}

	/**
	 * Constructs AND binding
	 * @param booleanValues listened values
	 */
	@SafeVarargs
	public AndBooleanBinding(ObservableValue<Boolean>...booleanValues) {
		this();
		addAll(booleanValues);
	}

	@Override
	protected boolean computeValue(Stream<Boolean> stream) {
		return stream.allMatch(v -> v);
	}
}
