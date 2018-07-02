package cz.deznekcz.javafx.configurator.components.choice;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.javafx.configurator.components.Choice;
import cz.deznekcz.javafx.configurator.components.support.HasDirProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class DirectoryChoice extends Choice implements HasDirProperty {
	
	private static class DirectoryChoiceSkin extends DirectoryChoice.ChoiceSkin {
		
		private StringProperty dir;
		private BooleanProperty unnecesary;
		private Label dirLabel;

		public DirectoryChoiceSkin(DirectoryChoice text) {
			super(text);
			
			dir = new SimpleStringProperty();
			dir.addListener((o,l,n) -> {
				text.refresh();
			});
			
			dirLabel = new Label();
			dirLabel.textProperty().bind(Bindings.concat(Configurator.choice.DIR, dir));
			
			unnecesary = new SimpleBooleanProperty(true);
			unnecesary.bind(Unnecesary.hiddenProperty());
			unnecesary.addListener((o,l,n) -> {
				if (n) getBox().setBottom(null);
				else   getBox().setBottom(dirLabel);
			});
		}

		@Override
		public DirectoryChoice getSkinnable() {
			return (DirectoryChoice) super.getNode();
		}

		@Override
		public Node getNode() {
			return super.getNode();
		}

		@Override
		public void dispose() {
			
		}

	}
	
	public DirectoryChoice() {
		setSkin(new DirectoryChoiceSkin(this));
		register();
	}
	
	public StringProperty dirProperty() {
		return ((DirectoryChoiceSkin) getSkin()).dir;
	}
	
	public void setDir(String dir) {
		dirProperty().set(dir);
	}
	
	public String getDir() {
		return dirProperty().get();
	}
	
	public void refresh() {
		getItems().clear();
		try {
			getItems().addAll(
				Arrays.asList(
					new File(getDir()).listFiles()
				)
				.stream()
				.filter((f)->f.isDirectory())
				.map((f)->f.getName())
				.collect(Collectors.toList())
			);
		} catch (Exception e) {
		}
	}
}
