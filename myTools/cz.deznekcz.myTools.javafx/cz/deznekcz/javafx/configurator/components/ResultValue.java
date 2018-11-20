package cz.deznekcz.javafx.configurator.components;

import java.util.Calendar;
import java.util.function.Supplier;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Configurator.result;
import cz.deznekcz.javafx.configurator.components.result.ResultValueImage;
import cz.deznekcz.javafx.configurator.components.support.AValue;
import cz.deznekcz.javafx.configurator.components.support.Refreshable;
import cz.deznekcz.util.ForEach;
import cz.deznekcz.util.Utils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

public class ResultValue extends AValue {

	private static class ResultValueSkin implements Skin<ResultValue> {

		private ResultValue text;
		private BorderPane box;
		private Label label;
		private Label timeLabel;

		private ResultValueImage okValue;
		private ResultValueImage failValue;
		private ResultValueImage noResultValue;
		private ResultValueImage invalidFunctionValue;

		private StringProperty value;

		private StringProperty tooltipText;
		private StringProperty fullTooltip;

		private SimpleObjectProperty<result> resultPropterty;
		private Property<Supplier<Configurator.result>> onValidation;
		private LongProperty timePropterty;

		public ResultValueSkin(ResultValue text) {
			this.text = text;
			text.getStyleClass().add("text-value");
			text.setTooltip(new Tooltip());

			box = new BorderPane();
			label = new Label();
			timeLabel = new Label();
			okValue = new ResultValueImage("resultOkIcon.png", result.OK.value());
			failValue = new ResultValueImage("resultFailIcon.png", result.FAIL.value());
			noResultValue = new ResultValueImage("resultNoResultIcon.png", result.NR.value());
			invalidFunctionValue = new ResultValueImage("resultInvalidIcon.png", result.IF.value());
			box.disableProperty().bind(text.disableProperty());

			label.getStyleClass().add("result-value-label");
			timeLabel.getStyleClass().add("result-value-time-label");
			okValue.getStyleClass().add("result-value-ok");
			failValue.getStyleClass().add("result-value-fail");
			noResultValue.getStyleClass().add("result-value-no-result");
			invalidFunctionValue.getStyleClass().add("result-value-invalid-function");

			label.idProperty().bind(text.idProperty().concat("_label"));
			timeLabel.idProperty().bind(text.idProperty().concat("_timeLabel"));
			okValue.idProperty().bind(text.idProperty().concat("_ok"));
			failValue.idProperty().bind(text.idProperty().concat("_fail"));
			noResultValue.idProperty().bind(text.idProperty().concat("_noResult"));
			invalidFunctionValue.idProperty().bind(text.idProperty().concat("_invalidFunction"));

			BorderPane.setAlignment(label, Pos.CENTER_LEFT);
			box.setLeft(label);
			BorderPane.setAlignment(timeLabel, Pos.CENTER_RIGHT);
			timeLabel.setPadding(new Insets(0, 5, 0, 0));
			box.setCenter(timeLabel);
			box.setRight(invalidFunctionValue);

			onValidation = new SimpleObjectProperty<>();

			resultPropterty = new SimpleObjectProperty<>(result.IF);
			resultPropterty.addListener((o,l,n) -> {
				box.setRight(resultNode(n));
				listener(null, null, null);
			});

			onValidation.addListener((o,l,n) -> {
				text.refresh();
			});

			timePropterty = new SimpleLongProperty(0L);
			timeLabel.textProperty().bind(new StringBinding() {
				{
					bind(timePropterty);
				}
				@Override
				protected String computeValue() {
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(timePropterty.get());
					return (timePropterty.get() == 0) ? "" :
						String.format("%1$te. %1$tb %1$tY, %1$tT", calendar);
				}
			});

			tooltipText = new SimpleStringProperty();
			fullTooltip = new SimpleStringProperty();

			for (StringExpression expr :
					Utils.array(
						tooltipText,
						okValue.tooltipTextProperty(), failValue.tooltipTextProperty(),
						invalidFunctionValue.tooltipTextProperty(), noResultValue.tooltipTextProperty()
					)
				) {
				expr.addListener(this::listener);
			}

			value = new SimpleStringProperty(resultPropterty.get().name());
			value.addListener((o,l,n) -> {
				result r = result.valueOf(n);
				if (r != null) {
					if (!r.name().equals(value.getValue())) resultPropterty.set(r);
				} else {
					value.set(result.IF.name());
				}
			});

			resultPropterty.addListener((o,l,n) -> {
				if (n != null) {
					if (!resultPropterty.get().name().equals(value.getValue())) value.set(n.name());
				} else {
					resultPropterty.set(result.IF);
				}
			});
		}

