package cz.deznekcz.util.xml;

import javafx.util.Pair;

public class XMLroot extends XMLPairTagBase<XML> {

	public XMLroot(String name, XML parent) {
		super(name, parent, true);
	}

	@Override
	public XML close() {
		return parent;
	}

	@Override
	public XMLroot getThis() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLSingleTag<XMLroot> newSingleTag(String name) {
		return new XMLSingleTag<XMLroot>(name, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLPairTag<XMLroot> newPairTag(String name) {
		return new XMLPairTag<>(name, this, true);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XMLroot setText(String text) {
		super.text = text;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XMLroot setTextCDATA(String text) {
		super.text = XML.CDATA(text);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLPairTag<XMLroot> newPairTag(String name, boolean expanded) {
		return new XMLPairTag<>(name, this, expanded);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XMLroot attribute(String name, String value) {
		attributes.add(new Pair<String, String>(name, value));
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XMLroot comment(String comment) {
		super.comment = String.format("<!-- %s -->", comment);
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("<rootTag=%s,\nattributes=%s,\nchildren=%s\n>",name,attributes,children);
	}
}
