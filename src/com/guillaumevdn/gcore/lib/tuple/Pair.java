package com.guillaumevdn.gcore.lib.tuple;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class Pair<A, B> {

	private A a;
	private B b;

	protected Pair() {
		this(null, null);
	}

	protected Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	// ----- get
	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	// ----- set
	public void setA(A a) {
		this.a = a;
	}

	public void setB(B b) {
		this.b = b;
	}

	public void set(A a, B b) {
		setA(a);
		setB(b);
	}

	// ----- do
	public void consume(BiConsumer<A, B> consumer) {
		consumer.accept(a, b);
	}

	public <R> R process(BiFunction<A, B, R> function) {
		return function.apply(a, b);
	}

	// ----- object
	@Override
	public String toString() {
		return "(" + String.valueOf(a) + "," + String.valueOf(b) + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		Pair<A, B> other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && Objects.deepEquals(a, other.a) && Objects.deepEquals(b, other.b);
	}

	// ----- static
	public static <A, B> Pair<A, B> of(A a, B b) {
		return new Pair<>(a, b);
	}

	public static <A, B> Pair<A, B> empty() {
		return new Pair<>();
	}

}
