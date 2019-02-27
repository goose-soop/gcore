package be.pyrrh4.pyrcore.lib.util;

public class Wrapper<T> {

	// base
	private T value;

	public Wrapper() {
		this(null);
	}

	public Wrapper(T value) {
		this.value = value;
	}

	// get
	public T getValue() {
		return value;
	}

	// set
	public void setValue(T value) {
		this.value = value;
	}

}
