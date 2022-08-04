package com.guillaumevdn.gcore.integration.placeholderapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.data.BoardStatistics;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.statistic.Statistic;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * @author GuillaumeVDN
 */
public class PlaceholderExpansionGCore extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		return "GuillaumeVDN";
	}

	@Override
	public String getIdentifier() {
		return "gcore";
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String onPlaceholderRequest(Player player, String params) {
		// other player
		int last = params.lastIndexOf('_');
		if (last != -1) {
			Player otherPlayer = Bukkit.getPlayer(params.substring(last + 1));
			if (otherPlayer != null) {
				return parse(otherPlayer, params.substring(0, last));
			}
		}

		// parsing without a player
		if (player == null) {
			return null;  // might happen, for instance, when parsing placeholders temporarily, in GUIs ; it's important to return null so that it will be replaced later
		}

		// this player
		return parse(player, params);
	}

	private static String parse(Player player, String params) {
		params = params.toLowerCase();

		// statistic
		if (params.startsWith("statistic_")) {
			Statistic stat = Statistic.safeValueOf(params.substring("statistic_".length()));
			if (stat != null) {
				Double value = BoardStatistics.inst().getCachedValue(stat, player.getUniqueId());
				return value != null ? "" + NumberUtils.round(value, 2) : "0";
			}
			return ConfigGCore.unknownPlaceholderResult;
		}

		// invalid placeholder
		return null;
	}

}
