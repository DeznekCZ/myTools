package cz.deznekcz.javafx.configurator;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Unnecesary {

	public static final boolean HIDDEN = true;
	
	private static BooleanProperty hidden = new SimpleBooleanProperty(HIDDEN);

	public static BooleanProperty hiddenProperty() {
		return hidden;
	}

}
