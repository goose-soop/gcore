package com.guillaumevdn.gcore.lib.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.number.NumberUtils;

/**
 * @author GuillaumeVDN
 */
public final class EntityUtils {
	public static final EntityType ENTITY_TYPE_FIREWORK_ROCKET;
	public static final EntityType ENTITY_TYPE_END_CRYSTAL;

	static {
		EntityType rocketType = null;
		EntityType crystalType = null;

		try {
			rocketType = EntityType.valueOf("FIREWORK");
		} catch (IllegalArgumentException e) {
			rocketType = EntityType.FIREWORK_ROCKET;
		}
		try {
			crystalType = EntityType.valueOf("ENDER_CRYSTAL");
		} catch (IllegalArgumentException e) {
			crystalType = EntityType.END_CRYSTAL;
		}

		ENTITY_TYPE_FIREWORK_ROCKET = rocketType;
		ENTITY_TYPE_END_CRYSTAL = crystalType;
	}

	// ----- find
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

	public static Entity getEntityByUUID(UUID uuid) {
		if (Version.ATLEAST_1_11) {
			return Bukkit.getEntity(uuid);
		}
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (uuid.equals(entity.getUniqueId())) {
					return entity;
				}
			}
		}
		return null;
	}

	// ----- player
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

	// ----- misc
	public static void spawnRandomFirework(Location location) {
		Firework firework = (Firework) location.getWorld().spawnEntity(location, ENTITY_TYPE_FIREWORK_ROCKET);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(FireworkEffect.builder()
				.with(CollectionUtils.randomArray(FireworkEffect.Type.values()))
				.withColor(Color.fromRGB(NumberUtils.random(0, 255), NumberUtils.random(0, 255), NumberUtils.random(0, 255)))
				.withFade(Color.fromRGB(NumberUtils.random(0, 255), NumberUtils.random(0, 255), NumberUtils.random(0, 255)))
				.trail(NumberUtils.random())
				.flicker(NumberUtils.random())
				.build());
		firework.setFireworkMeta(meta);
	}

}
