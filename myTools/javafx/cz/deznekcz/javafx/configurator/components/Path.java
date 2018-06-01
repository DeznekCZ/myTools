package cz.deznekcz.javafx.configurator.components;

import java.awt.Desktop;
import java.util.function.Predicate;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.reference.Out;
import cz.deznekcz.reference.OutBoolean;
import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.util.LiveStorage;
import cz.deznekcz.util.Utils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public abstract class Path extends Control implements Value {

	private final ILangKey OPEN;
	private final ILangKey SELECT;
	private java.io.File last = new java.io.File(".");
	
	private static class PathSkin implements Skin<Path> {

		private Path text;
		private BorderPane box;
		private Label label;
		private TextField value;
		private Button button;
		private BorderPane path;

		public BooleanProperty invalidValue;
		public BooleanProperty selectable;
		private BooleanBinding valueVisible;
		
		public PathSkin(Path text) {
			this.text = text;
			text.getStyleClass().add("path");
			text.setTooltip(new Tooltip());
			
			box = new BorderPane();
			label = new Label();
			value = new TextField();
			button = new Button();
			button.setText(text.SELECT.value());
			button.setOnAction(text::selectPath);
			
			path = new BorderPane();
			path.setCenter(value);
			path.setRight(button);
			path.maxWidthProperty().bind(text.widthProperty().divide(2));
			path.prefWidthProperty().bind(text.widthProperty().divide(2));
			
			box.disableProperty().bind(text.disableProperty());

			label.getStyleClass().add("path-label");	
			value.getStyleClass().add("path-value");

			label.idProperty().bind(text.idProperty().concat("_label"));
			value.idProperty().bind(text.idProperty().concat("_value"));
			button.idProperty().bind(text.idProperty().concat("_button"));

			BorderPane.setAlignment(label, Pos.CENTER_LEFT);
			box.setLeft(label);
			box.setRight(path);
			
			invalidValue = new SimpleBooleanProperty(false);
			value.textProperty().addListener((o,l,n) -> invalidValue.set(!text.getValidator().test(n)));
			
			selectable = new SimpleBooleanProperty(true);
			selectable.addListener((o,l,n) -> {
				if (n) {
					button.setText(text.SELECT.value());
					button.setOnAction(text::selectPath);
					button.disableProperty().unbind();
				}
				else   {
					button.setText(text.OPEN.value());
					button.setOnAction(text::openPath);
					button.disableProperty().bind(invalidValue);
				}
			});
			
			valueVisible = selectable.or(Unnecesary.hiddenProperty().not());
			valueVisible.addListener((o,l,n) -> {
				path.setCenter(n ? value : null);
			});
		}

		@Override
		public Path getSkinnable() {
			return text;
		}

		@Override
		public Node getNode() {
			return box;
		}

		@Override
		public void dispose() {
			
		}

	}
	
	public StringProperty textProperty() {
		return ((PathSkin) getSkin()).label.textProperty();
	}

	public String getText() {
		return textProperty().get();
	}
	
	public void setText(String text) {
		this.textProperty().set(text);
	}
	
	public StringProperty valueProperty() {
		return ((PathSkin) getSkin()).value.textProperty();
	}
	
	public String getValue() {
		return valueProperty().get();
	}
	
	public void setValue(String value) {
		this.valueProperty().set(value);
	}
	
	public StringProperty helpPropterty() {
		return getTooltip().textProperty();
	}
	
	public void setHelp(String prompt) {
		helpPropterty().set(prompt);
	}
	
	public String getHelp() {
		return helpPropterty().get();
	}
	
	public BooleanProperty selectablePropterty() {
		return ((PathSkin) getSkin()).selectable;
	}
	
	public boolean isSelectable() {
		return selectablePropterty().get();
	}
	
	public void setSelectable(boolean selectable) {
		selectablePropterty().set(selectable);
	}
	
	public abstract void openPath(ActionEvent event);
	
	public abstract void selectPath(ActionEvent event);
	
	public abstract Predicate<String> getValidator();
	
	protected Path(ILangKey open, ILangKey select) {
		this.OPEN = open;
		this.SELECT = select;
		setSkin(new PathSkin(this));
	}
	
	public static class Directory extends Path {
		public Directory() {
			super(Configurator.path.OPEN.extended("_DIR"), Configurator.path.SELECT.extended("_DIR"));
		}
		@Override
		public Predicate<String> getValidator() {
			return (value) -> {
				try {
					java.io.File f = new java.io.File(value);
					return f.exists() && f.isDirectory();
				} catch (Exception e) {
					return false;
				}
			};
		}
		@Override
		public void openPath(ActionEvent event) {
			try {
				Desktop.getDesktop().browse(new java.io.File(getValue()).toURI());
			} catch (Exception e) {
				Dialog.EXCEPTION.show(e);
			}
		}
		@Override
		public void selectPath(ActionEvent event) {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle(getText());
			chooser.setInitialDirectory(super.last);
			java.io.File result = chooser.showDialog(null);
			if (result != null) {
				setValue(result.getPath());
			}
		}
	}
	
	public static class File extends Path {
		
		private StringProperty filterHint;
		private StringProperty extensions;

		public File() {
			super(Configurator.path.OPEN.extended("_FILE"), Configurator.path.SELECT.extended("_FILE"));
			filterHint = new SimpleStringProperty(Configurator.path.ALL_FILES.value());
			extensions = new SimpleStringProperty("*");
		}
		@Override
		public Predicate<String> getValidator() {
			return (value) -> {
				try {
					java.io.File f = new java.io.File(value);
					return f.exists() && f.isFile();
				} catch (Exception e) {
					return false;
				}
			};
		}
		@Override
		public void openPath(ActionEvent event) {
			try {
				Desktop.getDesktop().open(new java.io.File(getValue()));
			} catch (Exception e) {
				Dialog.EXCEPTION.show(e);
			}
		}
		@Override
		public void selectPath(ActionEvent event) {
			FileChooser chooser = new FileChooser();
			chooser.setTitle(getText());
			chooser.setInitialDirectory(super.last);
			chooser.getExtensionFilters().add(new ExtensionFilter(getFilterHint(), extensions.get().split(";")));
			java.io.File result = chooser.showOpenDialog(null);
			if (result != null) {
				setValue(result.getPath());
			}
		}
		
		public StringProperty filterHintProperty() {
			return filterHint;
		}
		
		public void setFilterHint(String hint) {
			filterHintProperty().set(hint);
		}
		
		public String getFilterHint() {
			return filterHintProperty().get();
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
			return extensions.get();
		}
	}
	
	public static class Configuration extends Path {
		public Configuration() {
			super(Configurator.path.OPEN.extended("_CONFIG"), Configurator.path.SELECT.extended("_CONFIG"));
		}
		@Override
		public Predicate<String> getValidator() {
			return (value) -> {
				try {
					java.io.File f = new java.io.File(value);
					return f.exists() && f.isFile() && f.getName().endsWith(".run.xml") && LiveStorage.isStorage(f);
				} catch (Exception e) {
					return false;
				}
			};
		}
		@Override
		public void openPath(ActionEvent event) {
			try {
				Configurator.getCtrl().loadConfig(new java.io.File(getValue()));
			} catch (Exception e) {
				Dialog.EXCEPTION.show(e);
			}
		}
		@Override
		public void selectPath(ActionEvent event) {
			FileChooser chooser = new FileChooser();
			chooser.setTitle(getText());
			chooser.setInitialDirectory(super.last);
			chooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter(Configurator.path.FILTER_CONFIG.value(), "*.run.xml"));
			java.io.File result = chooser.showOpenDialog(null);
			if (result != null) {
				setValue(result.getPath());
			}
		}
	}
}
