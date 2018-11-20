package cz.deznekcz.javafx.configurator.components;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.components.support.BooleanValue;
import javafx.beans.DefaultProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@DefaultProperty("content")
public class Cathegory extends TitledPane implements BooleanValue {
	private VBox parameters;

	private StringProperty help = new SimpleStringProperty("");

	private StringProperty expandedAsString;

	public StringProperty helpPropterty() {
		return help;
	}

	public void setHelp(String prompt) {
		helpPropterty().set(prompt);
	}

	public String getHelp() {
		return helpPropterty().get();
	}

	public void setParameters(Node...parameters) {
		this.parameters.getChildren().setAll(parameters);
	}

	public ObservableList<Node> getParameters() {
		return parameters.getChildren();
	}

	public Cathegory() {
		getStyleClass().add("cathegory");
		setCollapsible(false);

		parameters = new VBox();
		parameters.getStyleClass().add("parameters");
		setContent(parameters);

		expandedAsString = new SimpleStringProperty("true");
		expandedAsString.addListener((o,l,n) -> {
			if ("true".compareToIgnoreCase(n) == 0) {
				if (!isExpanded()) setExpanded(true);
			} else {
				if (isExpanded()) setExpanded(false);
			}
		});

		expandedProperty().addListener((o,l,n) -> {
			if ("true".compareToIgnoreCase(expandedAsString.getValue()) == 0) {
				if (!n) expandedAsString.setValue(Boolean.toString(n));
			} else {
				if (n) expandedAsString.setValue(Boolean.toString(n));
			}
		});
	}

	private boolean searched;
	private ASetup found;

	public ASetup getConfiguration() {
		if (searched) return found;
		searched = true;

		for (Tab tab : Configurator.getCtrl().getConfigs()) {
			Node tabContent = tab.getContent();
			Node parent = getParent();
			while (parent != null && !parent.equals(tabContent))
				parent = parent.getParent();
			if (parent != null) {
				found = (ASetup) tab.getProperties().get(ASetup.class);
			}
		}

		return found;
	}

	private Property<ASetup> configuration = new SimpleObjectProperty<>();

	public Property<ASetup> configurationProperty() {
		return configuration;
	}

	@Override
	public ObservableBooleanValue booleanProperty() {
		return expandedProperty();
	}

	public void setVerticalContent(Node...nodes) {
		parameters.getChildren().setAll(nodes);
	}

	public void setHorizontalContent(Node...nodes) {
		HBox box = new HBox();
		box.getStyleClass().add("line-parameters");
		box.getChildren().addAll(nodes);
		parameters.getChildren().setAll(box);
	}
}
