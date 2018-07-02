package cz.deznekcz.javafx.configurator.components;

import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

@DefaultProperty("content")
public class Cathegory extends TitledPane {
	private VBox parameters;
	
	public StringProperty helpPropterty() {
//		lookupAll(".title");
//		
//		try {
//			for(String method : Arrays.asList(getClass().getMethods()).stream().map((m)->m.toString()).sorted().collect(Collectors.toList())) {
//				System.out.println(method);
//			}
//		} catch (Exception e) {
//			
//		}
		
//        if (title != null) {
//            return title.getTooltip().textProperty();
//        } else {
        	System.err.println("Bad initialized Cathegory");
        	return new SimpleStringProperty("");
//        }
	}
	
	public void setHelp(String prompt) {
		helpPropterty().set(prompt);
	}
	
	public String getHelp() {
		return helpPropterty().get();
	}
	
	public void setParameters(Node...parameters) {
		this.parameters.getChildren().setAll(parameters);
	}
	
	public ObservableList<Node> getParameters() {
		return parameters.getChildren();
	}
	
	public Cathegory() {
		getStyleClass().add("cathegory");
		
//		setTooltip(new Tooltip());
		
		parameters = new VBox();
		parameters.getStyleClass().add("parameters");
		setContent(parameters);
	}
}
