package cz.deznekcz.javafx.parametricPane.parameters;

import cz.deznekcz.javafx.parametricPane.parsing.ParameterElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

/**
 * While take an error with ID, instances of {@link MissingParameter} will be created. 
 * @author Zdenek Novotny (DeznekCZ)
 *
 */
public class MissingParameter extends AParameter<String> {

	private StringProperty readOnly = new SimpleStringProperty("WRONG PARAM");

	/**
	 * Constructor of missing parameter
	 * @param id not found identifier
	 */
	public MissingParameter(String id) {
		super(id, ParameterElement.no_element);
		System.out.println("Parameter not found: \""+id+"\"");
	}

	@Override
	public StringProperty valueProperty() {
		return readOnly;
	}

	@Override
	protected void initComponent() {
		
	}

	@Override
	public Node getEnclosingComponent() {
		return null;
	}

	@Override
	public void setEnabled(boolean b) {
		
	}

	@Override
	public boolean isEnabled() {
		return false;
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
	}

	@Override
	public void set(String newValue) {
		
	}

	@Override
	public void setFocusComponent() {
		
	}

	@Override
	public boolean isFocusTraversable() {
		return false;
	}

}
