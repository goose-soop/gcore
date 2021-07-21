package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
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
public class ArgumentPlayer extends Argument<Player> implements PlayerArgument {

	private boolean senderIfNone;

	public ArgumentPlayer(NeedType need, boolean playerOnly, Permission permission, Text usage, boolean senderIfNone) {
		super(need, playerOnly, permission, usage);
		this.senderIfNone = senderIfNone;
	}

	public boolean senderIfNone() {
		return senderIfNone;
	}

	// ----- override permission
	@Override
	public boolean hasPermission(CommandSender sender) {
		return senderIfNone || getPermission() == null || getPermission().has(sender);
	}

	@Override
	public boolean canUseBecauseOfSenderIfNone(CommandSender sender) {
		return senderIfNone && getPermission() != null && !getPermission().has(sender);
	}

	// ----- do
	@Override
	public Player consume(CommandCall call) {
		if (call.getArguments().isEmpty()) {
			return senderIfNone && !call.isForTabComplete() ? call.getSenderPlayer() : null;
		}
		if (senderIfNone && getPermission() != null && !getPermission().has(call.getSender())) {
			return call.getSenderPlayer();
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			String arg = call.getArguments().get(i).toLowerCase();
			// exact
			Player value = exactMatch(arg);
			if (value == null) {
				List<Player> matches = partialMatches(arg).collect(Collectors.toList());
				if (matches.size() == 1) {
					value = matches.get(0);
				}
			}
			if (value != null) {
				call.getArguments().remove(i);
				return value;
			}
		}
		return senderIfNone && !call.isForTabComplete() ? call.getSenderPlayer() : null;
	}

	protected Player exactMatch(String arg) {
		return Bukkit.getPlayer(arg);
	}

	protected Stream<? extends Player> partialMatches(String arg) {
		return PlayerUtils.getOnlineStream().filter(pl -> pl.getName().toLowerCase().startsWith(arg));
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return PlayerUtils.getOnlineStream().map(pl -> pl.getName()).sorted(String::compareTo).collect(Collectors.toList());
	}

}
