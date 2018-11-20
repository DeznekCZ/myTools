package cz.deznekcz.javafx.configurator.components;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import cz.deznekcz.javafx.configurator.ATemplate;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.javafx.configurator.components.command.CommandInstance;
import cz.deznekcz.javafx.configurator.components.command.CommandInstance.JavaRunnability;
import cz.deznekcz.javafx.configurator.components.command.Exit;
import cz.deznekcz.javafx.configurator.components.support.AValue;
import cz.deznekcz.javafx.configurator.components.support.HasArgsProperty;
import cz.deznekcz.javafx.configurator.components.support.HasCmdProperty;
import cz.deznekcz.javafx.configurator.components.support.HasDirProperty;
import cz.deznekcz.javafx.configurator.components.support.Refreshable;
import cz.deznekcz.javafx.configurator.components.support.ValueLink;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
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
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@DefaultProperty("exits")
public class Command extends Control implements HasDirProperty, HasCmdProperty, HasArgsProperty, Refreshable {

    private BorderPane box;
    private Label label;
    private BorderPane buttons;
    private Button button;
    private Button instancesButton;
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
    private Property<ResultValue> validator;

	private StringProperty name;
    private BooleanProperty unnecesary;
    private StringProperty argsText;
    private StringProperty dirText;
    private StringProperty cmdText;
    private ObservableList<ValueLink> refreshables;
    private ObservableList<CommandInstance> runningCommands;
    private BooleanProperty runnable;
    private ObservableList<Exit> exits;
    private BooleanProperty silent;
    private StringProperty buttonTextProperty;
    private StringBinding instancesTextProperty;
	private StringProperty help;

	private Map<String, StringProperty> environment;

    private class CommandSkin implements Skin<Command> {

        private static final String PREFIX = "| ";
		private Command text;

        public CommandSkin(Command text) {
            this.text = text;
            Command.this.getStyleClass().add("command");
            Command.this.help = new SimpleStringProperty();
            Command.this.runnable = new SimpleBooleanProperty(false);

            argsText = new SimpleStringProperty("");
            dirText  = new SimpleStringProperty("");
            cmdText  = new SimpleStringProperty("");

            box = new BorderPane();
            label = new Label();
            label.tooltipProperty().bind(AValue.tooltipBind(help, runnable.not(), Configurator.command.NOT_ACTIVATED.value()));
            args = new Label();
            dir  = new Label();
            cmd  = new Label();
            buttons = new BorderPane();
            button = new Button();
            button.tooltipProperty().bind(AValue.tooltipBind(help));
            instancesButton = new Button();
            instancesButton.tooltipProperty().bind(AValue.tooltipBind(help));

            lines = new BorderPane();
            lines.setTop(cmd);
            lines.centerProperty().bind(setNodeIfTextHasLength(args, argsText));
            lines.bottomProperty().bind(setNodeIfTextHasLength(dir,  dirText ));

            BorderPane.setAlignment(label          , Pos.CENTER_LEFT);
            BorderPane.setAlignment(args           , Pos.CENTER_LEFT);
            BorderPane.setAlignment(dir            , Pos.CENTER_LEFT);
            BorderPane.setAlignment(cmd            , Pos.CENTER_LEFT);
            BorderPane.setAlignment(button         , Pos.CENTER_LEFT);
            BorderPane.setAlignment(instancesButton, Pos.CENTER_LEFT);

            args.textProperty().bind(Bindings.concat(PREFIX, Configurator.command.ARGS, " ", trimmed(argsText)));
            dir .textProperty().bind(Bindings.concat(PREFIX, Configurator.command.DIR, " ", trimmed(dirText)));
            cmd .textProperty().bind(Bindings.concat(PREFIX, Configurator.command.CMD, " ", trimmed(cmdText)));

            args.setMnemonicParsing(false);
            dir .setMnemonicParsing(false);
            cmd .setMnemonicParsing(false);

            label.getStyleClass().add("command-label");
            args .getStyleClass().add("command-args");
            dir  .getStyleClass().add("command-dir");
            cmd  .getStyleClass().add("command-cmd");
            button.getStyleClass().add("command-button");

            label.idProperty().bind(Command.this.idProperty().concat("_label"));
            args .idProperty().bind(Command.this.idProperty().concat("_args"));
            dir  .idProperty().bind(Command.this.idProperty().concat("_dir"));
            cmd  .idProperty().bind(Command.this.idProperty().concat("_cmd"));
            button.idProperty().bind(Command.this.idProperty().concat("_button"));

            unnecesary = new SimpleBooleanProperty(true);
            unnecesary.bind(Unnecesary.hiddenProperty());
            unnecesary.addListener((o,l,n) -> {
                if (n) box.setBottom(null);
                else   box.setBottom(lines);
            });

            box.setLeft(label);
            box.setRight(buttons);

            buttons.setCenter(button);

            silent = new SimpleBooleanProperty(false);
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
        return label.textProperty();
    }

