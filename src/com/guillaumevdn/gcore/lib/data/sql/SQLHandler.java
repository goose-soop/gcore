package com.guillaumevdn.gcore.lib.data.sql;

import java.sql.ResultSet;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.logging.Logger;

/**
 * @author GuillaumeVDN
 */
public interface SQLHandler {

	void shutdown();

	@Deprecated
	default boolean performUpdateQuery(GPlugin plugin, Logger logQueryTo, String query) {
		return performUpdateQuery(logQueryTo, query);
	}

	@Deprecated
	default boolean performUpdateQuery(GPlugin plugin, Logger logQueryTo, Query query) {
		return performUpdateQuery(logQueryTo, query);
	}

	@Deprecated
	default boolean performGetQuery(GPlugin plugin, Logger logQueryTo, String query, ThrowableConsumer<ResultSet> syncProcessor) {
		return performGetQuery(logQueryTo, query, syncProcessor);
	}

	@Deprecated
	default boolean performGetQuery(GPlugin plugin, Logger logQueryTo, Query query, ThrowableConsumer<ResultSet> syncProcessor) {
		return performGetQuery(logQueryTo, query, syncProcessor);
	}

	boolean performUpdateQuery(Logger logQueryTo, String query);
	boolean performUpdateQuery(Logger logQueryTo, Query query);
	boolean performGetQuery(Logger logQueryTo, String query, ThrowableConsumer<ResultSet> syncProcessor);
	boolean performGetQuery(Logger logQueryTo, Query query, ThrowableConsumer<ResultSet> syncProcessor);

}
