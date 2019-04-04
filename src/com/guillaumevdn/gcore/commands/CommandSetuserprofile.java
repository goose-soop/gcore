package com.guillaumevdn.gcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Param;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandSetuserprofile extends CommandArgument {

	private static final Param paramPlayer = new Param(Utils.asList("player"), "name", GPerm.GCORE_ADMIN, false, true);
	private static final Param paramProfile = new Param(Utils.asList("profile"), "id", GPerm.GCORE_ADMIN, false, true);

	public CommandSetuserprofile() {
		super(GCore.inst(), Utils.asList("setuserprofile"), "change data profile for an user", GPerm.GCORE_ADMIN, false, paramPlayer, paramProfile);
	}

	@Override
	public void perform(CommandCall call) {
		OfflinePlayer target = paramPlayer.getOfflinePlayer(call, false);
		String profile = paramProfile.getStringAlphanumeric(call);
		if (target != null && profile != null) {
			// change profile
			String oldProfile = GCore.inst().getData().getDataProfiles().get(target.getUniqueId());
			GCore.inst().getData().getDataProfiles().set(target.getUniqueId(), profile);
			// message
			Messenger.send(Utils.asList(call.getSender(), Bukkit.getConsoleSender()), Messenger.Level.NORMAL_SUCCESS, "GCore", "Changed profile from '" + oldProfile + "' to '" + profile + "' for user " + target.getName() + " (" + target.getUniqueId().toString() + ").");
		}
	}

}
