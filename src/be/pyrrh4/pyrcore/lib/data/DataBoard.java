package be.pyrrh4.pyrcore.lib.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.pyrcore.lib.data.DataManager.BackEnd;
import be.pyrrh4.pyrcore.lib.data.DataManager.Callback;
import be.pyrrh4.pyrcore.lib.data.mysql.Query;
import be.pyrrh4.pyrcore.lib.util.Utils;

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
				getDataManager().getPlugin().debug("Initialized " + DataBoard.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
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
				getDataManager().getPlugin().debug("Loaded " + DataBoard.this.getClass().getSimpleName() + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
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
				getDataManager().getPlugin().debug("Loaded " + elements.size() + " " + DataBoard.this.getClass().getSimpleName() + Utils.getPluralFor(" element", elements.size()) + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
			} catch (Throwable exception) {
				exception.printStackTrace();
				getDataManager().getPlugin().error("Couldn't load " + elements.size() + " " + DataBoard.this.getClass().getSimpleName() + Utils.getPluralFor(" element", elements.size()) + " for " + getDataManager().getClass().getSimpleName());
			}
		}});
	}

	public void pushAsync(final Collection<? extends T> elements) {
		push(true, elements);
	}

	public void push(boolean async, final Collection<? extends T> elements) {
		if (elements.isEmpty()) return;
		BukkitRunnable runnable = new BukkitRunnable() { @Override public void run() {
			try {
				//long start = System.currentTimeMillis();
				if (getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					for (T element : elements) {
						element.jsonPush();
					}
				} else if (getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					Query query = new Query();
					for (T element : elements) {
						query.add(element.getMySQLPushQuery());
					}
					if (!query.isEmpty()) {
						getDataManager().performMySQLUpdateQuery(query);
					}
				}
				//getDataManager().getPlugin().debug("Saved " + elements.size() + " " + DataBoard.this.getClass().getSimpleName() + Utils.getPluralFor(" element", elements.size()) + " for " + getDataManager().getClass().getSimpleName() + " (took " + (System.currentTimeMillis() - start) + " ms)");
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
