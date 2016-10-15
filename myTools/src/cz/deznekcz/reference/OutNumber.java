package cz.deznekcz.reference;

import java.util.function.Function;

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
	
	public OutBoolean bindCompared(Function<I,Boolean> transform, I value) {
		OutBoolean transformed = OutBoolean.FALSE();
		this.addListener((o, l, n) -> {
			transformed.set(transform.apply(value));
		});
		return transformed;
	}
}

