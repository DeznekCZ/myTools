package cz.deznekcz.reference;

/**
 * @see #add(Number)
 * @see #mul(Number)
 */
public class OutLong extends OutNumber<Long> {
	public OutLong(long n) {
		super(n);
	}
	
	public synchronized OutLong add(Number n) {
		set(get() + n.longValue());
		return this;
	}
	
	public synchronized OutLong mul(Number n) {
		set(get() * n.longValue());
		return this;
	}
}

