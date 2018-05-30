package cz.deznekcz.javafx.configurator;

import cz.deznekcz.javafx.configurator.components.Value;
import cz.deznekcz.javafx.configurator.data.LiveStorage;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public abstract class ASetup {

	@FXML
	private MenuBar menus;

	@FXML
	private BorderPane root;
	
	protected LiveStorage storage;

	private ConfiguratorController ctrl;

	private Tab tab;

	private Value[] values;
	
	public ASetup() {
		
	}
	
	public void externalInitializetion(ConfiguratorController ctrl, LiveStorage storage, Tab tab) {
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
}
