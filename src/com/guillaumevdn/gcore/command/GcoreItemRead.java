package com.guillaumevdn.gcore.command;

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

/**
 * @author GuillaumeVDN
 */
public final class GcoreItemRead extends Subcommand {

	public GcoreItemRead() {
		super(true, PermissionGCore.inst().gcoreAdmin, TextGCore.commandDescriptionGcoreItemRead, ConfigGCore.commandsAliasesItemRead);
	}

	@Override
	public void perform(CommandCall call) {
		Player sender = call.getSenderPlayer();
		ItemStack item = sender.getItemInHand();
		if (Mat.isVoid(item)) {
			TextGCore.messageItemReadNull.send(sender);
		} else {
			YMLConfiguration config = GCore.inst().loadConfigurationFile("item_export.yml");
			int nextId = config.readInteger("last_id", 0) + 1;
			config.write("last_id", nextId);
			config.write("" + nextId, item);
			config.save();
			TextGCore.messageItemReadExported.replace("{file}", () -> "GCore/item_export.yml").replace("{path}", () -> nextId).send(sender);
		}
	}

}
