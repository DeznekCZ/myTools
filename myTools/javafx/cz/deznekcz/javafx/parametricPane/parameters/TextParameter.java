package cz.deznekcz.javafx.parametricPane.parameters;

import cz.deznekcz.javafx.parametricPane.ParametricPane;
import cz.deznekcz.javafx.parametricPane.ParametricTraverser;
import cz.deznekcz.javafx.parametricPane.dynamic.Format;
import cz.deznekcz.javafx.parametricPane.parsing.ParameterElement;
import cz.deznekcz.reference.OutInteger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * Instances of class {@link TextParameter} represent text values,
 * can be editable of formated by regex:
 * <br>((\$(\[[a-zA-Z]+\])*\{[a-zA-Z]+\})|[^$]|(\$\$))*
 * <br><b>Examples:</b>
 * <br>Message send to ${receiver} (ID: $[upperCase]{messageId}).
 * <br>Was accepted on ${day}/${month}/${year} ${dayCycle}.
 * @author Zdenek Novotny (DeznekCZ)
 * @see ParameterElement#text
 */
public class TextParameter extends AParameter<String> {
	
	private StringProperty value;
	private TextField textField;
	private String format;
	private Label label;
	private BorderPane borderPane;
	private String defaultValue;
	
	/**
	 * Represents a text parameter
	 * @param id unique identifier
	 * @param defaultEditable string value <b>true</b> or <b>false</b>
	 * @param formating formating regular expression
	 * @see ParameterElement#text
	 */
	public TextParameter(String id, String defaultEditable, String formating) {
		super(id, ParameterElement.text);
		
		this.format = formating;
		
		initComponent();
		setEditable(defaultEditable.length() == 0 || Boolean.parseBoolean(defaultEditable));
	}

	@Override
	protected void initComponent() {
		borderPane = new BorderPane();
		
		textField = new TextField();
		textField.setId(getId());
		textField.setVisible(textField.isEditable());
		textField.setEditable(true);
		textField.setTooltip(getTooltip());
		ParametricTraverser.registerElement(textField);
		
		label = new Label();
		label.setId(getId()+"-label");
		label.setVisible(!textField.isVisible());
		label.setTooltip(getTooltip());
		
		value = new SimpleStringProperty();
		value.addListener((v,l,n) -> {
			if (label.isVisible()) 
				label.setText(n);
			defaultValue = n;
		});
		
		textField.textProperty().addListener((e,l,n) -> {
			valueProperty().setValue(n);
		});
		
//		checkVisibilty();
	}

	private void checkVisibilty() {
		if (label.isVisible()) {
			value.setValue(defaultValue);
			textField.setText(defaultValue);
			borderPane.setCenter(label);
		}
		if (textField.isVisible()) {
			textField.setText(defaultValue);
			borderPane.setCenter(textField);
		}
	}

	@Override
	public Node getEnclosingComponent() {
		if ( format.length() > 0 ) {
			Format.create(format, valueProperty());
			format = "";
		}
		return borderPane;
	}

	@Override
	public StringProperty valueProperty() {
		return value;
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
		textField.setDisable(!b);
		label.setDisable(!b);
		
		checkVisibilty();
	}

	@Override
	public boolean isEnabled() {
		return !textField.isDisabled();
	}

	@Override
	public void setEditable(boolean b) {
		textField.setVisible(b);
		label.setVisible(!b);
		
		checkVisibilty();
	}

	@Override
	public boolean isEditable() {
		return textField.isVisible();
	}

	@Override
	public void fromString(String string) {
		textField.setText(string);
		label.setText(string);
		defaultValue = string;
	}

	/**
	 * Returns loaded instance of {@link AParameter}
	 * @param node intance of {@link org.w3c.dom.Node} (Element)
	 * @param indexOut returns an index in {@link ParametricPane}
	 * @return instance of {@link AParameter}
	 */
	public static TextParameter fromXml(org.w3c.dom.Node node, OutInteger indexOut) {
		indexOut.set(Integer.parseInt(ParameterElement.Param(ParameterElement.INDEX, node)));
		String id = ParameterElement.Param(ParameterElement.ID, node);
		String editable = ParameterElement.Param(ParameterElement.EDITABLE, node);
		String format = ParameterElement.Param(ParameterElement.FORMAT, node);
		return new TextParameter(id, editable, format);
	}

	@Override
	public void setFocusComponent() {
		if (textField.isVisible())
			textField.requestFocus();
	}

	@Override
	public boolean isFocusTraversable() {
		return isEnabled() && isEditable();
	}
}
