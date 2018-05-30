package cz.deznekcz.javafx.configurator.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
		private BooleanProperty limited;
		private BooleanProperty mismach;
		
		public TextEntrySkin(TextEntry text) {
			this.text = text;
			text.getStyleClass().add("text-entry");
			text.setTooltip(new Tooltip(""));
			
			box = new HBox();
			label = new Label();
			fill = new Pane();
			value = new TextField();

			label.getStyleClass().add("text-entry-label");	
			fill .getStyleClass().add("text-entry-fill");	
			value.getStyleClass().add("text-entry-value");
			value.getStylesheets().add(getClass().getPackage().getName().replace('.', '/').concat("/TextEntry.css"));

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
			limited = new SimpleBooleanProperty(false);
			
			mismach = new SimpleBooleanProperty(false);
			
			pattern.addListener((o,l,n) -> {
				refresh();
			});
			value.textProperty().addListener((o,l,n) -> {
				refresh();
			});

//			System.out.println(label.getText());
			label.tooltipProperty().bind(text.tooltipProperty());
			value.tooltipProperty().bind(text.tooltipProperty());
		}
		
		private void refresh() {
			limited.set(!pattern.get().equals("*"));
			boolean active = limited.get() && !value.getText().matches(pattern.get());
			value.pseudoClassStateChanged(PseudoClass.getPseudoClass("mismach"), active);
			mismach.set(active);
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

	public String getPattern() {
		return patternProperty().get();
	}
	
	public void setPattern(String text) {
		this.patternProperty().set(text);
	}
	
	public ReadOnlyBooleanProperty limitedProperty() {
		return ((TextEntrySkin) getSkin()).limited;
	}
	
	public boolean isLimited() {
		return limitedProperty().get();
	}
	
	public boolean hasPattern() {
		return !isLimited();
	}
	
	public ReadOnlyBooleanProperty mismachProperty() {
		return ((TextEntrySkin) getSkin()).mismach;
	}
	
	public Boolean isMismach() {
		return mismachProperty().get();
	}
	
	public StringProperty promptPropterty() {
		return ((TextEntrySkin) getSkin()).value.promptTextProperty();
	}
	
	public void setPrompt(String prompt) {
		promptPropterty().set(prompt);
	}
	
	public String getPrompt() {
		return promptPropterty().get();
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
	
	public TextEntry() {
		setSkin(new TextEntrySkin(this));
	}
}
