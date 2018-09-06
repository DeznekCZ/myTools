package cz.deznekcz.javafx.configurator.components.support;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Constant extends AValue {
	
	private StringProperty property;
	
	public Constant() {
		property = new SimpleStringProperty("");
	}
	
	public Property<String> valueProperty() {
		return property;
	}

	@Override
	public void setValue(String value) {
		valueProperty().setValue(value);
	}

	@Override
	public String getValue() {
		return valueProperty().getValue();
	}

	@Override
	public void refresh() {
		
	}
}
