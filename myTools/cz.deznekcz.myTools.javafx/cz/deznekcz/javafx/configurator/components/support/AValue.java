package cz.deznekcz.javafx.configurator.components.support;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;

public abstract class AValue extends Control implements ReadOnlyValue, Storeable {
	@Override
	public abstract Property<String> valueProperty();
	
	public abstract void setValue(String value);
	
	public abstract String getValue();

	public static ObservableValue<? extends Tooltip> tooltipBind(ObservableValue<? extends Tooltip> property) {
		return new ObjectBinding<Tooltip>() {
			ObservableValue<? extends Tooltip> _property = property;
			{
				bind(_property);
			}
			@Override
			protected Tooltip computeValue() {
				Tooltip newTooltip = new Tooltip();
				if (_property.getValue() != null) {
					newTooltip.textProperty().bind(_property.getValue().textProperty());
				}
				return newTooltip;
			}
		};
	}
}
