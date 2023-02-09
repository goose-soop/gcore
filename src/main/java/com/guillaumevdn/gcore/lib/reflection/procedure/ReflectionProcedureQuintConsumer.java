package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableQuintConsumer;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureQuintConsumer<A, B, C, D, E> extends ReflectionProcedure<ThrowableQuintConsumer<A, B, C, D, E>, ReflectionProcedureQuintConsumer<A, B, C, D, E>> {

	// ----- methods
	public void process(A a, B b, C c, D d, E e) {
		ThrowableQuintConsumer<A, B, C, D, E> function = get();
		if (function != null) {
			try {
				function.accept(a, b, c, d, e);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
	}

}
