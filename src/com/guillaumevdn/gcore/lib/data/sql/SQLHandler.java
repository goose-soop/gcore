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

	boolean performUpdateQuery(GPlugin plugin, Logger logQueryTo, String query);
	boolean performUpdateQuery(GPlugin plugin, Logger logQueryTo, Query query);
	boolean performGetQuery(GPlugin plugin, Logger logQueryTo, String query, ThrowableConsumer<ResultSet> syncProcessor);
	boolean performGetQuery(GPlugin plugin, Logger logQueryTo, Query query, ThrowableConsumer<ResultSet> syncProcessor);

}
