package cz.deznekcz.javafx.configurator.components.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.components.Command;
import cz.deznekcz.javafx.configurator.components.support.RefreshOnChange;
import cz.deznekcz.reference.Out;
import cz.deznekcz.reference.OutArray;
import cz.deznekcz.reference.OutBoolean;
import cz.deznekcz.reference.OutString;
import cz.deznekcz.util.Builder;
import cz.deznekcz.util.Utils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;

public class CommandInstance extends Thread implements RefreshOnChange {
	
	private static int index = 0;
	
	private static enum Force {
		/**  */
		INMEDIATELY,
		USER_INTERRUPT,
		COMPLETE,
		ERROR,
		INTERRUPT
	}
	
	private static final File TEMP_FILE = new File("temp");
	static {
		TEMP_FILE.mkdirs();
		TEMP_FILE.deleteOnExit();
	}

	private Command command;
	private boolean invalid;
	private Tab tab;
	private Out<Configurator.command> state;
	private Out<Configurator.command> fxState;
	private MenuItem item;
	private Menu menu;
	private String cmd;
	private String args;
	private String dir;
	private RadioButton node;
	private File standartOutputFile;
	private File standartInputFile;
	private File standartErrorFile;
	private Exception error;
	
	private TextArea outputTextArea;
	private TextArea errorTextArea;
	private TextField cmdLine;
	private Button cmdSend;

	private Button cmdInterruptButton;
	private Button cmdExportButton;
	
	private BorderPane commandPane;
	private Process process;

