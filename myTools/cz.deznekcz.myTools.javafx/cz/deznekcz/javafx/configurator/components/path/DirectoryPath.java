package cz.deznekcz.javafx.configurator.components.path;

import java.awt.Desktop;
import java.io.File;
import java.util.function.Predicate;

import cz.deznekcz.javafx.components.Dialogs;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.components.ListEntry;
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
			Dialogs.EXCEPTION.show(e);
		}
	}
	@Override
	public void selectPath(ActionEvent event) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle(getText());
		if (getValueSafe().length() == 0) {
			chooser.setInitialDirectory(getLast());
		} else {
			chooser.setInitialDirectory(new File(getValue()));
		}
		java.io.File result = chooser.showDialog(null);
		if (result != null) {
			setValue(result.getPath());
			setLast(result.getParentFile());
		}
	}

	/**
	 * Returns validator for {@link ListEntry}
	 * @return
	 */
	public Predicate<String> isChildDirectory() {
		return fileName -> {
            try {
            	String tmpVal = getValue();
            	if (!getValue().endsWith("\\")) tmpVal += "\\";
            	File file = new File(tmpVal + fileName);
                return file.exists() && file.isDirectory();
            } catch (Exception e) {
                return false;
            }
        };
	}

	/**
	 * Returns validator for {@link ListEntry}
	 * @return
	 */
	public Predicate<String> isChildFile() {
		return fileName -> {
			try {
            	String tmpVal = getValue();
            	if (!getValue().endsWith("\\")) tmpVal += "\\";
            	File file = new File(tmpVal + fileName);
                return file.exists() && file.isFile();
            } catch (Exception e) {
                return false;
            }
        };
	}
}