package cz.deznekcz.reference;

import java.util.function.Consumer;

public class OutByteArray extends Out<Byte[]> {

	/**
	 * 
	 * @param implicitLength
	 * @param data
	 * @return Returns byte array reference with filled by data and zeroes from left
	 */
	public static OutByteArray initLeft(int implicitLength, byte... data) {
		OutByteArray a = new OutByteArray(new Byte[implicitLength]);
		for (int i = 0; i < data.length && i < implicitLength; i++) {
			a.get()[i] = data[i];
		}
		return a;
	}
	/**
	 * 
	 * @param implicitLength
	 * @param data
	 * @return Returns byte array reference with filled by data and zeroes from left
	 */
	public static OutByteArray initRight(int implicitLength, byte... data) {
		OutByteArray a = new OutByteArray(new Byte[implicitLength]);
		for (int i = data.length - 1, j = implicitLength - 1; i > -1 && j > -1; i++, j--) {
			a.get()[i] = data[j];
		}
		return a;
	}
	
	protected OutByteArray(Byte[] defaultValue) {
		super(defaultValue);
		// TODO Auto-generated constructor stub
	}
	
	public void forEach(Consumer<Byte> consumer) {
		for (Byte _byte : get()) {
			consumer.accept(_byte);
		}
	}

}
