package com.guillaumevdn.gcore.lib.object;

import java.util.function.BiFunction;

/**
 * @author GuillaumeVDN
 */
public enum Operation {

	ADD((value, param) -> value + param, (value, param) -> value + param),
	TAKE((value, param) -> value - param, (value, param) -> value.contains(param) ? value.replace(param, "") : value),
	SET((value, param) -> param, (value, param) -> param)
	;

	private BiFunction<Double, Double, Double> applierDouble;
	private BiFunction<String, String, String> applierString;

	private Operation(BiFunction<Double, Double, Double> applierDouble, BiFunction<String, String, String> applierString) {
		this.applierDouble = applierDouble;
		this.applierString = applierString;
	}

	// ----- do
	public double apply(double value, double param) {
		return applierDouble.apply(value, param);
	}

	public String apply(String value, String param) {
		return applierString.apply(value, param);
	}

}
