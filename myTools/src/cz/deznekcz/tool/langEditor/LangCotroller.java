package cz.deznekcz.tool.langEditor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.xml.internal.ws.handler.HandlerException;

import cz.deznekcz.reference.Out;
import cz.deznekcz.util.XMLLoader;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeView.EditEvent;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.converter.DefaultStringConverter;

public class LangCotroller implements Initializable {

	@FXML
    private TreeView<String> xmlTreeView;

	@FXML
	private MenuItem saveAsMenuItem;

	@FXML
	private MenuItem exportMenuItem;

	@FXML
	private Menu editMenu;
	
	@FXML
	private Button newContextButton;
	
	@FXML
	private Button newGroupButton;
	
	@FXML
	private Button newKeyButton;

	private FileChooser chooser;
    
	private static File lastVisited = new File(".");


    @FXML
    void openFile(ActionEvent event) {
    	while (true) {
    		File opened = chooser.showOpenDialog(null);
    		if (opened != null) {
    			try {
					TreeGenerator.from(opened.getName(), opened, XMLLoader.load(opened), xmlTreeView);
					break; // Succesfully loaded
				} catch (Exception e) {
					XMLLoader.showError(e);
				}
    		} else break;
    	}
    }
    
    @FXML
    void saveAsFile(ActionEvent event) {
    	File xml = chooser.showSaveDialog(null);
    	if (xml == null) {
    		return;
    	}
    	RootLangKey root = (RootLangKey) xmlTreeView.getRoot();
    	root.setXml(xml);
    	TreeGenerator.renameRoot(
    			root, xml.getName().split("\\.")[0]
    			);
    	TreeGenerator.storeChanges(root);
    }

    @FXML
    void exitApplication(ActionEvent event) {

    }

    @FXML
    void undoChanges(ActionEvent event) {

    }

    @FXML
    void redoChanges(ActionEvent event) {

    }

    @FXML
    void exportFile(ActionEvent event) {

    }

    @FXML
    void importFile(ActionEvent event) {

    }

    @FXML
    void newFile(ActionEvent event) {
    	File xml = chooser.showSaveDialog(null);
    	if (xml == null) {
    		return;
    	}

    	Out<Exception> eOut = Out.init();
    	String langName = xml.getName().split("\\.")[0];
    	XMLLoader.newXml(xml, TreeGenerator.XML_ROOT, eOut);
    	
    	if (!eOut.isNull())
    		return;
    	else 
    		handlerException(eOut.get());
    	
    	eOut.set();
    	Node root = XMLLoader.load(xml, eOut);
    	if (eOut.isNull())
    		TreeGenerator.from(
    			langName, 
    			xml, 
    			root, 
    			xmlTreeView);
    }

	private void handlerException(Exception exception) {
		System.out.println(exception);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		chooser = new FileChooser();
    	chooser.setInitialDirectory(lastVisited);
    	chooser.getExtensionFilters().addAll(
    			new ExtensionFilter(Keys.IO.FILTER.value(), Keys.EXTENSION),
    			new ExtensionFilter(Keys.IO.NO_FILTER.value(), Keys.NO_EXTENSION
    					));
    	xmlTreeView.setCellFactory(list -> new TextFieldTreeCell<String>(new DefaultStringConverter()){
    		public void startEdit() {
    			TreeItem<String> item = list.getSelectionModel().getSelectedItem();
    			if (item instanceof RootLangKey) return;
    			super.startEdit();
    		};
    	});
    	
    	BooleanBinding notOpenedBind = new BooleanBinding() {
    		{
    			bind(xmlTreeView.rootProperty());
    		}
    		@Override
			protected boolean computeValue() {
    			return xmlTreeView.getRoot() == null;
			}
		};
    	saveAsMenuItem.disableProperty().bind(notOpenedBind);
    	exportMenuItem.disableProperty().bind(notOpenedBind);
//    	editMenu.disableProperty().bind(notOpenedBind);
    	
    	xmlTreeView.setEditable(true);
    	xmlTreeView.setOnEditCommit(event -> apply(event));
	}

	private void apply(EditEvent<String> event) {
		System.out.println("Changed");
		Platform.runLater(() -> TreeGenerator.storeChanges((RootLangKey) xmlTreeView.getRoot()));
	}

}