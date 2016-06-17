package cz.deznekcz.javafx.parametricPane.parameters;

import java.io.File;

import cz.deznekcz.javafx.parametricPane.ParametricPane;
import cz.deznekcz.javafx.parametricPane.ParametricTraverser;
import cz.deznekcz.javafx.parametricPane.parsing.ParameterElement;
import cz.deznekcz.reference.OutInteger;
import cz.deznekcz.tool.ILangKey;
import cz.deznekcz.tool.Lang;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Instances of class {@link BrowseParameter} represent parameters selection 
 * files {@link #BrowseParameter(String, String)}
 * or directories {@link #BrowseParameter(String)}
 * @see ParameterElement#browse_file
 * @see #BrowseParameter(String)
 * @see #BrowseParameter(String, String)
 * @author Zdenek Novotny (DeznekCZ)
 */
public class BrowseParameter extends AParameter<File> {

	private static final ILangKey BROWSE = ILangKey.simple("ParametricPane.button.browse");
	private static final ILangKey EXTENSION = ILangKey.simple("ParametricPane.extension.");
	private BorderPane borderPane;
	private Button browseButton;
	private TextField pathTextField;
	private File value;
	private FileChooser fc;
	private DirectoryChooser dc;
	private ExtensionFilter asociatedFilter;
	
	/**
	 * Parameter selecting files
	 * @param id unique identifier
	 * @param extensions enabled extensions
	 * @see ParameterElement#browse_file
	 * @see ExtensionFilter
	 */
	public BrowseParameter(String id, String extensions) {
		super(id, ParameterElement.browse_file);
		initComponent();
		asociatedFilter = new ExtensionFilter(
				EXTENSION.extended(extensions).value(), 
				extensions.split(";")
			);
		fc = new FileChooser();
		fc.getExtensionFilters().add(asociatedFilter);
		fc.setSelectedExtensionFilter(asociatedFilter);
		browseButton.setOnAction(event -> {
			fc.setInitialDirectory(value.exists() ? value.getParentFile() : new File("."));
			File output = fc.showOpenDialog(null);
			if (output != null) {
				set(output);
			}
		});
	}
	
	/**
	 * Parameter selecting folders
	 * @param id unique identifier
	 * @see ParameterElement#browse_file XML element "browse_file"
	 */
	public BrowseParameter(String id) {
		super(id, ParameterElement.browse_dir);
		initComponent();
		dc = new DirectoryChooser();
		browseButton.setOnAction(event -> {
			dc.setInitialDirectory(value.exists() ? value : new File("."));
			File output = dc.showDialog(null);
			if (output != null) {
				set(output);
			}
		});
	}

	@Override
	protected void initComponent() {
		pathTextField = new TextField();
		pathTextField.setEditable(false);
		pathTextField.setId(getId()+"-path");
		
		browseButton = new Button(Lang.LANG(BROWSE));
		browseButton.setId(getId());
		browseButton.setTooltip(getTooltip());
		ParametricTraverser.registerElement(browseButton);
		ParametricTraverser.registerElement(pathTextField);
		
		value = new File(".");
		
		borderPane = new BorderPane(pathTextField, null, browseButton, null, null);
	}

	@Override
	public Node getEnclosingComponent() {
		return borderPane;
	}

	@Override
	public StringProperty valueProperty() {
		return pathTextField.textProperty();
	}

	@Override
	public void set(File newValue) {
		pathTextField.setText(newValue.getAbsolutePath());
		value = newValue;
	}

	@Override
	public void setEnabled(boolean b) {
		pathTextField.setDisable(!b);
	}

	@Override
	public boolean isEnabled() {
		return !pathTextField.isDisabled();
	}

	@Override
	public void setEditable(boolean b) {
		
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public void fromString(String string) {
		set(new File(string));
	}

	/**
	 * Returns loaded instance of {@link AParameter}
	 * @param node intance of {@link org.w3c.dom.Node} (Element)
	 * @param indexOut returns an index in {@link ParametricPane}
	 * @return instance of {@link AParameter}
	 */
	public static BrowseParameter fromXml(org.w3c.dom.Node node, OutInteger indexOut) {
		indexOut.set(Integer.parseInt(ParameterElement.Param(ParameterElement.INDEX, node)));
		String id = ParameterElement.Param(ParameterElement.ID, node);
		ParameterElement type = ParameterElement.fromXMLName(node.getNodeName());
		if (type == ParameterElement.browse_dir) {
			return new BrowseParameter(id);
		} else {
			String filter = ParameterElement.Param(ParameterElement.EXTENSION, node);
			return new BrowseParameter(id, filter);
		}
	}

	@Override
	public void setFocusComponent() {
		if (browseButton.isVisible())
			browseButton.requestFocus();
	}

	@Override
	public boolean isFocusTraversable() {
		return isEnabled();
	}
}
