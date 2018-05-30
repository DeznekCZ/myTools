package cz.deznekcz.javafx.configurator;

import cz.deznekcz.javafx.configurator.data.LiveStorage;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public abstract class ASetup {

	@FXML
	private MenuBar extendedMenus;

	@FXML
	private BorderPane root;
	
	protected LiveStorage storage;

	private ConfiguratorController ctrl;

	private Tab tab;
	
	public ASetup() {
		
	}
	
	public void externalInitializetion(ConfiguratorController ctrl, LiveStorage storage, Tab tab) {
		this.ctrl = ctrl;
		this.storage = storage;
		this.tab = tab;
		
		if (extendedMenus != null) {
			ctrl.registerExtendedMenus(tab, extendedMenus.getMenus());
			root.setTop(null);
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
}
