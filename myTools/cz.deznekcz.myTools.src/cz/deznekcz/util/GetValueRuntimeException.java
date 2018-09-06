package cz.deznekcz.util;

public class GetValueRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -8866656606935617977L;

	public GetValueRuntimeException(String key, Throwable cause) {
		super("Can not get value with cast for value: \"" + key + "\"\n" + cause.getLocalizedMessage(), cause);
	}
}
