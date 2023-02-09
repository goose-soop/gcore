package com.guillaumevdn.gcore.command;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentPlayer;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.player.PlayerUtils;
import com.guillaumevdn.gcore.lib.string.TextElement;

/**
 * @author GuillaumeVDN
 */
public final class GcorePermsReset extends Subcommand {

	private ArgumentPlayer argumentTarget = addArgumentPlayer(NeedType.REQUIRED, false, null, TextGeneric.commandParameterUsageTarget, true);

	public GcorePermsReset() {
		super(false, PermissionGCore.inst().gcoreAdmin, new TextElement("To reset the permission cache of a player"), CollectionUtils.asList("permsreset"));
	}

	@Override
	public void perform(CommandCall call) {
		Player target = argumentTarget.get(call);
		PlayerUtils.permissionCache.remove(target);
		call.getSender().sendMessage("§7Permission cache was reset for §e" + target.getName() + "§7.");
	}

}
