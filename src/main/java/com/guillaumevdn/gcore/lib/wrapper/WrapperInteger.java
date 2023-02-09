package com.guillaumevdn.gcore.lib.wrapper;

/**
 * @author GuillaumeVDN
 */
public class WrapperInteger extends Wrapper<Integer> {

	private WrapperInteger(Integer value) {
		super(value);
	}

	// ----- methods
	public int alter(int delta) {
		set(get() + delta);
		return get();
	}

	// ----- static
	public static WrapperInteger of(Integer value) {
		return new WrapperInteger(value);
	}

}
