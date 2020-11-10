package com.guillaumevdn.gcore.lib.wrapper;

/**
 * @author GuillaumeVDN
 */
public class WrapperBoolean extends Wrapper<Boolean> {

	private WrapperBoolean(Boolean value) {
		super(value);
	}

	// static
	public static WrapperBoolean of(Boolean value) {
		return new WrapperBoolean(value);
	}

}
