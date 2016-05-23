package cz.deznekcz.javafx.parametricPane.dynamic;

import cz.deznekcz.javafx.parametricPane.parameters.AParameter;

@FunctionalInterface
public interface IDynamicFunction {
	public void apply(AParameter<?> parameter, boolean value);
}
