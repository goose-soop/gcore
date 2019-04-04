package be.guillaumevdn.gcore.lib.util;

public class Pair<A, B> implements Cloneable {

	private A a;
	private B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	public void setA(A a) {
		this.a = a;
	}

	public void setB(B b) {
		this.b = b;
	}

	@Override
	public Pair<A, B> clone() {
		return new Pair<A, B>(a, b);
	}
	
	@Override
	public String toString() {
		return "Pair{a=" + (a == null ? null : a.toString()) + ",b=" + (b == null ? null : b.toString()) + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		try {
			Pair<A, B> other = (Pair<A, B>) obj;
			return Utils.equals(a, other.a) && Utils.equals(b, other.b);
		} catch (Throwable exception) {
			return false;
		}
	}

	public static <A, B> Pair<A, B> create(A a, B b) {
		return new Pair<A, B>(a, b);
	}

}
