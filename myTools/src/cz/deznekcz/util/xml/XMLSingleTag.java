package cz.deznekcz.util.xml;

import javafx.util.Pair;

/**
 * Represents single tag elements
 * 
 * @author Zdenek Novotny (DeznekCZ)
 *
 * @param <PARENT> Class type of parent implementation (parent in parent child instances relation)
 * 
 * @see XMLRoot XMLRoot is top PARENT
 * @see XMLPairTag XMLPairTag is closses PARENT
 */
public class XMLSingleTag<PARENT> extends XMLElement<PARENT, XMLSingleTag<PARENT>> {

	protected XMLSingleTag(String name, PARENT root) {
		super(name, root, true);
	}

	@Override
	public XMLSingleTag<PARENT> addAttribute(String name, String value) {
		attributes.add(new Pair<String, String>(name, value));
		return this;
	}
	
	@Override
	public XMLSingleTag<PARENT> setComment(String comment) {
		super.comment = String.format("<!-- %s -->", comment);
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("<singleTag=%s,\nattributes=%s,\n>",name,attributes);
	}

}
