package cz.deznekcz.tool.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import cz.deznekcz.reference.OutArray;
import cz.deznekcz.reference.OutString;
import cz.deznekcz.util.ForEach;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Lanuguage configuration class<br><br>
 * 
 * An singleton class that can use static import.
 * Class generate a custom files. Files is written
 * in UTF-8 and can be rewrited. Used extension
 * is *.lng. Every value of {@link LangItem} can
 * use default formating symbols.
 * 
 * <br><br>Usage:<br>
 * <br>- import static cz.deznekcz.Lang.*;
 * <br>- set up method: LANGload("language_fileName");
 * <br>- String s = LANG("cus-TOM_5ym bol");
 * <br>- String s = LANG("cus-TOM_5ym bol", var1, var2);
 * <br>- String s = LANGlined("cus-TOM_5ym bol");
 * <br>- String s = LANGlined("cus-TOM_5ym bol", var1, var2);
 * <br>- tear down method: LANGgenerate("language_fileName");
 * <br>&nbsp;&nbsp;&nbsp;&nbsp; //on close aplication
 * 
 * <br><br>GUI commands:<br>
 * <br>- LANGset("cus-TOM_5ym bol", "value %d/n/next line");
 * 
 * @author Zdenek Novotny (DeznekCZ)
 * @version 4.0.1 (JavaFX - ResourceBundle getable)
 */
public class Lang {
	
	public static final String LANG_SHORT_NAME = "_lang_short";
	public static final String DEFAULT_SHORT_NAME = "en_US";

	/** Singleton instance */
	private static Lang instance;
	
	/** List of used symbols */
	//private final static List<LangItem> SYMBOLS;
	private Properties SYMBOLS;
	/** Load default language */
	static {
		//SYMBOLS = new EqualArrayList<LangItem>();
		//@Deprecated LANGload("english");
	}
	
	/** Current used language */
	private String langName;

	/** Singleton constructor of {@link Lang} */
	private Lang(String langName) {
		this.langName = langName;
	}
	
	/**
	 * Method refactors an old *.lng file to new *.xlng file. It may change a charset.
	 * @param langName
	 * @throws IOException
	 */
	public static void LANGconvert(String langName) throws IOException {
		File lng = new File("./lang/"+langName+".lng");
		File xlng = new File("./lang/"+langName+".xlng");
		Lang l = new Lang(langName);
		l.SYMBOLS.clear();
		l.SYMBOLS.load(new FileInputStream(lng));
		l.SYMBOLS.storeToXML(new FileOutputStream(xlng)
				, "Converted from early version");
		instance = l;
	}
	


	public static void LANGload(Locale locale) {
		Locale.setDefault(locale);
		LANGload(locale.toString());
	}

	/**
	 * Method loads language from a {@link Lang} file.
	 * by specific name. That method is used as factory
	 * method. If the language file does not exist, program uses an symbol to display
	 * @param langName {@link String} value
	 */
	public static void LANGload(String langName) {
		boolean loaded = false;
		try {
			if (instance != null) {
				LANGgererate();
				instance.SYMBOLS.clear();
			}
			instance = new Lang(langName);
			File f = new File("./lang/"+langName+".xlng");
			instance.SYMBOLS = new Properties();
			if (f.exists()) {
				instance.SYMBOLS.loadFromXML(new FileInputStream(f));
				if (!instance.SYMBOLS.containsKey(LANG_SHORT_NAME))
					instance.SYMBOLS.put(LANG_SHORT_NAME, langName);
				loaded = true;
			}
			loadBundle(langName);
			
		} catch (IOException | MissingResourceException e) {
			System.err.println(e.getLocalizedMessage());
			if (!loaded)
				LANGgererate(langName);
		}
	}

	private static void loadBundle(String langName) throws IOException, MissingResourceException {
		Locale.setDefault((OutArray.from(langName.split("_")).to((value) -> new Locale(value[0], value[1]))));
		
		URL[] urls = {new File("lang").toURI().toURL()};
		ClassLoader loader = new URLClassLoader(urls);
		ResourceBundle bundle = ResourceBundle.getBundle("lang", new Locale(langName), loader);
		ForEach.start(ForEach.enumeration(bundle.getKeys()), (String key) -> {
			LANGset(key, bundle.getString(key));
			return true;
		});
	}

	/**
	 * Generates a {@link Lang} file with used symbols.
	 * <br><font color="red">WARNING!</font>
	 *  - method rewrite previous version of {@link Lang} file
	 * @return true/false
	 */
	public static boolean LANGgererate() {
		//try {
			File f = new File("lang");
			if (!f.exists()) {
				f.mkdir();
			}
//			f = new File("lang/"+instance.langName+".lng");
//			SYMBOLS.list(new PrintStream(f));
			try {
				instance.SYMBOLS.storeToXML(
						new FileOutputStream("lang/"+instance.langName+".xlng"), 
						"Generated by Lang class made by DeznekCZ");
			} catch (IOException e) {
				System.err.println("Lang symbols are not stored by reason: "+e.getLocalizedMessage());
			}
			
			
			
		/*
			PrintStream out = 
				new PrintStream("lang/"+instance.langName+".lng", "utf-8");
			
			out.print(LANGlist());
			
			out.close();
			
			return true;*//*
		} catch (FileNotFoundException e) {
			// wrong encoding, or file creating error
			return false;
		}*/
		return true;
	}

