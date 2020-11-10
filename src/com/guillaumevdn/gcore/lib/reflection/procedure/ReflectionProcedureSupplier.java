package com.guillaumevdn.gcore.lib.reflection.procedure;

import java.util.function.Supplier;

/**
 * @author GuillaumeVDN
 */
public class ReflectionProcedureSupplier<T> extends ReflectionProcedure<Supplier<T>, ReflectionProcedureSupplier<T>> {

	// methods
	public T process() {
		Supplier<T> function = get();
		if (function != null) {
			return function.get();
		}
		throw new IllegalStateException("no function found");
	}

}
