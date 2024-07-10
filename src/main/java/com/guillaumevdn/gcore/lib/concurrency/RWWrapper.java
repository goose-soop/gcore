package com.guillaumevdn.gcore.lib.concurrency;

import java.util.function.Supplier;

/**
 * @author GuillaumeVDN
 */
public class RWWrapper<T> {

	private final RWLock lock = new RWLock();

	private T value;

	protected RWWrapper(T value) {
		this.value = value;
	}

	// ----- get
	public T get() {
		return lock.read(() -> value);
	}

	public T getOr(Supplier<T> def) {
		T value = get();
		return value != null ? value : def.get(); // run supplier outside of lock
	}

	// ----- set
	public T set(T value) {
		return lock.write(() -> {
			this.value = value;
			return value;
		});
	}

	// ----- object
	@Override
	public int hashCode() {
		return lock.read(() -> {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		});
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return lock.read(() -> {
			RWWrapper other = (RWWrapper) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		});
	}

	@Override
	public String toString() {
		return lock.read(() -> String.valueOf(value));
	}

	// ----- static
	public static <T> RWWrapper<T> of(T value) {
		return new RWWrapper<>(value);
	}

}
