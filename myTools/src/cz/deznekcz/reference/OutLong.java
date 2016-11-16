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
	
	public synchronized OutLong sub(Number n) {
		set(get() - n.longValue());
		return this;
	}
	
	public synchronized OutLong mul(Number n) {
		set(get() * n.longValue());
		return this;
	}
	
	public synchronized OutLong div(Number n) {
		set(get() / n.longValue());
		return this;
	}

	public synchronized boolean isEqual(Long value) {
		return value == get();
	}

	public synchronized boolean isLess(Long value) {
		return value > get();
	}

	public synchronized boolean isGreater(Long value) {
		return value < get();
	}

	public synchronized boolean isLessOrEqual(Long value) {
		return value >= get();
	}

	public synchronized boolean isGreatherOrEqual(Long value) {
		return value <= get();
	}
}

