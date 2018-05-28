package cz.deznekcz.javafx.configurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.data.LiveStorage;
import cz.deznekcz.javafx.configurator.errhdl.CFGLoadException;
import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.tool.i18n.Lang;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.WindowEvent;

public class ConfiguratorController implements Initializable {

	private static final File LAST_STORED = new File(
				System.getenv("APPDATA").concat("\\passxde\\lastOpened.cfg")
			);
	
	@FXML private TabPane CFG_tabs;
	@FXML private Tab     CFG_tab_configs;
	@FXML private TabPane CFG_tab_configs_tabs;
	@FXML private Tab     CFG_tab_commands;
	@FXML private TabPane CFG_tab_commands_tabs;

	@FXML private Menu     CFG_menu_file;
	@FXML private MenuItem CFG_menu_file_open;
	@FXML private MenuItem CFG_menu_file_close;
	
	@FXML private Menu     CFG_menu_settings;
	@FXML private MenuItem CFG_menu_settings_unnecessary;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		CFG_menu_file_close.setOnAction(this::exit);
	}

	public void exit(Event event) {
		// TODO close all opened
		Platform.exit();
	}

	private boolean notFirstLoad = true;
	private PrintStream loadStream;
	
	public void loadConfig(File cfg) {
		try {
			if (notFirstLoad) {
				notFirstLoad = false;
				LAST_STORED.getParentFile().mkdirs();
				loadStream = new PrintStream(LAST_STORED);
			}
			boolean loaded = true;
			
			FXMLLoader loader = new FXMLLoader(cfg.toURI().toURL(), Lang.asResourceBundle());
			Tab tab = loader.load();
			LiveStorage storage = new LiveStorage(cfg);
			tab.textProperty().bind(storage.nameProperty());
			
			if (loaded == true)
				loadStream.println(cfg);
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
				).get() == ButtonType.YES)
			{
				for (File file : defaultFiles) {
					loadConfig(file);
				}
			}
			
			for (File file : list) {
				loadConfig(file);
			}
		} else {
			for (File file : defaultFiles) {
				loadConfig(file);
			}
		}
	}
}
