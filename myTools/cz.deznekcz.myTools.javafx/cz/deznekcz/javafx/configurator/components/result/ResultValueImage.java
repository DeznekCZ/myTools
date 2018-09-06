package cz.deznekcz.javafx.configurator.components.result;

import javax.swing.GroupLayout.Alignment;

import cz.deznekcz.javafx.configurator.Unnecesary;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class ResultValueImage extends Control {
	
	protected static class ResultValueImageSking implements Skin<ResultValueImage> {

		private ResultValueImage skinnable;
		private ImageView imageView;
		private BorderPane box;
		private Label text;

		private SimpleBooleanProperty unnecesary;

		public ResultValueImageSking(String imageName, ResultValueImage skinnable, String text) {
			this.skinnable = skinnable;
			this.imageView = new ImageView(new Image(skinnable.getClass().getResourceAsStream(
					"/" + ResultValueImage.class.getPackage().getName().replaceAll("[.]", "/") + "/" + imageName
				), 16, 16, true, true));
			this.text = new Label(text);
			this.text.setPadding(new Insets(0, 5, 0, 0));
			BorderPane.setAlignment(this.text, Pos.CENTER_LEFT);
			BorderPane.setAlignment(this.imageView, Pos.CENTER_RIGHT);
			
			box = new BorderPane();
			box.setRight(imageView);
			
			unnecesary = new SimpleBooleanProperty(true);
			unnecesary.bind(Unnecesary.hiddenProperty());
			unnecesary.addListener((o,l,n) -> {
				if (n) box.setLeft(null);
				else   box.setLeft(this.text);
			});
		}

		@Override
		public void dispose() {
			
		}

		@Override
		public Node getNode() {
			return box;
		}

		@Override
		public ResultValueImage getSkinnable() {
			return skinnable;
		}

	}

	public ResultValueImage(String imageName, String text) {
		setSkin(new ResultValueImageSking(imageName, this, text));
	}
}
