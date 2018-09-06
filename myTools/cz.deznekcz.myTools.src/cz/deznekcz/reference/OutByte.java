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

	public synchronized boolean isEqual(Byte value) {
		return value == get();
	}

	public synchronized boolean isLess(Byte value) {
		return value > get();
	}

	public synchronized boolean isGreater(Byte value) {
		return value < get();
	}

	public synchronized boolean isLessOrEqual(Byte value) {
		return value >= get();
	}

	public synchronized boolean isGreatherOrEqual(Byte value) {
		return value <= get();
	}

	@Override
	public OutByte sub(Number n) {
		set((byte) (get() - n.byteValue()));
		return this;
	}

	@Override
	public OutByte div(Number n) {
		set((byte) (get() / n.byteValue()));
		return this;
	}
}

