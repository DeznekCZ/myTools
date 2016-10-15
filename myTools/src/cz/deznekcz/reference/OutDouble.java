package cz.deznekcz.reference;

/**
 * @see #add(Number)
 * @see #mul(Number)
 */
public class OutDouble extends OutNumber<Double> {
	private OutDouble(double n) {
		super(n);
	}
	
	public synchronized OutDouble add(Number n) {
		set(get() + n.doubleValue());
		return this;
	}
	
	public synchronized OutDouble mul(Number n) {
		set(get() * n.doubleValue());
		return this;
	}

	public static OutDouble create() {
		return new OutDouble(0);
	}

	public static OutDouble create(double i) {
		return new OutDouble(i);
	}
}

