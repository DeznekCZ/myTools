package cz.deznekcz.javafx.configurator.components.support;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;

public interface BooleanValue {
	public ObservableBooleanValue booleanProperty();

	default boolean getBooleanValue() {
		return booleanProperty().getValue();
	}

	public static BooleanValue or(BooleanValue a, BooleanValue b) {
		return new BooleanValue() {
			BooleanBinding orBinding = Bindings.or(a.booleanProperty(), b.booleanProperty());

			@Override
			public ObservableBooleanValue booleanProperty() {
				return orBinding;
			}
		};
	}

	public static BooleanValue and(BooleanValue a, BooleanValue b) {
		return new BooleanValue() {
			BooleanBinding andBinding = Bindings.and(a.booleanProperty(), b.booleanProperty());

			@Override
			public ObservableBooleanValue booleanProperty() {
				return andBinding;
			}
		};
	}
}
