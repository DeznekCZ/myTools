package cz.deznekcz.javafx.configurator.components;

import cz.deznekcz.javafx.configurator.components.support.AValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.EventTarget;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

public class PasswordEntry extends AValue {

	private static class PasswordEntrySkin implements Skin<PasswordEntry> {

		private PasswordEntry text;
		private BorderPane box;
		private Label label;
		private PasswordField value;
		private BooleanProperty failed;

		private StringProperty valueString;

		public PasswordEntrySkin(PasswordEntry text) {
			this.text = text;
			text.getStyleClass().add("text-entry");
			text.setTooltip(new Tooltip(""));

			box = new BorderPane();
			label = new Label();
			value = new PasswordField();
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
			BorderPane.setAlignment(value, Pos.CENTER_RIGHT);
			box.setLeft(label);
			box.setRight(value);

			value.textProperty().addListener((o,l,n) -> {
				refresh();
			});
			value.disabledProperty().addListener((o,l,n) -> {
				refresh();
			});

			label.tooltipProperty().bind(text.tooltipProperty());
			value.tooltipProperty().bind(text.tooltipProperty());

			valueString = new SimpleStringProperty("");
			valueString.addListener((o,l,n) -> {
				value.setText(n);
			});

			failed = new SimpleBooleanProperty(false);
			failed.addListener((o,l,n) -> {
				value.pseudoClassStateChanged(PseudoClass.getPseudoClass("mismach"), n != null && n);
			});

		}

		private void refresh() {
			valueString.set(value.getText());
		}

		@Override
		public PasswordEntry getSkinnable() {
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
		return ((PasswordEntrySkin) getSkin()).label.textProperty();
	}

	public String getText() {
		return textProperty().get();
	}

	public void setText(String text) {
		this.textProperty().set(text);
	}

	public StringProperty valueProperty() {
		return ((PasswordEntrySkin) getSkin()).valueString;
	}

	public StringProperty promptPropterty() {
		return ((PasswordEntrySkin) getSkin()).value.promptTextProperty();
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

	public PasswordEntry() {
		setSkin(new PasswordEntrySkin(this));
	}

	public BooleanProperty failedProperty() {
		return ((PasswordEntrySkin) getSkin()).failed;
	}

	public boolean isFailed() {
		return failedProperty().get();
	}

	public void setFailed(boolean failed) {
		this.failedProperty().set(failed);
	}

	@Override
	public void refresh() {

	}

	@Override
	public EventTarget getEventTarget() {
		return ((PasswordEntrySkin) getSkin()).value;
	}
}
