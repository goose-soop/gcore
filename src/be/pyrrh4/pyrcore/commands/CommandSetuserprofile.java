package be.pyrrh4.pyrcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import be.pyrrh4.pyrcore.PCPerm;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.command.CommandArgument;
import be.pyrrh4.pyrcore.lib.command.CommandCall;
import be.pyrrh4.pyrcore.lib.command.Param;
import be.pyrrh4.pyrcore.lib.messenger.Messenger;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CommandSetuserprofile extends CommandArgument {

	private static final Param paramPlayer = new Param(Utils.asList("player"), "name", PCPerm.PYRCORE_ADMIN, false, true);
	private static final Param paramProfile = new Param(Utils.asList("profile"), "id", PCPerm.PYRCORE_ADMIN, false, true);

	public CommandSetuserprofile() {
		super(PyrCore.inst(), Utils.asList("setuserprofile"), "change data profile for an user", PCPerm.PYRCORE_ADMIN, false, paramPlayer, paramProfile);
	}

	@Override
	public void perform(CommandCall call) {
		OfflinePlayer target = paramPlayer.getOfflinePlayer(call, false);
		String profile = paramProfile.getStringAlphanumeric(call);
		if (target != null && profile != null) {
			// change profile
			String oldProfile = PyrCore.inst().getData().getDataProfiles().get(target.getUniqueId());
			PyrCore.inst().getData().getDataProfiles().set(target.getUniqueId(), profile);
			// message
			Messenger.send(Utils.asList(call.getSender(), Bukkit.getConsoleSender()), Messenger.Level.NORMAL_SUCCESS, "PyrCore", "Changed profile from '" + oldProfile + "' to '" + profile + "' for user " + target.getName() + " (" + target.getUniqueId().toString() + ").");
		}
	}

}
