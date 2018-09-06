package cz.deznekcz.javafx.configurator.components.choice;

import com.sun.javafx.scene.control.behavior.ChoiceBoxBehavior;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;

public class GraphChoiceSkin extends BehaviorSkinBase<ChoiceBox<String>, ChoiceBoxBehavior<String>> {

	private ContextMenu popup;

	public GraphChoiceSkin(ChoiceBox<String> control) {
		super(control, new ChoiceBoxBehavior<>(control));

		initialize();
	}

	private void initialize() {
		popup = new ContextMenu();
	}
}
