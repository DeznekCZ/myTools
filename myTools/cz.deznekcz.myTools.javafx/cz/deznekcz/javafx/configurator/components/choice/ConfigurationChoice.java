package cz.deznekcz.javafx.configurator.components.choice;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.javafx.configurator.components.Choice;
import cz.deznekcz.javafx.configurator.components.path.ConfigurationPath;
import cz.deznekcz.javafx.configurator.components.support.HasDirProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class ConfigurationChoice extends Choice implements HasDirProperty {

	private static class ConfigurationChoiceSkin extends ConfigurationChoice.ChoiceSkin {

		private static final String PREFIX = "| ";
		private StringProperty dir;
		private Label dirLabel;
		private ConfigurationPath openButton;

		private BooleanProperty unnecesary;
		private BooleanProperty openable;

		public ConfigurationChoiceSkin(ConfigurationChoice text) {
			super(text);

			dir = new SimpleStringProperty();
			dir.addListener((o,l,n) -> {
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

			openable = new SimpleBooleanProperty(false);
			openable.addListener((o,l,n) -> {
				if (!n) getValueDecorator().setBottom(null);
				else    {
					initOpen();
					getValueDecorator().setRight(openButton);
				}
			});
		}

		@Override
		public ConfigurationChoice getSkinnable() {
			return (ConfigurationChoice) super.getSkinnable();
		}

		@Override
		public Node getNode() {
			return super.getNode();
		}

		@Override
		public void dispose() {

		}

		private void initOpen() {
			openButton = new ConfigurationPath();
			openButton.setSelectable(false);
			openButton.valueProperty().bind(new StringBinding() {
				StringProperty file = getSkinnable().valueProperty();
				{
					bind(dir, file);
				}
				@Override
				protected String computeValue() {
					return dir.get() + "\\" + file.get();
				}
			});
		}

	}

	public ConfigurationChoice() {
		setSkin(new ConfigurationChoiceSkin(this));
		register();
	}

	public StringProperty dirProperty() {
		return ((ConfigurationChoiceSkin) getSkin()).dir;
	}

	public void setDir(String dir) {
		dirProperty().set(dir);
	}

	public String getDir() {
		return dirProperty().get();
	}

	public BooleanProperty openableProperty() {
		return ((ConfigurationChoiceSkin) getSkin()).openable;
	}

	public void setOpenable(boolean openable) {
		openableProperty().set(openable);
	}

	public boolean isOpenable() {
		return openableProperty().get();
	}

	@Override
	protected void refreshList() {
		getItems().clear();
		try {
			getItems().addAll(
				Arrays.asList(
					new File(getDir()).listFiles()
				)
				.stream()
				.filter((f)->f.isFile())
				.map((f)->f.getName())
				.filter((name)->name.endsWith(".run.xml"))
				.collect(Collectors.toList())
			);
		} catch (Exception e) {
		}
	}
}
