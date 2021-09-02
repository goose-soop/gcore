package com.guillaumevdn.gcore.lib.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

/**
 * @author GuillaumeVDN
 */
public class PlayerDataSaver {

	private Map<UUID, Location> lastLocations = new HashMap();
	private Map<UUID, ItemStack[]> lastInventory = new HashMap();
	private Map<UUID, ItemStack[]> lastArmor = new HashMap();
	private Map<UUID, Scoreboard> lastScoreboard = new HashMap();
	private Map<UUID, PotionEffect[]> lastPotionEffects = new HashMap();
	private Map<UUID, Float> lastFlySpeeds = new HashMap();
	private Map<UUID, Float> lastWalkSpeeds = new HashMap();
	private Map<UUID, Boolean> allowFly = new HashMap();
	private Map<UUID, GameMode> gamemodes = new HashMap();

	public void save(Player player) {
		lastLocations.put(player.getUniqueId(), player.getLocation().clone());
		lastInventory.put(player.getUniqueId(), player.getInventory().getContents());
		lastArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
		lastScoreboard.put(player.getUniqueId(), player.getScoreboard());
		lastPotionEffects.put(player.getUniqueId(), (PotionEffect[])player.getActivePotionEffects().toArray(new PotionEffect[player.getActivePotionEffects().size()]));
		allowFly.put(player.getUniqueId(), Boolean.valueOf(player.getAllowFlight()));
		gamemodes.put(player.getUniqueId(), player.getGameMode());

		PlayerUtils.clear(player);
		resetEffects(player);

		lastFlySpeeds.put(player.getUniqueId(), player.getFlySpeed());
		lastWalkSpeeds.put(player.getUniqueId(), player.getWalkSpeed());

		player.setAllowFlight(false);
		player.setFlying(false);

		player.setGameMode(GameMode.SURVIVAL);
	}

	public void restore(Player player) {
		if (lastLocations.containsKey(player.getUniqueId())) {
			player.teleport(lastLocations.remove(player.getUniqueId()));
			player.setVelocity(new Vector(0d, 0d, 0d));
		}

		if (lastInventory.containsKey(player.getUniqueId())) {
			player.getInventory().setContents(lastInventory.remove(player.getUniqueId()));
		}
		if (lastArmor.containsKey(player.getUniqueId())) {
			player.getInventory().setArmorContents(lastArmor.remove(player.getUniqueId()));
		}
		player.updateInventory();

		if (lastScoreboard.containsKey(player.getUniqueId())) {
			Scoreboard scoreboard = lastScoreboard.remove(player.getUniqueId());
			if (scoreboard == null) {
				scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
			}
			player.setScoreboard(scoreboard);
		} else {
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}

		resetEffects(player);

		if (lastPotionEffects.containsKey(player.getUniqueId())) {
			for (PotionEffect effect : lastPotionEffects.get(player.getUniqueId())) {
				player.addPotionEffect(effect);
			}
		}

		player.setGameMode(GameMode.SURVIVAL);

		if (allowFly.containsKey(player.getUniqueId())) {
			player.setAllowFlight(allowFly.get(player.getUniqueId()));
		}

		if (gamemodes.containsKey(player.getUniqueId())) {
			player.setGameMode(gamemodes.remove(player.getUniqueId()));
		}

		if (lastFlySpeeds.containsKey(player.getUniqueId())) {
			player.setFlySpeed(lastFlySpeeds.remove(player.getUniqueId()));
		}

		if (lastWalkSpeeds.containsKey(player.getUniqueId())) {
			player.setFlySpeed(lastWalkSpeeds.remove(player.getUniqueId()));
		}
	}

	private static void resetEffects(Player player) {
		for (PotionEffectType effect : PotionEffectType.values()) {
			try {
				player.removePotionEffect(effect);
			} catch (Throwable ignored) {}// FUCK
		}
	}

}
