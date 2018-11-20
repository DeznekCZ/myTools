package cz.deznekcz.javafx.configurator.components;
import cz.deznekcz.javafx.configurator.components.support.AValue;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

@DefaultProperty("choices")
public class ToggleValue extends AValue implements InvalidationListener {

    private static class ToggleValueSkin implements Skin<ToggleValue> {

        private ToggleValue text;
        private HBox box;
        private Label label;
        private HBox value;
        private Pane fill;

        public ToggleValueSkin(ToggleValue text) {
            this.text = text;
            text.getStyleClass().add("option-value");
            text.setTooltip(new Tooltip());

            box = new HBox();
            label = new Label();
            fill = new Pane();
            value = new HBox();
            box.disableProperty().bind(text.disableProperty());

            label.getStyleClass().add("option-value-label");
            fill .getStyleClass().add("option-value-fill");
            value.getStyleClass().add("option-value-value");

            label.idProperty().bind(text.idProperty().concat("_label"));
            fill .idProperty().bind(text.idProperty().concat("_fill" ));
            value.idProperty().bind(text.idProperty().concat("_value"));

            HBox.setHgrow(fill, Priority.ALWAYS);

            box.getChildren().addAll(label, fill, value);
        }

        @Override
        public ToggleValue getSkinnable() {
            return text;
        }

        @Override
        public Node getNode() {
            return box;
        }

        @Override
        public void dispose() {

        }

    }

    private ToggleGroup group;
    private StringProperty selectedProperty;

    public StringProperty textProperty() {
        return ((ToggleValueSkin) getSkin()).label.textProperty();
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String text) {
        this.textProperty().set(text);
    }

    public StringProperty valueProperty() {
        return selectedProperty;
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

    public ToggleValue() {
        selectedProperty = new SimpleStringProperty("");
        group = new ToggleGroup();
        setSkin(new ToggleValueSkin(this));
        selectedProperty.addListener(this);
        group.getToggles().addListener(this);
        group.selectedToggleProperty().addListener(this);
    }

    public ObservableList<Toggle> getChoices() {
        return group.getToggles();
    }

    @Override
    public void refresh() {

    }

    @Override
    public void invalidated(Observable observable) {
        if (observable == selectedProperty) {
            for (Toggle toggle : group.getToggles()) {
                if (((Node) toggle).getId().equals(selectedProperty.getValue())) {
                    toggle.setSelected(true);
                    break;
                }
            }
        } else if (observable == group.getToggles()) {
            ((ToggleValueSkin) getSkin()).value.getChildren().clear();
            group.getToggles().stream().map(toggle -> (Node) toggle).forEach(((ToggleValueSkin) getSkin()).value.getChildren()::add);
        } else if (observable == group.selectedToggleProperty()) {
        	if (group.getSelectedToggle() != null) {
        		String id = ((Node) group.getSelectedToggle()).getId();
        		selectedProperty.setValue(id != null ? id : "");
        	} else {
        		selectedProperty.setValue("");
        	}
        }
    }

	@Override
	public EventTarget getEventTarget() {
		return ((ToggleValueSkin) getSkin()).value;
	}
}
