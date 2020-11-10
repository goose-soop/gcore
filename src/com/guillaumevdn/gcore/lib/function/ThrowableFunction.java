package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableFunction<T, R> {

	R apply(T t) throws Throwable;

}