		private void listener(ObservableValue<? extends String> o, String l, String n) {
			ResultValueImage image = resultNode(resultPropterty.get());
			boolean isImageTooltiped = image.getTooltipText() != null;
			boolean isTooltiped = getSkinnable().getHelp() != null;

			String tooltipText = null;
			String lastTooltipText = fullTooltip.get();

			if (isTooltiped && isImageTooltiped) {
				tooltipText = getSkinnable().getHelp() + ": \n" + image.getTooltipText();
			} else if (isTooltiped && !isImageTooltiped) {
				tooltipText = getSkinnable().getHelp();
			} else if (!isTooltiped && isImageTooltiped) {
				tooltipText = image.getTooltipText();
			}

			if (lastTooltipText == null ? tooltipText != null : !lastTooltipText.equals(tooltipText)) {
				getSkinnable().setTooltip(new Tooltip(tooltipText));
				fullTooltip.unbind();
				fullTooltip.bind(getSkinnable().getTooltip().textProperty());
			}
		}

		private ResultValueImage resultNode(result n) {
			switch (n) {
			case OK:   return okValue;
			case FAIL: return failValue;
			case NR:   return noResultValue;
			default:   return invalidFunctionValue;
			}
		}

		@Override
		public ResultValue getSkinnable() {
			return text;
		}

		@Override
		public Node getNode() {
			return box;
		}

		@Override
		public void dispose() {

		}

	}

	public StringProperty textProperty() {
		return ((ResultValueSkin) getSkin()).label.textProperty();
	}

	public String getText() {
		return textProperty().get();
	}

	public void setText(String text) {
		this.textProperty().set(text);
	}

	public StringProperty helpPropterty() {
		return ((ResultValueSkin) getSkin()).tooltipText;
	}

	public void setHelp(String prompt) {
		helpPropterty().set(prompt);
	}

	public String getHelp() {
		return helpPropterty().get();
	}

	public StringProperty helpOkPropterty() {
		return ((ResultValueSkin) getSkin()).okValue.tooltipTextProperty();
	}

	public void setHelpOk(String prompt) {
		helpOkPropterty().set(prompt);
	}

	public String getHelpOk() {
		return helpOkPropterty().get();
	}

	public StringProperty helpFailPropterty() {
		return ((ResultValueSkin) getSkin()).failValue.tooltipTextProperty();
	}

	public void setHelpFail(String prompt) {
		helpFailPropterty().set(prompt);
	}

	public String getHelpFail() {
		return helpFailPropterty().get();
	}

	public StringProperty helpInvalidFunctionPropterty() {
		return ((ResultValueSkin) getSkin()).invalidFunctionValue.tooltipTextProperty();
	}

	public void setHelpInvalidFunction(String prompt) {
		helpInvalidFunctionPropterty().set(prompt);
	}

	public String getHelpInvalidFunction() {
		return helpInvalidFunctionPropterty().get();
	}

	public StringProperty helpNoResultPropterty() {
		return ((ResultValueSkin) getSkin()).noResultValue.tooltipTextProperty();
	}

	public void setHelpNoResult(String prompt) {
		helpNoResultPropterty().set(prompt);
	}

	public String getHelpNoResult() {
		return helpNoResultPropterty().get();
	}

	public String getHelpFull() {
		return ((ResultValueSkin) getSkin()).fullTooltip.get();
	}

	public Property<Supplier<Configurator.result>> validatorProperty() {
		return ((ResultValueSkin) getSkin()).onValidation;
	}

	public void setValidator(Supplier<Configurator.result> validation) {
		validatorProperty().setValue(validation);
	}

	public Supplier<Configurator.result> getValidator() {
		return validatorProperty().getValue();
	}

	public Property<result> resultProperty() {
		return ((ResultValueSkin) getSkin()).resultPropterty;
	}

	public result getResult() {
		return resultProperty().getValue();
	}

	public void setResult(result resultValue) {
		this.resultProperty().setValue(resultValue);
	}

	public LongProperty timeProperty() {
		return ((ResultValueSkin) getSkin()).timePropterty;
	}

	public long getTime() {
		return timeProperty().get();
	}

	public void setTime(long time) {
		timeProperty().set(time);
	}

	public void refresh() {
		if (!resultProperty().isBound()) {
			if (getValidator() != null) {
				setResult(getValidator().get());
			} else {
				setResult(result.IF);
			}
		} else {
			// changes independently to refresh
		}
	}

	public ResultValue() {
		setSkin(new ResultValueSkin(this));
		setFocusTraversable(false);
		register();
	}

	public BooleanBinding isBinding(result resultValue) {
		return new BooleanBinding() {
			{
				bind(resultProperty());
			}
			@Override
			protected boolean computeValue() {
				return resultProperty().getValue() == resultValue;
			}
		};
	}

	private boolean searched;
	private ASetup found;

	@Override
	public ASetup getConfiguration() {
		if (searched) return found;
		searched = true;

		for (Tab tab : Configurator.getCtrl().getConfigs()) {
			Node tabContent = tab.getContent();
			Node parent = getParent();
			while (parent != null && !parent.equals(tabContent))
				parent = parent.getParent();
			if (parent != null) {
				found = (ASetup) tab.getProperties().get(ASetup.class);
			}
		}

		return found;
	}

	@Override
	public Property<String> valueProperty() {
		return ((ResultValueSkin) getSkin()).value;
	}

	@Override
	public EventTarget getEventTarget() {
		return null;
	}
}
