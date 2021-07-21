package com.guillaumevdn.gcore.lib.tuple;

/**
 * @author GuillaumeVDN
 */
public class LongPair extends Pair<Long, Long> {

	private LongPair() {
		super();
	}

	private LongPair(Long a, Long b) {
		super(a, b);
	}

	// ----- static
	public static LongPair of(Long a, Long b) {
		return new LongPair(a, b);
	}

	public static LongPair empty() {
		return new LongPair();
	}

}
