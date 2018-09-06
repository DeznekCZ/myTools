package cz.deznekcz.javafx.configurator.components;

import java.util.function.Predicate;
import java.util.function.Supplier;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Configurator.result;
import cz.deznekcz.javafx.configurator.Unnecesary;
import cz.deznekcz.javafx.configurator.components.support.AValue;
import cz.deznekcz.tool.i18n.ILangKey;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

@DefaultProperty("value")
public abstract class Path extends AValue {

	private final ILangKey OPEN;
	private final ILangKey SELECT;
	private java.io.File last = new java.io.File(".");

	protected static class PathSkin implements Skin<Path> {

		private Path text;
		private BorderPane box;
		private Label label;
		private TextField value;
		private Button button;
		private BorderPane path;
		private ResultValue upToDateResult;

		private BooleanProperty invalidValue;
		private BooleanProperty selectable;
		private BooleanBinding valueVisible;
		private BooleanProperty visibleField;
		private StringProperty selectButtonText;
		private StringProperty openButtonText;
		public Property<Supplier<result>> upToDateFunction;

		public PathSkin(Path text) {
			this.text = text;
			text.getStyleClass().add("path");
			text.setTooltip(new Tooltip());

			box = new BorderPane();
			label = new Label();
			value = new TextField();
			button = new Button();

			selectButtonText = new SimpleStringProperty(text.SELECT.value());
			openButtonText = new SimpleStringProperty(text.OPEN.value());

			button.textProperty().bind(selectButtonText);
			button.setOnAction(text::selectPath);

			path = new BorderPane();
			path.setCenter(value);
			path.setRight(button);
			path.maxWidthProperty().bind(text.widthProperty().divide(2));
			path.prefWidthProperty().bind(text.widthProperty().divide(2));

			box.disableProperty().bind(text.disableProperty());

			label.getStyleClass().add("path-label");
			value.getStyleClass().add("path-value");

			label.idProperty().bind(text.idProperty().concat("_label"));
			value.idProperty().bind(text.idProperty().concat("_value"));
			button.idProperty().bind(text.idProperty().concat("_button"));

			BorderPane.setAlignment(label, Pos.CENTER_LEFT);
			box.setLeft(label);
			box.setRight(path);

			invalidValue = new SimpleBooleanProperty(false);
			value.textProperty().addListener((o,l,n) -> refresh());

			selectable = new SimpleBooleanProperty(true);
			selectable.addListener((o,l,n) -> {
				if (n) {
					button.textProperty().bind(selectButtonText);
					button.setOnAction(text::selectPath);
					button.disableProperty().unbind();
				}
				else   {
					button.textProperty().bind(openButtonText);
					button.setOnAction(text::openPath);
					button.disableProperty().bind(invalidValue);
				}
			});

			visibleField = new SimpleBooleanProperty(false);

			valueVisible = selectable.or(Unnecesary.hiddenProperty().not().or(visibleField));
			valueVisible.addListener((o,l,n) -> {
				path.setCenter(n ? value : null);
			});

			upToDateResult = new ResultValue();
			upToDateFunction = new SimpleObjectProperty<Supplier<result>>();
			upToDateResult.validatorProperty().bind(upToDateFunction);
			upToDateFunction.addListener((o,l,n) -> {
				if (n != null) {
					button.setGraphic(upToDateResult);
				} else {
					button.setGraphic(null);
				}
			});
		}

		@Override
		public Path getSkinnable() {
			return text;
		}

		@Override
		public Node getNode() {
			return box;
		}

		@Override
		public void dispose() {

		}

		public void refresh() {
			invalidValue.set(!text.getValidator().test(value.getText()));
		}
	}

	public StringProperty textProperty() {
		return ((PathSkin) getSkin()).label.textProperty();
	}

	public String getText() {
		return textProperty().get();
	}

	public void setText(String text) {
		this.textProperty().set(text);
	}

	public StringProperty selectButtonTextPropterty() {
		return ((PathSkin) getSkin()).selectButtonText;
	}

	public String getSelectButtonText() {
		return selectButtonTextPropterty().get();
	}

	public void setSelectButtonText(String text) {
		this.selectButtonTextPropterty().set(text);
	}

	public StringProperty openButtonTextPropterty() {
		return ((PathSkin) getSkin()).openButtonText;
	}

	public String getOpenButtonText() {
		return openButtonTextPropterty().get();
	}

	public void setOpenButtonText(String text) {
		this.openButtonTextPropterty().set(text);
	}

	public StringProperty valueProperty() {
		return ((PathSkin) getSkin()).value.textProperty();
	}

	public StringProperty helpPropterty() {
		return getTooltip().textProperty();
	}

	public void setHelp(String prompt) {
		helpPropterty().set(prompt);
	}

	public String getHelp() {
		return helpPropterty().get();
	}

	public BooleanProperty selectablePropterty() {
		return ((PathSkin) getSkin()).selectable;
	}

	public boolean isSelectable() {
		return selectablePropterty().get();
	}

	public void setSelectable(boolean selectable) {
		selectablePropterty().set(selectable);
	}

	public BooleanProperty visibleFieldPropterty() {
		return ((PathSkin) getSkin()).visibleField;
	}

	public boolean isVisibleField() {
		return visibleFieldPropterty().get();
	}

	public void setVisibleField(boolean selectable) {
		visibleFieldPropterty().set(selectable);
	}

	public java.io.File getLast() {
		return last;
	}

	public void setLast(java.io.File last) {
		this.last = last;
	}

	public abstract void openPath(ActionEvent event);

	public abstract void selectPath(ActionEvent event);

	public abstract Predicate<String> getValidator();

	protected Path(ILangKey open, ILangKey select) {
		this.OPEN = open;
		this.SELECT = select;
		setSkin(new PathSkin(this));
		register();
	}

	public Property<Supplier<Configurator.result>> upToDateFunctionProperty() {
		return ((PathSkin) getSkin()).upToDateFunction;
	}

	public void setUpToDateFunction(Supplier<Configurator.result> resultFunction) {
		upToDateFunctionProperty().setValue(resultFunction);
	}

	public Supplier<Configurator.result> getUpToDateFunction() {
		return upToDateFunctionProperty().getValue();
	}

	@Override
	public void refresh() {
		((PathSkin) getSkin()).refresh();
	}
}
