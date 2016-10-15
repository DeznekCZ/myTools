package cz.deznekcz.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import cz.deznekcz.reference.OutBoolean;
import cz.deznekcz.reference.OutInteger;

public class ArgumentParser {

	private static class Pair {

		public String defaultValue;
		public Consumer<String> action;
		public boolean changed;
		
	}

	public static class Argument {

		private String argument;
		private String help;
		private List<Pair> values;
		private Consumer<String> globalAction;
		private boolean parsed;

		private Argument(String argument, String help) {
			this.argument = argument;
			this.help = help;
			this.parsed = false;
			this.values = new ArrayList<>(0);
		}
		
		public void globalAction(Consumer<String> action) {
			this.globalAction = action;
		}
		
		public void subValue(String defaultValue, Consumer<String> action) {
			Pair pair = new Pair();
			pair.action = action;
			if (defaultValue != null) {
				pair.defaultValue = defaultValue;
			}
			values.add(pair);
		}

		public void parse(String[] args, OutInteger index) {
			if (globalAction != null) {
				globalAction.accept(args[index.get()]);
				parsed = true;
			}
			if (values != null) {
				values.forEach((pair) -> {
					pair.action.accept(args[index.get()]);
					pair.changed = true;
					index.increment();
				});
				parsed = true;
			}
		}
		
		public boolean isParsed() {
			if (!parsed) {
				OutBoolean ob = OutBoolean.TRUE();
				
				for (Pair pair : values) {
					if (!pair.changed && pair.defaultValue != null) {
						pair.action.accept(pair.defaultValue);
					} else {
						ob.and(pair.changed);
					}
				}
				
				parsed = ob.get();
			}
			return parsed;
		}

		@Override
		public String toString() {
			return "Argument [argument=" + argument + ", help=" + help + ", values=" + values + ", globalAction="
					+ globalAction + ", parsed=" + parsed + "]";
		}
	}

	private static HashMap<String, Object> keys = new HashMap<>();

	public static Argument add(String argument, String help) {
		Argument arg = new Argument(argument, help);
		keys.put(argument, arg);
		return arg;
	}

	public static Exception parse(String[] args) {
		Argument lastArgument = null;
		OutInteger i = OutInteger.create();
		try {
			while (i.isLower(args.length)) {
				lastArgument = ((Argument) keys.get(args[i.get()]));
				lastArgument.parse(args, i.increment());
			}
			keys.forEach((key, value) -> {
				if (!((Argument) value).isParsed()) throw new NullPointerException();
			});
		} catch (Exception e) {
			return new Exception("Arguments: dad use of argument: \"" + i + "\" using: " + lastArgument.help, e);
		}
		return null;
	}

	
}
