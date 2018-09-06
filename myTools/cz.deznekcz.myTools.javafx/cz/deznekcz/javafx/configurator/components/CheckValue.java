package cz.deznekcz.javafx.configurator.components;

import cz.deznekcz.javafx.configurator.components.support.AValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

public class CheckValue extends AValue {
	
	private static class CheckValueSkin implements Skin<CheckValue> {

		private CheckValue text;
		private CheckBox value;
		private BorderPane box;

		private StringProperty valueProperty;
		private BooleanProperty selectedProperty;
		
		public CheckValueSkin(CheckValue text) {
			this.text = text;
			text.getStyleClass().add("text-value");
			text.setTooltip(new Tooltip());

			value = new CheckBox();
			value.tooltipProperty().bind(AValue.tooltipBind(text.tooltipProperty()));
			value.disableProperty().bind(text.disableProperty());
			
			BorderPane.setAlignment(value, Pos.CENTER_RIGHT);
			box = new BorderPane(null,null,null,null,value);

			value.getStyleClass().add("check-value-value");

			value.idProperty().bind(text.idProperty().concat("_value"));
			
			selectedProperty = new SimpleBooleanProperty(false);
			
			valueProperty = new SimpleStringProperty("false");
			value.selectedProperty().addListener((o,l,n) -> {
				if (n) {
					selectedProperty.set(true);
					valueProperty.set("true");
				} else {
					selectedProperty.set(false);
					valueProperty.set("false");
				}
			});
			valueProperty.addListener((o,l,n) -> {
				if (n == null) value.setSelected(false);
				String current = Boolean.toString(value.isSelected());
				if (!current.equals(n)) {
					value.setSelected(Boolean.parseBoolean(n));
				}
			});
		}

		@Override
		public CheckValue getSkinnable() {
			return text;
		}

		@Override
		public Node getNode() {
			return box;
		}

		@Override
		public void dispose() {
			
		}

	}
	
	public StringProperty textProperty() {
		return ((CheckValueSkin) getSkin()).value.textProperty();
	}
	
	public String getText() {
		return textProperty().get();
	}
	
	public void setText(String text) {
		this.textProperty().set(text);
	}
	
	public StringProperty valueProperty() {
		return ((CheckValueSkin) getSkin()).valueProperty;
	}
	
	public void setValue(boolean value) {
		this.valueProperty().set(Boolean.toString(value));
	}
	
	public StringProperty helpPropterty() {
		return getTooltip().textProperty();
	}
	
	public void setHelp(String prompt) {
		helpPropterty().set(prompt);
	}
	
	public String getHelp() {
		return helpPropterty().get();
	}

	public BooleanProperty selectedProperty() {
		return ((CheckValueSkin) getSkin()).selectedProperty;
	}
	
	public boolean isSelected() {
		return "true".equals(valueProperty().get());
	}
	
	public void setSelected(boolean value) {
		valueProperty().set(Boolean.toString(value));
	}
	
	public CheckValue() {
		setSkin(new CheckValueSkin(this));
	}

	@Override
	public void setValue(String value) {
		valueProperty().set(value);
	}

	@Override
	public String getValue() {
		return valueProperty().get();
	}
	
	@Override
	public void refresh() {
		
	}
}
