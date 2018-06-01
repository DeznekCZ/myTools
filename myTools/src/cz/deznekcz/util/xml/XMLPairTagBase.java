package cz.deznekcz.util.xml;

import java.util.List;

/**
 * Class represents ALL pair tag elements in enclosing implementations ({@link XMLPairTag} and {@link XMLRoot})
 * 
 * @author Zdenek Novotny (DeznekCZ)
 *
 * @param <PARENT> Class type of parent implementation (parent in parent child instances relation)
 * 
 * @see XMLPairTag
 * @see XMLRoot
 */
public abstract class XMLPairTagBase<PARENT> extends XMLElement<PARENT, XMLPairTagBase<PARENT>> {

	protected XMLPairTagBase(String name, PARENT parent, boolean expanded) {
		super(name, parent, expanded);
	}

	/**
	 * Adds new single tag element to current tag.
	 * @param name name of new tag element
	 * @return new instance of {@link XMLSingleTag}
	 */
	public abstract XMLSingleTag<? extends XMLPairTagBase<PARENT>> newSingleTag(String name);
	
	/**
	 * Adds new single tag element to current tag.
	 * @param name name of new tag element
	 * @return new instance of {@link XMLPairTag}
	 */
	public abstract XMLPairTag<? extends XMLPairTagBase<PARENT>> newPairTag(String name);
	
	/**
	 * Adds new single tag element to current tag.
	 * @param name name of new tag element
	 * @param expanded defines that element uses more lines (default = true)
	 * @return new instance of {@link XMLPairTag}
	 */
	public abstract XMLPairTag<? extends XMLPairTagBase<PARENT>> newPairTag(String name, boolean expanded);

	/**
	 * Return single tag elements with name.
	 * @param name name of tag element
	 * @return list instances of {@link XMLSingleTag}
	 */
	public abstract List<? extends XMLSingleTag<? extends XMLPairTagBase<PARENT>>> getSingleTag(String name);
	
	/**
	 * Return single tag elements with name.
	 * @param name name of tag element
	 * @return list instances of {@link XMLPairTag}
	 */
	public abstract List<? extends XMLPairTag<? extends XMLPairTagBase<PARENT>>> getPairTag(String name);
	
	/**
	 * Return tag elements with name.
	 * @param name name of tag element
	 * @return list instances of {@link XMLSingleTag} or {@link XMLPairTag}
	 */
	public abstract List<? extends XMLElement<? extends XMLPairTagBase<PARENT>, ?>> getBoothTag(String name);
	
	/**
	 * Remove all children
	 */
	public void clear() {
		children.clear();
	}
	
	/**
	 * Sets text content of XML element
	 * @param text text value
	 * @return this instance of {@link XMLPairTagBase}
	 */
	public abstract XMLPairTagBase<PARENT> setText(String text);
	
	/**
	 * Sets text content of XML element packed in &lt;![CDATA[text]]>
	 * @param text text value
	 * @return this instance of {@link XMLPairTagBase}
	 */
	public abstract XMLPairTagBase<PARENT> setTextCDATA(String text);
	
	/**
	 * Appends text content of XML element
	 * @param text text value
	 * @return this instance of {@link XMLPairTagBase}
	 */
	public abstract XMLPairTagBase<PARENT> appendText(String text);
	
	/**
	 * Appends text content of XML element packed in &lt;![CDATA[text]]>
	 * @param text text value
	 * @return this instance of {@link XMLPairTagBase}
	 */
	public abstract XMLPairTagBase<PARENT> appendTextCDATA(String text);
	
	/**
	 * Returns text value of element
	 * @return text value
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Returns text value of element
	 * @return text value
	 */
	public String getTextCDATA() {
		return text.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", "");
	}
}
