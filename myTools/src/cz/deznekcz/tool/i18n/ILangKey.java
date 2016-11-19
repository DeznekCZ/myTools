package cz.deznekcz.tool.i18n;

import java.util.IllegalFormatException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.deznekcz.tool.langEditor.LangKey;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 * New type of searching key in lang file.
 * Is a better form for Object oriented programing.
 * <br><b>Using</b>: enum Keys implements ILangKey {KEY_1,KEY_2}
 * <br><b>Output</b>: "Keys.KEY_1", "Keys.KEY_2"
 * @author Zdenek Novotny (DeznekCZ)
 * @version Needs {@link Lang} version 4.0
 * 
 * @see IContextClassLangKey
 * @see IKeysClassLangKey
 * @see Arguments
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
	public default String value(Object... args) {
		if (!Lang.LANGexists(this)) {
			Lang.LANGset(this.symbol(), defaultValue());
		}
		
		return Lang.LANG(this, args);
	}
	
	/**
	 * Method returns a translated value of language key
	 * @see #value(Object...)
	 * @see Lang#LANG(ILangKey, Object...)
	 * @see #symbol()
	 * @return returns translated value
	 */
	public default String value() {
		if (!Lang.LANGexists(this)) {
			Lang.LANGset(this.symbol(), defaultValue());
		}
		return Lang.LANG(this);
	}

	/**
	 * Returns default generated values
	 * @return default string value
	 */
	public default String defaultValue() {
		return LangItem.compile(symbol(), getClass().getAnnotation(Arguments.class).types()).getValue();
	}

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
	 * Returns a simple language key
	 * @param symbol symbol of key
	 * @param defaultValue symbol of key
	 * @return instance of {@link ILangKey}
	 */
	public static ILangKey simple(String symbol, String defaultValue) {
		return new ILangKey() {
			@Override
			public String symbol() {
				return symbol;
			}
			@Override
			public String name() {
				return null;
			}
			@Override
			public String defaultValue() {
				return defaultValue;
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
		final String finalSymbol = ILangKey.this.symbol().concat(symbolExtension);
		
		if (symbolExtension != null) {
			return new ILangKey() {
				@Override
				public String symbol() {
					return finalSymbol;
				}
				@Override
				public String name() {
					return null;
				}
			};
		} else {
			return ILangKey.this;
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
		try {
			final String finalSymbol = String.format(ILangKey.this.symbol(), arguments);
			
			return new ILangKey() {
				@Override
				public String symbol() {
					return finalSymbol;
				}
				@Override
				public String name() {
					return null;
				}
			};
		} catch (IllegalFormatException e) {
			Logger.getGlobal().log(
					Level.FINER, String.format("LangKey<%s>[%s]:\n%s",
						ILangKey.this.getClass().getSimpleName(), 
						ILangKey.this.symbol(),
						e.getLocalizedMessage()
					));
			return ILangKey.this;
		}
	}

	/**
	 * Returns new {@link StringBinding} for observable value with instances of {@link ILangKey}
	 * @param simpleKey {@link ObservableValue} of {@link ILangKey}
	 * @return new instance of {@link StringBinding}
	 */
	public static ObservableValue<? extends String> binding(ObservableValue<ILangKey> simpleKey) {
		return new StringBinding() {
			{
				bind(simpleKey);
			}
			@Override
			protected String computeValue() {
				return simpleKey.getValue().value();
			}
		};
	}
	
	public default ILangKey initDefault(String defaultValue) {
		Lang.LANGset(this.symbol(), defaultValue);
		return this;
	}
}
