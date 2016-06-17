package cz.deznekcz.reference;


/**
 * @see #add(Number)
 * @see #mul(Number)
 */
public class OutByte extends OutNumber<Byte> {
	public OutByte(byte n) {
		super(n);
	}
	
	public synchronized OutByte add(Number n) {
		set((byte) (get() + n.byteValue()));
		return this;
	}
	
	public synchronized OutByte mul(Number n) {
		set((byte) (get() * n.byteValue()));
		return this;
	}
}

