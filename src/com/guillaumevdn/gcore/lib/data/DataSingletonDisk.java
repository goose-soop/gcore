package com.guillaumevdn.gcore.lib.data;

import java.io.File;

import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.DataManager.Callback;

public abstract class DataSingletonDisk {

	// data
	public abstract DataManager getDataManager();

	public void pullAsync(final Callback callback) {
		getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				jsonPull();
				if (callback != null) callback.callback();
				if (GCore.inst().getConfiguration() == null ? false : GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Loaded " + DataSingletonDisk.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't load " + DataSingletonDisk.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName());
			}
		}});
	}

	public void pushAsync() {
		getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				jsonPush();
				if (GCore.inst().getConfiguration() == null ? false : GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Saved " + DataSingletonDisk.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't save " + DataSingletonDisk.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName());
			}
		}});
	}

	public void deleteAsync() {
		getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				jsonDelete();
				if (GCore.inst().getConfiguration() == null ? false : GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Deleted " + DataSingletonDisk.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't delete " + DataSingletonDisk.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName());
			}
		}});
	}

	// Json
	protected File getJsonFile() {
		throw new UnsupportedOperationException();
	}

	protected void jsonPull() {
		throw new UnsupportedOperationException();
	}

	protected void jsonPush() {
		throw new UnsupportedOperationException();
	}

	protected void jsonDelete() {
		throw new UnsupportedOperationException();
	}

}
