package cz.deznekcz.javafx.configurator.components;

import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.reference.OutString;
import cz.deznekcz.tool.i18n.ILangKey;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

public class Command extends Control {

	private static ILangKey EXECUTE = ILangKey.simple("Configurator.command.button.execute");
	private static ILangKey INSTANCES = ILangKey.simple("Configurator.command.button.instances");
	private static ILangKey ARGS = ILangKey.simple("Configurator.command.args");
	private static ILangKey DIR = ILangKey.simple("Configurator.command.dir");
	private static ILangKey CMD = ILangKey.simple("Configurator.command.cmd");
	
	private static class CommandSkin implements Skin<Command> {

		private Command text;
		private BorderPane box;
		private Label label;
		private Button button;
		private Menu menu;
		private BorderPane lines;
		private Label args;
		private Label dir;
		private Label cmd;
		
		private BooleanProperty unnecesary;
		private StringProperty argsText;
		private StringProperty dirText;
		private StringProperty cmdText;
		private ObservableList<CommandInstance> runningCommands;
		private BooleanProperty runnable;
		
		public CommandSkin(Command text) {
			this.text = text;
			text.getStyleClass().add("command");
			text.setTooltip(new Tooltip());
			
			argsText = new SimpleStringProperty("");
			dirText  = new SimpleStringProperty("");
			cmdText  = new SimpleStringProperty("");
			
			box = new BorderPane();
			label = new Label();
			args = new Label(); 
			dir  = new Label();  
			cmd  = new Label();  
			button = new Button(EXECUTE.value());
			box.disableProperty().bind(text.disableProperty());
			
			lines = new BorderPane();
			lines.setTop(cmd);

			BorderPane.setAlignment(label, Pos.CENTER_LEFT);
			BorderPane.setAlignment(args , Pos.CENTER_LEFT);
			BorderPane.setAlignment(dir  , Pos.CENTER_LEFT);
			BorderPane.setAlignment(cmd  , Pos.CENTER_LEFT);
			
			argsText.addListener((o,l,n) -> {
				if (n != null && n.length() > 0) {
					lines.setCenter(args);
				} else {
					lines.setCenter(null);
				}
			});
			
			dirText.addListener((o,l,n) -> {
				if (n != null && n.length() > 0) {
					lines.setBottom(dir);
				} else {
					lines.setBottom(null);
				}
			});

			args.textProperty().bind(Bindings.concat(ARGS, argsText));
			dir.textProperty().bind(Bindings.concat(DIR, dirText));
			cmd.textProperty().bind(Bindings.concat(CMD, cmdText));
			
			label.getStyleClass().add("command-label");
			args .getStyleClass().add("command-args");
			dir  .getStyleClass().add("command-dir");
			cmd  .getStyleClass().add("command-cmd");
			button.getStyleClass().add("command-button");

			label.idProperty().bind(text.idProperty().concat("_label"));
			args .idProperty().bind(text.idProperty().concat("_args"));
			dir  .idProperty().bind(text.idProperty().concat("_dir"));
			cmd  .idProperty().bind(text.idProperty().concat("_cmd"));
			button.idProperty().bind(text.idProperty().concat("_button"));
			
			unnecesary = new SimpleBooleanProperty(true);
			unnecesary.bind(Unnecesary.hiddenProperty());
			unnecesary.addListener((o,l,n) -> {
				if (n) box.setBottom(null);
				else   box.setBottom(lines);
			});
			
			box.setLeft(label);
			box.setRight(button);
			
			runningCommands = FXCollections.observableArrayList();
			button.setOnAction(text::runCommand);
			runnable = new SimpleBooleanProperty(false);
			button.disableProperty().bind(runnable.not());
		}

		@Override
		public Command getSkinnable() {
			return text;
		}

		private boolean notPrepared = true;
		
		@Override
		public Node getNode() {
			if (notPrepared) {
				runnable.bind(CommandInstance.runnability(text));
				notPrepared = false;
			}
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
	
	public StringProperty cmdProperty() {
		return ((CommandSkin) getSkin()).cmdText;
	}
	
	public String getCmd() {
		return cmdProperty().get();
	}
	
	public void setCmd(String cmd) {
		cmdProperty().set(cmd);
	}
	
	public StringProperty dirProperty() {
		return ((CommandSkin) getSkin()).dirText;
	}
	
	public String getDir() {
		return dirProperty().get();
	}
	
	public void setDir(String cmd) {
		dirProperty().set(cmd);
	}
	
	public StringProperty argsProperty() {
		return ((CommandSkin) getSkin()).argsText;
	}
	
	public String getArgs() {
		return argsProperty().get();
	}
	
	public void setArgs(String args) {
		argsProperty().set(args);
	}
	
	public ReadOnlyBooleanProperty runnableProperty() {
		return ((CommandSkin) getSkin()).runnable;
	}
	
	public boolean isRunnable() {
		return runnableProperty().get();
	}
	
	public Command() {
		setSkin(new CommandSkin(this));
	}

	public void setCommandsMenu(Menu menu) {
		((CommandSkin) getSkin()).menu = menu;
	}
	
	public ObservableList<CommandInstance> getRunningCommands() {
		return ((CommandSkin) getSkin()).runningCommands;
	}
	
	public void runCommand(ActionEvent event) {
		getRunningCommands().add(new CommandInstance(this));
	}
}
