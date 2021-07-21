package com.guillaumevdn.gcore.lib.reflection;

/**
 * @author GuillaumeVDN
 */
public class ReflectionParams {

	private Object[] params = null;

	// ----- get
	public Object[] get() {
		return params;
	}

	// ----- set
	public ReflectionParams setIf(boolean condition, Object... params) {
		if (this.params == null && condition) {
			this.params = params;
		}
		return this;
	}

	public ReflectionParams orElse(Object... params) {
		return setIf(true, params);
	}

	public Object[] orElseGet(Object... params) {
		return orElse(params).get();
	}

}
