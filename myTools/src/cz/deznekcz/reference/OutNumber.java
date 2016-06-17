package cz.deznekcz.reference;


/**
 * @see #add(Number)
 * @see #mul(Number)
 */
public abstract class OutNumber<I extends Number> extends Out<I> {
	public OutNumber(I n) {
		super(n);
	}
	
	@Override
	public String toString() {
		return super.toString().replace("Reference", "Number");
	}
	
	public abstract OutNumber<I> add(Number n);
	
	public abstract OutNumber<I> mul(Number n);
	
	@SuppressWarnings("unchecked")
	@Override
	public void set() {
		set((I) get().getClass().cast(0));
	}
}

