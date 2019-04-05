package com.guillaumevdn.gcore.lib.parseable;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.Logger;
import com.guillaumevdn.gcore.lib.Logger.Level;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;

public class ConfigData {

	// base
	private GPlugin plugin;
	private String superId;
	private Parseable component = null;
	private String lastError = null;
	private YMLConfiguration config = null;
	private String path = null;
	private boolean contains = false;

	// compact
	public ConfigData(GPlugin plugin, String superId, YMLConfiguration config, String path) {
		this.plugin = plugin;
		this.superId = superId;
		this.config = config;
		this.path = path;
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

	public YMLConfiguration getConfig() {
		return config;
	}

	public String getPath() {
		return path;
	}

	public boolean contains() {
		return contains;
	}

	// set
	public void setContains(boolean contains) {
		this.contains = contains;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setComponent(Parseable component) {
		this.component = component;
	}

	// methods
	public void log(String error) {
		String log = buildLog(this.lastError = error);
		Logger.log(Level.SEVERE, plugin != null ? plugin.getName() : "UNKNOWN PLUGIN", log);
	}

	protected String buildLog(String error) {
		return getSuperId().toUpperCase() + ": <" + error + "> AT PATH <" + getPath();
	}

	public ConfigData clone() {
		return new ConfigData(plugin, superId, config, path);
	}

	// copy
	public void copyPropertiesTo(ConfigData other) {
		other.plugin = plugin;
		other.superId = superId;
	}

}
