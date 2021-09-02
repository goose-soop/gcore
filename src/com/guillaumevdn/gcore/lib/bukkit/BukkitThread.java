package com.guillaumevdn.gcore.lib.bukkit;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.lib.GPlugin;
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

	public void operate(GPlugin plugin, ThrowableRunnable callable) {
		operate(plugin, callable, null);
	}

	public void operate(GPlugin plugin, ThrowableRunnable callable, Consumer<Throwable> onError) {
		try {
			if (!plugin.isEnabled() /* use enabled and not activated here, otherwise tasks on plugin startup will not run correctly */) {
				callable.run();
			} else if (equals(BukkitThread.ASYNC)) {
				if (Bukkit.isPrimaryThread()) {
					buildRunnable(callable, onError).runTaskAsynchronously(plugin);
				} else {
					callable.run();
				}
			} else if (equals(BukkitThread.SYNC)) {
				if (Bukkit.isPrimaryThread()) {
					callable.run();
				} else {
					buildRunnable(callable, onError).runTask(plugin);
				}
			} else if (equals(BukkitThread.FORCE_ASYNC)) {
				buildRunnable(callable, onError).runTaskAsynchronously(plugin);
			} else if (equals(BukkitThread.FORCE_SYNC)) {
				buildRunnable(callable, onError).runTask(plugin);
			}
		} catch (Throwable error) {
			if (onError != null) {
				onError.accept(error);
			} else {
				error.printStackTrace();
			}
		}
	}

	public BukkitTask operateLater(GPlugin plugin, ThrowableRunnable callable, long ticks) {
		return operateLater(plugin, callable, null, ticks);
	}

	public BukkitTask operateLater(GPlugin plugin, ThrowableRunnable callable, Consumer<Throwable> onError, long ticks) {
		try {
			if (!plugin.isEnabled() /* use enabled and not activated here, otherwise tasks on plugin startup will not run correctly */) {
				callable.run();
			} else if (ticks <= 0L) {
				operate(plugin, callable, onError);
			} else if (isSync) {
				return buildRunnable(callable, onError).runTaskLater(plugin, ticks);
			} else {
				return buildRunnable(callable, onError).runTaskLaterAsynchronously(plugin, ticks);
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

	public static BukkitThread current() {
		return regular(!Bukkit.isPrimaryThread());
	}

}
