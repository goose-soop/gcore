package com.guillaumevdn.gcore.lib.tuple;

/**
 * @author GuillaumeVDN
 */
public class StringTriple extends Triple<String, String, String> {

	private StringTriple() {
		super();
	}

	private StringTriple(String a, String b, String c) {
		super(a, b, c);
	}

	// ----- static
	public static StringTriple of(String a, String b, String c) {
		return new StringTriple(a, b, c);
	}

	public static StringTriple empty() {
		return new StringTriple();
	}

}
