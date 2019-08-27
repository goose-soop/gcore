package com.guillaumevdn.gcore.commands;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.messenger.Messenger.Level;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandItemSetunbreakable extends CommandArgument {

	public CommandItemSetunbreakable() {
		super(GCore.inst(), Utils.asList("setunbreakable", "setunbr"), "set an item unbreakable", GPerm.GCORE_ADMIN, true); 
	}

	@Override
	public void perform(CommandCall call) {
		Player player = call.getSenderAsPlayer();
		ItemStack item = player.getInventory().getItemInHand();
		Mat mat = Mat.fromItem(item);
		// invalid mat
		if (mat == null || mat.isAir()) {
			GCore.inst().messageError(player, "Can't set this item unbreakable");
			return;
		}
		// set name
		ItemMeta meta = item.getItemMeta();
		try {
			Method method = meta.getClass().getDeclaredMethod("setUnbreakable", boolean.class);
			method.setAccessible(true);
			method.invoke(meta, true);
		} catch (Throwable ignored) {
			ignored.printStackTrace();
			GCore.inst().messageError(player, "Command isn't supported");
			return;
		}
		item.setItemMeta(meta);
		player.setItemInHand(item);
		player.updateInventory();
		Messenger.send(player, Level.NORMAL_INFO, "GCore", "Set item unbreakable");
	}

}
