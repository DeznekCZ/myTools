package cz.deznekcz.javafx.configurator.components.support;

import java.util.stream.Collectors;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class AListValue extends AValue {
	
	public static AListValue init() {
		return new AListValue() {
			private ObservableList<String> items = FXCollections.observableArrayList();
			private Property<String> value;
			
			private ObservableValue<ObservableList<String>> wrapper = new SimpleObjectProperty<>(items);
			
			{
				value = new SimpleStringProperty("");
			}

			@Override
			public Property<String> valueProperty() {
				return value;
			}

			@Override
			public boolean isSortable() {
				return false;
			}

			@Override
			public void setSortable(boolean sortable) {
				
			}

			@Override
			public void setValue(String value) {
				valueProperty().setValue(value);
			}

			@Override
			public String getValue() {
				return valueProperty().getValue();
			}

			@Override
			public void refresh() {
			}

			@Override
			protected ObservableValue<ObservableList<String>> itemsProperty() {
				return wrapper;
			}
		};
	}
	
	protected abstract ObservableValue<ObservableList<String>> itemsProperty();

	public abstract boolean isSortable();	
	
	public abstract void setSortable(boolean sortable);	

	public final void setItems(ObservableList<String> items) {
		getItems().setAll(
				isSortable()
				? items.stream().sorted().collect(Collectors.toList())
				: items
				);
	}
	
	public final ObservableList<String> getItems() {
		return itemsProperty().getValue();
	}
}
