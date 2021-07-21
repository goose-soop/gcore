package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableSexaConsumer<A, B, C, D, E, F> {

	void accept(A a, B b, C c, D d, E e, F f) throws Throwable;

}
