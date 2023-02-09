package com.guillaumevdn.gcore.lib.player;

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

import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;

/**
 * @author GuillaumeVDN
 */
public class PlayerDataSaver {

	private final boolean saveLocation;
	private final boolean saveInventory;
	private final boolean saveScoreboard;
	private final boolean saveEffects;
	private final boolean saveSpeeds;
	private final boolean saveGameModeAndFly;

	private final RWHashMap<UUID, Location> lastLocations = new RWHashMap<>(5, 1f);
	private final RWHashMap<UUID, ItemStack[]> lastInventory = new RWHashMap<>(5, 1f);
	private final RWHashMap<UUID, ItemStack[]> lastArmor = new RWHashMap<>(5, 1f);
	private final RWHashMap<UUID, Scoreboard> lastScoreboard = new RWHashMap<>(5, 1f);
	private final RWHashMap<UUID, PotionEffect[]> lastPotionEffects = new RWHashMap<>(5, 1f);
	private final RWHashMap<UUID, Float> lastFlySpeeds = new RWHashMap<>(5, 1f);
	private final RWHashMap<UUID, Float> lastWalkSpeeds = new RWHashMap<>(5, 1f);
	private final RWHashMap<UUID, Boolean> allowFly = new RWHashMap<>(5, 1f);
	private final RWHashMap<UUID, GameMode> gamemodes = new RWHashMap<>(5, 1f);

	public PlayerDataSaver() {
		this(true, true, true, true, true, true);
	}

	public PlayerDataSaver(boolean saveLocation, boolean saveInventory, boolean saveScoreboard, boolean saveEffects, boolean saveSpeeds, boolean saveGameModeAndFly) {
		this.saveLocation = saveLocation;
		this.saveInventory = saveInventory;
		this.saveScoreboard = saveScoreboard;
		this.saveEffects = saveEffects;
		this.saveSpeeds = saveSpeeds;
		this.saveGameModeAndFly = saveGameModeAndFly;
	}

	public void save(Player player) {
		if (saveLocation) {
			lastLocations.put(player.getUniqueId(), player.getLocation().clone());
		}
		if (saveInventory) {
			lastInventory.put(player.getUniqueId(), player.getInventory().getContents());
			lastArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
			PlayerUtils.clear(player);
		}
		if (saveScoreboard) {
			lastScoreboard.put(player.getUniqueId(), player.getScoreboard());
		}
		if (saveEffects) {
			lastPotionEffects.put(player.getUniqueId(), (PotionEffect[])player.getActivePotionEffects().toArray(new PotionEffect[player.getActivePotionEffects().size()]));
			resetEffects(player);
		}
		if (saveSpeeds) {
			lastFlySpeeds.put(player.getUniqueId(), player.getFlySpeed());
			lastWalkSpeeds.put(player.getUniqueId(), player.getWalkSpeed());
		}
		if (saveGameModeAndFly) {
			allowFly.put(player.getUniqueId(), Boolean.valueOf(player.getAllowFlight()));
			gamemodes.put(player.getUniqueId(), player.getGameMode());
			player.setAllowFlight(false);
			player.setFlying(false);
			player.setGameMode(GameMode.SURVIVAL);
		}
	}

	public void restore(Player player) {
		if (saveLocation) {
			if (lastLocations.containsKey(player.getUniqueId())) {
				player.teleport(lastLocations.remove(player.getUniqueId()));
				player.setVelocity(new Vector(0d, 0d, 0d));
			}
		}
		if (saveInventory) {
			if (lastInventory.containsKey(player.getUniqueId())) {
				player.getInventory().setContents(lastInventory.remove(player.getUniqueId()));
			}
			if (lastArmor.containsKey(player.getUniqueId())) {
				player.getInventory().setArmorContents(lastArmor.remove(player.getUniqueId()));
			}
			player.updateInventory();
		}
		if (saveScoreboard) {
			if (lastScoreboard.containsKey(player.getUniqueId())) {
				Scoreboard scoreboard = lastScoreboard.remove(player.getUniqueId());
				if (scoreboard == null) {
					scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
				}
				player.setScoreboard(scoreboard);
			} else {
				player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			}
		}
		if (saveEffects) {
			resetEffects(player);
			if (lastPotionEffects.containsKey(player.getUniqueId())) {
				for (PotionEffect effect : lastPotionEffects.get(player.getUniqueId())) {
					player.addPotionEffect(effect);
				}
			}
		}
		if (saveSpeeds) {
			if (lastFlySpeeds.containsKey(player.getUniqueId())) {
				player.setFlySpeed(lastFlySpeeds.remove(player.getUniqueId()));
			}
			if (lastWalkSpeeds.containsKey(player.getUniqueId())) {
				player.setFlySpeed(lastWalkSpeeds.remove(player.getUniqueId()));
			}
		}
		if (saveGameModeAndFly) {
			if (allowFly.containsKey(player.getUniqueId())) {
				player.setAllowFlight(allowFly.get(player.getUniqueId()));
			}
			if (gamemodes.containsKey(player.getUniqueId())) {
				player.setGameMode(gamemodes.remove(player.getUniqueId()));
			}
		}
	}

	private static void resetEffects(Player player) {
		for (PotionEffectType effect : PotionEffectType.values()) {
			try {
				player.removePotionEffect(effect);
			} catch (Throwable ignored) {}
		}
	}

}
