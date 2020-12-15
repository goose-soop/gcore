package com.guillaumevdn.gcore.lib.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;

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
	public boolean performUpdateQuery(GPlugin plugin, Query query) {
		return canConnect ? mysql.performUpdateQuery(plugin, query) : false;
	}

	public boolean performGetQuery(GPlugin plugin, Query query, ThrowableConsumer<ResultSet> syncProcessor) {
		return canConnect ? mysql.performGetQuery(plugin, query, syncProcessor) : false;
	}

}
