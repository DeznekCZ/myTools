package cz.deznekcz.javafx.configurator.components;

import cz.deznekcz.javafx.configurator.Unnecesary;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class Command extends Control {
	
	private static class CommandSkin implements Skin<Command> {

		private Command text;
		private BorderPane box;
		private Label label;
		private Label value;
		private Button button;
		private Pane fill;
		private Menu menu;
		
		private BooleanProperty unnecesary;
		
		public CommandSkin(Command text) {
			this.text = text;
			text.getStyleClass().add("command");
			text.setTooltip(new Tooltip());
			
			box = new BorderPane();
			label = new Label();
			fill = new Pane();
			value = new Label();
			box.disableProperty().bind(text.disableProperty());

			label.getStyleClass().add("command-label");
			value.getStyleClass().add("command-dir");
			value.getStyleClass().add("command-cmd");

			label.idProperty().bind(text.idProperty().concat("_label"));
			value.idProperty().bind(text.idProperty().concat("_value"));

			HBox.setHgrow(fill, Priority.ALWAYS);
			
			unnecesary = new SimpleBooleanProperty(true);
			unnecesary.bind(Unnecesary.hiddenProperty());
			unnecesary.addListener((o,l,n) -> {
				
			});
			
			box.setLeft(label);
			box.setRight(button);
		}

		@Override
		public Command getSkinnable() {
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
		return ((CommandSkin) getSkin()).label.textProperty();
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
	
	public void setButtonText(String text) {
		((CommandSkin) getSkin()).button.setText(text);
	}
	
	public Command() {
		setSkin(new CommandSkin(this));
	}

	public void setCommandsMenu(Menu menu) {
		((CommandSkin) getSkin()).menu = menu;
	}
}
