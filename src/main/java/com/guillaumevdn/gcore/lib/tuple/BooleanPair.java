package com.guillaumevdn.gcore.lib.tuple;

/**
 * @author GuillaumeVDN
 */
public class BooleanPair extends Pair<Boolean, Boolean> {

	protected BooleanPair() {
		super();
	}

	protected BooleanPair(Boolean a, Boolean b) {
		super(a, b);
	}

	// ----- static
	public static BooleanPair of(Boolean a, Boolean b) {
		return new BooleanPair(a, b);
	}

	public static BooleanPair empty() {
		return new BooleanPair();
	}

}
