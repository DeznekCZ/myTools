package cz.deznekcz.javafx.configurator.components.support;

import cz.deznekcz.javafx.configurator.ASetup;

public interface Refreshable {
	void refresh();

	default void register() {
		RefreshOnChange.add(this);
	}

	ASetup getConfiguration();

	default boolean isValid() {
		return getConfiguration() != null
				&& getConfiguration().getRoot() != null
				&& getConfiguration().getRoot().getTabPane() != null;
	}
}
