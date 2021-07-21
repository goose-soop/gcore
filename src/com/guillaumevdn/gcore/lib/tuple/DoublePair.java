package com.guillaumevdn.gcore.lib.tuple;

import com.guillaumevdn.gcore.lib.number.NumberUtils;

/**
 * @author GuillaumeVDN
 */
public class DoublePair extends Pair<Double, Double> {

	private DoublePair() {
		super();
	}

	private DoublePair(Double a, Double b) {
		super(a, b);
	}

	public boolean aAtLeastB() {
		return getA() >= getB();
	}

	public double randomInRange() {
		return NumberUtils.random(getA(), getB());
	}

	// ----- set
	public double alterA(double delta) {
		setA(getA() + delta);
		return getA();
	}

	public double alterA(double delta, boolean maxB) {
		setA(maxB && getA() + delta > getB() ? getB() : getA() + delta);
		return getA();
	}

	public double alterB(double delta) {
		setB(getB() + delta);
		return getB();
	}

	// ----- clone
	@Override
	public DoublePair clone() {
		return DoublePair.of(getA(), getB());
	}

	// ----- static
	public static DoublePair of(Double a, Double b) {
		return new DoublePair(a, b);
	}

	public static DoublePair empty() {
		return new DoublePair();
	}

}
