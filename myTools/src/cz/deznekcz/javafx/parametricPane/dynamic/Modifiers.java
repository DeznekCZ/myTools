package cz.deznekcz.javafx.parametricPane.dynamic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Modifiers {
	upperCase((value) -> value.toUpperCase()),
	lowerCase((value) -> value.toLowerCase()),
	numberSplit((value) -> {
		if (value.length() == 0) return value;
		int startIndex = 0, endIndex;
		StringBuilder builder = new StringBuilder();
		Matcher formatMatcher = Pattern.compile("([a-zA-Z]+[_a-zA-Z]*[a-zA-Z]+)")
				.matcher(value);
		while (formatMatcher.find()) {
			endIndex = formatMatcher.start();
			if (endIndex != startIndex) {
				builder.append(value.substring(startIndex, endIndex)+"_");
			}
			builder.append(formatMatcher.group()+"_");
			
			startIndex = formatMatcher.end();
		}
		String modified;
		if (startIndex < value.length()) {
			modified = builder.append(value.substring(startIndex, value.length())).toString();
		} else {
			modified = builder.substring(0, builder.length()-1);
		}
		return modified.replaceAll("__", "_");
	});
	@FunctionalInterface
	private static interface Modifier {
		String modify(String value);
	}
	private Modifier mod;
	private Modifiers(Modifier mod) {
		this.mod = mod;
	}
	public String apply(String value) { return mod.modify(value); }
}
