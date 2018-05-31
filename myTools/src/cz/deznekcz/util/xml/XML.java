package cz.deznekcz.util.xml;

import cz.deznekcz.reference.OutString;

public class XML {

	public static enum Type {
		UTF8("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		
		String value;
		private Type(String value) {
			this.value = value;
		}
	}

	private String head;
	private String comment;
	private XMLroot root;

	public static XML init(String root) {
		return new XML(root, Type.UTF8, "");
	}

	public static XML init(String root, Type head) {
		return new XML(root, head, "");
	}

	public static XML init(String root, Type head, String comment) {
		return new XML(root, head, comment);
	}

	private XML(String root, Type head, String comment) {
		this.head = head.value;
		this.comment = String.format("<!-- %s -->", comment);
		this.root = new XMLroot(root, this);
	}
	
	public XMLroot root() {
		return this.root;
	}

	public static String CDATA(String text) {
		return String.format("<![CDATA[%s]]>", text);
	}

	@Override
	public String toString() {
		return "XML: " + root.toString();
	}

	public String write() {
		OutString builder = OutString.init();
		
		builder.appendLn  (head);
		builder.appendLn  ("");
		builder.appendLnIf((t) -> t != null && t.length() > 0, comment);
		builder.append    (root.toString(0, root.expanded))
		;
		
		return builder.get();
	}
}
