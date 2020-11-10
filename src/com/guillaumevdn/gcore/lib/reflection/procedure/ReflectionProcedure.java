package com.guillaumevdn.gcore.lib.reflection.procedure;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedure<F, T> {

	private F function = null;

	// get
	public F get() {
		return function;
	}

	// set
	public T set(F function) {
		return setIf(true, function);
	}

	public T setIf(boolean condition, F function) {
		if (condition) {
			this.function = function;
		}
		return (T) this;
	}

	public T orIf(boolean condition, F function) {
		return this.function != null ? (T) this : setIf(condition, function);
	}

	public T orElse(F function) {
		if (this.function == null) {
			this.function = function;
		}
		return (T) this;
	}

	public F orElseGet(F function) {
		orElse(function);
		return get();
	}

}
