package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableSexaConsumer;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureSexaConsumer<A, B, C, D, E, F> extends ReflectionProcedure<ThrowableSexaConsumer<A, B, C, D, E, F>, ReflectionProcedureSexaConsumer<A, B, C, D, E, F>> {

	// ----- methods
	public void process(A a, B b, C c, D d, E e, F f) {
		ThrowableSexaConsumer<A, B, C, D, E, F> function = get();
		if (function != null) {
			try {
				function.accept(a, b, c, d, e, f);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
	}

}
