package com.guillaumevdn.gcore.lib.particlescript;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;

/**
 * @author GuillaumeVDN
 */
public class MultipleBasedParticleScriptExecution<K> {

	private final GPlugin plugin;
	private ParticleScript script;
	private final Supplier<Map<K, Location>> locationSupplier;
	private final boolean autoLoop;
	private Map<K, Location> locations = new HashMap<>();
	private RWHashMap<K, ParticleScriptExecution> executions = new RWHashMap<>();

	public MultipleBasedParticleScriptExecution(GPlugin plugin, ParticleScript script, Supplier<Map<K, Location>> locationSupplier, boolean autoLoop) {
		this.plugin = plugin;
		this.script = script;
		this.locationSupplier = locationSupplier;
		this.autoLoop = autoLoop;
	}

	// ----- get
	public GPlugin getPlugin() {
		return plugin;
	}
	
	public final ParticleScript getScript() {
		return script;
	}

	// ----- set
	public void setScript(ParticleScript script) {
		this.script = script;
	}

	// ----- do
	public void update(Collection<Player> players, boolean isAsync) {
		locationSupplier.get().forEach((key, location) -> {
			// update location
			locations.put(key, location);
			// maybe init execution
			ParticleScriptExecution exec = executions.computeIfAbsent(key, __ -> new ParticleScriptExecution(plugin, script, autoLoop) {
				@Override
				public Location getBaseLocation() {
					return locations.get(key); // will be reset every time this class' update() is called so we get it from the map (only one execution instance will be created)
				}
			});
			// maybe update script if it changed
			if (!exec.getScript().equals(script)) {
				exec.setScript(script);
			}
			// update execution
			exec.update(players, isAsync);
		});
	}

}
