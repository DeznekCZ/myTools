package cz.deznekcz.tool.langEditor;

import java.io.File;

import org.w3c.dom.Node;

import javafx.beans.property.ObjectProperty;

public class RootLangKey extends AbstractLangKey {
	
	private Node rootNode;
	private File xml;

	public RootLangKey(String fileName, File xml, Node rootNode) {
		super(fileName);
		this.xml = xml;
		this.rootNode = rootNode;
	}

	@Override
	public ObjectProperty<String> nameProperty() {
		return valueProperty();
	}

	public File getXml() {
		return xml;
	}

	public void setXml(File xml) {
		this.xml = xml;
		setValue(xml.getName());
	}

	public Node getDocumentRoot() {
		return rootNode;
	}
}
