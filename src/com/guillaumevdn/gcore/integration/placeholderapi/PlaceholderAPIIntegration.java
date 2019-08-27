package com.guillaumevdn.gcore.integration.placeholderapi;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.integration.PluginIntegration;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderAPIIntegration extends PluginIntegration {

	// base
	public PlaceholderAPIIntegration(String pluginName) {
		super(pluginName);
	}

	// methods
	@Override
	public void enable() {
		// register hook
		PlaceholderAPI.registerPlaceholderHook("gcore", new GCorePlaceholdersHook());
		// register
		GCore.inst().setPlaceholderAPIIntegration(this);
	}

	@Override
	public void disable() {
		// unregister hook
		PlaceholderAPI.unregisterPlaceholderHook("gcore");
		// unregister
		GCore.inst().setPlaceholderAPIIntegration(null);
	}

}
