package com.guillaumevdn.gcore.lib.tuple;

import com.guillaumevdn.gcore.lib.number.NumberUtils;

/**
 * @author GuillaumeVDN
 */
public class FloatPair extends Pair<Float, Float> {

	private FloatPair() {
		super();
	}

	private FloatPair(Float a, Float b) {
		super(a, b);
	}

	public boolean aAtLeastB() {
		return getA() >= getB();
	}

	public float randomInRange() {
		return NumberUtils.random(getA(), getB());
	}

	// ----- set
	public float alterA(float delta) {
		setA(getA() + delta);
		return getA();
	}

	public float alterA(float delta, boolean maxB) {
		setA(maxB && getA() + delta > getB() ? getB() : getA() + delta);
		return getA();
	}

	public float alterB(float delta) {
		setB(getB() + delta);
		return getB();
	}

	// ----- clone
	@Override
	public FloatPair clone() {
		return FloatPair.of(getA(), getB());
	}

	// ----- static
	public static FloatPair of(Float a, Float b) {
		return new FloatPair(a, b);
	}

	public static FloatPair empty() {
		return new FloatPair();
	}

}
