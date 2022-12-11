package com.guillaumevdn.gcore.lib.data.sql;

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
public final class SQLConnector {

	private final GPlugin plugin;
	private transient final String url;
	private transient final String usr;
	private transient final String pwd;

	public SQLConnector(GPlugin plugin, String url, String usr, String pwd) {
		this.plugin = plugin;
		this.url = url;
		this.usr = usr;
		this.pwd = pwd;
	}

	// ----- methods
	//private final Object LOCK = new Object();
	private Connection connection;

	public void ensureConnection() throws SQLException {
		//synchronized (LOCK) {
		try {
			if (connection != null && !connection.isClosed() && connection.isValid(1)) {
				return;
			}
		} catch (Throwable error) {
			plugin.getMainLogger().error("Couldn't ensure connection to database " + url, error);
		}
		connection = DriverManager.getConnection(url, usr, pwd);
		//}
	}

	public void closeConnection() {
		//synchronized (LOCK) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (Throwable error) {
			plugin.getMainLogger().error("Couldn't close connection to database " + url, error);
		} finally {
			connection = null;
		}
		//}
	}

	/*private static final DateTimeFormatter ISLANDPASS_DEBUG_LOCALDATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd' 'HH'h'mm'm'ss's'SSS'ms'");
	public void islandpassLog(String line, boolean logTime) {
		if (url.contains("islands.sqlite.db")) {
			final File logFile = plugin.getDataFile("debug_island_data/sql_requests_log.txt");
			FileUtils.ensureExistence(logFile);

			try (final BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
				writer.write((logTime ? "[" + ISLANDPASS_DEBUG_LOCALDATETIME_FORMAT.format(ConfigGCore.timeNow()) + "] " : "") + line + "\n");
			} catch (IOException error) {
				plugin.getMainLogger().error("Couldn't log SQL : " + line, error);
			}
		}
	}*/

	public boolean performUpdateQuery(Query query) {
		return doPerformUpdateQuery(query, false);
	}

	private boolean doPerformUpdateQuery(Query query, boolean retrying) {
		if (!query.isEmpty()) {
			//synchronized (LOCK) {
			//String islandPassDebugOutput = query.combineParts() + "\n";
			try {
				ensureConnection();
				final PreparedStatement statement = connection.prepareStatement(query.combineParts());
				try {
					statement.executeUpdate();
				} finally {
					statement.close();
				}
				//islandPassDebugOutput += "... success, " + count + " updated\n";
				//islandpassLog(islandPassDebugOutput, true);
			} catch (Throwable exception) {
				if (exception.getMessage() != null && exception.getMessage().contains("Connection timed out") && !retrying) {
					//islandPassDebugOutput += "... failed";
					//islandpassLog(islandPassDebugOutput, false);
					closeConnection();
					return doPerformUpdateQuery(query, true);
				}
				//islandPassDebugOutput += "... failed\n";
				//islandpassLog(islandPassDebugOutput, false);
				printQueryError(query, exception, retrying);
				return false;
			}
			//}
		}
		return true;
	}

	public boolean performGetQuery(Query query, ThrowableConsumer<ResultSet> syncProcessor) {
		return doPerformGetQuery(query, syncProcessor, false);
	}

	private boolean doPerformGetQuery(Query query, ThrowableConsumer<ResultSet> syncProcessor, boolean retrying) {
		if (!query.isEmpty()) {
			//synchronized (LOCK) {
			//String islandPassDebugOutput = query.combineParts() + "\n";
			try {
				ensureConnection();
				final PreparedStatement statement = connection.prepareStatement(query.combineParts());
				final ResultSet set = statement.executeQuery();
				try {
					syncProcessor.accept(set);
				} finally {
					statement.close();
					set.close();
				}
				//islandPassDebugOutput += "... success, got " + set.getRow() + " row(s)\n";
				//islandpassLog(islandPassDebugOutput, true);
			} catch (Throwable exception) {
				if (exception.getMessage() != null && exception.getMessage().contains("Connection timed out") && !retrying) {
					//islandPassDebugOutput += "... failed";
					//islandpassLog(islandPassDebugOutput, false);
					closeConnection();
					return doPerformGetQuery(query, syncProcessor, true);
				}
				//islandPassDebugOutput += "... failed\n";
				//islandpassLog(islandPassDebugOutput, false);
				printQueryError(query, exception, retrying);
				return false;
			}
			//}
		}
		return true;
	}

	private void printQueryError(Query query, Throwable exception, boolean retried) {
		plugin.getMainLogger().error("Couldn't perform SQLConnector query (retried " + retried + ")\n---------- PARTS ----------" + query.logToString() + "\n---------------------------------", exception);
	}

}
