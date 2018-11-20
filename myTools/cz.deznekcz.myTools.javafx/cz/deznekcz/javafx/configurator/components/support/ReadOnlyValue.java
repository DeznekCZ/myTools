package cz.deznekcz.javafx.configurator.components.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.components.CheckValue;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ReadOnlyValue {

	public static String VALUE_PREFIX = "Value: ";

	public static Constant VOID = new Constant();


	public static Constant ifTrue(BooleanValue checkValue, AValue ifMatches, AValue ifElse) {
		return ReadOnlyValue.ifMatches((AValue) checkValue, "(t|T)(r|R)(u|U)(e|E)", ifMatches, ifElse);
	}

	public static Constant ifMatches(AValue matchedValue, String regex, AValue ifMatches, AValue ifElse) {
		Constant value = new Constant() {
			@Override
			public String toString() {
				return VALUE_PREFIX + getValue();
			}
		};
		value.valueProperty().bind(new StringBinding() {
			AValue _matchedValue = matchedValue;
			AValue _ifMatches = ifMatches;
			AValue _ifElse = ifElse;
			String _regex = regex;
			{
				bind(_matchedValue.valueProperty(), _ifMatches.valueProperty(), _ifElse.valueProperty());
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

	public static Constant ifNotMatches(AValue matchedValue, String regex, AValue ifMatches, AValue ifElse) {
		Constant value = new Constant() {
			@Override
			public String toString() {
				return VALUE_PREFIX + getValue();
			}
		};
		value.valueProperty().bind(new StringBinding() {
			AValue _matchedValue = matchedValue;
			AValue _ifMatches = ifMatches;
			AValue _ifElse = ifElse;
			String _regex = regex;
			{
				bind(_matchedValue.valueProperty(), _ifMatches.valueProperty(), _ifElse.valueProperty());
			}
			@Override
			protected String computeValue() {
				if (_matchedValue.getValue() != null && !_matchedValue.getValue().matches(_regex)) {
					return _ifMatches.getValue();
				} else {
					return _ifElse.getValue();
				}
			}

		});
		return value;
	}

	static Constant getNumberOnly(AValue transformed) {
		Constant number = new Constant() {
			@Override
			public String toString() {
				return VALUE_PREFIX + getValue();
			}
		};
		number.valueProperty().bind(new StringBinding(){
			AValue _transformed = transformed;
			{
				bind(_transformed.valueProperty());
			}
			@Override
			protected String computeValue() {
				String value = _transformed.getValue();
				return value == null ? "" : value.replaceAll("[^0-9]", "");
			}
		});
		return ifMatches(transformed, "[0-9]*", transformed, number);
	}

	public static Constant empty() {
		return new Constant("");
	}

	public static Constant concat(AValue...values) {
		return new Constant() {
			{
				List<ObservableValue<String>> observables = new ArrayList<>();
				for (AValue aValue : values) {
					observables.add(aValue.valueProperty());
				}
				valueProperty().bind(Bindings.concat(observables.toArray()));
			}

			@Override
			public String toString() {
				return VALUE_PREFIX + getValue();
			}
		};
	}

	public static Constant init(String value) {
		return new Constant(value) {
			@Override
			public String toString() {
				return VALUE_PREFIX + getValue();
			}
		};
	}

	public static Constant bound(Supplier<String> comupute, AValue...values) {
		Constant instance = new Constant(){
			@Override
			public String toString() {
				return VALUE_PREFIX + getValue();
			}
		};
		instance.valueProperty().bind(new StringBinding() {
			{
				bind(Arrays.asList(values).stream().map(AValue::valueProperty).toArray(Observable[]::new));
			}
			@Override
			protected String computeValue() {
				return comupute.get();
			}
		});
		return instance;
	}

	public static Constant bound(Function<String,String> comupute, AValue value) {
		Constant instance = new Constant(){
			@Override
			public String toString() {
				return VALUE_PREFIX + getValue();
			}
		};
		instance.valueProperty().bind(new StringBinding() {
			AValue _value = value;
			{
				bind(value.valueProperty());
			}
			@Override
			protected String computeValue() {
				return comupute.apply(_value.getValue());
			}
		});
		return instance;
	}

	public static Constant fromObservable(ObservableValue<String> textProperty) {
		return new Constant() {
			{
				valueProperty().bind(textProperty);
			}
			@Override
			public String toString() {
				return "Bound: " + getValue();
			}
		};
	}
}
