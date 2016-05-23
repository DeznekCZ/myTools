package cz.deznekcz.util;

/**
 * TODO
 * @author Zdenek Novotny (DeznekCZ)
 *
 * @param <E> class of value instance
 */
public class Builder<E> {
	
	/**
	 * An interface of setting function
	 * @author Zdenek Novotny (DeznekCZ)
	 * @param <E> class of value instance
	 */
	@FunctionalInterface
	public static interface IBuild<E> {
		/**
		 * @param value current builded instance
		 */
		void set(E value);
	}

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
	 * @param methods instances or lambdas of {@link IBuild}
	 * @return returns instance of {@link Builder}
	 */
	@SafeVarargs
	public final Builder<E> set(IBuild<E> ...methods) {
		if (methods != null && methods instanceof IBuild[] && methods.length > 0)
			for (int i = 0; i < methods.length; i++)
				methods[i].set(value);
		return this;
	}
	
	/**
	 * Returns a final builded instance
	 * @return instance of {@link E}
	 */
	public E build() {
		return value;
	}
	
	/**
	 * Creates new instance of {@link Builder}
	 * @param initialInstance instance of {@link C}
	 * @return instance of {@link Builder}
	 */
	public static <C> Builder<C> create(C initialInstance) {
		return new Builder<C>(initialInstance);
	}
}
