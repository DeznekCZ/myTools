package cz.deznekcz.tool.i18n;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.deznekcz.reference.Out;
import cz.deznekcz.reference.OutArrays;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Pair;

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

public interface ILangKey extends ObservableValue<String> {

	public class Base implements ILangKey {

		private String name;
		private String symbol;

		public Base(String name, String symbol) {
			this.name = name;
			this.symbol = symbol;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public String symbol() {
			return symbol;
		}
	}

	HashMap<String, String> DEFAULTS = new HashMap<>();

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
		String last = DEFAULTS.get(this.symbol());
		if (last == null) {
			Arguments anno = getClass().getAnnotation(Arguments.class);
			last = LangItem.compile(symbol(), anno == null ? null : anno.types()).getValue();
			DEFAULTS.put(this.symbol(), last);
		}
		return last;
	}

	/**
	 * Returns a simple language key
	 * @param symbol symbol of key
	 * @return instance of {@link ILangKey}
	 */
	public static ILangKey simple(String symbol) {
		return new ILangKey.Base("Simple: \\"+symbol+"\\", symbol);
	}

	/**
	 * Returns a simple language key
	 * @param symbol symbol of key
	 * @param defaultValue symbol of key
	 * @return instance of {@link ILangKey}
	 */
	public static ILangKey simple(String symbol, String defaultValue) {
		return new ILangKey.Base("Simple: \\"+symbol+"\\", symbol).initDefault(defaultValue);
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
			return new ILangKey.Base("Extended: \\"+symbol()+"\\", finalSymbol);
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
			final String finalSymbol = String.format(symbol(), arguments);
			
			return new ILangKey.Base("Formated: \\"+symbol()+"\\", finalSymbol);
		} catch (IllegalFormatException e) {
			Logger.getGlobal().log(
					Level.FINER, String.format("LangKey<%s>[%s]:\n%s",
						getClass().getSimpleName(), 
						symbol(),
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
		DEFAULTS.put(this.symbol(), defaultValue);
		return this;
	}

	default String getValue() {
		return value();
	}

	public static class LangChangeListener implements Comparable<LangChangeListener>, ChangeListener<String>, InvalidationListener {

		private InvalidationListener invalidator;
		private ILangKey ilk;
		private ChangeListener<? super String> changer;

		public LangChangeListener(ILangKey iLangKey, ChangeListener<? super String> listener) {
			ilk = iLangKey;
			changer = listener;
		}
		
		public LangChangeListener(ILangKey iLangKey, InvalidationListener listener) {
			ilk = iLangKey;
			invalidator = listener;
		}

		@Override
		public int compareTo(LangChangeListener o) {
			return ilk.symbol().compareTo(o.ilk.symbol());
		}

		@Override
		public void invalidated(Observable observable) {
			invalidator.invalidated(ilk);
		}

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			changer.changed(ilk,oldValue,ilk.value());
		}
		
	}
	
	@Override
	default void addListener(ChangeListener<? super String> listener) {
		Lang.reloadRequestsChange.add(new LangChangeListener(this, listener));
	}

	@Override
	default void removeListener(ChangeListener<? super String> listener) {
		Lang.reloadRequestsChange.remove(new LangChangeListener(this, listener));
	}

	@Override
	default void addListener(InvalidationListener listener) {
		Lang.reloadRequestsChange.add(new LangChangeListener(this, listener));
	}

	@Override
	default void removeListener(InvalidationListener listener) {
		Lang.reloadRequestsChange.remove(new LangChangeListener(this, listener));
	}
	
	@SuppressWarnings("rawtypes")
	public static HashMap<String, Pair<ILangKey,Pair<Out[],InvalidationListener>>> argumentedILK = new HashMap<>();
	
	@SuppressWarnings("rawtypes")
	default ObservableValue<? extends String> argumented(Out... args) {
		Pair<ILangKey,Pair<Out[],InvalidationListener>> argumented = argumentedILK.get(this.symbol());
		if (argumented != null) {
			Pair<Out[],InvalidationListener> removing = argumented.getValue();
			for (Out out : removing.getKey()) {
				out.removeListener(removing.getValue());
			}
		}
		argumented = new Pair<>(this, new Pair<>(args,new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				for (LangChangeListener listener : Lang.reloadRequestsInvalidate) {
					if (listener.ilk.symbol().equals(ILangKey.this.symbol()))
						listener.invalidator.invalidated(listener.ilk);
				}
				for (LangChangeListener listener : Lang.reloadRequestsChange) {
					if (listener.ilk.symbol().equals(ILangKey.this.symbol()))
						listener.changer.changed(listener.ilk, "", listener.ilk.value(OutArrays.mapToValues(args)));
				}
			}
		}));
		
		return this;
	}
}
