package cz.deznekcz.javafx.configurator.components.choice;

import java.io.File;
import java.nio.file.FileSystem;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileSystemView;

import cz.deznekcz.javafx.components.Dialogs;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.ContextMenuEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PathChoice extends Choice implements HasDirProperty {

	private boolean selectFromRoot;

	private static class PathChoiceSkin extends PathChoice.ChoiceSkin {

		private static final String PREFIX = "| ";
		public StringProperty dir;
		private Label dirLabel;
		private BooleanProperty unnecesary;

		public PathChoiceSkin(PathChoice text) {
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

			ChoiceBox<String> choiceBox = getChoiceBox();
			choiceBox.setSkin(new GraphChoiceSkin(choiceBox));
		}

		@Override
		public PathChoice getSkinnable() {
			return (PathChoice) super.getSkinnable();
		}

		@Override
		public Node getNode() {
			return super.getNode();
		}

		@Override
		public void dispose() {

		}

	}

	public PathChoice() {
		selectFromRoot = true;
		setSkin(new PathChoiceSkin(this));
		register();
		refresh();
	}

	public StringProperty dirProperty() {
		return ((PathChoiceSkin) getSkin()).dir;
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
					getNonFXItems().setAll(
							Arrays.asList(
								File.listRoots()
							)
							.stream()
							.map(File::getAbsolutePath)
							.map(string -> string.substring(0, 1))
							.collect(Collectors.toList())
						);
				} else {
					getNonFXItems().setAll(
							Arrays.asList(
								new File(getDir()).listFiles()
							)
							.stream()
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
