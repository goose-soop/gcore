package com.guillaumevdn.gcore.lib.tuple;

/**
 * @author GuillaumeVDN
 */
public class StringPair extends Pair<String, String> {

	private StringPair() {
		super();
	}

	private StringPair(String a, String b) {
		super(a, b);
	}

	// ----- static
	public static StringPair of(String a, String b) {
		return new StringPair(a, b);
	}

	public static StringPair empty() {
		return new StringPair();
	}

}
