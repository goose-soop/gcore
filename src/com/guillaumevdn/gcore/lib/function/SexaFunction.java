package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface SexaFunction<A, B, C, D, E, F, R> {

	R apply(A a, B b, C c, D d, E e, F f);

}
