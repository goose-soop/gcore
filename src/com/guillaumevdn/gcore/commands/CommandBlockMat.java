package com.guillaumevdn.gcore.commands;

import java.util.HashSet;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.messenger.Messenger.Level;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandBlockMat extends CommandArgument {

	public CommandBlockMat() {
		super(GCore.inst(), Utils.asList("mat", "material", "iteminfo", "itemdb"), "see GCore material for a block", GPerm.GCORE_ADMIN, true); 
	}

	@Override
	public void perform(CommandCall call) {
		Player player = call.getSenderAsPlayer();
		Block block = player.getTargetBlock((HashSet<Byte>) null, 10);
		if (block == null) {
			Messenger.send(player, Level.NORMAL_INFO, "GCore", "No block was found ; point at a block to see its type.");
		} else {
			Mat mat = Mat.fromBlock(block);
			Messenger.send(player, Level.NORMAL_INFO, "GCore", "GCore type : " + mat.getModernName() + " (durability " + mat.getDurability() + ")");
		}
	}

}
