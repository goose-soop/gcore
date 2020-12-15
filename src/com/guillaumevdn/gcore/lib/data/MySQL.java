package com.guillaumevdn.gcore.lib.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
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
		if (!query.isEmpty()) {
			try (Connection conn = connect(); PreparedStatement statement = prepareStatement(query)) {
				statement.executeUpdate();
			} catch (Throwable exception) {
				printQueryError(plugin, query, exception);
				return false;
			}
		}
		return true;
	}

	public boolean performGetQuery(GPlugin plugin, Query query, ThrowableConsumer<ResultSet> syncProcessor) {
		if (!query.isEmpty()) {
			try (Connection conn = connect(); PreparedStatement statement = prepareStatement(query); ResultSet set = statement.executeQuery()) {
				syncProcessor.accept(set);
			} catch (Throwable exception) {
				printQueryError(plugin, query, exception);
				return false;
			}
		}
		return true;
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
		String rng = StringUtils.generateRandomAlphanumericString(5);
		plugin.getMainLogger().error("Couldn't perform MySQL query (" + rng + ")", exception);
		String out = "\n--------- QUERY PARTS (" + rng + ") ----------";
		WrapperInteger i = WrapperInteger.of(0);
		for (Entry<String, List<Object>> entry : query.getParts().entrySet()) {
			out += i.alter(1) + " ----------\n";
			out += entry.getKey() + "\n";
			for (Object param : entry.getValue()) {
				out += "? " + String.valueOf(param) + "\n";
			}
		}
		System.out.println(out + "\n--------------------------------");
		System.out.flush();
	}

}
