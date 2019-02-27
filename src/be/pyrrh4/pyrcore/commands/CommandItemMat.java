package be.pyrrh4.pyrcore.commands;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCPerm;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.command.CommandArgument;
import be.pyrrh4.pyrcore.lib.command.CommandCall;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.messenger.Messenger;
import be.pyrrh4.pyrcore.lib.messenger.Messenger.Level;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CommandItemMat extends CommandArgument {

	public CommandItemMat() {
		super(PyrCore.inst(), Utils.asList("mat", "material", "iteminfo", "itemdb"), "see PyrCore material for an item", PCPerm.PYRCORE_ADMIN, true); 
	}

	@Override
	public void perform(CommandCall call) {
		Player player = call.getSenderAsPlayer();
		Mat mat = Mat.from(player.getInventory().getItemInHand());
		Messenger.send(player, Level.NORMAL_INFO, "PyrCore", "PyrCore type : " + mat.getModernName() + " (durability " + mat.getDurability() + ")");
	}

}
