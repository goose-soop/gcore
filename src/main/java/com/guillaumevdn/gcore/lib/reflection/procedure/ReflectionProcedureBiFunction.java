package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableBiFunction;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureBiFunction<A, B, R> extends ReflectionProcedure<ThrowableBiFunction<A, B, R>, ReflectionProcedureBiFunction<A, B, R>> {

	// ----- methods
	public R process(A a, B b) {
		return process(a, b, null);
	}

	public R process(A a, B b, R def) {
		ThrowableBiFunction<A, B, R> function = get();
		if (function != null) {
			try {
				return function.apply(a, b);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
		return def;
	}

}
