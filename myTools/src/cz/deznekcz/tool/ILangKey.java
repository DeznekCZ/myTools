package cz.deznekcz.tool;

import java.util.IllegalFormatException;

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
	 * @return returns translated value
	 */
	public default String value(Object... args) {return Lang.LANG(this, args);}
	
	/**
	 * Method returns a translated value of language key
	 * @see #value(Object...)
	 * @see Lang#LANG(ILangKey, Object...)
	 * @see #symbol()
	 * @return returns translated value
	 */
	public default String value() {return Lang.LANG(this);}

	/**
	 * Returns a simple language key
	 * @param symbol symbol of key
	 * @return instance of {@link ILangKey}
	 */
	public static ILangKey simple(String symbol) {
		return new ILangKey() {
			@Override
			public String symbol() {
				return symbol;
			}
			@Override
			public String name() {
				return null;
			}
		};
	}
	
	/**
	 * Returns changed copy of <b>this</b> key modified by adding
	 * an extension to symbol
	 * @param symbolExtension a text extension of symbol
	 * @return <b>new</b> instance of {@link ILangKey}
	 */
	public default ILangKey extended(String symbolExtension) {
		ILangKey templateKey = ILangKey.this;
		if (symbolExtension != null) {
			return new ILangKey() {
				@Override
				public String symbol() {
					return templateKey.symbol().concat(symbolExtension);
				}
				@Override
				public String name() {
					return null;
				}
			};
		} else {
			return templateKey;
		}
	}

	/**
	 * Returns changed copy of <b>this</b> key modified by string formating
	 * of symbol
	 * @param arguments formating arguments
	 * @return <b>new</b> instance of {@link ILangKey}
	 * @throws IllegalFormatException from {@link String#format(String, Object...)}
	 * @see String#format(String,Object...)
	 */
	public default ILangKey format(Object...arguments) {
		ILangKey templateKey = ILangKey.this;
		String finalString = String.format(templateKey.symbol(), arguments);
		
		return new ILangKey() {
			@Override
			public String symbol() {
				return finalString;
			}
			@Override
			public String name() {
				return null;
			}
		};
	}
}
