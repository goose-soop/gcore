package com.guillaumevdn.gcore.lib.integration;

public abstract class PluginIntegration {

	// base
	private String pluginName;

	public PluginIntegration(String pluginName) {
		this.pluginName = pluginName;
	}

	// get
	public String getPluginName() {
		return pluginName;
	}

	// abstract methods
	public abstract void enable();
	public abstract void disable();

}
