package cz.deznekcz.javafx.configurator.components.support;

import java.util.ArrayList;
import java.util.List;

import cz.deznekcz.javafx.components.Dialogs;
import cz.deznekcz.javafx.configurator.Configurator;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Control;

public class RefreshOnChange {
	private static List<Refreshable> refreshables = new ArrayList<>();

	public static void doRefresh() {
		Dialogs.LOADING.start(Configurator.refresh.HEADER.value(), 0.0);
		double size = refreshables.size();
		int index = 0;
		for (Refreshable refreshable : refreshables) {
			String idText = Configurator.refresh.NO_ID.value();

			if (refreshable instanceof Node    && ((Node) refreshable).getId()    != null)
				Configurator.refresh.ID.value(((Node)    refreshable).getId());

			if (refreshable instanceof Control && ((Control) refreshable).getId() != null)
				Configurator.refresh.ID.value(((Control) refreshable).getId());

			final int fIndex = index;
			if (refreshable.isValid()) {
				Platform.runLater(() -> {
					refreshable.refresh();
					Dialogs.LOADING.updateLoadingText(idText);
					Dialogs.LOADING.updateProgress(fIndex / size);
				});
			}

			index ++;
		}
		Platform.runLater(Dialogs.LOADING::close);
	}

	public static void add(Refreshable refreshable) {
		refreshables.add(refreshable);
	}

	private RefreshOnChange() {
	}

}
