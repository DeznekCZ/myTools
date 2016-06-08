package cz.deznekcz.javafx.parametricPane.dynamic;

import cz.deznekcz.javafx.parametricPane.parameters.AParameter;

public class Enabled<T> extends ADynamic<T> {

	public Enabled(String params, AParameter<T> parameter) {
		super(params, parameter);
	}

	@Override
	protected IDynamicFunction<T> getFunction() {
		return (parameter, value) -> parameter.setEnabled(value);
	}
	
}
