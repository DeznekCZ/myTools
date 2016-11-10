package cz.deznekcz.reference;

/**
 * @see #add(Number)
 * @see #mul(Number)
 */
public class OutShort extends OutNumber<Short> {
	public OutShort(short n) {
		super(n);
	}
	
	public synchronized OutShort add(Number n) {
		set((short) (get() + n.intValue()));
		return this;
	}
	
	public synchronized OutShort sub(Number n) {
		set((short) (get() - n.intValue()));
		return this;
	}
	
	public synchronized OutShort mul(Number n) {
		set((short) (get() * n.intValue()));
		return this;
	}
	
	public synchronized OutShort div(Number n) {
		set((short) (get() / n.intValue()));
		return this;
	}

	public synchronized boolean isEqual(Short value) {
		return value == get();
	}

	public synchronized boolean isLower(Short value) {
		return value > get();
	}

	public synchronized boolean isGreater(Short value) {
		return value < get();
	}

	public synchronized boolean isLowerOrEqual(Short value) {
		return value >= get();
	}

	public synchronized boolean isGreatherOrEqual(Short value) {
		return value <= get();
	}
}

