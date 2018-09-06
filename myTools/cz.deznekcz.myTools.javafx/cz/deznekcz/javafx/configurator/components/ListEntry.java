package cz.deznekcz.javafx.configurator.components;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cz.deznekcz.javafx.binding.AndBooleanBinding;
import cz.deznekcz.javafx.configurator.components.support.AValue;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ListEntry extends AValue {

	public class ValueBinding extends StringBinding {
		private LongProperty invalidator;
		private String lastValue;

		public ValueBinding() {
			invalidator = new SimpleLongProperty(0);

			bind(start, join, end, values, invalidator);
		}
		@Override
		protected String computeValue() {
			if (!mismach.get()) {
				lastValue = ( hasStart() ? getStart() : "" )
						+	( hasJoin()
								? values.stream().map(ObservableValue::getValue).collect(Collectors.joining(getJoin()))
								: values.stream().map(ObservableValue::getValue).collect(Collectors.joining()) )
						+	( hasEnd()   ? getEnd()   : "" )
				;
			}
			return lastValue;
		}
		@Override
		public String toString() {
			return String.format("ArrayBinding [start=\"%s\", join=\"%s\", end=\"%s\", value=%s]"
					, hasStart() ? getStart() : ""
					, hasJoin()  ? getJoin()  : ""
					, hasEnd()   ? getEnd()   : ""
					, Arrays.toString(values.toArray()));
		}

		public void newChange() {
			invalidator.set(invalidator.get() + 1L);
		}
	}

	public class ListEntryField extends HBox implements InvalidationListener {

		private TextField field = new TextField();
		private Button removeButton = new Button("X");
		private BooleanProperty match = new SimpleBooleanProperty(false);
		private VBox content;

		public ListEntryField(VBox content) {
			this.content = content;

			field.getStylesheets().add(ListEntry.this.getClass().getPackage().getName().replace('.', '/').concat("/TextEntry.css"));

			removeButton.setOnAction(this::remove);
			field.textProperty().addListener(this);

			pattern.addListener(this);
			validator.addListener(this);

			HBox.setHgrow(field, Priority.ALWAYS);
			getChildren().add(field);
			getChildren().add(removeButton);

			field.getStyleClass().add(styleSheetString);
			field.pseudoClassStateChanged(PseudoClass.getPseudoClass("mismach"), !match.get() && isLimited());

			conditions.add(match);
			content.getChildren().add(this);
			values.add(field.textProperty());
		}

		@Override
		public void invalidated(Observable observable) {
			if (field.getText() != null) {
				match.set(!isLimited() ||
						getValidator() !=null
						? getValidator().test(field.getText())
						: field.getText().matches(getPattern()));
			} else {
				match.set(!isLimited());
			}

			field.pseudoClassStateChanged(PseudoClass.getPseudoClass("mismach"), !match.get());
			valueBinding.newChange();
		}

		private void remove(ActionEvent event) {
			pattern.removeListener(this);
			validator.removeListener(this);
			field.textProperty().removeListener(this);
			content.getChildren().remove(this);
			values.remove(field.textProperty());
			conditions.remove(match);
			valueBinding.newChange();
		}

		public void setValue(String value) {
			field.setText(value);
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}
	}

	private class ListEntrySkin implements Skin<ListEntry> {

		private BorderPane box;
		private BorderPane contentdecorator;

		private Label label;
		private VBox content;
		private Button addButton;

		public ListEntrySkin() {
			ListEntry.this.getStyleClass().add("list-entry");
			ListEntry.this.setTooltip(new Tooltip(""));

			box = new BorderPane();
			label = new Label();
			content = new VBox();
			addButton = new Button("+");
			addButton.setOnAction(e -> new ListEntryField(content));
			contentdecorator = new BorderPane();
			box.disableProperty().bind(ListEntry.this.disableProperty());

			label.getStyleClass().add("list-entry-label");
			content.getStyleClass().add("list-entry-content");
			addButton.getStyleClass().add("list-entry-add-button");

			label.idProperty().bind(ListEntry.this.idProperty().concat("_label"));
			content.idProperty().bind(ListEntry.this.idProperty().concat("_value"));
			addButton.idProperty().bind(ListEntry.this.idProperty().concat("_add"));

			content.maxWidthProperty().bind(ListEntry.this.widthProperty().divide(2));
			content.prefWidthProperty().bind(ListEntry.this.widthProperty().divide(2));

			label.setOnMouseClicked((e) -> {
				if (e.getButton() == MouseButton.PRIMARY) content.requestFocus();
			});

			BorderPane.setAlignment(label, Pos.CENTER_LEFT);
			BorderPane.setAlignment(contentdecorator, Pos.CENTER_RIGHT);
			box.setLeft(label);
			box.setRight(contentdecorator);

			BorderPane.setAlignment(addButton, Pos.BOTTOM_LEFT);
			BorderPane.setAlignment(content, Pos.CENTER_RIGHT);
			contentdecorator.setLeft(addButton);
			contentdecorator.setCenter(content);
		}

		@Override
		public ListEntry getSkinnable() {
			return ListEntry.this;
		}

		@Override
		public Node getNode() {
			return box;
		}

		@Override
		public void dispose() {

		}

	}

	private StringProperty value;
	private ObservableList<ObservableValue<String>> values;

	private StringProperty join;
	private StringProperty start;
	private StringProperty end;
	private StringProperty pattern;
	private BooleanProperty limited;
	private BooleanProperty mismach;
	private AndBooleanBinding conditions;
	private ValueBinding valueBinding;

	private String styleSheetString;
	private ObjectProperty<Predicate<String>> validator;

	public ListEntry() {
		styleSheetString = getClass().getPackage().getName().replace('.', '/').concat("/TextEntry.css");

		values = FXCollections.observableArrayList();
		value  = new SimpleStringProperty("");

		pattern = new SimpleStringProperty("*");

		conditions = new AndBooleanBinding();
		limited    = new SimpleBooleanProperty(false);
		mismach    = new SimpleBooleanProperty(false);
		validator  = new SimpleObjectProperty<>(patternValidator());

		limited.bind(validator.isNotNull().or(pattern.isNotEqualTo("*")));
		mismach.bind(conditions.not());

		pattern.addListener((o,l,n) -> {
			if (n == null || n.length() == 0) {
				pattern.set("*");
			} else if (n != null && !n.equals("*")) {
				validator.set(patternValidator());
			}
		});

		start = new SimpleStringProperty("");
		join  = new SimpleStringProperty("");
		end   = new SimpleStringProperty("");

		setSkin(new ListEntrySkin());

		valueBinding = new ValueBinding();
		value.addListener((o,l,n) -> {
			if (n != null) {
				if (n.equals(valueBinding.get())) return;

				if (hasStart() && n.startsWith(getStart()))
					n = n.substring(getStart().length());
				if (hasEnd()   && n.endsWith(getEnd()))
					n = n.substring(0, n.length() - getEnd().length());
				values.clear();
				for (String value : n.split(toRegex(getJoin()))) {
					new ListEntryField(((ListEntrySkin) getSkin()).content).setValue(value);
				}
			} else if (n == null) {
				values.clear();
			}
		});
		valueBinding.addListener((o,l,n) -> {
			if (n != null) {
				if (!n.equals(value.get())) {
					value.set(n);
				}
			} else {
				if (value.get() != null) {
					value.set(null);
				}
			}
		});
	}

	private Predicate<String> patternValidator() {
		return string -> string.matches(getPattern());
	}

	private String toRegex(String nonRegex) {
		return nonRegex != null ? Pattern.quote(nonRegex) : "";
	}

	public boolean hasStart() {
		return getStart() != null && getStart().length() > 0;
	}

	public boolean hasJoin() {
		return getJoin() != null && getJoin().length() > 0;
	}

	public boolean hasEnd() {
		return getEnd() != null && getEnd().length() > 0;
	}

	@Override
	public Property<String> valueProperty() {
		return value;
	}

	@Override
	public void refresh() {

	}

	public StringProperty joinProperty() {
		return join;
	}

	public String getJoin() {
		return joinProperty().get();
	}

	public void setJoin(String value) {
		this.joinProperty().set(value);
	}

	public StringProperty startProperty() {
		return start;
	}

	public String getStart() {
		return startProperty().get();
	}

	public void setStart(String value) {
		this.startProperty().set(value);
	}

	public StringProperty endProperty() {
		return end;
	}

	public String getEnd() {
		return endProperty().get();
	}

	public void setEnd(String value) {
		this.endProperty().set(value);
	}

	public StringProperty textProperty() {
		return ((ListEntrySkin) getSkin()).label.textProperty();
	}

	public String getText() {
		return textProperty().get();
	}

	public void setText(String text) {
		this.textProperty().set(text);
	}

	public StringProperty patternProperty() {
		return pattern;
	}

	public String getPattern() {
		return patternProperty().get();
	}

	public void setPattern(String text) {
		this.patternProperty().set(text != null && text.length() > 0 ? text : "*");
	}

	public ReadOnlyBooleanProperty limitedProperty() {
		return limited;
	}

	public boolean isLimited() {
		return limitedProperty().get();
	}

	public boolean hasPattern() {
		return !isLimited();
	}

	public ReadOnlyBooleanProperty mismachProperty() {
		return mismach;
	}

	public Boolean isMismach() {
		return mismachProperty().get();
	}

	public Property<Predicate<String>> validatorProperty() {
		return validator;
	}

	public void setValidator(Predicate<String> validator) {
		validatorProperty().setValue(validator);
	}

	public Predicate<String> getValidator() {
		return validatorProperty().getValue();
	}

}
