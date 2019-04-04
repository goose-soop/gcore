package com.guillaumevdn.gcore.lib.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.lib.data.DataManager.BackEnd;
import com.guillaumevdn.gcore.lib.data.DataManager.Callback;
import com.guillaumevdn.gcore.lib.data.mysql.Query;

public abstract class DataElement {

	// data
	protected abstract DataBoard<?> getBoard();
	protected abstract String getDataId();

	public void pullAsync() {
		pullAsync(null);
	}

	public void pullAsync(final Callback callback) {
		getBoard().getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				if (getBoard().getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					jsonPull();
					if (callback != null) callback.callback();
				} else if (getBoard().getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					try {
						Query query = getMySQLPullQuery();
						if (!query.isEmpty()) {
							ResultSet set = getBoard().getDataManager().performMySQLGetQuery(query);
							if (set.next()) {
								mysqlPull(set);
								if (callback != null) callback.callback();
							}
						}
					} catch (Throwable exception) {
						exception.printStackTrace();
						getBoard().getDataManager().getPlugin().error("Couldn't load data " + DataElement.this.getClass().getSimpleName() + " (id " + getDataId() + ") for " + getBoard().getDataManager().getClass().getSimpleName());
					}
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getBoard().getDataManager().getPlugin().error("Couldn't load data " + DataElement.this.getClass().getSimpleName() + " (id " + getDataId() + ") for " + getBoard().getDataManager().getClass().getSimpleName());
			}
		}});
	}

	public void pushAsync() {
		push(true);
	}

	public void push(boolean async) {
		BukkitRunnable runnable = new BukkitRunnable() { @Override public void run() {
			try {
				if (getBoard().getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					jsonPush();
				} else if (getBoard().getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					Query query = getMySQLPushQuery();
					if (!query.isEmpty()) {
						getBoard().getDataManager().performMySQLUpdateQuery(query);
					}
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getBoard().getDataManager().getPlugin().error("Couldn't save data " + DataElement.this.getClass().getSimpleName() + " for " + getBoard().getDataManager().getClass().getSimpleName());
			}
		}};
		if (async) {
			getBoard().getDataManager().runAsync(runnable);
		} else {
			getBoard().getDataManager().run(runnable);
		}
	}

	public void deleteAsync() {
		getBoard().getDataManager().runAsync(new BukkitRunnable() { @Override public void run() {
			try {
				if (getBoard().getDataManager().getBackEnd().equals(BackEnd.JSON)) {
					jsonDelete();
				} else if (getBoard().getDataManager().getBackEnd().equals(BackEnd.MYSQL)) {
					Query query = getMySQLDeleteQuery();
					if (!query.isEmpty()) {
						getBoard().getDataManager().performMySQLUpdateQuery(query);
					}
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				getBoard().getDataManager().getPlugin().error("Couldn't delete data " + DataElement.this.getClass().getSimpleName() + " for " + getBoard().getDataManager().getClass().getSimpleName());
			}
		}});
	}

	// Json
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
	/**
	 * Pull element data from a result set
	 * @param set the result set, positioned at the correct row
	 */
	protected void mysqlPull(ResultSet set) throws SQLException {
		throw new UnsupportedOperationException();
	}

	protected Query getMySQLPullQuery() {
		throw new UnsupportedOperationException();
	}

	protected Query getMySQLPushQuery() {
		throw new UnsupportedOperationException();
	}

	protected Query getMySQLDeleteQuery() {
		throw new UnsupportedOperationException();
	}

}
