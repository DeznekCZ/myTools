package cz.deznekcz.javafx.parametricPane.parsing;

import java.io.File;
import java.util.function.BiFunction;

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
import cz.deznekcz.reference.OutInteger;
import cz.deznekcz.tool.RandomAccessList;
import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.util.xml.XMLLoader;
import javafx.application.Platform;
import cz.deznekcz.javafx.components.Dialogs;

public enum ParameterElement implements ILangKey {
	text       ( TextParameter    ::fromXml ),
	check      ( CheckParameter   ::fromXml ),
	browse_file( BrowseParameter  ::fromXml ),
	browse_dir ( BrowseParameter  ::fromXml ),
	list       ( ListParameter    ::fromXml ),
	password   ( PasswordParameter::fromXml ),
	
	editable((node, list) -> {
		NodeList elements = node.getChildNodes();
		RandomAccessList<AParameter<?>> paramaters = new RandomAccessList<>();
		for(int i = 0; i < elements.getLength(); i++) {
			Node child = elements.item(i);
			ParameterElement.fromXMLName(child.getNodeName()).getParameter(child, paramaters);
		}
		String values = ParameterElement.Param(ParameterElement.VALUES, node);
		String defaultValue = ParameterElement.Param(ParameterElement.DEFAULT, node);
		for(int i = 0; i < paramaters.size(); i++) {
			AParameter<?> v = paramaters.get(i);
			if (v != null)
				list.add(i, v.editable(values, Boolean.parseBoolean(defaultValue)));
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
		String defaultValue = ParameterElement.Param(ParameterElement.DEFAULT, node);
		for(int i = 0; i < paramaters.size(); i++) {
			AParameter<?> v = paramaters.get(i);
			if (v != null)
				list.add(i, v.enabled(values, Boolean.parseBoolean(defaultValue)));
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
	public static final String DEFAULT = "default";
	public static final String EDITABLE = "editable";
	public static final String EXTENSION = "extension";
	
	private static final File XML_DEFINITION_FILE = new File("./config/parametric_pane.xml");
	
	public static ParameterElement fromXMLName(String name) {
		if (name.startsWith("#")) return ParameterElement.no_element;
		return ParameterElement.valueOf(name);
	}
	
	private ElementFunction<AParameter<?>> elementFunction;
	
	private ParameterElement(ElementFunction<AParameter<?>> elementFunction) {
		this.elementFunction = elementFunction;
	}
	
	private ParameterElement(BiFunction<Node, OutInteger, AParameter<?>> fromXml) {
		this.elementFunction = (node, list) -> {
			OutInteger indexOut = OutInteger.create();
			AParameter<?> param = fromXml.apply(node, indexOut);
			list.add( indexOut.get(), param );
		};
	}
	
	@Override
	public String symbol() {
		return ParametricPane.class.getSimpleName() + "." + name();
	}

	/**
	 * Returns value of attribute of node.
	 * @param id attribute ID
	 * @param node selected node
	 * @return {@link String} value
	 */
	public static String Param(String id, Node node) {
		NamedNodeMap params;
		Node paramNode;
		if ((params = node.getAttributes()) != null 
				&& (paramNode = params.getNamedItem(id)) != null)
			return paramNode.getNodeValue();
		else 
			return "";
	}
	
	/**
	 * Returns result of parsing node to list of parameters
	 * @param node parsed node
	 * @param parameterList output list of parameters
	 * @throws DOMException exception while parsing
	 */
	public void getParameter(Node node, RandomAccessList<AParameter<?>> parameterList) throws DOMException {
		elementFunction.returnValue(node, parameterList);
	}

	/**
	 * Loads settings of pane from file "config/parametric_pane.xml"
	 * @return array of instances of {@link AParameter}
	 */
	public static AParameter<?>[] loadFromXML() {
		RandomAccessList<AParameter<?>> list = new RandomAccessList<>();
		list.setOnlyWritable(true);
		try {
			Node root = XMLLoader.load(XML_DEFINITION_FILE);
			NodeList elements = root.getChildNodes();
			for (int i = 0; i < elements.getLength(); i++) {
				Node element = elements.item(i);
				ParameterElement type = ParameterElement.fromXMLName(element.getNodeName());
				type.getParameter(element, list);
			}
		} catch (Exception e) {
			Dialogs.EXCEPTION.show(e);
			Platform.exit();
		}
		return list.toArray(new AParameter<?>[list.count()]);
	}

	private ILangKey translate = extended(".%s-label");
	
	private ILangKey tooltip = extended(".%s-tooltip");
	
	/**
	 * Creates a translation symbol of name for parameter id
	 * @param id unique id of parameter
	 * @return translated {@link String}
	 */
	public String translate(String id) {
		return translate.format(id).value();
	}

	/**
	 * Creates a tooltip symbol of name for parameter id
	 * @param id unique id of parameter
	 * @return translated {@link String}
	 */
	public String tooltip(String id) {
		return tooltip.format(id).value();
	}
}
