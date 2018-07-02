package cz.deznekcz.javafx.configurator.components.support;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import cz.deznekcz.javafx.configurator.components.CheckValue;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public interface ReadOnlyValue extends ObservableValue<String>, Refreshable {
	
	static ReadOnlyValue VOID = new Constant();

	ObservableValue<String> valueProperty();
	
	default ReadOnlyValue ends(String decorator) {
		ReadOnlyValue superThis = this;
		return new ReadOnlyValue() {
			StringBinding binding = new StringBinding() {
				ReadOnlyValue _superThis = superThis;
				String _decorator = decorator;
				{
					bind(_superThis.valueProperty());
				}
				@Override
				protected String computeValue() {
					String value = _superThis.valueProperty().getValue();
					if (!value.endsWith(_decorator)) {
						return value + _decorator;
					} else {
						return value;
					}
				}
			};
			
			@Override
			public ObservableValue<String> valueProperty() {
				return binding;
			}

			@Override
			public void refresh() {
				binding.invalidate();
			}
			
			@Override
			public String toString() {
				return "Value: " + getValue();
			}
		};
	}

	static ReadOnlyValue ifMatches(ReadOnlyValue matchedValue, String regex, ReadOnlyValue ifMatches, ReadOnlyValue ifElse) {
		Constant value = new Constant() {
			@Override
			public String toString() {
				return "Value: " + getValue();
			}
		};
		value.valueProperty().bind(new StringBinding() {
			ReadOnlyValue _matchedValue = matchedValue;
			ReadOnlyValue _ifMatches = ifMatches;
			ReadOnlyValue _ifElse = ifElse;
			String _regex = regex;
			{
				bind(_matchedValue, _ifMatches, _ifElse);
			}
			@Override
			protected String computeValue() {
				if (_matchedValue.getValue() != null && _matchedValue.getValue().matches(_regex)) {
					return _ifMatches.getValue();
				} else {
					return _ifElse.getValue();
				}
			}
			
		});
		return value;
	}

	default ReadOnlyValue appendIfMatches(ReadOnlyValue matchedValue, String regex, ReadOnlyValue ifMatches, ReadOnlyValue ifElse) {
		return append(ifMatches(matchedValue, regex, ifMatches, ifElse));
	}

	static ReadOnlyValue ifTrue(CheckValue checkValue, ReadOnlyValue ifMatches, ReadOnlyValue ifElse) {
		return ifMatches(checkValue, "true", ifMatches, ifElse);
	}

	default ReadOnlyValue appendIfTrue(CheckValue checkValue, ReadOnlyValue ifMatches, ReadOnlyValue ifElse) {
		return append(ifTrue(checkValue, ifMatches, ifElse));
	}

	static ReadOnlyValue getNumberOnly(ReadOnlyValue transformed) {
		Constant number = new Constant() {
			@Override
			public String toString() {
				return "Value: " + getValue();
			}
		};
		number.valueProperty().bind(new StringBinding(){
			ReadOnlyValue _transformed = transformed;
			{
				bind(_transformed);
			}
			@Override
			protected String computeValue() {
				String value = _transformed.getValue();
				return value == null ? "" : value.replaceAll("[^0-9]", "");
			}
		});
		return ifMatches(transformed, "[0-9]*", transformed, number);
	}

	default ReadOnlyValue appendGetNumberOnly(ReadOnlyValue transformed) {
		return append(getNumberOnly(transformed));
	}

	static ReadOnlyValue empty() {
		return init("");
	}

	default ReadOnlyValue append(String s) {
		return append(init(s != null ? s : ""));
	}
	
	default String getValue() {
		return valueProperty().getValue();
	}

	static ReadOnlyValue concat(ReadOnlyValue...values) {
		return new Constant() {
			{
				List<ObservableValue<String>> observables = new ArrayList<>();
				for (ReadOnlyValue readOnlyValue : values) {
					observables.add(readOnlyValue.valueProperty());
				}
				valueProperty().bind(Bindings.concat(observables.toArray()));
			}
			
			@Override
			public String toString() {
				return "Value: " + getValue();
			}
		};
	}
	
	static ReadOnlyValue init(String value) {
		Constant instance = new Constant() {
			@Override
			public String toString() {
				return "Value: " + getValue();
			}
		};
		instance.setValue(value);
		return instance;
	}

	@Override
	default void addListener(ChangeListener<? super String> listener) {
		valueProperty().addListener(listener);
	}

	@Override
	default void removeListener(ChangeListener<? super String> listener) {
		valueProperty().removeListener(listener);
	}

	@Override
	default void addListener(InvalidationListener listener) {
		valueProperty().addListener(listener);
	}

	@Override
	default void removeListener(InvalidationListener listener) {
		valueProperty().removeListener(listener);
	}

	default ReadOnlyValue append(ReadOnlyValue appender) {
		ReadOnlyValue superThis = this;
		return new ReadOnlyValue() {
			StringBinding binding = new StringBinding() {
				ReadOnlyValue _superThis = superThis;
				ReadOnlyValue _appender = appender;
				{
					bind(_superThis.valueProperty(), _appender.valueProperty());
				}
				@Override
				protected String computeValue() {
					return _superThis.getValue() + _appender.getValue();
				}
			};
			
			@Override
			public ObservableValue<String> valueProperty() {
				return binding;
			}

			@Override
			public void refresh() {
				binding.invalidate();
			}
			
			@Override
			public String toString() {
				return "Value: " + getValue();
			}
		};
	}

	static ReadOnlyValue bound(Supplier<String> comupute, ReadOnlyValue...values) {
		Constant instance = new Constant(){
			@Override
			public String toString() {
				return "Value: " + getValue();
			}
		};
		instance.valueProperty().bind(new StringBinding() {
			{
				bind(values);
			}
			@Override
			protected String computeValue() {
				return comupute.get();
			}
		});
		return instance;
	}

	default ReadOnlyValue upperCase() {
		ObservableValue<String> thisValueProperty = this.valueProperty();
		Constant instance = new Constant() {
			@Override
			public String toString() {
				return "Value: " + getValue();
			}
		};
		instance.valueProperty().bind(new StringBinding() {
			ObservableValue<String> _thisValueProperty = thisValueProperty;
			{
				bind(_thisValueProperty);
			}
			@Override
			protected String computeValue() {
				return _thisValueProperty.getValue().toUpperCase();
			}
		});
		return instance;
	}

	default ReadOnlyValue lowerCase() {
		ObservableValue<String> thisValueProperty = this.valueProperty();
		Constant instance = new Constant() {
			@Override
			public String toString() {
				return "Value: " + getValue();
			}
		};
		instance.valueProperty().bind(new StringBinding() {
			ObservableValue<String> _thisValueProperty = thisValueProperty;
			{
				bind(_thisValueProperty);
			}
			@Override
			protected String computeValue() {
				return _thisValueProperty.getValue().toLowerCase();
			}
		});
		return instance;
	}
}
