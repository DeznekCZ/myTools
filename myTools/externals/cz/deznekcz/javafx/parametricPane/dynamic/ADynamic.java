package cz.deznekcz.javafx.parametricPane.dynamic;

import java.util.ArrayList;
import java.util.Arrays;

import cz.deznekcz.javafx.parametricPane.ParametricPane;
import cz.deznekcz.javafx.parametricPane.parameters.AParameter;
import cz.deznekcz.util.ForEach;
import javafx.beans.property.Property;

public abstract class ADynamic<T> {
	public class DynamicEntry {
		private Property<String> param;
		private boolean desired;
		
		public DynamicEntry(Property<String> param, boolean desired) {
			this.param = param;
			this.desired = desired;
		}

		public boolean isTrue() {
			return Boolean.parseBoolean(param.getValue()) == desired;
		}
	}

	private AParameter<T> parameter;
	private String ids;
	private IDynamicFunction<T> setFunction;
	private ArrayList<DynamicEntry> paramList;

	public ADynamic(String params, AParameter<T> parameter) {
		this.ids = params;
		this.parameter = parameter;
		this.setFunction = getFunction();
		this.paramList = new ArrayList<>();
	}
	
	protected abstract IDynamicFunction<T> getFunction();

	public void init() {
		ForEach.start(Arrays.asList(ids.split(";")), (value) -> {
			String[] detail = value.split("=");
			boolean desiredValue = detail.length > 1 ? Boolean.parseBoolean(detail[1]) : true;
			Property<String> param = ParametricPane.getInstance().parameterValueByName(detail[0]);
			param.addListener((e,l,n) -> {
				refresh();
			});
			paramList.add(new DynamicEntry(param, desiredValue));
			return true;
		});
	}
	
	public void refresh() {
		boolean result = true;
		for (DynamicEntry dynamicEntry : paramList) {
			result = result && dynamicEntry.isTrue();
			if (result == false) break;
		}
		System.out.println("Dynamic: "+setFunction.getClass().getSimpleName()+" for "+parameter.getId());
		setFunction.apply(parameter, result);
	}
}
