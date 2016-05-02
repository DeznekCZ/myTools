package cz.deznekcz.tool.langEditor;

import org.w3c.dom.Node;

import javafx.beans.property.ObjectProperty;
;
public class ValueKey extends AbstractLangKey {

	public ValueKey(Node value, RootLangKey root) {
		super(value.getTextContent());
		valueProperty().addListener((p,l,n) -> {
			value.setTextContent(n);
			TreeGenerator.storeChanges(root);
		});
	}

	@Override
	public ObjectProperty<String> nameProperty() {
		return valueProperty();
	}

}
