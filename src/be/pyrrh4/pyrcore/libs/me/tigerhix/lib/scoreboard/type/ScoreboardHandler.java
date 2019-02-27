package be.pyrrh4.pyrcore.libs.me.tigerhix.lib.scoreboard.type;

import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.util.Pair;

/**
 * Represents the handler to determine the title and entries of a scoreboard.
 * @author TigerHix, modified by PYRRH4
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
