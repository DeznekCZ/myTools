package cz.deznekcz.javafx.configurator.components.support;

import java.util.HashMap;
import java.util.Map;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.components.ResultValue;

public class ValueLink {

	private final static Map<Refreshable, ValueLink> REFERENCES
		= new HashMap<>();

	private Refreshable value;
	private boolean notSearched;
	private String ref;

	public ValueLink() {
		this.notSearched = true;
	}

	public String getRef() {
		return ref;
	}

	public Refreshable getValue() {
		return value;
	}

	public final void setRef(String ref) {
		this.ref = ref;
	}

	public void refresh(ASetup aSetup) {
		if (value != null) {
			value.refresh();
		} else if (notSearched) {
			notSearched = false;
			value = aSetup.getVariable(ref);
			if (value != null) {
				REFERENCES.put(value, this);
				value.refresh();
			}
		}
	}

	public static ValueLink find(Refreshable refreshable) {
		return REFERENCES.get(refreshable);
	}

}
