package cz.deznekcz.javafx.configurator;

import cz.deznekcz.javafx.configurator.components.Command;
import cz.deznekcz.tool.i18n.Arguments;
import cz.deznekcz.tool.i18n.IKeysClassLangKey;
import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.tool.i18n.Lang;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Configurator extends Application {

	/** Class keys for {@link Command} */
	public static enum command implements IKeysClassLangKey {
		STATE_RUN, STATE_STOPPED, STATE_COMPLETE, STATE_ERROR,
		STATE, 
		@Arguments(hints="command name", types=String.class)
		NOT_RUNABLE,
		EXECUTE, INSTANCES, ARGS, DIR, CMD
	}
	/** Class keys for {@link Tab} */
	public static enum tab implements IKeysClassLangKey {
		CLOSE_DEFAULT
	}
	/** Class keys for {@link Path} */
	public static enum path implements IKeysClassLangKey {
		OPEN, SELECT, FILTER_CONFIG, ALL_FILES
	}
	
	public static String lang = "en_US";
	public static ILangKey title;
	public static boolean unnecesary;
	public static String[] defaultConfigs; 
	
	public static void launch(String...args) {
		Lang.LANGload(lang);
		Application.launch(args);
		Lang.LANGgererate(lang);
	}

	private static ConfiguratorController ctrl;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(Configurator.class.getResource("Configurator.fxml"), Lang.asResourceBundle());
		BorderPane root = loader.load();
		root.getStylesheets().add("Configurator.css");
		ctrl = loader.<ConfiguratorController>getController();
		primaryStage.setOnCloseRequest(ctrl::exit);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
		ctrl.loadLast(defaultConfigs);
	}
	
	public static ConfiguratorController getCtrl() {
		return ctrl;
	}
}
