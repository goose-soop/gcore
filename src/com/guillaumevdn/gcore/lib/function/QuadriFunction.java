package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface QuadriFunction<A, B, C, D, R> {

	R apply(A a, B b, C c, D d);

}
