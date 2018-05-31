package cz.deznekcz.javafx.configurator.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class Choice extends Control implements Value {
	
	private static class ChoiceSkin implements Skin<Choice> {

		private Choice text;
		private HBox box;
		private Label label;
		private ChoiceBox<String> value;
		private Pane fill;
		private StringProperty valueString;
		
		public ChoiceSkin(Choice text) {
			this.text = text;
			text.getStyleClass().add("choice");
			text.setTooltip(new Tooltip());
			
			box = new HBox();
			label = new Label();
			fill = new Pane();
			value = new ChoiceBox<>();
			box.disableProperty().bind(text.disableProperty());

			label.getStyleClass().add("text-value-label");	
			fill .getStyleClass().add("text-value-fill");	
			value.getStyleClass().add("text-value-value");

			label.idProperty().bind(text.idProperty().concat("_label"));
			fill .idProperty().bind(text.idProperty().concat("_fill" ));
			value.idProperty().bind(text.idProperty().concat("_value"));

			value.maxWidthProperty().bind(text.widthProperty().divide(2));
			value.prefWidthProperty().bind(text.widthProperty().divide(2));

			HBox.setHgrow(fill, Priority.ALWAYS);
			
			valueString = new SimpleStringProperty("");
			valueString.addListener((o,l,n) -> {
				if (n == value.getSelectionModel().getSelectedItem()) return;
				if (!value.getItems().contains(n)) return;
				value.getSelectionModel().select(n);
			});
			
			value.getSelectionModel().selectedItemProperty().addListener((o,l,n) -> {
				if (n == valueString.get()) return;
				valueString.set(n);
			});
			
			box.getChildren().addAll(label, fill, value);
		}

		@Override
		public Choice getSkinnable() {
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
		return ((ChoiceSkin) getSkin()).label.textProperty();
	}
	
	public String getText() {
		return textProperty().get();
	}
	
	public void setText(String text) {
		this.textProperty().set(text);
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
	
	public ObjectProperty<ObservableList<String>> itemsProperty() {
		return ((ChoiceSkin) getSkin()).value.itemsProperty();
	}
	
	public ObservableList<String> getItems() {
		return itemsProperty().get();
	}
	
	public void setItems(ObservableList<String> items) {
		this.itemsProperty().set(items);
	}
	
	public ObjectProperty<SingleSelectionModel<String>> selectionModelProperty() {
		return ((ChoiceSkin) getSkin()).value.selectionModelProperty();
	}
	
	public SingleSelectionModel<String> getSelectionModel() {
		return selectionModelProperty().get();
	}
	
	public void setSelectionModel(SingleSelectionModel<String> items) {
		this.selectionModelProperty().set(items);
	}
	
	public StringProperty valueProperty() {
		return ((ChoiceSkin) getSkin()).valueString;
	}
	
	public String getValue() {
		return valueProperty().get();
	}
	
	public void setValue(String value) {
		valueProperty().set(value);
	}
	
	public Choice() {
		setSkin(new ChoiceSkin(this));
	}
}
