package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.player.PlayerUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.Pair;

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
	public OfflinePlayer consume(CommandCall call) {
		if (call.getArguments().isEmpty()) {
			return senderIfNone && !call.isForTabComplete() ? call.getSenderPlayer() : null;
		}
		if (senderIfNone && getPermission() != null && !getPermission().has(call.getSender())) {
			return call.getSenderPlayer();
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			String arg = call.getArguments().get(i).toLowerCase();
			// exact
			OfflinePlayer value = exactMatch(arg);
			if (value == null) {
				List<OfflinePlayer> matches = partialMatches(arg).collect(Collectors.toList());
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

	protected OfflinePlayer exactMatch(String arg) {
		Player online = Bukkit.getPlayer(arg);
		if (online != null) {
			return online;
		}
		Pair<UUID, String> offline = WorkerGCore.inst().getOfflinePlayer(arg);
		if (offline != null) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(offline.getA());
			if (player != null && player.getLastPlayed() != 0L) {
				WorkerGCore.inst().registerOfflinePlayer(player.getName(), player.getUniqueId());
				return player;
			}
		}
		return null;
	}

	protected Stream<? extends OfflinePlayer> partialMatches(String arg) {
		return PlayerUtils.getOnlineStream().filter(pl -> pl.getName().toLowerCase().startsWith(arg));
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return Stream.concat(PlayerUtils.getOnlineStream().map(pl -> pl.getName()), WorkerGCore.inst().getOfflinePlayersNames()).filter(n -> n != null).sorted(StringUtils.STRING_WITHNUMBERS_IGNORECASE).collect(Collectors.toList());
	}

}
