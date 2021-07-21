package com.guillaumevdn.gcore.command;

import org.bukkit.Bukkit;

import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.string.TextElement;

/**
 * @author GuillaumeVDN
 */
public final class GcoreImpl extends Subcommand {

	public GcoreImpl() {
		super(true, PermissionGCore.inst().gcoreAdmin, new TextElement("server implementation"), CollectionUtils.asList("impl"));
	}

	@Override
	public void perform(CommandCall call) {
		call.getSender().sendMessage("Version : " + Bukkit.getVersion());
		call.getSender().sendMessage("Bukkit version : " + Bukkit.getBukkitVersion());
		call.getSender().sendMessage("Internal version : " + Version.CURRENT);
	}

}
