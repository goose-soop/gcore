package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureFunction<T, R> extends ReflectionProcedure<ThrowableFunction<T, R>, ReflectionProcedureFunction<T, R>> {

	// ----- methods
	public R process(T param) {
		return process(param, null);
	}

	public R process(T param, R def) {
		ThrowableFunction<T, R> function = get();
		if (function != null) {
			try {
				return function.apply(param);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		} else {
			throw new IllegalStateException("No function found for reflection procedure " + getClass());
		}
		return def;
	}

}
