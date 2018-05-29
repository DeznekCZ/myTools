package cz.deznekcz.javafx.configurator;

import cz.deznekcz.javafx.configurator.data.LiveStorage;
import javafx.scene.control.Tab;

public abstract class ASetup {
	
	protected LiveStorage storage;
	
	public ASetup() {
		
	}
	
	public LiveStorage getStorage() {
		return storage;
	}
	
	public void setStorage(LiveStorage storage) {
		this.storage = storage;
	}
}
