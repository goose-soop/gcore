package com.guillaumevdn.gcore.migration.v8_0.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.data.Query;
import com.guillaumevdn.gcore.lib.wrapper.WrapperInteger;

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
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();
		query.getParts().forEach((part, partParams) -> {
			sql.append(part);
			params.addAll(partParams);
		});
		PreparedStatement statement = connection.prepareStatement(sql.toString());
		for (int i = 0; i < params.size(); i++) {
			setParam(statement, i + 1, params.get(i));
		}
		return statement;
	}

	private void setParam(PreparedStatement statement, int paramNulber, Object param) throws SQLException {
		if (param instanceof Integer) {
			statement.setInt(paramNulber, (int) param);
		} else if (param instanceof Long) {
			statement.setLong(paramNulber, (long) param);
		} else if (param instanceof Float) {
			statement.setFloat(paramNulber, (float) param);
		} else if (param instanceof Double) {
			statement.setDouble(paramNulber, (double) param);
		} else {
			statement.setString(paramNulber, String.valueOf(param));
		}
	}

	private void printQueryError(GPlugin plugin, Query query, Throwable exception) {
		plugin.getMainLogger().error("Couldn't perform InstantMySQL query", exception);
		System.out.println("\n---------- QUERY PARTS ----------");
		WrapperInteger i = WrapperInteger.of(0);
		query.getParts().entrySet().forEach(entry -> {
			System.out.println(i.alter(1) + " ----------");
			System.out.println(entry.getKey());	
			entry.getValue().forEach(param -> System.out.println("? " + String.valueOf(param)));
		});
		System.out.println("\n---------------------------------");
	}

}
