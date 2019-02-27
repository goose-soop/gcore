package be.pyrrh4.pyrcore.commands;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.pyrcore.PCPerm;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.command.CommandArgument;
import be.pyrrh4.pyrcore.lib.command.CommandCall;
import be.pyrrh4.pyrcore.lib.messenger.Messenger;
import be.pyrrh4.pyrcore.lib.messenger.Messenger.Level;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.versioncompat.Compat;

public class CommandItemNbt extends CommandArgument {

	public CommandItemNbt() {
		super(PyrCore.inst(), Utils.asList("nbt"), "get the custom NBT for the item in your hand, base64 encoded", PCPerm.PYRCORE_ADMIN, true);
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
					Messenger.send(player, Level.NORMAL_INFO, "PyrCore", "NBT of this item was printed in console (" + id + ").");
					PyrCore.inst().log("NBT of item " + id + " : " + Compat.INSTANCE.serializeNbt(nbt));
				} catch (IOException exception) {
					exception.printStackTrace();
					Messenger.send(player, Level.SEVERE_ERROR, "PyrCore", "Couldn't serialize this item custom NBT, check the console.");
				}
				return;
			}
		}
		Messenger.send(player, Level.SEVERE_ERROR, "PyrCore", "This item doesn't has a custom NBT.");
	}

}
