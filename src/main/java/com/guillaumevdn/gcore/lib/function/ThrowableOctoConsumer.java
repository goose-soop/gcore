package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableOctoConsumer<A, B, C, D, E, F, G, H> {

	void accept(A a, B b, C c, D d, E e, F f, G g, H h) throws Throwable;

}
