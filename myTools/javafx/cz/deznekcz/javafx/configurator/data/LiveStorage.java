package cz.deznekcz.javafx.configurator.data;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.sun.javafx.collections.ObservableMapWrapper;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.ConfiguratorController;
import cz.deznekcz.reference.OutString;
import cz.deznekcz.tool.i18n.Lang;
import cz.deznekcz.util.Utils;
import cz.deznekcz.util.xml.XML;
import cz.deznekcz.util.xml.XMLLoader;
import cz.deznekcz.util.xml.XMLStepper;
import cz.deznekcz.util.xml.XMLStepper.Step;
import cz.deznekcz.util.xml.XMLStepper.StepDocument;
import cz.deznekcz.util.xml.XMLStepper.StepList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class LiveStorage {

	private final String TITLE;
	private StringProperty id;
	private StepDocument xmlRoot;
	private File cfg;
	private BorderPane component;
	private FXMLLoader loader;
	private ASetup setup;
	private HashMap<String, String> searched;
	private URL fxmlFile;
	private ResourceBundle bundle;
	private Tab tab;
	private StepList valueList;
	
	public LiveStorage(File cfg, ConfiguratorController ctrl) throws Exception {
		this.cfg = cfg;
		
		xmlRoot = XMLStepper.fromFile(cfg.getPath());
		id = new SimpleStringProperty(xmlRoot.getNode("storage/id").text());
		
		searched = new HashMap<>();
		
		valueList = xmlRoot.getList("storage/values/value");
		for (Step value : valueList.asList()) {
			searched.put(value.attribute("id"), value.text());
		}
		
		URL[] urls = {new File("config\\" + id.get()).toURI().toURL()};
		ClassLoader clLoader = new URLClassLoader(urls);
		
		fxmlFile = new File("config\\" + id.get() + "\\Layout.fxml").toURI().toURL();
		bundle = ResourceBundle.getBundle("lang", Locale.getDefault(), clLoader);
		
		loader = new FXMLLoader(fxmlFile, bundle);
		component = loader.load();
		TITLE = "Configurator.config." + id.get();
		tab = new Tab(bundle.getString(TITLE), component);
		tab.setClosable(true);
		
		setup = loader.<ASetup>getController();
		setup.externalInitializetion(ctrl, this, tab);
	}

	public StringProperty idProperty() {
		return id;
	}

	public void save() {
		try {
			OutString valueEntries = OutString.init();
			
			valueEntries.append(XML.headUTF8()); valueEntries.append("\n");
			valueEntries.append(XML.startTag("storage")); valueEntries.append("\n  ");
				valueEntries.append(XML.textTag("id", id.get())); valueEntries.append("\n  ");
				valueEntries.append(XML.startTag("values")); valueEntries.append("\n    ");
					for (String value : searched.keySet()) {
						valueEntries.append(XML.startTag("value", XML.attribute("id", value)));
						valueEntries.append(XML.CDATA(searched.get(value)));
						valueEntries.append(XML.endTag("value")); valueEntries.append("\n    ");
					}
				valueEntries.subSequence(0, valueEntries.length()-2)
				            .append(XML.endTag("values")); valueEntries.append("\n");
			valueEntries.append(XML.endTag("storage"));
			
			XMLLoader.save(cfg, valueEntries.get());
		} catch (Exception e) {
			Dialog.EXCEPTION.show(e);
		}
	}
	
	public String getId() {
		return id.get();
	}
	
	public void setId(String id) {
		this.id.set(id);
	}
	
	public Tab getTab() {
		return tab;
	}

	public String getValue(String key) {
		return searched.get(key);
	}

	public void setValue(String key, String value) {
		if (!value.equals(searched.put(key, value))) {
			save(); 
		}
	}
}
