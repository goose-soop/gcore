package com.guillaumevdn.gcore.lib.data.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.logging.Logger;

/**
 * @author GuillaumeVDN
 */
public final class MySQLHandler implements SQLHandler {

	private SQLConnector connector = null;
	private boolean canConnect = false;

	public MySQLHandler() {
	}

	public boolean canConnect() {
		return canConnect;
	}

	public SQLConnector getConnector() {
		return connector;
	}

	public void setConnector(SQLConnector mysql) {
		this.connector = mysql;
		canConnect = false;
	}

	public void updateCanConnect() throws SQLException {
		canConnect = false;
		if (connector != null) {
			connector.ensureConnection();
			canConnect = true;
		}
	}

	@Override
	public void shutdown() {
		if (connector != null) {
			connector.shutdown();
		}
	}

	// ----- methods
	@Override
	public boolean performUpdateQuery(GPlugin plugin, Logger logQueryTo, String query) {
		return performUpdateQuery(plugin, logQueryTo, new Query(query));
	}

	@Override
	public boolean performUpdateQuery(GPlugin plugin, Logger logQueryTo, Query query) {
		if (!canConnect) {
			return false;
		}
		query.logTo(logQueryTo);
		return connector.performUpdateQuery(plugin, query);
	}

	@Override
	public boolean performGetQuery(GPlugin plugin, Logger logQueryTo, String query, ThrowableConsumer<ResultSet> syncProcessor) {
		return performGetQuery(plugin, logQueryTo, new Query(query), syncProcessor);
	}

	@Override
	public boolean performGetQuery(GPlugin plugin, Logger logQueryTo, Query query, ThrowableConsumer<ResultSet> syncProcessor) {
		if (!canConnect) {
			return false;
		}
		query.logTo(logQueryTo);
		return connector.performGetQuery(plugin, query, syncProcessor);
	}

}
