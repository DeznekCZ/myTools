package cz.deznekcz.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.reference.OutString;
import cz.deznekcz.util.xml.XML;
import cz.deznekcz.util.xml.XMLLoader;
import cz.deznekcz.util.xml.XMLStepper;
import cz.deznekcz.util.xml.XMLStepper.Step;
import cz.deznekcz.util.xml.XMLStepper.StepDocument;
import cz.deznekcz.util.xml.XMLStepper.StepList;
import cz.deznekcz.util.xml.XMLPairTag;
import cz.deznekcz.util.xml.XMLRoot;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LiveStorage {

    private StringProperty id;
    private File cfg;
    private Map<String, String> searched;

    private XML xml;
    private XMLPairTag<XMLRoot> values;

    public LiveStorage(File cfg) throws Exception {
        this.cfg = cfg;

        searched = new HashMap<String, String>();
        
        StepDocument document = XMLStepper.fromFile(cfg);
        id = new SimpleStringProperty( document.getNode("storage/id").text() );

        xml = XML.init("storage");
        xml.comment("Autogenerated by LiveStorage.save()");
        values = xml.root() // continues at next line with definition
            .newPairTag("id", false)
                .setComment("Loads a configuration tab")
                .setText(id.get())
            .close()
            .newPairTag("values")
                .setComment("Last stored values");
        
        for (Step value : document.getList("storage/values/value")) {
        	searched.put(value.attribute("id"), value.text());
        }
    }

    public StringProperty idProperty() {
        return id;
    }

    public void save() {
        try {
            values.clear();
            // Fill values
            for (String value : searched.keySet()) {
                values.newPairTag("value", false)
                        .addAttribute("id", value)
                        .setTextCDATA(searched.get(value));
            }

            XMLLoader.save(cfg, xml.write());
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

    public String getValue(String key) {
        return searched.get(key);
    }

    public void setValue(String key, String value) {
        if (compare(value, searched.put(key, value == null ? "" : value))) {
            save();
        }
    }

    private boolean compare(String s1, String s2) {
    	if (s1 == null) {
    		return s2 == null;
    	} else {
    		return s1.equals(s2);
    	}
	}

	public static boolean isStorage(java.io.File cfg) {
        try {
            new LiveStorage(cfg);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

	public String getName() {
		return cfg.getName().replace(".run.xml", "");
	}

	public static LiveStorage create(String id, File cfg) throws Exception {
		XMLLoader.save(cfg, 
			XML.init("storage")
				.comment("Autogenerated by LiveStorage.create()")
				.root() // continues at next line with definition
		            .newPairTag("id", false)
		                .setComment("Loads a configuration tab")
		                .setText(id)
		            .close()
		            .newPairTag("values")
		                .setComment("Last stored values")
		            .close()
		        .close()
		    .write());
		return new LiveStorage(cfg);
	}
}
