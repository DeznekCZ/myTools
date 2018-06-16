package cz.deznekcz.javafx.configurator.components;

import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.reference.OutBoolean;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Command extends Control {
	
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
		private Stage instancesStage;
		private VBox instancesRoot;
		private VBox instancesValues;
		private Button instancesExecute;
		
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
			button = new Button();
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

			args.textProperty().bind(Bindings.concat(Configurator.command.ARGS, argsText));
			dir.textProperty().bind(Bindings.concat(Configurator.command.DIR, dirText));
			cmd.textProperty().bind(Bindings.concat(Configurator.command.CMD, cmdText));
			
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
			
			runnable = new SimpleBooleanProperty(false);
			runningCommands = FXCollections.observableArrayList();
			
			button.setText(Configurator.command.EXECUTE.value());
			button.disableProperty().bind(runnable.not());
			button.setOnAction(text::runCommand);
			
			instancesStage = new Stage(StageStyle.UTILITY);
			instancesStage.setWidth(200);
			instancesStage.setHeight(400);
			instancesStage.titleProperty().bind(label.textProperty());

			instancesRoot = new VBox();
			instancesRoot.getStylesheets().add("Configurator.css");
			instancesValues = new VBox();
			VBox.setVgrow(instancesValues, Priority.ALWAYS);
			instancesStage.setScene(new Scene(instancesRoot));
			instancesExecute = new Button();
			instancesExecute.setText(Configurator.command.EXECUTE.value());
			instancesExecute.disableProperty().bind(runnable.not());
			instancesExecute.prefWidthProperty().bind(instancesRoot.widthProperty());
			instancesExecute.setOnAction(text::runCommand);
			
			instancesRoot.getChildren().addAll(new ScrollPane(instancesValues), new Separator(), instancesExecute);
			
			runningCommands.addListener(new ListChangeListener<CommandInstance>() {
				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends CommandInstance> c) {
					c.next();
					if (runningCommands.size() == 0) {
						button.setText(Configurator.command.EXECUTE.value());
						button.disableProperty().bind(runnable.not());
						button.setOnAction(text::runCommand);
					} else {
						button.setText(Configurator.command.INSTANCES.value(runningCommands.size()));
						button.disableProperty().bind(OutBoolean.FALSE());
						button.setOnAction(text::showInstances);
					}
					if (c.wasAdded()) {
						for (CommandInstance commandInstance : c.getAddedSubList()) {
							instancesValues.getChildren().add(commandInstance.getNode());
						}
					}
				}
			});
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
	
	public Menu getCommandsMenu() {
		return ((CommandSkin) getSkin()).menu;
	}
	
	public ObservableList<CommandInstance> getRunningCommands() {
		return ((CommandSkin) getSkin()).runningCommands;
	}
	
	public void runCommand(ActionEvent event) {
		getRunningCommands().add(new CommandInstance(this));
	}
	
	public void showInstances(ActionEvent event) {
		Stage stage = ((CommandSkin) getSkin()).instancesStage;
		if (stage.isShowing()) stage.requestFocus();
		else                   stage.show();
	}
}
