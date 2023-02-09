package com.guillaumevdn.gcore.lib.data.sql;

import java.io.File;
import java.sql.ResultSet;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.logging.Logger;

/**
 * @author GuillaumeVDN
 */
public final class SQLiteHandler implements SQLHandler {

	private SQLConnector connector;

	@Deprecated
	public SQLiteHandler(File dbFile) {
		this(GCore.inst(), dbFile);
	}

	public SQLiteHandler(GPlugin plugin, File dbFile) {
		connector = new SQLConnector(plugin, "jdbc:sqlite:" + dbFile.getPath(), null, null);
	}

	@Override
	public final boolean performUpdateQuery(Logger logQueryTo, String query) {
		return performUpdateQuery(logQueryTo, new Query(query));
	}

	@Override
	public final boolean performUpdateQuery(Logger logQueryTo, Query query) {
		return connector.performUpdateQuery(query);
	}

	@Override
	public final boolean performGetQuery(Logger logQueryTo, String query, ThrowableConsumer<ResultSet> syncProcessor) {
		return performGetQuery(logQueryTo, new Query(query), syncProcessor);
	}

	@Override
	public final boolean performGetQuery(Logger logQueryTo, Query query, ThrowableConsumer<ResultSet> syncProcessor) {
		return connector.performGetQuery(query, syncProcessor);
	}

	@Override
	public void shutdown() {
		connector.closeConnection();
	}

}
