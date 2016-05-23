package cz.deznekcz.javafx.parametricPane.parameters;

import cz.deznekcz.javafx.parametricPane.ParametricPane;
import cz.deznekcz.javafx.parametricPane.ParametricTraverser;
import cz.deznekcz.javafx.parametricPane.parsing.ParameterElement;
import cz.deznekcz.reference.Out.IntegerOut;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

/**
 * Instances of class {@link CheckParameter} represent 
 * parameters with state <b>true</b> or <b>false</b>
 * @author Zdenek Novotny (DeznekCZ)
 * @see ParameterElement#check
 */
public class CheckParameter extends AParameter<Boolean> {

	private CheckBox checkBox;
	private boolean checked;
	private StringProperty selectedAsString;
	
	/**
	 * Represents boolean value <b>true</b> or <b>false</b> in text form.
	 * @param id unique identifier
	 * @param defaultBoolean stored boolean value
	 * @see ParameterElement#check
	 */
	public CheckParameter(String id, boolean defaultBoolean) {
		super(id, ParameterElement.check);
		this.checked = defaultBoolean;
		selectedAsString = new SimpleStringProperty();
		initComponent();
	}

	@Override
	protected void initComponent() {
		checkBox = new CheckBox();
		checkBox.setId(getId());
		checkBox.setSelected(checked);
		checkBox.selectedProperty().addListener((b,l,n) -> selectedAsString.setValue(n.toString()));
		checkBox.setTooltip(getTooltip());
		ParametricTraverser.registerElement(checkBox);
	}

	@Override
	public Node getEnclosingComponent() {
		return checkBox;
	}

	@Override
	public StringProperty valueProperty() {
		return selectedAsString;
	}

	@Override
	public void set(Boolean newValue) {
		checkBox.setSelected(newValue);
	}

	@Override
	public String get() {
		return selectedAsString.getValue();
	}

	@Override
	public void setEnabled(boolean b) {
		checkBox.setDisable(!b);
	}

	@Override
	public boolean isEnabled() {
		return !checkBox.isDisabled();
	}

	@Override
	public void setEditable(boolean b) {}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public Boolean fromString(String string) {
		checkBox.setSelected(Boolean.parseBoolean(string));
		return checkBox.isSelected();
	}

	/**
	 * Returns loaded instance of {@link AParameter}
	 * @param node instance of {@link org.w3c.dom.Node} (Element)
	 * @param indexOut returns an index in {@link ParametricPane}
	 * @return instance of {@link AParameter}
	 */
	public static CheckParameter fromXml(org.w3c.dom.Node node, IntegerOut indexOut) {
		indexOut.set(Integer.parseInt(ParameterElement.Param(ParameterElement.INDEX, node)));
		String id = ParameterElement.Param(ParameterElement.ID, node);
		boolean defaultValue = Boolean.parseBoolean(ParameterElement.Param(ParameterElement.FORMAT, node));
		return new CheckParameter(id, defaultValue);
	}

	@Override
	public void setFocusComponent() {
		if (checkBox.isVisible())
			checkBox.requestFocus();
	}

	@Override
	public boolean isFocusTraversable() {
		return isEnabled();
	}
}
