package cz.deznekcz.tool.langEditor;

import javafx.scene.control.TreeItem;

public abstract class AbstractLangKey extends TreeItem<String> implements IKeyNode {
	public AbstractLangKey(String id) {
		super(id);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + valueProperty().get() + " @" + hashCode();
	}
}
