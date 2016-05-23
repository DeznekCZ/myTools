package cz.deznekcz.tool.langEditor;

import javafx.beans.property.ObjectProperty;

public class ContextLangKey extends AbstractLangKey {
	
	public ContextLangKey(String string) {
		super(string);
	}

	@Override
	public ObjectProperty<String> nameProperty() {
		return valueProperty();
	}


}
