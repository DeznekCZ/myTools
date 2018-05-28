package cz.deznekcz.javafx.configurator;

import cz.deznekcz.javafx.configurator.data.LiveStorage;
import javafx.scene.control.Tab;

public abstract class ASetup {
	
	protected LiveStorage storage;
	
	public ASetup(LiveStorage storage) {
		this.storage = storage;
	}
	
	public LiveStorage getStorage() {
		return storage;
	}
	
	public abstract Tab component();
}
