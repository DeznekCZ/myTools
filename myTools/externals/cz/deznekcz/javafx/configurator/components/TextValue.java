package cz.deznekcz.javafx.configurator.components;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class TextValue extends Control {
	
	@FXML
	private Label label;
	
	@FXML
	private Label value;
	
	@FXML
	private Pane fill;
	
	public StringProperty textProperty() {
		return label.textProperty();
	}
	
	public TextValue() {
		label = new Label();
		fill = new Pane();
		value = new Label();
		
		HBox.setHgrow(fill, Priority.ALWAYS);
		
		getChildren().setAll(label, fill, value);

		label.getStyleClass().add("label");	
		fill .getStyleClass().add("fill");	
		value.getStyleClass().add("value");

		label.idProperty().bind(idProperty().concat("_label"));
		fill .idProperty().bind(idProperty().concat("_fill" ));
		value.idProperty().bind(idProperty().concat("_value"));
	}
}
