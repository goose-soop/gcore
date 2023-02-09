package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableBiConsumer;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureBiConsumer<A, B> extends ReflectionProcedure<ThrowableBiConsumer<A, B>, ReflectionProcedureBiConsumer<A, B>> {

	// ----- methods
	public void process(A param1, B param2) {
		ThrowableBiConsumer<A, B> function = get();
		if (function != null) {
			try {
				function.accept(param1, param2);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
	}

}
