package cz.deznekcz.util.xml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.deznekcz.reference.OutString;
import javafx.util.Pair;

/**
 * 
 * @author Zdenek Novotny (DeznekCZ)
 *
 * Represent base of all XML tags
 *
 * @param <PARENT> Class type of parent implementation (parent in parent child instances relation)
 * @param <THIS> Class type of final implementation
 * 
 * @see XMLSingleTag
 * @see XMLPairTag
 * @see XMLRoot
 */
public abstract class XMLElement<PARENT, THIS extends XMLElement<PARENT, THIS>> {

	protected PARENT parent;
	protected String name;
	protected String text;
	protected String comment;
	protected boolean expanded;

	protected List<Pair<String, String>> attributes;
	protected Map<String,List<XMLElement<?,?>>> children;
	
	protected XMLElement(String name, PARENT parent, boolean expanded) {
		if (parent instanceof XMLPairTagBase) {
			XMLPairTagBase<?> cast = (XMLPairTagBase<?>) parent;
			if (!cast.children.containsKey(name)) 
				cast.children.put(name, new LinkedList<>());
			cast.children.get(name).add(this);
		}
		
		this.parent = parent;
		this.name = name;
		this.text = "";
		this.comment = "";
		this.expanded = expanded;
		
		this.children = new HashMap<>();
		this.attributes = new LinkedList<>();
	}

	/**
	 * Returns parent instance
	 * @return instance of {@link XMLElement#PARENT}
	 */
	public PARENT close() {
		return parent;
	}

	/**
	 * Adds an attribute to XML element
	 * @param name name of attribute
	 * @param value value of attribute
	 * @return this instance of {@link XMLElement#THIS} (for builder initialization see {@link XML})
	 */
	public abstract THIS addAttribute(String name, String value);
	/**
	 * Sets comment for XML element
	 * @param comment comment value
	 * @return this instance of {@link XMLElement#THIS} (for builder initialization see {@link XML})
	 */
	public abstract THIS setComment(String comment);
	
	/**
	 * Returns value of comment
	 * @return comment value
	 */
	public String getComment() {
		return comment.substring(4, comment.length() - 3).trim();
	}
	
	/**
	 * 
	 * @param indent
	 * @param parentExpanded
	 * @return
	 */
	public String write(int indent, boolean parentExpanded) {
		if (this instanceof XMLSingleTag) {
			return String.format("%s<%s%s />", parentExpanded ? indent(indent) : "", name, attributes(indent+2));
		} else {
			OutString builder = OutString.init();
			if (parentExpanded) builder.append(indent(indent));
			if (comment.length() > 0) {
				builder.append(comment);
				if (parentExpanded) builder.appendLn("").append(indent(indent));
			}
			builder.append(String.format("<%s%s >", name, attributes(indent + 2)));
			if (parentExpanded && expanded) builder.append('\n');
			for (List<XMLElement<?, ?>> elementList : children.values()) {
				for (XMLElement<?,?> xmlelem : elementList) {
					builder.append(xmlelem.write(indent + 1, parentExpanded && expanded));
					if (parentExpanded && expanded) builder.append('\n');
				}
			}
			
			if (builder.get().endsWith("\n\n")) builder.subSequence(0, builder.length() - 1);
			if (parentExpanded && expanded) builder.append(indent(indent));
			builder.append(text);
			builder.append(String.format("</%s>", name));
			if (parentExpanded && expanded) builder.append('\n');
			return builder.get();
		}
	}

	private String indent(int indent) {
		OutString builder = OutString.init();
		for (int i = 0; i < indent; i++) {
			builder.append("  ");
		}
		return builder.get();
	}

	private String attributes(int indent) {
		OutString builder = OutString.init();
		int counter = 0;
		for (Pair<String, String> pair : attributes) {
			if (expanded && (counter++ & 0xFFFFFFFD) == 0xFFFFFFFD) builder.appendLn(indent(indent));
			builder.append(String.format(" %s=\"%s\"", pair.getKey(), pair.getValue()));	
		}
		return builder.get();
	}
}
