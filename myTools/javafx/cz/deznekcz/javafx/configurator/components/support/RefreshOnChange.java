package cz.deznekcz.javafx.configurator.components.support;

import java.util.ArrayList;
import java.util.List;

public interface RefreshOnChange {
	static List<Refreshable> refreshables = new ArrayList<>();
	
	default void doRefresh() {
		for (Refreshable refreshable : refreshables) {
			refreshable.refresh();
		}
	}
}
