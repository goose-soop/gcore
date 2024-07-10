package com.guillaumevdn.gcore.lib.validator.type;

/**
 * @author GuillaumeVDN
 */
public class IntegerRangedModuloValidator extends IntegerValidator {

	private int min, max, modulo;

	public IntegerRangedModuloValidator(int min, int max, int modulo) {
		super("must be a multiple of " + modulo + " between " + min + " and " + max);
		this.min = min;
		this.max = max;
		this.modulo = modulo;
	}

	// ----- get
	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getModulo() {
		return modulo;
	}

	// ----- methods
	@Override
	public boolean isValid(Integer value) {
		return value != null && value >= min && value <= max && value % modulo == 0;
	}

}
