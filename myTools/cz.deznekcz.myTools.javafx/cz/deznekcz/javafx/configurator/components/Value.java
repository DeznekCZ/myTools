package cz.deznekcz.javafx.configurator.components;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public interface Value {
	Property<String> valueProperty();
	String getId();
	
	static Value init(String getenv) {
		return new Value() {
			StringProperty value = new SimpleStringProperty(getenv);
			@Override
			public Property<String> valueProperty() {
				return value;
			}
			
			@Override
			public String getId() {
				return "";
			}
		};
	}
}
