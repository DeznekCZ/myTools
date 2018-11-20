package cz.deznekcz.javafx.configurator.components.choice;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.javafx.configurator.components.Choice;
import cz.deznekcz.javafx.configurator.components.support.HasDirProperty;
import cz.deznekcz.util.ITryDo;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class DirectoryChoice extends Choice implements HasDirProperty {

	private boolean selectFromRoot;

	private static class DirectoryChoiceSkin extends DirectoryChoice.ChoiceSkin {

		private static final String PREFIX = "| ";
		private StringProperty dir;
		private BooleanProperty unnecesary;
		private Label dirLabel;

		public DirectoryChoiceSkin(DirectoryChoice text) {
			super(text);

			dir = new SimpleStringProperty();
			dir.addListener((o,l,n) -> {
				text.selectFromRoot = (n == null || n.length() == 0);
				text.refresh();
			});

			dirLabel = new Label();
			dirLabel.textProperty().bind(Bindings.concat(PREFIX, Configurator.choice.DIR, dir));

			unnecesary = new SimpleBooleanProperty(true);
			unnecesary.bind(Unnecesary.hiddenProperty());
			unnecesary.addListener((o,l,n) -> {
				if (n) getBox().setBottom(null);
				else   getBox().setBottom(dirLabel);
			});
		}

		@Override
		public DirectoryChoice getSkinnable() {
			return (DirectoryChoice) super.getSkinnable();
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
		selectFromRoot = true;
		setSkin(new DirectoryChoiceSkin(this));
		register();
//		refresh();
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

	@Override
	protected void refreshList() {
		Configurator.getService().execute(() -> {
			try {
				if (selectFromRoot) {
					File[] files = File.listRoots();
					getNonFXItems().setAll(
							Arrays.asList(
								files != null ? files : new File[0]
							)
							.stream()
							.map(File::getAbsolutePath)
							.map(string -> string.substring(0, 1))
							.collect(Collectors.toList())
						);
				} else {
					File[] files = new File(getDir()).listFiles();
					getNonFXItems().setAll(
							Arrays.asList(
								files != null ? files : new File[0]
							)
							.stream()
							.filter(File::isDirectory)
							.map(File::getName)
							.collect(Collectors.toList())
						);
				}
			} catch (Exception e) {
				getNonFXItems().clear();
			}
		});
	}
}
