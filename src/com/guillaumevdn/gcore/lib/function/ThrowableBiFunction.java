package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableBiFunction<A, B, R> {

	R apply(A a, B b) throws Throwable;

}
