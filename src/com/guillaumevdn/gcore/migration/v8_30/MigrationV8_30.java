package com.guillaumevdn.gcore.migration.v8_30;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.migration.BackupBehavior;
import com.guillaumevdn.gcore.lib.migration.MigrationNextMinor;

/**
 * @author GuillaumeVDN
 */
public final class MigrationV8_30 extends MigrationNextMinor {

	public MigrationV8_30() {
		super(GCore.inst(), "data_v8", 8, 30);
	}

	@Override
	protected void doMigrate() throws Throwable {
		attemptOperation("adding new currencies to config.yml", BackupBehavior.RESTORE, () -> {
			YMLConfiguration config = getPlugin().loadConfigurationFile("config.yml");
			if (config.getFile().exists()) {

				String path = "currencies.XP";
				if (!config.contains(path)) {
					config.write(path + ".name", "XP");
					config.write(path + ".format_multiplier", 1d);
					config.write(path + ".format_decimal_precision", 0);  // all three currencies are integers
					config.write(path + ".format_single", "{amount} XP");
					config.write(path + ".format_multiple", "{amount} XP");
					config.write(path + ".icon.type", "PAPER");
					config.write(path + ".icon.name", "{formatted_amount}");
					config.write(path + ".icon.enchantments.DURABILITY", 1);
					config.write(path + ".icon.flags", CollectionUtils.asList(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES));
					countMod();
					log("> Added " + path);
					config.save();
				}
			}
		});
	}

}
