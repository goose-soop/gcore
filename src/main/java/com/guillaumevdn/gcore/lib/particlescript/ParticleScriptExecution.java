package com.guillaumevdn.gcore.lib.particlescript;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.number.NumberUtils;

/**
 * @author GuillaumeVDN
 */
public class ParticleScriptExecution {

	// ----- base
	private GPlugin plugin;
	private ParticleScript script;
	private final boolean autoLoop;
	private int operationIndex;
	private int ticksToWait;
	private ConcurrentHashMap<String, Double> variables = new ConcurrentHashMap<>(5); // lowercase keys
	private final Location fixedBaseLocation;

	/**
	 * Initializes a new particle script execution Same as calling
	 * {@link #ParticleScriptExecution(ParticleScript, Location)} with a null fixedBaseLocation argument
	 * 
	 * @param script the particle script
	 */
	public ParticleScriptExecution(GPlugin plugin, ParticleScript script, boolean autoLoop) {
		this(plugin, script, null, autoLoop);
	}

	/**
	 * Initializes a new particle script execution
	 * 
	 * @param script            the particle script
	 * @param fixedBaseLocation the fixed base location ; if this is null (because the location is volatile), you'll have to
	 *                          override method {@link #getBaseLocation()}
	 */
	public ParticleScriptExecution(GPlugin plugin, ParticleScript script, Location fixedBaseLocation, boolean autoLoop) {
		this.plugin = plugin;
		this.script = script;
		this.fixedBaseLocation = fixedBaseLocation;
		this.autoLoop = autoLoop;
		reset();
	}

	// ----- get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final ParticleScript getScript() {
		return script;
	}

	public Location getBaseLocation() {
		return fixedBaseLocation;
	}

	public int getOperationIndex() {
		return operationIndex;
	}

	public int getTicksToWait() {
		return ticksToWait;
	}

	public double getVariable(String name) {
		return variables.getOrDefault(name.toLowerCase(), 0d);
	}

	// ----- set
	public void setScript(ParticleScript script) {
		this.script = script;
	}

	public void setOperationIndex(int operationIndex) {
		this.operationIndex = operationIndex;
	}

	public void setTicksToWait(int ticksToWait) {
		this.ticksToWait = ticksToWait;
	}

	public void setVariable(String name, double value) {
		variables.put(name.toLowerCase(), value);
	}

	// ----- methods
	public void reset() {
		// indexes
		operationIndex = -1;
		ticksToWait = 0;
		// variables
		variables.clear();
	}

	/**
	 * @param players the players to perform the action to, or null if none (all actions might not be affected by this
	 *                parameter)
	 * @param isAsync true if the execution of actions should be async
	 * @return true if this execution is over
	 */
	public boolean update(Collection<Player> players, boolean isAsync) {
		try {
			// must wait
			if (--ticksToWait > 0) {
				return false;
			}
			// get base location (and end if invalid)
			Location baseLocation = getBaseLocation();
			if (baseLocation == null) {
				return true;
			}
			// reset base location variables (it might have moved)
			setVariable("baseX", baseLocation.getX());
			setVariable("baseY", baseLocation.getY());
			setVariable("baseZ", baseLocation.getZ());
			// execute operations
			for (;;) {
				// done for this execution
				if (++operationIndex >= script.getOperations().size()) {
					if (autoLoop) {
						reset();
					}
					return true;
				}
				// perform next operation
				if ((ticksToWait = script.getOperations().get(operationIndex).perform(this, players, isAsync)) > 0) {
					return false; // must wait
				}
			}
		} catch (Throwable ignored) {
			return false; // sometimes an error appears, on startup and such, but it apparently has no impact on players
		}
	}

	public Double parseAndCalculate(String raw) {
		if (raw == null) {
			return null;
		}
		String parsed = raw;
		for (String var : variables.keySet()) {
			parsed = parsed.replaceAll("\\b" + var + "\\b", "" + getVariable(var));
		}
		try {
			return NumberUtils.calculateExpression(parsed);
		} catch (Throwable exception) {
			// script.getPlugin().getMainLogger().error("Couldn't parse mathematical expression '" + parsed + "' (raw '" + raw + "')
			// (" + exception.getMessage() + ")");
			// - just don't print this anymore, it shows for invalid stuff
			return null;
		}
	}

	public Integer parseAndCalculateInteger(String raw) {
		Double parsed = parseAndCalculate(raw);
		return parsed != null ? parsed.intValue() : null;
	}

}
