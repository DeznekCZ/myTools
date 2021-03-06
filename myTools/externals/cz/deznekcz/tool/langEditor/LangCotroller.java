package cz.deznekcz.tool.langEditor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.w3c.dom.Node;

import cz.deznekcz.javafx.components.Dialogs;
import cz.deznekcz.reference.OutException;
import cz.deznekcz.util.xml.XML;
import cz.deznekcz.util.xml.XMLLoader;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeView.EditEvent;
import javafx.scene.control.cell.TextFieldTreeCell;
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
	
	@FXML
	private Button deleteButton;

	private FileChooser chooser;
    
	private static File lastVisited = new File(".");


    @FXML
    void openFile(ActionEvent event) {
    	while (true) {
    		File opened = chooser.showOpenDialog(null);
    		if (opened != null) {
    			try {
					TreeGenerator.from(opened.getName(), opened, XMLLoader.load(opened), xmlTreeView);
					break; // Successfully loaded
				} catch (Exception e) {
					Dialogs.EXCEPTION.show(e);
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
	void removeElement(ActionEvent event) {
    	TreeItem<String> selected = xmlTreeView.getSelectionModel().getSelectedItem();
    	if (selected instanceof RootLangKey) {
    		displayError( 
    				Keys.RemoveTitle.WANT_REMOVE_ROOT.value(),
    				Keys.RemoveHead.WANT_REMOVE_ROOT.value(),
    				Keys.RemoveMessage.WANT_REMOVE_ROOT.value());
    	} else if (selected instanceof ContextLangKey
    			&& confirm(
	    				Keys.RemoveTitle.WANT_REMOVE_CONTEXT.value(),
	    				Keys.RemoveHead.WANT_REMOVE_CONTEXT.value(),
	    				Keys.RemoveMessage.WANT_REMOVE_CONTEXT.value()
    					)) {
    		boolean remove = selected.getParent().getChildren().remove(selected);
    		if (!remove) {
    			displayError(
        				Keys.RemoveTitle.NOT_REMOVED.value(),
        				Keys.RemoveHead.NOT_REMOVED.value(),
        				Keys.RemoveMessage.NOT_REMOVED.value());
    		}
    	} else if (selected instanceof CathegoryLangKey
    			&& confirm(
	    				Keys.RemoveTitle.WANT_REMOVE_GROUP.value(),
	    				Keys.RemoveHead.WANT_REMOVE_GROUP.value(),
	    				Keys.RemoveMessage.WANT_REMOVE_GROUP.value()
    					)) {
    		boolean remove = selected.getParent().getChildren().remove(selected);
    		if (!remove) {
    			displayError(
        				Keys.RemoveTitle.NOT_REMOVED.value(),
        				Keys.RemoveHead.NOT_REMOVED.value(),
        				Keys.RemoveMessage.NOT_REMOVED.value());
    		}
    	} else if (selected instanceof LangKey
    			&& confirm(
	    				Keys.RemoveTitle.WANT_REMOVE_KEY.value(),
	    				Keys.RemoveHead.WANT_REMOVE_KEY.value(),
	    				Keys.RemoveMessage.WANT_REMOVE_KEY.value()
    					)) {
    		boolean remove = selected.getParent().getChildren().remove(selected);
    		if (!remove) {
    			displayError(
        				Keys.RemoveTitle.NOT_REMOVED.value(),
        				Keys.RemoveHead.NOT_REMOVED.value(),
        				Keys.RemoveMessage.NOT_REMOVED.value());
    		}
    	}
	}

    private boolean confirm(String title, String head, String message) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(head);
		alert.setContentText(message);
    	Optional<ButtonType> option = alert.showAndWait();
    	return option != null && option.get() == ButtonType.YES;
	}

	private void displayError(String title, String head, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(head);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
    void newFile(ActionEvent event) {
    	File xml = chooser.showSaveDialog(null);
    	if (xml == null) {
    		return;
    	}

    	OutException eOut = OutException.init();
    	String langName = xml.getName().split("\\.")[0];
    	
    	try {
			XMLLoader.save(xml, 
					XML.init(TreeGenerator.XML_ROOT)
					.doctype("properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\"")
					.root().newPairTag("entry").addAttribute("_lang_short", langName).close().close());
		} catch (IOException e) {
			eOut.set(e);
		}
    	
    	if (!eOut.isNull())
    		return;
    	else 
    		handlerException(eOut.get());
    	
    	eOut.set();
    	Node root = XMLLoader.load(xml, eOut.typed());
    	if (eOut.isNull())
    		TreeGenerator.from(
    			langName, 
    			xml, 
    			root, 
    			xmlTreeView);
    }

	private void handlerException(Exception exception) {
		System.out.println(exception);
		exception.printStackTrace();
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