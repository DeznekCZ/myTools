package cz.deznekcz.javafx.configurator.components.support;

import javafx.beans.property.Property;

public interface Storeable extends ReadOnlyValue {
	String getId();
	Property<String> valueProperty();
	void setValue(String value);
}
