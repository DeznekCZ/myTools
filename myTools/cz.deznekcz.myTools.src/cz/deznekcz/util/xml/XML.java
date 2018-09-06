package cz.deznekcz.util.xml;

import cz.deznekcz.reference.OutString;

/**
 * Represent structure of XML document.
 * Could be exported to {@link String} via method {@link #write()}
 *
 *  <br><br>Example:
<br>String output = {@link XML#init(String, Type, String) XML.init("root",Type.UTF8,"comment")}
<br>&nbsp;&nbsp;{@link XML#root() .root()}
<br>&nbsp;&nbsp;&nbsp;&nbsp;{@link XMLPairTagBase#newPairTag(String) .newPairTag("value")}</code>
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{@link XMLElement#setComment(String) .setComment("comment of")}</code>
<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{@link XMLElement#setText(String) .setText("textValue")}</code>
<br>&nbsp;&nbsp;&nbsp;&nbsp;{@link XMLElement#close() .close()}</code>
<br>&nbsp;&nbsp;{@link XMLElement#close() .close()}</code>
<br>{@link XML#write() .write()}
 * @author Zdenek Novotny (DeznekCZ)
 *
 * @see XMLRoot is CHILD
 * @see #root()
 */
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
	private XMLRoot root;

	/**
	 * Creates an instance of {@link XML}
	 *
	 * <br>{@link XML#init(String, Type, String) calls init(String, Type.UTF8, "")}
	 *
	 * @param root name of root element
	 * @return new instance of {@link XML} builder
	 *
	 * @see #init(String, Type) init(String, Type, "")
	 * @see #init(String, Type, String) init(String, Type, String)
	 */
	public static XML init(String root) {
		return new XML(root, Type.UTF8, "");
	}

	/**
	 * Creates an instance of {@link XML}
	 *
	 * <br>{@link XML#init(String, Type, String) calls init(String, Type, "")}
	 *
	 * @param root name of root element
	 * @param head {@link Type} of XML document (default = {@link Type#UTF8})
	 * @return new instance of {@link XML} builder
	 *
	 * @see #init(String) init(String, Type.UTF8, "")
	 * @see #init(String, Type, String) init(String, Type, String)
	 */
	public static XML init(String root, Type head) {
		return new XML(root, head, "");
	}

	/**
	 * Creates an instance of {@link XML}
	 *
	 * @param root name of root element
	 * @param head {@link Type} of XML document (default = {@link Type#UTF8})
	 * @param comment comment for {@link XMLRoot root} element (default = "")
	 * @return new instance of {@link XML} builder
	 *
	 * @see #init(String) init(String, Type.UTF8, "")
	 * @see #init(String, Type) init(String, Type, "")
	 */
	public static XML init(String root, Type head, String comment) {
		return new XML(root, head, comment);
	}

	private XML(String root, Type head, String comment) {
		this.head = head == null ? Type.UTF8.value : head.value;
		this.comment = ((comment == null || comment.length() == 0) ? "" : String.format("<!-- %s -->", comment));
		this.root = new XMLRoot(root, this);
	}

	public XMLRoot root() {
		return this.root;
	}

	@Override
	public String toString() {
		return "XML: " + root.toString();
	}

	/**
	 * Converts {@link XML} tree to {@link String}
	 * @return string value of XML tree
	 */
	public String write() {
		OutString builder = OutString.init();

		builder.appendLn  (head);
		builder.appendLn  ("");
		builder.appendLnIf((t) -> t != null && t.length() > 0, comment);
		builder.append    (root.write(0, root.expanded))
		;

		return builder.get();
	}

	/**
	 * Adds comment after XML header
	 * @param comment comment value
	 * @return returns builded {@link XML}
	 */
	public XML comment(String comment) {
		this.comment = ((comment == null || comment.length() == 0) ? "" : String.format("<!-- %s -->", comment));
		return this;
	}
}
