package cz.deznekcz.tool;

import java.util.Arrays;
import java.util.FormatFlagsConversionMismatchException;
import java.util.IllegalFormatException;

import cz.deznekcz.reference.OutBoolean;
import cz.deznekcz.reference.OutString;

public class ArgumentsTest {

	private static final String INVALID_COUNT = "Invalid count of arguments: expected: %d, given: %d";
	private static final String INVALID_ARGUMENT_CLASS = "Invalid class of argument %d: expected: %s, given: %s";
	private static final String INVALID_TRANSLATE = "Invalid translate, given expression \"%s\" not equals to arguments: %s";

	public static boolean test(Arguments arguments, Object[] instances, ILangKey key, OutString finalValue) {
		return test(arguments, instances, key, finalValue, OutString.empty());
	}
	
	public static boolean test(Arguments arguments, Object[] instances, ILangKey key, OutString finalValue, OutString mistake) {
		// Clearing of error
		mistake.set("\"");
		mistake.append(key.symbol()).append("\" :: ");
		int mistakeTitleLen = mistake.length();
		
		if (instances == null) {
			mistake.append(String.format(INVALID_COUNT, arguments.value().length, 0));
		} else if (arguments.value().length != instances.length) {
			mistake.append(String.format(INVALID_COUNT, arguments.value().length, instances.length));
		} else {
			OutBoolean ok = OutBoolean.TRUE();
			for (int i = 0; i < instances.length; i++) {
				ok.and(arguments.value()[i].isInstance(instances[i]));
				if (ok.getNot()) {
					mistake.append(String.format(INVALID_ARGUMENT_CLASS, i, arguments.value()[i].toString(), instances[i].getClass().toString()));
					break;
				}
			}
			if (ok.get()) {
				try {
					finalValue.set(key.value());
					finalValue.format(instances);
				} catch (FormatFlagsConversionMismatchException e) {
					mistake.append(String.format(INVALID_TRANSLATE, finalValue.get(), Arrays.toString(instances)));
				}
			}
		}
		return mistake.length() <= mistakeTitleLen;
	}
}
