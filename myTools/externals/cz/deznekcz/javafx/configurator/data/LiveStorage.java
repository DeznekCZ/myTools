package cz.deznekcz.javafx.configurator.data;

import java.io.File;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.util.xml.XMLStepper;
import cz.deznekcz.util.xml.XMLStepper.Step;
import cz.deznekcz.util.xml.XMLStepper.StepDocument;
import cz.deznekcz.util.xml.XMLStepper.StepList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LiveStorage {

	private StringProperty name;

	@SuppressWarnings("unchecked")
	public LiveStorage(File cfg) throws Exception {
		StepDocument stepper = XMLStepper.fromFile(cfg.getPath());
		name = new SimpleStringProperty(stepper.getNode("storage/name").text());
		
		
		clazz.getConstructor(LiveStorage.class).newInstance(this);
		
		StepList valueList = stepper.getList("storage/values");
		for (Step value : valueList.asList()) {
			
		}
	}

	public StringProperty nameProperty() {
		return name;
	}

}
