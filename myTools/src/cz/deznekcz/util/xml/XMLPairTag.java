package cz.deznekcz.util.xml;

import javafx.util.Pair;

public class XMLPairTag<SROOT> extends XMLPairTagBase<SROOT> {

	public XMLPairTag(String name, SROOT parent, boolean expanded) {
		super(name, parent, expanded);
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLPairTag<SROOT> getThis() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLSingleTag<XMLPairTag<SROOT>> newSingleTag(String name) {
		return new XMLSingleTag<XMLPairTag<SROOT>>(name, getThis());
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLPairTag<XMLPairTag<SROOT>> newPairTag(String name) {
		return new XMLPairTag<XMLPairTag<SROOT>>(name, this, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLPairTag<XMLPairTag<SROOT>> newPairTag(String name, boolean expanded) {
		return new XMLPairTag<XMLPairTag<SROOT>>(name, this, expanded);
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLPairTag<SROOT> setText(String text) {
		super.text = text;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLPairTag<SROOT> setTextCDATA(String text) {
		super.text = XML.CDATA(text);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XMLPairTag<SROOT> attribute(String name, String value) {
		attributes.add(new Pair<String, String>(name, value));
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XMLPairTag<SROOT> comment(String comment) {
		super.comment = String.format("<!-- %s -->", comment);
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("<pairTag=%s,\nattributes=%s,\nchildren=%s\n>",name,attributes,children);
	}

}
