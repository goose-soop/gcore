package com.guillaumevdn.gcore.lib.validator.type;

/**
 * @author GuillaumeVDN
 */
public class DoubleRangedValidator extends DoubleValidator {

	private double min, max;

	public DoubleRangedValidator(double min, double max) {
		super("must be a number between " + min + " and " + max);
		this.min = min;
		this.max = max;
	}

	// get
	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	// methods
	@Override
	public boolean isValid(Double value) {
		return value != null && value >= min && value <= max;
	}

}
