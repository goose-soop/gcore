package com.guillaumevdn.gcore.lib.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import com.guillaumevdn.gcore.lib.GPlugin;

/**
 * @author GuillaumeVDN
 */
public class MySQLConnector {

	private MySQL mysql = null;
	private boolean canConnect = false;

	public MySQLConnector() {
	}

	// get
	public boolean canConnect() {
		return canConnect;
	}

	public void updateCanConnect() throws SQLException {
		canConnect = false;
		if (mysql != null && mysql.connect() != null) {
			canConnect = true;
		}
	}

	// set
	public void setMysql(MySQL mysql) {
		this.mysql = mysql;
		canConnect = false;
	}

	// methods
	public boolean performUpdateQuery(GPlugin plugin, String query, Object... params) {
		return performUpdateQuery(plugin, new Query(query, params));
	}

	public boolean performUpdateQuery(GPlugin plugin, String query, Collection<?> params) {
		return performUpdateQuery(plugin, new Query(query, params));
	}

	public boolean performUpdateQuery(GPlugin plugin, Query query) {
		return canConnect ? mysql.performUpdateQuery(plugin, query) : false;
	}

	public ResultSet performGetQuery(GPlugin plugin, String query, Object... params) {
		return performGetQuery(plugin, new Query(query, params));
	}

	public ResultSet performGetQuery(GPlugin plugin, String query, Collection<?> params) {
		return performGetQuery(plugin, new Query(query, params));
	}

	public ResultSet performGetQuery(GPlugin plugin, Query query) {
		return canConnect ? mysql.performGetQuery(plugin, query) : null;
	}

}
