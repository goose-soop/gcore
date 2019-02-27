package be.pyrrh4.pyrcore.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.pyrcore.PCPerm;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.command.CommandArgument;
import be.pyrrh4.pyrcore.lib.command.CommandCall;
import be.pyrrh4.pyrcore.lib.command.Param;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.messenger.Messenger;
import be.pyrrh4.pyrcore.lib.messenger.Messenger.Level;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CommandItemSetdura extends CommandArgument {

	private static final Param paramDura = new Param(Utils.asList("durability", "d"), "durability", null, true, true);

	public CommandItemSetdura() {
		super(PyrCore.inst(), Utils.asList("setdurability", "setdura"), "set the durability of an item", PCPerm.PYRCORE_ADMIN, true, paramDura); 
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
				PyrCore.inst().messageError(player, "Can't set durability for this item");
				return;
			}
			// set dura
			item.setDurability((short) durability);
			player.setItemInHand(item);
			player.updateInventory();
			mat = Mat.from(item);
			Messenger.send(player, Level.NORMAL_INFO, "PyrCore", "Set durability to " + durability + ". Item : PyrCore type : " + mat.getModernName() + " (durability " + mat.getDurability() + ")");
		}
	}

}
