package cz.deznekcz.javaplay.casting;

public class Caller {
	public void call(Object c) {
		System.out.println("Object: "+c.getClass().getSimpleName());
	}
	public void call(ToBeCasted c) {
		System.out.println("ToBeCasted: "+c.getClass().getSimpleName());
	}
	public void call(Casted c) {
		System.out.println("Casted: "+c.getClass().getSimpleName());
	}
}
