package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.player.PlayerUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentOfflinePlayer extends Argument<OfflinePlayer> implements PlayerArgument {

	private boolean senderIfNone;

	public ArgumentOfflinePlayer(NeedType need, boolean playerOnly, Permission permission, Text usage, boolean senderIfNone) {
		super(need, playerOnly, permission, usage);
		this.senderIfNone = senderIfNone;
	}

	public boolean senderIfNone() {
		return senderIfNone;
	}

	// override permission
	@Override
	public boolean hasPermission(CommandSender sender) {
		return senderIfNone || getPermission() == null || getPermission().has(sender);
	}

	@Override
	public boolean canUseBecauseOfSenderIfNone(CommandSender sender) {
		return senderIfNone && getPermission() != null && !getPermission().has(sender);
	}

	// do
	@Override
	public OfflinePlayer consume(CommandCall call) {
		if (call.getArguments().isEmpty()) {
			return senderIfNone && !call.isForTabComplete() ? call.getSenderPlayer() : null;
		}
		if (senderIfNone && getPermission() != null && !getPermission().has(call.getSender())) {
			return call.getSenderPlayer();
		}
		OfflinePlayer player = null;
		main: for (int i = 0; i < call.getArguments().size(); ++i) {
			String arg = call.getArguments().get(i).toLowerCase();
			// offline
			player = Bukkit.getOfflinePlayer(arg);
			if (player != null) {
				call.getArguments().remove(i);
				break main;
			}
			// online
			for (Player pl : PlayerUtils.getOnline()) {
				if (pl.getName().toLowerCase().startsWith(arg)) {
					player = pl;
					call.getArguments().remove(i);
					break main;
				}
			}
		}
		return player != null ? player : (senderIfNone && !call.isForTabComplete() ? call.getSenderPlayer() : null);
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return ArgumentPlayer.tabCompleteOnline(this);
	}

}
