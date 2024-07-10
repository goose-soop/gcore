package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableSeptaConsumer;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureSeptaConsumer<A, B, C, D, E, F, G>
		extends ReflectionProcedure<ThrowableSeptaConsumer<A, B, C, D, E, F, G>, ReflectionProcedureSeptaConsumer<A, B, C, D, E, F, G>> {

	// ----- methods
	public void process(A a, B b, C c, D d, E e, F f, G g) {
		ThrowableSeptaConsumer<A, B, C, D, E, F, G> function = get();
		if (function != null) {
			try {
				function.accept(a, b, c, d, e, f, g);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
	}

}
