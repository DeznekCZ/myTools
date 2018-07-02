package cz.deznekcz.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import cz.deznekcz.reference.Out;

/**
 * XML loading provider
 * @author Zdenek Novotny (DeznekCZ)
 *
 */
public class XMLLoader {

//	private static final Alert xmlError;
//	static {
//		xmlError = new Alert(AlertType.ERROR);
//	}

	public static Node load(File xmlFile) throws Exception {
		Out<Exception> eOut = Out.init();
		Node node = load(xmlFile, eOut);
		if (eOut.isNull())
			return node;
		else
			throw eOut.get();
	}

	public static Node load(File xmlFile, Out<Exception> exception) {
		try {
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = parser.parse(xmlFile);
			return document.getDocumentElement();
		} catch (IOException | SAXException | IllegalArgumentException | ParserConfigurationException e) {
			exception.set(e);
			return null;
		}
	}

	public static void showError(Exception e) {
		e.printStackTrace();
//		xmlError.setContentText(e.getMessage());
//		xmlError.showAndWait();
	}

	public static boolean save(File xml, Node rootNode) {
		return save(xml, rootNode, transformer -> {}, Out.init());
	}

	public static boolean save(File xml, Node rootNode, Out<TransformerException> exception) {
		return save(xml, rootNode, transformer -> {}, exception);
	}

	public static boolean save(File xml, Node rootNode, Consumer<Transformer> parameters) {
		return save(xml, rootNode, parameters, Out.init());
	}

	public static boolean save(File xml, Node rootNode, Consumer<Transformer> parameters, Out<TransformerException> exception) {
		try {
			// write the content into xml file
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        parameters.accept(transformer);
	        DOMSource source = new DOMSource(rootNode.getOwnerDocument());
	        StreamResult result = new StreamResult(xml);
	        transformer.transform(source, result);
	        // Output to console for testing
	        StreamResult consoleResult = new StreamResult(System.out);
	        transformer.transform(source, consoleResult);
			System.out.println("Document \""+xml.getName()+"\" has been stored");
			return true;
		} catch (TransformerException e) {
			System.out.println("Document \""+xml.getName()+"\" not be stored");
			exception.set(e);
			return false;
		}
	}

	public static void save(File xml, String xmlAsText) throws Exception {
		PrintStream ps = new PrintStream(xml, "UTF8");
		ps.println(xmlAsText);
		ps.close();
	}

	public static void newXml(File xml, String rootNodeName) throws Exception {
		Out<Exception> exception = Out.init();
		newXml(xml, rootNodeName, exception);
		if (!exception.isNull())
			throw exception.get();
	}

	public static void newXml(File xml, String rootNodeName, Out<Exception> exception) {
		try {
			// write the content into xml file
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document document = builder.newDocument();
	        Element root = document.createElement(rootNodeName);
	        document.appendChild(root);
	        DOMSource source = new DOMSource(document);
	        StreamResult result = new StreamResult(xml);
	        transformer.transform(source, result);
	        // Output to console for testing
	        StreamResult consoleResult = new StreamResult(System.out);
	        transformer.transform(source, consoleResult);
			System.out.println("Document \""+xml.getName()+"\" has been stored");
		} catch (TransformerException | ParserConfigurationException e) {
			System.out.println("Document \""+xml.getName()+"\" not be stored");
			exception.set(e);
		}
	}

}
