package com.guillaumevdn.gcore.integration.placeholderapi;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;

import me.clip.placeholderapi.PlaceholderHook;

public class GCorePlaceholdersHook extends PlaceholderHook {

	@Override
	public String onPlaceholderRequest(Player player, String params) {
		if (player == null) {
			return "{require player parameter}";
		}
		// %gcore_statistic_<statistic>%
		if (params.toLowerCase().startsWith("statistic_")) {
			// not loaded
			if (GCore.inst().getData() == null || GCore.inst().getData().getStatistics() == null) {
				return "0";
			}
			// return stat
			String statistic = params.substring("statistic_".length());
			return "" + GCore.inst().getData().getStatistics().get(statistic, player.getUniqueId());
		}
		// unknown
		return "{unknown placeholder}";
	}

}
