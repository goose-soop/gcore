package be.guillaumevdn.gcore.lib.util;

import org.bukkit.scheduler.BukkitRunnable;

import be.guillaumevdn.gcore.GCore;

public abstract class Handler
{
	// ------------------------------------------------------------
	// Run methods
	// ------------------------------------------------------------

	public void runSync() {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute();
			}
		}.runTask(GCore.inst());
	}

	public void runSyncLater(long ticks) {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute();
			}
		}.runTaskLater(GCore.inst(), ticks);
	}

	public void runAsync() {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute();
			}
		}.runTaskAsynchronously(GCore.inst());
	}

	// ------------------------------------------------------------
	// Abstract methods
	// ------------------------------------------------------------

	public abstract void execute();
}
