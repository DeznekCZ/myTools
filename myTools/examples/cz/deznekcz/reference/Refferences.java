package cz.deznekcz.reference;

import cz.deznekcz.reference.Out;

public class Refferences {

	private static Double globalDouble;

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
		Out<TestedEnum> outEnum = Out.init(TestedEnum.VALUE_1);
		
		/* Comparing values of references */
		int answer = outEnum.compareTo(Out.init(TestedEnum.VALUE_2));
		System.out.println("Answer: " + answer);
		
		/* Global using */
		callByOnSetAction(Out.init(value -> globalDouble = value));
		/* globalDouble can be a property of instance or class */
		System.out.println(globalDouble);
	}
	
	private static void callByOnSetAction(Out<Double> doubleOutArg) {
		doubleOutArg.set(5.3);
	}

	private static enum TestedEnum { VALUE_1, VALUE_2 }

	private static void callByInstanceOut(Out<String> out) {
		out.set("out variable"); // or out.lock(T)
	}
}
