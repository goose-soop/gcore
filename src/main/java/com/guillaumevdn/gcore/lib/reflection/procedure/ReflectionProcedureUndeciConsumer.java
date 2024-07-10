package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableUndeciConsumer;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureUndeciConsumer<A, B, C, D, E, F, G, H, I, J, K> extends
		ReflectionProcedure<ThrowableUndeciConsumer<A, B, C, D, E, F, G, H, I, J, K>, ReflectionProcedureUndeciConsumer<A, B, C, D, E, F, G, H, I, J, K>> {

	// ----- methods
	public void process(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k) {
		ThrowableUndeciConsumer<A, B, C, D, E, F, G, H, I, J, K> function = get();
		if (function != null) {
			try {
				function.accept(a, b, c, d, e, f, g, h, i, j, k);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
	}

}
