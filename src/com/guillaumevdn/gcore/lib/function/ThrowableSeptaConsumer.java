package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableSeptaConsumer<A, B, C, D, E, F, G> {

	void accept(A a, B b, C c, D d, E e, F f, G g) throws Throwable;

}
