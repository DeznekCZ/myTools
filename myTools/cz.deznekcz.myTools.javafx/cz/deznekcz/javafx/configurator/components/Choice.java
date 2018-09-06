package cz.deznekcz.javafx.configurator.components;

import cz.deznekcz.javafx.configurator.components.support.AListValue;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

@DefaultProperty("items")
public class Choice extends AListValue {

	protected static class ChoiceSkin implements Skin<Choice> {

		private Choice text;
		private BorderPane box;
		private Label label;
		private ChoiceBox<String> value;
		
		private BorderPane valueDecorator;
		
		private StringProperty valueString;
		private BooleanProperty sortable;
		
		public ChoiceSkin(Choice text) {
			this.text = text;
			text.getStyleClass().add("choice");
			text.setTooltip(new Tooltip());
			
			box = new BorderPane();
			label = new Label();
			value = new ChoiceBox<>();
			valueDecorator = new BorderPane(value);
			box.disableProperty().bind(text.disableProperty());

			label.getStyleClass().add("text-value-label");		
			value.getStyleClass().add("text-value-value");

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
				value.getSelectionModel().select(n);
			});
			
			value.getSelectionModel().selectedItemProperty().addListener((o,l,n) -> {
				if (n == valueString.get()) return;
				valueString.set(n);
			});
			
			value.setDisable(true);
			value.getItems().add("");
			value.getItems().addListener(new ListChangeListener<String>() {
				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> c) {
					c.next();
					value.setDisable(value.getItems().size() == 0 
							||	(	value.getItems().size() == 1
								&&	(	value.getItems().get(0) == null
									||	value.getItems().get(0).length() == 0
									)
								)
							);
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
		return ((ChoiceSkin) getSkin()).value.itemsProperty();
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
	
	public boolean isSortable() {
		return sortableProperty().get();
	}
	
	public void setSortable(boolean value) {
		sortableProperty().set(value);
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
		setSkin(new ChoiceSkin(this));
		register();
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
}
