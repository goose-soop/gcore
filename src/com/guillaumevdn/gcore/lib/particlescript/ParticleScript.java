package com.guillaumevdn.gcore.lib.particlescript;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class ParticleScript {

	// base
	private final GPlugin plugin;
	private final String id;
	private final List<Operation> operations;

	public ParticleScript(GPlugin plugin, String id) {
		this(plugin, id, new ArrayList<>());
	}

	public ParticleScript(GPlugin plugin, String id, List<Operation> operations) {
		this.plugin = plugin;
		this.id = id;
		this.operations = operations;
	}

	// get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final String getId() {
		return id;
	}

	public final List<Operation> getOperations() {
		return operations;
	}

	// object
	@Override
	public String toString() {
		return getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		return ObjectUtils.equals(obj, ParticleScript.class, other -> other.id.equals(id));
	}

}
