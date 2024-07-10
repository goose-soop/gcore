package com.guillaumevdn.gcore.lib;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;

/**
 * @author GuillaumeVDN
 */
public class Task {

	private GPlugin plugin;
	private String id;
	private boolean async;
	private int ticksPeriod;
	private ThrowableRunnable runner;
	private BukkitTask task = null;

	public Task(GPlugin plugin, String id, boolean async, int ticksPeriod, ThrowableRunnable runner) {
		this.plugin = plugin;
		this.id = id;
		this.async = async;
		this.ticksPeriod = ticksPeriod;
		this.runner = runner;
	}

	// ----- get
	public String getId() {
		return id;
	}

	public boolean isAsync() {
		return async;
	}

	public int getTicksPeriod() {
		return ticksPeriod;
	}

	public ThrowableRunnable getRunnable() {
		return runner;
	}

	public BukkitTask getTask() {
		return task;
	}

	public boolean isActive() {
		return task != null;
	}

	// ----- methods
	public void start() {
		stop();
		BukkitRunnable runnable = new BukkitRunnable() {

			@Override
			public void run() {
				if (plugin.isReloading()) {
					return;
				}
				try {
					runner.run();
				} catch (Throwable exception) {
					new Error("an error occured while executing task " + id + " for " + plugin.getName() + " v" + plugin.getDescription().getVersion(),
							exception).printStackTrace();
				}
			}

		};
		long period = (long) ticksPeriod;
		task = async ? runnable.runTaskTimerAsynchronously(plugin, period, period) : runnable.runTaskTimer(plugin, period, period);
	}

	public void stop() {
		if (task != null) {
			task.cancel();
		}
	}

}
