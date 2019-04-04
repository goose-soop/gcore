package be.guillaumevdn.gcore.lib.util;

public class MinMax
{
	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private int min;
	private int max;

	public MinMax(int i1, int i2)
	{
		if (i1 >= i2) {
			max = i1;
			min = i2;
		} else {
			min = i1;
			max = i2;
		}
	}

	// ------------------------------------------------------------
	// Getters
	// ------------------------------------------------------------

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}
}
