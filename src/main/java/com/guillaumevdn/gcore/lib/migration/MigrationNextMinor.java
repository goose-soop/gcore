package com.guillaumevdn.gcore.lib.migration;

import com.guillaumevdn.gcore.lib.GPlugin;

/**
 * @author GuillaumeVDN
 */
public abstract class MigrationNextMinor extends Migration {

	public MigrationNextMinor(GPlugin plugin, String dataFolderName, int major, int nextMinor) {
		super(plugin, "v" + major + "." + (nextMinor - 1), "v" + major + "." + (nextMinor - 1) + " -> v" + major + "." + nextMinor, dataFolderName + "/migrated_v" + major + "." + nextMinor + ".0.DONTREMOVE");
	}

}
