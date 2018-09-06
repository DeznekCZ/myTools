package cz.deznekcz.javafx.configurator.components.support;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.Configurator;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;

public abstract class AValue extends Control implements Storeable {

	public static ObservableValue<? extends Tooltip> tooltipBind(ObservableValue<? extends Tooltip> property) {
		return new ObjectBinding<Tooltip>() {
			ObservableValue<? extends Tooltip> tooltipProperty = property;
			{
				bind(tooltipProperty);
			}
			@Override
			protected Tooltip computeValue() {
				Tooltip newTooltip = new Tooltip();
				if (tooltipProperty.getValue() != null) {
					newTooltip.textProperty().bind(tooltipProperty.getValue().textProperty());
				}
				return newTooltip;
			}
		};
	}

	public static ObservableValue<? extends Tooltip> tooltipBind(StringProperty property, ObservableValue<Boolean> extended, String concat) {
		return new ObjectBinding<Tooltip>() {
			ObservableValue<? extends String> stringProperty = property;
			ObservableValue<Boolean> extendedProperty = extended;
			String concatString = concat;
			{
				bind(stringProperty, extendedProperty);
			}
			@Override
			protected Tooltip computeValue() {
				if (stringProperty.getValue() != null) {
					return new Tooltip(stringProperty.getValue() + " " + concatString);
				} else {
					return new Tooltip(concatString);
				}
			}
		};
	}

	public static ObservableValue<? extends Tooltip> tooltipBind(StringProperty property) {
		return new ObjectBinding<Tooltip>() {
			ObservableValue<? extends String> stringProperty = property;
			{
				bind(stringProperty);
			}
			@Override
			protected Tooltip computeValue() {
				if (stringProperty.getValue() != null) {
					return new Tooltip(stringProperty.getValue());
				} else {
					return null;
				}
			}
		};
	}

	private Property<ASetup> configuration = new SimpleObjectProperty<>();

	public Property<ASetup> configurationProperty() {
		return configuration;
	}

	@Override
	public ASetup getConfiguration() {
		if (configuration.getValue() != null) return configuration.getValue();

		for (Tab tab : Configurator.getCtrl().getConfigs()) {
			Node tabContent = tab.getContent();
			Node parent = getParent();
			while (parent != null && !parent.equals(tabContent))
				parent = parent.getParent();
			if (parent != null) {
				configuration.setValue((ASetup) tab.getProperties().get(ASetup.class));
			}
		}

		return configuration.getValue();
	}

	@Override
	public final String getValue() {
		return valueProperty().getValue();
	}

	@Override
	public final void setValue(String value) {
		valueProperty().setValue(value);
	}
}
