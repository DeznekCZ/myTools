package cz.deznekcz.javafx.parametricPane.dynamic;

import cz.deznekcz.javafx.parametricPane.parameters.AParameter;

@FunctionalInterface
public interface IDynamicFunction<T> {
	public void apply(AParameter<T> parameter, boolean value);
}
