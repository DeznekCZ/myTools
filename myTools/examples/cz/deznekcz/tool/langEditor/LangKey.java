package cz.deznekcz.tool.langEditor;

import org.w3c.dom.Node;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LangKey extends AbstractLangKey {


	private StringProperty nodeKey;

	public LangKey(ContextLangKey conLK, CathegoryLangKey catLK, RootLangKey root, String name, Node value, Node attribute) {
		super(name);
		
		getChildren().add(new ValueKey(value, root));
		
		nodeKey = new SimpleStringProperty(attribute.getTextContent());
		nodeKey.addListener((p,l,n) -> {
			value.setTextContent(n);
			TreeGenerator.storeChanges(root);
		});
		nodeKey.bind(new StringBinding() {
			{
				bind(conLK.nameProperty(), catLK.nameProperty(), valueProperty());
			}
			@Override
			protected String computeValue() {
				return (conLK.equals(TreeGenerator.DEFAULT_CONTEXT  ) ? "" : (conLK.nameProperty().get()+"."))
					+  (catLK.equals(TreeGenerator.DEFAULT_CATHEGORY) ? "" : (catLK.nameProperty().get()+"."))
					+  (valueProperty().get());
			}
			
		});
	}

	@Override
	public ObjectProperty<String> nameProperty() {
		return valueProperty();
	}

}
