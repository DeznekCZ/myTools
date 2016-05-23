package cz.deznekcz.javafx.parametricPane.parameters;

import cz.deznekcz.javafx.parametricPane.ParametricPane;
import cz.deznekcz.javafx.parametricPane.ParametricTraverser;
import cz.deznekcz.javafx.parametricPane.parsing.ParameterElement;
import cz.deznekcz.reference.Out.IntegerOut;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;

/**
 * Instances of class {@link PasswordParameter} represent 
 * parameters with visual hidden password (password is not stored in logic case)
 * @author Zdenek Novotny (DeznekCZ)
 * @see ParameterElement#password
 */
public class PasswordParameter extends AParameter<String> {
	
	private PasswordField passwordField;

	/**
	 * Represents a password value
	 * @param id unique identifier
	 * @see ParameterElement#password
	 */
	public PasswordParameter(String id) {
		super(id, ParameterElement.password);
		initComponent();
	}

	@Override
	public void initComponent() {
		passwordField = new PasswordField();
		passwordField.setId(getId());
		passwordField.setTooltip(getTooltip());
		ParametricTraverser.registerElement(passwordField);
	}

	@Override
	public Node getEnclosingComponent() {
		return passwordField;
	}

	@Override
	public StringProperty valueProperty() {
		return passwordField.textProperty();
	}

	@Override
	public void set(String newValue) {
		valueProperty().setValue(newValue);
	}

	@Override
	public String get() {
		return valueProperty().getValue();
	}

	@Override
	public void setEnabled(boolean b) {
		passwordField.setDisable(!b);
	}

	@Override
	public boolean isEnabled() {
		return !passwordField.isDisabled();
	}
	
	@Override
	public void setEditable(boolean b) {
		
	}
	
	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public String fromString(String string) {
		set(string);
		return string;
	}

	/**
	 * Returns loaded instance of {@link AParameter}
	 * @param node instance of {@link org.w3c.dom.Node} (Element)
	 * @param indexOut returns an index in {@link ParametricPane}
	 * @return instance of {@link AParameter}
	 */
	public static PasswordParameter fromXml(org.w3c.dom.Node node, IntegerOut indexOut) {
		indexOut.set(Integer.parseInt(ParameterElement.Param(ParameterElement.INDEX, node)));
		String id = ParameterElement.Param(ParameterElement.ID, node);
		return new PasswordParameter(id);
	}

	@Override
	public void setFocusComponent() {
		if (passwordField.isVisible())
			passwordField.requestFocus();
	}

	@Override
	public boolean isFocusTraversable() {
		return isEnabled();
	}
}
