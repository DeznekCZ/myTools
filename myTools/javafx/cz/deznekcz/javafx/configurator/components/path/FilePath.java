package cz.deznekcz.javafx.configurator.components.path;

import java.awt.Desktop;
import java.util.function.Predicate;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.components.Path;
import cz.deznekcz.util.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class FilePath extends Path {
		
	private StringProperty filterHint;
	private StringProperty extensions;

	public FilePath() {
		super(Configurator.path.OPEN_FILE, Configurator.path.SELECT_FILE);
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
		chooser.setInitialDirectory(getLast());
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

	@Override
	public void setValue(String value) {
		valueProperty().setValue(value);
	}

	@Override
	public String getValue() {
		return valueProperty().getValue();
	}
}