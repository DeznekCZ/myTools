package cz.deznekcz.util;

public class Elem<T> {

	private T value;
	private boolean marked;

	public Elem(boolean marked, T value) {
		this.marked = marked;
		this.value = value;
	}

	public T getValue() {
		return value;
	}
	
	public boolean isMarked() {
		return marked;
	}
}
