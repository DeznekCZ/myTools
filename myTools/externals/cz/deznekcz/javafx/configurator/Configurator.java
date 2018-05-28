package cz.deznekcz.javafx.configurator;

import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.tool.i18n.Lang;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Configurator extends Application {

	public static String lang = "en_US";
	public static ILangKey title;
	public static boolean unnecesary;
	public static String[] defaultConfigs; 
	
	public static void launch(String...args) {
		Lang.LANGload(lang);
		Application.launch(args);
		Lang.LANGgererate(lang);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(Configurator.class.getResource("Configurator.fxml"), Lang.asResourceBundle());
		BorderPane root = loader.load();
		root.getStylesheets().add("Configurator.css");
		ConfiguratorController ctrl = loader.<ConfiguratorController>getController();
		primaryStage.setOnCloseRequest(ctrl::exit);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
		ctrl.loadLast(defaultConfigs);
	}
}
