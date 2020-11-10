package com.guillaumevdn.gcore.lib.player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class PlayerUtils {

	public static List<Player> getOnline() {
		return getOnlineStream().collect(Collectors.toList());
	}

	public static Stream<? extends Player> getOnlineStream() {
		if (Version.CURRENT.isLessOrEqualsTo(Version.MC_1_7_R4)) {
			Object array = Bukkit.getOnlinePlayers();
			return Arrays.stream((Player[]) array);
		} else {
			return Bukkit.getOnlinePlayers().stream();
		}
	}

	public static List<Player> getOnline(Collection<UUID> uuids) {
		return uuids.stream().map(uuid -> Bukkit.getPlayer(uuid)).filter(pl -> pl != null).collect(Collectors.toList());
	}

	public static List<UUID> getUniqueIds(Collection<Player> players) {
		return players.stream().map(pl -> pl.getUniqueId()).collect(Collectors.toList());
	}

	public static List<Location> getLocations(Collection<Player> players) {
		return players.stream().map(pl -> pl.getLocation()).collect(Collectors.toList());
	}

	public static boolean hasPermission(Object target, String permission) {
		if (target == null) return false;
		if ((permission = StringUtils.nonEmptyOrNull(permission)) == null) return true;
		return hasPermission0(target, permission);
	}

	private static boolean hasPermission0(Object target, String permission) {
		if (target instanceof Collection<?>) {
			for (Object sub : ((Collection<?>) target)) {
				if (!hasPermission0(sub, permission)) {
					return false;
				}
			}
			return true;
		} else if (target instanceof CommandSender) {
			CommandSender sender = (CommandSender) target;
			return sender.isOp() || sender.hasPermission(permission);
		} else if (target instanceof OfflinePlayer) {
			OfflinePlayer player = (OfflinePlayer) target;
			Player playerOnline = player.getPlayer();
			return playerOnline == null ? player.isOp() : hasPermission0(playerOnline, permission);
		} else if (target instanceof UUID) {
			return hasPermission(Bukkit.getOfflinePlayer((UUID) target), permission);
		}
		return false;
	}

	public static void sendMessage(Object target, String message) {
		sendMessage(target, message, null, false);
	}

	public static void sendMessage(Object target, String message, CommandSender cc, boolean onlyCC) {
		if (target == null) {
			return;
		}
		if (message.trim().isEmpty()) {
			return; // ignore strings that are completely empty (no format code &r)
		}
		if (target instanceof CommandSender) {
			CommandSender targetSender = (CommandSender) target;
			if (!onlyCC) targetSender.sendMessage(message);
			if (cc != null && (onlyCC || !cc.equals(target))) cc.sendMessage(message + (onlyCC ? TextGeneric.messageSilentCC : TextGeneric.messageCC).replace("{og}", () -> targetSender.getName()).parseLine());
		} else if (target instanceof Collection<?>) {
			for (Object sub : ((Collection<?>) target)) {
				sendMessage(sub, message, cc, onlyCC);
			}
		} else if (target instanceof OfflinePlayer) {
			sendMessage(((OfflinePlayer) target).getPlayer(), message, cc, onlyCC);
		} else if (target instanceof CommandCall) {
			sendMessage(((CommandCall) target).getSender(), message, cc, onlyCC);
		} else if (target instanceof UUID) {
			sendMessage(Bukkit.getPlayer((UUID) target), message, cc, onlyCC);
		} else if (target instanceof Replacer) {
			sendMessage(((Replacer) target).getReplacerData().getPlayer(), message, cc, onlyCC);
		} else {
			throw new IllegalArgumentException("invalid target type " + target.getClass());
		}
	}

}
