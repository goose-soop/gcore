package be.guillaumevdn.gcore.lib.data.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.guillaumevdn.gcore.GCore;
import be.guillaumevdn.gcore.lib.util.Utils;

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
	public Connection connect() throws SQLException {
		return DriverManager.getConnection(url, usr, pwd);
	}

	public boolean performUpdateQuery(Query query) {
		try {
			Connection connection = connect();
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
