package com.guillaumevdn.gcore.migration.v8_24;

import java.util.Map;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.migration.BackupBehavior;
import com.guillaumevdn.gcore.lib.migration.Migration;
import com.guillaumevdn.gcore.lib.migration.MigrationNextMinor;

/**
 * @author GuillaumeVDN
 */
public final class MigrationV8_24 extends MigrationNextMinor {

	public MigrationV8_24() {
		super(GCore.inst(), "data_v8", 8, 24);
	}

	@Override
	protected void doMigrate() throws Throwable {
		migrateRequiredContentItems(this, "guis/SYSTEM_confirm.yml",
				CollectionUtils.asMap("contents.confirm", "item_confirm", "contents.cancel", "item_cancel"));
	}

	public static void migrateRequiredContentItems(Migration migration, String filePath, Map<String, String> paths) throws Throwable {
		migration.attemptOperation("migrate required GUI contents to their own settings in " + filePath, BackupBehavior.RESTORE, () -> {
			int initialCount = migration.getModCount();
			YMLConfiguration config = migration.getPlugin().loadConfigurationFile(filePath);
			if (config.getFile().exists()) {
				for (String old : paths.keySet()) {
					String neww = paths.get(old);
					if (config.contains(old)) {
						config.move(old, neww);
						migration.countMod();
						migration.log("> Moved item " + old + " to " + neww);
					}
				}
				if (migration.getModCount() != initialCount) {
					config.save();
				}
			}
		});
	}

}