	public CommandInstance(Command command) {
		super(command.getText() + ":" + index);
		this.command = command;
		this.cmd = command.getCmd();
		this.args = command.getArgs();
		this.dir = command.getDir();
		this.state = Out.init(Configurator.command.STATE_RUN);
		this.fxState = this.state.fxThread();
		
		node = new RadioButton();
		node.textProperty().bind(Bindings.concat(
				index + ": ", command.textProperty(), 
				" ", OutString.bindFormat(
						Configurator.command.STATE, 
						(Out<?>) fxState.bindTransform(OutString::init, (cmd) -> cmd.value())
					),
				args.length() > 0 ? Bindings.concat("\n", Configurator.command.ARGS, args) : "",
				dir .length() > 0 ? Bindings.concat("\n", Configurator.command.DIR,  dir ) : ""
				));
		node.setTextAlignment(TextAlignment.LEFT);
		node.setAlignment(Pos.CENTER_LEFT);
		node.getStyleClass().add("command-instance-button");
		
		try {
			this.standartOutputFile = File.createTempFile("commandOutput", ".log");
			this.standartOutputFile.deleteOnExit();
			this.standartInputFile = File.createTempFile("commandInput", ".log");
			this.standartInputFile.deleteOnExit();
			this.standartErrorFile = File.createTempFile("commandError", ".log");
			this.standartErrorFile.deleteOnExit();
		} catch (IOException e1) {
			Dialog.EXCEPTION.show(e1, Configurator.command.NO_LOG_FILE.value(node.getText()));
		}
		
		ContextMenu contextMenu = new ContextMenu(OutArray.from(
				Builder.create(new MenuItem())
				.set((menuItem) -> {
					menuItem.textProperty().bind(Configurator.command.SAVE_LOG);
					menuItem.setOnAction(CommandInstance.this::saveLog);
					menuItem.setDisable(standartOutputFile == null);
				})
				.build(),
				Builder.create(new MenuItem())
				.set((menuItem) -> {
					menuItem.textProperty().bind(Configurator.command.CLOSE);
					menuItem.setOnAction((e) -> CommandInstance.this.exitInstance(Force.USER_INTERRUPT));
				})
				.build()
				
				).getValue());
		node.setContextMenu(contextMenu);
		
		if (command.isRunnable()) {
			tab = new Tab();
			tab.textProperty().bind(Bindings.concat(
					index + ": ", command.getInstancesStage().titleProperty(), 
					" ", OutString.bindFormat(
							Configurator.command.STATE, 
							(Out<?>) fxState.bindTransform(OutString::init, (cmd) -> cmd.value())
						)
					));
			tab.setOnCloseRequest((e) -> {
				if (!exitInstance(Force.USER_INTERRUPT)) e.consume();
			});
			tab.selectedProperty().addListener((o,l,n) -> {
				node.setSelected(n);
			});
			node.selectedProperty().addListener((o,l,n) -> {
				if (n != tab.isSelected()) node.setSelected(tab.isSelected());
			});
			
			Configurator.getCtrl().openCommand(tab);
			
			node.setOnAction((e) -> {
				Configurator.getCtrl().selectCommand(tab);
			});
			
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
			
			outputTextArea = new TextArea();
			outputTextArea.setEditable(false);
			outputTextArea.setStyle("-fx-font-family: monospace;");
			
			errorTextArea = new TextArea();
			errorTextArea.setEditable(false);
			errorTextArea.setStyle("-fx-font-family: monospace; -fx-font-color: red; ");
			errorTextArea.setPrefHeight(50);
			
			BorderPane logs = new BorderPane();
			logs.setCenter(outputTextArea);
			logs.setBottom(errorTextArea);

			cmdExportButton = new Button(Configurator.command.EXPORT_LOG.value());
			cmdExportButton.setOnAction((event) -> {
				if (Dialog.ASK.show(Configurator.command.ASK_STORE_LOG.value(node.getText()),
						ButtonType.YES, Utils.array(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL)) == ButtonType.YES) {
					saveLog(null);
				}
			});
			cmdExportButton.disableProperty().bind(new BooleanBinding(){
				{
					bind(fxState);
				}
				@Override
				protected boolean computeValue() {
					return Configurator.command.STATE_RUN == fxState.get();
				}
			});
			
			cmdInterruptButton = new Button(Configurator.command.INTERRUPT.value());
			cmdInterruptButton.setOnAction((event) -> {
				if (doInterrupt()) exitInstance(Force.INTERRUPT);
			});
			cmdInterruptButton.disableProperty().bind(new BooleanBinding(){
				{
					bind(fxState);
				}
				@Override
				protected boolean computeValue() {
					return Configurator.command.STATE_RUN != fxState.get();
				}
			});
			
			cmdSend = new Button(Configurator.command.SEND.value());
			cmdLine = new TextField();
			cmdLine.setOnKeyPressed((event) -> {
				if (event.getCode() == KeyCode.ENTER) cmdSend.getOnAction().handle(null);
			});
			
			HBox cmdBox = new HBox(cmdLine, cmdSend, cmdInterruptButton, cmdExportButton);
			cmdBox.getStyleClass().add("parameters");
			HBox.setHgrow(cmdLine, Priority.ALWAYS);
			
			commandPane = new BorderPane();
			commandPane.setCenter(logs);
			commandPane.setBottom(cmdBox);
			
			tab.setContent(commandPane);
			
			index++;
			start();
		} else {
			Dialog.EXCEPTION.show(new IllegalAccessError(Configurator.command.NOT_RUNABLE.value(command.getText())));
			invalid = true;
		}
	}

	public static class Runnability extends BooleanBinding implements ChangeListener<String> {
		private ExecutorService service = Executors.newSingleThreadExecutor();
		private Future<?> lastCall;
		private BooleanProperty computed = new SimpleBooleanProperty(false);
		private BooleanProperty result = new SimpleBooleanProperty(false);
		private Command command;
		
		public Runnability(Command command) {
			bind(computed);
			command.cmdProperty().addListener(this);
			command.dirProperty().addListener(this);
			this.command = command;
			
			Configurator.getServices().add(service);
			
			// RUN FIRST CHECK
			refresh();
		}
		
		@Override
		protected boolean computeValue() {
			return computed.get() && result.get();
		}

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			computed.set(false);
			
			if (lastCall != null && !lastCall.isDone()) {
				lastCall.cancel(true);
				lastCall = null;
			}
			final String cmd = command.getCmd();
			final String dir = command.getDir();
			
