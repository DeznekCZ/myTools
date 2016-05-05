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
	interface IBuild<E> {
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
	public Builder<E> set(@SuppressWarnings("unchecked") IBuild<E> ...methods) {
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
	 * @param initialInstance instance of {@link E}
	 * @return instance of {@link Builder}
	 */
	public Builder<E> create(E initialInstance) {
		return new Builder<E>(initialInstance);
	}
}
