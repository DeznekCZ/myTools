package cz.deznekcz.tool.langEditor;

import java.io.File;
import java.util.Arrays;

import org.w3c.dom.Node;

import cz.deznekcz.reference.Out;
import cz.deznekcz.tool.Lang;
import cz.deznekcz.util.XMLLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public class LangApplication extends Application {

	public static void main(String[] args) {	
		System.out.println(Arrays.toString(args));
//		Lang.FXselect();
		Lang.LANGload("en_US");
		
		if (args.length == 1) {
			file = new File(args[0]);
		}
		
		LangApplication.launch(new String[0]);
		Lang.LANGgererate();
	}

	private static File file;
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("lang.fxml"), Lang.asResourceBundle())));
		
		if (file != null) {
			Node rootNode = XMLLoader.load(file, Out.init(ex->XMLLoader.showError(ex)));
			TreeGenerator.from(file.getName(), file, rootNode, 
					(TreeView<String>) primaryStage.getScene().lookup("#xmlTreeView"));
		}
		
		primaryStage.show();
	}

}
