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
	
	public synchronized OutShort mul(Number n) {
		set((short) (get() * n.intValue()));
		return this;
	}
}

