package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface QuintConsumer<A, B, C, D, E> {

	void accept(A a, B b, C c, D d, E e);

}
