package cz.deznekcz.util;

import java.nio.charset.Charset;
import java.util.Random;

public class ShareableRandom extends Random {

	private static final long serialVersionUID = 1L;
	private String stringSeed;
	private long longSeed;
	private long restore;
	
	public ShareableRandom(String stringSeed, long longSeed, long restore) {
		this.longSeed = longSeed;
		if (restore == 0L)
			this.restore = longSeed;
		else
			this.restore = restore;
		this.stringSeed = (stringSeed != null ? stringSeed : "");
	}
	
	public static ShareableRandom from(String stringSeed) {
		return from(stringSeed, 0L);
	}
	
	public static ShareableRandom from(long longSeed) {
		return from(longSeed, 0L);
	}
	
	public static ShareableRandom from(String stringSeed, long restore) {
		return from(stringSeed, longHash(stringSeed), restore);
	}

	private static long longHash(String stringSeed) {
		long hash = 0L;
		if (stringSeed != null && stringSeed.length() > 0)
		{
			byte[] byteString = stringSeed.getBytes(Charset.forName("UTF8"));
			
			for (int i = 0, bytePos = 0; i < byteString.length; i++, bytePos++) {
				switch (bytePos) {
				case 3:
					hash = apply(hash, 0xFFL, 0xFFL & Byte.toUnsignedLong(byteString[i]), 3 * 8);
					break;
				case 2:
					hash = apply(hash, 0xFFL, 0xFFL & Byte.toUnsignedLong(byteString[i]), 2 * 8);
					break;
				case 1:
					hash = apply(hash, 0xFFL, 0xFFL & Byte.toUnsignedLong(byteString[i]),     8);
					break;
				default:
					hash = apply(hash, 0xFFL, 0xFFL & Byte.toUnsignedLong(byteString[i]),     0);
					bytePos = 0;
					break;
				}
			}
		}
		return hash;
	}
	private static long apply(long before, long flagNew, long valueNew, int shift) {
		return ( before & ~flagNew ) | ( ( before & ( flagNew << shift ) ) ^ ( ( valueNew ) << shift ) );
	}

	public static ShareableRandom from(long longSeed, long restore) {
		return from("",longSeed,restore);
	}
	
	public static ShareableRandom from(String stringSeed, long longSeed, long restore) {
		return new ShareableRandom(stringSeed, longSeed, restore);
	}

	@Override
	public synchronized int nextInt() {
		recalculate();
		return super.nextInt();
	}

	@Override
	public synchronized int nextInt(int bound) {
		recalculate();
		return super.nextInt(bound);
	}
	
	@Override
	public synchronized boolean nextBoolean() {
		recalculate();
		return super.nextBoolean();
	}
	
	@Override
	public synchronized double nextDouble() {
		recalculate();
		return super.nextDouble();
	}
	
	@Override
	public synchronized long nextLong() {
		recalculate();
		return super.nextLong();
	}
	
	private synchronized void recalculate() {
		restore = (Long.rotateRight(restore, 1) ^ restore) + 3;
		super.setSeed(restore);
	}
	
	@Override
	public synchronized void setSeed(long seed) {
		this.longSeed = seed;
		this.restore = seed;
		this.stringSeed = "";
		super.setSeed(restore);
	}
	
	public synchronized void setSeed(String seed) {
		this.longSeed = longHash(seed);
		this.restore = longSeed;
		this.stringSeed = (seed != null ? seed : "");
		super.setSeed(restore);
	}
	
	public String getStringSeed() {
		return stringSeed;
	}
	
	public long getLongSeed() {
		return longSeed;
	}
	
	public long getRestore() {
		return restore;
	}
}
