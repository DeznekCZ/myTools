package cz.deznekcz.javafx.configurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.errhdl.CFGLoadException;
import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.tool.i18n.Lang;
import cz.deznekcz.util.LiveStorage;
import cz.deznekcz.util.Utils;
import javafx.application.Platform;
import javafx.beans.Observable;
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

public class ConfiguratorController implements Initializable {

	private static final File LAST_STORED = new File(
				System.getenv("APPDATA") + "\\" + Configurator.getApplication().getProject() +"\\lastOpened.cfg"
			);
	@FXML private MenuBar       CFG_menu_bar;

	@FXML private TabPane       CFG_tabs;
	@FXML private Tab           CFG_tab_configs;
	@FXML private TabPane       CFG_tab_configs_tabs;
	@FXML private Tab           CFG_tab_commands;
	@FXML private TabPane       CFG_tab_commands_tabs;

	@FXML private Menu          CFG_menu_file;
	@FXML private MenuItem      CFG_menu_file_open;
	@FXML private MenuItem      CFG_menu_file_close;

	@FXML private Menu          CFG_menu_settings;
	@FXML private CheckMenuItem CFG_menu_settings_unnecessary;

	private ObservableList<Menu> fixedMenus;
	private HashMap<Tab, ObservableList<Menu>> configMenus;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		CFG_menu_file_close.setOnAction(this::exit);

		fixedMenus = FXCollections.observableArrayList(CFG_menu_bar.getMenus());
		try {
			MenuBar extendedBar = FXMLLoader.load(Configurator.getApplication().thisClass().getResource("extendedMenus.fxml"), Lang.asResourceBundle());
			for (Menu menu : extendedBar.getMenus()) {
				if (menu.getId().endsWith("Settings")) {
					CFG_menu_settings.getItems().add(new SeparatorMenuItem());
					CFG_menu_settings.getItems().addAll(menu.getItems());
				} else {
					CFG_menu_bar.getMenus().add(menu);
				}
			}
		} catch (IOException e) {
			if (Configurator.getApplication().hasExtendedMenus())
				Dialog.EXCEPTION.show(e);
		}
		
		
		configMenus = new HashMap<>();

		CFG_menu_settings_unnecessary.selectedProperty().bindBidirectional(Unnecesary.hiddenProperty());

		CFG_tab_configs_tabs.getTabs().addListener((Observable o) -> {

			LAST_STORED.getParentFile().mkdirs();
			try {
				loadStream = new PrintStream(LAST_STORED);
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (Tab tab : CFG_tab_configs_tabs.getTabs()) {
				String tabLocation = (String) tab.getProperties().get("path");
				loadStream.println(tabLocation);
			}
		});
		
		CFG_tabs.getStyleClass().add("split-tab-pane");

		CFG_tabs.tabMinWidthProperty().bind(CFG_tabs.widthProperty().divide(2).subtract(20));
		CFG_tab_configs_tabs.tabMinWidthProperty().bind(CFG_tab_configs_tabs.widthProperty().subtract(80).divide(5));
		CFG_tab_commands_tabs.tabMinWidthProperty().bind(CFG_tab_commands_tabs.widthProperty().subtract(80).divide(5));
		
		CFG_tab_configs_tabs.getSelectionModel().selectedItemProperty().addListener((o,l,n) -> {
			if (n != null && configMenus.containsKey(n)) {
				CFG_menu_bar.getMenus().setAll(fixedMenus);
				CFG_menu_bar.getMenus().addAll(configMenus.get(n));
			} else {
				CFG_menu_bar.getMenus().setAll(fixedMenus);
			}
		});
		
		CFG_tab_configs_tabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
	}

	public void exit(Event event) {
		// TODO close all opened
		Platform.exit();
	}

	private PrintStream loadStream;

	public void loadConfig(File cfg) {
		try {
			LiveStorage storage = new LiveStorage(cfg);

			URL[] urls = {new File("config\\" + storage.getId()).toURI().toURL()};
			ClassLoader clLoader = new URLClassLoader(urls);

			URL fxmlFile = new File("config\\" + storage.getId() + "\\Layout.fxml").toURI().toURL();
			ResourceBundle bundle = ResourceBundle.getBundle("lang", Locale.getDefault(), clLoader);

			FXMLLoader loader = new FXMLLoader(fxmlFile, bundle);
			loader.load();

			ASetup setup = loader.<ASetup>getController();
			setup.externalInitialization(this, storage);
			
			setup.getRoot().getProperties().put("path", cfg.getPath());

			CFG_tab_configs_tabs.getTabs().add(setup.getRoot());
		} catch (Exception e) {
			Dialog.EXCEPTION.show(new CFGLoadException(e));
		}
	}

	public void loadLast(String[] defaults) {
		List<File> defaultFiles = new ArrayList<>();
		for (String path : defaults) {
			defaultFiles.add(new File(path));
		}

		if (LAST_STORED.exists()) {

			List<File> list = new ArrayList<>();
			try {
				Scanner file = new Scanner(LAST_STORED);
				while(file.hasNextLine()) list.add(new File(file.nextLine()));
				file.close();
			} catch (FileNotFoundException e) {
				// nothing to do
			}
			if (!list.containsAll(defaultFiles) &&
				Dialog.ASK.show(
					ILangKey.simple("CFG_file_open_missing",
									"Do you want reopen default configurations?")
							.value(),
					ButtonType.YES,
					new ButtonType[]{ ButtonType.YES, ButtonType.NO }
				) == ButtonType.YES)
			{
				loadDefault(defaultFiles);
			} else {
				list.removeAll(defaultFiles);
				loadDefault(defaultFiles);
			}

			for (File file : list) {
				loadConfig(file);
			}
		} else {
			loadDefault(defaultFiles);
		}
	}

	private void loadDefault(List<File> defaultFiles) {
		for (File file : defaultFiles) {
			loadConfig(file);
		}

		for (Tab tab : CFG_tab_configs_tabs.getTabs()) {
			tab.setOnCloseRequest((e) -> {
				if (Dialog.ASK.show(
							Configurator.tab.CLOSE_DEFAULT.value(tab.getText()),
							ButtonType.YES,
							Utils.array(ButtonType.YES, ButtonType.NO)) != ButtonType.YES
					) {
					e.consume();
				}
			});
		}
	}

	public void registerExtendedMenus(Tab tab, ObservableList<Menu> menu) {
		configMenus.put(tab, menu);

		tab.onClosedProperty().addListener((o,l,n) -> {
			if (configMenus.containsKey(tab)) configMenus.remove(tab);
		});
	}

	public void openCommand(Tab tab) {
		CFG_tab_commands_tabs.getTabs().add(tab);
	}

	public void selectCommand(Tab tab) {
		CFG_tab_commands_tabs.getSelectionModel().select(tab);
		CFG_tabs.getSelectionModel().select(CFG_tab_commands);
	}

	public void removeCommand(Tab tab) {
		CFG_tab_commands_tabs.getTabs().remove(tab);
	}
	
	@FXML
	private void openConfiguration(Event event) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File("."));
		chooser.getExtensionFilters().setAll(new ExtensionFilter(Configurator.path.FILTER_CONFIG.value(), "*.run.xml"));
		
		File selected = chooser.showOpenDialog(null);
		if (selected != null) {
			loadConfig(selected);
		}
	}
}
