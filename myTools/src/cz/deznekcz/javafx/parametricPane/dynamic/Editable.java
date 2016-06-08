package cz.deznekcz.javafx.parametricPane.dynamic;

import cz.deznekcz.javafx.parametricPane.parameters.AParameter;

public class Editable<T> extends ADynamic<T> {

	public Editable(String params, AParameter<T> parameter) {
		super(params, parameter);
	}

	@Override
	protected IDynamicFunction<T> getFunction() {
		return (parameter, value) -> parameter.setEditable(value);
	}
}
