package com.guillaumevdn.gcore.lib.versioncompat;

import java.io.IOException;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.util.Utils;

public abstract class Compat {

	// instance
	public static final Compat INSTANCE = Utils.createCompat();

	public void init() {
	}

	// optional
	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int duration, int fadeOut) {}
	public void sendActionBar(Player player, String message) {}
	public void changeTab(Player player, String head, String foot) {}

	public ItemMeta addItemFlags(ItemMeta meta) {
		return meta;
	}

	public Mat getArmorStandHelmetType(Entity armorStand) {
		return null;
	}

	public void setUnbreakable(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;
		try {
			ItemMeta.class.getDeclaredMethod("setUnbreakable", boolean.class).invoke(meta, true);
			item.setItemMeta(meta);
		} catch (Throwable ignored) {
			try {
				Object spigot = ItemMeta.class.getDeclaredMethod("spigot").invoke(item.getItemMeta());
				spigot.getClass().getDeclaredMethod("setUnbreakable", boolean.class).invoke(meta, true);
				item.setItemMeta(meta);
			} catch (Throwable ignored2) {
			}
		}
	}

	public boolean isUnbreakable(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return false;
		try {
			return (boolean) ItemMeta.class.getDeclaredMethod("isUnbreakable").invoke(meta);
		} catch (Throwable ignored) {
			try {
				Object spigot = ItemMeta.class.getDeclaredMethod("spigot").invoke(meta);
				return (boolean) spigot.getClass().getDeclaredMethod("isUnbreakable").invoke(spigot);
			} catch (Throwable ignored2) {
			}
		}
		return false;
	}

	// nbt
	public abstract String serializeNbt(Object nbt) throws IOException;
	public abstract Object unserializeNbt(String serialized) throws IOException;
	public abstract Object getNbt(ItemStack item);
	public abstract ItemStack setNbt(ItemStack item, Object nbt);

	// misc
	public abstract Score getScore(Objective objective, String name);
	public void setScoreboardTeamNameTags(Team team, String prefix, String suffix) {}
	public abstract Enchantment getEnchantment(String raw);

}
