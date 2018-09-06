package cz.deznekcz.util.debug;

import java.util.HashMap;

import cz.deznekcz.util.debug.Debug.Action;

public class Debug {

	public static interface Action {
		
	}

	public static interface Command {
		void apply(Object...args);
	}

	private static final HashMap<String, Command> COMMANDS = new HashMap<>();

	public static Command subCommand(String string, Action... action) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Command subCommand(String string, Command... subCommand) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void addCommand(String string, Runnable... action) {
		
	}

	public static void addCommand(String string, Command... subCommand) {
		COMMANDS.put(string, subCommand(string, subCommand));
	}

}
