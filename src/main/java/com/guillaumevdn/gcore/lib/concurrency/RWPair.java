package com.guillaumevdn.gcore.lib.concurrency;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class RWPair<A, B> {

	private final RWLock lock = new RWLock();

	private A a;
	private B b;

	public RWPair() {
		this(null, null);
	}

	public RWPair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	// ----- get
	public A getA() {
		return lock.read(() -> a);
	}

	public B getB() {
		return lock.read(() -> b);
	}

	// ----- set
	public void setA(A a) {
		lock.write(() -> this.a = a);
	}

	public void setB(B b) {
		lock.write(() -> this.b = b);
	}

	public void set(A a, B b) {
		lock.write(() -> {
			this.a = a;
			this.b = b;
		});
	}

	public void equalizeToA() throws ClassCastException {
		lock.write(() -> this.b = (B) a);
	}

	public void equalizeToB() throws ClassCastException {
		lock.write(() -> this.a = (A) b);
	}

	// ----- do
	public void consume(BiConsumer<A, B> consumer) {
		lock.read(() -> consumer.accept(a, b));
	}

	public <R> R process(BiFunction<A, B, R> function) {
		return lock.read(() -> function.apply(a, b));
	}

	// ----- object
	@Override
	public String toString() {
		return lock.read(() -> "(" + String.valueOf(a) + "," + String.valueOf(b) + ")");
	}

	@Override
	public int hashCode() {
		return lock.read(() -> Objects.hash(a, b));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		RWPair<A, B> other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && lock.read(() -> Objects.deepEquals(a, other.a) && Objects.deepEquals(b, other.b));
	}

	// ----- static
	public static <A, B> RWPair<A, B> of(A a, B b) {
		return new RWPair<>(a, b);
	}

	public static <A, B> RWPair<A, B> empty() {
		return new RWPair<>();
	}

}
