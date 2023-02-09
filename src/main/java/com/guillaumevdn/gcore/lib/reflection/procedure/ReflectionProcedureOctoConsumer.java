package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableOctoConsumer;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureOctoConsumer<A, B, C, D, E, F, G, H> extends ReflectionProcedure<ThrowableOctoConsumer<A, B, C, D, E, F, G, H>, ReflectionProcedureOctoConsumer<A, B, C, D, E, F, G, H>> {

	// ----- methods
	public void process(A a, B b, C c, D d, E e, F f, G g, H h) {
		ThrowableOctoConsumer<A, B, C, D, E, F, G, H> function = get();
		if (function != null) {
			try {
				function.accept(a, b, c, d, e, f, g, h);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
	}

}
