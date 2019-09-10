package com.guillaumevdn.gcore.lib.data.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.util.Utils;

public class MySQL {

	// fields and constrictor
	private final String url;
	private final String usr;
	private final String pwd;

	public MySQL(String url, String usr, String pwd) {
		this.url = url;
		this.usr = usr;
		this.pwd = pwd;
	}

	// methods
	private transient Connection connection = null;
	private transient long lastQuery = 0L;

	public Connection connect() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			if (System.currentTimeMillis() - lastQuery > 5000L) {
				try {
					connection.close();
				} catch (Throwable ignored) {}
			}
		}
		if (connection == null || connection.isClosed()) {
			connection = DriverManager.getConnection(url, usr, pwd);
		}
		return connection;
	}

	public boolean performUpdateQuery(Query query) {
		try {
			Connection connection = connect();
			lastQuery = System.currentTimeMillis();
			PreparedStatement statement = connection.prepareStatement(query.getQuery());
			for (int i = 0; i < query.getStringParams().size(); i++) {
				statement.setString(i + 1, query.getStringParams().get(i));
			}
			statement.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException exception) {
			exception.printStackTrace();
			GCore.inst().error("Couldn't perform query '" + query.getQuery() + "' with params '" + Utils.asNiceString(query.getStringParams(), true) + "'");
			return false;
		}
	}

	public ResultSet performGetQuery(Query query) {
		try {
			Connection connection = connect();
			lastQuery = System.currentTimeMillis();
			PreparedStatement statement = connection.prepareStatement(query.getQuery());
			for (int i = 0; i < query.getStringParams().size(); i++) {
				statement.setString(i + 1, query.getStringParams().get(i));
			}
			return statement.executeQuery();
		} catch (SQLException exception) {
			exception.printStackTrace();
			GCore.inst().error("Couldn't perform query '" + query.getQuery() + "' with params '" + Utils.asNiceString(query.getStringParams(), true) + "'");
			return null;
		}
	}

}