    protected ObservableValue<Node> setNodeIfTextHasLength(Label node, StringExpression text) {
        return new ObjectBinding<Node>() {
            private IntegerExpression attachedLength = text.length();
            private Node attachedLabel = node;
            {
                bind(attachedLength);
            }
            @Override
            protected Node computeValue() {
                return attachedLength.getValue() == 0 ? null : attachedLabel;
            }
        };
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String text) {
        Command.this.textProperty().set(text);
    }

    public StringProperty buttonTextPropterty() {
        return buttonTextProperty;
    }

    public String getButtonText() {
        return buttonTextPropterty().get();
    }

    public void setButtonText(String text) {
        Command.this.buttonTextPropterty().set(text);
    }

    public StringProperty helpPropterty() {
        return help;
    }

    public void setHelp(String prompt) {
        helpPropterty().set(prompt);
    }

    public String getHelp() {
        return helpPropterty().get();
    }

    public StringProperty cmdProperty() {
        return cmdText;
    }

    public String getCmd() {
        return cmdProperty().get().trim();
    }

    public void setCmd(String cmd) {
        cmdProperty().set(cmd);
    }

    public StringProperty dirProperty() {
        return dirText;
    }

    public String getDir() {
        return dirProperty().get().trim();
    }

    public void setDir(String dir) {
        dirProperty().set(dir);
    }

    public StringProperty argsProperty() {
        return argsText;
    }

    public String getArgs() {
        return argsProperty().get().trim();
    }

    public void setArgs(String args) {
        argsProperty().set(args);
    }

    public ReadOnlyBooleanProperty runnableProperty() {
        return runnable;
    }

    public boolean isRunnable() {
        return runnableProperty().get();
    }

