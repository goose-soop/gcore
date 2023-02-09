package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableTriConsumer;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureTriConsumer<A, B, C> extends ReflectionProcedure<ThrowableTriConsumer<A, B, C>, ReflectionProcedureTriConsumer<A, B, C>> {

	// ----- methods
	public void process(A param1, B param2, C param3) {
		ThrowableTriConsumer<A, B, C> function = get();
		if (function != null) {
			try {
				function.accept(param1, param2, param3);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
	}

}
