package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableConsumer<T> {

	void accept(T t) throws Throwable;

}
