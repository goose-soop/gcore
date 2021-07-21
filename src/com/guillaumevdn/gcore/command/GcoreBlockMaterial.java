package com.guillaumevdn.gcore.command;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.string.TextElement;

/**
 * @author GuillaumeVDN
 */
public final class GcoreBlockMaterial extends Subcommand {

	public GcoreBlockMaterial() {
		super(true, PermissionGCore.inst().gcoreAdmin, new TextElement("block type"), CollectionUtils.asList("blockmaterial"));
	}

	@Override
	public void perform(CommandCall call) {
		Player sender = call.getSenderPlayer();
		Block block = sender.getTargetBlock(null, 50);
		sender.sendMessage(block == null ? "none found" : block.getType() + (Version.ATLEAST_1_13 ? "" : ", data " + block.getData()));
	}

}
