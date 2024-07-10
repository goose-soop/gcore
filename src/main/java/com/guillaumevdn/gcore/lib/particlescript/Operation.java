package com.guillaumevdn.gcore.lib.particlescript;

import java.util.Collection;

import org.bukkit.entity.Player;

/**
 * @author GuillaumeVDN
 */
public interface Operation {

	/**
	 * @param execution the execution instance
	 * @param players   the players to perform the action to, or null if none (all actions might not be affected by this
	 *                  parameter)
	 * @return the amount of ticks to wait before executing the next operation
	 */
	int perform(ParticleScriptExecution execution, Collection<Player> players, boolean isAsync);

}
