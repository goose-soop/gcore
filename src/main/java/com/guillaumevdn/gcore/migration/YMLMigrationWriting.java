package com.guillaumevdn.gcore.migration;

import java.io.File;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;

/**
 * @author GuillaumeVDN
 */
public class YMLMigrationWriting extends YMLConfiguration {

	public YMLMigrationWriting(GPlugin plugin, File file) {
		super(plugin, file);
		if (file.exists()) {
			file.delete();
		}
	}

	// ----- don't load, completely override
	@Override
	public void load() {
	}

}
