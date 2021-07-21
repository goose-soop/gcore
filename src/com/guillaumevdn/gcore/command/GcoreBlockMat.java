package com.guillaumevdn.gcore.command;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.string.TextElement;

/**
 * @author GuillaumeVDN
 */
public final class GcoreBlockMat extends Subcommand {

	public GcoreBlockMat() {
		super(true, PermissionGCore.inst().gcoreAdmin, new TextElement("block type"), CollectionUtils.asList("blockmat"));
	}

	@Override
	public void perform(CommandCall call) {
		Player sender = call.getSenderPlayer();
		Block block = sender.getTargetBlock(null, 50);
		sender.sendMessage(Mat.fromBlock(block).orAir().getId());
	}

}
