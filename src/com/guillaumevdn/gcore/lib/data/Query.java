package com.guillaumevdn.gcore.lib.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.logging.Logger;

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

	public void logTo(Logger logger) {
		if (logger != null) {
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

	// ----- static methods
	public static String escapeValue(String value) {
		return "'" + value.replace("'", "''") /* ' can be replaced by '' in MySQL queries */ + "'";
	}

	public static <T> String buildWhereKeysInString(String tableName, String keyRowName, Collection<T> keysToString) {
		if (keysToString.isEmpty()) {
			return "";
		}
		String query = "WHERE " + keyRowName + " IN (";
		int i = -1;
		for (T key : keysToString) {
			if (++i != 0) query += ",";
			query += escapeValue(String.valueOf(key));
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

	public static <T> Query buildInsertOrUpdatePair(String tableName, String keyRowName, String dataName, Collection<T> keysToString, Function<T, String> getData) {
		// build query
		Query query = new Query("INSERT INTO " + tableName + " (" + keyRowName + ", " + dataName + ") VALUES ");
		int i = -1;
		for (T key : keysToString) {
			String q = ++i != 0 ? "," : "";
			q += "(" + Query.escapeValue(String.valueOf(key)) + "," + Query.escapeValue(getData.apply(key)) + ")";
			query.add(q);
		}
		// on duplicate key
		if (ConfigGCore.mySQLPre8019) {
			query.add(" ON DUPLICATE KEY UPDATE data = VALUES(data);");
		} else {
			query.add(" AS new ON DUPLICATE KEY UPDATE data = new.data;");
		}
		// done
		return query;
	}

}
