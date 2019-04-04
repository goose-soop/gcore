package be.guillaumevdn.gcore.commands;

import org.bukkit.entity.Player;

import be.guillaumevdn.gcore.GPerm;
import be.guillaumevdn.gcore.GCore;
import be.guillaumevdn.gcore.lib.command.CommandArgument;
import be.guillaumevdn.gcore.lib.command.CommandCall;
import be.guillaumevdn.gcore.lib.material.Mat;
import be.guillaumevdn.gcore.lib.messenger.Messenger;
import be.guillaumevdn.gcore.lib.messenger.Messenger.Level;
import be.guillaumevdn.gcore.lib.util.Utils;

public class CommandItemMat extends CommandArgument {

	public CommandItemMat() {
		super(GCore.inst(), Utils.asList("mat", "material", "iteminfo", "itemdb"), "see GCore material for an item", GPerm.GCORE_ADMIN, true); 
	}

	@Override
	public void perform(CommandCall call) {
		Player player = call.getSenderAsPlayer();
		Mat mat = Mat.from(player.getInventory().getItemInHand());
		Messenger.send(player, Level.NORMAL_INFO, "GCore", "GCore type : " + mat.getModernName() + " (durability " + mat.getDurability() + ")");
	}

}
