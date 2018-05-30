package cz.deznekcz.javafx.configurator.components;

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class TextValue extends Control {
	
	private static class TextValueSkin implements Skin<TextValue> {

		private TextValue text;
		private HBox box;
		private Label label;
		private Label value;
		private Pane fill;
		
		public TextValueSkin(TextValue text) {
			this.text = text;
			
			box = new HBox();
			label = new Label();
			fill = new Pane();
			value = new Label();

			label.getStyleClass().add("text-value-label");	
			fill .getStyleClass().add("text-value-fill");	
			value.getStyleClass().add("text-value-value");

			label.idProperty().bind(text.idProperty().concat("_label"));
			fill .idProperty().bind(text.idProperty().concat("_fill" ));
			value.idProperty().bind(text.idProperty().concat("_value"));
		}

		@Override
		public TextValue getSkinnable() {
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
		return ((TextValueSkin) getSkin()).label.textProperty();
	}
	
	public String getText() {
		return textProperty().get();
	}
	
	public void setText(String text) {
		this.textProperty().set(text);
	}
	
	public StringProperty valueProperty() {
		return ((TextValueSkin) getSkin()).value.textProperty();
	}
	
	public String getValue() {
		return valueProperty().get();
	}
	
	public void setValue(String value) {
		this.valueProperty().set(value);
	}
	
	public TextValue() {
		setSkin(new TextValueSkin(this));
	}
}
