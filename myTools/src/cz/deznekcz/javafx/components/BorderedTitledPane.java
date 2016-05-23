package cz.deznekcz.javafx.components;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.text.TextAlignment;

public class BorderedTitledPane extends TitledPane {
	public BorderedTitledPane(String titleString, Node content) {
		super(titleString, content);
//		setAnimated(false);
//		setCollapsible(false);
		setTextAlignment(TextAlignment.CENTER);
		setFocusTraversable(false);
//		setOnMouseClicked(event -> content.requestFocus());
	}
}