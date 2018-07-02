package cz.deznekcz.javafx.configurator.components;

import java.io.File;

import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.javafx.configurator.Configurator.command;
import cz.deznekcz.javafx.configurator.components.command.CommandInstance;
import cz.deznekcz.javafx.configurator.components.command.CommandInstance.Runnability;
import cz.deznekcz.javafx.configurator.components.command.Exit;
import cz.deznekcz.javafx.configurator.components.support.AValue;
import cz.deznekcz.javafx.configurator.components.support.HasArgsProperty;
import cz.deznekcz.javafx.configurator.components.support.HasCmdProperty;
import cz.deznekcz.javafx.configurator.components.support.HasDirProperty;
import cz.deznekcz.javafx.configurator.components.support.RefreshOnChange;
import cz.deznekcz.reference.OutBoolean;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@DefaultProperty("exits")
public class Command extends Control implements HasDirProperty, HasCmdProperty, HasArgsProperty {
	
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
		private ScrollPane instancesScroll;
		
		private BooleanProperty unnecesary;
		private StringProperty argsText;
		private StringProperty dirText;
		private StringProperty cmdText;
		private ObservableList<CommandInstance> runningCommands;
		private BooleanProperty runnable;
		private ObservableList<Exit> exits;
		private BooleanProperty silent;
		private StringProperty buttonTextProperty;
		private StringBinding instancesTextProperty;
		
		public CommandSkin(Command text) {
			this.text = text;
			text.getStyleClass().add("command");
			text.setTooltip(new Tooltip());
			
			argsText = new SimpleStringProperty("");
			dirText  = new SimpleStringProperty("");
			cmdText  = new SimpleStringProperty("");
			
			box = new BorderPane();
			label = new Label();
			label.tooltipProperty().bind(AValue.tooltipBind(text.tooltipProperty()));
			args = new Label(); 
			dir  = new Label();  
			cmd  = new Label();  
			button = new Button();
			runnable = new SimpleBooleanProperty(false);
			button.tooltipProperty().bind(new ObjectBinding<Tooltip>() {
				{
					bind(text.tooltipProperty());
				}
				@Override
				protected Tooltip computeValue() {
					String tooltipText = text.getTooltip().getText();
					if (tooltipText == null) tooltipText = "";
					return new Tooltip(runnable.isBound() ? tooltipText : tooltipText.concat(" ").concat(Configurator.command.NOT_ACTIVATED.value()));
				}
			});
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

			args.textProperty().bind(Bindings.concat(Configurator.command.ARGS, trimmed(argsText)));
			dir.textProperty().bind(Bindings.concat(Configurator.command.DIR, trimmed(dirText)));
			cmd.textProperty().bind(Bindings.concat(Configurator.command.CMD, trimmed(cmdText)));
			
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

			buttonTextProperty = new SimpleStringProperty(Configurator.command.EXECUTE.value());
			instancesTextProperty = new StringBinding() {
				{
					bind(runningCommands, buttonTextProperty);
				}
				@Override
				protected String computeValue() {
					return buttonTextProperty.get() + ": " + runningCommands.size();
				}
			};
			
			button.textProperty().bind(buttonTextProperty);
			button.disableProperty().bind(runnable.not());
			button.setOnAction(text::runCommand);
			
			instancesStage = new Stage(StageStyle.UTILITY);
			instancesStage.setWidth(300);
			instancesStage.setHeight(450);
			instancesStage.titleProperty().bind(new StringBinding() {
				{
					bind(label.textProperty(), buttonTextProperty);
				}
				@Override
				protected String computeValue() {
					return label.getText().length() == 0 ? buttonTextProperty.getValue() : label.getText();
				}
			});

			instancesRoot = new VBox();
			instancesRoot.getStylesheets().add("Configurator.css");
			instancesValues = new VBox();
			instancesScroll = new ScrollPane(instancesValues);
			instancesScroll.setFitToWidth(true);
			VBox.setVgrow(instancesScroll, Priority.ALWAYS);
			instancesStage.setScene(new Scene(instancesRoot));
			instancesStage.setAlwaysOnTop(true);
			instancesExecute = new Button();
			instancesExecute.textProperty().bind(buttonTextProperty);
			instancesExecute.disableProperty().bind(runnable.not());
			instancesExecute.prefWidthProperty().bind(instancesRoot.widthProperty());
			instancesExecute.setOnAction(text::runCommand);
			
			instancesRoot.getChildren().addAll(instancesScroll, new Separator(), instancesExecute);
			
			runningCommands.addListener(new ListChangeListener<CommandInstance>() {
				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends CommandInstance> c) {
					c.next();
					if (runningCommands.size() == 0) {
						button.textProperty().bind(buttonTextProperty);
						button.disableProperty().bind(runnable.not());
						button.setOnAction(text::runCommand);
					} else {
						button.textProperty().bind(instancesTextProperty);
						button.disableProperty().unbind();
						button.disableProperty().set(false);
						button.setOnAction(text::showInstances);
					}
					if (c.wasAdded()) {
						for (CommandInstance commandInstance : c.getAddedSubList()) {
							RadioButton cmdButton = commandInstance.getButton();
							cmdButton.prefWidthProperty().bind(instancesValues.widthProperty());
							instancesValues.getChildren().add(cmdButton);
						}
					}
				}
			});
			
