package cz.deznekcz.util.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.deznekcz.reference.OutString;
import cz.deznekcz.util.ForEach;



public class XMLStepper {

	public static class StepList implements Step {

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
					stepList.add(new StepNode(node, listValueName, this));
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

		@Override
		public Step getNode(String path) throws XMLStepperException {
			StepList step = new StepList(); // TODO
			step.list = new ArrayList<>(list);
//			this.list.forEach((Node listNode) -> {
//				ForEach.DOMNodeIterable(listNode.getChildNodes()).forEach((node) -> {
//					if ()
//				});;
//			});
			return step;
		}
		
		@Override
		public StepList getList(String path) throws XMLStepperException {
			return this;
		}
		
		@Override
		public void collectText(List<String> collector) {
			
		}

		public void foreach(Predicate<Step> filtered, Consumer<Step> object) {
			for (Step step : stepList) {
				if (filtered.test(step))
					object.accept(step);
			}
		}

		public void foreach(Consumer<Step> object) {
			foreach((v)->true, object);
		}

		public List<Node> asNodeList() {
			return list;
		}

		public List<Step> asStepList() {
			return stepList;
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
		private String path;

		public StepNode(Node current, String path, Step parent) throws XMLStepperException {
			this.parent = parent;
			this.node = current;
			this.path = path;
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
			String[] pathArray = path.split("/",2);
			for (Node node : ForEach.DOMNodeIterable(getXmlNode().getChildNodes())) {
				if (node.getNodeName().equals(pathArray[0]))
					return new StepNode(node, pathArray[0], this);
			}
			throw XMLStepperException.notExists(XMLStepperException.ELEMENT,pathArray[0]);
		}
		
		public void setXmlNode(Node node);
		public Node getXmlNode();
		public Step getParent();
		public default String attribute(String name) {
			OutString result = OutString.init();
			ForEach.start(ForEach.DOMNodeIterable(getXmlNode().getAttributes()), (node) -> {
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
			ForEach.start(ForEach.DOMNodeIterable(getXmlNode().getAttributes()), (node) -> {
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
			String[] pathArray = path.split("/",2);
			if (pathArray.length > 1)
				return getNode(pathArray[0]).getList(pathArray[1]);
			else {
				return new StepList(pathArray[0], getXmlNode().getChildNodes(), this);
			}
		}

		default void collectText(List<String> collector) {
			collector.add(getXmlNode().getTextContent());
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
				for (Node node : ForEach.DOMNodeIterable(getXmlNode().getAttributes()))
					if (node.getNodeName().startsWith(name))
						return true;
				
			return false;
		}
	}
	
	public static StepDocument from(Document document) {
		return new StepDocument(document);
	}

	
	public static boolean hasAttribute(Step step)
	{
		return step.getXmlNode() != null && step.getXmlNode().hasAttributes();
	}
}
