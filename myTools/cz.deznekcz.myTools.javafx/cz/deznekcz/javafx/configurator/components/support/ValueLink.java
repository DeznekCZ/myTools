package cz.deznekcz.javafx.configurator.components.support;

import cz.deznekcz.javafx.configurator.ATemplate;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.fxml.LoadException;
import javafx.scene.control.Control;

public class ValueLink extends Control {

    private AValue value;
    private String id;

    public ValueLink() {

	}

    public ValueLink(@NamedArg("id") String id) {
    	setId(id);
    }

    public ValueLink(@NamedArg("value") AValue value) {
		setValue(value);
	}

    public ValueLink(@NamedArg("id") String id, @NamedArg("value") AValue value) {
    	setId(id);
		setValue(value);
	}

    public void init(ATemplate controller) {
    	if (id == null || id.length() == 0) {
    		controller.getVariable(getId().replace("ref_", ""));
    	} else {
    		Platform.runLater(() -> init(controller));
    	}
    }

	public void setValue(AValue value) {
		this.value = value;
	}

    public AValue getValue() {
    	if (value == null) {
    		throw new RuntimeException(new LoadException("ValueLink id:"+getId()+" was not initialized"));
    	} else {
    		return value;
    	}
    }

    public void refresh() {
        value.refresh();
    }

    @Override
    public boolean equals(Object obj) {
    	return obj instanceof ValueLink && ((ValueLink) obj).value == this.value;
    }

}
