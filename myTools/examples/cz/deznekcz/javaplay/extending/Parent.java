package cz.deznekcz.javaplay.extending;

public abstract class Parent {

	public void callOutIn() {
		System.out.println("Hi in!");
	}
	
	public void callInAbstractOut() {
		callAbstract();
	}

	public abstract void callAbstract();
	
	public void callInOut() {
		callNonAbstract();
	}
	
	public void callNonAbstract() {
		System.out.println("NA in!");
	}

}
