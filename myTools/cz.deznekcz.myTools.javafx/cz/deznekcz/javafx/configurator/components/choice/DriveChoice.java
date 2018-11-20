package cz.deznekcz.javafx.configurator.components.choice;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.javafx.configurator.components.Choice;
import cz.deznekcz.javafx.configurator.components.choice.DriveChoice.SelectType;
import cz.deznekcz.javafx.configurator.components.support.HasDirProperty;
import cz.deznekcz.util.Builder;
import cz.deznekcz.util.ForEach;
import cz.deznekcz.util.ITryDo;
import cz.deznekcz.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class DriveChoice extends Choice implements ChangeListener<SelectType> {

	public enum SelectType {
		USED, FREE, ALL
	}


	private static class DirectoryChoiceSkin extends DriveChoice.ChoiceSkin {

		public DirectoryChoiceSkin(DriveChoice text) {
			super(text);
		}

		@Override
		public DriveChoice getSkinnable() {
			return (DriveChoice) super.getSkinnable();
		}

		@Override
		public Node getNode() {
			return super.getNode();
		}

		@Override
		public void dispose() {

		}

	}


	private Property<SelectType> select;

	public DriveChoice() {
		select = new SimpleObjectProperty<>(SelectType.USED);
		setSkin(new DirectoryChoiceSkin(this));
		select.addListener(this);

		register();
//		refresh();
	}

	public Property<SelectType> selectProperty() {
		return select;
	}

	public void setSelect(SelectType select) {
		this.select.setValue(select);
	}

	public SelectType getSelect() {
		return selectProperty().getValue() != null ? selectProperty().getValue() : SelectType.USED;
	}

	public static List<Character> ALPHABET = Arrays.asList(
			'A','B','C','D','E','E','F','G','H',
			'I','J','K','L','M','N','O','P','Q',
			'R','S','T','U','V','W','X','Y','Z'
		);

	@Override
	protected void refreshList() {
		Configurator.getService().execute(() -> {
			try {
				if (select.getValue() == SelectType.FREE) {
					File[] roots = File.listRoots();
					Collection<Character> usedCharacters = roots == null ?
							new ArrayList<>(1) : Arrays.asList(roots).stream().map(file -> file.getPath().charAt(0))
							.collect(Collectors.toList());
					getNonFXItems().setAll(
							ALPHABET
							.stream()
							.filter(letter -> !usedCharacters.contains(letter))
							.map(character -> character+":")
							.collect(Collectors.toList())
						);
				} else if (select.getValue() == SelectType.USED) {
					File[] roots = File.listRoots();
					getNonFXItems().setAll(
							Arrays.asList(
								roots != null ? roots : new File[0]
							)
							.stream()
							.map(File::getAbsolutePath)
							.map(string -> string.substring(0, 1))
							.collect(Collectors.toList())
						);
				} else {
					getNonFXItems().setAll(
							ALPHABET
							.stream()
							.map(character -> character+":")
							.collect(Collectors.toList())
						);
				}
			} catch (Exception e) {
				getNonFXItems().clear();
			}
		});
	}

	@Override
	public void changed(ObservableValue<? extends SelectType> observable, SelectType oldValue, SelectType newValue) {
		refresh();
	}
}
