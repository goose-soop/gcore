package com.guillaumevdn.gcore.lib.data.sql;

import java.io.File;
import java.sql.ResultSet;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.logging.Logger;

/**
 * @author GuillaumeVDN
 */
public final class SQLiteHandler implements SQLHandler {

	private SQLConnector connector;

	public SQLiteHandler(File dbFile) {
		connector = new SQLConnector("jdbc:sqlite:" + dbFile.getPath(), null, null);
	}

	@Override
	public final boolean performUpdateQuery(GPlugin plugin, Logger logQueryTo, String query) {
		return performUpdateQuery(plugin, logQueryTo, new Query(query));
	}

	@Override
	public final boolean performUpdateQuery(GPlugin plugin, Logger logQueryTo, Query query) {
		return connector.performUpdateQuery(plugin, query);
	}

	@Override
	public final boolean performGetQuery(GPlugin plugin, Logger logQueryTo, String query, ThrowableConsumer<ResultSet> syncProcessor) {
		return performGetQuery(plugin, logQueryTo, new Query(query), syncProcessor);
	}

	@Override
	public final boolean performGetQuery(GPlugin plugin, Logger logQueryTo, Query query, ThrowableConsumer<ResultSet> syncProcessor) {
		return connector.performGetQuery(plugin, query, syncProcessor);
	}

	@Override
	public void shutdown() {
		connector.shutdown();
	}

}