    public ObservableList<Exit> getExits() {
        return exits;
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
    private BooleanExpression runnability;
	private boolean javaFunction;
	private boolean usingLayout;

    public void setActive() {
        if (notPrepared) {
            notPrepared = false;
            if (isJavaFunction() && !isUsingLayout()) {
            	runnability = new CommandInstance.JavaRunnability(this);
            } else {
            	runnability = new SimpleBooleanProperty(true);
            }
        	((BooleanProperty) runnableProperty()).bind(runnability);
        }
    }

    public boolean isJavaFunction() {
		return javaFunction;
	}

    public void setJavaFunction(boolean value) {
    	javaFunction = value;
    }

    public boolean isUsingLayout() {
		return usingLayout;
	}

    public void setUsingLayout(boolean value) {
    	usingLayout = value;
    }

    public Command() {
        setSkin(new CommandSkin(this));
        environment = FXCollections.observableHashMap();
        box.disableProperty().bind(Command.this.disableProperty());

        runningCommands = FXCollections.observableArrayList();

        buttonTextProperty = new SimpleStringProperty(Configurator.command.EXECUTE.value());
        instancesTextProperty = new StringBinding() {
            {
                bind(runningCommands);
            }
            @Override
            protected String computeValue() {
                return "" + runningCommands.size();
            }
        };

        button.textProperty().bind(buttonTextProperty);
        button.disableProperty().bind(runnable.not());
        button.setOnAction(this::runCommand);

        instancesButton.textProperty().bind(instancesTextProperty);
        instancesButton.setOnAction(Command.this::showInstances);

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
        instancesExecute.setOnAction(Command.this::runCommand);

        instancesRoot.getChildren().addAll(instancesScroll, new Separator(), instancesExecute);

        runningCommands.addListener(new ListChangeListener<CommandInstance>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends CommandInstance> c) {
                c.next();
                if (runningCommands.isEmpty()) {
                	buttons.setRight(null);
                } else {
                	buttons.setRight(instancesButton);
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

        refreshables = FXCollections.observableArrayList();
        exits = FXCollections.observableArrayList();

        name = new SimpleStringProperty();
        name.bind(new StringBinding() {
        	{
        		bind(label.textProperty(), buttonTextProperty);
        	}
			@Override
			protected String computeValue() {
				return label.textProperty().getValueSafe().length() > 0 ? label.getText() : buttonTextProperty.getValue();
			}
		});

        validator = new SimpleObjectProperty<>(null);
        validator.addListener((o,l,n) -> {
        	if (n == null && l != null) {
        		refreshables.remove(new ValueLink(l));
        	} else if (n == null) {
        		refreshables.add(new ValueLink(n));
        	}
        });

        buttons.leftProperty().bind(validator);
    }

    public void setCommandsMenu(Menu menu) {
        Command.this.menu = menu;
    }

    public Menu getCommandsMenu() {
        return menu;
    }

    public ObservableList<CommandInstance> getRunningCommands() {
        return runningCommands;
    }

    public void runCommand(ActionEvent event) {
        if (isSilent()) {
            CommandInstance.executeSilent(this);
        } else {
            getRunningCommands().add(new CommandInstance(this));
        }

    }

    public void showInstances(ActionEvent event) {
        Stage stage = instancesStage;
        if (stage.isShowing()) stage.requestFocus();
        else                   stage.show();
    }

    public void removeInstance(CommandInstance commandInstance) {
        getRunningCommands().remove(commandInstance);
        if (getCommandsMenu() != null)
            getCommandsMenu().getItems().remove(commandInstance.getMenuItem());
        Configurator.getCtrl().removeCommand(commandInstance.getTab());
        instancesValues.getChildren().remove(commandInstance.getButton());
    }

    public BooleanProperty silentProperty() {
        return silent;
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
        return instancesStage;
    }

    public void refresh() {
        if (runnability != null) {
        	if (isJavaFunction()) {
        		((JavaRunnability) runnability).refresh();
        	} else {
        		// Removed
        	}
        }
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

    public final ObservableList<ValueLink> getRefreshables() {
        return refreshables;
    }

	public StringProperty nameProperty() {
		return name;
	}

	public Property<ResultValue> validatorProperty() {
		return validator;
	}

	public ResultValue getValidator() {
		return validator.getValue();
	}

	public void setValidator(ResultValue validator) {
		this.validator.setValue(validator);
	}

	public BooleanExpression getRunnabilty() {
		return runnability;
	}

	public Map<String, StringProperty> getExtendedEnvironment() {
		return environment;
	}

	public void addEnviroment(Map<String, StringProperty> environment) {
		for (Entry<String, StringProperty> variable : environment.entrySet()) {
			this.environment.put(variable.getKey(), variable.getValue());
		}
	}

	public static interface LayoutGenerator {
		public ATemplate generate() throws IOException;
	}

	private ObjectProperty<LayoutGenerator> layoutGenerator = new SimpleObjectProperty<>();

	public ObjectProperty<LayoutGenerator> layoutGeneratorProperty() {
		return layoutGenerator;
	}

	public void setLayoutGenerator(LayoutGenerator layoutGenerator) {
		layoutGeneratorProperty().setValue(layoutGenerator);
	}

	public LayoutGenerator getLayoutGenerator() {
		return layoutGeneratorProperty().getValue();
	}
}
