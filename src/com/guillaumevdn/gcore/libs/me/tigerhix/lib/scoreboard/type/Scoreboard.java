package com.guillaumevdn.gcore.libs.me.tigerhix.lib.scoreboard.type;

import org.bukkit.entity.Player;

/**
 * Represents an advanced scoreboard that can display up to 48 characters in a single entry.
 * @author TigerHix
 */

public interface Scoreboard
{
	/**
	 * Activate the scoreboard.
	 */

	void activate();

	/**
	 * Deactivate the scoreboard.
	 */

	void deactivate();

	/**
	 * Determine if the scoreboard has been already activated.
	 * @return activated
	 */

	boolean isActivated();

	/**
	 * Returns the handler for this scoreboard.
	 * @return handler
	 */

	ScoreboardHandler getHandler();

	/**
	 * Set the handler for this scoreboard.
	 * @param handler handler
	 */

	Scoreboard setHandler(ScoreboardHandler handler);

	/**
	 * Returns the update interval.
	 * @return update interval
	 */

	long getUpdateInterval();

	/**
	 * Force update the scoreboard.
	 */

    void update();

	/**
	 * Returns the holder of this scoreboard.
	 * @return holder
	 */

	Player getHolder();

	/**
	 * Returns the bukkit scoreboard.
	 * @return the bukkit scoreboard
	 */

	org.bukkit.scoreboard.Scoreboard getBukkitScoreboard();
}
