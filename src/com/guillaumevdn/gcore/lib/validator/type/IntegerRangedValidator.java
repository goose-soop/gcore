package com.guillaumevdn.gcore.lib.validator.type;

/**
 * @author GuillaumeVDN
 */
public class IntegerRangedValidator extends IntegerValidator {
	
	private int min, max;

	public IntegerRangedValidator(int min, int max) {
		super("must be a number between " + min + " and " + max);
		this.min = min;
		this.max = max;
	}

	// get
	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	// methods
	@Override
	public boolean isValid(Integer value) {
		return value != null && value >= min && value <= max;
	}

}
