package cz.deznekcz.util.xml;

import javafx.util.Pair;

public class XMLSingleTag<ROOT> extends XMLelem<ROOT, XMLSingleTag<ROOT>> {

	public XMLSingleTag(String name, ROOT root) {
		super(name, root, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLSingleTag<ROOT> getThis() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public XMLSingleTag<ROOT> attribute(String name, String value) {
		attributes.add(new Pair<String, String>(name, value));
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XMLSingleTag<ROOT> comment(String comment) {
		super.comment = String.format("<!-- %s -->", comment);
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("<singleTag=%s,\nattributes=%s,\n>",name,attributes);
	}

}
