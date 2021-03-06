package cz.deznekcz.tool.langEditor;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.deznekcz.javafx.components.Dialogs;
import cz.deznekcz.tool.i18n.Lang;
import cz.deznekcz.util.xml.XML;
import cz.deznekcz.util.xml.XMLLoader;
import cz.deznekcz.util.xml.XMLRoot;
import cz.deznekcz.util.xml.XMLStepper;
import cz.deznekcz.util.xml.XMLStepper.Step;
import cz.deznekcz.util.xml.XMLStepper.StepDocument;
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
//		XMLLoader.save(root.getXml(), root.getDocumentRoot(), transformer -> 
//	        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, 
//	        		"http://java.sun.com/dtd/properties.dtd")
//		);
//		^ OLD SAVING
		
		StepDocument doc = XMLStepper.from(root.getDocumentRoot().getOwnerDocument());
		
		XMLRoot properties = XML.init(doc.getXMLDocument().getChildNodes().item(0).getNodeName())
				.doctype(doc.getXMLDocument().getDoctype().getName())
				.root();
		
		for (Step entry : doc.getList("entry")) {
			properties.newPairTag("entry", false)
				.addAttribute("key", entry.attribute("key"))
				.setText(entry.text());
		}
		
		try {
			XMLLoader.save(root.getXml(), properties.close());
		} catch (IOException e) {
			Dialogs.EXCEPTION.show(e);
		}
	}

	/**
	 * Returns a reference to renamed document root
	 * @param root document root
	 * @param documentName new name of document
	 */
	public static void renameRoot(RootLangKey root, String documentName) {
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
			node.setTextContent(documentName);
		} else {
			Element element = rootNode.getOwnerDocument().createElement(NODE_ENTRY);
			element.setAttribute(ATTRIBUTE_KEY, Lang.LANG_SHORT_NAME);
			element.setTextContent(documentName);
			rootNode.appendChild(element);
		}
	}

}
