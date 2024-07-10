package com.guillaumevdn.gcore.migration.v8_25;

import java.util.List;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.migration.BackupBehavior;
import com.guillaumevdn.gcore.lib.migration.MigrationNextMinor;
import com.guillaumevdn.gcore.lib.tuple.Triple;

/**
 * @author GuillaumeVDN
 */
public final class MigrationV8_25 extends MigrationNextMinor {

	public MigrationV8_25() {
		super(GCore.inst(), "data_v8", 8, 25);
	}

	@Override
	protected void doMigrate() throws Throwable {
		attemptOperation("adding new currencies to config.yml", BackupBehavior.RESTORE, () -> {
			YMLConfiguration config = getPlugin().loadConfigurationFile("config.yml");
			if (config.getFile().exists()) {
				List<Triple<String, String, String>> currencies = CollectionUtils.asList(Triple.of("PLAYER_POINTS", "PlayerPoints points", "{amount} point"),
						Triple.of("TOKEN_ENCHANT", "TokenEnchant tokens", "{amount} token"), Triple.of("XP_LEVEL", "XP levels", "{amount} level"));
				int added = 0;

				for (Triple<String, String, String> currency : currencies) {
					String path = "currencies." + currency.getA();
					if (!config.contains(path)) {
						config.write(path + ".name", currency.getB());
						config.write(path + ".format_multiplier", 1d);
						config.write(path + ".format_decimal_precision", 0); // all three currencies are integers
						config.write(path + ".format_single", currency.getC());
						config.write(path + ".format_multiple", currency.getC() + "s");
						config.write(path + ".icon.type", "PAPER");
						config.write(path + ".icon.name", "{formatted_amount}");
						config.write(path + ".icon.enchantments.DURABILITY", 1);
						config.write(path + ".icon.flags", CollectionUtils.asList(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES));
						countMod();
						log("> Added " + path);
						++added;
					}
				}

				if (added != 0) {
					config.save();
				}
			}
		});
	}

}
