package cz.deznekcz.javafx.configurator.components;

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class Cathegory extends Control {
	
	private static class CathegorySkin implements Skin<Cathegory> {

		private Cathegory text;
		private HBox box;
		private Label label;
		
		public CathegorySkin(Cathegory text) {
			this.text = text;
			text.getStyleClass().add("cathegory");
			text.setTooltip(new Tooltip());
			
			box = new HBox();
			label = new Label();

			label.getStyleClass().add("cathegory");

			label.idProperty().bind(text.idProperty().concat("_label"));

			HBox.setHgrow(label, Priority.ALWAYS);
			
			box.getChildren().addAll(label);
		}

		@Override
		public Cathegory getSkinnable() {
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
		return ((CathegorySkin) getSkin()).label.textProperty();
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
	
	public Cathegory() {
		setSkin(new CathegorySkin(this));
	}
}
