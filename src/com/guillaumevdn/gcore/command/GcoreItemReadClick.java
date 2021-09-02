package com.guillaumevdn.gcore.command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextGCore;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.file.FileUtils;

/**
 * @author GuillaumeVDN
 */
public final class GcoreItemReadClick extends Subcommand {

	public static final Set<Player> TOGGLED = Collections.newSetFromMap(new WeakHashMap<>());

	public GcoreItemReadClick() {
		super(true, PermissionGCore.inst().gcoreAdmin, TextGCore.commandDescriptionGcoreItemReadClick, ConfigGCore.commandsAliasesItemReadClick);
	}

	@Override
	public void perform(CommandCall call) {
		Player sender = call.getSenderPlayer();

		if (!TOGGLED.add(sender)) {
			TOGGLED.remove(sender);
		}

		sender.sendMessage(TOGGLED.contains(sender) ? "§aToggled ON - click on items in inventories to see items configs" : "§eToggled OFF");
	}

	public static void logItem(Player player, ItemStack item) {
		if (!Mat.isVoid(item)) {
			YMLConfiguration config = GCore.inst().loadConfigurationFile("data_v8/tmp.yml");
			config.write("item", item);
			config.save();
			try (BufferedReader reader = new BufferedReader(new FileReader(config.getFile()))) {
				for (String line = null; (line = reader.readLine()) != null; ) {
					player.sendMessage(line);
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			FileUtils.delete(config.getFile());
		}
	}

}
