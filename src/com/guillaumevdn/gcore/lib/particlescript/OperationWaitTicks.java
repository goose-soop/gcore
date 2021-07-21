package com.guillaumevdn.gcore.lib.particlescript;

import java.util.Collection;

import org.bukkit.entity.Player;

/**
 * @author GuillaumeVDN
 */
public class OperationWaitTicks implements Operation {

	// ----- base
	private String ticks;

	public OperationWaitTicks(String ticks) {
		this.ticks = ticks;
	}

	// ----- get
	public String getTicks() {
		return ticks;
	}

	// ----- methods
	@Override
	public int perform(ParticleScriptExecution execution, Collection<Player> players, boolean isAsync) {
		return (int) Math.ceil(execution.parseAndCalculate(ticks));
	}

}
