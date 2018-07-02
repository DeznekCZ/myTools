package cz.deznekcz.javafx.configurator.components.choice;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.javafx.configurator.components.Choice;
import cz.deznekcz.javafx.configurator.components.Command;
import cz.deznekcz.javafx.configurator.components.path.FilePath;
import cz.deznekcz.javafx.configurator.components.support.HasDirProperty;
import cz.deznekcz.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class FileChoice extends Choice implements HasDirProperty {
	
	private static class FileChoiceSkin extends FileChoice.ChoiceSkin {
		
		private StringProperty dir;
		private Label dirLabel;
		private FilePath openButton;
		private Command runButton;
		
		private BooleanProperty unnecesary;
		private BooleanProperty openable;
		private BooleanProperty runnable;
		private StringProperty buttonText;
		private StringProperty commandArgs;

		public FileChoiceSkin(FileChoice text) {
			super(text);
			
			dir = new SimpleStringProperty();
			dir.addListener((o,l,n) -> {
				text.refresh();
			});
			
			dirLabel = new Label();
			dirLabel.textProperty().bind(Bindings.concat(Configurator.choice.DIR, dir));
			
			unnecesary = new SimpleBooleanProperty(true);
			unnecesary.bind(Unnecesary.hiddenProperty());
			unnecesary.addListener((o,l,n) -> {
				if (n) getBox().setBottom(null);
				else   getBox().setBottom(dirLabel);
			});
			
			openable = new SimpleBooleanProperty(false);
			openable.addListener((o,l,n) -> {
				if (!n) getValueDecorator().setBottom(null);
				else    {
					initOpen();
					getValueDecorator().setRight(openButton);
				}
			});

			buttonText = new SimpleStringProperty("");
			commandArgs = new SimpleStringProperty("");
			
			runnable = new SimpleBooleanProperty(false);
			runnable.addListener((o,l,n) -> {
				if (!n) getValueDecorator().setBottom(null);
				else    {
					initCommand();
					getValueDecorator().setRight(runButton);
				}
			});
		}

		private void initOpen() {
			openButton = new FilePath();
			openButton.setSelectable(false);
			openButton.openButtonTextPropterty().bind(getSkinnable().buttonTextProperty());
			openButton.valueProperty().bind(new StringBinding() {
				StringProperty file = getSkinnable().valueProperty();
				{
					bind(dir, file);
				}
				@Override
				protected String computeValue() {
					return dir.get() + "\\" + file.get();
				}
			});
		}

		private void initCommand() {
			runButton = new Command();
			runButton.buttonTextPropterty().bind(getSkinnable().buttonTextProperty());
			runButton.argsProperty().bind(getSkinnable().commandArgsProperty());
			runButton.dirProperty().bind(getSkinnable().dirProperty());
			runButton.cmdProperty().bind(new StringBinding() {
				StringProperty file = getSkinnable().valueProperty();
				{
					bind(dir, file);
				}
				@Override
				protected String computeValue() {
					return dir.get() + "\\" + file.get();
				}
			});
		}

		@Override
		public FileChoice getSkinnable() {
			return (FileChoice) super.getSkinnable();
		}

		@Override
		public Node getNode() {
			return super.getNode();
		}

		@Override
		public void dispose() {
			
		}

	}

	private StringProperty extensions;
	
	public FileChoice() {
		extensions = new SimpleStringProperty("*");
		setSkin(new FileChoiceSkin(this));
		register();
	}

	public StringProperty dirProperty() {
		return ((FileChoiceSkin) getSkin()).dir;
	}
	
	public void setDir(String dir) {
		dirProperty().set(dir);
	}
	
	public String getDir() {
		return dirProperty().get();
	}
	
	public void refresh() {
		getItems().clear();
		try {
			getItems().addAll(
				Arrays.asList(
					new File(getDir()).listFiles()
				)
				.stream()
				.filter((f)->f.isFile())
				.map((f)->f.getName())
				.filter((fname)->{
					String extensions = getExtensions();
					if ("*".equals(extensions)) return true;
					boolean matched = false;
					for (String name : extensions.split(";")) {
						matched |= Pattern.matches(
									name.replaceAll("[.]", "\\[[.]\\]").replaceAll("[*]", "[.][*]")
									, fname);
						if (matched) break;
					}
					return matched;
				})
				.collect(Collectors.toList())
			);
		} catch (Exception e) {
		}
	}
	
	public StringProperty extensionsProperty() {
		return extensions;
	}
	
	public void setExtensions(String extensions) {
		extensionsProperty().set(extensions);
	}
	
	public void setExtensions(String...extensions) {
		extensionsProperty().set(Utils.concat(";",extensions));
	}
	
	public String getExtensions() {
		return extensionsProperty().get();
	}
	
	public BooleanProperty openableProperty() {
		return ((FileChoiceSkin) getSkin()).openable;
	}
	
	public void setOpenable(boolean openable) {
		openableProperty().set(openable);
	}
	
	public boolean isOpenable() {
		return openableProperty().get();
	}
	
	public BooleanProperty runnableProperty() {
		return ((FileChoiceSkin) getSkin()).runnable;
	}
	
	public void setRunnable(boolean openable) {
		openableProperty().set(openable);
	}
	
	public boolean isRunnable() {
		return openableProperty().get();
	}
	
	public StringProperty buttonTextProperty() {
		return ((FileChoiceSkin) getSkin()).buttonText;
	}
	
	public void setButtonText(String dir) {
		buttonTextProperty().set(dir);
	}
	
	public String getButtonText() {
		return buttonTextProperty().get();
	}
	
	public StringProperty commandArgsProperty() {
		return ((FileChoiceSkin) getSkin()).commandArgs;
	}
	
	public void setCommandArgs(String dir) {
		commandArgsProperty().set(dir);
	}
	
	public String getCommandArgs() {
		return commandArgsProperty().get();
	}
	
	public Command getCommand() {
		if (((FileChoiceSkin) getSkin()).runButton == null) ((FileChoiceSkin) getSkin()).initCommand();
		return ((FileChoiceSkin) getSkin()).runButton;
	}
}
