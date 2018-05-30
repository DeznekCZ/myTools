package cz.deznekcz.javafx.configurator.components;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class TextEntry extends Control {

	private static class TextEntrySkin implements Skin<TextEntry> {
		
		private TextEntry text;
		private HBox box;
		private Label label;
		private TextField value;
		private Pane fill;
		
		private StringProperty pattern;
		private ReadOnlyBooleanProperty limited;
		
		public TextEntrySkin(TextEntry text) {
			this.text = text;
			
			box = new HBox();
			label = new Label();
			fill = new Pane();
			value = new TextField();

			label.getStyleClass().add("text-entry-label");	
			fill .getStyleClass().add("text-entry-fill");	
			value.getStyleClass().add("text-entry-value");

			label.idProperty().bind(text.idProperty().concat("_label"));
			fill .idProperty().bind(text.idProperty().concat("_fill" ));
			value.idProperty().bind(text.idProperty().concat("_value"));

			value.maxWidthProperty().bind(text.widthProperty().divide(2));
			value.prefWidthProperty().bind(text.widthProperty().divide(2));

			HBox.setHgrow(fill, Priority.ALWAYS);
			
			label.setOnMouseClicked((e) -> {
				if (e.getButton() == MouseButton.PRIMARY) value.requestFocus();
			});
			fill.setOnMouseClicked(label.getOnMouseClicked());
			
			box.getChildren().setAll(label, fill, value);
			
			pattern = new SimpleStringProperty("*");
			limited = new SimpleBooleanProperty();
			((BooleanProperty) limited).bind(pattern.isNotEqualTo("*"));
			
//			mismaches = value.textProperty().
		}
		
		@Override
		public TextEntry getSkinnable() {
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
		return ((TextEntrySkin) getSkin()).label.textProperty();
	}
	
	public String getText() {
		return textProperty().get();
	}
	
	public void setText(String text) {
		this.textProperty().set(text);
	}
	
	public StringProperty valueProperty() {
		return ((TextEntrySkin) getSkin()).value.textProperty();
	}
	
	public String getValue() {
		return valueProperty().get();
	}
	
	public void setValue(String value) {
		this.valueProperty().set(value);
	}
	
	public StringProperty patternProperty() {
		return ((TextEntrySkin) getSkin()).pattern;
	}
	
	public boolean hasPattern() {
		return !getParent().equals("*");
	}
	
	public ReadOnlyBooleanProperty limitedProperty() {
		return ((TextEntrySkin) getSkin()).limited;
	}

	public String getPattern() {
		return patternProperty().get();
	}
	
	public void setPattern(String text) {
		this.patternProperty().set(text);
	}
	
	public TextEntry() {
		setSkin(new TextEntrySkin(this));
	}
}
