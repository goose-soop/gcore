package com.guillaumevdn.gcore.lib.concurrency;

/**
 * @author GuillaumeVDN
 */
public final class RWDoublePair extends RWPair<Double, Double> {

	public RWDoublePair() {
		super();
	}

	public RWDoublePair(Double a, Double b) {
		super(a, b);
	}

	public boolean aAtLeastB() {
		return getA() >= getB();
	}

	// ----- set
	public double alterA(double delta) {
		setA(getA() + delta);
		return getA();
	}

	public double alterA(double delta, boolean maxB) {
		double a = getA();
		double b = getB();
		setA(maxB && a + delta > b ? b : a + delta);
		return getA();
	}

	public double alterB(double delta) {
		setB(getB() + delta);
		return getB();
	}

	@Override
	public void equalizeToA() {
		super.equalizeToA();
	}

	@Override
	public void equalizeToB() {
		super.equalizeToB();
	}

	// ----- clone
	@Override
	public RWDoublePair clone() {
		return RWDoublePair.of(getA(), getB());
	}

	// ----- static
	public static RWDoublePair of(Double a, Double b) {
		return new RWDoublePair(a, b);
	}

	public static RWDoublePair empty() {
		return new RWDoublePair();
	}

}
