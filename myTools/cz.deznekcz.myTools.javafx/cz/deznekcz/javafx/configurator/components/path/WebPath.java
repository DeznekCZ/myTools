package cz.deznekcz.javafx.configurator.components.path;

import java.awt.Desktop;
import java.net.URL;
import java.util.function.Predicate;

import cz.deznekcz.javafx.components.Dialogs;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.components.Path;
import cz.deznekcz.reference.OutBoolean;
import javafx.event.ActionEvent;

public class WebPath extends Path {

	public WebPath() {
		super(Configurator.path.OPEN_WEB, Configurator.path.OPEN_WEB);
		selectablePropterty().bind(OutBoolean.FALSE());
	}
	@Override
	public Predicate<String> getValidator() {
		return (value) -> {
			try {
				new URL(value).toURI(); // if these throws no exception
				return true;
			} catch (Exception e) {
				return false;
			}
		};
	}
	@Override
	public void openPath(ActionEvent event) {
		try {
			Desktop.getDesktop().browse(new URL(getValue()).toURI());
		} catch (Exception e) {
			Dialogs.EXCEPTION.show(e);
		}
	}
	@Override
	public void selectPath(ActionEvent event) {
	}
}