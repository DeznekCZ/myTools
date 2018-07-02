package cz.deznekcz.util.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.deznekcz.reference.OutString;
import cz.deznekcz.util.ForEach;


/**
 * Handles loading of XML document.
 * @author Zdenek Novotny (DeznekCZ)
 * @see #from(Document)
 * @see #fromFile(String)
 * @see #fromFile(File)
 * @see XML
 */
public class XMLStepper {

	public static class StepList implements Step, Iterable<Step> {

		private Step parent;
		private List<Node> list;
		private List<Step> stepList;
		

		public StepList(String listValueName, NodeList childNodes, Step parent) {
			this.list = new ArrayList<>();
			this.stepList = new ArrayList<>();
			for (Node node : ForEach.DOMNodeIterable(childNodes)) {
				if (node.getNodeName().equals(listValueName))
				{
					list.add(node);
					stepList.add(new StepNode(node, this));
				}
			}
			this.parent = parent;
		}

		public StepList() {
			// FOR CLONE
		}

		@Override
		public void setXmlNode(Node node) {
			
		}

		@Override
		public Node getXmlNode() {
			return parent.getXmlNode();
		}

		@Override
		public Step getParent() {
			return parent;
		}

		/**
		 * @see StepList#forEach
		 * @see StepList#asList()
		 * @see StepList#asNodeList()
		 */
		@Override
		@Deprecated
		public Step getNode(String path) throws XMLStepperException {
			return this;
		}
		
		/**
		 * @see StepList#forEach
		 */
		@Override
		@Deprecated
		public StepList getList(String path) throws XMLStepperException {
			return this;
		}
		
		@Override
		public void collectText(List<String> collector) {
			forEach((step)->collector.add(step.text()));
		}

		/**
		 * Applies loop consumer for each elements that matches filter test.
		 * @param filter instance or lambda of {@link Predicate}&lt;{@link Step}&gt;
		 * @param loop instance or lambda of {@link Consumer}&lt;{@link Step}&gt;
		 * @see Iterable#forEach(Consumer)
		 */
		public void foreach(Predicate<Step> filter, Consumer<Step> loop) {
			for (Step step : stepList) {
				if (filter.test(step))
					loop.accept(step);
			}
		}

		/**
		 * Alternatively can be used {@link StepList this} instance
		 * @return instance of {@link List}. While using do not remove elements, is able no roll back.
		 */
		public List<Node> asNodeList() {
			return list;
		}

		public List<Step> asList() {
			return stepList;
		}

		@Override
		public Iterator<Step> iterator() {
			return stepList.iterator();
		} 
	}
	
	public static class StepNONE implements Step {

		@Override
		public Node getXmlNode() {
			return null;
		}

		@Override
		public Step getParent() {
			return null;
		}

		@Override
		public void setXmlNode(Node node) {
			
		}
		
	}

	@SuppressWarnings("serial")
	public static class XMLStepperException extends RuntimeException {
		public static final String ELEMENT = "Element not exists: \"";
		public XMLStepperException(String message) {
			super(message);
		}
		public static XMLStepperException notExists(String type, String value) {
			return new XMLStepperException(type + value + "\"");
		}
	}

	public static class StepNode implements Step {

		private Node node;
		private Step parent;

		public StepNode(Node current, Step parent) throws XMLStepperException {
			this.parent = parent;
			this.node = current;
		}

		@Override
		public Node getXmlNode() {
			return node;
		}

		@Override
		public Step getParent() {
			return parent;
		}

		@Override
		public void setXmlNode(Node node) {
			this.node = node;
		}

	}
	
	public static class StepDocument implements Step {
		private Document document;
		private Node rootNode;

		public StepDocument(Document document) {
			this.document = document;
			this.rootNode = document;
		}

		@Override
		public Node getXmlNode() {
			return rootNode;
		}
		
		@Override
		public Step getParent() {
			return new StepNONE();
		}

		@Override
		public void setXmlNode(Node node) {
			rootNode = node;
		}
		
		public Document getXMLDocument() {
			return document;
		}
	}

	public static interface Step {
		public default Step getNode(String path) throws XMLStepperException {
			if (path.contains("/")) {
				String rootPath = path.substring(0, path.lastIndexOf('/'));
				String rootEnclosing = path.substring(path.lastIndexOf('/') + 1, path.length());
				return getNode(rootPath).getNode(rootEnclosing);
			} else {
				for (Node node : ForEach.DOMNodeIterable(getXmlNode().getChildNodes())) {
					if (node.getNodeName().equals(path))
						return new StepNode(node, this);
				}
			}
			throw XMLStepperException.notExists(XMLStepperException.ELEMENT,path);
		}
		
		public void setXmlNode(Node node);
		public Node getXmlNode();
		public Step getParent();
		
		public default String attribute(String name) {
			OutString result = OutString.init();
			ForEach.start(ForEach.DOMNodeIterableMap(getXmlNode().getAttributes()), (node) -> {
				if (node.getNodeName().startsWith(name))
				{
					result.set(node.getNodeValue());
					return false;
				}
				else
				{
					return true;
				}
			});
			return result.get();
		}
		public default <R> R attribute(String name, Function<String, R> converter) {
			OutString result = OutString.init();
			ForEach.start(ForEach.DOMNodeIterableMap(getXmlNode().getAttributes()), (node) -> {
				if (node.getNodeName().startsWith(name))
				{
					result.set(node.getNodeValue());
					return false;
				}
				else
				{
					return true;
				}
			});
			return converter.apply(result.get());
		}
		
		public default StepList getList(String path) throws XMLStepperException {
			if (path.contains("/")) {
				String rootPath = path.substring(0, path.lastIndexOf('/'));
				String rootEnclosing = path.substring(path.lastIndexOf('/') + 1, path.length());
				return getNode(rootPath).getList(rootEnclosing);
			} else {
				return new StepList(path, getXmlNode().getChildNodes(), this);
			}
		}

		default void collectText(List<String> collector) {
			collector.add(text());
		}

		default boolean hasElement(String name)
		{
			if (this.getXmlNode().hasChildNodes())
				for (Node node : ForEach.DOMNodeIterable(getXmlNode().getChildNodes()))
					if (node.getNodeName().equals(name))
						return true;
				
			return false;
		}

		default boolean hasAttribute(String name)
		{
			if (this.getXmlNode().hasAttributes())
				for (Node node : ForEach.DOMNodeIterableMap(getXmlNode().getAttributes()))
					if (node.getNodeName().startsWith(name))
						return true;
				
			return false;
		}

		public default String text() { return getXmlNode().getTextContent(); }
	}
	
	public static StepDocument from(Document document) {
		return new StepDocument(document);
	}

	
	public static boolean hasAttribute(Step step)
	{
		return step.getXmlNode() != null && step.getXmlNode().hasAttributes();
	}

	public static StepDocument fromFile(String xml) throws Exception {
		return fromFile(new File(xml));
	}

	public static StepDocument fromFile(File xml) throws Exception {
		return from(XMLLoader.load(xml).getOwnerDocument());
	}
}
