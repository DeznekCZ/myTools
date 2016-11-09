package cz.deznekcz.reference;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javafx.beans.value.ChangeListener;

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

	/**
	 * TODO
	 * @param value number value
	 * @return condition result
	 * @see PredictionAble
	 * @see #bindChecked(Predicate, Object)
	 */
	@PredictionAble
	public abstract boolean isEqual(I value);

	/**
	 * TODO
	 * @param value number value
	 * @return condition result
	 * @see PredictionAble
	 * @see #bindChecked(Predicate, Object)
	 */
	@PredictionAble
	public abstract boolean isLower(I value);

	/**
	 * TODO
	 * @param value number value
	 * @return condition result
	 * @see PredictionAble
	 * @see #bindChecked(Predicate, Object)
	 */
	@PredictionAble
	public abstract boolean isGreater(I value); 

	/**
	 * TODO
	 * @param value number value
	 * @return condition result
	 * @see PredictionAble
	 * @see #bindChecked(Predicate, Object)
	 */
	@PredictionAble
	public abstract boolean isLowerOrEqual(I value); 

	/**
	 * TODO
	 * @param value number value
	 * @return condition result
	 * @see PredictionAble
	 * @see #bindChecked(Predicate, Object)
	 */
	@PredictionAble
	public abstract boolean isGreatherOrEqual(I value);
	
	
}

