package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {

	void accept(A a, B b, C c);

}
