package com.guillaumevdn.gcore.lib.util;

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

	// object
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
            return other.value == null;
		} else return value.equals(other.value);
    }

	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
