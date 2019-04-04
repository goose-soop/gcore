package be.guillaumevdn.gcore.lib.util;

import java.util.List;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderAPIHandler {

	public static String fill(Player player, String text) {
		return PlaceholderAPI.setBracketPlaceholders(player, PlaceholderAPI.setPlaceholders(player, text));
	}

	public static List<String> fill(Player player, List<String> text) {
		return PlaceholderAPI.setBracketPlaceholders(player, PlaceholderAPI.setPlaceholders(player, text));
	}

}
