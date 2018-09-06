package cz.deznekcz.javafx.configurator.components.support;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.ConfiguratorController;
import cz.deznekcz.util.LiveStorage;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.layout.Pane;

/**
 * Represents string value which can be also changed inside controller but initial value is always same.
 * Could be stored in {@link LiveStorage} (on restore set to last value)
 * @author Zdenek Novotny (DeznekCZ)
 * @see Constant
 */
public class Variable extends AValue {

	private StringProperty property;
	private AccessType global;

	@Override
	protected Skin<?> createDefaultSkin() {
		return new Skin<Skinnable>() {
			Pane pane = new Pane();

			@Override
			public Skinnable getSkinnable() {
				return Variable.this;
			}

			@Override
			public Node getNode() {
				return pane;
			}

			@Override
			public void dispose() {

			}
		};
	}

	public Variable() {
		property = new SimpleStringProperty("");
	}

	public StringProperty valueProperty() {
		return property;
	}

	@Override
	public void refresh() {

	}

	public final boolean isGlobal() {
		return global != AccessType.NOT_GLOBAL;
	}

	public final AccessType getGlobal() {
		return global;
	}

	public final void setGlobal(AccessType newState) {
		ConfiguratorController controller = Configurator.getCtrl();
		if (controller == null) controller = ConfiguratorController.getTest();

		if (newState == null || newState == AccessType.NOT_GLOBAL) {
			if (this.global == AccessType.SOURCE) {
				valueProperty().unbindBidirectional(controller.getGlobal(getId()));
			} else if (this.global == AccessType.REFERENCE) {
				valueProperty().unbind();
			} else {
				// was not bound by global access
			}
		} else {
			if (newState == AccessType.SOURCE) {
				valueProperty().bindBidirectional(controller.getGlobal(getId()));
			} else if (newState == AccessType.REFERENCE) {
				valueProperty().bind(controller.getGlobal(getId()));
			} else {
				// was not bound by global access
			}
		}
		this.global = newState == null ? AccessType.NOT_GLOBAL : newState;
	}

	@Override
	public ASetup getConfiguration() {
		return null;
	}
}
