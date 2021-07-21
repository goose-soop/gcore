package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableTriConsumer<A, B, C> {

	void accept(A a, B b, C c) throws Throwable;

}
