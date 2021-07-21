package com.guillaumevdn.gcore.lib.particlescript;

import java.util.Collection;

import org.bukkit.entity.Player;

public class OperationSet implements Operation {

	// ----- base
	private String variable, value;

	public OperationSet(String variable, String value) {
		this.variable = variable;
		this.value = value;
	}

	// ----- get
	public String getVariable() {
		return variable;
	}

	public String getValue() {
		return value;
	}

	// ----- methods
	@Override
	public int perform(ParticleScriptExecution execution, Collection<Player> players, boolean isAsync) {
		execution.setVariable(variable, execution.parseAndCalculate(value));
		return 0;
	}

}
