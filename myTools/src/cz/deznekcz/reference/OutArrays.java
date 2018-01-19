package cz.deznekcz.reference;

public class OutArrays {

	public static Object[] mapToValues(@SuppressWarnings("rawtypes") Out...outs) {
		Object[] output = new Object[outs.length];
		for (int i = 0; i < outs.length; i++) {
			output[i] = outs[i].get();
		}
 		return output;
	}
	
	public static Object[] mapToReferences(Object...values) {
		@SuppressWarnings("rawtypes")
		Out[] output = new Out[values.length];
		for (int i = 0; i < values.length; i++) {
			output[i] = Out.init(values[i]);
		}
 		return output;
	}
}
