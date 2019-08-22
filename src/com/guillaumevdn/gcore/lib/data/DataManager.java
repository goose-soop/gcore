package com.guillaumevdn.gcore.lib.data;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.data.mysql.MySQL;
import com.guillaumevdn.gcore.lib.data.mysql.Query;
import com.guillaumevdn.gcore.lib.util.Utils;

public abstract class DataManager {

	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private GPlugin plugin;
	private BackEnd backEnd;
	private int syncDelay;
	private MySQL mySQL = null;
	private BukkitTask synchronizationTask = null;

	public DataManager(GPlugin plugin, BackEnd backEnd) {
		this(plugin, backEnd, plugin.getConfiguration().getInt("data.sync_delay", -1));
	}

	public DataManager(GPlugin plugin, BackEnd backEnd, int syncDelay) {
		this.plugin = plugin;
		this.backEnd = backEnd;
		this.syncDelay = syncDelay;
		// initialize MySQL
		if (backEnd.equals(BackEnd.MYSQL)) {
			String host = plugin.getConfiguration().getString("data.mysql.host", "mysql.myserver.com");
			String name = plugin.getConfiguration().getString("data.mysql.name", "mydatabase");
			String usr = plugin.getConfiguration().getString("data.mysql.user", "username");
			String pwd = plugin.getConfiguration().getString("data.mysql.pass", "pwd");
			String customArgs = plugin.getConfiguration().getString("data.mysql.args", "");
			String url = "jdbc:mysql://" + host + "/" + name + "?allowMultiQueries=true" + customArgs;
			this.mySQL = new MySQL(url, usr, pwd);
		}
	}

	// ------------------------------------------------------------
	// Abstract methods
	// ------------------------------------------------------------

	protected abstract void innerEnable();
	protected abstract void innerSynchronize();
	protected abstract void innerReset();
	protected abstract void innerDisable();

	// ------------------------------------------------------------
	// Getters
	// ------------------------------------------------------------

	public GPlugin getPlugin() {
		return plugin;
	}

	public BackEnd getBackEnd() {
		return backEnd;
	}

	public int getSyncDelay() {
		return syncDelay;
	}

	// ------------------------------------------------------------
	// Enable/disable/synchronize/reset
	// ------------------------------------------------------------

	public void enable() {
		// synchronization task
		long delay = Utils.getSecondsInTicks(syncDelay);
		if (delay > 0L) {
			this.synchronizationTask = new BukkitRunnable() {
				@Override
				public void run() {
					synchronize();
				}
			}.runTaskTimerAsynchronously(plugin, delay, delay);
		}
		// on register
		innerEnable();
		// log
		plugin.success("Registered data manager (using " + backEnd.toString() + ")");
	}

	public void synchronize() {
		innerSynchronize();
		if (GCore.inst().getConfiguration() == null ? false : GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
			plugin.debug("Synchronized data manager (using " + backEnd.toString() + ")");
		}
	}

	public void reset() {
		innerReset();
		plugin.success("Reset data manager (using " + backEnd.toString() + ")");
	}

	public void disable() {
		// stop synchronization task
		if (synchronizationTask != null) {
			synchronizationTask.cancel();
		}
		// on unregister
		innerDisable();
		// log
		plugin.success("Unregistered data manager");
	}

	// ------------------------------------------------------------
	// MySQL
	// ------------------------------------------------------------

	public void performMySQLUpdateQuery(Query query) {
		if (mySQL == null) {
			plugin.error("Couldn't perform MySQL query because it's not initialized (back end is " + backEnd.toString() + ")");
			plugin.error("Query : '" + query.getQuery() + "' with params '" + Utils.asNiceString(query.getStringParams(), true) + "'");
		} else {
			mySQL.performUpdateQuery(query);
		}
	}

	public ResultSet performMySQLGetQuery(Query query) {
		if (mySQL == null) {
			plugin.error("Couldn't perform MySQL get query because it's not initialized (back end is " + backEnd.toString() + ")");
			plugin.error("Query : '" + query.getQuery() + "' with params '" + Utils.asNiceString(query.getStringParams(), true) + "'");
			return null;
		} else {
			return mySQL.performGetQuery(query);
		}
	}

	// ------------------------------------------------------------
	// Misc
	// ------------------------------------------------------------

	public void runAsync(BukkitRunnable runnable) {
		if (!getPlugin().isEnabled()) {// trying to run async, but plugin is disabled -> just run it in the current thread
			runnable.run();
		} else {// we good
			if (Bukkit.isPrimaryThread()) {// we need to async
				runnable.runTaskAsynchronously(getPlugin());
			} else {// we're already async
				runnable.run();
			}
		}
	}

	public void run(BukkitRunnable runnable) {
		if (Bukkit.isPrimaryThread()) {// we're already sync
			runnable.run();
		} else {// we need to resync
			runnable.runTask(getPlugin());
		}
	}

	// data backend
	public static enum BackEnd {
		JSON,
		MYSQL;
	}

	// callback
	public static interface Callback {
		public void callback();
	}

	// mysql get callback
	public static interface MySQLGetCallback {
		public void callback(ResultSet set);
	}

}
