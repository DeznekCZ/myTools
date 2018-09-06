package cz.deznekcz.javafx.configurator;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import cz.deznekcz.javafx.configurator.components.Command;
import cz.deznekcz.javafx.configurator.components.support.Storeable;
import cz.deznekcz.util.LiveStorage;
import cz.deznekcz.util.LiveStorage.EntryValue;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public abstract class ASetup extends ATemplate {

    @FXML
    protected MenuBar menus;

    @FXML
    protected Menu commandsMenu;

    @FXML
    protected BorderPane init;

    protected LiveStorage storage;

    private ConfiguratorController ctrl;

    private boolean tittleAsStorageFileName;

	private ResourceBundle resourceBundle;

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

    public ASetup() {
    	super();
    }

    public final void externalInitialization(ConfiguratorController ctrl, LiveStorage storage, ResourceBundle bundle) {
        this.ctrl = ctrl;
        this.storage = storage;
        this.resourceBundle = bundle;
        if (this.root.getTabPane() != null) {
        	this.root.getTabPane().getTabs().remove(this.root);
        }

        if (menus != null) {
            ctrl.registerExtendedMenus(root, menus.getMenus());
            init.setTop(null);
        }

        if (tittleAsStorageFileName) {
            root.textProperty().unbind();
            root.setText(this.storage.getName());
        }

        lateInitialization();

        readStored();
    }

	public final LiveStorage getStorage() {
        return storage;
    }

    public final ConfiguratorController getCtrl() {
        return ctrl;
    }

    public final void command(Command...commands) {
        if (commands == null || commands.length == 0) return;

        if (commandsMenu != null) {
            for (Command command : commands) {
                Menu menu = new Menu();
                menu.textProperty().bind(command.nameProperty());
                command.setCommandsMenu(menu);
                commandsMenu.getItems().add(menu);

                MenuItem exec = new MenuItem();
                exec.setText(Configurator.command.EXECUTE.value());
                exec.setOnAction(command::runCommand);
                exec.disableProperty().bind(command.runnableProperty().not());
                menu.getItems().add(exec);

                menu.getItems().addListener((ListChangeListener.Change<? extends MenuItem> c) -> {
                    c.next();
                    if (c.wasRemoved() && menu.getItems().size() == 2) {
                        Platform.runLater(() -> menu.getItems().remove(1));
                    }
                });
            }
            commandsMenu.getItems().sort((o1, o2) -> Collator.getInstance().compare(o1.getText(), o2.getText()));
        }

        for (Command command : commands) {
            command.setActive();
        }
    }

    protected final void setTittleAsStorageFileName(boolean tittleAsStorageFileName) {
        this.tittleAsStorageFileName = tittleAsStorageFileName;
    }

    public final boolean isTittleAsStorageFileName() {
        return tittleAsStorageFileName;
    }
}
