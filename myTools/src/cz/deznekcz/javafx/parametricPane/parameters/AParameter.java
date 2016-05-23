package cz.deznekcz.javafx.parametricPane.parameters;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.robot.FXRobotFactory;

import cz.deznekcz.javafx.parametricPane.dynamic.ADynamic;
import cz.deznekcz.javafx.parametricPane.dynamic.Editable;
import cz.deznekcz.javafx.parametricPane.dynamic.Enabled;
import cz.deznekcz.javafx.parametricPane.parsing.ParameterElement;
import cz.deznekcz.reference.Out;
import cz.deznekcz.util.ITryDo;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.StringConverter;

public abstract class AParameter <T> extends StringConverter<T> {
	
	private String id;
	private Tooltip tooltip;
	private List<ADynamic> implementation;
	private boolean implementationChecked;
	private boolean logic;
	private ParameterElement type;
	
	public abstract StringProperty valueProperty();
	
	public AParameter(String id, ParameterElement type) {
		this.type = type;
		this.id = id;
		this.tooltip = new Tooltip(type.tooltip(id));
		this.implementation = new ArrayList<>();
		this.implementationChecked = false;
	}
	
	public String getId() {
		return id;
	}

	public static class NameValueCallback implements 
			Callback<TableColumn.CellDataFeatures<AParameter<?>, String>, ObservableValue<String>> {
		@Override
		public ObservableValue<String> call(CellDataFeatures<AParameter<?>, String> param) {
			AParameter<?> parameter = param.getValue();
			return new ReadOnlyObjectWrapper<>(parameter.type.translate(parameter.id));
		}
	}

	public static class NameCellCallback extends TableCell<AParameter<?>, String>{
		@Override
		public void updateItem(String name, boolean empty) {
	        super.updateItem(name, empty);
			
	        if (name != null) {
	        	Label nameLabel = new Label(name);
	        	nameLabel.setTooltip(getTableRow().getTooltip());
	            setText(null);
	            setGraphic(nameLabel);
	            setTooltip(null);
	        } else {
	            setText(null);
	            setGraphic(null);
	            setTooltip(null);
	        }
		}
	}
	
	public static class ComponentValueCallback implements 
			Callback<TableColumn.CellDataFeatures<AParameter<?>, AParameter<?>>, ObservableValue<AParameter<?>>> {

		@Override
		public ObservableValue<AParameter<?>> call(CellDataFeatures<AParameter<?>, AParameter<?>> data) {
			AParameter<?> value = data.getValue();
	        return new ReadOnlyObjectWrapper<>(value);
	    }
	}
	
	public static class ComponentCellCalback extends TableCell<AParameter<?>, AParameter<?>> {
		@Override
	    protected void updateItem(AParameter<?> item, boolean empty) {
	        super.updateItem(item, empty);
	        
	        if (item != null) {
	            setText(null);
	            setGraphic(item.getComponent());
		        setTooltip(item.getTooltip());
	        } else {
	            setText(null);
	            setGraphic(null);
	            setTooltip(null);
	        }
	    }
	}

	protected abstract void initComponent();
	
	public abstract javafx.scene.Node getEnclosingComponent();

	public javafx.scene.Node getComponent() {
		javafx.scene.Node enclosingComponent = getEnclosingComponent();
		initDynamic();
		return enclosingComponent;
	}
	
	public boolean isLogic() {
		return logic;
	}
	
	public AParameter<T> logic() {
		this.logic = true;
		return this;
	}

	public AParameter<T> enabled(String params) {
		this.implementation.add(new Enabled(params, this));
		return this;
	}

	public abstract void setEnabled(boolean b);
	
	public abstract boolean isEnabled();
	
	public AParameter<T> editable(String params) {
		this.implementation.add(new Editable(params, this));
		return this;
	}
	
	public abstract void setEditable(boolean b);
	
	public abstract boolean isEditable();
	
	@Override
	public String toString(T object) {
		return valueProperty().getValue();
	}
	
	protected Tooltip getTooltip() {
		return tooltip;
	}

	public boolean isDynamic() {
		return implementation.size() > 0;
	}

	public void refresh() {
		for (ADynamic aDynamic : implementation) {
			aDynamic.refresh();
		}
	}

	public void initDynamic() {
		if (!implementationChecked) {
			implementationChecked = true;
			for (ADynamic aDynamic : implementation) {
				aDynamic.init();
			}
		}
	}

	public abstract void set(T newValue);

	public void set(String newValue) {
		fromString(newValue);
	}

	public String get() {
		return valueProperty().getValue();
	}

	public abstract void setFocusComponent();

	public abstract boolean isFocusTraversable();
}
