package cz.deznekcz.javafx.parametricPane.parameters;

import java.util.ArrayList;

import org.w3c.dom.NodeList;

import cz.deznekcz.javafx.parametricPane.ParametricPane;
import cz.deznekcz.javafx.parametricPane.ParametricTraverser;
import cz.deznekcz.javafx.parametricPane.parsing.ParameterElement;
import cz.deznekcz.reference.Out.OutInteger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

/**
 * Instances of {@link ListParameter} represent lists of values, 
 * where can be only one selected.
 * <br><b>Note:</b> in further versions will be added more options for values 
 * @author Zdenek Novotny (DeznekCZ)
 *
 */
public class ListParameter extends AParameter<String[]> {

	private String[] value;
	private ComboBox<String> comboBox;
	private StringProperty valueProperty;

	public ListParameter(String id, String...values) {
		super(id, ParameterElement.list);
		value = values;
		valueProperty = new SimpleStringProperty();
		initComponent();
	}

	@Override
	protected void initComponent() {
		comboBox = new ComboBox<>(FXCollections.observableArrayList(value));
		comboBox.setMaxWidth(Double.MAX_VALUE);
		comboBox.setId(getId());
		valueProperty.bind(comboBox.valueProperty());
		comboBox.setTooltip(getTooltip());
		ParametricTraverser.registerElement(comboBox);
	}

	@Override
	public Node getEnclosingComponent() {
		return comboBox;
	}

	@Override
	public StringProperty valueProperty() {
		return valueProperty;
	}

	@Override
	public void set(String[] newValue) {
		comboBox.getItems().clear();
		comboBox.getItems().addAll(newValue);
	}
	
	public void setSelected(String value) {
		comboBox.getSelectionModel().select(value);
	}

	@Override
	public String get() {
		return comboBox.getValue();
	}

	@Override
	public void setEnabled(boolean b) {
		comboBox.setDisable(b);
	}

	@Override
	public boolean isEnabled() {
		return !comboBox.isDisabled();
	}

	@Override
	public void setEditable(boolean b) {
		comboBox.setEditable(b);
	}

	@Override
	public boolean isEditable() {
		return comboBox.isEditable();
	}

	@Override
	public void fromString(String string) {
		setSelected(string);
	}

	/**
	 * Returns loaded instance of {@link AParameter}
	 * @param node intance of {@link org.w3c.dom.Node} (Element)
	 * @param indexOut returns an index in {@link ParametricPane}
	 * @return instance of {@link AParameter}
	 */
	public static ListParameter fromXml(org.w3c.dom.Node node, OutInteger indexOut) {
		indexOut.set(Integer.parseInt(ParameterElement.Param(ParameterElement.INDEX, node)));
		String id = ParameterElement.Param(ParameterElement.ID, node);
		ArrayList<String> values = new ArrayList<>();
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeName().compareTo("value") == 0)
				values.add(list.item(i).getTextContent());
		}
		return new ListParameter(id, values.toArray(new String[values.size()]));
	}

	@Override
	public void setFocusComponent() {
		if (comboBox.isVisible())
			comboBox.requestFocus();
	}

	@Override
	public boolean isFocusTraversable() {
		return isEnabled();
	}

}
