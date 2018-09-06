package cz.deznekcz.javafx.configurator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.components.Command;
import cz.deznekcz.tool.i18n.Arguments;
import cz.deznekcz.tool.i18n.IKeysClassLangKey;
import cz.deznekcz.tool.i18n.Lang;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Configurator extends Application {

	/** Class keys for {@link Command} */
	public static enum command implements IKeysClassLangKey {
		STATE_RUN, STATE_STOPPED, STATE_COMPLETE, STATE_ERROR,
		@Arguments(hints="command state", types=String.class)
		STATE,
		@Arguments(hints="command name", types=String.class)
		NOT_RUNABLE,
		EXECUTE, INSTANCES, ARGS, DIR, CMD, SAVE_LOG, CLOSE,
		@Arguments(hints="cmd data", types=String.class)
		ASK_INTERRUPT,
		@Arguments(hints="cmd data", types=String.class)
		ASK_STORE_LOG, 
		@Arguments(hints="cmd data", types=String.class)
		NO_LOG_FILE, 
		@Arguments(hints="cmd data", types=String.class)
		SAVE_EXCEPTION,
		COMMAND_EXECUTION, 
		NOT_ACTIVATED, 
		SEND, INTERRUPT, EXPORT_LOG
		;
		
		static {
			STATE_RUN.initDefault("Running");
			STATE_STOPPED.initDefault("Stopped");
			STATE_COMPLETE.initDefault("Complete");
			STATE_ERROR.initDefault("Error");
			STATE.initDefault("[%s]");
			NOT_RUNABLE.initDefault("Commmand \"%s\" is not runnable!");
			NOT_ACTIVATED.initDefault("Command is not initialized!");
			EXECUTE.initDefault("EXECUTE");
			INSTANCES.initDefault("Instances: %d");
			CLOSE.initDefault("Close");
			SAVE_LOG.initDefault("Save log");
			CMD.initDefault("| Command: ");
			DIR.initDefault("| Directory: ");
			ARGS.initDefault("| Arguments: ");
			NO_LOG_FILE.initDefault("| Arguments: ");
			SAVE_EXCEPTION.initDefault("Error while saving of log file!\n%s");
			ASK_INTERRUPT.initDefault("Do you want interrupt command?\n%s");
			ASK_STORE_LOG.initDefault("Do you want store log?\n%s");
			COMMAND_EXECUTION.initDefault("Error while silent execution!");
			SEND.initDefault("Send");
			INTERRUPT.initDefault("Interrupt");
			EXPORT_LOG.initDefault("Export");
		}
	}
	/** Class keys for {@link Tab} */
	public static enum tab implements IKeysClassLangKey {
		CLOSE_DEFAULT;
		
		static {
			CLOSE_DEFAULT.initDefault("Do you want close default tab \"%s\"?");
		}
	}
	/** Class keys for {@link ResultValue} */
	public static enum result implements IKeysClassLangKey {
		OK, FAIL, NR, IF;
		
		static {
			OK.initDefault("OK");
			FAIL.initDefault("FAIL");
			NR.initDefault("NO RESULTS");
			IF.initDefault("INVALID FUNCTION");
		}
	}
	/** Class keys for {@link DirectoryChoice} */
	public static enum choice implements IKeysClassLangKey {
		DIR;
		
		static {
			DIR.initDefault("| Choosing directory: ");
		}
	}
	/** Class keys for {@link Path} */
	public static enum path implements IKeysClassLangKey {
		OPEN_FILE, SELECT_FILE,
		OPEN_DIR, SELECT_DIR,
		OPEN_CONFIG, SELECT_CONFIG, 
		OPEN_WEB,
		FILTER_CONFIG, ALL_FILES, FILTER_LAYOUT;
		
		static {
			OPEN_FILE.initDefault("Open File");
			SELECT_FILE.initDefault("Browse File");
			OPEN_DIR.initDefault("Open Directory");
			SELECT_DIR.initDefault("Browse Directory");
			OPEN_CONFIG.initDefault("Open Configuration");
			SELECT_CONFIG.initDefault("Browse Configuration");
			OPEN_WEB.initDefault("Browse URL");
			FILTER_CONFIG.initDefault("Configuration files \"*.run.xml\"");
			FILTER_LAYOUT.initDefault("Layout files \"*.fxml\"");
			ALL_FILES.initDefault("All files \"*\"");
		}
	}

	private static ConfiguratorApplication application;

	private static List<ExecutorService> services = new ArrayList<>();
	
	public static void launch(ConfiguratorApplication appl) {
		application = appl;
		Lang.LANGload(appl.getLang());
		Application.launch(appl.getArgs());
		for (ExecutorService executorService : services) {
			executorService.shutdownNow();
		}
		Lang.LANGgererate(appl.getLang());
	}

	private static ConfiguratorController ctrl;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(Configurator.class.getResource("Configurator.fxml"), Lang.asResourceBundle());
		BorderPane root = loader.load();
		root.getStylesheets().add("Configurator.css");
		ctrl = loader.<ConfiguratorController>getController();
		primaryStage.titleProperty().bind(application.getTitle());
		for (String path : application.getIconPaths()) {
			try {
				primaryStage.getIcons().add(new Image(new File(path).toURI().toString()));
			} catch (Exception e) {
				Dialog.EXCEPTION.show(e);
			}
		}
		primaryStage.setOnCloseRequest(ctrl::exit);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		ctrl.loadLast(application.getDefaultConfigs());
	}


	public static ConfiguratorController getCtrl() {
		return ctrl;
	}

	public static ConfiguratorApplication getApplication() {
		return application;
	}


	public static List<ExecutorService> getServices() {
		return services;
	}
}
