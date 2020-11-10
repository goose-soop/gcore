package com.guillaumevdn.gcore.lib.tuple;

/**
 * @author GuillaumeVDN
 */
public class IntegerPair extends Pair<Integer, Integer> {

	
	public IntegerPair() {
		super();
	}

	public IntegerPair(Integer a, Integer b) {
		super(a, b);
	}

	// static
	public static IntegerPair of(Integer a, Integer b) {
		return new IntegerPair(a, b);
	}

	public static IntegerPair empty() {
		return new IntegerPair();
	}

}
