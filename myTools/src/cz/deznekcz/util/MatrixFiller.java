package cz.deznekcz.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class MatrixFiller {
	
	@SafeVarargs
	public static <V> ArrayList<V> fill(V...values) {
		return fill(values != null ? Arrays.asList(values) : new ArrayList<V>(0));
	}
	
	public static <V> ArrayList<V> fill(Collection<V> values) {
		if (values != null) {
			return new ArrayList<>(values);
		} else {
			return new ArrayList<>();
		}
	}
}
