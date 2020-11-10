package com.guillaumevdn.gcore.lib.number;

/**
 * @author GuillaumeVDN
 */
public class MinMaxInteger {

	private int min, max;

	public MinMaxInteger(int a, int b) {
		this.min = Math.min(a, b);
		this.max = Math.max(a, b);
	}

	// get
	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	// static
	public static MinMaxInteger of(int a, int b) {
		return new MinMaxInteger(a, b);
	}

}
