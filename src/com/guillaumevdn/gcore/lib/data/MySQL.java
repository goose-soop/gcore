package com.guillaumevdn.gcore.lib.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.wrapper.WrapperInteger;

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

	// methods
	public Connection connect() throws SQLException {
		return DriverManager.getConnection(url, usr, pwd);
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
		PreparedStatement statement = connect().prepareStatement(sql.toString());
		for (int i = 0; i < params.size(); i++) {
			setParam(statement, i + 1, params.get(i));
		}
		return statement;
	}

	private void setParam(PreparedStatement statement, int paramNumber, Object param) throws SQLException {
		if (ObjectUtils.instanceOf(param, Integer.class)) {
			statement.setInt(paramNumber, (int) param);
		} else if (ObjectUtils.instanceOf(param, Long.class)) {
			statement.setLong(paramNumber, (long) param);
		} else if (ObjectUtils.instanceOf(param, Float.class)) {
			statement.setFloat(paramNumber, (float) param);
		} else if (ObjectUtils.instanceOf(param, Double.class)) {
			statement.setDouble(paramNumber, (double) param);
		} else {
			statement.setString(paramNumber, String.valueOf(param));
		}
	}

	private void printQueryError(GPlugin plugin, Query query, Throwable exception) {
		plugin.getMainLogger().error("Couldn't perform MySQL query", exception);
		System.out.println("\n--------- QUERY PARTS ----------");
		WrapperInteger i = WrapperInteger.of(0);
		query.getParts().entrySet().forEach(entry -> {
			System.out.println(i.alter(1) + " ----------");
			System.out.println(entry.getKey());	
			entry.getValue().forEach(param -> System.out.println("? " + String.valueOf(param)));
		});
		System.out.println("\n--------------------------------");
	}

}
