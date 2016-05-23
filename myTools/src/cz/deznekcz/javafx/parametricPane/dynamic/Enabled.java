package cz.deznekcz.javafx.parametricPane.dynamic;

import cz.deznekcz.javafx.parametricPane.parameters.AParameter;

public class Enabled extends ADynamic {

	public Enabled(String params, AParameter<?> parameter) {
		super(params, parameter);
	}

	@Override
	protected IDynamicFunction getFunction() {
		return (parameter, value) -> parameter.setEnabled(value);
	}
	
}
