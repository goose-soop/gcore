package com.guillaumevdn.gcore.lib.data.sql;

import java.sql.ResultSet;

import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.logging.Logger;

/**
 * @author GuillaumeVDN
 */
public final class MySQLHandler implements SQLHandler {

	private final SQLConnector connector;

	public MySQLHandler(SQLConnector connector) {
		this.connector = connector;
	}

	public SQLConnector getConnector() {
		return connector;
	}

	/*public void setConnector(SQLConnector mysql) {
		this.connector = mysql;
		canConnect = false;
	}

	public void updateCanConnect() throws SQLException {
		canConnect = false;
		if (connector != null) {
			connector.ensureConnection();
			canConnect = true;
		}
	}*/

	@Override
	public void shutdown() {
		if (connector != null) {
			connector.closeConnection();
		}
	}

	// ----- methods
	@Override
	public boolean performUpdateQuery(Logger logQueryTo, String query) {
		return performUpdateQuery(logQueryTo, new Query(query));
	}

	@Override
	public boolean performUpdateQuery(Logger logQueryTo, Query query) {
		query.logTo(logQueryTo);
		return connector.performUpdateQuery(query);
	}

	@Override
	public boolean performGetQuery(Logger logQueryTo, String query, ThrowableConsumer<ResultSet> syncProcessor) {
		return performGetQuery(logQueryTo, new Query(query), syncProcessor);
	}

	@Override
	public boolean performGetQuery(Logger logQueryTo, Query query, ThrowableConsumer<ResultSet> syncProcessor) {
		query.logTo(logQueryTo);
		return connector.performGetQuery(query, syncProcessor);
	}

}
