package com.guillaumevdn.gcore.commands;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.messenger.Messenger.Level;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandItemSpawnarmorstand extends CommandArgument {

	public CommandItemSpawnarmorstand() {
		super(GCore.inst(), Utils.asList("spawnarmorstand", "spawnas"), "get the configuration data for the item in your hand", GPerm.GCORE_ADMIN, true);
	}

	@Override
	public void perform(CommandCall call) {
		Player player = call.getSenderAsPlayer();
		ItemStack item = player.getInventory().getItemInHand();
		if (item != null) {
			ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
			stand.setGravity(false);
			stand.setCollidable(false);
			stand.setCanPickupItems(false);
			stand.setCustomName("§aHi test");
			stand.setCustomNameVisible(true);
			stand.setVisible(false);
			stand.setHelmet(item);
			stand.setSmall(true);
			return;
		}
		Messenger.send(player, Level.SEVERE_ERROR, "GCore", "This item can't be read.");
	}

}
