package com.guillaumevdn.gcore.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Param;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.messenger.Messenger.Level;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandItemSetdura extends CommandArgument {

	private static final Param paramDura = new Param(Utils.asList("durability", "d"), "durability", null, true, true);

	public CommandItemSetdura() {
		super(GCore.inst(), Utils.asList("setdurability", "setdura"), "set the durability of an item", GPerm.GCORE_ADMIN, true, paramDura); 
	}

	@Override
	public void perform(CommandCall call) {
		int durability = paramDura.getInt(call);
		if (durability != Integer.MIN_VALUE) {
			Player player = call.getSenderAsPlayer();
			ItemStack item = player.getInventory().getItemInHand();
			Mat mat = Mat.from(item);
			// invalid mat
			if (mat == null || mat.isAir()) {
				GCore.inst().messageError(player, "Can't set durability for this item");
				return;
			}
			// set dura
			item.setDurability((short) durability);
			player.setItemInHand(item);
			player.updateInventory();
			mat = Mat.from(item);
			Messenger.send(player, Level.NORMAL_INFO, "GCore", "Set durability to " + durability + ". Item : GCore type : " + mat.getModernName() + " (durability " + mat.getDurability() + ")");
		}
	}

}
