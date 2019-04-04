package com.guillaumevdn.gcore.libs.me.tigerhix.lib.scoreboard.type;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.util.Pair;

/**
 * Represents the handler to determine the title and entries of a scoreboard.
 * @author TigerHix, modified by GuillaumeVDN
 */

public interface ScoreboardHandler
{
	/**
	 * Determines both the title and the entries for this player.
	 * @param player player
	 * @return title and entries
	 */

	Pair<String, List<Entry>> update(Player player);
}
