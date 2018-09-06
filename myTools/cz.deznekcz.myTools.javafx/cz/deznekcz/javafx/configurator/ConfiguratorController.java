package cz.deznekcz.javafx.configurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

import cz.deznekcz.javafx.components.Dialogs;
import cz.deznekcz.javafx.configurator.components.support.RefreshOnChange;
import cz.deznekcz.javafx.configurator.components.support.Refreshable;
import cz.deznekcz.javafx.configurator.errhdl.CFGLoadException;
import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.tool.i18n.Lang;
import cz.deznekcz.util.EqualArrayList;
import cz.deznekcz.util.LiveStorage;
import cz.deznekcz.util.Utils;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class ConfiguratorController implements Initializable {

	private static final File LAST_STORED = new File(
				System.getenv("APPDATA") + "\\" +
						(Configurator.getApplication() != null ? Configurator.getApplication().getProject() : "test")
						+"\\lastOpened.cfg"
			);
	public static final ConfiguratorController getTest() {
		return new ConfiguratorController();
	}


	private final Map<String, StringProperty> globals = new HashMap<>();

	@FXML protected MenuBar       configuratorMenuBar;

	@FXML protected TabPane       configuratorTabs;
	@FXML protected Tab           configuratorTabConfigs;
	@FXML protected TabPane       configuratorTabConfigsTabs;
	@FXML protected Tab           configuratorTabCommands;
	@FXML protected TabPane       configuratorTabCommandsTabs;

	@FXML protected Menu          configuratorMenuFile;
	@FXML protected MenuItem      configuratorMenuFileOpen;
	@FXML protected MenuItem      configuratorMenuFileClose;

	@FXML protected Menu          configuratorMenuSettings;
	@FXML protected CheckMenuItem configuratorMenuSettingsUnnecessary;
	@FXML protected MenuItem      configuratorMenuSettingsRefresh;

	protected ObservableList<Menu> fixedMenus;
	protected HashMap<Tab, ObservableList<Menu>> configMenus;

	public void handleApplication(Stage stage, ConfiguratorApplication application) {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configuratorMenuFileClose.setOnAction(this::exit);

		fixedMenus = FXCollections.observableArrayList(configuratorMenuBar.getMenus());
		try {
			MenuBar extendedBar = FXMLLoader.load(Configurator.getApplication().thisClass().getResource("extendedMenus.fxml"), Lang.asResourceBundle());
			for (Menu menu : extendedBar.getMenus()) {
				if (menu.getId().endsWith("Settings")) {
					configuratorMenuSettings.getItems().add(new SeparatorMenuItem());
					configuratorMenuSettings.getItems().addAll(menu.getItems());
				} else if (menu.getId().endsWith("File")) {
					configuratorMenuFile.getItems().add(configuratorMenuFile.getItems().size() - 2, new SeparatorMenuItem());
					configuratorMenuFile.getItems().addAll(configuratorMenuFile.getItems().size() - 2, menu.getItems());
				} else if (menu.getId().endsWith("Ribbon") && Configurator.getApplication().hasRibbonHeader()) {
					((ConfiguratorRibbonController) this).addMenuToRibbon(menu);
				} else {
					configuratorMenuBar.getMenus().add(menu);
				}
			}
		} catch (IOException e) {
			if (Configurator.getApplication().hasExtendedMenus())
				Dialogs.EXCEPTION.show(e);
		}


		configMenus = new HashMap<>();

		configuratorMenuSettingsUnnecessary.selectedProperty().bindBidirectional(Unnecesary.hiddenProperty());

		configuratorTabConfigsTabs.getTabs().addListener((Observable o) -> {

			LAST_STORED.getParentFile().mkdirs();
			try {
				loadStream = new PrintStream(LAST_STORED);
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (Tab tab : configuratorTabConfigsTabs.getTabs()) {
				String tabLocation = (String) tab.getProperties().get("path");
				loadStream.println(tabLocation);
			}
		});

		configuratorMenuSettingsRefresh.setOnAction((event) -> RefreshOnChange.doRefresh());

		configuratorTabs.getStyleClass().add("split-tab-pane");

		configuratorTabs.tabMinWidthProperty().bind(configuratorTabs.widthProperty().divide(2).subtract(20));
		configuratorTabConfigsTabs.tabMinWidthProperty().bind(configuratorTabConfigsTabs.widthProperty().subtract(80).divide(5));
		configuratorTabCommandsTabs.tabMinWidthProperty().bind(configuratorTabCommandsTabs.widthProperty().subtract(80).divide(5));

		configuratorTabConfigsTabs.getSelectionModel().selectedItemProperty().addListener((o,l,n) -> {
			if (n != null && configMenus.containsKey(n)) {
				configuratorMenuBar.getMenus().setAll(fixedMenus);
				configuratorMenuBar.getMenus().addAll(configMenus.get(n));
			} else {
				configuratorMenuBar.getMenus().setAll(fixedMenus);
			}
		});

		configuratorTabConfigsTabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
	}

	public void exit(Event event) {
		// TODO close all opened
		Platform.exit();
	}

	private PrintStream loadStream;
	private Tab lastConfig;

	public void loadConfig(ConfigEntry configEntry) {
		try {
			LiveStorage storage = configEntry.isTemplate() ? configEntry.createCopyStorage() : configEntry.getStorage();
			if (storage == null) throw new NullPointerException("storage is not initialized");
			storage.setAutosave(true);
			String storagePath = configEntry.getFile().getAbsolutePath();

			URL[] urls = { new File("config\\" + storage.getId()).toURI().toURL() };
			URLClassLoader clLoader = new URLClassLoader(urls);
			ResourceBundle bundle = ResourceBundle.getBundle("lang", Locale.getDefault(), clLoader);

			URL fxmlFile = new File("config\\" + storage.getId() + "\\Layout.fxml").toURI().toURL();

			FXMLLoader loader = new FXMLLoader(fxmlFile, bundle);
			loader.load();

			ASetup setup = loader.<ASetup>getController();

			Tab foundRoot = null;
			for (Tab tab : configuratorTabConfigsTabs.getTabs()) {
				ASetup openedSetup = (ASetup) tab.getProperties().get(ASetup.class);
				if (openedSetup.storage.getFile().equals(storage.getFile())) {
					foundRoot = tab;
					break;
				}
			}

			if (foundRoot == null) {
				setup.externalInitialization(this, storage, bundle);

				setup.getRoot().getProperties().put("path", storagePath);
				setup.getRoot().getProperties().put(ASetup.class, setup);

				configuratorTabConfigsTabs.getTabs().add(setup.getRoot());
				lastConfig = setup.getRoot();

				if (configEntry.isDefaultInstance()) {
					final String finalName = lastConfig.getText();
					lastConfig.setOnCloseRequest(e -> {
						if (Dialogs.ASK.show(
									Configurator.tab.CLOSE_DEFAULT.value(finalName),
									ButtonType.YES,
									Utils.array(ButtonType.YES, ButtonType.NO)) != ButtonType.YES
							) {
							e.consume();
						}
					});
				}

			} else {
				lastConfig = foundRoot;
			}
		} catch (Exception e) {
			Dialogs.EXCEPTION.show(new CFGLoadException(e));
			lastConfig = null;
		}
	}

	public void loadLast(EqualArrayList<ConfigEntry> defaultFiles) {

		if (LAST_STORED.exists()) {

			EqualArrayList<ConfigEntry> list = new EqualArrayList<>();
			list.addAll(defaultFiles);
			List<String> paths;
			try {
				paths = Files.readAllLines(LAST_STORED.toPath());
			} catch (IOException e) {
				paths = new ArrayList<>();
				e.printStackTrace();
			}

			int countOfDefaults = 0;
			for (String path : paths) {
				ConfigEntry configEntry = ConfigEntry.loaded(path);
				int contains = defaultFiles.indexOf(configEntry);
				if (contains >= 0) {
					list.set(contains, configEntry);
					configEntry.setAsDefaultInstance();
					countOfDefaults++;
				}
				list.add(configEntry);
			}

			boolean reopenDefault = countOfDefaults != defaultFiles.size() &&
				Dialogs.ASK.show(
					ILangKey.simple("CFG_file_open_missing",
									"Do you want reopen default configurations?")
							.value(),
					ButtonType.YES,
					Utils.array( ButtonType.YES, ButtonType.NO )
				) == ButtonType.YES;

			for (ConfigEntry configEntry : list) {
				if (!configEntry.isDefault() || reopenDefault)
					loadConfig(configEntry);
			}
		} else {
			for (ConfigEntry configEntry : defaultFiles) {
				loadConfig(configEntry);
			}
		}
	}

	public void registerExtendedMenus(Tab tab, ObservableList<Menu> menu) {
		configMenus.put(tab, menu);

		tab.onClosedProperty().addListener((o,l,n) -> {
			if (configMenus.containsKey(tab)) configMenus.remove(tab);
		});
	}

	public void openCommand(Tab tab) {
		configuratorTabCommandsTabs.getTabs().add(tab);
	}

	public void selectCommand(Tab tab) {
		configuratorTabCommandsTabs.getSelectionModel().select(tab);
		configuratorTabs.getSelectionModel().select(configuratorTabCommands);
	}

	public void removeCommand(Tab tab) {
		configuratorTabCommandsTabs.getTabs().remove(tab);
	}

	@FXML
	private void openConfiguration(Event event) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File("."));
		chooser.getExtensionFilters().setAll(new ExtensionFilter(Configurator.path.FILTER_CONFIG.value(), "*.run.xml"));

		File selected = chooser.showOpenDialog(null);
		if (selected != null) {
			loadConfig(ConfigEntry.loaded(selected.getAbsolutePath()));
			selectLastConfig();
		}
	}

	public ObservableList<Tab> getConfigs() {
		return configuratorTabConfigsTabs.getTabs();
	}

	public void selectLastConfig() {
		if (lastConfig != null) {
			configuratorTabConfigsTabs.getSelectionModel().select(lastConfig);
		}
	}

	public StringProperty getGlobal(String id) {
		StringProperty variable;
		if (globals.containsKey(id)) {
			variable = globals.get(id);

		} else {
			variable = new SimpleStringProperty("");
			globals.put(id, variable);
		}
		return variable;
	}
}
