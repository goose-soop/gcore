package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableQuadriConsumer;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureQuadriConsumer<A, B, C, D> extends ReflectionProcedure<ThrowableQuadriConsumer<A, B, C, D>, ReflectionProcedureQuadriConsumer<A, B, C, D>> {

	// ----- methods
	public void process(A a, B b, C c, D d) {
		ThrowableQuadriConsumer<A, B, C, D> function = get();
		if (function != null) {
			try {
				function.accept(a, b, c, d);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
	}

}
