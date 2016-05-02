package cz.deznekcz.tool;

/**
 * New type of searching key in lang file.
 * Is a better form for Object oriented programing.
 * <br><b>Using</b>: enum Keys implements ILangKey {KEY_1,KEY_2}
 * <br><b>Output</b>: "Keys.KEY_1", "Keys.KEY_2"
 * @author Zdenek Novotny (DeznekCZ)
 * @version Needs {@link Lang} version 4.0
 */

public interface ILangKey {
	
	/**
	 * Returns a name of <code>enum</code> constant or user defined
	 * @return instance of {@link String}
	 */
	public String name();
	
	/**
	 * Method returns a symbol value of language key
	 * @return instance of {@link String}
	 * @see #value()
	 * @see #value(Object...)
	 */
	public default String symbol() {return getClass().getSimpleName()+"."+name();}

	/**
	 * Method returns a translated value of language key
	 * @param args translated arguments
	 * @see #value()
	 * @see Lang#LANG(ILangKey, Object...)
	 * @see #symbol()
	 */
	public default String value(Object... args) {return Lang.LANG(this, args);}
	
	/**
	 * Method returns a translated value of language key
	 * @see #value(Object...)
	 * @see Lang#LANG(ILangKey, Object...)
	 * @see #symbol()
	 */
	public default String value() {return Lang.LANG(this);}
}
