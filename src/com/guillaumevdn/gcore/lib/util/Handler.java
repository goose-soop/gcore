package com.guillaumevdn.gcore.lib.util;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;

public abstract class Handler
{
	// ------------------------------------------------------------
	// Run methods
	// ------------------------------------------------------------

	public void runSync() {
		if (Bukkit.isPrimaryThread()) {
			execute();
		} else {
			new BukkitRunnable() {
				@Override
				public void run() {
					execute();
				}
			}.runTask(GCore.inst());
		}
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
		if (Bukkit.isPrimaryThread()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					execute();
				}
			}.runTaskAsynchronously(GCore.inst());
		} else {
			execute();
		}
	}

	// ------------------------------------------------------------
	// Abstract methods
	// ------------------------------------------------------------

	public abstract void execute();
}
