package com.guillaumevdn.gcore.lib.data.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.logging.Logger;
import com.guillaumevdn.gcore.lib.serialization.Serializer;

/**
 * @author GuillaumeVDN
 */
public final class Query {

	private List<String> parts = new ArrayList<>();

	public Query() {
	}

	public Query(String part) {
		add(part);
	}

	// ----- get
	public List<String> getParts() {
		return Collections.unmodifiableList(parts);
	}

	public boolean isEmpty() {
		return parts.isEmpty();
	}

	// ----- methods
	public Query add(String part) {
		parts.add(part);
		return this;
	}

	public Query add(Query query) {
		parts.addAll(query.parts);
		return this;
	}

	public String combineParts() {
		String q = "";
		for (String part : parts) {
			q += part;
		}
		return q;
	}

	public void logTo(Logger logger) {
		if (logger != null && logger.isLogSQL()) {
			logger.info("\n--------- PERFORMING QUERY ----------" + logToString() + "\n--------------------------------");
		}
	}

	public String logToString() {
		String out = "";
		for (int i = 0; i < parts.size(); ++i) {
			if (i != 0) out += "\n-----";
			out += "\n" + parts.get(i);
		}
		return out;
	}

	public String logToString(String partMustContains) {
		String out = "";
		for (int i = 0; i < parts.size(); ++i) {
			String part = parts.get(i);
			if (part.contains(partMustContains)) {
				if (i != 0) out += "\n-----";
				out += "\n" + part;
			}
		}
		return out;
	}

	// ----- static methods
	public static String escapeValue(String value) {
		return "'" + value.replace("'", "''") /* ' can be replaced by '' in SQLConnector queries */ + "'";
	}

	public static <T> String buildWhereKeysInString(String tableName, String keyRowName, Collection<T> keysToString) {
		if (keysToString.isEmpty()) {
			return "";
		}
		String query = "WHERE " + keyRowName + " IN (";
		int i = -1;
		for (T key : keysToString) {
			if (++i != 0) query += ",";
			final Serializer<T> serializer = Serializer.find((Class<T>) key.getClass());
			query += escapeValue(serializer.serialize(key));
		}
		query += ")";
		return query;
	}

	public static Query buildSelectAll(String tableName) {
		return new Query("SELECT * FROM " + tableName + ";");
	}

	public static <T> Query buildSelectKeysIn(String tableName, String keyRowName, Collection<T> keysToString) {
		if (keysToString.isEmpty()) {
			return new Query();
		}
		return new Query("SELECT * FROM " + tableName + " " + buildWhereKeysInString(tableName, keyRowName, keysToString) + ";");
	}

	public static <T> Query buildDeleteKeysIn(String tableName, String keyRowName, Collection<T> keysToString) {
		if (keysToString.isEmpty()) {
			return new Query();
		}
		return new Query("DELETE FROM " + tableName + " " + buildWhereKeysInString(tableName, keyRowName, keysToString) + ";");
	}

	public static <T> Query buildInsertOrUpdatePair(String tableName, String keyRowName, String dataName, Collection<T> keysToString, Function<T, String> getData, boolean sqlite) {
		// build query
		Query query = new Query("INSERT INTO " + tableName + " (" + keyRowName + ", " + dataName + ") VALUES ");
		int i = -1;
		for (T key : keysToString) {
			String data = getData.apply(key);
			if (data != null && !data.equals("null")) {  // happens to WarnD sometimes, this writes a null value directly into the database, causing quests to reset ; this avoids it, although further investigation is needed to find the cause
				String q = ++i != 0 ? "," : "";
				final Serializer<T> serializer = Serializer.find((Class<T>) key.getClass());
				q += "(" + Query.escapeValue(serializer.serialize(key)) + "," + Query.escapeValue(data) + ")";
				query.add(q);
			}
		}

		if (query.getParts().size() == 1) {  // if data is null
			return new Query();
		}

		// on duplicate key
		if (sqlite) {
			query.add(" ON CONFLICT(" + keyRowName + ") DO UPDATE SET " + dataName + " = excluded." + dataName + ";");
		} else if (ConfigGCore.mySQLPre8019) {
			query.add(" ON DUPLICATE KEY UPDATE " + dataName + " = VALUES(" + dataName + ");");
		} else {
			query.add(" AS new ON DUPLICATE KEY UPDATE " + dataName + " = new." + dataName + ";");
		}

		// done
		return query;
	}

}
