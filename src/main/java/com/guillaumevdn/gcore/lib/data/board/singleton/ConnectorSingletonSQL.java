package com.guillaumevdn.gcore.lib.data.board.singleton;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.data.board.BoardConnector;
import com.guillaumevdn.gcore.lib.data.sql.Query;
import com.guillaumevdn.gcore.lib.data.sql.SQLHandler;
import com.guillaumevdn.gcore.lib.data.sql.SQLiteHandler;

public abstract class ConnectorSingletonSQL<W> implements BoardConnector {

	private SingletonBoard board;
	private Class<W> jsonDataWrapperClass;
	private SQLHandler handler;

	public ConnectorSingletonSQL(SingletonBoard board,  Class<W> jsonDataWrapperClass, SQLHandler handler) {
		this.board = board;
		this.jsonDataWrapperClass = jsonDataWrapperClass;
		this.handler = handler;
	}

	public String tableName() {
		return board.getId();
	}

	public int dataVersion() {
		return 1;
	}

	@Override
	public void shutdown() {
		handler.shutdown();
	}

	@Override
	public final void forcePushAllCached() throws Throwable {
		remotePushCached();
	}

	@Override
	public void remoteInit() throws Throwable {
		if (handler instanceof SQLiteHandler) {
			handler.performUpdateQuery(board.getLogger(),
					"CREATE TABLE IF NOT EXISTS " + tableName() + "("
							+ "version SMALLINT NOT NULL PRIMARY KEY,"
							+ "data LONGTEXT NOT NULL"
							+ ");"
					);
		} else {
			handler.performUpdateQuery(board.getLogger(),
					"CREATE TABLE IF NOT EXISTS " + tableName() + "("
							+ "version SMALLINT NOT NULL,"
							+ "data LONGTEXT NOT NULL,"
							+ "PRIMARY KEY(data_key)"
							+ ") ENGINE=InnoDB DEFAULT CHARSET = 'utf8';"
					);
		}
	}

	@Override
	public final void remotePullAll() throws Throwable {
		Query query = new Query("SELECT * FROM " + tableName() + " WHERE version = " + dataVersion() + ";");
		handler.performGetQuery(board.getLogger(), query, set -> {
			if (set.next()) {
				try {
					String json = set.getString("data");
					W wrapper = board.getPluginGson().fromJson(json, jsonDataWrapperClass);
					if (wrapper != null) {
						unwrapJsonData(wrapper);
					}
				} catch (Throwable exception) {
					exception.printStackTrace();
				}
			}
		});
	}

	protected abstract void wrapJsonData(W wrapper);

	@Override
	public final void remotePushCached() throws Throwable {
		W wrapper = jsonDataWrapperClass.newInstance();
		wrapJsonData(wrapper);
		String json = board.getPluginGson().toJson(wrapper, jsonDataWrapperClass);

		Query insertQuery = new Query("INSERT INTO " + tableName() + " (version,data) VALUES(" + dataVersion() + "," + Query.escapeValue(json) + ")");

		if (handler instanceof SQLiteHandler) {
			insertQuery.add(" ON CONFLICT(version) DO UPDATE SET "
					+ "data = excluded.data"
					+ ";");
		} else if (ConfigGCore.mySQLPre8019) {
			insertQuery.add(" ON DUPLICATE KEY UPDATE "
					+ "data = VALUES(data)"
					+ ";");
		} else {
			insertQuery.add(" AS new ON DUPLICATE KEY UPDATE "
					+ "data = new.data"
					+ ";");
		}

		handler.performUpdateQuery(board.getLogger(), insertQuery);
	}

	protected abstract void unwrapJsonData(W wrapper);

}
