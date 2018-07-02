package cz.deznekcz.javafx.configurator.components.support;

public interface Refreshable {
	void refresh();
	
	default void register() {
		RefreshOnChange.refreshables.add(this);
	}
}
