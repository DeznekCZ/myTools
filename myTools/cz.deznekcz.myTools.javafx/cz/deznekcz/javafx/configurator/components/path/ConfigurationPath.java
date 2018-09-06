package cz.deznekcz.javafx.configurator.components.path;

import java.util.function.Predicate;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.components.Path;
import cz.deznekcz.util.LiveStorage;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

public class ConfigurationPath extends Path {
	public ConfigurationPath() {
		super(Configurator.path.OPEN_CONFIG, Configurator.path.SELECT_CONFIG);
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
		chooser.setInitialDirectory(getLast());
		chooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter(Configurator.path.FILTER_CONFIG.value(), "*.run.xml"));
		java.io.File result = chooser.showOpenDialog(null);
		if (result != null) {
			setValue(result.getPath());
		}
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