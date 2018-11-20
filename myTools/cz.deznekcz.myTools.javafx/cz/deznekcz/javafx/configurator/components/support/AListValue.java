package cz.deznekcz.javafx.configurator.components.support;

import java.text.Collator;
import java.util.Comparator;

import cz.deznekcz.javafx.configurator.ASetup;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;

public abstract class AListValue extends AValue implements ListChangeListener<String> {

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
			public void refresh() {
			}

			@Override
			protected ObservableValue<ObservableList<String>> itemsProperty() {
				return wrapper;
			}

			@Override
			public ASetup getConfiguration() {
				return null;
			}

			private BooleanProperty sortable = new SimpleBooleanProperty(false);

			@Override
			public BooleanProperty sortableProperty() {
				return sortable;
			}

			@Override
			public EventTarget getEventTarget() {
				return null;
			}
		};
	}

	protected abstract ObservableValue<ObservableList<String>> itemsProperty();

	public abstract BooleanProperty sortableProperty();

	public final boolean isSortable() {
		return sortableProperty().getValue();
	}

	private boolean sortingNotInitialized = true;

	private Property<Comparator<String>> listComparator = new SimpleObjectProperty<>(Collator.getInstance()::compare);

	public final void setSortable(boolean sortable) {
		if (sortable && sortingNotInitialized) {
			sortingNotInitialized = false;
			getItems().addListener(this);
			getItems().sort(getListComparator());
		}
		sortableProperty().setValue(sortable);
	}

	public final Comparator<String> getListComparator() {
		return listComparatorProperty().getValue();
	}

	public Property<Comparator<String>> listComparatorProperty() {
		return listComparator;
	}

	public final ObservableList<String> getItems() {
		return itemsProperty().getValue();
	}

	@Override
	public void onChanged(Change<? extends String> c) {
		c.next();
		if (isSortable() && !c.wasPermutated()) {
			c.getList().sort(getListComparator());
		}
	}

	public void setComparator(Comparator<String> comparator) {
		listComparatorProperty().setValue(comparator);
	}
}
