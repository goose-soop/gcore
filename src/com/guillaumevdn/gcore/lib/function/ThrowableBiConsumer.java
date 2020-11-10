package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableBiConsumer<A, B> {

	void accept(A a, B b) throws Throwable;

}
