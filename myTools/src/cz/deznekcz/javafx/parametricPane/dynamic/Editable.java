package cz.deznekcz.javafx.parametricPane.dynamic;

import cz.deznekcz.javafx.parametricPane.parameters.AParameter;

public class Editable extends ADynamic {

	public Editable(String params, AParameter<?> parameter) {
		super(params, parameter);
	}

	@Override
	protected IDynamicFunction getFunction() {
		return (parameter, value) -> parameter.setEditable(value);
	}
}
