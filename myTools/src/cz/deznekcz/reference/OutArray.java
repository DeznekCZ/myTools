package cz.deznekcz.reference;

import java.util.function.Function;

public class OutArray<C> extends Out<C[]> {

	private OutArray(C[] defaultValue) {
		super(defaultValue);
	}

	@SafeVarargs
	public static <C> OutArray<C> from(C... split) {
		return new OutArray<>(split);
	}

}
