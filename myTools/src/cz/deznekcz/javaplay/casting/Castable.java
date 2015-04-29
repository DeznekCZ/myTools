package cz.deznekcz.javaplay.casting;

public class Castable {
	public static void main(String[] args) {
		Object a = new ToBeCasted();
		Object b = new Casted();
		
		Caller C = new Caller();
		System.out.println("Static cast:");
		
		C.call((ToBeCasted) a);
		C.call((Casted) b);
		
		System.out.println();
		
		System.out.println("Dynamic cast:");
		
		C.call(a.getClass().cast(a));
		C.call(b.getClass().cast(b));
	}
}
