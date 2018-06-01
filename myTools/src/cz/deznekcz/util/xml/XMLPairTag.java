package cz.deznekcz.util.xml;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

/**
 * Represent all XML pair tags (excluding root element)
 * 
 * @author Zdenek Novotny (DeznekCZ)
 *
 * @param <PARENT> Class type of parent implementation (parent in parent child instances relation)
 * 
 * @see XMLRoot XMLRoot is top PARENT
 * @see XMLPairTag XMLPairTag is closses PARENT or CHILD
 * @see XMLSingleTag XMLRoot is CHILD
 */
public class XMLPairTag<PARENT> extends XMLPairTagBase<PARENT> {

	protected XMLPairTag(String name, PARENT parent, boolean expanded) {
		super(name, parent, expanded);
	}

	@Override
	public XMLSingleTag<XMLPairTag<PARENT>> newSingleTag(String name) {
		return new XMLSingleTag<XMLPairTag<PARENT>>(name, this);
	}

	@Override
	public XMLPairTag<XMLPairTag<PARENT>> newPairTag(String name) {
		return new XMLPairTag<XMLPairTag<PARENT>>(name, this, true);
	}

	@Override
	public XMLPairTag<XMLPairTag<PARENT>> newPairTag(String name, boolean expanded) {
		return new XMLPairTag<XMLPairTag<PARENT>>(name, this, expanded);
	}

	@Override
	public XMLPairTag<PARENT> setText(String text) {
		super.text = text;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<XMLSingleTag<XMLPairTag<PARENT>>> getSingleTag(String name) {
		List<XMLSingleTag<XMLPairTag<PARENT>>> list = new ArrayList<>(children.size());
		if (children.containsKey(name)) for (XMLElement<?, ?> e : children.get(name)) {
			if (e.name.equals(name) && e instanceof XMLSingleTag) list.add((XMLSingleTag<XMLPairTag<PARENT>>) e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<XMLPairTag<XMLPairTag<PARENT>>> getPairTag(String name) {
		List<XMLPairTag<XMLPairTag<PARENT>>> list = new ArrayList<>(children.size());
		if (children.containsKey(name)) for (XMLElement<?, ?> e : children.get(name)) {
			if (e.name.equals(name) && e instanceof XMLPairTag) list.add((XMLPairTag<XMLPairTag<PARENT>>) e);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<XMLElement<XMLPairTag<PARENT>, ?>> getBoothTag(String name) {
		List<XMLElement<XMLPairTag<PARENT>, ?>> list = new ArrayList<>(children.size());
		if (children.containsKey(name)) for (XMLElement<?, ?> e : children.get(name)) {
			if (e.name.equals(name)) list.add((XMLElement<XMLPairTag<PARENT>, ?>) e);
		}
		return list;
	}

	@Override
	public XMLPairTag<PARENT> setTextCDATA(String text) {
		super.text = String.format("<![CDATA[%s]]>", text);
		return this;
	}
	
	@Override
	public XMLPairTag<PARENT> addAttribute(String name, String value) {
		attributes.add(new Pair<String, String>(name, value));
		return this;
	}
	
	@Override
	public XMLPairTag<PARENT> setComment(String comment) {
		super.comment = String.format("<!-- %s -->", comment);
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("<pairTag=%s,\nattributes=%s,\nchildren=%s\n>",name,attributes,children);
	}

}
