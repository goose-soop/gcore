package be.guillaumevdn.gcore.commands;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.guillaumevdn.gcore.GPerm;
import be.guillaumevdn.gcore.GCore;
import be.guillaumevdn.gcore.lib.command.CommandArgument;
import be.guillaumevdn.gcore.lib.command.CommandCall;
import be.guillaumevdn.gcore.lib.messenger.Messenger;
import be.guillaumevdn.gcore.lib.messenger.Messenger.Level;
import be.guillaumevdn.gcore.lib.util.Utils;
import be.guillaumevdn.gcore.lib.versioncompat.Compat;

public class CommandItemNbt extends CommandArgument {

	public CommandItemNbt() {
		super(GCore.inst(), Utils.asList("nbt"), "get the custom NBT for the item in your hand, base64 encoded", GPerm.GCORE_ADMIN, true);
	}

	@Override
	public void perform(CommandCall call) {
		Player player = call.getSenderAsPlayer();
		ItemStack item = player.getInventory().getItemInHand();
		if (item != null) {
			Object nbt = Compat.INSTANCE.getNbt(item);
			if (nbt != null) {
				try {
					String id = UUID.randomUUID().toString().split("-")[0];
					Messenger.send(player, Level.NORMAL_INFO, "GCore", "NBT of this item was printed in console (" + id + ").");
					GCore.inst().log("NBT of item " + id + " : " + Compat.INSTANCE.serializeNbt(nbt));
				} catch (IOException exception) {
					exception.printStackTrace();
					Messenger.send(player, Level.SEVERE_ERROR, "GCore", "Couldn't serialize this item custom NBT, check the console.");
				}
				return;
			}
		}
		Messenger.send(player, Level.SEVERE_ERROR, "GCore", "This item doesn't has a custom NBT.");
	}

}
