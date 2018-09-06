package cz.deznekcz.javafx.configurator.components.command;

public class Exit {
	public static enum Type {
		SUCCESS, FAIL, CANCEL
	}

	private int number;
	private Type type;

	public Exit() {
		// TODO Auto-generated constructor stub
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
