package cz.deznekcz.javafx.configurator.components;

import javafx.beans.property.Property;

public interface Value {
	Property<String> valueProperty();
	String getId();
}
