package cz.deznekcz.javafx.configurator.components;

import cz.deznekcz.javafx.configurator.components.support.AValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

public class TextEntry extends AValue {

	private static class TextEntrySkin implements Skin<TextEntry> {
		
		private TextEntry text;
		private BorderPane box;
		private Label label;
		private TextField value;

		private StringProperty pattern;
		private StringProperty valueString;
		private BooleanProperty limited;
		private BooleanProperty mismach;
		
		public TextEntrySkin(TextEntry text) {
			this.text = text;
			text.getStyleClass().add("text-entry");
			text.setTooltip(new Tooltip(""));
			
			box = new BorderPane();
			label = new Label();
			value = new TextField();
			box.disableProperty().bind(text.disableProperty());

			label.getStyleClass().add("text-entry-label");	
			value.getStyleClass().add("text-entry-value");
			value.getStylesheets().add(getClass().getPackage().getName().replace('.', '/').concat("/TextEntry.css"));

			label.idProperty().bind(text.idProperty().concat("_label"));
			value.idProperty().bind(text.idProperty().concat("_value"));

			value.maxWidthProperty().bind(text.widthProperty().divide(2));
			value.prefWidthProperty().bind(text.widthProperty().divide(2));
			
			label.setOnMouseClicked((e) -> {
				if (e.getButton() == MouseButton.PRIMARY) value.requestFocus();
			});
			
			BorderPane.setAlignment(label, Pos.CENTER_LEFT);
			box.setLeft(label);
			box.setRight(value);
			
			pattern = new SimpleStringProperty("*");
			limited = new SimpleBooleanProperty(false);
			
			mismach = new SimpleBooleanProperty(false);
			
			pattern.addListener((o,l,n) -> {
				refresh();
			});
			value.textProperty().addListener((o,l,n) -> {
				refresh();
			});
			value.disabledProperty().addListener((o,l,n) -> {
				refresh();
			});

//			System.out.println(label.getText());
			label.tooltipProperty().bind(text.tooltipProperty());
			value.tooltipProperty().bind(text.tooltipProperty());
			
			valueString = new SimpleStringProperty("");
			valueString.addListener((o,l,n) -> {
				value.setText(n);
			});
		}
		
		private void refresh() {
			limited.set(!pattern.get().equals("*"));
			boolean active = limited.get() && !value.isDisabled()
					&& (value.getText() == null || !value.getText().matches(pattern.get()));
			value.pseudoClassStateChanged(PseudoClass.getPseudoClass("mismach"), active);
			mismach.set(active);
			
			if ((!limited.get() || !active) && !value.getText().equals(valueString.get())) 
				valueString.set(value.getText());
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
		return ((TextEntrySkin) getSkin()).valueString;
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
