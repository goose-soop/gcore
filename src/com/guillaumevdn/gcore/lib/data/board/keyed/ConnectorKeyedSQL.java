package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.Collection;
import java.util.Set;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.data.sql.Query;
import com.guillaumevdn.gcore.lib.data.sql.SQLHandler;
import com.guillaumevdn.gcore.lib.data.sql.SQLiteHandler;
import com.guillaumevdn.gcore.lib.reflection.Reflection;

public abstract class ConnectorKeyedSQL<K, V> extends ConnectorKeyed<K, V> {

	private SQLHandler handler;

	public ConnectorKeyedSQL(KeyedBoard<K, V> board, SQLHandler handler) {
		super(board);
		this.handler = handler;
	}

	public String tableName() {
		return board.getId();
	}

	public abstract String keyName();
	public String keyType() {
		return "CHAR(36)";
	}

	public String valueName() {
		return "data";
	}

	@Override
	public void shutdown() {
		handler.shutdown();
	}

	@Override
	public void remoteInit() throws Throwable {
		if (handler instanceof SQLiteHandler) {
			handler.performUpdateQuery(board.getPlugin(), board.getLogger(),
					"CREATE TABLE IF NOT EXISTS " + tableName() + "("
							+ keyName() + " " + keyType() + " NOT NULL PRIMARY KEY,"
							+ valueName() + " LONGTEXT NOT NULL"
							+ ");"
					);
		} else {
			handler.performUpdateQuery(board.getPlugin(), board.getLogger(),
					"CREATE TABLE IF NOT EXISTS " + tableName() + "("
							+ keyName() + " " + keyType() + " NOT NULL,"
							+ valueName() + " LONGTEXT NOT NULL,"
							+ "PRIMARY KEY(" + keyName() + ")"
							+ ") ENGINE=InnoDB DEFAULT CHARSET = 'utf8';"
					);
		}
	}

	@Override
	public void remotePullAll() throws Throwable {
		remotePull(Query.buildSelectAll(tableName()));
	}

	@Override
	public void remotePullElements(Set<K> references) throws Throwable {
		Query query = Query.buildSelectKeysIn(tableName(), keyName(), references);
		logPullQuery(references, query);
		remotePull(query);
	}

	private void remotePull(Query query) {
		handler.performGetQuery(board.getPlugin(), board.getLogger(), query, set -> {
			while (set.next()) {
				String rawKey = set.getString(keyName());
				try {
					K key = decodeKey(rawKey);
					if (key == null) {
						board.getLogger().warning("Found invalid " + keyName() + " '" + rawKey + "' in database, skipped it");
						continue;
					}
					String rawValue = set.getString(valueName());
					V value = decodeValue(rawValue);
					if (value == null) {
						board.getLogger().warning("Found invalid " + valueName() + " for '" + key + "' in database, skipped it");
						continue;
					}
					board.putInCache(key, value);
					logPulled(key, value, rawKey, rawValue);
				} catch (Throwable exception) {
					if (Reflection.stackTraceContainsIgnoreCase(exception, "JsonSyntaxException")) {
						board.getLogger().warning("Found invalid " + valueName() + " (syntax error) for '" + rawKey + "' in database, skipped it");
					} else {
						exception.printStackTrace();
					}
				}
			}
		});
	}

	protected abstract K decodeKey(String raw);
	protected abstract V decodeValue(String rawData);
	protected abstract String encodeValue(V value);

	@Override
	public void remotePushElements(Set<K> refs) throws Throwable {
		if (refs.isEmpty()) return;
		for (Collection<? extends K> references : CollectionUtils.splitCollection(refs, 999)) {  // multiple VALUES are limited to 1000 elements ; https://stackoverflow.com/questions/452859/inserting-multiple-rows-in-a-single-sql-query#comment22032805_452934
			Query query = Query.buildInsertOrUpdatePair(tableName(), keyName(), valueName(), references, k -> encodeValue(board.getCachedValue(k)), handler instanceof SQLiteHandler);
			handler.performUpdateQuery(board.getPlugin(), board.getLogger(), query);
			logPush(references, query);
		}
	}

	@Override
	public void remoteDeleteElements(Set<K> references) throws Throwable {
		if (references.isEmpty()) return;  // let's avoid deleting the whole table just because there's no WHERE clause
		Query query = Query.buildDeleteKeysIn(tableName(), keyName(), references);
		handler.performUpdateQuery(board.getPlugin(), board.getLogger(), query);
	}

	protected void logPullQuery(Set<K> references, Query query) {
	}

	protected void logPulled(K key, V value, String rawKey, String rawValue) {
	}

	protected void logPush(Collection<? extends K> references, Query query) {
	}

}