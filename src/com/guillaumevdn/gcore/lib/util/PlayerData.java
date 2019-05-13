package com.guillaumevdn.gcore.lib.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerData {

	// static fields
	private static PlayerData INSTANCE = new PlayerData();

	// base
	private Map<UUID, Location> lastLocations = new HashMap<UUID, Location>();
	private Map<UUID, ItemStack[]> lastInventory = new HashMap<UUID, ItemStack[]>();
	private Map<UUID, ItemStack[]> lastArmor = new HashMap<UUID, ItemStack[]>();
	private Map<UUID, Scoreboard> lastScoreboard = new HashMap<UUID, Scoreboard>();
	private Map<UUID, PotionEffect[]> lastPotionEffects = new HashMap<UUID, PotionEffect[]>();
	private Map<UUID, Boolean> allowFly = new HashMap<UUID, Boolean>();
	private Map<UUID, GameMode> gamemodes = new HashMap<UUID, GameMode>();

	private PlayerData() {
	}

	// methods
	public static void save(final Player player) {
		if (player == null) return;

		INSTANCE.lastLocations.put(player.getUniqueId(), player.getLocation().clone());
		INSTANCE.lastInventory.put(player.getUniqueId(), player.getInventory().getContents());
		INSTANCE.lastArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
		INSTANCE.lastScoreboard.put(player.getUniqueId(), player.getScoreboard());
		INSTANCE.lastPotionEffects.put(player.getUniqueId(), player.getActivePotionEffects().toArray(new PotionEffect[player.getActivePotionEffects().size()]));
		INSTANCE.allowFly.put(player.getUniqueId(), player.getAllowFlight());
		INSTANCE.gamemodes.put(player.getUniqueId(), player.getGameMode());

		Utils.clear(player);
		Utils.resetEffects(player);
		Utils.allowFly(player, false);
		Utils.resetGameMode(player);
		Utils.showToAll(player);
	}

	public static void restore(final Player player) {
		if (player == null) return;

		// location
		if (INSTANCE.lastLocations.containsKey(player.getUniqueId())) {
			player.teleport(INSTANCE.lastLocations.remove(player.getUniqueId()));
		}

		// inv
		if (INSTANCE.lastInventory.containsKey(player.getUniqueId())) {
			player.getInventory().setContents(INSTANCE.lastInventory.remove(player.getUniqueId()));
		}
		if (INSTANCE.lastArmor.containsKey(player.getUniqueId())) {
			player.getInventory().setArmorContents(INSTANCE.lastArmor.remove(player.getUniqueId()));
		}
		player.updateInventory();

		// scoreboard
		if (INSTANCE.lastScoreboard.containsKey(player.getUniqueId())) {
			Scoreboard scoreboard = INSTANCE.lastScoreboard.remove(player.getUniqueId());
			if (scoreboard == null) {
				scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
			}
			player.setScoreboard(scoreboard);
		} else {
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}

		// effects
		Utils.resetEffects(player);
		if (INSTANCE.lastPotionEffects.containsKey(player.getUniqueId())) {
			for (PotionEffect effect : INSTANCE.lastPotionEffects.remove(player.getUniqueId())) {
				player.addPotionEffect(effect);
			}
		}

		// other
		Utils.resetWalkSpeed(player);
		Utils.resetGameMode(player);
		Utils.showToAll(player);
		if (INSTANCE.allowFly.containsKey(player.getUniqueId())) {
			Utils.allowFly(player, INSTANCE.allowFly.remove(player.getUniqueId()));
		}
		if (INSTANCE.gamemodes.containsKey(player.getUniqueId())) {
			player.setGameMode(INSTANCE.gamemodes.remove(player.getUniqueId()));
		}
	}

}
