package cz.deznekcz.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import cz.deznekcz.reference.OutBoolean;

/**
 * TODO
 * @author Zdenek Novotny (DeznekCZ)
 *
 * @param <E> class of value instance
 */
public final class Builder<E> {

	/** Current instance of builded class */
	private E value;
	
	/**
	 * Hidden constructor of class {@link Builder}
	 * @param value initial instance of builded class
	 */
	private Builder(E value) {
		this.value = value;
	}
	
	/**
	 * Method sets a parameters of instance by methods
	 * @param methods lambdas, delegates, array or list instance of {@link Consumer} 
	 * @return returns <b>this</b> instance of {@link Builder}
	 */
	@SafeVarargs
	public final Builder<E> set(Consumer<E> ...methods) {
		if (methods != null && methods instanceof Consumer[] && methods.length > 0)
			for (int i = 0; i < methods.length; i++)
				methods[i].accept(value);
		return this;
	}

	/**
	 * Method sets a parameters of instance by methods if enabling function is passed
	 * @param enablingFunction lambda, delegate or instance of {@link Predicate}
	 * @param methods lambdas, delegates, array or list instance of {@link Consumer} 
	 * @return returns <b>this</b> instance of {@link Builder}
	 */
	@SafeVarargs
	public final Builder<E> setIf(
			Predicate<E> enablingFunction, 
			Consumer<E> ...methods
	) {
		if (enablingFunction.test(value))
			set(methods);
		return this;
	}

	/**
	 * Method sets a parameters of instance by methods if enabling function is passed
	 * @param enablingFunction lambda, delegate or instance of {@link Predicate}
	 * @param methods lambdas, delegates, array or list instance of {@link Consumer} 
	 * @param result reference to calculated value from enablingFunction
	 * @return returns <b>this</b> instance of {@link Builder}
	 * @throws NullPointerException OutBoolean reference is null
	 */
	@SafeVarargs
	public final Builder<E> setIf(
			Predicate<E> enablingFunction, 
			OutBoolean result,
			Consumer<E> ...methods
	) {
		Objects.requireNonNull(result, "OutBoolean reference is null");
		result.set(enablingFunction.test(value));
		if (result.get())
			set(methods);
		return this;
	}

	/**
	 * Method sets a parameters of instance by methods if enabling function is passed
	 * @param enablingFunction lambda, delegate or instance of {@link Predicate}
	 * @param methods lambdas, delegates, array or list instance of {@link Consumer} 
	 * @return returns <b>this</b> instance of {@link Builder}
	 * @throws NullPointerException OutBoolean reference is null
	 */
	@SafeVarargs
	public final Builder<E> setWhile(
			Predicate<E> enablingFunction, 
			Consumer<E> ...methods
	) {
		while(enablingFunction.test(value))
			set(methods);
		return this;
	}
	
	/**
	 * Returns a final builded instance
	 * @return instance of {@link E}
	 */
	public final E build() {
		return value;
	}
	
	/**
	 * Creates new instance of {@link Builder}
	 * @param initialInstance instance of {@link C}
	 * @return instance of {@link Builder}
	 */
	public static final <C> Builder<C> create(C initialInstance) {
		return new Builder<C>(initialInstance);
	}
}
