package com.guillaumevdn.gcore.lib.reflection.procedure;

import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureConsumer<T> extends ReflectionProcedure<ThrowableConsumer<T>, ReflectionProcedureConsumer<T>> {

	// ----- methods
	public void process(T param) {
		ThrowableConsumer<T> function = get();
		if (function != null) {
			try {
				function.accept(param);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		}
	}

}
