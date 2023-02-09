package com.guillaumevdn.gcore.lib.tuple;

/**
 * @author GuillaumeVDN
 */
public class LongIntegerPair extends Pair<Long, Integer> {

	private LongIntegerPair() {
		super();
	}

	private LongIntegerPair(Long a, Integer b) {
		super(a, b);
	}

	// ----- static
	public static LongIntegerPair of(Long a, Integer b) {
		return new LongIntegerPair(a, b);
	}

	public static LongIntegerPair empty() {
		return new LongIntegerPair();
	}

}
