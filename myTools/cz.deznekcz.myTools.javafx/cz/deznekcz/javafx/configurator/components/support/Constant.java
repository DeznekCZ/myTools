package cz.deznekcz.javafx.configurator.components.support;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.util.LiveStorage;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.layout.Pane;

/**
 * Represents string value which can be also changed inside controller but initial value is always same.
 * Could be stored in {@link LiveStorage} (on restore set to last value)
 * @author Zdenek Novotny (DeznekCZ)
 * @see Variable
 */
public class Constant extends AValue {

	private StringProperty property;

	@Override
	protected Skin<?> createDefaultSkin() {
		return new Skin<Skinnable>() {
			Pane pane = new Pane();

			@Override
			public Skinnable getSkinnable() {
				return Constant.this;
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

	public Constant() {
		property = new SimpleStringProperty("");
	}

	public Constant(@NamedArg("value") String value) {
		this();
		setValue(value);
	}

	public StringProperty valueProperty() {
		return property;
	}

	@Override
	public void refresh() {

	}

	@Override
	public ASetup getConfiguration() {
		return null;
	}

	@Override
	public EventTarget getEventTarget() {
		return null;
	}
}
