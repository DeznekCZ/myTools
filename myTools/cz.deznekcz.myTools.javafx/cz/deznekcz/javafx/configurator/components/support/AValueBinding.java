package cz.deznekcz.javafx.configurator.components.support;

import java.util.Arrays;

import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;

public abstract class AValueBinding extends StringBinding {

	public AValueBinding(AValue...variables) {
		bind(Arrays.asList(variables).stream().map(AValue::valueProperty).toArray(Observable[]::new));
	}
}
