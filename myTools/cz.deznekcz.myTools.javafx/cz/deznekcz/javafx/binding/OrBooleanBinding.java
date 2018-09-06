package cz.deznekcz.javafx.binding;

import java.util.stream.Stream;

import javafx.beans.value.ObservableValue;

public class OrBooleanBinding extends ListBooleanBinding<Boolean> {

	/**
	 * Constructs OR binding
	 */
	public OrBooleanBinding() {
		super("OrBooleanBinding");
	}

	/**
	 * Constructs OR binding
	 * @param booleanValues listened values
	 */
	@SafeVarargs
	public OrBooleanBinding(ObservableValue<Boolean>...booleanValues) {
		this();
		addAll(booleanValues);
	}

	@Override
	protected boolean computeValue(Stream<Boolean> stream) {
		return stream.anyMatch(v -> v);
	}
}
