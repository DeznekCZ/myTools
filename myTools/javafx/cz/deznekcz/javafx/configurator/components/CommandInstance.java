package cz.deznekcz.javafx.configurator.components;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.reference.Out;
import cz.deznekcz.reference.OutString;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;

public class CommandInstance extends Thread {
	
	private static int index = 0;
	
	private static enum Force {
		/**  */
		INMEDIATELY
	}

	private Command command;
	private boolean invalid;
	private Tab tab;
	private Out<Configurator.command> state;
	private MenuItem item;
	private Menu menu;
	private String cmd;
	private String args;
	private Node node;

	public CommandInstance(Command command) {
		super(command.getText() + ":" + index);
		this.command = command;
		this.cmd = command.getCmd();
		this.args = command.getArgs();
		
		if (command.isRunnable()) {
			state = Out.init(Configurator.command.STATE_RUN).fxThread();
			
			tab = new Tab();
			tab.textProperty().bind(Bindings.concat(
					index + ": ", command.textProperty(), 
					" ", OutString.bindFormat(
							Configurator.command.STATE, 
							(Out<?>) state.bindTransform(OutString::init, (cmd) -> cmd.value())
						)
					));
			tab.setOnClosed((e) -> {
				if (menu != null) menu.getItems().remove(item);
				command.getRunningCommands().remove(this);
			});
			
			Configurator.getCtrl().openCommand(tab);
			
			if (command.getCommandsMenu() != null) {
				menu = command.getCommandsMenu();
				
				if (menu.getItems().size() == 1) {
					menu.getItems().add(new SeparatorMenuItem());
				}
				
				item = new MenuItem();
				item.textProperty().bind(
						Bindings.concat(
								tab.textProperty(), 
								" ", 
								Configurator.command.ARGS, 
								args
								)
						);
				menu.getItems().add(item);
			}
			
			index++;
			start();
		} else {
			Dialog.EXCEPTION.show(new IllegalAccessError(Configurator.command.NOT_RUNABLE.value(command.getText())));
			invalid = true;
		}
	}

	public static BooleanBinding runnability(Command command) {
		return new BooleanBinding() {
			{
				bind(command.cmdProperty(), command.dirProperty());
			}
			
			@Override
			protected boolean computeValue() {
				return isLocal(command.getCmd(), command.getDir()) || isInPath(command.getCmd());
			}
		};
	}

	protected static boolean isInPath(String cmd) {
		if (cmd == null || cmd.length() == 0) return false;
		Map<String, String> enviroments = System.getenv();
		
		for (Entry<String, String> entry : enviroments.entrySet()) {
			try {
				String[] paths = entry.getValue().split(";");
				for (String path : paths) {
					File f = new File(path);
					if (!f.exists()) continue;
					
					if (f.isFile() && (path.endsWith(cmd) || path.endsWith(cmd + ".exe"))) return true;
					
					if (f.isDirectory()) {
						for (File subFile : f.listFiles()) {
							if (subFile.isDirectory()) continue;
							if (subFile.getName().endsWith(cmd) 
							||  subFile.getName().endsWith(cmd + ".exe"))
							{
								return true;
							}
						}
					}
				}
				
			} catch (Exception e) {
				// returns false;
			}
		}
		
		return false;
	}

	protected static boolean isLocal(String cmd, String dir) {
		if (cmd == null || cmd.length() == 0) return false;
		try {
			return ( ( dir == null || dir.length() == 0) && new File(cmd).exists()
				)||( new File(dir + "\\" + cmd).exists()
				);
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void run() {
		if (invalid) exitInstance(Force.INMEDIATELY);
		
		ProcessBuilder pb = new ProcessBuilder();
		
		
	}

	private void exitInstance(Force force) {
		switch (force) {
		case INMEDIATELY: command.getRunningCommands().remove(this); break;

		default:
			break;
		}
	}
	
	public String getCmd() {
		return cmd;
	}
	
	public String getArgs() {
		return args;
	}

	public Node getNode() {
		if (node == null) {
			
		}
		return node;
	}
}
