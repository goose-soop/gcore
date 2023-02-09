package com.guillaumevdn.gcore.migration.v8_0.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.data.sql.Query;

/**
 * @author GuillaumeVDN
 */
public final class InstantMySQL {

	private Connection connection;

	public InstantMySQL(String url, String usr, String pwd) throws SQLException {
		connection = DriverManager.getConnection(url, usr, pwd);
	}

	public void close() {
		try {
			connection.close();
		} catch (Throwable exception) {}
	}

	public boolean performUpdateQuery(GPlugin plugin, Query query) {
		try {
			if (!query.isEmpty()) {
				prepareStatement(query).executeUpdate();
				return true;
			}
		} catch (Throwable exception) {
			printQueryError(plugin, query, exception);
		}
		return false;
	}

	public ResultSet performGetQuery(GPlugin plugin, Query query) {
		try {
			if (!query.isEmpty()) {
				return prepareStatement(query).executeQuery();
			}
		} catch (Throwable exception) {
			printQueryError(plugin, query, exception);
		}
		return null;
	}

	private PreparedStatement prepareStatement(Query query) throws SQLException {
		String q = "";
		for (String part : query.getParts()) {
			q += part;
		}
		return connection.prepareStatement(q.toString());
	}

	private void printQueryError(GPlugin plugin, Query query, Throwable exception) {
		plugin.getMainLogger().error("Couldn't perform InstantMySQL query", exception);
		plugin.getMainLogger().error("\n---------- QUERY PARTS ----------" + query.logToString() + "\n---------------------------------");
	}

}
