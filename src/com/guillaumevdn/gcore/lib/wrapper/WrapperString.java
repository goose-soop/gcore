package com.guillaumevdn.gcore.lib.wrapper;

/**
 * @author GuillaumeVDN
 */
public class WrapperString extends Wrapper<String> {

	private WrapperString(String value) {
		super(value);
	}

	public static WrapperString of(String value) {
		return new WrapperString(value);
	}

}
