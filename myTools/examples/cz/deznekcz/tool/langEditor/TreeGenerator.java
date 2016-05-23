package cz.deznekcz.tool.langEditor;

import java.io.File;
import java.text.Collator;
import java.util.HashMap;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

import cz.deznekcz.tool.Lang;
import cz.deznekcz.util.XMLLoader;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeGenerator {

	public static final String DEFAULT_CATHEGORY = "%default_cathegory";
	public static final String DEFAULT_CONTEXT = "%default_context";
	public static final String NODE_ENTRY = "entry";
	public static final String ATTRIBUTE_KEY = "key";
	public static final String XML_ROOT = "properties";
	
	public static void from(String fileName, File xml, Node rootNode, TreeView<String> xmlTreeView)
	throws IllegalArgumentException {
		if (!rootNode.getNodeName().equals(XML_ROOT)) 
			throw new IllegalArgumentException(Keys.IO.NOT_A_PROPERTY_FILE.value());
		
		HashMap<String, ContextLangKey> contexts = new HashMap<>();
		HashMap<String, CathegoryLangKey> contextCats = new HashMap<>();
		
		RootLangKey root = new RootLangKey(fileName, xml, rootNode);
		
		xmlTreeView.setRoot(root);
		
		NodeList nodes = rootNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (!node.getNodeName().equals(NODE_ENTRY))
				continue;
			Node attribute = node.getAttributes().getNamedItem(ATTRIBUTE_KEY);
			if (attribute.getTextContent().equals(Lang.LANG_SHORT_NAME))
				continue;
			String fullName = attribute.getTextContent();
			String splitName[] = fullName.split("\\.");
			
			String conName = splitName.length == 3 ? splitName[0] : DEFAULT_CONTEXT;
			ContextLangKey conLK = contexts.get(conName);
			if (conLK == null) {
				conLK = new ContextLangKey(conName);
				contexts.put(conName, conLK);
				root.getChildren().add(conLK);
			}
			String catName = splitName.length >= 2 ? splitName[0 + (splitName.length > 2 ? 1 : 0)] : DEFAULT_CATHEGORY;
			String contextRef = conName+"."+catName;
			CathegoryLangKey catLK = contextCats.get(contextRef);
			if (catLK == null) {
				catLK = new CathegoryLangKey(catName);
				contextCats.put(contextRef, catLK);
				conLK.getChildren().add(catLK);
			}
			String nodeName = splitName[splitName.length-1];
			catLK.getChildren().add(new LangKey(conLK, catLK, root, nodeName, node, attribute));
		}
		String langName = xml.getName().split("\\.")[0];
		renameRoot(root, langName);
		sortValues(root);
	}
	
	private static class TreeItemCollator {
		public static int compare(TreeItem<String> v1, TreeItem<String> v2){
			return Collator.getInstance().compare(v1.getValue(),v2.getValue());
		}
	}

	private static void sortValues(AbstractLangKey root) {
		ObservableList<TreeItem<String>> children = root.getChildren();
		if (children.size() > 0) {
			children.forEach((child)->sortValues((AbstractLangKey) child));
			children.sort(TreeItemCollator::compare);
		}
		
	}

	public static void storeChanges(RootLangKey root) {
		XMLLoader.save(root.getXml(), root.getDocumentRoot(), transformer -> 
	        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, 
	        		"http://java.sun.com/dtd/properties.dtd")
		);
	}

	/**
	 * Returns a reference to renamed document root
	 * @param string new name of document
	 * @return
	 */
	public static void renameRoot(RootLangKey root, String langName) {
		Node rootNode = root.getDocumentRoot();
		NodeList nodes = rootNode.getChildNodes();
		Node node = null;
		boolean found = false;
		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			if (!node.getNodeName().equals(NODE_ENTRY))
				continue;
			Node attribute = node.getAttributes().getNamedItem("key");
			if (!attribute.getTextContent().equals(Lang.LANG_SHORT_NAME))
				continue;
			found = true;
			break;
		}
		if (found) {
			node.setTextContent(langName);
		} else {
			Element element = rootNode.getOwnerDocument().createElement(NODE_ENTRY);
			element.setAttribute(ATTRIBUTE_KEY, Lang.LANG_SHORT_NAME);
			element.setTextContent(langName);
			rootNode.appendChild(element);
		}
	}

}
