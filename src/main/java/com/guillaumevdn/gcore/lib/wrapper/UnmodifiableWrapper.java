package com.guillaumevdn.gcore.lib.wrapper;

/**
 * @author GuillaumeVDN
 */
public final class UnmodifiableWrapper<T> extends Wrapper<T> {

	private UnmodifiableWrapper(T value) {
		super(value);
	}

	private UnmodifiableWrapper(Wrapper<T> wrapper) {
		super(wrapper.get());
	}

	// ----- set
	@Override
	public T set(T value) {
		throw new UnsupportedOperationException();
	}

	// ----- static
	public static <T> Wrapper<T> of(T value) {
		return new UnmodifiableWrapper<T>(value);
	}

	public static <T> Wrapper<T> of(Wrapper<T> wrapper) {
		return new UnmodifiableWrapper<T>(wrapper);
	}

}
