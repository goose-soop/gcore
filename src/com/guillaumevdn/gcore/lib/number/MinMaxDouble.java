package com.guillaumevdn.gcore.lib.number;

/**
 * @author GuillaumeVDN
 */
public class MinMaxDouble {

	private double min, max;

	public MinMaxDouble(double a, double b) {
		this.min = Math.min(a, b);
		this.max = Math.max(a, b);
	}

	// ----- get
	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	// ----- static
	public static MinMaxDouble of(double a, double b) {
		return new MinMaxDouble(a, b);
	}

}
