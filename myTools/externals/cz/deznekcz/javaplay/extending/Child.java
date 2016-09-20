package cz.deznekcz.javaplay.extending;

public class Child extends Parent {

	public void callOutIn() {
		super.callOutIn();
		System.out.println("Hi out!");
	}

	@Override
	public void callAbstract() {
		System.out.println("Abstract is here!");
	}
	
	@Override
	public void callNonAbstract() {
		System.out.println("NA out!");
	}
}
