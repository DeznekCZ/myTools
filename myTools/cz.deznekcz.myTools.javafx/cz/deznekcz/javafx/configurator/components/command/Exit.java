package cz.deznekcz.javafx.configurator.components.command;

public class Exit {
	public static enum Type {
		SUCCESS, FAIL, CANCEL
	}
	
	private int id;
	private Type type;
	
	public Exit() {
		// TODO Auto-generated constructor stub
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
}
