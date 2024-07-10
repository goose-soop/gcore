package com.guillaumevdn.gcore.migration.v8_5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.file.ResourceExtractor;
import com.guillaumevdn.gcore.lib.migration.BackupBehavior;
import com.guillaumevdn.gcore.lib.migration.Migration;
import com.guillaumevdn.gcore.lib.migration.MigrationNextMinor;

/**
 * @author GuillaumeVDN
 */
public final class MigrationV8_5 extends MigrationNextMinor {

	public MigrationV8_5() {
		super(GCore.inst(), "data_v8", 8, 5);
	}

	@Override
	public boolean mustMigrate() {
		return GCore.inst().getDataFile("config.yml").exists();
	}

	@Override
	protected void doMigrate() throws Throwable {
		attemptOperation("converting config setting 'gui_items_refresh_seconds_placeholder' to ticks", BackupBehavior.RESTORE, () -> {
			YMLConfiguration config = GCore.inst().loadConfigurationFile("config.yml");
			config.write("gui_items_refresh_ticks_placeholder", config.readInteger("gui_items_refresh_seconds_placeholder", 30) * 20);
			config.write("gui_items_refresh_seconds_placeholder", null);
			config.save();
			countMod();
		});
		// migrate new GUIs files
		if (!getPlugin().getDataFile("guis").exists()) {
			new ResourceExtractor(getPlugin(), getPlugin().getDataFile("guis"), "guis").extract(false, true);
			migrateGUIToOwnFile(this, getPlugin().loadConfigurationFile("config.yml"), "confirm_gui", "guis/SYSTEM_confirm.yml",
					CollectionUtils.asMap("item_yes", "confirm", "item_no", "cancel"), "&dConfirm action", (src, target) -> {
						List<String> lore = src.readStringList("confirm_gui.item_yes.lore", new ArrayList<>());
						target.write("contents.confirm.lore", lore);
					});
		}
	}

	public static void migrateGUIToOwnFile(Migration migration, YMLConfiguration src, String srcPath, String targetFile, Map<String, String> customItems,
			String defaultName) throws Throwable {
		migrateGUIToOwnFile(migration, src, srcPath, targetFile, customItems, defaultName, null);
	}

	public static void migrateGUIToOwnFile(Migration migration, YMLConfiguration src, String srcPath, String targetFile, Map<String, String> customItems,
			String defaultName, BiConsumer<YMLConfiguration, YMLConfiguration> custom) throws Throwable {
		if (src.contains(srcPath)) {
			migration.attemptOperation("convert GUI to file " + targetFile, BackupBehavior.RESTORE, () -> {
				// type and name
				YMLConfiguration gui = migration.getPlugin().loadConfigurationFile(targetFile);
				gui.write("type", src.readString(srcPath + ".type", "GUI_6_ROW"));
				gui.write("name", src.readString(srcPath + ".name", defaultName));
				// contents
				if (src.contains(srcPath + ".contents")) {
					gui.copyFrom(src, srcPath + ".contents", "contents");
				}
				// custom items
				customItems.forEach((old, neww) -> {
					gui.copyFrom(src, srcPath + "." + old, "contents." + neww);
				});
				// custom
				if (custom != null) {
					custom.accept(src, gui);
				}
				// save files
				gui.save();
				src.write(srcPath, null);
				src.save();
				migration.countMod();
			});
		}
	}

}
