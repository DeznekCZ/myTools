package cz.deznekcz.javafx.configurator;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.deznekcz.javafx.configurator.components.Choice;
import cz.deznekcz.javafx.configurator.components.Command;
import cz.deznekcz.javafx.configurator.components.Value;
import cz.deznekcz.util.LiveStorage;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public abstract class ASetup {

	@FXML
	private MenuBar menus;
	
	@FXML
	private Menu commandsMenu;

	@FXML
	private BorderPane root;
	
	protected LiveStorage storage;

	private ConfiguratorController ctrl;

	private Tab tab;

	private Value[] values;
	
	public ASetup() {
		
	}
	
	public void externalInitialization(ConfiguratorController ctrl, LiveStorage storage, Tab tab) {
		this.ctrl = ctrl;
		this.storage = storage;
		this.tab = tab;
		
		if (menus != null) {
			ctrl.registerExtendedMenus(tab, menus.getMenus());
			root.setTop(null);
		}
		
		for (Value value : values) {
			value.valueProperty().setValue(storage.getValue(value.getId()));
			value.valueProperty().addListener((o,l,n) -> storage.setValue(value.getId(), n));
		}
	}
	
	public LiveStorage getStorage() {
		return storage;
	}
	
	public Tab getTab() {
		return tab;
	}
	
	public ConfiguratorController getCtrl() {
		return ctrl;
	}

	protected void storedValues(Value...values) {
		this.values = values;
	}
	
	protected void command(Command...commands) {
		if (commandsMenu != null && commands != null && commands.length > 0) {
			for (Command command : commands) {
				Menu menu = new Menu(command.getText());
				command.setCommandsMenu(menu);
				commandsMenu.getItems().add(menu);
				
				MenuItem exec = new MenuItem();
				exec.setText(Configurator.command.EXECUTE.value());
				exec.setOnAction(command::runCommand);
				exec.disableProperty().bind(command.runnableProperty().not());
				menu.getItems().add(exec);
				
				menu.getItems().addListener(new ListChangeListener<MenuItem>() {
					@Override
					public void onChanged(javafx.collections.ListChangeListener.Change<? extends MenuItem> c) {
						c.next();
						if (c.wasRemoved()) {
							if (menu.getItems().size() == 2) {
								Platform.runLater(() -> menu.getItems().remove(1));
							}
						}
					}
				});
			}
			commandsMenu.getItems().sort((o1, o2) -> Collator.getInstance().compare(o1.getText(), o2.getText()));
		}
	}

	protected void format(Value result, String format, Value...values) {
		if (values == null || values.length == 0) return;
		
		List<Object> texts = new ArrayList<>(values.length * 2);
		Pattern patter = Pattern.compile("\\$(\\$|\\{[0-9]+})");
		Matcher matcher = patter.matcher(format);
		
		int lastEnd = 0;
		while(matcher.find()) {
			int start = matcher.start();
			if (start > 0) texts.add(format.substring(lastEnd, start));
			lastEnd = matcher.end();
			String match = matcher.group();
			if (match.length() == 2) texts.add("$");
			else texts.add(values[Integer.parseInt(match.substring(2, match.length() - 1))].valueProperty());
		}
		if (lastEnd <= format.length()) texts.add(format.substring(lastEnd));
		
		result.valueProperty().bind(Bindings.concat(texts.toArray()));
	}
}
