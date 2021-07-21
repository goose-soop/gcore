package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableSexaFunction<A, B, C, D, E, F, R> {

	R apply(A a, B b, C c, D d, E e, F f) throws Throwable;

}
