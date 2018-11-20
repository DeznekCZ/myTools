package cz.deznekcz.javafx.configurator.components;

import cz.deznekcz.javafx.configurator.components.support.AValue;
import javafx.beans.property.StringProperty;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class TextValue extends AValue {

	private static class TextValueSkin implements Skin<TextValue> {

		private TextValue text;
		private HBox box;
		private Label label;
		private Label value;
		private Pane fill;

		public TextValueSkin(TextValue text) {
			this.text = text;
			text.getStyleClass().add("text-value");
			text.setTooltip(new Tooltip());

			box = new HBox();
			label = new Label();
			fill = new Pane();
			value = new Label();
			box.disableProperty().bind(text.disableProperty());

			label.getStyleClass().add("text-value-label");
			fill .getStyleClass().add("text-value-fill");
			value.getStyleClass().add("text-value-value");

			label.idProperty().bind(text.idProperty().concat("_label"));
			fill .idProperty().bind(text.idProperty().concat("_fill" ));
			value.idProperty().bind(text.idProperty().concat("_value"));

			HBox.setHgrow(fill, Priority.ALWAYS);

			box.getChildren().addAll(label, fill, value);
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

	public StringProperty helpPropterty() {
		return getTooltip().textProperty();
	}

	public void setHelp(String prompt) {
		helpPropterty().set(prompt);
	}

	public String getHelp() {
		return helpPropterty().get();
	}

	public TextValue() {
		setSkin(new TextValueSkin(this));
	}

	@Override
	public void refresh() {

	}

	@Override
	public EventTarget getEventTarget() {
		return ((TextValueSkin) getSkin()).value;
	}
}
