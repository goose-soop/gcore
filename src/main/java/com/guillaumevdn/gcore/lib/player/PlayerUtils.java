package com.guillaumevdn.gcore.lib.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.concurrency.RWArrayList;
import com.guillaumevdn.gcore.lib.concurrency.RWHashSet;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public final class PlayerUtils {

	public static List<Player> getOnline() {
		Object online = Bukkit.getOnlinePlayers();
		if (online instanceof Collection) { // on some servers (forge servers, #2462) it's actually a collection even in 1.7
			return new ArrayList<>((Collection<? extends Player>) online);
		} else {
			Player[] array = (Player[]) online;
			List<Player> result = new ArrayList<>(array.length);
			for (int i = 0; i < array.length; ++i) {
				result.add(array[i]);
			}
			return result;
		}
	}

	public static Stream<? extends Player> getOnlineStream() {
		Object online = Bukkit.getOnlinePlayers();
		if (online instanceof Collection) { // on some servers (forge servers, #2462) it's actually a collection even in 1.7
			return ((Collection<? extends Player>) online).stream();
		} else {
			Player[] array = (Player[]) online;
			return Arrays.stream(array);
		}
	}

	public static List<Player> getOnline(Collection<UUID> uuids) {
		return uuids.stream().map(uuid -> Bukkit.getPlayer(uuid)).filter(pl -> pl != null).collect(Collectors.toList());
	}

	public static List<? extends CommandSender> getOnlineAndConsole() {
		return CollectionUtils.asListMultiple(CommandSender.class, getOnline(), Bukkit.getConsoleSender());
	}

	public static List<UUID> getUniqueIds(Collection<Player> players) {
		return players.stream().map(pl -> pl.getUniqueId()).collect(Collectors.toList());
	}

	public static List<Location> getLocations(Collection<Player> players) {
		return players.stream().map(pl -> pl.getLocation()).collect(Collectors.toList());
	}

	public static boolean hasPermission(Object target, String permission) {
		if (target == null)
			return false;
		if ((permission = StringUtils.nonEmptyOrNull(permission)) == null)
			return true;
		return hasPermission0(target, permission);
	}

	public static transient WeakHashMap<CommandSender, LowerCaseHashMap<Pair<Long, Boolean>>> permissionCache = new WeakHashMap<>();

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
			LowerCaseHashMap<Pair<Long, Boolean>> cache = permissionCache.computeIfAbsent(sender, __ -> new LowerCaseHashMap<>(2, 1f));
			Pair<Long, Boolean> result = cache.get(permission);
			if (result == null || System.currentTimeMillis() - result.getA() > ConfigGCore.permissionCacheRetainMillis) {
				result = Pair.of(System.currentTimeMillis(), sender.isOp() || sender.hasPermission(permission));
				cache.put(permission, result);
			}
			return result.getB();
		} else if (target instanceof OfflinePlayer) {
			OfflinePlayer player = (OfflinePlayer) target;
			Player playerOnline = PlayerUtils.getOnline(player);
			return playerOnline == null ? player.isOp() : hasPermission0(playerOnline, permission);
		} else if (target instanceof CommandCall) {
			return hasPermission0(((CommandCall) target).getSender(), permission);
		} else if (target instanceof UUID) {
			return hasPermission(Bukkit.getOfflinePlayer((UUID) target), permission);
		} else if (target instanceof Replacer) {
			return hasPermission(((Replacer) target).getReplacerData().getPlayer(), permission);
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
			if (!onlyCC)
				targetSender.sendMessage(message);
			if (cc != null && (onlyCC || !cc.equals(target)))
				cc.sendMessage(
						message + (onlyCC ? TextGeneric.messageSilentCC : TextGeneric.messageCC).replace("{og}", () -> targetSender.getName()).parseLine());
		} else if (target instanceof RWArrayList<?>) {
			((RWArrayList<?>) target).forEach(sub -> sendMessage(sub, message, cc, onlyCC));
		} else if (target instanceof RWHashSet<?>) {
			((RWHashSet<?>) target).forEach(sub -> sendMessage(sub, message, cc, onlyCC));
		} else if (target instanceof Collection<?>) {
			for (Object sub : ((Collection<?>) target)) {
				sendMessage(sub, message, cc, onlyCC);
			}
		} else if (target instanceof OfflinePlayer) {
			OfflinePlayer offline = (OfflinePlayer) target;
			Player online = PlayerUtils.getOnline(offline);
			if (online != null) {
				sendMessage(online, message, cc, onlyCC);
			} else {
				if (cc != null)
					cc.sendMessage(
							message + (onlyCC ? TextGeneric.messageSilentCC : TextGeneric.messageCC).replace("{og}", () -> offline.getName()).parseLine());
			}
		} else if (target instanceof CommandCall) {
			sendMessage(((CommandCall) target).getSender(), message, cc, onlyCC);
		} else if (target instanceof ClickCall) {
			sendMessage(((ClickCall) target).getClicker(), message, cc, onlyCC);
		} else if (target instanceof UUID) {
			sendMessage(Bukkit.getOfflinePlayer((UUID) target), message, cc, onlyCC);
		} else if (target instanceof Replacer) {
			sendMessage(((Replacer) target).getReplacerData().getPlayer(), message, cc, onlyCC);
		} else {
			throw new IllegalArgumentException("invalid target type " + target.getClass());
		}
	}

	public static void ifOnline(OfflinePlayer player, Consumer<Player> consumer) {
		Player online = getOnline(player);
		if (online != null) {
			consumer.accept(online);
		}
	}

	public static Player getOnline(OfflinePlayer player) {
		if (player.isOnline()) { // if player is an old instance of a Player who disconnected, isOnline will be false but getPlayer() will return this,
									// aka the old instance
			return player.getPlayer();
		}
		return null;
	}

	public static OfflinePlayer getOfflineWhoPlayedBefore(String name) {
		final Player online = Bukkit.getPlayer(name);
		if (online != null) {
			return online;
		}
		final OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		return player != null && player.hasPlayedBefore() ? player : null;
	}

	public static OfflinePlayer getOfflineWhoPlayedBefore(UUID uuid) {
		final Player online = Bukkit.getPlayer(uuid);
		if (online != null) {
			return online;
		}
		final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		return player != null && player.hasPlayedBefore() ? player : null;
	}

	public static void clear(Player player) {
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.updateInventory();
	}

}
