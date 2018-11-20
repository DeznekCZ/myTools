package cz.deznekcz.javafx.configurator.components.support;

import javafx.beans.property.Property;

public interface Storeable {
	String getId();
	Property<String> valueProperty();
	void setValue(String value);
}
