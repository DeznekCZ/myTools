package cz.deznekcz.reference;

import java.util.Random;

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
		
		System.out.println(out); // after change
		
		/* Default value set from typed initialzer */
		
		Out<String> outString = Out.init("text");
		System.out.println(aStringVariable = outString.get()); // or out.value(); in older version
		
		/* Out can be typed to every known class */
		Out<TestedEnum> outEnum = Out.init(TestedEnum.VALUE_1);
		
		/* Comparing values of references */
		int answer = outEnum.compareTo(Out.init(TestedEnum.VALUE_2));
		System.out.println("Answer: " + answer);
		
		/* Global using */
		callByOnSetAction(Out.<Double>init().listened((obs,lastVal,newVal) -> globalDouble = newVal));
		/* globalDouble can be a property of instance or class */
		System.out.println(globalDouble);
		
		OutDouble doubleI = OutDouble.create();
		OutBoolean booleanI = doubleI.bindChecked(doubleI::isGreater, 6.);
		booleanI.addListener((o,l,n) -> {
			System.out.println(n ? "New number: " + doubleI.get() + " is greater then 6" : "New number: " + doubleI.get() + " is less or equal to 6");
		});
		
		Random r = new Random(7777L);
		for (int i = 0; i < 10; i++) {
			Double d = r.nextDouble() * 10;
			System.out.println("Changing to: " + d);
			doubleI.set(d);
		}
	}
	
	private static void callByOnSetAction(Out<Double> doubleOutArg) {
		doubleOutArg.set(5.3);
	}

	private static enum TestedEnum { VALUE_1, VALUE_2 }

	private static void callByInstanceOut(Out<String> out) {
		out.set("out variable"); // or out.lock(T)
	}
}
