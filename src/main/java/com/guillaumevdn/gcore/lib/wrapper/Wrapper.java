package com.guillaumevdn.gcore.lib.wrapper;

/**
 * @author GuillaumeVDN
 */
public class Wrapper<T> {

	private T value;

	protected Wrapper(T value) {
		this.value = value;
	}

	// ----- get
	public T get() {
		return value;
	}

	// ----- set
	public T set(T value) {
		this.value = value;
		return value;
	}

	// ----- object
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wrapper other = (Wrapper) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	// ----- static
	public static <T> Wrapper<T> of(T value) {
		return new Wrapper<>(value);
	}

}
