package com.guillaumevdn.gcore.lib.bukkit;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;

/**
 * @author GuillaumeVDN
 */
public enum BukkitThread {

	FORCE_SYNC,
	FORCE_ASYNC,
	SYNC,
	ASYNC;

	private final boolean isSync = !name().contains("ASYNC");

	public void operate(ThrowableRunnable callable) {
		operate(callable, null);
	}

	public void operate(ThrowableRunnable callable, Consumer<Throwable> onError) {
		try {
			if (!GCore.inst().isEnabled()) {
				callable.run();
			} else if (equals(BukkitThread.ASYNC)) {
				if (Bukkit.isPrimaryThread()) {
					buildRunnable(callable, onError).runTaskAsynchronously(GCore.inst());
				} else {
					callable.run();
				}
			} else if (equals(BukkitThread.SYNC)) {
				if (Bukkit.isPrimaryThread()) {
					callable.run();
				} else {
					buildRunnable(callable, onError).runTask(GCore.inst());
				}
			} else if (equals(BukkitThread.FORCE_ASYNC)) {
				buildRunnable(callable, onError).runTaskAsynchronously(GCore.inst());
			} else if (equals(BukkitThread.FORCE_SYNC)) {
				buildRunnable(callable, onError).runTask(GCore.inst());
			}
		} catch (Throwable error) {
			if (onError != null) {
				onError.accept(error);
			} else {
				error.printStackTrace();
			}
		}
	}

	public BukkitTask operateLater(ThrowableRunnable callable, Consumer<Throwable> onError, long ticks) {
		try {
			if (!GCore.inst().isEnabled()) {
				callable.run();
			} else if (isSync) {
				return buildRunnable(callable, onError).runTaskLater(GCore.inst(), ticks);
			} else {
				return buildRunnable(callable, onError).runTaskLaterAsynchronously(GCore.inst(), ticks);
			}
		} catch (Throwable error) {
			if (onError != null) {
				onError.accept(error);
			} else {
				error.printStackTrace();
			}
		}
		return null;
	}

	private BukkitRunnable buildRunnable(ThrowableRunnable callable, Consumer<Throwable> onError) {
		Error origin = new Error("An error occured while performing " + name() + " operation");
		return new BukkitRunnable() {
			@Override
			public void run() {
				try {
					callable.run();
				} catch (Throwable error) {
					origin.initCause(error);
					if (onError != null) {
						onError.accept(origin);
					} else {
						origin.printStackTrace();
					}
				}
			}
		};
	}

	public static BukkitThread regular(boolean async) {
		return async ? ASYNC : SYNC;
	}

}
