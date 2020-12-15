package com.guillaumevdn.gcore.migration.v8_9;

import java.io.File;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashSet;
import com.guillaumevdn.gcore.lib.migration.BackupBehavior;
import com.guillaumevdn.gcore.lib.migration.MigrationNextMinor;

/**
 * @author GuillaumeVDN
 */
public final class MigrationV8_9 extends MigrationNextMinor {

	public MigrationV8_9() {
		super(GCore.inst(), "data_v8", 8, 9);
	}

	private static final LowerCaseHashSet ids = CollectionUtils.asLowercaseSet("confirm", "cancel");

	@Override
	protected void doMigrate() throws Throwable {
		// make sure that control items in SYSTEM GUIs are persistent
		attemptDirectYMLFilesOperation("making sure that control items in SYSTEM GUIs are persistent", "gui", BackupBehavior.RESTORE, new File(getPluginFolder() + "/guis"), config -> {
			for (String contentId : config.readKeysForSection("contents")) {
				if (ids.contains(contentId) && !config.readBoolean("contents." + contentId + ".persistent", false)) {
					config.write("contents." + contentId + ".persistent", true);
					countMod();
				}
			}
		});
	}

}
