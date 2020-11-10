package com.guillaumevdn.gcore.lib.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * @author GuillaumeVDN
 */
public final class EntityUtils {

	// find
	public static List<Entity> getEntitiesInSquare(Location center, double range, Function<Entity, Boolean> validator) {
		List<Entity> entities = new ArrayList<>();
		for (Entity entity : center.getWorld().getEntities()) {
			Location loc = entity.getLocation();
			if (Math.abs(center.getX() - loc.getX()) <= range && Math.abs(center.getY() - loc.getY()) <= range && Math.abs(center.getZ() - loc.getZ()) <= range && (validator == null || validator.apply(entity))) {
				entities.add(entity);
			}
		}
		return entities;
	}

	// player
	public static void playUtilSound(Player player, Sound sound) {
		if (sound != null) {
			player.playSound(player.getEyeLocation(), sound, 0.5f, 1f);
		}
	}

	public static List<UUID> getUUIDs(Collection<? extends Player> players) {
		List<UUID> result = new ArrayList<>();
		for (Player player : players) {
			result.add(player.getUniqueId());
		}
		return result;
	}

	public static List<Player> getPlayers(Collection<UUID> uuids) {
		List<Player> result = new ArrayList<>();
		for (UUID uuid : uuids) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				result.add(player);
			}
		}
		return result;
	}

}
