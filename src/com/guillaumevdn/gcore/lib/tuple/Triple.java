package com.guillaumevdn.gcore.lib.tuple;

import java.util.Objects;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class Triple<A, B, C> {

	private A a;
	private B b;
	private C c;

	private Triple() {
		this(null, null, null);
	}

	private Triple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	// ----- get
	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	public C getC() {
		return c;
	}

	// ----- set
	public void setA(A a) {
		this.a = a;
	}

	public void setB(B b) {
		this.b = b;
	}

	public void setC(C c) {
		this.c = c;
	}

	// ----- object
	@Override
	public String toString() {
		return "(" + String.valueOf(a) + "," + String.valueOf(b) + "," + String.valueOf(c) + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b, c);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		Triple<A, B, C> other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && Objects.deepEquals(a, other.a) && Objects.deepEquals(b, other.b) && Objects.deepEquals(c, other.c);
	}

	// ----- static
	public static <A, B, C> Triple<A, B, C> of(A a, B b, C c) {
		return new Triple<>(a, b, c);
	}

	public static <A, B, C> Triple<A, B, C> empty() {
		return new Triple<>();
	}

}
