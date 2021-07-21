package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableQuintConsumer<A, B, C, D, E> {

	void accept(A a, B b, C c, D d, E e) throws Throwable;

}
