package com.guillaumevdn.gcore.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.messenger.Messenger.Level;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandItemRead extends CommandArgument {

	public CommandItemRead() {
		super(GCore.inst(), Utils.asList("read"), "get the configuration data for the item in your hand", GPerm.GCORE_ADMIN, true);
	}

	@Override
	public void perform(CommandCall call) {
		Player player = call.getSenderAsPlayer();
		ItemStack item = player.getInventory().getItemInHand();
		if (item != null) {
			try {
				String id = UUID.randomUUID().toString().split("-")[0];
				YMLConfiguration config = new YMLConfiguration(GCore.inst(), new File(GCore.inst().getDataFolder() + "/read_items.yml"), null, true, true);
				config.setItem(id, new ItemData(id, item));
				config.save();
				Messenger.send(player, Level.NORMAL_INFO, "GCore", "The data of this item was added to /GCore/read_items.yml (" + id + ").");
			} catch (Throwable exception) {
				exception.printStackTrace();
				Messenger.send(player, Level.SEVERE_ERROR, "GCore", "Couldn't get the configuration data for the item in your hand, check the console.");
			}
			return;
		}
		Messenger.send(player, Level.SEVERE_ERROR, "GCore", "This item can't be read.");
	}

}
