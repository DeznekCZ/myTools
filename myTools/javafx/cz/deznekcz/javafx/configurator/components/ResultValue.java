package cz.deznekcz.javafx.configurator.components;

import java.util.Calendar;
import java.util.function.Supplier;

import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Configurator.result;
import cz.deznekcz.javafx.configurator.components.result.ResultValueImage;
import cz.deznekcz.javafx.configurator.components.support.Refreshable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

public class ResultValue extends Control implements Refreshable {
	
	private static class CheckValueSkin implements Skin<ResultValue> {

		private ResultValue text;
		private BorderPane box;
		private Label label;
		private Label timeLabel;

		private Control okValue;
		private Control failValue;
		private Control noResultValue;
		private Control invalidFunctionValue;

		private SimpleObjectProperty<result> resultPropterty;
		private Property<Supplier<Configurator.result>> onValidation;
		private LongProperty timePropterty;
		
		public CheckValueSkin(ResultValue text) {
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
		}

		private Node resultNode(result n) {
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
		return ((CheckValueSkin) getSkin()).label.textProperty();
	}
	
	public String getText() {
		return textProperty().get();
	}
	
	public void setText(String text) {
		this.textProperty().set(text);
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
	
	public Property<Supplier<Configurator.result>> validatorProperty() {
		return ((CheckValueSkin) getSkin()).onValidation;
	}
	
	public void setValidator(Supplier<Configurator.result> validation) {
		validatorProperty().setValue(validation);
	}
	
	public Supplier<Configurator.result> getValidator() {
		return validatorProperty().getValue();
	}
	
	public Property<result> resultProperty() {
		return ((CheckValueSkin) getSkin()).resultPropterty;
	}
	
	public result getResult() {
		return resultProperty().getValue();
	}
	
	public void setResult(result resultValue) {
		this.resultProperty().setValue(resultValue);
	}

	public LongProperty timeProperty() {
		return ((CheckValueSkin) getSkin()).timePropterty;
	}
	
	public long getTime() {
		return timeProperty().get();
	}
	
	public void setTime(long time) {
		timeProperty().set(time);
	}
	
	public void refresh() {
		if (!resultProperty().isBound() && getValidator() != null)
			setResult(getValidator().get());
	}
	
	public ResultValue() {
		setSkin(new CheckValueSkin(this));
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
}
