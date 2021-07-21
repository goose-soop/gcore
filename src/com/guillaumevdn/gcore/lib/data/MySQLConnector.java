package com.guillaumevdn.gcore.lib.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.logging.Logger;

/**
 * @author GuillaumeVDN
 */
public final class MySQLConnector {

	private MySQL mysql = null;
	private boolean canConnect = false;

	public MySQLConnector() {
	}

	// ----- get
	public boolean canConnect() {
		return canConnect;
	}

	public void updateCanConnect() throws SQLException {
		canConnect = false;
		if (mysql != null) {
			mysql.ensureConnection();
			canConnect = true;
		}
	}

	// ----- set
	public void setMysql(MySQL mysql) {
		this.mysql = mysql;
		canConnect = false;
	}

	// ----- methods
	public boolean performUpdateQuery(GPlugin plugin, Logger logQueryTo, String query) {
		return performUpdateQuery(plugin, logQueryTo, new Query(query));
	}

	public boolean performUpdateQuery(GPlugin plugin, Logger logQueryTo, Query query) {
		if (!canConnect) {
			return false;
		}
		query.logTo(logQueryTo);
		return mysql.performUpdateQuery(plugin, query);
	}

	public boolean performGetQuery(GPlugin plugin, Logger logQueryTo, String query, ThrowableConsumer<ResultSet> syncProcessor) {
		return performGetQuery(plugin, logQueryTo, new Query(query), syncProcessor);
	}

	public boolean performGetQuery(GPlugin plugin, Logger logQueryTo, Query query, ThrowableConsumer<ResultSet> syncProcessor) {
		if (!canConnect) {
			return false;
		}
		query.logTo(logQueryTo);
		return mysql.performGetQuery(plugin, query, syncProcessor);
	}

}
