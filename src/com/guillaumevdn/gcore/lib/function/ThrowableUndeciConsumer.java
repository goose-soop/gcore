package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableUndeciConsumer<A, B, C, D, E, F, G, H, I, J, K> {

	void accept(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k) throws Throwable;

}
