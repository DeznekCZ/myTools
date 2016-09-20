package cz.deznekcz.tool.langEditor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

public class CathegoryLangKey extends AbstractLangKey {
	
	public CathegoryLangKey(String string) {
		super(string);
	}

	@Override
	public ObjectProperty<String> nameProperty() {
		return valueProperty();
	}

}
