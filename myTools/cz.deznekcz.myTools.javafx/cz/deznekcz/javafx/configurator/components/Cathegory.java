package cz.deznekcz.javafx.configurator.components;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.components.support.BooleanValue;
import javafx.beans.DefaultProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
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

	@Override
	public Property<String> valueProperty() {
		return expandedAsString;
	}

	@Override
	public void refresh() {

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
