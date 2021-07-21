package com.guillaumevdn.gcore.lib.function;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ThrowableRunnable {

	void run() throws Throwable;

	public static ThrowableRunnable fromSafe(Runnable runnable) {
		return () -> runnable.run();
	}

}
