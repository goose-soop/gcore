package com.guillaumevdn.gcore.lib.concurrency;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * @author GuillaumeVDN
 */
public final class AtomicRef<T> {

	private final AtomicReference<T> ref;

	private AtomicRef(T value) {
		ref = new AtomicReference<>(value);
	}

	public static <E> AtomicRef<E> of(E e) {
		return new AtomicRef(e);
	}

	// ----- read
	public T get() {
		return ref.get();
	}

	// ----- write
	public void set(T newValue) {
		set(t -> newValue);
	}

	public void set(UnaryOperator<T> newValueOperator) {
		for (;;) {
			T prev = ref.get();
			T next = newValueOperator.apply(prev);
			if (ref.compareAndSet(prev, next)) {
				break;
			}
		}
	}

}