			if (isLocal(cmd, dir)) {
				result.set(true);
				computed.set(true);
			} else {
				result.set(false);
				lastCall = service.submit(() -> {
					boolean resultV = isInPath(cmd);
					Platform.runLater(()->{
						result.set(resultV);
						computed.set(true);
					});
				});
			}
		}
		
		@Override
		protected void finalize() throws Throwable {
			service.shutdownNow();
			super.finalize();
		}

		public void refresh() {
			changed(null, null, null);
		}
	}

	protected static boolean isInPath(String cmd) {
		if (cmd == null || cmd.length() == 0) return false;
		Map<String, String> enviroments = System.getenv();
		
		for (Entry<String, String> entry : enviroments.entrySet()) {

			String[] paths = entry.getValue().split(";");
			try { 
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
			return (  (  dir == null 
				      || dir.length() == 0
				      ) 
				      && new File(cmd).exists()
				   )  
				   || 
				   (  new File(dir + "\\" + cmd).exists()
				   );
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void run() {
		if (invalid) exitInstance(Force.INMEDIATELY);
		try {
			ProcessBuilder pb = new ProcessBuilder();
			if (cmd.endsWith(".bat")) {
				pb.command(splitArguments("cmd.exe @echo off /c " + cmd, args));
			} else {
				pb.command(splitArguments(cmd, args));
			}
			if (dir.length() > 0)
				pb.directory(new File(dir));
			pb.redirectOutput(standartOutputFile);
			pb.redirectInput(standartInputFile);
			pb.redirectError(standartErrorFile);
			process = pb.start();

			BufferedReader outputReader = new BufferedReader(new FileReader(standartOutputFile));
			BufferedReader errorReader = new BufferedReader(new FileReader(standartErrorFile));
			
			cmdSend.setOnAction((event) -> {
				try {
					PrintStream stream = new PrintStream(standartInputFile);
					stream.println(cmdLine.getText());
					stream.close();
					cmdLine.setText("");
				} catch (Exception e) {
					Dialog.EXCEPTION.show(e);
				}
			});
			
			while (process.isAlive()) {
				handle(outputReader, outputTextArea);
				handle(errorReader, errorTextArea);
				Thread.sleep(100);
			}

			cmdLine.setDisable(true);
			cmdSend.setDisable(true);
			
			handle(outputReader, outputTextArea);
			handle(errorReader, errorTextArea);

			outputReader.close();
			errorReader.close();
			
			int returnKey = process.exitValue();
			Exit.Type returnValue = Exit.Type.FAIL;
			for (Exit exit : command.getExits()) {
				if (exit.getId() == returnKey) {
					returnValue = exit.getType();
					break;
				}
			}
			if (returnValue == Exit.Type.SUCCESS || returnKey == 0) {
				state.set(Configurator.command.STATE_COMPLETE);
			} else if (returnValue == Exit.Type.FAIL && state.get() != Configurator.command.STATE_STOPPED) {
				state.set(Configurator.command.STATE_ERROR);
			} else if (returnValue == Exit.Type.CANCEL) {
				state.set(Configurator.command.STATE_STOPPED);
			} else {
				state.set(Configurator.command.STATE_COMPLETE);
			}
				
		} catch(IllegalThreadStateException e) {
			state.set(Configurator.command.STATE_STOPPED);
		} catch (Exception e) {
			error = e;
			exitInstance(Force.ERROR);
		}
		
		Platform.runLater(this::doRefresh); // Refresh on finish
	}

	private String[] splitArguments(String command, String arguments) {
		if (arguments.contains("\"")) {
			List<String> argList = new ArrayList<>();
			argList.add(command);
			
			// (("[^"]*")|([^" =]+=("[^"]*"|[^" =]*))|[^" =]+) // original regex
			Matcher matcher = Pattern.compile("((\"[^\"]*\")|([^\" =]+=(\"[^\"]*\"|[^\" =]*))|[^\" =]+)").matcher(arguments);
			while(matcher.find()) {
				argList.add(matcher.group());
			}
			
			return argList.toArray(new String[argList.size()]);
		} else {
			return (command + " " + arguments).split(" ");
		}
	}

	private void handle(BufferedReader reader, TextArea textArea) throws Exception {
		while (reader.ready()) {
			CharBuffer buffer = CharBuffer.allocate(1024);
			int len = reader.read(buffer);
			if (len > 0 && len < 1024) {
				String text = new String(buffer.array()).substring(0, len);
				Platform.runLater(()->textArea.appendText(text));
				break;
			} else if (len > 0) {
				String text = new String(buffer.array());
				Platform.runLater(()->textArea.appendText(text));
			}
		}
	}

	private boolean exitInstance(Force force) {
		switch (force) {
		case INMEDIATELY: 
			if (menu != null) menu.getItems().remove(item);
			command.getRunningCommands().remove(this); 
			state.set(Configurator.command.STATE_ERROR);
			return true;
			
		case USER_INTERRUPT:
			//={"Question", "Default button", "Buttons"}, 
			if (state.equalsTo(Configurator.command.STATE_COMPLETE) 
			||  state.equalsTo(Configurator.command.STATE_ERROR)  
			||  state.equalsTo(Configurator.command.STATE_STOPPED)
			||  doInterrupt()) {
				ButtonType askLogSave = Dialog.ASK.show(Configurator.command.ASK_STORE_LOG.value(node.getText()),
						ButtonType.YES, Utils.array(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL));
				if (askLogSave == ButtonType.YES) {
					OutBoolean canceled = OutBoolean.FALSE();
					ActionEvent event = new ActionEvent(canceled, null);
					saveLog(event);
					if (!canceled.get()) {
						command.removeInstance(this);
						return true;
					} else {
						return false;
					}
				} else if (askLogSave == ButtonType.NO) {
					command.removeInstance(this);
					return true;
				}
			}
			return false;
			
		case INTERRUPT:
			if (process != null && process.isAlive())
				process.destroy();
			state.set(Configurator.command.STATE_STOPPED);
			return false;
			
		case ERROR:
			state.set(Configurator.command.STATE_ERROR);
			Platform.runLater(()->{
				if (error != null) Dialog.EXCEPTION.show(error);
			});
			return false;
			
		default:
			
			return false;
		}
	}
	
	private boolean doInterrupt() {
		return ButtonType.YES == Dialog.ASK.show(Configurator.command.ASK_INTERRUPT.value(node.getText()),
				ButtonType.YES, Utils.array(ButtonType.YES, ButtonType.NO)
				);
	}

	public String getCmd() {
		return cmd;
	}
	
	public String getArgs() {
		return args;
	}

	public RadioButton getButton() {
		return node;
	}
	
	public void saveLog(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(Command.getLogOutputDirectory());
		File file = chooser.showSaveDialog(null);
		if (file == null && event != null && event.getSource() != null && event.getSource() instanceof OutBoolean)
		{
			((OutBoolean) event.getSource()).set(true);
		}
		else if (file != null)
		{
			try {
				Utils.stream(new FileInputStream(standartOutputFile), new PrintStream(file));
			} catch (IOException e) {
				if (standartOutputFile == null)
					Dialog.EXCEPTION.show(e, Configurator.command.NO_LOG_FILE.value(node.getText()));
				else
					Dialog.EXCEPTION.show(e, Configurator.command.SAVE_EXCEPTION.value(node.getText()));
			}
		}
	}

	public MenuItem getMenuItem() {
		return item;
	}
	
	public Tab getTab() {
		return tab;
	}

	public static void executeSilent(Command command) {
		String cmd  = command.getCmd();
		String args = command.getArgs();
		String dir  = command.getDir();
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command(cmd, args);
			if (dir.length() > 0)
				pb.directory(new File(dir));
			pb.start();
		} catch (Exception e) {
			Dialog.EXCEPTION.show(e, Configurator.command.COMMAND_EXECUTION.value());
		}
	}
}
