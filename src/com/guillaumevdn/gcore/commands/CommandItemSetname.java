package com.guillaumevdn.gcore.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Param;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.messenger.Messenger.Level;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandItemSetname extends CommandArgument {

	private static final Param paramName = new Param(Utils.asList("name", "n"), "name", null, true, true);

	public CommandItemSetname() {
		super(GCore.inst(), Utils.asList("setname"), "set the name of an item", GPerm.GCORE_ADMIN, true, paramName); 
	}

	@Override
	public void perform(CommandCall call) {
		String name = paramName.getString(call);
		if (name != null) {
			Player player = call.getSenderAsPlayer();
			ItemStack item = player.getInventory().getItemInHand();
			Mat mat = Mat.from(item);
			// invalid mat
			if (mat == null || mat.isAir()) {
				GCore.inst().messageError(player, "Can't set name for this item");
				return;
			}
			// set name
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			item.setItemMeta(meta);
			player.setItemInHand(item);
			player.updateInventory();
			Messenger.send(player, Level.NORMAL_INFO, "GCore", "Set name to " + name + ".");
		}
	}

}
