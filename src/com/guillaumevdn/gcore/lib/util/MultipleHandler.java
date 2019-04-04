package com.guillaumevdn.gcore.lib.util;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.lib.GPlugin;

public abstract class MultipleHandler
{
	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private GPlugin plugin;
	protected int ticksTotal;
	protected int ticksElapsed = 0;
	protected boolean checkTicks;
	protected boolean syncStop;
	protected boolean cancelled = false;
	private BukkitTask task = null;

	public MultipleHandler(GPlugin plugin, int ticksTotal, boolean checkTicks, boolean syncStop)
	{
		this.plugin = plugin;
		this.ticksTotal = ticksTotal;
		this.checkTicks = checkTicks;
		this.syncStop = syncStop;
	}

	// ------------------------------------------------------------
	// Abstract methods
	// ------------------------------------------------------------

	/** @return true if the task musts stop */
	protected abstract boolean onStart();
	/** @return true if the task musts stop */
	protected abstract boolean update();
	protected abstract void onStop();

	// ------------------------------------------------------------
	// Begin the task
	// ------------------------------------------------------------

	public void begin()
	{
		if (onStart()) {
			stop();
		}
		else
		{
			task = new BukkitRunnable()
			{
				@Override
				public void run()
				{
					ticksElapsed++;

					if (update() || (checkTicks && ticksElapsed >= ticksTotal))
					{
						stop();
						cancel();
					}
				}
			}.runTaskTimerAsynchronously(plugin, 1L, 1L);
		}
	}

	// ------------------------------------------------------------
	// Stop the task
	// ------------------------------------------------------------

	public void stop()
	{
		if (!cancelled) {
			cancel();
		}

		Handler handler = new Handler() {
			public void execute() {
				onStop();
			}
		};

		if (syncStop) {
			handler.runSync();
		} else {
			handler.runAsync();
		}
	}

	// ------------------------------------------------------------
	// Cancel the task
	// ------------------------------------------------------------

	public void cancel() {
		if (task != null) {
			task.cancel();
		}
		cancelled = true;
	}
}
