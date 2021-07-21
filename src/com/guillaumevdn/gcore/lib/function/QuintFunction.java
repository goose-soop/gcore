package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface QuintFunction<A, B, C, D, E, R> {

	R apply(A a, B b, C c, D d, E e);

}
