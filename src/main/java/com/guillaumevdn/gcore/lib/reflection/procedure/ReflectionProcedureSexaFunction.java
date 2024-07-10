package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableSexaFunction;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureSexaFunction<A, B, C, D, E, F, R>
		extends ReflectionProcedure<ThrowableSexaFunction<A, B, C, D, E, F, R>, ReflectionProcedureSexaFunction<A, B, C, D, E, F, R>> {

	// ----- methods
	public R process(A a, B b, C c, D d, E e, F f) {
		return process(a, b, c, d, e, f, null);
	}

	public R process(A a, B b, C c, D d, E e, F f, R def) {
		ThrowableSexaFunction<A, B, C, D, E, F, R> function = get();
		if (function != null) {
			try {
				return function.apply(a, b, c, d, e, f);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
		return def;
	}

}
