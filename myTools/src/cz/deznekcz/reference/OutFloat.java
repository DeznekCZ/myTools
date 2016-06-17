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
	
	public synchronized OutFloat mul(Number n) {
		set(get() * n.floatValue());
		return this;
	}
}

