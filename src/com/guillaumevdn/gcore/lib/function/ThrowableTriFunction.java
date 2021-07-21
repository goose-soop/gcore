package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableTriFunction<A, B, C, R> {

	R apply(A a, B b, C c) throws Throwable;

}
