package com.guillaumevdn.gcore.lib.parseable.data;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.Logger;
import com.guillaumevdn.gcore.lib.Logger.Level;
import com.guillaumevdn.gcore.lib.parseable.Parseable;

public abstract class DataLink {

	// base
	private Parseable component;
	private String superId;
	private GPlugin plugin;
	private String lastError = null;

	// compact
	public DataLink(Parseable component, GPlugin plugin, String superId) {
		this.component = component;
	}

	// get
	public Parseable getComponent() {
		return component;
	}

	public String getSuperId() {
		return superId;
	}

	public GPlugin getPlugin() {
		return plugin;
	}

	public String getLastError() {
		return lastError;
	}

	// set
	public void setComponent(Parseable component) {
		this.component = component;
	}

	// methods
	public void log(String error) {
		String log = buildLog(this.lastError = error);
		Logger.log(Level.SEVERE, plugin != null ? plugin.getName() : "UNKNOWN PLUGIN", log);
	}

	// abstract
	public abstract boolean contains();
	public abstract void setContains(boolean contains);
	protected abstract String buildLog(String error);
	@Override
	public abstract DataLink clone();

	// copy
	public void copyPropertiesTo(DataLink other) {
		other.plugin = plugin;
		other.superId = superId;
	}

}
