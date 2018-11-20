package cz.deznekcz.javafx.configurator.components;

import java.util.ArrayList;
import java.util.List;

import cz.deznekcz.javafx.configurator.components.support.AListValue;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

@DefaultProperty("items")
public class Choice extends AListValue {

	private ObjectProperty<ObservableList<String>> items;
	private ObservableList<String> noFXList;
	private String lastValue;

	protected static class ChoiceSkin implements Skin<Choice> {

		private Choice text;
		private BorderPane box;
		private Label label;
		private ChoiceBox<String> value;

		private BorderPane valueDecorator;

		private StringProperty valueString;
		private BooleanProperty sortable;
		private String lastSelected;

		public ChoiceSkin(Choice text) {
			this.text = text;
			text.getStyleClass().add("choice");
			text.setTooltip(new Tooltip());

			this.lastSelected = "";

			box = new BorderPane();
			label = new Label();
			value = new ChoiceBox<>();
			valueDecorator = new BorderPane(value);
			box.disableProperty().bind(text.disableProperty());

			label.getStyleClass().add("choice-label");
			value.getStyleClass().add("choice-value");

			label.idProperty().bind(text.idProperty().concat("_label"));
			value.idProperty().bind(text.idProperty().concat("_value"));

			valueDecorator.maxWidthProperty().bind(text.widthProperty().divide(2));
			valueDecorator.prefWidthProperty().bind(text.widthProperty().divide(2));

			value.maxWidthProperty().bind(valueDecorator.widthProperty());
			value.prefWidthProperty().bind(valueDecorator.widthProperty());

			valueString = new SimpleStringProperty("");
			valueString.addListener((o,l,n) -> {
				if (n == value.getSelectionModel().getSelectedItem()) return;
				if (!value.getItems().contains(n)) return;
				if (n != null) lastSelected = n;
				value.getSelectionModel().select(n);
			});

			value.getSelectionModel().selectedItemProperty().addListener((o,l,n) -> {
				if (n != null && n.equals(valueString.get())) return;
				if (n != null) lastSelected = n;
				valueString.set(n);
			});

			value.setDisable(true);
			getSkinnable().getItems().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					String last = value.getSelectionModel().getSelectedItem();
					if (last != null && last.length() > 0) lastSelected = last;
					value.getSelectionModel().clearSelection();
					value.getItems().clear();
					value.getItems().setAll(getSkinnable().getItems());
					if (lastSelected != null && lastSelected.length() > 0)
						getSkinnable().setValue(lastSelected);
					value.setDisable(value.getItems().isEmpty());
				}
			});

			sortable = new SimpleBooleanProperty(false);

			BorderPane.setAlignment(label, Pos.CENTER_LEFT);
			box.setLeft(label);
			box.setRight(valueDecorator);
		}

		@Override
		public Choice getSkinnable() {
			return text;
		}

		@Override
		public Node getNode() {
			return box;
		}

		@Override
		public void dispose() {

		}

		protected BorderPane getBox() {
			return box;
		}

		protected BorderPane getValueDecorator() {
			return valueDecorator;
		}

		protected ChoiceBox<String> getChoiceBox() {
			return value;
		}
	}

	public StringProperty textProperty() {
		return ((ChoiceSkin) getSkin()).label.textProperty();
	}

	public String getText() {
		return textProperty().get();
	}

	public void setText(String text) {
		this.textProperty().set(text);
	}

	public StringProperty helpPropterty() {
		return getTooltip().textProperty();
	}

	public void setHelp(String prompt) {
		helpPropterty().set(prompt);
	}

	public String getHelp() {
		return helpPropterty().get();
	}

	protected ObjectProperty<ObservableList<String>> itemsProperty() {
		return items;
	}

	public ObjectProperty<SingleSelectionModel<String>> selectionModelProperty() {
		return ((ChoiceSkin) getSkin()).value.selectionModelProperty();
	}

	public SingleSelectionModel<String> getSelectionModel() {
		return selectionModelProperty().get();
	}

	public void setSelectionModel(SingleSelectionModel<String> items) {
		this.selectionModelProperty().set(items);
	}

	public StringProperty valueProperty() {
		return ((ChoiceSkin) getSkin()).valueString;
	}

	public BooleanProperty sortableProperty() {
		return ((ChoiceSkin) getSkin()).sortable;
	}

	public final ObjectProperty<Node> leftDecoratorProperty() {
		return ((ChoiceSkin) getSkin()).valueDecorator.leftProperty();
	}

	public final void setLeftDecorator(Node left) {
		leftDecoratorProperty().set(left);
	}

	public final Node getLeftDecorator() {
		return leftDecoratorProperty().get();
	}

	public final ObjectProperty<Node> rightDecoratorProperty() {
		return ((ChoiceSkin) getSkin()).valueDecorator.rightProperty();
	}

	public final void setRightDecorator(Node right) {
		rightDecoratorProperty().set(right);
	}

	public final Node getRightDecorator() {
		return rightDecoratorProperty().get();
	}

	public Choice() {
		items = new SimpleObjectProperty<>(FXCollections.observableArrayList());

		noFXList = FXCollections.observableArrayList();
		noFXList.addListener((Observable o) -> {
			final List<String> interloop = new ArrayList<>(noFXList);
			Platform.runLater(() -> {
				items.get().setAll(interloop);
				if (lastValue.length() > 0)
					setValueOrFirst(lastValue);
				else
					setValueToFirst();
			});
		});

		setSkin(new ChoiceSkin(this));
	}

	@Override
	public final void refresh() {
		lastValue = getValueSafe();
		getItems().clear();
		refreshList();
	}

	protected void refreshList() {

	}

	public void setValueToFirst() {
		if (!getItems().isEmpty()) {
			for (String next : getItems()) {
				if (next != null && next.length() > 0) {
					setValue(next);
					break;
				}
			}
		}
	}

	public void setValueOrFirst(String value) {
		if (getItems().contains(value)) {
			setValue(value);
		} else {
			setValueToFirst();
		}
	}

	public ObservableList<String> getNonFXItems() {
		return noFXList;
	}

	@Override
	public EventTarget getEventTarget() {
		return ((ChoiceSkin) getSkin()).value;
	}
}