			exits = FXCollections.observableArrayList();
			
			silent = new SimpleBooleanProperty(false);
		}

		private StringBinding trimmed(ObservableValue<String> untrimmed) {
			return new StringBinding() {
				ObservableValue<String> _untrimmed = untrimmed;
				{
					bind(_untrimmed);
				}
				@Override
				protected String computeValue() {
					return _untrimmed.getValue().trim();
				}
			};
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

	private static File logOutputDirectory = new File(".");
	
	public StringProperty textProperty() {
		return ((CommandSkin) getSkin()).label.textProperty();
	}
	
	public String getText() {
		return textProperty().get();
	}
	
	public void setText(String text) {
		this.textProperty().set(text);
	}
	
	public StringProperty buttonTextPropterty() {
		return ((CommandSkin) getSkin()).buttonTextProperty;
	}
	
	public String getButtonText() {
		return buttonTextPropterty().get();
	}
	
	public void setButtonText(String text) {
		this.buttonTextPropterty().set(text);
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
	
	public StringProperty cmdProperty() {
		return ((CommandSkin) getSkin()).cmdText;
	}
	
	public String getCmd() {
		return cmdProperty().get().trim();
	}
	
	public void setCmd(String cmd) {
		cmdProperty().set(cmd);
	}
	
	public StringProperty dirProperty() {
		return ((CommandSkin) getSkin()).dirText;
	}
	
	public String getDir() {
		return dirProperty().get().trim();
	}
	
	public void setDir(String dir) {
		dirProperty().set(dir);
	}
	
	public StringProperty argsProperty() {
		return ((CommandSkin) getSkin()).argsText;
	}
	
	public String getArgs() {
		return argsProperty().get().trim();
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
	
	public ObservableList<Exit> getExits() {
		return ((CommandSkin) getSkin()).exits;
	}
	
	public void setExits(Exit...args) {
		getExits().setAll(args);
	}

	private Property<EventHandler<ActionEvent>> onSuccess = new SimpleObjectProperty<>(null);
	
	public Property<EventHandler<ActionEvent>> onSuccessProperty() {
		return onSuccess;
	}
	
	public EventHandler<ActionEvent> getOnSuccess() {
		return onSuccessProperty().getValue();
	}
	
	public void setOnSuccess(EventHandler<ActionEvent> onSuccess) {
		onSuccessProperty().setValue(onSuccess);
	}

	private Property<EventHandler<ActionEvent>> onCancel = new SimpleObjectProperty<>(null);
	
	public Property<EventHandler<ActionEvent>> onCancelProperty() {
		return onCancel;
	}
	
	public EventHandler<ActionEvent> getOnCancel() {
		return onCancelProperty().getValue();
	}
	
	public void setOnCancel(EventHandler<ActionEvent> onCancel) {
		onCancelProperty().setValue(onCancel);
	}

	private Property<EventHandler<ActionEvent>> onFail = new SimpleObjectProperty<>(null);
	
	public Property<EventHandler<ActionEvent>> onFailProperty() {
		return onFail;
	}
	
	public EventHandler<ActionEvent> getOnFail() {
		return onFailProperty().getValue();
	}
	
	public void setOnFail(EventHandler<ActionEvent> onFail) {
		onFailProperty().setValue(onFail);
	}
	
	private boolean notPrepared = true;
	private Runnability runnability;
	
	public void setActive() {
		if (notPrepared) {
			notPrepared = false;
			runnability = new CommandInstance.Runnability(this);
			((BooleanProperty) runnableProperty()).bind(runnability);
		}
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
		if (isSilent()) {
			CommandInstance.executeSilent(this);
		} else {
			getRunningCommands().add(new CommandInstance(this));
		}
		
	}
	
	public void showInstances(ActionEvent event) {
		Stage stage = ((CommandSkin) getSkin()).instancesStage;
		if (stage.isShowing()) stage.requestFocus();
		else                   stage.show();
	}

	public void removeInstance(CommandInstance commandInstance) {
		getRunningCommands().remove(commandInstance);
		if (getCommandsMenu() != null)
			getCommandsMenu().getItems().remove(commandInstance.getMenuItem());
		Configurator.getCtrl().removeCommand(commandInstance.getTab());
		((CommandSkin) getSkin()).instancesValues.getChildren().remove(commandInstance.getButton());
	}
	
	public BooleanProperty silentProperty() {
		return ((CommandSkin) getSkin()).silent;
	}
	
	public void setSilent(boolean silent) {
		silentProperty().set(silent);
	}
	
	public boolean isSilent() {
		return silentProperty().get();
	}

	public static File getLogOutputDirectory() {
		return logOutputDirectory;
	}
	
	public static void setLogOutputDirectory(File logOutputDirectory) {
		Command.logOutputDirectory = logOutputDirectory;
	}

	public Stage getInstancesStage() {
		return ((CommandSkin) getSkin()).instancesStage;
	}

	public void refresh() {
		if (runnability != null) runnability.refresh();
	}
}
