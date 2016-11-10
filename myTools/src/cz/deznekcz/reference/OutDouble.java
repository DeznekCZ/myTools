package cz.deznekcz.reference;

/**
 * @see #add(Number)
 * @see #mul(Number)
 */
public class OutDouble extends OutNumber<Double> {
	private OutDouble(double n) {
		super(n);
	}
	
	public synchronized OutDouble add(Number n) {
		set(get() + n.doubleValue());
		return this;
	}

	@Override
	public OutDouble sub(Number n) {
		set(get() - n.doubleValue());
		return this;
	}
	
	public synchronized OutDouble mul(Number n) {
		set(get() * n.doubleValue());
		return this;
	}

	@Override
	public OutDouble div(Number n) {
		set(get() / n.doubleValue());
		return this;
	}

	public static OutDouble create() {
		return new OutDouble(0);
	}

	public static OutDouble create(double i) {
		return new OutDouble(i);
	}

	public synchronized boolean isEqual(Double value) {
		return value == get();
	}

	public synchronized boolean isLower(Double value) {
		return value > get();
	}

	public synchronized boolean isGreater(Double value) {
		return value < get();
	}

	public synchronized boolean isLowerOrEqual(Double value) {
		return value >= get();
	}

	public synchronized boolean isGreatherOrEqual(Double value) {
		return value <= get();
	}

	public static <N extends Number> OutDouble cast(Out<N> bind) {
		if (bind instanceof OutDouble) {
			return (OutDouble) bind;
		} else {
			return bind.bindTransform(OutDouble::new, (numb) -> numb.doubleValue());
		}
	}
}

