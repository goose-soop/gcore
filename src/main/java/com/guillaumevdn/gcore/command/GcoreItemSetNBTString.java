package com.guillaumevdn.gcore.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextGCore;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentInfinite;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentString;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTItem;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public final class GcoreItemSetNBTString extends Subcommand {

	final ArgumentString argKey = addArgumentString(NeedType.REQUIRED, false, null, Text.of("key"));
	final ArgumentInfinite argValue = addArgumentInfinite(NeedType.REQUIRED, false, null, Text.of("value"));

	public GcoreItemSetNBTString() {
		super(true, PermissionGCore.inst().gcoreAdmin, TextGCore.commandDescriptionGcoreItemSetNBTString, ConfigGCore.commandsAliasesItemSetNBTString);
	}

	@Override
	public void perform(CommandCall call) {
		final String key = argKey.get(call);
		final String value = argValue.get(call);
		final Player sender = call.getSenderPlayer();
		final ItemStack item = sender.getItemInHand();

		if (Mat.isVoid(item)) {
			TextGCore.messageItemReadNull.send(sender);
		} else {
			try {
				final NBTItem nbt = new NBTItem(item, false);
				nbt.setString(key, value);
				sender.setItemInHand(nbt.getModifiedItem());
			} catch (Throwable error) {
				GCore.inst().getMainLogger().error("Couldn't set NBT string '" + key + "' to '" + value + "'", error);
			}
		}
	}

}
