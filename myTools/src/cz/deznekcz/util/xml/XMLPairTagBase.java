package cz.deznekcz.util.xml;

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
}
