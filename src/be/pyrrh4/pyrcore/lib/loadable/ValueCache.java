package be.pyrrh4.pyrcore.lib.loadable;

public class ValueCache<T> {

	// base
	private T value;

	public ValueCache(T value) {
		this.value = value;
	}

	// get
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

}
