package com.guillaumevdn.gcore.lib.integration;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.guillaumevdn.gcore.lib.plugin.PluginUtils;

/**
 * @author GuillaumeVDN
 */
public class IntegrationListeners implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PluginEnableEvent event) {
		Plugin plugin = event.getPlugin();
		PluginUtils.getGPlugins().forEach(gplugin -> {
			Integration integration = gplugin.getIntegration(plugin.getName());
			if (integration != null && !integration.isActivated()) { // reactivate it if not active
				try {
					integration.activate();
				} catch (Throwable exception) {
					integration.getGPlugin().getMainLogger().error("Couldn't enable integration for " + integration.getPluginName(), exception);
				}
			}
		});
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PluginDisableEvent event) {
		Plugin plugin = event.getPlugin();
		PluginUtils.getGPlugins().forEach(gplugin -> {
			Integration integration = gplugin.getIntegration(plugin.getName());
			if (integration != null) {
				try {
					integration.deactivate();
				} catch (Throwable exception) {
					integration.getGPlugin().getMainLogger().error("Couldn't disable integration for " + integration.getPluginName(), exception);
				}
			}
		});
	}

}
