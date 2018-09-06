package cz.deznekcz.javafx.configurator.components.path;

import java.awt.Desktop;
import java.util.function.Predicate;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.components.Path;
import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;

public class DirectoryPath extends Path {
	public DirectoryPath() {
		super(Configurator.path.OPEN_DIR, Configurator.path.SELECT_DIR);
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
		chooser.setInitialDirectory(getLast());
		java.io.File result = chooser.showDialog(null);
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