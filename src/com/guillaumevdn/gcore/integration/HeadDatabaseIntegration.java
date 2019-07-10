package com.guillaumevdn.gcore.integration;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.integration.PluginIntegration;

import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class HeadDatabaseIntegration extends PluginIntegration {

	// base
	private HeadDatabaseAPI api = null;

	public HeadDatabaseIntegration(String pluginName) {
		super(pluginName);
	}

	// override
	@Override
	public void enable() {
		// register
		api = new HeadDatabaseAPI();
		GCore.inst().setHeadDatabaseIntegration(this);
	}

	@Override
	public void disable() {
		// unregister
		api = null;
		GCore.inst().setHeadDatabaseIntegration(null);
	}

	// methods
	public ItemStack getItem(String id) {
		try {
			return api.getItemHead(id);
		} catch (Throwable ignored) {
			return null;
		}
	}

}
