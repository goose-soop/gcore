package be.pyrrh4.pyrcore.lib.util;

import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.pyrcore.PyrCore;

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
		}.runTask(PyrCore.inst());
	}

	public void runSyncLater(long ticks) {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute();
			}
		}.runTaskLater(PyrCore.inst(), ticks);
	}

	public void runAsync() {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute();
			}
		}.runTaskAsynchronously(PyrCore.inst());
	}

	// ------------------------------------------------------------
	// Abstract methods
	// ------------------------------------------------------------

	public abstract void execute();
}
