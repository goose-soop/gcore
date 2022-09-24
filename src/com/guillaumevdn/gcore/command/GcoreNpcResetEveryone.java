package com.guillaumevdn.gcore.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.string.TextElement;

/**
 * @author GuillaumeVDN
 */
public final class GcoreNpcResetEveryone extends Subcommand {

	public GcoreNpcResetEveryone() {
		super(false, PermissionGCore.inst().gcoreAdmin, new TextElement("to reset all GCore NPCs"), CollectionUtils.asList("npcreseteveryone"));
	}

	@Override
	public void perform(CommandCall call) {
		NPCManager.ifPresent(manager -> {
			call.getSender().sendMessage("&aResetting NPCs for every player who ever connected...");
			for (OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
				UserNPCs.process(pl.getUniqueId(), user -> {
					user.clearNPCs();
					call.getSender().sendMessage("§a... reset all NPCs for " + pl.getName());
				});
			}
		});
	}

}
