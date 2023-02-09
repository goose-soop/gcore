package com.guillaumevdn.gcore.lib.data.board.keyed.bi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.data.sql.Query;
import com.guillaumevdn.gcore.lib.data.sql.SQLHandler;
import com.guillaumevdn.gcore.lib.data.sql.SQLiteHandler;
import com.guillaumevdn.gcore.lib.tuple.Pair;

public abstract class ConnectorBiKeyedSQL<K, K2, V> extends ConnectorBiKeyed<K, K2, V> {

	private SQLHandler handler;

	public ConnectorBiKeyedSQL(BiKeyedBoard<K, K2, V> board, SQLHandler handler) {
		super(board);
		this.handler = handler;
	}

	public String tableName() {
		return board.getId();
	}

	public abstract String keyName();
	public String keyType() {
		return "VARCHAR(100)";
	}

	public abstract String key2Name();
	public String key2Type() {
		return "CHAR(36)";
	}

	public String valueName() {
		return "value";
	}
	public String valueType() {
		return "DECIMAL(30,3)";
	}

	@Override
	public void shutdown() {
		handler.shutdown();
	}

	@Override
	public void remoteInit() throws Throwable {
		if (handler instanceof SQLiteHandler) {
			handler.performUpdateQuery(board.getLogger(),
					"CREATE TABLE IF NOT EXISTS " + tableName() + " ("
							+ "`key` INTEGER PRIMARY KEY,"  // in SQLite a primary key int will auto-increment by itself
							+ keyName() + " " + keyType() + " NOT NULL,"
							+ key2Name() + " " + key2Type() + " NOT NULL,"
							+ valueName() + " " + valueType() + " NOT NULL"
							+ ");"
					);
		} else {
			handler.performUpdateQuery(board.getLogger(),
					"CREATE TABLE IF NOT EXISTS " + tableName() + " ("
							+ "`key` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
							+ keyName() + " " + keyType() + " NOT NULL,"
							+ key2Name() + " " + key2Type() + " NOT NULL,"
							+ valueName() + " " + valueType() + " NOT NULL,"
							+ "PRIMARY KEY(`key`)"
							+ ") ENGINE=InnoDB DEFAULT CHARSET = 'utf8';"
					);
		}
	}

	@Override
	public void remotePullAll() throws Throwable {
		remotePull(Query.buildSelectAll(tableName()), () -> board.clearCache());
	}

	@Override
	public void remotePullElementsByPrimary(Set<K> references) throws Throwable {
		if (references.isEmpty()) return;
		remotePull(
				Query.buildSelectKeysIn(tableName(), keyName(), references.stream().map(k -> k.toString()).collect(Collectors.toSet())),
				() -> ((BiKeyedBoard<K, K2, V>) board).removeElementsFromCacheByPrimary(references)
				);
	}

	@Override
	public void remotePullElements(Set<Pair<K, K2>> references) throws Throwable {
		if (references.isEmpty()) return;
		String query = "SELECT * FROM " + tableName() + " ";
		int i = -1;
		for (Pair<K, K2> ref : references) {
			query += (++i == 0 ? "WHERE" : "OR") + " (" + keyName() + " = " + Query.escapeValue(ref.getA().toString()) + " AND " + key2Name() + " = " + Query.escapeValue(ref.getB().toString()) + ")";
		}
		query += ";";
		remotePull(new Query(query), () -> board.removeElementsFromCache(references));
	}

	private void remotePull(Query query, Runnable beforeSetProcessing) throws Throwable {
		handler.performGetQuery(board.getLogger(), query, set -> {
			if (beforeSetProcessing != null) {
				beforeSetProcessing.run();
			}
			Set<String> skippedKeys = new HashSet<>();
			while (set.next()) {
				try {
					String rawKey = set.getString(keyName());
					K key = decodeKey(rawKey);
					if (key == null) {
						if (skippedKeys.add(rawKey)) board.getLogger().warning("Found invalid " + keyName() + " '" + rawKey + "' in database, skipped it");
						continue;
					}
					String rawKey2 = set.getString(key2Name());
					K2 key2 = decodeKey2(rawKey2);
					if (key2 == null) {
						board.getLogger().warning("Found invalid " + key2Name() + " '" + rawKey2 + "' in database, skipped it");
						continue;
					}
					V value = getValue(set);
					board.putInCache(Pair.of(key, key2), value);
				} catch (Throwable exception) {
					exception.printStackTrace();
				}
			}
		});
	}

	protected abstract K decodeKey(String raw);
	protected abstract K2 decodeKey2(String raw);
	protected abstract V getValue(ResultSet set) throws SQLException;

	@Override
	public void remotePushElements(Set<Pair<K, K2>> refs) throws Throwable {
		if (refs.isEmpty()) return;  // let's avoid deleting the whole table just because there's no WHERE clause
		for (Collection<? extends Pair<K, K2>> references : CollectionUtils.splitCollection(refs, 999)) {  // multiple VALUES are limited to 1000 elements ; https://stackoverflow.com/questions/452859/inserting-multiple-rows-in-a-single-sql-query#comment22032805_452934
			Query query = buildRemoteDeleteElementsMySQLQuery(references);

			if (handler instanceof SQLiteHandler) {  // one query at once in SQLite, no semicolons
				handler.performUpdateQuery(board.getLogger(), query);
				query = new Query();
			}

			query.add("INSERT INTO " + tableName() + " (" + keyName() + "," + key2Name() + "," + valueName() + ") VALUES ");
			int i = -1;
			for (Pair<K, K2> ref : references) {
				V v = board.getCachedValue(ref);
				String vstr = v instanceof String ? Query.escapeValue((String) v) : v.toString();
				query.add((++i != 0 ? "," : "") + "(" + Query.escapeValue(ref.getA().toString()) + "," + Query.escapeValue(ref.getB().toString()) + "," + vstr + ")");
			}
			query.add(";");
			handler.performUpdateQuery(board.getLogger(), query);
		}
	}

	@Override
	public void remoteDeleteElements(Set<Pair<K, K2>> references) throws Throwable {
		if (references.isEmpty()) return; // let's avoid deleting the whole table just because there's no WHERE clause
		handler.performUpdateQuery(board.getLogger(), buildRemoteDeleteElementsMySQLQuery(references));
	}

	private Query buildRemoteDeleteElementsMySQLQuery(Collection<? extends Pair<K, K2>> references) {
		if (references.isEmpty()) return null; // let's avoid deleting the whole table just because there's no WHERE clause ; this shouldn't happen since this method is private but better be juuust a little more sure
		Query query = new Query("DELETE FROM " + tableName() + " ");
		int i = -1;
		for (Pair<K, K2> reference : references) {
			query.add((++i == 0 ? "WHERE" : "OR") + " (" + keyName() + " = " + Query.escapeValue(reference.getA().toString()) + " AND " + key2Name() + " = " + Query.escapeValue(reference.getB().toString()) + ")");
		}
		query.add(";");
		return query;
	}

}