	/**
	 * Generates a {@link Lang} file with used symbols.
	 * <br><font color="red">WARNING!</font>
	 *  - method rewrite previous version of {@link Lang} file
	 * @param langName {@link String} value
	 * @return true/false
	 */
	public static boolean LANGgererate(String langName) {
		instance.langName = langName;
		return LANGgererate();
	}
	
	/**
	 * Method returns a {@link String} value of language symbol<br><br>
	 * <b>using:</b> symbol_name, out_string_param...
	 * @param symbol array of params
	 * @param args array of params
	 * @return a {@link String} value
	 * 
	 * @since 4.0
	 * @see String#format({@link ILangKey} {@link Enum}, Object...)
	 */
	public static String LANG(ILangKey symbol, Object... args) {
		if (args == null || args.length == 0) {
			return newOrExistingKey(symbol.symbol());
		} else {
			OutString finalValue = OutString.empty();
			OutString mistake = OutString.empty();
			
			if (symbol.getClass().isAnnotationPresent(Arguments.class)) {
				if (!ArgumentsTest.test(
						symbol.getClass().getAnnotation(Arguments.class), 
						args, symbol, finalValue, mistake))
					System.err.println(mistake.get());
				return finalValue.get();
			} else {
				try {
					return String.format(newOrExistingKey(symbol.symbol()), args);
				} catch (FormatFlagsConversionMismatchException e) {
					String newValue = newOrExistingKey(symbol.symbol()).concat(ArgumentsTest.writeObjects(args));
					instance.SYMBOLS.setProperty(symbol.symbol(), newValue);
					return String.format(newValue, args);
				}
			}
		}
		
//		try {
//			return String.format(langItem.getValue(), args);
//		} catch (FormatFlagsConversionMismatchException e) {
//			return langItem.getValue();
//		}
	}
	
	private static String newOrExistingKey(String symbol) {
		String value = instance.SYMBOLS.getProperty(symbol);
		if (value == null) {
			value = "<".concat(symbol).concat(">");
			instance.SYMBOLS.setProperty(symbol, value);
		}
		return value;
	}

	/**
	 * Returns a list of language symbols. Every symbol
	 * is written in one line of {@link String}
	 * @return {@link String} value
	 */
	public static String LANGlist() {
		
		List<Object> list = Arrays.asList(instance.SYMBOLS.values().toArray());
		
		for (Object obj : list) {
			System.out.println(obj.toString());
		}
		
		Collator.getInstance(new Locale(instance.SYMBOLS.getProperty(LANG_SHORT_NAME)));
		return "nothing";
	}
	
	/**
	 * Method sets a value of {@link LangItem} specified by symbol.
	 * @param symbol {@link String} value
	 * @param value {@link String} value
	 * 
	 * @see #LANG(String, Object...)
	 */
	public static void LANGset(String symbol, String value) {
		instance.SYMBOLS.setProperty(symbol, value);
	}

	/**
	 * Method returns an instance {@link LangItem} by specific symbol.
	 * <br><font color="red">WARNING!</font>
	 *  - if NOT exists, create that symbol
	 * @param symbol {@link String} value
	 * @param args values to be formated
	 * @return instance of {@link LangItem} 
	 */
	private static LangItem LANGgetItem(String symbol, Object... args) {
		/*
		// Comparing LangItem to String
		int index = SYMBOLS.indexOf(symbol);
		 
		if (index < 0) {
			LangItem langItem = new LangItem(symbol, args);
			SYMBOLS.add(langItem);
			return langItem;
		} else {
			return SYMBOLS.get(index);
		}
		*/
		if (instance == null || instance.SYMBOLS == null)
			throw new NullPointerException("LANG symbols are not loaded. Before ese LANG is needed to use method LANGLoad(langName)");
		
		String prop = instance.SYMBOLS.getProperty(symbol);
		if (prop == null) {
			LangItem lng = LangItem.compile(symbol, args);
			instance.SYMBOLS.put(symbol, lng.getValue());
			return lng;
		} else {
			return LangItem.restore(symbol, prop);
		}
	}
	
	private static ResourceBundle langResourceBundle = new ResourceBundle() {

		@Override
		protected Object handleGetObject(String key) {
			return instance.SYMBOLS.get(key);
		}

		@Override
		public boolean containsKey(String key) {
			LANGgetItem(key);
			return true;
		}

		@Override @Deprecated
		public Enumeration<String> getKeys() { return null;	}
	};

	/**
	 * Returns instance of resource bundle
	 * @return instance of {@link ResourceBundle}
	 */
	public static ResourceBundle asResourceBundle() {
		return langResourceBundle;
	}
}

