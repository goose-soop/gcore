package com.guillaumevdn.gcore.lib.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;

/**
 * @author GuillaumeVDN
 */
public final class MySQL {

	private transient final String url;
	private transient final String usr;
	private transient final String pwd;

	public MySQL(String url, String usr, String pwd) {
		this.url = url;
		this.usr = usr;
		this.pwd = pwd;
	}

	// ----- methods
	private Connection connection;

	public void ensureConnection() throws SQLException {
		try {
			if (!connection.isClosed() && connection.isValid(1)) {
				return;
			}
		} catch (Throwable ignored) {}
		connection = DriverManager.getConnection(url, usr, pwd);
	}

	private PreparedStatement prepareStatement(Query query) throws SQLException {
		String q = "";
		for (String part : query.getParts()) {
			q += part;
		}
		ensureConnection();
		return connection.prepareStatement(q.toString());
	}

	public boolean performUpdateQuery(GPlugin plugin, Query query) {
		return performUpdateQuery(plugin, query, false);
	}

	private boolean performUpdateQuery(GPlugin plugin, Query query, boolean retried) {
		if (!query.isEmpty()) {
			try (PreparedStatement statement = prepareStatement(query)) {
				statement.executeUpdate();
			} catch (Throwable exception) {
				if (exception.getMessage() != null && exception.getMessage().contains("Connection timed out") && !retried) {
					try {
						connection.close();
					} catch (SQLException ignored) {}
					connection = null;
					return performUpdateQuery(plugin, query, true);
				}
				printQueryError(plugin, query, exception, retried);
				return false;
			}
		}
		return true;
	}

	public boolean performGetQuery(GPlugin plugin, Query query, ThrowableConsumer<ResultSet> syncProcessor) {
		return performGetQuery(plugin, query, syncProcessor, false);
	}

	private boolean performGetQuery(GPlugin plugin, Query query, ThrowableConsumer<ResultSet> syncProcessor, boolean retried) {
		if (!query.isEmpty()) {
			try (PreparedStatement statement = prepareStatement(query); ResultSet set = statement.executeQuery()) {
				syncProcessor.accept(set);
			} catch (Throwable exception) {
				if (exception.getMessage() != null && exception.getMessage().contains("Connection timed out") && !retried) {
					try {
						connection.close();
					} catch (SQLException ignored) {}
					connection = null;
					return performGetQuery(plugin, query, syncProcessor, true);
				}
				printQueryError(plugin, query, exception, retried);
				return false;
			}
		}
		return true;
	}

	private void printQueryError(GPlugin plugin, Query query, Throwable exception, boolean retried) {
		plugin.getMainLogger().error("Couldn't perform MySQL query (retried " + retried + ")\n---------- PARTS ----------" + query.logToString() + "\n---------------------------------", exception);
	}

}
