package cz.deznekcz.reference;

/**
 * @see #add(Number)
 * @see #mul(Number)
 */
public class OutFloat extends OutNumber<Float> {
	public OutFloat(float n) {
		super(n);
	}
	
	public synchronized OutFloat add(Number n) {
		set(get() + n.floatValue());
		return this;
	}

	@Override
	public OutFloat sub(Number n) {
		set(get() - n.floatValue());
		return this;
	}
	
	public synchronized OutFloat mul(Number n) {
		set(get() * n.floatValue());
		return this;
	}

	@Override
	public OutFloat div(Number n) {
		set(get() / n.floatValue());
		return this;
	}

	public synchronized boolean isEqual(Float value) {
		return value == get();
	}

	public synchronized boolean isLower(Float value) {
		return value > get();
	}

	public synchronized boolean isGreater(Float value) {
		return value < get();
	}

	public synchronized boolean isLowerOrEqual(Float value) {
		return value >= get();
	}

	public synchronized boolean isGreatherOrEqual(Float value) {
		return value <= get();
	}
}

