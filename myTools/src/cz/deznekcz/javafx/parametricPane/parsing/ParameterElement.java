package cz.deznekcz.javafx.parametricPane.parsing;

import static cz.deznekcz.javafx.parametricPane.parsing.IOutSuplier.indexOut;

import java.io.File;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.deznekcz.javafx.parametricPane.ParametricPane;
import cz.deznekcz.javafx.parametricPane.parameters.AParameter;
import cz.deznekcz.javafx.parametricPane.parameters.BrowseParameter;
import cz.deznekcz.javafx.parametricPane.parameters.CheckParameter;
import cz.deznekcz.javafx.parametricPane.parameters.ListParameter;
import cz.deznekcz.javafx.parametricPane.parameters.PasswordParameter;
import cz.deznekcz.javafx.parametricPane.parameters.TextParameter;
import cz.deznekcz.tool.ILangKey;
import cz.deznekcz.tool.RandomAccessList;
import cz.deznekcz.util.XMLLoader;

public enum ParameterElement implements ILangKey {
	text( (node, list) -> {
		AParameter<?> param = TextParameter.fromXml(node, indexOut);
		list.add( indexOut.get(), param );
	}),
	check( (node, list) -> {
		AParameter<?> param = CheckParameter.fromXml(node, indexOut);
		list.add( indexOut.get(), param );
	}),
	browse_file( (node, list) -> {
		AParameter<?> param = BrowseParameter.fromXml(node, indexOut);
		list.add( indexOut.get(), param );
	}), 
	browse_dir( (node, list) -> {
		AParameter<?> param = BrowseParameter.fromXml(node, indexOut);
		list.add( indexOut.get(), param );
	}), 
	list( (node, list) -> {
		AParameter<?> param = ListParameter.fromXml(node, indexOut);
		list.add( indexOut.get(), param );
	}),
	password( (node, list) -> {
		AParameter<?> param = PasswordParameter.fromXml(node, indexOut);
		list.add( indexOut.get(), param );
	}),
	
	editable((node, list) -> {
		NodeList elements = node.getChildNodes();
		RandomAccessList<AParameter<?>> paramaters = new RandomAccessList<>();
		for(int i = 0; i < elements.getLength(); i++) {
			Node child = elements.item(i);
			ParameterElement.fromXMLName(child.getNodeName()).getParameter(child, paramaters);
		}
		String values = ParameterElement.Param(ParameterElement.VALUES, node);
		for(int i = 0; i < paramaters.size(); i++) {
			AParameter<?> v = paramaters.get(i);
			if (v != null)
				list.add(i, v.editable(values));
		}
	}),
	enabled((node, list) -> {
		NodeList elements = node.getChildNodes();
		RandomAccessList<AParameter<?>> paramaters = new RandomAccessList<>();
		for(int i = 0; i < elements.getLength(); i++) {
			Node child = elements.item(i);
			ParameterElement.fromXMLName(child.getNodeName()).getParameter(child, paramaters);
		}
		String values = ParameterElement.Param(ParameterElement.VALUES, node);
		for(int i = 0; i < paramaters.size(); i++) {
			AParameter<?> v = paramaters.get(i);
			if (v != null)
				list.add(i, v.enabled(values));
		}
	}),
	logic((node, list) -> {
		NodeList elements = node.getChildNodes();
		RandomAccessList<AParameter<?>> paramaters = new RandomAccessList<>();
		for(int i = 0; i < elements.getLength(); i++) {
			Node child = elements.item(i);
			ParameterElement.fromXMLName(child.getNodeName()).getParameter(child, paramaters);
		}
		for(int i = 0; i < paramaters.size(); i++) {
			AParameter<?> v = paramaters.get(i);
			if (v != null)
				list.add(i, v.logic());
		}
	}),
	no_element((node, list) -> {});

	@FunctionalInterface
	private interface ElementFunction<T> {
		void returnValue(Node elementNode, RandomAccessList<AParameter<?>> parameterList);
	}
	
	public static final String ID = "id";
	public static final String INDEX = "index";
	public static final String FORMAT = "format";
	public static final String VALUES = "values";
	public static final String FILTER = "filter";
	public static final String EDITABLE = "editable";
	public static final String EXTENSION = "extension";
	
	private static final File XML_DEFINITION_FILE = new File("./config/parametric_pane.xml");
	
	public static ParameterElement fromXMLName(String name) {
		if (name.startsWith("#")) return ParameterElement.no_element;
		return ParameterElement.valueOf(name);
	}
	
	private String elementId;
	private ElementFunction<AParameter<?>> elementFunction;
	
	private ParameterElement(ElementFunction<AParameter<?>> elementFunction) {
		this.elementFunction = elementFunction;
	}
	
	/**
	 * 
	 */
	public String getId() {
		return elementId;
	}
	
	@Override
	public String symbol() {
		return ParametricPane.class.getSimpleName() + "." + name();
	}

	public static String Param(String id, Node node) {
		NamedNodeMap params;
		Node paramNode;
		if ((params = node.getAttributes()) != null 
				&& (paramNode = params.getNamedItem(id)) != null)
			return paramNode.getNodeValue();
		else 
			return "";
	}
	
	public void getParameter(Node node, RandomAccessList<AParameter<?>> parameterList) throws DOMException {
		elementFunction.returnValue(node, parameterList);
	}

	public static AParameter<?>[] loadFromXML() {
		RandomAccessList<AParameter<?>> list = new RandomAccessList<>();
		try {
			Node root = XMLLoader.load(XML_DEFINITION_FILE);
			NodeList elements = root.getChildNodes();
			for (int i = 0; i < elements.getLength(); i++) {
				Node element = elements.item(i);
				ParameterElement type = ParameterElement.fromXMLName(element.getNodeName());
				type.getParameter(element, list);
			}
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return list.toArray(new AParameter<?>[list.count()]);
	}

	private ILangKey translate = extended(".%s-label");
	
	private ILangKey tooltip = extended(".%s-tooltip");
	
	public String translate(String id) {
		return translate.format(id).value();
	}

	public String tooltip(String id) {
		return tooltip.format(id).value();
	}
}
