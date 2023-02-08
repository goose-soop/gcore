package com.guillaumevdn.gcore.lib.tuple;

/**
 * @author GuillaumeVDN
 */
public class LongTriple extends Triple<Long, Long, Long> {

	private LongTriple() {
		super();
	}

	private LongTriple(Long a, Long b, Long c) {
		super(a, b, c);
	}

	// ----- static
	public static LongTriple of(Long a, Long b, Long c) {
		return new LongTriple(a, b, c);
	}

	public static LongTriple empty() {
		return new LongTriple();
	}

}
