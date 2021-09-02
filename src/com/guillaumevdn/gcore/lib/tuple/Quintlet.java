package com.guillaumevdn.gcore.lib.tuple;

import java.util.Objects;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class Quintlet<A, B, C, D, E> {

	private A a;
	private B b;
	private C c;
	private D d;
	private E e;

	private Quintlet() {
		this(null, null, null, null, null);
	}

	private Quintlet(A a, B b, C c, D d, E e) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
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

	public E getE() {
		return e;
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

	public void setE(E e) {
		this.e = e;
	}

	// ----- object
	@Override
	public String toString() {
		return "(" + String.valueOf(a) + "," + String.valueOf(b) + "," + String.valueOf(c) + "," + String.valueOf(d) + "," + String.valueOf(e) + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, b, c, d, e);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		Quintlet<A, B, C, D, E> other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && Objects.deepEquals(a, other.a) && Objects.deepEquals(b, other.b) && Objects.deepEquals(c, other.c) && Objects.deepEquals(d, other.d) && Objects.deepEquals(e, other.e);
	}

	// ----- static
	public static <A, B, C, D, E> Quintlet<A, B, C, D, E> of(A a, B b, C c, D d, E e) {
		return new Quintlet<>(a, b, c, d, e);
	}

	public static <A, B, C, D, E> Quintlet<A, B, C, D, E> empty() {
		return new Quintlet<>();
	}

}
