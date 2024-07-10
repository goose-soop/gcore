package com.guillaumevdn.gcore.lib.tuple;

import java.util.Objects;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class Quadruple<A, B, C, D> {

	private A a;
	private B b;
	private C c;
	private D d;

	private Quadruple() {
		this(null, null, null, null);
	}

	private Quadruple(A a, B b, C c, D d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
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

	public D getD() {
		return d;
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

	public void setD(D d) {
		this.d = d;
	}

	// ----- object
	@Override
	public String toString() {
		return "(" + String.valueOf(a) + "," + String.valueOf(b) + "," + String.valueOf(c) + "," + String.valueOf(d) + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b, c, d);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		Quadruple<A, B, C, D> other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && Objects.deepEquals(a, other.a) && Objects.deepEquals(b, other.b) && Objects.deepEquals(c, other.c)
				&& Objects.deepEquals(d, other.d);
	}

	// ----- static
	public static <A, B, C, D> Quadruple<A, B, C, D> of(A a, B b, C c, D d) {
		return new Quadruple<>(a, b, c, d);
	}

	public static <A, B, C, D> Quadruple<A, B, C, D> empty() {
		return new Quadruple<>();
	}

}
