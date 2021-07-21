package com.guillaumevdn.gcore.lib.wrapper;

/**
 * @author GuillaumeVDN
 */
public class WrapperDouble extends Wrapper<Double> {

	private WrapperDouble(Double value) {
		super(value);
	}

	// ----- methods
	public double alter(double delta) {
		set(get() + delta);
		return get();
	}

	// ----- static
	public static WrapperDouble of(double value) {
		return new WrapperDouble(value);
	}

}
