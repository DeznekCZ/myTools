package cz.deznekcz.javafx.binding;

import java.util.Objects;
import java.util.stream.Stream;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class ListBooleanBinding<T> extends BooleanBinding {

	private ObservableList<ObservableValue<T>> dependencies;
	private String name;

    public ListBooleanBinding(String name) {
		this.dependencies = FXCollections.observableArrayList();
    	this.name = name;
	}

    @SafeVarargs
    public ListBooleanBinding(String name, ObservableValue<T>...booleanValues) {
		this(name);
		this.dependencies.addAll(booleanValues);
	}

	@Override
	public ObservableList<ObservableValue<T>> getDependencies() {
		return dependencies;
	}

	/**
	 * Adds new observable to listen
	 * @param booleanValue instance of value
	 */
	public void add(ObservableValue<T> booleanValue) {
		unbind(dependencies.toArray(new Observable[dependencies.size()]));
		getDependencies().add(booleanValue);
		bind(dependencies.toArray(new Observable[dependencies.size()]));
		invalidate();
	}

	/**
	 * Removes new observable to listen
	 * @param booleanValue instance of value
	 */
	public void remove(ObservableValue<T> booleanValue) {
		unbind(dependencies.toArray(new Observable[dependencies.size()]));
		getDependencies().remove(booleanValue);
		bind(dependencies.toArray(new Observable[dependencies.size()]));
		invalidate();
	}

	/**
	 * Adds new observable to listen
	 * @param booleanValue instances to add
	 */
	@SuppressWarnings("unchecked")
	public void addAll(ObservableValue<T>...booleanValues) {
		Objects.requireNonNull(booleanValues, "Dependencies muss be set");
		unbind(dependencies.toArray(new Observable[dependencies.size()]));
		getDependencies().addAll(booleanValues);
		bind(dependencies.toArray(new Observable[dependencies.size()]));
		invalidate();
	}

	/**
	 * Removes new observable to listen
	 * @param booleanValue instances to remove
	 */
	@SuppressWarnings("unchecked")
	public void removeAll(ObservableValue<T>...booleanValues) {
		Objects.requireNonNull(booleanValues, "Dependencies muss be set");
		unbind(dependencies.toArray(new Observable[dependencies.size()]));
		getDependencies().removeAll(booleanValues);
		bind(dependencies.toArray(new Observable[dependencies.size()]));
		invalidate();
	}

	/**
     * Returns a string representation of this {@code BooleanBinding} object.
     * @return a string representation of this {@code BooleanBinding} object.
     */
    @Override
    public String toString() {
        return isValid() ? (name + " [value: " + get() + "]")
                : (name + " [invalid]");
    }

    @Override
    protected final boolean computeValue() {
    	return computeValue(getDependencies().stream().map(ObservableValue::getValue));
    }

    /**
     * Returns computed result for observed values
     * @param stream values of {@link ListBooleanBinding}
     * @return boolean result
     */
	protected abstract boolean computeValue(Stream<T> stream);
}
