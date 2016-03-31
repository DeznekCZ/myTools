package cz.deznekcz.references;

import cz.deznekcz.reference.Out;

public class Refferences {
	
	public static void main(String[] args) {
		
		String aStringVariable;
		
		/* ***************************** *
		 * Standard out class parameter
		 * ***************************** */
		
		Out<String> out = Out.init("default"); // default value is set by initializer
		aStringVariable = out.get(); // or out.value(); in older version
		System.out.println(aStringVariable); // before change
		
		callByInstanceOut(out);
		
		aStringVariable = out.get(); // or out.value(); in older version
		System.out.println(aStringVariable); // after change
		
		/* Default value set from typed initialzer */
		
		Out<String> outString = Out.init("text");
		System.out.println(aStringVariable = outString.get()); // or out.value(); in older version
		
		/* Out can be typed to every known class */
		Out<TestedEnum> outEnum = Out.init(TestedEnum.VALUE);
	}
	
	private static enum TestedEnum {VALUE}

	private static void callByInstanceOut(Out<String> out) {
		out.set("out variable"); // or out.lock(T)
	}
}
