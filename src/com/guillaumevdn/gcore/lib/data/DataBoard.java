package com.guillaumevdn.gcore.lib.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.DataManager.BackEnd;
import com.guillaumevdn.gcore.lib.data.DataManager.Callback;
import com.guillaumevdn.gcore.lib.data.mysql.Query;
import com.guillaumevdn.gcore.lib.util.Utils;

public abstract class DataBoard<T extends DataElement> {

	// data
	public abstract DataManager getDataManager();
	public abstract T getElement(Object param);

	public void initAsync(final Callback callback) {
		getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
				} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					getDataManager().performMySQLUpdateQuery(getMySQLInitQuery());
				}
				if (callback != null) callback.callback();
				if (GCore.inst().getConfiguration() == null ? false : GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Initialized " + DataBoard.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't initialize " + DataBoard.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName());
			}
		}});
	}

	public void pullAsync(final Callback callback) {
		getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					jsonPull();
					if (callback != null) callback.callback();
				} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					mysqlPull();
					if (callback != null) callback.callback();
				}
				if (GCore.inst().getConfiguration() == null ? false : GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Loaded " + DataBoard.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't load " + DataBoard.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName());
			}
		}});
	}

	public void pullAsync(final Collection<? extends T> elements, final Callback callback) {
		if (elements.isEmpty()) return;
		getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					for (T element : elements) {
						element.jsonPull();
					}
					if (callback != null) callback.callback();
				} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					Query query = new Query();
					for (T element : elements) {
						query.add(element.getMySQLPullQuery());
					}
					if (!query.isEmpty()) {
						Map<String, T> byId = new HashMap<String, T>();
						for (T element : elements) {
							byId.put(element.getDataId(), element);
						}
						ResultSet set = getDataManager().performMySQLGetQuery(query);
						while (set.next()) {
							String id = set.getString("id");
							if (byId.containsKey(id)) {
								byId.get(id).mysqlPull(set);;
							}
						}
						if (callback != null) callback.callback();
					}
				}
				if (GCore.inst().getConfiguration() == null ? false : GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Loaded " + elements.size() + " " + DataBoard.this.getClass().getSimpleName() + Utils.getPluralFor(" element", elements.size()) + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't load " + elements.size() + " " + DataBoard.this.getClass().getSimpleName() + Utils.getPluralFor(" element", elements.size()) + " for " + getDataManager().getClass().getSimpleName());
			}
		}});
	}

	public void pushAsync(final Collection<? extends T> elements) {
		push(true, elements);
	}

	public void pushAsync(final Collection<? extends T> elements, Callback callback) {
		push(true, elements, callback);
	}

	public void push(boolean async, final Collection<? extends T> elements) {
		push(async, elements, null);
	}

	public void push(boolean async, final Collection<? extends T> elements, final Callback callback) {
		if (elements.isEmpty()) return;
		BukkitRunnable runnable = new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					for (T element : elements) {
						element.jsonPush();
					}
					if (callback != null) callback.callback();
				} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					Query query = new Query();
					for (T element : elements) {
						query.add(element.getMySQLPushQuery());
					}
					if (!query.isEmpty()) {
						getDataManager().performMySQLUpdateQuery(query);
					}
					if (callback != null) callback.callback();
				}
				if (GCore.inst().getConfiguration() == null ? false : GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Saved " + elements.size() + " " + DataBoard.this.getClass().getSimpleName() + Utils.getPluralFor(" element", elements.size()) + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't load " + elements.size() + " " + DataBoard.this.getClass().getSimpleName() + Utils.getPluralFor(" element", elements.size()) + " for " + getDataManager().getClass().getSimpleName());
			}
		}};
		if (async) {
			getDataManager().runAsync(runnable);
		} else {
			getDataManager().run(runnable);
		}
	}

	public void deleteAsync() {
		getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					jsonDelete();
				} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					mysqlDelete();
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't delete data (unknown error)");
			}
		}});
	}

	public void deleteAsync(final Collection<? extends T> elements) {
		delete(true, elements);
	}

	public void delete(boolean async, final Collection<? extends T> elements) {
		if (elements.isEmpty()) return;
		BukkitRunnable runnable = new BukkitRunnable() { @Override public void run() {
			try {
				long start = System.currentTimeMillis();
				if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					for (T element : elements) {
						element.jsonDelete();
					}
				} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					Query query = new Query();
					for (T element : elements) {
						query.add(element.getMySQLDeleteQuery());
					}
					if (!query.isEmpty()) {
						getDataManager().performMySQLUpdateQuery(query);
					}
				}
				if (GCore.inst().getConfiguration() == null ? false : GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
					getDataManager().getPlugin().debug("Deleted " + elements.size() + " " + DataBoard.this.getClass().getSimpleName() + Utils.getPluralFor(" element", elements.size()) + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't delete data (unknown error)");
			}
		}};
		if (async) {
			getDataManager().runAsync(runnable);
		} else {
			getDataManager().run(runnable);
		}
	}

	// Json
	protected File getJsonFile(T element) {
		throw new UnsupportedOperationException();
	}

	protected void jsonPull() {
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

	protected void mysqlDelete() {
		throw new UnsupportedOperationException();
	}

}
