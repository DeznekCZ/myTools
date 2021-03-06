package cz.deznekcz.tool.i18n;

//import cz.deznekcz.util.EqualAble;

/**
 * Instances of class {@link LangItem} represents a {@link String}
 * value of an {@link Lang} item defined by specific symbol 
 * 
 * @author Zdenek Novotny (DeznekCZ)
 * @version 3.0
 */
public class LangItem implements Comparable<LangItem>/*, EqualAble*/{
	private final String symbol;
	private String value;
	
	/**
	 * Constructor that creates a new instance of {@link LangItem}.
	 * Sets symbol by param and sets value to: <br>
	 * "&#60symbol,java.lang.Object,java.lang.Integer,...&#62"
	 * @param value 
	 * @param symbol 
	 * @param symbol {@link String} value
	 * @param args values to be formated
	 */
	private LangItem(String symbol, String value) {
		this.symbol = symbol;
		this.value = value;
	}

	/**
	 * Method returns a value of {@link LangItem}.
	 * Value can contains formating symbols.
	 * @return {@link String} value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Method sets a value of {@link LangItem}.
	 * Value can contains formating symbols.
	 * @param value {@link String} value
	 * 
	 * @see String#format(String, Object...)
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Method returns a calling symbol of {@link LangItem}.
	 * @return {@link String} value
	 */
	public String getSymbol() {
		return symbol;
	}
	
//	@Override
//	public boolean equalsTo(Object obj) {
//		return obj != null && (false
//				/* Ask by another instance */
//				|| (obj instanceof LangItem
//						&& ((LangItem) obj).getSymbol().equals(this.getSymbol()))
//				/* Ask by symbol */
//				|| (obj instanceof String
//						&& ((String) obj).equals(getSymbol()))
//		);
//	}

	@Override
	public int compareTo(LangItem o) {
		return getSymbol().compareTo(o.getSymbol());
	}

	public static LangItem restore(String symbol, String value) {
		return new LangItem(symbol, value);
	}

	public static LangItem compile(String symbol, Object[] args) {
		String classes = "<" + symbol;
		
		for (int i = 0; i < args.length; i++) {
			classes += "," + args[i].getClass().getName();
		}
		classes += ">";
		
		return new LangItem(symbol, classes);
	}

	public static LangItem compile(String symbol, Class<?>[] args) {
		String classes = "<" + symbol;
		
		if (args != null) for (int i = 0; i < args.length; i++) {
			classes += "," + args[i].getName();
		}
		classes += ">";
		
		return new LangItem(symbol, classes);
	}
}
