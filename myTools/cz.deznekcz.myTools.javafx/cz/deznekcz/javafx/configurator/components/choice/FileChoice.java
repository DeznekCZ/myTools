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
import cz.deznekcz.javafx.configurator.components.path.WebPath;
import cz.deznekcz.javafx.configurator.components.support.HasDirProperty;
import cz.deznekcz.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class FileChoice extends Choice implements HasDirProperty {

	public static enum OpenType { RUN, OPEN, BROWSE, NOTHING }

	private static class FileChoiceSkin extends FileChoice.ChoiceSkin {

		private static final String PREFIX = "| ";
		private StringProperty dir;
		private Label dirLabel;
		private FilePath openButton;
		private WebPath browseButton;
		private Command runButton;

		private BooleanProperty unnecesary;
		private StringProperty buttonText;
		private StringProperty extensionString;
		private ObservableList<String> extensions;

		private StringProperty commandArgs;
		private ObjectProperty<OpenType> openType;

		private BooleanBinding runnable;
		private BooleanBinding openable;
		private BooleanBinding browseable;

		public FileChoiceSkin(FileChoice text) {
			super(text);

			dir = new SimpleStringProperty();
			dir.addListener((o,l,n) -> text.refresh());

			extensionString = new SimpleStringProperty("");

			extensions = FXCollections.observableArrayList();
			extensions.addListener((ListChangeListener.Change<? extends String> c) -> text.refresh());

			dirLabel = new Label();
			dirLabel.textProperty().bind(Bindings.concat(
					PREFIX, Configurator.choice.DIR, dir, "\n",
					PREFIX, Configurator.choice.EXTENSIONS, extensionString));

			unnecesary = new SimpleBooleanProperty(true);
			unnecesary.bind(Unnecesary.hiddenProperty());
			unnecesary.addListener((o,l,n) -> {
				if (n) getBox().setBottom(null);
				else   getBox().setBottom(dirLabel);
			});

			openType = new SimpleObjectProperty<>(OpenType.NOTHING);
			openType.addListener((o,l,n) -> {
				switch (n) {
				case OPEN:
					if (FileChoiceSkin.this.openButton == null) initOpen();
					FileChoiceSkin.this.getSkinnable()
						.setRightDecorator(FileChoiceSkin.this.openButton);
					break;
				case BROWSE:
					if (FileChoiceSkin.this.browseButton == null) initBrowse();
					FileChoiceSkin.this.getSkinnable()
						.setRightDecorator(FileChoiceSkin.this.browseButton);
					break;
				case RUN:
					if (FileChoiceSkin.this.runButton == null) initCommand();
					FileChoiceSkin.this.getSkinnable()
						.setRightDecorator(FileChoiceSkin.this.runButton);
					break;
				default:
					FileChoiceSkin.this.getSkinnable()
						.setRightDecorator(null);
					break;
				}
			});

			buttonText  = new SimpleStringProperty("");
			commandArgs = new SimpleStringProperty("");

			openable   = openType.isEqualTo(OpenType.OPEN);
			browseable = openType.isEqualTo(OpenType.BROWSE);
			runnable   = openType.isEqualTo(OpenType.RUN);
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

		private void initBrowse() {
			browseButton = new WebPath();
			browseButton.setSelectable(false);
			browseButton.openButtonTextPropterty().bind(getSkinnable().buttonTextProperty());
			browseButton.valueProperty().bind(new StringBinding() {
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

	public FileChoice() {
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
		((FileChoiceSkin) getSkin()).extensionString.setValue(Utils.concat(";", ((FileChoiceSkin) getSkin()).extensions));
		getItems().clear();
		try {
			getItems().addAll(
				Arrays.asList(
					new File(getDir()).listFiles()
				)
				.stream()
				.filter(File::isFile)
				.map(File::getName)
				.filter(fname ->
					getExtensions().stream()
							// Convert to regex
							.map(ext -> ext.replaceAll("[.]", "[.]").replaceAll("[*]", ".*"))
							// Apply regex
							.anyMatch(fname::matches)
				)
				.collect(Collectors.toList())
			);
		} catch (Exception e) {
		}
	}

	public ObservableList<String> getExtensions() {
		return ((FileChoiceSkin) getSkin()).extensions;
	}

	public ObjectProperty<OpenType> openTypeProperty() {
		return ((FileChoiceSkin) getSkin()).openType;
	}

	public void setOpenType(OpenType openType) {
		openTypeProperty().set(openType);
	}

	public OpenType getOpenType() {
		return openTypeProperty().get();
	}

	public BooleanBinding openableProperty() {
		return ((FileChoiceSkin) getSkin()).openable;
	}

	public boolean isOpenable() {
		return openableProperty().get();
	}

	public BooleanBinding browseableProperty() {
		return ((FileChoiceSkin) getSkin()).browseable;
	}

	public boolean isBrowseable() {
		return browseableProperty().get();
	}

	public BooleanBinding runnableProperty() {
		return ((FileChoiceSkin) getSkin()).runnable;
	}

	public boolean isRunnable() {
		return runnableProperty().get();
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
