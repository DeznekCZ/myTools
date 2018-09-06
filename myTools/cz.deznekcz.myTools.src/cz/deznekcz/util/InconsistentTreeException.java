package cz.deznekcz.util;

public class InconsistentTreeException extends RuntimeException {

	public InconsistentTreeException() {
		// TODO Auto-generated constructor stub
	}

	public InconsistentTreeException(Class<?> expected, Class<?> found) {
		super("expected: "+expected.getSimpleName()+" found: "+found.getSimpleName());
	}

}
