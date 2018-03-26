package cz.deznekcz.util.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.deznekcz.reference.OutString;
import cz.deznekcz.util.ForEach;



public class XMLStepper {

	public static class StepList implements Step {

		private Step parent;
		private List<Node> list;
		

		public StepList(NodeList childNodes, Step parent) {
			this.list = new ArrayList<Node>();
			ForEach.DOMNodeIterable(childNodes).forEach(list::add);
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
			StepList step = new StepList();
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
			Step step;
			for (Node node : list) {
				step = new StepNode(node, node.getLocalName(), this);
				if (filtered.test(step))
					object.accept(step);
			}
		}

		public void foreach(Consumer<Step> object) {
			for (Node node : list) {
				object.accept(new StepNode(node, node.getLocalName(), this));
			}
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

		public StepNode(Node current, String path, Step parent) throws XMLStepperException {
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
				if (node.getNodeName().equals(name))
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
		
		public default StepList getList(String path) throws XMLStepperException {
			String[] pathArray = path.split("/",2);
			if (pathArray.length > 1)
				return getNode(pathArray[0]).getList(pathArray[1]);
			else {
				return new StepList(getXmlNode().getChildNodes(), this);
			}
		}

		default void collectText(List<String> collector) {
			collector.add(getXmlNode().getTextContent());
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
