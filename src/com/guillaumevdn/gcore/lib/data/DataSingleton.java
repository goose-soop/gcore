package com.guillaumevdn.gcore.lib.data;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.DataManager.BackEnd;
import com.guillaumevdn.gcore.lib.data.DataManager.Callback;
import com.guillaumevdn.gcore.lib.data.mysql.Query;

public abstract class DataSingleton {

	// data
	public abstract DataManager getDataManager();

	public void initAsync(final Callback callback) {
		getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
				} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					getDataManager().performMySQLUpdateQuery(getMySQLInitQuery());
				}
				if (callback != null) callback.callback();
				if (GCore.inst().getConfiguration() != null && GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Initialized " + DataSingleton.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't initialize " + DataSingleton.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName());
			}
		}});
	}

	public void pullAsync() {
		pullAsync(null);
	}

	public void pullAsync(final Callback callback) {
		getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					jsonPull();
				} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					mysqlPull();
				}
				if (callback != null) callback.callback();
				if (GCore.inst().getConfiguration() != null && GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Loaded " + DataSingleton.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't load " + DataSingleton.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName());
			}
		}});
	}

	public void push(final Object... params) {
		try {
			long start = System.currentTimeMillis();
			if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
				jsonPush();
			} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
				mysqlPush(params);
			}
			if (GCore.inst().getConfiguration() != null && GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
				getDataManager().getPlugin().debug("Saved " + DataSingleton.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
			}
		} catch (Throwable exception) {
			exception.printStackTrace();
			getDataManager().getPlugin().error("Couldn't save " + DataSingleton.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName());
		}
	}

	public void pushAsync(final Object... params) {
		getDataManager().runAsync(new BukkitRunnable() {
			@Override
			public void run() {
				push(params);
			}
		});
	}

	public void deleteAsync(final Object... params) {
		getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					if (params == null || params.length == 0) jsonDelete();
				} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					mysqlDelete(params);
				}
				if (GCore.inst().getConfiguration() != null && GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Deleted " + DataSingleton.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't delete data (unknown error)");
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

	// MySQL
	protected String getMySQLTable() {
		throw new UnsupportedOperationException();
	}

	protected Query getMySQLInitQuery() {
		throw new UnsupportedOperationException();
	}

	protected void mysqlPull() throws SQLException {
		throw new UnsupportedOperationException();
	}

	protected void mysqlPush(Object... params) {
		throw new UnsupportedOperationException();
	}

	protected void mysqlDelete(Object... params) {
		throw new UnsupportedOperationException();
	}

}
