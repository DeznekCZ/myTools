package cz.deznekcz.javafx.configurator.components.support;

public interface BooleanValue extends ReadOnlyValue, Storeable {

	@Override
	default String getValue() {
		return valueProperty().getValue();
	}

}
