package com.guillaumevdn.gcore.lib.string.placeholder;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.lib.reflection.Reflection;

/**
 * @author GuillaumeVDN
 */
public final class PlaceholderAPIUtils {

	public static ItemStack parse(Player player, ItemStack item) {
		try {
			ItemStack clone = item.clone();
			ItemMeta meta = clone.getItemMeta();
			if (meta.getDisplayName() != null) {
				meta.setDisplayName(parse(player, meta.getDisplayName()));
			}
			if (meta.getLore() != null) {
				meta.setLore(parse(player, meta.getLore()));
			}
			clone.setItemMeta(meta);
			return clone;
		} catch (Throwable ignored) {
			return item;
		}
	}

	public static String parse(Player player, String string) {
		try {
			string = Reflection.invokeMethod("me.clip.placeholderapi.PlaceholderAPI", "setPlaceholders", null, player, string).get(String.class);
			return Reflection.invokeMethod("me.clip.placeholderapi.PlaceholderAPI", "setBracketPlaceholders", null, player, string).get(String.class);
		} catch (Throwable ignored) {
			return string;
		}
	}

	public static List<String> parse(Player player, List<String> list) {
		try {
			list = Reflection.invokeMethod("me.clip.placeholderapi.PlaceholderAPI", "setPlaceholders", null, player, list).get(list.getClass());
			return Reflection.invokeMethod("me.clip.placeholderapi.PlaceholderAPI", "setBracketPlaceholders", null, player, list).get(list.getClass());
		} catch (Throwable ignored) {
			return list;
		}
	}

}
