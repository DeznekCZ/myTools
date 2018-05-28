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
import cz.deznekcz.tool.i18n.ILangKey;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
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
	@FXML private MenuItem CFG_menu_settings_unnecesary;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		CFG_menu_file_close.setOnAction(this::exit);
		
		bindTranslate(CFG_tab_configs, CFG_tab_commands,
				CFG_menu_file,
				CFG_menu_file_open, CFG_menu_file_close,
				CFG_menu_settings,
				CFG_menu_settings_unnecesary);
	}

	private void bindTranslate(Object...nodes) {
		for (Object object : nodes) {
			if (object instanceof Labeled) {
				Labeled cast = ((Labeled) object);
				cast.textProperty().bind(ILangKey.simple(cast.getId(), cast.getText()));
			} else if (object instanceof MenuItem) {
				MenuItem cast = ((MenuItem) object);
				cast.textProperty().bind(ILangKey.simple(cast.getId(), cast.getText()));
			} else if (object instanceof Tab) {
				Tab cast = ((Tab) object);
				cast.textProperty().bind(ILangKey.simple(cast.getId(), cast.getText()));
			}
		}
	}

	public void exit(Event event) {
		// TODO close all opened
		Platform.exit();
	}

	private boolean notFirstLoad = true;
	private PrintStream loadStream;
	
	public void loadConfig(File string) {
		try {
			if (notFirstLoad) {
				notFirstLoad = false;
				loadStream = new PrintStream(LAST_STORED);
			}
		} catch (IOException e) {
			Dialog.EXCEPTION.show(e);
		}
	}

	public void loadLast(String[] defaults) {
		if (LAST_STORED.exists()) {
			List<File> defaultFiles = new ArrayList<>();
			for (String path : defaults) {
				defaultFiles.add(new File(path));
			}
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
		}
	}
}
