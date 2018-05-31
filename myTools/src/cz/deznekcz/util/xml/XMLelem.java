package cz.deznekcz.util.xml;

import java.util.LinkedList;
import java.util.List;

import cz.deznekcz.reference.OutString;
import javafx.util.Pair;

public abstract class XMLelem<ROOT, T> {

	protected ROOT parent;
	protected String name;
	protected String text;
	protected String comment;
	protected boolean expanded;

	protected List<Pair<String, String>> attributes;
	protected List<XMLelem<?,?>> children;
	
	public XMLelem(String name, ROOT parent, boolean expanded) {
		if (parent instanceof XMLelem)
			((XMLelem<?,?>) parent).children.add(this);
		
		this.parent = parent;
		this.name = name;
		this.text = "";
		this.comment = "";
		this.expanded = expanded;
		
		this.children = new LinkedList<>();
		this.attributes = new LinkedList<>();
	}

	public ROOT close() {
		return parent;
	}

	public abstract <THIS extends XMLelem<ROOT, T>> THIS attribute(String name, String value);
	public abstract <THIS extends XMLelem<ROOT, T>> THIS comment(String comment);

	protected abstract <THIS extends XMLelem<ROOT, T>> THIS getThis();
	
	public String toString(int indent, boolean parentExpanded) {
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
			for (XMLelem<?,?> xmlelem : children) {
				builder.append(xmlelem.toString(indent + 1, parentExpanded && expanded));
				if (parentExpanded && expanded) builder.append('\n');
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
